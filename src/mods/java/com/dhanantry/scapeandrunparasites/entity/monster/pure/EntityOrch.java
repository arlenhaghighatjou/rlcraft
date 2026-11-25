package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileWebball;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityOrch extends EntityPPure implements EntityCutomAttack, EntityCanShoot {
   private float attackTimer;
   private boolean up;
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityOrch.class, DataSerializers.BYTE);

   public EntityOrch(World worldIn) {
      super(worldIn);
      this.setSize(1.901F, 4.1F);
      this.stepHeight = 1.0F;
      this.ignoreFrustumCheck = true;
   }

   public int getParasiteIDRegister() {
      return 84;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISkill(this, 40, 100, 10, true, 14));
      this.setskillLeapValues(0.5F, 3.5D, 4);
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.12D));
      this.tasks.addTask(6, new EntityAIAttackProjectile(this, 40, 15, 4));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 7));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 1.3D, false, 8.0D, 2.0D));
      this.tasks.addTask(2, new EntityAIEvadeDash(this, 17, 2, 5, 3.5D, 15));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ORCH_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ORCH_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2775D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ORCH_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.ORCH_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.pureFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.up) {
         this.attackTimer = (float)((double)this.attackTimer + 0.2D);
         if (this.attackTimer > 1.0F) {
            this.up = false;
         }
      } else {
         this.attackTimer = (float)((double)this.attackTimer - 0.1D);
      }

      if (!this.world.isRemote) {
         this.setBesideClimbableBlock(this.collidedHorizontally);
      }

   }

   protected boolean spawnT(EntityLivingBase in, int range2, int mini2, int check, int type) {
      if (this.canEntityBeSeen(in)) {
         return false;
      } else {
         return this.rand.nextInt(4) == 0 ? super.spawnT(in, range2, mini2, check, type) : super.spawnT(in, range2, mini2, check, 2);
      }
   }

   public void setInWeb() {
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(CLIMBING, Byte.valueOf((byte)0));
   }

   public boolean isBesideClimbableBlock() {
      return (((Byte)this.dataManager.get(CLIMBING)).byteValue() & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = ((Byte)this.dataManager.get(CLIMBING)).byteValue();
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.dataManager.set(CLIMBING, Byte.valueOf(b0));
   }

   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      super.setAttackTarget(entitylivingbaseIn);
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateClimberStatus(this, worldIn);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         entityIn.motionY += 0.5000000059604645D;
      }

      return flag;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 3.5F;
   }

   public static void registerFixesLodo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityOrch.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.MOBSILENCE;
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
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.orchOrbEffects, mobs);
      }

      return flag;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SRPSounds.MOBSILENCE, 0.15F, 1.0F);
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      if (this.borderOrb != 0) {
         return false;
      } else {
         this.up = true;
         this.attackTimer = 0.0F;
         this.world.setEntityState(this, (byte)12);
         boolean flag = false;
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0D, entityIn.posY + 1.0D, entityIn.posZ + 1.0D)).grow(2.0D);

         for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (mob instanceof EntityParasiteBase) {
               if (this.getAttackTarget() == mob) {
                  this.setAttackTarget((EntityLivingBase)null);
                  return false;
               }
            } else if (mob != this && this.canEntityBeSeen(mob) && this.attackEntityAsMob(mob)) {
               flag = true;
            }
         }

         return flag;
      }
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(this.rand.nextInt(1)) {
         case 0:
            this.setSkin(1);
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ORCH_HEALTH * 0.5D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ORCH_ATTACK_DAMAGE * 1.5D);
            this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
         }
      }

      return super.onInitialSpawn(difficulty, livingdata);
   }

   @SideOnly(Side.CLIENT)
   public float getAttackTimer() {
      return this.attackTimer;
   }

   protected void skillLeap() {
      if (this.leapMotionY != 0.0F) {
         if (this.getAttackTarget() != null && this.shouldWorkTask() && !this.isPotionActive(MobEffects.SLOWNESS) && this.getParasiteStatus() <= 2) {
            EntityLivingBase entitylivingbase = this.getAttackTarget();
            if (this.attacking == 0) {
               ++this.attacking;
               this.targetX = entitylivingbase.posX;
               this.targetZ = entitylivingbase.posZ;
            }
         }

         if (this.attacking >= 1) {
            ++this.attacking;
            this.skillBreakBlocks();
            if (this.attacking % 5 == 0 && this.attacking < 40 && SRPConfigMobs.lodoEnabled) {
               EntityLodo b = new EntityLodo(this.world);
               b.copyLocationAndAnglesFrom(this);
               this.world.spawnEntity(b);
            }

            if (this.attacking == 2 && this.onGround) {
               this.setParasiteStatus(10);
               this.getNavigator().clearPath();
               double d0 = this.targetX - this.posX;
               double d1 = this.targetZ - this.posZ;
               double f = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
               this.motionY = (double)this.leapMotionY;
               this.motionX += d0 / f * this.jumpSpeed * 0.9D + this.motionX * 0.3D;
               this.motionZ += d1 / f * this.jumpSpeed * 0.9D + this.motionZ * 0.3D;
            }

            if (this.attacking > 2 && this.onGround) {
               if (this.jumpR != 0) {
                  float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow((double)this.jumpR, 2.0D, (double)this.jumpR);

                  for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
                     if (mob != this && !(mob instanceof EntityParasiteBase)) {
                        mob.knockBack(mob, 2.5F, this.posX - mob.posX, this.posZ - mob.posZ);
                        this.attackEntityAsMob(mob);
                     }
                  }
               }

               this.setParasiteStatus(0);
               this.attacking = 0;
               if (this.type >= 31 && this.height > 2.0F) {
                  this.playSound(SRPSounds.HITGROUND, 15.0F, 1.0F);
               }

               this.SkillLeapFlag = true;
            }
         }

      }
   }

   public void skillBreakBlocks() {
      if (this.blockH != 0.0F) {
         int blocksbroke = 0;
         int i1 = MathHelper.floor(this.posY + 0.1D);
         double l1 = this.posX;
         double i2 = this.posZ;
         boolean flag = false;
         int Brangeatm = this.BGrange;
         int offsetT = 0;
         if (this.getAttackTarget() != null) {
            EntityLivingBase target = this.getAttackTarget();
            if (target.getDistanceSq(this.posX, target.posY, this.posZ) < 9.0D) {
               if (target.posY - this.posY < -1.0D) {
                  offsetT -= 2;
               } else if (target.posY - this.posY > 2.0D) {
                  ++offsetT;
                  this.BGrange = 0;
               }
            }
         }

         for(int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for(int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
               for(int j = 1 + offsetT; j <= this.BGheight + offsetT; ++j) {
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

   public EntityFireball getProj(double accelX, double accelY, double accelZ) {
      this.playSound(SRPSounds.DORPA_RANGE, 2.0F, 1.0F);
      EntityLivingBase entitylivingbase = this.getAttackTarget();
      accelY = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 3.0F) - (1.0D + this.posY + (double)(this.height / 2.0F));
      return new EntityProjectileWebball(this.world, this, accelX, accelY, accelZ, (byte)2);
   }

   public void playProjSound() {
   }
}
