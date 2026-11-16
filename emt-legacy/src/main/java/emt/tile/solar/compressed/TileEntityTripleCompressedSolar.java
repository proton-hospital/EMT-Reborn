package emt.tile.solar.compressed;

import emt.init.EMTBlocks;
import emt.tile.solar.TileEntitySolarBase;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityTripleCompressedSolar extends TileEntitySolarBase {
   public TileEntityTripleCompressedSolar() {
      this.output = EMTConfigHandler.tripleCompressedSolarOutput;
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars, 1, 2);
   }
}
