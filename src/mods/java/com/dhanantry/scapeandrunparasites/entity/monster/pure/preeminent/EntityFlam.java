package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.EntityScaryOrb;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFlam extends EntityPPreeminent {
   private EntityPPreeminent father;
   private BlockPos targetP;
   private int activationF;
   private boolean moveM;
   private byte typeDead;
   private byte steadyD;
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityFlam.class, DataSerializers.BYTE);

   public EntityFlam(World worldIn) {
      super(worldIn);
      this.moveHelper = new EntityFlam.AIMoveControl(this);
      this.setSize(1.2F, 1.2F);
      this.setNoGravity(true);
      this.tasks.removeTask(this.folow);
      this.borderOrb = -1;
      this.canModRender = 1;
      this.experienceValue = SRPAttributes.XP_LiTTLE;
      if (SRPConfigMobs.emanaMaxY != 256) {
         this.tasks.addTask(3, new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
      }

   }

   public int getParasiteIDRegister() {
      return 89;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(6, new EntityFlam.AIMoveRandom());
      this.tasks.addTask(4, new EntityFlam.AIChargeAttack());
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.EMANA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.EMANA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.EMANA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.EMANA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.preeminentFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.isAIDisabled()) {
         if (!this.world.isRemote) {
            if (this.moveM) {
               ++this.activationF;
            }

            if (this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
               ++this.steadyD;
            }

            if (this.steadyD > 20) {
               this.DeadDead();
               return;
            }

            if (this.targetP == null || this.father == null) {
               this.activationF += 5000;
               this.DeadDead();
               return;
            }

            if (this.ticksExisted % 20 == 0 && this.blockH != 0.0F) {
               this.skillBreakBlocks();
            }

            if (this.getDistanceSq(this.targetP) < 4.0D || this.moveM) {
               this.DeadDead();
               return;
            }

            if (this.ticksExisted > 400) {
               this.activationF += 5000;
               this.DeadDead();
               return;
            }
         }

      }
   }

   protected void selfExplode() {
      if (this.world.isRemote) {
         this.spawnEffectsGore();
      }

      if (!this.world.isRemote) {
         this.spawnGore();
         this.playSound(SRPSounds.MOBEXPLOTION, 1.0F, 1.0F);
         this.dead = true;
         this.setDead();
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY - 2.0D, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(4.0D);

         for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (!(mob instanceof EntityParasiteBase)) {
               this.attackEntityAsMob(mob);
            }
         }

         EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.world, this.posX, this.posY - 2.0D, this.posZ);
         entityareaeffectcloud.setRadius(this.width * 3.0F, this.height * 2.0F);
         entityareaeffectcloud.setWaitTime(10);
         entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.POISON, 300, 2));
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.WITHER, 300, 2));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
         this.world.spawnEntity(entityareaeffectcloud);
      }

   }

   public void DeadDead() {
      ++this.activationF;
      this.moveHelper.setMoveTo(this.posX, this.posY, this.posZ, 0.0D);
      this.moveM = true;
      if (this.activationF >= 20) {
         switch(this.typeDead) {
         case 1:
            this.selfExplode();
            break;
         case 2:
            Entity ttt = new EntityScaryOrb(this.world, this.father, this.fuseOrb, this.orbStartTimer, false);
            ttt.copyLocationAndAnglesFrom(this);
            ttt.posY -= 3.0D;
            this.world.spawnEntity(ttt);
            this.playSound(SRPSounds.ORB_S, 1.0F, 1.0F);
            break;
         case 3:
            if (this.targetP != null) {
               if (this.getDistanceSq(this.targetP) < 16.0D) {
                  this.father.copyLocationAndAnglesFrom(this);
                  this.father.setTeleFlam(false);
               } else {
                  switch(this.rand.nextInt(2)) {
                  case 0:
                     this.selfExplode();
                     break;
                  case 1:
                     Entity ttt1 = new EntityScaryOrb(this.world, this.father, this.fuseOrb, this.orbStartTimer, false);
                     ttt1.copyLocationAndAnglesFrom(this);
                     ttt1.posY -= 3.0D;
                     this.world.spawnEntity(ttt1);
                     this.playSound(SRPSounds.ORB_S, 1.0F, 1.0F);
                  }
               }
            } else {
               switch(this.rand.nextInt(2)) {
               case 0:
                  this.selfExplode();
                  break;
               case 1:
                  Entity ttt1 = new EntityScaryOrb(this.world, this.father, this.fuseOrb, this.orbStartTimer, false);
                  ttt1.copyLocationAndAnglesFrom(this);
                  ttt1.posY -= 2.0D;
                  this.world.spawnEntity(ttt1);
                  this.playSound(SRPSounds.ORB_S, 1.0F, 1.0F);
               }
            }
         }

         this.spawnCyst();
         this.setDead();
      }
   }

   public void setFatherTo(EntityPPreeminent in, int typeIn) {
      this.father = in;
      this.typeDead = (byte)typeIn;
   }

   public void onUpdate() {
      super.onUpdate();
      this.setNoGravity(true);
   }

   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      super.setAttackTarget(entitylivingbaseIn);
      if (entitylivingbaseIn != null) {
         this.targetP = entitylivingbaseIn.getPosition();
      }

   }

   protected void summonFlam(EntityLivingBase in) {
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 0.5F;
   }

   public void move(MoverType type, double x, double y, double z) {
      if (!this.moveM) {
         super.move(type, x, y, z);
      }

      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public static void registerFixesEmana(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityFlam.class);
   }

   protected void spawnGore() {
      double i1 = (double)MathHelper.floor(this.posY + 0.1D);
      double l1 = this.posX;
      double i2 = this.posZ;

      for(int i = 0; i < 10 && SRPConfig.paraGore; ++i) {
         double d0 = (double)((float)this.posX + this.world.rand.nextFloat());
         double d1 = (double)((float)this.posY + this.world.rand.nextFloat());
         double d2 = (double)((float)this.posZ + this.world.rand.nextFloat());
         double d3 = d0 - this.posX;
         double d4 = d1 - this.posY;
         double d5 = d2 - this.posZ;
         double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
         d3 = d3 / d6;
         d4 = d4 / d6;
         d5 = d5 / d6;
         double d7 = 0.5D / (d6 / 4.0D + 0.1D);
         d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
         d3 = d3 * d7;
         d4 = d4 * d7 * 2.0D;
         d5 = d5 * d7;
         EntityGore bomb = new EntityGore(this.world);
         bomb.setType((byte)12);
         bomb.copyLocationAndAnglesFrom(this);
         bomb.setMotion(d3, d4, d5, 0.25D, 0.65D);
         this.world.spawnEntity(bomb);
      }

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

   public void skillBreakBlocks() {
      if (!this.isDead) {
         if (this.blockH != 0.0F) {
            int blocksbroke = 0;
            int i1 = MathHelper.floor(this.posY + 0.1D);
            double l1 = this.posX;
            double i2 = this.posZ;
            boolean flag = false;
            int Brangeatm = this.BGrange;
            int offsetT = 0;

            for(int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
               for(int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
                  for(int j = -1; j <= this.BGheight + 1; ++j) {
                     double i3 = l1 + (double)k2;
                     double k = (double)(i1 + j);
                     double l = i2 + (double)l2;
                     BlockPos blockpos = new BlockPos(i3, k, l);
                     IBlockState iblockstate = this.world.getBlockState(blockpos);
                     Block block = iblockstate.getBlock();
                     float bHard = iblockstate.getBlockHardness(this.world, blockpos);
                     if (bHard <= this.blockH && bHard > 0.0F && (!(block instanceof IMetaName) || block == SRPBlocks.ParasiteCanister) && block != SRPBlocks.BiomeHeart && block != SRPBlocks.ColonyHeart && block != SRPBlocks.ParasiteRubbleDense && block != SRPBlocks.ParasiteCanisterActive) {
                        String name = block.getRegistryName().toString();
                        if (!this.blockException(name) && block != Blocks.AIR && block.canEntityDestroy(iblockstate, this.world, blockpos, this) && ForgeEventFactory.onEntityDestroyBlock(this, blockpos, iblockstate)) {
                           if (SRPConfig.cystActive) {
                              int meta = block.getMetaFromState(iblockstate);
                              flag = this.destroyBlockPos(blockpos, false) || flag;
                              if (SRPConfig.doTileDrops) {
                                 this.addToBlockInv(name + ";" + meta);
                              }
                           } else {
                              this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                           }

                           ++blocksbroke;
                        }
                     }
                  }
               }
            }

            this.BGrange = Brangeatm;
            this.SkillBGflag = true;
         }
      }
   }

   class AIChargeAttack extends EntityAIBase {
      public AIChargeAttack() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntityFlam.this.targetP != null && !EntityFlam.this.getMoveHelper().isUpdating() && EntityFlam.this.rand.nextInt(7) == 0 && !EntityFlam.this.moveM) {
            return EntityFlam.this.getDistanceSq(EntityFlam.this.targetP) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return EntityFlam.this.isCharging() && EntityFlam.this.targetP != null && !EntityFlam.this.moveM;
      }

      public void startExecuting() {
         if (EntityFlam.this.getAttackTarget() != null && EntityFlam.this.getAttackTarget().isEntityAlive()) {
            EntityFlam.this.targetP = EntityFlam.this.getAttackTarget().getPosition();
         }

         EntityFlam.this.moveHelper.setMoveTo((double)EntityFlam.this.targetP.getX() + 0.5D, (double)(EntityFlam.this.targetP.getY() + 2), (double)EntityFlam.this.targetP.getZ() + 0.5D, 0.8D);
         EntityFlam.this.setCharging(true);
      }

      public void resetTask() {
         EntityFlam.this.setCharging(false);
      }

      public void updateTask() {
         if (EntityFlam.this.getAttackTarget() != null && EntityFlam.this.getAttackTarget().isEntityAlive()) {
            EntityFlam.this.targetP = EntityFlam.this.getAttackTarget().getPosition();
         }

         if (EntityFlam.this.targetP != null) {
            double d0 = EntityFlam.this.getDistanceSq(EntityFlam.this.targetP);
            if (d0 < 9.0D) {
               EntityFlam.this.moveHelper.setMoveTo((double)EntityFlam.this.targetP.getX() + 0.5D, (double)(EntityFlam.this.targetP.getY() + 2), (double)EntityFlam.this.targetP.getZ() + 0.5D, 0.8D);
            }
         }

      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityFlam vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO && !EntityFlam.this.moveM) {
            double d0 = this.posX - EntityFlam.this.posX;
            double d1 = this.posY - EntityFlam.this.posY;
            double d2 = this.posZ - EntityFlam.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityFlam.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityFlam.this.motionX *= 0.5D;
               EntityFlam.this.motionY *= 0.5D;
               EntityFlam.this.motionZ *= 0.5D;
            } else {
               EntityFlam.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityFlam.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityFlam.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityFlam.this.getAttackTarget() == null) {
                  EntityFlam.this.rotationYaw = -((float)MathHelper.atan2(EntityFlam.this.motionX, EntityFlam.this.motionZ)) * 57.295776F;
                  EntityFlam.this.renderYawOffset = EntityFlam.this.rotationYaw;
               } else {
                  double d4 = EntityFlam.this.getAttackTarget().posX - EntityFlam.this.posX;
                  double d5 = EntityFlam.this.getAttackTarget().posZ - EntityFlam.this.posZ;
                  EntityFlam.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityFlam.this.renderYawOffset = EntityFlam.this.rotationYaw;
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
         return !EntityFlam.this.getMoveHelper().isUpdating() && EntityFlam.this.rand.nextInt(7) == 0 && !EntityFlam.this.moveM;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = new BlockPos(EntityFlam.this);
         byte flag = 1;
         double speed = 1.0D;
         if (EntityFlam.this.getAttackTarget() != null && EntityFlam.this.getAttackTarget().isEntityAlive()) {
            EntityFlam.this.targetP = EntityFlam.this.getAttackTarget().getPosition();
         }

         if (EntityFlam.this.targetP != null) {
            blockpos = EntityFlam.this.targetP;
            flag = 2;
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityFlam.this.rand.nextInt(15) - 7, EntityFlam.this.rand.nextInt(11) - 5, EntityFlam.this.rand.nextInt(15) - 7);
            if (flag == 2) {
               blockpos1 = blockpos.add(EntityFlam.this.rand.nextInt(6) - 2, EntityFlam.this.rand.nextInt(7) - 2, EntityFlam.this.rand.nextInt(6) - 2);
            } else if (flag == 3) {
               blockpos1 = blockpos.add(EntityFlam.this.rand.nextInt(4) + 3, EntityFlam.this.rand.nextInt(5) + 4, EntityFlam.this.rand.nextInt(4) + 3);
            }

            if (EntityFlam.this.world.isAirBlock(blockpos1)) {
               EntityFlam.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
               if (EntityFlam.this.getAttackTarget() == null) {
                  EntityFlam.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
