package mezz.jei.api.ingredients;

import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public final class VanillaTypes {
   public static final IIngredientType<ItemStack> ITEM = () -> {
      return ItemStack.class;
   };
   public static final IIngredientType<FluidStack> FLUID = () -> {
      return FluidStack.class;
   };
   /** @deprecated */
   @Deprecated
   public static final IIngredientType<EnchantmentData> ENCHANT = () -> {
      return EnchantmentData.class;
   };

   private VanillaTypes() {
   }
}
