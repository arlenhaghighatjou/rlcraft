package vazkii.quark.world.entity;

import com.google.common.collect.Sets;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.world.base.EnumStonelingVariant;
import vazkii.quark.world.entity.ai.EntityAIActWary;
import vazkii.quark.world.entity.ai.EntityAIFavorBlock;
import vazkii.quark.world.entity.ai.EntityAIRunAndPoof;
import vazkii.quark.world.feature.Frogs;
import vazkii.quark.world.feature.Stonelings;

public class EntityStoneling extends EntityCreature {
   public static final ResourceLocation CARRY_LOOT_TABLE = new ResourceLocation("quark", "entities/stoneling_carry");
   public static final ResourceLocation LOOT_TABLE = new ResourceLocation("quark", "entities/stoneling");
   private static final DataParameter<ItemStack> CARRYING_ITEM = EntityDataManager.createKey(EntityStoneling.class, DataSerializers.ITEM_STACK);
   private static final DataParameter<Byte> VARIANT = EntityDataManager.createKey(EntityStoneling.class, DataSerializers.BYTE);
   private static final DataParameter<Float> HOLD_ANGLE = EntityDataManager.createKey(EntityStoneling.class, DataSerializers.FLOAT);
   private static final String TAG_CARRYING_ITEM = "carryingItem";
   private static final String TAG_VARIANT = "variant";
   private static final String TAG_HOLD_ANGLE = "itemAngle";
   private static final String TAG_PLAYER_MADE = "playerMade";
   private EntityAIActWary waryTask;
   private boolean isTame;

   public EntityStoneling(World worldIn) {
      super(worldIn);
      this.setPathPriority(PathNodeType.DAMAGE_CACTUS, 1.0F);
      this.setPathPriority(PathNodeType.DANGER_CACTUS, 1.0F);
      this.setSize(0.5F, 0.9F);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(CARRYING_ITEM, ItemStack.EMPTY);
      this.dataManager.register(VARIANT, Byte.valueOf((byte)0));
      this.dataManager.register(HOLD_ANGLE, Float.valueOf(0.0F));
   }

   protected void initEntityAI() {
      this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.2D, 0.98F));
      this.tasks.addTask(4, new EntityAIFavorBlock(this, 0.2D, Blocks.DIAMOND_ORE));
      if (Stonelings.enableDiamondHeart || Stonelings.tamableStonelings) {
         int priority = Stonelings.tamableStonelings ? 0 : 3;
         this.tasks.addTask(priority, new EntityAITempt(this, 0.6D, Items.DIAMOND, false));
      }

      this.tasks.addTask(2, new EntityAIRunAndPoof(this, EntityPlayer.class, 4.0F, 0.5D, 0.5D));
      this.tasks.addTask(1, this.waryTask = new EntityAIActWary(this, 0.1D, 6.0D, Stonelings.cautiousStonelings));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.inWater) {
         this.stepHeight = 1.0F;
      } else {
         this.stepHeight = 0.6F;
      }

      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL && !this.isTame) {
         this.setDead();
      }

      this.prevRenderYawOffset = this.prevRotationYaw;
      this.renderYawOffset = this.rotationYaw;
   }

   public boolean isCreatureType(@Nonnull EnumCreatureType type, boolean forSpawnCount) {
      if (this.isTame) {
         return type == EnumCreatureType.CREATURE;
      } else {
         return type == EnumCreatureType.MONSTER;
      }
   }

   protected boolean canDespawn() {
      return !this.isTame;
   }

   private static boolean fitsOreKey(ItemStack stack, String key) {
      if (stack.isEmpty()) {
         return false;
      } else {
         int[] ids = OreDictionary.getOreIDs(stack);
         int oreKey = OreDictionary.getOreID(key);

         for(int id : ids) {
            if (id == oreKey) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (!stack.isEmpty() && stack.getItem() == Items.NAME_TAG) {
         stack.interactWithEntity(player, this, hand);
         return true;
      } else {
         return super.processInteract(player, hand);
      }
   }

   @Nonnull
   public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
      if (hand == EnumHand.MAIN_HAND) {
         ItemStack playerItem = player.getHeldItem(hand);
         if (!this.world.isRemote) {
            if (this.isPlayerMade()) {
               if (!player.isSneaking() && !playerItem.isEmpty()) {
                  EnumStonelingVariant currentVariant = this.getVariant();
                  EnumStonelingVariant targetVariant = null;

                  for(EnumStonelingVariant variant : EnumStonelingVariant.values()) {
                     if (fitsOreKey(playerItem, variant.getOreKey())) {
                        targetVariant = variant;
                     }
                  }

                  if (targetVariant != null) {
                     if (this.world instanceof WorldServer) {
                        ((WorldServer)this.world).spawnParticle(EnumParticleTypes.HEART, this.posX, this.posY + (double)this.height, this.posZ, 1, 0.1D, 0.1D, 0.1D, 0.1D, new int[0]);
                        if (targetVariant != currentVariant) {
                           ((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 16, 0.1D, 0.1D, 0.1D, 0.25D, new int[]{Block.getStateId(targetVariant.getDisplayState())});
                        }
                     }

                     if (targetVariant != currentVariant) {
                        this.playSound(QuarkSounds.ENTITY_STONELING_EAT, 1.0F, 1.0F);
                        this.dataManager.set(VARIANT, Byte.valueOf(targetVariant.getIndex()));
                     }

                     this.playSound(QuarkSounds.ENTITY_STONELING_PURR, 1.0F, 1.0F + this.world.rand.nextFloat() * 1.0F);
                     this.heal(1.0F);
                     if (!player.capabilities.isCreativeMode) {
                        playerItem.shrink(1);
                     }

                     return EnumActionResult.SUCCESS;
                  }

                  return EnumActionResult.PASS;
               }

               ItemStack stonelingItem = (ItemStack)this.dataManager.get(CARRYING_ITEM);
               if (!stonelingItem.isEmpty() || !playerItem.isEmpty()) {
                  player.setHeldItem(hand, stonelingItem.copy());
                  this.dataManager.set(CARRYING_ITEM, playerItem.copy());
                  if (playerItem.isEmpty()) {
                     this.playSound(QuarkSounds.ENTITY_STONELING_GIVE, 1.0F, 1.0F);
                  } else {
                     this.playSound(QuarkSounds.ENTITY_STONELING_TAKE, 1.0F, 1.0F);
                  }
               }
            } else if (Stonelings.tamableStonelings && fitsOreKey(playerItem, "gemDiamond")) {
               this.heal(8.0F);
               this.setPlayerMade(true);
               this.playSound(QuarkSounds.ENTITY_STONELING_PURR, 1.0F, 1.0F + this.world.rand.nextFloat() * 1.0F);
               if (!player.capabilities.isCreativeMode) {
                  playerItem.shrink(1);
               }

               if (this.world instanceof WorldServer) {
                  ((WorldServer)this.world).spawnParticle(EnumParticleTypes.HEART, this.posX, this.posY + (double)this.height, this.posZ, 4, 0.1D, 0.1D, 0.1D, 0.1D, new int[0]);
               }

               return EnumActionResult.SUCCESS;
            }
         }
      }

      return EnumActionResult.PASS;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
      byte variant;
      if (data instanceof EnumStonelingVariant) {
         variant = ((EnumStonelingVariant)data).getIndex();
      } else {
         variant = (byte)this.world.rand.nextInt(EnumStonelingVariant.values().length);
      }

      this.dataManager.set(VARIANT, Byte.valueOf(variant));
      this.dataManager.set(HOLD_ANGLE, Float.valueOf(this.world.rand.nextFloat() * 90.0F - 45.0F));
      if (!this.isTame && !this.world.isRemote) {
         if (ModuleLoader.isFeatureEnabled(Frogs.class) && this.rand.nextDouble() < 0.01D) {
            EntityFrog frog = new EntityFrog(this.world, 0.25F);
            frog.setPosition(this.posX, this.posY, this.posZ);
            this.world.spawnEntity(frog);
            frog.startRiding(this);
         } else {
            List<ItemStack> items = this.world.getLootTableManager().getLootTableFromLocation(CARRY_LOOT_TABLE).generateLootForPools(this.rand, (new Builder((WorldServer)this.world)).build());
            if (!items.isEmpty()) {
               this.dataManager.set(CARRYING_ITEM, items.get(0));
            }
         }
      }

      return super.onInitialSpawn(difficulty, data);
   }

   public boolean isEntityInvulnerable(@Nonnull DamageSource source) {
      return source == DamageSource.CACTUS || source.isProjectile() || super.isEntityInvulnerable(source);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean isNotColliding() {
      return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
   }

   public double getMountedYOffset() {
      return (double)this.height;
   }

   public boolean isPushedByWater() {
      return false;
   }

   protected int decreaseAirSupply(int air) {
      return air;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   protected void damageEntity(@Nonnull DamageSource damageSrc, float damageAmount) {
      super.damageEntity(damageSrc, damageAmount);
      if (!this.isPlayerMade() && damageSrc.getTrueSource() instanceof EntityPlayer) {
         this.startle();

         for(Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(16.0D))) {
            if (entity instanceof EntityStoneling) {
               EntityStoneling stoneling = (EntityStoneling)entity;
               if (!stoneling.isPlayerMade() && stoneling.getEntitySenses().canSee(this)) {
                  this.startle();
               }
            }
         }
      }

   }

   public void startle() {
      this.waryTask.startle();

      for(EntityAITaskEntry task : Sets.newHashSet(this.tasks.taskEntries)) {
         if (task.action instanceof EntityAITempt) {
            this.tasks.removeTask(task.action);
         }
      }

   }

   protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
      super.dropEquipment(wasRecentlyHit, lootingModifier);
      ItemStack stack = this.getCarryingItem();
      if (!stack.isEmpty()) {
         this.entityDropItem(stack, 0.0F);
      }

   }

   protected ResourceLocation getLootTable() {
      return Stonelings.enableDiamondHeart ? LOOT_TABLE : null;
   }

   public void setPlayerMade(boolean value) {
      this.isTame = value;
   }

   public ItemStack getCarryingItem() {
      return (ItemStack)this.dataManager.get(CARRYING_ITEM);
   }

   public EnumStonelingVariant getVariant() {
      return EnumStonelingVariant.byIndex(((Byte)this.dataManager.get(VARIANT)).byteValue());
   }

   public float getItemAngle() {
      return ((Float)this.dataManager.get(HOLD_ANGLE)).floatValue();
   }

   public boolean isPlayerMade() {
      return this.isTame;
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("carryingItem", 10)) {
         NBTTagCompound itemCmp = compound.getCompoundTag("carryingItem");
         ItemStack stack = new ItemStack(itemCmp);
         this.dataManager.set(CARRYING_ITEM, stack);
      }

      this.dataManager.set(VARIANT, Byte.valueOf(compound.getByte("variant")));
      this.dataManager.set(HOLD_ANGLE, Float.valueOf(compound.getFloat("itemAngle")));
      this.setPlayerMade(compound.getBoolean("playerMade"));
   }

   public boolean canEntityBeSeen(Entity entityIn) {
      Vec3d origin = new Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      Vec3d targetBase = new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ);
      float otherEyes = entityIn.getEyeHeight();

      for(float height = 0.0F; height <= otherEyes; height += otherEyes / 8.0F) {
         if (this.world.rayTraceBlocks(origin, targetBase.addVector(0.0D, (double)height, 0.0D), false, true, false) == null) {
            return true;
         }
      }

      return false;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setTag("carryingItem", this.getCarryingItem().serializeNBT());
      compound.setByte("variant", this.getVariant().getIndex());
      compound.setFloat("itemAngle", this.getItemAngle());
      compound.setBoolean("playerMade", this.isPlayerMade());
   }

   public boolean getCanSpawnHere() {
      IBlockState state = this.world.getBlockState((new BlockPos(this)).down());
      if (state.getMaterial() != Material.ROCK) {
         return false;
      } else {
         return Stonelings.dimensions.canSpawnHere(this.world) && this.posY < (double)Stonelings.maxYLevel && this.isValidLightLevel() && super.getCanSpawnHere();
      }
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return QuarkSounds.ENTITY_STONELING_CRY;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return QuarkSounds.ENTITY_STONELING_DIE;
   }

   public int getTalkInterval() {
      return 1200;
   }

   public void playLivingSound() {
      SoundEvent sound = this.getAmbientSound();
      if (sound != null) {
         this.playSound(sound, this.getSoundVolume(), 1.0F);
      }

   }

   public void setDead() {
      super.setDead();

      for(Entity passenger : this.getRecursivePassengers()) {
         if (!(passenger instanceof EntityPlayer)) {
            passenger.setDead();
         }
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.hasCustomName()) {
         String customName = this.getCustomNameTag();
         if (customName.equalsIgnoreCase("michael stevens") || customName.equalsIgnoreCase("vsauce")) {
            return QuarkSounds.ENTITY_STONELING_MICHAEL;
         }
      }

      return null;
   }

   protected boolean isValidLightLevel() {
      BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if (this.world.getLightFor(EnumSkyBlock.SKY, blockpos) != 0) {
         return false;
      } else {
         int i = this.world.getLightFromNeighbors(blockpos);
         if (this.world.isThundering()) {
            int j = this.world.getSkylightSubtracted();
            this.world.setSkylightSubtracted(10);
            i = this.world.getLightFromNeighbors(blockpos);
            this.world.setSkylightSubtracted(j);
         }

         return i <= this.rand.nextInt(8);
      }
   }
}
