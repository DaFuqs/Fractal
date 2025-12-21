package de.dafuqs.fractal.impl;

import de.dafuqs.fractal.api.*;
import net.fabricmc.fabric.api.event.*;
import net.minecraft.resources.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CreativeSubTabEventImpl {
	
	private static final Map<Identifier, Event<CreativeSubTabEvent.ModifyEntries>> EVENT_MAP = new HashMap<>();
	
	public static Event<CreativeSubTabEvent.ModifyEntries> getOrCreateModifyEntriesEvent(Identifier identifier) {
		return EVENT_MAP.computeIfAbsent(identifier, (g -> createModifyEvent()));
	}
	
	@Nullable
	public static Event<CreativeSubTabEvent.ModifyEntries> getModifyEntriesEvent(Identifier identifier) {
		return EVENT_MAP.get(identifier);
	}
	
	private static Event<CreativeSubTabEvent.ModifyEntries> createModifyEvent() {
		return EventFactory.createArrayBacked(CreativeSubTabEvent.ModifyEntries.class, callbacks -> (entries) -> {
			for (CreativeSubTabEvent.ModifyEntries callback : callbacks) {
				callback.modifyEntries(entries);
			}
		});
	}
}
