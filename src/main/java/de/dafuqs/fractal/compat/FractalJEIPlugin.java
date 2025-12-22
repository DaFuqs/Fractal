package de.dafuqs.fractal.compat;

import de.dafuqs.fractal.interfaces.*;
import de.dafuqs.fractal.mixin.client.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.handlers.*;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;

import java.util.*;

@SuppressWarnings("unused")
@JeiPlugin
public class FractalJEIPlugin implements IModPlugin {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("fractal", "jei_plugin");
	
	@Override
	public Identifier getPluginUid() {
		return ID;
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGenericGuiContainerHandler(CreativeModeInventoryScreen.class, new IGuiContainerHandler<CreativeModeInventoryScreen>() {
			@Override
			public List<Rect2i> getGuiExtraAreas(CreativeModeInventoryScreen screen) {
				CreativeModeTab selected = CreativeModeInventoryScreenAccessor.fractal$getSelectedTab();
				if (selected instanceof ICreativeTabParent parent && screen instanceof ISubTabLocation stl && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
					return List.of(
							new Rect2i(stl.fractal$getX(), stl.fractal$getY(), 72, stl.fractal$getH()),
							new Rect2i(stl.fractal$getX2(), stl.fractal$getY(), 72, stl.fractal$getH2())
					);
				}
				return List.of();
			}
		});
	}
}
