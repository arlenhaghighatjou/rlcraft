package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerVisibilityEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.Visibility;

public class MCPlayerVisibilityEvent implements PlayerVisibilityEvent {
   private Visibility event;

   public MCPlayerVisibilityEvent(Visibility event) {
      this.event = event;
   }

   public void modifyVisibility(double modifier) {
      this.event.modifyVisibility(modifier);
   }

   public double getModifier() {
      return this.event.getVisibilityModifier();
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
