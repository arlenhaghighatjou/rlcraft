package gigaherz.toolbelt.shadow.commons;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryHelper {
   public OreDictionaryHelper() {
   }

   public static boolean hasOreName(ItemStack stack, String oreName) {
      if (stack.isEmpty()) {
         return false;
      } else if (stack.getItem() == null) {
         return false;
      } else {
         int id = OreDictionary.getOreID(oreName);

         for(int i : OreDictionary.getOreIDs(stack)) {
            if (i == id) {
               return true;
            }
         }

         return false;
      }
   }
}
