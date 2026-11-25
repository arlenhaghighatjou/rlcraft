package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityCanra extends EntityPPrimitive implements EntityCanSummon {
   private int totalP = SRPConfigMobs.canraTotalActiveMobs;
   private int actualP = 0;
   private int[] mobID;
   private int[] mobPT;
   private int limit;
   private int border;
   private boolean skillSummon;

   public EntityCanra(World worldIn) {
      super(worldIn);
      this.mobID = new int[this.totalP + SRPConfigMobs.canraLimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.canraLimit];
      this.setSize(1.2F, 1.8F);

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.skillSummon = false;
   }

   public int getParasiteIDRegister() {
      return 8;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.095D));
      this.tasks.addTask(2, new EntityAISkill(this, 20 * SRPConfigMobs.canraSummoningCooldown, 16, true, 1));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, 8.0D));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 2, 16));
      this.tasks.addTask(2, new EntityAIEvade(this, 55, 10, 4));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.CANRA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.CANRA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.CANRA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.CANRA_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.primitiveFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote && this.ticksExisted % 20 == 0) {
         if (this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityCanraAdapted(this.world), true, true);
         }

         if (this.rand.nextInt(2) == 0 && this.ticksExisted % 60 == 0) {
            boolean flag = false;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(16.0D);

            for(EntityRemain mob : this.world.getEntitiesWithinAABB(EntityRemain.class, axisalignedbb)) {
               if (!mob.getActive()) {
                  mob.setPlus(SRPConfigMobs.canraRemainPlus);
                  mob.setHealth(SRPConfigMobs.canraRemainHealth);
                  flag = true;
               }
            }

            if (flag) {
               this.playSound(SRPSounds.CANRA_SPECIAL, 3.0F, 1.0F);
               this.particleStatus((byte)8);
            }
         }
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase) {
         switch(this.getSkin()) {
         case 5:
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 40, 0);
            break;
         case 6:
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 40, 0);
         }
      }

      return flag;
   }

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
      if (!this.world.isRemote) {
         if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 40, 0);
         }

      }
   }

   public int getTotalParasites() {
      return this.totalP;
   }

   public int getActualParasites() {
      return this.actualP;
   }

   public void setActualParasites(int i) {
      this.actualP += i;
   }

   public void addID(int id, int points) {
      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] == -777) {
            this.mobID[i] = id;
            this.mobPT[i] = points;
            return;
         }
      }

   }

   public int IDable() {
      int flag = 0;

      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] == -777) {
            ++flag;
         }
      }

      if (flag > this.totalP) {
         flag = this.totalP;
      }

      return flag;
   }

   public void checkID() {
      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] > 0) {
            Entity flag = this.world.getEntityByID(this.mobID[i]);
            if (flag == null) {
               this.mobID[i] = -777;
               int negative = this.mobPT[i] * -1;
               this.setActualParasites(negative);
            }
         }
      }

   }

   public int[] getIDList() {
      return null;
   }

   public int[] getPointList() {
      return null;
   }

   public float getEyeHeight() {
      return 0.9F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityLesh(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public static void registerFixesCarna(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityCanra.class);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      this.particleStatus((byte)5);
      if (!this.world.isRemote && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
         ParasiteEventEntity.spawnNext(this, new EntityCanraAdapted(this.world), true, true);
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.CANRA_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.CANRA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.CANRA_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.canraOrbEffects, mobs);
      }

      return flag;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SRPSounds.MONSTER_STEP, 0.15F, 1.0F);
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(this.rand.nextInt(2)) {
         case 0:
            this.setSkin(5);
            break;
         case 1:
            this.setSkin(6);
         }
      }

      return floo;
   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 1:
         return this.skillSummon;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillSummon = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.summon();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void summon() {
      this.setParasiteStatus(10);
      this.getNavigator().clearPath();
      if (this.ticksExisted % 20 == 0) {
         ++this.border;
         if (this.world.getBlockState(this.getPosition()).getBlock() instanceof BlockLiquid) {
            this.skillSummon = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
         } else {
            this.checkID();
            if (this.getActualParasites() < this.getTotalParasites() && this.limit < SRPConfigMobs.canraLimit) {
               this.playSound(SRPSounds.CANRA_SPECIAL, 3.0F, 1.0F);
               if (ParasiteEventEntity.spawnBiomassFromVomit(this, SRPConfigMobs.canraMobList, this.getAttackTarget())) {
                  ++this.limit;
                  --this.border;
                  this.particleStatus((byte)8);
               }
            } else {
               ++this.border;
            }

            if (this.limit >= SRPConfigMobs.canraLimit || this.border > 4) {
               this.skillSummon = true;
               this.setParasiteStatus(0);
               this.border = 0;
               this.limit = 0;
            }

         }
      }
   }
}
