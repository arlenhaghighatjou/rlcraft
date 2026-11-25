package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.entity.attribute.ExtendedRangedAttribute;
import com.charles445.rltweaker.entity.attribute.InversedAttributeInstance;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import meldexun.reflectionutil.ReflectionField;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class HookPotionCore {
   private static final ResourceLocation POTION_SICKNESS = new ResourceLocation("potioncore", "potion_sickness");
   private static final ResourceLocation TIPSY = new ResourceLocation("rustic", "tipsy");
   public static final ReflectionField<IAttribute> DAMAGE_RESISTANCE = new ReflectionField<IAttribute>("com.tmtravlr.potioncore.PotionCoreAttributes", "DAMAGE_RESISTANCE", "DAMAGE_RESISTANCE");
   private static float originalDamage;

   public HookPotionCore() {
   }

   public static void addPotionSicknessEffect(EntityLivingBase entity, List<Potion> possibleEffects) {
      Potion potion = (Potion)possibleEffects.get(entity.getRNG().nextInt(possibleEffects.size()));
      PotionEffect potionEffect = potion.isInstant() ? new PotionEffect(potion, 1) : new PotionEffect(potion, 1200);
      if (ModConfig.server.potioncore.incurablePotionSickness) {
         ((HookPotionCore.Incurable)potionEffect).setIncurable(true);
      }

      entity.addPotionEffect(potionEffect);
   }

   public static boolean isCurable(PotionEffect potionEffect) {
      ResourceLocation registryName = potionEffect.getPotion().getRegistryName();
      if (ModConfig.server.potioncore.incurablePotionSickness && registryName.equals(POTION_SICKNESS)) {
         return false;
      } else if (ModConfig.server.potioncore.incurableTipsy && registryName.equals(TIPSY)) {
         return false;
      } else if (((HookPotionCore.Incurable)potionEffect).isIncurable()) {
         return false;
      } else {
         return !ModConfig.server.potioncore.incurablePotionEffectsImpl.contains(registryName);
      }
   }

   public static boolean isCureDisabled(EntityLivingBase entity) {
      Stream var10000 = entity.getActivePotionEffects().stream().map(PotionEffect::getPotion).map(Impl::getRegistryName);
      Set var10001 = ModConfig.server.potioncore.cureDisablingPotionEffectsImpl;
      ModConfig.server.potioncore.cureDisablingPotionEffectsImpl.getClass();
      return var10000.anyMatch(var10001::contains);
   }

   public static PotionEffect onAddTipsyEffect(PotionEffect potionEffect) {
      if (ModConfig.server.potioncore.incurableTipsy) {
         ((HookPotionCore.Incurable)potionEffect).setIncurable(true);
      }

      return potionEffect;
   }

   public static RangedAttribute resistance_createAttribute(RangedAttribute originalAttribute) {
      return (RangedAttribute)(!ModConfig.server.potioncore.alternativeResistanceMode ? originalAttribute : new ExtendedRangedAttribute((IAttribute)null, originalAttribute.getName(), originalAttribute.getDefaultValue(), 0.0D, Double.MAX_VALUE) {
         public IAttributeInstance createInstance(AbstractAttributeMap attributeMap, IAttribute attribute) {
            return new InversedAttributeInstance(attributeMap, attribute);
         }
      });
   }

   public static Potion resistance_registerPotionAttributeModifier(Potion potion, IAttribute attribute, String uniqueId, double ammount, int operation) {
      return potion.registerPotionAttributeModifier(attribute, uniqueId, ModConfig.server.potioncore.resistanceAmount, ModConfig.server.potioncore.resistanceOperation);
   }

   public static Potion vulnerable_registerPotionAttributeModifier(Potion potion, IAttribute attribute, String uniqueId, double ammount, int operation) {
      return potion.registerPotionAttributeModifier(attribute, uniqueId, ModConfig.server.potioncore.vulnerableAmount, ModConfig.server.potioncore.vulnerableOperation);
   }

   public static double getAdjustedDamageResistanceAttribute(EntityLivingBase entity) {
      return 0.0D;
   }

   public static void preResistancePotionCalculation(float damage) {
      originalDamage = damage;
   }

   public static float postResistancePotionCalculation(EntityLivingBase entity, float damage) {
      if (!DAMAGE_RESISTANCE.isPresent()) {
         return damage;
      } else {
         IAttributeInstance attribute = entity.getEntityAttribute(DAMAGE_RESISTANCE.get((Object)null));
         return ModConfig.server.potioncore.alternativeResistanceMode ? (float)((double)originalDamage * attribute.getAttributeValue()) : (float)((double)originalDamage * (1.0D - (attribute.getAttributeValue() - 1.0D)));
      }
   }

   public static double getActualResistance(double resistance) {
      return ModConfig.server.potioncore.alternativeResistanceMode ? -resistance : resistance;
   }

   public interface Incurable {
      boolean isIncurable();

      void setIncurable(boolean var1);
   }
}
