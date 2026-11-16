package ic2.api.energy.prefab;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Replacement for the IC2 1.7.10 BasicSource. Instead of the old IC2 energy net it pushes energy
 * to adjacent Redstone Flux receivers; MFCL maps those to Forge Energy blocks of the 1.20.1 world
 * (IndustrialCraft 2 port cables and machines). 1 EU == 1 FE.
 * The owning tile should implement IEnergyProvider and delegate to {@link #extract}.
 */
public class BasicSource {
    public final TileEntity parent;
    protected double capacity;
    protected int tier;
    protected double energyStored;
    private long budgetTick = Long.MIN_VALUE;
    private double budgetUsed;

    public BasicSource(TileEntity parent, double capacity, int tier) {
        this.parent = parent;
        this.capacity = capacity;
        this.tier = tier;
    }

    public static double powerFromTier(int tier) {
        return 8.0 * Math.pow(4.0, Math.max(0, tier));
    }

    /** Pushes up to one packet (see remainingBudget) per tick to adjacent receivers. */
    public void updateEntity() {
        if (parent.getWorldObj() == null || parent.getWorldObj().isRemote || energyStored < 1) {
            return;
        }
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            double budget = remainingBudget();
            if (budget < 1 || energyStored < 1) {
                return;
            }
            TileEntity te = parent.getWorldObj().getTileEntity(parent.xCoord + dir.offsetX, parent.yCoord + dir.offsetY, parent.zCoord + dir.offsetZ);
            if (te instanceof IEnergyReceiver && te != parent) {
                IEnergyReceiver receiver = (IEnergyReceiver) te;
                ForgeDirection from = dir.getOpposite();
                if (receiver.canConnectEnergy(from)) {
                    int offer = (int) Math.min(Integer.MAX_VALUE, Math.min(budget, energyStored));
                    int sent = receiver.receiveEnergy(from, offer, false);
                    if (sent > 0) {
                        consume(sent);
                    }
                }
            }
        }
    }

    /** Called by IEnergyProvider#extractEnergy of the owning tile (pull from a 1.20.1 cable). */
    public int extract(int maxExtract, boolean simulate) {
        int amount = (int) Math.min(maxExtract, Math.min(energyStored, remainingBudget()));
        if (amount <= 0) {
            return 0;
        }
        if (!simulate) {
            consume(amount);
        }
        return amount;
    }

    /**
     * Energy that may leave per tick. The IndustrialCraft 2 port derives the voltage tier of a
     * Forge Energy source from how much it offers at once, so offering the full tier (2048 EU for
     * tier 4) made low-voltage machines explode. The packet now follows the real production:
     * the smallest IC2 tier that covers twice the average output (at least LV, 32 EU), never more
     * than the tier given in the constructor.
     */
    private double remainingBudget() {
        long now = parent.getWorldObj() == null ? 0 : parent.getWorldObj().getTotalWorldTime();
        if (now != budgetTick) {
            long elapsed = budgetTick == Long.MIN_VALUE ? 1 : Math.max(1, Math.min(200, now - budgetTick));
            for (long i = 0; i < elapsed; i++) {
                averageOutput = averageOutput * 0.95 + (i == 0 ? producedSinceTick : 0) * 0.05;
            }
            producedSinceTick = 0;
            budgetTick = now;
            budgetUsed = 0;
        }
        return Math.max(0, packetLimit() - budgetUsed);
    }

    private double packetLimit() {
        double wanted = Math.max(1.0, averageOutput * 2.0);
        int needed = Math.max(1, (int) Math.ceil(Math.log(wanted / 8.0) / Math.log(4.0)));
        return powerFromTier(Math.min(tier, needed));
    }

    private double averageOutput;
    private double producedSinceTick;

    private void consume(double amount) {
        remainingBudget();
        energyStored = Math.max(0, energyStored - amount);
        budgetUsed += amount;
    }

    public double addEnergy(double amount) {
        if (amount <= 0) {
            return 0;
        }
        double added = Math.min(amount, capacity - energyStored);
        if (added > 0) {
            energyStored += added;
        }
        producedSinceTick += amount; // production rate counts even when the buffer is full
        return Math.max(0, added);
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

    public double getOfferedEnergy() {
        return Math.min(energyStored, powerFromTier(tier));
    }

    public void onChunkUnload() {
    }

    public void invalidate() {
    }

    public void onLoaded() {
    }

    public void readFromNBT(NBTTagCompound tag) {
        NBTTagCompound data = tag.getCompoundTag("IC2BasicSource");
        energyStored = data.getDouble("energy");
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagCompound data = new NBTTagCompound();
        data.setDouble("energy", energyStored);
        tag.setTag("IC2BasicSource", data);
    }
}
