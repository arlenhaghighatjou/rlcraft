package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.BlockFarmlandTrampleEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.world.BlockEvent.FarmlandTrampleEvent;

public class MCBlockFarmlandTrampleEvent extends MCBlockEvent implements BlockFarmlandTrampleEvent {
   private final FarmlandTrampleEvent event;

   public MCBlockFarmlandTrampleEvent(FarmlandTrampleEvent event) {
      super(event);
      this.event = event;
   }

   public IEntity getEntity() {
      return CraftTweakerMC.getIEntity(this.event.getEntity());
   }

   public float getFallDistance() {
      return this.event.getFallDistance();
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
