package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCanraAdapted extends EntityPAdapted implements EntityCanSummon, EntityBodyParts {
   private EntityBody leftTendril;
   private EntityBody rightTendril;
   private float leftTendrilHealth;
   private float rightTendrilHealth;
   private int totalP = SRPConfigMobs.canraadaptedtotalactivemobs;
   private int actualP = 0;
   private int[] mobID;
   private int[] mobPT;
   private int limit;
   private int border;
   private boolean skillSummon;

   public EntityCanraAdapted(World worldIn) {
      super(worldIn);
      this.mobID = new int[this.totalP + SRPConfigMobs.canraadaptedlimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.canraadaptedlimit];
      this.setSize(1.3F, 3.3F);
      this.stepHeight = 1.0F;

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.leftTendril = new EntityBody(this, 0.6F, 1.7F, 1.0F, 0.9F, 1.8F, 1, 1, true);
      this.rightTendril = new EntityBody(this, 0.6F, 1.7F, 1.0F, 0.9F, 1.8F, -1, 2, true);
      this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.skillSummon = false;
   }

   public int getParasiteIDRegister() {
      return 53;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.11D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(2, new EntityAISkill(this, 20 * SRPConfigMobs.canraadaptedsummoningcooldown, 16, true, 1));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, 8.0D));
      if (SRPConfig.parasiteGenResidue) {
         this.tasks.addTask(9, new EntityAIBlockResidue(this, 2));
      }

      this.tasks.addTask(6, new EntityAIGetFollowers(this, 3, 32));
      this.tasks.addTask(2, new EntityAIEvade(this, 25, 10, 4));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.CANRA_HEALTH + SRPAttributes.CANRA_A_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.CANRA_ARMOR + SRPAttributes.CANRA_A_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.CANRA_KD_RESISTANCE + SRPAttributes.CANRA_A_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.CANRA_ATTACK_DAMAGE + SRPAttributes.CANRA_A_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.leftTendrilHealth > 0.0F) {
         this.leftTendril.onUpdate();
      }

      if (this.rightTendrilHealth > 0.0F) {
         this.rightTendril.onUpdate();
      }

      if (!this.world.isRemote && this.rand.nextInt(2) == 0 && this.ticksExisted % 60 == 0) {
         boolean flag = false;
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(16.0D);

         for(EntityRemain mob : this.world.getEntitiesWithinAABB(EntityRemain.class, axisalignedbb)) {
            if (!mob.getActive()) {
               mob.setPlus(SRPConfigMobs.canraadaptedremainplus);
               mob.setHealth(SRPConfigMobs.canraadaptedremainhealth);
               flag = true;
            }
         }

         if (flag) {
            this.playSound(SRPSounds.ACANRA_SPECIAL, 3.0F, 1.0F);
            this.particleStatus((byte)8);
         }
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase) {
         switch(this.getSkin()) {
         case 5:
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
            break;
         case 6:
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
         }
      }

      return flag;
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.world.isRemote) {
         return false;
      } else {
         boolean flag = this.attackEntityFrom(source, amount);
         if (!flag) {
            return false;
         } else {
            if (this.leftTendril.getId() == id) {
               this.leftTendrilHealth -= amount;
               if (this.leftTendrilHealth <= 0.0F) {
                  EntityTendril tendril = new EntityTendril(this.world);
                  tendril.setSkin(3);
                  tendril.copyLocationAndAnglesFrom(this.leftTendril);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.leftTendril);
                  this.world.setEntityState(this, (byte)11);
                  this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                  SRPMain.network.sendToAll(new SRPPacketEntityBodyDead(this.getEntityId(), id));
               }
            } else if (this.rightTendril.getId() == id) {
               this.rightTendrilHealth -= amount;
               if (this.rightTendrilHealth <= 0.0F) {
                  EntityTendril tendril = new EntityTendril(this.world);
                  tendril.setSkin(3);
                  tendril.copyLocationAndAnglesFrom(this.rightTendril);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.rightTendril);
                  this.world.setEntityState(this, (byte)22);
                  this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                  SRPMain.network.sendToAll(new SRPPacketEntityBodyDead(this.getEntityId(), id));
               }
            }

            return flag;
         }
      }
   }

   public void setBodyPartDead(int id) {
      if (this.leftTendril.getId() == id) {
         this.world.removeEntityDangerously(this.leftTendril);
      } else if (this.rightTendril.getId() == id) {
         this.world.removeEntityDangerously(this.rightTendril);
      }

   }

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
      if (!this.world.isRemote) {
         if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
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
      return 1.8F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityCanra(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public static void registerFixesCarna(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityCanraAdapted.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.ACANRA_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ACANRA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ACANRA_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.canraadaptedOrbEffects, mobs);
      }

      return flag;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SRPSounds.MONSTER_STEP, 0.15F, 1.0F);
   }

   public void setDead() {
      if (this.leftTendril != null) {
         this.world.removeEntityDangerously(this.leftTendril);
      }

      if (this.rightTendril != null) {
         this.world.removeEntityDangerously(this.rightTendril);
      }

      super.setDead();
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

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("parasiteleftTendril", this.leftTendrilHealth);
      compound.setFloat("parasiterightTendril", this.rightTendrilHealth);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("parasiteleftTendril", 99)) {
         this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
         if (this.leftTendrilHealth <= 0.0F) {
            this.world.setEntityState(this, (byte)11);
         }
      }

      if (compound.hasKey("parasiterightTendril", 99)) {
         this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
         if (this.rightTendrilHealth <= 0.0F) {
            this.world.setEntityState(this, (byte)22);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public float getLeft() {
      return this.leftTendrilHealth;
   }

   @SideOnly(Side.CLIENT)
   public float getRight() {
      return this.rightTendrilHealth;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 11) {
         this.leftTendrilHealth = 0.0F;
      } else if (id == 22) {
         this.rightTendrilHealth = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }

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
            if (this.getActualParasites() < this.getTotalParasites() && this.limit < SRPConfigMobs.canraadaptedlimit) {
               this.playSound(SRPSounds.ACANRA_SPECIAL, 3.0F, 1.0F);
               if (ParasiteEventEntity.spawnBiomassFromVomit(this, SRPConfigMobs.canraadaptedmoblist, this.getAttackTarget())) {
                  ++this.limit;
                  --this.border;
                  this.particleStatus((byte)8);
               }
            } else {
               ++this.border;
            }

            if (this.limit >= SRPConfigMobs.canraadaptedlimit || this.border > 4) {
               this.skillSummon = true;
               this.setParasiteStatus(0);
               this.border = 0;
               this.limit = 0;
            }

         }
      }
   }
}
