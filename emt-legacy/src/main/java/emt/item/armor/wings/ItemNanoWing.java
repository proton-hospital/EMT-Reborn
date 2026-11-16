package emt.item.armor.wings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class ItemNanoWing extends ItemThaumiumReinforcedWing implements IElectricItem, ISpecialArmor, IMetalArmor {
   public static int maxCharge = 100000;
   public int tier = 3;
   public double transferLimit = 1000.0;
   public int energyPerDamage = 100;

   public ItemNanoWing(ArmorMaterial material, int par3, int par4) {
      super(material, par3, par4);
      this.setMaxStackSize(1);
      this.setMaxDamage(27);
      this.setCreativeTab(EMT.TAB);
      this.visDiscount = 5;
   }

   @Override
   public float getFallDamageMult() {
      return 0.2F;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/wing_nano");
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

   @SideOnly(Side.CLIENT)
   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      if (entity == null) {
         return "emt:textures/models/transparent.png"; // wings are drawn by emtbridge, not as a chestplate
      }

      return "emt:textures/models/nanowing.png";
   }

   @Override
   public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
      this.useWings(player, stack, world, 0.25F, 0.6F, 0.3F, 5);
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
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

   public int getEnergyPerDamage() {
      return this.energyPerDamage;
   }

   public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
      if (source.isUnblockable()) {
         return new ArmorProperties(0, 0.0, 3);
      } else {
         double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
         int energyPerDamage = this.getEnergyPerDamage();
         double damageLimit = energyPerDamage <= 0 ? 0.0 : 25.0 * ElectricItem.manager.getCharge(armor) / (double)energyPerDamage;
         return new ArmorProperties(3, absorptionRatio, (int)damageLimit);
      }
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return ElectricItem.manager.getCharge(armor) >= (double)this.getEnergyPerDamage()
         ? (int)Math.round(20.0 * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio())
         : 0;
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      ElectricItem.manager.discharge(stack, (double)(damage * this.getEnergyPerDamage()), Integer.MAX_VALUE, true, false, false);
   }

   public double getDamageAbsorptionRatio() {
      return 1.1;
   }

   private double getBaseAbsorptionRatio() {
      return 0.15;
   }

   public boolean canProvideEnergy(ItemStack itemStack) {
      return false;
   }

   public double getMaxCharge(ItemStack itemStack) {
      return (double)maxCharge;
   }

   public int getTier(ItemStack itemStack) {
      return 2;
   }

   public double getTransferLimit(ItemStack itemStack) {
      return this.transferLimit;
   }

   public Item getChargedItem(ItemStack itemStack) {
      return this;
   }

   public Item getEmptyItem(ItemStack itemStack) {
      return this;
   }
}
