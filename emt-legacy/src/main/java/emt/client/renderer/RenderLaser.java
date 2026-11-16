package emt.client.renderer;

import emt.entity.EntityLaser;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderLaser extends Render {
   private static final ResourceLocation laserTexture = new ResourceLocation("emt", "textures/models/lasermodel.png");

   public void doRender(Entity entityLaser, double x, double y, double z, float par8, float pTick) {
      this.bindEntityTexture(entityLaser);
      GL11.glPushMatrix();
      GL11.glTranslated(x, y, z);
      GL11.glRotatef(entityLaser.rotationYaw - 90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(entityLaser.rotationPitch, 0.0F, 0.0F, 1.0F);
      Tessellator tess = Tessellator.instance;
      byte b0 = 0;
      float f2 = 0.4F;
      float f3 = 0.7F;
      float f4 = (float)(0 + b0 * 10) / 32.0F;
      float f5 = (float)(5 + b0 * 10) / 32.0F;
      float f10 = 0.05625F;
      GL11.glEnable(32826);
      GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(f10, f10, f10);
      GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
      GL11.glPushAttrib(2896);
      GL11.glDisable(2896);

      for (int i = 0; i < 4; i++) {
         GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glNormal3f(0.0F, 0.0F, f10);
         tess.startDrawingQuads();
         tess.addVertexWithUV(-8.0, 0.0, 0.0, (double)f2, (double)f4);
         tess.addVertexWithUV(8.0, 0.0, 0.0, (double)f3, (double)f4);
         tess.addVertexWithUV(8.0, 4.0, 0.0, (double)f3, (double)f5);
         tess.addVertexWithUV(-8.0, 4.0, 0.0, (double)f2, (double)f5);
         tess.draw();
         tess.startDrawingQuads();
         tess.addVertexWithUV(8.0, 0.0, 0.0, (double)f2, (double)f4);
         tess.addVertexWithUV(-8.0, 0.0, 0.0, (double)f3, (double)f4);
         tess.addVertexWithUV(-8.0, 4.0, 0.0, (double)f3, (double)f5);
         tess.addVertexWithUV(8.0, 4.0, 0.0, (double)f2, (double)f5);
         tess.draw();
      }

      GL11.glPopAttrib();
      GL11.glDisable(32826);
      GL11.glPopMatrix();
   }

   protected ResourceLocation getArrowTextures(EntityLaser entityLaser) {
      return laserTexture;
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return this.getArrowTextures((EntityLaser)entity);
   }
}
