package com.blamejared.compat.betterwithmods;

import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.KilnStructureManager;
import com.blamejared.ModTweaker;
import com.blamejared.compat.betterwithmods.base.blockrecipes.KilnBuilder;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.Kiln")
@ModOnly("betterwithmods")
@ZenRegister
public class Kiln {
   public static KilnBuilder INSTANCE = new KilnBuilder(() -> {
      return BWRegistry.KILN;
   }, "Kiln");

   public Kiln() {
   }

   @ZenMethod
   public static KilnBuilder builder() {
      return INSTANCE;
   }

   @ZenMethod
   public static void add(@Nonnull IIngredient input, IItemStack[] output) {
      builder().buildRecipe(input, output).build();
   }

   @ZenMethod
   public static void add(IItemStack[] output, @NotNull IIngredient input) {
      add(input, output);
   }

   @ZenMethod
   public static void remove(IItemStack input) {
      INSTANCE.removeRecipe(input);
   }

   @ZenMethod
   public static void remove(IItemStack[] outputs) {
      INSTANCE.removeRecipe(outputs);
   }

   @ZenMethod
   public static void removeAll() {
      builder().removeAll();
   }

   @ZenMethod
   public static void registerBlock(IItemStack block) {
      ModTweaker.LATE_ADDITIONS.add(new Kiln.KilnBlock(BWMRecipes.getStateFromStack(InputHelper.toStack(block))));
   }

   public static class KilnBlock extends BaseAction {
      private IBlockState state;

      KilnBlock(IBlockState state) {
         super("Set Kiln Structure Block");
         this.state = state;
      }

      public void apply() {
         KilnStructureManager.registerKilnBlock(this.state);
      }
   }
}
