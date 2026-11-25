package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerRightClickBlockEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IVector3d;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCPlayerRightClickBlockEvent extends MCPlayerInteractEvent implements PlayerRightClickBlockEvent {
   private final RightClickBlock event;

   public MCPlayerRightClickBlockEvent(RightClickBlock event) {
      super(event);
      this.event = event;
   }

   public IVector3d getHitVector() {
      return CraftTweakerMC.getIVector3d(this.event.getHitVec());
   }

   public String getUseBlock() {
      return this.event.getUseBlock().name();
   }

   public void setUseBlock(String useBlock) {
      this.event.setUseBlock(Result.valueOf(useBlock));
   }

   public String getUseItem() {
      return this.event.getUseItem().name();
   }

   public void setUseItem(String useItem) {
      this.event.setUseItem(Result.valueOf(useItem));
   }

   public String getCancellationResult() {
      return this.event.getCancellationResult().name();
   }

   public void setCancellationResult(String value) {
      this.event.setCancellationResult(EnumActionResult.valueOf(value));
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
