package emt;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import emt.command.CommandOutputs;
import emt.energy.NbtElectricManager;
import emt.init.EMTEntities;
import emt.init.EMTResearches;
import emt.init.Registry;
import emt.proxy.CommonProxy;
import emt.util.EMTConfigHandler;
import emt.util.EMTCreativeTab;
import emt.util.EMTDungeonChestGenerator;
import emt.util.EMTEssentiasOutputs;
import emt.util.EMTEventHandler;
import ic2.api.item.ElectricItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Electro-Magic Tools Reborn - legacy (1.7.10) half, based on Electro-Magic Tools 1.2.6.2
 * (Tombenpotter, TehNut, fewizz; GPLv3). Runs inside MFCL next to the original Thaumcraft 4.
 * IndustrialCraft 2 is the native 1.20.1 port: energy, keys and IC2 items are bridged by emtbridge.
 */
@Mod(modid = EMT.MOD_ID, name = EMT.NAME, version = EMT.VERSION, guiFactory = EMT.GUI_FACTORY,
        dependencies = "required-after:Thaumcraft")
public class EMT {
    public static final String NAME = "Electro-Magic Tools Reborn";
    public static final String MOD_ID = "EMT";
    public static final String VERSION = "1.0.0";
    public static final String TEXTURE_PATH = "emt";
    public static final String GUI_FACTORY = "emt.client.gui.config.EMTGuiFactory";
    public static final String CLIENT_PROXY = "emt.proxy.ClientProxy";
    public static final String COMMON_PROXY = "emt.proxy.CommonProxy";
    public static final String CHANNEL = "EMT";

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;
    public static final CreativeTabs TAB = new EMTCreativeTab("EMT.creativeTab");
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    @Instance(MOD_ID)
    public static EMT instance;

    static {
        // Must exist before any item is created (creative tab variants charge items).
        NbtElectricManager manager = new NbtElectricManager();
        ElectricItem.manager = manager;
        ElectricItem.rawManager = manager;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EMTConfigHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new EMTEventHandler());
        Registry.register();
        EMTEssentiasOutputs.addPrimalOutputs();
        EMTEssentiasOutputs.addOutputs();
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.load();
        MinecraftForge.EVENT_BUS.register(new EMTEventHandler());
        EMTDungeonChestGenerator.generateLoot();
        EMTEntities.registerEMTEntities();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Registry.registerLate();
        EMTResearches.register();
        LOGGER.info("EMT Reborn legacy content registered");
    }

    @EventHandler
    public void onFMLServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandOutputs());
    }
}
