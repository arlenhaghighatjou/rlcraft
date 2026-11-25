package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.RuinsReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RuinsHandler {
   private RuinsReflect reflector;

   public RuinsHandler() {
      try {
         this.reflector = new RuinsReflect();
         if (ModConfig.server.ruins.removeRUINSTRIGGERFunctionality) {
            CompatUtil.findAndRemoveHandlerFromEventBus("atomicstryker.ruins.common.RuinsMod", "onEntityEnteringChunk");
         }

         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup RuinsHandler!", var2);
         ErrorUtil.logSilent("Ruins Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         if (ModConfig.server.ruins.cleanupChunkLogger) {
            try {
               ConcurrentHashMap<Integer, Object> generatorMap = this.reflector.getGeneratorMap();
               int dimension = world.provider.getDimension();
               generatorMap.remove(Integer.valueOf(dimension));
               WorldSavedData chunkLoggerData = world.getPerWorldStorage().getOrLoadData(this.reflector.c_ChunkLoggerData, "ruinschunklogger");
               if (chunkLoggerData != null) {
                  synchronized(chunkLoggerData) {
                     try {
                        if (this.reflector.wipeChunkLoggerData(chunkLoggerData, ModConfig.server.ruins.chunkThreshold)) {
                           chunkLoggerData.setDirty(true);
                           RLTweaker.logger.info("Wiped ruinschunklogger.dat for dimension " + world.provider.getDimension());
                        }
                     } catch (IllegalAccessException | IllegalArgumentException var9) {
                        RLTweaker.logger.error("onWorldLoad critical failure in RuinsHandler!", var9);
                        ErrorUtil.logSilent("RuinsHandler Critical Failure");
                        throw new RuntimeException(var9);
                     }
                  }
               }
            } catch (IllegalAccessException | IllegalArgumentException var11) {
               RLTweaker.logger.error("onWorldLoad critical failure in RuinsHandler!", var11);
               ErrorUtil.logSilent("RuinsHandler Critical Failure");
               throw new RuntimeException(var11);
            }
         }

      }
   }
}
