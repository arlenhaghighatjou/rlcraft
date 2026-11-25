package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.TrollAIFleeSun;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityTroll extends EntityMob implements IAnimatedEntity, IVillagerFear {
   private int animationTick;
   private Animation currentAnimation;
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityTroll.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> WEAPON = EntityDataManager.createKey(EntityTroll.class, DataSerializers.VARINT);
   public static final Animation ANIMATION_STRIKE_HORIZONTAL = Animation.create(20);
   public static final Animation ANIMATION_STRIKE_VERTICAL = Animation.create(20);
   public static final Animation ANIMATION_SPEAK = Animation.create(10);
   public static final Animation ANIMATION_ROAR = Animation.create(25);
   public float stoneProgress;
   public static final ResourceLocation FOREST_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "troll_forest"));
   public static final ResourceLocation FROST_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "troll_frost"));
   public static final ResourceLocation MOUNTAIN_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "troll_mountain"));

   public EntityTroll(World worldIn) {
      super(worldIn);
      this.setSize(1.2F, 3.5F);
   }

   public boolean isAIDisabled() {
      return EntityGorgon.isStoneMob(this) || super.isAIDisabled();
   }

   public boolean getCanSpawnHere() {
      BlockPos pos = new BlockPos(this);
      return this.getRNG().nextInt(IceAndFire.CONFIG.trollSpawnCheckChance + 1) == 0 && !this.world.canSeeSky(pos) && pos.getY() <= 50 && super.getCanSpawnHere();
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new TrollAIFleeSun(this, 1.0D));
      this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(3, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F, 1.0F));
      this.tasks.addTask(4, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false));
      ((PathNavigateGround)this.getNavigator()).setAvoidSun(true);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(IceAndFire.CONFIG.trollAttackStrength);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(IceAndFire.CONFIG.trollMaxHealth);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(9.0D);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getRNG().nextBoolean()) {
         this.setAnimation(ANIMATION_STRIKE_VERTICAL);
      } else {
         this.setAnimation(ANIMATION_STRIKE_HORIZONTAL);
      }

      return true;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VARIANT, Integer.valueOf(0));
      this.dataManager.register(WEAPON, Integer.valueOf(0));
   }

   private int getVariant() {
      return ((Integer)this.dataManager.get(VARIANT)).intValue();
   }

   private void setVariant(int variant) {
      this.dataManager.set(VARIANT, Integer.valueOf(variant));
   }

   public void setType(EnumTroll variant) {
      this.setVariant(variant.ordinal());
   }

   public EnumTroll getType() {
      return EnumTroll.values()[this.getVariant()];
   }

   private int getWeapon() {
      return ((Integer)this.dataManager.get(WEAPON)).intValue();
   }

   private void setWeapon(int variant) {
      this.dataManager.set(WEAPON, Integer.valueOf(variant));
   }

   public void setWeaponType(EnumTroll.Weapon variant) {
      this.setWeapon(variant.ordinal());
   }

   public EnumTroll.Weapon getWeaponType() {
      return EnumTroll.Weapon.values()[this.getWeapon()];
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("Variant", this.getVariant());
      compound.setInteger("Weapon", this.getWeapon());
      compound.setFloat("StoneProgress", this.stoneProgress);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setVariant(compound.getInteger("Variant"));
      this.setWeapon(compound.getInteger("Weapon"));
      this.stoneProgress = compound.getFloat("StoneProgress");
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setType(EnumTroll.getBiomeType(this.world.getBiome(this.getPosition())));
      this.setWeaponType(EnumTroll.getWeaponForType(this.getType()));
      return livingdata;
   }

   public boolean attackEntityFrom(DamageSource source, float damage) {
      return source.damageType.contains("arrow") ? false : super.attackEntityFrom(source, damage);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      switch(this.getType()) {
      case MOUNTAIN:
         return MOUNTAIN_LOOT;
      case FROST:
         return FROST_LOOT;
      case FOREST:
         return FOREST_LOOT;
      default:
         return null;
      }
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 15 + this.world.rand.nextInt(10);
   }

   protected void onDeathUpdate() {
      super.onDeathUpdate();
      if (this.deathTime == 20 && !this.world.isRemote && IceAndFire.CONFIG.trollsDropWeapon) {
         if (this.getRNG().nextInt(3) == 0) {
            ItemStack weaponStack = new ItemStack(this.getWeaponType().item, 1, 0);
            weaponStack.attemptDamageItem(this.getRNG().nextInt(250), this.getRNG(), (EntityPlayerMP)null);
            this.dropItemAt(weaponStack, this.posX, this.posY, this.posZ);
         } else {
            ItemStack brokenDrop = new ItemStack(Blocks.STONEBRICK, this.getRNG().nextInt(2) + 1, 0);
            ItemStack brokenDrop2 = new ItemStack(Blocks.STONEBRICK, this.getRNG().nextInt(2) + 1, 0);
            if (this.getWeaponType() == EnumTroll.Weapon.AXE) {
               brokenDrop = new ItemStack(Items.STICK, this.getRNG().nextInt(2) + 1, 0);
               brokenDrop2 = new ItemStack(Blocks.COBBLESTONE, this.getRNG().nextInt(2) + 1, 0);
            }

            if (this.getWeaponType() == EnumTroll.Weapon.COLUMN) {
               brokenDrop = new ItemStack(Blocks.STONEBRICK, this.getRNG().nextInt(2) + 1, 2);
               brokenDrop2 = new ItemStack(Blocks.STONEBRICK, this.getRNG().nextInt(2) + 1, 2);
            }

            if (this.getWeaponType() == EnumTroll.Weapon.COLUMN_FOREST) {
               brokenDrop = new ItemStack(Blocks.STONEBRICK, this.getRNG().nextInt(2) + 1, 1);
               brokenDrop2 = new ItemStack(Blocks.STONEBRICK, this.getRNG().nextInt(2) + 1, 2);
            }

            if (this.getWeaponType() == EnumTroll.Weapon.COLUMN_FROST) {
               brokenDrop = new ItemStack(Blocks.STONEBRICK, this.getRNG().nextInt(2) + 1, 0);
               brokenDrop2 = new ItemStack(Items.SNOWBALL, this.getRNG().nextInt(4) + 1);
            }

            if (this.getWeaponType() == EnumTroll.Weapon.HAMMER) {
               brokenDrop = new ItemStack(Items.BONE, this.getRNG().nextInt(2) + 1, 0);
               brokenDrop2 = new ItemStack(Blocks.COBBLESTONE, this.getRNG().nextInt(2) + 1, 0);
            }

            if (this.getWeaponType() == EnumTroll.Weapon.TRUNK) {
               brokenDrop = new ItemStack(Blocks.LOG, this.getRNG().nextInt(2) + 1, 0);
               brokenDrop2 = new ItemStack(Blocks.LOG, this.getRNG().nextInt(2) + 1, 0);
            }

            if (this.getWeaponType() == EnumTroll.Weapon.TRUNK_FROST) {
               brokenDrop = new ItemStack(Blocks.LOG, this.getRNG().nextInt(4) + 1, 1);
               brokenDrop2 = new ItemStack(Items.SNOWBALL, this.getRNG().nextInt(4) + 1);
            }

            this.dropItemAt(brokenDrop, this.posX, this.posY, this.posZ);
            this.dropItemAt(brokenDrop2, this.posX, this.posY, this.posZ);
         }
      }

   }

   @Nullable
   private EntityItem dropItemAt(ItemStack stack, double x, double y, double z) {
      if (stack.getCount() > 0) {
         EntityItem entityitem = new EntityItem(this.world, x, y, z, stack);
         entityitem.setDefaultPickupDelay();
         if (this.captureDrops) {
            this.capturedDrops.add(entityitem);
         } else {
            this.world.spawnEntity(entityitem);
         }

         return entityitem;
      } else {
         return null;
      }
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      boolean stone = EntityGorgon.isStoneMob(this);
      if (stone && this.stoneProgress < 20.0F) {
         this.stoneProgress += 2.0F;
      } else if (!stone && this.stoneProgress > 0.0F) {
         this.stoneProgress -= 2.0F;
      }

      if (!stone && this.getAnimation() == NO_ANIMATION && this.getAttackTarget() != null && this.getRNG().nextInt(100) == 0) {
         this.setAnimation(ANIMATION_ROAR);
      }

      if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 5) {
         this.playSound(ModSounds.TROLL_ROAR, 1.0F, 1.0F);
      }

      if (!stone && this.getHealth() < this.getMaxHealth() && this.ticksExisted % 30 == 0) {
         this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 30, 1, false, false));
      }

      if (this.world.isDaytime() && !this.world.isRemote) {
         float f = this.getBrightness();
         BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ);
         if (f > 0.5F && this.world.canSeeSky(blockpos)) {
            StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
            if (properties != null && !properties.isStone) {
               properties.isStone = true;
               this.motionX = 0.0D;
               this.motionY = 0.0D;
               this.motionZ = 0.0D;
               this.setAnimation(NO_ANIMATION);
               this.playSound(ModSounds.GORGON_TURN_STONE, 1.0F, 1.0F);
            }
         }
      }

      if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
         float weaponX = (float)(this.posX + 1.899999976158142D * Math.cos((double)(this.renderYawOffset + 90.0F) * 3.141592653589793D / 180.0D));
         float weaponZ = (float)(this.posZ + 1.899999976158142D * Math.sin((double)(this.renderYawOffset + 90.0F) * 3.141592653589793D / 180.0D));
         float weaponY = (float)(this.posY + 0.20000000298023224D);
         IBlockState state = this.world.getBlockState(new BlockPos((double)weaponX, (double)(weaponY - 1.0F), (double)weaponZ));

         for(int i = 0; i < 20; ++i) {
            double motionX = this.getRNG().nextGaussian() * 0.07D;
            double motionY = this.getRNG().nextGaussian() * 0.07D;
            double motionZ = this.getRNG().nextGaussian() * 0.07D;
            if (state.getMaterial().isSolid() && this.world.isRemote) {
               this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, (double)(weaponX + (this.getRNG().nextFloat() - 0.5F)), (double)(weaponY + (this.getRNG().nextFloat() - 0.5F)), (double)(weaponZ + (this.getRNG().nextFloat() - 0.5F)), motionX, motionY, motionZ, new int[]{Block.getIdFromBlock(state.getBlock())});
            }
         }
      }

      if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 4.0D && this.getAnimationTick() == 10 && this.deathTime <= 0) {
         this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
      }

      if (this.getAnimation() == ANIMATION_STRIKE_HORIZONTAL && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 4.0D && this.getAnimationTick() == 10 && this.deathTime <= 0) {
         this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
         float f1 = 0.5F;
         float f2 = this.getAttackTarget().moveForward;
         float f3 = 0.6F;
         float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
         if (f4 < 1.0F) {
            f4 = 1.0F;
         }

         f4 = f1 / f4;
         f2 = f2 * f4;
         f3 = f3 * f4;
         float f5 = MathHelper.sin(this.rotationYaw * 0.017453292F);
         float f6 = MathHelper.cos(this.rotationYaw * 0.017453292F);
         float f7 = f2 * f6 - f3 * f5;
         float f8 = f3 * f6 + f2 * f5;
         this.getAttackTarget().motionX = (double)f5;
         this.getAttackTarget().motionZ = (double)f6;
         this.getAttackTarget().motionY = 0.4000000059604645D;
      }

      if (this.getNavigator().noPath() && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) > 3.0D && this.getDistanceSq(this.getAttackTarget()) < 30.0D && this.world.getGameRules().getBoolean("mobGriefing")) {
         this.faceEntity(this.getAttackTarget(), 30.0F, 30.0F);
         if (this.getAnimation() == NO_ANIMATION && this.rand.nextInt(15) == 0) {
            this.setAnimation(ANIMATION_STRIKE_VERTICAL);
         }

         if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
            float weaponX = (float)(this.posX + 1.899999976158142D * Math.cos((double)(this.renderYawOffset + 90.0F) * 3.141592653589793D / 180.0D));
            float weaponZ = (float)(this.posZ + 1.899999976158142D * Math.sin((double)(this.renderYawOffset + 90.0F) * 3.141592653589793D / 180.0D));
            float weaponY = (float)(this.posY + (double)(this.getEyeHeight() / 2.0F));
            IBlockState state = this.world.getBlockState(new BlockPos((double)weaponX, (double)weaponY, (double)weaponZ));
            BlockBreakExplosion explosion = new BlockBreakExplosion(this.world, this, (double)weaponX, (double)weaponY, (double)weaponZ, 1.0F + this.getRNG().nextFloat());
            explosion.doExplosionA();
            explosion.doExplosionB(true);
            this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
         }
      }

      if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
         this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 2.5F, 0.5F);
      }

      if (this.getAnimation() == ANIMATION_STRIKE_HORIZONTAL && this.getAnimationTick() == 10) {
         this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 2.5F, 0.5F);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
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

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.TROLL_IDLE;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.TROLL_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return ModSounds.TROLL_DIE;
   }

   public Animation[] getAnimations() {
      return new Animation[]{NO_ANIMATION, ANIMATION_STRIKE_HORIZONTAL, ANIMATION_STRIKE_VERTICAL, ANIMATION_SPEAK, ANIMATION_ROAR};
   }
}
