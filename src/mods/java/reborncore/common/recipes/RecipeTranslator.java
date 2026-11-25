package reborncore.common.recipes;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeTranslator {
   public RecipeTranslator() {
   }

   @Nullable
   public static ItemStack getStackFromObject(Object object) {
      if (object instanceof ItemStack) {
         return (ItemStack)object;
      } else {
         if (object instanceof String) {
            String oreName = (String)object;
            if (OreDictionary.doesOreNameExist(oreName)) {
               NonNullList<ItemStack> list = OreDictionary.getOres(oreName);
               return ((ItemStack)list.get(0)).copy();
            }
         } else if (object instanceof IRecipeInput) {
            return ((IRecipeInput)object).getItemStack();
         }

         return ItemStack.EMPTY;
      }
   }
}
