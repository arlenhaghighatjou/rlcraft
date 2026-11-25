package com.blamejared.compat.forestry;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.TreeManager;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.forestry.CharcoalWall")
@ModOnly("forestry")
@ZenRegister
public class CharcoalPile {
   public static final String NAME = "Forestry Charcoal Wall";

   public CharcoalPile() {
   }

   @ZenMethod
   public static void addWall(IBlock block, int amount) {
      ModTweaker.LATE_ADDITIONS.add(new CharcoalPile.Add(asBlock(block), amount));
   }

   @ZenMethod
   public static void addWallState(IBlockState state, int amount) {
      ModTweaker.LATE_ADDITIONS.add(new CharcoalPile.Add(asBlockState(state), amount));
   }

   @ZenMethod
   public static void addWallStack(IItemStack stack, int amount) {
      ModTweaker.LATE_ADDITIONS.add(new CharcoalPile.Add(asBlock(stack), amount));
   }

   @ZenMethod
   public static void removeWall(IBlock block) {
      ModTweaker.LATE_REMOVALS.add(new CharcoalPile.Remove(asBlock(block)));
   }

   @ZenMethod
   public static void removeWallState(IBlockState state) {
      ModTweaker.LATE_REMOVALS.add(new CharcoalPile.Remove(asBlockState(state)));
   }

   @ZenMethod
   public static void removeWallStack(IItemStack stack) {
      ModTweaker.LATE_REMOVALS.add(new CharcoalPile.Remove(asBlock(stack)));
   }

   @Nullable
   private static Block asBlock(IBlock block) {
      Object internal = block.getDefinition().getInternal();
      if (!(internal instanceof Block)) {
         CraftTweakerAPI.logError("Not a valid block: " + block);
         return null;
      } else {
         return (Block)internal;
      }
   }

   @Nullable
   private static net.minecraft.block.state.IBlockState asBlockState(IBlockState blockstate) {
      Object internal = blockstate.getInternal();
      if (!(internal instanceof net.minecraft.block.state.IBlockState)) {
         CraftTweakerAPI.logError("Not a valid blockstate: " + blockstate);
         return null;
      } else {
         return (net.minecraft.block.state.IBlockState)internal;
      }
   }

   @Nullable
   private static Block asBlock(IItemStack stack) {
      if (!InputHelper.isABlock(stack)) {
         CraftTweakerAPI.logError("Not a valid block: " + stack.getDisplayName());
         return null;
      } else {
         return asBlock(stack.asBlock());
      }
   }

   private static class Add extends CharcoalPile.BaseCharcoal {
      private final int amount;

      protected Add(Block block, int amount) {
         super(block);
         this.amount = amount;
      }

      protected Add(net.minecraft.block.state.IBlockState state, int amount) {
         super(state);
         this.amount = amount;
      }

      public void apply() {
         if (manager == null) {
            CraftTweakerAPI.logError("Charcoal manager is null, probably the charcoal module is not enabled");
         } else {
            if (this.block != null) {
               manager.registerWall(this.block, this.amount);
            } else if (this.state != null) {
               manager.registerWall(this.state, this.amount);
            } else {
               CraftTweakerAPI.logError("Both block and blockstate were null, no wall registered");
            }

         }
      }
   }

   private static class BaseCharcoal extends BaseAction {
      protected static final ICharcoalManager manager = TreeManager.charcoalManager;
      protected final Block block;
      protected final net.minecraft.block.state.IBlockState state;

      protected BaseCharcoal(Block block) {
         super("Forestry Charcoal Wall");
         this.block = block;
         this.state = null;
      }

      protected BaseCharcoal(net.minecraft.block.state.IBlockState state) {
         super("Forestry Charcoal Wall");
         this.state = state;
         this.block = null;
      }

      public void apply() {
         CraftTweakerAPI.logError("BaseCharcoal.apply() is not implemented");
      }
   }

   private static class Remove extends CharcoalPile.BaseCharcoal {
      protected Remove(Block block) {
         super(block);
      }

      protected Remove(net.minecraft.block.state.IBlockState state) {
         super(state);
      }

      public void apply() {
         if (manager == null) {
            CraftTweakerAPI.logError("Charcoal manager is null, probably the charcoal module is not enabled");
         } else {
            boolean success = false;
            if (this.block != null) {
               success = manager.removeWall(this.block);
            } else if (this.state != null) {
               success = manager.removeWall(this.state);
            } else {
               CraftTweakerAPI.logError("Both block and blockstate were null, no wall removed");
            }

            if (!success) {
               CraftTweakerAPI.logError("Removing wall was not successful");
            }

         }
      }
   }
}
