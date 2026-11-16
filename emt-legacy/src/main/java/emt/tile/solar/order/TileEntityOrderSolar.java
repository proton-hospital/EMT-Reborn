package emt.tile.solar.order;

import emt.init.EMTBlocks;
import emt.tile.solar.TileEntitySolarBase;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityOrderSolar extends TileEntitySolarBase {
   public TileEntityOrderSolar() {
      this.output = EMTConfigHandler.compressedSolarOutput;
   }

   @Override
   public void createEnergy() {
      if (this.theSunIsVisible) {
         this.energySource.addEnergy(this.output * 3.0);
      }
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars, 1, 9);
   }
}
