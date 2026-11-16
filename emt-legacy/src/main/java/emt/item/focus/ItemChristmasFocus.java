package emt.item.focus;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ItemChristmasFocus extends ItemBaseFocus {
   private static final AspectList visCost = new AspectList().add(Aspect.ORDER, 500).add(Aspect.AIR, 500);

   public ItemChristmasFocus() {
      super("christmas");
   }

   @Override
   public int getFocusColor(ItemStack stack) {
      return 65535;
   }

   @Override
   public AspectList getVisCost(ItemStack stack) {
      return visCost;
   }

   @Override
   public String getSortingHelper(ItemStack itemstack) {
      return "CHRISTMAS";
   }

   @Override
   public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer player, MovingObjectPosition mop) {
      ItemWandCasting wand = (ItemWandCasting)itemstack.getItem();
      if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
         int x = mop.blockX;
         int y = mop.blockY + 1;
         int z = mop.blockZ;
         if ((player.capabilities.isCreativeMode || wand.consumeAllVis(itemstack, player, this.getVisCost(itemstack), true, true)) && !world.isRemote) {
            // spawned as a 1.20.1 snow golem by emtbridge
            emt.compat.FeBridge.queueSnowman(player.getCommandSenderName(), x, y, z);
         }

         player.swingItem();
      }

      return itemstack;
   }
}
