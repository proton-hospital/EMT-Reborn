package emt.item.tool.drill;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.Icon;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.lib.utils.BlockUtils;

public class ItemRockbreakerDrill extends ItemThaumiumDrill {
   private static final Block[] isEffective = new Block[]{
      Blocks.nether_brick,
      Blocks.netherrack,
      Blocks.glowstone,
      Blocks.iron_block,
      Blocks.gold_block,
      Blocks.diamond_block,
      Blocks.lapis_block,
      Blocks.redstone_block,
      Blocks.redstone_ore,
      Blocks.emerald_ore,
      Blocks.emerald_block,
      Blocks.stonebrick,
      Blocks.glass,
      Blocks.stone,
      Blocks.gold_ore,
      Blocks.iron_ore,
      Blocks.coal_ore,
      Blocks.cobblestone,
      Blocks.diamond_ore,
      Blocks.lapis_ore,
      Blocks.dirt,
      Blocks.gravel,
      Blocks.sand,
      Blocks.sandstone,
      Blocks.soul_sand,
      Blocks.clay,
      Blocks.grass,
      Blocks.snow_layer,
      Blocks.snow,
      Blocks.farmland,
      Blocks.hardened_clay,
      Blocks.stained_hardened_clay,
      Blocks.mossy_cobblestone
   };
   public Icon icon;
   int side;
   public int searchCost = 1000;
   public int hitCost = 400;

   public ItemRockbreakerDrill() {
      this.side = 0;
      this.setCreativeTab(EMT.TAB);
      this.efficiencyOnProperMaterial = 25.0F;
      this.setMaxStackSize(1);
      if (!EMTConfigHandler.toolsInBore) {
         this.setMaxDamage(27);
      } else {
         this.setMaxDamage(2571);
      }

      this.maxCharge = 900000;
      this.transferLimit = 900.0;
      this.tier = 3;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:tools/drill_rockbreaker");
   }

   private boolean isEffectiveAgainst(Block block) {
      for (int var3 = 0; var3 < isEffective.length; var3++) {
         if (isEffective[var3] == block) {
            return true;
         }
      }

      return false;
   }

   public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
      MovingObjectPosition movingobjectposition = BlockUtils.getTargetBlock(player.worldObj, player, true);
      if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {
         this.side = movingobjectposition.sideHit;
      }

      return super.onBlockStartBreak(itemstack, X, Y, Z, player);
   }

   @Override
   public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entityLiving) {
      if (!EMTConfigHandler.toolsInBore) {
         this.cost = 350;
      } else {
         this.cost = 1;
      }

      if (entityLiving.isSneaking()) {
         if (ElectricItem.manager.canUse(stack, (double)this.cost)) {
            ElectricItem.manager.use(stack, (double)this.cost, entityLiving);
         }

         return true;
      } else {
         int md = world.getBlockMetadata(x, y, z);
         if (ForgeHooks.isToolEffective(stack, block, md) || this.isEffectiveAgainst(block)) {
            for (int aa = -1; aa <= 1; aa++) {
               for (int bb = -1; bb <= 1; bb++) {
                  int xx = 0;
                  int yy = 0;
                  int zz = 0;
                  if (this.side <= 1) {
                     xx = aa;
                     zz = bb;
                  } else if (this.side <= 3) {
                     xx = aa;
                     yy = bb;
                  } else {
                     zz = aa;
                     yy = bb;
                  }

                  Block bl = world.getBlock(x + xx, y + yy, z + zz);
                  md = world.getBlockMetadata(x + xx, y + yy, z + zz);
                  if (ForgeHooks.isToolEffective(stack, bl, md) || this.isEffectiveAgainst(bl)) {
                     if (ElectricItem.manager.canUse(stack, (double)this.cost)) {
                        ElectricItem.manager.use(stack, (double)this.cost, entityLiving);
                     }

                     int fortune = EnchantmentHelper.getFortuneModifier(entityLiving);
                     world.setBlockToAir(x + xx, y + yy, z + zz);
                     ArrayList ret = bl.getDrops(world, x + xx, y + yy, z + zz, md, fortune);
                     boolean creative = false;
                     if (entityLiving instanceof EntityPlayer && ((EntityPlayer)entityLiving).capabilities.isCreativeMode) {
                        creative = true;
                     }

                     if (ret.size() > 0 && !creative && !entityLiving.worldObj.isRemote) {
                        for (ItemStack is : (java.util.List<ItemStack>) ret) {
                           world.spawnEntityInWorld(
                              new EntityFollowingItem(
                                 world, (double)x + (double)xx + 0.5, (double)y + (double)yy + 0.5, (double)z + (double)zz + 0.5, is, entityLiving, 3
                              )
                           );
                        }
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
      if (!player.isSneaking()) {
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
      } else {
         ElectricItem.manager.use(stack, (double)this.searchCost, player);
         if (!world.isRemote) {
            world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "thaumcraft:wandfail", 0.2F, 0.2F + world.rand.nextFloat() * 0.2F);
            return super.onItemUse(stack, player, world, x, y, z, side, xOffset, xOffset, zOffset);
         } else {
            Minecraft mc = Minecraft.getMinecraft();
            Thaumcraft.instance.renderEventHandler.startScan(player, x, y, z, System.currentTimeMillis() + 5000L, 1);
            player.swingItem();
            return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
         }
      }
   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
      if (!player.worldObj.isRemote && (!(entity instanceof EntityPlayer) || MinecraftServer.getServer().isPVPEnabled())) {
         entity.setFire(2);
      }

      return super.onLeftClickEntity(stack, player, entity);
   }

   @Override
   public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
      if (ElectricItem.manager.use(itemstack, (double)this.hitCost, attacker)) {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 12.0F);
      }

      return false;
   }
}
