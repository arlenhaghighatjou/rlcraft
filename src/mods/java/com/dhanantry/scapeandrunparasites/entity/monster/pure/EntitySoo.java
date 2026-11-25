package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySoo extends EntityPPure {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntitySoo.class, DataSerializers.BYTE);

   public EntitySoo(World worldIn) {
      super(worldIn);
      this.moveHelper = new EntitySoo.AIMoveControl(this);
      this.setSize(1.9F, 2.6F);
      this.setNoGravity(true);
      this.tasks.removeTask(this.folow);
      this.ignoreFrustumCheck = true;
      this.borderOrb = -1;
      this.totalP = SRPConfigMobs.alafhaTotalActiveMobs;
      this.mobID = new int[this.totalP + SRPConfigMobs.alafhaLimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.alafhaLimit];

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      if (SRPConfigMobs.alafhaMaxY != 256) {
         this.tasks.addTask(3, new EntityAIFlightLimits(this, SRPConfigMobs.alafhaMaxY, true));
      }

      this.adaptationCap = 0.95F;
   }

   public int getParasiteIDRegister() {
      return 82;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, 64.0D));
      this.tasks.addTask(6, new EntitySoo.AIMoveRandom());
      this.tasks.addTask(2, new EntityAIAttackMeleeNotGround(this, 4.5D, 16.0D, 0.045D, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ALAFHA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ALAFHA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.ALAFHA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.pureFollow);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ALAFHA_MELLE);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.world.isRemote) {
         this.moveHelper.setMoveTo(this.posX, this.posY + 5.0D, this.posZ, 0.5D);
      }

   }

   public void onUpdate() {
      super.onUpdate();
      this.setNoGravity(true);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 1.6F;
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.ALAFHA_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ALAFHA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ALAFHA_DEATH;
   }

   protected float getSoundVolume() {
      return 2.0F;
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      return super.onInitialSpawn(difficulty, livingdata);
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public static void registerFixesAlafha(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntitySoo.class);
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

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntitySoo vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntitySoo.this.posX;
            double d1 = this.posY - EntitySoo.this.posY;
            double d2 = this.posZ - EntitySoo.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntitySoo.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntitySoo.this.motionX *= 0.5D;
               EntitySoo.this.motionY *= 0.5D;
               EntitySoo.this.motionZ *= 0.5D;
            } else {
               EntitySoo.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntitySoo.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntitySoo.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntitySoo.this.getAttackTarget() == null) {
                  EntitySoo.this.rotationYaw = -((float)MathHelper.atan2(EntitySoo.this.motionX, EntitySoo.this.motionZ)) * 57.295776F;
                  EntitySoo.this.renderYawOffset = EntitySoo.this.rotationYaw;
               } else {
                  double d4 = EntitySoo.this.getAttackTarget().posX - EntitySoo.this.posX;
                  double d5 = EntitySoo.this.getAttackTarget().posZ - EntitySoo.this.posZ;
                  EntitySoo.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntitySoo.this.renderYawOffset = EntitySoo.this.rotationYaw;
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
         EntityMoveHelper entitymovehelper = EntitySoo.this.getMoveHelper();
         if (!entitymovehelper.isUpdating()) {
            return true;
         } else {
            double d0 = entitymovehelper.getX() - EntitySoo.this.posX;
            double d1 = entitymovehelper.getY() - EntitySoo.this.posY;
            double d2 = entitymovehelper.getZ() - EntitySoo.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
         }
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         if (EntitySoo.this.getAttackTarget() != null) {
            BlockPos blockpos = new BlockPos(EntitySoo.this);
            byte flag = 1;
            double speed = 0.11D;
            if (EntitySoo.this.getDistanceSq(EntitySoo.this.getAttackTarget()) > 400.0D) {
               blockpos = new BlockPos(EntitySoo.this.getAttackTarget());
               flag = 2;
               speed += 0.11D;
            } else if (EntitySoo.this.getDistanceSq(EntitySoo.this.getAttackTarget()) < 100.0D) {
               blockpos = new BlockPos(EntitySoo.this.getAttackTarget());
               flag = 3;
               speed += 0.11D;
            }

            for(int i = 0; i < 3; ++i) {
               BlockPos blockpos1 = blockpos.add(EntitySoo.this.rand.nextInt(15) - 7, EntitySoo.this.rand.nextInt(9) - 5, EntitySoo.this.rand.nextInt(15) - 7);
               if (flag == 2) {
                  blockpos1 = blockpos.add(EntitySoo.this.rand.nextInt(6) - 2, EntitySoo.this.rand.nextInt(7) - 2, EntitySoo.this.rand.nextInt(6) - 2);
               } else if (flag == 3) {
                  blockpos1 = blockpos.add(EntitySoo.this.rand.nextInt(4) + 3, EntitySoo.this.rand.nextInt(5) + 4, EntitySoo.this.rand.nextInt(4) + 3);
               }

               if (EntitySoo.this.world.isAirBlock(blockpos1)) {
                  EntitySoo.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
                  if (EntitySoo.this.getAttackTarget() == null) {
                     EntitySoo.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                  }
                  break;
               }
            }
         } else {
            Random random = EntitySoo.this.getRNG();
            double d0 = EntitySoo.this.posX + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = EntitySoo.this.posY + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = EntitySoo.this.posZ + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            EntitySoo.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.5D);
         }

      }
   }
}
