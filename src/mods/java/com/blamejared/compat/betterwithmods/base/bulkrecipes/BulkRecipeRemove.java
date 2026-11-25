package com.blamejared.compat.betterwithmods.base.bulkrecipes;

import betterwithmods.common.registry.bulk.manager.CraftingManagerBulk;
import betterwithmods.common.registry.bulk.recipes.BulkRecipe;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import com.google.common.collect.Lists;
import crafttweaker.api.item.IItemStack;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;

public class BulkRecipeRemove<T extends BulkRecipe> extends BaseAction {
   private final List<ItemStack> outputs;
   private CraftingManagerBulk<T> manager;

   public BulkRecipeRemove(String name, Supplier<CraftingManagerBulk<T>> manager, IItemStack[] outputs) {
      this(name, (CraftingManagerBulk)manager.get(), Lists.newArrayList(InputHelper.toStacks(outputs)));
   }

   private BulkRecipeRemove(String name, CraftingManagerBulk<T> manager, List<ItemStack> outputs) {
      super(name);
      this.manager = manager;
      this.outputs = outputs;
   }

   public void apply() {
      if (!this.manager.remove(this.outputs)) {
         LogHelper.logWarning(String.format("No recipes were removed for output %s", this.getRecipeInfo()));
      } else {
         LogHelper.logInfo(String.format("Successfully removed all recipes for %s", this.getRecipeInfo()));
      }

   }

   protected String getRecipeInfo() {
      return String.format("%s - %s", this.name, this.outputs.stream().map(ItemStack::getDisplayName).collect(Collectors.joining(",")));
   }
}
