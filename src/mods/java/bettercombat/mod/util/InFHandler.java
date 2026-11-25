package bettercombat.mod.util;

import com.github.alexthe666.iceandfire.entity.EntityMutlipartPart;
import net.minecraft.entity.Entity;

public abstract class InFHandler {
   public InFHandler() {
   }

   public static boolean isMultipart(Entity entity) {
      return entity instanceof EntityMutlipartPart;
   }

   public static Entity getMultipartParent(Entity entity) {
      return (Entity)(entity instanceof EntityMutlipartPart ? ((EntityMutlipartPart)entity).getParent() : entity);
   }
}
