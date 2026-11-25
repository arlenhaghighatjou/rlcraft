package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.BlockPlaceEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

public class MCBlockPlaceEvent extends MCBlockEvent implements BlockPlaceEvent {
   private PlaceEvent event;

   public MCBlockPlaceEvent(PlaceEvent event) {
      super(event);
      this.event = event;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getPlayer());
   }

   public IBlockState getCurrent() {
      return CraftTweakerMC.getBlockState(this.event.getPlacedBlock());
   }

   public IBlockState getPlacedAgainst() {
      return CraftTweakerMC.getBlockState(this.event.getPlacedAgainst());
   }

   public IBlock getBlock() {
      return this.getBlockState().getBlock();
   }

   public String getHand() {
      return String.valueOf(this.event.getHand());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
