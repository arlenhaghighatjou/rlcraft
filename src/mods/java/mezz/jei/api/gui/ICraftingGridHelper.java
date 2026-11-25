package mezz.jei.api.gui;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface ICraftingGridHelper {
   <T> void setInputs(IGuiIngredientGroup<T> var1, List<List<T>> var2);

   <T> void setInputs(IGuiIngredientGroup<T> var1, List<List<T>> var2, int var3, int var4);

   /** @deprecated */
   @Deprecated
   void setInputStacks(IGuiItemStackGroup var1, List<List<ItemStack>> var2);

   /** @deprecated */
   @Deprecated
   void setInputStacks(IGuiItemStackGroup var1, List<List<ItemStack>> var2, int var3, int var4);

   /** @deprecated */
   @Deprecated
   void setOutput(IGuiItemStackGroup var1, List<ItemStack> var2);
}
