package emt.bridge.client;

import emt.bridge.LegacyItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

/** Client-only registrations, kept in their own class so the dedicated server never loads them. */
public final class ClientSetup {
    private ClientSetup() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(KeyInput::register);
        modBus.addListener(WingsLayer::install);
        MinecraftForge.EVENT_BUS.addListener(KeyInput::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(EmtSounds::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(WingFlight::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(JetpackFlight::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, false, RenderGuiOverlayEvent.Pre.class, ArmorHud::onOverlay);
        MinecraftForge.EVENT_BUS.addListener(ClientSetup::tooltip);
        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingIn event) -> LegacyItems.hostReady());
    }

    /** IC2 1.7.10 printed "charge/max EU" under every electric item; do the same for EMT items. */
    private static void tooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !LegacyItems.isEmtItem(stack.getItem())) {
            return;
        }
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            List<Component> lines = event.getToolTip();
            int at = Math.min(1, lines.size());
            lines.add(at, Component.literal(energy.getEnergyStored() + " / " + energy.getMaxEnergyStored() + " EU")
                    .withStyle(ChatFormatting.GRAY));
        });
    }
}
