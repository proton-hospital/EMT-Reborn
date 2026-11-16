package ic2.api.energy.prefab;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Replacement for the IC2 1.7.10 BasicSink. Energy arrives through IEnergyReceiver#receiveEnergy
 * of the owning tile (MFCL maps it to Forge Energy, so IC2 port cables can feed it).
 * 1 EU == 1 FE.
 */
public class BasicSink {
    public final TileEntity parent;
    protected double capacity;
    protected int tier;
    protected double energyStored;

    public BasicSink(TileEntity parent, double capacity, int tier) {
        this.parent = parent;
        this.capacity = capacity;
        this.tier = tier;
    }

    public void updateEntity() {
    }

    /** Called by IEnergyReceiver#receiveEnergy of the owning tile. */
    public int receive(int maxReceive, boolean simulate) {
        int amount = (int) Math.min(maxReceive, Math.floor(capacity - energyStored));
        if (amount <= 0) {
            return 0;
        }
        if (!simulate) {
            energyStored += amount;
        }
        return amount;
    }

    public boolean canUseEnergy(double amount) {
        return energyStored >= amount;
    }

    public boolean useEnergy(double amount) {
        if (energyStored >= amount) {
            energyStored -= amount;
            return true;
        }
        return false;
    }

    public double getEnergyStored() {
        return energyStored;
    }

    public void setEnergyStored(double value) {
        energyStored = Math.max(0, Math.min(capacity, value));
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public double getDemandedEnergy() {
        return Math.max(0, capacity - energyStored);
    }

    public void onChunkUnload() {
    }

    public void invalidate() {
    }

    public void onLoaded() {
    }

    public void readFromNBT(NBTTagCompound tag) {
        NBTTagCompound data = tag.getCompoundTag("IC2BasicSink");
        energyStored = data.getDouble("energy");
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagCompound data = new NBTTagCompound();
        data.setDouble("energy", energyStored);
        tag.setTag("IC2BasicSink", data);
    }
}
