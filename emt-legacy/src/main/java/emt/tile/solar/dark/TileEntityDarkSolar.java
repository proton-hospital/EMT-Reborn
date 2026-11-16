package emt.tile.solar.dark;

import emt.init.EMTBlocks;
import emt.tile.solar.TileEntitySolarBase;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityDarkSolar extends TileEntitySolarBase {
   public TileEntityDarkSolar() {
      this.output = EMTConfigHandler.compressedSolarOutput;
   }

   @Override
   public void createEnergy() {
      if (this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord)
         && !this.worldObj.isDaytime()
         && !this.worldObj.isRaining()
         && !this.worldObj.isThundering()) {
         this.energySource.addEnergy(this.output);
      }
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars, 1, 6);
   }
}
