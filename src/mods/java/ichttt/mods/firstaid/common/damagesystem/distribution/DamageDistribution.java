package ichttt.mods.firstaid.common.damagesystem.distribution;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import ichttt.mods.firstaid.common.EventHandler;
import ichttt.mods.firstaid.common.damagesystem.PlayerDamageModel;
import ichttt.mods.firstaid.common.network.MessageUpdatePart;
import ichttt.mods.firstaid.common.util.ArmorUtils;
import ichttt.mods.firstaid.common.util.CommonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;

public abstract class DamageDistribution implements IDamageDistribution {
   public DamageDistribution() {
   }

   public static float handleDamageTaken(IDamageDistribution damageDistribution, AbstractPlayerDamageModel damageModel, float damage, @Nonnull EntityPlayer player, @Nonnull DamageSource source, boolean addStat, boolean redistributeIfLeft) {
      if (FirstAidConfig.debug) {
         FirstAid.LOGGER.info("--- Damaging {} using {} for dmg source {}, redistribute {}, addStat {} ---", Float.valueOf(damage), damageDistribution.toString(), source.damageType, Boolean.valueOf(redistributeIfLeft), Boolean.valueOf(addStat));
      }

      NBTTagCompound beforeCache = (NBTTagCompound)damageModel.serializeNBT();
      if (!damageDistribution.skipGlobalPotionModifiers()) {
         damage = ArmorUtils.applyGlobalPotionModifiers(player, source, damage);
      }

      if (damage != 0.0F) {
         player.addExhaustion(source.getHungerDamage());
         float currentHealth = player.getHealth();
         player.getCombatTracker().trackDamage(source, currentHealth, damage);
      }

      float left = damageDistribution.distributeDamage(damage, player, source, addStat);
      if (left > 0.0F && redistributeIfLeft) {
         boolean hasTriedNoKill = damageDistribution == RandomDamageDistribution.NEAREST_NOKILL || damageDistribution == RandomDamageDistribution.ANY_NOKILL;
         IDamageDistribution var10 = hasTriedNoKill ? RandomDamageDistribution.NEAREST_KILL : RandomDamageDistribution.getDefault();
         left = var10.distributeDamage(left, player, source, addStat);
         if (left > 0.0F && !hasTriedNoKill) {
            left = RandomDamageDistribution.NEAREST_KILL.distributeDamage(left, player, source, addStat);
         }
      }

      PlayerDamageModel before = PlayerDamageModel.create();
      before.deserializeNBT(beforeCache);
      if (MinecraftForge.EVENT_BUS.post(new FirstAidLivingDamageEvent(player, damageModel, before, source, left))) {
         damageModel.deserializeNBT(beforeCache);
         if (FirstAidConfig.debug) {
            FirstAid.LOGGER.info("--- DONE! Event got canceled ---");
         }

         return 0.0F;
      } else {
         if (damageModel.isDead(player)) {
            CommonUtils.killPlayer(damageModel, player, source);
         }

         if (FirstAidConfig.debug) {
            FirstAid.LOGGER.info("--- DONE! {} still left ---", Float.valueOf(left));
         }

         return left;
      }
   }

   protected float minHealth(@Nonnull EntityPlayer player, @Nonnull AbstractDamageablePart part) {
      return 0.0F;
   }

   protected float distributeDamageOnParts(float damage, @Nonnull AbstractPlayerDamageModel damageModel, @Nonnull EnumPlayerPart[] enumParts, @Nonnull EntityPlayer player, boolean addStat) {
      ArrayList<AbstractDamageablePart> damageableParts = new ArrayList(enumParts.length);

      for(EnumPlayerPart part : enumParts) {
         damageableParts.add(damageModel.getFromEnum(part));
      }

      Collections.shuffle(damageableParts);

      for(AbstractDamageablePart part : damageableParts) {
         float minHealth = this.minHealth(player, part);
         float dmgDone = damage - part.damage(damage, player, !player.isPotionActive(EventHandler.MORPHINE), minHealth);
         FirstAid.NETWORKING.sendTo(new MessageUpdatePart(part), (EntityPlayerMP)player);
         if (addStat) {
            player.addStat(StatList.DAMAGE_TAKEN, Math.round(dmgDone * 10.0F));
         }

         damage -= dmgDone;
         if (damage == 0.0F) {
            break;
         }

         if (damage < 0.0F) {
            FirstAid.LOGGER.error("Got negative damage {} left? Logic error? ", Float.valueOf(damage));
            break;
         }
      }

      return damage;
   }

   @Nonnull
   protected abstract List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> getPartList();

   public float distributeDamage(float damage, @Nonnull EntityPlayer player, @Nonnull DamageSource source, boolean addStat) {
      if (damage <= 0.0F) {
         return 0.0F;
      } else {
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         if (FirstAidConfig.debug) {
            FirstAid.LOGGER.info("Starting distribution of {} damage...", Float.valueOf(damage));
         }

         Iterator var6 = this.getPartList().iterator();

         while(true) {
            if (var6.hasNext()) {
               Pair<EntityEquipmentSlot, EnumPlayerPart[]> pair = (Pair)var6.next();
               EntityEquipmentSlot slot = (EntityEquipmentSlot)pair.getLeft();
               EnumPlayerPart[] parts = (EnumPlayerPart[])pair.getRight();
               if (!Arrays.stream(parts).map(damageModel::getFromEnum).anyMatch((part) -> {
                  return part.currentHealth > this.minHealth(player, part);
               })) {
                  if (FirstAidConfig.debug) {
                     FirstAid.LOGGER.info("Skipping {}, no health > min in parts!", slot);
                  }
                  continue;
               }

               float originalDamage = damage;
               damage = ArmorUtils.applyArmor(player, player.getItemStackFromSlot(slot), source, (double)damage, slot);
               if (damage <= 0.0F) {
                  return 0.0F;
               }

               damage = ArmorUtils.applyEnchantmentModifiers(player, slot, source, damage);
               if (damage <= 0.0F) {
                  return 0.0F;
               }

               damage = ForgeHooks.onLivingDamage(player, source, damage);
               if (damage <= 0.0F) {
                  return 0.0F;
               }

               float dmgAfterReduce = damage;
               damage = this.distributeDamageOnParts(damage, damageModel, parts, player, addStat);
               if (damage != 0.0F) {
                  float absorbFactor = originalDamage / dmgAfterReduce;
                  float damageDistributed = dmgAfterReduce - damage;
                  damage = originalDamage - damageDistributed * absorbFactor;
                  if (FirstAidConfig.debug) {
                     FirstAid.LOGGER.info("Distribution round: Not done yet, going to next round. Needed to distribute {} damage (reduced to {}) to {}, but only distributed {}. New damage to be distributed is {}, based on absorb factor {}", Float.valueOf(originalDamage), Float.valueOf(dmgAfterReduce), slot, Float.valueOf(damageDistributed), Float.valueOf(damage), Float.valueOf(absorbFactor));
                  }
                  continue;
               }
            }

            return damage;
         }
      }
   }
}
