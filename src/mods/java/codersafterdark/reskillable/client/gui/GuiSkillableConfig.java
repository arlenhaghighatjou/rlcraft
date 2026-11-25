package codersafterdark.reskillable.client.gui;

import codersafterdark.reskillable.base.ConfigHandler;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;

public class GuiSkillableConfig extends GuiConfig {
   public GuiSkillableConfig(GuiScreen parentScreen) {
      super(parentScreen, getAllElements(), "reskillable", false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
   }

   public static List<IConfigElement> getAllElements() {
      return (List)ConfigHandler.config.getCategoryNames().stream().filter((s) -> {
         return !s.contains(".");
      }).map((s) -> {
         return new DummyCategoryElement(s, s, (new ConfigElement(ConfigHandler.config.getCategory(s))).getChildElements());
      }).collect(Collectors.toList());
   }
}
