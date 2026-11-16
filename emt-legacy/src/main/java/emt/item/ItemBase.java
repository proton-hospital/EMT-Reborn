package emt.item;

import emt.EMT;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBase extends Item {
   public ItemBase(String unlocName, String textureName) {
      this.setUnlocalizedName("EMT" + unlocName);
      this.setTextureName("emt:" + textureName);
      this.setCreativeTab(EMT.TAB);
   }

   public ItemBase(String unlocName) {
      this.setUnlocalizedName("EMT.item." + unlocName);
      this.setCreativeTab(EMT.TAB);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return ("§2" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
   }
}
