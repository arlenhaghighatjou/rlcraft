package com.blamejared.compat.thermalexpansion.devicemanagers;

import cofh.thermalexpansion.util.managers.device.FactorizerManager;
import com.blamejared.ModTweaker;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.Factorizer")
@ModOnly("thermalexpansion")
@ZenRegister
public class Factorizer {
   public Factorizer() {
   }

   @ZenMethod
   public static void addRecipeSplit(IItemStack in, IItemStack out) {
      ModTweaker.LATE_ADDITIONS.add(new Factorizer.ActionAddFactorizer(Factorizer.ActionAddFactorizer.Type.Split, in, out));
   }

   @ZenMethod
   public static void addRecipeCombine(IItemStack in, IItemStack out) {
      ModTweaker.LATE_ADDITIONS.add(new Factorizer.ActionAddFactorizer(Factorizer.ActionAddFactorizer.Type.Combine, in, out));
   }

   @ZenMethod
   public static void addRecipeBoth(IItemStack combined, IItemStack split) {
      ModTweaker.LATE_ADDITIONS.add(new Factorizer.ActionAddFactorizer(Factorizer.ActionAddFactorizer.Type.Both, combined, split));
   }

   @ZenMethod
   public static void removeRecipeCombine(IItemStack in) {
      ModTweaker.LATE_REMOVALS.add(new Factorizer.ActionRemoveFactorizer(in, false));
   }

   @ZenMethod
   public static void removeRecipeSplit(IItemStack in) {
      ModTweaker.LATE_REMOVALS.add(new Factorizer.ActionRemoveFactorizer(in, true));
   }

   private static final class ActionAddFactorizer implements IAction {
      private final Factorizer.ActionAddFactorizer.Type type;
      private final IItemStack input;
      private final IItemStack output;

      private ActionAddFactorizer(Factorizer.ActionAddFactorizer.Type type, IItemStack input, IItemStack output) {
         this.type = type;
         this.input = input;
         this.output = output;
      }

      public void apply() {
         ItemStack inputStack = CraftTweakerMC.getItemStack(this.input);
         ItemStack outputStack = CraftTweakerMC.getItemStack(this.output);
         switch(this.type) {
         case Split:
            FactorizerManager.addRecipe(inputStack, outputStack, true);
            break;
         case Combine:
            FactorizerManager.addRecipe(inputStack, outputStack, false);
            break;
         case Both:
            FactorizerManager.addRecipe(inputStack, outputStack, true);
            FactorizerManager.addRecipe(outputStack, inputStack, false);
         }

      }

      public String describe() {
         switch(this.type) {
         case Split:
            return String.format("Adding Factorizer Split recipe for %s -> %s", this.input.toCommandString(), this.output.toCommandString());
         case Combine:
            return String.format("Adding Factorizer Combine recipe for %s -> %s", this.input.toCommandString(), this.output.toCommandString());
         case Both:
            return String.format("Adding Factorizer Two-Way recipe for %s <-> %s", this.input.toCommandString(), this.output.toCommandString());
         default:
            return "Error, no valid Factorizer type!";
         }
      }

      private static enum Type {
         Split,
         Combine,
         Both;

         private Type() {
         }
      }
   }

   private static final class ActionRemoveFactorizer implements IAction {
      private final IItemStack input;
      private final boolean isSplit;

      private ActionRemoveFactorizer(IItemStack input, boolean isSplit) {
         this.input = input;
         this.isSplit = isSplit;
      }

      public void apply() {
         FactorizerManager.removeRecipe(CraftTweakerMC.getItemStack(this.input), this.isSplit);
      }

      public String describe() {
         return "Removing Factorizer for " + this.input.toCommandString();
      }
   }
}
