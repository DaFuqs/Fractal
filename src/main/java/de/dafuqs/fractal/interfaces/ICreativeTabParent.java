package de.dafuqs.fractal.interfaces;

import de.dafuqs.fractal.api.*;
import org.jetbrains.annotations.*;

import java.util.*;

@ApiStatus.Internal
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
