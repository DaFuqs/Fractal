package de.dafuqs.fractal.api;

import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.*;

public class CreativeSubTabEvent extends Event {

	private final ResourceKey<CreativeModeTab> parentKey;
	private final CreativeModeTab tab;
	private final CreativeSubTab subTab;
	private final CreativeModeTab.Output itemDisplayBuilder;
	private final CreativeModeTab.ItemDisplayParameters parameters;

	public CreativeSubTabEvent(ResourceKey<CreativeModeTab> parentKey, CreativeModeTab tab, CreativeSubTab subTab, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output itemDisplayBuilder) {
		this.parentKey = parentKey;
		this.tab = tab;
		this.subTab = subTab;
		this.itemDisplayBuilder = itemDisplayBuilder;
		this.parameters = parameters;
	}

	public ResourceKey<CreativeModeTab> getParentKey() {
		return parentKey;
	}
	
	public CreativeModeTab getTab() {
		return tab;
	}
	
	public CreativeSubTab subGroup() {
		return subTab;
	}
	
	public CreativeModeTab.Output getItemDisplayBuilder() {
		return itemDisplayBuilder;
	}

	public CreativeModeTab.ItemDisplayParameters getParameters() {
		return parameters;
	}

}
