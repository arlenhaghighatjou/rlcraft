package com.lycanitesmobs.core.mobevent;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.mobevent.trigger.AltarMobEventTrigger;
import com.lycanitesmobs.core.mobevent.trigger.MobEventTrigger;
import com.lycanitesmobs.core.mobevent.trigger.RandomMobEventTrigger;
import com.lycanitesmobs.core.mobevent.trigger.TickMobEventTrigger;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class MobEventListener {
   protected static MobEventListener INSTANCE;
   public List<RandomMobEventTrigger> randomMobEventTriggers = new ArrayList();
   public List<TickMobEventTrigger> tickMobEventTriggers = new ArrayList();

   public MobEventListener() {
   }

   public static MobEventListener getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new MobEventListener();
      }

      return INSTANCE;
   }

   public boolean addTrigger(MobEventTrigger mobEventTrigger) {
      if (mobEventTrigger instanceof RandomMobEventTrigger && !this.randomMobEventTriggers.contains(mobEventTrigger)) {
         this.randomMobEventTriggers.add((RandomMobEventTrigger)mobEventTrigger);
         return true;
      } else if (mobEventTrigger instanceof TickMobEventTrigger && !this.tickMobEventTriggers.contains(mobEventTrigger)) {
         this.tickMobEventTriggers.add((TickMobEventTrigger)mobEventTrigger);
         return true;
      } else {
         return false;
      }
   }

   public void removeTrigger(MobEventTrigger mobEventTrigger) {
      if (this.randomMobEventTriggers.contains(mobEventTrigger)) {
         this.randomMobEventTriggers.remove(mobEventTrigger);
      }

      if (this.tickMobEventTriggers.contains(mobEventTrigger)) {
         this.tickMobEventTriggers.remove(mobEventTrigger);
      }

      if (mobEventTrigger instanceof AltarMobEventTrigger) {
         AltarMobEventTrigger altarMobEventTrigger = (AltarMobEventTrigger)mobEventTrigger;
         altarMobEventTrigger.onRemove();
      }

   }

   public void register(EventBus bus) {
      bus.register(WorldTickEvent.class, this::onWorldUpdate);
   }

   public void onWorldUpdate(WorldTickEvent event) {
      World world = event.world;
      if (!world.isRemote) {
         ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
         if (worldExt != null) {
            if (MobEventManager.getInstance().mobEventsEnabled && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
               if (worldExt.lastEventScheduleTime != world.getTotalWorldTime()) {
                  worldExt.lastEventScheduleTime = world.getTotalWorldTime();

                  for(MobEventSchedule mobEventSchedule : MobEventManager.getInstance().mobEventSchedules) {
                     if (mobEventSchedule.canStart(world)) {
                        mobEventSchedule.start(worldExt);
                     }
                  }

                  for(TickMobEventTrigger mobEventTrigger : this.tickMobEventTriggers) {
                     mobEventTrigger.onTick(world, worldExt.lastEventScheduleTime);
                  }

                  if (MobEventManager.getInstance().mobEventsRandom) {
                     if (MobEventManager.getInstance().minEventsRandomDay > 0 && Math.floor((double)(worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 24000.0D) < (double)MobEventManager.getInstance().minEventsRandomDay) {
                        return;
                     }

                     if (worldExt.getWorldEventStartTargetTime() <= 0L || worldExt.getWorldEventStartTargetTime() > world.getTotalWorldTime() + (long)MobEventManager.getInstance().maxTicksUntilEvent) {
                        worldExt.setWorldEventStartTargetTime(world.getTotalWorldTime() + (long)worldExt.getRandomEventDelay(world.rand));
                     }

                     if (world.getTotalWorldTime() == worldExt.getWorldEventStartTargetTime()) {
                        this.triggerRandomMobEvent(world, worldExt);
                     } else if (world.getTotalWorldTime() > worldExt.getWorldEventStartTargetTime()) {
                        worldExt.setWorldEventStartTargetTime(0L);
                     }
                  }

               }
            } else {
               if (worldExt.serverWorldEventPlayer != null) {
                  worldExt.stopWorldEvent();
               }

            }
         }
      }
   }

   public void triggerRandomMobEvent(World world, ExtendedWorld worldExt) {
      List<RandomMobEventTrigger> validTriggers = new ArrayList();
      int totalWeights = 0;
      int highestPriority = 0;

      for(RandomMobEventTrigger mobEventTrigger : this.randomMobEventTriggers) {
         if (mobEventTrigger.priority >= highestPriority && mobEventTrigger.canTrigger(world, (EntityPlayer)null)) {
            if (mobEventTrigger.priority > highestPriority) {
               totalWeights = 0;
               validTriggers.clear();
            }

            totalWeights += mobEventTrigger.weight;
            highestPriority = mobEventTrigger.priority;
            validTriggers.add(mobEventTrigger);
         }
      }

      if (totalWeights > 0) {
         int randomWeight = 1;
         if (totalWeights > 1) {
            randomWeight = world.rand.nextInt(totalWeights - 1) + 1;
         }

         int searchWeight = 0;

         for(RandomMobEventTrigger mobEventTrigger : validTriggers) {
            if (mobEventTrigger.weight + searchWeight > randomWeight) {
               mobEventTrigger.trigger(world, (EntityPlayer)null, new BlockPos(0, 0, 0), 1, -1);
               return;
            }

            searchWeight += mobEventTrigger.weight;
         }

      }
   }
}
