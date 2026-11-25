package com.teamacronymcoders.contenttweaker.modules.tinkers.traits;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import java.util.Arrays;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.mantle.util.RecipeMatch.ItemCombination;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.contenttweaker.tconstruct.Trait")
@ZenRegister
@ModOnly("tconstruct")
public class TConTraitRepresentation {
   private final ITrait trait;

   public TConTraitRepresentation(ITrait trait) {
      this.trait = trait;
   }

   public static TConTraitRepresentation getFromString(String identifier) {
      ITrait trait = TinkerRegistry.getTrait(identifier);
      if (trait == null) {
         CraftTweakerAPI.logError("Cannot identify trait <ticontrait:" + identifier + ">");
         return null;
      } else {
         return new TConTraitRepresentation(trait);
      }
   }

   @ZenMethod
   public void addItem(IIngredient item, @Optional(valueLong = 1L) int amountNeeded, @Optional(valueLong = 1L) int amountMatched) {
      if (!(this.trait instanceof RecipeMatchRegistry)) {
         CraftTweakerAPI.logError("Cannot add item " + item.toCommandString() + " to trait " + this.toCommandString());
      } else {
         RecipeMatchRegistry trait = (RecipeMatchRegistry)this.trait;
         if (item instanceof IItemStack) {
            trait.addItem(CraftTweakerMC.getItemStack(item), amountNeeded, amountMatched);
         } else if (item instanceof IOreDictEntry) {
            trait.addItem(((IOreDictEntry)item).getName(), amountNeeded, amountMatched);
         } else {
            for(IItemStack itemStack : item.getItems()) {
               this.addItem(itemStack, amountNeeded, amountMatched);
            }
         }

      }
   }

   @ZenMethod
   public void addMultiItem(int amountMatched, IItemStack... items) {
      if (!(this.trait instanceof RecipeMatchRegistry)) {
         CraftTweakerAPI.logError("Cannot add items " + Arrays.toString(items) + " to trait " + this.toCommandString());
      } else {
         RecipeMatchRegistry recipeMatchRegistry = (RecipeMatchRegistry)this.trait;
         recipeMatchRegistry.addRecipeMatch(new ItemCombination(amountMatched, CraftTweakerMC.getItemStacks(items)));
      }
   }

   @ZenGetter("identifier")
   public String getIdentifier() {
      return this.trait.getIdentifier();
   }

   @ZenGetter("commandString")
   public String toCommandString() {
      return "<ticontrait:" + this.trait.getIdentifier() + ">";
   }

   @ZenMethod
   public TConTraitDataRepresentation getData(IItemStack itemStack) {
      if (this.trait instanceof ModifierTrait) {
         return new TConTraitDataRepresentation(((ModifierTrait)this.trait).getData(CraftTweakerMC.getItemStack(itemStack)));
      } else {
         CraftTweakerAPI.logError("Trait " + this.trait.getIdentifier() + " is not applicable to the getData function!");
         return null;
      }
   }

   public ITrait getTrait() {
      return this.trait;
   }
}
