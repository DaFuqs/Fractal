package de.dafuqs.fractal.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.Event;

public class CreativeSubTabEvent extends Event {

	private final ResourceKey<CreativeModeTab> parentKey;
	private final CreativeModeTab tab;
	private final CreativeSubTab subGroup;
	private final CreativeModeTab.Output itemDisplayBuilder;
	private final CreativeModeTab.ItemDisplayParameters parameters;

	public CreativeSubTabEvent(ResourceKey<CreativeModeTab> parentKey, CreativeModeTab tab, CreativeSubTab subGroup, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output itemDisplayBuilder) {
		this.parentKey = parentKey;
		this.tab = tab;
		this.subGroup = subGroup;
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
		return subGroup;
	}
	
	public CreativeModeTab.Output getItemDisplayBuilder() {
		return itemDisplayBuilder;
	}

	public CreativeModeTab.ItemDisplayParameters getParameters() {
		return parameters;
	}

}
