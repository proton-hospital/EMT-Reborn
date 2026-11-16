package emt.bridge;

import com.mojang.logging.LogUtils;
import github.com.gengyoubo.mfcl.api.LegacyEvents;
import github.com.gengyoubo.mfcl.api.MfclApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

/**
 * Electro-Magic Tools Reborn - 1.20.1 companion.
 * <ul>
 * <li>gives the mirror items of the legacy EMT mod (running in MFCL) a Forge Energy capability,
 * which the IndustrialCraft 2 port uses for charging and discharging;</li>
 * <li>emulates Forge 1.7.10 ISpecialArmor for EMT armour and draws its armour points;</li>
 * <li>sends IC2-style key states (mode switch, boost, jump...) to the legacy code.</li>
 * </ul>
 */
@Mod(EmtBridge.MOD_ID)
public final class EmtBridge {
    public static final String MOD_ID = "emtbridge";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation ENERGY_KEY = ResourceLocation.fromNamespaceAndPath(MOD_ID, "legacy_energy");
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();
    private static final List<AutoCloseable> FORWARDS = new ArrayList<>();

    public EmtBridge() {
        Keys.register();
        github.com.gengyoubo.mfcl.api.LegacyItemProviders.register(new EmtItemSplits());
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, EmtBridge::attach);
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> BridgeCommand.register(event.getDispatcher()));
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingHurtEvent.class, ArmorHandler::onHurt);
        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.server.ServerStartedEvent event) -> {
            LegacyItems.hostReady();
            IC2MachineRecipes.inject(event.getServer().getRecipeManager());
        });
        // /reload replaces the recipe sets: add EMT's entries again before they are sent to players
        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.OnDatapackSyncEvent event) ->
                IC2MachineRecipes.inject(event.getPlayerList().getServer().getRecipeManager()));
        MinecraftForge.EVENT_BUS.addListener(Boots::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(Boots::onJump);
        MinecraftForge.EVENT_BUS.addListener(NbtRepair::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(ChestParts::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(Spawns::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(Spawns::onJoin);
        MinecraftForge.EVENT_BUS.addListener(Spawns::onDeath);
        MinecraftForge.EVENT_BUS.addListener(Spawns::onDrops);
        MinecraftForge.EVENT_BUS.addListener(DungeonLoot::onLootLoad);
        MinecraftForge.EVENT_BUS.addListener(ArmorPower::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(ArmorPower::onFall);
        if (net.minecraftforge.fml.ModList.get().isLoaded("tcmodernlib")) {
            MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.server.ServerAboutToStartEvent event) -> {
                try {
                    IC2Aspects.install();
                } catch (RuntimeException | LinkageError e) {
                    LOGGER.warn("EMT: could not register Thaumcraft aspects for IndustrialCraft 2", e);
                }
            });
        }
        if (FMLEnvironment.dist == Dist.CLIENT) {
            emt.bridge.client.ClientSetup.init(FMLJavaModLoadingContext.get().getModEventBus());
        }
        // EMT armour and wings listen to these legacy Forge events (fall damage, living update...).
        MfclApi.whenReady(runtime -> {
            try {
                for (LegacyEvents.Kind kind : new LegacyEvents.Kind[]{LegacyEvents.Kind.LIVING_FALL,
                        LegacyEvents.Kind.LIVING_UPDATE, LegacyEvents.Kind.LIVING_HURT, LegacyEvents.Kind.BREAK_SPEED}) {
                    FORWARDS.add(runtime.events().forward(kind));
                }
            } catch (RuntimeException e) {
                LOGGER.warn("MFCL legacy event forwarding is unavailable", e);
            }
        });
    }

    private static void attach(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (LegacyItems.isEmtItem(stack.getItem())) {
            event.addCapability(ENERGY_KEY, new LegacyItemEnergy(stack));
        }
    }

    public static int armorDisplayPoints(LivingEntity entity) {
        return ArmorHandler.displayPoints(entity);
    }

    static void warnOnce(String code, String message, Throwable error) {
        if (WARNED.add(code)) {
            LOGGER.warn(message, error);
        }
    }
}
