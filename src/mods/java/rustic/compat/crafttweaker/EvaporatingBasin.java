package rustic.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.EvaporatingBasinRecipe;
import rustic.common.crafting.IEvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.EvaporatingBasin")
public class EvaporatingBasin {
   public EvaporatingBasin() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, ILiquidStack input) {
      EvaporatingBasinRecipe r = new EvaporatingBasinRecipe(CraftTweakerHelper.toStack(output), CraftTweakerHelper.toFluidStack(input));
      CraftTweakerAPI.apply(new EvaporatingBasin.Add(r));
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, ILiquidStack input, int time) {
      if (time < 20) {
         throw new IllegalArgumentException("Minimum evaporation time for evaporating basin is 20 ticks");
      } else {
         CraftTweakerAPI.apply(new EvaporatingBasin.Add(new CrTEvaporatingBasinRecipe(CraftTweakerMC.getItemStack(output), CraftTweakerMC.getLiquidStack(input), time)));
      }
   }

   @ZenMethod
   public static void removeRecipe(@NotNull IItemStack output) {
      CraftTweakerAPI.apply(new EvaporatingBasin.RemoveItem(CraftTweakerMC.getItemStack(output)));
   }

   @ZenMethod
   public static void removeRecipe(@NotNull ILiquidStack input) {
      CraftTweakerAPI.apply(new EvaporatingBasin.RemoveFluid(CraftTweakerMC.getLiquidStack(input)));
   }

   private static class Add implements IAction {
      private final IEvaporatingBasinRecipe recipe;

      public Add(IEvaporatingBasinRecipe recipe) {
         this.recipe = recipe;
      }

      public void apply() {
         Recipes.evaporatingRecipesMap.put(this.recipe.getFluid(), this.recipe);
      }

      public String describe() {
         return "Adding Evaporating Recipe for Item " + this.recipe.getOutput().getDisplayName();
      }
   }

   private static class RemoveFluid implements IAction {
      private final FluidStack input;

      public RemoveFluid(FluidStack input) {
         this.input = input;
      }

      public void apply() {
         Recipes.removeEvaporatingRecipe(this.input);
      }

      public String describe() {
         return "Removing Evaporating Recipe for Fluid " + this.input.getLocalizedName();
      }
   }

   private static class RemoveItem implements IAction {
      private final ItemStack output;

      public RemoveItem(ItemStack output) {
         this.output = output;
      }

      public void apply() {
         Recipes.removeEvaporatingRecipe(this.output);
      }

      public String describe() {
         return "Removing Evaporating Recipes for Item " + this.output.getDisplayName();
      }
   }
}
