package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.core.ModVillagers;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIDefendHive;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIFindHidingSpot;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAILeaveHive;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAILookAtTradePlayer;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIMoveThroughHive;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIReEnterHive;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAITradePlayer;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIWander;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class EntityMyrmexSentinel extends EntityMyrmexBase {
   private static final ResourceLocation TEXTURE_DESERT = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_sentinel.png");
   private static final ResourceLocation TEXTURE_JUNGLE = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_sentinel.png");
   private static final ResourceLocation TEXTURE_DESERT_HIDDEN = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_sentinel_hidden.png");
   private static final ResourceLocation TEXTURE_JUNGLE_HIDDEN = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_sentinel_hidden.png");
   public static final Animation ANIMATION_GRAB = Animation.create(15);
   public static final Animation ANIMATION_NIBBLE = Animation.create(10);
   public static final Animation ANIMATION_STING = Animation.create(25);
   public static final Animation ANIMATION_SLASH = Animation.create(25);
   private static final DataParameter<Boolean> HIDING = EntityDataManager.createKey(EntityMyrmexSentinel.class, DataSerializers.BOOLEAN);
   public float holdingProgress;
   public float hidingProgress;
   public int visibleTicks = 0;
   public int daylightTicks = 0;
   public static final ResourceLocation DESERT_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "myrmex_sentinel_desert"));
   public static final ResourceLocation JUNGLE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "myrmex_sentinel_jungle"));

   @Nullable
   protected ResourceLocation getLootTable() {
      return this.isJungle() ? JUNGLE_LOOT : DESERT_LOOT;
   }

   public EntityMyrmexSentinel(World worldIn) {
      super(worldIn);
      this.setSize(1.3F, 1.95F);
   }

   public Entity getHeldEntity() {
      return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.visibleTicks > 0) {
         --this.visibleTicks;
      } else {
         this.visibleTicks = 0;
      }

      if (this.canSeeSky()) {
         ++this.daylightTicks;
      } else {
         this.daylightTicks = 0;
      }

      boolean holding = this.getHeldEntity() != null;
      boolean hiding = this.isHiding() && !this.isTrading();
      if (holding || this.isOnResin() || this.getAttackTarget() != null || this.visibleTicks > 0) {
         this.setHiding(false);
      }

      if (holding && this.holdingProgress < 20.0F) {
         ++this.holdingProgress;
      } else if (!holding && this.holdingProgress > 0.0F) {
         --this.holdingProgress;
      }

      if (hiding) {
         this.rotationYaw = this.prevRotationYaw;
      }

      if (hiding && this.hidingProgress < 20.0F) {
         ++this.hidingProgress;
      } else if (!hiding && this.hidingProgress > 0.0F) {
         --this.hidingProgress;
      }

      if (this.getHeldEntity() != null) {
         this.setAnimation(ANIMATION_NIBBLE);
         if (this.getAnimationTick() == 5) {
            this.playBiteSound();
            this.getHeldEntity().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() / 6));
         }
      }

      if (this.getAnimation() == ANIMATION_GRAB && this.getAttackTarget() != null && this.getAnimationTick() == 7) {
         this.playStingSound();
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 16.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() / 2));
            this.getAttackTarget().startRiding(this);
         }
      }

      if (this.getAnimation() == ANIMATION_SLASH && this.getAttackTarget() != null && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() <= 20) {
         this.playBiteSound();
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 16.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() / 4));
         }
      }

      if (this.getAnimation() == ANIMATION_STING && (this.getAnimationTick() == 0 || this.getAnimationTick() == 10)) {
         this.playStingSound();
      }

      if (this.getAnimation() == ANIMATION_STING && this.getAttackTarget() != null && (this.getAnimationTick() == 6 || this.getAnimationTick() == 16)) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 18.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 3));
         }
      }

   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(0, new MyrmexAIFindHidingSpot(this));
      this.tasks.addTask(0, new MyrmexAITradePlayer(this));
      this.tasks.addTask(0, new MyrmexAILookAtTradePlayer(this));
      this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(3, new MyrmexAILeaveHive(this, 1.0D));
      this.tasks.addTask(3, new MyrmexAIReEnterHive(this, 1.0D));
      this.tasks.addTask(4, new MyrmexAIMoveThroughHive(this, 1.0D));
      this.tasks.addTask(5, new MyrmexAIWander(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new MyrmexAIDefendHive(this));
      this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(4, new MyrmexAIAttackPlayers(this));
      this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityLiving.class, 4, true, true, new Predicate<EntityLiving>() {
         public boolean apply(@Nullable EntityLiving entity) {
            return entity != null && !IMob.VISIBLE_MOB_SELECTOR.apply(entity) && !EntityMyrmexBase.haveSameHive(EntityMyrmexSentinel.this, entity) && DragonUtils.isAlive(entity);
         }
      }));
   }

   public VillagerProfession getProfessionForge() {
      return this.isJungle() ? ModVillagers.INSTANCE.jungleMyrmexSentinel : ModVillagers.INSTANCE.desertMyrmexSentinel;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HIDING, Boolean.valueOf(false));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(12.0D);
   }

   public ResourceLocation getAdultTexture() {
      if (this.isHiding()) {
         return this.isJungle() ? TEXTURE_JUNGLE_HIDDEN : TEXTURE_DESERT_HIDDEN;
      } else {
         return this.isJungle() ? TEXTURE_JUNGLE : TEXTURE_DESERT;
      }
   }

   public float getModelScale() {
      return 0.8F;
   }

   public int getCasteImportance() {
      return 2;
   }

   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      tag.setBoolean("Hiding", this.isHiding());
      tag.setInteger("DaylightTicks", this.daylightTicks);
   }

   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      this.setHiding(tag.getBoolean("Hiding"));
      this.daylightTicks = tag.getInteger("DaylightTicks");
   }

   public boolean shouldLeaveHive() {
      return true;
   }

   public boolean shouldEnterHive() {
      return false;
   }

   public void updatePassenger(Entity passenger) {
      super.updatePassenger(passenger);
      if (this.isPassenger(passenger)) {
         this.renderYawOffset = this.rotationYaw;
         float radius = 1.25F;
         float extraY = 0.35F;
         if (this.getAnimation() == ANIMATION_GRAB) {
            int modTick = MathHelper.clamp(this.getAnimationTick(), 0, 10);
            radius = 3.25F - (float)modTick * 0.2F;
            extraY = (float)modTick * 0.035F;
         }

         float angle = 0.017453292F * this.renderYawOffset;
         double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
         double extraZ = (double)(radius * MathHelper.cos(angle));
         if (passenger.height >= 1.75F) {
            extraY = passenger.height - 2.0F;
         }

         passenger.setPosition(this.posX + extraX, this.posY + (double)extraY, this.posZ + extraZ);
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      if ((double)amount >= 1.0D && !this.getPassengers().isEmpty() && this.rand.nextInt(2) == 0) {
         for(Entity entity : this.getPassengers()) {
            entity.dismountRidingEntity();
         }
      }

      this.visibleTicks = 300;
      this.setHiding(false);
      return super.attackEntityFrom(source, amount);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getGrowthStage() < 2) {
         return false;
      } else if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_SLASH && this.getAnimation() != ANIMATION_GRAB && this.getHeldEntity() == null) {
         if (this.getRNG().nextInt(2) == 0 && entityIn.width < 2.0F) {
            this.setAnimation(ANIMATION_GRAB);
         } else {
            this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_STING : ANIMATION_SLASH);
         }

         this.visibleTicks = 300;
         return true;
      } else {
         return false;
      }
   }

   public boolean needsGaurding() {
      return false;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_SLASH, ANIMATION_STING, ANIMATION_GRAB, ANIMATION_NIBBLE};
   }

   public boolean canMove() {
      return super.canMove() && this.getHeldEntity() == null && !this.isHiding();
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public boolean isHiding() {
      return ((Boolean)this.dataManager.get(HIDING)).booleanValue();
   }

   public void setHiding(boolean hiding) {
      this.dataManager.set(HIDING, Boolean.valueOf(hiding));
   }
}
