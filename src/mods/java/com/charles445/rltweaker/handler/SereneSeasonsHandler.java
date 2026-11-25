package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.hook.HookWorld;
import com.charles445.rltweaker.reflect.SereneSeasonsReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class SereneSeasonsHandler {
   SereneSeasonsReflect reflector;

   public SereneSeasonsHandler() {
      try {
         this.reflector = new SereneSeasonsReflect();
         if (ModConfig.server.sereneseasons.replaceRandomUpdateHandler && ModConfig.patches.chunkTicks) {
            IEventListener listener = (IEventListener)CompatUtil.findAndRemoveHandlerFromEventBus("sereneseasons.handler.season.RandomUpdateHandler", "onWorldTick");
            if (listener != null) {
               RLTweaker.logger.info("Replacing SereneSeasons RandomUpdateHandler world tick");
               HookWorld.sereneSeasonsPost = new SereneSeasonsHandler.SereneChunkTick(listener);
            }
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup SereneSeasonsHandler!", var2);
         ErrorUtil.logSilent("Serene Seasons Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   public class SereneChunkTick implements HookWorld.IChunkTickPost<SereneSeasonsReflect.SubSeason> {
      IEventListener handler;
      private boolean selfWorking;
      private int LCG = (new Random()).nextInt();
      private Map<ResourceLocation, Boolean> cachedBiomeSeasonalEffects;
      private Map<ResourceLocation, Boolean> cachedBiomeTropical;

      public SereneChunkTick(IEventListener handler) {
         this.handler = handler;
         this.selfWorking = true;
         MinecraftForge.EVENT_BUS.register(this);

         try {
            this.cachedBiomeSeasonalEffects = SereneSeasonsHandler.this.reflector.createBiomeSeasonalEffects();
            this.cachedBiomeTropical = SereneSeasonsHandler.this.reflector.createBiomeTropicalSeasons();
         } catch (Exception var4) {
            this.selfWorking = false;
            RLTweaker.logger.error("Failed to cache biome information!", var4);
            ErrorUtil.logSilent("Serene Seasons Chunk Tick Biome Seasonal Effects");
         }

      }

      @SubscribeEvent
      public void onWorldTick(WorldTickEvent event) {
         if (!this.selfWorking || !HookWorld.chunkTickPatchEnabled) {
            this.handler.invoke(event);
         }

      }

      public SereneSeasonsReflect.SubSeason preUpdate(WorldServer world) {
         if (this.selfWorking) {
            try {
               SereneSeasonsReflect.SubSeason subSeason = SereneSeasonsHandler.this.reflector.getSubSeason(SereneSeasonsHandler.this.reflector.getISeasonState(world));
               SereneSeasonsReflect.Season season = subSeason.getSeason();
               Object seasonConfig = SereneSeasonsHandler.this.reflector.getSeasonConfig();
               boolean changeWeatherFrequency = SereneSeasonsHandler.this.reflector.getConfigChangeWeatherFrequency(seasonConfig);
               WorldInfo worldinfo = world.getWorldInfo();
               if (season == SereneSeasonsReflect.Season.WINTER) {
                  if (changeWeatherFrequency) {
                     if (worldinfo.isThundering()) {
                        worldinfo.setThundering(false);
                     }

                     if (!worldinfo.isRaining() && worldinfo.getRainTime() > 36000) {
                        worldinfo.setRainTime(world.rand.nextInt(24000) + 12000);
                     }
                  }
               } else {
                  if (changeWeatherFrequency) {
                     if (season == SereneSeasonsReflect.Season.SPRING) {
                        if (!worldinfo.isRaining() && worldinfo.getRainTime() > 96000) {
                           worldinfo.setRainTime(world.rand.nextInt(84000) + 12000);
                        }
                     } else if (season == SereneSeasonsReflect.Season.SUMMER && !worldinfo.isThundering() && worldinfo.getThunderTime() > 36000) {
                        worldinfo.setThunderTime(world.rand.nextInt(24000) + 12000);
                     }
                  }

                  if (SereneSeasonsHandler.this.reflector.getConfigGenerateSnowAndIce(seasonConfig) && SereneSeasonsHandler.this.reflector.isDimensionWhitelisted(world.provider.getDimension())) {
                     return subSeason;
                  }
               }
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var7) {
               this.selfWorking = false;
               RLTweaker.logger.error("Failed to run Serene Seasons Chunk Tick! Returning to default behavior.", var7);
               ErrorUtil.logSilent("Serene Seasons Chunk Tick Invocation");
            }
         }

         return null;
      }

      public void invoke(Chunk chunk, SereneSeasonsReflect.SubSeason companion) {
         int randVal = 4;
         switch(companion) {
         case EARLY_SPRING:
            randVal = 16;
            break;
         case MID_SPRING:
            randVal = 12;
            break;
         case LATE_SPRING:
            randVal = 8;
         }

         if (chunk.getWorld().rand.nextInt(randVal) == 0) {
            int blockX = chunk.x << 4;
            int blockZ = chunk.z << 4;
            World world = chunk.getWorld();
            int updateLCG = this.LCG * 3 + 1013904223;
            this.LCG = updateLCG;
            int chunkOffset = updateLCG >> 2;
            BlockPos pos = chunk.getPrecipitationHeight(new BlockPos(blockX + (chunkOffset & 15), 0, blockZ + (chunkOffset >> 8 & 15)));
            Biome biome = chunk.getBiome(pos, world.getBiomeProvider());
            if (!Objects.equals(this.cachedBiomeSeasonalEffects.get(biome.getRegistryName()), Boolean.valueOf(false))) {
               boolean firstRun = true;
               int posX = pos.getX();
               int posZ = pos.getZ();

               for(int posY = pos.getY(); posY >= 0; --posY) {
                  Block block = chunk.getBlockState(posX, posY, posZ).getBlock();
                  if (block == Blocks.SNOW_LAYER) {
                     pos = new BlockPos(posX, posY, posZ);
                     if (this.getUnsafeTemperature(chunk, biome, pos, companion) >= 0.15F) {
                        world.setBlockToAir(pos);
                        break;
                     }
                  }

                  if (!firstRun) {
                     if (block == Blocks.ICE) {
                        pos = new BlockPos(posX, posY, posZ);
                        if (this.getUnsafeTemperature(chunk, biome, pos, companion) >= 0.15F) {
                           try {
                              SereneSeasonsHandler.this.reflector.turnIntoWater(block, world, pos);
                              break;
                           } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var17) {
                              this.selfWorking = false;
                              RLTweaker.logger.error("Failed to run Serene Seasons Chunk Tick! Ice broke. Returning to default behavior.", var17);
                              ErrorUtil.logSilent("Serene Seasons Chunk Tick Invocation Ice");
                              return;
                           }
                        }
                     }
                  } else {
                     firstRun = false;
                  }
               }

            }
         }
      }

      private float getUnsafeTemperature(Chunk chunk, Biome biome, BlockPos pos, SereneSeasonsReflect.SubSeason subseason) {
         float baseTemp = biome.getTemperature(pos);
         if (biome.getDefaultTemperature() > 0.8F) {
            return baseTemp;
         } else if (Objects.equals(this.cachedBiomeTropical.get(biome.getRegistryName()), Boolean.valueOf(true))) {
            return baseTemp;
         } else {
            switch(subseason) {
            case EARLY_SPRING:
            case LATE_AUTUMN:
               return baseTemp - 0.4F;
            case MID_SPRING:
            case MID_AUTUMN:
               return baseTemp - 0.2F;
            case LATE_SPRING:
            case EARLY_AUTUMN:
               return baseTemp - 0.1F;
            default:
               return baseTemp;
            }
         }
      }
   }
}
