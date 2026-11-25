package dev.satyrn.wolfarmor.common.event;

import dev.satyrn.wolfarmor.api.entity.IFoodStatsCreature;
import dev.satyrn.wolfarmor.entity.ai.EntityAIWolfEatFromPack;
import dev.satyrn.wolfarmor.entity.ai.EntityAIWolfHowl;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {
   public EntityEventHandler() {
   }

   @SubscribeEvent
   public void onAttachEntityAI(@Nonnull EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityWolf) {
         EntityWolf entity = (EntityWolf)event.getEntity();
         entity.tasks.addTask(8, new EntityAIWolfHowl(entity));
         entity.tasks.addTask(1, new EntityAIWolfEatFromPack(entity));
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onLivingDamage(LivingDamageEvent event) {
      if (!event.isCanceled() && event.getEntity() instanceof IFoodStatsCreature) {
         IFoodStatsCreature creacher = (IFoodStatsCreature)event.getEntity();
         if (event.getAmount() != 0.0F) {
            creacher.addExhaustion(event.getSource().getHungerDamage());
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onLivingJump(LivingJumpEvent event) {
      if (!event.isCanceled() && event.getEntity() instanceof IFoodStatsCreature) {
         Entity entity = event.getEntity();
         IFoodStatsCreature creacher = (IFoodStatsCreature)event.getEntity();
         if (entity.isSprinting()) {
            creacher.addExhaustion(0.2F);
         } else {
            creacher.addExhaustion(0.05F);
         }

      }
   }
}
