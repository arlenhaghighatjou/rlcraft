package com.blamejared.compat.thaumcraft.handlers.handlers;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.WeightedRandomLoot;

@ZenClass("mods.thaumcraft.LootBag")
@ZenRegister
@ModOnly("thaumcraft")
public class LootBag {
   public LootBag() {
   }

   @ZenMethod
   public static void addLoot(WeightedItemStack stack, int[] bagTypes) {
      if (bagTypes != null && bagTypes.length > 0) {
         ModTweaker.LATE_ADDITIONS.add(new LootBag.Add(InputHelper.toStack(stack.getStack()), (int)stack.getPercent(), bagTypes));
      } else {
         CraftTweakerAPI.logError("Invalid array size (or null) for LootBag!");
      }
   }

   @ZenMethod
   public static void removeLoot(IItemStack stack, int[] bagTypes) {
      if (bagTypes != null && bagTypes.length > 0) {
         ModTweaker.LATE_ADDITIONS.add(new LootBag.Remove(stack, bagTypes));
      } else {
         CraftTweakerAPI.logError("Invalid array size (or null) for LootBag!");
      }
   }

   private static class Add extends BaseAction {
      private ItemStack stack;
      private int chance;
      private int[] bagTypes;

      public Add(ItemStack stack, int chance, int[] bagTypes) {
         super("LootBag");
         this.stack = stack;
         this.chance = chance;
         this.bagTypes = bagTypes;
      }

      public void apply() {
         ThaumcraftApi.addLootBagItem(this.stack, this.chance, this.bagTypes);
      }

      public String describe() {
         return "Adding " + this.getRecipeInfo() + " to LootBag types: " + this.getBags();
      }

      public String getBags() {
         String[] arr = new String[this.bagTypes.length];

         for(int i = 0; i < this.bagTypes.length; ++i) {
            arr[i] = String.valueOf(this.bagTypes[i]);
         }

         return String.join(",", arr);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }

   private static class Remove extends BaseAction {
      private IItemStack stack;
      private int[] bagTypes;

      public Remove(IItemStack stack, int[] bagTypes) {
         super("LootBag");
         this.stack = stack;
         this.bagTypes = bagTypes;
      }

      public void apply() {
         for(int i : this.bagTypes) {
            ArrayList<WeightedRandomLoot> list = new ArrayList();
            switch(i) {
            case 0:
               list = WeightedRandomLoot.lootBagCommon;
               break;
            case 1:
               list = WeightedRandomLoot.lootBagUncommon;
               break;
            case 2:
               list = WeightedRandomLoot.lootBagRare;
               break;
            default:
               CraftTweakerAPI.logError("Invalid bag type! Type: " + i + " is out of bounds!");
            }

            List<WeightedRandomLoot> remove = new ArrayList();

            for(WeightedRandomLoot loot : list) {
               if (this.stack.matches(InputHelper.toIItemStack(loot.item))) {
                  remove.add(loot);
               }
            }

            list.removeAll(remove);
         }

      }

      public String describe() {
         return "Removing " + this.getRecipeInfo() + " from LootBag types: " + this.getBags();
      }

      public String getBags() {
         String[] arr = new String[this.bagTypes.length];

         for(int i = 0; i < this.bagTypes.length; ++i) {
            arr[i] = String.valueOf(this.bagTypes[i]);
         }

         return String.join(",", arr);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }
}
