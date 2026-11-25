package com.teamacronymcoders.contenttweaker.modules.tinkers.materials;

import com.teamacronymcoders.contenttweaker.modules.tinkers.traits.TConTraitRepresentation;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.contenttweaker.tconstruct.Material")
@ZenRegister
@ModOnly("tconstruct")
public class TConMaterialRepresentation {
   private final Material material;

   public TConMaterialRepresentation(Material material) {
      this.material = material;
   }

   public static TConMaterialRepresentation getFromString(String identifier) {
      Material material = TinkerRegistry.getMaterial(identifier);
      if (material != null && material != Material.UNKNOWN) {
         return new TConMaterialRepresentation(material);
      } else {
         CraftTweakerAPI.logError("Cannot identify material <ticonmaterial:" + identifier + ">");
         return null;
      }
   }

   @ZenMethod
   public void addItem(IIngredient item, @Optional(valueLong = 1L) int amountNeeded, @Optional(valueLong = 144L) int amountMatched) {
      if (item instanceof IItemStack) {
         this.material.addItem(CraftTweakerMC.getItemStack(item), amountNeeded, amountMatched);
      } else if (item instanceof IOreDictEntry) {
         this.material.addItem(((IOreDictEntry)item).getName(), amountNeeded, amountMatched);
      } else {
         for(IItemStack itemStack : item.getItems()) {
            this.addItem(itemStack, amountNeeded, amountMatched);
         }
      }

   }

   @ZenMethod
   public void addTrait(TConTraitRepresentation trait, @Optional String dependencies) {
      if (trait == null) {
         CraftTweakerAPI.logError("Trying to add null trait to material " + this.material.identifier);
      } else if (this.material instanceof CoTTConMaterial) {
         ((CoTTConMaterial)this.material).addTrait(trait.getTrait().getIdentifier(), dependencies);
      } else {
         CraftTweakerAPI.logError("Trying to add trait " + trait.getIdentifier() + " to nonCoT Material + " + this.material.identifier);
      }

   }

   @ZenMethod
   public void addTrait(String trait, @Optional String dependencies) {
      if (this.material instanceof CoTTConMaterial) {
         ((CoTTConMaterial)this.material).addTrait(trait, dependencies);
      } else {
         CraftTweakerAPI.logError("Trying to add trait " + trait + " to nonCoT Material + " + this.material.identifier);
      }

   }

   public Material getMaterial() {
      return this.material;
   }

   @ZenGetter("commandString")
   public String toCommandString() {
      return "<ticonmaterial:" + this.material.getIdentifier() + ">";
   }

   @ZenGetter("identifier")
   public String getIdentifier() {
      return this.material.identifier;
   }
}
