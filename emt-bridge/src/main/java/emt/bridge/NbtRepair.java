package emt.bridge;

import java.nio.charset.StandardCharsets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

/**
 * EMT Reborn 0.3.2-0.3.5 stored the jetpack attached to the infused quantum chestplate as a quoted
 * SNBT string ("jetpackModern"). The 1.7.10 NBT parser used by MFCL cannot read quotes inside a
 * string, so such a chestplate broke every legacy item use while worn and crashed the inventory
 * screen. This rewrites the value as hex ("jetpackHex"), which the legacy half reads since 0.3.6.
 */
public final class NbtRepair {

    private NbtRepair() {
    }

    private static final java.util.regex.Pattern KEY = java.util.regex.Pattern.compile("(jetpackModern|jetpackHex):\"");
    /** End of the value: a quote right after the closing brace of the item SNBT, followed by the next key or the end. */
    private static final java.util.regex.Pattern END = java.util.regex.Pattern.compile("(?<=[}])\"(?=,[A-Za-z_]+:|,?[}]$)");
    private static final java.util.regex.Pattern NEXT = java.util.regex.Pattern.compile("\"(,[A-Za-z_]+:|,?[}]$)");

    /** Returns true when the stack was changed. */
    public static boolean repair(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        CompoundTag legacy = stack.getTagElement("mfcl_legacy");
        if (legacy == null) {
            return false;
        }
        String fixed = repair(legacy.getString("snbt"));
        if (fixed == null) {
            return false;
        }
        legacy.putString("snbt", fixed);
        return true;
    }

    /**
     * Repairs the jetpack value of a legacy SNBT string, or returns null when it is fine.
     * MFCL writes legacy strings without escaping, so the old value looks like
     * jetpackModern:"{id:"ic2:...",tag:{...}}" (and 0.3.6 may have hexed only its first part).
     */
    static String repair(String snbt) {
        java.util.regex.Matcher key = KEY.matcher(snbt);
        if (!key.find()) {
            return null;
        }
        int start = key.end();
        int i = start;
        while (i < snbt.length() && Character.digit(snbt.charAt(i), 16) >= 0) {
            i++;
        }
        boolean hexKey = "jetpackHex".equals(key.group(1));
        String hexPart = hexKey ? snbt.substring(start, i) : "";
        int restStart = hexKey ? i : start;
        if (hexKey && NEXT.matcher(snbt).region(i, snbt.length()).lookingAt()) {
            return null; // already a clean hex value
        }
        java.util.regex.Matcher end = END.matcher(snbt);
        if (!end.find(restStart)) {
            return null;
        }
        String value = unhex(hexPart) + snbt.substring(restStart, end.start());
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        value = value.replace("\\\"", "\"");
        return snbt.substring(0, key.start()) + "jetpackHex:\"" + hex(value) + snbt.substring(end.start());
    }

    static String unhex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int k = 0; k < bytes.length; k++) {
            bytes[k] = (byte) Integer.parseInt(hex.substring(k * 2, k * 2 + 2), 16);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    static String hex(String text) {
        StringBuilder out = new StringBuilder();
        for (byte b : text.getBytes(StandardCharsets.UTF_8)) {
            out.append(Character.forDigit((b >> 4) & 15, 16)).append(Character.forDigit(b & 15, 16));
        }
        return out.toString();
    }

    public static void repair(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            repair(stack);
        }
        for (ItemStack stack : player.getInventory().armor) {
            repair(stack);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            repair(stack);
        }
    }

    /** Both sides: the server fixes the saved item, the client its copy so it cannot crash before the sync. */
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && (event.player.tickCount < 40 || event.player.tickCount % 10 == 0)) {
            repair(event.player);
        }
    }
}
