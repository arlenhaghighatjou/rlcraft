package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityEmana extends EntityPPrimitive implements EntityCanShoot {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityEmana.class, DataSerializers.BYTE);
   private int count;

   public EntityEmana(World worldIn) {
      super(worldIn);
      this.moveHelper = new EntityEmana.AIMoveControl(this);
      this.setSize(0.4F, 1.5F);
      this.setNoGravity(true);
      this.tasks.removeTask(this.folow);
      this.borderOrb = -1;
      if (SRPConfigMobs.emanaMaxY != 256) {
         this.tasks.addTask(3, new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
      }

   }

   public int getParasiteIDRegister() {
      return 4;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, SRPConfig.primitiveFollow));
      this.tasks.addTask(6, new EntityEmana.AIMoveRandom());
      this.tasks.addTask(1, new EntityAIAttackProjectile(this, 80, 20, 1));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.EMANA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.EMANA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.EMANA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.EMANA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.primitiveFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.world.isRemote) {
         this.moveHelper.setMoveTo(this.posX, this.posY + 5.0D, this.posZ, 0.5D);
      }

      if (!this.world.isRemote && this.ticksExisted % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
         ParasiteEventEntity.spawnNext(this, new EntityEmanaAdapted(this.world), true, true);
      }

   }

   public void onUpdate() {
      super.onUpdate();
      this.setNoGravity(true);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 0.7F;
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.EMANA_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.EMANA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.EMANA_DEATH;
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public static void registerFixesEmana(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityEmana.class);
   }

   private boolean getVexFlag(int mask) {
      int i = ((Byte)this.dataManager.get(VEX_FLAGS)).byteValue();
      return (i & mask) != 0;
   }

   private void setVexFlag(int mask, boolean value) {
      int i = ((Byte)this.dataManager.get(VEX_FLAGS)).byteValue();
      if (value) {
         i = i | mask;
      } else {
         i = i & ~mask;
      }

      this.dataManager.set(VEX_FLAGS, Byte.valueOf((byte)(i & 255)));
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 100) {
         for(int i = 0; i <= 1; ++i) {
            this.spawnParticles(EnumParticleTypes.FLAME);
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public EntityFireball getProj(double accelX, double accelY, double accelZ) {
      float pit = 1.0F;
      if (this.count == 4) {
         pit = 2.0F;
      }

      this.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, pit);
      if (this.count >= 4) {
         this.count = 0;
         return new EntityProjectileNade(this.world, this, accelX, accelY, accelZ, 3, 60);
      } else {
         EntityProjectileSpineball ball = new EntityProjectileSpineball(this.world, this, accelX, accelY, accelZ, SRPAttributes.EMANA_RANGED_DAMAGE);
         ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration, SRPConfigMobs.emanaPoisonAmplifier);
         ball.setGearDamage(SRPConfigMobs.emanaGearD);
         return ball;
      }
   }

   public void playProjSound() {
      ++this.count;
      if (this.count == 4) {
         this.world.setEntityState(this, (byte)100);
         float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 2.0F;
         this.playSound(this.getHurtSound(DamageSource.GENERIC), 4.0F, v);
      } else {
         this.playSound(SRPSounds.EMANA_SHOOTINGPOST, 2.0F, 1.0F);
      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityEmana vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityEmana.this.posX;
            double d1 = this.posY - EntityEmana.this.posY;
            double d2 = this.posZ - EntityEmana.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityEmana.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityEmana.this.motionX *= 0.5D;
               EntityEmana.this.motionY *= 0.5D;
               EntityEmana.this.motionZ *= 0.5D;
            } else {
               EntityEmana.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityEmana.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityEmana.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityEmana.this.getAttackTarget() == null) {
                  EntityEmana.this.rotationYaw = -((float)MathHelper.atan2(EntityEmana.this.motionX, EntityEmana.this.motionZ)) * 57.295776F;
                  EntityEmana.this.renderYawOffset = EntityEmana.this.rotationYaw;
               } else {
                  double d4 = EntityEmana.this.getAttackTarget().posX - EntityEmana.this.posX;
                  double d5 = EntityEmana.this.getAttackTarget().posZ - EntityEmana.this.posZ;
                  EntityEmana.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityEmana.this.renderYawOffset = EntityEmana.this.rotationYaw;
               }
            }
         }

      }
   }

   class AIMoveRandom extends EntityAIBase {
      public AIMoveRandom() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         return !EntityEmana.this.getMoveHelper().isUpdating() && EntityEmana.this.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = new BlockPos(EntityEmana.this);
         byte flag = 1;
         double speed = 0.5D;
         if (EntityEmana.this.getAttackTarget() != null) {
            if (EntityEmana.this.getDistanceSq(EntityEmana.this.getAttackTarget()) > 100.0D) {
               blockpos = new BlockPos(EntityEmana.this.getAttackTarget());
               flag = 2;
               speed += 0.25D;
            } else if (EntityEmana.this.getDistanceSq(EntityEmana.this.getAttackTarget()) < 36.0D) {
               blockpos = new BlockPos(EntityEmana.this.getAttackTarget());
               flag = 3;
               speed += 0.25D;
            }
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityEmana.this.rand.nextInt(15) - 7, EntityEmana.this.rand.nextInt(11) - 5, EntityEmana.this.rand.nextInt(15) - 7);
            if (flag == 2) {
               blockpos1 = blockpos.add(EntityEmana.this.rand.nextInt(6) - 2, EntityEmana.this.rand.nextInt(7) - 2, EntityEmana.this.rand.nextInt(6) - 2);
            } else if (flag == 3) {
               blockpos1 = blockpos.add(EntityEmana.this.rand.nextInt(4) + 3, EntityEmana.this.rand.nextInt(5) + 4, EntityEmana.this.rand.nextInt(4) + 3);
            }

            if (EntityEmana.this.world.isAirBlock(blockpos1)) {
               EntityEmana.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
               if (EntityEmana.this.getAttackTarget() == null) {
                  EntityEmana.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
