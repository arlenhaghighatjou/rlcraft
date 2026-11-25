package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal;
import com.lycanitesmobs.core.entity.navigate.ArenaNode;
import com.lycanitesmobs.core.entity.navigate.ArenaNodeNetwork;
import com.lycanitesmobs.core.entity.navigate.ArenaNodeNetworkGrid;
import com.lycanitesmobs.core.entity.projectile.EntityDevilGatling;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAsmodeus extends BaseCreatureEntity implements IMob, IGroupHeavy, IGroupBoss {
   protected static final DataParameter<Byte> ANIMATION_STATES = EntityDataManager.createKey(EntityAsmodeus.class, DataSerializers.BYTE);
   public AttackRangedGoal aiRangedAttack;
   public boolean firstPlayerTargetCheck = false;
   public List<EntityAstaroth> astarothMinions = new ArrayList();
   public List<EntityGrell> grellMinions = new ArrayList();
   public int devilstarStreamTime = 0;
   public int devilstarStreamTimeMax = 100;
   public int devilstarStreamCharge = 400;
   public int devilstarStreamChargeMax = 400;
   public int hellshieldAstarothRespawnTime = 0;
   public int hellshieldAstarothRespawnTimeMax = 30;
   public int rebuildAstarothRespawnTime = 0;
   public int rebuildAstarothRespawnTimeMax = 40;
   public ArenaNodeNetwork arenaNodeNetwork;
   public ArenaNode currentArenaNode;
   public int arenaNodeChangeCooldown = 0;
   public int arenaNodeChangeCooldownMax = 200;
   public int arenaJumpingTime = 0;
   public int arenaJumpingTimeMax = 60;
   protected double jumpHeight = 6.0D;

   public EntityAsmodeus(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setAttackCooldownMax(30);
      this.hasJumpSound = true;
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
      this.tasks.addTask(this.nextIdleGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("grigori").setAntiFlight(true));
      this.aiRangedAttack = (new AttackRangedGoal(this)).setSpeed(1.0D).setStaminaTime(200).setStaminaDrainRate(3).setRange(90.0F).setChaseTime(0).setCheckSight(false);
      this.tasks.addTask(this.nextCombatGoalIndex++, this.aiRangedAttack);
      this.tasks.addTask(this.nextIdleGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("trite").setSummonRate(60).setSummonCap(3).setPerPlayer(true).setConditions((new GoalConditions()).setBattlePhase(0)));
      super.initEntityAI();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(ANIMATION_STATES, Byte.valueOf((byte)0));
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(200.0D, 50.0D, 200.0D).offset(0.0D, -25.0D, 0.0D);
   }

   public boolean isPersistant() {
      return this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityAsmodeus ? true : super.isPersistant();
   }

   public void onFirstSpawn() {
      super.onFirstSpawn();
      if (this.getArenaCenter() == null) {
         this.setArenaCenter(this.getPosition());
      }

   }

   public void setArenaCenter(BlockPos pos) {
      super.setArenaCenter(pos);
      this.arenaNodeNetwork = new ArenaNodeNetworkGrid(this.getEntityWorld(), pos, 3, 1, 3, 60);
      this.currentArenaNode = this.arenaNodeNetwork.getClosestNode(this.getPosition());
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         this.updatePhases();
         this.updateCurrentArenaNode();
         this.updateArenaMovement();
      }

      if (this.getEntityWorld().isRemote && this.updateTick % 200L == 0L) {
         if (!this.astarothMinions.isEmpty()) {
            for(EntityAstaroth minion : (EntityAstaroth[])this.astarothMinions.toArray(new EntityAstaroth[this.astarothMinions.size()])) {
               if (minion == null || !minion.isEntityAlive() || minion.getMasterTarget() != this) {
                  this.astarothMinions.remove(minion);
               }
            }
         }

         if (!this.grellMinions.isEmpty()) {
            for(EntityGrell minion : (EntityGrell[])this.grellMinions.toArray(new EntityGrell[this.grellMinions.size()])) {
               if (minion == null || !minion.isEntityAlive() || minion.getMasterTarget() != this) {
                  this.grellMinions.remove(minion);
               }
            }
         }
      }

      if (!this.getEntityWorld().isRemote) {
         byte animationState = 0;
         if (this.aiRangedAttack != null && this.aiRangedAttack.attackOnCooldown) {
            animationState += EntityAsmodeus.ANIMATION_STATES_ID.COOLDOWN.id;
         }

         this.dataManager.set(ANIMATION_STATES, Byte.valueOf(animationState));
      }

      if (this.getEntityWorld().isRemote && (((Byte)this.dataManager.get(ANIMATION_STATES)).byteValue() & EntityAsmodeus.ANIMATION_STATES_ID.COOLDOWN.id) > 0) {
         BlockPos particlePos = this.getFacingPosition(this, 13.0D, (double)(this.getRotationYawHead() - this.rotationYaw));

         for(int i = 0; i < 4; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)particlePos.getX() + (this.rand.nextDouble() - 0.5D) * 2.0D, (double)particlePos.getY() + (double)this.height * 0.2D + this.rand.nextDouble() * 2.0D, (double)particlePos.getZ() + (this.rand.nextDouble() - 0.5D) * 2.0D, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public boolean rollWanderChance() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public void updateCurrentArenaNode() {
      if (this.hasArenaCenter()) {
         if (this.arenaNodeChangeCooldown > 0) {
            --this.arenaNodeChangeCooldown;
         } else if (this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive()) {
            if (this.currentArenaNode != null) {
               this.setCurrentArenaNode(this.currentArenaNode.getClosestAdjacentNode(this.getAttackTarget().getPosition()));
            } else {
               this.setCurrentArenaNode(this.arenaNodeNetwork.getClosestNode(this.getAttackTarget().getPosition()));
            }

         } else {
            this.setCurrentArenaNode(this.arenaNodeNetwork.centralNode);
         }
      }
   }

   public void setCurrentArenaNode(ArenaNode arenaNode) {
      this.arenaNodeChangeCooldown = this.arenaNodeChangeCooldownMax;
      if (this.currentArenaNode != arenaNode) {
         this.currentArenaNode = arenaNode;
         if (this.currentArenaNode != null && this.currentArenaNode.pos != null) {
            this.arenaJumpingTime = this.arenaJumpingTimeMax;
            this.leap(200.0F, this.jumpHeight, this.currentArenaNode.pos);
            if (this.hasJumpSound) {
               this.playJumpSound();
            }
         }

      }
   }

   public void updateArenaMovement() {
      this.noClip = false;
      if (this.currentArenaNode != null && this.currentArenaNode.pos != null) {
         if (this.arenaJumpingTime > 0) {
            --this.arenaJumpingTime;
            if (this.updateTick % 4L == 0L) {
               double dropForce = -0.5D;
               this.noClip = this.posY > (double)(this.currentArenaNode.pos.getY() + 8);
               if (this.posY < (double)this.currentArenaNode.pos.getY()) {
                  this.setPosition(this.posX, (double)this.currentArenaNode.pos.getY(), this.posZ);
                  dropForce = 0.0D;
               }

               this.leap(200.0F, dropForce, this.currentArenaNode.pos);
            }

            if (this.arenaJumpingTime == 0) {
               this.playStepSound(this.currentArenaNode.pos.down(), this.getEntityWorld().getBlockState(this.currentArenaNode.pos.down()).getBlock());
            }

         } else {
            BlockPos arenaPos = this.currentArenaNode.pos;
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
      }
   }

   public void updatePhases() {
      int playerCount = Math.max(this.playerTargets.size(), 1);
      if (this.getBattlePhase() == 0) {
         if (this.devilstarStreamTime > 0) {
            --this.devilstarStreamTime;
            if (this.updateTick % 10L == 0L) {
               for(float angle = 0.0F; angle < 360.0F; angle += 10.0F) {
                  this.attackDevilstar(angle);
               }
            }
         } else if (this.devilstarStreamCharge > 0) {
            --this.devilstarStreamCharge;
         } else {
            this.devilstarStreamCharge = this.devilstarStreamChargeMax;
            this.devilstarStreamTime = this.devilstarStreamTimeMax;
         }
      } else if (this.getBattlePhase() == 1 && this.updateTick % 20L == 0L) {
         if (this.astarothMinions.isEmpty() && this.hellshieldAstarothRespawnTime-- <= 0) {
            for(int i = 0; i < 2 * playerCount; ++i) {
               EntityAstaroth minion = (EntityAstaroth)CreatureManager.getInstance().getCreature("astaroth").createEntity(this.getEntityWorld());
               this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 0.0D);
               minion.setSizeScale(2.5D);
               this.astarothMinions.add(minion);
            }

            this.hellshieldAstarothRespawnTime = this.hellshieldAstarothRespawnTimeMax;
         }
      } else if (this.updateTick % 20L == 0L) {
         if (this.astarothMinions.size() < playerCount * 4 && this.rebuildAstarothRespawnTime-- <= 0) {
            for(int i = 0; i < playerCount; ++i) {
               EntityAstaroth minion = (EntityAstaroth)CreatureManager.getInstance().getCreature("astaroth").createEntity(this.getEntityWorld());
               this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 0.0D);
               minion.setSizeScale(2.5D);
               this.astarothMinions.add(minion);
            }

            this.rebuildAstarothRespawnTime = this.rebuildAstarothRespawnTimeMax;
         }

         if (this.grellMinions.size() < playerCount * 6 && this.updateTick % 10L * 20L == 0L) {
            for(int i = 0; i < 5 * playerCount; ++i) {
               EntityGrell minion = (EntityGrell)CreatureManager.getInstance().getCreature("grell").createEntity(this.getEntityWorld());
               this.summonMinion(minion, this.getRNG().nextDouble() * 360.0D, 10.0D);
               minion.posY += (double)(10 + this.getRNG().nextInt(20));
               this.grellMinions.add(minion);
            }
         }

         if (!this.astarothMinions.isEmpty()) {
            float healAmount = (float)this.astarothMinions.size();
            if ((double)((this.getHealth() + healAmount) / this.getMaxHealth()) <= 0.2D) {
               this.heal(healAmount * 2.0F);
            }
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

   public boolean canAttackEntity(EntityLivingBase target) {
      return !(target instanceof EntityTrite) && !(target instanceof EntityGrell) && !(target instanceof EntityAstaroth) ? super.canAttackEntity(target) : false;
   }

   public void attackRanged(Entity target, float range) {
      for(int i = 0; i < 5; ++i) {
         this.fireProjectile(EntityDevilGatling.class, target, range, 0.0F, new Vec3d(0.0D, -24.0D, 0.0D), 4.0F, 2.0F, 8.0F);
      }

      this.attackHitscan(target, target instanceof EntityPlayer ? 1.0D : 10.0D);
   }

   public void attackDevilstar(float angle) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("devilstar");
      if (projectileInfo != null) {
         BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
         projectile.setProjectileScale(4.0F);
         BlockPos offset = this.getFacingPosition(this, 8.0D, (double)angle);
         projectile.posX = (double)offset.getX();
         projectile.posY = (double)offset.getY() + (double)this.height * 0.5D;
         projectile.posZ = (double)offset.getZ();
         float range = 20.0F + 20.0F * this.getRNG().nextFloat();
         BlockPos target = this.getFacingPosition(this, (double)range, (double)angle);
         double d0 = (double)target.getX() - projectile.posX;
         double d1 = (double)target.getY() - projectile.posY;
         double d2 = (double)target.getZ() - projectile.posZ;
         float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.1F;
         float velocity = 1.2F;
         projectile.shoot(d0, d1 + (double)f1, d2, velocity, 0.0F);
         this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
         this.getEntityWorld().spawnEntity(projectile);
      }
   }

   public void onDeath(DamageSource damageSource) {
      if (!this.getEntityWorld().isRemote && CreatureManager.getInstance().getCreature("trite").enabled) {
         int j = 6 + this.rand.nextInt(20) + this.getEntityWorld().getDifficulty().getDifficultyId() * 4;

         for(int k = 0; k < j; ++k) {
            float f = ((float)(k % 2) - 0.5F) * this.width / 4.0F;
            float f1 = ((float)(k / 2) - 0.5F) * this.width / 4.0F;
            EntityTrite trite = (EntityTrite)CreatureManager.getInstance().getCreature("trite").createEntity(this.getEntityWorld());
            trite.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
            trite.setMinion(true);
            trite.applyVariant(this.getVariantIndex());
            this.getEntityWorld().spawnEntity(trite);
            if (this.getAttackTarget() != null) {
               trite.setRevengeTarget(this.getAttackTarget());
            }
         }
      }

      super.onDeath(damageSource);
   }

   public void onMinionDeath(EntityLivingBase minion, DamageSource damageSource) {
      if (minion instanceof EntityAstaroth && this.astarothMinions.contains(minion)) {
         this.astarothMinions.remove(minion);
      }

      if (minion instanceof EntityGrell && this.grellMinions.contains(minion)) {
         this.grellMinions.remove(minion);
      }

      super.onMinionDeath(minion, damageSource);
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return this.isBlocking() ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBurn() {
      return false;
   }

   public boolean isBlocking() {
      if (this.getEntityWorld().isRemote) {
         return super.isBlocking();
      } else {
         return this.getBattlePhase() == 1 && !this.astarothMinions.isEmpty();
      }
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
      if (nbtTagCompound.hasKey("DevilstarStreamCharge")) {
         this.devilstarStreamCharge = nbtTagCompound.getInteger("DevilstarStreamCharge");
      }

      if (nbtTagCompound.hasKey("DevilstarStreamTime")) {
         this.devilstarStreamTime = nbtTagCompound.getInteger("DevilstarStreamTime");
      }

      if (nbtTagCompound.hasKey("AstarothIDs")) {
         NBTTagList astarothIDs = nbtTagCompound.getTagList("AstarothIDs", 10);

         for(int i = 0; i < astarothIDs.tagCount(); ++i) {
            NBTTagCompound astarothID = astarothIDs.getCompoundTagAt(i);
            if (astarothID.hasKey("ID")) {
               Entity entity = this.getEntityWorld().getEntityByID(astarothID.getInteger("ID"));
               if (entity != null && entity instanceof EntityAstaroth) {
                  this.astarothMinions.add((EntityAstaroth)entity);
               }
            }
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      nbtTagCompound.setInteger("DevilstarStreamCharge", this.devilstarStreamCharge);
      nbtTagCompound.setInteger("DevilstarStreamTime", this.devilstarStreamTime);
      if (this.getBattlePhase() > 0) {
         NBTTagList astarothIDs = new NBTTagList();

         for(EntityAstaroth entityAstaroth : this.astarothMinions) {
            NBTTagCompound astarothID = new NBTTagCompound();
            astarothID.setInteger("ID", entityAstaroth.getEntityId());
            astarothIDs.appendTag(astarothID);
         }

         nbtTagCompound.setTag("AstarothIDs", astarothIDs);
      }

   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public static enum ANIMATION_STATES_ID {
      SNAP_TO_ARENA((byte)1),
      COOLDOWN((byte)2);

      public final byte id;

      private ANIMATION_STATES_ID(byte value) {
         this.id = value;
      }

      public byte getAttributeValue() {
         return this.id;
      }
   }
}
