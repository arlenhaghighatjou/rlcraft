package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
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

public class EntityOmboo extends EntityPPure {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityOmboo.class, DataSerializers.BYTE);

   public EntityOmboo(World worldIn) {
      super(worldIn);
      this.moveHelper = new EntityOmboo.AIMoveControl(this);
      this.setSize(1.7F, 2.4F);
      this.setNoGravity(true);
      this.tasks.removeTask(this.folow);
      this.borderOrb = -1;
      if (SRPConfigMobs.ombooMaxY != 256) {
         this.tasks.addTask(3, new EntityAIFlightLimits(this, SRPConfigMobs.ombooMaxY, true));
      }

      this.adaptationCap = 0.95F;
   }

   public int getParasiteIDRegister() {
      return 47;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, SRPConfig.pureFollow));
      this.tasks.addTask(4, new EntityOmboo.AIChargeAttack());
      this.tasks.addTask(6, new EntityOmboo.AIMoveRandom());
      this.tasks.addTask(5, new EntityOmboo.AIBomb(this));
      this.tasks.addTask(4, new EntityAIFlightLimits(this, 7, false));
      this.tasks.addTask(4, new EntityAIFlightLimits(this, 20, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.OMBOO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.OMBOO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.OMBOO_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.pureFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote && this.onGround) {
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
      EntityLiving.registerFixesMob(fixer, EntityOmboo.class);
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
         if (this.ccc >= 15) {
            this.ccc = 0;
            if (this.parent.getAttackTarget() != null) {
               EntityLivingBase target = this.parent.getAttackTarget();
               if (target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 25.0D) {
                  return true;
               }
            }
         }

         return false;
      }

      public void updateTask() {
         EntityBomb out = new EntityBomb(this.parent.world, this.parent, SRPConfigMobs.ombooGriefing);
         out.copyLocationAndAnglesFrom(this.parent);
         out.setFuse(80);
         out.setStren(1.0F);
         out.setDamage((float)SRPAttributes.OMBOO_BOMBDAMAGE, 4);
         this.parent.world.spawnEntity(out);
         out.updateSTR();
      }
   }

   class AIChargeAttack extends EntityAIBase {
      public AIChargeAttack() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntityOmboo.this.getAttackTarget() != null && EntityOmboo.this.rand.nextInt(7) == 0) {
            return EntityOmboo.this.getDistanceSq(EntityOmboo.this.getAttackTarget()) > 3.0D;
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return EntityOmboo.this.getMoveHelper().isUpdating() && EntityOmboo.this.isCharging() && EntityOmboo.this.getAttackTarget() != null && EntityOmboo.this.getAttackTarget().isEntityAlive();
      }

      public void startExecuting() {
         EntityLivingBase entitylivingbase = EntityOmboo.this.getAttackTarget();
         Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
         EntityOmboo.this.moveHelper.setMoveTo(vec3d.x, vec3d.y + 10.0D, vec3d.z, 1.0D);
         EntityOmboo.this.setCharging(true);
      }

      public void resetTask() {
         EntityOmboo.this.setCharging(false);
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = EntityOmboo.this.getAttackTarget();
         if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
            if (EntityOmboo.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox())) {
               EntityOmboo.this.attackEntityAsMob(entitylivingbase);
               EntityOmboo.this.setCharging(false);
            } else {
               double d0 = EntityOmboo.this.getDistanceSq(entitylivingbase);
               if (d0 < 9.0D) {
                  Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                  EntityOmboo.this.moveHelper.setMoveTo(vec3d.x, vec3d.y + 10.0D, vec3d.z, 1.0D);
               }
            }
         }

      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityOmboo vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityOmboo.this.posX;
            double d1 = this.posY - EntityOmboo.this.posY;
            double d2 = this.posZ - EntityOmboo.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityOmboo.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityOmboo.this.motionX *= 0.5D;
               EntityOmboo.this.motionY *= 0.5D;
               EntityOmboo.this.motionZ *= 0.5D;
            } else {
               EntityOmboo.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityOmboo.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityOmboo.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityOmboo.this.getAttackTarget() == null) {
                  EntityOmboo.this.rotationYaw = -((float)MathHelper.atan2(EntityOmboo.this.motionX, EntityOmboo.this.motionZ)) * 57.295776F;
                  EntityOmboo.this.renderYawOffset = EntityOmboo.this.rotationYaw;
               } else {
                  double d4 = EntityOmboo.this.getAttackTarget().posX - EntityOmboo.this.posX;
                  double d5 = EntityOmboo.this.getAttackTarget().posZ - EntityOmboo.this.posZ;
                  EntityOmboo.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityOmboo.this.renderYawOffset = EntityOmboo.this.rotationYaw;
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
         return !EntityOmboo.this.getMoveHelper().isUpdating() && EntityOmboo.this.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = new BlockPos(EntityOmboo.this);
         byte flag = 1;
         double speed = 0.2D;
         if (EntityOmboo.this.getAttackTarget() != null) {
            if (EntityOmboo.this.getDistanceSq(EntityOmboo.this.getAttackTarget()) > 100.0D) {
               blockpos = new BlockPos(EntityOmboo.this.getAttackTarget());
               flag = 2;
               speed += 0.1D;
            } else if (EntityOmboo.this.getDistanceSq(EntityOmboo.this.getAttackTarget()) < 36.0D) {
               blockpos = new BlockPos(EntityOmboo.this.getAttackTarget());
               flag = 3;
               speed += 0.1D;
            }
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityOmboo.this.rand.nextInt(15) - 7, EntityOmboo.this.rand.nextInt(11) - 5, EntityOmboo.this.rand.nextInt(15) - 7);
            if (flag == 2) {
               blockpos1 = blockpos.add(EntityOmboo.this.rand.nextInt(6) - 2, EntityOmboo.this.rand.nextInt(7) - 2, EntityOmboo.this.rand.nextInt(6) - 2);
            } else if (flag == 3) {
               blockpos1 = blockpos.add(EntityOmboo.this.rand.nextInt(4) + 3, EntityOmboo.this.rand.nextInt(5) + 4, EntityOmboo.this.rand.nextInt(4) + 3);
            }

            if (EntityOmboo.this.world.isAirBlock(blockpos1)) {
               EntityOmboo.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 1.0D, (double)blockpos1.getZ() + 0.5D, speed);
               if (EntityOmboo.this.getAttackTarget() == null) {
                  EntityOmboo.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 1.0D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
