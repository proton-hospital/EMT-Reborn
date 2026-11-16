package emt.item.tool.chainsaw;

import emt.compat.EMTKeyboard;
import emt.compat.EMTPlatform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemDiamondChainsaw extends ItemAxe implements IElectricItem {
   public int maxCharge = 40000;
   public int cost = 200;
   public int hitCost = 300;
   public int tier = 2;

   public ItemDiamondChainsaw() {
      super(ToolMaterial.EMERALD);
      this.efficiencyOnProperMaterial = 16.0F;
      this.setCreativeTab(EMT.TAB);
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:tools/chainsaw_diamond");
   }

   public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int par4, int par5, int par6, EntityLivingBase entityLiving) {
      ElectricItem.manager.use(stack, (double)this.cost, entityLiving);
      return true;
   }

   public boolean canHarvestBlock(Block block, ItemStack stack) {
      return Items.diamond_axe.canHarvestBlock(block, stack)
         || Items.diamond_sword.canHarvestBlock(block, stack)
         || Items.shears.canHarvestBlock(block, stack);
   }

   public float getDigSpeed(ItemStack stack, Block block, int meta) {
      if (!ElectricItem.manager.canUse(stack, (double)this.cost)) {
         return 1.0F;
      } else {
         return !(Items.wooden_axe.getDigSpeed(stack, block, meta) > 1.0F) && !(Items.wooden_sword.getDigSpeed(stack, block, meta) > 1.0F)
            ? super.getDigSpeed(stack, block, meta)
            : this.efficiencyOnProperMaterial;
      }
   }

   public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
      if (ElectricItem.manager.use(itemstack, (double)this.hitCost, attacker)) {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 12.0F);
      }

      return false;
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

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
      for (int i = 0; i < player.inventory.mainInventory.length; i++) {
         ItemStack torchStack = player.inventory.mainInventory[i];
         if (torchStack != null) {
            Item item = torchStack.getItem();
            if (item instanceof ItemBlock && Block.getBlockFromItem(item) instanceof BlockTorch) {
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

   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (!EMTPlatform.isSimulating(world)) {
         return super.onItemRightClick(stack, world, player);
      } else {
         if (EMTKeyboard.isModeSwitchKeyDown(player)) {
            NBTTagCompound nbt = EMTPlatform.getOrCreateNbtData(stack);
            if (!nbt.hasKey("shearsMode")) {
               nbt.setBoolean("shearsMode", true);
            }

            if (!nbt.getBoolean("shearsMode")) {
               nbt.setBoolean("shearsMode", true);
               EMTPlatform.messagePlayer(player, "ic2.tooltip.mode", new Object[]{"ic2.tooltip.mode.normal"});
            } else {
               nbt.setBoolean("shearsMode", false);
               EMTPlatform.messagePlayer(player, "ic2.tooltip.mode", new Object[]{"ic2.tooltip.mode.noShear"});
            }
         }

         return super.onItemRightClick(stack, world, player);
      }
   }

   public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
      NBTTagCompound nbt = EMTPlatform.getOrCreateNbtData(itemstack);
      if (!nbt.hasKey("shearsMode")) {
         nbt.setBoolean("shearsMode", true);
      }

      if (nbt.getBoolean("shearsMode") && !player.worldObj.isRemote) {
         Block block = player.worldObj.getBlock(x, y, z);
         if (block instanceof IShearable) {
            IShearable target = (IShearable)block;
            if (target.isShearable(itemstack, player.worldObj, x, y, z)) {
               ArrayList<ItemStack> drops = target.onSheared(
                  itemstack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack)
               );
               Random rand = new Random();

               for (ItemStack stack : drops) {
                  float f = 0.7F;
                  double xOffset = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5;
                  double yOffset = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5;
                  double zOffset = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5;
                  EntityItem entityitem = new EntityItem(player.worldObj, (double)x + xOffset, (double)y + yOffset, (double)z + zOffset, stack);
                  entityitem.delayBeforeCanPickup = 10;
                  player.worldObj.spawnEntityInWorld(entityitem);
               }

               player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
      NBTTagCompound nbt = EMTPlatform.getOrCreateNbtData(itemstack);
      if (!nbt.hasKey("shearsMode")) {
         nbt.setBoolean("shearsMode", true);
      }

      if (!nbt.getBoolean("shearsMode") || entity.worldObj.isRemote) {
         return false;
      } else if (!(entity instanceof IShearable)) {
         return false;
      } else {
         IShearable target = (IShearable)entity;
         if (target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ)) {
            ArrayList<ItemStack> drops = target.onSheared(
               itemstack,
               entity.worldObj,
               (int)entity.posX,
               (int)entity.posY,
               (int)entity.posZ,
               EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack)
            );
            Random rand = new Random();

            for (ItemStack stack : drops) {
               EntityItem ent = entity.entityDropItem(stack, 1.0F);
               ent.motionY = ent.motionY + (double)(rand.nextFloat() * 0.05F);
               ent.motionX = ent.motionX + (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F);
               ent.motionZ = ent.motionZ + (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F);
            }
         }

         return true;
      }
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean flag) {
      if (entity instanceof EntityLivingBase) {
         NBTTagCompound nbt = EMTPlatform.getOrCreateNbtData(stack);
         if (!nbt.hasKey("shearsMode")) {
            nbt.setBoolean("shearsMode", true);
         }
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
      return this.tier;
   }

   public double getTransferLimit(ItemStack itemStack) {
      return 300.0;
   }
}
