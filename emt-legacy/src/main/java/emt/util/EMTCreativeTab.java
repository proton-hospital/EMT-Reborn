package emt.util;

import emt.init.EMTItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class EMTCreativeTab extends CreativeTabs {
   public EMTCreativeTab(String tabLabel) {
      super(tabLabel);
      this.setBackgroundImageName("emt.png");
   }

   public Item getTabIconItem() {
      return EMTItems.electricThorHammer;
   }

   public boolean hasSearchBar() {
      return true;
   }

   public int getSearchbarWidth() {
      return 60;
   }
}
