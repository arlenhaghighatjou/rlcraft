package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHostII extends EntityPMalleable implements IRangedAttackMob, EntityCutomAttack {
   private boolean open;
   private float buried;
   private int blockI;
   private int buriedC;
   private static final DataParameter<Boolean> UP = EntityDataManager.createKey(EntityHostII.class, DataSerializers.BOOLEAN);
   private float attackTimer;
   private boolean up;

   public EntityHostII(World worldIn) {
      super(worldIn);
      this.setSize(1.5F, 0.25F);
      this.borderOrb = -1;
      this.tasks.removeTask(this.folow);
      this.setPathPriority(PathNodeType.WATER, -1.0F);
      this.ignoreFrustumCheck = true;
      this.canModRender = 0;
      this.type = 41;
      this.buried = 8.3F;
      this.stepHeight = 1.0F;
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, SRPConfig.adaptedWalls, false, (Predicate)null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, SRPConfig.adaptedWalls, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
      }

      this.experienceValue = SRPAttributes.XP_ADAPTED;
      this.canD = SRPConfig.adapteddespawn;
      this.damageCap = SRPConfig.adaptedCap;
      this.foodSteal = SRPConfig.adaptedFoodSteal;
      this.pointCap = SRPConfig.adaptedPointCap;
      this.pointReduction = SRPConfig.adaptedPointRed;
      this.chanceLearn = SRPConfig.adaptedChanceLe;
      this.chanceLearnFire = SRPConfig.adaptedChanceLeFire;
      this.DamageTypeCap = SRPConfig.adaptedPointDamCap;
      this.MiniDamage = SRPConfig.adaptedMinDamage;
      this.regen = SRPConfig.adaptedRegen * SRPConfig.globalHealthMultiplier;
      this.oneMindDeathValue = SRPConfig.adaptedOneMindDeathV;
      this.regenEff = 2;
      this.adaptationCap = 0.95F;
   }

   public int getParasiteIDRegister() {
      return 75;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 1.3D, false, 0.0D, 5.0D));
      this.tasks.addTask(6, new EntityAIAttackMeleeRangeSwitch(this, 5.0F));
      this.tasks.addTask(4, new EntityAIAttackRangedStatus(this, 1.3D, 10, 16.0F));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(UP, Boolean.valueOf(false));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.HERD_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.HERD_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.12D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.HERD_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
   }

   public void onLivingUpdate() {
      if (this.isAIDisabled()) {
         this.setParasiteStatus(3);
         this.setBurrowed(true);
         this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
         this.buriedC = 80;
         if (this.buriedC > 0) {
            --this.buriedC;
         }

         ++this.blockI;
         if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.2D);
            if (this.attackTimer > 1.0F) {
               this.up = false;
            }
         } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.1D);
         }

         this.checkBurrowed();
      } else {
         super.onLivingUpdate();
         if (this.buriedC > 0) {
            --this.buriedC;
         }

         ++this.blockI;
         if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.2D);
            if (this.attackTimer > 1.0F) {
               this.up = false;
            }
         } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.1D);
         }

         this.spawnRupters();
         this.checkBurrowed();
         this.checkSpeed();
         this.teleportByGround();
         if (!this.getBurrowed() && (this.posX != this.prevPosX || this.posZ != this.prevPosZ)) {
            this.spawnGroundParticles();
         }

      }
   }

   private void spawnRupters() {
      if (SRPConfigMobs.nuuhEnabled) {
         if (!this.world.isRemote && !this.getBurrowed()) {
            if (this.getAttackTarget() != null && this.rand.nextInt(150) == 0) {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(16.0D);
               List<EntityNuuh> moblist = this.world.getEntitiesWithinAABB(EntityNuuh.class, axisalignedbb);
               if (moblist.size() <= 4) {
                  EntityNuuh out = new EntityNuuh(this.world);
                  out.copyLocationAndAnglesFrom(this);
                  this.world.spawnEntity(out);
                  out.setAttackTarget(this.getAttackTarget());
               }
            } else if (this.rand.nextInt(400) == 0) {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(16.0D);
               List<EntityNuuh> moblist = this.world.getEntitiesWithinAABB(EntityNuuh.class, axisalignedbb);
               if (moblist.size() <= 3) {
                  EntityNuuh out = new EntityNuuh(this.world);
                  out.copyLocationAndAnglesFrom(this);
                  this.world.spawnEntity(out);
               }
            }
         }

      }
   }

   private void checkBurrowed() {
      if (this.getBurrowed()) {
         if (this.height < 7.5F) {
            this.setSize(1.5F, this.height + 0.09F);
         }

         if (this.buried >= 0.0F) {
            this.buried -= 0.1F;
            this.spawnGroundParticles();
            this.open = false;
            this.world.setEntityState(this, (byte)12);
         } else {
            this.open = true;
            this.world.setEntityState(this, (byte)11);
         }
      } else {
         if (this.height > 0.25F) {
            this.setSize(1.5F, this.height - 0.09F);
         }

         if (this.buried < 8.3F) {
            this.buried += 0.1F;
            this.spawnGroundParticles();
            this.open = false;
            this.world.setEntityState(this, (byte)12);
         }
      }

   }

   private void checkSpeed() {
      if (!this.world.isRemote) {
         if (this.getAttackTarget() != null) {
            if (this.getDistanceSq(this.getAttackTarget()) <= 9.0D) {
               this.setParasiteStatus(3);
               this.setBurrowed(true);
               this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
               this.buriedC = 80;
            } else if (this.buriedC <= 0) {
               this.setParasiteStatus(0);
               this.setBurrowed(false);
               this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.12D);
            }
         } else if (this.getBurrowed() && this.buriedC <= 0) {
            this.setParasiteStatus(0);
            this.setBurrowed(false);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.12D);
         }

         if (this.blockI > 20) {
            this.blockI = 0;
            IBlockState block = this.world.getBlockState(this.getPosition().down());
            if (!(block.getBlock() instanceof IMetaName) && block.isFullBlock() && this.world.getBlockState(this.getPosition()).getBlock() == Blocks.AIR) {
               this.world.setBlockState(this.getPosition(), SRPBlocks.InfestRemain.getStateFromMeta(1));
            }
         }
      }

   }

   private void spawnGroundParticles() {
      IBlockState state = this.world.getBlockState(this.getPosition().down());
      if (state.getBlock() != Blocks.AIR) {
         int id = Block.getStateId(state);

         for(int i = 0; i < 15; ++i) {
            this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 3.0F) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 3.0F) - (double)this.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[]{id});
         }
      }

   }

   private void teleportByGround() {
      if (this.srpTicks == 10) {
         if (!this.getBurrowed() && this.rand.nextInt(2) == 0 && this.height <= 0.25F) {
            EntityLivingBase target = this.getAttackTarget();
            if (target != null && target.getDistanceSq(this) > 49.0D && ParasiteEventEntity.teleportDigging(this, 15.0F, target.getPosition(), 5, 1)) {
               this.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, 30, false, false));
            }
         }

      }
   }

   public void addVelocity(double x, double y, double z) {
      if (this.getBurrowed()) {
         super.addVelocity(x, y, z);
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.world.isRemote) {
         return false;
      } else if (source == DamageSource.IN_WALL) {
         return false;
      } else if (source == DamageSource.OUT_OF_WORLD) {
         return super.attackEntityFrom(source, amount);
      } else if (!this.getBurrowed() && this.height < 1.0F) {
         if (source == DamageSource.DROWN) {
            return false;
         } else {
            return source.getTrueSource() == null ? super.attackEntityFrom(source, amount) : false;
         }
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
      if (!this.isPotionActive(MobEffects.SLOWNESS) || this.getBurrowed()) {
         if (this.rand.nextDouble() < 0.9D && !this.getBurrowed()) {
            if (this.getNavigator().getPath() == null) {
               this.getNavigator().tryMoveToEntityLiving(target, 0.5D);
            }

         } else {
            this.buriedC = 120;
            if (this.getBurrowed() && this.height > 1.0F) {
               if (this.rand.nextInt(4) == 0 && target.getDistanceSq(this) < 225.0D && target.getDistanceSq(this) > 25.0D) {
                  double d0 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D;
                  double d1 = target.posX + target.motionX - this.posX;
                  double d2 = d0 - this.posY;
                  double d3 = target.posZ + target.motionZ - this.posZ;
                  float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
                  EntityBomb bomb = new EntityBomb(this.world, this, false);
                  bomb.setFuse(40);
                  bomb.setStren(0.0F);
                  bomb.setSkin(1);
                  bomb.setDamage((float)SRPAttributes.HERD_BOMBDAMAGE, 5);
                  bomb.rotationPitch -= -20.0F;
                  bomb.shootTwo(d1, d2 + (double)(f * 0.2F), d3, 0.75F, 8.0F);
                  this.world.spawnEntity(bomb);
                  bomb.updateSTR();
               }

               if (this.rand.nextInt(2) == 0) {
                  return;
               }

               double d1 = 4.0D;
               Vec3d vec3d = this.getLook(1.0F);
               double d2 = target.posX - (this.posX + vec3d.x);
               double d3 = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F) - (2.5D + this.posY + (double)(this.height / 1.5F));
               double d4 = target.posZ - (this.posZ + vec3d.z);
               EntityFireball entitylargefireball = new EntityProjectileSpineball(this.world, this, d2, d3, d4, SRPAttributes.EMANA_RANGED_DAMAGE);
               EntityProjectileSpineball ball = (EntityProjectileSpineball)entitylargefireball;
               ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration, SRPConfigMobs.emanaPoisonAmplifier);
               ball.setGearDamage(SRPConfigMobs.emanaGearD);
               entitylargefireball.posX = this.posX + vec3d.x;
               entitylargefireball.posY = this.posY + (double)this.getEyeHeight() - 0.2D;
               entitylargefireball.posZ = this.posZ + vec3d.z;
               this.world.spawnEntity(entitylargefireball);
            } else {
               this.setParasiteStatus(3);
               this.setBurrowed(true);
               this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
               this.getNavigator().clearPath();
            }

         }
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getBurrowed() && this.height >= 1.0F) {
         boolean flag = super.attackEntityAsMob(entityIn);
         if (flag) {
            this.buriedC += 160;
            if (this.rand.nextInt(3) == 0) {
               ;
            }

            if (entityIn instanceof EntityLivingBase && this.rand.nextDouble() < (double)SRPConfigSystems.cothAdapted) {
               EntityLivingBase target = (EntityLivingBase)entityIn;
               if (!target.isPotionActive(SRPPotions.COTH_E)) {
                  if (target.isPotionActive(SRPPotions.EPEL_E)) {
                     if (this.rand.nextDouble() >= SRPConfigSystems.epelChanceCOTH) {
                        target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                     }
                  } else {
                     target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                  }
               }
            }
         }

         return flag;
      } else {
         this.setParasiteStatus(3);
         this.setBurrowed(true);
         this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
         this.buriedC = 160;
         return false;
      }
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      this.up = true;
      this.attackTimer = 0.0F;
      this.world.setEntityState(this, (byte)13);
      boolean flag = false;
      this.playSound(SRPSounds.SWIPE, 1.0F, 1.0F);
      this.playSound(SRPSounds.SWIPE, 1.0F, 2.0F);
      this.playSound(SRPSounds.SWIPE, 2.0F, 1.0F);
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(3.0D);

      for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
         if (mob != this && !(mob instanceof EntityParasiteBase) && this.canEntityBeSeen(mob) && this.attackEntityAsMob(mob)) {
            flag = true;
         }
      }

      return flag;
   }

   public boolean isPushedByWater() {
      return super.isPushedByWater();
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityHost(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      return super.isPotionApplicable(potioneffectIn);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SKELETON_AMBIENT;
   }

   public boolean getBurrowed() {
      return ((Boolean)this.dataManager.get(UP)).booleanValue();
   }

   public void setBurrowed(boolean in) {
      this.dataManager.set(UP, Boolean.valueOf(in));
   }

   public float getEyeHeight() {
      return this.height;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }

   public static void registerFixesHost(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityHostII.class);
   }

   @SideOnly(Side.CLIENT)
   public float getBurrowTimer() {
      return this.buried;
   }

   @SideOnly(Side.CLIENT)
   public boolean getOpen() {
      return this.open;
   }

   @SideOnly(Side.CLIENT)
   public float getAttackTimer() {
      return this.attackTimer;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 11) {
         this.open = true;
      } else if (id == 12) {
         this.open = false;
      } else if (id == 13) {
         this.up = true;
         this.attackTimer = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void setSwingingArms(boolean swingingArms) {
   }
}
