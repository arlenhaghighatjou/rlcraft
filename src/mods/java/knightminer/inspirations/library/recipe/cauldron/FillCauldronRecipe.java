package knightminer.inspirations.library.recipe.cauldron;

import net.lax1dude.eaglercraft.v1_8.HString;
import javax.annotation.ParametersAreNonnullByDefault;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.util.RecipeMatch;

@ParametersAreNonnullByDefault
public class FillCauldronRecipe extends CauldronFluidRecipe {
   private int amount;

   public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container, Boolean boiling, SoundEvent sound) {
      super(input, fluid, container, boiling, 0, sound);
      this.amount = amount;
   }

   public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container, SoundEvent sound) {
      this(input, fluid, amount, container, (Boolean)null, sound);
   }

   public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container) {
      this(input, fluid, amount, container, SoundEvents.ITEM_BOTTLE_EMPTY);
   }

   public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount) {
      this(input, fluid, amount, ItemStack.EMPTY);
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return level != InspirationsRegistry.getCauldronMax() && (level <= 0 || state.matches(this.fluid)) && (this.boiling == null || boiling == this.boiling.booleanValue()) ? this.matches(stack) : false;
   }

   public ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return this.fluid;
   }

   public int getLevel(int level) {
      return level + this.amount;
   }

   public int getInputLevel() {
      return 0;
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }

   public String toString() {
      return HString.format("FillCauldronRecipe: filling with %s", this.fluid.getFluid().getName());
   }
}
