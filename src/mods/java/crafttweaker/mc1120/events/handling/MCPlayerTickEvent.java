package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerTickEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class MCPlayerTickEvent implements PlayerTickEvent {
   private final net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent event;

   /** @deprecated */
   @Deprecated
   public MCPlayerTickEvent(IPlayer player, String phase) {
      this(new net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent(Phase.valueOf(phase), CraftTweakerMC.getPlayer(player)));
   }

   public MCPlayerTickEvent(net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent event) {
      this.event = event;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.player);
   }

   public String getSide() {
      return this.event.side.name().toUpperCase();
   }

   public String getPhase() {
      return this.event.phase.name().toUpperCase();
   }
}
