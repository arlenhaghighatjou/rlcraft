package xzeroair.trinkets.events;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.ISleepAbility;
import xzeroair.trinkets.util.TrinketsConfig;

public class PlayerEventMC {
   public PlayerEventMC() {
   }

   @SubscribeEvent
   public void startTracking(StartTracking event) {
      EntityPlayer player = event.getEntityPlayer();
      Entity entity = event.getTarget();
      Capabilities.getEntityProperties(entity, (cap) -> {
         cap.sendInformationToPlayer(player);
      });
      if (TrinketsConfig.SERVER.misc.retrieveVIP) {
         Capabilities.getVipStatus(entity, (cap) -> {
            cap.sendStatusToPlayer(player);
         });
      }

   }

   @SubscribeEvent
   public void playerRespawn(PlayerRespawnEvent event) {
      EntityPlayer player = event.player;
      boolean client = player.world.isRemote;
      if (!client && player.isEntityAlive()) {
         Capabilities.getMagicStats(player, (cap) -> {
            cap.sendManaToPlayer(player);
         });
         TrinketHelper.applyToAccessories(player, (stack) -> {
            Capabilities.getTrinketProperties(stack, (prop) -> {
               prop.turnOff();
               prop.sendInformationToPlayer(player, player);
            });
         });
      }

   }

   @SubscribeEvent
   public void playerSleep(PlayerSleepInBedEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      Capabilities.getEntityProperties(entity, (prop) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = value.getAbility();
               if (ability instanceof ISleepAbility) {
                  SleepResult defaultResult = event.getResultStatus();
                  SleepResult result = ((ISleepAbility)ability).onStartSleeping(entity, event.getPos(), event.getResultStatus());
                  if (result != null) {
                     boolean ok = true;
                     if (defaultResult != null) {
                        ok = result.compareTo(defaultResult) != 0;
                     }

                     if (ok) {
                        event.setResult(result);
                     }
                  }
               }
            } catch (Exception var12) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var12.printStackTrace();
            }
         }

      });
   }

   @SubscribeEvent
   public void playerWakeUp(PlayerWakeUpEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      Capabilities.getEntityProperties(entity, (prop) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = value.getAbility();
               if (ability instanceof ISleepAbility) {
                  ((ISleepAbility)ability).onWakeUp(entity, event.wakeImmediately(), event.updateWorld(), event.shouldSetSpawn());
               }
            } catch (Exception var9) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var9.printStackTrace();
            }
         }

      });
   }

   @SubscribeEvent
   public void playerClone(PlayerPickupXpEvent event) {
   }

   @SubscribeEvent
   public void playerClone(Clone event) {
      EntityPlayer oldPlayer = event.getOriginal();
      EntityPlayer newPlayer = event.getEntityPlayer();
      boolean wasDeath = event.isWasDeath();
      boolean keepInv = event.getOriginal().getEntityWorld().getGameRules().getBoolean("keepInventory");

      try {
         Capabilities.getVipStatus(oldPlayer, (oldVIP) -> {
            Capabilities.getVipStatus(newPlayer, (newVIP) -> {
               newVIP.copyFrom(oldVIP, wasDeath, keepInv);
            });
         });
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      try {
         Capabilities.getEntityProperties(oldPlayer, (oldProp) -> {
            Capabilities.getEntityProperties(newPlayer, (newProp) -> {
               newProp.copyFrom(oldProp, wasDeath, keepInv);
            });
         });
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      try {
         Capabilities.getMagicStats(oldPlayer, (oldMagic) -> {
            Capabilities.getMagicStats(newPlayer, (newMagic) -> {
               newMagic.copyFrom(oldMagic, wasDeath, keepInv);
            });
         });
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }
}
