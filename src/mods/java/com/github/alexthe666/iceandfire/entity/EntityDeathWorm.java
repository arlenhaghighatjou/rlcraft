package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModKeys;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.DeathWormAIFindSandTarget;
import com.github.alexthe666.iceandfire.entity.ai.DeathWormAIGetInSand;
import com.github.alexthe666.iceandfire.entity.ai.DeathWormAITarget;
import com.github.alexthe666.iceandfire.entity.ai.DeathWormAIWander;
import com.github.alexthe666.iceandfire.entity.ai.DeathwormAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.PathNavigateDeathWormLand;
import com.github.alexthe666.iceandfire.entity.ai.PathNavigateDeathWormSand;
import com.github.alexthe666.iceandfire.message.MessageDeathWormHitbox;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.client.model.tools.ChainBuffer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.multipart.IMultipartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDeathWorm extends EntityTameable implements IBlacklistedFromStatues, IMultipartEntity, IAnimatedEntity, IVillagerFear, IAnimalFear, IPhasesThroughBlock {
   private int animationTick;
   private boolean willExplode = false;
   private int ticksTillExplosion = 60;
   private Animation currentAnimation;
   private EntityMutlipartPart[] segments = new EntityMutlipartPart[6];
   @SideOnly(Side.CLIENT)
   public ChainBuffer tail_buffer;
   private boolean isSandNavigator;
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.VARINT);
   private static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.FLOAT);
   private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.BYTE);
   private static final DataParameter<Integer> WORM_AGE = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.VARINT);
   private static final DataParameter<BlockPos> HOME = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.BLOCK_POS);
   public static Animation ANIMATION_BITE = Animation.create(10);
   public static final ResourceLocation TAN_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "deathworm_tan"));
   public static final ResourceLocation WHITE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "deathworm_white"));
   public static final ResourceLocation RED_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "deathworm_red"));
   public static final ResourceLocation TAN_GIANT_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "deathworm_tan_giant"));
   public static final ResourceLocation WHITE_GIANT_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "deathworm_white_giant"));
   public static final ResourceLocation RED_GIANT_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "deathworm_red_giant"));

   public EntityDeathWorm(World worldIn) {
      super(worldIn);
      this.ignoreFrustumCheck = true;
      this.stepHeight = 1.0F;
      this.setSize(0.8F, 0.8F);
      if (FMLCommonHandler.instance().getSide().isClient()) {
         this.tail_buffer = new ChainBuffer();
      }

      this.spawnableBlock = Blocks.SAND;
      this.switchNavigator(false);
      this.tasks.addTask(0, new EntityAIAttackMelee(this, 1.5D, true));
      this.tasks.addTask(1, new DeathWormAIFindSandTarget(this, 10));
      this.tasks.addTask(2, new DeathWormAIGetInSand(this, 1.0D));
      this.tasks.addTask(3, new DeathWormAIWander(this, 1.0D));
      this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(3, new DeathwormAITargetItems(this, false, false));
      this.targetTasks.addTask(5, new DeathWormAITarget(this, EntityLivingBase.class, false, new Predicate<EntityLivingBase>() {
         public boolean apply(@Nullable EntityLivingBase input) {
            if (EntityDeathWorm.this.isTamed()) {
               return input instanceof EntityMob;
            } else {
               boolean var10000;
               label31: {
                  if (IceAndFire.CONFIG.deathWormAttackMonsters) {
                     if (!(input instanceof EntityLivingBase)) {
                        break label31;
                     }
                  } else if (!(input instanceof EntityAnimal) && !(input instanceof EntityPlayer)) {
                     break label31;
                  }

                  if (DragonUtils.isAlive(input) && (!(input instanceof EntityDragonBase) || !((EntityDragonBase)input).isModelDead()) && !EntityDeathWorm.this.isOwner(input)) {
                     var10000 = true;
                     return var10000;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }
      }));
      this.initSegments(1.0F);
   }

   public boolean getCanSpawnHere() {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getEntityBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      return this.world.getBlockState(blockpos.down()).getBlock() == this.spawnableBlock && this.getRNG().nextInt(1 + IceAndFire.CONFIG.deathWormSpawnCheckChance) == 0 && this.world.getLight(blockpos) > 8 && super.getCanSpawnHere();
   }

   public void onUpdateParts() {
      for(Entity entity : this.segments) {
         if (entity != null) {
            entity.onUpdate();
         }
      }

   }

   protected int getExperiencePoints(EntityPlayer player) {
      return (int)(this.getScaleForAge() * 5.0F) + this.world.rand.nextInt(10);
   }

   public void initSegments(float scale) {
      this.setScaleForAge(false);
      this.segments = new EntityMutlipartPart[11];

      for(int i = 0; i < this.segments.length; ++i) {
         this.segments[i] = new EntityMutlipartPart(this, (-0.8F - (float)i * 0.8F) * scale, 0.0F, 0.0F, 0.7F * scale, 0.7F * scale, 1.0F);
      }

   }

   private void clearSegments() {
      for(Entity entity : this.segments) {
         if (entity != null) {
            entity.onKillCommand();
            this.world.removeEntityDangerously(entity);
         }
      }

   }

   public void setExplosive(boolean explosive) {
      this.willExplode = true;
      this.ticksTillExplosion = 60;
   }

   public void setScaleForAge(boolean baby) {
      this.setScale(Math.min(this.getScaleForAge(), 7.0F));
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getAnimation() != ANIMATION_BITE) {
         this.setAnimation(ANIMATION_BITE);
         this.playSound(this.getScaleForAge() > 3.0F ? ModSounds.DEATHWORM_GIANT_ATTACK : ModSounds.DEATHWORM_ATTACK, 1.0F, 1.0F);
      }

      if (this.getRNG().nextInt(3) == 0 && this.getScaleForAge() > 1.0F && this.world.getGameRules().getBoolean("mobGriefing")) {
         SandExplosion explosion = new SandExplosion(this.world, this, entityIn.posX, entityIn.posY, entityIn.posZ, this.getScaleForAge());
         explosion.doExplosionA();
         explosion.doExplosionB(true);
      }

      return false;
   }

   public void onDeath(DamageSource cause) {
      if (!ForgeHooks.onLivingDeath(this, cause)) {
         this.clearSegments();
         if (!this.dead) {
            Entity entity = cause.getTrueSource();
            EntityLivingBase entitylivingbase = this.getAttackingEntity();
            if (this.scoreValue >= 0 && entitylivingbase != null) {
               entitylivingbase.awardKillScore(this, this.scoreValue, cause);
            }

            if (entity != null) {
               entity.onKillEntity(this);
            }

            this.dead = true;
            this.getCombatTracker().reset();
            if (!this.world.isRemote) {
               int i = ForgeHooks.getLootingLevel(this, entity, cause);
               this.captureDrops = true;
               this.capturedDrops.clear();
               if (this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot")) {
                  boolean flag = this.recentlyHit > 0;
                  this.dropLoot(flag, i, cause);
               }

               this.captureDrops = false;
               if (!ForgeHooks.onLivingDrops(this, cause, this.capturedDrops, i, this.recentlyHit > 0)) {
                  for(EntityItem item : this.capturedDrops) {
                     this.world.spawnEntity(item);
                     item.posY = (double)this.getSurface((int)item.posX, (int)item.posY, (int)item.posZ);
                  }
               }
            }

            this.world.setEntityState(this, (byte)3);
         }

      }
   }

   public void fall(float distance, float damageMultiplier) {
   }

   @Nullable
   private EntityItem dropItemAt(ItemStack stack, double x, double y, double z) {
      EntityItem entityitem = new EntityItem(this.world, x, y, z, stack);
      entityitem.setDefaultPickupDelay();
      if (this.captureDrops) {
         this.capturedDrops.add(entityitem);
      } else {
         this.world.spawnEntity(entityitem);
      }

      return entityitem;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      switch(this.getVariant()) {
      case 0:
         return this.getScaleForAge() > 3.0F ? TAN_GIANT_LOOT : TAN_LOOT;
      case 1:
         return this.getScaleForAge() > 3.0F ? WHITE_GIANT_LOOT : WHITE_LOOT;
      case 2:
         return this.getScaleForAge() > 3.0F ? RED_GIANT_LOOT : RED_LOOT;
      default:
         return null;
      }
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable ageable) {
      return null;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VARIANT, Integer.valueOf(0));
      this.dataManager.register(SCALE, Float.valueOf(1.0F));
      this.dataManager.register(CONTROL_STATE, Byte.valueOf((byte)0));
      this.dataManager.register(WORM_AGE, Integer.valueOf(10));
      this.dataManager.register(HOME, BlockPos.ORIGIN);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("Variant", this.getVariant());
      compound.setFloat("Scale", this.getScale());
      compound.setInteger("WormAge", this.getWormAge());
      compound.setLong("WormHome", this.getWormHome().toLong());
      compound.setBoolean("WillExplode", this.willExplode);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setVariant(compound.getInteger("Variant"));
      this.setDeathWormScale(compound.getFloat("Scale"));
      this.setWormAge(compound.getInteger("WormAge"));
      this.setWormHome(BlockPos.fromLong(compound.getLong("WormHome")));
      this.willExplode = compound.getBoolean("WillExplode");
   }

   private void setStateField(int i, boolean newState) {
      byte prevState = ((Byte)this.dataManager.get(CONTROL_STATE)).byteValue();
      if (newState) {
         this.dataManager.set(CONTROL_STATE, Byte.valueOf((byte)(prevState | 1 << i)));
      } else {
         this.dataManager.set(CONTROL_STATE, Byte.valueOf((byte)(prevState & ~(1 << i))));
      }

   }

   public byte getControlState() {
      return ((Byte)this.dataManager.get(CONTROL_STATE)).byteValue().byteValue();
   }

   public void setControlState(byte state) {
      this.dataManager.set(CONTROL_STATE, Byte.valueOf(state));
   }

   public int getVariant() {
      return ((Integer)this.dataManager.get(VARIANT)).intValue().intValue();
   }

   public void setVariant(int variant) {
      this.dataManager.set(VARIANT, Integer.valueOf(variant));
   }

   public BlockPos getWormHome() {
      return (BlockPos)this.dataManager.get(HOME);
   }

   public void setWormHome(BlockPos home) {
      if (home instanceof BlockPos) {
         this.dataManager.set(HOME, home);
      }

   }

   public int getWormAge() {
      return Math.max(1, ((Integer)this.dataManager.get(WORM_AGE)).intValue().intValue());
   }

   public void setWormAge(int age) {
      this.dataManager.set(WORM_AGE, Integer.valueOf(age));
   }

   public float getScale() {
      return ((Float)this.dataManager.get(SCALE)).floatValue().floatValue();
   }

   public float getScaleForAge() {
      return this.getScale() * ((float)this.getWormAge() / 5.0F);
   }

   public void setDeathWormScale(float scale) {
      this.dataManager.set(SCALE, Float.valueOf(scale));
      this.updateAttributes();
      this.clearSegments();
      if (!this.world.isRemote) {
         this.initSegments(scale * ((float)this.getWormAge() / 5.0F));
         IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageDeathWormHitbox(this.getEntityId(), scale * ((float)this.getWormAge() / 5.0F)));
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setVariant(this.getRNG().nextInt(3));
      float size = 0.25F + (float)(Math.random() * 0.3499999940395355D);
      this.setDeathWormScale(this.getRNG().nextInt(20) == 0 ? size * 4.0F : size);
      if (this.isSandBelow() && this.getScaleForAge() != 1.0F) {
         this.motionY = -0.5D;
      }

      return livingdata;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(IceAndFire.CONFIG.deathWormAttackStrength);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(IceAndFire.CONFIG.deathWormMaxHealth);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)Math.min(2048, IceAndFire.CONFIG.deathWormTargetSearchLength));
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
   }

   public void updatePassenger(Entity passenger) {
      super.updatePassenger(passenger);
      if (this.isPassenger(passenger)) {
         this.renderYawOffset = this.rotationYaw;
         this.rotationYaw = passenger.rotationYaw;
         float radius = -0.5F * this.getScaleForAge();
         float angle = 0.017453292F * this.renderYawOffset;
         double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
         double extraZ = (double)(radius * MathHelper.cos(angle));
         passenger.setPosition(this.posX + extraX, this.posY + (double)this.getEyeHeight() - 0.550000011920929D, this.posZ + extraZ);
      }

   }

   @Nullable
   public Entity getControllingPassenger() {
      for(Entity passenger : this.getPassengers()) {
         if (passenger instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)passenger;
            return player;
         }
      }

      return null;
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      player.getHeldItem(hand);
      if (this.getWormAge() > 4 && !player.isRiding() && player.getHeldItemMainhand().getItem() == Items.FISHING_ROD && player.getHeldItemOffhand().getItem() == Items.FISHING_ROD && !this.world.isRemote) {
         player.startRiding(this);
         return true;
      } else {
         return super.processInteract(player, hand);
      }
   }

   private void switchNavigator(boolean inSand) {
      if (inSand) {
         this.moveHelper = new EntityDeathWorm.SandMoveHelper();
         this.navigator = new PathNavigateDeathWormSand(this, this.world);
         this.isSandNavigator = true;
      } else {
         this.moveHelper = new EntityMoveHelper(this);
         this.navigator = new PathNavigateDeathWormLand(this, this.world);
         this.isSandNavigator = false;
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (source != DamageSource.IN_WALL && source != DamageSource.FALLING_BLOCK) {
         return this.isBeingRidden() && source.getTrueSource() != null && this.getControllingPassenger() != null && source.getTrueSource() == this.getControllingPassenger() ? false : super.attackEntityFrom(source, amount);
      } else {
         return false;
      }
   }

   public boolean checkBlockCollision(AxisAlignedBB bb) {
      int j2 = MathHelper.floor(bb.minX);
      int k2 = MathHelper.ceil(bb.maxX);
      int l2 = MathHelper.floor(bb.minY);
      int i3 = MathHelper.ceil(bb.maxY);
      int j3 = MathHelper.floor(bb.minZ);
      int k3 = MathHelper.ceil(bb.maxZ);
      PooledMutableBlockPos blockpos$pooledmutableblockpos = PooledMutableBlockPos.retain();

      for(int l3 = j2; l3 < k2; ++l3) {
         for(int i4 = l2; i4 < i3; ++i4) {
            for(int j4 = j3; j4 < k3; ++j4) {
               IBlockState iblockstate1 = this.world.getBlockState(blockpos$pooledmutableblockpos.setPos(l3, i4, j4));
               if (iblockstate1.getMaterial() != Material.AIR && iblockstate1.getMaterial() != Material.SAND) {
                  blockpos$pooledmutableblockpos.release();
                  return true;
               }
            }
         }
      }

      blockpos$pooledmutableblockpos.release();
      return false;
   }

   public boolean isEntityInsideOpaqueBlock() {
      PooledMutableBlockPos blockpos$pooledmutableblockpos = PooledMutableBlockPos.retain();

      for(int i = 0; i < 8; ++i) {
         int j = MathHelper.floor(this.posY + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
         int k = MathHelper.floor(this.posX + (double)(((float)((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
         int l = MathHelper.floor(this.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F));
         if (blockpos$pooledmutableblockpos.getX() != k || blockpos$pooledmutableblockpos.getY() != j || blockpos$pooledmutableblockpos.getZ() != l) {
            blockpos$pooledmutableblockpos.setPos(k, j, l);
            if (this.world.getBlockState(blockpos$pooledmutableblockpos).causesSuffocation() && this.world.getBlockState(blockpos$pooledmutableblockpos).getMaterial() != Material.SAND) {
               blockpos$pooledmutableblockpos.release();
               return true;
            }
         }
      }

      blockpos$pooledmutableblockpos.release();
      return false;
   }

   protected boolean pushOutOfBlocks(double x, double y, double z) {
      BlockPos blockpos = new BlockPos(x, y, z);
      double d0 = x - (double)blockpos.getX();
      double d1 = y - (double)blockpos.getY();
      double d2 = z - (double)blockpos.getZ();
      if (!this.world.collidesWithAnyBlock(this.getEntityBoundingBox())) {
         return false;
      } else {
         EnumFacing enumfacing = EnumFacing.UP;
         double d3 = Double.MAX_VALUE;
         if (!this.isBlockFullCube(blockpos.west()) && d0 < d3) {
            d3 = d0;
            enumfacing = EnumFacing.WEST;
         }

         if (!this.isBlockFullCube(blockpos.east()) && 1.0D - d0 < d3) {
            d3 = 1.0D - d0;
            enumfacing = EnumFacing.EAST;
         }

         if (!this.isBlockFullCube(blockpos.north()) && d2 < d3) {
            d3 = d2;
            enumfacing = EnumFacing.NORTH;
         }

         if (!this.isBlockFullCube(blockpos.south()) && 1.0D - d2 < d3) {
            d3 = 1.0D - d2;
            enumfacing = EnumFacing.SOUTH;
         }

         if (!this.isBlockFullCube(blockpos.up()) && 1.0D - d1 < d3) {
            d3 = 1.0D - d1;
            enumfacing = EnumFacing.UP;
         }

         float f = this.rand.nextFloat() * 0.2F + 0.1F;
         float f1 = (float)enumfacing.getAxisDirection().getOffset();
         if (enumfacing.getAxis() == Axis.X) {
            this.motionX = (double)(f1 * f);
            this.motionY *= 0.75D;
            this.motionZ *= 0.75D;
         } else if (enumfacing.getAxis() == Axis.Y) {
            this.motionX *= 0.75D;
            this.motionY = (double)(f1 * f);
            this.motionZ *= 0.75D;
         } else if (enumfacing.getAxis() == Axis.Z) {
            this.motionX *= 0.75D;
            this.motionY *= 0.75D;
            this.motionZ = (double)(f1 * f);
         }

         return true;
      }
   }

   private boolean isBlockFullCube(BlockPos pos) {
      AxisAlignedBB axisalignedbb = this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos);
      return this.world.getBlockState(pos).getMaterial() != Material.SAND && axisalignedbb != Block.NULL_AABB && axisalignedbb.getAverageEdgeLength() >= 1.0D;
   }

   private void updateAttributes() {
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Math.min(0.2D, 0.15D * (double)this.getScaleForAge()));
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Math.max(1.0D, IceAndFire.CONFIG.deathWormAttackStrength * (double)this.getScaleForAge()));
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Math.max(6.0D, IceAndFire.CONFIG.deathWormMaxHealth * (double)this.getScaleForAge()));
      this.heal(30.0F * this.getScaleForAge());
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      if (this.isTamed()) {
         this.heal(14.0F);
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.willExplode) {
         if (this.ticksTillExplosion == 0) {
            this.world.newExplosion((Entity)null, this.posX, this.posY, this.posZ, 2.5F * this.getScaleForAge(), false, ForgeEventFactory.getMobGriefingEvent(this.world, this));
         } else {
            --this.ticksTillExplosion;
         }
      }

      if (this.ticksExisted == 1) {
         this.initSegments(this.getScaleForAge());
      }

      if (this.ticksExisted % 1000 == 0 && this.getWormAge() < 5) {
         this.setWormAge(Math.min(5, this.getWormAge() + 1));
         this.clearSegments();
         this.heal(15.0F);
         this.setDeathWormScale(this.getScale());
         if (this.world.isRemote) {
            for(int i = 0; (float)i < 10.0F * this.getScaleForAge(); ++i) {
               this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, (double)((float)this.getSurface((int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ)) + 0.5F), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[0]);

               for(int j = 0; j < this.segments.length; ++j) {
                  this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.segments[j].posX + (double)(this.rand.nextFloat() * this.segments[j].width * 2.0F) - (double)this.segments[j].width, (double)((float)this.getSurface((int)Math.floor(this.segments[j].posX), (int)Math.floor(this.segments[j].posY), (int)Math.floor(this.segments[j].posZ)) + 0.5F), this.segments[j].posZ + (double)(this.rand.nextFloat() * this.segments[j].width * 2.0F) - (double)this.segments[j].width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[0]);
               }
            }
         }
      }

      if (this.getControllingPassenger() != null) {
         if (this.isEntityInsideOpaqueBlock()) {
            this.motionY = 2.0D;
         } else {
            this.noClip = false;
         }
      }

      if (this.getControllingPassenger() != null && this.getAttackTarget() != null) {
         this.getNavigator().clearPath();
         this.setAttackTarget((EntityLivingBase)null);
      }

      if (this.getAttackTarget() == null) {
         this.rotationPitch = 0.0F;
      } else {
         this.faceEntity(this.getAttackTarget(), 10.0F, 10.0F);
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist >= 4.0D * (double)this.getScaleForAge() && dist <= 16.0D * (double)this.getScaleForAge() && (this.isInSand() || this.onGround)) {
            double d0 = this.getAttackTarget().posX - this.posX;
            double d1 = this.getAttackTarget().posZ - this.posZ;
            float leap = MathHelper.sqrt(d0 * d0 + d1 * d1);
            if ((double)leap >= 1.0E-4D) {
               this.motionX += d0 / (double)leap * 0.800000011920929D + this.motionX * 0.20000000298023224D;
               this.motionZ += d1 / (double)leap * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
            }

            this.motionY = 0.5D;
            this.setAnimation(ANIMATION_BITE);
         }

         if (dist < Math.min(4.0D, 4.0D * (double)this.getScaleForAge()) && this.getAnimation() == ANIMATION_BITE) {
            float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), f);
            this.motionY /= 2.0D;
            this.motionY -= 0.4D;
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
      if (!this.world.isBlockLoaded(blockpos$mutableblockpos)) {
         return 0;
      } else {
         blockpos$mutableblockpos.setY(MathHelper.floor(this.posY + (double)this.getEyeHeight()));
         if (this.world.getBlockState(blockpos$mutableblockpos).getMaterial() == Material.SAND || this.isEntityInsideOpaqueBlock()) {
            blockpos$mutableblockpos.setY(this.world.getHeight(MathHelper.floor(this.posX), MathHelper.floor(this.posZ)));
         }

         return this.world.getCombinedLight(blockpos$mutableblockpos, 0);
      }
   }

   public boolean canBeSteered() {
      return true;
   }

   public int getSurface(int x, int y, int z) {
      BlockPos pos;
      for(pos = new BlockPos(x, y, z); !this.world.isAirBlock(pos); pos = pos.up()) {
         ;
      }

      return pos.getY();
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.getScaleForAge() > 3.0F ? ModSounds.DEATHWORM_GIANT_IDLE : ModSounds.DEATHWORM_IDLE;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.getScaleForAge() > 3.0F ? ModSounds.DEATHWORM_GIANT_HURT : ModSounds.DEATHWORM_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return this.getScaleForAge() > 3.0F ? ModSounds.DEATHWORM_GIANT_DIE : ModSounds.DEATHWORM_DIE;
   }

   public void onUpdate() {
      super.onUpdate();
      this.onUpdateParts();
      if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer) {
         EntityLivingBase target = DragonUtils.riderLookingAtEntity(this, (EntityPlayer)this.getControllingPassenger(), 3.0D);
         if (this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(ANIMATION_BITE);
            this.playSound(this.getScaleForAge() > 3.0F ? ModSounds.DEATHWORM_GIANT_ATTACK : ModSounds.DEATHWORM_ATTACK, 1.0F, 1.0F);
            if (this.getRNG().nextInt(3) == 0 && this.getScaleForAge() > 1.0F) {
               float radius = 1.5F * this.getScaleForAge();
               float angle = 0.017453292F * this.renderYawOffset;
               double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
               double extraZ = (double)(radius * MathHelper.cos(angle));
               SandExplosion explosion = new SandExplosion(this.world, this, this.posX + extraX, this.posY - (double)this.getEyeHeight(), this.posZ + extraZ, this.getScaleForAge() * 0.75F);
               explosion.doExplosionA();
               explosion.doExplosionB(true);
            }
         }

         if (target != null) {
            target.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         }
      }

      if (!this.isInSand()) {
         this.noClip = false;
      } else {
         IBlockState state = this.world.getBlockState((new BlockPos(this.posX, (double)this.getSurface((int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ)), this.posZ)).down());
         int blockId = Block.getStateId(state);
         if (state.isOpaqueCube() && this.world.isRemote) {
            this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, (double)((float)this.getSurface((int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ)) + 0.5F), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[]{blockId});

            for(int i = 0; i < this.segments.length; ++i) {
               this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.segments[i].prevPosX + (double)(this.rand.nextFloat() * this.segments[i].width * 2.0F) - (double)this.segments[i].width, (double)((float)this.getSurface((int)Math.floor(this.segments[i].prevPosX), (int)Math.floor(this.segments[i].prevPosY), (int)Math.floor(this.segments[i].prevPosZ)) + 0.5F), this.segments[i].prevPosZ + (double)(this.rand.nextFloat() * this.segments[i].width * 2.0F) - (double)this.segments[i].width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[]{blockId});
            }
         }

         if (this.ticksExisted % 10 == 0) {
            this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1.0F, 0.5F);
         }
      }

      if (this.up() && this.onGround) {
         this.jump();
      }

      if (this.isInSand() && !this.isSandNavigator) {
         this.switchNavigator(true);
      }

      if (!this.isInSandStrict() && this.isSandNavigator) {
         this.switchNavigator(false);
      }

      if (this.world.isRemote) {
         this.tail_buffer.calculateChainSwingBuffer(90.0F, 20, 5.0F, this);
      }

      if (this.getControllingPassenger() != null) {
         this.noClip = false;
         this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
         if (this.isSandBelow() && this.world.isRemote) {
            this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, (double)((float)this.getSurface((int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ)) + 0.5F), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[]{Block.getIdFromBlock(Blocks.SAND)});

            for(int i = 0; i < this.segments.length; ++i) {
               this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.segments[i].posX + (double)(this.rand.nextFloat() * this.segments[i].width * 2.0F) - (double)this.segments[i].width, (double)((float)this.getSurface((int)Math.floor(this.segments[i].posX), (int)Math.floor(this.segments[i].posY), (int)Math.floor(this.segments[i].posZ)) + 0.5F), this.segments[i].posZ + (double)(this.rand.nextFloat() * this.segments[i].width * 2.0F) - (double)this.segments[i].width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[]{Block.getIdFromBlock(Blocks.SAND)});
            }
         }
      }

      if (this.world.isRemote) {
         this.updateClientControls();
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   @SideOnly(Side.CLIENT)
   protected void updateClientControls() {
      Minecraft mc = Minecraft.getMinecraft();
      if (this.isRidingPlayer(mc.player)) {
         byte previousState = this.getControlState();
         this.up(mc.gameSettings.keyBindJump.isKeyDown());
         this.dismount(mc.gameSettings.keyBindSneak.isKeyDown());
         this.attack(ModKeys.dragon_strike.isKeyDown());
         byte controlState = this.getControlState();
         if (controlState != previousState) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, this.posX, this.posY, this.posZ));
         }
      }

      if (this.getRidingEntity() != null && this.getRidingEntity() == mc.player) {
         byte previousState = this.getControlState();
         this.dismount(mc.gameSettings.keyBindSneak.isKeyDown());
         byte controlState = this.getControlState();
         if (controlState != previousState) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, this.posX, this.posY, this.posZ));
         }
      }

   }

   public boolean up() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() & 1) == 1;
   }

   public boolean dismount() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 1 & 1) == 1;
   }

   public boolean attack() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 2 & 1) == 1;
   }

   public void up(boolean up) {
      this.setStateField(0, up);
   }

   public void dismount(boolean dismount) {
      this.setStateField(1, dismount);
   }

   public void attack(boolean attack) {
      this.setStateField(2, attack);
   }

   public boolean isRidingPlayer(EntityPlayer player) {
      return this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer && this.getControllingPassenger().getUniqueID().equals(player.getUniqueID());
   }

   public boolean isSandBelow() {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY - 1.0D);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      return iblockstate.getMaterial() == Material.SAND;
   }

   public boolean isInSand() {
      return this.getControllingPassenger() == null && this.world.isMaterialInBB(this.getEntityBoundingBox().grow(0.25D, 0.25D, 0.25D), Material.SAND);
   }

   public boolean isInSandStrict() {
      return this.getControllingPassenger() == null && this.world.isMaterialInBB(this.getEntityBoundingBox(), Material.SAND);
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
      return new Animation[]{ANIMATION_BITE};
   }

   public Entity[] getWormParts() {
      return this.segments;
   }

   public int getHorizontalFaceSpeed() {
      return 10;
   }

   public void travel(float strafe, float vertical, float forward) {
      if (this.isBeingRidden() && this.canBeSteered()) {
         EntityLivingBase controller = (EntityLivingBase)this.getControllingPassenger();
         if (controller != null) {
            if (this.getAttackTarget() != null) {
               this.setAttackTarget((EntityLivingBase)null);
               this.getNavigator().clearPath();
            }

            strafe = controller.moveStrafing * 0.5F;
            forward = controller.moveForward;
            this.fallDistance = 0.0F;
            this.setAIMoveSpeed(this.onGround ? (float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() : 1.0F);
            vertical = 0.0F;
            super.travel(strafe, 0.0F, forward);
            return;
         }
      }

      if (this.isServerWorld()) {
         if (this.isInSandStrict()) {
            this.moveRelative(strafe, vertical, forward, 0.1F);
            float f4 = 0.8F;
            float d0 = (float)EnchantmentHelper.getDepthStriderModifier(this);
            if (d0 > 3.0F) {
               d0 = 3.0F;
            }

            if (!this.onGround) {
               d0 *= 0.5F;
            }

            if (d0 > 0.0F) {
               f4 += (0.54600006F - f4) * d0 / 3.0F;
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)f4;
            this.motionX *= 0.900000011920929D;
            this.motionY *= 0.900000011920929D;
            this.motionY *= (double)f4;
            this.motionZ *= 0.900000011920929D;
            this.motionZ *= (double)f4;
         } else {
            super.travel(strafe, vertical, forward);
         }
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double deltaX = this.posX - this.prevPosX;
      double deltaZ = this.posZ - this.prevPosZ;
      double deltaY = this.posY - this.prevPosY;
      float delta = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 4.0F;
      if (delta > 1.0F) {
         delta = 1.0F;
      }

      this.limbSwingAmount += (delta - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public boolean shouldAnimalsFear(Entity entity) {
      return true;
   }

   public boolean canBeTurnedToStone() {
      return false;
   }

   public boolean canPhaseThroughBlock(World world, BlockPos pos) {
      return world.getBlockState(pos).getMaterial() == Material.SAND;
   }

   public class SandMoveHelper extends EntityMoveHelper {
      private EntityDeathWorm worm = EntityDeathWorm.this;

      public SandMoveHelper() {
         super(EntityDeathWorm.this);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO && !this.worm.getNavigator().noPath() && !this.worm.isBeingRidden()) {
            double distanceX = this.posX - this.worm.posX;
            double distanceY = this.posY - this.worm.posY;
            double distanceZ = this.posZ - this.worm.posZ;
            double distance = Math.abs(distanceX * distanceX + distanceZ * distanceZ);
            double distanceWithY = (double)MathHelper.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
            distanceY = distanceY / distanceWithY;
            float angle = (float)(Math.atan2(distanceZ, distanceX) * 180.0D / 3.141592653589793D) - 90.0F;
            this.worm.rotationYaw = this.limitAngle(this.worm.rotationYaw, angle, 30.0F);
            this.worm.setAIMoveSpeed(1.0F);
            this.worm.motionY += (double)this.worm.getAIMoveSpeed() * distanceY * 0.1D;
            if (distance < (double)Math.max(1.0F, this.entity.width)) {
               float f = this.worm.rotationYaw * 0.017453292F;
               this.worm.motionX -= (double)(MathHelper.sin(f) * 0.35F);
               this.worm.motionZ += (double)(MathHelper.cos(f) * 0.35F);
            }
         } else if (this.action == Action.JUMPING) {
            this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
         } else {
            this.worm.setAIMoveSpeed(0.0F);
         }

      }
   }
}
