package pl.asie.foamfix.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import pl.asie.foamfix.shared.FoamFixShared;

public class FoamFixGuiFactory implements IModGuiFactory {
   public FoamFixGuiFactory() {
   }

   public void initialize(Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new FoamFixGuiFactory.ConfigGui(parentScreen);
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }

   public static class ConfigGui extends GuiConfig {
      public ConfigGui(GuiScreen parentScreen) {
         super(parentScreen, getConfigElements(), "foamfix", "FoamFix", false, false, I18n.format("FoamFix Configuration", new Object[0]));
      }

      private static List<IConfigElement> getConfigElements() {
         List<IConfigElement> elements = new ArrayList();
         elements.add(new DummyCategoryElement("Load Time Options (Restart required)", "foamfix.config.restartRequired", getChildElements(true)));
         elements.addAll(getChildElements(false));
         return elements;
      }

      private static List<IConfigElement> getChildElements(boolean restartRequired) {
         List<IConfigElement> elements = new ArrayList();
         Configuration c = FoamFixShared.config.getConfig();

         for(String s : c.getCategoryNames()) {
            boolean isExperimental = "experimental".equals(s);
            final ConfigCategory category = c.getCategory(s);

            for(Property p : category.values()) {
               if (FoamFixShared.config.isApplicable(p) && p.showInGui() && p.requiresMcRestart() == restartRequired) {
                  if (isExperimental) {
                     elements.add(new ConfigElement(p) {
                        public String getName() {
                           return "[EXPERIMENTAL] " + super.getName();
                        }
                     });
                  } else {
                     elements.add(new ConfigElement(p) {
                        public String getName() {
                           return category.getName() + "." + super.getName();
                        }
                     });
                  }
               }
            }
         }

         return elements;
      }
   }
}
