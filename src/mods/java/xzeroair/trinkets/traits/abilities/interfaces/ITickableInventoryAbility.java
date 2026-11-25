package xzeroair.trinkets.traits.abilities.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ITickableInventoryAbility extends IContainerAbility {
   default void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean inHand) {
   }
}
