package pl.asie.foamfix.client.jei;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import mezz.jei.ingredients.IngredientFilter;
import net.minecraft.client.util.SearchTree;
import net.minecraft.item.ItemStack;
import pl.asie.foamfix.coremod.patches.jei.SearchTreeJEIPatchGlue;

public class SearchTreeJEIItems extends SearchTree<ItemStack> {
   public SearchTreeJEIItems() {
      super((Function)null, (Function)null);
   }

   public void recalculate() {
   }

   public void add(ItemStack element) {
   }

   public List<ItemStack> search(String searchText) {
      try {
         return (List)SearchTreeJEIPatchGlue.GET_INGREDIENT_LIST_UNCACHED.invokeExact((IngredientFilter)SearchTreeJEIPatchGlue.src, searchText).stream().filter((a) -> {
            return a != null && a.getIngredient() instanceof ItemStack;
         }).map((a) -> {
            return (ItemStack)a.getIngredient();
         }).collect(Collectors.toList());
      } catch (Throwable var3) {
         var3.printStackTrace();
         return Collections.emptyList();
      }
   }
}
