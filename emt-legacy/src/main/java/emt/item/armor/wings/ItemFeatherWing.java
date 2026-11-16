package emt.item.armor.wings;

import emt.compat.EMTKeyboard;
import emt.compat.EMTPlatform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.client.model.ModelWings;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemFeatherWing extends ItemArmor {
   public int visDiscount = 0;

   public ItemFeatherWing(ArmorMaterial material, int par3, int par4) {
      super(material, par3, par4);
      this.setMaxStackSize(1);
      this.setMaxDamage(120);
      this.setCreativeTab(EMT.TAB);
      this.isDamageable();
   }

   public float getFallDamageMult() {
      return 0.75F;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:armor/wing_feather");
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      if (entity == null) {
         return "emt:textures/models/transparent.png"; // wings are drawn by emtbridge, not as a chestplate
      }

      return "emt:textures/models/featherwing.png";
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int armorSlot) {
      try {
         if (entity instanceof EntityPlayer) {
            ModelWings mw = new ModelWings();
            mw.isJumping = stack.stackTagCompound.getBoolean("isJumping");
            return mw;
         }
      } catch (NullPointerException var5) {
         return new ModelWings();
      }

      return new ModelWings();
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
      this.useWings(player, stack, world, 0.11F, 0.9F, 0.9F, 0);
   }

   public void useWings(EntityPlayer player, ItemStack stack, World world, float motionY, float motionXZ, float f1, int amount) {
      NBTTagCompound nbtData = EMTPlatform.getOrCreateNbtData(stack);
      boolean isJmuping = nbtData.getBoolean("isJumping");
      boolean isHolding = nbtData.getBoolean("isHolding");
      nbtData.setBoolean("isJumping", EMTKeyboard.isJumpKeyDown(player));
      if (isJmuping) {
         byte f = nbtData.getByte("f");
         nbtData.setBoolean("isHolding", true);
         nbtData.setByte("f", (byte)(f + 1));
         if (f > 7) {
            nbtData.setByte("f", (byte)7);
         }
      } else if (isHolding) {
         byte f = nbtData.getByte("f");
         nbtData.setBoolean("isHolding", false);
         player.motionY = (double)(motionY * (float)f);
         ElectricItem.manager.use(stack, (double)(motionY * (float)f * 10.0F * (float)amount), player);
         if (player.motionX < 0.5 && player.motionZ < 0.5 && player.motionX > -0.5 && player.motionZ > -0.5) {
            player.motionX /= (double)motionXZ;
            player.motionZ /= (double)motionXZ;
         }

         world.playSoundEffect(player.posX, player.posY, player.posZ, "mob.ghast.fireball", 1.0F, 1.0F);

         for (int i = 0; i < 4; i++) {
            world.spawnParticle(
               "cloud",
               player.posX - 1.0 + (double)world.rand.nextInt(100) / 50.0,
               player.posY - 1.0,
               player.posZ - 1.0 + (double)world.rand.nextInt(100) / 50.0,
               0.0,
               -0.5,
               0.0
            );
         }

         nbtData.setByte("f", (byte)0);
      }

      if (isJmuping && !player.onGround && player.motionY < 0.0) {
         player.motionY *= (double)f1;
      }

      if (player.isInWater() && !player.capabilities.isCreativeMode) {
         player.motionY += -0.03;
      }

      if (EMTConfigHandler.impactOfRain
         && emt.compat.EMTPlatform.biome(world, (int)player.posX, (int)player.posZ).canSpawnLightningBolt()
         && world.canBlockSeeTheSky((int)player.posX, (int)player.posY, (int)player.posZ)
         && world.isRaining()
         && !player.capabilities.isCreativeMode) {
         player.motionY += -0.03;
      }

      if (player.isSneaking() && !player.onGround && player.motionY < 0.0) {
         player.motionY *= 0.6;
      }
   }
}
