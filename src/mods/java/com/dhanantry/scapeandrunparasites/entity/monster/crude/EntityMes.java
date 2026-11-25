package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPReference;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMes extends EntityPMalleable {
   public EntityMes(World worldIn) {
      super(worldIn);
      this.setSize(0.8F, 3.05F);
      this.borderOrb = -1;
      this.canModRender = 1;
      this.type = 11;
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, SRPConfig.primitiveWalls, false, (Predicate)null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
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
      this.type = 31;
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
      this.valueEvDeath = SRPConfig.primitiveLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 80;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.0D, false, 0.0D));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 1, 16));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.THRALL_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.THRALL_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.THRALL_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.THRALL_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.primitiveFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         if (entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).getName().equals(this.getCustomNameTag())) {
            entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.5D));
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
   }

   public float getEyeHeight() {
      return 1.73F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityInfPlayer(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public static void registerFixesInfHuman(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityMes.class);
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_STEP;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   protected void spawnGore() {
      int range = 2;
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
            if (block == Blocks.AIR && blockDown != Blocks.AIR && this.world.getBlockState(blockpos.down()).isFullBlock() && blockDown != SRPBlocks.InfestedStain && this.world.rand.nextInt(3) == 0) {
               this.world.setBlockState(blockpos, SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.PRI_FLAT));
            }
         }
      }

      if (this.world.getBlockState(this.getPosition().down()).isFullBlock() && (this.world.getBlockState(this.getPosition()).getBlock() instanceof BlockBush || this.world.getBlockState(this.getPosition()).getBlock() == Blocks.AIR)) {
         this.world.setBlockState(this.getPosition(), SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.PRI_BIG));
         EntityRemain nnn = new EntityRemain(this.world);
         nnn.setLocationAndAngles((double)this.getPosition().getX() + 0.5D, (double)this.getPosition().getY(), (double)this.getPosition().getZ() + 0.5D, 0.0F, 0.0F);
         nnn.setParasite(EntityList.getKey(this).toString());
         nnn.setSkin((byte)this.getSkin());
         nnn.setGoal(20 * SRPConfig.primitiveRemainValue);
         this.world.spawnEntity(nnn);
      }

      for(int i = 0; i < 4 && SRPConfig.paraGore; ++i) {
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
         bomb.setType((byte)2);
         bomb.copyLocationAndAnglesFrom(this);
         bomb.setMotion(d3, d4, d5, 0.15D, 0.55D);
         this.world.spawnEntity(bomb);
      }

   }

   @SideOnly(Side.CLIENT)
   public void spawnEffectsGore() {
      for(int i = 0; i <= 60; ++i) {
         if (i % 4 == 0) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
         }

         if (i % 5 == 0) {
            this.spawnParticles(SRPEnumParticle.GSPLASH, 2, -1, -1);
         }
      }

   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      SRPReference.setSimPlayerNameSub1(this);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(2) {
         case 2:
            this.setSkin(1);
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.THRALL_HEALTH * 0.5D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.THRALL_ATTACK_DAMAGE * 1.5D);
            this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
         }
      }

      return floo;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
   }
}
