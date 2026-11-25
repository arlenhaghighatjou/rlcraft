package greymerk.roguelike.worldgen;

import com.github.fnar.util.Strings;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public enum VanillaStructure {
   STRONGHOLD,
   MANSION,
   MONUMENT,
   VILLAGE,
   MINESHAFT,
   TEMPLE;

   private VanillaStructure() {
   }

   public static String getName(VanillaStructure type) {
      switch(type) {
      case STRONGHOLD:
         return "Stronghold";
      case MANSION:
         return "Mansion";
      case MONUMENT:
         return "Monument";
      case VILLAGE:
         return "Village";
      case MINESHAFT:
         return "Mineshaft";
      case TEMPLE:
         return "Temple";
      default:
         return null;
      }
   }

   public static VanillaStructure getType(String name) {
      return valueOf(name.toUpperCase());
   }

   public static String getAllAsCommaDelimitedString() {
      return (String)Arrays.stream(values()).map(VanillaStructure::getName).reduce(Strings::commaConcatenate).orElse("");
   }
}
