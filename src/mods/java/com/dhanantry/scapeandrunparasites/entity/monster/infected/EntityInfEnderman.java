package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityInfEnderman extends EntityPInfected {
   private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier ATTACKING_SPEED_BOOST = (new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15000000596046448D, 0)).setSaved(false);
   private static final DataParameter<Boolean> SCREAMING = EntityDataManager.createKey(EntityInfEnderman.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> CRAWLING = EntityDataManager.createKey(EntityInfEnderman.class, DataSerializers.BOOLEAN);
   private int lastCreepySound;
   private int targetChangeTime;
   private double targetX;
   private double targetY;
   private double targetZ;
   private int ally;
   private EntityParasiteBase toTele;
   private int toTeleCool;
   private int spotCool;
   private String tpPlayerName;

   public EntityInfEnderman(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 2.9F);
      this.canModRender = 0;
      this.type = 14;
      this.tasks.removeTask(this.folow);
      this.killcount = -10.0D;
      this.stepHeight = 1.0F;
      this.ally = 0;
      this.tpPlayerName = null;
   }

   public int getParasiteIDRegister() {
      return 59;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.08D));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.2D, false, 0.0D));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 1, 16));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.INFENDERMAN_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.INFENDERMAN_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.INFENDERMAN_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.INFENDERMAN_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
   }

   public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
      boolean flag = this.getAttackTarget() == null;
      super.setAttackTarget(entitylivingbaseIn);
      IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (entitylivingbaseIn == null) {
         this.targetChangeTime = 0;
         this.dataManager.set(SCREAMING, Boolean.valueOf(false));
         iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST);
      } else {
         this.targetChangeTime = this.ticksExisted;
         this.dataManager.set(SCREAMING, Boolean.valueOf(true));
         if (flag) {
            this.spotCool = SRPConfigMobs.infendermansaw;
            if (entitylivingbaseIn instanceof EntityPlayer) {
               if (this.tpPlayerName != null) {
                  if (!this.tpPlayerName.equals(((EntityPlayer)entitylivingbaseIn).getName())) {
                     this.tpPlayerName = ((EntityPlayer)entitylivingbaseIn).getName();
                     this.world.playSound((EntityPlayer)null, entitylivingbaseIn.prevPosX, entitylivingbaseIn.prevPosY, entitylivingbaseIn.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.getSoundCategory(), 0.3F, 1.0F);
                  }
               } else {
                  this.tpPlayerName = ((EntityPlayer)entitylivingbaseIn).getName();
                  this.world.playSound((EntityPlayer)null, entitylivingbaseIn.prevPosX, entitylivingbaseIn.prevPosY, entitylivingbaseIn.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.getSoundCategory(), 0.3F, 1.0F);
               }
            } else {
               this.tpPlayerName = null;
            }
         }

         if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST)) {
            iattributeinstance.applyModifier(ATTACKING_SPEED_BOOST);
         }
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(SCREAMING, Boolean.valueOf(false));
      this.dataManager.register(CRAWLING, Boolean.valueOf(false));
   }

   public void playEndermanSound() {
      if (this.ticksExisted >= this.lastCreepySound + 400) {
         this.lastCreepySound = this.ticksExisted;
         if (!this.isSilent()) {
            this.world.playSound(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, SoundEvents.ENTITY_ENDERMEN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
         }
      }

   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (SCREAMING.equals(key) && this.isScreaming() && this.world.isRemote) {
         ;
      }

      super.notifyDataManagerChange(key);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.isCrawling()) {
         this.setSize(0.95F, 1.25F);
      }

      if (this.world.isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
         }
      } else if (this.getAttackTarget() != null && this.ticksExisted % 20 == 0 && this.getDistanceSq(this.getAttackTarget()) > 4.0D && this.rand.nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
         this.teleportRandomly();
      }

      if (!this.world.isRemote) {
         if (this.ally > 0) {
            ++this.ally;
            this.teleportAlly();
         }

         if (this.spotCool >= 0) {
            --this.spotCool;
         }

         if (this.toTeleCool >= 0) {
            --this.toTeleCool;
         }

         if (this.srpTicks == 10 && this.isPotionActive(SRPPotions.RAGE_E)) {
            this.spotCool = 0;
            this.toTeleCool = 0;
         }
      }

      this.isJumping = false;
   }

   protected boolean teleportAllies() {
      if (this.ally <= 0 && SRPConfigMobs.infendermanteleally && this.toTeleCool <= 0 && this.spotCool <= 0) {
         EntityLivingBase target = this.getAttackTarget();
         if (target == null) {
            return false;
         } else {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(64.0D);
            List<EntityPInfected> moblist = this.world.getEntitiesWithinAABB(EntityPInfected.class, axisalignedbb);
            Iterator var4 = moblist.iterator();

            EntityPInfected mob;
            while(true) {
               if (!var4.hasNext()) {
                  for(EntityParasiteBase mob : moblist) {
                     if (mob instanceof EntityMudo && mob.getAttackTarget() == null && mob.getHealth() - SRPConfigMobs.infendermanTeleDamage >= 2.0F && this.teleportToEntity(mob, 1.0D)) {
                        this.setCoordTarget(target.posX, target.posY, target.posZ);
                        this.toTele = mob;
                        this.setWorkTask(false);
                        this.ally = 1;
                        return true;
                     }
                  }

                  return false;
               }

               mob = (EntityPInfected)var4.next();
               if (mob != this && mob.getAttackTarget() == null && mob.getParasiteType() <= 15 && (!(mob instanceof EntityCanMelt) || !((EntityCanMelt)mob).isMelting()) && mob.getHealth() - SRPConfigMobs.infendermanTeleDamage >= 2.0F && this.teleportToEntity(mob, 1.0D)) {
                  break;
               }
            }

            this.setCoordTarget(target.posX, target.posY, target.posZ);
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
         }
      } else {
         return false;
      }
   }

   private void teleportAlly() {
      if (this.ally >= 8) {
         if (this.toTele != null) {
            if (!this.toTele.isEntityAlive()) {
               this.toTele = null;
               this.ally = 0;
               this.setWorkTask(true);
               boolean flag2 = false;

               for(int lag2 = 10; !flag2 && lag2 > 0; --lag2) {
                  flag2 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0D);
               }

               return;
            }

            boolean flag1 = false;

            for(int lag1 = 10; !flag1 && lag1 > 0; --lag1) {
               flag1 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0D);
               if (flag1) {
                  break;
               }
            }

            if (flag1) {
               boolean flag2 = false;
               int lag2 = 12;
               this.toTele.copyLocationAndAnglesFrom(this);
               this.toTele.attackEntityFrom(DamageSource.FALL, SRPConfigMobs.infendermanTeleDamage);
               if (this.isBurning() && this.rand.nextInt(4) != 0) {
                  this.toTele.setFire(8);
               }

               this.world.playSound((EntityPlayer)null, this.toTele.prevPosX, this.toTele.prevPosY, this.toTele.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.getSoundCategory(), 1.0F, 1.0F);
               flag2 = true;
               if (flag2) {
                  EntityLivingBase target = this.getAttackTarget();
                  if (target != null && target.isEntityAlive()) {
                     this.toTele.setAttackTarget(target);
                  }
               }
            }
         } else {
            boolean flag2 = false;

            for(int lag2 = 10; !flag2 && lag2 > 0; --lag2) {
               flag2 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0D);
            }
         }

         this.setWorkTask(true);
         this.ally = 0;
         this.toTele = null;
         this.toTeleCool = SRPConfigMobs.infendermanallyCool;
      }
   }

   protected boolean teleportRandomly() {
      if (this.spotCool > 0) {
         return false;
      } else {
         double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * 64.0D;
         double d1 = this.posY + (double)(this.rand.nextInt(64) - 32);
         double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * 64.0D;
         return this.teleportTo(d0, d1, d2);
      }
   }

   protected boolean teleportToEntity(Entity in, double dis) {
      double d1 = in.posX + (this.rand.nextDouble() - 0.5D) * dis;
      double d2 = in.posY + (double)(this.rand.nextInt(16) - 8) * dis;
      double d3 = in.posZ + (this.rand.nextDouble() - 0.5D) * dis;
      return this.teleportTo(d1, d2, d3);
   }

   protected boolean teleportToPos(double x, double y, double z, double dis) {
      double d1 = x + (this.rand.nextDouble() - 0.5D) * dis;
      double d2 = y + (double)(this.rand.nextInt(16) - 8) * dis;
      double d3 = z + (this.rand.nextDouble() - 0.5D) * dis;
      return this.teleportTo(d1, d2, d3);
   }

   protected boolean teleportEntityTo(EntityParasiteBase in, double x, double y, double z, double dis) {
      double d1 = x + (this.rand.nextDouble() - 0.5D) * dis;
      double d2 = y + (double)(this.rand.nextInt(16) - 8) * dis;
      double d3 = z + (this.rand.nextDouble() - 0.5D) * dis;
      return this.teleportTo(in, d1, d2, d3);
   }

   private boolean teleportTo(double x, double y, double z) {
      EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0.0F);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         return false;
      } else {
         boolean flag = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
         if (flag) {
            this.world.playSound((EntityPlayer)null, this.prevPosX, this.prevPosY, this.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.getSoundCategory(), 1.0F, 1.0F);
            this.playSound(SRPSounds.INFECTEDENDERMAN_PORTAL, 1.0F, 1.0F);
         }

         return flag;
      }
   }

   public boolean attemptTeleport(double x, double y, double z) {
      double d0 = this.posX;
      double d1 = this.posY;
      double d2 = this.posZ;
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      boolean flag = false;
      BlockPos blockpos = new BlockPos(this);
      World world = this.world;
      Random random = this.getRNG();
      if (world.isBlockLoaded(blockpos)) {
         boolean flag1 = false;

         while(!flag1 && blockpos.getY() > 0) {
            BlockPos blockpos1 = blockpos.down();
            IBlockState iblockstate = world.getBlockState(blockpos1);
            if (!iblockstate.getMaterial().blocksMovement() && !(iblockstate.getBlock() instanceof BlockLiquid)) {
               --this.posY;
               blockpos = blockpos1;
            } else {
               flag1 = true;
            }
         }

         if (flag1) {
            this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            if (world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(this.getEntityBoundingBox())) {
               flag = true;
            }
         }
      }

      if (!flag) {
         this.setPositionAndUpdate(d0, d1, d2);
         return false;
      } else {
         int i = 128;

         for(int j = 0; j < 128; ++j) {
            double d6 = (double)j / 127.0D;
            float f = (random.nextFloat() - 0.5F) * 0.2F;
            float f1 = (random.nextFloat() - 0.5F) * 0.2F;
            float f2 = (random.nextFloat() - 0.5F) * 0.2F;
            double d3 = d0 + (this.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double)this.width * 2.0D;
            double d4 = d1 + (this.posY - d1) * d6 + random.nextDouble() * (double)this.height;
            double d5 = d2 + (this.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double)this.width * 2.0D;
            world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double)f, (double)f1, (double)f2, new int[0]);
         }

         if (this instanceof EntityCreature) {
            this.getNavigator().clearPath();
         }

         return true;
      }
   }

   private boolean teleportTo(EntityParasiteBase in, double x, double y, double z) {
      EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0.0F);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         return false;
      } else {
         boolean flag = in.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
         if (flag) {
            in.world.playSound((EntityPlayer)null, in.prevPosX, in.prevPosY, in.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, in.getSoundCategory(), 1.0F, 1.0F);
            in.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
         }

         return flag;
      }
   }

   private void setCoordTarget(double x, double y, double z) {
      this.targetX = x;
      this.targetY = y;
      this.targetZ = z;
   }

   protected void onDeathUpdate() {
      super.onDeathUpdate();
      if (this.deathTime == 20 && !this.world.isRemote && this.rand.nextDouble() <= SRPAttributes.INFENDERMAN_HEADCHANCE) {
         ParasiteEventEntity.spawnM(this, new String[]{"srparasites:sim_endermanhead;1;1"}, 0, false, this.getCustomNameTag());
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      this.spotCool = 0;
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else if (source instanceof EntityDamageSourceIndirect) {
         for(int i = 0; i < 64; ++i) {
            if (this.teleportRandomly()) {
               return true;
            }
         }

         return false;
      } else {
         boolean flag = super.attackEntityFrom(source, amount);
         if (source.isUnblockable() && this.rand.nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
         }

         return flag;
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         if (this.rand.nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof EntityLivingBase) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
         }

         if (this.rand.nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
         }
      }

      return flag;
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      this.tpPlayerName = null;
   }

   public boolean isScreaming() {
      return ((Boolean)this.dataManager.get(SCREAMING)).booleanValue();
   }

   public boolean isCrawling() {
      return ((Boolean)this.dataManager.get(CRAWLING)).booleanValue();
   }

   public void setCrawling(boolean in) {
      this.dataManager.set(CRAWLING, Boolean.valueOf(in));
   }

   public float getEyeHeight() {
      return this.height * 0.88F;
   }

   public static void registerFixesInfHuman(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityInfEnderman.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.INFECTEDENDERMAN_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.INFECTEDENDERMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.INFECTEDENDERMAN_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("thiscancrawl", this.isCrawling());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("thiscancrawl", 99)) {
         this.setCrawling(compound.getBoolean("thiscancrawl"));
         if (this.isCrawling()) {
            ;
         }
      }

   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         this.setCrawling(true);
      }

      return floo;
   }
}
