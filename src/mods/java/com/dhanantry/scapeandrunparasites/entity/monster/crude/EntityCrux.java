package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCrux extends EntityPCrude implements EntityCutomAttack {
   private float attackTimerM;
   private boolean upM;
   private float attackTimerR;
   private boolean upR;
   private int extraDamageCap;
   private int currentDamageTimes;
   private double baseDamage;
   private byte cooldownBack;
   private boolean canBack;
   private int limit;
   private int border;
   private double targetX;
   private double targetZ;
   private double mottY;
   private boolean skillThrow;

   public EntityCrux(World worldIn) {
      super(worldIn);
      this.setSize(1.13333F, 3.3F);
      this.stepHeight = 1.0F;
      this.tasks.removeTask(this.folow);
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, !SRPConfigSystems.useOneMind, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
      }

      this.type = 41;
      this.skillThrow = false;
      this.currentDamageTimes = 0;
      this.extraDamageCap = SRPConfigMobs.cruxaDamageCap;
      this.baseDamage = -1.0D;
      this.cooldownBack = 0;
      this.canBack = true;
   }

   public int getParasiteIDRegister() {
      return 62;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.11D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(2, new EntityAISkill(this, 60, 20, 12, true, 1));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 1.3D, true, 0.0D, 3.0D));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.CRUXA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.CRUXA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.CRUXA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.CRUXA_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.upM) {
         this.attackTimerM = (float)((double)this.attackTimerM + 0.2D);
         if ((double)this.attackTimerM > 0.9D) {
            this.upM = false;
         }
      } else {
         this.attackTimerM = (float)((double)this.attackTimerM - 0.1D);
      }

      if (this.upR) {
         this.attackTimerR = (float)((double)this.attackTimerR + 0.4D);
         if ((double)this.attackTimerR > 2.2D) {
            this.upR = false;
         }
      } else {
         this.attackTimerR = (float)((double)this.attackTimerR - 0.2D);
      }

   }

   private void popBack() {
      if (this.canBack) {
         if (this.srpTicks == 10) {
            ++this.cooldownBack;
         }

         if (this.cooldownBack < 20) {
            return;
         }

         this.canBack = false;
         this.playSound(SRPSounds.MOBEXPLOTION, 3.0F, 1.0F);
         this.cooldownBack = 0;
         if (this.world.isRemote) {
            for(int i = 0; i <= 180; ++i) {
               if (i % 5 == 0) {
                  this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1, 5.0D, 9.0D);
               }
            }

            for(int i = 0; i <= 80; ++i) {
               if (i % 5 == 0) {
                  this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
               }

               if (i % 5 == 0) {
                  this.spawnParticles(SRPEnumParticle.GSPLASH, 0, -1, -1);
               }
            }

            return;
         }

         int range = 4;
         double i1 = (double)MathHelper.floor(this.posY + 0.1D);
         double l1 = this.posX;
         double i2 = this.posZ;

         for(int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
            for(int l2 = -1 * range; l2 <= 1 * range; ++l2) {
               double i3 = l1 + (double)k2;
               double l = i2 + (double)l2;
               BlockPos blockpos = new BlockPos(i3, i1, l);
               Block block = this.world.getBlockState(blockpos).getBlock();
               Block blockDown = this.world.getBlockState(blockpos.down()).getBlock();
               if (block == Blocks.AIR && blockDown != Blocks.AIR && this.world.getBlockState(blockpos.down()).isFullBlock() && blockDown != SRPBlocks.InfestedStain && this.world.rand.nextInt(4) == 0) {
                  this.world.setBlockState(blockpos, SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.SIM_FLAT));
               }
            }
         }

         for(int i = 0; i < 7 && SRPConfig.paraGore; ++i) {
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
            d3 = d3 * d7 * 7.0D;
            d4 = d4 * d7 * 12.0D;
            d5 = d5 * d7 * 7.0D;
            EntityGore bomb = new EntityGore(this.world);
            bomb.setType((byte)1);
            bomb.copyLocationAndAnglesFrom(this);
            bomb.setMotion(d3, d4, d5, 0.28D, 0.55D);
            bomb.posY += 3.5D;
            this.world.spawnEntity(bomb);
         }
      } else {
         if (this.srpTicks == 10) {
            ++this.cooldownBack;
         }

         if (this.cooldownBack > 15) {
            this.cooldownBack = 0;
            this.canBack = true;
         }
      }

   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      if (this.extraDamageCap <= this.currentDamageTimes) {
         ++this.currentDamageTimes;
         if (this.baseDamage == -1.0D) {
            this.baseDamage = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue() * SRPConfigMobs.cruxaDamageGain;
         }

         double getD = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
         this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getD + this.baseDamage);
      }
   }

   public float getEyeHeight() {
      return 3.3F;
   }

   @SideOnly(Side.CLIENT)
   public boolean getBack() {
      return this.canBack;
   }

   public static void registerFixesShyco(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityCrux.class);
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SRPSounds.MONSTER_STEP, 0.15F, 1.0F);
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      this.upM = true;
      this.attackTimerM = 0.0F;
      this.world.setEntityState(this, (byte)22);
      boolean flag = false;
      this.playSound(SRPSounds.SWIPE, 2.0F, 1.0F);
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0D, entityIn.posY + 1.0D, entityIn.posZ + 1.0D)).grow(1.0D);

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

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (SRPConfigSystems.rageEnable) {
         this.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 1333320, 0, false, false));
      }

      return floo;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setDouble("parasitecruxdamage", this.baseDamage);
      compound.setInteger("parasitecruxtimes", this.currentDamageTimes);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("parasitecruxdamage", 99)) {
         this.baseDamage = compound.getDouble("parasitecruxdamage");
      }

      if (compound.hasKey("parasitecruxtimes", 99)) {
         this.currentDamageTimes = compound.getInteger("parasitecruxtimes");
      }

   }

   @SideOnly(Side.CLIENT)
   public float getAttackTimerM() {
      return this.attackTimerM;
   }

   @SideOnly(Side.CLIENT)
   public float getAttackTimerR() {
      return this.attackTimerR;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 22) {
         this.upM = true;
         this.attackTimerM = 0.0F;
      } else if (id == 23) {
         this.upR = true;
         this.attackTimerR = 0.0F;
      } else if (id == 24) {
         this.canBack = true;
      } else if (id == 25) {
         this.canBack = false;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 1:
         return this.skillThrow;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillThrow = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.throwBlock();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void throwBlock() {
      EntityLivingBase entitylivingbase = this.getAttackTarget();
      if (entitylivingbase == null) {
         this.skillThrow = true;
         this.setParasiteStatus(0);
         this.border = 0;
         this.limit = 0;
      } else {
         this.targetX = entitylivingbase.posX;
         this.targetZ = entitylivingbase.posZ;
         if (this.getDistanceSq(entitylivingbase) >= 144.0D && entitylivingbase.posY >= this.posY && entitylivingbase.posY <= this.posY + 3.0D) {
            this.mottY = this.getDistanceSq(entitylivingbase);
            if (this.ticksExisted % 20 == 0) {
               ++this.border;
               IBlockState b = this.blockNotAvailable();
               if (b == null) {
                  ++this.limit;
                  if (this.limit >= 5) {
                     this.skillThrow = true;
                     this.setParasiteStatus(0);
                     this.border = 0;
                     this.limit = 0;
                  }

               } else {
                  this.skillBreakBlocks();
                  this.setParasiteStatus(10);
                  this.getNavigator().tryMoveToXYZ(this.targetX, entitylivingbase.posY, this.targetZ, 0.0D);
                  this.world.setEntityState(this, (byte)23);
                  BlockPos pos = this.getPosition();
                  Vec3d vec3d = this.getLook(1.0F);
                  EntityFallingBlock entityfallingblock = new EntityFallingBlock(this.world, this.posX + vec3d.x, this.posY + (double)this.getEyeHeight() - 0.7D, this.posZ + vec3d.z, b);
                  entityfallingblock.fallTime = -100;
                  this.targetX += Math.random() * 2.0D - 1.0D;
                  this.targetZ += Math.random() * 2.0D - 1.0D;
                  double d0 = this.targetX - this.posX;
                  double d1 = this.targetZ - this.posZ;
                  double f = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
                  entityfallingblock.motionY = this.mottY * 0.0D;
                  if (entitylivingbase.posY >= this.posY + 2.0D) {
                     entityfallingblock.motionY += 0.5D;
                  }

                  if (entitylivingbase.posY <= this.posY - 2.0D) {
                     entityfallingblock.motionY += -0.5D;
                  }

                  double mmx = d0 / f * this.mottY * 0.009D * 0.9D + entityfallingblock.motionX * 0.3D;
                  double mmz = d1 / f * this.mottY * 0.009D * 0.9D + entityfallingblock.motionZ * 0.3D;
                  entityfallingblock.motionX += mmx;
                  entityfallingblock.motionZ += mmz;
                  EntityDamage damage = new EntityDamage(this.world, entityfallingblock.posX, entityfallingblock.posY, entityfallingblock.posZ, 0.0F, this, 10.0F + b.getBlockHardness(this.world, this.getPosition()), false, 0.0F);
                  damage.setFollower(entityfallingblock);
                  this.world.spawnEntity(entityfallingblock);
                  this.world.spawnEntity(damage);
                  if (this.limit >= 5 || this.border > 8) {
                     this.skillThrow = true;
                     this.setParasiteStatus(0);
                     this.border = 0;
                     this.limit = 0;
                  }

               }
            }
         } else {
            this.skillThrow = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
         }
      }
   }

   private IBlockState blockNotAvailable() {
      IBlockState b = null;
      int range = 2;
      int newX = this.rand.nextInt(range) + 1;
      int newZ = this.rand.nextInt(range) + 1;
      if (this.rand.nextBoolean()) {
         newX *= -1;
      }

      if (this.rand.nextBoolean()) {
         newZ *= -1;
      }

      BlockPos pos = ParasiteEventEntity.getFloor(this.world, new BlockPos(this.posX + (double)newX, this.posY, this.posZ + (double)newZ), 4);
      if (pos != null) {
         pos = pos.down();
         if (this.checkBlock(this.world.getBlockState(pos).getBlock(), this.world.getBlockState(pos), pos)) {
            return null;
         }

         b = this.world.getBlockState(pos);
         this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
      }

      return b;
   }

   private boolean checkBlock(Block in, IBlockState state, BlockPos pos) {
      return in == Blocks.AIR || in instanceof BlockLiquid || !state.isFullBlock() || state.getBlockHardness(this.world, pos) <= 0.0F || this.blockException(in.getRegistryName().toString());
   }
}
