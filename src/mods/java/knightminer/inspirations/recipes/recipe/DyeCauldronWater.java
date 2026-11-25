package knightminer.inspirations.recipes.recipe;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.shared.InspirationsOredict;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.oredict.OreIngredient;

public class DyeCauldronWater implements ISimpleCauldronRecipe {
   private EnumDyeColor color;
   private Ingredient dye;

   public DyeCauldronWater(EnumDyeColor color) {
      this.color = color;
      this.dye = new OreIngredient(InspirationsOredict.dyeNameFor(color));
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      if (level != 0 && stack.getItem() != InspirationsRecipes.dyedWaterBottle) {
         return (state.isWater() || state.getColor() > -1) && this.dye.apply(stack) && this.color.colorValue != state.getColor();
      } else {
         return false;
      }
   }

   public List<ItemStack> getInput() {
      return (List)Arrays.stream(this.dye.getMatchingStacks()).filter((stack) -> {
         return stack.getItem() != InspirationsRecipes.dyedWaterBottle;
      }).collect(Collectors.toList());
   }

   public ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      int newColor = this.color.colorValue;
      int color = state.getColor();
      if (color > -1) {
         color = Util.combineColors(newColor, color, level);
      } else {
         color = newColor;
      }

      return ICauldronRecipe.CauldronState.dye(color);
   }

   public Object getState() {
      return this.color;
   }

   public SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return null;
   }
}
