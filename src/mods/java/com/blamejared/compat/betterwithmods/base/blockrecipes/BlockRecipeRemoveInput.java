package com.blamejared.compat.betterwithmods.base.blockrecipes;

import betterwithmods.common.registry.block.managers.CraftingManagerBlock;
import betterwithmods.common.registry.block.recipe.BlockRecipe;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;

public class BlockRecipeRemoveInput<T extends BlockRecipe> extends BaseAction {
   private final ItemStack input;
   private CraftingManagerBlock<T> manager;

   public BlockRecipeRemoveInput(String name, CraftingManagerBlock<T> manager, IItemStack input) {
      this(name, manager, InputHelper.toStack(input));
   }

   private BlockRecipeRemoveInput(String name, CraftingManagerBlock<T> manager, ItemStack input) {
      super(name);
      this.manager = manager;
      this.input = input;
   }

   public void apply() {
      if (!this.manager.removeByInput(this.input)) {
         LogHelper.logWarning(String.format("No recipes were removed for input %s", this.getRecipeInfo()));
      } else {
         LogHelper.logInfo(String.format("Successfully removed all recipes with %s as input", this.getRecipeInfo()));
      }

   }

   protected String getRecipeInfo() {
      return String.format("%s - %s", this.name, this.input.getDisplayName());
   }
}
