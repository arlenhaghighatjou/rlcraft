package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.PlayerAttackEntityEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class MCPlayerAttackEntityEvent implements PlayerAttackEntityEvent {
   private final AttackEntityEvent event;

   public MCPlayerAttackEntityEvent(AttackEntityEvent event) {
      this.event = event;
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public IEntity getTarget() {
      return CraftTweakerMC.getIEntity(this.event.getTarget());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
