package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.fabricmc.api.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.input.*;
import net.minecraft.client.renderer.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Environment(EnvType.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenAddTabsMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements ISubTabLocation, CreativeModeInventoryScreenAccessor {
	
	@Unique
	private static final int LAST_TAB_INDEX_RENDERING_LEFT = 11;
	
	@Unique
	private static final Identifier TINYFONT_TEXTURE = Identifier.fromNamespaceAndPath("fractal", "textures/gui/tinyfont.png");
	
	public CreativeModeInventoryScreenAddTabsMixin(CreativeModeInventoryScreen.ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
		super(screenHandler, playerInventory, text);
	}
	
	@Shadow
	private float scrollOffs;
	
	@Shadow
	private static CreativeModeTab selectedTab;
	
	@Unique
	private int fractal$y; // tab start y
	@Unique
	private int fractal$x, fractal$h; // left tabs
	@Unique
	private int fractal$x2, fractal$h2; // right tabs
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
	public void fractal$render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!(selectedTab instanceof ICreativeTabParent parent) || parent.fractal$getChildren().isEmpty()) return;
		
		var matrices = guiGraphics.pose();
		matrices.pushMatrix();
		matrices.translate(this.leftPos, this.topPos);
		
		if (!selectedTab.showTitle()) {
			CreativeModeTab child = parent.fractal$getSelectedChild();
			var selected = selectedTab.getDisplayName();
			guiGraphics.drawString(font, selected, 8, 6, CommonColors.DARK_GRAY, false);
			int x = 8 + font.width(selected);
			if (child != null) {
				guiGraphics.drawString(font, " ", x, 6, CommonColors.DARK_GRAY, false);
				x += font.width(" ");
				guiGraphics.drawString(font, child.getDisplayName(), x, 6, CommonColors.DARK_GRAY, false);
			}
		}

		int curX = 0;
		int curY = 6;
		int tabStartOffset = 68;
		int tabWidth = 72;

		fractal$x = curX - tabWidth;
		fractal$y = curY;
		fractal$x2 = curX + 259;
		boolean rendersOnTheRight = false;
		List<CreativeSubTab> children = parent.fractal$getChildren();
		for (CreativeSubTab child : parent.fractal$getChildren()) {
			boolean thisChildSelected = child == parent.fractal$getSelectedChild();
			CreativeSubTabStyle style = child.getStyle();
			Identifier subtabTextureID = thisChildSelected
					? rendersOnTheRight ? style.selectedSubtabTextureRight() :  style.selectedSubtabTextureLeft()
					: rendersOnTheRight ? style.unselectedSubtabTextureRight() : style.unselectedSubtabTextureLeft();
			
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, subtabTextureID, curX - tabStartOffset, curY, 72, 11);
			
			int textOffset = thisChildSelected ? 8 : 5; // makes the text pop slightly outwards if selected
			int textColor = child.getStyle().subtabNameTextColor();
			String tabDisplayName = child.getDisplayName().getString();
			if (rendersOnTheRight) {
				for (int i = 0; i < tabDisplayName.length(); i++) {
					char c = tabDisplayName.charAt(i);
					if (c > 0x7F) continue;
					int u = (c % 16) * 4;
					int v = (c / 16) * 6;
					guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TINYFONT_TEXTURE, curX + 1 - tabStartOffset + textOffset, curY + 3, u, v, 4, 6, 64, 48, textColor);
					curX += 4;
				}
			} else {
				for (int i = tabDisplayName.length() - 1; i >= 0; i--) {
					char c = tabDisplayName.charAt(i);
					if (c > 0x7F) continue;
					int u = (c % 16) * 4;
					int v = (c / 16) * 6;
					guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TINYFONT_TEXTURE, curX - textOffset, curY + 3, u, v, 4, 6, 64, 48, textColor);
					curX -= 4;
				}
			}
			
			int index = child.getIndexInParent();
			if (index >= LAST_TAB_INDEX_RENDERING_LEFT) {
				if (index == LAST_TAB_INDEX_RENDERING_LEFT) {
					rendersOnTheRight = true;
					curY -= 10 * (LAST_TAB_INDEX_RENDERING_LEFT + 1);
				}
				curX = fractal$x2;
			} else {
				curX = 0;
			}
			curY += 10;
		}

		fractal$h = 11 * Math.min(LAST_TAB_INDEX_RENDERING_LEFT + 1, children.size());
		fractal$h2 = 11 * Math.max(0, children.size() - LAST_TAB_INDEX_RENDERING_LEFT - 1);

		matrices.popMatrix();
		// adjust relative coords to "absolute" ones
		fractal$x  += this.leftPos;
		fractal$x2 += this.leftPos;
		fractal$y  += this.topPos;
	}
	
	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	public void fractal$mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		double mouseX = mouseButtonEvent.x();
		double mouseY = mouseButtonEvent.y();
		CreativeModeTab selected = selectedTab;
		if (selected instanceof ICreativeTabParent parent && !parent.fractal$getChildren().isEmpty()) {
			int x = fractal$x;
			int y = fractal$y;
			int w = 77;
			for (CreativeSubTab child : parent.fractal$getChildren()) {
				if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 11) {
					parent.fractal$setSelectedChild(child);
					
					this.menu.items.clear();
					this.menu.items.addAll(selected.getDisplayItems());
					
					this.scrollOffs = 0.0F;
					this.menu.scrollTo(0.0F);
					cir.setReturnValue(true);
					return;
				}
				y += 10;
				
				if(child.getIndexInParent() == LAST_TAB_INDEX_RENDERING_LEFT) {
					x += 259;
					y = fractal$y;
				}
			}
		}
	}
	
	@Override
	public int fractal$getX() {
		return fractal$x;
	}
	
	@Override
	public int fractal$getY() {
		return fractal$y;
	}
	
	@Override
	public int fractal$getH() {
		return fractal$h;
	}
	
	@Override
	public int fractal$getX2() {
		return fractal$x2 - 72;
	}
	
	@Override
	public int fractal$getH2() {
		return fractal$h2;
	}
	
}
