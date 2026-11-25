package com.endertech.minecraft.mods.adhooks.motion.realistic;

import com.endertech.minecraft.forge.entities.ForgeEntity;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/** @deprecated */
@Deprecated
public class RealisticController {
   protected static final Map<Entity, RealisticTarget> serverTargets = new ConcurrentHashMap();
   protected static final Map<Entity, RealisticTarget> clientTargets = new ConcurrentHashMap();

   public RealisticController() {
   }

   public static Optional<RealisticTarget> getTarget(Entity entity) {
      return Optional.ofNullable(findTarget(entity));
   }

   public static RealisticTarget getOrCreateTarget(Entity entity) {
      RealisticTarget target = findTarget(entity);
      if (target == null) {
         target = createTarget(entity);
      }

      return target;
   }

   protected static RealisticTarget createTarget(Entity entity) {
      RealisticTarget target = new RealisticTarget(entity);
      if (ForgeEntity.isServerSide(entity)) {
         serverTargets.put(entity, target);
      } else {
         clientTargets.put(entity, target);
      }

      return target;
   }

   @Nullable
   protected static RealisticTarget findTarget(Entity entity) {
      return ForgeEntity.isServerSide(entity) ? (RealisticTarget)serverTargets.get(entity) : (RealisticTarget)clientTargets.get(entity);
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      Collection<RealisticTarget> targets = serverTargets.values();
      if (event.phase == Phase.END) {
         applyTo(targets);
      }

   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      Collection<RealisticTarget> targets = clientTargets.values();
      if (event.phase == Phase.END) {
         applyTo(targets);
      }

   }

   @SubscribeEvent
   public static void onEntityJump(LivingJumpEvent event) {
      getTarget(event.getEntity()).ifPresent((target) -> {
         target.jumpTicksLeft = 10;
      });
   }

   protected static void applyTo(Collection<RealisticTarget> targets) {
      Iterator<RealisticTarget> iterator = targets.iterator();

      while(iterator.hasNext()) {
         RealisticTarget target = (RealisticTarget)iterator.next();
         if (target.hasSources()) {
            target.applyMotion();
         } else {
            iterator.remove();
         }
      }

   }
}
