package com.endertech.minecraft.mods.adhooks.motion;

import com.endertech.minecraft.forge.entities.ForgeEntity;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@EventBusSubscriber
public class MotionController {
   protected static final double DAMPING = 0.5D;
   protected static final Map<Entity, EntityTarget<?>> SERVER_TARGETS = new ConcurrentHashMap();
   protected static final Map<EntityPlayerSP, SinglePlayerTarget> CLIENT_TARGETS = new ConcurrentHashMap();

   public MotionController() {
   }

   public static Optional<EntityTarget<?>> getTarget(Entity entity) {
      if (ForgeEntity.isServerSide(entity) && !(entity instanceof EntityPlayer)) {
         EntityTarget<?> target = (EntityTarget)SERVER_TARGETS.get(entity);
         if (target == null) {
            target = new EntityTarget(entity, 0.5D);
            SERVER_TARGETS.put(entity, target);
         }

         return Optional.of(target);
      } else if (ForgeEntity.isClientSide(entity) && entity instanceof EntityPlayerSP) {
         EntityPlayerSP player = (EntityPlayerSP)entity;
         SinglePlayerTarget target = (SinglePlayerTarget)CLIENT_TARGETS.get(player);
         if (target == null) {
            target = new SinglePlayerTarget(player, 0.5D);
            CLIENT_TARGETS.put(player, target);
         }

         return Optional.of(target);
      } else {
         return Optional.empty();
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         applyTo(SERVER_TARGETS.values());
      }

   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         applyTo(CLIENT_TARGETS.values());
      }

   }

   protected static <T extends EntityTarget<?>> void applyTo(Collection<T> targets) {
      Iterator<T> iterator = targets.iterator();

      while(iterator.hasNext()) {
         T target = iterator.next();
         if (target.exists()) {
            target.applyMotions();
         } else {
            iterator.remove();
         }
      }

   }
}
