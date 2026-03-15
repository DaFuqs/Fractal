package de.dafuqs.fractal.impl.client;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.util.Identifier;

public record AngelFont(
		// info
		String face,
		float size,
		boolean bold,
		boolean italic,
		float paddingTop,
		float paddingRight,
		float paddingBottom,
		float paddingLeft,
		int horizontalSpacing,
		int verticalSpacing,

		// common
		float lineHeight,
		float base,
		int scaleW,
		int scaleH,

		// chars
		Int2ObjectMap<CharDef> chars
	) {

	public record CharDef(int x, int y, int width, int height, int xOffset, int yOffset, int xAdvance, Identifier page) {}
	
	public static AngelFont parse(Identifier reference, BufferedReader r) throws FileNotFoundException, IOException {
		try (r) {
			var info = tokenize(r.readLine());
			if (!"info".equals(info.get("_type")))
				throw new IOException("Must be the info tag at line 1");
			var face = info.get("face");
			var size = parseFloat(info.get("size"));
			var bold = "1".equals(info.get("bold"));
			var italic = "1".equals(info.get("italic"));
			if (!"".equals(info.get("charset")) && !"1".equals(info.get("unicode")))
				throw new IOException("Non-Unicode fonts are not supported by this implementation; expected libGDX Hiero `charset=\"\" unicode=0` or standard `charset=* unicode=1` at line 1");
			
			if (!"100".equals(info.get("stretchH")))
				throw new IOException("Stretched fonts are not supported by this implementation at line 1");
			
			var paddingSpl = info.get("padding").split(",");
			if (paddingSpl.length != 4)
				throw new IOException("padding must have exactly 4 entries at line 1");
			var paddingTop = parseFloat(paddingSpl[0]);
			var paddingRight = parseFloat(paddingSpl[1]);
			var paddingBottom = parseFloat(paddingSpl[2]);
			var paddingLeft = parseFloat(paddingSpl[3]);
			
			var spacingSpl = info.get("spacing").split(",");
			if (spacingSpl.length != 2)
				throw new IOException("spacing must have exactly 2 entries at line 1");
			var horizontalSpacing = parseInt(spacingSpl[0]);
			var verticalSpacing = parseInt(spacingSpl[1]);
			
			var common = tokenize(r.readLine());
			if (!"common".equals(common.get("_type")))
				throw new IOException("Must be the common tag at line 2");
			
			if (!"0".equals(common.get("packed")))
				throw new IOException("Packed fonts are not supported by this implementation at line 2");
			
			var lineHeight = parseFloat(common.get("lineHeight"));
			var base = parseFloat(common.get("base"));
			var scaleW = parseInt(common.get("scaleW"));
			var scaleH = parseInt(common.get("scaleH"));
			var pages = new Int2ObjectOpenHashMap<Identifier>(parseInt(common.get("pages")));
			
			Int2ObjectMap<CharDef> chars = null;
			
			int lineNo = 2;
			
			while (true) {
				var line = r.readLine();
				if (line == null) break;
				lineNo++;
				var tk = tokenize(line);
				switch (tk.get("_type")) {
					case "info" -> throw new IOException("Unexpected late info tag at line "+lineNo);
					case "common" -> throw new IOException("Unexpected late common tag at line "+lineNo);
					case "page" -> {
						if (chars != null)
							throw new IOException("Got page tag after chars tag at line "+lineNo);
						pages.put(parseInt(tk.get("id")), reference.withSuffixedPath("/"+tk.get("file")));
					}
					case "chars" -> {
						chars = new Int2ObjectOpenHashMap<>(parseInt(tk.get("count")));
					}
					case "char" -> {
						if (chars == null)
							throw new IOException("Got char tag before chars tag at line "+lineNo);
						if (!"0".equals(tk.get("chnl")) && !"15".equals(tk.get("chnl")))
							throw new IOException("Packed fonts are not supported by this implementation; expected libGDX chnl=0 or standard chnl=15 at line "+lineNo);
						int page = parseInt(tk.get("page"));
						if (!pages.containsKey(page))
							throw new IOException("Got char tag for undeclared page id="+page+" at line "+lineNo);
						chars.put(parseInt(tk.get("id")), new CharDef(
									parseInt(tk.get("x")), parseInt(tk.get("y")),
									parseInt(tk.get("width")), parseInt(tk.get("height")),
									parseInt(tk.get("xoffset")), parseInt(tk.get("yoffset")),
									parseInt(tk.get("xadvance")), pages.get(page)
								));
					}
					case "kernings" -> {
						if (!"0".equals(tk.get("count")))
							throw new IOException("Kerning is not supported by this implementation at line "+lineNo);
					}
					case "kerning" -> {
						throw new IOException("Kerning is not supported by this implementation at line "+lineNo);
					}
					default -> throw new IOException("Unrecognized tag type "+tk.get("_type")+" at line "+lineNo);
				}
			}
			
			if (chars == null) throw new IOException("Found no char definitions");
			
			return new AngelFont(face, size, bold, italic, paddingTop, paddingRight, paddingBottom,
					paddingLeft, horizontalSpacing, verticalSpacing, lineHeight, base, scaleW, scaleH,
					Int2ObjectMaps.unmodifiable(chars));
		}
	}
	
	private static Map<String, String> tokenize(String line) throws IOException {
		if (line == null) return Map.of();
		var out = new Object2ObjectLinkedOpenHashMap<String, String>();
		String key = null;
		var buf = new StringBuilder();
		
		enum S {
			BEFORE_KEY,
			KEY,
			BEFORE_DELIMITER,
			DELIMITER,
			UNQUOTED_VALUE,
			QUOTED_VALUE,
		}
		
		int idx = line.indexOf(' ');
		String type = line.substring(0, idx);
		out.put("_type", type);
		
		var state = S.BEFORE_KEY;
		for (int i = idx; i < line.length(); i++) {
			var ch = line.charAt(i);
			switch (state) {
				case BEFORE_KEY:
					if (ch == ' ') continue;
					state = S.KEY;
					// fall-thru
				case KEY:
					if (ch == ' ' || ch == '=') {
						state = ch == ' ' ? S.BEFORE_DELIMITER : S.DELIMITER;
						key = buf.toString();
						buf.setLength(0);
					} else {
						buf.append(ch);
					}
					break;
				case BEFORE_DELIMITER:
					if (ch == ' ') continue;
					if (ch == '=') {
						state = S.DELIMITER;
					}
					break;
				case DELIMITER:
					if (ch == '=') continue;
					if (ch == '"') {
						state = S.QUOTED_VALUE;
						continue;
					} else if (ch != ' ') {
						state = S.UNQUOTED_VALUE;
					} else {
						continue;
					}
					// fall-thru
				case UNQUOTED_VALUE:
				case QUOTED_VALUE:
					char end = state == S.UNQUOTED_VALUE ? ' ' : '"';
					if (ch == end) {
						out.put(key, buf.toString());
						buf.setLength(0);
						state = S.BEFORE_KEY;
					} else {
						buf.append(ch);
					}
					break;
			}
		}
		if (state == S.DELIMITER) {
			throw new IOException("Unexpected EOF while searching for beginning of value");
		}
		if (state == S.QUOTED_VALUE) {
			throw new IOException("Unexpected EOF while parsing quoted value");
		}
		if (state == S.UNQUOTED_VALUE) {
			out.put(key, buf.toString());
		}
		return out;
	}
	
}
