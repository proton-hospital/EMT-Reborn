package emt.init;

import cpw.mods.fml.common.registry.GameRegistry;
import emt.block.BlockElectricCloud;
import emt.block.BlockEssentiaGenerators;
import emt.block.BlockMachines;
import emt.block.BlockPortableNode;
import emt.block.BlockShield;
import emt.block.BlockSolars;
import emt.item.block.ItemBlockElectricCloud;
import emt.item.block.ItemBlockEssentiaGenerators;
import emt.item.block.ItemBlockMachines;
import emt.item.block.ItemBlockSolars;
import net.minecraft.block.Block;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class EMTBlocks {
   public static Block portableNode;
   public static Block essentiaGens;
   public static Block shield;
   public static Block emtSolars;
   public static Block emtMachines;
   public static Block emtSolars2;
   public static Block electricCloud;

   public static void registerBlocks() {
      portableNode = new BlockPortableNode("portablenode");
      GameRegistry.registerBlock(portableNode, "PortableNode");
      shield = new BlockShield("shield");
      GameRegistry.registerBlock(shield, "ShieldBlock");
      emtSolars = new BlockSolars("solar", 16, 0);
      GameRegistry.registerBlock(emtSolars, ItemBlockSolars.class, "EMTSolars");
      emtSolars2 = new BlockSolars("solar2", 5, 1);
      GameRegistry.registerBlock(emtSolars2, ItemBlockSolars.class, "EMTSolars2");
      emtMachines = new BlockMachines("machine");
      GameRegistry.registerBlock(emtMachines, ItemBlockMachines.class, "EMTMachines");
      essentiaGens = new BlockEssentiaGenerators("essentia");
      GameRegistry.registerBlock(essentiaGens, ItemBlockEssentiaGenerators.class, "EssentiaGenerators");
      electricCloud = new BlockElectricCloud("electricCloud");
      GameRegistry.registerBlock(electricCloud, ItemBlockElectricCloud.class, "electricCloud");
   }

   public static void addAspects() {
      // EMT 1.2.6.2 gave IC2 blocks Thaumcraft aspects here. IC2 now lives in 1.20.1, its aspects
      // would have to be registered through tc-modern-lib instead.

   }
}
