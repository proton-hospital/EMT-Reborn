package emt.tile.solar.air;

import emt.init.EMTBlocks;
import emt.tile.solar.TileEntitySolarBase;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityAirSolar extends TileEntitySolarBase {
   public TileEntityAirSolar() {
      this.output = EMTConfigHandler.compressedSolarOutput;
   }

   @Override
   public void createEnergy() {
      if (this.theSunIsVisible && this.yCoord >= 160) {
         this.energySource.addEnergy(this.output * 2.5);
      } else if (this.theSunIsVisible) {
         this.energySource.addEnergy(this.output);
      }
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars, 1, 15);
   }
}
