package de.dafuqs.fractal.mixin.client;

import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@OnlyIn(Dist.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
	@Accessor("selectedTab")
	static CreativeModeTab fractal$getSelectedTab() {
		throw new AssertionError();
	}
}
