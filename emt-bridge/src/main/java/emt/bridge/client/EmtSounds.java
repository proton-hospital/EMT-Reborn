package emt.bridge.client;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Sounds EMT 1.2.6.2 played through the IC2 1.7.10 audio manager, now using the sound events of
 * the IndustrialCraft 2 port: chainsaw idle loop while an EMT chainsaw is held (stop sound when
 * switching away) and the jetpack loop for the infused quantum chestplate in jetpack mode.
 */
public final class EmtSounds {
    private static final Set<String> CHAINSAWS = Set.of("emt:diamond_chainsaw", "emt:thaumium_chainsaw", "emt:chainsaw_stream");
    private static final ResourceLocation IDLE = ResourceLocation.fromNamespaceAndPath("ic2", "tools.chainsaw.chainsawidle");
    private static final ResourceLocation STOP = ResourceLocation.fromNamespaceAndPath("ic2", "tools.chainsaw.chainsawstop");
    private static final ResourceLocation JETPACK = ResourceLocation.fromNamespaceAndPath("ic2", "tools.jetpack.jetpackloop");
    private static final Pattern WING = Pattern.compile("[{,]wing:(\\d+)b");

    private static Loop chainsaw;
    private static Loop jetpack;

    private EmtSounds() {
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.isPaused()) {
            return;
        }
        boolean holding = CHAINSAWS.contains(String.valueOf(ForgeRegistries.ITEMS.getKey(player.getMainHandItem().getItem())));
        if (holding && (chainsaw == null || chainsaw.isStopped())) {
            chainsaw = play(IDLE, player);
        } else if (!holding && chainsaw != null) {
            chainsaw.end();
            chainsaw = null;
            SoundEvent stop = ForgeRegistries.SOUND_EVENTS.getValue(STOP);
            if (stop != null) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(stop, 1.0f));
            }
        }

        boolean flying = jetpackMode(player.getItemBySlot(EquipmentSlot.CHEST)) && !player.onGround()
                && (mc.options.keyJump.isDown() || player.getDeltaMovement().y > 0);
        if (flying && (jetpack == null || jetpack.isStopped())) {
            jetpack = play(JETPACK, player);
        } else if (!flying && jetpack != null) {
            jetpack.end();
            jetpack = null;
        }
    }

    private static boolean jetpackMode(ItemStack chest) {
        if (chest.isEmpty() || !"emt:item_armor_quantum_chestplate".equals(String.valueOf(ForgeRegistries.ITEMS.getKey(chest.getItem())))) {
            return false;
        }
        CompoundTag legacy = chest.getTagElement("mfcl_legacy");
        if (legacy == null) {
            return false;
        }
        Matcher m = WING.matcher(legacy.getString("snbt"));
        return m.find() && "1".equals(m.group(1));
    }

    private static Loop play(ResourceLocation id, LocalPlayer player) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(id);
        if (sound == null) {
            return null;
        }
        Loop loop = new Loop(sound, player);
        Minecraft.getInstance().getSoundManager().play(loop);
        return loop;
    }

    /** Looping sound that follows the player until {@link #end()} is called. */
    private static final class Loop extends AbstractTickableSoundInstance {
        private final LocalPlayer player;

        Loop(SoundEvent sound, LocalPlayer player) {
            super(sound, SoundSource.PLAYERS, player.getRandom());
            this.player = player;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.7f;
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
        }

        void end() {
            stop();
        }

        @Override
        public void tick() {
            if (player.isRemoved()) {
                stop();
                return;
            }
            x = player.getX();
            y = player.getY();
            z = player.getZ();
        }
    }
}
