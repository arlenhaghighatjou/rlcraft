package com.creativemd.creativecore.common.config.premade;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

public class NamedList<T> extends LinkedHashMap<String, T> {
   public NamedList() {
   }

   public Set<Entry<String, T>> entrySet() {
      return super.entrySet();
   }
}
