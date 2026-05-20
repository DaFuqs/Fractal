package de.dafuqs.fractal.api;

import net.minecraft.world.flag.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;

import java.util.*;

public class DefaultStackEntryCollector implements CreativeModeTab.Output {
	public final Collection<ItemStack> tabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
	public final Set<ItemStack> searchTabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
	private final CreativeModeTab tab;
	private final FeatureFlagSet featureFlagSet;

	public DefaultStackEntryCollector(CreativeModeTab tab, FeatureFlagSet featureFlagSet) {
		this.tab = tab;
		this.featureFlagSet = featureFlagSet;
	}
	
	@Override
	public void accept(ItemLike item, CreativeModeTab.TabVisibility visibility) {
		this.accept(item.asItem().getDefaultInstance(), visibility);
	}
	
	@Override
	public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
		if (stack.getCount() != 1) {
			throw new IllegalArgumentException("Stack size must be exactly 1");
		} else {
			if (this.tabContents.contains(stack) && visibility != CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY) {
				throw new IllegalStateException("Accidentally adding the same item stack twice " + stack.getHoverName().getString() + " to a Creative Mode Tab: " + this.tab.getDisplayName().getString());
			} else {
				if (stack.getItem().isEnabled(this.featureFlagSet)) {
					switch (visibility) {
						case PARENT_AND_SEARCH_TABS -> {
							this.tabContents.add(stack);
							this.searchTabContents.add(stack);
						}
						case PARENT_TAB_ONLY -> this.tabContents.add(stack);
						case SEARCH_TAB_ONLY -> this.searchTabContents.add(stack);
					}
				}
			}
		}
	}
}
