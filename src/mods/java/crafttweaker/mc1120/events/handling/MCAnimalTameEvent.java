package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityAnimal;
import crafttweaker.api.event.AnimalTameEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;

public class MCAnimalTameEvent implements AnimalTameEvent {
   private final net.minecraftforge.event.entity.living.AnimalTameEvent event;

   public MCAnimalTameEvent(net.minecraftforge.event.entity.living.AnimalTameEvent event) {
      this.event = event;
   }

   public IEntityAnimal getAnimal() {
      return CraftTweakerMC.getIEntityAnimal(this.event.getAnimal());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getTamer());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
