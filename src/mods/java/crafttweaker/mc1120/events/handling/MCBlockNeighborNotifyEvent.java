package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.BlockNeighborNotifyEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IFacing;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;

public class MCBlockNeighborNotifyEvent extends MCBlockEvent implements BlockNeighborNotifyEvent {
   private final NeighborNotifyEvent event;

   public MCBlockNeighborNotifyEvent(NeighborNotifyEvent event) {
      super(event);
      this.event = event;
   }

   public boolean getForceRedstoneUpdate() {
      return this.event.getForceRedstoneUpdate();
   }

   public IFacing[] notifiedSides() {
      return (IFacing[])this.event.getNotifiedSides().stream().map(CraftTweakerMC::getIFacing).toArray((x$0) -> {
         return new IFacing[x$0];
      });
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
