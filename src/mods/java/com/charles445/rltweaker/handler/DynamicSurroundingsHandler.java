package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ServerRunnable;
import javax.annotation.Nullable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class DynamicSurroundingsHandler {
   @Nullable
   DynamicSurroundingsHandler.EnvironmentServiceHelper environmentServiceHelper;

   public DynamicSurroundingsHandler() {
      if (ModConfig.server.dynamicsurroundings.environmentServiceDelay) {
         this.environmentServiceHelper = new DynamicSurroundingsHandler.EnvironmentServiceHelper();
         RLTweaker.serverRunnables.put("EnvironmentServiceHelper", this.environmentServiceHelper);
      }

   }

   public class EnvironmentServiceHelper implements ServerRunnable {
      IEventListener handler_tickEvent;

      public EnvironmentServiceHelper() {
      }

      public void onServerStarting() {
         try {
            this.handler_tickEvent = (IEventListener)CompatUtil.findAndRemoveHandlerFromEventBus("org.orecruncher.dsurround.server.services.EnvironmentService", "tickEvent");
            MinecraftForge.EVENT_BUS.register(this);
         } catch (Exception var2) {
            ErrorUtil.logSilent("DynamicSurroundings EnvironmentServiceHelper Wrapper");
            if (var2 instanceof CriticalException) {
               throw new RuntimeException("Critical error setting up EnvironmentServiceHelper!", var2);
            }
         }

      }

      public void onServerStopping() {
         MinecraftForge.EVENT_BUS.unregister(this);
         this.handler_tickEvent = null;
      }

      @SubscribeEvent
      public void onPlayerTick(PlayerTickEvent event) {
         if (this.handler_tickEvent != null && event.player.ticksExisted % 20 == 0) {
            this.handler_tickEvent.invoke(event);
         }

      }
   }
}
