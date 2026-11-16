package emt.bridge;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Infused quantum chestplate of EMT 1.2.6.2: sneak with an IC2 electric jetpack or EMT wings in the
 * hand to attach them, hold the unequip key (Z) and sneak with an empty hand to take them off,
 * jump + mode switch (M) toggles the jetpack hover mode. Done here on the server because MFCL
 * neither shows the 1.20.1 hand to the legacy onArmorTick nor keeps its NBT changes.
 */
public final class ChestParts {
    private static final String CHEST = "emt:item_armor_quantum_chestplate";
    private static final Pattern WING = Pattern.compile("[{,]wing:(\\d+)b");
    private static final Map<UUID, Integer> COOLDOWN = new ConcurrentHashMap<>();
    private static final int JUMP = 1;
    private static final int MODE = 8;
    private static final int UNEQUIP = 64;

    private ChestParts() {
    }

    public static int wingMode(ItemStack chest) {
        CompoundTag legacy = chest.getTagElement("mfcl_legacy");
        if (legacy == null) {
            return 0;
        }
        Matcher m = WING.matcher(legacy.getString("snbt"));
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private static String id(ItemStack stack) {
        ResourceLocation key = stack.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key == null ? "" : key.toString();
    }

    private static int partOf(ItemStack hand) {
        return switch (id(hand)) {
            case "ic2:armorjetpackelectric" -> 1;
            case "emt:thaumium_wing" -> 2;
            case "emt:nanosuit_wing" -> 3;
            case "emt:quantum_wing" -> 4;
            default -> 0;
        };
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!CHEST.equals(id(chest))) {
            return;
        }
        UUID uuid = player.getUUID();
        int cooldown = COOLDOWN.getOrDefault(uuid, 0);
        if (cooldown > 0) {
            COOLDOWN.put(uuid, cooldown - 1);
            return;
        }
        int mask = Keys.mask(uuid);
        int wing = wingMode(chest);
        ItemStack hand = player.getMainHandItem();
        if ((mask & JUMP) != 0 && (mask & MODE) != 0) {
            int r = LegacyCall.on(chest, FeBridgeAccess::chestToggleHover, true).orElse(0);
            if (r > 0) {
                player.displayClientMessage(Component.translatable(r == 1 ? "message.emtbridge.hover_on" : "message.emtbridge.hover_off"), true);
                COOLDOWN.put(uuid, 30);
            }
        } else if (player.isShiftKeyDown() && wing == 0 && partOf(hand) > 0) {
            int part = partOf(hand);
            int charge = part >= 3 ? hand.getCapability(ForgeCapabilities.ENERGY).map(e -> e.getEnergyStored()).orElse(0) : 0;
            String jetpack = part == 1 ? hand.save(new CompoundTag()).toString() : "";
            int r = LegacyCall.on(chest, (b, s) -> b.chestAttach(s, part, charge, jetpack), true).orElse(0);
            if (r > 0) {
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                player.displayClientMessage(Component.translatable("message.emtbridge.attached." + part), true);
                COOLDOWN.put(uuid, 30);
            }
        } else if (player.isShiftKeyDown() && (mask & UNEQUIP) != 0 && hand.isEmpty() && wing > 0) {
            int charge = LegacyCall.intOn(chest, FeBridgeAccess::chestCharge);
            String jetpack = wing == 1 ? LegacyCall.obj(chest, FeBridgeAccess::chestJetpack).orElse("") : "";
            ItemStack back = partStack(wing, charge, jetpack);
            int r = LegacyCall.on(chest, FeBridgeAccess::chestDetach, true).orElse(0);
            if (r > 0) {
                player.setItemInHand(InteractionHand.MAIN_HAND, back);
                player.displayClientMessage(Component.translatable("message.emtbridge.detached." + wing), true);
                COOLDOWN.put(uuid, 30);
            }
        }
    }

    private static ItemStack partStack(int wing, int charge, String jetpack) {
        if (wing == 1) {
            try {
                if (!jetpack.isEmpty()) {
                    return ItemStack.of(TagParser.parseTag(jetpack));
                }
            } catch (Exception e) {
                EmtBridge.warnOnce("jetpack-restore", "Could not restore the jetpack stored in the chestplate", e);
            }
            return stack("ic2:armorjetpackelectric");
        }
        ItemStack stack = stack(wing == 2 ? "emt:thaumium_wing" : wing == 3 ? "emt:nanosuit_wing" : "emt:quantum_wing");
        if (charge > 0) {
            stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(e -> {
                int left = charge;
                for (int i = 0; i < 1000 && left > 0; i++) {
                    int got = e.receiveEnergy(left, false);
                    if (got <= 0) {
                        break;
                    }
                    left -= got;
                }
            });
        }
        return stack;
    }

    private static ItemStack stack(String id) {
        ResourceLocation key = ResourceLocation.tryParse(id);
        Item item = key == null ? null : ForgeRegistries.ITEMS.getValue(key);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}
