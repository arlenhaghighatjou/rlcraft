package com.teamacronymcoders.contenttweaker.modules.tinkers.traits;

import com.teamacronymcoders.contenttweaker.modules.tinkers.utils.Functions;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.enchantments.MCEnchantmentDefinition;
import crafttweaker.mc1120.events.handling.MCBlockBreakEvent;
import crafttweaker.mc1120.events.handling.MCBlockHarvestDropsEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingHurtEvent;
import crafttweaker.mc1120.events.handling.MCPlayerBreakSpeedEvent;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.traits.ITrait;

public class CoTTrait extends ModifierTrait implements ITrait {
   Functions.AfterBlockBreak afterBlockBreak = null;
   Functions.BeforeBlockBreak beforeBlockBreak = null;
   Functions.BlockHarvestDrops onBlockHarvestDrops = null;
   Functions.Damage calcDamage = null;
   Functions.IsCriticalHit calcCrit = null;
   Functions.MiningSpeed getMiningSpeed = null;
   Functions.OnHit onHit = null;
   Functions.OnUpdate onUpdate = null;
   Functions.AfterHit afterHit = null;
   Functions.KnockBack calcKnockBack = null;
   Functions.OnBlock onBlock = null;
   Functions.OnToolDamage onToolDamage = null;
   Functions.OnToolHeal calcToolHeal = null;
   Functions.OnToolRepair onToolRepair = null;
   Functions.OnPlayerHurt onPlayerHurt = null;
   Functions.CanApplyTogetherTrait canApplyTogetherTrait = null;
   Functions.CanApplyTogetherEnchantment canApplyTogetherEnchantment = null;
   Functions.ExtraInfo extraInfo = null;
   String localizedName = null;
   String localizedDescription = null;
   boolean hidden = false;
   private final TConTraitRepresentation thisTrait = new TConTraitRepresentation(this);

   public CoTTrait(@Nonnull String identifier, int color, int maxLevel, int countPerLevel) {
      super(identifier, color, maxLevel, countPerLevel);
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void onPlayerHurt(ItemStack tool, EntityPlayer player, EntityLivingBase attacker, LivingHurtEvent event) {
      if (this.onPlayerHurt != null) {
         this.onPlayerHurt.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIPlayer(player), CraftTweakerMC.getIEntityLivingBase(attacker), new MCEntityLivingHurtEvent(event));
      } else {
         super.onPlayerHurt(tool, player, attacker, event);
      }

   }

   public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (this.onUpdate != null) {
         this.onUpdate.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIWorld(world), CraftTweakerMC.getIEntity(entity), itemSlot, isSelected);
      } else {
         super.onUpdate(tool, world, entity, itemSlot, isSelected);
      }

   }

   public void miningSpeed(ItemStack tool, BreakSpeed event) {
      if (this.getMiningSpeed != null) {
         this.getMiningSpeed.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), new MCPlayerBreakSpeedEvent(event));
      } else {
         super.miningSpeed(tool, event);
      }

   }

   public void beforeBlockBreak(ItemStack tool, BreakEvent event) {
      if (this.beforeBlockBreak != null) {
         this.beforeBlockBreak.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), new MCBlockBreakEvent(event));
      } else {
         super.beforeBlockBreak(tool, event);
      }

   }

   public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
      if (this.afterBlockBreak != null) {
         this.afterBlockBreak.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIWorld(world), CraftTweakerMC.getBlockState(state), CraftTweakerMC.getIBlockPos(pos), CraftTweakerMC.getIEntityLivingBase(player), wasEffective);
      } else {
         super.afterBlockBreak(tool, world, state, pos, player, wasEffective);
      }

   }

   public void blockHarvestDrops(ItemStack tool, HarvestDropsEvent event) {
      if (this.onBlockHarvestDrops != null) {
         this.onBlockHarvestDrops.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), new MCBlockHarvestDropsEvent(event));
      } else {
         super.blockHarvestDrops(tool, event);
      }

   }

   public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
      return this.calcCrit != null ? this.calcCrit.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIEntityLivingBase(player), CraftTweakerMC.getIEntityLivingBase(target)) : super.isCriticalHit(tool, player, target);
   }

   public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
      return this.calcDamage != null ? this.calcDamage.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIEntityLivingBase(player), CraftTweakerMC.getIEntityLivingBase(target), damage, newDamage, isCritical) : super.damage(tool, player, target, damage, newDamage, isCritical);
   }

   public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
      if (this.onHit != null) {
         this.onHit.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIEntityLivingBase(player), CraftTweakerMC.getIEntityLivingBase(target), damage, isCritical);
      } else {
         super.onHit(tool, player, target, damage, isCritical);
      }

   }

   public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
      if (this.afterHit != null) {
         this.afterHit.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIEntityLivingBase(player), CraftTweakerMC.getIEntityLivingBase(target), damageDealt, wasCritical, wasHit);
      } else {
         super.afterHit(tool, player, target, damageDealt, wasCritical, wasHit);
      }

   }

   public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
      return this.calcKnockBack != null ? this.calcKnockBack.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIEntityLivingBase(player), CraftTweakerMC.getIEntityLivingBase(target), damage, knockback, newKnockback, isCritical) : super.knockBack(tool, player, target, damage, knockback, newKnockback, isCritical);
   }

   public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
      if (this.onBlock != null) {
         this.onBlock.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), CraftTweakerMC.getIPlayer(player), new MCEntityLivingHurtEvent(event));
      } else {
         super.onBlock(tool, player, event);
      }

   }

   public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
      return this.onToolDamage != null ? this.onToolDamage.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), damage, newDamage, CraftTweakerMC.getIEntityLivingBase(entity)) : super.onToolDamage(tool, damage, newDamage, entity);
   }

   public int onToolHeal(ItemStack tool, int amount, int newAmount, EntityLivingBase entity) {
      return this.calcToolHeal != null ? this.calcToolHeal.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), amount, newAmount, CraftTweakerMC.getIEntityLivingBase(entity)) : super.onToolHeal(tool, amount, newAmount, entity);
   }

   public void onRepair(ItemStack tool, int amount) {
      if (this.onToolRepair != null) {
         this.onToolRepair.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), amount);
      } else {
         super.onRepair(tool, amount);
      }

   }

   public boolean canApplyTogether(IToolMod otherModifier) {
      return this.canApplyTogetherTrait != null ? this.canApplyTogetherTrait.handle(this.thisTrait, otherModifier.getIdentifier()) : super.canApplyTogether(otherModifier);
   }

   public boolean canApplyTogether(Enchantment enchantment) {
      return this.canApplyTogetherEnchantment != null ? this.canApplyTogetherEnchantment.handle(this.thisTrait, new MCEnchantmentDefinition(enchantment)) : super.canApplyTogether(enchantment);
   }

   public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
      return this.extraInfo != null ? Arrays.asList(this.extraInfo.handle(this.thisTrait, CraftTweakerMC.getIItemStack(tool), NBTConverter.from(modifierTag, true))) : super.getExtraInfo(tool, modifierTag);
   }

   public String getLocalizedName() {
      return this.localizedName != null ? this.localizedName : super.getLocalizedName();
   }

   public String getLocalizedDesc() {
      return this.localizedDescription != null ? this.localizedDescription : super.getLocalizedDesc();
   }

   public void addItem(RecipeMatch recipeMatch) {
      this.items.add(recipeMatch);
   }
}
