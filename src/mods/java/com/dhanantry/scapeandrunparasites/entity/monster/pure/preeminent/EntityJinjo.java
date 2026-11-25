package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityJinjo extends EntityPPreeminent {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityJinjo.class, DataSerializers.BYTE);

   public EntityJinjo(World worldIn) {
      super(worldIn);
      this.moveHelper = new EntityJinjo.AIMoveControl(this);
      this.setSize(3.7F, 4.4F);
      this.setNoGravity(true);
      if (SRPConfigMobs.ombooMaxY != 256) {
         this.tasks.addTask(3, new EntityAIFlightLimits(this, SRPConfigMobs.ombooMaxY, true));
      }

      this.canTeleportToo = true;
      this.adaptationCap = 0.95F;
      this.ignoreFrustumCheck = true;
   }

   public int getParasiteIDRegister() {
      return 65;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, SRPConfig.pureFollow));
      this.tasks.addTask(4, new EntityJinjo.AIChargeAttack());
      this.tasks.addTask(6, new EntityJinjo.AIMoveRandom());
      this.tasks.addTask(5, new EntityJinjo.AIBomb(this));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(4, new EntityAIFlightLimits(this, 10, false));
      this.tasks.addTask(4, new EntityAIFlightLimits(this, 25, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.JINJO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.JINJO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.JINJO_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.JINJO_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.preeminentFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote) {
         if (this.onGround) {
            this.moveHelper.setMoveTo(this.posX, this.posY + 5.0D, this.posZ, 0.5D);
         }
      } else if (this.world.isRemote && this.rand.nextInt(30) == 0) {
         for(int i = 0; i <= 20; ++i) {
            if (i % 5 == 0) {
               this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1, 0.2D, 0.0D);
            }
         }
      }

   }

   public void onUpdate() {
      super.onUpdate();
      this.setNoGravity(true);
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.jinjoOrbEffects, mobs);
      }

      return flag;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 2.4F;
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public static void registerFixesButhol(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityJinjo.class);
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

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setCharging(boolean charging) {
      this.setVexFlag(1, charging);
   }

   class AIBomb extends EntityAIBase {
      private final EntityParasiteBase parent;
      private int ccc;

      public AIBomb(EntityParasiteBase parentIn) {
         this.parent = parentIn;
         this.ccc = 0;
      }

      public boolean shouldExecute() {
         ++this.ccc;
         if (this.ccc >= 100) {
            this.ccc = 0;
            if (this.parent.getAttackTarget() != null) {
               EntityLivingBase target = this.parent.getAttackTarget();
               if (target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 256.0D) {
                  return true;
               }
            }
         }

         return false;
      }

      public void updateTask() {
         EntityBomb out = new EntityBomb(this.parent.world, this.parent, SRPConfigMobs.jinjoGriefing);
         if (this.parent.getAttackTarget() != null) {
            boolean flag = this.parent.canEntityBeSeen(this.parent.getAttackTarget()) || this.parent.world.rand.nextInt(3) == 0;
            out.copyLocationAndAnglesFrom(this.parent);
            out.setFuse(80);
            out.setStren(flag ? 4.0F : 8.0F);
            out.setSkin(flag ? 2 : 3);
            out.setDamage((float)this.parent.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * SRPConfigMobs.jinjoExplotionMult, 7);
            this.parent.world.spawnEntity(out);
            out.updateSTR();
         }

      }
   }

   class AIChargeAttack extends EntityAIBase {
      public AIChargeAttack() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntityJinjo.this.getAttackTarget() != null && EntityJinjo.this.rand.nextInt(5) == 0) {
            return EntityJinjo.this.getDistanceSq(EntityJinjo.this.getAttackTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return EntityJinjo.this.getMoveHelper().isUpdating() && EntityJinjo.this.isCharging() && EntityJinjo.this.getAttackTarget() != null && EntityJinjo.this.getAttackTarget().isEntityAlive();
      }

      public void startExecuting() {
         EntityLivingBase entitylivingbase = EntityJinjo.this.getAttackTarget();
         Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
         EntityJinjo.this.moveHelper.setMoveTo(vec3d.x, vec3d.y + 15.0D, vec3d.z, 0.2D);
         EntityJinjo.this.setCharging(true);
      }

      public void resetTask() {
         EntityJinjo.this.setCharging(false);
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = EntityJinjo.this.getAttackTarget();
         if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
            if (EntityJinjo.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox())) {
               EntityJinjo.this.attackEntityAsMob(entitylivingbase);
               EntityJinjo.this.setCharging(false);
            } else {
               double d0 = EntityJinjo.this.getDistanceSq(entitylivingbase);
               if (d0 < 9.0D) {
                  Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                  EntityJinjo.this.moveHelper.setMoveTo(vec3d.x, vec3d.y + 20.0D, vec3d.z, 1.0D);
               }
            }
         }

      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityJinjo vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityJinjo.this.posX;
            double d1 = this.posY - EntityJinjo.this.posY;
            double d2 = this.posZ - EntityJinjo.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityJinjo.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityJinjo.this.motionX *= 0.5D;
               EntityJinjo.this.motionY *= 0.5D;
               EntityJinjo.this.motionZ *= 0.5D;
            } else {
               EntityJinjo.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityJinjo.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityJinjo.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityJinjo.this.getAttackTarget() == null) {
                  EntityJinjo.this.rotationYaw = -((float)MathHelper.atan2(EntityJinjo.this.motionX, EntityJinjo.this.motionZ)) * 57.295776F;
                  EntityJinjo.this.renderYawOffset = EntityJinjo.this.rotationYaw;
               } else {
                  double d4 = EntityJinjo.this.getAttackTarget().posX - EntityJinjo.this.posX;
                  double d5 = EntityJinjo.this.getAttackTarget().posZ - EntityJinjo.this.posZ;
                  EntityJinjo.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityJinjo.this.renderYawOffset = EntityJinjo.this.rotationYaw;
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
         return !EntityJinjo.this.getMoveHelper().isUpdating() && EntityJinjo.this.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = new BlockPos(EntityJinjo.this);
         byte flag = 1;
         double speed = 0.1D;
         if (EntityJinjo.this.getAttackTarget() != null) {
            if (EntityJinjo.this.getDistanceSq(EntityJinjo.this.getAttackTarget()) > 100.0D) {
               blockpos = new BlockPos(EntityJinjo.this.getAttackTarget());
               flag = 2;
               speed += 0.05D;
            } else if (EntityJinjo.this.getDistanceSq(EntityJinjo.this.getAttackTarget()) < 36.0D) {
               blockpos = new BlockPos(EntityJinjo.this.getAttackTarget());
               flag = 3;
               speed += 0.05D;
            }
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityJinjo.this.rand.nextInt(15) - 7, EntityJinjo.this.rand.nextInt(11) - 5, EntityJinjo.this.rand.nextInt(15) - 7);
            if (flag == 2) {
               blockpos1 = blockpos.add(EntityJinjo.this.rand.nextInt(6) - 2, EntityJinjo.this.rand.nextInt(7) - 2, EntityJinjo.this.rand.nextInt(6) - 2);
            } else if (flag == 3) {
               blockpos1 = blockpos.add(EntityJinjo.this.rand.nextInt(4) + 3, EntityJinjo.this.rand.nextInt(5) + 4, EntityJinjo.this.rand.nextInt(4) + 3);
            }

            if (EntityJinjo.this.world.isAirBlock(blockpos1)) {
               EntityJinjo.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 1.0D, (double)blockpos1.getZ() + 0.5D, speed);
               if (EntityJinjo.this.getAttackTarget() == null) {
                  EntityJinjo.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 1.0D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
