package crafttweaker.api.recipes;

import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;

public class CraftingInfo implements ICraftingInfo {
   private final ICraftingInventory inventory;
   private final IWorld world;

   public CraftingInfo(ICraftingInventory inventory, IWorld world) {
      this.inventory = inventory;
      if (world != null) {
         this.world = world;
      } else {
         this.world = inventory.getPlayer() == null ? null : inventory.getPlayer().getWorld();
      }

   }

   public ICraftingInventory getInventory() {
      return this.inventory;
   }

   public IPlayer getPlayer() {
      return this.inventory.getPlayer();
   }

   public int getDimension() {
      return this.world.getDimension();
   }

   public IWorld getWorld() {
      return this.world;
   }
}
