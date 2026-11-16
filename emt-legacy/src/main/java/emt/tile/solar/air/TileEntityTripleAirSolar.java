package emt.tile.solar.air;

import emt.init.EMTBlocks;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityTripleAirSolar extends TileEntityAirSolar {
   public TileEntityTripleAirSolar() {
      this.output = EMTConfigHandler.tripleCompressedSolarOutput;
   }

   @Override
   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars2, 1, 1);
   }
}
