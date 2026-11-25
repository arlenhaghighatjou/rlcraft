package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PortalSpawnEvent;

public class MCPortalSpawnEvent extends MCBlockEvent implements PortalSpawnEvent {
   private final net.minecraftforge.event.world.BlockEvent.PortalSpawnEvent event;

   public MCPortalSpawnEvent(net.minecraftforge.event.world.BlockEvent.PortalSpawnEvent event) {
      super(event);
      this.event = event;
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public boolean isValid() {
      return this.event.getPortalSize().isValid();
   }

   public int getHeight() {
      return this.event.getPortalSize().getHeight();
   }

   public int getWidth() {
      return this.event.getPortalSize().getWidth();
   }
}
