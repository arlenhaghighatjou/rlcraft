package goblinbob.mobends.core.bender;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.Entity;

public class PreviewHelper {
   private static final Set<Entity> previewEntities = new HashSet();

   public PreviewHelper() {
   }

   public static void registerPreviewEntity(Entity entity) {
      previewEntities.add(entity);
   }

   public static boolean isPreviewEntity(Entity entity) {
      return previewEntities.contains(entity);
   }
}
