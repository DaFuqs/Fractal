package de.dafuqs.fractal.api;

import net.minecraft.core.registries.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.*;

import java.util.*;

public class CreativeSubTab extends CreativeModeTab {
	public static final List<CreativeSubTab> SUBTABS = new ArrayList<>();
	
	protected final CreativeModeTab parent;
	protected final ResourceLocation identifier;
	protected final int indexInParent;
	protected final CreativeSubTabStyle style;
	protected boolean showParentTitle = true;
	
	public static final CreativeSubTabStyle DEFAULT_STYLE = new CreativeSubTabStyle.Builder().build();
	
	protected CreativeSubTab(CreativeModeTab parent, ResourceLocation identifier, Component displayName, DisplayItemsGenerator entryCollector, CreativeSubTabStyle style) {
		//noinspection DataFlowIssue
		super(
				parent.row(),
				parent.column(),
				parent.getType(),
				displayName,
				() -> ItemStack.EMPTY,
				entryCollector,
				null,
				false,
				0,
				null,
				0xFFFFFFFF,
				0xFFFFFFFF,
				List.of(),
				List.of()
		);
		
		this.style = style;
		this.identifier = identifier;
		this.parent = parent;
		
		this.indexInParent = parent.fractal$getChildren().size();
		parent.fractal$getChildren().add(this);
		if (parent.fractal$getSelectedChild() == null) {
			parent.fractal$setSelectedChild(this);
		}
	}
	
	@SuppressWarnings("unused")
	public ResourceLocation getIdentifier() {
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
		final ResourceKey<CreativeModeTab> parentKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(parent).orElseThrow(() -> new IllegalStateException("Unregistered parent item group : " + parent));

		this.displayItemsGenerator.accept(parameters, entries);
		NeoForge.EVENT_BUS.post(new CreativeSubTabEvent(parentKey, parent, this, parameters, entries));

		// Convert the stacks back to sets after the events had a chance to modify them
		this.displayItems = entries.tabContents;
		this.displayItemsSearchTab = entries.searchTabContents;
		this.parent.displayItemsSearchTab.addAll(this.displayItemsSearchTab);
		this.parent.displayItems.addAll(this.displayItems);
	}

	@Override
	public ItemStack getIconItem() {
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
		protected CreativeModeTab parent;
		protected final ResourceLocation identifier;
		protected Component displayName;
		protected CreativeSubTabStyle style = DEFAULT_STYLE;
		protected boolean showParentTitle = true;
		private DisplayItemsGenerator entryCollector;
		
		public Builder(CreativeModeTab parent, ResourceLocation identifier, Component displayName) {
			this.parent = parent;
			this.identifier = identifier;
			this.displayName = displayName;
		}
		
		public Builder styled(CreativeSubTabStyle style) {
			this.style = style;
			return this;
		}
		
		public Builder entries(DisplayItemsGenerator entryCollector) {
			this.entryCollector = entryCollector;
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
			CreativeSubTab subtab = new CreativeSubTab(parent, identifier, displayName, entryCollector, style);
			subtab.showParentTitle = this.showParentTitle;
			SUBTABS.add(subtab);
			return subtab;
		}
	}
}
