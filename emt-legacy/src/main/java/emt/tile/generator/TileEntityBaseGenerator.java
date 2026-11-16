package emt.tile.generator;

import cofh.api.energy.IEnergyProvider;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import emt.tile.TileEntityEMT;
import ic2.api.energy.prefab.BasicSource;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;

public abstract class TileEntityBaseGenerator extends TileEntityEMT implements IEnergyProvider {
   public BasicSource energySource = new BasicSource(this, 1.0E9, Math.max(1, Math.min(3, emt.util.EMTConfigHandler.generatorMaxTier)));
   public Aspect aspect;
   public double output;
   public int tick = 0;
   public boolean isActive = false;
   Random rnd = new Random();

   public TileEntityBaseGenerator() {
      this.output = 2000.0;
   }

   public void updateEntity() {
      if (this.tick > 0) {
         this.tick--;
      }

      this.energySource.updateEntity(); // push every tick (was once per second)
      if (this.tick == 0) {
         this.isActive = false;
         this.createEnergy();
         this.tick = 20;
      }
   }

   public void createEnergy() {
      int count = 0;

      for (int x = this.xCoord - 4; x < this.xCoord + 4; x++) {
         for (int y = this.yCoord - 4; y < this.yCoord + 4; y++) {
            for (int z = this.zCoord - 4; z < this.zCoord + 4; z++) {
               TileEntity tile = this.worldObj.getTileEntity(x, y, z);
               if (tile != null && tile instanceof IAspectSource) {
                  IAspectSource as = (IAspectSource)tile;
                  if (as.doesContainerContainAmount(this.aspect, 1) && as.takeFromContainer(this.aspect, 1)) {
                     this.isActive = true;
                     SimpleNetworkWrapper var10000 = PacketHandler.INSTANCE;
                     PacketFXEssentiaSource var10001 = new PacketFXEssentiaSource(
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        (byte)(this.xCoord - x),
                        (byte)(this.yCoord - y),
                        (byte)(this.zCoord - z),
                        this.aspect.getColor()
                     );
                     double var10005 = (double)this.xCoord;
                     double var10006 = (double)this.yCoord;
                     double var10007 = (double)this.zCoord;
                     var10000.sendToAllAround(var10001, new TargetPoint(this.getWorldObj().provider.dimensionId, var10005, var10006, var10007, 32.0));
                     this.energySource.addEnergy(this.output / 30.0);
                  }
               }
            }
         }
      }
   }

   public void onChunkUnload() {
      this.energySource.onChunkUnload();
   }

   public void invalidate() {
      this.energySource.invalidate();
      super.invalidate();
   }

   @Override
   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      this.energySource.readFromNBT(tag);
   }

   @Override
   public void writeToNBT(NBTTagCompound tag) {
      super.writeToNBT(tag);
      this.energySource.writeToNBT(tag);
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
