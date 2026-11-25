package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.CropGrowPostEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent.Post;

public class MCCropGrowPostEvent extends MCBlockEvent implements CropGrowPostEvent {
   private Post event;

   public MCCropGrowPostEvent(Post event) {
      super(event);
      this.event = event;
   }

   public IBlockState getOriginalBlockState() {
      return CraftTweakerMC.getBlockState(this.event.getOriginalState());
   }
}
