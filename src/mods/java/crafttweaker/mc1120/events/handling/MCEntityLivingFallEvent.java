package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingFallEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class MCEntityLivingFallEvent implements EntityLivingFallEvent {
   private final LivingFallEvent event;

   public MCEntityLivingFallEvent(LivingFallEvent event) {
      this.event = event;
   }

   public float getDistance() {
      return this.event.getDistance();
   }

   public void setDistance(float distance) {
      this.event.setDistance(distance);
   }

   public float getDamageMultiplier() {
      return this.event.getDamageMultiplier();
   }

   public void setDamageMultiplier(float damageMultiplier) {
      this.event.setDamageMultiplier(damageMultiplier);
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }
}
