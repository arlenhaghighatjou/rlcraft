package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerAdvancementEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent;

public class MCPlayerAdvancementEvent implements PlayerAdvancementEvent {
   private final AdvancementEvent event;

   public MCPlayerAdvancementEvent(AdvancementEvent event) {
      this.event = event;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }

   public String getId() {
      return this.event.getAdvancement().getId().toString();
   }
}
