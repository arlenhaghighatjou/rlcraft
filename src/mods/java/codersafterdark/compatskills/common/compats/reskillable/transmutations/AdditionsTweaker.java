package codersafterdark.compatskills.common.compats.reskillable.transmutations;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.reskillable.ReskillableCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.reskillable.api.transmutations.TransmutationRegistry;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("reskillable")
@ZenClass("mods.compatskills.transmutations.additions")
@ZenRegister
public class AdditionsTweaker {
   public AdditionsTweaker() {
   }

   @ZenMethod
   public static void addEntryToReagent(IItemStack reagent, IItemStack startState, IItemStack endState) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new AdditionsTweaker.AddEntryToReagent(CraftTweakerMC.getItemStack(reagent).getItem(), startState, endState));
      }

   }

   @ZenMethod
   public static void addEntryToReagent(IItemStack reagent, IBlockState state1, IBlockState state2) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new AdditionsTweaker.AddEntryToReagent(CraftTweakerMC.getItemStack(reagent).getItem(), CraftTweakerMC.getBlockState(state1), CraftTweakerMC.getBlockState(state2)));
      }

   }

   @ZenMethod
   public static void addEntryToReagentAgnostic(IItemStack startState, IItemStack endState) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new AdditionsTweaker.AddEntryToAgnosticReagent(startState, endState));
      }

   }

   @ZenMethod
   public static void addEntryToReagentAgnostic(IBlockState state1, IBlockState state2) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new AdditionsTweaker.AddEntryToAgnosticReagent(CraftTweakerMC.getBlockState(state1), CraftTweakerMC.getBlockState(state2)));
      }

   }

   private static class AddEntryToAgnosticReagent implements IAction {
      private final net.minecraft.block.state.IBlockState state1;
      private final net.minecraft.block.state.IBlockState state2;

      private AddEntryToAgnosticReagent(net.minecraft.block.state.IBlockState state1, net.minecraft.block.state.IBlockState state2) {
         this.state1 = state1;
         this.state2 = state2;
      }

      private AddEntryToAgnosticReagent(IItemStack state1, IItemStack state2) {
         this.state1 = CheckMethods.convertItemStackToIBlockState(CraftTweakerMC.getItemStack(state1));
         this.state2 = CheckMethods.convertItemStackToIBlockState(CraftTweakerMC.getItemStack(state2));
      }

      public void apply() {
         TransmutationRegistry.addEntryReagentAgnostic(this.state1, this.state2);
      }

      public String describe() {
         return "Added Transmutation for " + this.state1.getBlock().getLocalizedName() + " -> " + this.state2.getBlock().getLocalizedName() + " :: Using All Reagents";
      }
   }

   private static class AddEntryToReagent implements IAction {
      private final Item reagent;
      private final net.minecraft.block.state.IBlockState state1;
      private final net.minecraft.block.state.IBlockState state2;

      private AddEntryToReagent(Item reagent, net.minecraft.block.state.IBlockState state1, net.minecraft.block.state.IBlockState state2) {
         this.reagent = reagent;
         this.state1 = state1;
         this.state2 = state2;
      }

      private AddEntryToReagent(Item reagent, IItemStack state1, IItemStack state2) {
         this.reagent = reagent;
         this.state1 = CheckMethods.convertItemStackToIBlockState(CraftTweakerMC.getItemStack(state1));
         this.state2 = CheckMethods.convertItemStackToIBlockState(CraftTweakerMC.getItemStack(state2));
      }

      public void apply() {
         TransmutationRegistry.addEntryToReagent(this.reagent, this.state1, this.state2);
      }

      public String describe() {
         return "Added Transmutation for " + this.state1.getBlock().getLocalizedName() + " -> " + this.state2.getBlock().getLocalizedName() + " :: Using Reagent: " + (new ItemStack(this.reagent)).getDisplayName();
      }
   }
}
