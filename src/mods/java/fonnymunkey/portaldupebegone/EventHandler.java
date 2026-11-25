package fonnymunkey.portaldupebegone;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class EventHandler {
   public EventHandler() {
   }

   public static void register(EventBus bus) {
      bus.register(EntityTravelToDimensionEvent.class, EventHandler::dimensionChangeEvent);
   }

   public static void dimensionChangeEvent(EntityTravelToDimensionEvent event) {
      if (!event.getEntity().world.isRemote) {
         if (event.getEntity() instanceof EntityLiving && !(event.getEntity() instanceof EntityPlayer)) {
            EntityLiving entity = (EntityLiving)event.getEntity();
            if (entity.isDead || !entity.isEntityAlive() || entity.getHealth() <= 0.0F) {
               event.setCanceled(true);
               if (ModConfig.server.logPossibleAttemptedDupe) {
                  PortalDupeBegone.logger.log(Level.WARN, "Possible attempted dupe at X: " + entity.posX + " Y: " + entity.posY + " Z: " + entity.posZ + " Entity Name: " + entity.getName());
               }
            }
         }

      }
   }
}
