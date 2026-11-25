package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.CriticalHitEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCCriticalHitEvent implements CriticalHitEvent {
   private net.minecraftforge.event.entity.player.CriticalHitEvent event;

   public MCCriticalHitEvent(net.minecraftforge.event.entity.player.CriticalHitEvent event) {
      this.event = event;
   }

   public IEntity getTarget() {
      return CraftTweakerMC.getIEntity(this.event.getTarget());
   }

   public float getDamageModifier() {
      return this.event.getDamageModifier();
   }

   public void setDamageModifier(float modifier) {
      this.event.setDamageModifier(modifier);
   }

   public float getOldDamageModifier() {
      return this.event.getOldDamageModifier();
   }

   public boolean isVanillaCrit() {
      return this.event.isVanillaCritical();
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

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
