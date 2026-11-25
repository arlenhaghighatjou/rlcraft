package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.MobGriefingEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCMobGriefingEvent implements MobGriefingEvent {
   private EntityMobGriefingEvent event;

   public MCMobGriefingEvent(EntityMobGriefingEvent event) {
      this.event = event;
   }

   public IEntity getEntity() {
      return CraftTweakerMC.getIEntity(this.event.getEntity());
   }

   public String getResult() {
      return String.valueOf(this.event.getResult());
   }

   public void setDenied() {
      this.event.setResult(Result.DENY);
   }

   public void setDefault() {
      this.event.setResult(Result.DEFAULT);
   }

   public void setAllowed() {
      this.event.setResult(Result.ALLOW);
   }
}
