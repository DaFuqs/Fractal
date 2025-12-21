package de.dafuqs.fractal.interfaces;

import de.dafuqs.fractal.api.*;

import java.util.*;

public interface ICreativeTabParent {
	
	default List<CreativeSubTab> fractal$getChildren() {
		return List.of();
	}
	
	default CreativeSubTab fractal$getSelectedChild() {
		return null;
	}
	
	default void fractal$setSelectedChild(CreativeSubTab group) {
	}
	
}
