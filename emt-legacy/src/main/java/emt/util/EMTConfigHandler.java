package emt.util;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class EMTConfigHandler {
   public static Configuration config;
   public static final String RANDOM = "Random Configs";
   public static final String RESEARCH = "Research";
   public static final String VALUES = "Numeric Values";
   public static final String OUTPUTS = "Essentia Generator Outputs";
   public static int etherealProcessorBaseSpeed;
   public static int etherealProcessorBonus;
   public static int chargeFocusProduction;
   public static int armorBaubleProduction;
   public static int inventoryBaubleProdution;
   public static int wandChargeFocusCost;
   public static double wandChargerConsumption;
   public static double compressedSolarOutput;
   public static double doubleCompressedSolarOutput;
   public static double nanoBootsSpeed;
   public static double quantumBootsSpeed;
   public static double nanoBootsJump;
   public static double quantumBootsJump;
   public static double tripleCompressedSolarOutput;
   public static double fireOutput;
   public static double waterOutput;
   public static double airOutput;
   public static double earthOutput;
   public static double orderOutput;
   public static double entropyOutput;
   public static double outputCap;
   public static int generatorMaxTier = 1;
   public static boolean impactOfRain;
   public static boolean capesOn;
   public static boolean toolsInBore;
   public static boolean nightVisionOff;
   public static boolean enchanting;
   public static boolean smoke;
   public static boolean oneRingSpawn;
   public static boolean removeAmberAndCinnabarMacerating;
   public static int chanceOneRing;
   public static int chanceTaintedMjolnir;
   public static boolean thaumiumDrillResearch;
   public static boolean thaumiumChainsawResearch;
   public static boolean thaumiumOmnitoolResearch;
   public static boolean rockbreakerDrillResearch;
   public static boolean streamChainsawResearch;
   public static boolean electricGooglesResearch;
   public static boolean nanoGooglesResearch;
   public static boolean quantumGooglesResearch;
   public static boolean ironOmnitoolResearch;
   public static boolean diamondOmnitoolResearch;
   public static boolean explosionFocusResearch;
   public static boolean christmasFocusResearch;
   public static boolean energyBallFocusResearch;
   public static boolean shieldFocusResearch;
   public static boolean shieldBlockResearch;
   public static boolean potentiaGeneratorResearch;
   public static boolean ignisGeneratorResearch;
   public static boolean auramGeneratorResearch;
   public static boolean arborGeneratorResearch;
   public static boolean wandChargingSationResearch;
   public static boolean tinyUraniumResearch;
   public static boolean thorHammerResearch;
   public static boolean superchargedThorHammerResearch;
   public static boolean compressedSolarsResearch;
   public static boolean solarHelmetResearch;
   public static boolean electricBootsResearch;
   public static boolean nanoBootsResearch;
   public static boolean quantumBootsResearch;
   public static boolean electricScribingToolsResearch;
   public static boolean etherealProcessorResearch;
   public static boolean waterSolarsResearch;
   public static boolean darkSolarsResearch;
   public static boolean orderSolarsResearch;
   public static boolean fireSolarsResearch;
   public static boolean airSolarsResearch;
   public static boolean earthSolarsResearch;
   public static boolean saxumGenResearch;
   public static boolean uuMInfusionResearch;
   public static boolean portableNodeResarch;
   public static boolean electricHoeGrowthResearch;
   public static boolean chargeFocusResearch;
   public static boolean wandChargeFocusResearch;
   public static boolean inventoryChargingRing;
   public static boolean armorChargingRing;
   public static boolean thaumiumWingResearch;
   public static boolean nanoWingResearch;
   public static boolean quantumWingsResearch;
   public static boolean infusedQuantumChestplate;
   public static boolean aerGeneratorResearch;

   public static void init(File file) {
      config = new Configuration(file);
      syncConfig();
   }

   public static void syncConfig() {
      config.addCustomCategoryComment("Numeric Values", "The only way to change some numbers that my machines uses.");
      config.addCustomCategoryComment(
         "Research",
         "The only way to disable some researches. Be careful, if you disable some researches, all researches linked to it will be removed too. Really, don't touch that unless you know exactly what you are doing."
      );
      config.addCustomCategoryComment("Essentia Generator Outputs", "Change outputs linked to different Essentias here");
      config.addCustomCategoryComment("Random Configs", "Options that don't fit into the other categories.");
      capesOn = config.get(
            "Random Configs",
            "Enable capes",
            true,
            "This config option is to enable or disable capes for people who have them. Seriously, you shouldn't touch that unless you have another cape and it conflicts with it."
         )
         .getBoolean(capesOn);
      toolsInBore = config.get(
            "Random Configs",
            "Tools for Arcane Bore",
            false,
            "This will augment the durablilty of the tools, and will also decrease the EU cost to 1. The tools should have the same number of uses than before."
         )
         .getBoolean(toolsInBore);
      nightVisionOff = config.get(
            "Random Configs",
            "Disable nightvision helmets",
            false,
            "This was added because of mods making you totally blind if using nightvision. For example, enable that when in the Deep Dark."
         )
         .getBoolean(nightVisionOff);
      enchanting = config.get("Random Configs", "Enable enchanting tools", false, "Warning: the enchantability is low.").getBoolean(enchanting);
      smoke = config.get("Random Configs", "Disable smoke effect for boots", false, "This effect only appears when the player isn't on the ground")
         .getBoolean(smoke);
      oneRingSpawn = config.get("Random Configs", "Disable One Ring in dungeon chests", false, "There is no other way to get this item.")
         .getBoolean(oneRingSpawn);
      chanceTaintedMjolnir = config.get(
            "Random Configs", "Tainted Mjolnir spawning change", 25, "If you have a lot of mods adding dungeon loot, you should definetely increase this"
         )
         .getInt();
      chanceOneRing = config.get(
            "Random Configs", "One Ring spawning chance", 15, "If you have a lot of mods adding dungeon loot, you should definetely increase this"
         )
         .getInt();
      removeAmberAndCinnabarMacerating = config.get(
            "Random Configs",
            "Clear Amber/Cinnabar Macerating",
            true,
            "This is here because of conflicts between mods that add the same input and different outputs"
         )
         .getBoolean(removeAmberAndCinnabarMacerating);
      impactOfRain = config.get("Random Configs", "Impact of rain", true, "The impact of rain on all wings").getBoolean(impactOfRain);
      airOutput = config.get("Essentia Generator Outputs", "Aer Output", 15000).getDouble(airOutput);
      waterOutput = config.get("Essentia Generator Outputs", "Aqua Output", 5000).getDouble(waterOutput);
      fireOutput = config.get("Essentia Generator Outputs", "Ignis Output", 20000).getDouble(fireOutput);
      orderOutput = config.get("Essentia Generator Outputs", "Ordo Output", 16000).getDouble(orderOutput);
      entropyOutput = config.get("Essentia Generator Outputs", "Perditio Output", 10000).getDouble(entropyOutput);
      earthOutput = config.get("Essentia Generator Outputs", "Terra Output", 2000).getDouble(earthOutput);
      outputCap = config.get("Essentia Generator Outputs", "Output Cap", -1).getDouble(outputCap);
      generatorMaxTier = config.get(
            "Essentia Generator Outputs",
            "Max voltage tier",
            1,
            "Highest IndustrialCraft 2 voltage the essentia generators send: 1 = LV (32 EU/t, safe for BatBox), 2 = MV (128, MFE), 3 = HV (512, MFSU). Extra energy waits in the generator."
         )
         .getInt(generatorMaxTier);
      wandChargerConsumption = config.get(
            "Numeric Values",
            "Wand Charging Station Consumption",
            50000,
            "This is the number you have to modify if you want to make the Industrial Wand Charging Station use more or less EU to charge 1 unit of Vis on your wand."
         )
         .getDouble(wandChargerConsumption);
      compressedSolarOutput = config.get(
            "Numeric Values",
            "Compressed Solar Panel Output",
            10,
            "This is the number you have to modify if you want to make the Compressed Solar Panel yield more or less EU per tick. Really, you shouldn't touch that, since it's pretty balanced as it is."
         )
         .getDouble(compressedSolarOutput);
      doubleCompressedSolarOutput = config.get(
            "Numeric Values",
            "Double Compressed Solar Panel Output",
            100,
            "This is the number you have to modify if you want to make the Double Compressed Solar Panel yield more or less EU per tick. Really, you shouldn't touch that, since it's pretty balanced as it is."
         )
         .getDouble(doubleCompressedSolarOutput);
      tripleCompressedSolarOutput = config.get(
            "Numeric Values",
            "Triple Compressed Solar Panel Output",
            1000,
            "This is the number you have to modify if you want to make the Triple Compressed Solar Panel yield more or less EU per tick. Really, you shouldn't touch that, since it's pretty balanced as it is."
         )
         .getDouble(tripleCompressedSolarOutput);
      nanoBootsSpeed = config.get("Numeric Values", "Nano Boots of the Traveller Speed", 0.25, "Watch out, that goes up REALLY quickly.")
         .getDouble(nanoBootsSpeed);
      quantumBootsSpeed = config.get("Numeric Values", "Quantum Boots of the Traveller Speed", 0.5, "Watch out, that goes up REALLY quickly.")
         .getDouble(quantumBootsSpeed);
      nanoBootsJump = config.get("Numeric Values", "Nano Boots of the Traveller jump", 0.6, "Watch out, that goes up REALLY quickly.").getDouble(nanoBootsJump);
      quantumBootsJump = config.get("Numeric Values", "Quantum Boots of the Traveller jump", 0.9, "Watch out, that goes up REALLY quickly.")
         .getDouble(nanoBootsJump);
      etherealProcessorBaseSpeed = config.get("Numeric Values", "Etheral Processor speed", 400, "Default is 400, the double of a regular furnace").getInt();
      etherealProcessorBonus = config.get(
            "Numeric Values",
            "Ethereal Processor Bonus",
            10,
            "This number is the chance of getting a Thaumium Ingot as a bonus when processing an item in the machine. It is 1 out of the number you will enter. The default is 1/10 chance."
         )
         .getInt();
      chargeFocusProduction = config.get("Numeric Values", "Wand Focus: Charge production", 64, "Default is 64").getInt();
      armorBaubleProduction = config.get("Numeric Values", "Armor Charging Ring production", 32, "Default is 32").getInt();
      inventoryBaubleProdution = config.get("Numeric Values", "Inventory Charging Ring production", 32, "Default is 32").getInt();
      wandChargeFocusCost = config.get("Numeric Values", "Wand Focus: Wand Charging Cost", 40000, "Default is 40000").getInt();
      thaumiumDrillResearch = config.get("Research", "Thaumium Drill", false).getBoolean(thaumiumDrillResearch);
      thaumiumChainsawResearch = config.get("Research", "Thaumium Chainsaw", false).getBoolean(thaumiumChainsawResearch);
      thaumiumOmnitoolResearch = config.get("Research", "Thaumium Omnitool", false).getBoolean(thaumiumOmnitoolResearch);
      rockbreakerDrillResearch = config.get("Research", "Drill of the Rockbreaker", false).getBoolean(rockbreakerDrillResearch);
      streamChainsawResearch = config.get("Research", "Chainsaw of the Stream", false).getBoolean(streamChainsawResearch);
      electricGooglesResearch = config.get("Research", "Electric Googles of Revealing", false).getBoolean(electricGooglesResearch);
      nanoGooglesResearch = config.get("Research", "Nanosuit Googles of Revealing", false).getBoolean(nanoGooglesResearch);
      quantumGooglesResearch = config.get("Research", "Quantum Googles of Revealing", false).getBoolean(quantumGooglesResearch);
      ironOmnitoolResearch = config.get("Research", "Omnitool", false).getBoolean(ironOmnitoolResearch);
      diamondOmnitoolResearch = config.get("Research", "Diamond Omnitool", false).getBoolean(diamondOmnitoolResearch);
      explosionFocusResearch = config.get("Research", "Explosion Focus", false).getBoolean(explosionFocusResearch);
      christmasFocusResearch = config.get("Research", "Kris-tmas Focus", false).getBoolean(christmasFocusResearch);
      energyBallFocusResearch = config.get("Research", "EnergyBall Focus", false).getBoolean(energyBallFocusResearch);
      shieldFocusResearch = config.get("Research", "Shield Focus", false).getBoolean(shieldFocusResearch);
      shieldBlockResearch = config.get("Research", "Shield Block", false).getBoolean(shieldBlockResearch);
      potentiaGeneratorResearch = config.get("Research", "Potentia Generator", false).getBoolean(potentiaGeneratorResearch);
      ignisGeneratorResearch = config.get("Research", "Ignis Generator", false).getBoolean(ignisGeneratorResearch);
      auramGeneratorResearch = config.get("Research", "Auram Generator", false).getBoolean(auramGeneratorResearch);
      arborGeneratorResearch = config.get("Research", "Arbor Generator", false).getBoolean(arborGeneratorResearch);
      wandChargingSationResearch = config.get("Research", "Industrial Wand Charging Station", false).getBoolean(wandChargingSationResearch);
      tinyUraniumResearch = config.get("Research", "Tiny Piles of Uranium", false).getBoolean(tinyUraniumResearch);
      thorHammerResearch = config.get("Research", "Mjolnir", false).getBoolean(thorHammerResearch);
      superchargedThorHammerResearch = config.get("Research", "Supercharged Mjolnir", false).getBoolean(superchargedThorHammerResearch);
      compressedSolarsResearch = config.get("Research", "Compressed Solars", false).getBoolean(compressedSolarsResearch);
      solarHelmetResearch = config.get("Research", "Solar Helmet of Revealing", false).getBoolean(solarHelmetResearch);
      electricBootsResearch = config.get("Research", "Electric Boots of the Traveller", false).getBoolean(electricBootsResearch);
      nanoBootsResearch = config.get("Research", "Nano Boots of the Traveller", false).getBoolean(nanoBootsResearch);
      quantumBootsResearch = config.get("Research", "Quantum Boots of the Traveller", false).getBoolean(quantumBootsResearch);
      electricScribingToolsResearch = config.get("Research", "Electric Scribing Tools", false).getBoolean(electricScribingToolsResearch);
      etherealProcessorResearch = config.get("Research", "Ethereal Processor", false).getBoolean(etherealProcessorResearch);
      waterSolarsResearch = config.get("Research", "Water Solar Panels", false).getBoolean(waterSolarsResearch);
      darkSolarsResearch = config.get("Research", "Entropy Solar Panels", false).getBoolean(darkSolarsResearch);
      orderSolarsResearch = config.get("Research", "Order Solar Panels", false).getBoolean(orderSolarsResearch);
      fireSolarsResearch = config.get("Research", "Fire Solar Panels", false).getBoolean(fireSolarsResearch);
      airSolarsResearch = config.get("Research", "Air Solar Panels", false).getBoolean(airSolarsResearch);
      earthSolarsResearch = config.get("Research", "Earth Solar Panels", false).getBoolean(earthSolarsResearch);
      saxumGenResearch = config.get("Research", "Saxum Generator", false).getBoolean(saxumGenResearch);
      uuMInfusionResearch = config.get("Research", "UU-Mater Infusion", false).getBoolean(uuMInfusionResearch);
      portableNodeResarch = config.get("Research", "Portable Node", false).getBoolean(portableNodeResarch);
      electricHoeGrowthResearch = config.get("Research", "Electric Hoe of Growth", false).getBoolean(electricHoeGrowthResearch);
      chargeFocusResearch = config.get("Research", "Wand Focus: Charging", false).getBoolean(chargeFocusResearch);
      wandChargeFocusResearch = config.get("Research", "Wand Focus: Wand Charging", false).getBoolean(wandChargeFocusResearch);
      inventoryChargingRing = config.get("Research", "Inventory Charging Ring", false).getBoolean(inventoryChargingRing);
      armorChargingRing = config.get("Research", "Armor Charging Ring", false).getBoolean(armorChargingRing);
      thaumiumWingResearch = config.get("Research", "Thaumium Reinforced Wings", false).getBoolean(thaumiumWingResearch);
      nanoWingResearch = config.get("Research", "Nanosuit Wings", false).getBoolean(nanoWingResearch);
      quantumWingsResearch = config.get("Research", "Quantum Wings", false).getBoolean(quantumWingsResearch);
      infusedQuantumChestplate = config.get("Research", "Infused Quantum Chestplate", false).getBoolean(infusedQuantumChestplate);
      aerGeneratorResearch = config.get("Research", "Aer Generator", false).getBoolean(aerGeneratorResearch);
      if (config.hasChanged()) {
         config.save();
      }
   }
}
