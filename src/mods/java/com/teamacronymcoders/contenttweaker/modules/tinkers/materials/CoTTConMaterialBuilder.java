package com.teamacronymcoders.contenttweaker.modules.tinkers.materials;

import com.teamacronymcoders.contenttweaker.modules.tinkers.traits.CoTTraitBuilder;
import com.teamacronymcoders.contenttweaker.modules.tinkers.traits.TConTraitRepresentation;
import com.teamacronymcoders.contenttweaker.modules.tinkers.utils.CoTRecipeMatch;
import com.teamacronymcoders.contenttweaker.modules.tinkers.utils.Functions;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.ProjectileMaterialStats;
import slimeknights.tconstruct.library.traits.ITrait;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;
import stanhebben.zenscript.util.Pair;

@ZenClass("mods.contenttweaker.tconstruct.MaterialBuilder")
@ZenRegister
@ModOnly("tconstruct")
public class CoTTConMaterialBuilder {
   public static List<CoTTConMaterial> addedMaterials = new ArrayList();
   @ZenProperty
   public String identifier;
   @ZenProperty
   public int color = 16777215;
   @ZenProperty
   public boolean hidden = false;
   @ZenProperty
   public ILiquidStack liquid = null;
   @ZenProperty
   public boolean craftable = false;
   @ZenProperty
   public boolean castable = false;
   @ZenProperty
   public IItemStack representativeItem = null;
   @ZenProperty
   public IOreDictEntry representativeOre = null;
   @ZenProperty
   public IItemStack shard = null;
   @ZenProperty
   public String localizedName = null;
   @ZenProperty
   public Functions.ItemLocalizer itemLocalizer = null;
   private List<CoTRecipeMatch> itemMatches = new ArrayList();
   private List<Pair<String, String>> materialTraits = new ArrayList();
   private HeadMaterialStats headMaterialStats = null;
   private HandleMaterialStats handleMaterialStats = null;
   private ExtraMaterialStats extraMaterialStats = null;
   private BowMaterialStats bowMaterialStats = null;
   private BowStringMaterialStats bowStringMaterialStats = null;
   private ArrowShaftMaterialStats arrowShaftMaterialStats = null;
   private FletchingMaterialStats fletchingMaterialStats = null;
   private ProjectileMaterialStats projectileMaterialStats = null;

   public CoTTConMaterialBuilder(String identifier) {
      this.identifier = identifier;
   }

   @ZenMethod
   public static CoTTConMaterialBuilder create(String identifier) {
      return new CoTTConMaterialBuilder(identifier);
   }

   @ZenMethod
   public void addItem(IIngredient ingredient, @Optional(valueLong = 1L) int amountNeeded, @Optional(valueLong = 144L) int amountMatched) {
      this.itemMatches.add(new CoTRecipeMatch(ingredient, amountMatched, amountNeeded));
   }

   @ZenMethod
   public void removeItem(IItemStack itemStack) {
      this.itemMatches.removeIf((coTRecipeMatch) -> {
         return coTRecipeMatch.matches(itemStack);
      });
   }

   @ZenMethod
   public void addMaterialTrait(ITrait trait, @Optional String dependency) {
      this.addMaterialTrait(trait.getIdentifier(), dependency);
   }

   @ZenMethod
   public void addMaterialTrait(TConTraitRepresentation trait, @Optional String dependency) {
      this.addMaterialTrait(trait.getTrait(), dependency);
   }

   @ZenMethod
   public void addMaterialTrait(CoTTraitBuilder traitBuilder, @Optional String dependency) {
      this.addMaterialTrait(traitBuilder.identifier, dependency);
   }

   @ZenMethod
   public void addMaterialTrait(String identifier, @Optional String dependency) {
      this.materialTraits.add(new Pair(identifier, dependency));
   }

   @ZenMethod
   public void removeMaterialTrait(String identifier, @Optional String dependency) {
      this.materialTraits.removeIf((traitPair) -> {
         return identifier.equals(traitPair.getKey()) && (dependency == null || dependency.equals(traitPair.getValue()));
      });
   }

   @ZenMethod
   public void addHeadMaterialStats(int durability, float miningSpeed, float attackDamage, int harvestLevel) {
      this.headMaterialStats = new HeadMaterialStats(durability, miningSpeed, attackDamage, harvestLevel);
   }

   @ZenMethod
   public void removeHeadMaterialStats() {
      this.headMaterialStats = null;
   }

   @ZenMethod
   public void addHandleMaterialStats(float modifier, int durability) {
      this.handleMaterialStats = new HandleMaterialStats(modifier, durability);
   }

   @ZenMethod
   public void removeHandleMaterialStats() {
      this.handleMaterialStats = null;
   }

   @ZenMethod
   public void addExtraMaterialStats(int extraDurability) {
      this.extraMaterialStats = new ExtraMaterialStats(extraDurability);
   }

   @ZenMethod
   public void removeExtraMaterialStats() {
      this.extraMaterialStats = null;
   }

   @ZenMethod
   public void addBowMaterialStats(float drawSpeed, float range, float bonusDamage) {
      this.bowMaterialStats = new BowMaterialStats(drawSpeed, range, bonusDamage);
   }

   @ZenMethod
   public void removeBowMaterialStats() {
      this.bowMaterialStats = null;
   }

   @ZenMethod
   public void addBowStringMaterialStats(float modifier) {
      this.bowStringMaterialStats = new BowStringMaterialStats(modifier);
   }

   @ZenMethod
   public void removeBowStringMaterialStats() {
      this.bowStringMaterialStats = null;
   }

   @ZenMethod
   public void addArrowShaftMaterialStats(float modifier, int bonusAmmo) {
      this.arrowShaftMaterialStats = new ArrowShaftMaterialStats(modifier, bonusAmmo);
   }

   @ZenMethod
   public void removeArrowShaftMaterialStats() {
      this.arrowShaftMaterialStats = null;
   }

   @ZenMethod
   public void addFletchingMaterialStats(float accuracy, float modifier) {
      this.fletchingMaterialStats = new FletchingMaterialStats(accuracy, modifier);
   }

   @ZenMethod
   public void removeFletchingMaterialStats() {
      this.fletchingMaterialStats = null;
   }

   @ZenMethod
   public void addProjectileMaterialStats() {
      this.projectileMaterialStats = new ProjectileMaterialStats();
   }

   @ZenMethod
   public void removeProjectileMaterialStats() {
      this.projectileMaterialStats = null;
   }

   @ZenMethod
   public TConMaterialRepresentation register() {
      CoTTConMaterial material = new CoTTConMaterial(this.identifier, this.color, this.materialTraits);
      addedMaterials.add(material);
      if (this.headMaterialStats != null) {
         material.addStats(this.headMaterialStats);
      }

      if (this.handleMaterialStats != null) {
         material.addStats(this.handleMaterialStats);
      }

      if (this.extraMaterialStats != null) {
         material.addStats(this.extraMaterialStats);
      }

      if (this.bowMaterialStats != null) {
         material.addStats(this.bowMaterialStats);
      }

      if (this.bowStringMaterialStats != null) {
         material.addStats(this.bowStringMaterialStats);
      }

      if (this.arrowShaftMaterialStats != null) {
         material.addStats(this.arrowShaftMaterialStats);
      }

      if (this.fletchingMaterialStats != null) {
         material.addStats(this.fletchingMaterialStats);
      }

      if (this.projectileMaterialStats != null) {
         material.addStats(this.projectileMaterialStats);
      }

      if (this.liquid != null) {
         material.liquid = this.liquid;
      }

      material.setCastable(this.castable);
      material.setCraftable(this.craftable);
      material.hidden = this.hidden;
      this.itemMatches.forEach(material::addItemMatch);
      if (this.representativeItem != null) {
         material.representativeItem = this.representativeItem;
      }

      if (this.representativeOre != null) {
         material.setRepresentativeItem(this.representativeOre.getName());
      }

      if (this.shard != null) {
         material.setShard(CraftTweakerMC.getItemStack(this.shard));
      }

      material.itemLocalizer = this.itemLocalizer;
      material.localizedName = this.localizedName;
      TinkerRegistry.addMaterial(material);
      TinkerRegistry.integrate(new CoTTConMaterialIntegration(material));
      return material.thisMaterial;
   }
}
