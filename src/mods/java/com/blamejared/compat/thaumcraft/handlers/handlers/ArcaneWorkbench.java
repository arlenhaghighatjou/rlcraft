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
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

@ZenClass("mods.thaumcraft.ArcaneWorkbench")
@ZenRegister
@ModOnly("thaumcraft")
public class ArcaneWorkbench {
   public ArcaneWorkbench() {
   }

   @ZenMethod
   public static void registerShapedRecipe(String name, String research, int vis, CTAspectStack[] aspectList, IItemStack output, IIngredient[][] input) {
      ModTweaker.LATE_ADDITIONS.add(new ArcaneWorkbench.AddShaped(new ResourceLocation("thaumcraft", name), research, vis, ThaumCraft.getAspects(aspectList), InputHelper.toStack(output), InputHelper.toShapedObjects(input)));
   }

   @ZenMethod
   public static void registerShapelessRecipe(String name, String research, int vis, CTAspectStack[] aspectList, IItemStack output, IIngredient[] input) {
      NonNullList<Ingredient> list = NonNullList.withSize(input.length, Ingredient.EMPTY);

      for(int i = 0; i < input.length; ++i) {
         list.set(i, CraftTweakerMC.getIngredient(input[i]));
      }

      ModTweaker.LATE_ADDITIONS.add(new ArcaneWorkbench.AddShapeless(new ResourceLocation("thaumcraft", name), research, vis, ThaumCraft.getAspects(aspectList), InputHelper.toStack(output), list));
   }

   @ZenMethod
   public static void removeRecipe(String name) {
      ModTweaker.LATE_REMOVALS.add(new ArcaneWorkbench.Remove(name));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack output) {
      ModTweaker.LATE_REMOVALS.add(new ArcaneWorkbench.Remove(output));
   }

   public static class AddShaped extends BaseAction {
      private ResourceLocation location;
      private String research;
      private int vis;
      private AspectList list;
      private ItemStack output;
      private Object[] shaped;

      public AddShaped(ResourceLocation location, String research, int vis, AspectList list, ItemStack output, Object[] shaped) {
         super("ArcaneWorkbench");
         this.location = location;
         this.research = research;
         this.vis = vis;
         this.list = list;
         this.output = output;
         this.shaped = shaped;
      }

      public void apply() {
         ThaumcraftApi.addArcaneCraftingRecipe(this.location, new ShapedArcaneRecipe(this.location, this.research, this.vis, this.list, this.output, this.shaped));
      }

      public String describe() {
         return "Adding shaped recipe for ArcaneWorkbench with name: " + this.location + " with output: " + LogHelper.getStackDescription(this.output);
      }
   }

   public static class AddShapeless extends BaseAction {
      private ResourceLocation location;
      private String research;
      private int vis;
      private AspectList list;
      private ItemStack output;
      private NonNullList<Ingredient> shapeless;

      public AddShapeless(ResourceLocation location, String research, int vis, AspectList list, ItemStack output, NonNullList<Ingredient> shapeless) {
         super("ArcaneWorkbench");
         this.location = location;
         this.research = research;
         this.vis = vis;
         this.list = list;
         this.output = output;
         this.shapeless = shapeless;
      }

      public void apply() {
         ThaumcraftApi.addArcaneCraftingRecipe(this.location, new ShapelessArcaneRecipe(this.location, this.research, this.vis, this.list, this.shapeless, this.output));
      }

      public String describe() {
         return "Adding shapeless recipe for ArcaneWorkbench with name: " + this.location + " with output: " + LogHelper.getStackDescription(this.output);
      }
   }

   public static class Remove extends BaseAction {
      private String name;
      private IItemStack output;

      public Remove(String name) {
         super("ArcaneWorkbench");
         this.name = name;
      }

      public Remove(IItemStack output) {
         super("ArcaneWorkbench");
         this.output = output;
      }

      public void apply() {
         List<ResourceLocation> removed = new ArrayList();

         for(Entry<ResourceLocation, IRecipe> entry : ForgeRegistries.RECIPES.getEntries()) {
            if (entry.getValue() instanceof IArcaneRecipe) {
               if (this.name != null && !this.name.isEmpty() && ((ResourceLocation)entry.getKey()).toString().equals(this.name)) {
                  removed.add(entry.getKey());
               }

               if (this.output != null && this.output.matches(InputHelper.toIItemStack(((IRecipe)entry.getValue()).getRecipeOutput()))) {
                  removed.add(entry.getKey());
               }
            }
         }

         ForgeRegistry var10001 = RegistryManager.ACTIVE.getRegistry(GameData.RECIPES);
         removed.forEach(var10001::remove);
      }
   }
}
