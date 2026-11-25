package mezz.jei.api.ingredients;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.item.ItemStack;

public interface IIngredientHelper<V> {
   default List<V> expandSubtypes(List<V> ingredients) {
      return ingredients;
   }

   default IFocus<?> translateFocus(IFocus<V> focus, IIngredientHelper.IFocusFactory focusFactory) {
      return focus;
   }

   @Nullable
   V getMatch(Iterable<V> var1, V var2);

   String getDisplayName(V var1);

   String getUniqueId(V var1);

   String getWildcardId(V var1);

   String getModId(V var1);

   default String getDisplayModId(V ingredient) {
      return this.getModId(ingredient);
   }

   default Iterable<Color> getColors(V ingredient) {
      return Collections.emptyList();
   }

   String getResourceId(V var1);

   default ItemStack getCheatItemStack(V ingredient) {
      return ItemStack.EMPTY;
   }

   V copyIngredient(V var1);

   default boolean isValidIngredient(V ingredient) {
      return true;
   }

   default boolean isIngredientOnServer(V ingredient) {
      return true;
   }

   default Collection<String> getOreDictNames(V ingredient) {
      return Collections.emptyList();
   }

   default Collection<String> getCreativeTabNames(V ingredient) {
      return Collections.emptyList();
   }

   String getErrorInfo(@Nullable V var1);

   /** @deprecated */
   @Deprecated
   default ItemStack cheatIngredient(V ingredient, boolean fullStack) {
      return ItemStack.EMPTY;
   }

   public interface IFocusFactory {
      <V> IFocus<V> createFocus(IFocus.Mode var1, V var2);
   }
}
