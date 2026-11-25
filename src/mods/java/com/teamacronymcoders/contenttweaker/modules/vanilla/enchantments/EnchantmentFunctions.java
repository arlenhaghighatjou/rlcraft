package com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;

public final class EnchantmentFunctions {
   public EnchantmentFunctions() {
   }

   @ZenRegister
   @ZenClass("mods.contenttweaker.enchantments.CalcCanApply")
   public interface CalcCanApply {
      boolean handle(IEnchantmentDefinition var1, IItemStack var2);
   }

   @ZenRegister
   @ZenClass("mods.contenttweaker.enchantments.CalcCanApplyTogether")
   public interface CalcCanApplyTogether {
      boolean handle(IEnchantmentDefinition var1, IEnchantmentDefinition var2);
   }

   @ZenRegister
   @ZenClass("mods.contenttweaker.enchantments.CalcDamageByCreature")
   public interface CalcDamageByCreature {
      float handle(IEnchantmentDefinition var1, int var2, String var3);
   }

   @ZenRegister
   @ZenClass("mods.contenttweaker.enchantments.CalcEnchantability")
   public interface CalcEnchantability {
      int handle(IEnchantmentDefinition var1, int var2);
   }

   @ZenRegister
   @ZenClass("mods.contenttweaker.enchantments.CalcModifierDamage")
   public interface CalcModifierDamage {
      int handle(IEnchantmentDefinition var1, int var2, IDamageSource var3);
   }

   @ZenRegister
   @ZenClass("mods.contenttweaker.enchantments.CalcTranslatedName")
   public interface CalcTranslatedName {
      String handle(IEnchantmentDefinition var1, int var2);
   }

   @ZenRegister
   @ZenClass("mods.contenttweaker.enchantments.OnEntityDamaged")
   public interface OnEntityDamaged {
      void handle(IEnchantmentDefinition var1, IEntityLivingBase var2, IEntity var3, int var4);
   }

   @ZenRegister
   @ZenClass("mods.contenttweaker.enchantments.OnUserHurt")
   public interface OnUserHurt {
      void handle(IEnchantmentDefinition var1, IEntityLivingBase var2, IEntity var3, int var4);
   }
}
