package com.charles445.simpledifficulty.config.json;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class ExtraItem {
   public String _comment = "Unspecified";
   public boolean enabled = false;
   public Map<String, String> settings = new HashMap();

   public ExtraItem(String comment, boolean isEnabled) {
      this._comment = comment;
      this.enabled = isEnabled;
   }

   public ExtraItem put(String key, String value) {
      this.settings.put(key, value);
      return this;
   }

   @Nullable
   public String get(String key) {
      return (String)this.settings.get(key);
   }

   @Nullable
   public Integer getInteger(String key) {
      String val = (String)this.settings.get(key);
      if (val == null) {
         return null;
      } else {
         try {
            return Integer.parseInt(val);
         } catch (NumberFormatException var4) {
            return null;
         }
      }
   }
}
