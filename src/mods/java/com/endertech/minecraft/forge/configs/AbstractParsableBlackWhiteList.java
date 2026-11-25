package com.endertech.minecraft.forge.configs;

import com.endertech.common.CommonCollect;
import com.endertech.minecraft.forge.data.Names;
import java.util.Collections;
import java.util.List;

public abstract class AbstractParsableBlackWhiteList<T> extends AbstractParsableList {
   protected final String[] defaultBlackValues;
   protected final String[] defaultWhiteValues;
   protected CommonCollect.BlackWhiteList<T> blackWhiteList;

   public AbstractParsableBlackWhiteList(UnitConfig config, String category, String key) {
      this(config, category, key, new String[0], new String[0]);
   }

   public AbstractParsableBlackWhiteList(UnitConfig config, String category, String key, String[] defaultWhiteValues, String[] defaultBlackValues) {
      super(config, category, key, new String[0]);
      this.blackWhiteList = new CommonCollect.BlackWhiteList<T>(Collections.emptyList(), Collections.emptyList());
      this.defaultWhiteValues = defaultWhiteValues;
      this.defaultBlackValues = defaultBlackValues;
   }

   protected abstract List<T> parse(List<String> var1);

   public boolean isAllowed(T element) {
      return this.blackWhiteList.isAllowed(element);
   }

   /** @deprecated */
   @Deprecated
   protected void parseConfigValues() {
   }

   protected List<T> parseList(String name, String[] defaultValues) {
      String listCategory = Names.dotted().join(this.category, this.key);
      String[] strArray = UnitConfig.getStrArray(this.config, listCategory, name, defaultValues, "");
      return this.parse(this.asTrimmedList(true, strArray));
   }

   public void loadData() {
      this.blackWhiteList = new CommonCollect.BlackWhiteList<T>(this.parseList("blackList", this.defaultBlackValues), this.parseList("whiteList", this.defaultWhiteValues));
   }
}
