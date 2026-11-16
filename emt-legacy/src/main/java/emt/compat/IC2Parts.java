package emt.compat;

import emt.recipe.HostIngredient;
import java.util.HashMap;
import java.util.Map;

/**
 * IndustrialCraft 2 items by their 1.7.10 IC2Items name, mapped to the registry ids of the
 * IndustrialCraft 2 port for 1.20.1 (Sviluppatore). Used as recipe ingredients only.
 */
public final class IC2Parts {
    private static final Map<String, HostIngredient> PARTS = new HashMap<String, HostIngredient>();

    static {
        put("iridiumPlate", "Iridium Reinforced Plate", "ic2:iridium_plate");
        put("carbonPlate", "Carbon Plate", "ic2:carbon_plate");
        put("lapotronCrystal", "Lapotron Crystal", "ic2:batlamacrystal");
        put("energyCrystal", "Energy Crystal", "ic2:energycrystal");
        put("advBattery", "Advanced RE-Battery", "ic2:advbat");
        put("reBattery", "RE-Battery", "ic2:batredischarged");
        put("chargedReBattery", "RE-Battery", "ic2:batredischarged");
        put("electronicCircuit", "Electronic Circuit", "ic2:partcircuit");
        put("advancedCircuit", "Advanced Circuit", "ic2:partcircuitadv");
        put("coil", "Coil", "ic2:partcoil");
        put("elemotor", "Electric Motor", "ic2:partelemotor");
        put("plateiron", "Iron Plate", "ic2:iron_plate");
        put("denseplateiron", "Dense Iron Plate", "ic2:dense_iron_plate");
        put("plateobsidian", "Obsidian Plate", "ic2:obsidian_plate");
        put("reinforcedStone", "Reinforced Stone", "ic2:blockalloy");
        put("reinforcedGlass", "Reinforced Glass", "ic2:blockalloyglass");
        put("machine", "Machine Casing", "ic2:blockmachine");
        put("advancedMachine", "Advanced Machine Casing", "ic2:blockadvmachine");
        put("generator", "Generator", "ic2:generator");
        put("geothermalGenerator", "Geothermal Generator", "ic2:geogenerator");
        put("semifluidGenerator", "Semifluid Generator", "ic2:semifluidgenerator");
        put("solarPanel", "Solar Panel", "ic2:solargenerator");
        put("nuclearReactor", "Nuclear Reactor", "ic2:nuclearreactor");
        put("batBox", "BatBox", "ic2:batbox");
        put("mvTransformer", "MV Transformer", "ic2:transformermv");
        put("macerator", "Macerator", "ic2:macerator");
        put("electroFurnace", "Electric Furnace", "ic2:elecfurnace");
        put("orewashingplant", "Ore Washing Plant", "ic2:orewashingplant");
        put("replicator", "Replicator", "ic2:replicator");
        put("overclockerUpgrade", "Overclocker Upgrade", "ic2:upgrademodule");
        put("glassFiberCableItem", "Glass Fibre Cable", "ic2:cable");
        put("scrap", "Scrap", "ic2:scrap");
        put("rubber", "Rubber", "ic2:rubber");
        put("resin", "Sticky Resin", "ic2:resin");
        put("chainsaw", "Chainsaw", "ic2:toolchainsaw");
        put("miningDrill", "Mining Drill", "ic2:tooldrill");
        put("diamondDrill", "Diamond Drill", "ic2:toolddrill");
        put("electricHoe", "Electric Hoe", "ic2:toolhoe");
        put("miningLaser", "Mining Laser", "ic2:toolmininglaser");
        put("nanoSaber", "Nano Saber", "ic2:nanosaber");
        put("ForgeHammer", "Forge Hammer", "ic2:forgehammer");
        put("nanoHelmet", "NanoSuit Helmet", "ic2:armornanohelmet");
        put("nanoBodyarmor", "NanoSuit Bodyarmor", "ic2:armornanochestplate");
        put("nanoBoots", "NanoSuit Boots", "ic2:armornanoboots");
        put("quantumHelmet", "QuantumSuit Helmet", "ic2:armorquantumhelmet");
        put("quantumBodyarmor", "QuantumSuit Bodyarmor", "ic2:armorquantumchestplate");
        put("quantumBoots", "QuantumSuit Boots", "ic2:armorquantumboots");
        put("hazmatBoots", "Rubber Boots", "ic2:armorrubboots");
        put("energyPack", "EnergyPack", "ic2:armorenergypack");
        put("electricJetpack", "Electric Jetpack", "ic2:armorjetpackelectric");
        put("Uran238", "Uranium 238", "ic2:uranium238");
        put("smallUran235", "Tiny Pile of Uranium 235", "ic2:uranium235small");
        put("uraniumOre", "Uranium Ore", "ic2:uranium_ore");
        put("iridiumOre", "Iridium", "ic2:iridium_ore");
        put("uuMatterCell", "UU-Matter Cell", "ic2:celluumatter");
        put("stoneDust", "Stone Dust", "ic2:stone_dust");
        put("coalDust", "Coal Dust", "ic2:coal_dust");
        put("smallGoldDust", "Tiny Pile of Gold Dust", "ic2:tiny_gold_dust");
        put("smallIronDust", "Tiny Pile of Iron Dust", "ic2:tiny_iron_dust");
        put("smallCopperDust", "Tiny Pile of Copper Dust", "ic2:tiny_copper_dust");
        put("smallTinDust", "Tiny Pile of Tin Dust", "ic2:tiny_tin_dust");
        put("smallSilverDust", "Tiny Pile of Silver Dust", "ic2:tiny_silver_dust");
        put("smallLeadDust", "Tiny Pile of Lead Dust", "ic2:tiny_lead_dust");
        put("copperOre", "Copper Ore", "ic2:copper_ore");
        put("tinOre", "Tin Ore", "ic2:tin_ore");
        put("leadOre", "Lead Ore", "ic2:lead_ore");
        put("crushedCopperOre", "Crushed Copper Ore", "ic2:crushed_copper_ore");
        put("crushedTinOre", "Crushed Tin Ore", "ic2:crushed_tin_ore");
        put("crushedLeadOre", "Crushed Lead Ore", "ic2:crushed_lead_ore");
        put("crushedIronOre", "Crushed Iron Ore", "ic2:crushed_iron_ore");
        put("crushedGoldOre", "Crushed Gold Ore", "ic2:crushed_gold_ore");
        put("nightvisionGoggles", "Nightvision Goggles", "ic2:nightvisiongoggles");
        put("windKineticGenerator", "Wind Kinetic Generator", "ic2:windkineticgenerator");
        put("waterKineticGenerator", "Water Kinetic Generator", "ic2:waterkineticgenerator");
    }

    private IC2Parts() {
    }

    private static void put(String legacyName, String display, String... ids) {
        PARTS.put(legacyName, new HostIngredient(display, ids));
    }

    /** Ingredient for an IC2 1.7.10 item name; throws if the name was not mapped. */
    public static HostIngredient ing(String legacyName) {
        HostIngredient part = PARTS.get(legacyName);
        if (part == null) {
            throw new IllegalArgumentException("No IndustrialCraft 2 (1.20.1) mapping for IC2 item '" + legacyName + "'");
        }
        return part;
    }

    /** Same ingredient with a different stack size, used for recipe outputs. */
    public static HostIngredient ing(String legacyName, int count) {
        return ing(legacyName).withCount(count);
    }
}
