package emt.block;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.tile.TileEntityEMT;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBaseContainer extends BlockContainer {
   @SideOnly(Side.CLIENT)
   BlockBaseContainer.IconSet[] iconSets;
   public int instance;
   public int countOfMetas;

   public BlockBaseContainer(String unlocName, Material material, SoundType soundType, float hardness) {
      this(unlocName, material, soundType, 1, hardness);
   }

   public BlockBaseContainer(String unlocName, Material material, SoundType soundType, int countOfMetas, float hardness) {
      this(unlocName, material, soundType, countOfMetas, 0, hardness);
   }

   public BlockBaseContainer(String unlocName, Material material, SoundType soundType, int countOfMetas, int curInstance, float hardness) {
      super(material);
      this.setBlockName("EMT." + unlocName);
      this.setCreativeTab(EMT.TAB);
      this.setStepSound(soundType);
      this.setHardness(hardness);
      this.countOfMetas = countOfMetas;
      this.instance = curInstance;
      if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
         this.iconSets = new BlockBaseContainer.IconSet[countOfMetas];

         for (int meta = 0; meta < countOfMetas; meta++) {
            this.iconSets[meta] = new BlockBaseContainer.IconSet();
         }
      }
   }

   public BlockBaseContainer.IconSet[] getIconSets() {
      return this.iconSets;
   }

   public TileEntity createNewTileEntity(World world, int meta) {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
      TileEntityEMT tile = (TileEntityEMT)access.getTileEntity(x, y, z);
      int meta = access.getBlockMetadata(x, y, z);
      BlockBaseContainer.IconSet set = this.iconSets[meta];
      if (side == 0) {
         return set.bottom;
      } else if (side == 1) {
         return set.top;
      } else if (side != tile.facing) {
         return set.side;
      } else if (tile.isOn) {
         if (set.frontOn != null) {
            return set.frontOn;
         } else {
            return set.frontOff != null ? set.frontOff : set.side;
         }
      } else {
         return set.frontOff != null ? set.frontOff : set.side;
      }
   }

   public IIcon getIcon(int side, int meta) {
      BlockBaseContainer.IconSet set = this.iconSets[meta];
      if (side < 1) {
         return set.bottom;
      } else if (side == 1) {
         return set.top;
      } else {
         return side == 3 ? set.frontOff : set.side;
      }
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
      int facing = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
      TileEntityEMT tile = (TileEntityEMT)world.getTileEntity(x, y, z);
      switch (facing) {
         case 0:
            tile.facing = 2;
            break;
         case 1:
            tile.facing = 5;
            break;
         case 2:
            tile.facing = 3;
            break;
         case 3:
            tile.facing = 4;
      }
   }

   public int damageDropped(int meta) {
      return meta;
   }

   @SideOnly(Side.CLIENT)
   public static class IconSet {
      public IIcon top;
      public IIcon bottom;
      public IIcon side;
      public IIcon frontOff;
      public IIcon frontOn;
   }
}
