package de.dafuqs.fractal.mixin;

import com.google.common.collect.*;
import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(ItemGroup.class)
public class MixinItemGroup implements ItemGroupParent, ItemGroupConfiguration {
	
	@Unique
	private final List<ItemSubGroup> fractal$children = Lists.newArrayList();
	@Unique
	private ItemSubGroup fractal$selectedChild = null;
	private int fractal$tabOffset = 0;
	private float fractal$textR = 0;
	private float fractal$textG = 0;
	private float fractal$textB = 0;
	
	@Inject(at = @At("HEAD"), method = "getDisplayStacks", cancellable = true)
	public void getDisplayStacks(CallbackInfoReturnable<Collection<ItemStack>> cir) {
		if (fractal$selectedChild != null) {
			cir.setReturnValue(fractal$selectedChild.getDisplayStacks());
		}
	}
	
	@Override
	public List<ItemSubGroup> fractal$getChildren() {
		return fractal$children;
	}
	
	@Override
	public ItemSubGroup fractal$getSelectedChild() {
		return fractal$selectedChild;
	}
	
	@Override
	public void fractal$setSelectedChild(ItemSubGroup group) {
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
