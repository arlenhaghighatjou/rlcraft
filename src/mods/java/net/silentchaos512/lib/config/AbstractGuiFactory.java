package net.silentchaos512.lib.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;

public abstract class AbstractGuiFactory implements IModGuiFactory {
   public AbstractGuiFactory() {
   }

   public abstract ConfigBase getConfig();

   public abstract String getModId();

   public abstract String getGuiTitle();

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new AbstractGuiFactory.BasicGuiConfig(parentScreen, this);
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }

   public static class BasicGuiConfig extends GuiConfig {
      public BasicGuiConfig(GuiScreen parentScreen, AbstractGuiFactory factory) {
         super(parentScreen, getAllElements(factory), factory.getModId(), false, false, factory.getGuiTitle());
      }

      public static List<IConfigElement> getAllElements(AbstractGuiFactory factory) {
         ConfigBase config = factory.getConfig();
         List<IConfigElement> list = new ArrayList();

         for(String cat : config.getConfiguration().getCategoryNames()) {
            if (!cat.contains(".")) {
               List<IConfigElement> elements = (new ConfigElement(config.getCategory(cat))).getChildElements();
               String langKey = "config." + factory.getModId() + ":" + cat;
               list.add(new DummyCategoryElement(cat, langKey, elements));
            }
         }

         return list;
      }
   }
}
