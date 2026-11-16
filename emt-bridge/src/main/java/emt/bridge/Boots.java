package emt.bridge;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * EMT Boots of the Traveller (electric / nano / quantum) applied to the 1.20.1 player.
 * Same behaviour as EMT 1.2.6.2: step height of one block while not sneaking, extra running
 * speed on the ground when the speed boost is on (Ctrl+M toggles it) and a jump boost.
 * The values (speedBonus / jumpBonus) are read from the legacy item, so EMT.cfg still applies.
 */
public final class Boots {
    private static final float DEFAULT_STEP = 0.6F;
    private static final float BOOTS_STEP = 1.0F;
    private static final Map<Item, float[]> VALUES = new IdentityHashMap<>();
    private static final Map<UUID, Boolean> STEPPED = new ConcurrentHashMap<>();
    /** Speed boost state per player; on by default (in 1.2.6.2 it started off, which confused players). */
    private static final Map<UUID, Boolean> BOOST = new ConcurrentHashMap<>();

    private Boots() {
    }

    /** {speedBonus, jumpBonus} or null when the item is not EMT traveller boots. */
    static float[] values(ItemStack stack) {
        if (stack.isEmpty() || !LegacyItems.isEmtItem(stack.getItem())) {
            return null;
        }
        synchronized (VALUES) {
            if (VALUES.containsKey(stack.getItem())) {
                return VALUES.get(stack.getItem());
            }
        }
        Integer speed = LegacyCall.on(stack, FeBridgeAccess::bootsSpeedMicros, false).orElse(null);
        Integer jump = LegacyCall.on(stack, FeBridgeAccess::bootsJumpMicros, false).orElse(null);
        if (speed == null || jump == null) {
            return null; // runtime not ready, ask again later
        }
        float[] result = speed < 0 ? null : new float[]{speed / 1_000_000f, jump / 1_000_000f};
        synchronized (VALUES) {
            VALUES.put(stack.getItem(), result);
        }
        return result;
    }

    static float[] worn(Player player) {
        return values(player.getItemBySlot(EquipmentSlot.FEET));
    }

    public static boolean isWearing(Player player) {
        return worn(player) != null;
    }

    public static boolean boostOn(Player player) {
        return BOOST.getOrDefault(player.getUUID(), true);
    }

    public static boolean toggleBoost(Player player) {
        boolean now = !boostOn(player);
        BOOST.put(player.getUUID(), now);
        return now;
    }

    /** Runs on both sides so the server agrees with the client about step height. */
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        Player player = event.player;
        float[] boots = worn(player);
        UUID id = player.getUUID();
        if (boots == null) {
            if (STEPPED.remove(id) != null) {
                player.setMaxUpStep(DEFAULT_STEP);
            }
            return;
        }
        boolean step = !player.isShiftKeyDown() && !player.getAbilities().flying;
        if (step) {
            player.setMaxUpStep(BOOTS_STEP);
            STEPPED.put(id, true);
        } else if (STEPPED.remove(id) != null) {
            player.setMaxUpStep(DEFAULT_STEP);
        }
        // movement is client authoritative: only the local player's client adds the speed
        if (player.level().isClientSide && player.isLocalPlayer() && boostOn(player) && !player.getAbilities().flying
                && player.zza > 0 && player.onGround() && !player.isShiftKeyDown()) {
            player.moveRelative(boots[0], new Vec3(0, 0, 1));
        }
    }

    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            float[] boots = worn(player);
            if (boots != null && boots[1] > 0) {
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x, motion.y + boots[1], motion.z);
            }
        }
    }
}
