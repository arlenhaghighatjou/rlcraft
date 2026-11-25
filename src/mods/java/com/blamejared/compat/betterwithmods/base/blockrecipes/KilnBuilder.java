package com.blamejared.compat.betterwithmods.base.blockrecipes;

import betterwithmods.common.registry.block.managers.CraftingManagerBlock;
import betterwithmods.common.registry.block.recipe.KilnRecipe;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.function.Supplier;
import stanhebben.zenscript.annotations.ZenMethod;

public class KilnBuilder extends BlockRecipeBuilder<KilnRecipe> {
   private int heat = 2;
   private boolean ignoreHeat;

   public KilnBuilder(Supplier<CraftingManagerBlock<KilnRecipe>> registry, String name) {
      super(registry, name);
   }

   @ZenMethod
   public KilnBuilder setHeat(int heat) {
      this.heat = heat;
      return this;
   }

   @ZenMethod
   public KilnBuilder setIgnoreHeat(boolean ignoreHeat) {
      this.ignoreHeat = ignoreHeat;
      return this;
   }

   @ZenMethod
   public KilnBuilder buildRecipe(IIngredient input, IItemStack[] outputs) {
      this._buildRecipe(input, outputs);
      return this;
   }

   public void build() {
      this.addRecipe((new KilnRecipe(this.input, this.outputs, this.heat)).setIgnoreHeat(this.ignoreHeat));
   }
}
