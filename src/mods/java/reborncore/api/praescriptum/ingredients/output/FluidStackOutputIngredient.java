package reborncore.api.praescriptum.ingredients.output;

import net.minecraftforge.fluids.FluidStack;
import reborncore.common.util.FluidUtils;

public class FluidStackOutputIngredient extends OutputIngredient<FluidStack> {
   public static FluidStackOutputIngredient of(FluidStack ingredient) {
      return new FluidStackOutputIngredient(ingredient);
   }

   protected FluidStackOutputIngredient(FluidStack ingredient) {
      super(ingredient);
   }

   public OutputIngredient<FluidStack> copy() {
      return of(((FluidStack)this.ingredient).copy());
   }

   public boolean isEmpty() {
      return ((FluidStack)this.ingredient).amount <= 0;
   }

   public boolean matches(Object other) {
      return other instanceof FluidStack && ((FluidStack)this.ingredient).isFluidEqual((FluidStack)other);
   }

   public boolean matchesStrict(Object other) {
      return other instanceof FluidStack && ((FluidStack)this.ingredient).isFluidStackIdentical((FluidStack)other);
   }

   public String toFormattedString() {
      return FluidUtils.toFormattedString((FluidStack)this.ingredient);
   }
}
