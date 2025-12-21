package de.dafuqs.fractal.mixin;

import de.dafuqs.fractal.api.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(CreativeModeTabs.class)
public class MixinCreativeModeTabs {
	
	@Inject(at = @At("HEAD"), method = "buildAllTabContents")
	private static void updateEntries(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CallbackInfo ci) {
		ItemSubGroup.SUB_GROUPS.forEach((group) -> {
			group.buildContents(itemDisplayParameters);
		});
	}
	
}
