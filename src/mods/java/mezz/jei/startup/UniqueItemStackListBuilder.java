package mezz.jei.startup;

import java.util.HashSet;
import java.util.Set;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class UniqueItemStackListBuilder {
   private final StackHelper stackHelper;
   private final NonNullList<ItemStack> ingredients = NonNullList.create();
   private final Set<String> ingredientUids = new HashSet();

   public UniqueItemStackListBuilder(StackHelper stackHelper) {
      this.stackHelper = stackHelper;
   }

   public void add(ItemStack itemStack) {
      if (!itemStack.isEmpty()) {
         try {
            String uid = this.stackHelper.getUniqueIdentifierForStack(itemStack);
            if (!this.ingredientUids.contains(uid)) {
               this.ingredientUids.add(uid);
               this.ingredients.add(itemStack);
            }
         } catch (LinkageError | RuntimeException var3) {
            Log.get().error("Failed to get unique identifier for stack.", var3);
         }

      }
   }

   public NonNullList<ItemStack> build() {
      return this.ingredients;
   }
}
