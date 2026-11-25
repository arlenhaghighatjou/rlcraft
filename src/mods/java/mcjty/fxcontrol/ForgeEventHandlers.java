package mcjty.fxcontrol;

import java.util.HashMap;
import java.util.Map;
import mcjty.fxcontrol.rules.EffectRule;
import mcjty.fxcontrol.rules.HarvestRule;
import mcjty.fxcontrol.rules.LeftClickRule;
import mcjty.fxcontrol.rules.PlaceRule;
import mcjty.fxcontrol.rules.RightClickRule;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class ForgeEventHandlers {
   public static boolean debug = false;
   public static Map<Integer, Integer> tickCounters = new HashMap();

   public ForgeEventHandlers() {
   }

   @SubscribeEvent
   public void onRightClickEvent(RightClickBlock event) {
      if (!event.getWorld().isRemote) {
         int i = 0;

         for(RightClickRule rule : RulesManager.rightclickRules) {
            if (rule.match(event)) {
               Result result = rule.getResult();
               if (debug) {
                  FxControl.setup.getLogger().log(Level.INFO, "Rule " + i + ": " + result + " entity: " + event.getEntityPlayer().getName() + " y: " + event.getPos().getY() + " biome: " + event.getWorld().getBiome(event.getPos()).biomeName);
               }

               rule.action(event);
               event.setUseBlock(result);
               if (result == Result.DENY) {
                  event.setCanceled(true);
               }

               return;
            }

            ++i;
         }

      }
   }

   @SubscribeEvent
   public void onLeftClickEvent(LeftClickBlock event) {
      if (!event.getWorld().isRemote) {
         int i = 0;

         for(LeftClickRule rule : RulesManager.leftclickRules) {
            if (rule.match(event)) {
               Result result = rule.getResult();
               if (debug) {
                  FxControl.setup.getLogger().log(Level.INFO, "Rule " + i + ": " + result + " entity: " + event.getEntityPlayer().getName() + " y: " + event.getPos().getY() + " biome: " + event.getWorld().getBiome(event.getPos()).biomeName);
               }

               rule.action(event);
               event.setUseBlock(result);
               if (result == Result.DENY) {
                  event.setCanceled(true);
               }

               return;
            }

            ++i;
         }

      }
   }

   @SubscribeEvent
   public void onBlockPaceEvent(PlaceEvent event) {
      if (!event.getWorld().isRemote) {
         int i = 0;

         for(PlaceRule rule : RulesManager.placeRules) {
            if (rule.match(event)) {
               Result result = rule.getResult();
               if (debug) {
                  FxControl.setup.getLogger().log(Level.INFO, "Rule " + i + ": " + result + " entity: " + event.getPlayer().getName() + " y: " + event.getPos().getY() + " biome: " + event.getWorld().getBiome(event.getPos()).biomeName);
               }

               rule.action(event);
               if (result == Result.DENY) {
                  event.setCanceled(true);
               }

               return;
            }

            ++i;
         }

      }
   }

   @SubscribeEvent
   public void onBlockBreakEvent(BreakEvent event) {
      if (!event.getWorld().isRemote) {
         int i = 0;

         for(HarvestRule rule : RulesManager.harvestRules) {
            if (rule.match(event)) {
               Result result = rule.getResult();
               if (debug) {
                  FxControl.setup.getLogger().log(Level.INFO, "Rule " + i + ": " + result + " entity: " + event.getPlayer().getName() + " y: " + event.getPos().getY() + " biome: " + event.getWorld().getBiome(event.getPos()).biomeName);
               }

               rule.action(event);
               if (result == Result.DENY) {
                  event.setCanceled(true);
               }

               return;
            }

            ++i;
         }

      }
   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         if (event.side == Side.SERVER) {
            int id = event.player.getEntityId();
            if (!tickCounters.containsKey(Integer.valueOf(id))) {
               tickCounters.put(Integer.valueOf(id), Integer.valueOf(0));
            }

            int tickCounter = ((Integer)tickCounters.get(Integer.valueOf(id))).intValue() + 1;
            tickCounters.put(Integer.valueOf(id), Integer.valueOf(tickCounter));
            int i = 0;

            for(EffectRule rule : RulesManager.effectRules) {
               if (tickCounter % rule.getTimeout() == 0 && rule.match(event)) {
                  if (debug) {
                     FxControl.setup.getLogger().log(Level.INFO, "Join Rule " + i + " entity: " + event.player.getName() + " y: " + event.player.getPosition().getY());
                  }

                  rule.action(event);
                  return;
               }

               ++i;
            }

         }
      }
   }
}
