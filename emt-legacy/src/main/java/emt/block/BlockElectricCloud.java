package emt.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.tile.TileElectricCloud;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXSparkle;

public class BlockElectricCloud extends BlockBase {
   public IIcon icon;

   public BlockElectricCloud(String unlocName) {
      super(unlocName, Material.cloth, soundTypeCloth, 0.1F);
      this.setBlockBounds(0.3F, 0.3F, 0.3F, 0.7F, 0.7F, 0.7F);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(World world, int x, int y, int z, Random rnd) {
      FXSparkle ef = new FXSparkle(
         world,
         (double)((float)x + 0.5F),
         (double)((float)y + 0.5F),
         (double)((float)z + 0.5F),
         (double)((float)x + 0.5F + (rnd.nextFloat() - rnd.nextFloat()) / 3.0F),
         (double)((float)y + 0.5F + (rnd.nextFloat() - rnd.nextFloat()) / 3.0F),
         (double)((float)z + 0.5F + (rnd.nextFloat() - rnd.nextFloat()) / 3.0F),
         1.0F,
         6,
         3
      );
      ef.setGravity(0.05F);
      ParticleEngine.instance.addEffect(world, ef);
   }

   public TileEntity createTileEntity(World world, int metadata) {
      return new TileElectricCloud();
   }

   public String getUnlocalizedName() {
      return "tile.EMT." + super.getUnlocalizedName().substring(15);
   }

   public void registerBlockIcons(IIconRegister ir) {
      // EMT Reborn: the original was invisible (Thaumcraft particles only, which MFCL does not show);
      // drawn as crossed sprites of the cloud texture instead, which is also the item icon under MFCL
      this.icon = ir.registerIcon("emt:electric_cloud");
   }

   public IIcon getIcon(int side, int meta) {
      return this.icon;
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
      return null;
   }

   public int getLightValue() {
      return 15;
   }

   public boolean isAir(IBlockAccess world, int x, int y, int z) {
      return false;
   }

   public boolean hasTileEntity() {
      return true;
   }

   public boolean hasTileEntity(int metadata) {
      return true;
   }

   public int getRenderType() {
      return 1;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public boolean isOpaqueCube() {
      return false;
   }
}
