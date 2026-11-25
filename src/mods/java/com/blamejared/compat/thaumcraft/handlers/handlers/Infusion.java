package com.blamejared.compat.thaumcraft.handlers.handlers;

import com.blamejared.ModTweaker;
import com.blamejared.compat.thaumcraft.handlers.ThaumCraft;
import com.blamejared.compat.thaumcraft.handlers.aspects.CTAspectStack;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.internal.CommonInternals;

@ZenClass("mods.thaumcraft.Infusion")
@ZenRegister
@ModOnly("thaumcraft")
public class Infusion {
   public Infusion() {
   }

   @ZenMethod
   public static void registerRecipe(String name, String research, IItemStack output, int instability, CTAspectStack[] aspects, IIngredient centralItem, IIngredient[] recipe) {
      InfusionRecipe infRecipe = new InfusionRecipe(research, InputHelper.toStack(output), instability, ThaumCraft.getAspects(aspects), InputHelper.toObject(centralItem), InputHelper.toObjects(recipe));
      ModTweaker.LATE_ADDITIONS.add(new Infusion.Add(new ResourceLocation("thaumcraft", name), infRecipe));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack output) {
      ModTweaker.LATE_REMOVALS.add(new Infusion.Remove(output));
   }

   @ZenMethod
   public static void removeRecipe(String recipeName) {
      ModTweaker.LATE_REMOVALS.add(new Infusion.Remove(recipeName));
   }

   private static class Add extends BaseAction {
      private ResourceLocation resourceLocation;
      private InfusionRecipe recipe;

      public Add(ResourceLocation resourceLocation, InfusionRecipe recipe) {
         super("Infusion");
         this.resourceLocation = resourceLocation;
         this.recipe = recipe;
      }

      public void apply() {
         ThaumcraftApi.addInfusionCraftingRecipe(this.resourceLocation, this.recipe);
      }

      public String describe() {
         return "Adding infusion recipe for: " + this.recipe.recipeOutput;
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.recipe.getRecipeOutput());
      }
   }

   private static class Remove extends BaseAction {
      private IItemStack output;
      private String recipeName;

      public Remove(IItemStack output) {
         super("Infusion");
         this.output = output;
      }

      public Remove(String recipeName) {
         super("Infusion");
         this.recipeName = recipeName;
      }

      public void apply() {
         List<ResourceLocation> remove = new ArrayList();

         for(Entry<ResourceLocation, IThaumcraftRecipe> entry : CommonInternals.craftingRecipeCatalog.entrySet()) {
            if (entry.getValue() instanceof InfusionRecipe) {
               InfusionRecipe recipe = (InfusionRecipe)entry.getValue();
               if (this.output != null) {
                  if (recipe.getRecipeOutput() instanceof ItemStack) {
                     IItemStack recOutput = InputHelper.toIItemStack((ItemStack)recipe.getRecipeOutput());
                     if (this.output.matches(recOutput)) {
                        remove.add(entry.getKey());
                     }
                  }
               } else if (this.recipeName != null && ((ResourceLocation)entry.getKey()).toString().equals(this.recipeName)) {
                  remove.add(entry.getKey());
               }
            }
         }

         HashMap var10001 = CommonInternals.craftingRecipeCatalog;
         CommonInternals.craftingRecipeCatalog.getClass();
         remove.forEach(var10001::remove);
      }

      public String describe() {
         return "Removing Infusion recipe for:" + this.getRecipeInfo();
      }

      protected String getRecipeInfo() {
         return this.output == null ? this.recipeName : LogHelper.getStackDescription(this.output);
      }
   }
}
