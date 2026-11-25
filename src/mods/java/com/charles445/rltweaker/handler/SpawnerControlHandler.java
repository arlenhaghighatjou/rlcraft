package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.function.Consumer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class SpawnerControlHandler {
   public SpawnerControlHandler() {
      try {
         if (ModConfig.server.spawnercontrol.synchronizeSpawnerIteration || ModConfig.server.spawnercontrol.removeWorldTicks) {
            CompatUtil.wrapSpecificHandler("SCWorldTick", (x$0) -> {
               new SpawnerControlHandler.SCWorldTick(x$0);
            }, "ladysnake.spawnercontrol.SpawnerEventHandler", "onTickWorldTick");
         }

         if (ModConfig.server.spawnercontrol.removeWorldTicks) {
            Class c_SpawnerEventHandler = Class.forName("ladysnake.spawnercontrol.SpawnerEventHandler");
            Field f_SpawnerEventHandler_allSpawners = ReflectUtil.findField(c_SpawnerEventHandler, "allSpawners");
            f_SpawnerEventHandler_allSpawners.set((Object)null, new SpawnerControlHandler.DenySet());
         }
      } catch (Exception var3) {
         RLTweaker.logger.error("Failed to setup SpawnerControlHandler!", var3);
         ErrorUtil.logSilent("Spawner Control Critical Setup Failure");
         if (var3 instanceof CriticalException) {
            throw new RuntimeException(var3);
         }
      }

   }

   public class DenySet<T> extends HashSet<T> {
      public DenySet() {
      }

      public boolean add(Object val) {
         return false;
      }
   }

   public class SCWorldTick {
      private IEventListener handler;
      private boolean synchronizeSpawnerIteration;
      private boolean removeWorldTicks;

      public SCWorldTick(IEventListener handler) {
         this.synchronizeSpawnerIteration = ModConfig.server.spawnercontrol.synchronizeSpawnerIteration;
         this.removeWorldTicks = ModConfig.server.spawnercontrol.removeWorldTicks;
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onWorldTick(WorldTickEvent event) {
         if (!this.removeWorldTicks) {
            if (event.phase != Phase.START && event.side != Side.CLIENT) {
               if (this.synchronizeSpawnerIteration) {
                  synchronized(this) {
                     this.handler.invoke(event);
                  }
               } else {
                  this.handler.invoke(event);
               }

            }
         }
      }
   }
}
