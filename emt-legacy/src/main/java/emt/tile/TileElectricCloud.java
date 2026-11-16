package emt.tile;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXWisp;
import thaumcraft.common.Thaumcraft;

public class TileElectricCloud extends TileEntity {
   public boolean canUpdate() {
      return true;
   }

   public void updateEntity() {
      if (this.worldObj.isRemote
         && FMLCommonHandler.instance().getSide().isClient()
         && FMLClientHandler.instance().getClient().renderViewEntity != null) {
         this.fx();
      }
   }

   @SideOnly(Side.CLIENT)
   public void fx() {
      if (this.worldObj.rand.nextInt(4) == 0) {
         Thaumcraft.proxy
            .wispFX3(
               this.worldObj,
               (double)((float)this.xCoord + 0.5F),
               (double)((float)this.yCoord + 0.5F),
               (double)((float)this.zCoord + 0.5F),
               (double)((float)this.xCoord + 0.4F + this.worldObj.rand.nextFloat() * 0.2F),
               (double)((float)this.yCoord + 0.5F),
               (double)((float)this.zCoord + 0.4F + this.worldObj.rand.nextFloat() * 0.2F),
               0.25F,
               1,
               true,
               -0.02F
            );
      }

      if (this.worldObj.rand.nextInt(12) == 0) {
         Thaumcraft.proxy
            .nodeBolt(
               this.worldObj,
               (float)this.xCoord + 0.5F,
               (float)this.yCoord + 0.5F,
               (float)this.zCoord + 0.5F,
               (float)this.xCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F,
               (float)this.yCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F,
               (float)this.zCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F
            );
         if (this.worldObj.rand.nextInt(2) == 0) {
            Thaumcraft.proxy
               .nodeBolt(
                  this.worldObj,
                  (float)this.xCoord + 0.5F,
                  (float)this.yCoord + 0.5F,
                  (float)this.zCoord + 0.5F,
                  (float)this.xCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F,
                  (float)this.yCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F,
                  (float)this.zCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F
               );
         }

         if (this.worldObj.rand.nextInt(4) == 0) {
            Thaumcraft.proxy
               .nodeBolt(
                  this.worldObj,
                  (float)this.xCoord + 0.5F,
                  (float)this.yCoord + 0.5F,
                  (float)this.zCoord + 0.5F,
                  (float)this.xCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F,
                  (float)this.yCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F,
                  (float)this.zCoord + (float)this.worldObj.rand.nextInt(40) / 20.0F - 0.5F
               );
         }
      }

      if (this.worldObj.rand.nextInt(3) == 0) {
         FXWisp wisp = new FXWisp(
            this.worldObj,
            (double)this.xCoord + 0.5 + (double)((float)this.worldObj.rand.nextInt(40) / 160.0F - 0.125F),
            (double)this.yCoord + 0.5,
            (double)this.zCoord + 0.5 + (double)((float)this.worldObj.rand.nextInt(40) / 160.0F - 0.125F),
            0.4F + (float)this.worldObj.rand.nextInt(40) / 100.0F,
            1
         );
         wisp.setGravity(-0.02F);
         wisp.setRBGColorF((float)this.worldObj.rand.nextInt(40) / 100.0F, 0.0F, 1.0F);
         wisp.setAlphaF(1.0F);
         ParticleEngine.instance.addEffect(this.worldObj, wisp);
      }
   }
}
