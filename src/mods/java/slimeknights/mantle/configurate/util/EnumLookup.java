package slimeknights.mantle.configurate.util;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class EnumLookup {
   private static final LoadingCache<Class<? extends Enum<?>>, Map<String, Enum<?>>> enumFieldCache = CacheBuilder.newBuilder().weakKeys().maximumSize(512L).build(new CacheLoader<Class<? extends Enum<?>>, Map<String, Enum<?>>>() {
      public Map<String, Enum<?>> load(Class<? extends Enum<?>> key) throws Exception {
         Map<String, Enum<?>> ret = new HashMap();

         for(Enum<?> field : (Enum[])key.getEnumConstants()) {
            ret.put(field.name(), field);
            ret.putIfAbsent(EnumLookup.processKey(field.name()), field);
         }

         return ImmutableMap.copyOf(ret);
      }
   });

   private EnumLookup() {
   }

   private static String processKey(String key) {
      Preconditions.checkNotNull(key, "key");
      return "\ud83c\udf38" + key.toLowerCase().replace("_", "");
   }

   public static <T extends Enum<T>> Optional<T> lookupEnum(Class<T> clazz, String key) {
      Preconditions.checkNotNull(clazz, "clazz");

      try {
         Map<String, Enum<?>> vals = (Map)enumFieldCache.get(clazz);
         Enum<?> possibleRet = (Enum)vals.get(key);
         return possibleRet != null ? Optional.of(possibleRet) : Optional.ofNullable((Enum)vals.get(processKey(key)));
      } catch (ExecutionException var4) {
         return Optional.empty();
      }
   }
}
