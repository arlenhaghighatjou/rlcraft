package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHost extends EntityPMalleable implements IRangedAttackMob, EntityCutomAttack {
   private boolean open;
   private float buried;
   private int blockI;
   private int buriedC;
   private static final DataParameter<Boolean> UP = EntityDataManager.createKey(EntityHost.class, DataSerializers.BOOLEAN);
   private float attackTimer;
   private boolean up;

   public EntityHost(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 0.25F);
      this.borderOrb = -1;
      this.tasks.removeTask(this.folow);
      this.setPathPriority(PathNodeType.WATER, -1.0F);
      this.canModRender = 0;
      this.type = 11;
      this.buried = 4.8F;
      this.stepHeight = 1.0F;
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, SRPConfig.primitiveWalls, false, (Predicate)null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, SRPConfig.primitiveWalls, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
      }

      this.experienceValue = SRPAttributes.XP_PRIMITIVE;
      this.damageCap = SRPConfig.primitiveCap;
      this.canD = SRPConfig.primitivedespawn;
      this.foodSteal = SRPConfig.primitiveFoodSteal;
      this.pointCap = SRPConfig.primitivePointCap;
      this.pointReduction = SRPConfig.primitivePointRed;
      this.chanceLearn = SRPConfig.primitiveChanceLe;
      this.chanceLearnFire = SRPConfig.primitiveChanceLeFire;
      this.DamageTypeCap = SRPConfig.primitivePointDamCap;
      this.MiniDamage = SRPConfig.primitiveMinDamage;
      this.regen = SRPConfig.primitiveRegen * SRPConfig.globalHealthMultiplier;
      this.oneMindDeathValue = SRPConfig.primitiveOneMindDeathV;
      this.regenEff = 1;
      this.adaptationCap = 0.95F;
      this.type = 11;
   }

   public int getParasiteIDRegister() {
      return 48;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 1.3D, false, 0.0D, 3.0D));
      this.tasks.addTask(6, new EntityAIAttackMeleeRangeSwitch(this, 8.0F));
      this.tasks.addTask(4, new EntityAIAttackRangedStatus(this, 1.3D, 50, 8.0F));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(UP, Boolean.valueOf(false));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.HOST_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.HOST_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.12D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.HOST_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.infectedFollow);
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

         if (!this.world.isRemote && this.srpTicks == 10 && this.killcount > (double)SRPConfigMobs.hostHerd && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityHostII(this.world), true, true);
         }

      }
   }

   private void spawnRupters() {
      if (SRPConfigMobs.mudoEnabled) {
         if (!this.world.isRemote && !this.getBurrowed()) {
            if (this.getAttackTarget() != null && this.rand.nextInt(150) == 0) {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(16.0D);
               List<EntityMudo> moblist = this.world.getEntitiesWithinAABB(EntityMudo.class, axisalignedbb);
               if (moblist.size() <= 4) {
                  EntityMudo out = new EntityMudo(this.world);
                  out.copyLocationAndAnglesFrom(this);
                  this.world.spawnEntity(out);
                  out.setAttackTarget(this.getAttackTarget());
               }
            } else if (this.rand.nextInt(400) == 0) {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(16.0D);
               List<EntityMudo> moblist = this.world.getEntitiesWithinAABB(EntityMudo.class, axisalignedbb);
               if (moblist.size() <= 3) {
                  EntityMudo out = new EntityMudo(this.world);
                  out.copyLocationAndAnglesFrom(this);
                  this.world.spawnEntity(out);
               }
            }
         }

      }
   }

   private void checkBurrowed() {
      if (this.getBurrowed()) {
         if (this.height < 3.5F) {
            this.setSize(0.9F, this.height + 0.05F);
         }

         if (this.buried >= 0.0F) {
            this.buried -= 0.08F;
            this.spawnGroundParticles();
            this.open = false;
            this.world.setEntityState(this, (byte)12);
         } else {
            this.open = true;
            this.world.setEntityState(this, (byte)11);
         }
      } else {
         if (this.height > 0.25F) {
            this.setSize(0.9F, this.height - 0.05F);
         }

         if (this.buried < 4.8F) {
            this.buried += 0.08F;
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
         if (!this.getBurrowed() && this.rand.nextInt(3) == 0 && this.height <= 0.25F) {
            EntityLivingBase target = this.getAttackTarget();
            if (target != null && target.getDistanceSq(this) > 49.0D && ParasiteEventEntity.teleportDigging(this, 10.0F, target.getPosition(), 4, 1)) {
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
         if (target.getDistanceSq(this) <= 169.0D) {
            this.buriedC = 80;
            if (this.getBurrowed() && this.height > 1.0F) {
               double d0 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D;
               double d1 = target.posX + target.motionX - this.posX;
               double d2 = d0 - this.posY;
               double d3 = target.posZ + target.motionZ - this.posZ;
               float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
               EntityBomb bomb = new EntityBomb(this.world, this, false);
               bomb.setFuse(80);
               bomb.setStren(0.0F);
               bomb.setSkin(1);
               bomb.setDamage((float)SRPAttributes.HOST_BOMBDAMAGE, 4);
               bomb.rotationPitch -= -20.0F;
               bomb.shootTwo(d1, d2 + (double)(f * 0.2F), d3, 0.75F, 8.0F);
               this.world.spawnEntity(bomb);
               bomb.updateSTR();
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

            if (entityIn instanceof EntityLivingBase && this.rand.nextDouble() < (double)SRPConfigSystems.cothPrimitive) {
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

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      return super.isPotionApplicable(potioneffectIn);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      this.particleStatus((byte)5);
      if (!this.world.isRemote && this.killcount > (double)SRPConfigMobs.hostHerd && !this.isDead && ParasiteEventEntity.canSpawnNext) {
         ParasiteEventEntity.spawnNext(this, new EntityHostII(this.world), true, true);
      }

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

   public static void registerFixesHost(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityHost.class);
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
