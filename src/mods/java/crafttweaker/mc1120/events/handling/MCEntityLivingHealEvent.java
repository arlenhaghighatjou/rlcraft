package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingHealEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingHealEvent;

public class MCEntityLivingHealEvent implements EntityLivingHealEvent {
   private final LivingHealEvent event;

   public MCEntityLivingHealEvent(LivingHealEvent event) {
      this.event = event;
   }

   public float getAmount() {
      return this.event.getAmount();
   }

   public void setAmount(float amount) {
      this.event.setAmount(amount);
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
