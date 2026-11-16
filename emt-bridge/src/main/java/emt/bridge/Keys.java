package emt.bridge;

import github.com.gengyoubo.mfcl.api.LegacyRuntime;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Key state transport: IC2 1.7.10 used to sync "mode switch", "boost", jump, etc. to the server.
 * The 1.20.1 client reads the keys (see client.KeyInput) and this class hands them to the legacy
 * emt.compat.EMTKeyboard on both sides.
 */
public final class Keys {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(EmtBridge.MOD_ID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private static KeyboardAccess access;
    private static final java.util.Map<UUID, Integer> MASKS = new java.util.concurrent.ConcurrentHashMap<>();

    /** Last key mask the player's client sent (server side). */
    public static int mask(UUID player) {
        return MASKS.getOrDefault(player, 0);
    }
    private static boolean failed;

    private Keys() {
    }

    static void register() {
        CHANNEL.messageBuilder(KeyState.class, 0)
                .encoder(KeyState::encode)
                .decoder(KeyState::decode)
                .consumerMainThread(KeyState::handle)
                .add();
    }

    public static void sendToServer(int mask) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), new KeyState(mask));
    }

    /** Stores the mask in the legacy runtime; safe to call before it is ready. */
    public static void push(UUID player, int mask) {
        LegacyRuntime runtime = LegacyItems.runtime().orElse(null);
        if (runtime == null) {
            return;
        }
        try {
            synchronized (Keys.class) {
                if (access == null && !failed) {
                    access = runtime.accessor(KeyboardAccess.class);
                }
            }
            if (access != null) {
                access.setState(player.toString(), mask);
            }
        } catch (RuntimeException e) {
            failed = true;
            EmtBridge.warnOnce("keys", "Legacy EMT keyboard is unavailable, EMT key bindings will not work", e);
        }
    }

    public record KeyState(int mask) {
        static void encode(KeyState msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.mask);
        }

        static KeyState decode(FriendlyByteBuf buf) {
            return new KeyState(buf.readVarInt());
        }

        static void handle(KeyState msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                MASKS.put(player.getUUID(), msg.mask & 0x7F);
                push(player.getUUID(), msg.mask & 0x7F);
            }
            ctx.get().setPacketHandled(true);
        }
    }
}
