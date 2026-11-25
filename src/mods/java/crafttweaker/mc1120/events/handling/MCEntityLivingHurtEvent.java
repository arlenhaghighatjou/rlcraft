package crafttweaker.mc1120.events.handling;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingHurtEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class MCEntityLivingHurtEvent implements EntityLivingHurtEvent {
   private final LivingHurtEvent event;

   public MCEntityLivingHurtEvent(LivingHurtEvent event) {
      this.event = event;
   }

   public IDamageSource getDamageSource() {
      return CraftTweakerMC.getIDamageSource(this.event.getSource());
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
