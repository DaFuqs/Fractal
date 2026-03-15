# About Fractal
This repo is a fork of [lib39/fractal](https://git.sleeping.town/unascribed-mods/Lib39) by unascribed, with added support for styled groups. I will take care of this repo while unascribed is busy.

Fractal introduces item **subgroups for the creative menu**.

### Why Fractal?
- Fractals Subgroups are very condensed, allowing you to add up to 12 subgroups for each of your tabs
- Creating a new ItemSubGroup only takes one line of code and no changes in the way you assign your items to groups. Just pass them the ItemSubGroup instead of your main item group

### Limitations
- More than 12 subgroups per item group, while fully functional, will look weird.

## Examples

### Vanilla Style Subgroups

![Screenshots of the Creative Tabs](images/screenshot_vanilla_style.png)

```java
public static final Identifier GROUP_ID = Identifier.of("mymod", "main");

public static final ItemGroup MAIN = FabricItemGroup.builder()
		.icon(() -> new ItemStack(Blocks.REDSTONE_BLOCK))
		.entries((displayContext, entries) -> {
		    entries.add(Items.APPLE, ItemGroup.StackVisibility.PARENT_TAB_ONLY);
		    ItemGroupParent parent = (ItemGroupParent) Fractal.MAIN;
		    for (ItemSubGroup subGroup : parent.fractal$getChildren()) {
		        entries.addAll(subGroup.getSearchTabStacks(), ItemGroup.StackVisibility.SEARCH_TAB_ONLY);
		    }
		})
		.displayName(Text.translatable("mymod.1"))
		.noRenderedName()
		.build();

public static final Identifier ITEM_GROUP_BACKGROUND_TEXTURE_IDENTIFIER = Identifier.of("mymod", "textures/custom_item_group.png");

public static final ItemGroup EQUIPMENT = new ItemSubGroup.Builder(MAIN, Identifier.of("mymod", "equipment"), Text.translatable("itemGroup.mymod.equipment")).entries((displayContext, entries) -> entries.add(Items.APPLE)).build();
public static final ItemGroup FUNCTIONAL = new ItemSubGroup.Builder(MAIN, Identifier.of("mymod", "functional"), Text.translatable("itemGroup.mymod.functional")).entries((displayContext, entries) -> entries.add(Items.BAKED_POTATO)).build();
public static final ItemGroup CUISINE = new ItemSubGroup.Builder(MAIN, Identifier.of("mymod", "cuisine"), Text.translatable("itemGroup.mymod.cuisine")).entries((displayContext, entries) -> entries.add(Items.CACTUS)).build();
public static final ItemGroup RESOURCES = new ItemSubGroup.Builder(MAIN, Identifier.of("mymod", "resources"), Text.translatable("itemGroup.mymod.resources")).entries((displayContext, entries) -> entries.add(Items.DANDELION)).build();

@Override
public void onInitialize() {
    Registry.register(Registries.ITEM_GROUP, GROUP_ID, MAIN);
}
```

### Applying a custom style
You are also able to apply a style to your ItemSubGroups, by supplying custom background, tab, subtab and scrollbar textures. You can even mix and match!
In this example, the first two ItemSubGroups use a custom style by supplying texture files that are being shipped with your mod. The latter two tabs use the vanilla style.

![Screenshots of the Creative Tabs](images/screenshot_custom_style.png)

```java
// Texture (put into \resources\assets\fractal\textures\gui\container\creative_inventory)
public static final Identifier BACKGROUND_TEXTURE = Identifier.of("fractal", "textures/gui/container/creative_inventory/custom_background.png");

// Sprites (put into \resources\assets\fractal\textures\gui\sprites\container\creative_inventory)
public static final Identifier SCROLLBAR_ENABLED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_scrollbar_enabled");
public static final Identifier SCROLLBAR_DISABLED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_scrollbar_disabled");

public static final Identifier SUBTAB_SELECTED_TEXTURE_LEFT = Identifier.of("fractal", "container/creative_inventory/custom_subtab_selected_left");
public static final Identifier SUBTAB_UNSELECTED_TEXTURE_LEFT = Identifier.of("fractal", "container/creative_inventory/custom_subtab_unselected_left");
public static final Identifier SUBTAB_SELECTED_TEXTURE_RIGHT = Identifier.of("fractal", "container/creative_inventory/custom_subtab_selected_right");
public static final Identifier SUBTAB_UNSELECTED_TEXTURE_RIGHT = Identifier.of("fractal", "container/creative_inventory/custom_subtab_unselected_right");

public static final Identifier TAB_TOP_FIRST_SELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_top_first_selected");
public static final Identifier TAB_TOP_SELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_top_selected");
public static final Identifier TAB_TOP_LAST_SELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_top_last_selected");
public static final Identifier TAB_TOP_FIRST_UNSELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_top_first_unselected");
public static final Identifier TAB_TOP_UNSELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_top_unselected");
public static final Identifier TAB_TOP_LAST_UNSELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_top_last_unselected");
public static final Identifier TAB_BOTTOM_FIRST_SELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_bottom_first_selected");
public static final Identifier TAB_BOTTOM_SELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_bottom_selected");
public static final Identifier TAB_BOTTOM_LAST_SELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_bottom_last_selected");
public static final Identifier TAB_BOTTOM_FIRST_UNSELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_bottom_first_unselected");
public static final Identifier TAB_BOTTOM_UNSELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_bottom_unselected");
public static final Identifier TAB_BOTTOM_LAST_UNSELECTED_TEXTURE = Identifier.of("fractal", "container/creative_inventory/custom_tab_bottom_last_unselected");

public static final ItemSubGroupStyle STYLE = new ItemSubGroupStyle.Builder()
        .background(BACKGROUND_TEXTURE)
        .scrollbar(SCROLLBAR_ENABLED_TEXTURE, SCROLLBAR_DISABLED_TEXTURE)
        .subtab(SUBTAB_SELECTED_TEXTURE_LEFT, SUBTAB_UNSELECTED_TEXTURE_LEFT, SUBTAB_SELECTED_TEXTURE_RIGHT, SUBTAB_UNSELECTED_TEXTURE_RIGHT)
        .tab(TAB_TOP_FIRST_SELECTED_TEXTURE, TAB_TOP_SELECTED_TEXTURE, TAB_TOP_LAST_SELECTED_TEXTURE, TAB_TOP_FIRST_UNSELECTED_TEXTURE, TAB_TOP_UNSELECTED_TEXTURE, TAB_TOP_LAST_UNSELECTED_TEXTURE,
                TAB_BOTTOM_FIRST_SELECTED_TEXTURE, TAB_BOTTOM_SELECTED_TEXTURE, TAB_BOTTOM_LAST_SELECTED_TEXTURE, TAB_BOTTOM_FIRST_UNSELECTED_TEXTURE, TAB_BOTTOM_UNSELECTED_TEXTURE, TAB_BOTTOM_LAST_UNSELECTED_TEXTURE)
        .build();

public static final ItemGroup EQUIPMENT = new ItemSubGroup.Builder(MAIN, Identifier.of("mymod", "equipment"), Text.translatable("itemGroup.mymod.equipment")).styled(STYLE).entries((displayContext, entries) -> entries.add(Items.APPLE)).build();
public static final ItemGroup FUNCTIONAL = new ItemSubGroup.Builder(MAIN, Identifier.of("mymod", "functional"), Text.translatable("itemGroup.mymod.functional")).styled(STYLE).entries((displayContext, entries) -> entries.add(Items.BAKED_POTATO)).build();
public static final ItemGroup CUISINE = new ItemSubGroup.Builder(MAIN, Identifier.of("mymod", "cuisine"), Text.translatable("itemGroup.mymod.cuisine")).entries((displayContext, entries) -> entries.add(Items.CACTUS)).build();
public static final ItemGroup RESOURCES = new ItemSubGroup.Builder(MAIN, Identifier.of("mymod", "resources"), Text.translatable("itemGroup.mymod.resources")).entries((displayContext, entries) -> entries.add(Items.DANDELION)).build();
```

### Adding items to existing ItemSubGroups

There exists an API for that! `ItemSubGroupEvents.modifyEntriesEvent(<Identifier of the Subtab you want to modify)` behaves exactly like its Fabric API counterpart, but targets ItemSubGroups instead of ItemGroups (it needs to be a separate event, since ItemSubGroups are not registered item groups, having no RegistryEntry that the Fabric API version targets)
