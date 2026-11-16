package emt.item.armor.goggles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
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

public class ItemSolarHelmetRevealing extends ItemQuantumGoggles {
   private int ticker;
   private int generating;
   private int genDay;
   private int genNight;
   private boolean sunIsUp;
   private boolean skyIsVisible;
   private boolean noSunWorld;
   private boolean dampPlace;
   private static final Map potionCost = new HashMap();

   public ItemSolarHelmetRevealing(ArmorMaterial material, int par3, int par4) {
      super(material, par3, par4);
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
      this.setCreativeTab(EMT.TAB);
      this.maxCharge = 1000000.0;
      this.genDay = 256;
      this.genNight = 128;
      this.maxCharge = 1.0E7;
      this.visDiscount = 7;
      this.tier = 3;
      this.transferLimit = 10000.0;
      this.energyPerDamage = 10000;
      potionCost.put(Potion.poison.id, 10000);
      potionCost.put(Potion.wither.id, 15000);
      potionCost.put(Potion.confusion.id, 5000);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/goggles_solar");
   }

   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String layerType) {
      return "emt:textures/models/solarrevealinghelmet.png";
   }

   @Override
   public void onArmorTick(World worldObj, EntityPlayer player, ItemStack itemStack) {
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

      // air refill and poison / wither / nausea cure: done by emtbridge (ArmorPower), MFCL's armour-tick player has no air or potions
   }

   public int checkForSun(EntityPlayer player) {
      if (this.ticker++ % tickRate() == 0) {
         this.updateVisibility(player);
      }

      if (this.sunIsUp && this.skyIsVisible) {
         this.generating = 0 + this.genDay;
         return this.generating;
      } else if (this.skyIsVisible) {
         this.generating = 0 + this.genNight;
         return this.generating;
      } else {
         this.generating = 0;
         return this.generating;
      }
   }

   public void updateVisibility(EntityPlayer player) {
      this.dampPlace = emt.compat.EMTPlatform.biome(player.worldObj, (int)player.posX, (int)player.posZ).getIntRainfall() > 0;
      this.noSunWorld = player.worldObj.provider.hasNoSky;
      Boolean rainWeather = this.dampPlace && (player.worldObj.isRaining() || player.worldObj.isThundering());
      if (player.worldObj.isDaytime() && !rainWeather) {
         this.sunIsUp = true;
      } else {
         this.sunIsUp = false;
      }

      if (player.worldObj.canBlockSeeTheSky((int)player.posX, (int)player.posY + 1, (int)player.posZ) && !this.noSunWorld) {
         this.skyIsVisible = true;
      } else {
         this.skyIsVisible = false;
      }
   }

   public static int tickRate() {
      return 128;
   }
}
