package emt.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;

@SideOnly(Side.CLIENT)
public class ModelSpecialArmor extends ModelBiped {
   public ModelRenderer rightWing;
   public ModelRenderer leftWing;
   public ModelRenderer center;
   public ModelRenderer jetpack;
   public boolean isJumping;
   public int type = 0;

   public ModelSpecialArmor() {
      this(1.0F, 0);
   }

   public ModelSpecialArmor(float scale, int type) {
      this(scale, 0.0F, type);
   }

   public ModelSpecialArmor(float scale, float offset, int type) {
      super(scale, offset, type == 1 ? 128 : 64, type == 0 ? 32 : (type == 1 ? 96 : 64));
      this.type = type;
      switch (this.type) {
         case 1:
            this.textureWidth = 128;
            this.textureHeight = 96;
            this.jetpack = new ModelRenderer(this, 16, 69);
            this.jetpack.setTextureSize(64, 48);
            this.jetpack.addBox(-4.0F, -12.0F, -2.0F, 8, 24, 4, scale * 2.0F);
            this.jetpack.setRotationPoint(0.0F, 0.0F + offset, 0.0F);
            break;
         case 2:
            this.textureWidth = 64;
            this.textureHeight = 64;
            this.rightWing = new ModelRenderer(this, 24, 32);
            this.rightWing.addBox(-11.0F, -1.0F, 0.0F, 11, 13, 1);
            this.rightWing.setRotationPoint(-2.0F, 0.0F, 3.0F);
            this.rightWing.setTextureSize(64, 32);
            this.rightWing.mirror = false;
            this.setRotation(this.rightWing, 0.0F, 0.1570796F, 0.0F);
            this.leftWing = new ModelRenderer(this, 0, 32);
            this.leftWing.addBox(0.0F, -1.0F, 0.0F, 11, 13, 1);
            this.leftWing.setRotationPoint(2.0F, 0.0F, 3.0F);
            this.leftWing.setTextureSize(64, 32);
            this.leftWing.mirror = true;
            this.setRotation(this.leftWing, 0.0F, -0.1570796F, 0.0F);
            this.center = new ModelRenderer(this, 0, 46);
            this.center.addBox(0.0F, 0.0F, 1.0F, 8, 14, 1);
            this.center.setRotationPoint(-4.0F, 0.0F, 3.0F);
            this.center.setTextureSize(64, 32);
            this.center.mirror = true;
            this.setRotation(this.center, 0.0F, 0.0F, 0.0F);
      }
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
      EntityPlayer player = (EntityPlayer)entity;
      this.heldItemRight = player.inventory.getCurrentItem() != null ? 1 : 0;
      this.isRiding = player.isRiding();
      this.isSneak = player.isSneaking();
      this.aimedBow = player.inventory.getCurrentItem() != null
         && player.inventory.getCurrentItem().getItem().getItemUseAction(player.inventory.getCurrentItem()) == EnumAction.bow
         && player.isUsingItem();
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entity);
      this.bipedBody.render(scale);
      this.bipedRightArm.render(scale);
      this.bipedLeftArm.render(scale);
      switch (this.type) {
         case 1:
            this.jetpack.render(scale);
            break;
         case 2:
            this.center.render(scale);
            this.leftWing.render(scale);
            this.rightWing.render(scale);
      }
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entity) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entity);
      if (this.isJumping) {
         this.rightWing.rotateAngleY = 0.5F;
         this.leftWing.rotateAngleY = -0.5F;
      }

      if (this.isSneak) {
         switch (this.type) {
            case 1:
               this.jetpack.rotateAngleX = 0.5F;
               break;
            case 2:
               this.center.offsetY = (float)((double)this.center.offsetY - 0.1);
               this.rightWing.offsetY = (float)((double)this.rightWing.offsetY - 0.1);
               this.leftWing.offsetY = (float)((double)this.leftWing.offsetY - 0.1);
               this.center.rotateAngleX = 0.5F;
               this.rightWing.rotateAngleX = 0.5F;
               this.leftWing.rotateAngleX = 0.5F;
         }
      }
   }
}
