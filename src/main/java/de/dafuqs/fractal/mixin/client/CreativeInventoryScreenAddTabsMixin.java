package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.fabricmc.api.*;
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
		if (selectedTab instanceof ItemGroupParent parent && !parent.fractal$getChildren().isEmpty()) {
			if (!selectedTab.shouldRenderName()) {
				ItemGroup child = parent.fractal$getSelectedChild();
				int x = context.drawText(textRenderer, selectedTab.getDisplayName(), this.x + 8, this.y + 6, 4210752, false);
				if (child != null) {
					x = context.drawText(textRenderer, " ", x, this.y + 6, 4210752, false);
					context.drawText(textRenderer, child.getDisplayName(), x, this.y + 6, 4210752, false);
				}
			}
			
			int[] pos = {this.x, this.y + 6};
			int tabStartOffset = 68;
			int tabWidth = 72;
			
			fractal$x = pos[0] - tabWidth;
			fractal$y = pos[1];
			fractal$x2 = pos[0] + 259;
			boolean rendersOnTheRight = false;
			List<ItemSubGroup> children =  parent.fractal$getChildren();
			for (ItemSubGroup child : parent.fractal$getChildren()) {

				boolean thisChildSelected = child == parent.fractal$getSelectedChild();
				ItemSubGroupStyle style = child.getStyle();
				Identifier subtabTextureID = thisChildSelected
						? rendersOnTheRight ? style.selectedSubtabTextureRight() :  style.selectedSubtabTextureLeft()
						: rendersOnTheRight ? style.unselectedSubtabTextureRight() : style.unselectedSubtabTextureLeft();
				
				context.drawGuiTexture(RenderLayer::getGuiTextured, subtabTextureID, pos[0] - tabStartOffset, pos[1], 72, 11);
				
				int textOffset = thisChildSelected ? 8 : 5; // makes the text pop slightly outwards if selected
				int textColor = child.getStyle().subtabNameTextColor();
				String tabDisplayName = child.getDisplayName().getString();
				if(rendersOnTheRight) {
					context.draw(vertexConsumerProvider -> {
						for (int i = 0; i < tabDisplayName.length(); i++) {
							char c = tabDisplayName.charAt(i);
							if (c > 0x7F) continue;
							int u = (c % 16) * 4;
							int v = (c / 16) * 6;
							context.drawTexture(RenderLayer::getGuiTextured, TINYFONT_TEXTURE, pos[0] + 1 - tabStartOffset + textOffset, pos[1] + 3, u, v, 4, 6, 64, 48, textColor);
							pos[0] += 4;
						}
					});
				} else {
					context.draw(vertexConsumerProvider -> {
						for (int i = tabDisplayName.length() - 1; i >= 0; i--) {
							char c = tabDisplayName.charAt(i);
							if (c > 0x7F) continue;
							int u = (c % 16) * 4;
							int v = (c / 16) * 6;
							context.drawTexture(RenderLayer::getGuiTextured, TINYFONT_TEXTURE, pos[0] - textOffset, pos[1] + 3, u, v, 4, 6, 64, 48, textColor);
							pos[0] -= 4;
						}
					});
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
		}
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
					
					this.handler.itemList.clear();
					this.handler.itemList.addAll(selected.getDisplayStacks());
					
					this.scrollPosition = 0.0F;
					this.handler.scrollItems(0.0F);
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
