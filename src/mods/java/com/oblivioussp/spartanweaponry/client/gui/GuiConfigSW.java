package com.oblivioussp.spartanweaponry.client.gui;

import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigSW extends GuiConfig {
   public GuiConfigSW(GuiScreen guiScreen) {
      super(guiScreen, getConfigElements(), "spartanweaponry", false, true, "Spartan Weaponry Config", GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> elements = new ArrayList();

      for(int i = 0; i < ConfigHandler.categories.length; ++i) {
         elements.add(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.categories[i])));
      }

      return elements;
   }
}
