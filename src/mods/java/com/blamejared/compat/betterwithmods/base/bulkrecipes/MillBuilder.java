package com.blamejared.compat.betterwithmods.base.bulkrecipes;

import betterwithmods.common.registry.bulk.manager.CraftingManagerBulk;
import betterwithmods.common.registry.bulk.recipes.MillRecipe;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.function.Supplier;
import stanhebben.zenscript.annotations.ZenMethod;

public class MillBuilder extends BulkRecipeBuilder<MillRecipe> {
   private String sound;
   private int ticks;

   public MillBuilder(Supplier<CraftingManagerBulk<MillRecipe>> registry, String name) {
      super(registry, name);
   }

   @ZenMethod
   public void build() {
      this.addRecipe((new MillRecipe(this.inputs, this.outputs)).setSound(this.sound).setPriority(this.priority).setTicks(this.ticks));
   }

   @ZenMethod
   public MillBuilder setPriority(int priority) {
      this.priority = priority;
      return this;
   }

   @ZenMethod
   public MillBuilder setGrindType(String sound) {
      this.sound = sound;
      return this;
   }

   @ZenMethod
   public MillBuilder setTicks(int ticks) {
      this.ticks = ticks;
      return this;
   }

   @ZenMethod
   public MillBuilder buildRecipe(IIngredient[] inputs, IItemStack[] outputs) {
      this._buildRecipe(inputs, outputs);
      return this;
   }
}
