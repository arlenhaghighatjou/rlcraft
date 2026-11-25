package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBeholder extends RideableCreatureEntity {
   public boolean griefing = true;

   public EntityBeholder(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.trueSight = true;
      this.setAttackCooldownMax(20);
      this.solidCollision = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
   }

   public void loadCreatureFlags() {
      this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
   }

   public void riderEffects(EntityLivingBase rider) {
      if (rider.isPotionActive(MobEffects.MINING_FATIGUE)) {
         rider.removePotionEffect(MobEffects.MINING_FATIGUE);
      }

      if (rider.isPotionActive(ObjectManager.getEffect("weight"))) {
         rider.removePotionEffect(ObjectManager.getEffect("weight"));
      }

   }

   public void onDamage(DamageSource damageSrc, float damage) {
      super.onDamage(damageSrc, damage);
      Entity damageEntity = damageSrc.getTrueSource();
      if (damageEntity != null && ("mob".equals(damageSrc.damageType) || "player".equals(damageSrc.damageType)) && damageEntity instanceof EntityLivingBase) {
         EntityLivingBase targetLiving = (EntityLivingBase)damageEntity;
         List<Potion> goodEffects = new ArrayList();

         for(PotionEffect effect : targetLiving.getActivePotionEffects()) {
            if (ObjectLists.inEffectList("buffs", effect.getPotion())) {
               goodEffects.add(effect.getPotion());
            }
         }

         if (goodEffects.size() > 0 && this.getRNG().nextBoolean()) {
            if (goodEffects.size() > 1) {
               targetLiving.removePotionEffect((Potion)goodEffects.get(this.getRNG().nextInt(goodEffects.size())));
            } else {
               targetLiving.removePotionEffect((Potion)goodEffects.get(0));
            }

            float leeching = damage * 1.1F;
            this.heal(leeching);
         }
      }

   }

   public boolean isFlying() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("arcanelaserstorm", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.6F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public boolean isDamageEntityApplicable(Entity entity) {
      return entity instanceof EntityBeholder ? false : super.isDamageEntityApplicable(entity);
   }

   public boolean canBurn() {
      return false;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.9D;
   }

   public double getMountedZOffset() {
      return (double)this.width * -0.2D;
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.hasPickupEntity()) {
               this.dropPickupEntity();
            } else if (this.getStamina() >= this.getStaminaCost()) {
               if (rider instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)rider;
                  ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("arcanelaserstorm");
                  if (projectileInfo != null) {
                     BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), player);
                     this.getEntityWorld().spawnEntity(projectile);
                     this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                     this.triggerAttackCooldown();
                  }
               }

               this.applyStaminaCost();
            }
         }
      }
   }

   public float getStaminaCost() {
      return 10.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 40;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }

   public float getBrightness() {
      return this.isAttackOnCooldown() ? 1.0F : super.getBrightness();
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return this.isAttackOnCooldown() ? 15728880 : super.getBrightnessForRender();
   }
}
