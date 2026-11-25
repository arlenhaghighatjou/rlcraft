package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.CropGrowPreEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCCropGrowPreEvent extends MCBlockEvent implements CropGrowPreEvent {
   private Pre event;

   public MCCropGrowPreEvent(Pre event) {
      super(event);
      this.event = event;
   }

   public String getResult() {
      return String.valueOf(this.event.getResult());
   }

   public void setDenied() {
      this.event.setResult(Result.DENY);
   }

   public void setDefault() {
      this.event.setResult(Result.DEFAULT);
   }

   public void setAllowed() {
      this.event.setResult(Result.ALLOW);
   }
}
