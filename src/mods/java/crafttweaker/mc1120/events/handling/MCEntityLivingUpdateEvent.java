package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingUpdateEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class MCEntityLivingUpdateEvent implements EntityLivingUpdateEvent {
   private final LivingUpdateEvent event;

   public MCEntityLivingUpdateEvent(LivingUpdateEvent event) {
      this.event = event;
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
