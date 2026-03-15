package de.dafuqs.fractal.impl.client;

import java.io.IOException;
import java.io.UncheckedIOException;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class SmallFontRenderer {
	
	private static boolean hasRegisteredReloader = false;
	
	private static final Identifier TINYFONT_TEXTURE = Identifier.fromNamespaceAndPath("fractal", "textures/gui/tinyfont.png");
	private static final Identifier SILVER_REF = Identifier.fromNamespaceAndPath("fractal", "textures/gui");
	private static final Identifier SILVER_FNT = Identifier.fromNamespaceAndPath("fractal", "textures/gui/silver.fnt");
	private static AngelFont SILVER;

	public static void draw(GuiGraphics ctx, String str, int x, int y, boolean rtl, int textColor) {
		int[] codePoints = str.codePoints().toArray();
		if (rtl) {
			int hf = codePoints.length/2;
			int anc = codePoints.length-1;
			for (int i = 0; i < hf; i++) {
				int swp = codePoints[i];
				codePoints[i] = codePoints[anc-i];
				codePoints[anc-i] = swp;
			}
		}
		boolean forceUnicode = Minecraft.getInstance().isEnforceUnicode();
		if (!forceUnicode) {
			for (int i = 0; i < codePoints.length; i++) {
				int c = codePoints[i];
				if (c > 0x7F) {
					// avoid disjointed character rendering in e.g. Polish
					forceUnicode = true;
					break;
				}
			}
		}
		ctx.pose().pushMatrix();
		ctx.pose().translate(x, y);
		ctx.pose().scale(0.5f);
		int xPos = 0;
		for (int i = 0; i < codePoints.length; i++) {
			int c = codePoints[i];
			int advance;
			if (!forceUnicode && c <= 0x7F) {
				int u = (c % 16) * 4;
				int v = (c / 16) * 6;
				ctx.blit(RenderPipelines.GUI_TEXTURED, TINYFONT_TEXTURE, xPos+(rtl?-8:8), 0, u*2, v*2, 8, 12, 128, 96, textColor);
				advance = 8;
			} else {
				if (!hasRegisteredReloader) {
					hasRegisteredReloader = true;
					ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(Identifier.fromNamespaceAndPath("fractal", "font_loader"), new Reloader());
				}
				if (SILVER == null) {
					SILVER = Reloader.parse(Minecraft.getInstance().getResourceManager());
				}
				var ch = SILVER.chars().get(c);
				if (ch == null) continue;
				advance = ch.xAdvance();
				ctx.blit(RenderPipelines.GUI_TEXTURED, ch.page(), xPos+ch.xOffset()+(rtl?-advance:8), ch.yOffset()-2, ch.x(), ch.y(), ch.width(), ch.height(), SILVER.scaleW(), SILVER.scaleH(), textColor);
			}
			if (rtl) {
				xPos -= advance;
			} else {
				xPos += advance;
			}
		}
		ctx.pose().popMatrix();
	}
	
	private static final class Reloader extends SimplePreparableReloadListener<AngelFont> {
		
		private static AngelFont parse(ResourceManager manager) {
			try {
				return AngelFont.parse(SILVER_REF, manager.getResourceOrThrow(SILVER_FNT).openAsReader());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		
		@Override
		protected AngelFont prepare(ResourceManager manager, ProfilerFiller profiler) {
			return parse(manager);
		}

		@Override
		protected void apply(AngelFont cache, ResourceManager manager, ProfilerFiller profiler) {
			SILVER = cache;
		}
		
	}

}
