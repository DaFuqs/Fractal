package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.CreativeSubTab;
import de.dafuqs.fractal.api.CreativeSubTabStyle;
import de.dafuqs.fractal.interfaces.ICreativeTabParent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.CreativeTabsScreenPage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@OnlyIn(Dist.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenCustomTextureMixin {
	@Shadow
	private static CreativeModeTab selectedTab;
	
	@Shadow
	protected abstract boolean canScroll();

	@Shadow
	private CreativeTabsScreenPage currentPage;
	@Unique
	private CreativeModeTab fractal$renderedItemGroup;
	
	// BACKGROUND
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
	private ResourceLocation injectCustomGroupTexture(ResourceLocation original) {
		CreativeSubTab subGroup = fractal$getSelectedSubGroup();
		return (subGroup == null || subGroup.getStyle().backgroundTexture() == null) ? original : subGroup.getStyle().backgroundTexture();
	}
	
	// SCROLLBAR
	@ModifyArgs(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
	private void injectCustomScrollbarTexture(Args args) {
		CreativeSubTab subGroup = fractal$getSelectedSubGroup();
		if (subGroup != null) {
			ResourceLocation scrollbarTextureID = this.canScroll() ? subGroup.getStyle().enabledScrollbarTexture() : subGroup.getStyle().disabledScrollbarTexture();
			if (scrollbarTextureID != null) {
				args.set(0, scrollbarTextureID);
			}
		}
	}
	
	// ICON
	@Inject(method = "renderTabButton", at = @At("HEAD"))
	private void captureContextGroup(GuiGraphics context, CreativeModeTab group, CallbackInfo ci) {
		this.fractal$renderedItemGroup = group;
	}
	
	@ModifyArg(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
	private ResourceLocation injectCustomTabTexture(ResourceLocation original) {
		CreativeSubTab subGroup = fractal$getRenderedSubGroup();
		if (subGroup == null) {
			return original;
		}

		boolean isSelected = selectedTab == this.fractal$renderedItemGroup;

		CreativeSubTabStyle style = subGroup.getStyle();
		ResourceLocation texture = this.currentPage.isTop(this.fractal$renderedItemGroup)
				? isSelected ? this.fractal$renderedItemGroup.column() == 0 ? style.tabTopFirstSelectedTexture() : style.tabTopSelectedTexture() : style.tabTopUnselectedTexture()
				: isSelected ? this.fractal$renderedItemGroup.column() == 0 ? style.tabBottomFirstSelectedTexture() : style.tabBottomSelectedTexture() : style.tabBottomUnselectedTexture();
		
		return texture == null ? original : texture;
	}
	
	@Unique
	private @Nullable CreativeSubTab fractal$getRenderedSubGroup() {
		return fractal$renderedItemGroup instanceof ICreativeTabParent ICreativeTabParent ? ICreativeTabParent.fractal$getSelectedChild() : null;
	}
	
	@Unique
	private @Nullable CreativeSubTab fractal$getSelectedSubGroup() {
		return selectedTab instanceof ICreativeTabParent ICreativeTabParent ? ICreativeTabParent.fractal$getSelectedChild() : null;
	}
}
