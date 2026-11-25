package mezz.jei.api.gui;

import javax.annotation.Nullable;
import net.minecraftforge.fluids.FluidStack;

public interface IGuiFluidStackGroup extends IGuiIngredientGroup<FluidStack> {
   void init(int var1, boolean var2, int var3, int var4, int var5, int var6, int var7, boolean var8, @Nullable IDrawable var9);

   void set(int var1, @Nullable FluidStack var2);

   void addTooltipCallback(ITooltipCallback<FluidStack> var1);
}
