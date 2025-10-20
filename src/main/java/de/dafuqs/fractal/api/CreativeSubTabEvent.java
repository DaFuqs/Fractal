package de.dafuqs.fractal.api;

import net.minecraft.world.item.*;
import net.neoforged.bus.api.*;

public class CreativeSubTabEvent extends Event {
	private final CreativeModeTab tab;
	private final CreativeSubTab subGroup;
	private final CreativeModeTab.Output itemDisplayBuilder;
	
	public CreativeSubTabEvent(CreativeModeTab tab, CreativeSubTab subGroup, CreativeModeTab.Output itemDisplayBuilder) {
		this.tab = tab;
		this.subGroup = subGroup;
		this.itemDisplayBuilder = itemDisplayBuilder;
	}
	
	public CreativeModeTab getTab() {
		return tab;
	}
	
	public CreativeSubTab subGroup() {
		return subGroup;
	}
	
	public CreativeModeTab.Output getItemDisplayBuilder() {
		return itemDisplayBuilder;
	}
}
