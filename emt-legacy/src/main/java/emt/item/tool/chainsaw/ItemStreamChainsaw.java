package emt.item.tool.chainsaw;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import ic2.api.item.ElectricItem;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBubble;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;

public class ItemStreamChainsaw extends ItemThaumiumChainsaw {
   boolean alternateServer;
   boolean alternateClient;
   public static ArrayList oreDictLogs = new ArrayList();

   public ItemStreamChainsaw() {
      this.efficiencyOnProperMaterial = 25.0F;
      this.alternateServer = false;
      this.alternateClient = false;
      this.setCreativeTab(EMT.TAB);
      this.maxStackSize = 1;
      this.setMaxDamage(27);
      this.maxCharge = 900000;
      this.cost = 400;
      this.hitCost = 500;
      this.tier = 3;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:tools/chainsaw_stream");
   }

   public EnumAction getItemUseAction(ItemStack itemstack) {
      return EnumAction.bow;
   }

   @Override
   public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
      Block bi = world.getBlock(x, y, z);
      if (!player.isSneaking() && Utils.isWoodLog(world, x, y, z) && ElectricItem.manager.getCharge(itemstack) > (double)this.cost) {
         if (!world.isRemote) {
            if (BlockUtils.breakFurthestBlock(world, x, y, z, bi, player)) {
               world.playSoundEffect((double)x, (double)y, (double)z, "thaumcraft:bubble", 0.15F, 1.0F);
               ElectricItem.manager.use(itemstack, (double)this.cost, player);
               this.alternateServer = !this.alternateServer;
            }
         } else {
            player.swingItem();
            ElectricItem.manager.use(itemstack, (double)this.cost, player);
            this.alternateClient = !this.alternateClient;
         }
      }

      return super.onItemUse(itemstack, player, world, x, y, z, par7, par8, par9, par10);
   }

   @Override
   public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
      World world = player.worldObj;
      Block bi = world.getBlock(x, y, z);
      if (!player.isSneaking() && Utils.isWoodLog(world, x, y, z)) {
         if (!world.isRemote) {
            BlockUtils.breakFurthestBlock(world, x, y, z, bi, player);
            PacketHandler.INSTANCE
               .sendToAllAround(
                  new PacketFXBlockBubble(x, y, z, new Color(0.33F, 0.33F, 1.0F).getRGB()),
                  new TargetPoint(world.provider.dimensionId, (double)x, (double)y, (double)z, 32.0)
               );
            world.playSoundEffect((double)x, (double)y, (double)z, "thaumcraft:bubble", 0.15F, 1.0F);
         }

         ElectricItem.manager.use(itemstack, (double)this.cost, player);
         return true;
      } else {
         return super.onBlockStartBreak(itemstack, x, y, z, player);
      }
   }

   @Override
   public double getTransferLimit(ItemStack itemStack) {
      return 900.0;
   }
}
