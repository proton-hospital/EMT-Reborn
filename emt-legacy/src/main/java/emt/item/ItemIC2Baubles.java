package emt.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemIC2Baubles extends ItemBase implements IBauble {
   public IIcon[] icon = new IIcon[16];
   public static int wornTick;
   public Random random = new Random();

   public ItemIC2Baubles() {
      super("bauble");
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setMaxStackSize(1);
      wornTick = 0;
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      String name = "";
      switch (itemstack.getItemDamage()) {
         case 0:
            name = "euMaker.armor";
            break;
         case 1:
            name = "euMaker.inventory";
            break;
         default:
            name = "nothing";
      }

      return this.getUnlocalizedName() + "." + name;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister ri) {
      this.icon[0] = ri.registerIcon("emt:armoreumaker");
      this.icon[1] = ri.registerIcon("emt:inventoryeumaker");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int meta) {
      return this.icon[meta];
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) {
      list.add(new ItemStack(this, 1, 0));
      list.add(new ItemStack(this, 1, 1));
   }

   public BaubleType getBaubleType(ItemStack stack) {
      return stack.getItemDamage() <= 1 ? BaubleType.RING : null;
   }

   public void onWornTick(ItemStack stack, EntityLivingBase player) {
      if (!player.worldObj.isRemote) {
         if (stack != null && stack.getItemDamage() == 0 && player instanceof EntityPlayer) {
            int energyLeft = EMTConfigHandler.armorBaubleProduction;

            for (int i = 0; i < ((EntityPlayer)player).inventory.armorInventory.length; i++) {
               if (energyLeft > 0
                  && ((EntityPlayer)player).inventory.armorInventory[i] != null
                  && ((EntityPlayer)player).inventory.armorInventory[i].getItem() instanceof IElectricItem) {
                  double sentPacket = ElectricItem.manager.charge(((EntityPlayer)player).inventory.armorInventory[i], (double)energyLeft, 4, false, false);
                  energyLeft = (int)((double)energyLeft - sentPacket);
               }
            }
         }

         if (stack != null && stack.getItemDamage() == 1 && player instanceof EntityPlayer) {
            int energyLeft = EMTConfigHandler.inventoryBaubleProdution;

            for (int ix = 0; ix < ((EntityPlayer)player).inventory.mainInventory.length; ix++) {
               if (energyLeft > 0
                  && ((EntityPlayer)player).inventory.mainInventory[ix] != null
                  && ((EntityPlayer)player).inventory.mainInventory[ix].getItem() instanceof IElectricItem) {
                  double sentPacket = ElectricItem.manager.charge(((EntityPlayer)player).inventory.mainInventory[ix], (double)energyLeft, 4, false, false);
                  energyLeft = (int)((double)energyLeft - sentPacket);
               }
            }
         }
      }
   }

   public void onEquipped(ItemStack stack, EntityLivingBase player) {
   }

   public void onUnequipped(ItemStack stack, EntityLivingBase player) {
   }

   public boolean canEquip(ItemStack stack, EntityLivingBase player) {
      return true;
   }

   public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
      return true;
   }
}
