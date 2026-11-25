package com.endertech.minecraft.forge.configs;

import com.endertech.minecraft.forge.ForgeMod;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModConfigGui extends GuiConfig {
   private final ModConfig config;

   public ModConfigGui(ForgeMod mod, GuiScreen parent) {
      super(parent, mod.getConfig().getRootElements(), mod.getId(), false, false, getAbridgedConfigPath(mod.getConfig().toString()));
      this.config = mod.getConfig();
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals(this.modID)) {
         this.getConfig().init();
         if (this.getConfig().hasChanged()) {
            this.getConfig().save();
         }
      }

   }

   public ModConfig getConfig() {
      return this.config;
   }

   public abstract static class Factory implements IModGuiFactory {
      public Factory() {
      }

      public void initialize(Minecraft minecraftInstance) {
      }

      public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
         return null;
      }

      public boolean hasConfigGui() {
         return true;
      }
   }

   public static class PercentageSliderEntry extends NumberSliderEntry {
      public PercentageSliderEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
         super(owningScreen, owningEntryList, configElement);
         if (this.btnValue instanceof GuiSlider) {
            GuiSlider slider = (GuiSlider)this.btnValue;
            slider.suffix = "%";
            slider.updateSlider();
         }

      }
   }
}
