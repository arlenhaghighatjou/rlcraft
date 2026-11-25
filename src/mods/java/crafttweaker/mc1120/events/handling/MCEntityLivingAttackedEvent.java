package crafttweaker.mc1120.events.handling;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingAttackedEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class MCEntityLivingAttackedEvent implements EntityLivingAttackedEvent {
   private final LivingAttackEvent event;

   public MCEntityLivingAttackedEvent(LivingAttackEvent event) {
      this.event = event;
   }

   public IDamageSource getDamageSource() {
      return CraftTweakerMC.getIDamageSource(this.event.getSource());
   }

   public float getAmount() {
      return this.event.getAmount();
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
