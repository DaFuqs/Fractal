package de.dafuqs.fractal.api;

import net.minecraft.resources.Identifier;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

public record ItemSubGroupStyle(@Nullable Identifier backgroundTexture, int subtabNameTextColor,

								@Nullable Identifier selectedSubtabTextureLeft,
								@Nullable Identifier unselectedSubtabTextureLeft,
								@Nullable Identifier selectedSubtabTextureRight,
								@Nullable Identifier unselectedSubtabTextureRight,

								@Nullable Identifier enabledScrollbarTexture,
								@Nullable Identifier disabledScrollbarTexture,

								@Nullable Identifier tabTopFirstSelectedTexture,
								@Nullable Identifier tabTopSelectedTexture,
								@Nullable Identifier tabTopLastSelectedTexture,
								@Nullable Identifier tabTopFirstUnselectedTexture,
								@Nullable Identifier tabTopUnselectedTexture,
								@Nullable Identifier tabTopLastUnselectedTexture,
								@Nullable Identifier tabBottomFirstSelectedTexture,
								@Nullable Identifier tabBottomSelectedTexture,
								@Nullable Identifier tabBottomLastSelectedTexture,
								@Nullable Identifier tabBottomFirstUnselectedTexture,
								@Nullable Identifier tabBottomUnselectedTexture,
								@Nullable Identifier tabBottomLastUnselectedTexture) {
	
	public static final Identifier DEFAULT_SUBTAB_SELECTED_TEXTURE_LEFT = Identifier.fromNamespaceAndPath("fractal", "container/creative_inventory/subtab_selected_left");
	public static final Identifier DEFAULT_SUBTAB_UNSELECTED_TEXTURE_LEFT = Identifier.fromNamespaceAndPath("fractal", "container/creative_inventory/subtab_unselected_left");
	public static final Identifier DEFAULT_SUBTAB_SELECTED_TEXTURE_RIGHT = Identifier.fromNamespaceAndPath("fractal", "container/creative_inventory/subtab_selected_right");
	public static final Identifier DEFAULT_SUBTAB_UNSELECTED_TEXTURE_RIGHT = Identifier.fromNamespaceAndPath("fractal", "container/creative_inventory/subtab_unselected_right");
	
	public static class Builder {
		
		protected @Nullable Identifier backgroundTexture;
		protected int subtabNameTextColor = 0xFF000000; // default vanilla text is 0xFF404040;
		
		protected @Nullable Identifier selectedSubtabTextureLeft = DEFAULT_SUBTAB_SELECTED_TEXTURE_LEFT;
		protected @Nullable Identifier unselectedSubtabTextureLeft = DEFAULT_SUBTAB_UNSELECTED_TEXTURE_LEFT;
		protected @Nullable Identifier selectedSubtabTextureRight = DEFAULT_SUBTAB_SELECTED_TEXTURE_RIGHT;
		protected @Nullable Identifier unselectedSubtabTextureRight = DEFAULT_SUBTAB_UNSELECTED_TEXTURE_RIGHT;
		
		protected @Nullable Identifier enabledScrollbarTexture;
		protected @Nullable Identifier disabledScrollbarTexture;
		
		protected @Nullable Identifier tabTopFirstSelectedTexture;
		protected @Nullable Identifier tabTopSelectedTexture;
		protected @Nullable Identifier tabTopLastSelectedTexture;
		protected @Nullable Identifier tabTopFirstUnselectedTexture;
		protected @Nullable Identifier tabTopUnselectedTexture;
		protected @Nullable Identifier tabTopLastUnselectedTexture;
		
		protected @Nullable Identifier tabBottomFirstSelectedTexture;
		protected @Nullable Identifier tabBottomSelectedTexture;
		protected @Nullable Identifier tabBottomLastSelectedTexture;
		protected @Nullable Identifier tabBottomFirstUnselectedTexture;
		protected @Nullable Identifier tabBottomUnselectedTexture;
		protected @Nullable Identifier tabBottomLastUnselectedTexture;
		
		public Builder() {
		}
		
		public Builder background(Identifier backgroundTexture) { // texture
			this.backgroundTexture = backgroundTexture;
			return this;
		}
		
		public Builder subtabNameTextColor(int color) {
			this.subtabNameTextColor = color;
			return this;
		}
		
		public Builder scrollbar(Identifier enabledTexture, Identifier disabledTexture) { // sprite
			this.enabledScrollbarTexture = enabledTexture;
			this.disabledScrollbarTexture = disabledTexture;
			return this;
		}
		
		public Builder subtab(Identifier selectedSubtabTextureLeft, Identifier unselectedSubtabTextureLeft, Identifier selectedSubtabTextureRight, Identifier unselectedSubtabTextureRight) { // sprite
			this.selectedSubtabTextureLeft = selectedSubtabTextureLeft;
			this.unselectedSubtabTextureLeft = unselectedSubtabTextureLeft;
			this.selectedSubtabTextureRight = selectedSubtabTextureRight;
			this.unselectedSubtabTextureRight = unselectedSubtabTextureRight;
			return this;
		}
		
		public Builder tab(Identifier topFirstSelectedTexture, Identifier topSelectedTexture, Identifier topLastSelectedTexture, Identifier topFirstUnselectedTexture, Identifier topUnselectedTexture, Identifier topLastUnselectedTexture,
						   Identifier bottomFirstSelectedTexture, Identifier bottomSelectedTexture, Identifier bottomLastSelectedTexture, Identifier bottomFirstUnselectedTexture, Identifier bottomUnselectedTexture, Identifier bottomLastUnselectedTexture) { // sprite
			
			this.tabTopFirstSelectedTexture = topFirstSelectedTexture;
			this.tabTopSelectedTexture = topSelectedTexture;
			this.tabTopLastSelectedTexture = topLastSelectedTexture;
			this.tabTopFirstUnselectedTexture = topFirstUnselectedTexture;
			this.tabTopUnselectedTexture = topUnselectedTexture;
			this.tabTopLastUnselectedTexture = topLastUnselectedTexture;
			
			this.tabBottomFirstSelectedTexture = bottomFirstSelectedTexture;
			this.tabBottomSelectedTexture = bottomSelectedTexture;
			this.tabBottomLastSelectedTexture = bottomLastSelectedTexture;
			this.tabBottomFirstUnselectedTexture = bottomFirstUnselectedTexture;
			this.tabBottomUnselectedTexture = bottomUnselectedTexture;
			this.tabBottomLastUnselectedTexture = bottomLastUnselectedTexture;
			
			return this;
		}
		
		public ItemSubGroupStyle build() {
			return new ItemSubGroupStyle(backgroundTexture, subtabNameTextColor,
					selectedSubtabTextureLeft, unselectedSubtabTextureLeft, selectedSubtabTextureRight, unselectedSubtabTextureRight,
					enabledScrollbarTexture, disabledScrollbarTexture,
					tabTopFirstSelectedTexture, tabTopSelectedTexture, tabTopLastSelectedTexture, tabTopFirstUnselectedTexture, tabTopUnselectedTexture, tabTopLastUnselectedTexture,
					tabBottomFirstSelectedTexture, tabBottomSelectedTexture, tabBottomLastSelectedTexture, tabBottomFirstUnselectedTexture, tabBottomUnselectedTexture, tabBottomLastUnselectedTexture);
		}
	}
	
}
