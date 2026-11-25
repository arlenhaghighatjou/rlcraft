package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWave;
import com.lycanitesmobs.core.info.CreatureManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityRahovart extends BaseCreatureEntity implements IMob, IGroupHeavy, IGroupBoss {
   public int hellfireEnergy = 0;
   public List<EntityHellfireOrb> hellfireOrbs = new ArrayList();
   protected static final DataParameter<Integer> HELLFIRE_ENERGY = EntityDataManager.createKey(EntityRahovart.class, DataSerializers.VARINT);
   public List<EntityBelph> hellfireBelphMinions = new ArrayList();
   public List<EntityBehemoth> hellfireBehemothMinions = new ArrayList();
   public int hellfireWallTime = 0;
   public int hellfireWallTimeMax = 400;
   public boolean hellfireWallClockwise = false;
   public EntityHellfireBarrier hellfireWallLeft;
   public EntityHellfireBarrier hellfireWallRight;
   public List<EntityHellfireBarrier> hellfireBarriers = new ArrayList();
   public int hellfireBarrierHealth = 100;

   public EntityRahovart(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = false;
      this.setAttackCooldownMax(40);
      this.solidCollision = true;
      this.trueSight = true;
      this.entityCollisionReduction = 1.0F;
      this.setupMob();
      this.hitAreaWidthScale = 2.0F;
      this.damageMax = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
      this.damageLimit = (float)BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(this.nextFindTargetIndex, new FindNearbyPlayersGoal(this));
      this.tasks.addTask(this.nextIdleGoalIndex, new FaceTargetGoal(this));
      this.tasks.addTask(this.nextIdleGoalIndex, new HealWhenNoPlayersGoal(this));
      this.tasks.addTask(this.nextIdleGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("wraith").setAntiFlight(true));
      this.tasks.addTask(this.nextIdleGoalIndex, (new FireProjectilesGoal(this)).setProjectile("hellfireball").setFireRate(40).setVelocity(1.0F).setScale(8.0F).setAllPlayers(true));
      this.tasks.addTask(this.nextIdleGoalIndex, (new FireProjectilesGoal(this)).setProjectile("hellfireball").setFireRate(60).setVelocity(1.0F).setScale(8.0F));
      this.tasks.addTask(this.nextIdleGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("archvile").setSummonRate(200).setSummonCap(1).setPerPlayer(true).setSizeScale(2.0D).setConditions((new GoalConditions()).setBattlePhase(2)));
      super.initEntityAI();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HELLFIRE_ENERGY, Integer.valueOf(this.hellfireEnergy));
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(200.0D, 50.0D, 200.0D).offset(0.0D, -25.0D, 0.0D);
   }

   public void onFirstSpawn() {
      super.onFirstSpawn();
      if (this.getArenaCenter() == null) {
         this.setArenaCenter(this.getPosition());
      }

   }

   public void setArenaCenter(BlockPos pos) {
      super.setArenaCenter(pos);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.hasAttackTarget() && !this.getEntityWorld().isRemote) {
         this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 30.0F, 30.0F);
      }

      if (this.hasArenaCenter()) {
         BlockPos arenaPos = this.getArenaCenter();
         double arenaY = this.posY;
         if (this.getEntityWorld().isAirBlock(arenaPos)) {
            arenaY = (double)arenaPos.getY();
         } else if (this.getEntityWorld().isAirBlock(arenaPos.add(0, 1, 0))) {
            arenaY = (double)arenaPos.add(0, 1, 0).getY();
         }

         if (this.posX != (double)arenaPos.getX() || this.posY != arenaY || this.posZ != (double)arenaPos.getZ()) {
            this.setPosition((double)arenaPos.getX(), arenaY, (double)arenaPos.getZ());
         }
      }

      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(HELLFIRE_ENERGY, Integer.valueOf(this.hellfireEnergy));
      } else {
         this.hellfireEnergy = ((Integer)this.dataManager.get(HELLFIRE_ENERGY)).intValue();
      }

      updateHellfireOrbs(this, this.updateTick, 5, this.hellfireEnergy, 10.0F, this.hellfireOrbs);
      if (!this.getEntityWorld().isRemote) {
         this.updatePhases();
      }

   }

   public boolean rollWanderChance() {
      return false;
   }

   public void updatePhases() {
      if (this.getBattlePhase() == 0) {
         if (!this.hellfireBehemothMinions.isEmpty()) {
            for(EntityBehemoth minion : (EntityBehemoth[])this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
               minion.hellfireEnergy = 0;
            }

            this.hellfireBehemothMinions = new ArrayList();
         }

         this.hellfireWallTime = 0;
         this.hellfireBarrierCleanup();
         if (this.updateTick % 20L == 0L) {
            for(EntityBelph minion : (EntityBelph[])this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
               if (!minion.isEntityAlive()) {
                  this.onMinionDeath(minion, (DamageSource)null);
               } else {
                  minion.hellfireEnergy += 5;
                  if (minion.hellfireEnergy >= 100) {
                     this.hellfireEnergy += 20;
                     this.onMinionDeath(minion, (DamageSource)null);
                     this.getEntityWorld().createExplosion(minion, minion.posX, minion.posY, minion.posZ, 1.0F, false);
                     minion.hellfireEnergy = 0;
                     minion.setDead();
                  }
               }
            }
         }

         if (this.hellfireEnergy >= 100) {
            this.hellfireEnergy = 0;
            double angle = (double)(this.getRNG().nextFloat() * 360.0F);
            if (this.hasAttackTarget()) {
               double deltaX = this.getAttackTarget().posX - this.posX;
               double deltaZ = this.getAttackTarget().posZ - this.posZ;
               angle = Math.atan2(deltaZ, deltaX) * 180.0D / 3.141592653589793D;
            }

            this.hellfireWaveAttack(angle);
         }

         if (this.updateTick % 100L == 0L) {
            int summonAmount = this.getRNG().nextInt(4);
            summonAmount = summonAmount * this.playerTargets.size();
            if (summonAmount > 0) {
               for(int summonCount = 0; summonCount <= summonAmount; ++summonCount) {
                  EntityBelph minion = (EntityBelph)CreatureManager.getInstance().getCreature("belph").createEntity(this.getEntityWorld());
                  this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 5.0D);
                  this.hellfireBelphMinions.add(minion);
               }
            }
         }
      }

      if (this.getBattlePhase() == 1) {
         if (!this.hellfireBelphMinions.isEmpty()) {
            for(EntityBelph minion : (EntityBelph[])this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
               minion.hellfireEnergy = 0;
            }

            this.hellfireBelphMinions = new ArrayList();
         }

         this.hellfireBarrierCleanup();
         if (this.hellfireWallTime <= 0 && this.updateTick % 20L == 0L) {
            for(EntityBehemoth minion : (EntityBehemoth[])this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
               if (!minion.isEntityAlive()) {
                  this.onMinionDeath(minion, (DamageSource)null);
               } else {
                  minion.hellfireEnergy += 5;
                  if (minion.hellfireEnergy >= 100) {
                     this.hellfireEnergy += 20;
                     this.onMinionDeath(minion, (DamageSource)null);
                     this.getEntityWorld().createExplosion(minion, minion.posX, minion.posY, minion.posZ, 1.0F, false);
                     minion.hellfireEnergy = 0;
                     minion.setDead();
                  }
               }
            }
         }

         if (this.hellfireEnergy >= 100) {
            this.hellfireEnergy = 0;
            this.hellfireWallAttack((double)this.rotationYaw);
         }

         if (this.hellfireWallTime > 0) {
            this.hellfireWallUpdate();
            --this.hellfireWallTime;
         }

         if (this.updateTick % 400L == 0L) {
            int summonAmount = 2;
            summonAmount = summonAmount * this.playerTargets.size();
            if (summonAmount > 0) {
               for(int summonCount = 0; summonCount <= summonAmount; ++summonCount) {
                  EntityBehemoth minion = (EntityBehemoth)CreatureManager.getInstance().getCreature("behemoth").createEntity(this.getEntityWorld());
                  this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 5.0D);
                  this.hellfireBehemothMinions.add(minion);
               }
            }
         }

         if (this.updateTick % 200L == 0L) {
            int summonAmount = this.getRNG().nextInt(4) - 1;

            for(int summonCount = 0; summonCount <= summonAmount; ++summonCount) {
               EntityBelph minion = (EntityBelph)CreatureManager.getInstance().getCreature("belph").createEntity(this.getEntityWorld());
               this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 5.0D);
            }
         }
      }

      if (this.getBattlePhase() >= 2) {
         if (!this.hellfireBelphMinions.isEmpty()) {
            for(EntityBelph minion : (EntityBelph[])this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
               minion.hellfireEnergy = 0;
            }

            this.hellfireBelphMinions = new ArrayList();
         }

         if (!this.hellfireBehemothMinions.isEmpty()) {
            for(EntityBehemoth minion : (EntityBehemoth[])this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
               minion.hellfireEnergy = 0;
            }

            this.hellfireBehemothMinions = new ArrayList();
         }

         this.hellfireWallTime = 0;
         if (this.updateTick % 20L == 0L && this.hellfireEnergy < 100) {
            this.hellfireEnergy += 5;
         }

         if (this.hellfireEnergy >= 100 && this.hellfireBarriers.size() < 20) {
            this.hellfireEnergy = 0;
            this.hellfireBarrierAttack((double)(360.0F * this.getRNG().nextFloat()));
         }

         if (this.hellfireBarriers.size() > 0) {
            this.hellfireBarrierUpdate();
         }

         if (this.updateTick % 200L == 0L) {
            int summonAmount = this.getRNG().nextInt(2);
            summonAmount = summonAmount * this.playerTargets.size();
            if (summonAmount > 0) {
               for(int summonCount = 0; summonCount <= summonAmount; ++summonCount) {
                  EntityBehemoth minion = (EntityBehemoth)CreatureManager.getInstance().getCreature("behemoth").createEntity(this.getEntityWorld());
                  this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 5.0D);
                  this.hellfireBehemothMinions.add(minion);
               }
            }
         }

         if (this.updateTick % 400L == 0L) {
            int summonAmount = this.getRNG().nextInt(4);
            summonAmount = summonAmount * this.playerTargets.size();
            if (summonAmount > 0) {
               for(int summonCount = 0; summonCount <= summonAmount; ++summonCount) {
                  EntityBelph minion = (EntityBelph)CreatureManager.getInstance().getCreature("belph").createEntity(this.getEntityWorld());
                  this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 5.0D);
               }
            }

            summonAmount = this.getRNG().nextInt(3);
            summonAmount = summonAmount * this.playerTargets.size();
            if (summonAmount > 0) {
               for(int summonCount = 0; summonCount <= summonAmount; ++summonCount) {
                  EntityWraith minion = (EntityWraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getEntityWorld());
                  this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 5.0D);
               }
            }
         }
      }

      if (this.hellfireWallTime <= 0) {
         this.hellfireWallCleanup();
      }

   }

   public void onMinionDeath(EntityLivingBase minion, DamageSource damageSource) {
      if (minion instanceof EntityBelph && this.hellfireBelphMinions.contains(minion)) {
         this.hellfireBelphMinions.remove(minion);
      } else if (minion instanceof EntityBehemoth && this.hellfireBehemothMinions.contains(minion)) {
         this.hellfireBehemothMinions.remove(minion);
      } else {
         if (this.hellfireBarriers.size() > 0) {
            if (minion instanceof EntityBehemoth) {
               this.hellfireBarrierHealth -= 100;
            } else {
               this.hellfireBarrierHealth -= 50;
            }
         }

         super.onMinionDeath(minion, damageSource);
      }
   }

   public void updateBattlePhase() {
      double healthNormal = (double)(this.getHealth() / this.getMaxHealth());
      if (healthNormal <= 0.2D) {
         this.setBattlePhase(2);
      } else if (healthNormal <= 0.6D) {
         this.setBattlePhase(1);
      } else {
         this.setBattlePhase(0);
      }
   }

   public static void updateHellfireOrbs(EntityLivingBase entity, long orbTick, int hellfireOrbMax, int hellfireOrbEnergy, float orbSize, List<EntityHellfireOrb> hellfireOrbs) {
      if (entity.getEntityWorld().isRemote) {
         int hellfireChargeCount = Math.round((float)Math.min(hellfireOrbEnergy, 100) / (100.0F / (float)hellfireOrbMax));
         int hellfireOrbRotationTime = 100;
         double hellfireOrbAngle = (double)(360.0F * ((float)(orbTick % (long)hellfireOrbRotationTime) / (float)hellfireOrbRotationTime));
         double hellfireOrbAngleOffset = 360.0D / (double)hellfireOrbMax;

         while(hellfireOrbs.size() < hellfireChargeCount) {
            EntityHellfireOrb hellfireOrb = new EntityHellfireOrb(entity.getEntityWorld(), entity);
            hellfireOrb.clientOnly = true;
            hellfireOrbs.add(hellfireOrb);
            entity.getEntityWorld().spawnEntity(hellfireOrb);
            hellfireOrb.setProjectileScale(orbSize);
         }

         while(hellfireOrbs.size() > hellfireChargeCount) {
            ((EntityHellfireOrb)hellfireOrbs.get(hellfireOrbs.size() - 1)).setDead();
            hellfireOrbs.remove(hellfireOrbs.size() - 1);
         }

         for(int i = 0; i < hellfireOrbs.size(); ++i) {
            EntityHellfireOrb hellfireOrb = (EntityHellfireOrb)hellfireOrbs.get(i);
            double rotationRadians = Math.toRadians((hellfireOrbAngle + hellfireOrbAngleOffset * (double)i) % 360.0D);
            double x = (double)entity.width * 1.25D * Math.cos(rotationRadians) + Math.sin(rotationRadians);
            double z = (double)entity.width * 1.25D * Math.sin(rotationRadians) - Math.cos(rotationRadians);
            hellfireOrb.posX = entity.posX - x;
            hellfireOrb.posY = entity.posY + (double)(entity.height * 0.75F);
            hellfireOrb.posZ = entity.posZ - z;
            hellfireOrb.setPosition(entity.posX - x, entity.posY + (double)(entity.height * 0.75F), entity.posZ - z);
            hellfireOrb.projectileLife = 5;
         }

      }
   }

   public boolean canAttackEntity(EntityLivingBase targetEntity) {
      if (!(targetEntity instanceof EntityBelph) && !(targetEntity instanceof EntityBehemoth) && !(targetEntity instanceof EntityArchvile) && !(targetEntity instanceof EntityWraith)) {
         return super.canAttackEntity(targetEntity);
      } else {
         return targetEntity instanceof TameableCreatureEntity ? ((TameableCreatureEntity)targetEntity).getOwner() instanceof EntityPlayer : false;
      }
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("hellfireball", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 8.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public void hellfireWaveAttack(double angle) {
      this.triggerAttackCooldown();
      this.playAttackSound();
      EntityHellfireWave hellfireWave = new EntityHellfireWave(this.getEntityWorld(), this);
      hellfireWave.posY = this.posY;
      hellfireWave.rotation = angle;
      this.getEntityWorld().spawnEntity(hellfireWave);
   }

   public void hellfireWallAttack(double angle) {
      this.playAttackSound();
      this.triggerAttackCooldown();
      this.hellfireWallTime = this.hellfireWallTimeMax;
      this.hellfireWallClockwise = this.getRNG().nextBoolean();
   }

   public void hellfireWallUpdate() {
      this.triggerAttackCooldown();
      double hellfireWallNormal = (double)this.hellfireWallTime / (double)this.hellfireWallTimeMax;
      double hellfireWallAngle = 360.0D;
      if (this.hellfireWallClockwise) {
         hellfireWallAngle = -360.0D;
      }

      if (this.hellfireWallLeft == null) {
         this.hellfireWallLeft = new EntityHellfireBarrier(this.getEntityWorld(), this);
         this.hellfireWallLeft.wall = true;
         this.getEntityWorld().spawnEntity(this.hellfireWallLeft);
      }

      this.hellfireWallLeft.time = 0;
      this.hellfireWallLeft.posX = this.posX;
      this.hellfireWallLeft.posY = this.posY;
      this.hellfireWallLeft.posZ = this.posZ;
      this.hellfireWallLeft.rotation = hellfireWallNormal * hellfireWallAngle;
      if (this.hellfireWallRight == null) {
         this.hellfireWallRight = new EntityHellfireBarrier(this.getEntityWorld(), this);
         this.hellfireWallRight.wall = true;
         this.getEntityWorld().spawnEntity(this.hellfireWallRight);
      }

      this.hellfireWallRight.time = 0;
      this.hellfireWallRight.posX = this.posX;
      this.hellfireWallRight.posY = this.posY;
      this.hellfireWallRight.posZ = this.posZ;
      this.hellfireWallRight.rotation = 180.0D + hellfireWallNormal * hellfireWallAngle;
   }

   public void hellfireWallCleanup() {
      if (this.hellfireWallLeft != null) {
         this.hellfireWallLeft.setDead();
         this.hellfireWallLeft = null;
      }

      if (this.hellfireWallRight != null) {
         this.hellfireWallRight.setDead();
         this.hellfireWallRight = null;
      }

   }

   public void hellfireBarrierAttack(double angle) {
      if (this.hellfireBarriers.size() < 10) {
         this.triggerAttackCooldown();
         this.playAttackSound();
         EntityHellfireBarrier hellfireBarrier = new EntityHellfireBarrier(this.getEntityWorld(), this);
         this.getEntityWorld().spawnEntity(hellfireBarrier);
         hellfireBarrier.time = 0;
         hellfireBarrier.posX = this.posX;
         hellfireBarrier.posY = this.posY;
         hellfireBarrier.posZ = this.posZ;
         hellfireBarrier.rotation = angle;
         this.hellfireBarriers.add(hellfireBarrier);
      }
   }

   public void hellfireBarrierUpdate() {
      if (this.hellfireBarrierHealth <= 0) {
         this.hellfireBarrierHealth = 100;
         if (this.hellfireBarriers.size() > 0) {
            EntityHellfireBarrier hellfireBarrier = (EntityHellfireBarrier)this.hellfireBarriers.get(this.hellfireBarriers.size() - 1);
            hellfireBarrier.setDead();
            this.hellfireBarriers.remove(this.hellfireBarriers.size() - 1);
         }
      }

      for(EntityHellfireBarrier hellfireBarrier : this.hellfireBarriers) {
         hellfireBarrier.time = 0;
         hellfireBarrier.posX = this.posX;
         hellfireBarrier.posY = this.posY;
         hellfireBarrier.posZ = this.posZ;
      }

   }

   public void hellfireBarrierCleanup() {
      if (!this.getEntityWorld().isRemote && this.hellfireBarriers.size() >= 1) {
         for(EntityHellfireBarrier hellfireBarrier : this.hellfireBarriers) {
            hellfireBarrier.setDead();
         }

         this.hellfireBarriers = new ArrayList();
         this.hellfireBarrierHealth = 100;
      }
   }

   public boolean canBePushed() {
      return false;
   }

   public boolean isPotionApplicable(PotionEffect potionEffect) {
      if (potionEffect.getPotion() == MobEffects.WITHER) {
         return false;
      } else if (ObjectManager.getEffect("decay") != null && potionEffect.getPotion() == ObjectManager.getEffect("decay")) {
         return false;
      } else {
         super.isPotionApplicable(potionEffect);
         return true;
      }
   }

   public boolean canBurn() {
      return false;
   }

   public boolean isDamageEntityApplicable(Entity entity) {
      if (entity instanceof EntityPigZombie) {
         entity.setDead();
         return false;
      } else if (entity instanceof EntityIronGolem) {
         entity.setDead();
         return false;
      } else {
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!player.capabilities.disableDamage && player.posY > this.posY + CreatureManager.getInstance().config.bossAntiFlight) {
               return false;
            }
         }

         return super.isDamageEntityApplicable(entity);
      }
   }

   public boolean attackEntityFrom(DamageSource damageSrc, float damageAmount) {
      if (this.playerTargets != null && damageSrc.getTrueSource() != null && damageSrc.getTrueSource() instanceof EntityPlayer && !this.playerTargets.contains(damageSrc.getTrueSource())) {
         this.playerTargets.add((EntityPlayer)damageSrc.getTrueSource());
      }

      return super.attackEntityFrom(damageSrc, damageAmount);
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      super.readFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasKey("HellfireEnergy")) {
         this.hellfireEnergy = nbtTagCompound.getInteger("HellfireEnergy");
      }

      if (nbtTagCompound.hasKey("HellfireWallTime")) {
         this.hellfireWallTime = nbtTagCompound.getInteger("HellfireWallTime");
      }

      if (nbtTagCompound.hasKey("BelphIDs")) {
         NBTTagList belphIDs = nbtTagCompound.getTagList("BelphIDs", 10);

         for(int i = 0; i < belphIDs.tagCount(); ++i) {
            NBTTagCompound belphID = belphIDs.getCompoundTagAt(i);
            if (belphID.hasKey("ID")) {
               Entity entity = this.getEntityWorld().getEntityByID(belphID.getInteger("ID"));
               if (entity != null && entity instanceof EntityBelph) {
                  this.hellfireBelphMinions.add((EntityBelph)entity);
               }
            }
         }
      }

      if (nbtTagCompound.hasKey("BehemothIDs")) {
         NBTTagList behemothIDs = nbtTagCompound.getTagList("BehemothIDs", 10);

         for(int i = 0; i < behemothIDs.tagCount(); ++i) {
            NBTTagCompound behemothID = behemothIDs.getCompoundTagAt(i);
            if (behemothID.hasKey("ID")) {
               Entity entity = this.getEntityWorld().getEntityByID(behemothID.getInteger("ID"));
               if (entity != null && entity instanceof EntityBehemoth) {
                  this.hellfireBehemothMinions.add((EntityBehemoth)entity);
               }
            }
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      nbtTagCompound.setInteger("HellfireEnergy", this.hellfireEnergy);
      nbtTagCompound.setInteger("HellfireWallTime", this.hellfireWallTime);
      if (this.getBattlePhase() == 0) {
         NBTTagList belphIDs = new NBTTagList();

         for(EntityBelph entityBelph : this.hellfireBelphMinions) {
            NBTTagCompound belphID = new NBTTagCompound();
            belphID.setInteger("ID", entityBelph.getEntityId());
            belphIDs.appendTag(belphID);
         }

         nbtTagCompound.setTag("BelphIDs", belphIDs);
      }

      if (this.getBattlePhase() == 1) {
         NBTTagList behemothIDs = new NBTTagList();

         for(EntityBehemoth entityBehemoth : this.hellfireBehemothMinions) {
            NBTTagCompound behemothID = new NBTTagCompound();
            behemothID.setInteger("ID", entityBehemoth.getEntityId());
            behemothIDs.appendTag(behemothID);
         }

         nbtTagCompound.setTag("BehemothIDs", behemothIDs);
      }

   }

   protected void playStepSound(BlockPos pos, Block block) {
      if (!this.hasArenaCenter()) {
         super.playStepSound(pos, block);
      }
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
