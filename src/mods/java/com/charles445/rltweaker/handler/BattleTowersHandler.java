package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.BattleTowersReflect;
import com.charles445.rltweaker.util.AIUtil;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.WorldGeneratorWrapper;
import com.google.common.base.Function;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class BattleTowersHandler {
   public long tickedTime;
   BattleTowersReflect reflector;
   public int cachedTowerDestroyerConfig;

   public BattleTowersHandler() {
      try {
         this.reflector = new BattleTowersReflect();
         if (ModConfig.server.battletowers.dimensionBlacklistEnabled || ModConfig.server.battletowers.consistentTowerGeneration) {
            CompatUtil.wrapSpecificHandler("BTWorldLoadEvent", (x$0) -> {
               new BattleTowersHandler.BTWorldLoadEvent(x$0);
            }, "atomicstryker.battletowers.common.WorldGenHandler", "eventWorldLoad");
            CompatUtil.findAndRemoveHandlerFromEventBus("atomicstryker.battletowers.common.WorldGenHandler", "eventWorldLoad");
         }

         if (ModConfig.server.battletowers.consistentTowerGeneration) {
            CompatUtil.wrapSpecificHandler("BTWorldSaveEvent", (x$0) -> {
               new BattleTowersHandler.BTWorldSaveEvent(x$0);
            }, "atomicstryker.battletowers.common.WorldGenHandler", "eventWorldSave");
            CompatUtil.findAndRemoveHandlerFromEventBus("atomicstryker.battletowers.common.WorldGenHandler", "eventWorldSave");
            BattleTowersHandler.BTWorldGenerator generator = new BattleTowersHandler.BTWorldGenerator();
            generator = (BattleTowersHandler.BTWorldGenerator)CompatUtil.tryWrapWorldGenerator(generator, this.reflector.c_WorldGenHandler);
         }

         this.cachedTowerDestroyerConfig = this.reflector.getTowerDestroyerEnabled();
         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup BattleTowersHandler!", var2);
         ErrorUtil.logSilent("BattleTowers Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (ModConfig.server.battletowers.golemHighAggression && this.reflector.isEntityGolem(event.getEntity())) {
         EntityCreature golem = (EntityCreature)event.getEntity();
         AIUtil.tryAndReplaceAllTasks(golem, golem.targetTasks, EntityAIHurtByTarget.class, (oldTask) -> {
            return new BattleTowersHandler.GolemHurtByTarget(golem, false, new Class[0]);
         });
      }

      if (this.reflector.isLycanitesAvailable() && ModConfig.server.battletowers.golemLycanitesProjectile && this.reflector.isEntityGolemFireball(event.getEntity())) {
         try {
            Entity fireball = event.getEntity();
            EntityLiving shooter = (EntityLiving)this.reflector.f_AS_EntityGolemFireball_shooterEntity.get(fireball);
            Entity demonFireball = this.reflector.createLycanitesProjectileWithNameAndShooter(ModConfig.server.battletowers.golemLycanitesProjectileName, shooter, (float)ModConfig.server.battletowers.golemLycanitesProjectileScaleModifier);
            if (demonFireball == null) {
               return;
            }

            demonFireball.setPosition(fireball.posX, fireball.posY, fireball.posZ);
            demonFireball.motionX = this.reflector.getGolemFireballAccelerationX(fireball);
            demonFireball.motionY = this.reflector.getGolemFireballAccelerationY(fireball);
            demonFireball.motionZ = this.reflector.getGolemFireballAccelerationZ(fireball);
            demonFireball.rotationYaw = fireball.rotationYaw;
            demonFireball.rotationPitch = fireball.rotationPitch;
            demonFireball.prevRotationYaw = fireball.prevRotationYaw;
            demonFireball.prevRotationPitch = fireball.prevRotationPitch;
            double speedModifier = ModConfig.server.battletowers.golemLycanitesProjectileSpeedModifier;
            demonFireball.motionX *= speedModifier;
            demonFireball.motionY *= speedModifier;
            demonFireball.motionZ *= speedModifier;
            shooter.getEntityWorld().spawnEntity(demonFireball);
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var7) {
            ErrorUtil.logSilent("BT golemLycanitesProjectile Invocation");
         }

         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onLivingAttack(LivingAttackEvent event) {
      boolean suffocationFix = ModConfig.server.battletowers.golemSuffocatingFix;
      boolean fallingBlockFix = ModConfig.server.battletowers.golemFallingBlockFix;
      boolean anvilFix = ModConfig.server.battletowers.golemAnvilFix;
      boolean lycanitesFluidFix = ModConfig.server.battletowers.golemLycanitesFluidFix;
      if (suffocationFix || fallingBlockFix || anvilFix || lycanitesFluidFix) {
         String damageType = event.getSource().getDamageType();
         if (suffocationFix && damageType.equals("inWall") && this.reflector.isEntityGolem(event.getEntityLiving())) {
            event.setCanceled(true);
         } else if (fallingBlockFix && damageType.equals("fallingBlock") && this.reflector.isEntityGolem(event.getEntityLiving())) {
            event.setCanceled(true);
         } else if (anvilFix && damageType.equals("anvil") && this.reflector.isEntityGolem(event.getEntityLiving())) {
            event.setCanceled(true);
         } else if (lycanitesFluidFix && (damageType.equals("acid") || damageType.equals("ooze")) && this.reflector.isEntityGolem(event.getEntityLiving())) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      boolean dormantSpeedFix = ModConfig.server.battletowers.golemDormantSpeedFix;
      boolean drownFix = ModConfig.server.battletowers.golemDrowningFix;
      double golemSpeedCap = ModConfig.server.battletowers.golemSpeedCap;
      double golemSpeedCapUpwards = ModConfig.server.battletowers.golemSpeedCapUpwards;
      boolean dismountFix = ModConfig.server.battletowers.golemAutoDismount;
      if (dormantSpeedFix || drownFix || dismountFix || golemSpeedCap >= 0.0D || golemSpeedCapUpwards >= 0.0D) {
         if (this.reflector.isEntityGolem(event.getEntityLiving())) {
            EntityLivingBase golem = event.getEntityLiving();
            if (dismountFix) {
               golem.dismountRidingEntity();
            }

            if (drownFix) {
               golem.setAir(300);
            }

            if (golemSpeedCap >= 0.0D) {
               if (golem.motionX > golemSpeedCap) {
                  golem.motionX = golemSpeedCap;
               } else if (golem.motionX < -golemSpeedCap) {
                  golem.motionX = -golemSpeedCap;
               }

               if (golem.motionZ > golemSpeedCap) {
                  golem.motionZ = golemSpeedCap;
               } else if (golem.motionZ < -golemSpeedCap) {
                  golem.motionZ = -golemSpeedCap;
               }
            }

            if (golemSpeedCapUpwards >= 0.0D && golem.motionY > golemSpeedCapUpwards) {
               golem.motionY = golemSpeedCapUpwards;
            }

            if (dormantSpeedFix) {
               try {
                  if (this.reflector.getIsDormant(golem)) {
                     golem.motionX = 0.0D;
                     golem.motionY = 0.0D;
                     golem.motionZ = 0.0D;
                  }
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var11) {
                  ErrorUtil.logSilent("BT getIsDormant Invocation");
               }
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onTick(WorldTickEvent tick) {
      if (tick.phase == Phase.START && ModConfig.server.battletowers.enforceTowerDestructionConfig) {
         try {
            if (this.reflector.getTowerDestroyerEnabled() != this.cachedTowerDestroyerConfig) {
               RLTweaker.logger.info("Fixing Tower Destroyer Config");
               this.reflector.setTowerDestroyerEnabled(this.cachedTowerDestroyerConfig);
            }
         } catch (IllegalAccessException | IllegalArgumentException var5) {
            ErrorUtil.logSilent("BT enforceTowerDestructionConfig Invocation");
         }
      }

      if (ModConfig.server.battletowers.towerExplosionNoCredit && System.currentTimeMillis() > this.tickedTime) {
         this.tickedTime = System.currentTimeMillis() + 14000L;

         try {
            Set<Object> towerDestroyers = this.reflector.getTowerDestroyers();
            if (towerDestroyers != null && towerDestroyers.size() > 0) {
               for(Object destroyer : towerDestroyers) {
                  if (destroyer != null) {
                     this.reflector.setDestroyerPlayer(destroyer, (Entity)null);
                  }
               }
            }
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var6) {
            ErrorUtil.logSilent("BT getTowerDestroyers Invocation");
         }
      }

   }

   @SubscribeEvent
   public void onApplyPotion(PotionApplicableEvent event) {
      if (ModConfig.server.battletowers.golemLycanitesFluidFix && this.reflector.isEntityGolem(event.getEntityLiving())) {
         PotionEffect effect = event.getPotionEffect();
         if (effect == null) {
            return;
         }

         String effectName = effect.getEffectName();
         if (effectName.equals("effect.plague")) {
            event.setResult(Result.DENY);
         }
      }

   }

   @SubscribeEvent
   public void onWorldUnload(Unload event) {
      if (event.getWorld() != null && ModConfig.server.battletowers.removeUnloadedTowerDestroyers) {
         try {
            Set<Object> towerDestroyers = this.reflector.getTowerDestroyers();
            if (towerDestroyers != null && towerDestroyers.size() > 0) {
               for(Object destroyer : towerDestroyers) {
                  if (destroyer != null && !this.reflector.getDestroyerDeleteMe(destroyer)) {
                     RLTweaker.logger.info("Removing tower destroyer for unloaded world");
                     this.reflector.setDestroyerDeleteMe(destroyer, true);
                  }
               }
            }
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
            ErrorUtil.logSilent("BT removeUnloadedTowerDestroyers Invocation");
         }
      }

   }

   public boolean isDimensionWhitelisted(World world) {
      if (!ModConfig.server.battletowers.dimensionBlacklistEnabled) {
         return true;
      } else {
         int dimension = world.provider.getDimension();
         int[] blackListIds = ModConfig.server.battletowers.dimensionBlacklistIds;
         boolean blacklistHasDimension = false;

         for(int i = 0; i < blackListIds.length; ++i) {
            if (blackListIds[i] == dimension) {
               blacklistHasDimension = true;
               break;
            }
         }

         return blacklistHasDimension == ModConfig.server.battletowers.dimensionBlacklistIsWhitelist;
      }
   }

   public class BTWorldGenerator extends WorldGeneratorWrapper {
      private int setup = -1;
      private Object worldGenHandler = null;
      private int minDistanceFromSpawn;
      private int minDistanceBetweenTowers;
      private double minDistanceBetweenTowersArea;

      public BTWorldGenerator() {
      }

      public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
         int alignment = 2;
         if (chunkX % alignment == 0) {
            if (chunkZ % alignment == 0) {
               if (this.setup == -1) {
                  try {
                     this.worldGenHandler = BattleTowersHandler.this.reflector.c_WorldGenHandler.cast(this.getWrappedGenerator());
                     this.minDistanceFromSpawn = BattleTowersHandler.this.reflector.getMinDistanceFromSpawn();
                     this.minDistanceBetweenTowers = BattleTowersHandler.this.reflector.getMinDistanceBetweenTowers();
                     this.minDistanceBetweenTowersArea = 3.141592653589793D * (double)this.minDistanceBetweenTowers * (double)this.minDistanceBetweenTowers / 1024.0D;
                     this.minDistanceBetweenTowersArea /= (double)alignment;
                     this.minDistanceBetweenTowersArea /= (double)alignment;
                     if (this.minDistanceBetweenTowersArea < 1.0D) {
                        this.minDistanceBetweenTowersArea = 1.0D;
                     }

                     this.setup = 1;
                  } catch (IllegalArgumentException | IllegalAccessException | ClassCastException var22) {
                     var22.printStackTrace();
                     this.setup = 0;
                  }
               }

               if (this.setup == 0) {
                  this.generateWrapped(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
               } else if (BattleTowersHandler.this.isDimensionWhitelisted(world)) {
                  try {
                     int blockX = chunkX * 16;
                     int blockZ = chunkZ * 16;
                     BlockPos blockPos = new BlockPos(blockX, 0, blockZ);
                     Biome biome = world.getBiome(blockPos.add(16, 0, 16));
                     BlockPos middlePos = blockPos.add(8, 0, 8);
                     if (biome != Biome.getBiome(8) && BattleTowersHandler.this.reflector.getIsBiomeAllowed(this.worldGenHandler, biome) && BattleTowersHandler.this.reflector.getIsChunkProviderAllowed(this.worldGenHandler, chunkProvider)) {
                        BlockPos spawn = world.getSpawnPoint();
                        int xx = spawn.getX() - middlePos.getX();
                        int zz = spawn.getZ() - middlePos.getZ();
                        if (Math.sqrt((double)(xx * xx + zz * zz)) < (double)this.minDistanceFromSpawn) {
                           return;
                        }

                        random.setSeed((random.nextLong() * 4455743L + 7L) * 4455743L);
                        double threshold = 1.0D / this.minDistanceBetweenTowersArea;
                        if (random.nextDouble() > threshold) {
                           return;
                        }

                        int blockY = BattleTowersHandler.this.reflector.getSurfaceBlockHeight(this.worldGenHandler, world, blockX, blockZ);
                        if (blockY <= 49) {
                           return;
                        }

                        Object towerPosition = BattleTowersHandler.this.reflector.newTowerPosition(this.worldGenHandler, blockX, 0, blockZ, 0, false);
                        world.rand.setSeed(random.nextLong());
                        boolean spawned = BattleTowersHandler.this.reflector.attemptToSpawnTower(this.worldGenHandler, world, towerPosition, random, middlePos.getX(), blockY, middlePos.getZ());
                        if (spawned) {
                           RLTweaker.logger.trace("BattleTower generation success: " + middlePos.getX() + " " + middlePos.getZ());
                        } else {
                           RLTweaker.logger.trace("BattleTower generation failure: " + middlePos.getX() + " " + middlePos.getZ());
                        }
                     }

                  } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException var21) {
                     this.generateWrapped(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                  }
               }
            }
         }
      }
   }

   public class BTWorldLoadEvent {
      private IEventListener handler;

      public BTWorldLoadEvent(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onWorldLoad(Load event) {
         if (!ModConfig.server.battletowers.consistentTowerGeneration) {
            this.handler.invoke(event);
            if (ModConfig.server.battletowers.dimensionBlacklistEnabled && !BattleTowersHandler.this.isDimensionWhitelisted(event.getWorld())) {
               boolean success = false;

               try {
                  success = BattleTowersHandler.this.reflector.setWorldDisableGenerationHook(event.getWorld(), 1000);
               } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException var4) {
                  ;
               }

               if (!success) {
                  RLTweaker.logger.error("Failed to prevent Battletowers from spawning in loaded dimension: " + event.getWorld().provider.getDimension());
                  ErrorUtil.logSilent("BT Dimension Blacklist Failure");
               }
            }

         }
      }
   }

   public class BTWorldSaveEvent {
      private IEventListener handler;

      public BTWorldSaveEvent(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onWorldSave(Save event) {
         if (!ModConfig.server.battletowers.consistentTowerGeneration) {
            this.handler.invoke(event);
         }
      }
   }

   public class GolemHurtByTarget extends EntityAIHurtByTarget {
      public GolemHurtByTarget(EntityCreature creature, boolean entityCallsForHelp, Class<?>[] excludedReinforcements) {
         super(creature, entityCallsForHelp, excludedReinforcements);
         this.shouldCheckSight = false;
      }

      public double getTargetDistance() {
         double ret = super.getTargetDistance();
         ret = 64.0D;
         return ret;
      }
   }
}
