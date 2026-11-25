package net.silentchaos512.lib.collection;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class EntityMatchList extends AbstractMatchList<Entity> {
   public EntityMatchList(boolean whitelist, boolean allowUserToChangeType, String... defaultValues) {
      super(whitelist, allowUserToChangeType, defaultValues);
   }

   protected boolean contains(Entity entity) {
      ResourceLocation resource = EntityList.getKey(entity);
      if (resource == null) {
         return false;
      } else {
         String id = resource.toString();
         String idOld = EntityList.getEntityString(entity);
         return this.getList().stream().anyMatch((entry) -> {
            return keyMatches(entry, id) || entry.equalsIgnoreCase(idOld) || entry.equalsIgnoreCase("minecraft:" + id);
         });
      }
   }
}
