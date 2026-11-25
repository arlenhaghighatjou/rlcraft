package vazkii.quark.base.util;

import net.minecraft.entity.Entity;

public class EntityOpacityHandler {
   public EntityOpacityHandler() {
   }

   public static boolean isEntityInsideOpaqueBlock(Entity entity) {
      return !entity.noClip && entity.world.getBlockState(entity.getPosition()).causesSuffocation();
   }
}
