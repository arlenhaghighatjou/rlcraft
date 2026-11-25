package xzeroair.trinkets.util.compat.firstaid;

import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.events.EventBaseHandler;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;

public class FirstAidDamageEvent extends EventBaseHandler {
   public FirstAidDamageEvent() {
   }

   @SubscribeEvent
   public void DamageEvent(FirstAidLivingDamageEvent event) {
      Capabilities.getEntityProperties(event.getEntityLiving(), (prop) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = value.getAbility();
               if (ability instanceof IFirstAidAbility) {
                  boolean cancel = ((IFirstAidAbility)ability).firstAidHit(event.getEntityLiving(), event.getSource(), event.getUndistributedDamage(), event.getBeforeDamage(), event.getAfterDamage());
                  if (cancel) {
                     this.cancelEvent(event);
                     break;
                  }
               }
            } catch (Exception var10) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var10.printStackTrace();
            }
         }

      });
   }
}
