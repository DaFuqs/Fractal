package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.fabricmc.api.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenCustomTextureMixin {
	
	@Shadow
	private static ItemGroup selectedTab;
	
	@Shadow protected abstract boolean hasScrollbar();
	
	@Unique
	private ItemGroup fractal$renderedItemGroup;
	
	// BACKGROUND
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIII)V"))
	private Identifier injectCustomGroupTexture(Identifier original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		return (subGroup == null || subGroup.getStyle().backgroundTexture() == null) ? original : subGroup.getStyle().backgroundTexture();
	}
	
	// SCROLLBAR
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIII)V"))
	private Identifier injectCustomScrollbarTexture(Identifier original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if(subGroup != null) {
			Identifier scrollbarTextureID = this.hasScrollbar() ? subGroup.getStyle().enabledScrollbarTexture() : subGroup.getStyle().disabledScrollbarTexture();
			if(scrollbarTextureID != null) {
				return scrollbarTextureID;
			}
		}
		return original;
	}
	
	// ICON
	@Inject(method = "renderTabIcon", at = @At("HEAD"))
	private void captureContextGroup(DrawContext context, ItemGroup group, CallbackInfo ci) {
		this.fractal$renderedItemGroup = group;
	}
	
	@ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
	private Identifier injectCustomTabTexture(Identifier original) {
		ItemSubGroup subGroup = getRenderedSubGroup();
		if(subGroup == null) {
			return original;
		}
		ItemSubGroupStyle style = subGroup.getStyle();
		if(style == null) {
			return original;
		}
		
		boolean onTop = this.fractal$renderedItemGroup.getRow() == ItemGroup.Row.TOP;
		boolean isSelected = selectedTab == this.fractal$renderedItemGroup;
		
		Identifier texture = onTop
				? isSelected ? this.fractal$renderedItemGroup.getColumn() == 0 ? style.tabTopFirstSelectedTexture() : style.tabTopSelectedTexture() : style.tabTopUnselectedTexture()
				: isSelected ? this.fractal$renderedItemGroup.getColumn() == 0 ? style.tabBottomFirstSelectedTexture() : style.tabBottomSelectedTexture() : style.tabBottomUnselectedTexture();
		
		return texture == null ? original : texture;
	}
	
	@Unique
	private @Nullable ItemSubGroup getRenderedSubGroup() {
		return fractal$renderedItemGroup instanceof ItemGroupParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
	}
	
	@Unique
	private @Nullable ItemSubGroup getSelectedSubGroup() {
		return selectedTab instanceof ItemGroupParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
	}
	
}