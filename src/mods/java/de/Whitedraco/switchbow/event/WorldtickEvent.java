package de.Whitedraco.switchbow.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EntitySelectors;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class WorldtickEvent {
   public WorldtickEvent() {
   }

   public void register(EventBus bus) {
      bus.register(WorldTickEvent.class, this::onRenderTick);
   }

   public void onRenderTick(WorldTickEvent event) {
      if (!event.world.isRemote) {
         List<EntityArrow> a = event.world.getEntities(EntityArrow.class, EntitySelectors.IS_ALIVE);
         if (!a.isEmpty()) {
            for(Entity entityarrow : a) {
               if (entityarrow instanceof EntityArrow && entityarrow.getEntityData().hasKey("NoGravity")) {
                  int nogravity = entityarrow.getEntityData().getInteger("NoGravity");
                  if (nogravity > 0) {
                     entityarrow.setNoGravity(true);
                     entityarrow.getEntityData().setInteger("NoGravity", nogravity - 1);
                  } else {
                     entityarrow.setNoGravity(false);
                     entityarrow.getEntityData().removeTag("NoGravity");
                  }
               }
            }
         }
      }

   }
}
