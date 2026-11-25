package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.event.PlayerItemPickupEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

public class MCPlayerItemPickupEvent implements PlayerItemPickupEvent {
   private ItemPickupEvent event;

   public MCPlayerItemPickupEvent(ItemPickupEvent event) {
      this.event = event;
   }

   public IItemStack getStackCopy() {
      return CraftTweakerMC.getIItemStack(this.event.getStack());
   }

   public IEntityItem getOriginalEntity() {
      return CraftTweakerMC.getIEntityItem(this.event.getOriginalEntity());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.player);
   }
}
