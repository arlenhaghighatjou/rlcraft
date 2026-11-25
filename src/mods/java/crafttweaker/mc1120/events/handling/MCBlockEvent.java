package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.IBlockEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;

public class MCBlockEvent implements IBlockEvent {
   private final BlockEvent event;

   public MCBlockEvent(BlockEvent event) {
      this.event = event;
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
   }

   public IBlockState getBlockState() {
      return CraftTweakerMC.getBlockState(this.event.getState());
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
   }
}
