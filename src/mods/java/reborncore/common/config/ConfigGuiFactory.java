package reborncore.common.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.registration.impl.ConfigRegistryFactory;

public abstract class ConfigGuiFactory implements IModGuiFactory {
   public ConfigGuiFactory() {
   }

   public abstract String getModID();

   public abstract String getModName();

   @SideOnly(Side.CLIENT)
   public void initialize(Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new ConfigGuiFactory.ConfigGui(parentScreen, this.getModID(), this.getModName());
   }

   @SideOnly(Side.CLIENT)
   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return Collections.emptySet();
   }

   @SideOnly(Side.CLIENT)
   public static class ConfigGui extends GuiConfig {
      public ConfigGui(GuiScreen parentScreen, String modid, String modName) {
         super(parentScreen, getElemets(modid), modid, modName, false, false, modName + " Configuration");
      }

      public static List<IConfigElement> getElemets(String modid) {
         return (List)ConfigRegistryFactory.getConfigs(modid).stream().map((pair) -> {
            return new DummyCategoryElement((String)pair.getRight(), (String)pair.getRight(), getChildren((Configuration)pair.getLeft()));
         }).collect(Collectors.toList());
      }

      public static List<IConfigElement> getChildren(Configuration configuration) {
         List<IConfigElement> elements = new ArrayList();

         for(final String categoryName : configuration.getCategoryNames()) {
            ConfigCategory category = configuration.getCategory(categoryName);

            for(Property property : category.values()) {
               elements.add(new ConfigElement(property) {
                  public String getName() {
                     return categoryName + "." + super.getName();
                  }
               });
            }
         }

         return elements;
      }
   }
}
