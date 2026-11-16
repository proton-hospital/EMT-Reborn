package emt.item.armor.wings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTTextHelper;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

public class ItemThaumiumReinforcedWing extends ItemFeatherWing implements IVisDiscountGear, IRepairable {
   public ItemThaumiumReinforcedWing(ArmorMaterial material, int par3, int par4) {
      super(material, par3, par4);
      this.setMaxStackSize(1);
      this.setMaxDamage(250);
      this.setCreativeTab(EMT.TAB);
      this.isDamageable();
      this.visDiscount = 4;
   }

   @Override
   public float getFallDamageMult() {
      return 0.5F;
   }

   public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
      return this.visDiscount;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/wing_thaumium");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      if (entity == null) {
         return "emt:textures/models/transparent.png"; // wings are drawn by emtbridge, not as a chestplate
      }

      return "emt:textures/models/thaumiumwing.png";
   }

   public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
      list.add(EMTTextHelper.localize("tooltip.EMT.visDiscount") + ": " + this.visDiscount + "%");
   }

   @Override
   public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
      this.useWings(player, stack, world, 0.15F, 0.7F, 0.5F, 0);
   }
}
