package com.blamejared.compat.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.core.registry.AlchemyTableRecipeRegistry;
import WayofTime.bloodmagic.item.types.ComponentTypes;
import WayofTime.bloodmagic.potion.BMPotionUtils;
import WayofTime.bloodmagic.recipe.alchemyTable.AlchemyTablePotionRecipe;
import WayofTime.bloodmagic.recipe.alchemyTable.AlchemyTableRecipe;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotionEffect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.bloodmagic.AlchemyTable")
@ZenRegister
@ModOnly("bloodmagic")
public class AlchemyTable {
   public AlchemyTable() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IIngredient[] inputs, int syphon, int ticks, int minTier) {
      if (inputs.length != 0 && inputs.length <= 6) {
         ModTweaker.LATE_ADDITIONS.add(new AlchemyTable.Add(InputHelper.toStack(output), syphon, ticks, minTier, InputHelper.toObjects(inputs)));
      } else {
         CraftTweakerAPI.logError("Invalid Input Array! Maximum size is 6!");
      }
   }

   @ZenMethod
   public static void addPotionRecipe(IItemStack[] inputs, IPotionEffect effects, int syphon, int ticks, int minTier) {
      if (inputs.length != 0 && inputs.length <= 5) {
         List<ItemStack> inputsNew = withoutFlask(Arrays.asList(InputHelper.toStacks(inputs)));
         ModTweaker.LATE_ADDITIONS.add(new AlchemyTable.AddPotion(inputsNew, CraftTweakerMC.getPotionEffect(effects), syphon, ticks, minTier));
      } else {
         CraftTweakerAPI.logError("Invalid Input Array! Maximum size is 5 (to account for catalysts)!");
      }
   }

   @ZenMethod
   public static void removeRecipe(IItemStack[] inputs) {
      if (inputs.length != 0 && inputs.length <= 6) {
         ModTweaker.LATE_REMOVALS.add(new AlchemyTable.Remove(InputHelper.toStacks(inputs)));
      } else {
         CraftTweakerAPI.logError("Invalid Input Array! Maximum size is 6!");
      }
   }

   public static List<ItemStack> withoutFlask(List<ItemStack> inputs) {
      List<ItemStack> inputsNew = new ArrayList();

      for(ItemStack item : inputs) {
         if (item.getItem() != RegistrarBloodMagicItems.POTION_FLASK) {
            inputsNew.add(item);
         }
      }

      return inputsNew;
   }

   public static String[] getStringFromStacks(Object[] arr) {
      String[] retArr = new String[arr.length];

      for(int i = 0; i < arr.length; ++i) {
         retArr[i] = arr[i].toString();
      }

      return retArr;
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private int syphon;
      private int ticks;
      private int minTier;
      private Object[] inputs;

      public Add(ItemStack output, int syphon, int ticks, int minTier, Object[] inputs) {
         super("AlchemyTable");
         this.output = output;
         this.syphon = syphon;
         this.ticks = ticks;
         this.minTier = minTier;
         this.inputs = inputs;
      }

      public void apply() {
         BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyTable(this.output, this.syphon, this.ticks, this.minTier, this.inputs);
      }

      public String describe() {
         return "Adding AlchemyTable recipe for: " + this.output + " from: [" + String.join(",", AlchemyTable.getStringFromStacks(this.inputs)) + "] syphon: " + this.syphon + ", ticks: " + this.ticks + ", minTier: " + this.minTier;
      }
   }

   private static class AddPotion extends BaseAction {
      private int syphon;
      private int ticks;
      private int minTier;
      private List<ItemStack> inputs;
      private PotionEffect effect;

      public AddPotion(List<ItemStack> inputs, PotionEffect effect, int syphon, int ticks, int minTier) {
         super("AlchemyTable");
         this.inputs = inputs;
         this.effect = effect;
         this.syphon = syphon;
         this.ticks = ticks;
         this.minTier = minTier;
      }

      public void apply() {
         AlchemyTableRecipeRegistry.registerRecipe(new AlchemyTablePotionRecipe(this.syphon, this.ticks, this.minTier, this.inputs, this.effect));
         List<ItemStack> lengtheningList = new ArrayList();
         lengtheningList.addAll(this.inputs);
         lengtheningList.add(ComponentTypes.CATALYST_LENGTH_1.getStack());
         AlchemyTableRecipeRegistry.registerRecipe(BMPotionUtils.getLengthAugmentRecipe(this.syphon, this.ticks, this.minTier, lengtheningList, this.effect, 1.0D));
         List<ItemStack> powerList = new ArrayList();
         powerList.addAll(this.inputs);
         powerList.add(ComponentTypes.CATALYST_POWER_1.getStack());
         AlchemyTableRecipeRegistry.registerRecipe(BMPotionUtils.getPowerAugmentRecipe(this.syphon, this.ticks, this.minTier, powerList, this.effect, 1));
      }

      public String describe() {
         return "Adding AlchemyTable potion recipe for: [" + this.effect.getEffectName() + "] with input: [" + String.join(",", AlchemyTable.getStringFromStacks(this.inputs.toArray())) + "] syphon: " + this.syphon + ", ticks: " + this.ticks + ", minTier: " + this.minTier;
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack[] inputs;

      public Remove(ItemStack[] inputs) {
         super("AlchemyTable");
         this.inputs = inputs;
      }

      public void apply() {
         if (!BloodMagicAPI.INSTANCE.getRecipeRegistrar().removeAlchemyTable(this.inputs)) {
            List<ItemStack> inputsList = new ArrayList(Arrays.asList(this.inputs));
            AlchemyTableRecipe recipe = AlchemyTableRecipeRegistry.getMatchingRecipe(inputsList, (World)null, (BlockPos)null);
            if (recipe == null) {
               return;
            }

            AlchemyTableRecipeRegistry.removeRecipe(recipe);
            List<ItemStack> newInputsList = AlchemyTable.withoutFlask(inputsList);
            newInputsList.add(recipe.getRecipeOutput(inputsList));
            newInputsList.add(ComponentTypes.CATALYST_POWER_1.getStack());
            recipe = AlchemyTableRecipeRegistry.getMatchingRecipe(newInputsList, (World)null, (BlockPos)null);
            if (recipe != null) {
               AlchemyTableRecipeRegistry.removeRecipe(recipe);
            }

            newInputsList.remove(ComponentTypes.CATALYST_POWER_1.getStack());
            newInputsList.add(ComponentTypes.CATALYST_LENGTH_1.getStack());
            recipe = AlchemyTableRecipeRegistry.getMatchingRecipe(newInputsList, (World)null, (BlockPos)null);
            if (recipe != null) {
               AlchemyTableRecipeRegistry.removeRecipe(recipe);
            }
         }

      }

      public String describe() {
         return "Removing AlchemyTable recipe for: [" + String.join(",", AlchemyTable.getStringFromStacks(this.inputs)) + "]";
      }
   }
}
