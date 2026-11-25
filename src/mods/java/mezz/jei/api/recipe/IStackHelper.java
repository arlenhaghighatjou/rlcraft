package mezz.jei.api.recipe;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public interface IStackHelper {
   List<ItemStack> getSubtypes(ItemStack var1);

   List<ItemStack> getAllSubtypes(Iterable var1);

   List<ItemStack> toItemStackList(@Nullable Object var1);

   List<List<ItemStack>> expandRecipeItemStackInputs(@Nullable List var1);

   @Nullable
   ItemStack containsAnyStack(Iterable<ItemStack> var1, Iterable<ItemStack> var2);

   boolean isEquivalent(@Nullable ItemStack var1, @Nullable ItemStack var2);
}
