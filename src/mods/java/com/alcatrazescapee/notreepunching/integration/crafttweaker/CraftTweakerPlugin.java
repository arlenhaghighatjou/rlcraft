package com.alcatrazescapee.notreepunching.integration.crafttweaker;

import com.alcatrazescapee.notreepunching.util.HarvestBlockHandler;
import com.alcatrazescapee.notreepunching.util.WoodRecipeHandler;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.notreepunching.NoTreePunching")
public final class CraftTweakerPlugin {
   public CraftTweakerPlugin() {
   }

   @ZenMethod
   public static void addKnifeGrassDrop(final IItemStack stack) {
      CraftTweakerAPI.apply(new IAction() {
         public void apply() {
            HarvestBlockHandler.addGrassDrop(CraftTweakerPlugin.toStack(stack));
         }

         public String describe() {
            return "Adding Knife grass drop for " + stack.getDisplayName();
         }
      });
   }

   @ZenMethod
   public static void addWoodChoppingRecipe(final IItemStack inputStack, final IItemStack outputStack) {
      CraftTweakerAPI.apply(new IAction() {
         public void apply() {
            WoodRecipeHandler.registerWoodRecipe(CraftTweakerPlugin.toStack(inputStack), CraftTweakerPlugin.toStack(outputStack));
         }

         public String describe() {
            return "Adding Wood Chopping Recipe for " + inputStack.getDisplayName();
         }
      });
   }

   @Nonnull
   static ItemStack toStack(IIngredient ingredient) {
      if (!(ingredient instanceof IItemStack)) {
         throw new IllegalArgumentException("Must be an IItemStack!");
      } else {
         Object obj = ingredient.getInternal();
         return obj instanceof ItemStack ? (ItemStack)obj : ItemStack.EMPTY;
      }
   }
}
