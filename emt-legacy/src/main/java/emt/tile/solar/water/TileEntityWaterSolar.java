package emt.tile.solar.water;

import emt.init.EMTBlocks;
import emt.tile.solar.TileEntitySolarBase;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityWaterSolar extends TileEntitySolarBase {
   public TileEntityWaterSolar() {
      this.output = EMTConfigHandler.compressedSolarOutput;
   }

   @Override
   public void createEnergy() {
      if (this.worldObj.isRaining() && this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord)
         || this.worldObj.isThundering() && this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord)) {
         this.energySource.addEnergy(this.output);
      }
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars, 1, 3);
   }
}
