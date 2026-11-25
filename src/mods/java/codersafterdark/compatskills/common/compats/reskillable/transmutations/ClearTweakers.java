package codersafterdark.compatskills.common.compats.reskillable.transmutations;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.reskillable.ReskillableCompatHandler;
import codersafterdark.reskillable.api.transmutations.TransmutationRegistry;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("reskillable")
@ZenClass("mods.compatskills.transmutations.clears")
@ZenRegister
public class ClearTweakers {
   public ClearTweakers() {
   }

   @ZenMethod
   public static void clearMapOfReagent(IItemStack stack) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ClearTweakers.ClearMapOfReagent(stack));
      }

   }

   @ZenMethod
   public static void clearReagentOfEntries(IItemStack stack) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ClearTweakers.ClearReagentOfEntries(stack));
      }

   }

   @ZenMethod
   public static void clearReagentMap() {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ClearTweakers.ClearReagentMap());
      }

   }

   private static class ClearMapOfReagent implements IAction {
      private final Item reagent;

      private ClearMapOfReagent(IItemStack stack) {
         this.reagent = CraftTweakerMC.getItemStack(stack).getItem();
      }

      public void apply() {
         TransmutationRegistry.clearMapOfReagent(this.reagent);
      }

      public String describe() {
         return "Clearing Map of Reagent: " + this.reagent.getItemStackDisplayName(new ItemStack(this.reagent));
      }
   }

   private static class ClearReagentMap implements IAction {
      private ClearReagentMap() {
      }

      public void apply() {
         TransmutationRegistry.clearReagentMap();
      }

      public String describe() {
         return "Clearing Transmutation Map";
      }
   }

   private static class ClearReagentOfEntries implements IAction {
      private final Item reagent;

      private ClearReagentOfEntries(IItemStack stack) {
         this.reagent = CraftTweakerMC.getItemStack(stack).getItem();
      }

      public void apply() {
         TransmutationRegistry.clearReagentOfEntries(this.reagent);
      }

      public String describe() {
         return "Clearing Reagent: " + this.reagent.getItemStackDisplayName(new ItemStack(this.reagent)) + " Of Entries!";
      }
   }
}
