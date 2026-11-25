package rustic.common.crafting;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface ICondenserRecipe {
   boolean matches(Fluid var1, ItemStack var2, ItemStack var3, ItemStack[] var4);

   boolean isBasic();

   boolean isAdvanced();

   FluidStack getFluid();

   List<ItemStack> getModifiers();

   List<ItemStack> getBottles();

   List<List<ItemStack>> getInputs();

   int getTime();

   int getModifierConsumption(ItemStack var1);

   int getBottleConsumption(ItemStack var1);

   int[] getInputConsumption(ItemStack[] var1);

   ItemStack getResult();
}
