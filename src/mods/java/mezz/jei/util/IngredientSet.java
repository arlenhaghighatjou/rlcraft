package mezz.jei.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.startup.StackHelper;
import net.minecraft.item.ItemStack;

public class IngredientSet<V> extends AbstractSet<V> {
   private final Function<V, String> uidGenerator;
   private final Map<String, V> ingredients;

   public static <V> IngredientSet<V> create(IIngredientType<V> ingredientType, IIngredientHelper<V> ingredientHelper) {
      Function<V, String> uidGenerator;
      if (ingredientType == VanillaTypes.ITEM) {
         StackHelper stackHelper = Internal.getStackHelper();
         uidGenerator = (stack) -> {
            return stackHelper.getUniqueIdentifierForStack((ItemStack)stack, StackHelper.UidMode.FULL);
         };
      } else {
         uidGenerator = ingredientHelper::getUniqueId;
      }

      return new IngredientSet<V>(uidGenerator);
   }

   private IngredientSet(Function<V, String> uidGenerator) {
      this.uidGenerator = uidGenerator;
      this.ingredients = new LinkedHashMap();
   }

   public boolean add(V v) {
      String uid = (String)this.uidGenerator.apply(v);
      return this.ingredients.put(uid, v) == null;
   }

   public boolean remove(Object o) {
      String uid = (String)this.uidGenerator.apply((V)o);
      return this.ingredients.remove(uid) != null;
   }

   public boolean removeAll(Collection<?> c) {
      if (c instanceof IngredientSet) {
         return super.removeAll(c);
      } else {
         Objects.requireNonNull(c);
         boolean modified = false;

         for(Object aC : c) {
            modified |= this.remove(aC);
         }

         return modified;
      }
   }

   public boolean contains(Object o) {
      String uid = (String)this.uidGenerator.apply((V)o);
      return this.ingredients.containsKey(uid);
   }

   @Nullable
   public V getByUid(String uid) {
      return (V)this.ingredients.get(uid);
   }

   public void clear() {
      this.ingredients.clear();
   }

   public Iterator<V> iterator() {
      return this.ingredients.values().iterator();
   }

   public int size() {
      return this.ingredients.size();
   }
}
