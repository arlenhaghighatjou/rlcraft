package sereneseasons.api.config;

import com.google.common.collect.Maps;
import java.util.Map;

public class SyncedConfig {
   public static Map<String, SyncedConfig.SyncedConfigEntry> optionsToSync = Maps.newHashMap();

   public SyncedConfig() {
   }

   public static void addOption(ISyncedOption option, String defaultValue) {
      optionsToSync.put(option.getOptionName(), new SyncedConfig.SyncedConfigEntry(defaultValue));
   }

   public static boolean getBooleanValue(ISyncedOption option) {
      return Boolean.valueOf(getValue(option)).booleanValue();
   }

   public static int getIntValue(ISyncedOption option) {
      SyncedConfig.SyncedConfigEntry entry = (SyncedConfig.SyncedConfigEntry)optionsToSync.get(option.getOptionName());
      if (entry.parsedValue != entry.value) {
         entry.parsedInt = Integer.parseInt(entry.value);
         entry.parsedValue = entry.value;
      }

      return entry.parsedInt;
   }

   public static String getValue(ISyncedOption option) {
      return ((SyncedConfig.SyncedConfigEntry)optionsToSync.get(option.getOptionName())).value;
   }

   public static void restoreDefaults() {
      for(SyncedConfig.SyncedConfigEntry entry : optionsToSync.values()) {
         entry.value = entry.defaultValue;
      }

   }

   public static class SyncedConfigEntry {
      public String value;
      public final String defaultValue;
      private String parsedValue;
      private int parsedInt;

      public SyncedConfigEntry(String defaultValue) {
         this.defaultValue = defaultValue;
         this.value = defaultValue;
      }
   }
}
