package de.dafuqs.fractal.api;

import net.fabricmc.fabric.api.event.*;
import net.fabricmc.fabric.api.creativetab.v1.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CreativeSubTab extends CreativeModeTab {
	
	public static final List<CreativeSubTab> SUB_GROUPS = new ArrayList<>();
	
	protected final CreativeModeTab parent;
	protected final Identifier identifier;
	protected final int indexInParent;
	protected final CreativeSubTabStyle style;
	
	public static final CreativeSubTabStyle DEFAULT_STYLE = new CreativeSubTabStyle.Builder().build();
	
	protected CreativeSubTab(CreativeModeTab parent, Identifier identifier, Component displayName, DisplayItemsGenerator displayItemsGenerator, CreativeSubTabStyle style) {
		super(parent.row(), parent.column(), parent.getType(), displayName, () -> ItemStack.EMPTY, displayItemsGenerator);
		this.style = style;
		this.identifier = identifier;
		this.parent = parent;

        this.indexInParent = parent.fractal$getChildren().size();
		parent.fractal$getChildren().add(this);
		if (parent.fractal$getSelectedChild() == null) {
			parent.fractal$setSelectedChild(this);
		}
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * 100 % the vanilla code, but the check for registered item groups was removed
	 * (we do not want to register our subgroups, so other mods do not pick them up)
	 */

	@Override
	public void buildContents(ItemDisplayParameters context) {
		ItemDisplayBuilder entries = new ItemDisplayBuilder(this, context.enabledFeatures());
		this.displayItemsGenerator.accept(context, entries);
		this.displayItems = entries.tabContents;
		this.displayItemsSearchTab = entries.searchTabContents;
		
		triggerEntryUpdateEvent(context);
		
		this.parent.displayItemsSearchTab.addAll(this.displayItemsSearchTab);
		this.parent.displayItems.addAll(this.displayItems);
	}
	
	// Custom impl of the default fabric event trigger at
	// https://github.com/FabricMC/fabric/blob/95a137205b0b47b97b1ab35ac09a3430641137de/fabric-item-group-api-v1/src/main/java/net/fabricmc/fabric/mixin/itemgroup/ItemGroupMixin.java#L55
	protected void triggerEntryUpdateEvent(ItemDisplayParameters context) {
		final ResourceKey<CreativeModeTab> registryKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(parent).orElseThrow(() -> new IllegalStateException("Unregistered parent item group : " + parent));
		
		// Do not modify special item groups (except Operator Blocks) at all.
		// Special item groups include Saved Hotbars, Search, and Survival Inventory.
		// Note, search gets modified as part of the parent item group.
		if (parent.isAlignedRight() && registryKey != CreativeModeTabs.OP_BLOCKS) return;
		
		// Sanity check for the injection point. It should be after these fields are set.
		Objects.requireNonNull(displayItems, "displayItems");
		Objects.requireNonNull(displayItemsSearchTab, "searchTabStacks");
		
		// Convert the entries to lists
		List<ItemStack> mutableDisplayStacks = new LinkedList<>(displayItems);
		List<ItemStack> mutableSearchTabStacks = new LinkedList<>(displayItemsSearchTab);
		FabricCreativeModeTabOutput entries = new FabricCreativeModeTabOutput(context, mutableDisplayStacks, mutableSearchTabStacks); // scary ApiStatus.Internal usage

		final Event<CreativeSubTabEvent.ModifyEntries> modifyEntriesEvent = CreativeSubTabEvent.modifyEntriesEvent(identifier);
		
		if (modifyEntriesEvent != null) {
			modifyEntriesEvent.invoker().modifyEntries(entries);
		}
		
		// Now trigger the global event
		if (registryKey != CreativeModeTabs.OP_BLOCKS || context.hasPermissions()) {
			CreativeSubTabEvent.MODIFY_ENTRIES_ALL.invoker().modifyEntries(this, entries);
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
		
		protected CreativeModeTab parent;
		protected final Identifier identifier;
		protected Component displayName;
		protected CreativeSubTabStyle style = DEFAULT_STYLE;
		private DisplayItemsGenerator displayItemsGenerator;
		
		public Builder(CreativeModeTab parent, Identifier identifier, Component displayName) {
			this.parent = parent;
			this.identifier = identifier;
			this.displayName = displayName;
		}
		
		public Builder styled(CreativeSubTabStyle style) {
			this.style = style;
			return this;
		}
		
		public Builder entries(DisplayItemsGenerator displayItemsGenerator) {
			this.displayItemsGenerator = displayItemsGenerator;
			return this;
		}
		
		public CreativeSubTab build() {
			CreativeSubTab subGroup = new CreativeSubTab(parent, identifier, displayName, displayItemsGenerator, style);
			SUB_GROUPS.add(subGroup);
			return subGroup;
		}
	}
	
}
