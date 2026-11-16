package emt.item.armor.boots;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.util.EMTConfigHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class ItemNanoBootsTraveller extends ItemElectricBootsTraveller {
   public ItemNanoBootsTraveller(int par3, int par4) {
      super(par3, par4);
      this.maxCharge = 1000000;
      this.speedBonus = (float)EMTConfigHandler.nanoBootsSpeed;
      this.jumpBonus = (float)EMTConfigHandler.nanoBootsJump;
      this.visDiscount = 4;
      this.transferLimit = 1000.0;
      this.energyPerDamage = 500;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/boots_nano");
   }

   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "emt:textures/models/nanobootstravel.png";
   }
}
