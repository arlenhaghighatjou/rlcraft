package com.blamejared.compat.thaumcraft.handlers.handlers;

import com.blamejared.ModTweaker;
import com.blamejared.compat.thaumcraft.handlers.ThaumCraft;
import com.blamejared.compat.thaumcraft.handlers.aspects.CTAspectStack;
import com.blamejared.mtlib.helpers.InputHelper;
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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.internal.CommonInternals;

@ZenClass("mods.thaumcraft.Crucible")
@ZenRegister
@ModOnly("thaumcraft")
public class Crucible {
   public Crucible() {
   }

   @ZenMethod
   public static void registerRecipe(String name, String researchKey, IItemStack output, IIngredient input, CTAspectStack[] aspects) {
      ModTweaker.LATE_ADDITIONS.add(new Crucible.Add(new ResourceLocation("thaumcraft", name), researchKey, InputHelper.toStack(output), InputHelper.toObject(input), ThaumCraft.getAspects(aspects)));
   }

   @ZenMethod
   public static void removeRecipe(String name) {
      ModTweaker.LATE_REMOVALS.add(new Crucible.Remove(name));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack output) {
      ModTweaker.LATE_REMOVALS.add(new Crucible.Remove(output));
   }

   public static class Add extends BaseAction {
      private ResourceLocation name;
      private String research;
      private ItemStack output;
      private Object input;
      private AspectList aspects;

      public Add(ResourceLocation name1, String research, ItemStack output, Object input, AspectList aspects) {
         super("Crucible");
         this.name = name1;
         this.research = research;
         this.output = output;
         this.input = input;
         this.aspects = aspects;
      }

      public void apply() {
         ThaumcraftApi.addCrucibleRecipe(this.name, new CrucibleRecipe(this.research, this.output, this.input, this.aspects));
      }
   }

   public static class Remove extends BaseAction {
      private String name;
      private IItemStack output;

      protected Remove(String name) {
         super("Crucible");
         this.name = name;
      }

      protected Remove(IItemStack output) {
         super("Crucible");
         this.output = output;
      }

      public void apply() {
         List<ResourceLocation> toRemove = new ArrayList();

         for(Entry<ResourceLocation, IThaumcraftRecipe> entry : CommonInternals.craftingRecipeCatalog.entrySet()) {
            if (entry.getValue() instanceof CrucibleRecipe) {
               if (this.name != null && !this.name.isEmpty() && ((ResourceLocation)entry.getKey()).toString().equals(this.name)) {
                  toRemove.add(entry.getKey());
               }

               if (this.output != null && !this.output.isEmpty() && this.output.matches(InputHelper.toIItemStack(((CrucibleRecipe)entry.getValue()).getRecipeOutput()))) {
                  toRemove.add(entry.getKey());
               }
            }
         }

         HashMap var10001 = CommonInternals.craftingRecipeCatalog;
         CommonInternals.craftingRecipeCatalog.getClass();
         toRemove.forEach(var10001::remove);
      }
   }
}
