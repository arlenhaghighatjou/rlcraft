package com.endertech.minecraft.forge.configs;

import com.endertech.common.CommonString;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.data.IReloadableData;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParsableList implements IReloadableData {
   protected final UnitConfig config;
   protected final String category;
   protected final String key;
   protected final List<String> defaultValues = new ArrayList();
   protected final List<String> configValues = new ArrayList();

   public AbstractParsableList(UnitConfig config, String category, String key, String[] defaultValues) {
      this.config = config;
      this.category = category;
      this.key = key;
      this.defaultValues.addAll(this.asTrimmedList(true, defaultValues));
   }

   public void addDefaultValues(String... strings) {
      this.defaultValues.addAll(this.asTrimmedList(true, strings));
   }

   protected abstract String getComment();

   protected abstract void parseConfigValues();

   protected List<String> asTrimmedList(boolean removeEmpty, String... strings) {
      List<String> list = new ArrayList();

      for(String str : strings) {
         str = str.trim();
         if (!removeEmpty || !str.isEmpty()) {
            list.add(str);
         }
      }

      return list;
   }

   protected List<String> split(String string, String delimiter) {
      String[] split = string.split(delimiter);
      return this.asTrimmedList(false, split);
   }

   protected List<String> splitArgs(String string) {
      return this.split(string, ",");
   }

   protected void logParsingError(String parsedString) {
      ForgeMain.instance.getLogger().error("Unable to parse " + this.key + " from " + CommonString.doubleQuoted(parsedString) + " in " + this.config.getConfigFile());
   }

   public void loadData() {
      String[] strArray = UnitConfig.getStrArray(this.config, this.category, this.key, (String[])this.defaultValues.toArray(new String[0]), this.getComment());
      this.configValues.clear();
      this.configValues.addAll(this.asTrimmedList(true, strArray));
      this.parseConfigValues();
   }
}
