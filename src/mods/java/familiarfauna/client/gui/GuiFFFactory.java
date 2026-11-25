package familiarfauna.client.gui;

import com.google.common.collect.Lists;
import familiarfauna.config.ConfigurationHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import org.apache.commons.lang3.text.WordUtils;

public class GuiFFFactory extends DefaultGuiFactory {
   public GuiFFFactory() {
      super("familiarfauna", "Familiar Fauna");
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new GuiConfig(parentScreen, getConfigElements(), this.modid, false, false, this.title);
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> list = new ArrayList();
      List<IConfigElement> configChildCategories = Lists.newArrayList();

      for(String categoryName : ConfigurationHandler.config.getCategoryNames()) {
         ConfigCategory category = ConfigurationHandler.config.getCategory(categoryName);
         List<IConfigElement> elements = (new ConfigElement(category)).getChildElements();
         configChildCategories.add(new DummyCategoryElement(WordUtils.capitalize(categoryName), "", elements));
      }

      list.add(new DummyCategoryElement(I18n.translateToLocal("config.category.mobSettings.title"), "", configChildCategories));
      return list;
   }
}
