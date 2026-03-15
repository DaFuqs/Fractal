package de.dafuqs.fractal.interfaces;

import de.dafuqs.fractal.api.*;

import java.util.*;

public interface ItemGroupParent {
	
	List<ItemSubGroup> fractal$getChildren();
	ItemSubGroup fractal$getSelectedChild();
	void fractal$setSelectedChild(ItemSubGroup group);
	int fractal$getTabOffset();
	float fractal$getTextR();
	float fractal$getTextG();
	float fractal$getTextB();
	
}
