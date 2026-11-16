package emt.init;

import cpw.mods.fml.common.registry.GameRegistry;
import emt.EMT;
import emt.compat.IC2Parts;
import emt.recipe.EMTArcaneRecipe;
import emt.recipe.HostIngredient;
import emt.recipe.EMTCrucibleRecipe;
import emt.recipe.EMTDisplayShapedRecipe;
import emt.recipe.EMTInfusionRecipe;
import emt.recipe.EMTShapelessArcaneRecipe;
import emt.util.EMTConfigHandler;
import emt.util.EMTCraftingAspects;
import emt.util.EMTRandomHelper;
import emt.util.EMTResearchAspects;
import java.util.Iterator;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.common.blocks.ItemJarNode;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public class EMTRecipes {
   public static ItemStack uuMCell = new ItemStack(EMTItems.itemEMTItems, 1, 15);
   public static IRecipe ironOmnitool;
   public static IRecipe diamondChainsaw;
   public static IRecipe ductTape;
   public static IRecipe rubberBall;
   public static IRecipe featherMesh;
   public static IRecipe cardboard;
   public static IRecipe featherWing;
   public static IRecipe featherWings;
   public static IRecipe taintedFeathers;
   public static InfusionRecipe uraniumToIridium;
   public static InfusionRecipe diamondToUranium;
   public static InfusionRecipe goldToDiamond;
   public static InfusionRecipe ironToGold;
   public static InfusionRecipe stoneBricksToIronOre;
   public static InfusionRecipe arcaneStoneToCopperOre;
   public static InfusionRecipe arcaneStoneBricksToTinOre;
   public static InfusionRecipe amberBlockToLeadOre;
   public static InfusionRecipe amberBricksToUraniumOre;
   public static InfusionRecipe charcoalToCoal;
   public static InfusionRecipe shardToResin;
   public static InfusionRecipe shardToRedstone;
   public static InfusionRecipe shardToLapis;
   public static InfusionRecipe glowstoneDustToBlock;
   public static InfusionRecipe infusedQuantumArmor;
   public static InfusionRecipe thaumiumDrill;
   public static InfusionRecipe thaumiumChainsaw;
   public static InfusionRecipe thaumicQuantumHelmet;
   public static InfusionRecipe thaumiumOmnitool;
   public static InfusionRecipe thaumicNanoHelmet;
   public static InfusionRecipe explosionFocus;
   public static InfusionRecipe shieldFocus;
   public static InfusionRecipe potentiaGenerator;
   public static InfusionRecipe streamChainsaw;
   public static InfusionRecipe rockbreakerDrill;
   public static InfusionRecipe thorHammer;
   public static InfusionRecipe superchargedThorHammer;
   public static InfusionRecipe wandRecharger;
   public static InfusionRecipe solarHelmetRevealing;
   public static InfusionRecipe electricBootsTravel;
   public static InfusionRecipe nanoBootsTravel;
   public static InfusionRecipe quantumBootsTravel;
   public static InfusionRecipe diamondOmnitoolToThaumium;
   public static InfusionRecipe etheralProcessor;
   public static InfusionRecipe tripleCompressedSolar;
   public static InfusionRecipe electricHoeGrowth;
   public static InfusionRecipe chargeFocus;
   public static InfusionRecipe wandChargeFocus;
   public static InfusionRecipe energyBallFocus;
   public static InfusionRecipe inventoryChargingRing;
   public static InfusionRecipe armorChargingRing;
   public static InfusionRecipe nanoWings;
   public static InfusionRecipe quantumWings;
   public static ShapelessArcaneRecipe diamondOmnitool;
   public static ShapelessArcaneRecipe tinyUranium;
   public static ShapedArcaneRecipe christmasFocus;
   public static ShapedArcaneRecipe electricGoggles;
   public static ShapedArcaneRecipe electricGoggles2;
   public static ShapedArcaneRecipe shieldBlock;
   public static ShapedArcaneRecipe compressedSolar;
   public static ShapedArcaneRecipe doubleCompressedSolar;
   public static ShapedArcaneRecipe ironOmnitoolToDiamond;
   public static ShapedArcaneRecipe electricScribingTools;
   public static ShapedArcaneRecipe thaumiumWing;
   public static ShapedArcaneRecipe thaumiumWings;
   public static CrucibleRecipe ignisGenerator;
   public static CrucibleRecipe auramGenerator;
   public static CrucibleRecipe arborGenerator;
   public static CrucibleRecipe aerGenerator;
   public static CrucibleRecipe waterSolar;
   public static CrucibleRecipe doubleWaterSolar;
   public static CrucibleRecipe tripleWaterSolar;
   public static CrucibleRecipe darkSolar;
   public static CrucibleRecipe doubleDarkSolar;
   public static CrucibleRecipe tripleDarkSolar;
   public static CrucibleRecipe orderSolar;
   public static CrucibleRecipe doubleOrderSolar;
   public static CrucibleRecipe tripleOrderSolar;
   public static CrucibleRecipe fireSolar;
   public static CrucibleRecipe doubleFireSolar;
   public static CrucibleRecipe tripleFireSolar;
   public static CrucibleRecipe airSolar;
   public static CrucibleRecipe doubleAirSolar;
   public static CrucibleRecipe tripleAirSolar;
   public static CrucibleRecipe earthSolar;
   public static CrucibleRecipe doubleEarthSolar;
   public static CrucibleRecipe tripleEarthSolar;
   public static CrucibleRecipe portableNode;
   public static CrucibleRecipe uuMCrystal;
   public static IRecipe thaumiumPlate;

   private static void registerShapedRecipes() {
      ironOmnitool = new EMTDisplayShapedRecipe(
         EMTRandomHelper.getChargedItem(EMTItems.ironOmnitool, 10),
         new Object[]{
            "X",
            "Z",
            "Y",
            'X',
            IC2Parts.ing("chainsaw"),
            'Y',
            IC2Parts.ing("miningDrill"),
            'Z',
            IC2Parts.ing("plateiron")
         }
      );
      diamondChainsaw = new EMTDisplayShapedRecipe(
         EMTRandomHelper.getChargedItem(EMTItems.diamondChainsaw, 10),
         new Object[]{" X ", "XYX", 'X', new ItemStack(Items.diamond), 'Y', IC2Parts.ing("chainsaw")}
      );
      ductTape = GameRegistry.addShapedRecipe(
         new ItemStack(EMTItems.itemEMTItems, 1, 9),
         new Object[]{"XXX", "YYY", "XXX", 'X', new ItemStack(EMTItems.itemEMTItems, 1, 8), 'Y', new ItemStack(Items.paper)}
      );
      featherMesh = GameRegistry.addShapedRecipe(
         new ItemStack(EMTItems.itemEMTItems, 1, 7),
         new Object[]{"XYX", "YXY", "XYX", 'X', new ItemStack(Items.feather), 'Y', new ItemStack(EMTItems.itemEMTItems, 1, 9)}
      );
      featherWing = GameRegistry.addShapedRecipe(
         new ItemStack(EMTItems.itemEMTItems, 1, 12),
         new Object[]{
            "XYZ",
            "XYZ",
            "XYZ",
            'X',
            new ItemStack(EMTItems.itemEMTItems, 1, 7),
            'Y',
            new ItemStack(EMTItems.itemEMTItems, 1, 8),
            'Z',
            new ItemStack(EMTItems.itemEMTItems, 1, 11)
         }
      );
      featherWings = GameRegistry.addShapedRecipe(new ItemStack(EMTItems.featherWing), new Object[]{"XX", 'X', new ItemStack(EMTItems.itemEMTItems, 1, 12)});
      taintedFeathers = GameRegistry.addShapedRecipe(
         new ItemStack(EMTItems.itemEMTItems, 1, 13),
         new Object[]{" X ", "XYX", " X ", 'X', new ItemStack(ConfigItems.itemResource, 1, 12), 'Y', new ItemStack(Items.feather)}
      );
      // (EMT 1.2.6.2 changed an IndustrialCraft 2 recipe here; the 1.20.1 port keeps its own)
      // (EMT 1.2.6.2 changed an IndustrialCraft 2 recipe here; the 1.20.1 port keeps its own)
      // (EMT 1.2.6.2 changed an IndustrialCraft 2 recipe here; the 1.20.1 port keeps its own)

      for (int i = 0; i < OreDictionary.getOres("logWood").size(); i++) {
         cardboard = GameRegistry.addShapedRecipe(
            new ItemStack(EMTItems.itemEMTItems, 1, 11),
            new Object[]{"XXX", "YYY", "XXX", 'X', new ItemStack(Items.paper), 'Y', OreDictionary.getOres("logWood").get(i)}
         );
      }

      for (int i = 0; i < OreDictionary.getOres("itemRubber").size(); i++) {
         rubberBall = GameRegistry.addShapedRecipe(
            new ItemStack(EMTItems.itemEMTItems, 1, 10), new Object[]{"XX", "XX", 'X', OreDictionary.getOres("itemRubber").get(i)}
         );
      }

      GameRegistry.addSmelting(new ItemStack(EMTItems.itemEMTItems, 1, 1), new ItemStack(ConfigItems.itemResource, 1, 6), 0.0F);
      GameRegistry.addSmelting(new ItemStack(EMTItems.itemEMTItems, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 6), 0.0F);
      GameRegistry.addSmelting(new ItemStack(EMTItems.itemEMTItems, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 3), 0.0F);
      GameRegistry.addSmelting(new ItemStack(EMTItems.itemEMTItems, 1, 4), new ItemStack(ConfigItems.itemResource, 1, 3), 0.0F);
      GameRegistry.addSmelting(new ItemStack(EMTItems.itemEMTItems, 1, 10), new ItemStack(EMTItems.itemEMTItems, 2, 8), 0.0F);
   }

   private static void registerShaplessRecipes() {
   }

   private static void registerSmeltingRecipes() {
   }

   public static void registerEarlyRecipes() {
      registerShapedRecipes();
      registerShaplessRecipes();
      registerSmeltingRecipes();
   }

   public static void registerUUMInfusionRecipes() {
      HostIngredient iridium = IC2Parts.ing("iridiumOre", 2);
      uraniumToIridium = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         iridium,
         4,
         EMTCraftingAspects.uuMatterInfusions,
         IC2Parts.ing("Uran238"),
         new Object[]{uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell}
      );
      HostIngredient uranium = IC2Parts.ing("Uran238", 2);
      diamondToUranium = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         uranium,
         4,
         EMTCraftingAspects.lesserUUMInfusions,
         new ItemStack(Items.diamond),
         new Object[]{uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell, uuMCell}
      );
      ItemStack diamond = new ItemStack(Items.diamond).copy();
      diamond.stackSize = 1;
      goldToDiamond = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         diamond,
         4,
         EMTCraftingAspects.cheapUUMInfusions,
         new ItemStack(Items.gold_ingot),
         new Object[]{uuMCell, uuMCell, uuMCell, uuMCell}
      );
      ItemStack gold = new ItemStack(Items.gold_ingot).copy();
      gold.stackSize = 2;
      ironToGold = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         gold,
         4,
         EMTCraftingAspects.cheapestUUMInfusions,
         new ItemStack(Items.iron_ingot),
         new Object[]{uuMCell, uuMCell, uuMCell}
      );
      ItemStack ironOre = new ItemStack(Blocks.iron_ore).copy();
      ironOre.stackSize = 16;
      stoneBricksToIronOre = EMTInfusionRecipe.register(
         "UU-Matter Infusion", ironOre, 4, EMTCraftingAspects.cheapUUMInfusions, new ItemStack(Blocks.stonebrick), new Object[]{uuMCell, uuMCell}
      );
      HostIngredient copperOre = IC2Parts.ing("crushedCopperOre", 32);
      arcaneStoneToCopperOre = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         copperOre,
         4,
         EMTCraftingAspects.cheapUUMInfusions,
         new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),
         new Object[]{uuMCell, uuMCell}
      );
      HostIngredient tinOre = IC2Parts.ing("crushedTinOre", 32);
      arcaneStoneBricksToTinOre = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         tinOre,
         4,
         EMTCraftingAspects.cheapUUMInfusions,
         new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7),
         new Object[]{uuMCell, uuMCell}
      );
      HostIngredient leadOre = IC2Parts.ing("crushedLeadOre", 32);
      amberBlockToLeadOre = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         leadOre,
         4,
         EMTCraftingAspects.cheapUUMInfusions,
         new ItemStack(ConfigBlocks.blockCosmeticOpaque, 1, 0),
         new Object[]{uuMCell, uuMCell}
      );
      HostIngredient uraniumOre = IC2Parts.ing("uraniumOre", 8);
      amberBricksToUraniumOre = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         uraniumOre,
         4,
         EMTCraftingAspects.lesserUUMInfusions,
         new ItemStack(ConfigBlocks.blockCosmeticOpaque, 1, 1),
         new Object[]{uuMCell, uuMCell, uuMCell}
      );
      ItemStack coal = new ItemStack(Items.coal, 1, 0).copy();
      coal.stackSize = 16;
      charcoalToCoal = EMTInfusionRecipe.register(
         "UU-Matter Infusion", coal, 4, EMTCraftingAspects.cheapUUMInfusions, new ItemStack(Items.coal, 1, 1), new Object[]{uuMCell, uuMCell}
      );
      HostIngredient stickyResin = IC2Parts.ing("resin", 21);
      shardToResin = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         stickyResin,
         4,
         EMTCraftingAspects.cheapUUMInfusions,
         new ItemStack(ConfigItems.itemShard, 1, 32767),
         new Object[]{uuMCell, uuMCell, uuMCell}
      );
      ItemStack redstone = new ItemStack(Items.redstone).copy();
      redstone.stackSize = 24;
      shardToRedstone = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         redstone,
         4,
         EMTCraftingAspects.cheapUUMInfusions,
         new ItemStack(ConfigItems.itemShard, 1, 32767),
         new Object[]{uuMCell, uuMCell, uuMCell}
      );
      ItemStack lapisLazuli = new ItemStack(Items.dye, 1, 4).copy();
      lapisLazuli.stackSize = 8;
      shardToLapis = EMTInfusionRecipe.register(
         "UU-Matter Infusion",
         lapisLazuli,
         4,
         EMTCraftingAspects.cheapUUMInfusions,
         new ItemStack(ConfigItems.itemShard, 1, 32767),
         new Object[]{uuMCell, uuMCell, uuMCell}
      );
      ItemStack glowstone = new ItemStack(Blocks.glowstone).copy();
      glowstone.stackSize = 1;
      glowstoneDustToBlock = EMTInfusionRecipe.register(
         "UU-Matter Infusion", glowstone, 4, EMTCraftingAspects.cheapestUUMInfusions, new ItemStack(Items.glowstone_dust), new Object[]{uuMCell}
      );
   }

   public static void registerLateRecipes() {
      thaumiumDrill = EMTInfusionRecipe.register(
         "Thaumium Drill",
         EMTRandomHelper.getChargedItem(EMTItems.thaumiumDrill, 10),
         5,
         EMTCraftingAspects.thaumiumDrillCrafting,
         IC2Parts.ing("diamondDrill"),
         new Object[]{
            new ItemStack(Items.diamond),
            new ItemStack(Items.diamond),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            IC2Parts.ing("denseplateiron")
         }
      );
      thaumiumChainsaw = EMTInfusionRecipe.register(
         "Thaumium Chainsaw",
         EMTRandomHelper.getChargedItem(EMTItems.thaumiumChainsaw, 10),
         5,
         EMTCraftingAspects.thaumiumChainsawCrafting,
         new ItemStack(EMTItems.diamondChainsaw, 1, 32767),
         new Object[]{
            new ItemStack(Items.diamond),
            new ItemStack(Items.diamond),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            IC2Parts.ing("denseplateiron")
         }
      );
      thaumicQuantumHelmet = EMTInfusionRecipe.register(
         "Quantum Goggles of Revealing",
         EMTRandomHelper.getChargedItem(EMTItems.quantumThaumicHelmet, 10),
         6,
         EMTCraftingAspects.thaumicQuantumHelmetCrafting,
         new ItemStack(EMTItems.nanoThaumicHelmet, 1, 32767),
         new Object[]{
            new ItemStack(Items.diamond),
            new ItemStack(Items.milk_bucket),
            IC2Parts.ing("quantumHelmet"),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("advancedCircuit")
         }
      );
      thaumicNanoHelmet = EMTInfusionRecipe.register(
         "Nanosuit Goggles of Revealing",
         EMTRandomHelper.getChargedItem(EMTItems.nanoThaumicHelmet, 10),
         5,
         EMTCraftingAspects.thaumicNanoHelmetCrafting,
         new ItemStack(EMTItems.electricGoggles, 1, 32767),
         new Object[]{
            new ItemStack(Items.diamond),
            new ItemStack(Items.gold_ingot),
            IC2Parts.ing("nanoHelmet"),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            IC2Parts.ing("carbonPlate"),
            IC2Parts.ing("electronicCircuit")
         }
      );
      thaumiumOmnitool = EMTInfusionRecipe.register(
         "Thaumium Omnitool",
         EMTRandomHelper.getChargedItem(EMTItems.thaumiumOmnitool, 10),
         6,
         EMTCraftingAspects.thaumiumOmnitoolCrafting,
         new ItemStack(EMTItems.thaumiumChainsaw, 1, 32767),
         new Object[]{
            new ItemStack(EMTItems.thaumiumDrill, 1, 32767),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            IC2Parts.ing("carbonPlate"),
            IC2Parts.ing("plateobsidian")
         }
      );
      diamondOmnitoolToThaumium = EMTInfusionRecipe.register(
         "Thaumium Omnitool",
         new ItemStack(EMTItems.thaumiumOmnitool),
         6,
         EMTCraftingAspects.thaumiumOmnitoolCrafting,
         new ItemStack(EMTItems.diamondOmnitool, 1, 32767),
         new Object[]{
            new ItemStack(Blocks.diamond_block),
            new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9),
            new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9),
            IC2Parts.ing("energyCrystal"),
            IC2Parts.ing("carbonPlate"),
            IC2Parts.ing("advancedCircuit")
         }
      );
      explosionFocus = EMTInfusionRecipe.register(
         "Explosion Focus",
         new ItemStack(EMTItems.explosionFocus),
         6,
         EMTCraftingAspects.laserFocusCrafting,
         new ItemStack(ConfigItems.itemFocusHellbat, 1),
         new Object[]{
            IC2Parts.ing("miningLaser"),
            new ItemStack(Items.arrow),
            new ItemStack(Items.gunpowder),
            new ItemStack(Items.firework_charge),
            new ItemStack(ConfigItems.itemResource, 1, 1)
         }
      );
      shieldFocus = EMTInfusionRecipe.register(
         "Shield Focus",
         new ItemStack(EMTItems.shieldFocus),
         4,
         EMTCraftingAspects.shieldFocusCrafting,
         new ItemStack(ConfigItems.itemFocusPortableHole, 1),
         new Object[]{
            IC2Parts.ing("reinforcedStone"),
            IC2Parts.ing("reinforcedGlass"),
            IC2Parts.ing("reinforcedStone"),
            IC2Parts.ing("reinforcedGlass"),
            new ItemStack(Blocks.soul_sand),
            new ItemStack(Blocks.obsidian),
            new ItemStack(Blocks.obsidian)
         }
      );
      potentiaGenerator = EMTInfusionRecipe.register(
         "Potentia Generator",
         new ItemStack(EMTBlocks.essentiaGens, 1, 0),
         6,
         EMTCraftingAspects.potentiaGeneratorCrafting,
         IC2Parts.ing("semifluidGenerator"),
         new Object[]{
            new ItemStack(Items.diamond),
            new ItemStack(ConfigItems.itemFocusTrade),
            new ItemStack(Blocks.hopper),
            new ItemStack(ConfigBlocks.blockJar),
            IC2Parts.ing("mvTransformer"),
            IC2Parts.ing("advancedMachine"),
            IC2Parts.ing("orewashingplant"),
            IC2Parts.ing("scrap")
         }
      );
      streamChainsaw = EMTInfusionRecipe.register(
         "Chainsaw of the Stream",
         EMTRandomHelper.getChargedItem(EMTItems.streamChainsaw, 10),
         6,
         EMTCraftingAspects.streamChaisnawCrafting,
         new ItemStack(EMTItems.thaumiumChainsaw, 1, 32767),
         new Object[]{
            new ItemStack(Items.water_bucket),
            new ItemStack(ConfigItems.itemAxeElemental),
            new ItemStack(ConfigBlocks.blockMagicalLog),
            IC2Parts.ing("lapotronCrystal"),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("overclockerUpgrade")
         }
      );
      rockbreakerDrill = EMTInfusionRecipe.register(
         "Drill of the Rockbreaker",
         EMTRandomHelper.getChargedItem(EMTItems.rockbreakerDrill, 10),
         6,
         EMTCraftingAspects.rockbreakerDrillCrafting,
         new ItemStack(EMTItems.thaumiumDrill, 1, 32767),
         new Object[]{
            new ItemStack(Items.flint_and_steel),
            new ItemStack(Items.fire_charge),
            new ItemStack(ConfigItems.itemPickElemental),
            new ItemStack(ConfigItems.itemShovelElemental),
            IC2Parts.ing("lapotronCrystal"),
            IC2Parts.ing("reinforcedStone"),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("overclockerUpgrade")
         }
      );
      thorHammer = EMTInfusionRecipe.register(
         "Mjolnir",
         new ItemStack(EMTItems.thorHammer),
         7,
         EMTCraftingAspects.thorHammerCrafting,
         new ItemStack(EMTItems.taintedThorHammer, 1, 32767),
         new Object[]{
            new ItemStack(EMTItems.itemEMTItems, 1, 6),
            new ItemStack(EMTItems.itemEMTItems, 1, 6),
            new ItemStack(ConfigItems.itemSwordElemental),
            new ItemStack(ConfigItems.itemResource, 1, 1),
            new ItemStack(ConfigItems.itemFocusShock),
            IC2Parts.ing("rubber")
         }
      );
      superchargedThorHammer = EMTInfusionRecipe.register(
         "Supercharged Mjolnir",
         EMTRandomHelper.getChargedItem(EMTItems.electricThorHammer, 10),
         10,
         EMTCraftingAspects.superchargedThorHammerCrafting,
         new ItemStack(EMTItems.thorHammer, 1, 32767),
         new Object[]{
            new ItemStack(EMTItems.itemEMTItems, 1, 6),
            new ItemStack(EMTItems.itemEMTItems, 1, 6),
            new ItemStack(Blocks.web),
            new ItemStack(ConfigItems.itemFocusHellbat),
            new ItemStack(ConfigItems.itemSwordElemental),
            IC2Parts.ing("lapotronCrystal"),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("nanoSaber")
         }
      );
      wandRecharger = EMTInfusionRecipe.register(
         "Industrial Wand Charging Station",
         new ItemStack(EMTBlocks.emtMachines, 1, 0),
         6,
         EMTCraftingAspects.wandCharger,
         new ItemStack(ConfigBlocks.blockStoneDevice, 1, 5),
         new Object[]{
            IC2Parts.ing("replicator"),
            IC2Parts.ing("iridiumPlate"),
            new ItemStack(Blocks.diamond_block),
            new ItemStack(ConfigItems.itemShard, 1, 5),
            new ItemStack(ConfigBlocks.blockJar)
         }
      );
      solarHelmetRevealing = EMTInfusionRecipe.register(
         "Solar Helmet of Revealing",
         EMTRandomHelper.getChargedItem(EMTItems.solarHelmetRevealing, 10),
         5,
         EMTCraftingAspects.solarHelmetRevealing,
         new ItemStack(EMTItems.quantumThaumicHelmet, 1, 32767),
         new Object[]{
            new ItemStack(EMTBlocks.emtSolars, 1, 1),
            IC2Parts.ing("glassFiberCableItem"),
            IC2Parts.ing("lapotronCrystal"),
            new ItemStack(ConfigItems.itemShard, 1, 4)
         }
      );
      electricBootsTravel = EMTInfusionRecipe.register(
         "Electric Boots of the Traveller",
         EMTRandomHelper.getChargedItem(EMTItems.electricBootsTraveller, 10),
         2,
         EMTCraftingAspects.electricBootsTravel,
         new ItemStack(ConfigItems.itemBootsTraveller),
         new Object[]{
            new ItemStack(Items.diamond),
            IC2Parts.ing("elemotor"),
            IC2Parts.ing("coil"),
            IC2Parts.ing("hazmatBoots"),
            IC2Parts.ing("advBattery")
         }
      );
      nanoBootsTravel = EMTInfusionRecipe.register(
         "Nano Boots of the Traveller",
         EMTRandomHelper.getChargedItem(EMTItems.nanoBootsTraveller, 10),
         2,
         EMTCraftingAspects.nanoBootsTravel,
         new ItemStack(EMTItems.electricBootsTraveller, 1, 32767),
         new Object[]{
            new ItemStack(Items.diamond),
            IC2Parts.ing("nanoBoots"),
            IC2Parts.ing("energyCrystal")
         }
      );
      quantumBootsTravel = EMTInfusionRecipe.register(
         "Quantum Boots of the Traveller",
         EMTRandomHelper.getChargedItem(EMTItems.quantumBootsTraveller, 10),
         2,
         EMTCraftingAspects.quantumBootsTravel,
         new ItemStack(EMTItems.nanoBootsTraveller, 1, 32767),
         new Object[]{
            new ItemStack(Blocks.diamond_block),
            IC2Parts.ing("quantumBoots"),
            IC2Parts.ing("lapotronCrystal"),
            IC2Parts.ing("iridiumPlate")
         }
      );
      etheralProcessor = EMTInfusionRecipe.register(
         "Etheral Processor",
         new ItemStack(EMTBlocks.emtMachines, 1, 1),
         3,
         EMTCraftingAspects.etherealProcessor,
         IC2Parts.ing("macerator"),
         new Object[]{
            IC2Parts.ing("electroFurnace"),
            new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9),
            new ItemStack(Blocks.end_stone),
            new ItemStack(Blocks.end_stone),
            new ItemStack(Blocks.iron_block),
            new ItemStack(Blocks.iron_block),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5)
         }
      );
      tripleCompressedSolar = ThaumcraftApi.addInfusionCraftingRecipe(
         "Compressed Solars",
         new ItemStack(EMTBlocks.emtSolars, 1, 2),
         2,
         EMTCraftingAspects.compressedSolars,
         new ItemStack(EMTBlocks.emtSolars, 1, 1),
         new ItemStack[]{
            new ItemStack(EMTBlocks.emtSolars, 1, 1),
            new ItemStack(EMTBlocks.emtSolars, 1, 1),
            new ItemStack(EMTBlocks.emtSolars, 1, 1),
            new ItemStack(EMTBlocks.emtSolars, 1, 1),
            new ItemStack(EMTBlocks.emtSolars, 1, 1),
            new ItemStack(EMTBlocks.emtSolars, 1, 1),
            new ItemStack(EMTBlocks.emtSolars, 1, 1),
            new ItemStack(EMTBlocks.emtSolars, 1, 1)
         }
      );
      electricHoeGrowth = EMTInfusionRecipe.register(
         "Electric Hoe of Growth",
         EMTRandomHelper.getChargedItem(EMTItems.electricHoeGrowth, 10),
         4,
         EMTCraftingAspects.electricHoeGrowth,
         new ItemStack(ConfigItems.itemHoeElemental),
         new Object[]{
            IC2Parts.ing("lapotronCrystal"),
            IC2Parts.ing("electricHoe"),
            new ItemStack(Items.dye, 1, 15),
            IC2Parts.ing("elemotor"),
            IC2Parts.ing("coil"),
            new ItemStack(Blocks.sapling, 1, 32767)
         }
      );
      chargeFocus = EMTInfusionRecipe.register(
         "Wand Focus: Charging",
         new ItemStack(EMTItems.chargeFocus),
         4,
         EMTCraftingAspects.chargeFocus,
         IC2Parts.ing("advBattery"),
         new Object[]{
            IC2Parts.ing("generator"),
            IC2Parts.ing("batBox"),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            new ItemStack(ConfigItems.itemShard, 1, 32767),
            new ItemStack(ConfigItems.itemShard, 1, 32767)
         }
      );
      wandChargeFocus = EMTInfusionRecipe.register(
         "Wand Focus: Wand Charging",
         new ItemStack(EMTItems.wandChargeFocus),
         5,
         EMTCraftingAspects.wandChargeFocus,
         new ItemStack(EMTBlocks.emtMachines, 1, 0),
         new Object[]{
            new ItemStack(EMTItems.chargeFocus),
            IC2Parts.ing("energyPack"),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("iridiumPlate")
         }
      );
      energyBallFocus = EMTInfusionRecipe.register(
         "Wand Focus: Energy Ball",
         new ItemStack(EMTItems.energyBallFocus),
         4,
         EMTResearchAspects.energyBallFocusResearch,
         new ItemStack(ConfigItems.itemFocusShock),
         new Object[]{
            new ItemStack(Blocks.tnt),
            IC2Parts.ing("advBattery"),
            IC2Parts.ing("advBattery"),
            IC2Parts.ing("advBattery")
         }
      );
      inventoryChargingRing = EMTInfusionRecipe.register(
         "Inventory Charging Ring",
         new ItemStack(EMTItems.emtBauble, 1, 1),
         6,
         EMTCraftingAspects.inventoryChargingRing,
         new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1),
         new Object[]{
            new ItemStack(EMTItems.thaumiumDrill, 1, 32767),
            IC2Parts.ing("generator"),
            IC2Parts.ing("geothermalGenerator"),
            IC2Parts.ing("solarPanel"),
            IC2Parts.ing("windKineticGenerator"),
            IC2Parts.ing("waterKineticGenerator"),
            IC2Parts.ing("nuclearReactor")
         }
      );
      armorChargingRing = EMTInfusionRecipe.register(
         "Armor Charging Ring",
         new ItemStack(EMTItems.emtBauble, 1, 0),
         6,
         EMTCraftingAspects.armorChargingRing,
         new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1),
         new Object[]{
            new ItemStack(EMTItems.electricBootsTraveller, 1, 32767),
            IC2Parts.ing("generator"),
            IC2Parts.ing("geothermalGenerator"),
            IC2Parts.ing("solarPanel"),
            IC2Parts.ing("windKineticGenerator"),
            IC2Parts.ing("waterKineticGenerator"),
            IC2Parts.ing("nuclearReactor")
         }
      );
      nanoWings = EMTInfusionRecipe.register(
         "Nanosuit Wings",
         EMTRandomHelper.getChargedItem(EMTItems.nanoWing, 10),
         4,
         EMTCraftingAspects.nanoWing,
         new ItemStack(EMTItems.thaumiumWing),
         new Object[]{
            IC2Parts.ing("carbonPlate"),
            IC2Parts.ing("carbonPlate"),
            IC2Parts.ing("carbonPlate"),
            IC2Parts.ing("nanoBodyarmor"),
            new ItemStack(ConfigItems.itemResource, 1, 1)
         }
      );
      quantumWings = EMTInfusionRecipe.register(
         "Quantum Wings",
         EMTRandomHelper.getChargedItem(EMTItems.quantumWing, 10),
         6,
         EMTCraftingAspects.quantumWing,
         new ItemStack(EMTItems.nanoWing, 1, 32767),
         new Object[]{
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("quantumBodyarmor"),
            new ItemStack(ConfigItems.itemResource, 1, 1)
         }
      );
      infusedQuantumArmor = EMTInfusionRecipe.register(
         "Infused Quantum Armor",
         EMTRandomHelper.getChargedItem(EMTItems.quantumArmor, 10),
         5,
         EMTCraftingAspects.quantumWing,
         IC2Parts.ing("quantumBodyarmor"),
         new Object[]{
            new ItemStack(Items.diamond),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("iridiumPlate"),
            IC2Parts.ing("iridiumPlate"),
            new ItemStack(EMTBlocks.shield),
            new ItemStack(EMTBlocks.shield)
         }
      );
      diamondOmnitool = EMTShapelessArcaneRecipe.register(
         "Diamond Omnitool",
         EMTRandomHelper.getChargedItem(EMTItems.diamondOmnitool, 10),
         EMTCraftingAspects.diamondOmnitoolCrafting,
         new Object[]{new ItemStack(EMTItems.diamondChainsaw, 1, 32767), IC2Parts.ing("diamondDrill")}
      );
      ironOmnitoolToDiamond = EMTArcaneRecipe.register(
         "Diamond Omnitool",
         new ItemStack(EMTItems.diamondOmnitool),
         EMTCraftingAspects.diamondOmnitoolCrafting,
         new Object[]{
            "XAX",
            "XBX",
            "XAX",
            'X',
            new ItemStack(Items.diamond),
            'A',
            IC2Parts.ing("advancedCircuit"),
            'B',
            new ItemStack(EMTItems.ironOmnitool, 1, 32767)
         }
      );
      christmasFocus = ThaumcraftApi.addArcaneCraftingRecipe(
         "Kris-tmas Focus",
         new ItemStack(EMTItems.christmasFocus),
         EMTCraftingAspects.christmasFocusCrafting,
         new Object[]{
            "XYX",
            "YZY",
            "XYX",
            'X',
            new ItemStack(Blocks.snow),
            'Y',
            new ItemStack(Blocks.pumpkin),
            'Z',
            new ItemStack(ConfigItems.itemFocusFrost)
         }
      );
      electricGoggles = EMTArcaneRecipe.register(
         "Electric Goggles",
         EMTRandomHelper.getChargedItem(EMTItems.electricGoggles, 10),
         EMTCraftingAspects.electricGogglesCrafting,
         new Object[]{
            " Y ",
            "AZA",
            "BXB",
            'Z',
            new ItemStack(ConfigItems.itemGoggles),
            'X',
            IC2Parts.ing("electronicCircuit"),
            'Y',
            new ItemStack(Items.diamond_helmet),
            'A',
            IC2Parts.ing("reBattery"),
            'B',
            Items.repeater
         }
      );
      electricGoggles2 = EMTArcaneRecipe.register(
         "Electric Goggles",
         EMTRandomHelper.getChargedItem(EMTItems.electricGoggles, 10),
         EMTCraftingAspects.electricGogglesCrafting,
         new Object[]{
            " Y ",
            "AZA",
            "BXB",
            'Z',
            new ItemStack(ConfigItems.itemGoggles),
            'X',
            IC2Parts.ing("electronicCircuit"),
            'Y',
            new ItemStack(Items.diamond_helmet),
            'A',
            IC2Parts.ing("chargedReBattery"),
            'B',
            Items.repeater
         }
      );
      shieldBlock = ThaumcraftApi.addArcaneCraftingRecipe(
         "Shield Block",
         new ItemStack(EMTBlocks.shield, 6),
         EMTCraftingAspects.shieldBlockCrafting,
         new Object[]{"XYX", "X X", "XYX", 'X', new ItemStack(Blocks.glass), 'Y', new ItemStack(Blocks.obsidian)}
      );
      tinyUranium = EMTShapelessArcaneRecipe.register(
         "Tiny Uranium",
         IC2Parts.ing("smallUran235", 7),
         EMTCraftingAspects.tinyUraniumCrafting,
         new Object[]{IC2Parts.ing("Uran238")}
      );
      compressedSolar = EMTArcaneRecipe.register(
         "Compressed Solars",
         new ItemStack(EMTBlocks.emtSolars, 1, 0),
         EMTCraftingAspects.compressedSolars,
         new Object[]{"XXX", "XXX", "XXX", 'X', IC2Parts.ing("solarPanel")}
      );
      doubleCompressedSolar = ThaumcraftApi.addArcaneCraftingRecipe(
         "Compressed Solars",
         new ItemStack(EMTBlocks.emtSolars, 1, 1),
         EMTCraftingAspects.compressedSolars,
         new Object[]{"XXX", "XXX", "XXX", 'X', new ItemStack(EMTBlocks.emtSolars, 1, 0)}
      );
      electricScribingTools = EMTArcaneRecipe.register(
         "Electric Scribing Tools",
         EMTRandomHelper.getChargedItem(EMTItems.electricScribingTools, 10),
         EMTCraftingAspects.electricScribingTools,
         new Object[]{
            "YXY",
            "XZX",
            "YXY",
            'Y',
            IC2Parts.ing("electronicCircuit"),
            'X',
            new ItemStack(ConfigItems.itemInkwell),
            'Z',
            IC2Parts.ing("energyCrystal")
         }
      );
      thaumiumWing = EMTArcaneRecipe.register(
         "Thaumium Reinforced Wings",
         new ItemStack(EMTItems.itemEMTItems, 1, 14),
         EMTCraftingAspects.thaumiumWing,
         new Object[]{
            "XYZ",
            "XYZ",
            "XYZ",
            'X',
            new ItemStack(EMTItems.itemEMTItems, 1, 13),
            'Y',
            new ItemStack(EMTItems.itemEMTItems, 1, 5),
            'Z',
            IC2Parts.ing("plateiron")
         }
      );
      thaumiumWings = ThaumcraftApi.addArcaneCraftingRecipe(
         "Thaumium Reinforced Wings",
         new ItemStack(EMTItems.thaumiumWing),
         EMTCraftingAspects.thaumiumWing,
         new Object[]{"XX", 'X', new ItemStack(EMTItems.itemEMTItems, 1, 14)}
      );
      ignisGenerator = ThaumcraftApi.addCrucibleRecipe(
         "Ignis Generator", new ItemStack(EMTBlocks.essentiaGens, 1, 1), new ItemStack(EMTBlocks.essentiaGens, 1, 0), EMTCraftingAspects.ignisGeneratorCrafting
      );
      auramGenerator = ThaumcraftApi.addCrucibleRecipe(
         "Auram Generator", new ItemStack(EMTBlocks.essentiaGens, 1, 2), new ItemStack(EMTBlocks.essentiaGens, 1, 0), EMTCraftingAspects.auramGeneratorCrafting
      );
      arborGenerator = ThaumcraftApi.addCrucibleRecipe(
         "Arbor Generator", new ItemStack(EMTBlocks.essentiaGens, 1, 3), new ItemStack(EMTBlocks.essentiaGens, 1, 0), EMTCraftingAspects.arborGeneratorCrafting
      );
      aerGenerator = ThaumcraftApi.addCrucibleRecipe(
         "Aer Generator", new ItemStack(EMTBlocks.essentiaGens, 1, 4), new ItemStack(EMTBlocks.essentiaGens, 1, 0), EMTCraftingAspects.aerGenerator
      );
      waterSolar = ThaumcraftApi.addCrucibleRecipe(
         "Water Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 3), new ItemStack(EMTBlocks.emtSolars, 1, 0), EMTCraftingAspects.waterSolars
      );
      doubleWaterSolar = ThaumcraftApi.addCrucibleRecipe(
         "Water Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 4), new ItemStack(EMTBlocks.emtSolars, 1, 1), EMTCraftingAspects.waterSolars
      );
      tripleWaterSolar = ThaumcraftApi.addCrucibleRecipe(
         "Water Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 5), new ItemStack(EMTBlocks.emtSolars, 1, 2), EMTCraftingAspects.waterSolars
      );
      darkSolar = ThaumcraftApi.addCrucibleRecipe(
         "Entropy Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 6), new ItemStack(EMTBlocks.emtSolars, 1, 0), EMTCraftingAspects.darkSolars
      );
      doubleDarkSolar = ThaumcraftApi.addCrucibleRecipe(
         "Entropy Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 7), new ItemStack(EMTBlocks.emtSolars, 1, 1), EMTCraftingAspects.darkSolars
      );
      tripleDarkSolar = ThaumcraftApi.addCrucibleRecipe(
         "Entropy Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 8), new ItemStack(EMTBlocks.emtSolars, 1, 2), EMTCraftingAspects.darkSolars
      );
      orderSolar = ThaumcraftApi.addCrucibleRecipe(
         "Order Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 9), new ItemStack(EMTBlocks.emtSolars, 1, 0), EMTCraftingAspects.orderSolars
      );
      doubleOrderSolar = ThaumcraftApi.addCrucibleRecipe(
         "Order Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 10), new ItemStack(EMTBlocks.emtSolars, 1, 1), EMTCraftingAspects.orderSolars
      );
      tripleOrderSolar = ThaumcraftApi.addCrucibleRecipe(
         "Order Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 11), new ItemStack(EMTBlocks.emtSolars, 1, 2), EMTCraftingAspects.orderSolars
      );
      fireSolar = ThaumcraftApi.addCrucibleRecipe(
         "Fire Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 12), new ItemStack(EMTBlocks.emtSolars, 1, 0), EMTCraftingAspects.fireSolars
      );
      doubleFireSolar = ThaumcraftApi.addCrucibleRecipe(
         "Fire Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 13), new ItemStack(EMTBlocks.emtSolars, 1, 1), EMTCraftingAspects.fireSolars
      );
      tripleFireSolar = ThaumcraftApi.addCrucibleRecipe(
         "Fire Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 14), new ItemStack(EMTBlocks.emtSolars, 1, 2), EMTCraftingAspects.fireSolars
      );
      airSolar = ThaumcraftApi.addCrucibleRecipe(
         "Air Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars, 1, 15), new ItemStack(EMTBlocks.emtSolars, 1, 0), EMTCraftingAspects.airSolars
      );
      doubleAirSolar = ThaumcraftApi.addCrucibleRecipe(
         "Air Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars2, 1, 0), new ItemStack(EMTBlocks.emtSolars, 1, 1), EMTCraftingAspects.airSolars
      );
      tripleAirSolar = ThaumcraftApi.addCrucibleRecipe(
         "Air Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars2, 1, 1), new ItemStack(EMTBlocks.emtSolars, 1, 2), EMTCraftingAspects.airSolars
      );
      earthSolar = ThaumcraftApi.addCrucibleRecipe(
         "Earth Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars2, 1, 2), new ItemStack(EMTBlocks.emtSolars, 1, 0), EMTCraftingAspects.earthSolars
      );
      doubleEarthSolar = ThaumcraftApi.addCrucibleRecipe(
         "Earth Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars2, 1, 3), new ItemStack(EMTBlocks.emtSolars, 1, 1), EMTCraftingAspects.earthSolars
      );
      tripleEarthSolar = ThaumcraftApi.addCrucibleRecipe(
         "Earth Infused Solar Panels", new ItemStack(EMTBlocks.emtSolars2, 1, 4), new ItemStack(EMTBlocks.emtSolars, 1, 2), EMTCraftingAspects.earthSolars
      );
      ItemStack itemStack = new ItemStack(ConfigItems.itemJarNode);
      ((ItemJarNode)itemStack.getItem()).setAspects(itemStack, new AspectList());
      portableNode = ThaumcraftApi.addCrucibleRecipe("Portable Node", new ItemStack(EMTBlocks.portableNode), itemStack, EMTCraftingAspects.portableNode);
      uuMCrystal = EMTCrucibleRecipe.register(
         "UU-Matter Infusion", new ItemStack(EMTItems.itemEMTItems, 1, 15), IC2Parts.ing("uuMatterCell"), EMTCraftingAspects.uuMatterCrystal
      );
      // IC2 machine recipes (macerator / ore washing / centrifuge / metal former) are added to the
      // 1.20.1 IndustrialCraft 2 port by emtbridge, see IC2MachineRecipes there.
      thaumiumPlate = new EMTDisplayShapedRecipe(
         new ItemStack(EMTItems.itemEMTItems, 1, 5),
         new Object[]{
            "X",
            "Y",
            "Z",
            'Y',
            new ItemStack(ConfigItems.itemResource, 1, 2),
            'X',
            IC2Parts.ing("ForgeHammer"),
            'Z',
            new ItemStack(Blocks.obsidian)
         }
      );
   }
}
