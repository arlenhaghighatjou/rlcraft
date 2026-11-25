package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlock;
import crafttweaker.api.event.BlockBreakEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class MCBlockBreakEvent extends MCBlockEvent implements BlockBreakEvent {
   private final BreakEvent event;

   public MCBlockBreakEvent(BreakEvent event) {
      super(event);
      this.event = event;
   }

   public boolean getIsPlayer() {
      return this.event.getPlayer() instanceof EntityPlayerMP || this.event.getPlayer() instanceof EntityPlayerSP;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getPlayer());
   }

   public IBlock getBlock() {
      return this.getBlockState().getBlock();
   }

   public int getExperience() {
      return this.event.getExpToDrop();
   }

   public void setExperience(int experience) {
      this.event.setExpToDrop(experience);
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
