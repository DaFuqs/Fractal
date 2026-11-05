package de.dafuqs.fractal.compat;

import de.dafuqs.fractal.interfaces.*;
import de.dafuqs.fractal.mixin.client.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.plugins.*;
import me.shedaniel.rei.api.client.registry.screen.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.world.item.*;

import java.util.*;

public class FractalREIPlugin implements REIClientPlugin {
	
	@Override
	public void registerExclusionZones(ExclusionZones zones) {
		zones.register(CreativeModeInventoryScreen.class, (screen) -> {
			CreativeModeTab selected = CreativeModeInventoryScreenAccessor.fractal$getSelectedGroup();
			if (selected instanceof ICreativeTabParent parent && screen instanceof ISubTabLocation stl && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
				return List.of(
						new Rectangle(stl.fractal$getX(), stl.fractal$getY(), 72, stl.fractal$getH()),
						new Rectangle(stl.fractal$getX2(), stl.fractal$getY(), 72, stl.fractal$getH2())
				);
			}
			return List.of();
		});
	}
	
}