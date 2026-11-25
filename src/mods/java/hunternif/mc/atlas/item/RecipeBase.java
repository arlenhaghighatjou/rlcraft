package hunternif.mc.atlas.item;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

abstract class RecipeBase extends Impl<IRecipe> implements IRecipe {
   RecipeBase() {
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList<ItemStack> aitemstack = NonNullList.create();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         aitemstack.add(ForgeHooks.getContainerItem(itemstack));
      }

      return aitemstack;
   }

   public String getGroup() {
      return "antiqueatlas:atlas";
   }
}
