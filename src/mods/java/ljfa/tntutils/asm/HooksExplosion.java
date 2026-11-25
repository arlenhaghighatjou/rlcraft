package ljfa.tntutils.asm;

import ljfa.tntutils.Config;
import net.minecraft.world.Explosion;

public class HooksExplosion {
   public static final float alwaysDropThreshold = 10.0F;

   public HooksExplosion() {
   }

   public static float getDropChance(Explosion expl) {
      float baseChance = 1.0F / expl.size;
      if (Config.dropChanceModifier == 0.0F) {
         return baseChance;
      } else if (Config.dropChanceModifier > 0.0F) {
         return expl.size <= 10.0F ? baseChance + Config.dropChanceModifier * (1.0F - baseChance) : baseChance;
      } else {
         return (1.0F + Config.dropChanceModifier) * baseChance;
      }
   }
}
