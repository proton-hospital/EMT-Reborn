package emt.item.tool.drill;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;

public class ItemThaumiumDrill extends ItemPickaxe implements IElectricItem {
   public int maxCharge = 60000;
   public int cost;
   public int tier = 2;
   public double transferLimit = 100.0;

   public ItemThaumiumDrill() {
      super(ToolMaterial.EMERALD);
      this.efficiencyOnProperMaterial = 20.0F;
      this.setCreativeTab(EMT.TAB);
      this.setMaxStackSize(1);
      if (!EMTConfigHandler.toolsInBore) {
         this.setMaxDamage(27);
      } else {
         this.setMaxDamage(240);
      }
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:tools/drill_thaumium");
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

   public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int par4, int par5, int par6, EntityLivingBase entityLiving) {
      if (!EMTConfigHandler.toolsInBore) {
         this.cost = 250;
      } else {
         this.cost = 1;
      }

      ElectricItem.manager.use(stack, (double)this.cost, entityLiving);
      return true;
   }

   public boolean canHarvestBlock(Block block, ItemStack stack) {
      return Items.diamond_pickaxe.canHarvestBlock(block, stack) || Items.diamond_shovel.canHarvestBlock(block, stack);
   }

   public float getDigSpeed(ItemStack stack, Block block, int meta) {
      if (!ElectricItem.manager.canUse(stack, (double)this.cost)) {
         return 1.0F;
      } else {
         return !(Items.wooden_pickaxe.getDigSpeed(stack, block, meta) > 1.0F) && !(Items.wooden_shovel.getDigSpeed(stack, block, meta) > 1.0F)
            ? super.getDigSpeed(stack, block, meta)
            : this.efficiencyOnProperMaterial;
      }
   }

   public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase entityliving1) {
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

   public boolean canProvideEnergy(ItemStack itemStack) {
      return false;
   }

   public double getMaxCharge(ItemStack itemStack) {
      return (double)this.maxCharge;
   }

   public int getTier(ItemStack itemStack) {
      return this.tier;
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
