package com.blamejared.compat.betterwithmods.base.blockrecipes;

import betterwithmods.common.registry.block.managers.CraftingManagerBlock;
import betterwithmods.common.registry.block.recipe.BlockRecipe;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import com.google.common.collect.Lists;
import crafttweaker.api.item.IItemStack;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;

public class BlockRecipeRemove<T extends BlockRecipe> extends BaseAction {
   private final List<ItemStack> outputs;
   private CraftingManagerBlock<T> manager;

   public BlockRecipeRemove(String name, CraftingManagerBlock<T> manager, IItemStack[] outputs) {
      this(name, manager, Lists.newArrayList(InputHelper.toStacks(outputs)));
   }

   private BlockRecipeRemove(String name, CraftingManagerBlock<T> manager, List<ItemStack> outputs) {
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
