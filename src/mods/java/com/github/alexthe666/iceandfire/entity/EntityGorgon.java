package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.GorgonAIStareAttack;
import com.github.alexthe666.iceandfire.message.MessageStoneStatue;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.ilexiconn.llibrary.server.entity.multipart.PartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityGorgon extends EntityMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {
   public static Animation ANIMATION_SCARE;
   public static Animation ANIMATION_HIT;
   private int animationTick;
   private Animation currentAnimation;
   private GorgonAIStareAttack aiStare;
   private EntityAIAttackMelee aiMelee;
   public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("iceandfire", "gorgon"));

   public EntityGorgon(World worldIn) {
      super(worldIn);
      this.setSize(0.8F, 1.99F);
      ANIMATION_SCARE = Animation.create(30);
      ANIMATION_HIT = Animation.create(10);
   }

   public static boolean isEntityLookingAt(EntityLivingBase looker, EntityLivingBase seen, double degree) {
      degree = degree * (1.0D + (double)looker.getDistance(seen) * 0.1D);
      Vec3d vec3d = looker.getLook(1.0F).normalize();
      Vec3d vec3d1 = new Vec3d(seen.posX - looker.posX, seen.getEntityBoundingBox().minY + (double)seen.getEyeHeight() - (looker.posY + (double)looker.getEyeHeight()), seen.posZ - looker.posZ);
      double d0 = vec3d1.lengthVector();
      vec3d1 = vec3d1.normalize();
      double d1 = vec3d.dotProduct(vec3d1);
      return d1 > 1.0D - degree / d0 ? looker.canEntityBeSeen(seen) && !isStoneMob(seen) : false;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT;
   }

   public static boolean isStoneMob(EntityLivingBase mob) {
      if (mob != null && mob instanceof EntityLiving) {
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(mob, StoneEntityProperties.class);
         return properties != null && properties.isStone;
      } else {
         return false;
      }
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIRestrictSun(this));
      this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
      this.tasks.addTask(3, this.aiStare = new GorgonAIStareAttack(this, 1.0D, 0, 15.0F));
      this.tasks.addTask(3, this.aiMelee = new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(4, new GorgonAIStareAttack(this, 1.0D, 0, 15.0F));
      this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D) {
         public boolean shouldExecute() {
            this.executionChance = 20;
            return super.shouldExecute();
         }
      });
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F, 1.0F) {
         public boolean shouldContinueExecuting() {
            return this.closestEntity != null && this.closestEntity instanceof EntityPlayer && ((EntityPlayer)this.closestEntity).isCreative() ? false : super.shouldContinueExecuting();
         }
      });
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new Predicate<EntityPlayer>() {
         public boolean apply(@Nullable EntityPlayer entity) {
            return true;
         }
      }));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, false, new Predicate<Entity>() {
         public boolean apply(@Nullable Entity entity) {
            StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(entity, StoneEntityProperties.class);
            return entity instanceof EntityLiving && DragonUtils.isAlive((EntityLiving)entity) && !(entity instanceof PartEntity) && (properties == null || properties != null && !properties.isStone) || entity instanceof IBlacklistedFromStatues && ((IBlacklistedFromStatues)entity).canBeTurnedToStone();
         }
      }));
      this.tasks.removeTask(this.aiMelee);
   }

   public void attackEntityWithRangedAttack(EntityLivingBase entity) {
      if (!(entity instanceof EntityPlayer) && entity instanceof EntityLiving) {
         this.forcePreyToLook((EntityLiving)entity);
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean blindness = this.isPotionActive(MobEffects.BLINDNESS) || this.getAttackTarget() != null && this.getAttackTarget().isPotionActive(MobEffects.BLINDNESS) || this.getAttackTarget() != null && this.getAttackTarget() instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues)this.getAttackTarget()).canBeTurnedToStone();
      if (blindness && this.deathTime == 0) {
         if (this.getAnimation() != ANIMATION_HIT) {
            this.setAnimation(ANIMATION_HIT);
         }

         if (entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 2, false, true));
         }
      }

      return super.attackEntityAsMob(entityIn);
   }

   protected boolean canDespawn() {
      return false;
   }

   public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
      super.setAttackTarget(entitylivingbaseIn);
      if (entitylivingbaseIn != null && !this.world.isRemote) {
         boolean blindness = this.isPotionActive(MobEffects.BLINDNESS) || entitylivingbaseIn.isPotionActive(MobEffects.BLINDNESS) || entitylivingbaseIn instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues)entitylivingbaseIn).canBeTurnedToStone();
         if (blindness && this.deathTime == 0) {
            this.tasks.removeTask(this.aiStare);
            this.tasks.addTask(3, this.aiMelee);
         } else {
            this.tasks.removeTask(this.aiMelee);
            this.tasks.addTask(3, this.aiStare);
         }
      }

   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 20 + this.world.rand.nextInt(15);
   }

   protected void onDeathUpdate() {
      ++this.deathTime;
      this.livingSoundTime = 20;

      for(int k = 0; k < 5; ++k) {
         double d2 = 0.4D;
         double d0 = 0.1D;
         double d1 = 0.1D;
         IceAndFire.PROXY.spawnParticle("blood", this.world, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1);
      }

      if (this.deathTime >= 200) {
         if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot"))) {
            int i = this.getExperiencePoints(this.attackingPlayer);
            i = ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);

            while(i > 0) {
               int j = EntityXPOrb.getXPSplit(i);
               i -= j;
               this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
            }
         }

         this.setDead();

         for(int k = 0; k < 20; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1, new int[0]);
         }
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getAttackTarget() != null) {
         boolean blindness = this.isPotionActive(MobEffects.BLINDNESS) || this.getAttackTarget().isPotionActive(MobEffects.BLINDNESS);
         this.getLookHelper().setLookPosition(this.getAttackTarget().posX, this.getAttackTarget().posY + (double)this.getAttackTarget().getEyeHeight(), this.getAttackTarget().posZ, (float)this.getHorizontalFaceSpeed(), (float)this.getVerticalFaceSpeed());
         if (!blindness && this.deathTime == 0 && this.getAttackTarget() instanceof EntityLiving && !(this.getAttackTarget() instanceof EntityPlayer)) {
            this.forcePreyToLook((EntityLiving)this.getAttackTarget());
         }
      }

      if (this.getAttackTarget() != null && isEntityLookingAt(this, this.getAttackTarget(), 0.4D) && isEntityLookingAt(this.getAttackTarget(), this, 0.4D)) {
         boolean blindness = this.isPotionActive(MobEffects.BLINDNESS) || this.getAttackTarget().isPotionActive(MobEffects.BLINDNESS) || this.getAttackTarget() instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues)this.getAttackTarget()).canBeTurnedToStone();
         if (!blindness && this.deathTime == 0) {
            if (this.getAnimation() != ANIMATION_SCARE) {
               this.playSound(ModSounds.GORGON_ATTACK, 1.0F, 1.0F);
               this.setAnimation(ANIMATION_SCARE);
            }

            if (this.getAnimation() == ANIMATION_SCARE && this.getAnimationTick() > 10) {
               if (this.getAttackTarget() instanceof EntityPlayer) {
                  EntityStoneStatue statue = new EntityStoneStatue(this.world);
                  statue.setPositionAndRotation(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ, this.getAttackTarget().rotationYaw, this.getAttackTarget().rotationPitch);
                  statue.smallArms = true;
                  if (!this.world.isRemote) {
                     this.world.spawnEntity(statue);
                     this.getAttackTarget().setDead();
                  }

                  statue.prevRotationYaw = this.getAttackTarget().rotationYaw;
                  statue.rotationYaw = this.getAttackTarget().rotationYaw;
                  statue.rotationYawHead = this.getAttackTarget().rotationYaw;
                  statue.renderYawOffset = this.getAttackTarget().rotationYaw;
                  statue.prevRenderYawOffset = this.getAttackTarget().rotationYaw;
                  this.getAttackTarget().attackEntityFrom(IceAndFire.gorgon, 2.14748365E9F);
               } else if (this.getAttackTarget() instanceof EntityLiving && !(this.getAttackTarget() instanceof IBlacklistedFromStatues) || this.getAttackTarget() instanceof IBlacklistedFromStatues && ((IBlacklistedFromStatues)this.getAttackTarget()).canBeTurnedToStone()) {
                  StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this.getAttackTarget(), StoneEntityProperties.class);
                  EntityLiving attackTarget = (EntityLiving)this.getAttackTarget();
                  if (properties != null || !properties.isStone) {
                     properties.isStone = true;
                     if (this.world.isRemote) {
                        IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageStoneStatue(attackTarget.getEntityId(), true));
                     } else {
                        IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageStoneStatue(attackTarget.getEntityId(), true));
                     }

                     this.playSound(ModSounds.GORGON_TURN_STONE, 1.0F, 1.0F);
                     this.setAttackTarget((EntityLivingBase)null);
                  }

                  if (attackTarget instanceof EntityDragonBase) {
                     EntityDragonBase dragon = (EntityDragonBase)attackTarget;
                     dragon.setFlying(false);
                     dragon.setHovering(false);
                     dragon.airTarget = null;
                  }

                  if (attackTarget instanceof EntityHippogryph) {
                     EntityHippogryph dragon = (EntityHippogryph)attackTarget;
                     dragon.setFlying(false);
                     dragon.setHovering(false);
                     dragon.airTarget = null;
                  }
               }
            }
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
      EntityPlayer player = this.world.getClosestPlayerToEntity(this, 25.0D);
   }

   public int getVerticalFaceSpeed() {
      return 10;
   }

   public int getHorizontalFaceSpeed() {
      return 10;
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   public void forcePreyToLook(EntityLiving mob) {
      mob.getLookHelper().setLookPosition(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)mob.getHorizontalFaceSpeed(), (float)mob.getVerticalFaceSpeed());
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(IceAndFire.CONFIG.gorgonMaxHealth);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(1.0D);
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
      return new Animation[]{ANIMATION_SCARE, ANIMATION_HIT};
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.GORGON_IDLE;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return ModSounds.GORGON_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return ModSounds.GORGON_DIE;
   }

   public boolean shouldAnimalsFear(Entity entity) {
      return true;
   }
}
