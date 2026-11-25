package com.blamejared.compat.inspirations;

import com.blamejared.ModTweaker;
import com.blamejared.compat.mantle.RecipeMatchIIngredient;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientAny;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.event.RegisterEvent.RegisterCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronBrewingRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronDyeRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidTransformRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronMixRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronPotionRecipe;
import knightminer.inspirations.library.recipe.cauldron.FillCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import slimeknights.mantle.util.RecipeMatch;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.inspirations.Cauldron")
@ZenRegister
@ModOnly("inspirations")
public class Cauldron {
   private static final Set<ICauldronRecipe> ADDED_RECIPES = new HashSet();
   public static final List<Triple<Object, Object, IIngredient>> REMOVED_BREWING = new LinkedList();
   public static final List<Triple<IIngredient, IIngredient, Object>> REMOVED_POTION = new LinkedList();
   public static final List<Triple<IIngredient, IIngredient, EnumDyeColor>> REMOVED_DYE = new LinkedList();
   public static final List<Triple<IIngredient, IIngredient, Fluid>> REMOVED_FLUID = new LinkedList();
   public static final List<Triple<Fluid, IIngredient, Fluid>> REMOVED_FLUID_TRANSFORM = new LinkedList();
   public static final List<Pair<IIngredient, Fluid>> REMOVED_FILL = new LinkedList();
   private static boolean init = false;

   public Cauldron() {
   }

   private static void init() {
      if (!init) {
         MinecraftForge.EVENT_BUS.register(Cauldron.class);
         init = true;
      }

   }

   @ZenMethod
   public static void addBrewingRecipe(String output, String input, IIngredient reagent) {
      ModTweaker.LATE_ADDITIONS.add(new Cauldron.AddBrewing(output, input, reagent));
   }

   @ZenMethod
   public static void removeBrewingRecipe(String output, @Optional String input, @Optional IIngredient reagent) {
      init();
      CraftTweakerAPI.apply(new Cauldron.RemoveBrewing(output, input, reagent));
   }

   @ZenMethod
   public static void addPotionRecipe(IItemStack output, IIngredient input, String potion) {
      addPotionRecipe(output, input, potion, 1, (Boolean)null);
   }

   @ZenMethod
   public static void addPotionRecipe(IItemStack output, IIngredient input, String potion, int levels, @Optional Boolean boiling) {
      if (levels >= 0 && levels <= InspirationsRegistry.getCauldronMax()) {
         ModTweaker.LATE_ADDITIONS.add(new Cauldron.AddPotion(InputHelper.toStack(output), input, potion, levels, boiling));
      } else {
         CraftTweakerAPI.logError(String.format("Ignoring Cauldron Potion recipe for %s: Invalid levels %d given, must be between 0 and %d", output.getDisplayName(), levels, InspirationsRegistry.getCauldronMax()));
      }
   }

   @ZenMethod
   public static void removePotionRecipe(IIngredient output, @Optional IIngredient input, @Optional String potion) {
      init();
      CraftTweakerAPI.apply(new Cauldron.RemovePotion(output, input, potion));
   }

   @ZenMethod
   public static void addDyeRecipe(IItemStack output, IIngredient input, String stringDye) {
      addDyeRecipe(output, input, stringDye, 1);
   }

   @ZenMethod
   public static void addDyeRecipe(IItemStack output, IIngredient input, String stringDye, int levels) {
      if (levels >= 0 && levels <= InspirationsRegistry.getCauldronMax()) {
         EnumDyeColor dye = EnumDyeColor.valueOf(stringDye.toUpperCase());
         if (dye == null) {
            CraftTweakerAPI.logError("Ignoring Cauldron Dyeing recipe for " + output.getDisplayName() + ": Could not find matching dye color for " + stringDye);
         } else {
            ModTweaker.LATE_ADDITIONS.add(new Cauldron.AddDye(InputHelper.toStack(output), input, dye, levels));
         }
      } else {
         CraftTweakerAPI.logError(String.format("Ignoring Cauldron Dyeing recipe for %s: Invalid levels %d given, must be between 0 and %d", output.getDisplayName(), levels, InspirationsRegistry.getCauldronMax()));
      }
   }

   @ZenMethod
   public static void removeDyeRecipe(IIngredient output, @Optional IIngredient input, @Optional String stringDye) {
      init();
      EnumDyeColor dye = null;
      if (stringDye != null) {
         dye = EnumDyeColor.valueOf(stringDye.toUpperCase());
         if (dye == null) {
            CraftTweakerAPI.logError("Ignoring Cauldron Dyeing recipe removalS: Could not find matching dye color for " + stringDye);
            return;
         }
      }

      CraftTweakerAPI.apply(new Cauldron.RemoveDye(output, input, dye));
   }

   @ZenMethod
   public static void addFluidRecipe(IItemStack output, IIngredient input, ILiquidStack fluid) {
      addFluidRecipe(output, input, fluid, 1, (Boolean)null);
   }

   @ZenMethod
   public static void addFluidRecipe(IItemStack output, IIngredient input, ILiquidStack fluid, int levels, @Optional Boolean boiling) {
      if (levels >= 0 && levels <= InspirationsRegistry.getCauldronMax()) {
         ModTweaker.LATE_ADDITIONS.add(new Cauldron.AddFluid(InputHelper.toStack(output), input, InputHelper.toFluid(fluid), levels, boiling));
      } else {
         CraftTweakerAPI.logError(String.format("Ignoring Cauldron Fluid recipe for %s: Invalid levels %d given, must be between 0 and %d", output.getDisplayName(), levels, InspirationsRegistry.getCauldronMax()));
      }
   }

   @ZenMethod
   public static void addFluidTransform(ILiquidStack output, IIngredient input, ILiquidStack fluid) {
      addFluidTransform(output, input, fluid, InspirationsRegistry.getCauldronMax(), (Boolean)null);
   }

   @ZenMethod
   public static void addFluidTransform(ILiquidStack output, IIngredient input, ILiquidStack fluid, int maxLevel, @Optional Boolean boiling) {
      if (maxLevel >= 1 && maxLevel <= InspirationsRegistry.getCauldronMax()) {
         ModTweaker.LATE_ADDITIONS.add(new Cauldron.AddFluidTransform(InputHelper.toFluid(output), input, InputHelper.toFluid(fluid), maxLevel, boiling));
      } else {
         CraftTweakerAPI.logError(String.format("Ignoring Cauldron Fluid recipe for %s: Invalid max level %d given, must be between 1 and %d", output.getDisplayName(), maxLevel, InspirationsRegistry.getCauldronMax()));
      }
   }

   @ZenMethod
   public static void addFluidMix(IItemStack output, ILiquidStack liquid1, ILiquidStack liquid2) {
      ModTweaker.LATE_ADDITIONS.add(new Cauldron.AddFluidMix(InputHelper.toStack(output), InputHelper.toFluid(liquid1), InputHelper.toFluid(liquid2)));
   }

   @ZenMethod
   public static void removeFluidRecipe(IIngredient output, @Optional IIngredient input, @Optional ILiquidStack fluid) {
      init();
      CraftTweakerAPI.apply(new Cauldron.RemoveFluid(output, input, fluid));
   }

   @ZenMethod
   public static void removeFluidTransform(IIngredient output, @Optional IIngredient input, @Optional ILiquidStack fluid) {
      init();
      CraftTweakerAPI.apply(new Cauldron.RemoveFluidTransform(output, input, fluid));
   }

   @ZenMethod
   public static void addFillRecipe(IIngredient input, ILiquidStack fluid) {
      addFillRecipe(input, fluid, 1, (IItemStack)null, (Boolean)null);
   }

   @ZenMethod
   public static void addFillRecipe(IIngredient input, ILiquidStack fluid, int amount, @Optional IItemStack container, @Optional Boolean boiling) {
      if (amount >= 1 && amount <= InspirationsRegistry.getCauldronMax()) {
         ModTweaker.LATE_ADDITIONS.add(new Cauldron.AddFill(input, InputHelper.toFluid(fluid), amount, InputHelper.toStack(container), boiling));
      } else {
         CraftTweakerAPI.logError(String.format("Ignoring Cauldron Fill recipe for %s: Invalid amount %d given, must be between 1 and %d", fluid.getDisplayName(), amount, InspirationsRegistry.getCauldronMax()));
      }
   }

   @ZenMethod
   public static void removeFillRecipe(IIngredient input, @Optional ILiquidStack fluid) {
      init();
      CraftTweakerAPI.apply(new Cauldron.RemoveFill(input, fluid));
   }

   @SubscribeEvent
   public static void onCauldronRegister(RegisterCauldronRecipe event) {
      ICauldronRecipe irecipe = (ICauldronRecipe)event.getRecipe();
      if (!ADDED_RECIPES.contains(irecipe)) {
         if (irecipe instanceof CauldronBrewingRecipe) {
            for(Triple<Object, Object, IIngredient> remove : REMOVED_BREWING) {
               if (checkRecipeMatches((ISimpleCauldronRecipe)irecipe, (IIngredient)remove.getRight(), (IIngredient)null, remove.getMiddle(), remove.getLeft())) {
                  event.setCanceled(true);
                  return;
               }
            }
         } else if (irecipe instanceof CauldronDyeRecipe) {
            for(Triple<IIngredient, IIngredient, EnumDyeColor> remove : REMOVED_DYE) {
               if (checkRecipeMatches((ISimpleCauldronRecipe)irecipe, (IIngredient)remove.getMiddle(), (IIngredient)remove.getLeft(), remove.getRight(), (Object)null)) {
                  event.setCanceled(true);
                  return;
               }
            }
         } else if (irecipe instanceof CauldronFluidTransformRecipe) {
            for(Triple<Fluid, IIngredient, Fluid> remove : REMOVED_FLUID_TRANSFORM) {
               if (checkRecipeMatches((ISimpleCauldronRecipe)irecipe, (IIngredient)remove.getMiddle(), (IIngredient)null, remove.getRight(), remove.getLeft())) {
                  event.setCanceled(true);
                  return;
               }
            }
         } else if (irecipe instanceof FillCauldronRecipe) {
            for(Pair<IIngredient, Fluid> remove : REMOVED_FILL) {
               if (checkRecipeMatches((ISimpleCauldronRecipe)irecipe, (IIngredient)remove.getLeft(), (IIngredient)null, (Object)null, remove.getRight())) {
                  event.setCanceled(true);
                  return;
               }
            }
         } else if (irecipe instanceof CauldronFluidRecipe) {
            for(Triple<IIngredient, IIngredient, Fluid> remove : REMOVED_FLUID) {
               if (checkRecipeMatches((ISimpleCauldronRecipe)irecipe, (IIngredient)remove.getMiddle(), (IIngredient)remove.getLeft(), remove.getRight(), (Object)null)) {
                  event.setCanceled(true);
                  return;
               }
            }
         } else if (irecipe instanceof CauldronPotionRecipe) {
            for(Triple<IIngredient, IIngredient, Object> remove : REMOVED_POTION) {
               if (checkRecipeMatches((ISimpleCauldronRecipe)irecipe, (IIngredient)remove.getMiddle(), (IIngredient)remove.getLeft(), remove.getRight(), (Object)null)) {
                  event.setCanceled(true);
                  return;
               }
            }
         }

      }
   }

   private static void addRecipe(ICauldronRecipe recipe) {
      ADDED_RECIPES.add(recipe);
      InspirationsRegistry.addCauldronRecipe(recipe);
   }

   private static boolean checkRecipeMatches(ISimpleCauldronRecipe recipe, IIngredient input, IIngredient output, Object inputState, Object outputState) {
      if ((outputState == null || compareFluids(outputState, recipe.getState())) && (inputState == null || compareFluids(inputState, recipe.getInputState())) && (output == null || output.matches(CraftTweakerMC.getIItemStack(recipe.getResult())))) {
         if (input == null) {
            return true;
         }

         for(ItemStack stack : recipe.getInput()) {
            if (input.matches(CraftTweakerMC.getIItemStack(stack))) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean compareFluids(Object first, Object second) {
      if (first == second) {
         return true;
      } else {
         return first instanceof Fluid && second instanceof Fluid ? ((Fluid)first).getName().equals(((Fluid)second).getName()) : false;
      }
   }

   private static class AddBrewing extends BaseAction {
      private String output;
      private String input;
      private IIngredient reagent;

      public AddBrewing(String output, String input, IIngredient reagent) {
         super("Cauldron Brewing");
         this.output = output;
         this.input = input;
         this.reagent = reagent;
      }

      public void apply() {
         PotionType input = PotionType.getPotionTypeForName(this.input);
         if (input != null && input != PotionTypes.EMPTY) {
            PotionType output = PotionType.getPotionTypeForName(this.output);
            if (output != null && output != PotionTypes.EMPTY) {
               Ingredient reagent = Ingredient.fromStacks(CraftTweakerMC.getItemStacks(this.reagent.getItems()));
               Cauldron.addRecipe(new CauldronBrewingRecipe(input, reagent, output));
            } else {
               CraftTweakerAPI.logError("Could not find potion type for " + this.output + ". Ignoring Cauldron Brewing recipe.");
            }
         } else {
            CraftTweakerAPI.logError("Could not find potion type for " + this.input + ". Ignoring Cauldron Brewing recipe for " + this.output);
         }
      }

      protected String getRecipeInfo() {
         return '"' + this.output + '"';
      }
   }

   private static class AddDye extends BaseAction {
      private ItemStack output;
      private IIngredient input;
      private EnumDyeColor dye;
      private int levels;

      public AddDye(ItemStack output, IIngredient input, EnumDyeColor dye, int levels) {
         super("Cauldron Dyeing");
         this.output = output;
         this.input = input;
         this.dye = dye;
         this.levels = levels;
      }

      public void apply() {
         RecipeMatch input = new RecipeMatchIIngredient(this.input);
         Cauldron.addRecipe(new CauldronDyeRecipe(input, this.dye, this.output, this.levels));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddFill extends BaseAction {
      private IIngredient input;
      private FluidStack fluid;
      private int amount;
      private ItemStack container;
      private Boolean boiling;

      public AddFill(IIngredient input, FluidStack fluid, int amount, ItemStack container, Boolean boiling) {
         super("Cauldron Fill");
         this.fluid = fluid;
         this.input = input;
         this.amount = amount;
         this.container = container;
         this.boiling = boiling;
      }

      public void apply() {
         RecipeMatch input = new RecipeMatchIIngredient(this.input);
         SoundEvent sound = this.boiling == Boolean.TRUE ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BOTTLE_EMPTY;
         Cauldron.addRecipe(new FillCauldronRecipe(input, this.fluid.getFluid(), this.amount, this.container, this.boiling, sound));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }

   private static class AddFluid extends BaseAction {
      private ItemStack output;
      private IIngredient input;
      private FluidStack fluid;
      private Boolean boiling;
      private int levels;

      public AddFluid(ItemStack output, IIngredient input, FluidStack fluid, int levels, Boolean boiling) {
         super("Cauldron Fluid");
         this.output = output;
         this.input = input;
         this.fluid = fluid;
         this.boiling = boiling;
         this.levels = levels;
      }

      public void apply() {
         RecipeMatch input = new RecipeMatchIIngredient(this.input);
         Cauldron.addRecipe(new CauldronFluidRecipe(input, this.fluid.getFluid(), this.output, this.boiling, this.levels));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddFluidMix extends BaseAction {
      private ItemStack output;
      private FluidStack input1;
      private FluidStack input2;

      public AddFluidMix(ItemStack output, FluidStack input1, FluidStack input2) {
         super("Cauldron Mix");
         this.output = output;
         this.input1 = input1;
         this.input2 = input2;
      }

      public void apply() {
         Cauldron.addRecipe(new CauldronMixRecipe(this.input1.getFluid(), this.input2.getFluid(), this.output));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddFluidTransform extends BaseAction {
      private FluidStack output;
      private IIngredient input;
      private FluidStack fluid;
      private Boolean boiling;
      private int maxLevels;

      public AddFluidTransform(FluidStack output, IIngredient input, FluidStack fluid, int maxLevels, Boolean boiling) {
         super("Cauldron Fluid");
         this.output = output;
         this.input = input;
         this.fluid = fluid;
         this.maxLevels = maxLevels;
         this.boiling = boiling;
      }

      public void apply() {
         RecipeMatch input = new RecipeMatchIIngredient(this.input);
         Cauldron.addRecipe(new CauldronFluidTransformRecipe(input, this.fluid.getFluid(), this.output.getFluid(), this.boiling, this.maxLevels));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddPotion extends BaseAction {
      private ItemStack output;
      private IIngredient input;
      private String potion;
      private int levels;
      private Boolean boiling;

      public AddPotion(ItemStack output, IIngredient input, String potion, int levels, Boolean boiling) {
         super("Cauldron Potion");
         this.output = output;
         this.input = input;
         this.potion = potion;
         this.levels = levels;
         this.boiling = boiling;
      }

      public void apply() {
         RecipeMatch input = new RecipeMatchIIngredient(this.input);
         PotionType potion = PotionType.getPotionTypeForName(this.potion);
         if (potion != null && potion != PotionTypes.EMPTY) {
            Cauldron.addRecipe(new CauldronPotionRecipe(input, potion, this.output, this.levels, this.boiling));
         } else {
            CraftTweakerAPI.logError("Could not find potion type for " + this.potion + ". Ignoring Cauldron Brewing recipe for " + this.output);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class RemoveBrewing extends BaseAction {
      private String output;
      private String input;
      private IIngredient reagent;

      public RemoveBrewing(String output, String input, IIngredient reagent) {
         super("Cauldron Brewing");
         this.output = output;
         this.input = input;
         this.reagent = reagent;
      }

      public void apply() {
         Object input = null;
         if (this.input != null) {
            input = PotionType.getPotionTypeForName(this.input);
            if (input == null || input == PotionTypes.EMPTY) {
               CraftTweakerAPI.logError("Could not find potion type for " + this.input + ". Ignoring Cauldron Brewing recipe removal " + this.output);
               return;
            }

            if (input == PotionTypes.WATER) {
               input = FluidRegistry.WATER;
            }
         }

         Object output = null;
         if (this.output != null) {
            output = PotionType.getPotionTypeForName(this.output);
            if (output == null || output == PotionTypes.EMPTY) {
               CraftTweakerAPI.logError("Could not find potion type for " + this.output + ". Ignoring Cauldron Brewing recipe removal.");
               return;
            }

            if (output == PotionTypes.WATER) {
               output = FluidRegistry.WATER;
            }
         }

         Cauldron.REMOVED_BREWING.add(Triple.of(output, input, this.reagent));
      }

      protected String getRecipeInfo() {
         return '"' + this.output + '"';
      }
   }

   private static class RemoveDye extends BaseAction {
      private IIngredient output;
      private IIngredient input;
      private EnumDyeColor dye;

      public RemoveDye(IIngredient output, IIngredient input, EnumDyeColor dye) {
         super("Cauldron Dyeing");
         this.output = output;
         this.input = input;
         this.dye = dye;
      }

      public void apply() {
         Cauldron.REMOVED_DYE.add(Triple.of(this.output, this.input, this.dye));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class RemoveFill extends BaseAction {
      private IIngredient input;
      private ILiquidStack fluid;

      public RemoveFill(IIngredient input, ILiquidStack fluid) {
         super("Cauldron Fill");
         this.fluid = fluid;
         this.input = input;
      }

      public void apply() {
         Cauldron.REMOVED_FILL.add(Pair.of(this.input, InputHelper.getFluid(this.fluid)));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }

   private static class RemoveFluid extends BaseAction {
      private IIngredient output;
      private IIngredient input;
      private ILiquidStack fluid;

      public RemoveFluid(IIngredient output, IIngredient input, ILiquidStack fluid) {
         super("Cauldron Fluid");
         this.output = output;
         this.input = input;
         this.fluid = fluid;
      }

      public void apply() {
         Cauldron.REMOVED_FLUID.add(Triple.of(this.output, this.input, InputHelper.getFluid(this.fluid)));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class RemoveFluidTransform extends BaseAction {
      private IIngredient output;
      private IIngredient input;
      private ILiquidStack fluid;

      public RemoveFluidTransform(IIngredient output, IIngredient input, ILiquidStack fluid) {
         super("Cauldron Fluid");
         this.output = output;
         this.input = input;
         this.fluid = fluid;
      }

      public void apply() {
         Fluid output;
         if (this.output instanceof IngredientAny) {
            output = null;
         } else {
            if (!(this.output instanceof ILiquidStack)) {
               CraftTweakerAPI.logError("Ignoring Cauldron Fluid recipe removal: Output must be fluid or wildcard");
               return;
            }

            output = InputHelper.getFluid((ILiquidStack)this.output);
         }

         Cauldron.REMOVED_FLUID_TRANSFORM.add(Triple.of(output, this.input, InputHelper.getFluid(this.fluid)));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class RemovePotion extends BaseAction {
      private IIngredient output;
      private IIngredient input;
      private String potion;

      public RemovePotion(IIngredient output, IIngredient input, String potion) {
         super("Cauldron Potion");
         this.output = output;
         this.input = input;
         this.potion = potion;
      }

      public void apply() {
         Object potion = null;
         if (this.potion != null) {
            potion = PotionType.getPotionTypeForName(this.potion);
            if (potion == null || potion == PotionTypes.EMPTY) {
               CraftTweakerAPI.logError("Could not find potion type for " + this.potion + ". Ignoring Cauldron Brewing recipe removal.");
               return;
            }

            if (potion == PotionTypes.WATER) {
               potion = FluidRegistry.WATER;
            }
         }

         Cauldron.REMOVED_POTION.add(Triple.of(this.output, this.input, potion));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }
}
