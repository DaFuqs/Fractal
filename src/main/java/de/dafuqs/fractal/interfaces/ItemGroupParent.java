package de.dafuqs.fractal.interfaces;

import de.dafuqs.fractal.api.*;

import java.util.*;

public interface ItemGroupParent {

	default List<ItemSubGroup> fractal$getChildren() {
		return List.of();
	}

	default ItemSubGroup fractal$getSelectedChild() {
		return null;
	}

	default void fractal$setSelectedChild(ItemSubGroup group) {}

	default int fractal$getTabOffset() { return 0; }
	default float fractal$getTextR() { return 0; }
	default float fractal$getTextG() { return 0; }
	default float fractal$getTextB() { return 0; }
	
}
