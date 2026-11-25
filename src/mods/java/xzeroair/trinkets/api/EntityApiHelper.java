package xzeroair.trinkets.api;

import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import xzeroair.trinkets.capabilities.Capabilities;

public class EntityApiHelper {
   public EntityApiHelper() {
   }

   public static String getEntityRace(Entity entity) {
      return (String)Capabilities.getEntityProperties(entity, "", (prop, name) -> {
         return prop.getCurrentRace().getName();
      });
   }
}
