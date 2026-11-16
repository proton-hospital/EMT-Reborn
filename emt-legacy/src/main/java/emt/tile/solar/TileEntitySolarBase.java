package emt.tile.solar;

import cofh.api.energy.IEnergyProvider;
import net.minecraftforge.common.util.ForgeDirection;

import emt.tile.TileEntityEMT;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.tile.IWrenchable;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntitySolarBase extends TileEntityEMT implements IWrenchable, IEnergyProvider {
   public BasicSource energySource = new BasicSource(this, 10000.0, 4);
   public static Random random = new Random();
   public boolean initialized;
   public boolean theSunIsVisible;
   public boolean canRain;
   public boolean noSunlight;
   public int tick;
   public double output = 0.0;

   public TileEntitySolarBase() {
      this.tick = random.nextInt(64);
   }

   public void updateEntity() {
      this.energySource.updateEntity();
      this.checkConditions();
   }

   public void checkConditions() {
      if (!this.initialized && this.worldObj != null) {
         this.canRain = emt.compat.EMTPlatform.biome(this.worldObj, this.xCoord, this.zCoord).getIntRainfall() > 0;
         this.noSunlight = this.worldObj.provider.hasNoSky;
         this.initialized = true;
      }

      if (!this.noSunlight) {
         if (this.tick-- == 0) {
            this.updateSunState();
            this.tick = 64;
         }

         this.createEnergy();
      }
   }

   public void createEnergy() {
      if (this.theSunIsVisible) {
         this.energySource.addEnergy(this.output);
      }
   }

   public void updateSunState() {
      boolean isRaining = this.canRain && (this.worldObj.isRaining() || this.worldObj.isThundering());
      this.theSunIsVisible = this.worldObj.isDaytime()
         && !isRaining
         && this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord);
   }

   public void onChunkUnload() {
      this.energySource.onChunkUnload();
   }

   public void invalidate() {
      this.energySource.invalidate();
      super.invalidate();
   }

   @Override
   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      this.energySource.writeToNBT(nbttagcompound);
   }

   @Override
   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.energySource.readFromNBT(nbttagcompound);
   }

   public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
      return false;
   }

   public short getFacing() {
      return 0;
   }

   public void setFacing(short facing) {
   }

   public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
      return true;
   }

   public float getWrenchDropRate() {
      return 1.0F;
   }
   // ---- Redstone Flux (MFCL maps it to Forge Energy for IC2 cables), 1 RF == 1 EU ----
   public boolean canConnectEnergy(ForgeDirection from) {
      return true;
   }

   public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
      return this.energySource.extract(maxExtract, simulate);
   }

   public int getEnergyStored(ForgeDirection from) {
      return (int)Math.min(Integer.MAX_VALUE, this.energySource.getEnergyStored());
   }

   public int getMaxEnergyStored(ForgeDirection from) {
      return (int)Math.min(Integer.MAX_VALUE, this.energySource.getCapacity());
   }
}
