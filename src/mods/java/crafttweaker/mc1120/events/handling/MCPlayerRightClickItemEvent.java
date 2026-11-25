package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerRightClickItemEvent;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class MCPlayerRightClickItemEvent extends MCPlayerInteractEvent implements PlayerRightClickItemEvent {
   private RightClickItem event;

   public MCPlayerRightClickItemEvent(RightClickItem event) {
      super(event);
      this.event = event;
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public String getCancellationResult() {
      return this.event.getCancellationResult().name();
   }

   public void setCancellationResult(String value) {
      this.event.setCancellationResult(EnumActionResult.valueOf(value));
   }
}
