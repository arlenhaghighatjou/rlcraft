package com.lycanitesmobs.core.spawner;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.spawner.trigger.BlockSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.ChunkSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.EntitySpawnedSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.ExplosionSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.FishingSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.KillSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.MixBlockSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.MobEventSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.PlayerSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.SleepSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.SpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.WorldSpawnTrigger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Post;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.FluidPlaceBlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class SpawnerEventListener {
   protected static SpawnerEventListener INSTANCE;
   public static boolean testOnCreative = false;
   public List<WorldSpawnTrigger> worldSpawnTriggers = new ArrayList();
   public List<PlayerSpawnTrigger> playerSpawnTriggers = new ArrayList();
   public List<KillSpawnTrigger> killSpawnTriggers = new ArrayList();
   public List<EntitySpawnedSpawnTrigger> entitySpawnedSpawnTriggers = new ArrayList();
   public List<ChunkSpawnTrigger> chunkSpawnTriggers = new ArrayList();
   public List<BlockSpawnTrigger> blockSpawnTriggers = new ArrayList();
   public List<SleepSpawnTrigger> sleepSpawnTriggers = new ArrayList();
   public List<FishingSpawnTrigger> fishingSpawnTriggers = new ArrayList();
   public List<ExplosionSpawnTrigger> explosionSpawnTriggers = new ArrayList();
   public List<MobEventSpawnTrigger> mobEventSpawnTriggers = new ArrayList();
   public List<MixBlockSpawnTrigger> mixBlockSpawnTriggers = new ArrayList();
   private final Map<EntityPlayer, Long> playerUpdateTicks = new WeakHashMap();
   public boolean chunkSpawnTriggersActive = false;

   public SpawnerEventListener() {
   }

   public static SpawnerEventListener getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new SpawnerEventListener();
      }

      return INSTANCE;
   }

   public boolean addTrigger(SpawnTrigger spawnTrigger) {
      if (spawnTrigger instanceof WorldSpawnTrigger && !this.worldSpawnTriggers.contains(spawnTrigger)) {
         this.worldSpawnTriggers.add((WorldSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof PlayerSpawnTrigger && !this.playerSpawnTriggers.contains(spawnTrigger)) {
         this.playerSpawnTriggers.add((PlayerSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof KillSpawnTrigger && !this.killSpawnTriggers.contains(spawnTrigger)) {
         this.killSpawnTriggers.add((KillSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof EntitySpawnedSpawnTrigger && !this.entitySpawnedSpawnTriggers.contains(spawnTrigger)) {
         this.entitySpawnedSpawnTriggers.add((EntitySpawnedSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof ChunkSpawnTrigger && !this.chunkSpawnTriggers.contains(spawnTrigger)) {
         this.chunkSpawnTriggers.add((ChunkSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof MixBlockSpawnTrigger && !this.mixBlockSpawnTriggers.contains(spawnTrigger)) {
         this.mixBlockSpawnTriggers.add((MixBlockSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof SleepSpawnTrigger && !this.sleepSpawnTriggers.contains(spawnTrigger)) {
         this.sleepSpawnTriggers.add((SleepSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof BlockSpawnTrigger && !this.blockSpawnTriggers.contains(spawnTrigger)) {
         this.blockSpawnTriggers.add((BlockSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof FishingSpawnTrigger && !this.fishingSpawnTriggers.contains(spawnTrigger)) {
         this.fishingSpawnTriggers.add((FishingSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof ExplosionSpawnTrigger && !this.explosionSpawnTriggers.contains(spawnTrigger)) {
         this.explosionSpawnTriggers.add((ExplosionSpawnTrigger)spawnTrigger);
         return true;
      } else if (spawnTrigger instanceof MobEventSpawnTrigger && !this.mobEventSpawnTriggers.contains(spawnTrigger)) {
         this.mobEventSpawnTriggers.add((MobEventSpawnTrigger)spawnTrigger);
         return true;
      } else {
         return false;
      }
   }

   public void removeTrigger(SpawnTrigger spawnTrigger) {
      if (this.worldSpawnTriggers.contains(spawnTrigger)) {
         this.worldSpawnTriggers.remove(spawnTrigger);
      }

      if (this.playerSpawnTriggers.contains(spawnTrigger)) {
         this.playerSpawnTriggers.remove(spawnTrigger);
      }

      if (this.killSpawnTriggers.contains(spawnTrigger)) {
         this.killSpawnTriggers.remove(spawnTrigger);
      }

      if (this.entitySpawnedSpawnTriggers.contains(spawnTrigger)) {
         this.entitySpawnedSpawnTriggers.remove(spawnTrigger);
      }

      if (this.chunkSpawnTriggers.contains(spawnTrigger)) {
         this.chunkSpawnTriggers.remove(spawnTrigger);
      }

      if (this.blockSpawnTriggers.contains(spawnTrigger)) {
         this.blockSpawnTriggers.remove(spawnTrigger);
      }

      if (this.sleepSpawnTriggers.contains(spawnTrigger)) {
         this.sleepSpawnTriggers.remove(spawnTrigger);
      }

      if (this.fishingSpawnTriggers.contains(spawnTrigger)) {
         this.fishingSpawnTriggers.remove(spawnTrigger);
      }

      if (this.explosionSpawnTriggers.contains(spawnTrigger)) {
         this.explosionSpawnTriggers.remove(spawnTrigger);
      }

      if (this.mobEventSpawnTriggers.contains(spawnTrigger)) {
         this.mobEventSpawnTriggers.remove(spawnTrigger);
      }

      if (this.mixBlockSpawnTriggers.contains(spawnTrigger)) {
         this.mixBlockSpawnTriggers.remove(spawnTrigger);
      }

   }

   @SubscribeEvent
   public void onWorldUpdate(WorldTickEvent event) {
      World world = event.world;
      if (!world.isRemote) {
         ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
         if (worldExt != null) {
            if (worldExt.lastSpawnerTime != world.getTotalWorldTime()) {
               worldExt.lastSpawnerTime = world.getTotalWorldTime();
               List<BlockPos> triggerPositions = new ArrayList();

               for(EntityPlayer player : world.playerEntities) {
                  if (triggerPositions.isEmpty()) {
                     triggerPositions.add(player.getPosition());
                  } else {
                     boolean nearOtherPlayers = false;

                     for(BlockPos triggerPosition : triggerPositions) {
                        if (player.getDistanceSq(triggerPosition) <= 10000.0D) {
                           nearOtherPlayers = true;
                        }
                     }

                     if (!nearOtherPlayers) {
                        triggerPositions.add(player.getPosition());
                     }
                  }
               }

               for(BlockPos triggerPosition : triggerPositions) {
                  for(WorldSpawnTrigger spawnTrigger : this.worldSpawnTriggers) {
                     spawnTrigger.onTick(world, triggerPosition, worldExt.lastSpawnerTime);
                  }
               }

               if (worldExt.getWorldEvent() != null) {
                  for(MobEventSpawnTrigger spawnTrigger : this.mobEventSpawnTriggers) {
                     spawnTrigger.onTick(world, worldExt.serverWorldEventPlayer);
                  }
               }

            }
         }
      }
   }

   @SubscribeEvent
   public void onPlayerTickEvent(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         EntityPlayer player = event.player;
         if (!player.world.isRemote) {
            long entityUpdateTick = ((Long)this.playerUpdateTicks.compute(player, (k, v) -> {
               return v != null ? v.longValue() + 1L : 1L;
            })).longValue() - 1L;
            int tickOffset = 0;

            for(PlayerSpawnTrigger spawnTrigger : this.playerSpawnTriggers) {
               spawnTrigger.onTick(player, entityUpdateTick - (long)tickOffset);
               tickOffset += 105;
            }

         }
      }
   }

   @SubscribeEvent
   public void onEntityDeath(LivingDeathEvent event) {
      EntityLivingBase killedEntity = event.getEntityLiving();
      if (killedEntity != null && killedEntity.getEntityWorld() != null && !killedEntity.getEntityWorld().isRemote && !event.isCanceled() && killedEntity instanceof EntityLiving) {
         Entity killerEntity = event.getSource().getTrueSource();
         if (killerEntity instanceof EntityPlayer) {
            for(KillSpawnTrigger spawnTrigger : this.killSpawnTriggers) {
               spawnTrigger.onKill((EntityPlayer)killerEntity, (EntityLiving)killedEntity);
            }

         }
      }
   }

   @SubscribeEvent
   public void onEntitySpawn(LivingSpawnEvent event) {
      EntityLivingBase spawnedEntity = event.getEntityLiving();
      if (spawnedEntity != null && spawnedEntity.getEntityWorld() != null && !spawnedEntity.getEntityWorld().isRemote && !event.isCanceled() && spawnedEntity instanceof EntityLiving) {
         for(EntitySpawnedSpawnTrigger spawnTrigger : this.entitySpawnedSpawnTriggers) {
            spawnTrigger.onEntitySpawned((EntityLiving)spawnedEntity);
         }

      }
   }

   @SubscribeEvent
   public void onChunkPopulate(Post event) {
      if (!this.chunkSpawnTriggersActive) {
         for(ChunkSpawnTrigger spawnTrigger : this.chunkSpawnTriggers) {
            if (spawnTrigger.onChunkPopulate(event.getWorld(), new ChunkPos(event.getChunkX(), event.getChunkZ()))) {
               this.chunkSpawnTriggersActive = true;
            }
         }

         this.chunkSpawnTriggersActive = false;
      }
   }

   @SubscribeEvent
   public void onHarvestDrops(HarvestDropsEvent event) {
      EntityPlayer player = event.getHarvester();
      if (event.getState() != null && event.getWorld() != null && !event.getWorld().isRemote && !event.isCanceled()) {
         if (player == null || testOnCreative || !player.capabilities.isCreativeMode) {
            World world = event.getWorld();
            BlockPos blockPos = event.getPos();
            IBlockState blockState = event.getState();

            for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
               spawnTrigger.onBlockHarvest(world, player, blockPos, blockState, 0, event.getFortuneLevel(), event.isSilkTouching());
            }

         }
      }
   }

   @SubscribeEvent
   public void onBlockBreak(BreakEvent event) {
      if (event.getState() != null && event.getWorld() != null && !event.getWorld().isRemote && !event.isCanceled()) {
         this.onBlockBreak(event.getWorld(), event.getPos(), event.getState(), event.getPlayer(), 0);
      }
   }

   public void onBlockBreak(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer player, int chain) {
      if (player == null || testOnCreative || !player.capabilities.isCreativeMode) {
         for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
            spawnTrigger.onBlockBreak(world, player, blockPos, blockState, chain);
         }

      }
   }

   @SubscribeEvent
   public void onBlockPlace(PlaceEvent event) {
      if (event.getState() != null && event.getWorld() != null && !event.getWorld().isRemote && !event.isCanceled()) {
         this.onBlockPlace(event.getWorld(), event.getPos(), event.getState(), event.getPlayer(), 0);
      }
   }

   public void onBlockPlace(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer player, int chain) {
      if (player == null || testOnCreative || !player.capabilities.isCreativeMode) {
         for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
            spawnTrigger.onBlockPlace(world, player, blockPos, blockState, chain);
         }

      }
   }

   @SubscribeEvent
   public void onSleep(PlayerSleepInBedEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null && player.getEntityWorld() != null && !player.getEntityWorld().isRemote && !event.isCanceled()) {
         World world = player.getEntityWorld();
         BlockPos spawnPos = event.getPos().add(0, 0, 1);
         if (world != null && !world.isRemote && !world.provider.isDaytime()) {
            boolean interrupted = false;

            for(SleepSpawnTrigger spawnTrigger : this.sleepSpawnTriggers) {
               if (spawnTrigger.onSleep(world, player, spawnPos, world.getBlockState(event.getPos()))) {
                  interrupted = true;
               }
            }

            if (interrupted) {
               event.setResult(SleepResult.NOT_SAFE);
            }

         }
      }
   }

   @SubscribeEvent
   public void onFished(ItemFishedEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null && player.getEntityWorld() != null && !player.getEntityWorld().isRemote && !event.isCanceled()) {
         if (player == null || testOnCreative || !player.capabilities.isCreativeMode) {
            World world = player.getEntityWorld();
            Entity hookEntity = event.getHookEntity();

            for(FishingSpawnTrigger spawnTrigger : this.fishingSpawnTriggers) {
               spawnTrigger.onFished(world, player, hookEntity);
            }

         }
      }
   }

   @SubscribeEvent
   public void onExplosion(Detonate event) {
      Explosion explosion = event.getExplosion();
      if (explosion != null) {
         World world = event.getWorld();
         EntityPlayer player = null;
         if (explosion.getExplosivePlacedBy() instanceof EntityPlayer) {
            player = (EntityPlayer)explosion.getExplosivePlacedBy();
         }

         if (player == null || testOnCreative || !player.capabilities.isCreativeMode) {
            for(ExplosionSpawnTrigger spawnTrigger : this.explosionSpawnTriggers) {
               spawnTrigger.onExplosion(world, player, explosion);
            }

         }
      }
   }

   @SubscribeEvent
   public void onLavaMix(FluidPlaceBlockEvent event) {
      if (event.getWorld() != null) {
         World world = event.getWorld();
         if (event.getOriginalState().getBlock() == Blocks.LAVA && event.getNewState().getBlock() == Blocks.OBSIDIAN) {
            for(MixBlockSpawnTrigger spawnTrigger : this.mixBlockSpawnTriggers) {
               spawnTrigger.onMix(world, event.getState(), event.getLiquidPos());
            }
         }

      }
   }
}
