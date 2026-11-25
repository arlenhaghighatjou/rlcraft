package com.blamejared.compat.tcomplement.highoven;

import com.blamejared.ModTweaker;
import com.blamejared.compat.mantle.RecipeMatchIIngredient;
import com.blamejared.compat.tcomplement.highoven.recipes.MixRecipeTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import com.google.common.collect.ImmutableList;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.BiConsumer;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("mods.tcomplement.highoven.MixRecipeBuilder")
@ZenRegister
@ModOnly("tcomplement")
public class MixRecipeBuilder {
   private ILiquidStack output;
   private ILiquidStack input;
   private int temp;
   private Map<IIngredient, Integer> oxidizers;
   private Map<IIngredient, Integer> reducers;
   private Map<IIngredient, Integer> purifiers;

   public MixRecipeBuilder(ILiquidStack output, ILiquidStack input, int temp) {
      this.output = output;
      this.input = input;
      this.temp = temp;
      this.oxidizers = new LinkedHashMap();
      this.reducers = new LinkedHashMap();
      this.purifiers = new LinkedHashMap();
   }

   @ZenGetter("output")
   public ILiquidStack getOutput() {
      return this.output;
   }

   @ZenSetter("output")
   public void setOutput(ILiquidStack output) {
      this.output = output;
   }

   @ZenGetter("input")
   public ILiquidStack getInput() {
      return this.input;
   }

   @ZenSetter("input")
   public void setInput(ILiquidStack input) {
      this.input = input;
   }

   @ZenGetter("temp")
   public int getTemp() {
      return this.temp;
   }

   @ZenSetter("temp")
   public void setTemp(int temp) {
      this.temp = temp;
   }

   @ZenGetter("oxidizers")
   public List<IIngredient> getOxidizers() {
      return ImmutableList.copyOf(this.oxidizers.keySet());
   }

   @ZenGetter("reducers")
   public List<IIngredient> getReducers() {
      return ImmutableList.copyOf(this.reducers.keySet());
   }

   @ZenGetter("purifiers")
   public List<IIngredient> getPurifiers() {
      return ImmutableList.copyOf(this.purifiers.keySet());
   }

   @ZenMethod
   public int getOxidizerChance(IIngredient oxidizer) {
      return ((Integer)this.oxidizers.getOrDefault(oxidizer, Integer.valueOf(-1))).intValue();
   }

   @ZenMethod
   public int getReducerChance(IIngredient reducer) {
      return ((Integer)this.reducers.getOrDefault(reducer, Integer.valueOf(-1))).intValue();
   }

   @ZenMethod
   public int getPurifierChance(IIngredient purifier) {
      return ((Integer)this.purifiers.getOrDefault(purifier, Integer.valueOf(-1))).intValue();
   }

   @ZenMethod
   public MixRecipeBuilder addOxidizer(IIngredient oxidizer, int consumeChance) {
      this.oxidizers.put(oxidizer, Integer.valueOf(consumeChance));
      return this;
   }

   @ZenMethod
   public MixRecipeBuilder addReducer(IIngredient reducer, int consumeChance) {
      this.reducers.put(reducer, Integer.valueOf(consumeChance));
      return this;
   }

   @ZenMethod
   public MixRecipeBuilder addPurifier(IIngredient purifier, int consumeChance) {
      this.purifiers.put(purifier, Integer.valueOf(consumeChance));
      return this;
   }

   @ZenMethod
   public MixRecipeBuilder removeOxidizer(IIngredient oxidizer) {
      this.oxidizers.remove(oxidizer);
      return this;
   }

   @ZenMethod
   public MixRecipeBuilder removeReducer(IIngredient reducer) {
      this.reducers.remove(reducer);
      return this;
   }

   @ZenMethod
   public MixRecipeBuilder removePurifier(IIngredient purifier) {
      this.purifiers.remove(purifier);
      return this;
   }

   @ZenMethod
   public MixRecipeBuilder removeAllOxidizer() {
      this.oxidizers = new LinkedHashMap();
      return this;
   }

   @ZenMethod
   public MixRecipeBuilder removeAllReducer() {
      this.reducers = new LinkedHashMap();
      return this;
   }

   @ZenMethod
   public MixRecipeBuilder removeAllPurifier() {
      this.purifiers = new LinkedHashMap();
      return this;
   }

   @ZenMethod
   public void register() {
      ModTweaker.LATE_ADDITIONS.add(new MixRecipeBuilder.AddMixRecipe(this.output, this.input, this.temp, this.oxidizers, this.reducers, this.purifiers));
   }

   private static class AddMixRecipe extends BaseAction {
      private FluidStack input;
      private FluidStack output;
      private int temp;
      private List<SimpleEntry<RecipeMatch, MixAdditive>> additives;

      public AddMixRecipe(ILiquidStack output, ILiquidStack input, int temp, Map<IIngredient, Integer> oxidizers, Map<IIngredient, Integer> reducers, Map<IIngredient, Integer> purifiers) {
         super("High Oven Mix");
         this.input = InputHelper.toFluid(input);
         this.output = InputHelper.toFluid(output);
         this.temp = temp;
         this.additives = new LinkedList();
         oxidizers.forEach((ingredient, chance) -> {
            this.additives.add(new SimpleEntry(new RecipeMatchIIngredient(ingredient, chance.intValue()), MixAdditive.OXIDIZER));
         });
         reducers.forEach((ingredient, chance) -> {
            this.additives.add(new SimpleEntry(new RecipeMatchIIngredient(ingredient, chance.intValue()), MixAdditive.REDUCER));
         });
         purifiers.forEach((ingredient, chance) -> {
            this.additives.add(new SimpleEntry(new RecipeMatchIIngredient(ingredient, chance.intValue()), MixAdditive.PURIFIER));
         });
      }

      public void apply() {
         IMixRecipe recipe = TCompRegistry.registerMix(new MixRecipeTweaker(this.input, this.output, this.temp));

         for(SimpleEntry<RecipeMatch, MixAdditive> entry : this.additives) {
            recipe.addAdditive((MixAdditive)entry.getValue(), (RecipeMatch)entry.getKey());
         }

      }

      public String describe() {
         return String.format("Adding %s Recipe for %s", this.name, this.getRecipeInfo());
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output) + " from " + LogHelper.getStackDescription(this.input);
      }
   }
}
