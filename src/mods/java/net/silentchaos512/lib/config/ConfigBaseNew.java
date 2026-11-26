package net.silentchaos512.lib.config;

import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

public abstract class ConfigBaseNew extends ConfigBase {
   public ConfigBaseNew(String modId) {
      super(modId);
   }

   public abstract I18nHelper i18n();

   public abstract LogHelper log();

   public void load() {
      this.readAnnotatedFields();
   }

   protected void readAnnotatedFields() {
   }
}
