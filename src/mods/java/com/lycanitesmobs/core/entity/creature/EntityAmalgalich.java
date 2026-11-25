package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.EffectAuraGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.ForceGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.GrowGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SuicideGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAmalgalich extends BaseCreatureEntity implements IMob, IGroupHeavy, IGroupBoss {
   private ForceGoal consumptionGoalP0;
   private ForceGoal consumptionGoalP2;
   private int consumptionDuration = 300;
   private int consumptionWindUp = 60;
   private int consumptionAnimationTime = 0;

   public EntityAmalgalich(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = true;
      this.setAttackCooldownMax(30);
      this.hasJumpSound = false;
      this.trueSight = true;
      this.entityCollisionReduction = 1.0F;
      this.setupMob();
      this.hitAreaWidthScale = 2.0F;
      this.damageMax = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
      this.damageLimit = (float)BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(this.nextFindTargetIndex, new FindNearbyPlayersGoal(this));
      this.consumptionDuration = 300;
      this.consumptionWindUp = 60;
      this.consumptionAnimationTime = 0;
      int consumptionGoalCooldown = 400;
      this.tasks.addTask(this.nextIdleGoalIndex, new FaceTargetGoal(this));
      this.tasks.addTask(this.nextIdleGoalIndex, new HealWhenNoPlayersGoal(this));
      this.tasks.addTask(this.nextIdleGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("banshee").setAntiFlight(true));
      this.tasks.addTask(this.nextIdleGoalIndex, (new FireProjectilesGoal(this)).setProjectile("spectralbolt").setFireRate(40).setVelocity(1.0F).setScale(8.0F).setAllPlayers(true).setOverhead(true).setOffset(new Vec3d(0.0D, 6.0D, 0.0D)));
      this.tasks.addTask(this.nextIdleGoalIndex, (new FireProjectilesGoal(this)).setProjectile("spectralbolt").setFireRate(60).setVelocity(1.0F).setScale(8.0F).setOverhead(true).setOffset(new Vec3d(0.0D, 8.0D, 0.0D)));
      this.tasks.addTask(this.nextIdleGoalIndex, (new EffectAuraGoal(this)).setEffect("decay").setAmplifier(0).setEffectSeconds(5).setRange(52.0F).setCheckSight(false));
      this.tasks.addTask(this.nextIdleGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("reaper").setSummonRate(100).setSummonCap(5).setPerPlayer(true).setConditions((new GoalConditions()).setBattlePhase(0)));
      this.consumptionGoalP0 = (new ForceGoal(this)).setRange(64.0F).setCooldown(consumptionGoalCooldown).setDuration(this.consumptionDuration).setWindUp(this.consumptionWindUp).setForce(-0.75F).setPhase(0).setDismount(true);
      this.tasks.addTask(this.nextIdleGoalIndex, (new EffectAuraGoal(this)).setRange(1.0F).setCooldown(consumptionGoalCooldown + this.consumptionWindUp).setDuration(this.consumptionDuration - this.consumptionWindUp).setTickRate(5).setDamageAmount(1000.0F).setCheckSight(false).setTargetTypes((byte)(BaseCreatureEntity.TARGET_TYPES.ALLY.id | BaseCreatureEntity.TARGET_TYPES.ENEMY.id)).setConditions((new GoalConditions()).setBattlePhase(0)));
      this.tasks.addTask(this.nextIdleGoalIndex, this.consumptionGoalP0);
      this.tasks.addTask(this.nextIdleGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("geist").setSummonRate(100).setSummonCap(5).setPerPlayer(true).setConditions((new GoalConditions()).setBattlePhase(1)));
      this.tasks.addTask(this.nextIdleGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("epion").setSummonRate(100).setSummonCap(3).setPerPlayer(true).setConditions((new GoalConditions()).setBattlePhase(1)));
      this.consumptionGoalP2 = (new ForceGoal(this)).setRange(64.0F).setCooldown(consumptionGoalCooldown).setDuration(this.consumptionDuration).setWindUp(this.consumptionWindUp).setForce(-0.75F).setPhase(2).setDismount(true);
      this.tasks.addTask(this.nextIdleGoalIndex, this.consumptionGoalP2);
      this.tasks.addTask(this.nextIdleGoalIndex, (new EffectAuraGoal(this)).setRange(1.0F).setCooldown(consumptionGoalCooldown + this.consumptionWindUp).setDuration(this.consumptionDuration - this.consumptionWindUp).setTickRate(5).setDamageAmount(1000.0F).setCheckSight(false).setTargetTypes((byte)(BaseCreatureEntity.TARGET_TYPES.ALLY.id | BaseCreatureEntity.TARGET_TYPES.ENEMY.id)).setConditions((new GoalConditions()).setBattlePhase(2)));
      this.tasks.addTask(this.nextIdleGoalIndex, (new FireProjectilesGoal(this)).setProjectile("lobdarklings").setFireRate(200).setVelocity(0.8F).setScale(2.0F).setRandomCount(3).setAngle(360).setPhase(2));
      super.initEntityAI();
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

   public void onLivingUpdate() {
      super.onLivingUpdate();
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

      if (this.getEntityWorld().isRemote) {
         if (!this.extraAnimation01()) {
            this.consumptionAnimationTime = this.consumptionDuration;
         } else {
            --this.consumptionAnimationTime;
         }
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

   public boolean rollWanderChance() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public boolean canAttackEntity(EntityLivingBase target) {
      return !(target instanceof EntityBanshee) && !(target instanceof EntityReaper) && !(target instanceof EntityGeist) ? super.canAttackEntity(target) : false;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("spectralbolt", target, range, 0.0F, new Vec3d(0.0D, 8.0D, 0.0D), 1.2F, 6.0F, 0.0F);
      super.attackRanged(target, range);
   }

   public float getConsumptionAnimation() {
      if (this.consumptionAnimationTime >= this.consumptionDuration) {
         return 0.0F;
      } else {
         int windUpThreshhold = this.consumptionDuration - this.consumptionWindUp;
         if (this.consumptionAnimationTime > windUpThreshhold) {
            return 1.0F - (float)(this.consumptionAnimationTime - windUpThreshhold) / (float)this.consumptionWindUp;
         } else {
            float finishingTime = (float)this.consumptionWindUp / 2.0F;
            return (float)this.consumptionAnimationTime < finishingTime ? (float)this.consumptionAnimationTime / finishingTime : 1.0F;
         }
      }
   }

   public boolean extraAnimation01() {
      if (this.getEntityWorld().isRemote) {
         return super.extraAnimation01();
      } else if (this.getBattlePhase() == 0) {
         return this.consumptionGoalP0.cooldownTime <= 0;
      } else if (this.getBattlePhase() == 2) {
         return this.consumptionGoalP2.cooldownTime <= 0;
      } else {
         return super.extraAnimation01();
      }
   }

   public boolean addMinion(EntityLivingBase minion) {
      boolean minionAdded = super.addMinion(minion);
      if (minionAdded && minion instanceof BaseCreatureEntity) {
         BaseCreatureEntity minionCreature = (BaseCreatureEntity)minion;
         minionCreature.targetTasks.addTask(minionCreature.nextFindTargetIndex++, new CopyMasterAttackTargetGoal(minionCreature));
         if (minion instanceof EntityGeist) {
            minionCreature.tasks.addTask(minionCreature.nextIdleGoalIndex++, (new GrowGoal(minionCreature)).setGrowthAmount(0.1F).setTickRate(20));
            minionCreature.tasks.addTask(minionCreature.nextIdleGoalIndex++, (new SuicideGoal(minionCreature)).setCountdown(400));
         }
      }

      return minionAdded;
   }

   public void onMinionDeath(EntityLivingBase minion, DamageSource damageSource) {
      super.onMinionDeath(minion, damageSource);
      if (minion instanceof EntityEpion) {
         int extinguishWidth = 10;
         int extinguishHeight = 30;
         if (!this.getEntityWorld().isRemote) {
            for(int x = (int)minion.posX - extinguishWidth; x <= (int)minion.posX + extinguishWidth; ++x) {
               for(int y = (int)minion.posY - extinguishHeight; y <= (int)minion.posY + 2; ++y) {
                  for(int z = (int)minion.posZ - extinguishWidth; z <= (int)minion.posZ + extinguishWidth; ++z) {
                     Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                     if (block == ObjectManager.getBlock("shadowfire")) {
                        BlockPos placePos = new BlockPos(x, y, z);
                        this.getEntityWorld().setBlockToAir(placePos);
                     }
                  }
               }
            }
         }
      }

   }

   public void onTryToDamageMinion(EntityLivingBase minion, float damageAmount) {
      super.onTryToDamageMinion(minion, damageAmount);
      if (damageAmount >= 1000.0F) {
         minion.setDead();
         this.heal(25.0F);
      }

   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return this.isBlocking() ? true : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBurn() {
      return false;
   }

   public boolean isBlocking() {
      return super.isBlocking();
   }

   public boolean canAttackWhileBlocking() {
      return true;
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
   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
