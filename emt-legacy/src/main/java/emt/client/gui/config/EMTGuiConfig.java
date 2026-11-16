package emt.client.gui.config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import emt.util.EMTConfigHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigElement;

public class EMTGuiConfig extends GuiConfig {
   public EMTGuiConfig(GuiScreen parentScreen) {
      super(parentScreen, getConfigElements(parentScreen), "EMT", false, false, StatCollector.translateToLocal("gui.EMT.config.title"));
   }

   private static List<IConfigElement> getConfigElements(GuiScreen parent) {
      List<IConfigElement> list = new ArrayList<>();
      list.add(new ConfigElement(EMTConfigHandler.config.getCategory("Random Configs".toLowerCase(Locale.US))));
      list.add(new ConfigElement(EMTConfigHandler.config.getCategory("Numeric Values".toLowerCase(Locale.US))));
      list.add(new ConfigElement(EMTConfigHandler.config.getCategory("Research".toLowerCase(Locale.US))));
      list.add(new ConfigElement(EMTConfigHandler.config.getCategory("Essentia Generator Outputs".toLowerCase(Locale.US))));
      return list;
   }
}
