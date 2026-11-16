package emt.item.armor.goggles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemNanoGoggles extends ItemElectricGoggles {
   public ItemNanoGoggles(ArmorMaterial material, int renderIndex, int armorType) {
      super(material, renderIndex, armorType);
      this.setCreativeTab(EMT.TAB);
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
      this.visDiscount = 6;
      this.tier = 3;
      this.maxCharge = 100000.0;
      this.energyPerDamage = 1000;
      this.transferLimit = 100.0;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/goggles_nano");
   }

   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "emt:textures/models/thaumicnanohelmet.png";
   }

   @Override
   public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
      if (!EMTConfigHandler.nightVisionOff) {
         if (ElectricItem.manager.canUse(itemStack, 0.0)) {
            int x = MathHelper.floor_double(player.posX);
            int z = MathHelper.floor_double(player.posZ);
            int y = MathHelper.floor_double(player.posY);
            int lightlevel = player.worldObj.getBlockLightValue(x, y, z);
            if (lightlevel >= 0) {
               player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, -3));
            }

            ElectricItem.manager.use(itemStack, 0.0, player);
         } else {
            player.addPotionEffect(new PotionEffect(Potion.blindness.id, 300, 0, true));
         }
      }
   }
}
