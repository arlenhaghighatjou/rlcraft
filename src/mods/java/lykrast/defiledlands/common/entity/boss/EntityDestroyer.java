package lykrast.defiledlands.common.entity.boss;

import javax.annotation.Nullable;
import lykrast.defiledlands.common.entity.IEntityDefiled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityDestroyer extends EntityMob implements IEntityDefiled {
   public static final ResourceLocation LOOT = new ResourceLocation("defiledlands", "entities/the_destroyer");
   private static final DataParameter<Integer> INVULNERABILITY_TIME = EntityDataManager.createKey(EntityDestroyer.class, DataSerializers.VARINT);
   protected static final DataParameter<Boolean> LEAPING = EntityDataManager.createKey(EntityDestroyer.class, DataSerializers.BOOLEAN);
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.PURPLE, Overlay.PROGRESS)).setDarkenSky(true);

   public EntityDestroyer(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 2.4F);
      this.stepHeight = 1.0F;
      this.experienceValue = 50;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityDestroyer.AIDoNothing());
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityDestroyer.AIBigLeap(this, 1.2F));
      this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(128.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(INVULNERABILITY_TIME, Integer.valueOf(0));
      this.dataManager.register(LEAPING, Boolean.valueOf(false));
   }

   public boolean isLeaping() {
      return ((Boolean)this.dataManager.get(LEAPING)).booleanValue();
   }

   public void setLeaping(boolean val) {
      this.dataManager.set(LEAPING, Boolean.valueOf(val));
   }

   public void fall(float distance, float damageMultiplier) {
      if (this.isLeaping()) {
         this.setLeaping(false);
         if (!this.world.isRemote) {
            boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F, flag);
         }
      }

   }

   public int getMaxFallHeight() {
      return Integer.MAX_VALUE;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (super.attackEntityAsMob(entityIn)) {
         entityIn.motionY += 0.4000000059604645D;
         return true;
      } else {
         return false;
      }
   }

   public void onLivingUpdate() {
      this.isJumping = false;
      super.onLivingUpdate();
      if (this.getInvulTime() > 0) {
         this.motionY = 0.01D;

         for(int i1 = 0; i1 < 3; ++i1) {
            this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + this.rand.nextGaussian(), this.posY + (double)(this.rand.nextFloat() * 3.3F), this.posZ + this.rand.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D, new int[0]);
         }
      }

      if (this.isLeaping() && this.isInWater()) {
         this.setLeaping(false);
      }

   }

   protected void updateAITasks() {
      if (this.getInvulTime() > 0) {
         int j1 = this.getInvulTime() - 1;
         if (j1 <= 0) {
            this.world.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
            this.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 10.0F, 1.0F);
         }

         this.setInvulTime(j1);
      } else {
         super.updateAITasks();
         this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
      }

   }

   public void ignite() {
      this.setInvulTime(200);
   }

   public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
      float f = super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn);
      Block block = blockStateIn.getBlock();
      if (EntityWither.canDestroyBlock(block) && ForgeEventFactory.onEntityDestroyBlock(this, pos, blockStateIn)) {
         f = Math.min(2.0F, f);
      }

      return f;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else if (source != DamageSource.DROWN && !(source.getTrueSource() instanceof EntityDestroyer)) {
         return this.getInvulTime() > 0 && source != DamageSource.OUT_OF_WORLD ? false : super.attackEntityFrom(source, amount);
      } else {
         return false;
      }
   }

   protected void onDeathUpdate() {
      ++this.deathTime;
      if (this.deathTime >= 180 && this.deathTime <= 200) {
         float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + (double)f, this.posY + 2.0D + (double)f1, this.posZ + (double)f2, 0.0D, 0.0D, 0.0D, new int[0]);
      }

      boolean flag = this.world.getGameRules().getBoolean("doMobLoot");
      int i = this.experienceValue;
      if (!this.world.isRemote) {
         if (this.deathTime > 150 && this.deathTime % 5 == 0 && flag) {
            this.dropExperience(MathHelper.floor((float)i * 0.08F));
         }

         if (this.deathTime == 1) {
            this.playSound(SoundEvents.ENTITY_ENDERDRAGON_DEATH, 10.0F, 1.0F);
         }
      }

      this.motionY = 0.01D;
      if (this.deathTime >= 200) {
         if (!this.world.isRemote) {
            if (flag) {
               this.dropExperience(MathHelper.floor((float)i * 0.2F));
            }

            this.setDead();
         }

         for(int k = 0; k < 20; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1, new int[0]);
         }
      }

   }

   private void dropExperience(int p_184668_1_) {
      while(p_184668_1_ > 0) {
         int i = EntityXPOrb.getXPSplit(p_184668_1_);
         p_184668_1_ -= i;
         this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, i));
      }

   }

   public void addTrackingPlayer(EntityPlayerMP player) {
      super.addTrackingPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public void removeTrackingPlayer(EntityPlayerMP player) {
      super.removeTrackingPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public boolean isNonBoss() {
      return false;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("Invul", this.getInvulTime());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setInvulTime(compound.getInteger("Invul"));
      if (this.hasCustomName()) {
         this.bossInfo.setName(this.getDisplayName());
      }

   }

   public void setCustomNameTag(String name) {
      super.setCustomNameTag(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT;
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_WITHER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITHER_DEATH;
   }

   public int getInvulTime() {
      return ((Integer)this.dataManager.get(INVULNERABILITY_TIME)).intValue();
   }

   public void setInvulTime(int time) {
      this.dataManager.set(INVULNERABILITY_TIME, Integer.valueOf(time));
   }

   static class AIBigLeap extends EntityAILeapAtTarget {
      EntityDestroyer leaper;
      EntityLivingBase leapTarget;
      float leapMotionY;

      public AIBigLeap(EntityDestroyer leapingEntity, float leapMotionYIn) {
         super(leapingEntity, leapMotionYIn);
         this.leaper = leapingEntity;
         this.leapMotionY = leapMotionYIn;
      }

      public boolean shouldExecute() {
         this.leapTarget = this.leaper.getAttackTarget();
         if (this.leapTarget != null && this.leaper.onGround && !this.leaper.isInWater()) {
            double d0 = this.leaper.getDistanceSq(this.leapTarget);
            return d0 >= 16.0D && d0 <= 1024.0D && this.leaper.getRNG().nextInt(7) == 0;
         } else {
            return false;
         }
      }

      public void startExecuting() {
         double d0 = this.leapTarget.posX - this.leaper.posX;
         double d1 = this.leapTarget.posZ - this.leaper.posZ;
         float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
         float enrage = (1.0F - this.leaper.getHealth() / this.leaper.getMaxHealth()) * 1.0F + 1.0F;
         if ((double)f >= 1.0E-4D) {
            this.leaper.motionX += d0 / (double)f * 1.5D * 0.800000011920929D + this.leaper.motionX * 0.4D;
            this.leaper.motionZ += d1 / (double)f * 1.5D * 0.800000011920929D + this.leaper.motionZ * 0.4D;
         }

         this.leaper.motionX *= (double)enrage;
         this.leaper.motionZ *= (double)enrage;
         this.leaper.motionY = (double)this.leapMotionY;
         this.leaper.setLeaping(true);
         this.leaper.getLookHelper().setLookPositionWithEntity(this.leapTarget, 10.0F, 10.0F);
      }
   }

   class AIDoNothing extends EntityAIBase {
      public AIDoNothing() {
         this.setMutexBits(7);
      }

      public boolean shouldExecute() {
         return EntityDestroyer.this.getInvulTime() > 0;
      }
   }
}
