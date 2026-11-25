package reborncore.common.recipes;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IRecipeInput {
   ItemStack getItemStack();

   List<ItemStack> getAllStacks();
}
