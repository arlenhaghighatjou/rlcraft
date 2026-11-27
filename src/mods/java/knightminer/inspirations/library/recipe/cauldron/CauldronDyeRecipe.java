package knightminer.inspirations.library.recipe.cauldron;

import net.lax1dude.eaglercraft.v1_8.HString;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

@ParametersAreNonnullByDefault
public class CauldronDyeRecipe extends CauldronRecipeMatchRecipe {
   private EnumDyeColor color;

   public CauldronDyeRecipe(RecipeMatch input, EnumDyeColor color, ItemStack result, int levels) {
      super(input, result, (Boolean)null, levels);
      this.color = color;
   }

   public CauldronDyeRecipe(RecipeMatch input, EnumDyeColor color, ItemStack result) {
      this(input, color, result, 1);
   }

   public CauldronDyeRecipe(ItemStack input, EnumDyeColor color, ItemStack result) {
      this(RecipeMatch.of(input), color, result);
   }

   protected boolean matches(ICauldronRecipe.CauldronState state) {
      return state.getColor() == this.color.colorValue;
   }

   public Object getInputState() {
      return this.color;
   }

   public String toString() {
      return HString.format("CauldronDyeRecipe: %s dyed %s", this.getResult().toString(), this.color.getName());
   }
}
