package vazkii.quark.world.entity;

import com.google.common.base.Predicate;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.util.EntityOpacityHandler;
import vazkii.quark.oddities.feature.TinyPotato;
import vazkii.quark.tweaks.ai.EntityAIWantLove;
import vazkii.quark.world.entity.ai.EntityAIFoxhoundSleep;
import vazkii.quark.world.entity.ai.EntityAISleep;
import vazkii.quark.world.feature.Foxhounds;

public class EntityFoxhound extends EntityWolf implements IMob {
   public static final ResourceLocation FOXHOUND_LOOT_TABLE = new ResourceLocation("quark", "entities/foxhound");
   private static final DataParameter<Boolean> TEMPTATION = EntityDataManager.createKey(EntityFoxhound.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(EntityFoxhound.class, DataSerializers.BOOLEAN);
   private int timeUntilPotatoEmerges = 0;
   protected EntityAISleep aiSleep;

   public EntityFoxhound(World worldIn) {
      super(worldIn);
      this.setSize(0.8F, 0.8F);
      this.setPathPriority(PathNodeType.WATER, -1.0F);
      this.setPathPriority(PathNodeType.LAVA, 1.0F);
      this.setPathPriority(PathNodeType.DANGER_FIRE, 1.0F);
      this.setPathPriority(PathNodeType.DAMAGE_FIRE, 1.0F);
      this.isImmuneToFire = true;
   }

   protected void entityInit() {
      super.entityInit();
      this.setCollarColor(EnumDyeColor.ORANGE);
      this.dataManager.register(TEMPTATION, Boolean.valueOf(false));
      this.dataManager.register(SLEEPING, Boolean.valueOf(false));
   }

   public boolean isNoDespawnRequired() {
      return this.isTamed() || super.isNoDespawnRequired();
   }

   protected boolean canDespawn() {
      return !this.isTamed();
   }

   public boolean isEntityInsideOpaqueBlock() {
      return EntityOpacityHandler.isEntityInsideOpaqueBlock(this);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL && !this.isTamed()) {
         this.setDead();
      } else {
         if (!this.world.isRemote && TinyPotato.tiny_potato != null) {
            if (this.timeUntilPotatoEmerges == 1) {
               this.timeUntilPotatoEmerges = 0;
               ItemStack stack = new ItemStack(TinyPotato.tiny_potato);
               ItemNBTHelper.setBoolean(stack, "angery", true);
               this.entityDropItem(stack, 0.0F);
               this.playSound(SoundEvents.ENTITY_GENERIC_HURT, 1.0F, 1.0F);
            } else if (this.timeUntilPotatoEmerges > 1) {
               --this.timeUntilPotatoEmerges;
            }
         }

         if (EntityAIWantLove.needsPets(this)) {
            Entity owner = this.getOwner();
            if (owner != null && owner.getDistanceSq(this) < 1.0D && !owner.isInWater() && !owner.isImmuneToFire() && (!(owner instanceof EntityPlayer) || !((EntityPlayer)owner).isCreative())) {
               owner.setFire(5);
            }
         }

         if (this.world.isRemote) {
            this.world.spawnParticle(this.isSleeping() ? EnumParticleTypes.SMOKE_NORMAL : EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + (this.rand.nextDouble() - 0.5D) * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         if (this.isTamed()) {
            BlockPos below = this.getPosition().down();
            TileEntity tile = this.world.getTileEntity(below);
            if (tile instanceof TileEntityFurnace) {
               int cookTime = ((TileEntityFurnace)tile).getField(2);
               if (cookTime > 0 && cookTime % 3 == 0) {
                  List<EntityFoxhound> foxhounds = this.world.getEntitiesWithinAABB(EntityFoxhound.class, new AxisAlignedBB(this.getPosition()), (fox) -> {
                     return fox != null && fox.isTamed();
                  });
                  if (!foxhounds.isEmpty() && foxhounds.get(0) == this) {
                     ((TileEntityFurnace)tile).setField(2, Math.min(199, cookTime + 1));
                  }
               }
            }
         }

      }
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return FOXHOUND_LOOT_TABLE;
   }

   protected void initEntityAI() {
      this.aiSit = new EntityAISit(this);
      this.aiSleep = new EntityAISleep(this);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiSleep);
      this.tasks.addTask(3, this.aiSit);
      this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(6, new EntityAIFoxhoundSleep(this, 0.8D, true));
      this.tasks.addTask(7, new EntityAIFoxhoundSleep(this, 0.8D, false));
      this.tasks.addTask(8, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
      this.tasks.addTask(9, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(10, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(11, new EntityAIBeg(this, 8.0F));
      this.tasks.addTask(12, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(12, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityAnimal.class, false, (target) -> {
         return target instanceof EntitySheep || target instanceof EntityRabbit;
      }));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityPlayer.class, false, (target) -> {
         return !this.isTamed();
      }));
      this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, AbstractSkeleton.class, false));
   }

   public boolean isAngry() {
      return !this.isTamed() && this.world.getDifficulty() != EnumDifficulty.PEACEFUL || super.isAngry();
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (entityIn.isImmuneToFire()) {
         return entityIn instanceof EntityPlayer ? false : super.attackEntityAsMob(entityIn);
      } else {
         boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this).setFireDamage(), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         if (flag) {
            entityIn.setFire(5);
            this.applyEnchantments(this, entityIn);
         }

         return flag;
      }
   }

   public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
      this.setWoke();
      return super.attackEntityFrom(source, amount);
   }

   public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() == Items.BONE && !this.isTamed()) {
         return false;
      } else if (!this.isTamed() && !itemstack.isEmpty() && itemstack.getItem() == Items.COAL && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || player.isCreative() || player.getActivePotionEffect(MobEffects.FIRE_RESISTANCE) != null) && !this.world.isRemote) {
         if (this.rand.nextDouble() < Foxhounds.tameChance) {
            this.setTamedBy(player);
            this.navigator.clearPath();
            this.setAttackTarget((EntityLivingBase)null);
            this.aiSit.setSitting(true);
            this.setHealth(20.0F);
            this.playTameEffect(true);
            this.world.setEntityState(this, (byte)7);
         } else {
            this.playTameEffect(false);
            this.world.setEntityState(this, (byte)6);
         }

         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         return true;
      } else if (itemstack.getItem() == Item.getItemFromBlock(TinyPotato.tiny_potato)) {
         this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 0.5F + (float)Math.random() * 0.5F);
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.timeUntilPotatoEmerges = 1201;
         return true;
      } else {
         if (!this.world.isRemote) {
            this.setWoke();
         }

         return super.processInteract(player, hand);
      }
   }

   public boolean canMateWith(EntityAnimal otherAnimal) {
      return super.canMateWith(otherAnimal) && otherAnimal instanceof EntityFoxhound;
   }

   public EntityWolf createChild(EntityAgeable otherParent) {
      EntityWolf entitywolf = new EntityFoxhound(this.world);
      EaglercraftUUID uuid = this.getOwnerId();
      if (uuid != null) {
         entitywolf.setOwnerId(uuid);
         entitywolf.setTamed(true);
      }

      return entitywolf;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("OhLawdHeComin", this.timeUntilPotatoEmerges);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.timeUntilPotatoEmerges = compound.getInteger("OhLawdHeComin");
   }

   protected SoundEvent getAmbientSound() {
      return this.isSleeping() ? null : super.getAmbientSound();
   }

   public boolean isSleeping() {
      return ((Boolean)this.dataManager.get(SLEEPING)).booleanValue();
   }

   public void setSleeping(boolean sleeping) {
      this.dataManager.set(SLEEPING, Boolean.valueOf(sleeping));
   }

   public boolean getCanSpawnHere() {
      IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
      return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevel() && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F && iblockstate.canEntitySpawn(this);
   }

   public EntityAISleep getAISleep() {
      return this.aiSleep;
   }

   private void setWoke() {
      EntityAISleep sleep = this.getAISleep();
      if (sleep != null) {
         this.setSleeping(false);
         sleep.setSleeping(false);
      }

   }

   public float getBlockPathWeight(BlockPos pos) {
      return 0.5F - this.world.getLightBrightness(pos);
   }

   protected boolean isValidLightLevel() {
      BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      int i = this.world.getLightFromNeighbors(blockpos);
      return i < 8;
   }
}
