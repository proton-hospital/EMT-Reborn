package emt.bridge.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Jetpack of the infused quantum chestplate (EMT 1.2.6.2 useJetpack), applied to the local player:
 * jump = thrust (max 0.6 up), forward = extra push, hover mode (jump + M) keeps a slow descent,
 * sneak descends faster. Runs here because movement belongs to the 1.20.1 client and the legacy
 * armour tick is fragile under MFCL; the energy (8 EU/t, 10 in hover) is taken by ArmorPower.
 */
public final class JetpackFlight {
    private static final Pattern CHARGE = Pattern.compile("[{,]charge:([0-9.Ee+-]+)d");
    private static final Pattern HOVER = Pattern.compile("[{,]hoverMode:1b");

    private JetpackFlight() {
    }

    static double charge(String snbt) {
        Matcher m = CHARGE.matcher(snbt);
        try {
            return m.find() ? Double.parseDouble(m.group(1)) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.isPaused() || player.getAbilities().flying || player.isPassenger()) {
            return;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ResourceLocation id = chest.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(chest.getItem());
        if (id == null || !"emt:item_armor_quantum_chestplate".equals(id.toString()) || WingsLayer.wingMode(chest) != 1) {
            return;
        }
        CompoundTag legacy = chest.getTagElement("mfcl_legacy");
        String snbt = legacy == null ? "" : legacy.getString("snbt");
        double charge = charge(snbt);
        boolean hover = HOVER.matcher(snbt).find();
        boolean jump = mc.options.keyJump.isDown();
        Vec3 m = player.getDeltaMovement();
        if (charge <= 0 || !(jump || (hover && m.y < -0.03))) {
            return;
        }
        float power = 1.0f;
        if (charge / 30000.0 <= 0.05) {
            power *= (float) (charge / 30000.0 * 0.05);
        }
        if (mc.options.keyUp.isDown()) {
            player.moveRelative(hover ? 0.008f : 0.02f, new Vec3(0, 0, 1));
            m = player.getDeltaMovement();
        }
        int height = player.level().getMaxBuildHeight();
        double y = Math.min(player.getY(), height);
        if (y > height - 25) {
            power *= (float) ((height - y) / 25.0);
        }
        double prev = m.y;
        double motionY = Math.min(m.y + power * 0.2f, 0.6f);
        if (hover) {
            double maxHover = player.isShiftKeyDown() ? -0.1 : jump ? 0.1 : -0.025;
            if (motionY > maxHover) {
                motionY = maxHover;
                if (prev > motionY) {
                    motionY = prev;
                }
            }
        }
        player.setDeltaMovement(m.x, motionY, m.z);
        player.fallDistance = 0;
    }
}
