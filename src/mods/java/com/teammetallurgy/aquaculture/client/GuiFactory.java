package com.teammetallurgy.aquaculture.client;

import com.teammetallurgy.aquaculture.handlers.Config;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;

public class GuiFactory extends DefaultGuiFactory {
   public GuiFactory() {
      super("aquaculture", GuiConfig.getAbridgedConfigPath(Config.config.toString()));
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new GuiConfig(parentScreen, getConfigElements(), "aquaculture", false, false, this.title);
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> list = new ArrayList();
      List<IConfigElement> basic = (new ConfigElement(Config.config.getCategory("BASIC_OPTIONS"))).getChildElements();
      List<IConfigElement> fishRarity = (new ConfigElement(Config.config.getCategory("FISH_RARITY"))).getChildElements();
      List<IConfigElement> junkRarity = (new ConfigElement(Config.config.getCategory("JUNK_RARITY"))).getChildElements();
      list.add(new DummyCategoryElement("Basic options", String.valueOf(new ResourceLocation("aquaculture", "config.category.basic")), basic));
      list.add(new DummyCategoryElement("Fish rarity", String.valueOf(new ResourceLocation("aquaculture", "config.category.fishRarity")), fishRarity));
      list.add(new DummyCategoryElement("Junk rarity", String.valueOf(new ResourceLocation("aquaculture", "config.category.junkRarity")), junkRarity));
      return list;
   }
}
