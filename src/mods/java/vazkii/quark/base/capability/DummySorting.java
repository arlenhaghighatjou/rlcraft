package vazkii.quark.base.capability;

import java.util.Comparator;
import net.minecraft.item.ItemStack;
import vazkii.quark.api.ICustomSorting;

public class DummySorting implements ICustomSorting {
   public DummySorting() {
   }

   public Comparator<ItemStack> getItemComparator() {
      return (first, second) -> {
         return 0;
      };
   }

   public String getSortingCategory() {
      return "";
   }
}
