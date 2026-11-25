package vazkii.quark.world.entity;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.util.EntityOpacityHandler;
import vazkii.quark.world.entity.ai.EntityAIRave;
import vazkii.quark.world.entity.ai.MovementHelperZigZag;

public class EntityCrab extends EntityAnimal {
   public static final ResourceLocation CRAB_LOOT_TABLE = new ResourceLocation("quark", "entities/crab");
   private static final DataParameter<Float> SIZE_MODIFIER = EntityDataManager.createKey(EntityCrab.class, DataSerializers.FLOAT);
   private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(new Item[]{Items.WHEAT, Items.FISH, Items.CHICKEN});
   private static int lightningCooldown;
   private boolean crabRave;
   private BlockPos jukeboxPosition;

   public EntityCrab(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 0.5F);
      this.moveHelper = new MovementHelperZigZag(this);
      this.spawnableBlock = Blocks.SAND;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean isNotColliding() {
      return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
   }

   @Nonnull
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(SIZE_MODIFIER, Float.valueOf(1.0F));
   }

   public float getEyeHeight() {
      return 0.1F * this.getSizeModifier();
   }

   public float getSizeModifier() {
      return ((Float)this.dataManager.get(SIZE_MODIFIER)).floatValue();
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
      this.tasks.addTask(2, new EntityAIRave(this));
      this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2D, false, TEMPTATION_ITEMS));
      this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
      this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(2.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
   }

   public boolean isEntityInsideOpaqueBlock() {
      return EntityOpacityHandler.isEntityInsideOpaqueBlock(this);
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.inWater) {
         this.stepHeight = 1.0F;
      } else {
         this.stepHeight = 0.6F;
      }

      if (lightningCooldown > 0) {
         --lightningCooldown;
      }

      if (this.isRaving() && (this.jukeboxPosition == null || this.jukeboxPosition.distanceSq(this.posX, this.posY, this.posZ) > 24.0D || this.world.getBlockState(this.jukeboxPosition).getBlock() != Blocks.JUKEBOX)) {
         this.party((BlockPos)null, false);
      }

      float sizeModifier = this.getSizeModifier();
      if (this.height != sizeModifier * 0.5F) {
         this.setSize(0.9F * sizeModifier, 0.5F * sizeModifier);
      }

      if (this.isRaving() && this.world.isRemote && this.ticksExisted % 10 == 0) {
         BlockPos below = this.getPosition().down();
         IBlockState belowState = this.world.getBlockState(below);
         if (belowState.getMaterial() == Material.SAND) {
            this.world.playEvent(2001, below, Block.getStateId(belowState));
         }
      }

   }

   public boolean isPushedByWater() {
      return false;
   }

   protected int decreaseAirSupply(int air) {
      return air;
   }

   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
      if (lightningCooldown <= 0) {
         this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("Lightning Bonus", 0.5D, 1));
         this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier("Lightning Bonus", 0.125D, 1));
         this.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(new AttributeModifier("Lightning Bonus", 1.0D, 1));
         float sizeModifier = Math.min(this.getSizeModifier() * 2.0F, 16.0F);
         this.dataManager.set(SIZE_MODIFIER, Float.valueOf(sizeModifier));
         this.setSize(0.9F * sizeModifier, 0.5F * sizeModifier);
         lightningCooldown = 100;
      }
   }

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
      if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityCrab)) {
         entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
      }

   }

   public boolean isBreedingItem(ItemStack stack) {
      return !stack.isEmpty() && TEMPTATION_ITEMS.contains(stack.getItem());
   }

   @Nullable
   public EntityAgeable createChild(@Nonnull EntityAgeable other) {
      return new EntityCrab(this.world);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return CRAB_LOOT_TABLE;
   }

   public void party(BlockPos pos, boolean isPartying) {
      this.jukeboxPosition = pos;
      this.crabRave = isPartying;
   }

   @SideOnly(Side.CLIENT)
   public void setPartying(BlockPos pos, boolean isPartying) {
      this.party(pos, isPartying);
   }

   public boolean isRaving() {
      return this.crabRave;
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      lightningCooldown = compound.getInteger("LightningCooldown");
      if (compound.hasKey("EnemyCrabRating")) {
         float sizeModifier = compound.getFloat("EnemyCrabRating");
         this.dataManager.set(SIZE_MODIFIER, Float.valueOf(sizeModifier));
         this.setSize(0.5F * sizeModifier, 0.9F * sizeModifier);
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("EnemyCrabRating", this.getSizeModifier());
      compound.setInteger("LightningCooldown", lightningCooldown);
   }
}
