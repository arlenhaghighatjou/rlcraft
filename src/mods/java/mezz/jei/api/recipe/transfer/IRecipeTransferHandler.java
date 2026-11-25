package mezz.jei.api.recipe.transfer;

import javax.annotation.Nullable;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public interface IRecipeTransferHandler<C extends Container> {
   Class<C> getContainerClass();

   @Nullable
   IRecipeTransferError transferRecipe(C var1, IRecipeLayout var2, EntityPlayer var3, boolean var4, boolean var5);
}
