package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAIAggroLook;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAIFollowOwner;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAIStareAttack;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAITarget;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAIWander;
import com.github.alexthe666.iceandfire.entity.ai.EntityAIAttackMeleeNoCooldown;
import com.github.alexthe666.iceandfire.event.EventLiving;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCockatrice extends EntityTameable implements IAnimatedEntity, IBlacklistedFromStatues, IVillagerFear {
   private int animationTick;
   private Animation currentAnimation;
   private static final DataParameter<Boolean> HEN = EntityDataManager.createKey(EntityCockatrice.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> STARING = EntityDataManager.createKey(EntityCockatrice.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityCockatrice.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> TAMING_PLAYER = EntityDataManager.createKey(EntityCockatrice.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> TAMING_LEVEL = EntityDataManager.createKey(EntityCockatrice.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityCockatrice.class, DataSerializers.VARINT);
   private boolean isSitting;
   public float sitProgress;
   private boolean isStaring;
   public float stareProgress;
   public int ticksStaring = 0;
   public static final Animation ANIMATION_JUMPAT = Animation.create(30);
   public static final Animation ANIMATION_WATTLESHAKE = Animation.create(20);
   public static final Animation ANIMATION_BITE = Animation.create(15);
   public static final Animation ANIMATION_SPEAK = Animation.create(10);
   public static final Animation ANIMATION_EAT = Animation.create(20);
   private CockatriceAIStareAttack aiStare;
   private EntityAIAttackMelee aiMelee;
   private boolean isMeleeMode = false;
   private EntityLivingBase targetedEntity;
   private int clientSideAttackTime;
   public static final float VIEW_RADIUS = 0.6F;
   public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("iceandfire", "cockatrice"));

   public EntityCockatrice(World worldIn) {
      super(worldIn);
      this.setSize(0.95F, 0.95F);
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 10 + this.world.rand.nextInt(5);
   }

   public boolean getCanSpawnHere() {
      return this.getRNG().nextInt(IceAndFire.CONFIG.cockatriceSpawnCheckChance + 1) == 0 && super.getCanSpawnHere();
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new CockatriceAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
      this.tasks.addTask(2, new CockatriceAIWander(this, 1.0D));
      this.tasks.addTask(3, this.aiSit = new EntityAISit(this));
      this.tasks.addTask(4, this.aiStare = new CockatriceAIStareAttack(this, 1.0D, 0, 15.0F));
      this.tasks.addTask(4, this.aiMelee = new EntityAIAttackMeleeNoCooldown(this, 1.5D, false));
      this.tasks.addTask(6, new CockatriceAIAggroLook(this));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityLivingBase.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new CockatriceAITargetItems(this, false));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(3, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(4, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(5, new CockatriceAITarget(this, EntityLivingBase.class, true, new Predicate<Entity>() {
         public boolean apply(@Nullable Entity entity) {
            return (entity instanceof IMob && EntityCockatrice.this.isTamed() && !(entity instanceof EntityCreeper) && !(entity instanceof EntityPigZombie) && !(entity instanceof EntityEnderman) || entity instanceof EntityPlayer || EventLiving.isAnimaniaFerret(entity)) && !EventLiving.isAnimaniaChicken(entity);
         }
      }));
      this.tasks.removeTask(this.aiMelee);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT;
   }

   public boolean isOnSameTeam(Entity entityIn) {
      return EventLiving.isAnimaniaChicken(entityIn) || super.isOnSameTeam(entityIn);
   }

   public boolean attackEntityFrom(DamageSource source, float damage) {
      if (source.getTrueSource() != null && EventLiving.isAnimaniaFerret(source.getTrueSource())) {
         damage *= 5.0F;
      }

      return super.attackEntityFrom(source, damage);
   }

   private boolean canUseStareOn(Entity entity) {
      return (!(entity instanceof IBlacklistedFromStatues) || ((IBlacklistedFromStatues)entity).canBeTurnedToStone()) && !EventLiving.isAnimaniaFerret(entity);
   }

   private void switchAI(boolean melee) {
      if (melee) {
         this.tasks.removeTask(this.aiStare);
         if (this.aiMelee != null) {
            this.tasks.addTask(4, this.aiMelee);
         }

         this.isMeleeMode = true;
      } else {
         this.tasks.removeTask(this.aiMelee);
         if (this.aiStare != null) {
            this.tasks.addTask(4, this.aiStare);
         }

         this.isMeleeMode = false;
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.isStaring()) {
         return false;
      } else if (this.getRNG().nextBoolean()) {
         if (this.getAnimation() != ANIMATION_JUMPAT && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(ANIMATION_JUMPAT);
         }

         return false;
      } else {
         if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_JUMPAT) {
            this.setAnimation(ANIMATION_BITE);
         }

         return false;
      }
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
   }

   public boolean canMove() {
      return !this.isSitting() && (this.getAnimation() != ANIMATION_JUMPAT || this.getAnimationTick() >= 7);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HEN, Boolean.valueOf(false));
      this.dataManager.register(STARING, Boolean.valueOf(false));
      this.dataManager.register(TARGET_ENTITY, Integer.valueOf(0));
      this.dataManager.register(TAMING_PLAYER, Integer.valueOf(0));
      this.dataManager.register(TAMING_LEVEL, Integer.valueOf(0));
      this.dataManager.register(COMMAND, Integer.valueOf(0));
   }

   public void setTargetedEntity(int entityId) {
      this.dataManager.set(TARGET_ENTITY, Integer.valueOf(entityId));
   }

   public boolean hasTargetedEntity() {
      return ((Integer)this.dataManager.get(TARGET_ENTITY)).intValue() != 0;
   }

   public void setTamingPlayer(int entityId) {
      this.dataManager.set(TAMING_PLAYER, Integer.valueOf(entityId));
   }

   public boolean hasTamingPlayer() {
      return ((Integer)this.dataManager.get(TAMING_PLAYER)).intValue() != 0;
   }

   @Nullable
   public Entity getTamingPlayer() {
      if (!this.hasTamingPlayer()) {
         return null;
      } else if (this.world.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(TAMING_PLAYER)).intValue());
            if (entity instanceof EntityLivingBase) {
               this.targetedEntity = (EntityLivingBase)entity;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.world.getEntityByID(((Integer)this.dataManager.get(TAMING_PLAYER)).intValue());
      }
   }

   @Nullable
   public EntityLivingBase getTargetedEntity() {
      boolean blindness = this.isPotionActive(MobEffects.BLINDNESS) || this.getAttackTarget() != null && this.getAttackTarget().isPotionActive(MobEffects.BLINDNESS);
      if (blindness) {
         return null;
      } else if (!this.hasTargetedEntity()) {
         return null;
      } else if (this.world.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(TARGET_ENTITY)).intValue());
            if (entity instanceof EntityLivingBase) {
               this.targetedEntity = (EntityLivingBase)entity;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getAttackTarget();
      }
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      super.notifyDataManagerChange(key);
      if (TARGET_ENTITY.equals(key)) {
         this.clientSideAttackTime = 0;
         this.targetedEntity = null;
      }

   }

   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      tag.setBoolean("Hen", this.isHen());
      tag.setBoolean("Staring", this.isStaring());
      tag.setInteger("TamingLevel", this.getTamingLevel());
      tag.setInteger("TamingPlayer", ((Integer)this.dataManager.get(TAMING_PLAYER)).intValue());
      tag.setInteger("Command", this.getCommand());
   }

   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      this.setHen(tag.getBoolean("Hen"));
      this.setStaring(tag.getBoolean("Staring"));
      this.setTamingLevel(tag.getInteger("TamingLevel"));
      this.setTamingPlayer(tag.getInteger("TamingPlayer"));
      this.setCommand(tag.getInteger("Command"));
   }

   public boolean isSitting() {
      if (this.world.isRemote) {
         boolean isSitting = (((Byte)this.dataManager.get(TAMED)).byteValue() & 1) != 0;
         this.isSitting = isSitting;
         return isSitting;
      } else {
         return this.isSitting;
      }
   }

   public void setSitting(boolean sitting) {
      super.setSitting(sitting);
      if (!this.world.isRemote) {
         this.isSitting = sitting;
      }

   }

   public void fall(float distance, float damageMultiplier) {
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setHen(this.getRNG().nextBoolean());
      return livingdata;
   }

   public void setHen(boolean hen) {
      this.dataManager.set(HEN, Boolean.valueOf(hen));
   }

   public boolean isHen() {
      return ((Boolean)this.dataManager.get(HEN)).booleanValue();
   }

   public void setTamingLevel(int level) {
      this.dataManager.set(TAMING_LEVEL, Integer.valueOf(level));
   }

   public int getTamingLevel() {
      return ((Integer)this.dataManager.get(TAMING_LEVEL)).intValue().intValue();
   }

   public void setCommand(int command) {
      this.dataManager.set(COMMAND, Integer.valueOf(command));
      if (command == 1) {
         this.setSitting(true);
      } else {
         this.setSitting(false);
      }

   }

   public int getCommand() {
      return ((Integer)this.dataManager.get(COMMAND)).intValue().intValue();
   }

   public boolean isStaring() {
      return this.world.isRemote ? (this.isStaring = ((Boolean)this.dataManager.get(STARING)).booleanValue().booleanValue()) : this.isStaring;
   }

   public void setStaring(boolean staring) {
      this.dataManager.set(STARING, Boolean.valueOf(staring));
      if (!this.world.isRemote) {
         this.isStaring = staring;
      }

   }

   public void forcePreyToLook(EntityLiving mob) {
      mob.getLookHelper().setLookPosition(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)mob.getHorizontalFaceSpeed(), (float)mob.getVerticalFaceSpeed());
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      boolean flag = player.getHeldItem(hand).getItem() == Items.NAME_TAG || player.getHeldItem(hand).getItem() == Items.LEAD;
      if (flag) {
         player.getHeldItem(hand).interactWithEntity(player, this, hand);
         return true;
      } else if (player.getHeldItem(hand).getItem() == Items.POISONOUS_POTATO) {
         return super.processInteract(player, hand);
      } else {
         if (this.isTamed() && this.isOwner(player)) {
            if (FoodUtils.isSeeds(player.getHeldItem(hand)) || player.getHeldItem(hand).getItem() == Items.ROTTEN_FLESH) {
               if (this.getHealth() < this.getMaxHealth()) {
                  this.heal(8.0F);
                  this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                  player.getHeldItem(hand).shrink(1);
               }

               return true;
            }

            if (player.getHeldItem(hand).isEmpty()) {
               this.setCommand(this.getCommand() + 1);
               if (this.getCommand() > 2) {
                  this.setCommand(0);
               }

               player.sendStatusMessage(new TextComponentTranslation("cockatrice.command." + this.getCommand(), new Object[0]), true);
               this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1.0F, 1.0F);
               return true;
            }
         }

         return false;
      }
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityPlayer) {
         this.setAttackTarget((EntityLivingBase)null);
      }

      if (this.isSitting() && this.getCommand() != 1) {
         this.setSitting(false);
      }

      if (this.isSitting() && this.getAttackTarget() != null) {
         this.setAttackTarget((EntityLivingBase)null);
      }

      if (!this.world.isRemote) {
         if (this.getAttackTarget() != null && !this.getAttackTarget().isDead) {
            if (this.isStaring() || this.shouldStareAttack(this.getAttackTarget())) {
               this.setTargetedEntity(this.getAttackTarget().getEntityId());
            }
         } else {
            this.setTargetedEntity(0);
         }
      }

      if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 7) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 8.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         }
      }

      if (this.getAnimation() == ANIMATION_JUMPAT && this.getAttackTarget() != null) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         double d0 = this.getAttackTarget().posX - this.posX;
         double d1 = this.getAttackTarget().posZ - this.posZ;
         float leap = MathHelper.sqrt(d0 * d0 + d1 * d1);
         if (dist <= 16.0D && this.onGround && this.getAnimationTick() > 7 && this.getAnimationTick() < 12) {
            if ((double)leap >= 1.0E-4D) {
               this.motionX += d0 / (double)leap * 0.800000011920929D + this.motionX * 0.20000000298023224D;
               this.motionZ += d1 / (double)leap * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
            }

            this.motionY = 0.5D;
         }

         if (dist < 4.0D && this.getAnimationTick() > 10) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            if ((double)leap >= 1.0E-4D) {
               EntityLivingBase var10000 = this.getAttackTarget();
               var10000.motionX += d0 / (double)leap * 0.800000011920929D + this.motionX * 0.20000000298023224D;
               var10000 = this.getAttackTarget();
               var10000.motionZ += d1 / (double)leap * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
            }
         }
      }

      boolean sitting = this.isSitting();
      if (sitting && this.sitProgress < 20.0F) {
         this.sitProgress += 0.5F;
      } else if (!sitting && this.sitProgress > 0.0F) {
         this.sitProgress -= 0.5F;
      }

      boolean staring = this.isStaring();
      if (staring && this.stareProgress < 20.0F) {
         this.stareProgress += 0.5F;
      } else if (!staring && this.stareProgress > 0.0F) {
         this.stareProgress -= 0.5F;
      }

      if (!this.world.isRemote) {
         if (staring) {
            ++this.ticksStaring;
         } else {
            this.ticksStaring = 0;
         }
      }

      if (!this.world.isRemote && staring && (this.getAttackTarget() == null || this.shouldMelee())) {
         this.setStaring(false);
      }

      if (this.getAttackTarget() != null) {
         this.getLookHelper().setLookPosition(this.getAttackTarget().posX, this.getAttackTarget().posY + (double)this.getAttackTarget().getEyeHeight(), this.getAttackTarget().posZ, (float)this.getHorizontalFaceSpeed(), (float)this.getVerticalFaceSpeed());
         if (!this.shouldMelee() && this.getAttackTarget() instanceof EntityLiving && !(this.getAttackTarget() instanceof EntityPlayer)) {
            this.forcePreyToLook((EntityLiving)this.getAttackTarget());
         }
      }

      boolean blindness = this.isPotionActive(MobEffects.BLINDNESS) || this.getAttackTarget() != null && this.getAttackTarget().isPotionActive(MobEffects.BLINDNESS);
      if (blindness) {
         this.setStaring(false);
      }

      if (!this.world.isRemote && !blindness && this.getAttackTarget() != null && EntityGorgon.isEntityLookingAt(this, this.getAttackTarget(), 0.6000000238418579D) && EntityGorgon.isEntityLookingAt(this.getAttackTarget(), this, 0.6000000238418579D) && !this.shouldMelee()) {
         if (!this.isStaring()) {
            this.setStaring(true);
         } else {
            int attackStrength = this.getFriendsCount(this.getAttackTarget());
            if (this.world.getDifficulty() == EnumDifficulty.HARD) {
               ++attackStrength;
            }

            this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.WITHER, 10, 2 + Math.min(1, attackStrength)));
            this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, Math.min(4, attackStrength)));
            this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
            if (attackStrength >= 2 && this.getAttackTarget().ticksExisted % 40 == 0) {
               this.getAttackTarget().attackEntityFrom(DamageSource.WITHER, (float)(attackStrength - 1));
            }

            this.getAttackTarget().setRevengeTarget(this);
            if (!this.isTamed() && this.getAttackTarget() instanceof EntityPlayer) {
               this.setTamingPlayer(this.getAttackTarget().getEntityId());
               this.setTamingLevel(this.getTamingLevel() + 1);
               if (this.getTamingLevel() % 100 == 0) {
                  this.world.setEntityState(this, (byte)46);
               }

               if (this.getTamingLevel() >= 1000) {
                  this.world.setEntityState(this, (byte)45);
                  if (this.getTamingPlayer() != null && this.getTamingPlayer() instanceof EntityPlayer) {
                     this.setTamedBy((EntityPlayer)this.getTamingPlayer());
                  }

                  this.setAttackTarget((EntityLivingBase)null);
                  this.setTamingPlayer(0);
                  this.setTargetedEntity(0);
               }
            }
         }
      }

      if (!this.world.isRemote && this.getAttackTarget() == null && this.getRNG().nextInt(300) == 0 && this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_WATTLESHAKE);
      }

      if (!this.world.isRemote) {
         if (this.shouldMelee() && !this.isMeleeMode) {
            this.switchAI(true);
         }

         if (!this.shouldMelee() && this.isMeleeMode) {
            this.switchAI(false);
         }
      }

      if (this.world.isRemote && this.getTargetedEntity() != null && EntityGorgon.isEntityLookingAt(this, this.getTargetedEntity(), 0.6000000238418579D) && EntityGorgon.isEntityLookingAt(this.getTargetedEntity(), this, 0.6000000238418579D) && this.isStaring() && this.hasTargetedEntity()) {
         if (this.clientSideAttackTime < this.getAttackDuration()) {
            ++this.clientSideAttackTime;
         }

         EntityLivingBase entitylivingbase = this.getTargetedEntity();
         if (entitylivingbase != null) {
            this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);
            this.getLookHelper().onUpdateLook();
            double d5 = (double)this.getAttackAnimationScale(0.0F);
            double d0 = entitylivingbase.posX - this.posX;
            double d1 = entitylivingbase.posY + (double)(entitylivingbase.height * 0.5F) - (this.posY + (double)this.getEyeHeight());
            double d2 = entitylivingbase.posZ - this.posZ;
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d0 = d0 / d3;
            d1 = d1 / d3;
            d2 = d2 / d3;
            double d4 = this.rand.nextDouble();

            while(d4 < d3) {
               d4 += 1.8D - d5 + this.rand.nextDouble() * (1.7D - d5);
               this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + d0 * d4, this.posY + d1 * d4 + (double)this.getEyeHeight(), this.posZ + d2 * d4, 0.0D, 0.0D, 0.0D, new int[]{3484199});
            }
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private int getFriendsCount(EntityLivingBase attackTarget) {
      if (this.getAttackTarget() == null) {
         return 0;
      } else {
         float dist = (float)IceAndFire.CONFIG.cockatriceChickenSearchLength;
         List<EntityCockatrice> list = this.world.getEntitiesWithinAABB(EntityCockatrice.class, this.getEntityBoundingBox().expand((double)dist, (double)dist, (double)dist));
         int i = 0;

         for(EntityCockatrice cockatrice : list) {
            if (!cockatrice.isEntityEqual(this) && cockatrice.getAttackTarget() != null && cockatrice.getAttackTarget() == this.getAttackTarget()) {
               boolean bothLooking = EntityGorgon.isEntityLookingAt(cockatrice, cockatrice.getAttackTarget(), 0.6000000238418579D) && EntityGorgon.isEntityLookingAt(cockatrice.getAttackTarget(), cockatrice, 0.6000000238418579D);
               if (bothLooking) {
                  ++i;
               }
            }
         }

         return i;
      }
   }

   public float getAttackAnimationScale(float f) {
      return ((float)this.clientSideAttackTime + f) / (float)this.getAttackDuration();
   }

   public boolean shouldStareAttack(Entity entity) {
      return this.getDistance(entity) > 5.0F;
   }

   public int getAttackDuration() {
      return 80;
   }

   private boolean shouldMelee() {
      boolean blindness = this.isPotionActive(MobEffects.BLINDNESS) || this.getAttackTarget() != null && this.getAttackTarget().isPotionActive(MobEffects.BLINDNESS);
      return this.getAttackTarget() != null && ((double)this.getDistance(this.getAttackTarget()) < 4.0D || EventLiving.isAnimaniaFerret(this.getAttackTarget()) || blindness || !this.canUseStareOn(this.getAttackTarget()));
   }

   public void travel(float strafe, float forward, float vertical) {
      if (!this.canMove() && !this.isBeingRidden()) {
         strafe = 0.0F;
         forward = 0.0F;
      }

      super.travel(strafe, forward, vertical);
   }

   public void playLivingSound() {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_SPEAK);
      }

      super.playLivingSound();
   }

   protected void playHurtSound(DamageSource source) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_SPEAK);
      }

      super.playHurtSound(source);
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable ageable) {
      return null;
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{NO_ANIMATION, ANIMATION_JUMPAT, ANIMATION_WATTLESHAKE, ANIMATION_BITE, ANIMATION_SPEAK, ANIMATION_EAT};
   }

   public boolean canBeTurnedToStone() {
      return false;
   }

   public boolean isTargetBlocked(Vec3d target) {
      if (target != null) {
         RayTraceResult rayTrace = this.world.rayTraceBlocks(new Vec3d(this.getPosition()), target, false);
         if (rayTrace != null && rayTrace.hitVec != null) {
            BlockPos pos = new BlockPos(rayTrace.hitVec);
            if (!this.world.isAirBlock(pos)) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.COCKATRICE_IDLE;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.COCKATRICE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return ModSounds.COCKATRICE_DIE;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 45) {
         this.playEffect(true);
      } else if (id == 46) {
         this.playEffect(false);
      } else {
         super.handleStatusUpdate(id);
      }

   }

   protected void playEffect(boolean play) {
      EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;
      if (!play) {
         enumparticletypes = EnumParticleTypes.DAMAGE_INDICATOR;
      }

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
      }

   }
}
