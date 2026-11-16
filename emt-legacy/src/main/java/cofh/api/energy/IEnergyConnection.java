package cofh.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Redstone Flux API (CoFH, 1.7.10), reproduced so that MFCL exposes EMT tiles as Forge Energy
 * blocks in the 1.20.1 world. Signatures must stay identical to the original API.
 */
public interface IEnergyConnection {
    boolean canConnectEnergy(ForgeDirection from);
}
