package emt.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockShield extends Block {
   public BlockShield(String name) {
      super(Material.rock);
      this.setBlockName("EMT." + name);
      this.setCreativeTab(EMT.TAB);
      this.setHardness(1.0F);
      this.setResistance(1.0E37F);
   }

   public int quantityDropped(Random par1Random) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public int getRenderBlockPass() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister iconRegister) {
      this.blockIcon = iconRegister.registerIcon("emt:shield");
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   protected boolean canSilkHarvest() {
      return false;
   }

   public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
      return false;
   }

   public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
      return false;
   }

   public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
      return false;
   }

   public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
   }
}
