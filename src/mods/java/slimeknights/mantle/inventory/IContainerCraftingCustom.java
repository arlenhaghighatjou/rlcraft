package slimeknights.mantle.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IContainerCraftingCustom {
   void onCrafting(EntityPlayer var1, ItemStack var2, IInventory var3);
}
