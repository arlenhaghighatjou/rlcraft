package mezz.jei.api.recipe.transfer;

import net.minecraft.inventory.Container;

public interface IRecipeTransferRegistry {
   <C extends Container> void addRecipeTransferHandler(Class<C> var1, String var2, int var3, int var4, int var5, int var6);

   <C extends Container> void addRecipeTransferHandler(IRecipeTransferInfo<C> var1);

   void addRecipeTransferHandler(IRecipeTransferHandler<?> var1, String var2);

   void addUniversalRecipeTransferHandler(IRecipeTransferHandler<?> var1);
}
