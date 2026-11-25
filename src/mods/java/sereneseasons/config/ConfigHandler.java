package sereneseasons.config;

import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sereneseasons.api.config.ISyncedOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.init.ModConfig;

public abstract class ConfigHandler {
   public Configuration config;
   public final String description;

   protected ConfigHandler(File configFile, String description) {
      this.config = new Configuration(configFile);
      this.loadConfiguration();
      MinecraftForge.EVENT_BUS.register(this);
      this.description = description;
      ModConfig.configHandlers.add(this);
   }

   protected abstract void loadConfiguration();

   protected <T> void addSyncedValue(ISyncedOption option, T defaultValue, String category, String comment, T... args) {
      String value = "";
      if (defaultValue instanceof String) {
         value = this.config.getString(option.getOptionName(), category, defaultValue.toString(), comment);
      } else if (defaultValue instanceof Integer) {
         value = "" + this.config.getInt(option.getOptionName(), category, ((Integer)defaultValue).intValue(), ((Integer)args[0]).intValue(), ((Integer)args[1]).intValue(), comment);
      } else if (defaultValue instanceof Boolean) {
         value = "" + this.config.getBoolean(option.getOptionName(), category, ((Boolean)defaultValue).booleanValue(), comment);
      } else if (defaultValue instanceof Float) {
         value = "" + this.config.getFloat(option.getOptionName(), category, ((Float)defaultValue).floatValue(), ((Float)args[0]).floatValue(), ((Float)args[1]).floatValue(), comment);
      }

      SyncedConfig.addOption(option, value);
   }

   @SubscribeEvent
   public void onConfigurationChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equalsIgnoreCase("sereneseasons")) {
         this.loadConfiguration();
      }

   }
}
