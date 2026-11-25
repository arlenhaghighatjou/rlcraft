package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.AquaticAITempt;
import com.github.alexthe666.iceandfire.entity.ai.DragonAIAirTarget;
import com.github.alexthe666.iceandfire.entity.ai.DragonAILookIdle;
import com.github.alexthe666.iceandfire.entity.ai.DragonAIMate;
import com.github.alexthe666.iceandfire.entity.ai.DragonAITarget;
import com.github.alexthe666.iceandfire.entity.ai.DragonAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.DragonAIWander;
import com.github.alexthe666.iceandfire.entity.ai.DragonAIWatchClosest;
import com.google.common.base.Predicate;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityFireDragon extends EntityDragonBase {
   public static Animation ANIMATION_FIRECHARGE;
   public static final float[] growth_stage_1 = new float[]{1.0F, 3.0F};
   public static final float[] growth_stage_2 = new float[]{3.0F, 7.0F};
   public static final float[] growth_stage_3 = new float[]{7.0F, 12.5F};
   public static final float[] growth_stage_4 = new float[]{12.5F, 20.0F};
   public static final float[] growth_stage_5 = new float[]{20.0F, 30.0F};
   public static final ResourceLocation FEMALE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/fire_dragon_female"));
   public static final ResourceLocation MALE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/fire_dragon_male"));
   public static final ResourceLocation SKELETON_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/fire_dragon_skeleton"));

   public EntityFireDragon(World worldIn) {
      super(worldIn, 1.0D, (double)(1 + IceAndFire.CONFIG.dragonAttackDamage), (double)IceAndFire.CONFIG.dragonHealth * 0.04D, (double)IceAndFire.CONFIG.dragonHealth, 0.15000000596046448D, 0.4000000059604645D);
      this.setSize(0.78F, 1.2F);
      this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
      this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
      this.setPathPriority(PathNodeType.LAVA, 8.0F);
      this.isImmuneToFire = true;
      this.ignoreFrustumCheck = true;
      ANIMATION_SPEAK = Animation.create(20);
      ANIMATION_BITE = Animation.create(35);
      ANIMATION_SHAKEPREY = Animation.create(65);
      ANIMATION_TAILWHACK = Animation.create(40);
      ANIMATION_FIRECHARGE = Animation.create(25);
      ANIMATION_WINGBLAST = Animation.create(50);
      ANIMATION_ROAR = Animation.create(40);
      this.growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, this.aiSit = new EntityAISit(this));
      this.tasks.addTask(2, new EntityAISwimming(this));
      this.tasks.addTask(3, new DragonAIMate(this, 1.0D));
      this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.5D, false));
      this.tasks.addTask(5, new AquaticAITempt(this, 1.0D, ModItems.fire_stew, false));
      this.tasks.addTask(6, new DragonAIAirTarget(this));
      this.tasks.addTask(7, new DragonAIWander(this, 1.0D));
      this.tasks.addTask(8, new DragonAIWatchClosest(this, EntityLivingBase.class, 6.0F));
      this.tasks.addTask(8, new DragonAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(4, new DragonAITarget(this, EntityLivingBase.class, true, new Predicate<Entity>() {
         public boolean apply(@Nullable Entity entity) {
            return entity instanceof EntityLivingBase && DragonUtils.isAlive((EntityLivingBase)entity);
         }
      }));
      this.targetTasks.addTask(5, new DragonAITargetItems(this, false));
   }

   public String getVariantName(int variant) {
      switch(variant) {
      case 1:
         return "green_";
      case 2:
         return "bronze_";
      case 3:
         return "gray_";
      default:
         return "red_";
      }
   }

   public Item getVariantScale(int variant) {
      switch(variant) {
      case 1:
         return ModItems.dragonscales_green;
      case 2:
         return ModItems.dragonscales_bronze;
      case 3:
         return ModItems.dragonscales_gray;
      default:
         return ModItems.dragonscales_red;
      }
   }

   public Item getVariantEgg(int variant) {
      switch(variant) {
      case 1:
         return ModItems.dragonegg_green;
      case 2:
         return ModItems.dragonegg_bronze;
      case 3:
         return ModItems.dragonegg_gray;
      default:
         return ModItems.dragonegg_red;
      }
   }

   public boolean canBeSteered() {
      return true;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getAnimation() == ANIMATION_WINGBLAST) {
         return false;
      } else {
         switch((new EaglercraftRandom()).nextInt(4)) {
         case 0:
            if (this.getAnimation() != ANIMATION_BITE) {
               this.setAnimation(ANIMATION_BITE);
               return false;
            }

            if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
               boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
               this.attackDecision = this.getRNG().nextBoolean();
               return flag;
            }
            break;
         case 1:
            if ((new EaglercraftRandom()).nextInt(2) == 0 && this.isDirectPathBetweenPoints(this, this.getPositionVector(), entityIn.getPositionVector()) && entityIn.width < this.width * 0.5F && this.getControllingPassenger() == null && this.getDragonStage() > 1 && !(entityIn instanceof EntityDragonBase) && !DragonUtils.isAnimaniaMob(entityIn)) {
               if (this.getAnimation() != ANIMATION_SHAKEPREY) {
                  this.setAnimation(ANIMATION_SHAKEPREY);
                  entityIn.startRiding(this);
                  this.attackDecision = this.getRNG().nextBoolean();
                  return true;
               }
            } else {
               if (this.getAnimation() != ANIMATION_BITE) {
                  this.setAnimation(ANIMATION_BITE);
                  return false;
               }

               if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
                  boolean flag1 = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
                  this.attackDecision = this.getRNG().nextBoolean();
                  return flag1;
               }
            }
            break;
         case 2:
            if (this.getAnimation() != ANIMATION_TAILWHACK) {
               this.setAnimation(ANIMATION_TAILWHACK);
               return false;
            }

            if (this.getAnimationTick() > 20 && this.getAnimationTick() < 25) {
               boolean flag2 = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
               if (entityIn instanceof EntityLivingBase) {
                  ((EntityLivingBase)entityIn).knockBack(entityIn, 1.0F, 1.0D, 1.0D);
               }

               this.attackDecision = this.getRNG().nextBoolean();
               return flag2;
            }
            break;
         case 3:
            if (this.onGround && !this.isHovering() && !this.isFlying() && this.getDragonStage() > 2) {
               if (this.getAnimation() != ANIMATION_WINGBLAST) {
                  this.setAnimation(ANIMATION_WINGBLAST);
                  return true;
               }
            } else {
               if (this.getAnimation() != ANIMATION_BITE) {
                  this.setAnimation(ANIMATION_BITE);
                  return false;
               }

               if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
                  boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
                  this.attackDecision = this.getRNG().nextBoolean();
                  return flag;
               }
            }
            break;
         default:
            if (this.getAnimation() != ANIMATION_BITE) {
               this.setAnimation(ANIMATION_BITE);
               return false;
            }

            if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
               boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
               this.attackDecision = this.getRNG().nextBoolean();
               return flag;
            }
         }

         return false;
      }
   }

   public void moveEntityTowards(Entity entity, double x, double y, double z, float velocity, float inaccuracy) {
      float f = MathHelper.sqrt(x * x + y * y + z * z);
      x = x / (double)f;
      y = y / (double)f;
      z = z / (double)f;
      x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      x = x * (double)velocity;
      y = y * (double)velocity;
      z = z * (double)velocity;
      entity.motionX = x;
      entity.motionY = y;
      entity.motionZ = z;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote) {
         if (this.getAttackTarget() != null && !this.isSleeping() && this.getAnimation() != ANIMATION_SHAKEPREY) {
            if ((!this.attackDecision || this.isFlying()) && !this.isTargetBlocked(new Vec3d(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ))) {
               this.shootFireAtMob(this.getAttackTarget());
            } else if (this.getEntityBoundingBox().grow((double)(this.getRenderSize() * 0.5F), (double)(this.getRenderSize() * 0.5F), (double)(this.getRenderSize() * 0.5F)).intersects(this.getAttackTarget().getEntityBoundingBox())) {
               this.attackEntityAsMob(this.getAttackTarget());
            }
         } else {
            this.setBreathingFire(false);
         }
      }

   }

   public void riderShootFire(Entity controller) {
      if (this.getRNG().nextInt(5) == 0 && !this.isChild()) {
         if (this.getAnimation() != ANIMATION_FIRECHARGE) {
            this.setAnimation(ANIMATION_FIRECHARGE);
         } else if (this.getAnimationTick() == 15) {
            this.rotationYaw = this.renderYawOffset;
            float headPosX = (float)(this.posX + (double)(1.8F * this.getRenderSize() * 0.3F) * Math.cos((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
            float headPosZ = (float)(this.posZ + (double)(1.8F * this.getRenderSize() * 0.3F) * Math.sin((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
            float headPosY = (float)(this.posY + 0.5D * (double)this.getRenderSize() * 0.30000001192092896D);
            this.playSound(ModSounds.FIREDRAGON_BREATH, 4.0F, 1.0F);
            double d2 = controller.getLookVec().x;
            double d3 = controller.getLookVec().y;
            double d4 = controller.getLookVec().z;
            float inaccuracy = 1.0F;
            d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
            d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
            d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
            EntityDragonFireCharge entitylargefireball = new EntityDragonFireCharge(this.world, this, d2, d3, d4);
            float size = this.isChild() ? 0.4F : (this.isAdult() ? 1.3F : 0.8F);
            entitylargefireball.setSizes(size, size);
            entitylargefireball.setPosition((double)headPosX, (double)headPosY, (double)headPosZ);
            if (!this.world.isRemote) {
               this.world.spawnEntity(entitylargefireball);
            }
         }
      } else if (this.isBreathingFire()) {
         if (this.isActuallyBreathingFire() && this.ticksExisted % 3 == 0) {
            this.rotationYaw = this.renderYawOffset;
            float headPosX = (float)(this.posX + (double)(1.8F * this.getRenderSize() * 0.3F) * Math.cos((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
            float headPosZ = (float)(this.posZ + (double)(1.8F * this.getRenderSize() * 0.3F) * Math.sin((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
            float headPosY = (float)(this.posY + 0.5D * (double)this.getRenderSize() * 0.30000001192092896D);
            double d2 = controller.getLookVec().x;
            double d3 = controller.getLookVec().y;
            double d4 = controller.getLookVec().z;
            float inaccuracy = 1.0F;
            d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
            d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
            d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
            EntityDragonFire entitylargefireball = new EntityDragonFire(this.world, this, d2, d3, d4);
            this.playSound(ModSounds.FIREDRAGON_BREATH, 4.0F, 1.0F);
            if (this.isChild()) {
               float var10000 = 0.4F;
            } else {
               float var28 = this.isAdult() ? 1.3F : 0.8F;
            }

            entitylargefireball.setPosition((double)headPosX, (double)headPosY, (double)headPosZ);
            if (!this.world.isRemote) {
               this.world.spawnEntity(entitylargefireball);
            }
         }
      } else {
         this.setBreathingFire(true);
      }

   }

   public ResourceLocation getDeadLootTable() {
      if (this.getDeathStage() >= this.getAgeInDays() / 5 / 2) {
         return SKELETON_LOOT;
      } else {
         return this.isMale() ? MALE_LOOT : FEMALE_LOOT;
      }
   }

   private void shootFireAtMob(EntityLivingBase entity) {
      if (!this.attackDecision) {
         if (this.getRNG().nextInt(5) == 0) {
            if (this.getAnimation() != ANIMATION_FIRECHARGE) {
               this.setAnimation(ANIMATION_FIRECHARGE);
            } else if (this.getAnimationTick() == 15) {
               this.rotationYaw = this.renderYawOffset;
               float headPosX = (float)(this.posX + (double)(1.8F * this.getRenderSize() * 0.3F) * Math.cos((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
               float headPosZ = (float)(this.posZ + (double)(1.8F * this.getRenderSize() * 0.3F) * Math.sin((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
               float headPosY = (float)(this.posY + 0.5D * (double)this.getRenderSize() * 0.30000001192092896D);
               double d2 = entity.posX - (double)headPosX;
               double d3 = entity.posY - (double)headPosY;
               double d4 = entity.posZ - (double)headPosZ;
               float inaccuracy = 1.0F;
               d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
               d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
               d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
               this.playSound(ModSounds.FIREDRAGON_BREATH, 4.0F, 1.0F);
               EntityDragonFireCharge entitylargefireball = new EntityDragonFireCharge(this.world, this, d2, d3, d4);
               float size = this.isChild() ? 0.4F : (this.isAdult() ? 1.3F : 0.8F);
               entitylargefireball.setSizes(size, size);
               entitylargefireball.setPosition((double)headPosX, (double)headPosY, (double)headPosZ);
               if (!this.world.isRemote) {
                  this.world.spawnEntity(entitylargefireball);
               }

               if (entity.isDead || entity == null) {
                  this.setBreathingFire(false);
                  this.attackDecision = this.getRNG().nextBoolean();
               }
            }
         } else if (this.isBreathingFire()) {
            if (this.isActuallyBreathingFire() && this.ticksExisted % 3 == 0) {
               this.rotationYaw = this.renderYawOffset;
               float headPosX = (float)(this.posX + (double)(1.8F * this.getRenderSize() * 0.3F) * Math.cos((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
               float headPosZ = (float)(this.posZ + (double)(1.8F * this.getRenderSize() * 0.3F) * Math.sin((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
               float headPosY = (float)(this.posY + 0.5D * (double)this.getRenderSize() * 0.30000001192092896D);
               double d2 = entity.posX - (double)headPosX;
               double d3 = entity.posY - (double)headPosY;
               double d4 = entity.posZ - (double)headPosZ;
               float inaccuracy = 1.0F;
               d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
               d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
               d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
               this.playSound(ModSounds.FIREDRAGON_BREATH, 4.0F, 1.0F);
               EntityDragonFire entitylargefireball = new EntityDragonFire(this.world, this, d2, d3, d4);
               float size = this.isChild() ? 0.4F : (this.isAdult() ? 1.3F : 0.8F);
               entitylargefireball.setPosition((double)headPosX, (double)headPosY, (double)headPosZ);
               if (!this.world.isRemote && !entity.isDead) {
                  this.world.spawnEntity(entitylargefireball);
               }

               entitylargefireball.setSizes(size, size);
               if (entity.isDead || entity == null) {
                  this.setBreathingFire(false);
                  this.attackDecision = this.getRNG().nextBoolean();
               }
            }
         } else {
            this.setBreathingFire(true);
         }
      }

      this.faceEntity(entity, 360.0F, 360.0F);
   }

   protected SoundEvent getAmbientSound() {
      return this.isTeen() ? ModSounds.FIREDRAGON_TEEN_IDLE : (this.isAdult() ? ModSounds.FIREDRAGON_ADULT_IDLE : ModSounds.FIREDRAGON_CHILD_IDLE);
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isTeen() ? ModSounds.FIREDRAGON_TEEN_HURT : (this.isAdult() ? ModSounds.FIREDRAGON_ADULT_HURT : ModSounds.FIREDRAGON_CHILD_HURT);
   }

   protected SoundEvent getDeathSound() {
      return this.isTeen() ? ModSounds.FIREDRAGON_TEEN_DEATH : (this.isAdult() ? ModSounds.FIREDRAGON_ADULT_DEATH : ModSounds.FIREDRAGON_CHILD_DEATH);
   }

   public SoundEvent getRoarSound() {
      return this.isTeen() ? ModSounds.FIREDRAGON_TEEN_ROAR : (this.isAdult() ? ModSounds.FIREDRAGON_ADULT_ROAR : ModSounds.FIREDRAGON_CHILD_ROAR);
   }

   public Animation[] getAnimations() {
      return new Animation[]{IAnimatedEntity.NO_ANIMATION, EntityDragonBase.ANIMATION_EAT, EntityDragonBase.ANIMATION_SPEAK, EntityDragonBase.ANIMATION_BITE, EntityDragonBase.ANIMATION_SHAKEPREY, ANIMATION_TAILWHACK, ANIMATION_FIRECHARGE, ANIMATION_WINGBLAST, ANIMATION_ROAR};
   }

   public boolean isBreedingItem(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModItems.fire_stew;
   }
}
