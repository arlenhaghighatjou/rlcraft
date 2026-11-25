package vazkii.quark.base.client.gui.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import vazkii.quark.base.module.ModuleLoader;

public class GuiConfigCategory extends GuiConfig {
   public GuiConfigCategory(GuiScreen parentScreen, String baseCategory) {
      super(parentScreen, getAllElements(baseCategory), "quark", false, false, GuiConfig.getAbridgedConfigPath(ModuleLoader.config.toString()));
   }

   public static List<IConfigElement> getAllElements(String baseCategory) {
      return new ArrayList((new ConfigElement(ModuleLoader.config.getCategory(baseCategory))).getChildElements());
   }
}
