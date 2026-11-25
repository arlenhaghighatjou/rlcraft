package codersafterdark.compatskills.common.compats.minecraft.health;

import codersafterdark.reskillable.api.requirement.RequirementCache;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HealthChangeHandler {
   public HealthChangeHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onHeal(LivingHealEvent event) {
      if (event.getAmount() != 0.0F && event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         RequirementCache.invalidateCache(player, new Class[]{HealthRequirement.class, HeartRequirement.class});
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onDamaged(LivingDamageEvent event) {
      if (event.getAmount() != 0.0F && event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         RequirementCache.invalidateCache(player, new Class[]{HealthRequirement.class, HeartRequirement.class});
      }

   }
}
