package emt.item.armor.wings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.World;

public class ItemQuantumWing extends ItemNanoWing {
   public static int maxCharge = 1000000;

   public ItemQuantumWing(ArmorMaterial material, int par3, int par4) {
      super(material, par3, par4);
      this.setMaxStackSize(1);
      this.setMaxDamage(27);
      this.setCreativeTab(EMT.TAB);
      this.visDiscount = 6;
      this.transferLimit = 10000.0;
      this.energyPerDamage = 1000;
   }

   @Override
   public float getFallDamageMult() {
      return 0.0F;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/wing_quantum");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      if (entity == null) {
         return "emt:textures/models/transparent.png"; // wings are drawn by emtbridge, not as a chestplate
      }

      return "emt:textures/models/quantumwing.png";
   }

   @Override
   public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
      this.useWings(player, stack, world, 0.33F, 0.5F, 0.2F, 7);
   }

   @Override
   public double getMaxCharge(ItemStack itemStack) {
      return (double)maxCharge;
   }
}
