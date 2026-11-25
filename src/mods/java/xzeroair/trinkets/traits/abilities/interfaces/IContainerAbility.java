package xzeroair.trinkets.traits.abilities.interfaces;

import javax.annotation.Nullable;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public interface IContainerAbility extends IAbilityInterface {
   default void inventoryContainer(Container inventoryContainer) {
   }

   default void openContainer(@Nullable Container openContainer) {
   }

   default void playerInventory(InventoryPlayer inventory) {
   }
}
