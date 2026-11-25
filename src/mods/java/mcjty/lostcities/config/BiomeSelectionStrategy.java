package mcjty.lostcities.config;

import java.util.HashMap;
import java.util.Map;

public enum BiomeSelectionStrategy {
   ORIGINAL("original"),
   RANDOMIZED("randomized"),
   VARIED("varied");

   private final String name;
   private static final Map<String, BiomeSelectionStrategy> NAME_TO_TYPE = new HashMap();

   private BiomeSelectionStrategy(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static BiomeSelectionStrategy getTypeByName(String name) {
      return (BiomeSelectionStrategy)NAME_TO_TYPE.get(name);
   }

   static {
      for(BiomeSelectionStrategy type : values()) {
         NAME_TO_TYPE.put(type.getName(), type);
      }

   }
}
