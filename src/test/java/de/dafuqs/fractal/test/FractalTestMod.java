package de.dafuqs.fractal.test;

import de.dafuqs.fractal.api.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.*;
import net.neoforged.neoforge.registries.*;

@Mod(FractalTestMod.MODID)
public class FractalTestMod {
	public static final String MODID = "fractal_test";
	
	// Create a subtab style for the redstone sbutab.
	public static final CreativeSubTabStyle STYLE_REDSTONE = new CreativeSubTabStyle.Builder()
			.background(id("textures/gui/container/creative_inventory/redstone_menu.png"))
			.tab(
					id("container/creative_inventory/redstone_tab_top_selected_1"),
					id("container/creative_inventory/redstone_tab_top_selected_2"),
					id("container/creative_inventory/redstone_tab_top_selected_7"),
					id("container/creative_inventory/redstone_tab_top_unselected_1"),
					id("container/creative_inventory/redstone_tab_top_unselected_2"),
					id("container/creative_inventory/redstone_tab_top_unselected_7"),
					id("container/creative_inventory/redstone_tab_bottom_selected_1"),
					id("container/creative_inventory/redstone_tab_bottom_selected_2"),
					id("container/creative_inventory/redstone_tab_bottom_selected_7"),
					id("container/creative_inventory/redstone_tab_bottom_unselected_1"),
					id("container/creative_inventory/redstone_tab_bottom_unselected_2"),
					id("container/creative_inventory/redstone_tab_bottom_unselected_7")
			)
			.subtab(
					id("container/creative_inventory/redstone_subtab_selected_left"),
					id("container/creative_inventory/redstone_subtab_unselected_left"),
					id("container/creative_inventory/redstone_subtab_selected_right"),
					id("container/creative_inventory/redstone_subtab_unselected_right")
			)
			.scrollbar(
					id("container/creative_inventory/redstone_scroller"),
					id("container/creative_inventory/redstone_scroller_disabled")
			)
			.build();
	
	// Create a style for the components submenu.
	public static final CreativeSubTabStyle STYLE_COMPONENTS = new CreativeSubTabStyle.Builder()
			.background(id("textures/gui/container/creative_inventory/components_menu.png"))
			.subtab(
					id("container/creative_inventory/components_subtab_selected_left"),
					id("container/creative_inventory/components_subtab_unselected_left"),
					id("container/creative_inventory/components_subtab_selected_right"),
					id("container/creative_inventory/components_subtab_unselected_right")
			)
			.build();
	
	// Create a style for the logistics submenu.
	public static final CreativeSubTabStyle STYLE_LOGISTICS = new CreativeSubTabStyle.Builder()
			.subtab(
					id("container/creative_inventory/logistics_subtab_selected_left"),
					id("container/creative_inventory/logistics_subtab_unselected_left"),
					id("container/creative_inventory/logistics_subtab_selected_right"),
					id("container/creative_inventory/logistics_subtab_unselected_right")
			)
			.build();
	
	// Create a style for the automation submenu.
	public static final CreativeSubTabStyle STYLE_AUTOMATION = new CreativeSubTabStyle.Builder()
			.subtab(
					id("container/creative_inventory/automation_subtab_selected_left"),
					id("container/creative_inventory/automation_subtab_unselected_left"),
					id("container/creative_inventory/automation_subtab_selected_right"),
					id("container/creative_inventory/automation_subtab_unselected_right")
			)
			.build();
	
	// Create our parent creative tab
	public static final CreativeModeTab TAB = CreativeModeTab.builder()
			.icon(() -> new ItemStack(Blocks.REDSTONE_BLOCK))
			.displayItems((displayContext, entries) ->
			{
				// At least one item must be added to the parent tab or else it won't be visible.
				// Make sure that this item isn't in one of your subtabs, otherwise you'll get an error about duplicate items in a tab.
				entries.accept(Items.APPLE, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
				
				// Add all of our subgroup's items to the parent tab.
				for (CreativeSubTab subGroup : FractalTestMod.TAB.fractal$getChildren()) {
					entries.acceptAll(subGroup.getSearchTabDisplayItems(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
				}
			})
			.title(Component.translatable("itemGroup.fractal.main"))
			.build();
	
	// Create a subtab for the basic redstone things.
	public static final CreativeModeTab REDSTONE = new CreativeSubTab.Builder(
			TAB, // This is the parent tab we're registering the subtab to.
			id("redstone"),
			Component.translatable("itemGroup.fractal.redstone")
	)
			.entries((displayContext, entries) -> {
				entries.accept(Items.REDSTONE);
				entries.accept(Items.REDSTONE_TORCH);
				entries.accept(Items.REDSTONE_BLOCK);
				entries.accept(Items.REPEATER);
				entries.accept(Items.COMPARATOR);
				entries.accept(Items.REDSTONE_ORE);
			})
			.styled(STYLE_REDSTONE) // Use the redstone style we defined above.
			.build();
	public static final CreativeModeTab COMPONENTS = new CreativeSubTab.Builder(TAB, id("components"), Component.translatable("itemGroup.fractal.components"))
			.entries((displayContext, entries) -> {
				entries.accept(Items.WAXED_COPPER_BULB);
				entries.accept(Items.WAXED_EXPOSED_COPPER_BULB);
				entries.accept(Items.WAXED_WEATHERED_COPPER_BULB);
				entries.accept(Items.WAXED_OXIDIZED_COPPER_BULB);
				entries.accept(Items.LEVER);
				entries.accept(Items.OAK_BUTTON);
				entries.accept(Items.STONE_BUTTON);
				entries.accept(Items.OAK_PRESSURE_PLATE);
				entries.accept(Items.STONE_PRESSURE_PLATE);
				entries.accept(Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
				entries.accept(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
				entries.accept(Items.TRIPWIRE_HOOK);
				entries.accept(Items.STRING);
				entries.accept(Items.DAYLIGHT_DETECTOR);
				entries.accept(Items.PISTON);
				entries.accept(Items.STICKY_PISTON);
				entries.accept(Items.SLIME_BLOCK);
				entries.accept(Items.HONEY_BLOCK);
				entries.accept(Items.OBSERVER);
				entries.accept(Items.NOTE_BLOCK);
			})
			.styled(STYLE_COMPONENTS)
			.build();
	public static final CreativeModeTab LOGISTICS = new CreativeSubTab.Builder(TAB, id("logistics"), Component.translatable("itemGroup.fractal.logistics"))
			.entries((displayContext, entries) -> {
				entries.accept(Items.DISPENSER);
				entries.accept(Items.DROPPER);
				entries.accept(Items.HOPPER);
				entries.accept(Items.RAIL);
				entries.accept(Items.POWERED_RAIL);
				entries.accept(Items.DETECTOR_RAIL);
				entries.accept(Items.ACTIVATOR_RAIL);
				entries.accept(Items.MINECART);
				entries.accept(Items.HOPPER_MINECART);
				entries.accept(Items.CHEST_MINECART);
				entries.accept(Items.FURNACE_MINECART);
				entries.accept(Items.TNT_MINECART);
				entries.accept(Items.OAK_CHEST_BOAT);
				entries.accept(Items.BAMBOO_CHEST_RAFT);
			})
			.styled(STYLE_LOGISTICS)
			.build();
	public static final CreativeModeTab AUTOMATION = new CreativeSubTab.Builder(TAB, id("automation"), Component.translatable("itemGroup.fractal.automation"))
			.entries((displayContext, entries) -> {
				entries.accept(Items.CRAFTER);
			})
			.styled(STYLE_AUTOMATION)
			.build();
	public static final CreativeModeTab COMPARATOR_OUTPUTS = new CreativeSubTab.Builder(TAB, id("comparator_outputs"), Component.translatable("itemGroup.fractal.comparator_outputs"))
			.entries((displayContext, entries) -> {
				entries.accept(Items.CHEST);
				entries.accept(Items.BARREL);
				entries.accept(Items.CHISELED_BOOKSHELF);
				entries.accept(Items.FURNACE);
				entries.accept(Items.TRAPPED_CHEST);
				entries.accept(Items.JUKEBOX);
				entries.accept(Items.DECORATED_POT);
				entries.accept(Items.COMPOSTER);
				entries.accept(Items.CAULDRON);
			})
			.build();
	public static final CreativeModeTab MISC = new CreativeSubTab.Builder(TAB, id("misc"), Component.translatable("itemGroup.fractal.misc"))
			.entries((displayContext, entries) -> {
				entries.accept(Items.TARGET);
				entries.accept(Items.SCULK_SENSOR);
				entries.accept(Items.CALIBRATED_SCULK_SENSOR);
				entries.accept(Items.SCULK_SHRIEKER);
				entries.accept(Items.AMETHYST_BLOCK);
				entries.accept(Items.WHITE_WOOL);
				entries.accept(Items.LECTERN);
				entries.accept(Items.LIGHTNING_ROD);
				entries.accept(Items.OAK_DOOR);
				entries.accept(Items.IRON_DOOR);
				entries.accept(Items.OAK_FENCE_GATE);
				entries.accept(Items.OAK_TRAPDOOR);
				entries.accept(Items.IRON_TRAPDOOR);
				entries.accept(Items.TNT);
				entries.accept(Items.REDSTONE_LAMP);
				entries.accept(Items.BELL);
				entries.accept(Items.BIG_DRIPLEAF);
				entries.accept(Items.ARMOR_STAND);
			})
			// Normally subtab titles show as `<Parent Tab Title> <Sub Tab Title>`, this changes it to just be the sub tab's title.
			.hideParentTitle()
			.build();
	
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	
	public FractalTestMod(IEventBus modBus) {
		TABS.register("tab", () -> TAB);
		TABS.register(modBus);
	}
	
	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
