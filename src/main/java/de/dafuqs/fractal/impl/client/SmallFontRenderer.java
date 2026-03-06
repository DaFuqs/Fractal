package de.dafuqs.fractal.impl.client;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class SmallFontRenderer {
	
	private static boolean hasRegisteredReloader = false;
	
	private static final ResourceLocation TINYFONT_TEXTURE = ResourceLocation.fromNamespaceAndPath("fractal", "textures/gui/tinyfont.png");
	private static final ResourceLocation SILVER_REF = ResourceLocation.fromNamespaceAndPath("fractal", "textures/gui");
	private static final ResourceLocation SILVER_FNT = ResourceLocation.fromNamespaceAndPath("fractal", "textures/gui/silver.fnt");
	private static AngelFont SILVER;

	public static void draw(GuiGraphics ctx, String str, int x, int y, boolean rtl) {
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
		ResourceLocation lastTexture = null;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		var mat = ctx.pose().last().pose();
		var tess = Tesselator.getInstance();
		BufferBuilder bb = null;
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
		float xPos = 0;
		for (int i = 0; i < codePoints.length; i++) {
			int c = codePoints[i];
			float advance;
			if (!forceUnicode && c <= 0x7F) {
				int u = (c % 16) * 4;
				int v = (c / 16) * 6;
				if (lastTexture != TINYFONT_TEXTURE) {
					if (lastTexture != null) {
						assert bb != null;
						BufferUploader.drawWithShader(bb.build());
					}
					lastTexture = TINYFONT_TEXTURE;
					RenderSystem.setShaderTexture(0, lastTexture);
					bb = tess.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
				}
				quad(mat, bb, x+xPos+(rtl?-4:4), y, u, v, 4, 6, 64, 48);
				advance = 4;
			} else {
				if (!hasRegisteredReloader) {
					hasRegisteredReloader = true;
					((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(new Reloader());
				}
				if (SILVER == null) {
					SILVER = Reloader.parse(Minecraft.getInstance().getResourceManager());
				}
				var ch = SILVER.chars().get(c);
				if (ch == null) continue;
				if (lastTexture != ch.page()) {
					if (lastTexture != null) {
						assert bb != null;
						BufferUploader.drawWithShader(bb.build());
					}
					lastTexture = ch.page();
					RenderSystem.setShaderTexture(0, lastTexture);
					bb = tess.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
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
		if (lastTexture != null) {
			assert bb != null;
			BufferUploader.drawWithShader(bb.build());
		}
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
		vc.addVertex(mat, x1, y1, 0).setUv(u1, v1);
		vc.addVertex(mat, x1, y2, 0).setUv(u1, v2);
		vc.addVertex(mat, x2, y2, 0).setUv(u2, v2);
		vc.addVertex(mat, x2, y1, 0).setUv(u2, v1);
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
