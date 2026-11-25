package rustic.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.Condenser")
public class Condenser {
   public Condenser() {
   }

   @ZenMethod
   public static void addRecipe(@NotNull IItemStack output, IIngredient input1, IIngredient input2) {
      ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), new IIngredient[]{input1, input2});
      CraftTweakerAPI.apply(new Condenser.Add(r));
   }

   @ZenMethod
   public static void addRecipe(@NotNull IItemStack output, IIngredient[] inputs) {
      if (inputs.length > 3) {
         throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
      } else {
         ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs);
         CraftTweakerAPI.apply(new Condenser.Add(r));
      }
   }

   @ZenMethod
   public static void addRecipe(@NotNull IItemStack output, IIngredient[] inputs, IIngredient modifier) {
      if (inputs.length > 3) {
         throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
      } else {
         ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs, modifier);
         CraftTweakerAPI.apply(new Condenser.Add(r));
      }
   }

   @ZenMethod
   public static void addRecipe(@NotNull IItemStack output, @NotNull IIngredient[] inputs, IIngredient modifier, IIngredient bottle) {
      if (inputs.length > 3) {
         throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
      } else {
         ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs, modifier, bottle);
         CraftTweakerAPI.apply(new Condenser.Add(r));
      }
   }

   @ZenMethod
   public static void addRecipe(@NotNull IItemStack output, @NotNull IIngredient[] inputs, IIngredient modifier, IIngredient bottle, ILiquidStack fluid) {
      if (inputs.length > 3) {
         throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
      } else {
         if (fluid == null) {
            fluid = CraftTweakerMC.getILiquidStack(new FluidStack(FluidRegistry.WATER, 125));
         }

         ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs, modifier, bottle, CraftTweakerMC.getLiquidStack(fluid));
         CraftTweakerAPI.apply(new Condenser.Add(r));
      }
   }

   @ZenMethod
   public static void addRecipe(@NotNull IItemStack output, @NotNull IIngredient[] inputs, IIngredient modifier, IIngredient bottle, ILiquidStack fluid, int time) {
      if (inputs.length > 3) {
         throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
      } else {
         if (fluid == null) {
            fluid = CraftTweakerMC.getILiquidStack(new FluidStack(FluidRegistry.WATER, 125));
         }

         if (time <= 0) {
            throw new IllegalArgumentException("Brew time must be positive");
         } else {
            ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs, modifier, bottle, CraftTweakerMC.getLiquidStack(fluid), time);
            CraftTweakerAPI.apply(new Condenser.Add(r));
         }
      }
   }

   @ZenMethod
   public static void removeRecipe(@NotNull IItemStack output) {
      CraftTweakerAPI.apply(new Condenser.Remove(CraftTweakerMC.getItemStack(output)));
   }

   private static class Add implements IAction {
      private final ICondenserRecipe recipe;

      public Add(ICondenserRecipe recipe) {
         this.recipe = recipe;
      }

      public void apply() {
         Recipes.condenserRecipes.add(this.recipe);
      }

      public String describe() {
         return "Adding Alchemy Recipe for Item " + this.recipe.getResult().getDisplayName();
      }
   }

   private static class Remove implements IAction {
      private final ItemStack output;

      public Remove(ItemStack output) {
         this.output = output;
      }

      public void apply() {
         Recipes.removeCondenserRecipe(this.output);
      }

      public String describe() {
         return "Removing Condenser Recipes for Item " + this.output.getDisplayName();
      }
   }
}
