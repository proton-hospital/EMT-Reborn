package emt.item.tool.omnitool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemOmnitoolIron extends ItemPickaxe implements IElectricItem {
   public int maxCharge = 20000;
   public int cost;
   public int hitCost = 125;

   public ItemOmnitoolIron() {
      super(ToolMaterial.EMERALD);
      this.efficiencyOnProperMaterial = 13.0F;
      this.setCreativeTab(EMT.TAB);
      this.setMaxStackSize(1);
      if (!EMTConfigHandler.toolsInBore) {
         this.setMaxDamage(27);
      } else {
         this.setMaxDamage(200);
      }
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:tools/omnitool_iron");
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
         this.cost = 100;
      } else {
         this.cost = 1;
      }

      ElectricItem.manager.use(stack, (double)this.cost, entityLiving);
      return true;
   }

   public boolean canHarvestBlock(Block block, ItemStack stack) {
      return Items.iron_axe.canHarvestBlock(block, stack)
         || Items.iron_sword.canHarvestBlock(block, stack)
         || Items.iron_pickaxe.canHarvestBlock(block, stack)
         || Items.iron_shovel.canHarvestBlock(block, stack)
         || Items.shears.canHarvestBlock(block, stack);
   }

   public float getDigSpeed(ItemStack stack, Block block, int meta) {
      if (!ElectricItem.manager.canUse(stack, (double)this.cost)) {
         return 1.0F;
      } else {
         return !(Items.wooden_axe.getDigSpeed(stack, block, meta) > 1.0F)
               && !(Items.wooden_sword.getDigSpeed(stack, block, meta) > 1.0F)
               && !(Items.wooden_pickaxe.getDigSpeed(stack, block, meta) > 1.0F)
               && !(Items.wooden_shovel.getDigSpeed(stack, block, meta) > 1.0F)
               && !(Items.shears.getDigSpeed(stack, block, meta) > 1.0F)
            ? super.getDigSpeed(stack, block, meta)
            : this.efficiencyOnProperMaterial;
      }
   }

   public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
      if (ElectricItem.manager.use(itemstack, (double)this.hitCost, attacker)) {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 8.0F);
      }

      return false;
   }

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
      for (int i = 0; i < player.inventory.mainInventory.length; i++) {
         ItemStack torchStack = player.inventory.mainInventory[i];
         if (torchStack != null && torchStack.getUnlocalizedName().toLowerCase(Locale.US).contains("torch")) {
            Item item = torchStack.getItem();
            if (item instanceof ItemBlock) {
               int oldMeta = torchStack.getItemDamage();
               int oldSize = torchStack.stackSize;
               boolean result = torchStack.tryPlaceItemIntoWorld(player, world, x, y, z, side, xOffset, yOffset, zOffset);
               if (player.capabilities.isCreativeMode) {
                  torchStack.setItemDamage(oldMeta);
                  torchStack.stackSize = oldSize;
               } else if (torchStack.stackSize <= 0) {
                  ForgeEventFactory.onPlayerDestroyItem(player, torchStack);
                  player.inventory.mainInventory[i] = null;
               }

               if (result) {
                  return true;
               }
            }
         }
      }

      return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
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
      return 200.0;
   }
}
