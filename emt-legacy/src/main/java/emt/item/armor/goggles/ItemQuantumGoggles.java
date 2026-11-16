package emt.item.armor.goggles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemQuantumGoggles extends ItemNanoGoggles {
   private static final Map<Integer, Integer> potionCost = new HashMap<>();

   public ItemQuantumGoggles(ArmorMaterial material, int renderIndex, int armorType) {
      super(material, renderIndex, armorType);
      this.setCreativeTab(EMT.TAB);
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
      this.maxCharge = 1.0E7;
      this.tier = 3;
      this.visDiscount = 8;
      this.transferLimit = 10000.0;
      this.energyPerDamage = 1000;
      potionCost.put(Potion.poison.id, 10000);
      potionCost.put(Potion.wither.id, 15000);
      potionCost.put(Potion.confusion.id, 5000);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/goggles_quantum");
   }

   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "emt:textures/models/thaumicquantumhelmet.png";
   }

   @Override
   public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
      // air refill and poison / wither / nausea cure: done by emtbridge (ArmorPower), MFCL's armour-tick player has no air or potions
      if (!EMTConfigHandler.nightVisionOff) {
         if (ElectricItem.manager.canUse(itemStack, 0.0)) {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, -3));
         } else {
            player.addPotionEffect(new PotionEffect(Potion.blindness.id, 300, 0, true));
         }
      }
   }
}
