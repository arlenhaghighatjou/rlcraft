package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerCraftedEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.recipes.ICraftingInventory;
import crafttweaker.mc1120.recipes.MCCraftingInventorySquared;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class MCPlayerCraftedEvent implements PlayerCraftedEvent {
   private final ItemCraftedEvent event;

   public MCPlayerCraftedEvent(ItemCraftedEvent event) {
      this.event = event;
   }

   public IItemStack getOutput() {
      return CraftTweakerMC.getIItemStack(this.event.crafting);
   }

   public ICraftingInventory getInventory() {
      return new MCCraftingInventorySquared(this.event.craftMatrix, this.getPlayer());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.player);
   }
}
