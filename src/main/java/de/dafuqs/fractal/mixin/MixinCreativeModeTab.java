package de.dafuqs.fractal.mixin;

import com.google.common.collect.*;
import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(CreativeModeTab.class)
public class MixinCreativeModeTab implements ItemGroupParent {
	
	@Unique
	private final List<ItemSubGroup> fractal$children = Lists.newArrayList();
	@Unique
	private ItemSubGroup fractal$selectedChild = null;
	
	@Inject(at = @At("HEAD"), method = "getDisplayItems", cancellable = true)
	public void getDisplayStacks(CallbackInfoReturnable<Collection<ItemStack>> cir) {
		if (fractal$selectedChild != null) {
			cir.setReturnValue(fractal$selectedChild.getDisplayItems());
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
	
}
