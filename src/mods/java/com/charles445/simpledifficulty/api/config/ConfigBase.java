package com.charles445.simpledifficulty.api.config;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigBase {
   public final Map<String, String> values = new HashMap();

   public ConfigBase() {
   }

   public boolean getBoolean(IConfigOption option) {
      return Boolean.valueOf((String)this.values.get(option.getName())).booleanValue();
   }

   public int getInteger(IConfigOption option) {
      return Integer.valueOf((String)this.values.get(option.getName())).intValue();
   }

   public void put(IConfigOption option, Object obj) {
      this.values.put(option.getName(), "" + obj);
      QuickConfig.updateValues();
   }
}
