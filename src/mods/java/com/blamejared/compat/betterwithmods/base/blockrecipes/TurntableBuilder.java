package com.blamejared.compat.betterwithmods.base.blockrecipes;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.block.managers.CraftingManagerBlock;
import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import stanhebben.zenscript.annotations.ZenMethod;

public class TurntableBuilder extends BlockRecipeBuilder<TurntableRecipe> {
   private int rotations = 8;
   private IBlockState productState = Blocks.AIR.getDefaultState();

   public TurntableBuilder(Supplier<CraftingManagerBlock<TurntableRecipe>> registry, String name) {
      super(registry, name);
   }

   @ZenMethod
   public TurntableBuilder setRotations(int rotations) {
      this.rotations = rotations;
      return this;
   }

   @ZenMethod
   public TurntableBuilder setProductState(IItemStack productState) {
      if (InputHelper.isABlock(productState)) {
         Block block = CraftTweakerMC.getBlock(productState);
         this.productState = block.getStateFromMeta(productState.getMetadata());
      } else {
         LogHelper.logError(String.format("%s Product State must create a valid BlockState", productState.getDisplayName()), new IllegalArgumentException(String.format("%s Product State must create a valid BlockState", productState.getDisplayName())));
      }

      return this;
   }

   @ZenMethod
   public TurntableBuilder buildRecipe(IIngredient input, IItemStack[] outputs) {
      this._buildRecipe(input, outputs);
      return this;
   }

   public void build() {
      this.addRecipe(new TurntableRecipe(this.input, this.outputs, this.productState, this.rotations));
   }

   public void removeRecipe(IBlockState productState) {
      ModTweaker.LATE_REMOVALS.add(new TurntableBuilder.TurntableRemoveProduct(this.name, productState));
   }

   public class TurntableRemoveProduct extends BaseAction {
      private final IBlockState productState;

      private TurntableRemoveProduct(String name, IBlockState productState) {
         super(name);
         this.productState = productState;
      }

      public void apply() {
         if (!BWRegistry.TURNTABLE.remove(this.productState)) {
            LogHelper.logWarning(String.format("No recipes were removed for input %s", this.getRecipeInfo(this.productState)));
         } else {
            LogHelper.logInfo(String.format("Succesfully removed all recipes with %s as input", this.getRecipeInfo(this.productState)));
         }

      }

      private String getRecipeInfo(IBlockState productState) {
         return String.format("%s - %s", this.name, productState.getBlock().toString() + "@" + productState.getBlock().getMetaFromState(productState));
      }
   }
}
