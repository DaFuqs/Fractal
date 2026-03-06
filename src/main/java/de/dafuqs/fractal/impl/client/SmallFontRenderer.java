package de.dafuqs.fractal.impl.client;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class SmallFontRenderer {
	
	private static boolean hasRegisteredReloader = false;
	
	private static final Identifier TINYFONT_TEXTURE = new Identifier("fractal", "textures/tinyfont.png");
	private static final Identifier SILVER_REF = new Identifier("fractal", "textures");
	private static final Identifier SILVER_FNT = new Identifier("fractal", "textures/silver.fnt");
	private static AngelFont SILVER;

	public static void draw(DrawContext ctx, String str, int x, int y, boolean rtl) {
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
		Identifier lastTexture = null;
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		var mat = ctx.getMatrices().peek().getPositionMatrix();
		var bb = Tessellator.getInstance().getBuffer();
		boolean forceUnicode = MinecraftClient.getInstance().forcesUnicodeFont();
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
		float xPos = 0;
		for (int i = 0; i < codePoints.length; i++) {
			int c = codePoints[i];
			float advance;
			if (!forceUnicode && c <= 0x7F) {
				int u = (c % 16) * 4;
				int v = (c / 16) * 6;
				if (lastTexture != TINYFONT_TEXTURE) {
					if (lastTexture != null) BufferRenderer.drawWithGlobalProgram(bb.end());
					lastTexture = TINYFONT_TEXTURE;
					RenderSystem.setShaderTexture(0, lastTexture);
					bb.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
				}
				quad(mat, bb, x+xPos+(rtl?-4:4), y, u, v, 4, 6, 64, 48);
				advance = 4;
			} else {
				if (!hasRegisteredReloader) {
					hasRegisteredReloader = true;
					((ReloadableResourceManagerImpl)MinecraftClient.getInstance().getResourceManager()).registerReloader(new Reloader());
				}
				if (SILVER == null) {
					SILVER = Reloader.parse(MinecraftClient.getInstance().getResourceManager());
				}
				var ch = SILVER.chars().get(c);
				if (ch == null) continue;
				if (lastTexture != ch.page()) {
					if (lastTexture != null) {
						BufferRenderer.drawWithGlobalProgram(bb.end());
					}
					lastTexture = ch.page();
					RenderSystem.setShaderTexture(0, lastTexture);
					bb.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
				}
				advance = ch.xAdvance()/2f;
				quad(mat, bb, x+xPos+(ch.xOffset()/2f)+(rtl?-advance:4), y+(ch.yOffset()/2f)-1, ch.x()/2f, ch.y()/2f, ch.width()/2f, ch.height()/2f, SILVER.scaleW()/2f, SILVER.scaleH()/2f);
			}
			if (rtl) {
				xPos -= advance;
			} else {
				xPos += advance;
			}
		}
		if (lastTexture != null) BufferRenderer.drawWithGlobalProgram(bb.end());
	}
	
	private static void quad(Matrix4f mat, VertexConsumer vc, float x, float y, float u, float v, float width, float height, float texW, float texH) {
		float x1 = x;
		float y1 = y;
		float x2 = x+width;
		float y2 = y+height;
		float u1 = u/texW;
		float u2 = (u+width)/texW;
		float v1 = v/texH;
		float v2 = (v+height)/texH;
		vc.vertex(mat, x1, y1, 0).texture(u1, v1).next();
		vc.vertex(mat, x1, y2, 0).texture(u1, v2).next();
		vc.vertex(mat, x2, y2, 0).texture(u2, v2).next();
		vc.vertex(mat, x2, y1, 0).texture(u2, v1).next();
	}
	
	private static final class Reloader extends SinglePreparationResourceReloader<AngelFont> implements IdentifiableResourceReloadListener {
		
		private static final Identifier ID = new Identifier("fractal", "font_loader");

		@Override
		public Identifier getFabricId() {
			return ID;
		}
		
		private static AngelFont parse(ResourceManager manager) {
			try {
				return AngelFont.parse(SILVER_REF, manager.getResourceOrThrow(SILVER_FNT).getReader());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		
		@Override
		protected AngelFont prepare(ResourceManager manager, Profiler profiler) {
			return parse(manager);
		}

		@Override
		protected void apply(AngelFont cache, ResourceManager manager, Profiler profiler) {
			SILVER = cache;
		}
		
	}

}
