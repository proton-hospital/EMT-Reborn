package emt.client.renderer;

import emt.entity.EntityShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderShield extends Render {
   private static final ResourceLocation shieldTexture = new ResourceLocation("emt", "textures/models/shield.png");
   static int glCallList = GLAllocation.generateDisplayLists(3);

   public void doRender(Entity entityShield, double x, double y, double z, float par8, float par9) {
      this.bindEntityTexture(entityShield);
      GL11.glEnable(3008);
      GL11.glAlphaFunc(516, 0.0F);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glPushAttrib(2896);
      GL11.glDisable(2896);
      GL11.glDisable(2884);
      GL11.glPushMatrix();
      if (((EntityShield)entityShield).owner != null
         && !Minecraft.getMinecraft().thePlayer.getDisplayName().equals(((EntityShield)entityShield).owner.getDisplayName())) {
         GL11.glTranslated(x, y, z);
      }

      GL11.glCallList(glCallList);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
      GL11.glDisable(32826);
   }

   protected ResourceLocation getEntityTexture(Entity texture) {
      return shieldTexture;
   }

   static {
      GL11.glPushMatrix();
      GL11.glNewList(glCallList, 4864);
      Tessellator tessellator = Tessellator.instance;
      float hsin1 = -1.0F;
      float hcos1 = 0.0F;
      tessellator.startDrawingQuads();

      for (float h = -90.0F; h < 90.0F; h += 15.0F) {
         float sin1 = 0.0F;
         float cos1 = 1.0F;
         float hsin2 = MathHelper.sin((float)Math.toRadians((double)(h + 15.0F)));
         float hcos2 = MathHelper.cos((float)Math.toRadians((double)(h + 15.0F)));

         for (float angle = 0.0F; angle < 360.0F; angle += 15.0F) {
            float sin2 = MathHelper.sin((float)Math.toRadians((double)(angle + 15.0F)));
            float cos2 = MathHelper.cos((float)Math.toRadians((double)(angle + 15.0F)));
            tessellator.addVertexWithUV((double)(sin1 * hcos1 * 2.0F), (double)(hsin1 * 2.0F), (double)(cos1 * hcos1 * 2.0F), 1.0, 1.0);
            tessellator.addVertexWithUV((double)(sin1 * hcos2 * 2.0F), (double)(hsin2 * 2.0F), (double)(cos1 * hcos2 * 2.0F), 1.0, 0.0);
            tessellator.addVertexWithUV((double)(sin2 * hcos2 * 2.0F), (double)(hsin2 * 2.0F), (double)(cos2 * hcos2 * 2.0F), 0.0, 0.0);
            tessellator.addVertexWithUV((double)(sin2 * hcos1 * 2.0F), (double)(hsin1 * 2.0F), (double)(cos2 * hcos1 * 2.0F), 0.0, 1.0);
            sin1 = sin2;
            cos1 = cos2;
         }

         hsin1 = hsin2;
         hcos1 = hcos2;
      }

      tessellator.draw();
      GL11.glEndList();
   }
}
