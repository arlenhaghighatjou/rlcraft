package gigaherz.toolbelt.client.config;

import com.google.common.collect.Lists;
import gigaherz.toolbelt.Config;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigGui extends GuiConfig {
   public ConfigGui(GuiScreen parent) {
      super(parent, getConfigElements(), "toolbelt", false, false, getTitle());
   }

   private static String getTitle() {
      return I18n.format("text.toolbelt.name", new Object[0]);
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> configElements = Lists.newArrayList();
      configElements.addAll((new ConfigElement(Config.display)).getChildElements());
      configElements.addAll((new ConfigElement(Config.input)).getChildElements());
      configElements.addAll((new ConfigElement(Config.behaviour)).getChildElements());
      return configElements;
   }
}
