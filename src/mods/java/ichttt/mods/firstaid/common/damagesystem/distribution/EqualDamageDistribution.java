package ichttt.mods.firstaid.common.damagesystem.distribution;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.EventHandler;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import ichttt.mods.firstaid.common.util.ArmorUtils;
import ichttt.mods.firstaid.common.util.CommonUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EqualDamageDistribution implements IDamageDistribution {
   private static final Method applyPotionDamageCalculationsMethod = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "applyPotionDamageCalculations", Float.TYPE, new Class[]{DamageSource.class, Float.TYPE});
   private final boolean tryNoKill;
   private final float reductionMultiplier;

   public EqualDamageDistribution(boolean tryNoKill, float reductionMultiplier) {
      this.tryNoKill = tryNoKill;
      this.reductionMultiplier = reductionMultiplier;
   }

   private float reduceDamage(float originalDamage, EntityPlayer player, DamageSource source) {
      float damage = originalDamage;

      for(EntityEquipmentSlot slot : CommonUtils.ARMOR_SLOTS) {
         ItemStack armor = player.getItemStackFromSlot(slot);
         damage = ArmorUtils.applyArmor(player, armor, source, (double)damage, slot);
         if (damage <= 0.0F) {
            return 0.0F;
         }
      }

      try {
         damage = ((Float)applyPotionDamageCalculationsMethod.invoke(player, source, damage)).floatValue();
      } catch (InvocationTargetException | IllegalAccessException var10) {
         FirstAid.LOGGER.error("Could not invoke applyPotionDamageCalculations!", var10);
      }

      if (damage <= 0.0F) {
         return 0.0F;
      } else {
         float reduction = originalDamage - damage;
         if (reduction > 0.0F) {
            reduction *= this.reductionMultiplier;
         }

         damage = originalDamage - reduction;
         if (damage <= 0.0F) {
            return 0.0F;
         } else {
            damage = ForgeHooks.onLivingDamage(player, source, damage);
            return damage;
         }
      }
   }

   private float distributeOnParts(float damage, AbstractPlayerDamageModel damageModel, EntityPlayer player, boolean tryNoKillThisRound) {
      int iterationCounter = 0;
      int divCount = EnumPlayerPart.VALUES.length;
      float damageLeft = damage;

      while(true) {
         float prevDamageLeft = damageLeft;
         float toDamage = damageLeft / (float)divCount;
         divCount = 0;
         damageLeft = 0.0F;

         for(AbstractDamageablePart part : damageModel) {
            if (part.currentHealth > 0.0F) {
               damageLeft += part.damage(toDamage, player, !player.isPotionActive(EventHandler.MORPHINE), tryNoKillThisRound ? 1.0F : 0.0F);
               ++divCount;
            }
         }

         if (iterationCounter >= 50) {
            FirstAid.LOGGER.warn("Not done distribution equally after 50 rounds, diff {}. Dropping!", Float.valueOf(Math.abs(prevDamageLeft - damageLeft)));
            break;
         }

         ++iterationCounter;
         if (prevDamageLeft == damageLeft) {
            break;
         }
      }

      return damageLeft;
   }

   public float distributeDamage(float damage, @Nonnull EntityPlayer player, @Nonnull DamageSource source, boolean addStat) {
      damage = this.reduceDamage(damage, player, source);
      if (damage <= 0.0F) {
         return 0.0F;
      } else {
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
         Objects.requireNonNull(damageModel);
         float damageLeft = this.distributeOnParts(damage, damageModel, player, this.tryNoKill);
         if (damageLeft > 0.0F && this.tryNoKill) {
            damageLeft = this.distributeOnParts(damage, damageModel, player, false);
         }

         FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel(damageModel, false), (EntityPlayerMP)player);
         float effectiveDmg = damage - damageLeft;
         if (effectiveDmg < 3.4028235E37F) {
            player.addStat(StatList.DAMAGE_TAKEN, Math.round(effectiveDmg * 10.0F));
         }

         return damageLeft;
      }
   }

   public boolean skipGlobalPotionModifiers() {
      return true;
   }
}
