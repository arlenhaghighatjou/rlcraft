package com.blamejared.compat.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.bloodmagic.AlchemyArray")
@ZenRegister
@ModOnly("bloodmagic")
public class AlchemyArray {
   public AlchemyArray() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IItemStack input, IItemStack catalyst, @Optional String textureLocation) {
      ModTweaker.LATE_ADDITIONS.add(new AlchemyArray.Add(InputHelper.toStack(input), InputHelper.toStack(catalyst), InputHelper.toStack(output), textureLocation != null ? new ResourceLocation(textureLocation) : null));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input, IItemStack catalyst) {
      ModTweaker.LATE_REMOVALS.add(new AlchemyArray.Remove(InputHelper.toStack(input), InputHelper.toStack(catalyst)));
   }

   private static class Add extends BaseAction {
      private ItemStack input;
      private ItemStack catalyst;
      private ItemStack output;
      private ResourceLocation location;

      public Add(ItemStack input, ItemStack catalyst, ItemStack output, ResourceLocation location) {
         super("AlchemyArray");
         this.input = input;
         this.catalyst = catalyst;
         this.output = output;
         this.location = location;
      }

      public void apply() {
         BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(this.input, this.catalyst, this.output, this.location);
      }

      public String describe() {
         return "Adding AlchemyArray Recipe input=" + this.input + ", catalyst=" + this.catalyst + ", output=" + this.output + ", location=" + this.location;
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;
      private ItemStack catalyst;

      public Remove(ItemStack input, ItemStack catalyst) {
         super("AlchemyArray");
         this.input = input;
         this.catalyst = catalyst;
      }

      public void apply() {
         BloodMagicAPI.INSTANCE.getRecipeRegistrar().removeAlchemyArray(this.input, this.catalyst);
      }

      public String describe() {
         return "Removing AlchemyArray recipe for: " + this.input + " and " + this.catalyst;
      }
   }
}
