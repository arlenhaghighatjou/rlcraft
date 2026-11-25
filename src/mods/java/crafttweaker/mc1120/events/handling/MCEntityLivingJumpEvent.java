package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingJumpEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;

public class MCEntityLivingJumpEvent implements EntityLivingJumpEvent {
   private final LivingJumpEvent event;

   public MCEntityLivingJumpEvent(LivingJumpEvent event) {
      this.event = event;
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }
}
