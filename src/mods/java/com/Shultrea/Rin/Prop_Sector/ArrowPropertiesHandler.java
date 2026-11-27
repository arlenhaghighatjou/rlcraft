package com.Shultrea.Rin.Prop_Sector;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class ArrowPropertiesHandler {
   int counter = 0;

   public ArrowPropertiesHandler() {
   }

   public void register(EventBus bus) {
      bus.register(WorldTickEvent.class, EventPriority.HIGHEST, true, this::onEvent);
   }

   public void onEvent(WorldTickEvent event) {
      if (event.phase == Phase.START) {
         ++this.counter;
         if (this.counter % 20 == 0) {
            this.counter = 0;
            ArrayList<Entity> entities = (ArrayList)event.world.loadedEntityList;
            new ArrayList();

            for(Entity entity : entities) {
               if (entity instanceof EntityArrow) {
                  entity.writeToNBT(new NBTTagCompound());
                  if (entity.hasCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null)) {
                     IArrowProperties ar = (IArrowProperties)entity.getCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null);
                     if (ar.getIsStarFallMade()) {
                        NBTTagCompound life = new NBTTagCompound();
                        entity.writeToNBT(life);
                        short f = life.getShort("life");
                        life.setShort("life", (short)(f + 50));
                        entity.readFromNBT(life);
                     }
                  }
               }
            }

         }
      }
   }
}
