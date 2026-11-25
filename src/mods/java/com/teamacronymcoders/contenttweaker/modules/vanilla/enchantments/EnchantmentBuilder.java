package com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("mods.contenttweaker.enchantments.EnchantmentBuilder")
@ZenRegister
public class EnchantmentBuilder {
   private Rarity rarity = Rarity.COMMON;
   private EnumEnchantmentType type = EnumEnchantmentType.ALL;
   @ZenProperty
   public String name;
   @ZenProperty
   public IEntityEquipmentSlot[] applicableSlots = new IEntityEquipmentSlot[0];
   @ZenProperty
   public boolean treasure = false;
   @ZenProperty
   public boolean curse = false;
   @ZenProperty
   public boolean allowedOnBooks = true;
   @ZenProperty
   public int minLevel = 1;
   @ZenProperty
   public int maxLevel = 1;
   @ZenProperty
   public EnchantmentFunctions.CalcCanApply canApply = null;
   @ZenProperty
   public EnchantmentFunctions.CalcCanApply canApplyAtEnchantmentTable = null;
   @ZenProperty
   public EnchantmentFunctions.CalcCanApplyTogether canApplyTogether = null;
   @ZenProperty
   public EnchantmentFunctions.CalcDamageByCreature calcDamageByCreature = null;
   @ZenProperty
   public EnchantmentFunctions.CalcEnchantability calcEnchantabilityMin = null;
   @ZenProperty
   public EnchantmentFunctions.CalcEnchantability calcEnchantabilityMax = null;
   @ZenProperty
   public EnchantmentFunctions.CalcModifierDamage calcModifierDamage = null;
   @ZenProperty
   public EnchantmentFunctions.CalcTranslatedName calcTranslatedName = null;
   @ZenProperty
   public EnchantmentFunctions.OnEntityDamaged onEntityDamaged = null;
   @ZenProperty
   public EnchantmentFunctions.OnUserHurt onUserHurt = null;
   @ZenProperty
   public String domain = "contenttweaker";

   public EnchantmentBuilder(String name) {
      this.name = name;
   }

   @ZenMethod
   public static EnchantmentBuilder create(String name) {
      return new EnchantmentBuilder(name);
   }

   @ZenMethod
   public void setRarityCommon() {
      this.rarity = Rarity.COMMON;
   }

   @ZenMethod
   public void setRarityUncommon() {
      this.rarity = Rarity.UNCOMMON;
   }

   @ZenMethod
   public void setRarityRare() {
      this.rarity = Rarity.RARE;
   }

   @ZenMethod
   public void setRarityVeryRare() {
      this.rarity = Rarity.VERY_RARE;
   }

   @ZenSetter("rarity")
   public void setRarity(String rarity) {
      this.rarity = Rarity.valueOf(rarity);
   }

   @ZenGetter("rarity")
   public String getRarity() {
      return this.rarity.name();
   }

   @ZenMethod
   public void setTypeAll() {
      this.type = EnumEnchantmentType.ALL;
   }

   @ZenMethod
   public void setTypeArmor() {
      this.type = EnumEnchantmentType.ARMOR;
   }

   @ZenMethod
   public void setTypeFeed() {
      this.type = EnumEnchantmentType.ARMOR_FEET;
   }

   @ZenMethod
   public void setTypeLegs() {
      this.type = EnumEnchantmentType.ARMOR_LEGS;
   }

   @ZenMethod
   public void setTypeChest() {
      this.type = EnumEnchantmentType.ARMOR_CHEST;
   }

   @ZenMethod
   public void setTypeHead() {
      this.type = EnumEnchantmentType.ARMOR_HEAD;
   }

   @ZenMethod
   public void setTypeWeapon() {
      this.type = EnumEnchantmentType.WEAPON;
   }

   @ZenMethod
   public void setTypeDigger() {
      this.type = EnumEnchantmentType.DIGGER;
   }

   @ZenMethod
   public void setTypeFishingRod() {
      this.type = EnumEnchantmentType.FISHING_ROD;
   }

   @ZenMethod
   public void setTypeBreakable() {
      this.type = EnumEnchantmentType.BREAKABLE;
   }

   @ZenMethod
   public void setTypeBow() {
      this.type = EnumEnchantmentType.BOW;
   }

   @ZenMethod
   public void setTypeWearable() {
      this.type = EnumEnchantmentType.WEARABLE;
   }

   @ZenGetter("type")
   public String getType() {
      return this.type.name();
   }

   @ZenSetter("type")
   public void setType(String type) {
      this.type = EnumEnchantmentType.valueOf(type);
   }

   @ZenMethod
   public IEnchantmentDefinition register() {
      CoTEnchantment enchantment = new CoTEnchantment(this.rarity, this.type, this.applicableSlots);
      enchantment.minLevel = this.minLevel;
      enchantment.maxLevel = this.maxLevel;
      enchantment.allowedOnBooks = this.allowedOnBooks;
      enchantment.curse = this.curse;
      enchantment.treasure = this.treasure;
      enchantment.apply = this.canApply;
      enchantment.applyAtEnchTable = this.canApplyAtEnchantmentTable;
      enchantment.canApplyTogether = this.canApplyTogether;
      enchantment.damageByCreature = this.calcDamageByCreature;
      enchantment.enchantabilityMin = this.calcEnchantabilityMin;
      enchantment.enchantabilityMax = this.calcEnchantabilityMax;
      enchantment.modifierDamage = this.calcModifierDamage;
      enchantment.translatedName = this.calcTranslatedName;
      enchantment.onEntityDamaged = this.onEntityDamaged;
      enchantment.onUserHurt = this.onUserHurt;
      if (this.name == null) {
         CraftTweakerAPI.logError("Trying to register an Enchantment with null name! Aborting...");
      } else {
         enchantment.setName(this.name);
         enchantment.setRegistryName(this.domain, this.name);
         ForgeRegistries.ENCHANTMENTS.register(enchantment);
      }

      return enchantment.thisDefinition;
   }
}
