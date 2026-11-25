package xzeroair.trinkets.events;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.attributes.JumpAttribute;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IJumpAbility;
import xzeroair.trinkets.util.TrinketsConfig;

public class MovementHandler extends EventBaseHandler {
   public MovementHandler() {
   }

   @SubscribeEvent
   public void onCollideWithBlock(PlayerSPPushOutOfBlocksEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      Capabilities.getEntityProperties(player, (prop) -> {
         if (!prop.isNormalSize()) {
            event.setCanceled(true);
         }

      });
   }

   @SubscribeEvent
   public void livingJump(LivingJumpEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      IAttributeInstance attribute = entity.getAttributeMap().getAttributeInstance(JumpAttribute.Jump);
      if (attribute != null && !attribute.getModifiers().isEmpty()) {
         entity.motionY += attribute.getAttributeValue() - 0.41999998688697815D;
         if (entity.isSprinting()) {
            entity.motionX *= attribute.getAttributeValue() / 0.41999998688697815D;
            entity.motionZ *= attribute.getAttributeValue() / 0.41999998688697815D;
         }
      }

      Capabilities.getEntityProperties(entity, (prop) -> {
         if (TrinketsConfig.SERVER.misc.movement && prop.getRaceHandler().isTransforming()) {
            entity.motionY = 0.0D;
         } else {
            prop.getRaceHandler().jump();
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

            for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
               String key = (String)entry.getKey();
               AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

               try {
                  IAbilityInterface ability = value.getAbility();
                  if (ability instanceof IJumpAbility) {
                     ((IJumpAbility)ability).jump(entity);
                  }
               } catch (Exception var8) {
                  Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                  var8.printStackTrace();
               }
            }

         }
      });
   }

   @SubscribeEvent
   public void livingFall(LivingFallEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      float baseDistance = event.getDistance();
      float baseMultiplier = event.getDamageMultiplier();
      IAttributeInstance attribute = entity.getAttributeMap().getAttributeInstance(JumpAttribute.Jump);
      if (attribute != null) {
         double value = attribute.getAttributeValue();
         float multi = (float)(value / 0.41999998688697815D);
         float distance = 3.0F * (multi - 1.0F);
         event.setDistance(baseDistance - distance);
      }

      boolean cancel = ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(false), (prop, bool) -> {
         float fallDistance = event.getDistance();
         float damageMultiplier = event.getDamageMultiplier();
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = value.getAbility();
               if (ability instanceof IJumpAbility) {
                  IJumpAbility fall = (IJumpAbility)ability;
                  float abilityDistance = fall.fallDistance(entity, fallDistance);
                  if (fallDistance != abilityDistance) {
                     fallDistance = abilityDistance;
                  }

                  float abilityModifier = fall.fallDamageMultiplier(entity, damageMultiplier);
                  if (damageMultiplier != abilityModifier) {
                     damageMultiplier = abilityModifier;
                  }

                  boolean abilityCancel = fall.fall(entity, fallDistance, damageMultiplier, bool.booleanValue());
                  if (bool.booleanValue() != abilityCancel) {
                     bool = abilityCancel;
                  }
               }
            } catch (Exception var18) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var18.printStackTrace();
            }
         }

         if (fallDistance != baseDistance) {
            event.setDistance(MathHelper.clamp(fallDistance, 0.0F, fallDistance));
         }

         if (damageMultiplier != baseMultiplier) {
            event.setDamageMultiplier(MathHelper.clamp(damageMultiplier, 0.0F, damageMultiplier));
         }

         prop.getRaceHandler().fall(event);
         return event.isCanceled() || event.getDistance() <= 0.0F || event.getDamageMultiplier() <= 0.0F || bool.booleanValue();
      })).booleanValue();
      if (cancel) {
         this.cancelEvent(event);
      }

   }
}
