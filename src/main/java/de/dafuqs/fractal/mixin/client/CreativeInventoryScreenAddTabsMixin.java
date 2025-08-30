package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.fabricmc.api.*;
import net.minecraft.client.gl.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.*;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenAddTabsMixin extends HandledScreen<CreativeScreenHandler> implements SubTabLocation, CreativeInventoryScreenAccessor {
	
	@Unique
	private static final int LAST_TAB_INDEX_RENDERING_LEFT = 11;
	
	@Unique
	private static final Identifier TINYFONT_TEXTURE = Identifier.of("fractal", "textures/gui/tinyfont.png");
	
	public CreativeInventoryScreenAddTabsMixin(CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
	
	@Shadow
	private float scrollPosition;
	
	@Shadow
	private static ItemGroup selectedTab;
	
	@Unique
	private int fractal$y; // tab start y
	@Unique
	private int fractal$x, fractal$h; // left tabs
	@Unique
	private int fractal$x2, fractal$h2; // right tabs
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V"))
	public void fractal$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!(selectedTab instanceof ItemGroupParent parent) || parent.fractal$getChildren().isEmpty()) return;
		
		var matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(this.x, this.y);
		
		if (!selectedTab.shouldRenderName()) {
			ItemGroup child = parent.fractal$getSelectedChild();
			var selected = selectedTab.getDisplayName();
			context.drawText(textRenderer, selected, 8, 6, Colors.DARK_GRAY, false);
			int x = 8 + textRenderer.getWidth(selected);
			if (child != null) {
				context.drawText(textRenderer, " ", x, 6, Colors.DARK_GRAY, false);
				x += textRenderer.getWidth(" ");
				context.drawText(textRenderer, child.getDisplayName(), x, 6, Colors.DARK_GRAY, false);
			}
		}

		int curX = 0;
		int curY = 6;
		int tabStartOffset = 68;
		int tabWidth = 72;

		fractal$x = curX - tabWidth;
		fractal$y = curY;
		fractal$x2 = curX + 259;
		boolean rendersOnTheRight = false;
		List<ItemSubGroup> children =  parent.fractal$getChildren();
		for (ItemSubGroup child : parent.fractal$getChildren()) {
			boolean thisChildSelected = child == parent.fractal$getSelectedChild();
			ItemSubGroupStyle style = child.getStyle();
			Identifier subtabTextureID = thisChildSelected
					? rendersOnTheRight ? style.selectedSubtabTextureRight() :  style.selectedSubtabTextureLeft()
					: rendersOnTheRight ? style.unselectedSubtabTextureRight() : style.unselectedSubtabTextureLeft();
			
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, subtabTextureID, curX - tabStartOffset, curY, 72, 11);
			
			int textOffset = thisChildSelected ? 8 : 5; // makes the text pop slightly outwards if selected
			int textColor = child.getStyle().subtabNameTextColor();
			String tabDisplayName = child.getDisplayName().getString();
			if (rendersOnTheRight) {
				for (int i = 0; i < tabDisplayName.length(); i++) {
					char c = tabDisplayName.charAt(i);
					if (c > 0x7F) continue;
					int u = (c % 16) * 4;
					int v = (c / 16) * 6;
					context.drawTexture(RenderPipelines.GUI_TEXTURED, TINYFONT_TEXTURE, curX + 1 - tabStartOffset + textOffset, curY + 3, u, v, 4, 6, 64, 48, textColor);
					curX += 4;
				}
			} else {
				for (int i = tabDisplayName.length() - 1; i >= 0; i--) {
					char c = tabDisplayName.charAt(i);
					if (c > 0x7F) continue;
					int u = (c % 16) * 4;
					int v = (c / 16) * 6;
					context.drawTexture(RenderPipelines.GUI_TEXTURED, TINYFONT_TEXTURE, curX - textOffset, curY + 3, u, v, 4, 6, 64, 48, textColor);
					curX -= 4;
				}
			}
			
			int index = child.getIndexInParent();
			if (index >= LAST_TAB_INDEX_RENDERING_LEFT) {
				if (index == LAST_TAB_INDEX_RENDERING_LEFT) {
					rendersOnTheRight = true;
					curY -= 10 * (LAST_TAB_INDEX_RENDERING_LEFT + 1);
				}
				curX = fractal$x2;
			} else {
				curX = 0;
			}
			curY += 10;
		}

		fractal$h = 11 * Math.min(LAST_TAB_INDEX_RENDERING_LEFT + 1, children.size());
		fractal$h2 = 11 * Math.max(0, children.size() - LAST_TAB_INDEX_RENDERING_LEFT - 1);

		matrices.popMatrix();
		// adjust relative coords to "absolute" ones
		fractal$x  += this.x;
		fractal$x2 += this.x;
		fractal$y  += this.y;
	}
	
	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	public void fractal$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
		ItemGroup selected = selectedTab;
		if (selected instanceof ItemGroupParent parent && !parent.fractal$getChildren().isEmpty()) {
			int x = fractal$x;
			int y = fractal$y;
			int w = 77;
			for (ItemSubGroup child : parent.fractal$getChildren()) {
				if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 11) {
					parent.fractal$setSelectedChild(child);
					
					handler.itemList.clear();
					handler.itemList.addAll(selected.getDisplayStacks());
					
					this.scrollPosition = 0.0F;
					handler.scrollItems(0.0F);
					ci.setReturnValue(true);
					return;
				}
				y += 10;
				
				if(child.getIndexInParent() == LAST_TAB_INDEX_RENDERING_LEFT) {
					x += 259;
					y = fractal$y;
				}
			}
		}
	}
	
	@Override
	public int fractal$getX() {
		return fractal$x;
	}
	
	@Override
	public int fractal$getY() {
		return fractal$y;
	}
	
	@Override
	public int fractal$getH() {
		return fractal$h;
	}
	
	@Override
	public int fractal$getX2() {
		return fractal$x2 - 72;
	}
	
	@Override
	public int fractal$getH2() {
		return fractal$h2;
	}
	
}
