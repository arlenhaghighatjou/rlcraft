package com.blamejared.compat.chisel;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import team.chisel.api.carving.CarvingUtils;

@ZenRegister
@ZenClass("mods.chisel.Carving")
@ModOnly("chisel")
public class Carving {
   public Carving() {
   }

   @ZenMethod
   public static void addGroup(String name) {
      ModTweaker.LATE_ADDITIONS.add(new Carving.ActionAddGroup(name));
   }

   @ZenMethod
   public static void addVariation(String groupName, IItemStack stack) {
      ModTweaker.LATE_ADDITIONS.add(new Carving.ActionAddVariation(groupName, InputHelper.toStack(stack)));
   }

   @ZenMethod
   public static void removeGroup(String name) {
      ModTweaker.LATE_REMOVALS.add(new Carving.ActionRemoveGroup(name));
   }

   @ZenMethod
   public static void removeVariation(String groupName, IItemStack stack) {
      ModTweaker.LATE_ADDITIONS.add(new Carving.ActionRemoveVariation(groupName, InputHelper.toStack(stack)));
   }

   private static class ActionAddGroup extends BaseAction {
      private String groupName;

      protected ActionAddGroup(String groupName) {
         super("Carving");
         this.groupName = groupName;
      }

      public void apply() {
         if (CarvingUtils.getChiselRegistry().getSortedGroupNames().contains(this.groupName)) {
            CraftTweakerAPI.logError("Error trying to add a  Chisel Group that already exists! Group: " + this.groupName);
         } else {
            CarvingUtils.getChiselRegistry().addGroup(CarvingUtils.getDefaultGroupFor(this.groupName));
         }
      }

      public String describe() {
         return "Adding chisel group called: " + this.groupName;
      }
   }

   private static class ActionAddVariation extends BaseAction {
      private String groupName;
      private ItemStack stack;

      protected ActionAddVariation(String groupName, ItemStack stack) {
         super("Variation");
         this.groupName = groupName;
         this.stack = stack;
      }

      public void apply() {
         CarvingUtils.getChiselRegistry().addVariation(this.groupName, CarvingUtils.variationFor(this.stack, 0));
      }

      public String describe() {
         return "Adding chisel variation for group: " + this.groupName + " and item: " + this.stack;
      }
   }

   private static class ActionRemoveGroup extends BaseAction {
      private String groupName;

      protected ActionRemoveGroup(String groupName) {
         super("Carving");
         this.groupName = groupName;
      }

      public void apply() {
         if (!CarvingUtils.getChiselRegistry().getSortedGroupNames().contains(this.groupName)) {
            CraftTweakerAPI.logError("Error trying to remove Chisel Group that doesn't exist! Group: " + this.groupName);
         } else {
            CarvingUtils.getChiselRegistry().removeGroup(this.groupName);
         }
      }

      public String describe() {
         return "Removing chisel group called: " + this.groupName;
      }
   }

   private static class ActionRemoveVariation extends BaseAction {
      private String groupName;
      private ItemStack item;

      protected ActionRemoveVariation(String groupName, ItemStack item) {
         super("Variation");
         this.groupName = groupName;
         this.item = item;
      }

      public void apply() {
         CarvingUtils.getChiselRegistry().removeVariation(this.item, this.groupName);
      }

      public String describe() {
         return "Removing chisel group called: " + this.groupName;
      }
   }
}
