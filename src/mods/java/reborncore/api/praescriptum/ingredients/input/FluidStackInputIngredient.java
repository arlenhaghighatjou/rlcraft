package reborncore.api.praescriptum.ingredients.input;

import java.util.Objects;
import net.minecraftforge.fluids.FluidStack;
import reborncore.common.util.FluidUtils;

public class FluidStackInputIngredient extends InputIngredient<FluidStack> {
   public static FluidStackInputIngredient of(FluidStack ingredient) {
      return new FluidStackInputIngredient(ingredient);
   }

   public static FluidStackInputIngredient copyOf(FluidStack ingredient) {
      return new FluidStackInputIngredient(ingredient.copy());
   }

   protected FluidStackInputIngredient(FluidStack ingredient) {
      super(ingredient);
   }

   public Object getUnspecific() {
      return ((FluidStack)this.ingredient).getFluid();
   }

   public InputIngredient<FluidStack> copy() {
      return of(((FluidStack)this.ingredient).copy());
   }

   public boolean isEmpty() {
      return ((FluidStack)this.ingredient).amount <= 0;
   }

   public int getCount() {
      return ((FluidStack)this.ingredient).amount;
   }

   public void shrink(int amount) {
      ((FluidStack)this.ingredient).amount -= amount;
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

   public int hashCode() {
      return Objects.hash(new Object[]{this.ingredient, this.consumable});
   }

   public boolean equals(Object object) {
      if (this.getClass() != object.getClass()) {
         return false;
      } else {
         return this.matches(((FluidStackInputIngredient)object).ingredient) && this.consumable == ((FluidStackInputIngredient)object).consumable;
      }
   }
}
