package emt.bridge;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Energy costs and server effects of EMT armour that the legacy onArmorTick cannot apply under
 * MFCL (it keeps neither the item's NBT changes nor changes to air, fire or potion removal):
 * jetpack / wing energy use, quantum + solar helmet air refill and poison cure, solar helmet
 * charging, and fall protection of the traveller boots. Numbers are those of EMT 1.2.6.2.
 */
public final class ArmorPower {
    private static final int JUMP = 1;
    private static final Map<UUID, Integer> HELD = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> SOLAR = new ConcurrentHashMap<>();
    private static final List<Cure> CURES = List.of(
            new Cure(MobEffects.POISON, 10000), new Cure(MobEffects.WITHER, 15000), new Cure(MobEffects.CONFUSION, 5000));

    private record Cure(MobEffect effect, int cost) {
    }

    private ArmorPower() {
    }

    private static String id(ItemStack stack) {
        ResourceLocation key = stack.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key == null ? "" : key.toString();
    }

    private static boolean use(ItemStack stack, int amount) {
        return LegacyCall.on(stack, (b, s) -> b.use(s, amount), true).orElse(0) > 0;
    }

    private static int stored(ItemStack stack) {
        return LegacyCall.intOn(stack, FeBridgeAccess::stored);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player) || player.isSpectator()) {
            return;
        }
        chest(player);
        helmet(player);
    }

    // ---- infused quantum chestplate (jetpack, wings) and electric wings ----

    private static void chest(ServerPlayer player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        String id = id(chest);
        boolean jumping = (Keys.mask(player.getUUID()) & JUMP) != 0;
        int amount;
        switch (id) {
            case "emt:item_armor_quantum_chestplate" -> {
                player.clearFire();
                int wing = ChestParts.wingMode(chest);
                if (wing == 1) {
                    jetpack(player, chest, jumping);
                    return;
                }
                amount = wing == 3 ? 5 : wing == 4 ? 7 : 0;
            }
            case "emt:nanosuit_wing" -> amount = 5;
            case "emt:quantum_wing" -> amount = 7;
            default -> {
                HELD.remove(player.getUUID());
                return;
            }
        }
        // wing flap: hold jump up to 7 ticks, the cost is paid when it is released
        UUID uuid = player.getUUID();
        int held = HELD.getOrDefault(uuid, 0);
        if (jumping && !player.getAbilities().flying) {
            HELD.put(uuid, Math.min(7, held + 1));
        } else if (held > 0) {
            HELD.remove(uuid);
            float power = amount == 5 ? 0.25f : 0.33f;
            if (amount > 0) {
                LegacyCall.on(chest, (b, s) -> b.drain(s, Math.round(power * held * 10 * amount)), true);
            }
        }
    }

    private static void jetpack(ServerPlayer player, ItemStack chest, boolean jumping) {
        if (player.getAbilities().flying) {
            return;
        }
        String snbt = chest.getTagElement("mfcl_legacy") == null ? "" : chest.getTagElement("mfcl_legacy").getString("snbt");
        boolean hover = snbt.contains("hoverMode:1b");
        if (jumping || (hover && !player.onGround() && !player.isInWater())) {
            if (LegacyCall.on(chest, (b, s) -> b.drain(s, hover ? 10 : 8), true).orElse(0) > 0) {
                player.resetFallDistance(); // the thrust itself is applied by the client (JetpackFlight)
            }
        }
    }

    // ---- quantum goggles / solar helmet of revealing ----

    private static void helmet(ServerPlayer player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        String id = id(helmet);
        boolean solar = "emt:solar_helmet_revealing".equals(id);
        if (!solar && !"emt:quantum_goggles_revealing".equals(id)) {
            SOLAR.remove(player.getUUID());
            return;
        }
        if (player.getAirSupply() < 100 && stored(helmet) >= 1000 && use(helmet, 1000)) {
            player.setAirSupply(Math.min(player.getMaxAirSupply(), player.getAirSupply() + 200));
        }
        for (Cure cure : CURES) {
            MobEffectInstance active = player.getEffect(cure.effect());
            if (active != null && use(helmet, cure.cost() * (active.getAmplifier() + 1))) {
                player.removeEffect(cure.effect());
            }
        }
        if (solar && player.tickCount % 20 == 0) {
            solar(player, helmet);
        }
    }

    /** 256 EU/t in the sun, 128 EU/t under the open sky otherwise; paid out once a second. */
    private static void solar(ServerPlayer player, ItemStack helmet) {
        Level level = player.level();
        UUID uuid = player.getUUID();
        int rate = SOLAR.getOrDefault(uuid, -1);
        if (rate < 0 || player.tickCount % 120 == 0) {
            BlockPos head = BlockPos.containing(player.getX(), player.getY() + 1, player.getZ());
            boolean sky = level.dimensionType().hasSkyLight() && !level.dimensionType().hasCeiling() && level.canSeeSky(head);
            boolean wet = level.getBiome(head).value().getPrecipitationAt(head) != Biome.Precipitation.NONE;
            boolean sun = level.isDay() && !(wet && level.isRaining());
            rate = !sky ? 0 : sun ? 256 : 128;
            SOLAR.put(uuid, rate);
        }
        int left = rate * 20;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            if (left <= 0) {
                return;
            }
            left -= charge(player.getItemBySlot(slot), left);
        }
        for (ItemStack stack : player.getInventory().items) {
            if (left <= 0) {
                return;
            }
            left -= charge(stack, left);
        }
    }

    private static int charge(ItemStack stack, int amount) {
        if (stack.isEmpty()) {
            return 0;
        }
        return stack.getCapability(ForgeCapabilities.ENERGY).map(e -> e.canReceive() ? e.receiveEnergy(amount, false) : 0).orElse(0);
    }

    // ---- boots of the traveller: a landing costs energyPerDamage instead of fall damage ----

    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide || event.getDistance() <= 3.0f) {
            return;
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        int cost = switch (id(boots)) {
            case "emt:electric_boots_traveller" -> 100;
            case "emt:nano_boots_traveller" -> 500;
            case "emt:quantum_boots_traveller" -> 1000;
            default -> 0;
        };
        if (cost > 0 && use(boots, cost)) {
            event.setCanceled(true);
        }
    }
}
