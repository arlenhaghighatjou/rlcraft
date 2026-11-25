package xzeroair.trinkets.util.helpers;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;

public class MagicHelper {
   public MagicHelper() {
   }

   public static void refillMana(Entity entity) {
      Capabilities.getMagicStats(entity, MagicStats::refillMana);
   }

   public static void refillMana(Entity entity, float amount) {
      Capabilities.getMagicStats(entity, (magic) -> {
         magic.addMana(amount);
      });
   }

   public static void refillManaByPrecentage(Entity entity, float percent) {
      Capabilities.getMagicStats(entity, (magic) -> {
         magic.addMana(magic.getMaxMana() * percent);
      });
   }

   public static boolean spendMana(Entity entity, float cost) {
      return ((Boolean)Capabilities.getMagicStats(entity, Boolean.valueOf(true), (magic, rtn) -> {
         return magic.spendMana(cost);
      })).booleanValue();
   }
}
