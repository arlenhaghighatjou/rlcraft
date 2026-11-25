package ladysnake.spawnercontrol.client;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import ladysnake.spawnercontrol.config.CustomSpawnersConfig;
import ladysnake.spawnercontrol.config.SpawnerConfigHolder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;

class MSCGuiConfig extends GuiConfig {
   MSCGuiConfig(GuiScreen parentScreen, String modId, String title) {
      super(parentScreen, modId, title);
      List<IConfigElement> elements = (List)CustomSpawnersConfig.getCustomSpawnerConfigs().stream().map(SpawnerConfigHolder::getConfigCategory).map((category) -> {
         return new DummyCategoryElement(category.getName(), category.getLanguagekey(), (new ConfigElement(category)).getChildElements());
      }).collect(Collectors.toList());
      this.configElements.add(new DummyCategoryElement("custom spawners", "spawnercontrol.general.custom_spawners", elements));
      this.configElements.sort(Comparator.comparing(IConfigElement::isProperty));
   }
}
