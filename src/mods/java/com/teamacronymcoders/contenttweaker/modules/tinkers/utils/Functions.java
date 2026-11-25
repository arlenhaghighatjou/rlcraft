package com.teamacronymcoders.contenttweaker.modules.tinkers.utils;

import com.teamacronymcoders.contenttweaker.modules.tinkers.materials.TConMaterialRepresentation;
import com.teamacronymcoders.contenttweaker.modules.tinkers.traits.TConTraitRepresentation;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.data.IData;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.BlockBreakEvent;
import crafttweaker.api.event.BlockHarvestDropsEvent;
import crafttweaker.api.event.EntityLivingHurtEvent;
import crafttweaker.api.event.PlayerBreakSpeedEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;

public class Functions {
   public Functions() {
   }

   @ZenClass("mods.tconstruct.traits.AfterBlockBreak")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface AfterBlockBreak {
      void handle(TConTraitRepresentation var1, IItemStack var2, IWorld var3, IBlockState var4, IBlockPos var5, IEntityLivingBase var6, boolean var7);
   }

   @ZenClass("mods.tconstruct.traits.AfterHit")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface AfterHit {
      void handle(TConTraitRepresentation var1, IItemStack var2, IEntityLivingBase var3, IEntityLivingBase var4, float var5, boolean var6, boolean var7);
   }

   @ZenClass("mods.tconstruct.traits.BeforeBlockBreak")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface BeforeBlockBreak {
      void handle(TConTraitRepresentation var1, IItemStack var2, BlockBreakEvent var3);
   }

   @ZenClass("mods.tconstruct.traits.BlockHarvestDrops")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface BlockHarvestDrops {
      void handle(TConTraitRepresentation var1, IItemStack var2, BlockHarvestDropsEvent var3);
   }

   @ZenClass("mods.tconstruct.traits.CanApplyTogetherEnchantment")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface CanApplyTogetherEnchantment {
      boolean handle(TConTraitRepresentation var1, IEnchantmentDefinition var2);
   }

   @ZenClass("mods.tconstruct.traits.CanApplyTogetherTrait")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface CanApplyTogetherTrait {
      boolean handle(TConTraitRepresentation var1, String var2);
   }

   @ZenClass("mods.tconstruct.traits.Damage")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface Damage {
      float handle(TConTraitRepresentation var1, IItemStack var2, IEntityLivingBase var3, IEntityLivingBase var4, float var5, float var6, boolean var7);
   }

   @ZenClass("mods.tconstruct.traits.ExtraInfo")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface ExtraInfo {
      String[] handle(TConTraitRepresentation var1, IItemStack var2, IData var3);
   }

   @ZenClass("mods.tconstruct.traits.IsCriticalHit")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface IsCriticalHit {
      boolean handle(TConTraitRepresentation var1, IItemStack var2, IEntityLivingBase var3, IEntityLivingBase var4);
   }

   @ZenClass("mods.tconstruct.materials.ItemLocalizer")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface ItemLocalizer {
      String handle(TConMaterialRepresentation var1, String var2);
   }

   @ZenClass("mods.tconstruct.traits.KnockBack")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface KnockBack {
      float handle(TConTraitRepresentation var1, IItemStack var2, IEntityLivingBase var3, IEntityLivingBase var4, float var5, float var6, float var7, boolean var8);
   }

   @ZenClass("mods.tconstruct.traits.MiningSpeed")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface MiningSpeed {
      void handle(TConTraitRepresentation var1, IItemStack var2, PlayerBreakSpeedEvent var3);
   }

   @ZenClass("mods.tconstruct.traits.OnBlock")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface OnBlock {
      void handle(TConTraitRepresentation var1, IItemStack var2, IPlayer var3, EntityLivingHurtEvent var4);
   }

   @ZenClass("mods.tconstruct.traits.OnHit")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface OnHit {
      void handle(TConTraitRepresentation var1, IItemStack var2, IEntityLivingBase var3, IEntityLivingBase var4, float var5, boolean var6);
   }

   @ZenClass("mods.tconstruct.traits.OnPlayerHurt")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface OnPlayerHurt {
      void handle(TConTraitRepresentation var1, IItemStack var2, IPlayer var3, IEntityLivingBase var4, EntityLivingHurtEvent var5);
   }

   @ZenClass("mods.tconstruct.traits.OnToolDamage")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface OnToolDamage {
      int handle(TConTraitRepresentation var1, IItemStack var2, int var3, int var4, IEntityLivingBase var5);
   }

   @ZenClass("mods.tconstruct.traits.OnToolHeal")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface OnToolHeal {
      int handle(TConTraitRepresentation var1, IItemStack var2, int var3, int var4, IEntityLivingBase var5);
   }

   @ZenClass("mods.tconstruct.traits.OnToolRepair")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface OnToolRepair {
      void handle(TConTraitRepresentation var1, IItemStack var2, int var3);
   }

   @ZenClass("mods.tconstruct.traits.Update")
   @ZenRegister
   @ModOnly("tconstruct")
   public interface OnUpdate {
      void handle(TConTraitRepresentation var1, IItemStack var2, IWorld var3, IEntity var4, int var5, boolean var6);
   }
}
