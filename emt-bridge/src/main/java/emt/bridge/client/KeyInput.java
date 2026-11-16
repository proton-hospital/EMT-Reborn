package emt.bridge.client;

import com.mojang.blaze3d.platform.InputConstants;
import emt.bridge.KeyboardAccess;
import emt.bridge.Keys;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

/** Reads EMT / IC2-style keys on the client and sends them to the legacy EMT code. */
public final class KeyInput {
    private static final String CATEGORY = "key.categories.emtbridge";
    public static final KeyMapping MODE_SWITCH = new KeyMapping("key.emtbridge.mode_switch", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY);
    public static final KeyMapping BOOST = new KeyMapping("key.emtbridge.boost", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY);
    public static final KeyMapping ALT = new KeyMapping("key.emtbridge.alt", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);
    public static final KeyMapping UNEQUIP = new KeyMapping("key.emtbridge.unequip", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);

    private static int last = -1;
    private static int resend;

    private KeyInput() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(MODE_SWITCH);
        event.register(BOOST);
        event.register(ALT);
        event.register(UNEQUIP);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            last = -1;
            return;
        }
        int mask = 0;
        if (mc.screen == null) {
            Options o = mc.options;
            if (o.keyJump.isDown()) mask |= KeyboardAccess.JUMP;
            if (o.keyShift.isDown()) mask |= KeyboardAccess.SNEAK;
            if (o.keyUp.isDown()) mask |= KeyboardAccess.FORWARD;
            if (MODE_SWITCH.isDown()) mask |= KeyboardAccess.MODE_SWITCH;
            if (BOOST.isDown()) mask |= KeyboardAccess.BOOST;
            if (ALT.isDown()) mask |= KeyboardAccess.ALT;
            if (UNEQUIP.isDown()) mask |= KeyboardAccess.UNEQUIP;
        }
        // Ctrl+M (boost + mode switch) toggles the speed of EMT Boots of the Traveller, as in 1.2.6.2
        boolean combo = (mask & KeyboardAccess.BOOST) != 0 && (mask & KeyboardAccess.MODE_SWITCH) != 0;
        boolean comboBefore = last >= 0 && (last & KeyboardAccess.BOOST) != 0 && (last & KeyboardAccess.MODE_SWITCH) != 0;
        if (combo && !comboBefore && emt.bridge.Boots.isWearing(mc.player)) {
            boolean on = emt.bridge.Boots.toggleBoost(mc.player);
            mc.player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    on ? "message.emtbridge.boots_speed_on" : "message.emtbridge.boots_speed_off"), true);
        }
        // send on change, and once a second anyway (cheap; fixes a lost packet)
        if (mask != last || ++resend >= 20) {
            resend = 0;
            last = mask;
            Keys.push(mc.player.getUUID(), mask); // client-side legacy code (rendering, prediction)
            Keys.sendToServer(mask);
        }
    }
}
