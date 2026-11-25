package meldexun.renderlib.integration;

import com.pau101.fairylights.server.entity.EntityFenceFastener;
import net.minecraft.entity.Entity;

public class FairyLights {
   public FairyLights() {
   }

   public static boolean isFairyLightEntity(Entity entity) {
      return entity instanceof EntityFenceFastener;
   }
}
