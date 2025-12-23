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
	
	// Create a subtab style for the redstone subtab
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
			.displayItems((displayContext, output) -> {
				// At least one item must be added to the parent tab or else it won't be visible.
				// Make sure that this item isn't in one of your subtabs, otherwise you'll get an error about duplicate items in a tab.
				output.accept(Items.APPLE, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
				
				// Add all of our subgroup's items to the parent tab.
				for (CreativeSubTab subGroup : FractalTestMod.TAB.fractal$getChildren()) {
					output.acceptAll(subGroup.getSearchTabDisplayItems(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
				}
			})
			.title(Component.translatable("itemGroup.fractal.main"))
			.build();
	
	// Create a subtab for the basic redstone things.
	public static final CreativeModeTab REDSTONE = new CreativeSubTab.Builder(
			CreativeModeTab.builder()
					.displayItems((params, output) -> {
						output.accept(Items.REDSTONE);
						output.accept(Items.REDSTONE_TORCH);
						output.accept(Items.REDSTONE_BLOCK);
						output.accept(Items.REPEATER);
						output.accept(Items.COMPARATOR);
						output.accept(Items.REDSTONE_ORE);
					}), TAB, Identifier.fromNamespaceAndPath("fractal", "redstone"), Component.translatable("itemGroup.fractal.redstone"))
			.styled(STYLE_REDSTONE) // Use the redstone style we defined above.
			.build();
	
	public static final CreativeModeTab COMPONENTS = new CreativeSubTab.Builder(
			CreativeModeTab.builder()
					.displayItems((params, output) -> {
						output.accept(Items.WAXED_COPPER_BULB);
						output.accept(Items.WAXED_EXPOSED_COPPER_BULB);
						output.accept(Items.WAXED_WEATHERED_COPPER_BULB);
						output.accept(Items.WAXED_OXIDIZED_COPPER_BULB);
						output.accept(Items.LEVER);
						output.accept(Items.OAK_BUTTON);
						output.accept(Items.STONE_BUTTON);
						output.accept(Items.OAK_PRESSURE_PLATE);
						output.accept(Items.STONE_PRESSURE_PLATE);
						output.accept(Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
						output.accept(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
						output.accept(Items.TRIPWIRE_HOOK);
						output.accept(Items.STRING);
						output.accept(Items.DAYLIGHT_DETECTOR);
						output.accept(Items.PISTON);
						output.accept(Items.STICKY_PISTON);
						output.accept(Items.SLIME_BLOCK);
						output.accept(Items.HONEY_BLOCK);
						output.accept(Items.OBSERVER);
						output.accept(Items.NOTE_BLOCK);
					}), TAB, id("components"), Component.translatable("itemGroup.fractal.components"))
			.styled(STYLE_COMPONENTS)
			.build();
	public static final CreativeModeTab LOGISTICS = new CreativeSubTab.Builder(
			CreativeModeTab.builder()
					.displayItems((params, output) -> {
						output.accept(Items.DISPENSER);
						output.accept(Items.DROPPER);
						output.accept(Items.HOPPER);
						output.accept(Items.RAIL);
						output.accept(Items.POWERED_RAIL);
						output.accept(Items.DETECTOR_RAIL);
						output.accept(Items.ACTIVATOR_RAIL);
						output.accept(Items.MINECART);
						output.accept(Items.HOPPER_MINECART);
						output.accept(Items.CHEST_MINECART);
						output.accept(Items.FURNACE_MINECART);
						output.accept(Items.TNT_MINECART);
						output.accept(Items.OAK_CHEST_BOAT);
						output.accept(Items.BAMBOO_CHEST_RAFT);
					}), TAB, id("logistics"), Component.translatable("itemGroup.fractal.logistics"))
			.styled(STYLE_LOGISTICS)
			.build();
	public static final CreativeModeTab AUTOMATION = new CreativeSubTab.Builder(
			CreativeModeTab.builder()
					.displayItems((params, output) -> {
						output.accept(Items.CRAFTER);
					}), TAB, id("automation"), Component.translatable("itemGroup.fractal.automation"))
			.styled(STYLE_AUTOMATION)
			.build();
	public static final CreativeModeTab COMPARATOR_OUTPUTS = new CreativeSubTab.Builder(
			CreativeModeTab.builder()
					.displayItems((params, output) -> {
						output.accept(Items.CHEST);
						output.accept(Items.BARREL);
						output.accept(Items.CHISELED_BOOKSHELF);
						output.accept(Items.FURNACE);
						output.accept(Items.TRAPPED_CHEST);
						output.accept(Items.JUKEBOX);
						output.accept(Items.DECORATED_POT);
						output.accept(Items.COMPOSTER);
						output.accept(Items.CAULDRON);
					}), TAB, id("comparator_outputs"), Component.translatable("itemGroup.fractal.comparator_outputs"))
			.build();
	public static final CreativeModeTab MISC = new CreativeSubTab.Builder(
			CreativeModeTab.builder()
					.displayItems((params, output) -> {
						output.accept(Items.TARGET);
						output.accept(Items.SCULK_SENSOR);
						output.accept(Items.CALIBRATED_SCULK_SENSOR);
						output.accept(Items.SCULK_SHRIEKER);
						output.accept(Items.AMETHYST_BLOCK);
						output.accept(Items.WHITE_WOOL);
						output.accept(Items.LECTERN);
						output.accept(Items.LIGHTNING_ROD);
						output.accept(Items.OAK_DOOR);
						output.accept(Items.IRON_DOOR);
						output.accept(Items.OAK_FENCE_GATE);
						output.accept(Items.OAK_TRAPDOOR);
						output.accept(Items.IRON_TRAPDOOR);
						output.accept(Items.TNT);
						output.accept(Items.REDSTONE_LAMP);
						output.accept(Items.BELL);
						output.accept(Items.BIG_DRIPLEAF);
						output.accept(Items.ARMOR_STAND);
					}), TAB, id("misc"), Component.translatable("itemGroup.fractal.misc"))
			// Normally subtab titles show as `<Parent Tab Title> <Sub Tab Title>`, this changes it to just be the sub tab's title.
			.hideParentTitle()
			.build();
	
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	
	public FractalTestMod(IEventBus modBus) {
		TABS.register("tab", () -> TAB);
		TABS.register(modBus);
	}
	
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}
