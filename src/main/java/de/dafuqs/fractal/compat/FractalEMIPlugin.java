package de.dafuqs.fractal.compat;

import de.dafuqs.fractal.interfaces.*;
import de.dafuqs.fractal.mixin.client.*;
import dev.emi.emi.api.*;
import dev.emi.emi.api.widget.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.world.item.*;

@SuppressWarnings("unused")
@EmiEntrypoint
public class FractalEMIPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		registry.addExclusionArea(CreativeModeInventoryScreen.class, (screen, out) ->
		{
			if (screen != null) {
				CreativeModeTab selected = CreativeModeInventoryScreenAccessor.fractal$getSelectedTab();
				if (selected instanceof ICreativeTabParent parent && screen instanceof ISubTabLocation stl && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
					out.accept(new Bounds(stl.fractal$getX(), stl.fractal$getY(), 72, stl.fractal$getH()));
					out.accept(new Bounds(stl.fractal$getX2(), stl.fractal$getY(), 72, stl.fractal$getH2()));
				}
			}
		});
	}
}
