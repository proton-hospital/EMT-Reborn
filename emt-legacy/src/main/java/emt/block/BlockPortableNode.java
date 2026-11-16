package emt.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.tile.TileEntityPortableNode;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockAiry;

public class BlockPortableNode extends BlockAiry {
   public IIcon texture;

   public BlockPortableNode(String name) {
      this.setCreativeTab(EMT.TAB);
      this.setBlockName("EMT." + name);
      this.setBlockUnbreakable();
   }

   public TileEntity createTileEntity(World world, int meta) {
      return new TileEntityPortableNode();
   }

   public int quantityDropped(Random par1Random) {
      return 0;
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

   public boolean canEntityDestroy(World world, int x, int y, int z, Entity entity) {
      return false;
   }

   public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister ri) {
      this.texture = ri.registerIcon("emt:portablenode");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIcon(int side, int meta) {
      return this.texture;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
      if (!world.isRemote && player.isSneaking() && player.getHeldItem() == null) {
         world.setBlockToAir(x, y, z);
         world.removeTileEntity(x, y, z);
      }

      return true;
   }

   public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
      return null;
   }
}
