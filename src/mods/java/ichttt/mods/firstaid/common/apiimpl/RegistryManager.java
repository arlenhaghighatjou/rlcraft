package ichttt.mods.firstaid.common.apiimpl;

import net.lax1dude.eaglercraft.v1_8.HString;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.FirstAidRegistry;
import ichttt.mods.firstaid.api.debuff.builder.DebuffBuilderFactory;
import ichttt.mods.firstaid.api.debuff.builder.IDebuffBuilder;
import ichttt.mods.firstaid.api.distribution.DamageDistributionBuilderFactory;
import ichttt.mods.firstaid.api.enums.EnumDebuffSlot;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.EventHandler;
import ichttt.mods.firstaid.common.apiimpl.distribution.DamageDistributionBuilderFactoryImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.ArrayUtils;

public class RegistryManager {
   public static final List<String> debuffConfigErrors = new ArrayList();

   public RegistryManager() {
   }

   public static void setupRegistries() {
      FirstAidRegistry.setImpl(FirstAidRegistryImpl.INSTANCE);
      DebuffBuilderFactory.setInstance(DebuffBuilderFactoryImpl.INSTANCE);
      DamageDistributionBuilderFactory.setInstance(DamageDistributionBuilderFactoryImpl.INSTANCE);
   }

   public static void registerDefaults() {
      FirstAid.LOGGER.debug("Registering defaults registry values");
      DamageDistributionBuilderFactory distributionBuilderFactory = (DamageDistributionBuilderFactory)Objects.requireNonNull(DamageDistributionBuilderFactory.getInstance());
      distributionBuilderFactory.newStandardBuilder().addDistributionLayer(EntityEquipmentSlot.FEET, EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT).addDistributionLayer(EntityEquipmentSlot.LEGS, EnumPlayerPart.LEFT_LEG, EnumPlayerPart.RIGHT_LEG).registerStatic(new DamageSource[]{DamageSource.FALL, DamageSource.HOT_FLOOR});
      distributionBuilderFactory.newStandardBuilder().addDistributionLayer(EntityEquipmentSlot.HEAD, EnumPlayerPart.HEAD).registerStatic(new DamageSource[]{DamageSource.ANVIL});
      distributionBuilderFactory.newStandardBuilder().addDistributionLayer(EntityEquipmentSlot.HEAD, EnumPlayerPart.HEAD).addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.LEFT_ARM, EnumPlayerPart.RIGHT_ARM).ignoreOrder().registerStatic(new DamageSource[]{DamageSource.LIGHTNING_BOLT});
      distributionBuilderFactory.newRandomBuilder().registerStatic(new DamageSource[]{DamageSource.MAGIC});
      if (FirstAidConfig.hardMode) {
         distributionBuilderFactory.newStandardBuilder().addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.BODY).disableNeighbourRestDistribution().registerStatic(new DamageSource[]{DamageSource.STARVE});
         distributionBuilderFactory.newStandardBuilder().addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.BODY).addDistributionLayer(EntityEquipmentSlot.HEAD, EnumPlayerPart.HEAD).ignoreOrder().disableNeighbourRestDistribution().registerStatic(new DamageSource[]{DamageSource.DROWN});
      } else {
         distributionBuilderFactory.newRandomBuilder().tryNoKill().registerStatic(new DamageSource[]{DamageSource.STARVE, DamageSource.DROWN});
      }

      distributionBuilderFactory.newRandomBuilder().tryNoKill().registerStatic(new DamageSource[]{DamageSource.IN_WALL, DamageSource.CRAMMING});
      distributionBuilderFactory.newEqualBuilder().reductionMultiplier(0.8F).registerDynamic(DamageSource::isExplosion);
      DebuffBuilderFactory debuffBuilderFactory = DebuffBuilderFactory.getInstance();
      loadValuesFromConfig(debuffBuilderFactory, "blindness", () -> {
         return FirstAidConfig.debuffs.head.blindness;
      }, EventHandler.HEARTBEAT, FirstAidConfig.debuffs.head.blindnessConditions, EnumDebuffSlot.HEAD);
      loadValuesFromConfig(debuffBuilderFactory, "nausea", () -> {
         return FirstAidConfig.debuffs.head.nausea;
      }, (SoundEvent)null, FirstAidConfig.debuffs.head.nauseaConditions, EnumDebuffSlot.HEAD);
      loadValuesFromConfig(debuffBuilderFactory, "nausea", () -> {
         return FirstAidConfig.debuffs.body.nausea;
      }, (SoundEvent)null, FirstAidConfig.debuffs.body.nauseaConditions, EnumDebuffSlot.BODY);
      loadValuesFromConfig(debuffBuilderFactory, "weakness", () -> {
         return FirstAidConfig.debuffs.body.weakness;
      }, FirstAidConfig.debuffs.body.weaknessConditions, EnumDebuffSlot.BODY);
      loadValuesFromConfig(debuffBuilderFactory, "mining_fatigue", () -> {
         return FirstAidConfig.debuffs.arms.mining_fatigue;
      }, FirstAidConfig.debuffs.arms.miningFatigueConditions, EnumDebuffSlot.ARMS);
      loadValuesFromConfig(debuffBuilderFactory, "slowness", () -> {
         return FirstAidConfig.debuffs.legsAndFeet.slowness;
      }, FirstAidConfig.debuffs.legsAndFeet.slownessConditions, EnumDebuffSlot.LEGS_AND_FEET);
   }

   private static void loadValuesFromConfig(DebuffBuilderFactory factory, String potionName, BooleanSupplier enableCondition, SoundEvent event, FirstAidConfig.Debuffs.ConditionOnHit config, EnumDebuffSlot slot) {
      if (config.debuffLength.length != config.damageTaken.length) {
         logError("The fields to not have the same amount of values!", potionName, slot);
      } else if (config.debuffLength.length == 0) {
         logError("The fields are empty!", potionName, slot);
      } else {
         float[] healthPercentageLeft = new float[config.damageTaken.length];
         System.arraycopy(config.damageTaken, 0, healthPercentageLeft, 0, config.damageTaken.length);
         Arrays.sort(healthPercentageLeft);
         ArrayUtils.reverse(healthPercentageLeft);
         if (!Arrays.equals(healthPercentageLeft, config.damageTaken)) {
            logError("The damageTaken field is not sorted right!", potionName, slot);
         } else {
            IDebuffBuilder builder = factory.newOnHitDebuffBuilder(potionName);
            builder.addEnableCondition(enableCondition);

            for(int i = 0; i < config.damageTaken.length; ++i) {
               builder.addBound(config.damageTaken[i], config.debuffLength[i]);
            }

            if (event != null) {
               builder.addSoundEffect(event);
            }

            builder.register(slot);
         }
      }
   }

   private static void loadValuesFromConfig(DebuffBuilderFactory factory, String potionName, BooleanSupplier enableCondition, FirstAidConfig.Debuffs.ConditionConstant config, EnumDebuffSlot slot) {
      if (config.debuffStrength.length != config.healthPercentageLeft.length) {
         logError("The fields to not have the same amount of values!", potionName, slot);
      } else if (config.healthPercentageLeft.length == 0) {
         logError("The fields are empty!", potionName, slot);
      } else if (!ArrayUtils.isSorted(config.healthPercentageLeft)) {
         logError("The healthPercentageLeft field is not sorted right!", potionName, slot);
      } else {
         IDebuffBuilder builder = factory.newConstantDebuffBuilder(potionName);
         builder.addEnableCondition(enableCondition);

         for(int i = 0; i < config.healthPercentageLeft.length; ++i) {
            builder.addBound(config.healthPercentageLeft[i], config.debuffStrength[i]);
         }

         builder.register(slot);
      }
   }

   private static void logError(String error, String potionName, EnumDebuffSlot slot) {
      String errorMsg = HString.format("Invalid config entry for debuff %s at part %s: %s", potionName, slot.toString(), error);
      FirstAid.LOGGER.warn(errorMsg);
      debuffConfigErrors.add(errorMsg);
   }

   public static void finalizeRegistries() {
      FirstAidRegistryImpl.finish();
      DebuffBuilderFactoryImpl.verify();
   }
}
