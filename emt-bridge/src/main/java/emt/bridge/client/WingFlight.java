package emt.bridge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The wing "flap" of EMT 1.2.6.2: hold jump up to 7 ticks, release and the player is thrown up
 * (motionY = power * ticks held). The legacy code does this on the logical server, but player
 * movement belongs to the client in 1.20.1, so the throw is applied here. Gliding (slower fall
 * while jump or sneak is held) stays in the legacy item and already works.
 */
public final class WingFlight {
    private static int held;

    private WingFlight() {
    }

    /** {power per tick held, horizontal divisor} of the worn wings, or null. */
    private static float[] params(ItemStack chest) {
        if (chest.isEmpty()) {
            return null;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(chest.getItem());
        if (id == null || !"emt".equals(id.getNamespace())) {
            return null;
        }
        switch (id.getPath()) {
            case "feather_wing": return new float[]{0.11f, 0.9f};
            case "thaumium_wing": return new float[]{0.15f, 0.7f};
            case "nanosuit_wing": return new float[]{0.25f, 0.6f};
            case "quantum_wing": return new float[]{0.33f, 0.5f};
            case "item_armor_quantum_chestplate":
                switch (WingsLayer.wingMode(chest)) {
                    case 2: return new float[]{0.15f, 0.7f};
                    case 3: return new float[]{0.25f, 0.6f};
                    case 4: return new float[]{0.33f, 0.5f};
                    default: return null;
                }
            default: return null;
        }
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.isPaused()) {
            return;
        }
        float[] wings = params(player.getItemBySlot(EquipmentSlot.CHEST));
        if (wings == null || player.getAbilities().flying || player.isPassenger()) {
            held = 0;
            return;
        }
        if (mc.options.keyJump.isDown()) {
            held = Math.min(7, held + 1);
            return;
        }
        if (held > 0) {
            Vec3 m = player.getDeltaMovement();
            double x = m.x;
            double z = m.z;
            if (Math.abs(x) < 0.5 && Math.abs(z) < 0.5) {
                x /= wings[1];
                z /= wings[1];
            }
            player.setDeltaMovement(x, wings[0] * held, z);
            player.fallDistance = 0;
            player.playSound(SoundEvents.GHAST_SHOOT, 0.6f, 1.0f);
            for (int i = 0; i < 4; i++) {
                player.level().addParticle(ParticleTypes.CLOUD,
                        player.getX() - 1 + player.getRandom().nextInt(100) / 50.0, player.getY() - 0.5,
                        player.getZ() - 1 + player.getRandom().nextInt(100) / 50.0, 0, -0.5, 0);
            }
            held = 0;
        }
    }
}
