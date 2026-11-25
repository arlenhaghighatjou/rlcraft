package com.endertech.minecraft.mods.adhooks.forces;

import com.endertech.minecraft.forge.entities.ForgeEntity;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/** @deprecated */
@Deprecated
public class ForceController {
   protected static final Map<Entity, ForceTarget> serverTargets = new ConcurrentHashMap();
   protected static final Map<Entity, ForceTarget> clientTargets = new ConcurrentHashMap();

   public ForceController() {
   }

   public static Optional<ForceTarget> getTarget(Entity entity) {
      return Optional.ofNullable(findTarget(entity));
   }

   public static ForceTarget getOrCreateTarget(Entity entity) {
      ForceTarget target = findTarget(entity);
      if (target == null) {
         target = createTarget(entity);
      }

      return target;
   }

   protected static ForceTarget createTarget(Entity entity) {
      ForceTarget target = new ForceTarget(entity);
      if (ForgeEntity.isServerSide(entity)) {
         serverTargets.put(entity, target);
      } else {
         clientTargets.put(entity, target);
      }

      return target;
   }

   @Nullable
   protected static ForceTarget findTarget(Entity entity) {
      return ForgeEntity.isServerSide(entity) ? (ForceTarget)serverTargets.get(entity) : (ForceTarget)clientTargets.get(entity);
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      Collection<ForceTarget> targets = serverTargets.values();
      if (event.phase == Phase.END) {
         applyTo(targets);
      }

   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      Collection<ForceTarget> targets = clientTargets.values();
      if (event.phase == Phase.END) {
         applyTo(targets);
      }

   }

   protected static void applyTo(Collection<ForceTarget> targets) {
      Iterator<ForceTarget> iterator = targets.iterator();

      while(iterator.hasNext()) {
         ForceTarget target = (ForceTarget)iterator.next();
         if (target.exist()) {
            target.applyForces();
         } else {
            iterator.remove();
         }
      }

   }
}
