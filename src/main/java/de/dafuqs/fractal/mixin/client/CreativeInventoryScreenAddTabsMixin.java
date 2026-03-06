package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.interfaces.*;
import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.impl.client.SmallFontRenderer;
import net.fabricmc.api.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.*;
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
public abstract class CreativeInventoryScreenAddTabsMixin extends AbstractInventoryScreen<CreativeScreenHandler> implements SubTabLocation, CreativeInventoryScreenAccessor {
	
	@Unique
	private static final int LAST_TAB_INDEX_RENDERING_LEFT = 11;

	@Unique
	private static final Identifier SUBTAB_TEXTURE = new Identifier("fractal", "textures/subtab.png");
	
	public CreativeInventoryScreenAddTabsMixin(CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
	
	@Shadow
	private float scrollPosition;
	
	@Shadow
	private static ItemGroup selectedTab;
	
	private int fractal$y; // tab start y
	private int fractal$x, fractal$h; // left tabs
	private int fractal$x2, fractal$h2; // right tabs
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V"))
	public void fractal$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (selectedTab instanceof ItemGroupParent parent && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
			if (!selectedTab.shouldRenderName()) {
				ItemGroup child = parent.fractal$getSelectedChild();
				int x = context.drawText(textRenderer, selectedTab.getDisplayName(), this.x + 8, this.y + 6, 4210752, false);
				if (child != null) {
					x = context.drawText(textRenderer, " ", x, this.y + 6, 4210752, false);
					x = context.drawText(textRenderer, child.getDisplayName(), x, this.y + 6, 4210752, false);
				}
			}

			int[] pos = {this.x, this.y + 6 + parent.fractal$getTabOffset()};
			int tabStartOffset = 68;
			int tabWidth = 72;
			
			fractal$x = pos[0] - tabWidth;
			fractal$y = pos[1];
			fractal$x2 = pos[0] + 259;
			boolean rendersOnTheRight = false;
			List<ItemSubGroup> children =  parent.fractal$getChildren();
			for (ItemSubGroup child : children) {
				boolean thisChildSelected = child == parent.fractal$getSelectedChild();
				
				context.setShaderColor(1, 1, 1, 1);
				if (child.getBackgroundTexture() == null) {
					int bgV = (thisChildSelected ? 11 : 0) + (rendersOnTheRight ? 22 : 0);
					context.drawTexture(SUBTAB_TEXTURE, pos[0] - tabStartOffset, pos[1], 0, bgV, tabWidth, 11, tabWidth, 44);
				} else {
					int bgV = (thisChildSelected ? 136 + 11 : 136) + (rendersOnTheRight ? 22 : 0);
					context.drawTexture(child.getBackgroundTexture(),  pos[0] - tabStartOffset, pos[1], 24, bgV, tabWidth, 11, 256, 256);
				}
				
				int textOffset = thisChildSelected ? 3 : 0; // makes the text pop slightly outwards
				String tabDisplayName = child.getDisplayName().getString();

				context.setShaderColor(parent.fractal$getTextR(), parent.fractal$getTextG(), parent.fractal$getTextB(), 1);
				if(rendersOnTheRight) {
					SmallFontRenderer.draw(context, tabDisplayName, pos[0] + 1 - tabStartOffset + textOffset, pos[1] + 3, false);
				} else {
					SmallFontRenderer.draw(context, tabDisplayName, pos[0] - textOffset, pos[1] + 3, true);
				}
				
				int index = child.getIndexInParent();
				if(index >= LAST_TAB_INDEX_RENDERING_LEFT) {
					if(index == LAST_TAB_INDEX_RENDERING_LEFT) {
						rendersOnTheRight = true;
						pos[1] -= 10 * (LAST_TAB_INDEX_RENDERING_LEFT + 1);
					}
					pos[0] = fractal$x2;
				} else {
					pos[0] = this.x;
				}
				pos[1] += 10;
			}
			
			fractal$h = 11 * Math.min(LAST_TAB_INDEX_RENDERING_LEFT + 1, children.size());
			fractal$h2 = 11 * Math.max(0, children.size() - LAST_TAB_INDEX_RENDERING_LEFT - 1);

			context.setShaderColor(1, 1, 1, 1);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	public void fractal$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
		ItemGroup selected = selectedTab;
		if (selected instanceof ItemGroupParent parent && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
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
