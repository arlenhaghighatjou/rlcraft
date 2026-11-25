package com.endertech.minecraft.forge.configs;

import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.math.Percentage;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModConfig extends ForgeConfig {
   public static final String CATEGORY_CLIENT = "Client";
   public static final String CATEGORY_SERVER = "Server";
   public static final IntBounds BOUNDS_PERCENTAGE_SLIDER = IntBounds.from(Integer.valueOf(0), Integer.valueOf(1000));

   public ModConfig(String modId, FMLPreInitializationEvent event) {
      super(Paths.get(String.valueOf(event.getModConfigurationDirectory()), modId, modId + ".cfg").toFile());
      this.init();
   }

   public List<IConfigElement> getRootElements() {
      List<IConfigElement> list = new ArrayList();

      for(String name : this.getCategoryNames()) {
         ConfigCategory category = this.getCategory(name);
         if (!category.isChild()) {
            ConfigElement element = new ConfigElement(category);
            list.add(element);
         }
      }

      return list;
   }

   public void init() {
   }

   public int getInt(String category, String key, int defaultValue, IntBounds bounds, String comment) {
      Property prop = this.get(category, key, defaultValue, comment, bounds.getMin().intValue(), bounds.getMax().intValue());
      if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
         prop.setConfigEntryClass(NumberSliderEntry.class);
      }

      return prop.getInt();
   }

   public Percentage getPercentage(String category, String key, int defaultValue, IntBounds bounds, String comment) {
      Property prop = this.get(category, key, defaultValue, comment, bounds.getMin().intValue(), bounds.getMax().intValue());
      if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
         prop.setConfigEntryClass(ModConfigGui.PercentageSliderEntry.class);
      }

      return new Percentage((float)prop.getInt());
   }
}
