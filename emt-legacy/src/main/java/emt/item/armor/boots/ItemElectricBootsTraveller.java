package emt.item.armor.boots;

import emt.compat.EMTKeyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import emt.util.EMTTextHelper;
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
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;

public class ItemElectricBootsTraveller extends ItemArmor implements IElectricItem, IVisDiscountGear, IMetalArmor, ISpecialArmor {
   public int maxCharge = 100000;
   public int energyPerDamage = 100;
   public int visDiscount = 2;
   public float speedBonus = 0.055F;
   public float jumpBonus = 0.3F;
   public double transferLimit = 100.0;
   public boolean speedBoostActive = false;

   public ItemElectricBootsTraveller(int par3, int par4) {
      super(ArmorMaterial.DIAMOND, par3, par4);
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
      this.setCreativeTab(EMT.TAB);
      MinecraftForge.EVENT_BUS.register(this);
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
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/boots_electric");
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

   public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
      list.add(EMTTextHelper.localize("tooltip.EMT.visDiscount") + ": " + this.visDiscount + "%");
   }

   public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
      return this.visDiscount;
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
      // EMT Reborn: running speed, step height, air control and jump boost are applied by the
      // emtbridge companion on the 1.20.1 player (movement is computed by the 1.20.1 client),
      // using speedBonus / jumpBonus of this item. Ctrl+M (boost + mode switch) toggles the speed.
   }

   @SubscribeEvent
   public void onPlayerJump(LivingJumpEvent event) {
      if (event.entityLiving instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.entityLiving;
         boolean hasArmor = player.getCurrentArmor(0) != null && player.getCurrentArmor(0).getItem() == this;
         if (hasArmor) {
            // applied by emtbridge on the 1.20.1 player
         }
      }
   }

   @SubscribeEvent
   public void onLivingFall(LivingFallEvent event) {
      if (event.entity instanceof EntityPlayer) {
         EntityPlayer entity = (EntityPlayer)event.entity;
         if (entity.inventory.armorInventory[0] != null && entity.inventory.armorInventory[0].getItem() instanceof ItemElectricBootsTraveller) {
            ItemStack stack = entity.inventory.armorInventory[0];
            if (ElectricItem.manager.use(stack, (double)this.energyPerDamage, entity)) {
               event.setCanceled(true);
            }
         }
      }
   }

   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "emt:textures/models/electricboots.png";
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
      return this.transferLimit;
   }

   public Item getChargedItem(ItemStack itemStack) {
      return this;
   }

   public Item getEmptyItem(ItemStack itemStack) {
      return this;
   }
}
