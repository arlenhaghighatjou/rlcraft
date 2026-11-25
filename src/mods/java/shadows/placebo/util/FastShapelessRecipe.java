package shadows.placebo.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class FastShapelessRecipe extends ShapelessRecipes {
   static int packEmpty = RecipeItemHelper.pack(ItemStack.EMPTY);

   public FastShapelessRecipe(String group, ItemStack output, NonNullList<Ingredient> ingredients) {
      super(group, output, ingredients);
   }

   public boolean matches(InventoryCrafting inv, World world) {
      if (!this.isSimple) {
         return super.matches(inv, world);
      } else {
         IntList list = new IntArrayList();
         int k = inv.getSizeInventory();

         for(int ix = 0; ix < k; ++ix) {
            int is;
            if ((is = RecipeItemHelper.pack(inv.getStackInSlot(ix))) != packEmpty) {
               list.add(is);
            }
         }

         if (list.size() != this.recipeItems.size()) {
            return false;
         } else {
            for(Ingredient i : this.recipeItems) {
               for(int ix = 0; ix < list.size(); ++ix) {
                  if (i.getValidItemStacksPacked().contains(list.getInt(ix))) {
                     list.remove(ix);
                     break;
                  }
               }
            }

            return list.isEmpty();
         }
      }
   }
}
