package emt.tile.solar.fire;

import emt.init.EMTBlocks;
import emt.tile.solar.TileEntitySolarBase;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityFireSolar extends TileEntitySolarBase {
   public TileEntityFireSolar() {
      this.output = EMTConfigHandler.compressedSolarOutput;
   }

   @Override
   public void checkConditions() {
      if (!this.initialized && this.worldObj != null) {
         this.canRain = emt.compat.EMTPlatform.biome(this.worldObj, this.xCoord, this.zCoord).getIntRainfall() > 0;
         this.initialized = true;
      }

      if (this.worldObj.provider.dimensionId == -1) {
         this.energySource.addEnergy(this.output * 2.0);
      } else if (this.tick-- == 0) {
         this.updateSunState();
         this.tick = 64;
      }
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars, 1, 12);
   }
}
