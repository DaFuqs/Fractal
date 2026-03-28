package de.dafuqs.fractal.api;

import de.dafuqs.fractal.impl.*;
import net.fabricmc.fabric.api.event.*;
import net.fabricmc.fabric.api.creativetab.v1.*;
import net.minecraft.resources.*;

public final class CreativeSubTabEvent {
	private CreativeSubTabEvent() {
	}

	/**
	 * This event allows the entries of any item group to be modified.
	 * <p/>
	 * Use {@link #modifyEntriesEvent(Identifier)} to get the event for a specific item group.
	 * <p/>
	 * This event is invoked after those two more specific events.
	 */
	public static final Event<ModifyEntriesAll> MODIFY_ENTRIES_ALL = EventFactory.createArrayBacked(ModifyEntriesAll.class, callbacks -> (group, entries) -> {
		for (ModifyEntriesAll callback : callbacks) {
			callback.modifyEntries(group, entries);
		}
	});

	/**
	 * Returns the modify entries event for a specific item group. This uses the group ID and
	 * is suitable for modifying a modded item group that might not exist.
	 *
	 * @param identifier the {@link Identifier} of the item group to modify
	 * @return the event
	 */
	public static Event<CreativeSubTabEvent.ModifyEntries> modifyEntriesEvent(Identifier identifier) {
		return CreativeSubTabEventImpl.getOrCreateModifyEntriesEvent(identifier);
	}

	@FunctionalInterface
	public interface ModifyEntries {
		/**
		 * Modifies the item group entries.
		 * @param entries the entries
		 * @see FabricCreativeModeTabOutput
		 */
		void modifyEntries(FabricCreativeModeTabOutput entries);
	}

	@FunctionalInterface
	public interface ModifyEntriesAll {
		/**
		 * Modifies the item group entries.
		 * @param group the item group that is being modified
		 * @param entries the entries
		 * @see FabricCreativeModeTabOutput
		 */
		void modifyEntries(CreativeSubTab group, FabricCreativeModeTabOutput entries);
	}
}