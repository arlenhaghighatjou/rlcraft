package tschipp.carryon.client.gui;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import tschipp.carryon.common.config.CarryOnConfig;

public class GuiConfigCarryOn extends GuiConfig {
   private static final String LANG_PREFIX = "carryon.category.";

   public GuiConfigCarryOn(GuiScreen parent) {
      super(parent, getConfigElements(), "carryon", false, false, "Carry On Configuration");
   }

   private static List<IConfigElement> getConfigElements() {
      Configuration configuration = CarryOnConfig.EventHandler.getConfiguration();
      ConfigCategory topLevelCategory = configuration.getCategory("general");
      topLevelCategory.getChildren().forEach((configCategory) -> {
         configCategory.setLanguageKey("carryon.category." + configCategory.getName());
      });
      return (new ConfigElement(topLevelCategory)).getChildElements();
   }

   public void initGui() {
      super.initGui();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void actionPerformed(GuiButton button) {
      super.actionPerformed(button);
   }
}
