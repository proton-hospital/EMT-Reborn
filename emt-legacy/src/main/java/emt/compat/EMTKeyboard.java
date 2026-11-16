package emt.compat;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Key states of each player, replacing IC2's Keys/IC2.keyboard.
 * Keys are read by the 1.20.1 companion (emtbridge) and pushed here through MFCL with
 * {@link #setState}, on the client (for the local player) and on the server (from a packet).
 */
public final class EMTKeyboard {
    public static final int JUMP = 1;
    public static final int SNEAK = 1 << 1;
    public static final int FORWARD = 1 << 2;
    public static final int MODE_SWITCH = 1 << 3;
    public static final int BOOST = 1 << 4;
    public static final int ALT = 1 << 5;
    public static final int UNEQUIP = 1 << 6;

    private static final Map<String, Integer> STATES = new ConcurrentHashMap<String, Integer>();

    private EMTKeyboard() {
    }

    /** Called from emtbridge. Returns 1 so the bridge can confirm the call. */
    public static int setState(String uuid, int mask) {
        if (mask == 0) {
            STATES.remove(uuid);
        } else {
            STATES.put(uuid, mask);
        }
        return 1;
    }

    public static int getState(String uuid) {
        Integer value = STATES.get(uuid);
        return value == null ? 0 : value;
    }

    private static boolean down(EntityPlayer player, int bit) {
        return player != null && (getState(player.getUniqueID().toString()) & bit) != 0;
    }

    public static boolean isJumpKeyDown(EntityPlayer player) {
        return down(player, JUMP);
    }

    public static boolean isSneakKeyDown(EntityPlayer player) {
        return down(player, SNEAK) || (player != null && player.isSneaking());
    }

    public static boolean isForwardKeyDown(EntityPlayer player) {
        return down(player, FORWARD);
    }

    public static boolean isModeSwitchKeyDown(EntityPlayer player) {
        return down(player, MODE_SWITCH);
    }

    public static boolean isBoostKeyDown(EntityPlayer player) {
        return down(player, BOOST);
    }

    public static boolean isAltKeyDown(EntityPlayer player) {
        return down(player, ALT);
    }

    public static boolean isUnequipKeyDown(EntityPlayer player) {
        return down(player, UNEQUIP);
    }
}
