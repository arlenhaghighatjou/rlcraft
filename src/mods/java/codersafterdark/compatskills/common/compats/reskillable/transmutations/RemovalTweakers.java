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
@ZenClass("mods.compatskills.transmutations.removals")
@ZenRegister
public class RemovalTweakers {
   public RemovalTweakers() {
   }

   @ZenMethod
   public static void removeStartStateFromReagent(IItemStack reagent, IItemStack startState) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RemovalTweakers.RemoveStartStateFromReagent(reagent, startState));
      }

   }

   @ZenMethod
   public static void removeStartStateFromReagent(IItemStack reagent, IBlockState state) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RemovalTweakers.RemoveStartStateFromReagent(reagent, state));
      }

   }

   @ZenMethod
   public static void removeStartStateReagentAgnostic(IItemStack state) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RemovalTweakers.RemoveStartStateReagentAgnostic(state));
      }

   }

   @ZenMethod
   public static void removeStartStateReagentAgnostic(IBlockState state) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RemovalTweakers.RemoveStartStateReagentAgnostic(state));
      }

   }

   @ZenMethod
   public static void removeEndStateFromReagent(IItemStack reagent, IItemStack state) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RemovalTweakers.RemoveEndStateFromReagent(reagent, state));
      }

   }

   @ZenMethod
   public static void removeEndStateFromReagent(IItemStack reagent, IBlockState state) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RemovalTweakers.RemoveEndStateFromReagent(reagent, state));
      }

   }

   @ZenMethod
   public static void removeEndStateReagentAgnostic(IItemStack state) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RemovalTweakers.RemoveEndStateReagentAgnostic(state));
      }

   }

   @ZenMethod
   public static void removeEndStateReagentAgnostic(IBlockState state) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RemovalTweakers.RemoveEndStateReagentAgnostic(state));
      }

   }

   private static class RemoveEndStateFromReagent implements IAction {
      private final Item reagent;
      private final net.minecraft.block.state.IBlockState state;

      private RemoveEndStateFromReagent(IItemStack reagent, IItemStack state) {
         this.reagent = CraftTweakerMC.getItemStack(reagent).getItem();
         this.state = CheckMethods.convertItemStackToIBlockState(CraftTweakerMC.getItemStack(state));
      }

      private RemoveEndStateFromReagent(IItemStack reagent, IBlockState state) {
         this.reagent = CraftTweakerMC.getItemStack(reagent).getItem();
         this.state = CraftTweakerMC.getBlockState(state);
      }

      public void apply() {
         TransmutationRegistry.removeEndStateFromReagent(this.reagent, this.state);
      }

      public String describe() {
         return "Removing End State: " + this.state.getBlock().getLocalizedName() + " From Reagent: " + this.reagent.getItemStackDisplayName(new ItemStack(this.reagent));
      }
   }

   private static class RemoveEndStateReagentAgnostic implements IAction {
      private final net.minecraft.block.state.IBlockState state;

      private RemoveEndStateReagentAgnostic(IItemStack state) {
         this.state = CheckMethods.convertItemStackToIBlockState(CraftTweakerMC.getItemStack(state));
      }

      private RemoveEndStateReagentAgnostic(IBlockState state) {
         this.state = CraftTweakerMC.getBlockState(state);
      }

      public void apply() {
         TransmutationRegistry.removeEndStateFromReagentAgnostic(this.state);
      }

      public String describe() {
         return "Removing End State: " + this.state.getBlock().getLocalizedName() + " From All Reagents!";
      }
   }

   private static class RemoveStartStateFromReagent implements IAction {
      private final Item reagent;
      private final net.minecraft.block.state.IBlockState state;

      private RemoveStartStateFromReagent(IItemStack reagent, IItemStack state) {
         this.reagent = CraftTweakerMC.getItemStack(reagent).getItem();
         this.state = CheckMethods.convertItemStackToIBlockState(CraftTweakerMC.getItemStack(state));
      }

      private RemoveStartStateFromReagent(IItemStack reagent, IBlockState state) {
         this.reagent = CraftTweakerMC.getItemStack(reagent).getItem();
         this.state = CraftTweakerMC.getBlockState(state);
      }

      public void apply() {
         TransmutationRegistry.removeStartStateFromReagent(this.reagent, this.state);
      }

      public String describe() {
         return "Removing Start State: " + this.state.getBlock().getLocalizedName() + " From Reagent: " + this.reagent.getItemStackDisplayName(new ItemStack(this.reagent));
      }
   }

   private static class RemoveStartStateReagentAgnostic implements IAction {
      private final net.minecraft.block.state.IBlockState state;

      private RemoveStartStateReagentAgnostic(IItemStack state) {
         this.state = CheckMethods.convertItemStackToIBlockState(CraftTweakerMC.getItemStack(state));
      }

      private RemoveStartStateReagentAgnostic(IBlockState state) {
         this.state = CraftTweakerMC.getBlockState(state);
      }

      public void apply() {
         TransmutationRegistry.removeStartStateFromReagentAgnostic(this.state);
      }

      public String describe() {
         return "Removing Start State: " + this.state.getBlock().getLocalizedName() + " From All Reagents!";
      }
   }
}
