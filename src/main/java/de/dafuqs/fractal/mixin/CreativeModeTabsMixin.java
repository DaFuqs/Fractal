package de.dafuqs.fractal.mixin;

import de.dafuqs.fractal.api.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(CreativeModeTabs.class)
public abstract class CreativeModeTabsMixin {
	@Inject(at = @At("HEAD"), method = "buildAllTabContents")
	private static void buildAllTabContents(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
		CreativeSubTab.SUBTABS.forEach(it -> it.buildContents(parameters));
	}
}
