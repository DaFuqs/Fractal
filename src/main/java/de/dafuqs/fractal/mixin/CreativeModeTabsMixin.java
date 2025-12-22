package de.dafuqs.fractal.mixin;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(CreativeModeTabs.class)
public abstract class CreativeModeTabsMixin implements ISubTabLocation {
	@Inject(at = @At("HEAD"), method = "buildAllTabContents")
	private static void updateEntries(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CallbackInfo ci) {
		CreativeSubTab.SUBTABS.forEach((group) -> {
			group.buildContents(itemDisplayParameters);
		});
	}
}
