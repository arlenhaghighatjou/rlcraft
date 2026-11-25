package me.ichun.mods.ichunutil.common.module.worldportals.common.core;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Function;
import me.ichun.mods.ichunutil.common.module.worldportals.common.portal.WorldPortal;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EventHandlerWorldPortal {
   public EnumMap<Side, HashMap<Entity, HashSet<WorldPortal>>> monitoredEntities = new EnumMap<Side, HashMap<Entity, HashSet<WorldPortal>>>(Side.class) {
      {
         this.put(Side.SERVER, new HashMap());
         this.put(Side.CLIENT, new HashMap());
      }
   };

   public EventHandlerWorldPortal() {
   }

   @SubscribeEvent
   public void onLivingAttack(LivingAttackEvent event) {
      if (event.getSource() == DamageSource.IN_WALL && this.isInPortal(event.getEntity())) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onWorldUnload(Unload event) {
      Iterator<Entry<Entity, HashSet<WorldPortal>>> monitored = ((HashMap)this.monitoredEntities.get(event.getWorld().isRemote ? Side.CLIENT : Side.SERVER)).entrySet().iterator();

      while(monitored.hasNext()) {
         Entry<Entity, HashSet<WorldPortal>> e = (Entry)monitored.next();
         if (((Entity)e.getKey()).getEntityWorld() == event.getWorld()) {
            monitored.remove();
         }
      }

   }

   @SubscribeEvent
   public void onGetCollisionBoxesEvent(GetCollisionBoxesEvent event) {
      if (event.getEntity() != null) {
         HashMap<Entity, HashSet<WorldPortal>> sideMap = (HashMap)this.monitoredEntities.get(event.getEntity().getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER);
         if (sideMap.containsKey(event.getEntity())) {
            HashSet<WorldPortal> portals = (HashSet)sideMap.get(event.getEntity());
            HashSet<WorldPortal> invalid = new HashSet();

            for(WorldPortal portal : portals) {
               if (!portal.isValid()) {
                  invalid.add(portal);
               } else {
                  AxisAlignedBB check = portal.getCollisionRemovalAabbForEntity(event.getEntity());
                  if (check.intersects(event.getAabb())) {
                     if (event.getAabb().equals(event.getEntity().getEntityBoundingBox())) {
                        event.getCollisionBoxesList().clear();
                     } else {
                        for(int i = event.getCollisionBoxesList().size() - 1; i >= 0; --i) {
                           AxisAlignedBB aabb = (AxisAlignedBB)event.getCollisionBoxesList().get(i);
                           boolean flag = false;

                           for(AxisAlignedBB portalBorder : portal.getCollisionBoundaries()) {
                              if (portalBorder.equals(aabb)) {
                                 flag = true;
                                 break;
                              }
                           }

                           if (!flag && aabb.intersects(check)) {
                              event.getCollisionBoxesList().remove(i);
                           }
                        }
                     }
                  }
               }
            }

            portals.removeAll(invalid);
         }

      }
   }

   public void addMonitoredEntity(Entity ent, WorldPortal portal) {
      HashMap<Entity, HashSet<WorldPortal>> sideMap = (HashMap)this.monitoredEntities.get(ent.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER);
      HashSet<WorldPortal> portals = (HashSet)sideMap.computeIfAbsent(ent, (k) -> {
         return new HashSet();
      });
      portals.add(portal);
   }

   public void removeMonitoredEntity(Entity ent, WorldPortal portal) {
      HashMap<Entity, HashSet<WorldPortal>> sideMap = (HashMap)this.monitoredEntities.get(ent.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER);
      HashSet<WorldPortal> portals = (HashSet)sideMap.get(ent);
      if (portals != null) {
         portals.remove(portal);
         if (portals.isEmpty()) {
            sideMap.remove(ent);
         }
      }

   }

   public boolean isInPortal(Entity ent) {
      HashMap<Entity, HashSet<WorldPortal>> sideMap = (HashMap)this.monitoredEntities.get(ent.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER);
      if (sideMap.containsKey(ent)) {
         for(WorldPortal portal : (HashSet)sideMap.get(ent)) {
            if (portal.isValid()) {
               AxisAlignedBB check = portal.getCollisionRemovalAabbForEntity(ent);
               if (check.intersects(ent.getEntityBoundingBox())) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
