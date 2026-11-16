package emt.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.tile.TileEntityEtherealMacerator;
import emt.tile.TileEntityIndustrialWandRecharge;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMachines extends BlockBaseContainer {
   public BlockMachines(String name) {
      super(name, Material.iron, soundTypeMetal, 2, 4.0F);
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister ir) {
      super.registerBlockIcons(ir);
      this.blockIcon = ir.registerIcon("emt:machines/top");
      this.iconSets[0].top = ir.registerIcon("emt:machines/top");
      this.iconSets[1].top = ir.registerIcon("emt:machines/etherealmaceratortop");
      this.iconSets[0].bottom = ir.registerIcon("emt:machines/side");
      this.iconSets[1].bottom = ir.registerIcon("emt:machines/etherealmacerator");
      this.iconSets[0].side = ir.registerIcon("emt:machines/side");
      this.iconSets[1].side = ir.registerIcon("emt:machines/etherealmacerator");
      this.iconSets[0].frontOff = ir.registerIcon("emt:machines/wandcharger");
      this.iconSets[1].frontOff = ir.registerIcon("emt:machines/etherealmaceratorfront");
      this.iconSets[0].frontOn = ir.registerIcon("emt:machines/wandcharger");
      this.iconSets[1].frontOn = ir.registerIcon("emt:machines/etherealmaceratorfrontactive");
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item id, CreativeTabs tab, List list) {
      list.add(new ItemStack(id, 1, 0));
      list.add(new ItemStack(id, 1, 1));
   }

   public TileEntity createTileEntity(World world, int meta) {
      if (meta == 0) {
         return new TileEntityIndustrialWandRecharge();
      } else {
         return (TileEntity)(meta == 1 ? new TileEntityEtherealMacerator() : super.createTileEntity(world, meta));
      }
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
      if (!world.isRemote && world.getTileEntity(x, y, z) instanceof TileEntityIndustrialWandRecharge) {
         player.openGui(EMT.instance, 0, world, x, y, z);
      } else if (!world.isRemote && world.getTileEntity(x, y, z) instanceof TileEntityEtherealMacerator) {
         player.openGui(EMT.instance, 1, world, x, y, z);
      }

      return true;
   }

   public Item getItemDropped(int meta, Random random, int fortune) {
      return Item.getItemFromBlock(this); // EMT 1.2.6.2 dropped an IC2 machine casing
   }

   public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
      this.dropItems(world, x, y, z);
      world.removeTileEntity(x, y, z);
      super.breakBlock(world, x, y, z, block, meta);
   }

   private void dropItems(World world, int x, int y, int z) {
      Random rand = world.rand;
      TileEntity tileEntity = world.getTileEntity(x, y, z);
      if (tileEntity instanceof IInventory) {
         IInventory inventory = (IInventory)tileEntity;

         for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);
            if (item != null && item.stackSize > 0) {
               float rx = rand.nextFloat() * 0.8F + 0.1F;
               float ry = rand.nextFloat() * 0.8F + 0.1F;
               float rz = rand.nextFloat() * 0.8F + 0.1F;
               EntityItem entityItem = new EntityItem(
                  world,
                  (double)((float)x + rx),
                  (double)((float)y + ry),
                  (double)((float)z + rz),
                  new ItemStack(item.getItem(), item.stackSize, item.getItemDamage())
               );
               if (item.hasTagCompound()) {
                  entityItem.getEntityItem().setTagCompound((NBTTagCompound)item.getTagCompound().copy());
               }

               float factor = 0.05F;
               entityItem.motionX = rand.nextGaussian() * (double)factor;
               entityItem.motionY = rand.nextGaussian() * (double)factor + 0.2F;
               entityItem.motionZ = rand.nextGaussian() * (double)factor;
               world.spawnEntityInWorld(entityItem);
               item.stackSize = 0;
            }
         }
      }
   }
}
