package com.blamejared.compat.betterwithmods;

import betterwithmods.common.registry.block.recipe.StateIngredient;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.HeatRegistry")
@ModOnly("betterwithmods")
@ZenRegister
public class HeatRegistry {
   public HeatRegistry() {
   }

   @ZenMethod
   public static void addHeatSource(IBlockState state, int heat) {
      CraftTweakerAPI.apply(new HeatRegistry.AddHeatSource(Arrays.asList(CraftTweakerMC.getBlockState(state)), heat));
   }

   @ZenMethod
   public static void addHeatSource(List<IBlockState> states, IItemStack displayStack, int heat) {
      CraftTweakerAPI.apply(new HeatRegistry.AddHeatSource((List)states.stream().map(CraftTweakerMC::getBlockState).collect(Collectors.toList()), CraftTweakerMC.getItemStack(displayStack), heat));
   }

   @ZenMethod
   public static void addHeatSource(IItemStack stack, int heat) {
      if (InputHelper.isABlock(stack)) {
         Block block = CraftTweakerMC.getBlock(stack);
         net.minecraft.block.state.IBlockState state = block.getStateFromMeta(stack.getMetadata());
         CraftTweakerAPI.apply(new HeatRegistry.AddHeatSource(Arrays.asList(state), CraftTweakerMC.getItemStack(stack), heat));
      } else {
         LogHelper.logError(String.format("%s input must create a valid BlockState", stack.getDisplayName()), new IllegalArgumentException(String.format("%s input must create a valid BlockState", stack.getDisplayName())));
      }

   }

   public static class AddHeatSource extends BaseAction {
      private List<net.minecraft.block.state.IBlockState> states;
      private ItemStack displayStack;
      private int heat;

      AddHeatSource(List<net.minecraft.block.state.IBlockState> states, ItemStack displayStack, int heat) {
         super("Add Heat");
         this.states = states;
         this.displayStack = displayStack;
         this.heat = heat;
      }

      AddHeatSource(List<net.minecraft.block.state.IBlockState> states, int heat) {
         this(states, new ItemStack(((net.minecraft.block.state.IBlockState)states.get(0)).getBlock()), heat);
      }

      public void apply() {
         BWMHeatRegistry.addHeatSource(new StateIngredient(this.states, Arrays.asList(this.displayStack)), this.heat);
      }
   }
}
