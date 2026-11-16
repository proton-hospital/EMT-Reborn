package ic2.api.item;

/**
 * Minimal re-implementation of the IC2 1.7.10 API entry point.
 * EMT Reborn supplies its own NBT based manager (see emt.energy.NbtElectricManager),
 * because the real IndustrialCraft 2 runs natively on Minecraft 1.20.1.
 */
public final class ElectricItem {
    public static IElectricItemManager manager;
    public static IElectricItemManager rawManager;

    private ElectricItem() {
    }
}
