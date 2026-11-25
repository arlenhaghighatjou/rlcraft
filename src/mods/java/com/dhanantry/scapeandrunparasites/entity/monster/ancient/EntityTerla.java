package com.dhanantry.scapeandrunparasites.entity.monster.ancient;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAncient;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHomming;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;

public class EntityTerla extends EntityPAncient implements EntityBodyParts, IRangedAttackMob, EntityCutomAttack {
   private EntityBody head;
   private EntityBody middle;
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.RED, Overlay.PROGRESS)).setDarkenSky(false);

   public EntityTerla(World worldIn) {
      super(worldIn);
      this.setSize(2.4F, 2.9F);
      this.tasks.removeTask(this.folow);
      this.ignoreFrustumCheck = true;
      this.type = 63;
      this.borderOrb = -1;
      this.head = new EntityBody(this, 2.4F, 7.5F, 1.0F, -3.0F, 0.0F, -1, 1, false, 0.2F);
      this.middle = new EntityBody(this, 2.4F, 4.5F, 1.0F, 0.0F, 3.0F, 1, 2, false, 0.2F);
   }

   public int getParasiteIDRegister() {
      return 20;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(6, new EntityAIAttackMeleeRangeSwitch(this, 10.0F));
      this.tasks.addTask(2, new EntityAIAttackMeleeStatusAOE(this, 1.0D, false, 100.0D, 5.0D));
      this.tasks.addTask(4, new EntityAIAttackRangedStatus(this, 1.0D, 60, 80, 40.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.TERLA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.TERLA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.TERLA_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.ancientFollow);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.head.onUpdate();
      this.middle.onUpdate();
   }

   protected void updateAITasks() {
      super.updateAITasks();
      this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
   }

   public void setCustomNameTag(String name) {
      super.setCustomNameTag(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   public void addTrackingPlayer(EntityPlayerMP player) {
   }

   public void removeTrackingPlayer(EntityPlayerMP player) {
      super.removeTrackingPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
      Vec3d vec3d = this.getLook(1.0F);
      double var10000 = target.posX - (this.posX + vec3d.x);
      var10000 = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F) - (0.5D + this.posY + (double)(this.height / 2.0F));
      var10000 = target.posZ - (this.posZ + vec3d.z);
      this.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);
      var10000 = target.getEntityBoundingBox().minY + (double)(target.height / 4.0F) - (1.0D + this.posY + (double)(this.height / 2.0F));
      EntityProjectileHomming entitylargefireball = new EntityProjectileHomming(this.world, this, target, SRPAttributes.ORONCO_ATTACK_DAMAGE);
      entitylargefireball.posX = this.posX + vec3d.x;
      entitylargefireball.posY = this.posY + (double)this.getEyeHeight() - 0.2D;
      entitylargefireball.posZ = this.posZ + vec3d.z;
      this.world.spawnEntity(entitylargefireball);
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0D, entityIn.posY + 1.0D, entityIn.posZ + 1.0D)).grow(5.0D, 2.0D, 5.0D);
      List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
      if (moblist.size() > 4) {
         axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(5.0D, 3.0D, 5.0D);
         moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
         float luck = (float)(SRPAttributes.TERLA_ATTACK_DAMAGE * 2.0D);

         for(EntityLivingBase mob : moblist) {
            if (mob != null && !(mob instanceof EntityParasiteBase) && mob != this) {
               EntityDamage damage = new EntityDamage(this.world, mob.posX, mob.posY, mob.posZ, 0.0F, this, luck, false, 3.0F);
               this.world.spawnEntity(damage);
            }
         }

         return true;
      } else {
         for(EntityLivingBase mob : moblist) {
            if (mob != null && !(mob instanceof EntityParasiteBase)) {
               this.attackEntityAsMob(mob);
            }
         }

         return !moblist.isEmpty();
      }
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag && source.getTrueSource() instanceof EntityPlayerMP) {
         this.bossInfo.addPlayer((EntityPlayerMP)source.getTrueSource());
      }

      return flag;
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      boolean flag = this.attackEntityFrom(source, amount);
      return flag;
   }

   public void setBodyPartDead(int id) {
   }

   public int getHorizontalFaceSpeed() {
      return 3;
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      return potioneffectIn.getPotion() == MobEffects.POISON ? false : super.isPotionApplicable(potioneffectIn);
   }

   public float getEyeHeight() {
      return 4.7F;
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.MOBSILENCE;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }

   protected float getSoundVolume() {
      return 5.0F;
   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      if (this.middle != null) {
         this.world.removeEntityDangerously(this.middle);
      }

      super.setDead();
   }

   public void setSwingingArms(boolean swingingArms) {
   }

   public static void registerFixesTerla(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityTerla.class);
   }
}
