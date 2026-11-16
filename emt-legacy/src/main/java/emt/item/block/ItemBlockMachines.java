package emt.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMachines extends ItemBlock {
   public ItemBlockMachines(Block id) {
      super(id);
      this.setHasSubtypes(true);
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      String name = "";
      switch (itemstack.getItemDamage()) {
         case 0:
            name = "wandrecharge";
            break;
         case 1:
            name = "etheralprocessor";
            break;
         case 2:
            name = "raindispeller";
            break;
         default:
            name = "nothing";
      }

      return this.getUnlocalizedName() + "." + name;
   }

   public int getMetadata(int par1) {
      return par1;
   }
}
