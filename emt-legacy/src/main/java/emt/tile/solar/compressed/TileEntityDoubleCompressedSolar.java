package emt.tile.solar.compressed;

import emt.init.EMTBlocks;
import emt.tile.solar.TileEntitySolarBase;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityDoubleCompressedSolar extends TileEntitySolarBase {
   public TileEntityDoubleCompressedSolar() {
      this.output = EMTConfigHandler.doubleCompressedSolarOutput;
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtSolars, 1, 1);
   }
}
