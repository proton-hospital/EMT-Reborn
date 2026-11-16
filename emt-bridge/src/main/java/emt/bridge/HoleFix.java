package emt.bridge;

import github.com.gengyoubo.mfcl.api.LegacyRedirects;
import github.com.gengyoubo.mfcl.api.spi.LegacyRedirectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fix for the Thaumcraft 4 Portable Hole focus ("Червоточина") under MFCL.
 * When a hole closes, TileMemory.recreateTileEntity puts the old block back and then calls
 * markDirty() on itself, although it is no longer the tile at that position. MFCL rejects that
 * ("Legacy tile identity mismatch"), rolls the whole tick back and pauses the logic of every
 * hole until the game is restarted, so holes never close. The call is harmless to skip.
 */
public final class HoleFix implements LegacyRedirectProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger("EMT Reborn bridge");

    @Override
    public void register(LegacyRedirects redirects) {
        redirects.redirect("thaumcraft.common.tiles.TileMemory", "thaumcraft.common.tiles.TileMemory", "func_70296_d", "()V", call -> {
            try {
                return call.proceed();
            } catch (IllegalStateException e) {
                if (String.valueOf(e.getMessage()).contains("identity mismatch")) {
                    return null; // the old block is already back in place
                }
                throw e;
            }
        });
        LOGGER.info("Registered Thaumcraft portable hole fix");
    }
}
