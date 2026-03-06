package de.dafuqs.fractal.mixin;

import com.google.common.collect.*;
import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin implements ICreativeTabParent, ICreativeTabConfiguration {
	@Unique
	private final List<CreativeSubTab> fractal$children = Lists.newArrayList();
	@Unique
	private CreativeSubTab fractal$selectedChild = null;
	private int fractal$tabOffset = 0;
	private float fractal$textR = 0;
	private float fractal$textG = 0;
	private float fractal$textB = 0;
	
	@Inject(at = @At("HEAD"), method = "getDisplayItems", cancellable = true)
	public void getDisplayItems(CallbackInfoReturnable<Collection<ItemStack>> cir) {
		if (fractal$selectedChild != null) {
			cir.setReturnValue(fractal$selectedChild.getDisplayItems());
		}
	}
	
	@Override
	public List<CreativeSubTab> fractal$getChildren() {
		return fractal$children;
	}
	
	@Override
	public CreativeSubTab fractal$getSelectedChild() {
		return fractal$selectedChild;
	}
	
	@Override
	public void fractal$setSelectedChild(CreativeSubTab group) {
		fractal$selectedChild = group;
	}

	@Override
	public void fractal$setTabOffset(int offset) {
		fractal$tabOffset = offset;
	}
	
	@Override
	public int fractal$getTabOffset() {
		return fractal$tabOffset;
	}
	
	@Override
	public void fractal$setTextColor(float r, float g, float b) {
		fractal$textR = r;
		fractal$textG = g;
		fractal$textB = b;
	}
	
	@Override
	public float fractal$getTextR() {
		return fractal$textR;
	}
	
	@Override
	public float fractal$getTextG() {
		return fractal$textG;
	}
	
	@Override
	public float fractal$getTextB() {
		return fractal$textB;
	}
}
