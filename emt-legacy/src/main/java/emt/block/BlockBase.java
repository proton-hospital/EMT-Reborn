package emt.block;

import emt.EMT;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;

public abstract class BlockBase extends Block {
   public final String name;

   public BlockBase(String unlocName, Material material, String textureName, SoundType soundType, float hardness) {
      super(material);
      this.name = unlocName;
      this.setBlockName("EMT.block." + unlocName);
      if (textureName != null) {
         this.setBlockTextureName("emt:" + textureName);
      }

      this.setCreativeTab(EMT.TAB);
      this.setStepSound(soundType);
      this.setHardness(hardness);
   }

   public BlockBase(String unlocName, Material material, SoundType soundType, float hardness) {
      this(unlocName, material, null, soundType, hardness);
   }
}
