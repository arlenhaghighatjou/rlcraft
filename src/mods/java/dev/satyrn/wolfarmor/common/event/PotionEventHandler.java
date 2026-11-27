package dev.satyrn.wolfarmor.common.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import dev.satyrn.wolfarmor.api.entity.IFoodStatsCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;

public class PotionEventHandler {
   public PotionEventHandler() {
   }

   public void register(EventBus bus) {
      bus.register(PotionEffectEvent.class, this::onPotionEffectPreEvent);
   }

   public void onPotionEffectPreEvent(PotionEffectEvent event) {
      if (event.getEntity() instanceof IFoodStatsCreature) {
         IFoodStatsCreature creacher = (IFoodStatsCreature)event.getEntity();
         EntityLivingBase entity = event.getEntity();
         if (event.getPotion() == MobEffects.HUNGER) {
            creacher.addExhaustion(0.005F * ((float)event.getAmplifier() + 1.0F));
            event.setCanceled(true);
         } else if (event.getPotion() == MobEffects.SATURATION) {
            if (!entity.getEntityWorld().isRemote) {
               creacher.getFoodStats().addStats(event.getAmplifier() + 1, 1.0F);
            }

            event.setCanceled(true);
         }
      }

   }
}
