package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCacodemon extends RideableCreatureEntity {
   public boolean griefing = true;

   public EntityCacodemon(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = false;
      this.setupMob();
      this.stepHeight = 1.0F;
      this.hitAreaWidthScale = 1.5F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(EntityGhast.class));
   }

   public void loadCreatureFlags() {
      this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
   }

   public void onLivingUpdate() {
      if (!this.getEntityWorld().isRemote && this.isRareVariant() && this.hasAttackTarget() && this.ticksExisted % 20 == 0) {
         this.allyUpdate();
      }

      super.onLivingUpdate();
   }

   public void riderEffects(EntityLivingBase rider) {
      if (rider.isPotionActive(MobEffects.WITHER)) {
         rider.removePotionEffect(MobEffects.WITHER);
      }

      if (rider.isBurning()) {
         rider.extinguish();
      }

   }

   public void allyUpdate() {
      if (!this.getEntityWorld().isRemote) {
         if (CreatureManager.getInstance().getCreature("wraith").enabled && this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("wraith").getEntityClass(), 64.0D) < 10) {
            float random = this.rand.nextFloat();
            if (random <= 0.1F) {
               this.spawnAlly(this.posX - 2.0D + (double)(random * 4.0F), this.posY, this.posZ - 2.0D + (double)(random * 4.0F));
            }
         }

      }
   }

   public void spawnAlly(double x, double y, double z) {
      EntityWraith minion = (EntityWraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getEntityWorld());
      minion.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
      minion.setMinion(true);
      minion.setMasterTarget(this);
      this.getEntityWorld().spawnEntity(minion);
      if (this.getAttackTarget() != null) {
         minion.setRevengeTarget(this.getAttackTarget());
      }

      minion.setSizeScale(this.sizeScale);
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

   public boolean canAttackEntity(EntityLivingBase target) {
      return !(target instanceof EntityTrite) && !(target instanceof EntityAstaroth) && !(target instanceof EntityAsmodeus) && !(target instanceof EntityWraith) ? super.canAttackEntity(target) : false;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("demonicblast", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.6F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public boolean isDamageEntityApplicable(Entity entity) {
      return entity instanceof EntityCacodemon ? false : super.isDamageEntityApplicable(entity);
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return "explosion".equals(type) ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBurn() {
      return false;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.9D;
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.hasPickupEntity()) {
               this.dropPickupEntity();
            } else if (this.getStamina() >= this.getStaminaCost()) {
               if (rider instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)rider;
                  ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("demonicblast");
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
