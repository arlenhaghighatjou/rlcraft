package rustic.common.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ICrushingTubRecipe {
   ItemStack getInput();

   boolean matches(ItemStack var1);

   FluidStack getResult();

   ItemStack getByproduct();
}
