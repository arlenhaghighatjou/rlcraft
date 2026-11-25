package mcjty.lostcities.config;

import java.util.HashMap;
import java.util.Map;

public enum LandscapeType {
   DEFAULT("default"),
   FLOATING("floating"),
   SPACE("space"),
   CAVERN("cavern");

   private final String name;
   private static final Map<String, LandscapeType> NAME_TO_TYPE = new HashMap();

   private LandscapeType(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static LandscapeType getTypeByName(String name) {
      return (LandscapeType)NAME_TO_TYPE.get(name);
   }

   static {
      for(LandscapeType type : values()) {
         NAME_TO_TYPE.put(type.getName(), type);
      }

   }
}
