package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackSwell;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityRathol extends EntityParasiteBase {
   public EntityRathol(World worldIn) {
      super(worldIn);
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, true, false, (Predicate)null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, true, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
      }

      this.setSize(1.3F, 3.1F);
      this.experienceValue = SRPAttributes.XP_PRIMITIVE;
      this.fuseTime = 70;
      this.type = 41;
      this.killcount = -10.0D;
   }

   public int getParasiteIDRegister() {
      return 3;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackSwell(this));
      this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.1D, false));
      this.tasks.addTask(6, new EntityAILookIdle(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.RATHOL_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.RATHOL_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.RATHOL_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.RATHOL_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   public static void registerFixesRathol(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityRathol.class);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setShort("Fuse", (short)this.fuseTime);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("Fuse", 99)) {
         this.fuseTime = compound.getShort("Fuse");
      }

   }

   public void onUpdate() {
      if (this.isEntityAlive()) {
         this.lastActiveTime = this.timeSinceIgnited;
         if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.05D) {
            this.setSelfeState(1);
         }

         this.dyingBurst(false, 2);
      }

      super.onUpdate();
   }

   protected void onDeathUpdate() {
      if (this.isBurning()) {
         super.onDeathUpdate();
      } else {
         this.setSelfeState(1);
         this.dyingBurst(true, 2);
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      return true;
   }

   protected void selfExplode() {
      switch(this.getSkin()) {
      case 1:
         boolean flag = ForgeEventFactory.getMobGriefingEvent(this.world, this) && SRPConfigMobs.ratholGriefing;
         ParasiteEventEntity.createExplosion(this.world, this, this.posX, this.posY, this.posZ, 4.0F, flag);
         if (!this.world.isRemote) {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(11.0D);

            for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
               if (!(mob instanceof EntityParasiteBase)) {
                  mob.attackEntityFrom(DamageSource.WITHER, (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                  SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 400, 4);
               }
            }

            this.playSound(SRPSounds.RATHOL_BOOM, 2.0F, 1.0F);
            this.dead = true;
            this.setDead();
            this.spawnLingeringCloud();
         }

         return;
      default:
         boolean flag = ForgeEventFactory.getMobGriefingEvent(this.world, this) && SRPConfigMobs.ratholGriefing;
         ParasiteEventEntity.createExplosion(this.world, this, this.posX, this.posY, this.posZ, 4.0F, flag);
         if (!this.world.isRemote) {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(7.0D);

            for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
               if (!(mob instanceof EntityParasiteBase)) {
                  SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 400, 2);
               }
            }

            this.playSound(SRPSounds.RATHOL_BOOM, 2.0F, 1.0F);
            this.dead = true;
            this.setDead();
            this.spawnLingeringCloud();
            ParasiteEventEntity.spawnM(this, SRPConfigMobs.ratholMobs, 0, false, this.getCustomNameTag());
         }

      }
   }

   private void spawnLingeringCloud() {
      switch(this.getSkin()) {
      case 1:
         EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setRadius(this.width * 3.0F, this.height);
         entityareaeffectcloud.setWaitTime(10);
         entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() * 2);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.POISON, 300, 2));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.VIRA_E, 3600, 2, false, false));
         this.world.spawnEntity(entityareaeffectcloud);
         return;
      default:
         EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setRadius(this.width * 2.0F, this.height);
         entityareaeffectcloud.setWaitTime(10);
         entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() * 2);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.POISON, 300, 0));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.VIRA_E, 3600, 0, false, false));
         this.world.spawnEntity(entityareaeffectcloud);
      }
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(this.rand.nextInt(1)) {
         case 0:
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
            this.setSkin(1);
         }
      }

      return floo;
   }
}
