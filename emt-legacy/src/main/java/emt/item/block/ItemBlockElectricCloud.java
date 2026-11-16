package emt.item.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemBlockElectricCloud extends ItemBlock {
   public IIcon icon;

   public ItemBlockElectricCloud(Block block) {
      super(block);
   }

   public IIcon getIconFromDamage(int meta) {
      return this.icon;
   }

   public void registerIcons(IIconRegister ir) {
      this.icon = ir.registerIcon("emt:electric_cloud");
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return "§5§l" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
   }
}
