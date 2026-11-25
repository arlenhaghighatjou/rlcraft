package com.teamacronymcoders.contenttweaker.modules.tinkers.materials;

import com.teamacronymcoders.contenttweaker.modules.tinkers.utils.Functions;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.liquid.MCLiquidStack;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;
import stanhebben.zenscript.util.Pair;

public class CoTTConMaterial extends Material {
   public boolean hidden = false;
   public IItemStack representativeItem = null;
   public IItemStack shard = null;
   public ILiquidStack liquid = null;
   public String localizedName = null;
   public Functions.ItemLocalizer itemLocalizer = null;
   final TConMaterialRepresentation thisMaterial = new TConMaterialRepresentation(this);
   private final List<Pair<String, String>> traits;

   public CoTTConMaterial(String identifier, int color, List<Pair<String, String>> traits) {
      super(identifier, color);
      this.traits = traits;
   }

   public void addItemMatch(RecipeMatch recipeMatch) {
      this.items.add(recipeMatch);
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public ItemStack getRepresentativeItem() {
      return CraftTweakerMC.getItemStack(this.representativeItem);
   }

   public void setRepresentativeItem(ItemStack representativeItem) {
      this.representativeItem = CraftTweakerMC.getIItemStack(representativeItem);
   }

   public void setShard(@Nonnull ItemStack stack) {
      this.shard = CraftTweakerMC.getIItemStack(stack);
   }

   public ItemStack getShard() {
      return CraftTweakerMC.getItemStack(this.shard);
   }

   public boolean hasFluid() {
      return this.liquid != null;
   }

   public CoTTConMaterial setFluid(Fluid liquid) {
      this.liquid = new MCLiquidStack(new FluidStack(liquid, 1));
      return this;
   }

   public String getLocalizedName() {
      return this.localizedName != null ? this.localizedName : super.getLocalizedName();
   }

   public String getLocalizedItemName(String itemName) {
      return this.itemLocalizer != null ? this.itemLocalizer.handle(this.thisMaterial, itemName) : super.getLocalizedItemName(itemName);
   }

   public Fluid getFluid() {
      return this.liquid == null ? null : CraftTweakerMC.getFluid(this.liquid.getDefinition());
   }

   public void registerTraits() {
      for(Pair<String, String> traitPair : this.traits) {
         String traitName = (String)traitPair.getKey();
         ITrait trait = TinkerRegistry.getTrait(traitName);
         if (trait != null) {
            this.addTrait(trait, (String)traitPair.getValue());
         } else {
            CraftTweakerAPI.logError("Could not identify Trait <ticontrait:" + traitName + ">, it will not be added to material " + this.getIdentifier());
         }
      }

   }

   void addTrait(String materialTrait, String dependency) {
      this.traits.add(new Pair(materialTrait, dependency));
   }
}
