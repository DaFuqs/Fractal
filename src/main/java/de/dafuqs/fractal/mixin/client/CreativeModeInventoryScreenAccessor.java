package de.dafuqs.fractal.mixin.client;

import net.fabricmc.api.*;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Environment(EnvType.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
	
	@Accessor("selectedTab")
	static CreativeModeTab fractal$getSelectedGroup() {
		throw new AssertionError();
	}
	
}