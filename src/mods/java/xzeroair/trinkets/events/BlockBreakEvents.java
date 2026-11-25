package xzeroair.trinkets.events;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IMiningAbility;

public class BlockBreakEvents extends EventBaseHandler {
   public BlockBreakEvents() {
   }

   @SubscribeEvent
   public void BreakSpeed(BreakSpeed event) {
      Capabilities.getEntityProperties(event.getEntityLiving(), (prop) -> {
         prop.getRaceHandler().breakingBlock(event);
         if (!event.isCanceled()) {
            float newOriginal = event.getNewSpeed();
            float newSpeed = event.getNewSpeed();
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

            for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
               if (newSpeed <= 0.0F || newSpeed >= 100.0F) {
                  break;
               }

               String key = (String)entry.getKey();
               AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

               try {
                  IAbilityInterface ability = holder.getAbility();
                  if (ability instanceof IMiningAbility) {
                     newSpeed = ((IMiningAbility)ability).breakingBlock(event.getEntityLiving(), event.getState(), event.getPos(), newOriginal, newSpeed);
                  }
               } catch (Exception var11) {
                  Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                  var11.printStackTrace();
               }
            }

            if (newSpeed != newOriginal) {
               if (newSpeed <= 0.0F) {
                  this.cancelEvent(event);
               } else {
                  event.setNewSpeed(newSpeed);
               }
            }
         }

      });
   }

   @SubscribeEvent
   public void breakEvent(BreakEvent event) {
      EntityPlayer player = event.getPlayer();
      Capabilities.getEntityProperties(player, (prop) -> {
         prop.getRaceHandler().blockBroken(event);
         if (!event.isCanceled()) {
            int defaultExp = event.getExpToDrop();
            int exp = event.getExpToDrop();
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

            for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
               if (exp < 0) {
                  break;
               }

               String key = (String)entry.getKey();
               AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

               try {
                  IAbilityInterface ability = holder.getAbility();
                  if (ability instanceof IMiningAbility) {
                     int result = ((IMiningAbility)ability).brokeBlock(player, event.getWorld(), event.getState(), event.getPos(), exp);
                     exp = result;
                  }
               } catch (Exception var13) {
                  Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                  var13.printStackTrace();
               }

               if (exp < 0) {
                  break;
               }
            }

            if (exp != defaultExp) {
               if (exp < 0) {
                  this.cancelEvent(event);
               } else {
                  event.setExpToDrop(exp);
               }
            }
         }

      });
   }

   @SubscribeEvent
   public void blockDrops(HarvestDropsEvent event) {
      Capabilities.getEntityProperties(event.getHarvester(), (prop) -> {
         prop.getRaceHandler().blockDrops(event);
         if (!event.isCanceled()) {
            float defaultChance = event.getDropChance();
            float dropChance = event.getDropChance();
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

            for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
               if (dropChance <= 0.0F) {
                  break;
               }

               String key = (String)entry.getKey();
               AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

               try {
                  IAbilityInterface ability = holder.getAbility();
                  if (ability instanceof IMiningAbility) {
                     dropChance = ((IMiningAbility)ability).blockDrops(event.getHarvester(), event.getWorld(), event.getState(), event.getPos(), event.getDrops(), dropChance, event.isSilkTouching(), event.getFortuneLevel());
                  }
               } catch (Exception var11) {
                  Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                  var11.printStackTrace();
               }
            }

            if (dropChance != defaultChance) {
               if (dropChance <= 0.0F) {
                  this.cancelEvent(event);
               } else {
                  event.setDropChance(dropChance);
               }
            }
         }

      });
   }
}
