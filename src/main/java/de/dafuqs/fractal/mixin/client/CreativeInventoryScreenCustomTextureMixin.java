package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.CreativeSubTab;
import de.dafuqs.fractal.api.CreativeSubTabStyle;
import de.dafuqs.fractal.interfaces.ICreativeTabParent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenCustomTextureMixin {
	
	@Shadow
	private static CreativeModeTab selectedTab;
	
	@Shadow
	protected abstract boolean canScroll();
	
	@Unique
	private CreativeModeTab fractal$renderedItemGroup;
	
	// BACKGROUND
	@ModifyArg(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"))
	private Identifier injectCustomGroupTexture(Identifier original) {
		CreativeSubTab subGroup = fractal$getSelectedSubGroup();
		return (subGroup == null || subGroup.getStyle().backgroundTexture() == null) ? original : subGroup.getStyle().backgroundTexture();
	}
	
	// SCROLLBAR
	@ModifyArg(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
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
	@Inject(method = "extractTabButton", at = @At("HEAD"))
	private void captureContextGroup(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CreativeModeTab creativeModeTab, CallbackInfo ci) {
		this.fractal$renderedItemGroup = creativeModeTab;
	}

	@ModifyArg(method = "extractTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
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
