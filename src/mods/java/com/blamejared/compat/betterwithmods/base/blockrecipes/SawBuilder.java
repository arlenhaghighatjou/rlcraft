package com.blamejared.compat.betterwithmods.base.blockrecipes;

import betterwithmods.common.registry.block.managers.CraftingManagerBlock;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.function.Supplier;
import stanhebben.zenscript.annotations.ZenMethod;

public class SawBuilder extends BlockRecipeBuilder<SawRecipe> {
   public SawBuilder(Supplier<CraftingManagerBlock<SawRecipe>> registry, String name) {
      super(registry, name);
   }

   @ZenMethod
   public SawBuilder buildRecipe(IIngredient input, IItemStack[] outputs) {
      this._buildRecipe(input, outputs);
      return this;
   }

   public void build() {
      this.addRecipe(new SawRecipe(this.input, this.outputs));
   }

   @ZenMethod
   public void removeAll() {
      super.removeAll();
   }
}
