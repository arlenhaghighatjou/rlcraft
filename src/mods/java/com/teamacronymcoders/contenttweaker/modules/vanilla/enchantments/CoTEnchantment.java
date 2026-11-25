package com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments;

import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.enchantments.MCEnchantmentDefinition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class CoTEnchantment extends Enchantment {
   public int minLevel = 0;
   public int maxLevel = 0;
   public boolean curse = false;
   public boolean treasure = false;
   public boolean allowedOnBooks = true;
   public EnchantmentFunctions.CalcCanApply apply = null;
   public EnchantmentFunctions.CalcCanApply applyAtEnchTable = null;
   public EnchantmentFunctions.CalcCanApplyTogether canApplyTogether = null;
   public EnchantmentFunctions.CalcDamageByCreature damageByCreature = null;
   public EnchantmentFunctions.CalcEnchantability enchantabilityMin = null;
   public EnchantmentFunctions.CalcEnchantability enchantabilityMax = null;
   public EnchantmentFunctions.CalcModifierDamage modifierDamage = null;
   public EnchantmentFunctions.CalcTranslatedName translatedName = null;
   public EnchantmentFunctions.OnEntityDamaged onEntityDamaged = null;
   public EnchantmentFunctions.OnUserHurt onUserHurt = null;
   public final IEnchantmentDefinition thisDefinition = new MCEnchantmentDefinition(this);

   private static EntityEquipmentSlot[] calcSlotArray(IEntityEquipmentSlot[] array) {
      EntityEquipmentSlot[] out = new EntityEquipmentSlot[array.length];

      for(int indec = 0; indec < array.length; ++indec) {
         out[indec] = CraftTweakerMC.getEntityEquipmentSlot(array[indec]);
      }

      return out;
   }

   protected CoTEnchantment(Rarity rarity, EnumEnchantmentType type, IEntityEquipmentSlot[] applicableEquipmentTypes) {
      super(rarity, type, calcSlotArray(applicableEquipmentTypes));
   }

   public int getMinLevel() {
      return this.minLevel;
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return this.enchantabilityMin != null ? this.enchantabilityMin.handle(this.thisDefinition, enchantmentLevel) : super.getMinEnchantability(enchantmentLevel);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.enchantabilityMax != null ? this.enchantabilityMax.handle(this.thisDefinition, enchantmentLevel) : super.getMaxEnchantability(enchantmentLevel);
   }

   public int calcModifierDamage(int level, DamageSource source) {
      return this.modifierDamage != null ? this.modifierDamage.handle(this.thisDefinition, level, CraftTweakerMC.getIDamageSource(source)) : super.calcModifierDamage(level, source);
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return this.damageByCreature != null ? this.damageByCreature.handle(this.thisDefinition, level, creatureType.name()) : super.calcDamageByCreature(level, creatureType);
   }

   public String getTranslatedName(int level) {
      return this.translatedName != null ? this.translatedName.handle(this.thisDefinition, level) : super.getTranslatedName(level);
   }

   public boolean canApply(ItemStack stack) {
      return this.apply != null ? this.apply.handle(this.thisDefinition, CraftTweakerMC.getIItemStack(stack)) : super.canApplyAtEnchantingTable(stack);
   }

   public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
      if (this.onEntityDamaged != null) {
         this.onEntityDamaged.handle(this.thisDefinition, CraftTweakerMC.getIEntityLivingBase(user), CraftTweakerMC.getIEntity(target), level);
      } else {
         super.onEntityDamaged(user, target, level);
      }

   }

   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
      if (this.onUserHurt != null) {
         this.onUserHurt.handle(this.thisDefinition, CraftTweakerMC.getIEntityLivingBase(user), CraftTweakerMC.getIEntity(attacker), level);
      } else {
         super.onUserHurt(user, attacker, level);
      }

   }

   public boolean isTreasureEnchantment() {
      return this.treasure;
   }

   public boolean isCurse() {
      return this.curse;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return this.applyAtEnchTable != null ? this.applyAtEnchTable.handle(this.thisDefinition, CraftTweakerMC.getIItemStack(stack)) : super.canApplyAtEnchantingTable(stack);
   }

   public boolean isAllowedOnBooks() {
      return this.allowedOnBooks;
   }

   protected boolean canApplyTogether(Enchantment ench) {
      return this.canApplyTogether != null ? this.canApplyTogether.handle(this.thisDefinition, new MCEnchantmentDefinition(ench)) : super.canApplyTogether(ench);
   }
}
