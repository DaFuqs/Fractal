package de.dafuqs.fractal.mixin.client;

import com.llamalad7.mixinextras.injector.v2.*;
import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenAddTabsMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements ISubTabLocation, CreativeModeInventoryScreenAccessor {
	@Unique
	private static final int LAST_TAB_INDEX_RENDERING_LEFT = 11;
	
	@Unique
	private static final ResourceLocation TINYFONT_TEXTURE = ResourceLocation.fromNamespaceAndPath("fractal", "textures/gui/tinyfont.png");
	
	public CreativeInventoryScreenAddTabsMixin(CreativeModeInventoryScreen.ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
		super(screenHandler, playerInventory, text);
	}
	
	@Shadow
	private float scrollOffs;
	
	@Shadow
	private static CreativeModeTab selectedTab;
	
	@Unique
	private int fractal$y; // tab start y
	@Unique
	private int fractal$x, fractal$h; // left tabs
	@Unique
	private int fractal$x2, fractal$h2; // right tabs
	
	@WrapWithCondition(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"))
	public boolean fractal$wrapParentTitle(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean dropShadow) {
		if (selectedTab instanceof ICreativeTabParent parent && !parent.fractal$getChildren().isEmpty()) {
			CreativeSubTab child = parent.fractal$getSelectedChild();
			return child == null; // Parent title rendering is actually handled in fractal$renderSubTabs.
		}
		return true;
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
	public void fractal$renderSubTabs(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (selectedTab instanceof ICreativeTabParent parent && !parent.fractal$getChildren().isEmpty()) {
			if (selectedTab.showTitle()) {
				CreativeSubTab child = parent.fractal$getSelectedChild();
				int x = this.leftPos + 8;
				if (child.shouldShowParentTitle()) {
					x = graphics.drawString(font, selectedTab.getDisplayName(), x, this.topPos + 6, 4210752, false);
					x = graphics.drawString(font, " ", x, this.topPos + 6, 4210752, false);
				}
				graphics.drawString(font, child.getDisplayName(), x, this.topPos + 6, 4210752, false);
			}
			
			int[] pos = {this.leftPos, this.topPos + 6};
			int tabStartOffset = 68;
			int tabWidth = 72;
			
			fractal$x = pos[0] - tabWidth;
			fractal$y = pos[1];
			fractal$x2 = pos[0] + 259;
			boolean rendersOnTheRight = false;
			List<CreativeSubTab> children = parent.fractal$getChildren();
			for (CreativeSubTab child : parent.fractal$getChildren()) {
				boolean thisChildSelected = child == parent.fractal$getSelectedChild();
				CreativeSubTabStyle style = child.getStyle();
				ResourceLocation subtabTextureID = thisChildSelected
						? rendersOnTheRight ? style.selectedSubtabTextureRight() : style.selectedSubtabTextureLeft()
						: rendersOnTheRight ? style.unselectedSubtabTextureRight() : style.unselectedSubtabTextureLeft();
				
				graphics.setColor(1, 1, 1, 1);
				assert subtabTextureID != null; // This will only fail if a user explicitly sets the subtab texture to null.
				graphics.blitSprite(subtabTextureID, pos[0] - tabStartOffset, pos[1], 72, 11);
				
				int textOffset = thisChildSelected ? 8 : 5; // makes the text pop slightly outwards if selected
				String tabDisplayName = child.getDisplayName().getString();
				graphics.setColor(0, 0, 0, 1);
				if (rendersOnTheRight) {
					for (int i = 0; i < tabDisplayName.length(); i++) {
						char c = tabDisplayName.charAt(i);
						if (c > 0x7F) continue;
						int u = (c % 16) * 4;
						int v = (c / 16) * 6;
						graphics.blit(TINYFONT_TEXTURE, pos[0] + 1 - tabStartOffset + textOffset, pos[1] + 3, u, v, 4, 6, 64, 48);
						pos[0] += 4;
					}
				} else {
					for (int i = tabDisplayName.length() - 1; i >= 0; i--) {
						char c = tabDisplayName.charAt(i);
						if (c > 0x7F) continue;
						int u = (c % 16) * 4;
						int v = (c / 16) * 6;
						graphics.blit(TINYFONT_TEXTURE, pos[0] - textOffset, pos[1] + 3, u, v, 4, 6, 64, 48);
						pos[0] -= 4;
					}
				}
				
				int index = child.getIndexInParent();
				if (index >= LAST_TAB_INDEX_RENDERING_LEFT) {
					if (index == LAST_TAB_INDEX_RENDERING_LEFT) {
						rendersOnTheRight = true;
						pos[1] -= 10 * (LAST_TAB_INDEX_RENDERING_LEFT + 1);
					}
					pos[0] = fractal$x2;
				} else {
					pos[0] = this.leftPos;
				}
				pos[1] += 10;
			}
			
			fractal$h = 11 * Math.min(LAST_TAB_INDEX_RENDERING_LEFT + 1, children.size());
			fractal$h2 = 11 * Math.max(0, children.size() - LAST_TAB_INDEX_RENDERING_LEFT - 1);
			
			graphics.setColor(1, 1, 1, 1);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	public void fractal$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
		CreativeModeTab selected = selectedTab;
		if (selected instanceof ICreativeTabParent parent && !parent.fractal$getChildren().isEmpty()) {
			int x = fractal$x;
			int y = fractal$y;
			int w = 77;
			for (CreativeSubTab child : parent.fractal$getChildren()) {
				if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 11) {
					parent.fractal$setSelectedChild(child);
					
					menu.items.clear();
					menu.items.addAll(child.displayItems);
					
					this.scrollOffs = 0.0F;
					menu.scrollTo(0.0F);
					ci.setReturnValue(true);
					return;
				}
				y += 10;
				
				if (child.getIndexInParent() == LAST_TAB_INDEX_RENDERING_LEFT) {
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
