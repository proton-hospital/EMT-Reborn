package emt.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTTextHelper;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;

public class ItemThorHammer extends ItemSword implements IRepairable {
   public ItemThorHammer() {
      super(ToolMaterial.EMERALD);
      this.setCreativeTab(EMT.TAB);
      this.setMaxDamage(2000);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:hammer/thorhammer");
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
      player.swingItem();
      float f = 1.0F;
      float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
      float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
      double d = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
      double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62 - (double)player.yOffset;
      double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
      Vec3 vec3d = Vec3.createVectorHelper(d, d1, d2);
      float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.141593F);
      float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.141593F);
      float f5 = -MathHelper.cos(-f1 * 0.01745329F);
      float f6 = MathHelper.sin(-f1 * 0.01745329F);
      float f7 = f4 * f5;
      float f9 = f3 * f5;
      double d3 = 5000.0;
      Vec3 vec3d1 = vec3d.addVector((double)f7 * d3, (double)f6 * d3, (double)f9 * d3);
      MovingObjectPosition movingobjectposition = player.worldObj.rayTraceBlocks(vec3d, vec3d1, true);
      if (movingobjectposition == null) {
         return itemstack;
      } else {
         if (movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;
            emt.compat.FeBridge.queueLightning(world, player, (double)i, (double)j, (double)k);
         } else if (movingobjectposition.typeOfHit == MovingObjectType.ENTITY) {
            Entity entityhit = movingobjectposition.entityHit;
            double x = entityhit.posX;
            double y = entityhit.posY;
            double z = entityhit.posZ;
            emt.compat.FeBridge.queueLightning(world, player, x, y, z);
         }

         if (player.capabilities.isCreativeMode) {
            return itemstack;
         } else {
            itemstack.damageItem(20, player);
            return itemstack;
         }
      }
   }

   public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
      entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 12.0F);
      itemstack.damageItem(1, attacker);
      return true;
   }

   public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
      list.add(EMTTextHelper.localize("tooltip.EMT.hammer.broken.The Hammer of Thor"));
   }
}
