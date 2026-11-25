package bettercombat.mod.client.gui;

import bettercombat.mod.util.ConfigurationHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModGuiConfig extends GuiConfig {
   public ModGuiConfig(GuiScreen guiScreen) {
      super(guiScreen, (new ConfigElement(ConfigurationHandler.config.getCategory("general"))).getChildElements(), "bettercombatmod", false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
   }
}
