package meldexun.entityculling.integration;

import me.ichun.mods.hats.common.entity.EntityHat;
import net.minecraft.entity.Entity;

public class Hats {
   public Hats() {
   }

   public static boolean isHat(Entity entity) {
      return entity instanceof EntityHat;
   }
}
