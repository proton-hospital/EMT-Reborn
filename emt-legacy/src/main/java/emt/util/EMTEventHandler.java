package emt.util;

import baubles.api.BaublesApi;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import emt.EMT;
import emt.init.EMTBlocks;
import emt.init.EMTItems;
import emt.item.armor.wings.ItemFeatherWing;
import ic2.api.item.ElectricItem;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintChicken;

public class EMTEventHandler {
   @SubscribeEvent
   public void onLivingHurt(LivingHurtEvent e) {
      if (e.entityLiving instanceof EntityPlayer && e.source == DamageSource.fall) {
         ItemStack s = ((EntityPlayer)e.entityLiving).inventory.armorInventory[2];
         if (s != null && s.getItem() instanceof ItemFeatherWing) {
            e.ammount = e.ammount * ((ItemFeatherWing)s.getItem()).getFallDamageMult();
         }
      }
   }

   @SubscribeEvent
   public void onPlayerTickEvent(PlayerTickEvent e) {
      ItemStack s = e.player.inventory.armorInventory[2];
      if (s != null && s.getItem() instanceof ItemFeatherWing && e.phase != Phase.START) {
         if (e.player.motionY > 0.0 && e.player.fallDistance > 0.0F) {
            e.player.fallDistance = (float)((double)e.player.fallDistance - e.player.motionY);
         }
      }
   }

   @SubscribeEvent
   public void onEntityLivingDrops(LivingDropsEvent event) {
      if (event.entityLiving instanceof EntityCreeper) {
         EntityCreeper creeper = (EntityCreeper)event.entityLiving;
         if (creeper.getPowered()) {
            event.entityLiving.entityDropItem(new ItemStack(EMTItems.itemEMTItems, 1, 6), 1.0F);
         }
      }

      if (event.entityLiving instanceof EntityTaintChicken) {
         event.entityLiving.entityDropItem(new ItemStack(EMTItems.itemEMTItems, event.entityLiving.worldObj.rand.nextInt(3), 13), 1.0F);
      }
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.modID.equals("EMT")) {
         EMTConfigHandler.syncConfig();
         EMT.LOGGER.info("Refreshing configuration file.");
      }
   }

   @SubscribeEvent
   public void onSetEntityAttack(LivingSetAttackTargetEvent e) {
      if (e.entityLiving != null && e.target != null && e.entityLiving instanceof EntityLiving && e.target instanceof EntityPlayer) {
         IInventory inventory = BaublesApi.getBaubles((EntityPlayer)e.target);

         for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == EMTItems.onering) {
               ((EntityLiving)e.entityLiving).setAttackTarget(null);
            }
         }
      }
   }

   @SubscribeEvent
   public void createCloud(PlayerInteractEvent e) {
      if (e.action == Action.RIGHT_CLICK_BLOCK && !e.world.isRemote) {
         Block block = e.world.getBlock(e.x, e.y, e.z);
         int meta = e.world.getBlockMetadata(e.x, e.y, e.z);
         if (block == ConfigBlocks.blockAiry && meta == 1) {
            ItemStack currentStack = e.entityPlayer.inventory.getCurrentItem();
            if (currentStack != null) {
               double val = ElectricItem.manager.discharge(currentStack, 256.0, 4, false, true, false);
               if (!(val < 256.0)) {
                  e.world.setBlock(e.x, e.y, e.z, EMTBlocks.electricCloud);
               }
            }
         }
      }
   }
}
