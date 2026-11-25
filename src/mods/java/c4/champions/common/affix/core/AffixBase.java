package c4.champions.common.affix.core;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.IAffix;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public abstract class AffixBase implements IAffix {
   protected static final Random rand = new Random();
   private final String identifier;
   private final AffixCategory category;
   private final int tier;

   public AffixBase(String identifier, AffixCategory category) {
      this(identifier, category, 1);
   }

   public AffixBase(String identifier, AffixCategory category, int tier) {
      this.identifier = identifier;
      this.category = category;
      this.tier = tier;
      AffixRegistry.registerAffix(identifier, this);
   }

   public String getIdentifier() {
      return this.identifier;
   }

   public AffixCategory getCategory() {
      return this.category;
   }

   public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
   }

   public void onSpawn(EntityLiving entity, IChampionship cap) {
   }

   public void onUpdate(EntityLiving entity, IChampionship cap) {
   }

   public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float amount, LivingAttackEvent evt) {
   }

   public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt) {
   }

   public float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
      return newAmount;
   }

   public float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
      return newAmount;
   }

   public float onHealed(EntityLiving entity, IChampionship cap, float amount, float newAmount) {
      return newAmount;
   }

   public void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt) {
   }

   public void onDeath(EntityLiving entity, IChampionship cap, DamageSource source, LivingDeathEvent evt) {
   }

   public boolean canApply(EntityLiving entity) {
      return true;
   }

   public boolean isCompatibleWith(IAffix affix) {
      return affix != this;
   }

   public int getTier() {
      return this.tier;
   }

   public static boolean isValidAffixTarget(EntityLiving mob, EntityLivingBase target, boolean checkSight) {
      if (target != null && target.isEntityAlive()) {
         if (checkSight && !mob.canEntityBeSeen(target)) {
            return false;
         } else {
            IAttributeInstance iattributeinstance = mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            double targetRange = iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
            targetRange = ConfigHandler.affix.abilityRange == 0 ? targetRange : Math.min(targetRange, (double)ConfigHandler.affix.abilityRange);
            return (double)mob.getDistance(target) <= targetRange;
         }
      } else {
         return false;
      }
   }
}
