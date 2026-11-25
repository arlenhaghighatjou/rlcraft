package rustic.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.CrushingTubRecipe;
import rustic.common.crafting.Recipes;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.CrushingTub")
public class CrushingTub {
   public CrushingTub() {
   }

   @ZenMethod
   public static void addRecipe(ILiquidStack output, IItemStack byproduct, IItemStack input) {
      CrushingTubRecipe r = new CrushingTubRecipe(CraftTweakerHelper.toFluidStack(output), CraftTweakerHelper.toStack(input), CraftTweakerHelper.toStack(byproduct));
      CraftTweakerAPI.apply(new CrushingTub.Add(r));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input) {
      CraftTweakerAPI.apply(new CrushingTub.Remove((FluidStack)null, CraftTweakerMC.getItemStack(input)));
   }

   @ZenMethod
   public static void removeRecipe(ILiquidStack output, @Optional IItemStack input) {
      if (output == null) {
         throw new IllegalArgumentException("Cannot remove Crushing Tub recipe for null Fluid input");
      } else {
         CraftTweakerAPI.apply(new CrushingTub.Remove(CraftTweakerMC.getLiquidStack(output), input == null ? null : CraftTweakerMC.getItemStack(input)));
      }
   }

   private static class Add implements IAction {
      private final CrushingTubRecipe recipe;

      public Add(CrushingTubRecipe recipe) {
         this.recipe = recipe;
      }

      public void apply() {
         Recipes.crushingTubRecipes.add(this.recipe);
      }

      public String describe() {
         return "Adding Crushing Tub Recipe for Fluid " + this.recipe.getResult().getLocalizedName();
      }
   }

   private static class Remove implements IAction {
      private final FluidStack output;
      private final ItemStack input;

      public Remove(FluidStack output, ItemStack input) {
         this.output = output;
         this.input = input;
      }

      public void apply() {
         if (this.output == null) {
            Recipes.removeCrushingRecipe(this.input);
         } else {
            Recipes.removeCrushingRecipe(this.output, this.input);
         }

      }

      public String describe() {
         return this.output == null ? "Removing Crushing Tub Recipes for Item " + this.input.getDisplayName() : "Removing Crushing Tub Recipes for Fluid " + this.output.getLocalizedName();
      }
   }
}
