package emt.bridge;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Forge Energy view of a legacy EMT electric item. Every call is delegated to the legacy
 * emt.compat.FeBridge, the charge itself stays in the legacy "charge" NBT (1 FE == 1 EU).
 */
final class LegacyItemEnergy implements IEnergyStorage, ICapabilityProvider {
    private final ItemStack stack;
    private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> this);
    private Boolean electric;

    LegacyItemEnergy(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && isElectric()) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    private boolean isElectric() {
        if (electric == null) {
            Integer value = LegacyCall.on(stack, (b, s) -> b.isElectric(s) ? 1 : 0, false).orElse(null);
            if (value == null) {
                return false; // runtime not ready yet, ask again next time
            }
            electric = value != 0;
        }
        return electric;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        return LegacyCall.on(stack, (b, s) -> b.receive(s, maxReceive, simulate), !simulate).orElse(0);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) {
            return 0;
        }
        return LegacyCall.on(stack, (b, s) -> b.extract(s, maxExtract, simulate), !simulate).orElse(0);
    }

    @Override
    public int getEnergyStored() {
        return LegacyCall.intOn(stack, FeBridgeAccess::stored);
    }

    @Override
    public int getMaxEnergyStored() {
        return LegacyCall.intOn(stack, FeBridgeAccess::capacity);
    }

    @Override
    public boolean canExtract() {
        return LegacyCall.boolOn(stack, (b, s) -> b.canExtract(s) ? 1 : 0);
    }

    @Override
    public boolean canReceive() {
        return isElectric();
    }
}
