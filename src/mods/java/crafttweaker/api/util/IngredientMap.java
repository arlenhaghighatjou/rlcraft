package crafttweaker.api.util;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.item.IItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IngredientMap<T> {
   private final HashMap<IItemDefinition, List<IngredientMap.IngredientMapEntry<T>>> entries = new HashMap();

   public IngredientMap() {
   }

   public IngredientMap.IngredientMapEntry<T> register(IIngredient ingredient, T entry) {
      Set<IItemDefinition> items = (Set)ingredient.getItems().stream().map(IItemStack::getDefinition).collect(Collectors.toSet());
      IngredientMap.IngredientMapEntry<T> actualEntry = new IngredientMap.IngredientMapEntry<T>(ingredient, entry);

      for(IItemDefinition item : items) {
         if (!this.entries.containsKey(item)) {
            this.entries.put(item, new ArrayList());
         }

         ((List)this.entries.get(item)).add(actualEntry);
      }

      return actualEntry;
   }

   public void unregister(IngredientMap.IngredientMapEntry<T> entry) {
      for(IItemDefinition item : (Set)entry.ingredient.getItems().stream().map(IItemStack::getDefinition).collect(Collectors.toSet())) {
         if (this.entries.containsKey(item)) {
            ((List)this.entries.get(item)).remove(entry);
         }
      }

   }

   public T getFirstEntry(IItemStack item) {
      for(IngredientMap.IngredientMapEntry<T> entry : (List)this.entries.get(item.getDefinition())) {
         if (entry.ingredient.matches(item)) {
            return entry.entry;
         }
      }

      return (T)null;
   }

   public List<T> getEntries(IItemStack item) {
      if (item != null && item.getDefinition() != null) {
         List<IngredientMap.IngredientMapEntry<T>> entries = (List)this.entries.get(item.getDefinition());
         return entries != null ? (List)entries.stream().filter((entry) -> {
            return entry.ingredient.matches(item);
         }).map((entry) -> {
            return entry.entry;
         }).collect(Collectors.toCollection(ArrayList::new)) : Collections.emptyList();
      } else {
         return Collections.emptyList();
      }
   }

   public static class IngredientMapEntry<T> {
      private final IIngredient ingredient;
      private final T entry;

      public IngredientMapEntry(IIngredient ingredient, T entry) {
         this.ingredient = ingredient;
         this.entry = entry;
      }
   }
}
