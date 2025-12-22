package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@OnlyIn(Dist.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenCustomTextureMixin {
	
	@Shadow
	private static CreativeModeTab selectedTab;
	
	@Shadow
	protected abstract boolean canScroll();
	
	@Unique
	private CreativeModeTab fractal$renderedItemGroup;
	
	// BACKGROUND
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"))
	private Identifier injectCustomGroupTexture(Identifier original) {
		CreativeSubTab subGroup = fractal$getSelectedSubGroup();
		return (subGroup == null || subGroup.getStyle().backgroundTexture() == null) ? original : subGroup.getStyle().backgroundTexture();
	}
	
	// SCROLLBAR
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"))
	private Identifier injectCustomScrollbarTexture(Identifier original) {
		CreativeSubTab subGroup = fractal$getSelectedSubGroup();
		if (subGroup != null) {
			Identifier scrollbarTextureID = this.canScroll() ? subGroup.getStyle().enabledScrollbarTexture() : subGroup.getStyle().disabledScrollbarTexture();
			if (scrollbarTextureID != null) {
				return scrollbarTextureID;
			}
		}
		return original;
	}
	
	// ICON
	@Inject(method = "renderTabButton", at = @At("HEAD"))
	private void captureContextGroup(GuiGraphics guiGraphics, int i, int j, CreativeModeTab creativeModeTab, CallbackInfo ci) {
		this.fractal$renderedItemGroup = creativeModeTab;
	}
	
	@ModifyArg(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	private Identifier injectCustomTabTexture(Identifier original) {
		CreativeSubTab subGroup = fractal$getRenderedSubGroup();
		if (subGroup == null) {
			return original;
		}
		CreativeSubTabStyle style = subGroup.getStyle();
		if (style == null) {
			return original;
		}
		
		boolean onTop = this.fractal$renderedItemGroup.row() == CreativeModeTab.Row.TOP;
		boolean isSelected = selectedTab == this.fractal$renderedItemGroup;
		
		Identifier texture = onTop
				? isSelected ? this.fractal$renderedItemGroup.column() == 0 ? style.tabTopFirstSelectedTexture() : style.tabTopSelectedTexture() : style.tabTopUnselectedTexture()
				: isSelected ? this.fractal$renderedItemGroup.column() == 0 ? style.tabBottomFirstSelectedTexture() : style.tabBottomSelectedTexture() : style.tabBottomUnselectedTexture();
		
		return texture == null ? original : texture;
	}
	
	@Unique
	private @Nullable CreativeSubTab fractal$getRenderedSubGroup() {
		return fractal$renderedItemGroup instanceof ICreativeTabParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
	}
	
	@Unique
	private @Nullable CreativeSubTab fractal$getSelectedSubGroup() {
		return selectedTab instanceof ICreativeTabParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
	}
	
}
