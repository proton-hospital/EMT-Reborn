package emt.item.focus;

import emt.entity.EntityEnergyBall;
import ic2.api.item.ElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemEnergyBallFocus extends ItemBaseFocus {
   public ItemEnergyBallFocus() {
      super("energyBall");
   }

   @Override
   public int getFocusColor(ItemStack stack) {
      return 255;
   }

   @Override
   public String getSortingHelper(ItemStack itemstack) {
      return "ENERGYBALL";
   }

   @Override
   public ItemStack onFocusRightClick(ItemStack stack, World world, EntityPlayer player, MovingObjectPosition mop) {
      if (world.isRemote) {
         return stack;
      } else {
         ItemStack armor = player.inventory.armorInventory[2];
         if (armor == null) {
            return stack;
         } else {
            double val = ElectricItem.manager.discharge(armor, 5120.0, 4, true, false, false);
            if (val < 5120.0) {
               return stack;
            } else {
               float rotX = -MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI)
                  * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI)
                  * 100.0F;
               float rotY = -MathHelper.sin(player.rotationPitch / 180.0F * (float) Math.PI) * 100.0F;
               float rotZ = MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI)
                  * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI)
                  * 100.0F;
               world.spawnEntityInWorld(new EntityEnergyBall(world, player, (double)rotX, (double)rotY, (double)rotZ));
               return stack;
            }
         }
      }
   }
}
