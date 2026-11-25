package crafttweaker.mc1120.events.handling;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingDeathEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class MCEntityLivingDeathEvent implements EntityLivingDeathEvent {
   private final LivingDeathEvent event;

   public MCEntityLivingDeathEvent(LivingDeathEvent event) {
      this.event = event;
   }

   public IDamageSource getDamageSource() {
      return CraftTweakerMC.getIDamageSource(this.event.getSource());
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
