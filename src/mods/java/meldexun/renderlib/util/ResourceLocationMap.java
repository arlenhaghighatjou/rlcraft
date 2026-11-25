package meldexun.renderlib.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationMap<K, V> {
   private final Map<String, V> namespace2valueMap = new HashMap();
   private final Map<String, V> resourceLocation2valueMap = new HashMap();
   private final Map<Class<? extends K>, V> class2valueMap = new HashMap();
   private final Function<Class<K>, ResourceLocation> resourceLocationFunc;
   private final V defaultValue;
   private final Function<String[], V> valueParser;

   public ResourceLocationMap(Function<Class<K>, ResourceLocation> func, V defaultValue, Function<String[], V> valueParser) {
      this.resourceLocationFunc = func;
      this.defaultValue = defaultValue;
      this.valueParser = valueParser;
   }

   public V get(K t) {
      return (V)(this.namespace2valueMap.isEmpty() && this.resourceLocation2valueMap.isEmpty() ? this.defaultValue : this.class2valueMap.computeIfAbsent(t.getClass(), (k) -> {
         ResourceLocation resourceLocation = (ResourceLocation)this.resourceLocationFunc.apply(k);
         if (resourceLocation == null) {
            return this.defaultValue;
         } else {
            V value = (V)this.resourceLocation2valueMap.get(resourceLocation.toString());
            if (value != null) {
               return value;
            } else {
               value = (V)this.namespace2valueMap.get(resourceLocation.getResourceDomain());
               return (V)(value != null ? value : this.defaultValue);
            }
         }
      }));
   }

   public void load(String[] data) {
      this.namespace2valueMap.clear();
      this.resourceLocation2valueMap.clear();
      this.class2valueMap.clear();

      for(String entry : data) {
         int indexKeyDelimiter = entry.indexOf(58);
         int indexEntryDelimiter = entry.indexOf(61);
         String key = indexEntryDelimiter != -1 ? entry.substring(0, indexEntryDelimiter) : entry;
         String[] rawValue;
         if (indexEntryDelimiter != -1) {
            rawValue = (String[])Arrays.stream(entry.substring(indexEntryDelimiter + 1).split(",")).map(String::trim).filter((s) -> {
               return !s.isEmpty();
            }).toArray((x$0) -> {
               return new String[x$0];
            });
         } else {
            rawValue = new String[0];
         }

         V value = (V)this.valueParser.apply(rawValue);
         if (value != null) {
            if (indexKeyDelimiter != -1 && (indexEntryDelimiter == -1 || indexKeyDelimiter <= indexKeyDelimiter)) {
               this.resourceLocation2valueMap.put(key, value);
            } else {
               this.namespace2valueMap.put(key, value);
            }
         }
      }

   }
}
