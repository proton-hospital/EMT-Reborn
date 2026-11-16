package emt.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWings extends ModelBiped {
   ModelRenderer rightWing;
   ModelRenderer leftWing;
   ModelRenderer center;
   public boolean isJumping = false;

   public ModelWings() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.rightWing = new ModelRenderer(this, 24, 0);
      this.rightWing.addBox(-11.0F, -1.0F, -1.5F, 11, 13, 1);
      this.rightWing.setRotationPoint(-2.0F, 0.0F, 3.0F);
      this.rightWing.setTextureSize(64, 32);
      this.rightWing.mirror = false;
      this.setRotation(this.rightWing, 0.0F, 0.1570796F, 0.0F);
      this.leftWing = new ModelRenderer(this, 0, 0);
      this.leftWing.addBox(0.0F, -1.0F, -1.5F, 11, 13, 1);
      this.leftWing.setRotationPoint(2.0F, 0.0F, 3.0F);
      this.leftWing.setTextureSize(64, 32);
      this.leftWing.mirror = true;
      this.setRotation(this.leftWing, 0.0F, -0.1570796F, 0.0F);
      this.center = new ModelRenderer(this, 0, 14);
      this.center.addBox(0.0F, 0.0F, -0.5F, 8, 14, 1);
      this.center.setRotationPoint(-4.0F, 0.0F, 3.0F);
      this.center.setTextureSize(64, 32);
      this.center.mirror = true;
      this.setRotation(this.center, 0.0F, 0.0F, 0.0F);
      this.bipedBody.addChild(this.rightWing);
      this.bipedBody.addChild(this.leftWing);
      this.bipedBody.addChild(this.center);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.rightWing.render(f5);
      this.leftWing.render(f5);
      this.center.render(f5);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void setRotationAngles(float f, float f1, float pt, float f3, float f4, float f5, Entity entity) {
      if (this.isJumping) {
         this.rightWing.rotateAngleY = 0.5F;
         this.leftWing.rotateAngleY = -0.5F;
      }

      if (entity.isSneaking()) {
         this.center.offsetY = (float)((double)this.center.offsetY - 0.1);
         this.rightWing.offsetY = (float)((double)this.rightWing.offsetY - 0.1);
         this.leftWing.offsetY = (float)((double)this.leftWing.offsetY - 0.1);
         this.center.rotateAngleX = 0.5F;
         this.rightWing.rotateAngleX = 0.5F;
         this.leftWing.rotateAngleX = 0.5F;
      }
   }
}
