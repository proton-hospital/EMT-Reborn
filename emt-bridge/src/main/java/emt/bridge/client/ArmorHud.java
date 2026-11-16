package emt.bridge.client;

import com.mojang.blaze3d.systems.RenderSystem;
import emt.bridge.EmtBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

/**
 * Draws the armour bar including the points of EMT electric armour (ISpecialArmor#getArmorDisplay),
 * exactly as Forge 1.7.10 did. Vanilla's bar only knows the attribute value, which is 0 for them.
 */
public final class ArmorHud {
    private static final ResourceLocation ICONS = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");

    private ArmorHud() {
    }

    public static void onOverlay(RenderGuiOverlayEvent.Pre event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.ARMOR_LEVEL.id())) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !(mc.gui instanceof ForgeGui gui) || !gui.shouldDrawSurvivalElements()) {
            return;
        }
        int extra = EmtBridge.armorDisplayPoints(player);
        if (extra <= 0) {
            return;
        }
        event.setCanceled(true);
        render(gui, event.getGuiGraphics(), Math.min(20, player.getArmorValue() + extra),
                event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
    }

    private static void render(ForgeGui gui, GuiGraphics graphics, int level, int width, int height) {
        RenderSystem.enableBlend();
        int left = width / 2 - 91;
        int top = height - gui.leftHeight;
        for (int i = 1; level > 0 && i < 20; i += 2) {
            if (i < level) {
                graphics.blit(ICONS, left, top, 34, 9, 9, 9);
            } else if (i == level) {
                graphics.blit(ICONS, left, top, 25, 9, 9, 9);
            } else {
                graphics.blit(ICONS, left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        gui.leftHeight += 10;
        RenderSystem.disableBlend();
    }
}
