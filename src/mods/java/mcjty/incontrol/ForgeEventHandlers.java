package mcjty.incontrol;

import java.util.function.Function;
import java.util.function.Predicate;
import mcjty.incontrol.rules.ExperienceRule;
import mcjty.incontrol.rules.LootRule;
import mcjty.incontrol.rules.PotentialSpawnRule;
import mcjty.incontrol.rules.RulesManager;
import mcjty.incontrol.rules.SpawnRule;
import mcjty.incontrol.rules.SummonAidRule;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

public class ForgeEventHandlers {
   public static boolean debug = false;

   public ForgeEventHandlers() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onEntityJoinWorld(EntityJoinWorldEvent event) {
      int i = 0;
      if (event.getEntity() instanceof EntityLiving) {
         if (!event.getWorld().isRemote) {
            for(SpawnRule rule : RulesManager.rules) {
               if (rule.isOnJoin() && rule.match(event)) {
                  Result result = rule.getResult();
                  if (debug) {
                     InControl.setup.getLogger().log(Level.INFO, "Join Rule " + i + ": " + result + " entity: " + event.getEntity().getName() + " y: " + event.getEntity().getPosition().getY());
                  }

                  if (result != Result.DENY) {
                     rule.action(event);
                  } else {
                     event.setCanceled(true);
                  }

                  return;
               }

               ++i;
            }

         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onEntityJoinWorldLast(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityLiving) {
         InControl.setup.cache.registerSpawn(event.getWorld(), event.getEntity().getClass());
      }

   }

   @SubscribeEvent
   public void onWorldTick(WorldTickEvent event) {
      if (event.phase == Phase.START && !event.world.isRemote) {
         InControl.setup.cache.reset(event.world);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onEntitySpawnEvent(CheckSpawn event) {
      int i = 0;

      for(SpawnRule rule : RulesManager.rules) {
         if (rule.match(event)) {
            Result result = rule.getResult();
            if (debug) {
               InControl.setup.getLogger().log(Level.INFO, "Rule " + i + ": " + result + " entity: " + event.getEntity().getName() + " y: " + event.getY() + " biome: " + event.getWorld().getBiome(new BlockPos((double)event.getX(), (double)event.getY(), (double)event.getZ())).biomeName);
            }

            if (result != null) {
               event.setResult(result);
            }

            if (result != Result.DENY) {
               rule.action(event);
            }

            return;
         }

         ++i;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onSummonAidEvent(SummonAidEvent event) {
      int i = 0;

      for(SummonAidRule rule : RulesManager.summonAidRules) {
         if (rule.match(event)) {
            Result result = rule.getResult();
            if (debug) {
               InControl.setup.getLogger().log(Level.INFO, "SummonAid " + i + ": " + result + " entity: " + event.getEntity().getName() + " y: " + event.getY() + " biome: " + event.getWorld().getBiome(new BlockPos(event.getX(), event.getY(), event.getZ())).biomeName);
            }

            event.setResult(result);
            if (result != Result.DENY) {
               rule.action(event);
            }

            return;
         }

         ++i;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onPotentialSpawns(PotentialSpawns event) {
      int i = 0;

      for(PotentialSpawnRule rule : RulesManager.potentialSpawnRules) {
         if (rule.match(event)) {
            for(Class clazz : rule.getToRemoveMobs()) {
               for(int idx = event.getList().size() - 1; idx >= 0; --idx) {
                  if (((SpawnListEntry)event.getList().get(idx)).entityClass == clazz) {
                     event.getList().remove(idx);
                  }
               }
            }

            for(SpawnListEntry entry : rule.getSpawnEntries()) {
               if (debug) {
                  InControl.setup.getLogger().log(Level.INFO, "Potential " + i + ": " + entry.entityClass.toString());
               }

               event.getList().add(entry);
            }
         }

         ++i;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
      int i = 0;

      for(ExperienceRule rule : RulesManager.experienceRules) {
         if (rule.match(event)) {
            Result result = rule.getResult();
            if (debug) {
               InControl.setup.getLogger().log(Level.INFO, "Experience Rule " + i + ": " + result + " entity: " + event.getEntity().getName() + " y: " + event.getEntity().getPosition().getY());
            }

            if (result != Result.DENY) {
               int newxp = rule.modifyXp(event.getDroppedExperience());
               event.setDroppedExperience(newxp);
            } else {
               event.setCanceled(true);
            }

            return;
         }

         ++i;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onLivingDrops(LivingDropsEvent event) {
      int i = 0;

      for(LootRule rule : RulesManager.lootRules) {
         if (rule.match(event)) {
            if (debug) {
               InControl.setup.getLogger().log(Level.INFO, "Loot " + i + ":  entity: " + event.getEntity().getName());
            }

            if (rule.isRemoveAll()) {
               event.getDrops().clear();
            } else {
               for(Predicate<ItemStack> stackTest : rule.getToRemoveItems()) {
                  for(int idx = event.getDrops().size() - 1; idx >= 0; --idx) {
                     ItemStack stack = ((EntityItem)event.getDrops().get(idx)).getItem();
                     if (stackTest.test(stack)) {
                        event.getDrops().remove(idx);
                     }
                  }
               }
            }

            for(Pair<ItemStack, Function<Integer, Integer>> pair : rule.getToAddItems()) {
               ItemStack item = (ItemStack)pair.getLeft();
               int fortune = event.getLootingLevel();
               int amount = ((Integer)((Function)pair.getValue()).apply(Integer.valueOf(fortune))).intValue();
               BlockPos pos = event.getEntity().getPosition();

               while(amount > item.getMaxStackSize()) {
                  ItemStack copy = item.copy();
                  copy.setCount(item.getMaxStackSize());
                  amount -= item.getMaxStackSize();
                  event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), copy));
               }

               if (amount > 0) {
                  ItemStack copy = item.copy();
                  copy.setCount(amount);
                  event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), copy));
               }
            }
         }

         ++i;
      }

   }
}
