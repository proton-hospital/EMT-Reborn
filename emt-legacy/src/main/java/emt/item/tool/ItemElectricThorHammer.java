package emt.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import emt.util.EMTTextHelper;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemElectricThorHammer extends ItemSword implements IElectricItem {
   public int maxCharge = 1000000;
   private final int hitCost = 5000;
   private final int lightningCost = 75000;

   public ItemElectricThorHammer() {
      super(ToolMaterial.EMERALD);
      this.setCreativeTab(EMT.TAB);
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:hammer/electricthorhammer");
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
      ItemStack itemStack = new ItemStack(this, 1);
      if (this.getChargedItem(itemStack) == this) {
         ItemStack charged = new ItemStack(this, 1);
         ElectricItem.manager.charge(charged, 2.147483647E9, Integer.MAX_VALUE, true, false);
         itemList.add(charged);
      }

      if (this.getEmptyItem(itemStack) == this) {
         itemList.add(new ItemStack(this, 1, this.getMaxDamage()));
      }
   }

   public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
      if (ElectricItem.manager.use(itemstack, 5000.0, attacker)) {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 19.0F);
      }

      return false;
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
      list.add(EMTTextHelper.localize("tooltip.EMT.hammer.electric").replace("%player%", player.getCommandSenderName()));
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
      if (player.capabilities.isCreativeMode || ElectricItem.manager.canUse(itemstack, 75000.0)) {
         player.swingItem();
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 8.0, player.posY, player.posZ - 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 8.0, player.posY, player.posZ + 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 8.0, player.posY, player.posZ - 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 8.0, player.posY, player.posZ + 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 8.0, player.posY, player.posZ);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 8.0, player.posY, player.posZ);
         emt.compat.FeBridge.queueLightning(world, player, player.posX, player.posY, player.posZ - 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX, player.posY, player.posZ + 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 8.0, player.posY, player.posZ + 1.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 8.0, player.posY, player.posZ + 2.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 8.0, player.posY, player.posZ + 3.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 8.0, player.posY, player.posZ + 4.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 8.0, player.posY, player.posZ - 1.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 8.0, player.posY, player.posZ - 2.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 8.0, player.posY, player.posZ - 3.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 8.0, player.posY, player.posZ - 4.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 1.0, player.posY, player.posZ + 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 2.0, player.posY, player.posZ - 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 3.0, player.posY, player.posZ + 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX + 4.0, player.posY, player.posZ - 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 1.0, player.posY, player.posZ + 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 2.0, player.posY, player.posZ - 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 3.0, player.posY, player.posZ + 8.0);
         emt.compat.FeBridge.queueLightning(world, player, player.posX - 4.0, player.posY, player.posZ - 8.0);
         emt.compat.FeBridge.queueArcher(world, player, player.posX + 8.0, player.posY + 2.0, player.posZ - 8.0);
         emt.compat.FeBridge.queueArcher(world, player, player.posX - 8.0, player.posY + 2.0, player.posZ + 8.0);
         emt.compat.FeBridge.queueArcher(world, player, player.posX - 8.0, player.posY + 2.0, player.posZ - 8.0);
         emt.compat.FeBridge.queueArcher(world, player, player.posX + 8.0, player.posY + 2.0, player.posZ + 8.0);
         if (!player.capabilities.isCreativeMode) {
            ElectricItem.manager.use(itemstack, 75000.0, player);
         }
         return itemstack;
      } else {
         emt.compat.FeBridge.queueLightning(world, player, player.posX, player.posY, player.posZ);
         player.addPotionEffect(new PotionEffect(Potion.harm.getId(), 1, 1));
         return itemstack;
      }
   }

   public boolean isRepairable() {
      return false;
   }

   public int getItemEnchantability() {
      return !EMTConfigHandler.enchanting ? 0 : 4;
   }

   public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2) {
      return EMTConfigHandler.enchanting;
   }

   public Item getChargedItem(ItemStack itemStack) {
      return this;
   }

   public Item getEmptyItem(ItemStack itemStack) {
      return this;
   }

   public boolean canProvideEnergy(ItemStack itemStack) {
      return false;
   }

   public double getMaxCharge(ItemStack itemStack) {
      return (double)this.maxCharge;
   }

   public int getTier(ItemStack itemStack) {
      return 2;
   }

   public double getTransferLimit(ItemStack itemStack) {
      return 100.0;
   }
}
