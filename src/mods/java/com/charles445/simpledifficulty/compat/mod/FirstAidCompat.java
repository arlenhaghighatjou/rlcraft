package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.util.VersionDelimiter;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.distribution.DamageDistributionBuilderFactory;
import ichttt.mods.firstaid.api.distribution.IStandardDamageDistributionBuilder;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.apiimpl.distribution.BaseDamageDistributionBuilder;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;

public class FirstAidCompat {
   private VersionDelimiter version;

   public FirstAidCompat() {
      try {
         this.version = new VersionDelimiter(FirstAid.VERSION);
         if (this.version.isSameOrNewerVersion(1, 6, 16)) {
            this.registerDamageSources();
         } else {
            SimpleDifficulty.logger.info("FirstAid version is lower than 1.6.16, compatibility was skipped");
         }
      } catch (Exception var2) {
         SimpleDifficulty.logger.error("FirstAidCompat reflection failed! First Aid compatibility is now disabled!", var2);
      }

   }

   private void registerDamageSources() {
      DamageDistributionBuilderFactory factory = DamageDistributionBuilderFactory.getInstance();
      if (factory == null) {
         SimpleDifficulty.logger.error("FirstAid DamageDistributionBuilderFactory not found!");
      } else {
         IStandardDamageDistributionBuilder builder = factory.newStandardBuilder();
         builder.addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.BODY);
         builder.addDistributionLayer(EntityEquipmentSlot.HEAD, EnumPlayerPart.HEAD);
         builder.ignoreOrder();
         builder.disableNeighbourRestDistribution();
         this.registerStatic(builder, SDDamageSources.DEHYDRATION);
         SimpleDifficulty.logger.info("FirstAid Dehydration Registered");
      }
   }

   private void registerStatic(IStandardDamageDistributionBuilder builder, DamageSource... sources) {
      if (builder instanceof BaseDamageDistributionBuilder) {
         FirstAidRegistryImpl.INSTANCE.registerDistribution(sources, ((BaseDamageDistributionBuilder)builder).build());
      } else {
         throw new RuntimeException("Builder is not supported");
      }
   }
}
