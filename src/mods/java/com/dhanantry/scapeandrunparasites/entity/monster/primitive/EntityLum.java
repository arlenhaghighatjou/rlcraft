package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityLum extends EntityPPrimitive implements EntityCanFly, EntityCutomAttack {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityLum.class, DataSerializers.BYTE);

   public EntityLum(World worldIn) {
      super(worldIn);
      this.setSize(1.3F, 1.8F);
      this.moveHelper = new EntityLum.AIMoveControl(this);
      this.borderOrb = -1;
      this.tasks.removeTask(this.folow);
      this.tasks.removeTask(this.aiWander);
      this.ignoreFrustumCheck = true;
   }

   public int getParasiteIDRegister() {
      return 66;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(2, new EntityAIAttackMeleeNotGround(this, 4.0D, SRPConfig.primitiveFollow, 0.05D, false, 0));
      this.tasks.addTask(6, new EntityLum.AIMoveRandom());
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.LUM_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.LUM_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.LUM_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.LUM_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.primitiveFollow);
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateSwimmer(this, worldIn);
   }

   public void onLivingUpdate() {
      if (!this.isAIDisabled()) {
         this.liquidLeap = 0;
         super.onLivingUpdate();
         if (this.inWater) {
            this.setNoGravity(true);
         } else {
            this.setNoGravity(false);
         }

         this.liquidLeap = 0;
         if (!this.world.isRemote) {
            if (this.getAttackTarget() != null) {
               this.setParasiteStatus(1);
            }

            if (this.ticksExisted % 20 == 0) {
               ;
            }
         }

      }
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return super.attackEntityFrom(source, amount);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         ;
      }

      return flag;
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      return this.attackEntityAsMob(entityIn);
   }

   public float getEyeHeight() {
      return 1.0F;
   }

   public static void registerFixesHull(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityLum.class);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean getCanSpawnHere() {
      return true;
   }

   public boolean isNotColliding() {
      return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
   }

   public int getTalkInterval() {
      return 0;
   }

   public boolean canDespawn() {
      return true;
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 1 + this.world.rand.nextInt(3);
   }

   public void onEntityUpdate() {
      int i = this.getAir();
      super.onEntityUpdate();
      if (!this.isAIDisabled()) {
         if (this.isEntityAlive() && !this.isInWater()) {
            --i;
            this.setAir(i);
            if (this.getAir() == -20) {
               this.setAir(0);
               this.attackEntityFrom(DamageSource.DROWN, 2.0F);
            }
         } else {
            this.setAir(300);
         }

      }
   }

   public boolean isPushedByWater() {
      return false;
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public static void registerFixesLum(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityLum.class);
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
      public AIMoveControl(EntityLum vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityLum.this.posX;
            double d1 = this.posY - EntityLum.this.posY;
            double d2 = this.posZ - EntityLum.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityLum.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityLum.this.motionX *= 0.5D;
               EntityLum.this.motionY *= 0.5D;
               EntityLum.this.motionZ *= 0.5D;
            } else {
               EntityLum.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityLum.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityLum.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityLum.this.getAttackTarget() == null) {
                  EntityLum.this.rotationYaw = -((float)MathHelper.atan2(EntityLum.this.motionX, EntityLum.this.motionZ)) * 57.295776F;
                  EntityLum.this.renderYawOffset = EntityLum.this.rotationYaw;
               } else {
                  double d4 = EntityLum.this.getAttackTarget().posX - EntityLum.this.posX;
                  double d5 = EntityLum.this.getAttackTarget().posZ - EntityLum.this.posZ;
                  EntityLum.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityLum.this.renderYawOffset = EntityLum.this.rotationYaw;
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
         return EntityLum.this.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = new BlockPos(EntityLum.this);
         byte flag = 1;
         double speed = 0.15D;
         if (EntityLum.this.getAttackTarget() != null) {
            if (EntityLum.this.getDistanceSq(EntityLum.this.getAttackTarget()) > 100.0D) {
               blockpos = new BlockPos(EntityLum.this.getAttackTarget());
               flag = 2;
            } else if (EntityLum.this.getDistanceSq(EntityLum.this.getAttackTarget()) < 36.0D) {
               blockpos = new BlockPos(EntityLum.this.getAttackTarget());
               flag = 3;
            }
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityLum.this.rand.nextInt(15) - 7, EntityLum.this.rand.nextInt(11) - 5, EntityLum.this.rand.nextInt(15) - 7);
            if (flag == 2) {
               blockpos1 = blockpos.add(EntityLum.this.rand.nextInt(6) - 2, EntityLum.this.rand.nextInt(7) - 2, EntityLum.this.rand.nextInt(6) - 2);
            } else if (flag == 3) {
               blockpos1 = blockpos.add(EntityLum.this.rand.nextInt(4) + 3, EntityLum.this.rand.nextInt(5) + 4, EntityLum.this.rand.nextInt(4) + 3);
            }

            if (EntityLum.this.world.getBlockState(blockpos1).getMaterial() == Material.WATER) {
               EntityLum.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
               if (EntityLum.this.getAttackTarget() == null) {
                  EntityLum.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
