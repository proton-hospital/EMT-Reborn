package emt.tile.solar.air;

import emt.init.EMTBlocks;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityDoubleAirSolar extends TileEntityAirSolar {
   public TileEntityDoubleAirSolar() {
      this.output = EMTConfigHandler.doubleCompressedSolarOutput;
   }

   @Override
   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars2, 1, 0);
   }
}
