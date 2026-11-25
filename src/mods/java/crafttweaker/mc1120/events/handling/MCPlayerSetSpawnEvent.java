package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerSetSpawnEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;

public class MCPlayerSetSpawnEvent implements PlayerSetSpawnEvent {
   private final net.minecraftforge.event.entity.player.PlayerSetSpawnEvent event;

   public MCPlayerSetSpawnEvent(net.minecraftforge.event.entity.player.PlayerSetSpawnEvent event) {
      this.event = event;
   }

   public IBlockPos getNewSpawn() {
      return CraftTweakerMC.getIBlockPos(this.event.getNewSpawn());
   }

   public boolean getIsForced() {
      return this.event.isForced();
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
