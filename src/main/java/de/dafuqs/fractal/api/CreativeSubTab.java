package de.dafuqs.fractal.api;

import net.minecraft.core.registries.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CreativeSubTab extends CreativeModeTab {
	public static final List<CreativeSubTab> SUBTABS = new ArrayList<>();
	
	protected final CreativeModeTab parent;
	protected final Identifier identifier;
	protected final int indexInParent;
	protected final CreativeSubTabStyle style;
	protected final boolean showParentTitle;
	
	public static final CreativeSubTabStyle DEFAULT_STYLE = new CreativeSubTabStyle.Builder().build();
	
	protected CreativeSubTab(CreativeSubTab.Builder builder) {
		super(builder.base);
		this.style = builder.style;
		this.identifier = builder.identifier;
		this.parent = builder.parent;
		this.showParentTitle = builder.showParentTitle;
		
		this.indexInParent = parent.fractal$getChildren().size();
		parent.fractal$getChildren().add(this);
		if (parent.fractal$getSelectedChild() == null) {
			parent.fractal$setSelectedChild(this);
		}
	}
	
	@SuppressWarnings("unused")
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public boolean shouldShowParentTitle() {
		return showParentTitle;
	}
	
	/**
	 * 100 % the vanilla code, but the check for registered item groups was removed
	 * (we do not want to register our subgroups, so other mods do not pick them up)
	 */
	@Override
	public void buildContents(ItemDisplayParameters parameters) {
		DefaultStackEntryCollector entries = new DefaultStackEntryCollector(this, parameters.enabledFeatures());
		this.displayItemsGenerator.accept(parameters, entries);
		this.displayItems = entries.parentTabStacks;
		this.displayItemsSearchTab = entries.searchTabStacks;
		
		triggerEntryUpdateEvent(parameters, entries);
		
		this.parent.displayItemsSearchTab.addAll(this.displayItemsSearchTab);
		this.parent.displayItems.addAll(this.displayItems);
	}
	
	// Custom impl of the default fabric event trigger at
	// https://github.com/FabricMC/fabric/blob/95a137205b0b47b97b1ab35ac09a3430641137de/fabric-item-group-api-v1/src/main/java/net/fabricmc/fabric/mixin/itemgroup/ItemGroupMixin.java#L55
	protected void triggerEntryUpdateEvent(ItemDisplayParameters context, DefaultStackEntryCollector entries) {
		final ResourceKey<CreativeModeTab> registryKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(parent).orElseThrow(() -> new IllegalStateException("Unregistered parent item group : " + parent));
		
		// Do not modify special item groups (except Operator Blocks) at all.
		// Special item groups include Saved Hotbars, Search, and Survival Inventory.
		// Note, search gets modified as part of the parent item group.
		if (parent.isAlignedRight() && registryKey != CreativeModeTabs.OP_BLOCKS) {
			return;
		}
		
		// Sanity check for the injection point. It should be after these fields are set.
		Objects.requireNonNull(displayItems, "displayItems");
		Objects.requireNonNull(displayItemsSearchTab, "displayItemsSearchTab");
		
		// Convert the entries to lists
		List<ItemStack> mutableDisplayStacks = new LinkedList<>(displayItems);
		List<ItemStack> mutableSearchTabStacks = new LinkedList<>(displayItemsSearchTab);

//		EventHooks.onCreativeModeTabBuildContents(parent, registryKey, this.displayItemsGenerator, context, entries);
		
		if (registryKey != CreativeModeTabs.OP_BLOCKS || context.hasPermissions()) {
			NeoForge.EVENT_BUS.post(new CreativeSubTabEvent(parent, this, entries));
		}
		
		// Convert the stacks back to sets after the events had a chance to modify them
		displayItems.clear();
		displayItems.addAll(mutableDisplayStacks);
		
		displayItemsSearchTab.clear();
		displayItemsSearchTab.addAll(mutableSearchTabStacks);
	}
	
	@Override
	public @NotNull ItemStack getIconItem() {
		return ItemStack.EMPTY;
	}
	
	@SuppressWarnings("unused")
	public CreativeModeTab getParent() {
		return parent;
	}
	
	public int getIndexInParent() {
		return indexInParent;
	}
	
	public CreativeSubTabStyle getStyle() {
		return style;
	}
	
	public static class Builder {
		CreativeModeTab.Builder base;
		protected CreativeModeTab parent;
		protected final Identifier identifier;
		protected Component displayName;
		protected CreativeSubTabStyle style = DEFAULT_STYLE;
		protected boolean showParentTitle = true;
		
		public Builder(CreativeModeTab.Builder base, CreativeModeTab parent, Identifier identifier, Component displayName) {
			this.base = base;
			this.parent = parent;
			this.identifier = identifier;
			this.displayName = displayName;
		}
		
		public Builder styled(CreativeSubTabStyle style) {
			this.style = style;
			return this;
		}
		
		public Builder setShouldShowParentTitle(boolean value) {
			this.showParentTitle = value;
			return this;
		}
		
		public Builder hideParentTitle() {
			return setShouldShowParentTitle(false);
		}
		
		public CreativeSubTab build() {
			CreativeSubTab subtab = new CreativeSubTab(this);
			SUBTABS.add(subtab);
			return subtab;
		}
	}
}
