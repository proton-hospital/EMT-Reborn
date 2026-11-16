package emt.tile.solar.earth;

import emt.init.EMTBlocks;
import emt.tile.solar.TileEntitySolarBase;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityEarthSolar extends TileEntitySolarBase {
   public TileEntityEarthSolar() {
      this.output = EMTConfigHandler.compressedSolarOutput;
   }

   @Override
   public void createEnergy() {
      if (this.theSunIsVisible && this.yCoord <= 10) {
         this.energySource.addEnergy(this.output * 2.5);
      } else if (this.theSunIsVisible) {
         this.energySource.addEnergy(this.output);
      }
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars2, 1, 2);
   }
}
