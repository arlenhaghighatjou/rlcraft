package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerDestroyItemEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;

public class MCPlayerDestroyItemEvent implements PlayerDestroyItemEvent {
   private final net.minecraftforge.event.entity.player.PlayerDestroyItemEvent event;

   public MCPlayerDestroyItemEvent(net.minecraftforge.event.entity.player.PlayerDestroyItemEvent event) {
      this.event = event;
   }

   public IItemStack getOriginalItem() {
      return CraftTweakerMC.getIItemStack(this.event.getOriginal());
   }

   public String getHand() {
      return String.valueOf(this.event.getHand());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
