package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.reflect.RecurrentReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Pre;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

public class RecurrentHandler {
   private RecurrentReflect reflector;
   private boolean reflector_enabled = true;

   public RecurrentHandler() {
      try {
         if (ModConfig.server.recurrentcomplex.manageRCForgeEventHandler && !ModConfig.server.recurrentcomplex.generatePartially) {
            Object handler = CompatUtil.findAndRemoveHandlerFromEventBus("ivorius.reccomplex.events.handlers.RCForgeEventHandler", "onPreChunkDecoration");
            if (handler != null) {
               RLTweaker.logger.info("Registering RCHandler to the event bus");
               new RecurrentHandler.RCHandler(handler);
            }
         }

         if (ModConfig.server.recurrentcomplex.cleanStructureData) {
            RLTweaker.logger.info("Registering RCCleanup to the event bus");
            new RecurrentHandler.RCCleanup();
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup RecurrentHandler!", var2);
         ErrorUtil.logSilent("Recurrent Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   private boolean checkReflector() {
      if (this.reflector == null) {
         if (this.reflector_enabled) {
            try {
               this.reflector = new RecurrentReflect();
               return true;
            } catch (Exception var2) {
               this.reflector = null;
               this.reflector_enabled = false;
               RLTweaker.logger.error("Failed to setup RecurrentReflect!", var2);
               ErrorUtil.logSilent("RecurrentReflect Critical Setup Failure");
               throw new RuntimeException(var2);
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public class RCCleanup {
      private Map<Integer, Integer> dimensionSensitivity = new ConcurrentHashMap();

      public RCCleanup() {
         if (RecurrentHandler.this.checkReflector()) {
            Class selfClazz = this.getClass();

            try {
               CompatUtil.subscribeEventManually(Load.class, this, ReflectUtil.findMethod(selfClazz, "onWorldLoad"));
               CompatUtil.subscribeEventManually(RecurrentHandler.this.reflector.c_StructureGenerationEventLite$Post, this, ReflectUtil.findMethod(selfClazz, "onStructureGenerationLitePost"));
            } catch (Exception var4) {
               RLTweaker.logger.error("Failed to setup RCCleanup!", var4);
               ErrorUtil.logSilent("RCCleanup Critical Failure");
               if (var4 instanceof CriticalException) {
                  throw new RuntimeException(var4);
               }
            }

         }
      }

      private void resetSensitivity(int dimension) {
         this.dimensionSensitivity.put(Integer.valueOf(dimension), Integer.valueOf(ModConfig.server.recurrentcomplex.cleanStructureThreshold));
      }

      @SubscribeEvent(
         priority = EventPriority.LOW
      )
      public void onStructureGenerationLitePost(Object event) {
         WorldEvent worldEvent = (WorldEvent)event;
         World world = worldEvent.getWorld();
         if (!world.isRemote) {
            int dimension = world.provider.getDimension();
            if (!this.dimensionSensitivity.containsKey(Integer.valueOf(dimension))) {
               this.resetSensitivity(dimension);
            }

            int sensitivity = ((Integer)this.dimensionSensitivity.get(Integer.valueOf(dimension))).intValue();
            --sensitivity;
            if (sensitivity > 0) {
               this.dimensionSensitivity.put(Integer.valueOf(dimension), Integer.valueOf(sensitivity));
            } else {
               this.resetSensitivity(dimension);
               int amt = -1;

               try {
                  amt = this.cleanAllStructures(world);
               } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException var8) {
                  RLTweaker.logger.error("onStructureGenerationLitePost critical failure in RCCLeaner!", var8);
                  ErrorUtil.logSilent("RCCleanup Critical Failure");
                  throw new RuntimeException(var8);
               }

               if (ModConfig.server.minecraft.debug) {
                  DebugUtil.messageAll("Cleaned up structures: " + amt);
               }

            }
         }
      }

      @SubscribeEvent(
         priority = EventPriority.HIGH
      )
      public void onWorldLoad(Load event) {
         World world = event.getWorld();
         if (!world.isRemote) {
            try {
               this.wipeRecurrentData(world);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4) {
               RLTweaker.logger.error("onWorldLoad critical failure in RCCleaner!", var4);
               ErrorUtil.logSilent("RCCleanup Critical Failure");
               throw new RuntimeException(var4);
            }

            int dimension = world.provider.getDimension();
            this.dimensionSensitivity.put(Integer.valueOf(dimension), Integer.valueOf(ModConfig.server.recurrentcomplex.cleanStructureThreshold));
         }
      }

      private int cleanAllStructures(World world) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
         Object wsgData = RecurrentHandler.this.reflector.getWorldStructureGenerationData(world);
         synchronized(wsgData) {
            Map<UUID, Object> entryMap = (Map)RecurrentHandler.this.reflector.getEntryMap(wsgData);
            Set<UUID> entrySet = entryMap.keySet();
            Set<UUID> entrySetClone = new HashSet();
            entrySetClone.addAll(entrySet);

            for(UUID uuid : entrySetClone) {
               RecurrentHandler.this.reflector.removeEntry(wsgData, uuid);
            }

            return entrySetClone.size();
         }
      }

      private void wipeRecurrentData(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
         Object wsgData = RecurrentHandler.this.reflector.getWorldStructureGenerationData(world);
         synchronized(wsgData) {
            WorldSavedData data = (WorldSavedData)wsgData;
            NBTTagCompound compound = data.serializeNBT();
            compound.removeTag("entries");
            compound.removeTag("checkedChunks");
            compound.removeTag("checkedChunksFinal");
            data.readFromNBT(compound);
            world.getPerWorldStorage().setData(data.mapName, data);
         }
      }
   }

   public class RCHandler {
      private Object handler;

      public RCHandler(Object handler) {
         this.handler = handler;
         if (RecurrentHandler.this.checkReflector()) {
            MinecraftForge.EVENT_BUS.register(this);
         }
      }

      @SubscribeEvent
      public void onPreChunkDecoration(Pre event) {
         try {
            this.decorate((WorldServer)event.getWorld(), event.getRand(), new ChunkPos(event.getChunkX(), event.getChunkZ()));
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
            RLTweaker.logger.error("RecurrentReflect invocation failure!", var3);
            ErrorUtil.logSilent("RecurrentReflect Critical Invocation Failure");
            throw new RuntimeException(var3);
         }
      }

      private void decorate(WorldServer world, Random random, ChunkPos chunkPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
         boolean mapFeatures = world.getWorldInfo().isMapFeaturesEnabled();
         Object wsgData = RecurrentHandler.this.reflector.getWorldStructureGenerationData(world);
         synchronized(wsgData) {
            List structuresComplementList = RecurrentHandler.this.reflector.getStructuresComplementList(wsgData, chunkPos);
            RecurrentHandler.this.reflector.checkChunk(wsgData, chunkPos);
            RecurrentHandler.this.reflector.complementStructuresInChunk(chunkPos, world, structuresComplementList);
            if (mapFeatures || !RecurrentHandler.this.reflector.honorStructureGenerationOption()) {
               Biome biome = world.getBiome(chunkPos.getBlock(8, 0, 8));
               BlockPos spawnPos = world.getSpawnPoint();
               RecurrentHandler.this.reflector.planStaticStructuresInChunk(random, chunkPos, world, spawnPos, (Object)null);
               boolean generate = RecurrentHandler.this.reflector.isGenerationEnabled(biome) && RecurrentHandler.this.reflector.isGenerationEnabled(world.provider);
               if (world.provider.getDimension() == 0) {
                  double[] chunkArr = new double[]{(double)(chunkPos.x * 16 + 8), (double)(chunkPos.z * 16 + 8)};
                  double[] spawnArr = new double[]{(double)spawnPos.getX(), (double)spawnPos.getZ()};
                  double spawnDistSq = RecurrentHandler.this.reflector.distanceSq(chunkArr, spawnArr);
                  float minDistSq = RecurrentHandler.this.reflector.minDistToSpawnForGeneration();
                  minDistSq = minDistSq * minDistSq;
                  generate &= spawnDistSq >= (double)minDistSq;
               }

               if (generate) {
                  this.planStructuresInChunk(random, chunkPos, world, biome);
               }
            }

         }
      }

      private void planStructuresInChunk(Random random, ChunkPos chunkPos, WorldServer world, Biome biome) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
         float spawnDistance = RecurrentHandler.this.reflector.distance(new ChunkPos(world.getSpawnPoint()), chunkPos);
         Object structureRegistryInstance = RecurrentHandler.this.reflector.getStructureRegistryInstance();
         Object cachedSelectors = RecurrentHandler.this.reflector.getNaturalGenerationSelectors(structureRegistryInstance);
         Object selector = RecurrentHandler.this.reflector.getSelectorFromCachedSelectors(cachedSelectors, biome, world.provider);
         List<Pair<Object, Object>> generatedStructures = RecurrentHandler.this.reflector.getStructurePairsWithSelector(selector, random, world.getBiome(chunkPos.getBlock(0, 0, 0)), world.provider, Float.valueOf(spawnDistance));
         generatedStructures.stream().forEach((pair) -> {
            try {
               this.planStructureInChunk(chunkPos, world, pair.getLeft(), pair.getRight(), random.nextLong());
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException var6) {
               RLTweaker.logger.error("planStructuresInChunk failed on stream!", var6);
               ErrorUtil.logSilent("RecurrentReflect Critical Stream Failure");
               throw new RuntimeException(var6);
            }
         });
      }

      private void planStructureInChunk(ChunkPos chunkPos, WorldServer world, Object structure, Object naturalGeneration, long seed) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
         String structureName = RecurrentHandler.this.reflector.getStructureName(structure);
         Object blockSurfacePos = RecurrentHandler.this.reflector.getRandomSurfacePos(chunkPos, seed);
         Object limitations = RecurrentHandler.this.reflector.getNaturalGenerationLimitations(naturalGeneration);
         if (limitations == null || RecurrentHandler.this.reflector.isSpawnLimitationResolved(limitations, world, structureName)) {
            Object maturityEnum = RecurrentHandler.this.reflector.generateMaturityFromString("SUGGEST");
            Object placer = RecurrentHandler.this.reflector.getNaturalGenerationPlacer(naturalGeneration);
            Object generator = RecurrentHandler.this.reflector.newStructureGenerator();
            generator = RecurrentHandler.this.reflector.setStructure(generator, structure);
            generator = RecurrentHandler.this.reflector.setWorld(generator, world);
            generator = RecurrentHandler.this.reflector.setGenerationInfo(generator, naturalGeneration);
            generator = RecurrentHandler.this.reflector.setSeed(generator, seed);
            generator = RecurrentHandler.this.reflector.setMaturity(generator, maturityEnum);
            generator = RecurrentHandler.this.reflector.setRandomPosition(generator, blockSurfacePos, placer);
            generator = RecurrentHandler.this.reflector.setFromCenter(generator, true);
            generator = RecurrentHandler.this.reflector.partially(generator, false, chunkPos);
            Object environment = RecurrentHandler.this.reflector.getEnvironment(generator);
            Biome enviroBiome = RecurrentHandler.this.reflector.environmentToBiome(environment);
            if (RecurrentHandler.this.reflector.getNaturalGenerationWeight(naturalGeneration, world.provider, enviroBiome) <= 0.0D) {
               RLTweaker.logger.trace("" + structure + " failed to spawn at " + blockSurfacePos + " (incompatible biome edge)");
               return;
            }

            Object generationResult = RecurrentHandler.this.reflector.generate(generator);
            RLTweaker.logger.trace("Structure Generated: " + structure);
         }

      }
   }
}
