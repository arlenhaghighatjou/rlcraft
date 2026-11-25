package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.client.model.util.LegSolverQuadruped;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModKeys;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.PathNavigateExperimentalGround;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.message.MessageDragonArmor;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.client.model.tools.ChainBuffer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.ilexiconn.llibrary.server.entity.multipart.IMultipartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class EntityDragonBase extends EntityTameable implements IMultipartEntity, IAnimatedEntity, IDragonFlute, IDeadMob, IVillagerFear, IAnimalFear, IDropArmor {
   private static final int FLIGHT_CHANCE_PER_TICK = 1500;
   private static final DataParameter<Integer> HUNGER = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> AGE_TICKS = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> FIREBREATHING = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> HOVERING = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> HEAD_ARMOR = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> NECK_ARMOR = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> BODY_ARMOR = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> TAIL_ARMOR = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> MODEL_DEAD = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> DEATH_STAGE = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BYTE);
   private static final DataParameter<Boolean> TACKLE = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> AGINGDISABLED = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
   public static Animation ANIMATION_EAT;
   public static Animation ANIMATION_SPEAK;
   public static Animation ANIMATION_BITE;
   public static Animation ANIMATION_SHAKEPREY;
   public static Animation ANIMATION_WINGBLAST;
   public static Animation ANIMATION_ROAR;
   public static Animation ANIMATION_TAILWHACK;
   public double minimumDamage;
   public double maximumDamage;
   public double minimumHealth;
   public double maximumHealth;
   public double minimumSpeed;
   public double maximumSpeed;
   public double minimumArmor;
   public double maximumArmor;
   public float sitProgress;
   public float sleepProgress;
   public float hoverProgress;
   public float flyProgress;
   public float fireBreathProgress;
   public int fireStopTicks;
   public int flyTicks;
   public float modelDeadProgress;
   public float ridingProgress;
   public float tackleProgress;
   public ContainerHorseChest dragonInv;
   public boolean isDaytime;
   public boolean attackDecision;
   public int flightCycle;
   public BlockPos airTarget;
   public BlockPos homePos;
   public boolean hasHomePosition = false;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer roll_buffer;
   @SideOnly(Side.CLIENT)
   public ReversedBuffer turn_buffer;
   @SideOnly(Side.CLIENT)
   public ChainBuffer tail_buffer;
   public int spacebarTicks;
   public float[][] growth_stages;
   public boolean isFire = this instanceof EntityFireDragon;
   public LegSolverQuadruped legSolver;
   protected int flyHovering;
   private boolean isSleeping;
   private boolean isSitting;
   private boolean isHovering;
   private boolean isFlying;
   private boolean isBreathingFire;
   private boolean isTackling;
   private int fireTicks;
   private int hoverTicks;
   private boolean isModelDead;
   private int animationTick;
   private Animation currentAnimation;
   private ItemStackHandler itemHandler = null;
   public int walkCycle;
   private int tacklingTicks;
   private int ticksStill;
   private float lastScale;
   private EntityDragonPart headPart;
   private EntityDragonPart neckPart;
   private EntityDragonPart rightWingUpperPart;
   private EntityDragonPart rightWingLowerPart;
   private EntityDragonPart leftWingUpperPart;
   private EntityDragonPart leftWingLowerPart;
   private EntityDragonPart tail1Part;
   private EntityDragonPart tail2Part;
   private EntityDragonPart tail3Part;
   private EntityDragonPart tail4Part;

   public EntityDragonBase(World world, double minimumDamage, double maximumDamage, double minimumHealth, double maximumHealth, double minimumSpeed, double maximumSpeed) {
      super(world);
      this.minimumDamage = minimumDamage;
      this.maximumDamage = maximumDamage;
      this.minimumHealth = minimumHealth;
      this.maximumHealth = maximumHealth;
      this.minimumSpeed = minimumSpeed;
      this.maximumSpeed = maximumSpeed;
      this.minimumArmor = 1.0D;
      this.maximumArmor = 20.0D;
      ANIMATION_EAT = Animation.create(20);
      this.updateAttributes();
      this.initDragonInv();
      if (FMLCommonHandler.instance().getSide().isClient()) {
         this.roll_buffer = new IFChainBuffer();
         this.turn_buffer = new ReversedBuffer();
         this.tail_buffer = new ChainBuffer();
      }

      this.legSolver = new LegSolverQuadruped(0.2F, 1.2F, 1.0F);
      this.resetParts(1.0F);
   }

   public void resetParts(float scale) {
      this.removeParts();
      this.headPart = new EntityDragonPart(this, 1.55F * scale, 0.0F, 0.6F * scale, 0.5F * scale, 0.35F * scale, 1.5F);
      this.neckPart = new EntityDragonPart(this, 0.85F * scale, 0.0F, 0.7F * scale, 0.5F * scale, 0.2F * scale, 1.0F);
      this.rightWingUpperPart = new EntityDragonPart(this, 1.0F * scale, 90.0F, 0.5F * scale, 0.85F * scale, 0.3F * scale, 0.5F);
      this.rightWingLowerPart = new EntityDragonPart(this, 1.4F * scale, 100.0F, 0.3F * scale, 0.85F * scale, 0.2F * scale, 0.5F);
      this.leftWingUpperPart = new EntityDragonPart(this, 1.0F * scale, -90.0F, 0.5F * scale, 0.85F * scale, 0.3F * scale, 0.5F);
      this.leftWingLowerPart = new EntityDragonPart(this, 1.4F * scale, -100.0F, 0.3F * scale, 0.85F * scale, 0.2F * scale, 0.5F);
      this.tail1Part = new EntityDragonPart(this, -0.75F * scale, 0.0F, 0.6F * scale, 0.35F * scale, 0.35F * scale, 1.0F);
      this.tail2Part = new EntityDragonPart(this, -1.15F * scale, 0.0F, 0.45F * scale, 0.35F * scale, 0.35F * scale, 1.0F);
      this.tail3Part = new EntityDragonPart(this, -1.5F * scale, 0.0F, 0.35F * scale, 0.35F * scale, 0.35F * scale, 1.0F);
      this.tail4Part = new EntityDragonPart(this, -1.95F * scale, 0.0F, 0.25F * scale, 0.45F * scale, 0.3F * scale, 1.5F);
   }

   public void removeParts() {
      if (this.headPart != null) {
         this.world.removeEntityDangerously(this.headPart);
         this.headPart = null;
      }

      if (this.neckPart != null) {
         this.world.removeEntityDangerously(this.neckPart);
         this.neckPart = null;
      }

      if (this.rightWingUpperPart != null) {
         this.world.removeEntityDangerously(this.rightWingUpperPart);
         this.rightWingUpperPart = null;
      }

      if (this.rightWingLowerPart != null) {
         this.world.removeEntityDangerously(this.rightWingLowerPart);
         this.rightWingLowerPart = null;
      }

      if (this.leftWingUpperPart != null) {
         this.world.removeEntityDangerously(this.leftWingUpperPart);
         this.leftWingUpperPart = null;
      }

      if (this.leftWingLowerPart != null) {
         this.world.removeEntityDangerously(this.leftWingLowerPart);
         this.leftWingLowerPart = null;
      }

      if (this.tail1Part != null) {
         this.world.removeEntityDangerously(this.tail1Part);
         this.tail1Part = null;
      }

      if (this.tail2Part != null) {
         this.world.removeEntityDangerously(this.tail2Part);
         this.tail2Part = null;
      }

      if (this.tail3Part != null) {
         this.world.removeEntityDangerously(this.tail3Part);
         this.tail3Part = null;
      }

      if (this.tail4Part != null) {
         this.world.removeEntityDangerously(this.tail4Part);
         this.tail4Part = null;
      }

   }

   public void updateParts() {
      if (this.headPart != null) {
         this.headPart.onUpdate();
      }

      if (this.neckPart != null) {
         this.neckPart.onUpdate();
      }

      if (this.rightWingUpperPart != null) {
         this.rightWingUpperPart.onUpdate();
      }

      if (this.rightWingLowerPart != null) {
         this.rightWingLowerPart.onUpdate();
      }

      if (this.leftWingUpperPart != null) {
         this.leftWingUpperPart.onUpdate();
      }

      if (this.leftWingLowerPart != null) {
         this.leftWingLowerPart.onUpdate();
      }

      if (this.tail1Part != null) {
         this.tail1Part.onUpdate();
      }

      if (this.tail2Part != null) {
         this.tail2Part.onUpdate();
      }

      if (this.tail3Part != null) {
         this.tail3Part.onUpdate();
      }

      if (this.tail4Part != null) {
         this.tail4Part.onUpdate();
      }

   }

   protected PathNavigate createNavigator(World worldIn) {
      return (PathNavigate)(IceAndFire.CONFIG.experimentalPathFinder ? new PathNavigateExperimentalGround(this, worldIn) : super.createNavigator(worldIn));
   }

   public boolean canDestroyBlock(BlockPos pos) {
      float hardness = this.world.getBlockState(pos).getBlock().getBlockHardness(this.world.getBlockState(pos), this.world, pos);
      return this.world.getBlockState(pos).getBlock().canEntityDestroy(this.world.getBlockState(pos), this.world, pos, this) && hardness >= 0.0F;
   }

   public boolean isMobDead() {
      return this.isModelDead();
   }

   public int getHorizontalFaceSpeed() {
      return 10 * this.getDragonStage() / 5;
   }

   private void initDragonInv() {
      ContainerHorseChest animalchest = this.dragonInv;
      this.dragonInv = new ContainerHorseChest("dragonInventory", 4);
      this.dragonInv.setCustomName(this.getName());
      if (animalchest != null) {
         int i = Math.min(animalchest.getSizeInventory(), this.dragonInv.getSizeInventory());

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = animalchest.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               this.dragonInv.setInventorySlotContents(j, itemstack.copy());
            }
         }
      }

      this.itemHandler = new ItemStackHandler(4) {
         public void onContentsChanged() {
            int dragonArmorHead = EntityDragonBase.this.getArmorInSlot(0);
            int dragonArmorNeck = EntityDragonBase.this.getArmorInSlot(1);
            int dragonArmorBody = EntityDragonBase.this.getArmorInSlot(2);
            int dragonArmorTail = EntityDragonBase.this.getArmorInSlot(3);
            EntityDragonBase.this.updateDragonSlots();
            if (EntityDragonBase.this.ticksExisted > 20) {
               if (dragonArmorHead != EntityDragonBase.this.getIntFromArmor(EntityDragonBase.this.dragonInv.getStackInSlot(0))) {
                  EntityDragonBase.this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
               }

               if (dragonArmorNeck != EntityDragonBase.this.getIntFromArmor(EntityDragonBase.this.dragonInv.getStackInSlot(1))) {
                  EntityDragonBase.this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
               }

               if (dragonArmorBody != EntityDragonBase.this.getIntFromArmor(EntityDragonBase.this.dragonInv.getStackInSlot(2))) {
                  EntityDragonBase.this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
               }

               if (dragonArmorTail != EntityDragonBase.this.getIntFromArmor(EntityDragonBase.this.dragonInv.getStackInSlot(3))) {
                  EntityDragonBase.this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
               }
            }

         }
      };
      if (this.world.isRemote) {
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, this.getIntFromArmor(this.dragonInv.getStackInSlot(0))));
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, this.getIntFromArmor(this.dragonInv.getStackInSlot(1))));
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, this.getIntFromArmor(this.dragonInv.getStackInSlot(2))));
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 3, this.getIntFromArmor(this.dragonInv.getStackInSlot(3))));
      }

   }

   public void updateDragonSlots() {
      this.setArmorInSlot(0, this.getIntFromArmor(this.dragonInv.getStackInSlot(0)));
      this.setArmorInSlot(1, this.getIntFromArmor(this.dragonInv.getStackInSlot(1)));
      this.setArmorInSlot(2, this.getIntFromArmor(this.dragonInv.getStackInSlot(2)));
      this.setArmorInSlot(3, this.getIntFromArmor(this.dragonInv.getStackInSlot(3)));
      if (this.world.isRemote) {
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, this.getIntFromArmor(this.dragonInv.getStackInSlot(0))));
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, this.getIntFromArmor(this.dragonInv.getStackInSlot(1))));
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, this.getIntFromArmor(this.dragonInv.getStackInSlot(2))));
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 3, this.getIntFromArmor(this.dragonInv.getStackInSlot(3))));
      }

      double armorStep = (this.maximumArmor - this.minimumArmor) / 125.0D;
      double oldValue = this.minimumArmor + armorStep * (double)this.getAgeInDays();
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(oldValue + this.calculateArmorModifier());
   }

   public void openGUI(EntityPlayer playerEntity) {
      if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity))) {
         playerEntity.openGui(IceAndFire.INSTANCE, 0, this.world, this.getEntityId(), 0, 0);
      }

   }

   public int getTalkInterval() {
      return 90;
   }

   protected void onDeathUpdate() {
      this.deathTime = 0;
      if (!this.isModelDead() && !this.world.isRemote && this.recentlyHit > 0) {
         int i = this.getExperiencePoints(this.attackingPlayer);
         i = ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);

         while(i > 0) {
            int j = EntityXPOrb.getXPSplit(i);
            i -= j;
            this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
         }
      }

      this.setModelDead(true);
      if (this.getDeathStage() >= this.getAgeInDays() / 5) {
         this.setDead();

         for(int k = 0; k < 40; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            if (this.world.isRemote) {
               this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1, new int[0]);
            }
         }

         for(int k = 0; k < 3; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            if (this.isFire) {
               if (this.world.isRemote) {
                  this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1, new int[0]);
               }
            } else {
               IceAndFire.PROXY.spawnParticle("snowflake", this.world, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1);
            }
         }
      }

   }

   public void setDead() {
      this.removeParts();
      super.setDead();
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return this.isChild() ? 15 : 15 + this.getDragonStage() * 35;
   }

   public int getIntFromArmor(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModItems.dragon_armor_iron) {
         return 1;
      } else if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModItems.dragon_armor_gold) {
         return 2;
      } else {
         return !stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModItems.dragon_armor_diamond ? 3 : 0;
      }
   }

   public boolean isAIDisabled() {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      return this.isModelDead() || properties == null || properties.isStone || super.isAIDisabled();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HUNGER, Integer.valueOf(0));
      this.dataManager.register(AGE_TICKS, Integer.valueOf(0));
      this.dataManager.register(GENDER, Boolean.valueOf(false));
      this.dataManager.register(VARIANT, Integer.valueOf(0));
      this.dataManager.register(SLEEPING, Boolean.valueOf(false));
      this.dataManager.register(FIREBREATHING, Boolean.valueOf(false));
      this.dataManager.register(HOVERING, Boolean.valueOf(false));
      this.dataManager.register(FLYING, Boolean.valueOf(false));
      this.dataManager.register(HEAD_ARMOR, Integer.valueOf(0));
      this.dataManager.register(NECK_ARMOR, Integer.valueOf(0));
      this.dataManager.register(BODY_ARMOR, Integer.valueOf(0));
      this.dataManager.register(TAIL_ARMOR, Integer.valueOf(0));
      this.dataManager.register(DEATH_STAGE, Integer.valueOf(0));
      this.dataManager.register(MODEL_DEAD, Boolean.valueOf(false));
      this.dataManager.register(CONTROL_STATE, Byte.valueOf((byte)0));
      this.dataManager.register(TACKLE, Boolean.valueOf(false));
      this.dataManager.register(AGINGDISABLED, Boolean.valueOf(false));
      this.dataManager.register(COMMAND, Integer.valueOf(0));
   }

   public boolean up() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() & 1) == 1;
   }

   public boolean down() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 1 & 1) == 1;
   }

   public boolean attack() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 2 & 1) == 1;
   }

   public boolean strike() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 3 & 1) == 1;
   }

   public boolean dismount() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 4 & 1) == 1;
   }

   public void up(boolean up) {
      this.setStateField(0, up);
   }

   public void down(boolean down) {
      this.setStateField(1, down);
   }

   public void attack(boolean attack) {
      this.setStateField(2, attack);
   }

   public void strike(boolean strike) {
      this.setStateField(3, strike);
   }

   public void dismount(boolean dismount) {
      this.setStateField(4, dismount);
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
      return ((Byte)this.dataManager.get(CONTROL_STATE)).byteValue();
   }

   public void setControlState(byte state) {
      this.dataManager.set(CONTROL_STATE, Byte.valueOf(state));
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

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("Hunger", this.getHunger());
      compound.setInteger("AgeTicks", this.getAgeInTicks());
      compound.setBoolean("Gender", this.isMale());
      compound.setInteger("Variant", this.getVariant());
      compound.setBoolean("Sleeping", this.isSleeping());
      compound.setBoolean("FireBreathing", this.isBreathingFire());
      compound.setBoolean("AttackDecision", this.attackDecision);
      compound.setBoolean("Hovering", this.isHovering());
      compound.setBoolean("Flying", this.isFlying());
      compound.setInteger("ArmorHead", this.getArmorInSlot(0));
      compound.setInteger("ArmorNeck", this.getArmorInSlot(1));
      compound.setInteger("ArmorBody", this.getArmorInSlot(2));
      compound.setInteger("ArmorTail", this.getArmorInSlot(3));
      compound.setInteger("DeathStage", this.getDeathStage());
      compound.setBoolean("ModelDead", this.isModelDead());
      compound.setFloat("DeadProg", this.modelDeadProgress);
      compound.setBoolean("Tackle", this.isTackling());
      if (this.dragonInv != null) {
         NBTTagList nbttaglist = new NBTTagList();

         for(int i = 0; i < this.dragonInv.getSizeInventory(); ++i) {
            ItemStack itemstack = this.dragonInv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();
               nbttagcompound.setByte("Slot", (byte)i);
               itemstack.writeToNBT(nbttagcompound);
               nbttaglist.appendTag(nbttagcompound);
            }
         }

         compound.setTag("Items", nbttaglist);
      }

      if (this.getCustomNameTag() != null && !this.getCustomNameTag().isEmpty()) {
         compound.setString("CustomName", this.getCustomNameTag());
      }

      compound.setBoolean("HasHomePosition", this.hasHomePosition);
      if (this.homePos != null && this.hasHomePosition) {
         compound.setInteger("HomeAreaX", this.homePos.getX());
         compound.setInteger("HomeAreaY", this.homePos.getY());
         compound.setInteger("HomeAreaZ", this.homePos.getZ());
      }

      compound.setBoolean("AgingDisabled", this.isAgingDisabled());
      compound.setInteger("Command", this.getCommand());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setHunger(compound.getInteger("Hunger"));
      this.setAgeInTicks(compound.getInteger("AgeTicks"));
      this.setGender(compound.getBoolean("Gender"));
      this.setVariant(compound.getInteger("Variant"));
      this.setSleeping(compound.getBoolean("Sleeping"));
      this.setBreathingFire(compound.getBoolean("FireBreathing"));
      this.attackDecision = compound.getBoolean("AttackDecision");
      this.setHovering(compound.getBoolean("Hovering"));
      this.setFlying(compound.getBoolean("Flying"));
      this.setArmorInSlot(0, compound.getInteger("ArmorHead"));
      this.setArmorInSlot(1, compound.getInteger("ArmorNeck"));
      this.setArmorInSlot(2, compound.getInteger("ArmorBody"));
      this.setArmorInSlot(3, compound.getInteger("ArmorTail"));
      if (this.dragonInv != null) {
         NBTTagList nbttaglist = compound.getTagList("Items", 10);
         this.initDragonInv();

         for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            if (j <= 4) {
               this.dragonInv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
            }
         }
      } else {
         NBTTagList nbttaglist = compound.getTagList("Items", 10);
         this.initDragonInv();

         for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            this.initDragonInv();
            this.dragonInv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
            this.setArmorInSlot(j, this.getIntFromArmor(new ItemStack(nbttagcompound)));
            if (this.world.isRemote) {
               IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, this.getIntFromArmor(new ItemStack(nbttagcompound))));
               IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, this.getIntFromArmor(new ItemStack(nbttagcompound))));
               IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, this.getIntFromArmor(new ItemStack(nbttagcompound))));
               IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 3, this.getIntFromArmor(new ItemStack(nbttagcompound))));
            }
         }
      }

      this.setDeathStage(compound.getInteger("DeathStage"));
      this.setModelDead(compound.getBoolean("ModelDead"));
      this.modelDeadProgress = compound.getFloat("DeadProg");
      if (!compound.getString("CustomName").isEmpty()) {
         this.setCustomNameTag(compound.getString("CustomName"));
      }

      this.hasHomePosition = compound.getBoolean("HasHomePosition");
      if (this.hasHomePosition && compound.getInteger("HomeAreaX") != 0 && compound.getInteger("HomeAreaY") != 0 && compound.getInteger("HomeAreaZ") != 0) {
         this.homePos = new BlockPos(compound.getInteger("HomeAreaX"), compound.getInteger("HomeAreaY"), compound.getInteger("HomeAreaZ"));
      }

      this.setTackling(compound.getBoolean("Tackle"));
      this.setAgingDisabled(compound.getBoolean("AgingDisabled"));
      this.setCommand(compound.getInteger("Command"));
   }

   @Nullable
   public Entity getControllingPassenger() {
      for(Entity passenger : this.getPassengers()) {
         if (passenger instanceof EntityPlayer && this.getAttackTarget() != passenger) {
            EntityPlayer player = (EntityPlayer)passenger;
            if (this.isTamed() && this.getOwnerId() != null && this.getOwnerId().equals(player.getUniqueID())) {
               return player;
            }
         }
      }

      return null;
   }

   public boolean isRidingPlayer(EntityPlayer player) {
      return this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer && this.getControllingPassenger().getUniqueID().equals(player.getUniqueID());
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)Math.min(2048, IceAndFire.CONFIG.dragonTargetSearchLength));
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
   }

   private void updateAttributes() {
      double healthStep = (this.maximumHealth - this.minimumHealth) / 125.0D;
      double attackStep = (this.maximumDamage - this.minimumDamage) / 125.0D;
      double speedStep = (this.maximumSpeed - this.minimumSpeed) / 125.0D;
      double armorStep = (this.maximumArmor - this.minimumArmor) / 125.0D;
      if (this.getAgeInDays() <= 125) {
         this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)Math.round(this.minimumHealth + healthStep * (double)this.getAgeInDays()));
         this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)Math.round(this.minimumDamage + attackStep * (double)this.getAgeInDays()));
         this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.minimumSpeed + speedStep * (double)this.getAgeInDays());
         double oldValue = this.minimumArmor + armorStep * (double)this.getAgeInDays();
         this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(oldValue + this.calculateArmorModifier());
      }

   }

   public int getHunger() {
      return ((Integer)this.dataManager.get(HUNGER)).intValue();
   }

   public void setHunger(int hunger) {
      this.dataManager.set(HUNGER, Integer.valueOf(Math.min(100, hunger)));
   }

   public int getVariant() {
      return ((Integer)this.dataManager.get(VARIANT)).intValue();
   }

   public void setVariant(int variant) {
      this.dataManager.set(VARIANT, Integer.valueOf(variant));
   }

   public int getAgeInDays() {
      return ((Integer)this.dataManager.get(AGE_TICKS)).intValue() / 24000;
   }

   public void setAgeInDays(int age) {
      this.dataManager.set(AGE_TICKS, Integer.valueOf(age * 24000));
   }

   public int getAgeInTicks() {
      return ((Integer)this.dataManager.get(AGE_TICKS)).intValue();
   }

   public void setAgeInTicks(int age) {
      this.dataManager.set(AGE_TICKS, Integer.valueOf(age));
   }

   public int getDeathStage() {
      return ((Integer)this.dataManager.get(DEATH_STAGE)).intValue();
   }

   public void setDeathStage(int stage) {
      this.dataManager.set(DEATH_STAGE, Integer.valueOf(stage));
   }

   public boolean isMale() {
      return ((Boolean)this.dataManager.get(GENDER)).booleanValue();
   }

   public boolean isModelDead() {
      return this.world.isRemote ? (this.isModelDead = ((Boolean)this.dataManager.get(MODEL_DEAD)).booleanValue().booleanValue()) : this.isModelDead;
   }

   public void setModelDead(boolean modeldead) {
      this.dataManager.set(MODEL_DEAD, Boolean.valueOf(modeldead));
      if (!this.world.isRemote) {
         this.isModelDead = modeldead;
      }

   }

   public boolean isHovering() {
      return this.world.isRemote ? (this.isHovering = ((Boolean)this.dataManager.get(HOVERING)).booleanValue()) : this.isHovering;
   }

   public void setHovering(boolean hovering) {
      this.dataManager.set(HOVERING, Boolean.valueOf(hovering));
      if (!this.world.isRemote) {
         this.isHovering = hovering;
      }

   }

   public boolean isFlying() {
      return this.world.isRemote ? (this.isFlying = ((Boolean)this.dataManager.get(FLYING)).booleanValue()) : this.isFlying;
   }

   public void setFlying(boolean flying) {
      this.dataManager.set(FLYING, Boolean.valueOf(flying));
      if (!this.world.isRemote) {
         this.isFlying = flying;
      }

   }

   public void setGender(boolean male) {
      this.dataManager.set(GENDER, Boolean.valueOf(male));
   }

   public boolean isSleeping() {
      if (this.world.isRemote) {
         boolean isSleeping = ((Boolean)this.dataManager.get(SLEEPING)).booleanValue();
         this.isSleeping = isSleeping;
         return isSleeping;
      } else {
         return this.isSleeping;
      }
   }

   public void setSleeping(boolean sleeping) {
      this.dataManager.set(SLEEPING, Boolean.valueOf(sleeping));
      if (!this.world.isRemote) {
         this.isSleeping = sleeping;
      }

   }

   public boolean isBlinking() {
      return this.ticksExisted % 50 > 43;
   }

   public boolean isBreathingFire() {
      if (this.world.isRemote) {
         boolean breathing = ((Boolean)this.dataManager.get(FIREBREATHING)).booleanValue();
         this.isBreathingFire = breathing;
         return breathing;
      } else {
         return this.isBreathingFire;
      }
   }

   public void setBreathingFire(boolean breathing) {
      this.dataManager.set(FIREBREATHING, Boolean.valueOf(breathing));
      if (!this.world.isRemote) {
         this.isBreathingFire = breathing;
      }

   }

   public void setTackling(boolean tackling) {
      this.dataManager.set(TACKLE, Boolean.valueOf(tackling));
      if (!this.world.isRemote) {
         this.isTackling = tackling;
      }

   }

   public void setAgingDisabled(boolean isAgingDisabled) {
      this.dataManager.set(AGINGDISABLED, Boolean.valueOf(isAgingDisabled));
   }

   protected boolean canFitPassenger(Entity passenger) {
      return this.getPassengers().size() < 2;
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
      if (!this.world.isRemote) {
         this.isSitting = sitting;
      }

      byte b0 = ((Byte)this.dataManager.get(TAMED)).byteValue();
      if (sitting) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 | 1)));
      } else {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 & -2)));
      }

   }

   public int getArmorInSlot(int i) {
      switch(i) {
      case 1:
         return ((Integer)this.dataManager.get(NECK_ARMOR)).intValue();
      case 2:
         return ((Integer)this.dataManager.get(BODY_ARMOR)).intValue();
      case 3:
         return ((Integer)this.dataManager.get(TAIL_ARMOR)).intValue();
      default:
         return ((Integer)this.dataManager.get(HEAD_ARMOR)).intValue();
      }
   }

   public void riderShootFire(Entity controller) {
   }

   public void onKillEntity(EntityLivingBase entity) {
      super.onKillEntity(entity);
      this.setHunger(this.getHunger() + FoodUtils.getFoodPoints(entity));
   }

   public void setArmorInSlot(int i, int armorType) {
      switch(i) {
      case 0:
         this.dataManager.set(HEAD_ARMOR, Integer.valueOf(armorType));
         break;
      case 1:
         this.dataManager.set(NECK_ARMOR, Integer.valueOf(armorType));
         break;
      case 2:
         this.dataManager.set(BODY_ARMOR, Integer.valueOf(armorType));
         break;
      case 3:
         this.dataManager.set(TAIL_ARMOR, Integer.valueOf(armorType));
      }

      if (this.world.isRemote) {
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), i, armorType));
      }

      double armorStep = (this.maximumArmor - this.minimumArmor) / 125.0D;
      double oldValue = this.minimumArmor + armorStep * (double)this.getAgeInDays();
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(oldValue + this.calculateArmorModifier());
   }

   private double calculateArmorModifier() {
      double val = 1.0D;

      for(int i = 0; i < 4; ++i) {
         switch(this.getArmorInSlot(i)) {
         case 1:
            val += 2.0D;
            break;
         case 2:
            val += 3.0D;
            break;
         case 3:
            val += 5.0D;
         }
      }

      return val;
   }

   public boolean canMove() {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      if (properties != null && properties.isStone) {
         return false;
      } else {
         return !this.isSitting() && !this.isSleeping() && this.getControllingPassenger() == null && !this.isModelDead() && this.sleepProgress == 0.0F && this.getAnimation() != ANIMATION_SHAKEPREY;
      }
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      int lastDeathStage = this.getAgeInDays() / 5;
      if (this.isModelDead() && this.getDeathStage() < lastDeathStage && player.capabilities.allowEdit) {
         if (!this.world.isRemote && !stack.isEmpty() && stack.getItem() != null && stack.getItem() == Items.GLASS_BOTTLE && this.getDeathStage() < lastDeathStage / 2 && IceAndFire.CONFIG.dragonDropBlood) {
            if (!player.capabilities.isCreativeMode) {
               stack.shrink(1);
            }

            this.setDeathStage(this.getDeathStage() + 1);
            player.inventory.addItemStackToInventory(new ItemStack(this instanceof EntityFireDragon ? ModItems.fire_dragon_blood : ModItems.ice_dragon_blood, 1));
            return true;
         } else {
            if (!this.world.isRemote && stack.isEmpty() && IceAndFire.CONFIG.dragonDropSkull) {
               if (this.getDeathStage() == lastDeathStage - 1) {
                  ItemStack skull = new ItemStack(ModItems.dragon_skull, 1, this.isFire ? 0 : 1);
                  skull.setTagCompound(new NBTTagCompound());
                  skull.getTagCompound().setInteger("Stage", this.getDragonStage());
                  skull.getTagCompound().setInteger("DragonType", 0);
                  skull.getTagCompound().setInteger("DragonAge", this.getAgeInDays());
                  this.setDeathStage(this.getDeathStage() + 1);
                  if (!this.world.isRemote) {
                     this.entityDropItem(skull, 1.0F);
                  }

                  this.setDead();
               } else if (this.getDeathStage() == lastDeathStage / 2 - 1 && IceAndFire.CONFIG.dragonDropHeart) {
                  ItemStack heart = new ItemStack(this instanceof EntityFireDragon ? ModItems.fire_dragon_heart : ModItems.ice_dragon_heart, 1);
                  ItemStack egg = new ItemStack(this.getVariantEgg(this.rand.nextInt(4)), 1);
                  if (!this.world.isRemote) {
                     this.entityDropItem(heart, 1.0F);
                     if (!this.isMale() && this.getDragonStage() > 3) {
                        this.entityDropItem(egg, 1.0F);
                     }
                  }

                  this.setDeathStage(this.getDeathStage() + 1);
               } else {
                  this.setDeathStage(this.getDeathStage() + 1);
                  ItemStack drop = this.getRandomDrop();
                  if (!drop.isEmpty() && !this.world.isRemote) {
                     this.entityDropItem(drop, 1.0F);
                  }
               }
            }

            return true;
         }
      } else {
         if (!this.isModelDead() && this.isOwner(player)) {
            if (!stack.isEmpty()) {
               if (this.isBreedingItem(stack) && this.isAdult()) {
                  this.setGrowingAge(0);
                  this.consumeItemFromStack(player, stack);
                  this.setInLove(player);
                  return true;
               }

               if (stack.getItem() != null) {
                  int itemFoodAmount = FoodUtils.getFoodPoints(stack, true, !this.isFire);
                  if (itemFoodAmount <= 0 || this.getHunger() >= 100 && this.getHealth() >= this.getMaxHealth()) {
                     if (stack.getItem() == ModItems.dragon_meal) {
                        this.growDragon(1);
                        this.setHunger(this.getHunger() + 20);
                        this.heal(Math.min(this.getHealth(), (float)((int)(this.getMaxHealth() / 2.0F))));
                        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                        this.spawnItemCrackParticles(stack.getItem());
                        this.spawnItemCrackParticles(Items.BONE);
                        this.spawnItemCrackParticles(Items.DYE);
                        this.eatFoodBonus(stack);
                        if (!player.isCreative()) {
                           stack.shrink(1);
                        }

                        return true;
                     }

                     if (stack.getItem() == ModItems.sickly_dragon_meal && !this.isAgingDisabled()) {
                        this.setHunger(this.getHunger() + 20);
                        this.heal(this.getMaxHealth());
                        this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundVolume(), this.getSoundPitch());
                        this.spawnItemCrackParticles(stack.getItem());
                        this.spawnItemCrackParticles(Items.BONE);
                        this.spawnItemCrackParticles(Items.DYE);
                        this.spawnItemCrackParticles(Items.POISONOUS_POTATO);
                        this.spawnItemCrackParticles(Items.POISONOUS_POTATO);
                        this.setAgingDisabled(true);
                        this.eatFoodBonus(stack);
                        if (!player.isCreative()) {
                           stack.shrink(1);
                        }

                        return true;
                     }

                     if (stack.getItem() == ModItems.dragon_stick) {
                        if (player.isSneaking()) {
                           BlockPos pos = new BlockPos(this);
                           this.homePos = pos;
                           this.hasHomePosition = true;
                           player.sendStatusMessage(new TextComponentTranslation("dragon.command.new_home", new Object[]{this.homePos.getX(), this.homePos.getY(), this.homePos.getZ()}), true);
                           return true;
                        }

                        this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, this.getSoundVolume(), this.getSoundPitch());
                        this.setCommand(this.getCommand() + 1);
                        if (this.getCommand() > 1) {
                           this.setCommand(0);
                        }

                        player.sendStatusMessage(new TextComponentTranslation("dragon.command." + (this.getCommand() == 1 ? "sit" : "stand"), new Object[0]), true);
                        return true;
                     }

                     StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
                     if (stack.getItem() != ModItems.dragon_horn || this.world.isRemote || properties != null && properties.isStone) {
                        return super.processInteract(player, hand);
                     }

                     this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 3.0F, 1.25F);
                     ItemStack stack1 = new ItemStack(this.isFire ? ModItems.dragon_horn_fire : ModItems.dragon_horn_ice);
                     stack1.setTagCompound(new NBTTagCompound());
                     this.writeEntityToNBT(stack1.getTagCompound());
                     player.setHeldItem(hand, stack1);
                     this.setDead();
                     return true;
                  }

                  this.setHunger(this.getHunger() + itemFoodAmount);
                  this.setHealth(Math.min(this.getMaxHealth(), (float)((int)(this.getHealth() + (float)(itemFoodAmount / 10)))));
                  this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                  this.spawnItemCrackParticles(stack.getItem());
                  this.eatFoodBonus(stack);
                  if (!player.isCreative()) {
                     stack.shrink(1);
                  }

                  return true;
               }
            } else {
               if (stack.isEmpty() && !player.isSneaking() && !this.isDead) {
                  if (this.getDragonStage() < 2) {
                     this.startRiding(player, true);
                  }

                  if (this.getDragonStage() > 2 && !player.isRiding()) {
                     player.setSneaking(false);
                     player.startRiding(this, true);
                     this.setSleeping(false);
                  }

                  if (this.getDragonStage() < 2) {
                     this.startRiding(player, true);
                  }

                  return true;
               }

               if (stack.isEmpty() && player.isSneaking()) {
                  this.openGUI(player);
                  return true;
               }
            }
         }

         return super.processInteract(player, hand);
      }
   }

   private ItemStack getRandomDrop() {
      ItemStack stack = this.getItemFromLootTable();
      if (stack.getItem() == ModItems.dragonbone) {
         this.playSound(SoundEvents.ENTITY_SKELETON_AMBIENT, 1.0F, 1.0F);
      } else {
         this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
      }

      return stack;
   }

   public abstract ResourceLocation getDeadLootTable();

   public ItemStack getItemFromLootTable() {
      LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(this.getDeadLootTable());
      Builder lootcontext$builder = (new Builder((WorldServer)this.world)).withLootedEntity(this).withDamageSource(DamageSource.GENERIC);
      if (this.attackingPlayer != null) {
         lootcontext$builder = lootcontext$builder.withPlayer(this.attackingPlayer).withLuck(this.attackingPlayer.getLuck());
      }

      List<ItemStack> loot = loottable.generateLootForPools(this.rand, lootcontext$builder.build());
      return loot.isEmpty() ? ItemStack.EMPTY : (ItemStack)loot.get(0);
   }

   public void eatFoodBonus(ItemStack stack) {
   }

   protected void despawnEntity() {
      if (!IceAndFire.CONFIG.canDragonsDespawn) {
         super.despawnEntity();
      }

   }

   public void growDragon(int ageInDays) {
      if (!this.isAgingDisabled()) {
         this.setAgeInDays(this.getAgeInDays() + ageInDays);
         this.setScaleForAge(false);
         this.resetPositionToBB();
         if (this.getAgeInDays() % 25 == 0) {
            for(int i = 0; (float)i < this.getRenderSize() * 4.0F; ++i) {
               double motionX = this.getRNG().nextGaussian() * 0.07D;
               double motionY = this.getRNG().nextGaussian() * 0.07D;
               double motionZ = this.getRNG().nextGaussian() * 0.07D;
               float f = (float)((double)this.getRNG().nextFloat() * (this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX) + this.getEntityBoundingBox().minX);
               float f1 = (float)((double)this.getRNG().nextFloat() * (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) + this.getEntityBoundingBox().minY);
               float f2 = (float)((double)this.getRNG().nextFloat() * (this.getEntityBoundingBox().maxZ - this.getEntityBoundingBox().minZ) + this.getEntityBoundingBox().minZ);
               if (this.world.isRemote) {
                  this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (double)f, (double)f1, (double)f2, motionX, motionY, motionZ, new int[0]);
               }
            }
         }

         this.updateAttributes();
      }
   }

   public void spawnItemCrackParticles(Item item) {
      for(int i = 0; i < 15; ++i) {
         double motionX = this.getRNG().nextGaussian() * 0.07D;
         double motionY = this.getRNG().nextGaussian() * 0.07D;
         double motionZ = this.getRNG().nextGaussian() * 0.07D;
         float headPosX = (float)(this.posX + (double)(1.9F * this.getRenderSize() * 0.3F) * Math.cos((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
         float headPosZ = (float)(this.posZ + (double)(1.9F * this.getRenderSize() * 0.3F) * Math.sin((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
         float headPosY = (float)(this.posY + (double)(this.getRenderSize() * 0.2F));
         if (this.world.isRemote) {
            this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, (double)headPosX, (double)headPosY, (double)headPosZ, motionX, motionY, motionZ, new int[]{Item.getIdFromItem(item)});
         }
      }

   }

   public boolean isDaytime() {
      return this.world.isDaytime();
   }

   private boolean isStuck() {
      return !this.isTamed() && (!this.getNavigator().noPath() && (this.getNavigator().getPath() == null || this.getNavigator().getPath().getFinalPathPoint() != null && this.getDistanceSq(new BlockPos(this.getNavigator().getPath().getFinalPathPoint().x, this.getNavigator().getPath().getFinalPathPoint().y, this.getNavigator().getPath().getFinalPathPoint().z)) > 15.0D) || this.airTarget != null) && this.ticksStill > 80 && !this.isHovering() && this.canMove();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote) {
         if (this.isSitting() && (this.getCommand() != 1 || this.getControllingPassenger() != null)) {
            this.setSitting(false);
         }

         if (!this.isSitting() && this.getCommand() == 1 && this.getControllingPassenger() == null) {
            this.setSitting(true);
         }

         if (this.isSitting()) {
            this.getNavigator().clearPath();
         }

         if (this.isBeyondHeight()) {
            this.motionY -= 0.10000000149011612D;
         }

         if (this.isInLove()) {
            this.world.setEntityState(this, (byte)18);
         }

         if ((int)this.prevPosX == (int)this.posX && (int)this.prevPosZ == (int)this.posZ) {
            ++this.ticksStill;
         } else {
            this.ticksStill = 0;
         }

         if (this.getDragonStage() >= 3 && this.isStuck() && this.world.getGameRules().getBoolean("mobGriefing") && IceAndFire.CONFIG.dragonGriefing != 2) {
            if (this.getAnimation() == NO_ANIMATION && this.ticksExisted % 5 == 0) {
               this.setAnimation(ANIMATION_TAILWHACK);
            }

            if (this.getAnimation() == ANIMATION_TAILWHACK && this.getAnimationTick() == 10) {
               IBlockState state = this.world.getBlockState(new BlockPos(this));
               BlockBreakExplosion explosion = new BlockBreakExplosion(this.world, this, this.posX, this.posY, this.posZ, (float)(4 * this.getDragonStage() - 2));
               explosion.doExplosionA();
               explosion.doExplosionB(true);
               this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
            }
         }
      }

      if (this.isTackling() && !this.isFlying() && this.onGround) {
         ++this.tacklingTicks;
         if (this.tacklingTicks == 40) {
            this.tacklingTicks = 0;
            this.setTackling(false);
            this.setFlying(false);
         }
      }

      if (this.walkCycle < 39) {
         ++this.walkCycle;
      } else {
         this.walkCycle = 0;
      }

      if (!this.world.isRemote && this.getRNG().nextInt(500) == 0 && !this.isModelDead() && !this.isSleeping()) {
         this.roar();
      }

      if (!this.world.isRemote && this.onGround && this.getNavigator().noPath() && this.getAttackTarget() != null && this.getAttackTarget().posY - 3.0D > this.posY && this.getRNG().nextInt(15) == 0 && this.canMove() && !this.isHovering() && !this.isFlying() && !this.isChild()) {
         this.setHovering(true);
         this.setSleeping(false);
         this.setSitting(false);
         this.flyHovering = 0;
         this.hoverTicks = 0;
         this.flyTicks = 0;
      }

      if (this.getAnimation() == ANIMATION_WINGBLAST && (this.getAnimationTick() == 17 || this.getAnimationTick() == 22 || this.getAnimationTick() == 28)) {
         this.spawnGroundEffects();
         if (this.getAttackTarget() != null) {
            boolean flag = this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() / 4));
            this.getAttackTarget().knockBack(this.getAttackTarget(), (float)this.getDragonStage() * 0.6F, 1.0D, 1.0D);
            this.attackDecision = this.getRNG().nextBoolean();
         }
      }

      if (!this.world.isRemote && this.isFlying() && this.getAttackTarget() != null && this.attackDecision && this.isDirectPathBetweenPoints(this.getPositionVector(), this.getAttackTarget().getPositionVector())) {
         this.setTackling(true);
      }

      if (!this.world.isRemote && this.isFlying() && this.getAttackTarget() != null && this.isTackling() && this.getEntityBoundingBox().expand(2.0D, 2.0D, 2.0D).intersects(this.getAttackTarget().getEntityBoundingBox())) {
         this.attackDecision = true;
         this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)(this.getDragonStage() * 3));
         this.spawnGroundEffects();
         this.setFlying(false);
         this.setHovering(false);
      }

      if (!this.world.isRemote && this.isTackling() && this.getAttackTarget() == null) {
         this.setTackling(false);
      }

      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      if (properties != null && properties.isStone) {
         this.setFlying(false);
         this.setHovering(false);
      } else {
         if (this.isFlying() && this.ticksExisted % 40 == 0 || this.isFlying() && this.isSleeping()) {
            this.setFlying(false);
            this.setFlying(true);
            this.setSleeping(false);
         }

         if (!this.canMove() && this.getAttackTarget() != null) {
            this.setAttackTarget((EntityLivingBase)null);
         }

         if (!this.canMove()) {
            this.getNavigator().clearPath();
         }

         if (this.getControllingPassenger() != null) {
            if (this.motionY > 0.5D) {
               this.motionY = 0.5D;
            }

            if (this.motionY < -0.5D) {
               this.motionY = -0.5D;
            }
         } else {
            if (this.motionY > 0.5D) {
               this.motionY = 0.5D;
            }

            if (this.motionY < -0.5D) {
               this.motionY = -0.5D;
            }

            if (this.motionY > 1.0D) {
               this.motionY = 0.0D;
            }
         }

         this.updateCheckPlayer();
         AnimationHandler.INSTANCE.updateAnimations(this);
         this.legSolver.update(this);
         if ((this.isFlying() || this.isHovering()) && !this.isModelDead()) {
            if (this.flightCycle < 58) {
               this.flightCycle += 2;
            } else {
               this.flightCycle = 0;
            }

            if (this.flightCycle == 2) {
               this.playSound(ModSounds.DRAGON_FLIGHT, this.getSoundVolume() * (float)IceAndFire.CONFIG.dragonFlapNoiseDistance, this.getSoundPitch());
            }

            if (this.flightCycle > 10 && this.flightCycle < 12) {
               this.spawnGroundEffects();
            }

            if (this.isModelDead() && this.flightCycle != 0) {
               this.flightCycle = 0;
            }
         }

         if (this.isModelDead() && (this.isFlying() || this.isHovering())) {
            this.setFlying(false);
            this.setHovering(false);
         }

         boolean sitting = this.isSitting() && !this.isModelDead() && !this.isSleeping() && !this.isHovering() && !this.isFlying();
         if (sitting && this.sitProgress < 20.0F) {
            this.sitProgress += 0.5F;
         } else if (!sitting && this.sitProgress > 0.0F) {
            this.sitProgress -= 0.5F;
         }

         boolean sleeping = this.isSleeping() && !this.isHovering() && !this.isFlying();
         if (sleeping && this.sleepProgress < 20.0F) {
            this.sleepProgress += 0.5F;
         } else if (!sleeping && this.sleepProgress > 0.0F) {
            this.sleepProgress -= 0.5F;
         }

         boolean fireBreathing = this.isBreathingFire();
         if (fireBreathing && this.fireBreathProgress < 5.0F) {
            this.fireBreathProgress += 0.5F;
         } else if (!fireBreathing && this.fireBreathProgress > 0.0F) {
            this.fireBreathProgress -= 0.5F;
         }

         boolean hovering = this.isHovering();
         if (hovering && this.hoverProgress < 20.0F) {
            this.hoverProgress += 0.5F;
         } else if (!hovering && this.hoverProgress > 0.0F) {
            this.hoverProgress -= 0.5F;
         }

         boolean tackling = this.isTackling();
         if (tackling && this.tackleProgress < 5.0F) {
            this.tackleProgress += 0.5F;
         } else if (!tackling && this.tackleProgress > 0.0F) {
            --this.tackleProgress;
         }

         boolean flying = !tackling && this.isFlying() || !this.onGround && !this.isHovering() && this.airTarget != null;
         if (flying && this.flyProgress < 20.0F) {
            this.flyProgress += 0.5F;
         } else if (!flying && this.flyProgress > 0.0F) {
            this.flyProgress -= 0.5F;
         }

         boolean modeldead = this.isModelDead();
         if (modeldead && this.modelDeadProgress < 20.0F) {
            this.modelDeadProgress += 0.5F;
         } else if (!modeldead && this.modelDeadProgress > 0.0F) {
            this.modelDeadProgress -= 0.5F;
         }

         boolean riding = this.isRiding() && this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityPlayer;
         if (riding && this.ridingProgress < 20.0F) {
            this.ridingProgress += 0.5F;
         } else if (!riding && this.ridingProgress > 0.0F) {
            this.ridingProgress -= 0.5F;
         }

         if (!this.isModelDead()) {
            if (!this.world.isRemote && this.onGround && this.doesWantToLand() && (this.isFlying() || this.isHovering())) {
               this.setFlying(false);
               this.setHovering(false);
            }

            if (this.getControllingPassenger() != null && !this.onGround && (this.isFlying() || this.isHovering())) {
               this.motionY *= 0.0D;
            }

            if (this.isHovering()) {
               if (this.isSleeping()) {
                  this.setHovering(false);
               }

               ++this.hoverTicks;
               if (this.doesWantToLand() && !this.onGround) {
                  this.motionY -= 0.25D;
               } else {
                  if (this.getControllingPassenger() == null && !this.isBeyondHeight()) {
                     this.motionY += 0.08D;
                  }

                  if (this.hoverTicks > 40) {
                     if (!this.isChild()) {
                        this.setFlying(true);
                     }

                     this.setHovering(false);
                     this.flyHovering = 0;
                     this.hoverTicks = 0;
                     this.flyTicks = 0;
                  }
               }

               if (this.flyHovering == 0) {
                  ;
               }

               if (this.flyHovering == 1) {
                  ;
               }

               if (this.flyHovering == 2) {
                  ;
               }
            }

            if (this.isSleeping()) {
               this.getNavigator().clearPath();
            }

            if (!this.isFlying() && !this.isHovering() && this.airTarget != null && this.onGround) {
               this.airTarget = null;
            }

            if (this.isFlying() && this.airTarget == null && this.onGround && this.getControllingPassenger() == null) {
               this.setFlying(false);
            }

            if (this.isFlying() && this.getAttackTarget() == null) {
               this.flyAround();
            } else if (this.getAttackTarget() != null) {
               this.flyTowardsTarget();
            }

            if (this.onGround && this.flyTicks != 0) {
               this.flyTicks = 0;
            }

            if (this.isFlying() && this.doesWantToLand()) {
               this.setFlying(false);
               this.setHovering(!this.onGround);
               if (this.onGround) {
                  this.flyTicks = 0;
                  this.setFlying(false);
               }
            }

            if (this.isFlying()) {
               ++this.flyTicks;
            }

            if ((this.isHovering() || this.isFlying()) && this.isSleeping()) {
               this.setFlying(false);
               this.setHovering(false);
            }

            if ((properties == null || properties != null && !properties.isStone) && (!this.world.isRemote && this.getRNG().nextInt(1500) == 0 && !this.isSitting() && !this.isFlying() && this.getPassengers().isEmpty() && !this.isChild() && !this.isHovering() && !this.isSleeping() && this.canMove() && this.onGround || this.posY < -1.0D)) {
               this.setHovering(true);
               this.setSleeping(false);
               this.setSitting(false);
               this.flyHovering = 0;
               this.hoverTicks = 0;
               this.flyTicks = 0;
            }

            if (this.getAttackTarget() != null && this.getAttackTarget().posY + 5.0D < this.posY && (properties == null || properties != null && !properties.isStone) && !this.world.isRemote && !this.isSitting() && !this.isFlying() && this.getPassengers().isEmpty() && !this.isChild() && !this.isHovering() && !this.isSleeping() && this.canMove() && this.onGround) {
               this.setHovering(true);
               this.setSleeping(false);
               this.setSitting(false);
               this.flyHovering = 0;
               this.hoverTicks = 0;
               this.flyTicks = 0;
            }

            if (this.getAttackTarget() != null && !this.getPassengers().isEmpty() && this.getOwner() != null && this.getPassengers().contains(this.getOwner())) {
               this.setAttackTarget((EntityLivingBase)null);
            }

            if (!this.isAgingDisabled()) {
               this.setAgeInTicks(this.getAgeInTicks() + 1);
               if (this.getAgeInTicks() % 24000 == 0) {
                  this.updateAttributes();
                  this.growDragon(0);
               }
            }

            if (this.getAgeInTicks() % IceAndFire.CONFIG.dragonHungerTickRate == 0 && this.getHunger() > 0) {
               this.setHunger(this.getHunger() - 1);
            }

            if (this.attackDecision && this.getAttackTarget() != null && (double)this.getDistance(this.getAttackTarget()) > Math.min(this.getEntityBoundingBox().getAverageEdgeLength() * 5.0D, 25.0D) && !this.isChild()) {
               this.attackDecision = false;
            }

            if ((!this.attackDecision || this.getRNG().nextInt(750) == 0) && this.getDragonStage() < 2) {
               this.attackDecision = this.getRNG().nextBoolean();

               for(int i = 0; i < 5; ++i) {
                  float radiusAdd = (float)i * 0.15F;
                  float headPosX = (float)(this.posX + (double)(1.8F * this.getRenderSize() * (0.3F + radiusAdd)) * Math.cos((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
                  float headPosZ = (float)(this.posZ + (double)(1.8F * this.getRenderSize() * (0.3F + radiusAdd)) * Math.sin((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
                  float headPosY = (float)(this.posY + 0.5D * (double)this.getRenderSize() * 0.30000001192092896D);
                  if (this.isFire && this.world.isRemote) {
                     this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)headPosX, (double)headPosY, (double)headPosZ, 0.0D, 0.0D, 0.0D, new int[0]);
                  } else if (this.world.isRemote) {
                     IceAndFire.PROXY.spawnParticle("dragonice", this.world, (double)headPosX, (double)headPosY, (double)headPosZ, 0.0D, 0.0D, 0.0D);
                  }
               }

               if (this.isFire) {
                  this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
               } else {
                  this.playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);
               }
            }

            if (this.isBreathingFire()) {
               ++this.fireTicks;
               if (this.fireTicks > this.getDragonStage() * 25 || this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && this.fireStopTicks <= 0) {
                  this.setBreathingFire(false);
                  this.attackDecision = this.getRNG().nextBoolean();
                  this.fireTicks = 0;
               }

               if (this.fireStopTicks > 0 && this.getOwner() != null && this.getPassengers().contains(this.getOwner())) {
                  --this.fireStopTicks;
               }
            }

            if (this.isFlying() && this.getAttackTarget() != null && this.getEntityBoundingBox().expand(3.0D, 3.0D, 3.0D).intersects(this.getAttackTarget().getEntityBoundingBox())) {
               this.attackEntityAsMob(this.getAttackTarget());
            }

            this.breakBlock();
         }
      }
   }

   private boolean isBeyondHeight() {
      if (this.posY > (double)this.world.getHeight()) {
         return true;
      } else {
         return this.posY > (double)IceAndFire.CONFIG.maxDragonFlight;
      }
   }

   public void breakBlock() {
      if (IceAndFire.CONFIG.dragonGriefing != 2 || this.isTamed() && !IceAndFire.CONFIG.tamedDragonGriefing) {
         float hardness = IceAndFire.CONFIG.dragonGriefing != 1 && this.getDragonStage() > 3 ? 5.0F : 1.6F;
         if (!this.isModelDead() && this.getDragonStage() >= 3 && this.canMove()) {
            for(int a = (int)Math.round(this.getEntityBoundingBox().minX) - 1; a <= (int)Math.round(this.getEntityBoundingBox().maxX) + 1; ++a) {
               for(int b = (int)Math.round(this.getEntityBoundingBox().minY) + 1; b <= (int)Math.round(this.getEntityBoundingBox().maxY) + 2 && b <= 127; ++b) {
                  for(int c = (int)Math.round(this.getEntityBoundingBox().minZ) - 1; c <= (int)Math.round(this.getEntityBoundingBox().maxZ) + 1; ++c) {
                     IBlockState state = this.world.getBlockState(new BlockPos(a, b, c));
                     Block block = state.getBlock();
                     if (state.getMaterial() != Material.AIR && !(block instanceof BlockBush) && !(block instanceof BlockLiquid) && block != Blocks.BEDROCK && state.getBlockHardness(this.world, new BlockPos(a, b, c)) < hardness && DragonUtils.canDragonBreak(state.getBlock()) && this.canDestroyBlock(new BlockPos(a, b, c))) {
                        this.motionX *= 0.6D;
                        this.motionZ *= 0.6D;
                        if (block != Blocks.AIR && !this.world.isRemote) {
                           this.world.destroyBlock(new BlockPos(a, b, c), true);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void spawnGroundEffects() {
      for(int i = 0; (float)i < this.getRenderSize(); ++i) {
         for(int i1 = 0; i1 < 20; ++i1) {
            double motionX = this.getRNG().nextGaussian() * 0.07D;
            double motionY = this.getRNG().nextGaussian() * 0.07D;
            double motionZ = this.getRNG().nextGaussian() * 0.07D;
            float radius = 0.75F * (0.7F * this.getRenderSize() / 3.0F) * -3.0F;
            float angle = 0.017453292F * this.renderYawOffset + (float)i1 * 1.0F;
            double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
            double extraY = 0.800000011920929D;
            double extraZ = (double)(radius * MathHelper.cos(angle));
            IBlockState iblockstate = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX + extraX), MathHelper.floor(this.posY + extraY) - 1, MathHelper.floor(this.posZ + extraZ)));
            if (iblockstate.getMaterial() != Material.AIR && this.world.isRemote) {
               this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, true, this.posX + extraX, this.posY + extraY, this.posZ + extraZ, motionX, motionY, motionZ, new int[]{Block.getStateId(iblockstate)});
            }
         }
      }

   }

   public void fall(float distance, float damageMultiplier) {
   }

   public boolean isActuallyBreathingFire() {
      return this.fireTicks > 20 && this.isBreathingFire();
   }

   public boolean doesWantToLand() {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      return this.flyTicks > 6000 || this.down() || this.flyTicks > 40 && this.flyProgress == 0.0F || properties != null && properties.isStone;
   }

   public abstract String getVariantName(int var1);

   public void updatePassenger(Entity passenger) {
      super.updatePassenger(passenger);
      if (this.isPassenger(passenger)) {
         if (this.getControllingPassenger() != null && this.getControllingPassenger().getEntityId() == passenger.getEntityId()) {
            if (this.isModelDead()) {
               passenger.dismountRidingEntity();
            }

            float speed_walk = 0.2F;
            float speed_idle = 0.05F;
            float speed_fly = 0.2F;
            float degree_walk = 0.5F;
            float degree_idle = 0.5F;
            float degree_fly = 0.5F;
            this.renderYawOffset = this.rotationYaw;
            this.rotationYaw = passenger.rotationYaw;
            float hoverAddition = this.hoverProgress * -0.001F;
            float flyAddition = this.flyProgress * -1.0E-4F;
            float flyBody = Math.max(this.flyProgress, this.hoverProgress) * 0.0065F;
            float radius = 0.75F * (0.3F - flyBody) * this.getRenderSize() + this.getRenderSize() / 3.0F * flyAddition * 0.0065F;
            float angle = 0.017453292F * this.renderYawOffset;
            double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
            double extraZ = (double)(radius * MathHelper.cos(angle));
            float bob0 = !this.isFlying() && !this.isHovering() ? 0.0F : (this.hoverProgress <= 0.0F && this.flyProgress <= 0.0F ? 0.0F : this.bob(-speed_fly, degree_fly * 5.0F, false, (float)this.ticksExisted, -0.0625F));
            float bob1 = this.bob(speed_walk * 2.0F, degree_walk * 1.7F, false, this.limbSwing, this.limbSwingAmount * -0.0625F);
            float bob2 = this.bob(speed_idle, degree_idle * 1.3F, false, (float)this.ticksExisted, -0.0625F);
            double extraY_pre = 0.800000011920929D;
            double extraY = (extraY_pre - (double)hoverAddition + (double)flyAddition) * (double)(this.getRenderSize() / 3.0F) - 0.35D * (double)(1.0F - this.getRenderSize() / 30.0F) + (double)bob0 + (double)bob1 + (double)bob2;
            passenger.setPosition(this.posX + extraX, this.posY + extraY, this.posZ + extraZ);
            this.stepHeight = 1.0F;
         } else {
            this.updatePreyInMouth(passenger);
         }
      }

   }

   private float bob(float speed, float degree, boolean bounce, float f, float f1) {
      float bob = (float)(Math.sin((double)(f * speed)) * (double)f1 * (double)degree - (double)(f1 * degree));
      if (bounce) {
         bob = (float)(-Math.abs(Math.sin((double)(f * speed)) * (double)f1 * (double)degree));
      }

      return bob * this.getRenderSize() / 3.0F;
   }

   private void updatePreyInMouth(Entity prey) {
      this.setAnimation(ANIMATION_SHAKEPREY);
      if (this.getAnimation() == ANIMATION_SHAKEPREY && this.getAnimationTick() > 55 && prey != null) {
         prey.attackEntityFrom(DamageSource.causeMobDamage(this), prey instanceof EntityPlayer ? 17.0F : (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 4.0F);
         prey.dismountRidingEntity();
      }

      this.renderYawOffset = this.rotationYaw;
      float modTick_0 = (float)(this.getAnimationTick() - 25);
      float modTick_1 = this.getAnimationTick() > 25 && this.getAnimationTick() < 55 ? 8.0F * MathHelper.clamp(MathHelper.sin((float)(3.141592653589793D + (double)modTick_0 * 0.25D)), -0.8F, 0.8F) : 0.0F;
      float modTick_2 = this.getAnimationTick() > 30 ? 10.0F : (float)Math.max(0, this.getAnimationTick() - 20);
      float radius = 0.75F * (0.6F * this.getRenderSize() / 3.0F) * -3.0F;
      float angle = 0.017453292F * this.renderYawOffset + 3.15F + modTick_1 * 2.0F * 0.015F;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      double extraY = modTick_2 == 0.0F ? 0.0D : 0.03500000014901161D * ((double)(this.getRenderSize() / 3.0F) + (double)modTick_2 * 0.5D * (double)(this.getRenderSize() / 3.0F));
      prey.setPosition(this.posX + extraX, this.posY + extraY, this.posZ + extraZ);
   }

   public int getDragonStage() {
      int age = this.getAgeInDays();
      if (age >= 100) {
         return 5;
      } else if (age >= 75) {
         return 4;
      } else if (age >= 50) {
         return 3;
      } else {
         return age >= 25 ? 2 : 1;
      }
   }

   public boolean isTeen() {
      return this.getDragonStage() < 4 && this.getDragonStage() > 2;
   }

   public boolean isAdult() {
      return this.getDragonStage() >= 4;
   }

   public boolean isChild() {
      return this.getDragonStage() < 2;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setGender(this.getRNG().nextBoolean());
      int age = this.getRNG().nextInt(80) + 1;
      this.growDragon(age);
      this.setVariant((new EaglercraftRandom()).nextInt(4));
      this.setSleeping(false);
      this.updateAttributes();
      double healthStep = (this.maximumHealth - this.minimumHealth) / 125.0D;
      this.heal((float)Math.round(this.minimumHealth + healthStep * (double)age));
      this.attackDecision = true;
      this.setHunger(50);
      return livingdata;
   }

   public boolean attackEntityFrom(DamageSource dmg, float i) {
      if (this.isModelDead()) {
         return false;
      } else if (this.isBeingRidden() && dmg.getTrueSource() != null && this.getControllingPassenger() != null && dmg.getTrueSource() == this.getControllingPassenger()) {
         return false;
      } else if ((dmg.damageType.contains("arrow") || this.getRidingEntity() != null && dmg.getTrueSource() != null && dmg.getTrueSource().isEntityEqual(this.getRidingEntity())) && this.isRiding()) {
         return false;
      } else if (dmg != DamageSource.IN_WALL && dmg != DamageSource.FALLING_BLOCK) {
         if (!this.world.isRemote && dmg.getTrueSource() != null && this.getRNG().nextInt(4) == 0) {
            this.roar();
         }

         return super.attackEntityFrom(dmg, i);
      } else {
         return false;
      }
   }

   public void onUpdate() {
      super.onUpdate();
      this.updateParts();
      this.setScaleForAge(true);
      if (this.world.isRemote) {
         this.updateClientControls();
      }

      if (!this.isModelDead()) {
         if (this.up()) {
            if (!this.isFlying() && !this.isHovering()) {
               this.spacebarTicks += 2;
            }

            if (this.isFlying() || this.isHovering()) {
               this.motionY += 0.4D;
            }
         } else if (this.dismount() && (this.isFlying() || this.isHovering())) {
            this.motionY -= 0.4D;
            this.setFlying(false);
            this.setHovering(false);
         }

         if (this.down() && (this.isFlying() || this.isHovering())) {
            this.motionY -= 0.4D;
         }

         if (!this.dismount() && (this.isFlying() || this.isHovering())) {
            this.motionY += 0.01D;
         }

         if (this.attack() && this.getControllingPassenger() != null && this.getDragonStage() > 1) {
            this.setBreathingFire(true);
            this.riderShootFire(this.getControllingPassenger());
            this.fireStopTicks = 10;
         }

         if (this.strike() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer) {
            EntityLivingBase target = DragonUtils.riderLookingAtEntity(this, (EntityPlayer)this.getControllingPassenger(), (double)this.getDragonStage() + (this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX));
            if (this.getAnimation() != ANIMATION_BITE) {
               this.setAnimation(ANIMATION_BITE);
            }

            if (target != null && !DragonUtils.hasSameOwner(this, target)) {
               target.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            }
         }

         if (this.getControllingPassenger() != null && this.getControllingPassenger().isSneaking()) {
            MiscPlayerProperties properties = (MiscPlayerProperties)EntityPropertiesHandler.INSTANCE.getProperties(this.getControllingPassenger(), MiscPlayerProperties.class);
            if (properties != null) {
               properties.hasDismountedDragon = true;
            }

            this.getControllingPassenger().dismountRidingEntity();
         }

         if (this.isFlying() && !this.isHovering() && this.getControllingPassenger() != null && !this.onGround && Math.max(Math.abs(this.motionZ), Math.abs(this.motionX)) < 0.10000000149011612D) {
            this.setHovering(true);
            this.setFlying(false);
         }

         if ((this.isFlying() || this.isHovering()) && this.isInWater()) {
            ;
         }

         if (this.isHovering() && !this.isFlying() && this.getControllingPassenger() != null && !this.onGround && Math.max(Math.abs(this.motionZ), Math.abs(this.motionX)) > 0.10000000149011612D) {
            this.setFlying(true);
            this.setHovering(false);
         }

         if (this.spacebarTicks > 0) {
            --this.spacebarTicks;
         }

         if (this.spacebarTicks > 20 && this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && !this.isFlying() && !this.isHovering()) {
            this.setHovering(true);
         }

         if (this.world.isRemote && !this.isModelDead()) {
            this.roll_buffer.calculateChainFlapBuffer(50.0F, 10, 4.0F, this);
            this.turn_buffer.calculateChainSwingBuffer(50.0F, 0, 4.0F, this);
            this.tail_buffer.calculateChainSwingBuffer(90.0F, 10, 2.5F, this);
         }

         if (this.getAttackTarget() != null && this.getRidingEntity() == null && this.getAttackTarget().isDead || this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityDragonBase && ((EntityDragonBase)this.getAttackTarget()).isDead) {
            this.setAttackTarget((EntityLivingBase)null);
         }

         if (!this.world.isRemote && !this.isInWater() && !this.isSleeping() && this.onGround && !this.isFlying() && !this.isHovering() && this.getAttackTarget() == null && !this.isDaytime() && this.getRNG().nextInt(250) == 0 && this.getAttackTarget() == null && this.getPassengers().isEmpty()) {
            this.setSleeping(true);
         }

         if (!this.world.isRemote && this.isSleeping() && (this.isFlying() || this.isHovering() || this.isInWater() || this.world.canBlockSeeSky(new BlockPos(this)) && this.isDaytime() && !this.isTamed() || this.isDaytime() && this.isTamed() || this.getAttackTarget() != null || !this.getPassengers().isEmpty())) {
            this.setSleeping(false);
         }

         if (this.isSitting() && this.getControllingPassenger() != null) {
            this.setSitting(false);
         }

      }
   }

   public void setScaleForAge(boolean par1) {
      float scale = Math.min(this.getRenderSize() * 0.35F, 7.0F);
      this.setScale(scale);
      if (scale != this.lastScale) {
         this.resetParts(this.getRenderSize() / 3.0F);
      }

      this.lastScale = scale;
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   public float getRenderSize() {
      float step = (this.growth_stages[this.getDragonStage() - 1][1] - this.growth_stages[this.getDragonStage() - 1][0]) / 25.0F;
      return this.getAgeInDays() > 125 ? this.growth_stages[this.getDragonStage() - 1][0] + step * 25.0F : this.growth_stages[this.getDragonStage() - 1][0] + step * (float)this.getAgeFactor();
   }

   private int getAgeFactor() {
      return this.getDragonStage() > 1 ? this.getAgeInDays() - 25 * (this.getDragonStage() - 1) : this.getAgeInDays();
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.isTackling()) {
         return false;
      } else if (this.isModelDead()) {
         return false;
      } else {
         boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         if (flag) {
            this.applyEnchantments(this, entityIn);
         }

         return flag;
      }
   }

   public void updateRidden() {
      Entity entity = this.getRidingEntity();
      if (this.isRiding() && entity.isDead) {
         this.dismountRidingEntity();
      } else {
         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
         this.onUpdate();
         if (this.isRiding()) {
            this.updateRiding(entity);
         }
      }

   }

   public void updateRiding(Entity riding) {
      if (riding != null && riding.isPassenger(this) && riding instanceof EntityPlayer) {
         int i = riding.getPassengers().indexOf(this);
         float radius = (i == 2 ? 0.0F : 0.4F) + (float)(((EntityPlayer)riding).isElytraFlying() ? 2 : 0);
         float angle = 0.017453292F * ((EntityPlayer)riding).renderYawOffset + (float)(i == 1 ? -90 : (i == 0 ? 90 : 0));
         double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
         double extraZ = (double)(radius * MathHelper.cos(angle));
         double extraY = (riding.isSneaking() ? 1.2D : 1.4D) + (i == 2 ? 0.4D : 0.0D);
         this.rotationYaw = ((EntityPlayer)riding).rotationYawHead;
         this.rotationYawHead = ((EntityPlayer)riding).rotationYawHead;
         this.prevRotationYaw = ((EntityPlayer)riding).rotationYawHead;
         this.setPosition(riding.posX + extraX, riding.posY + extraY, riding.posZ + extraZ);
         if (this.getControlState() == 16 || ((EntityPlayer)riding).isElytraFlying()) {
            this.dismountRidingEntity();
         }
      }

   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation getAnimation() {
      return this.isModelDead() ? NO_ANIMATION : this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      if (!this.isModelDead()) {
         this.currentAnimation = animation;
      }
   }

   public void playLivingSound() {
      if (!this.isSleeping() && !this.isModelDead() && !this.world.isRemote) {
         if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
         }

         super.playLivingSound();
      }

   }

   protected void playHurtSound(DamageSource source) {
      if (!this.isModelDead()) {
         if (this.getAnimation() == NO_ANIMATION && !this.world.isRemote) {
            this.setAnimation(ANIMATION_SPEAK);
         }

         super.playHurtSound(source);
      }

   }

   public Animation[] getAnimations() {
      return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_EAT};
   }

   public EntityAgeable createChild(EntityAgeable ageable) {
      return null;
   }

   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal instanceof EntityDragonBase && otherAnimal != this && otherAnimal.getClass() == this.getClass()) {
         EntityDragonBase dragon = (EntityDragonBase)otherAnimal;
         if (this.isMale() && !dragon.isMale() || !this.isMale() && dragon.isMale()) {
            return true;
         }
      }

      return false;
   }

   public EntityDragonEgg createEgg(EntityDragonBase ageable) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY);
      int k = MathHelper.floor(this.posZ);
      BlockPos pos = new BlockPos(i, j, k);
      EntityDragonEgg dragon = new EntityDragonEgg(this.world);
      dragon.setType(EnumDragonEgg.byMetadata((new EaglercraftRandom()).nextInt(3) + (this.isFire ? 0 : 4)));
      dragon.setPosition((double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D);
      return dragon;
   }

   public void flyAround() {
      if (this.airTarget != null) {
         if (!this.isTargetInAir() || this.flyTicks > 6000 || !this.isFlying()) {
            this.airTarget = null;
         }

         this.flyTowardsTarget();
      }

   }

   public boolean isTargetBlocked(Vec3d target) {
      if (target != null) {
         RayTraceResult rayTrace = this.world.rayTraceBlocks(new Vec3d(this.getPosition()), target, false);
         if (rayTrace != null && rayTrace.hitVec != null) {
            BlockPos pos = new BlockPos(rayTrace.hitVec);
            if (this.world.isAirBlock(pos) && (this.world.getBlockState(pos).getMaterial() != Material.WATER || this.isFire)) {
               return rayTrace != null && rayTrace.typeOfHit != Type.BLOCK;
            }

            return true;
         }
      }

      return false;
   }

   public void flyTowardsTarget() {
      if (this.airTarget != null && this.airTarget.getY() > IceAndFire.CONFIG.maxDragonFlight) {
         this.airTarget = new BlockPos(this.airTarget.getX(), IceAndFire.CONFIG.maxDragonFlight, this.airTarget.getZ());
      }

      if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d((double)this.airTarget.getX(), this.posY, (double)this.airTarget.getZ())) > 3.0F) {
         double y = this.attackDecision ? (double)this.airTarget.getY() : this.posY;
         double targetX = (double)this.airTarget.getX() + 0.5D - this.posX;
         double targetY = Math.min(y, 256.0D) + 1.0D - this.posY;
         double targetZ = (double)this.airTarget.getZ() + 0.5D - this.posZ;
         this.motionX += (Math.signum(targetX) * 0.5D - this.motionX) * 0.100000000372529D * this.getFlySpeed();
         this.motionY += (Math.signum(targetY) * 0.5D - this.motionY) * 0.100000000372529D * this.getFlySpeed();
         this.motionZ += (Math.signum(targetZ) * 0.5D - this.motionZ) * 0.100000000372529D * this.getFlySpeed();
         float angle = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0D / 3.141592653589793D) - 90.0F;
         this.moveForward = 0.5F;
         double d0 = (double)this.airTarget.getX() + 0.5D - this.posX;
         double d2 = (double)this.airTarget.getZ() + 0.5D - this.posZ;
         double d1 = y + 0.5D - this.posY;
         double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
         float f = (float)(MathHelper.atan2(d2, d0) * 57.29577951308232D) - 90.0F;
         float f1 = (float)(-(MathHelper.atan2(d1, d3) * 57.29577951308232D));
         this.rotationPitch = this.updateRotation(this.rotationPitch, f1, 30.0F);
         this.rotationYaw = this.updateRotation(this.rotationYaw, f, 30.0F);
         if (!this.isFlying()) {
            this.setFlying(true);
         }
      } else {
         this.airTarget = null;
      }

      if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d((double)this.airTarget.getX(), this.posY, (double)this.airTarget.getZ())) < 3.0F && this.doesWantToLand()) {
         this.setFlying(false);
         this.setHovering(true);
         this.flyHovering = 1;
      }

   }

   private double getFlySpeed() {
      return (double)((2 + this.getAgeInDays() / 125 * 2) * (this.isTackling() ? 2 : 1));
   }

   private boolean isTackling() {
      if (this.world.isRemote) {
         boolean tackling = ((Boolean)this.dataManager.get(TACKLE)).booleanValue();
         this.isTackling = tackling;
         return tackling;
      } else {
         return this.isTackling;
      }
   }

   private boolean isAgingDisabled() {
      return ((Boolean)this.dataManager.get(AGINGDISABLED)).booleanValue();
   }

   protected boolean isTargetInAir() {
      return this.airTarget != null && (this.world.getBlockState(this.airTarget).getMaterial() == Material.AIR || this instanceof EntityIceDragon && (this.world.getBlockState(this.airTarget).getMaterial() == Material.WATER || this.world.getBlockState(this.airTarget).getMaterial() == Material.AIR));
   }

   private float updateRotation(float angle, float targetAngle, float maxIncrease) {
      float f = MathHelper.wrapDegrees(targetAngle - angle);
      if (f > maxIncrease) {
         f = maxIncrease;
      }

      if (f < -maxIncrease) {
         f = -maxIncrease;
      }

      return angle + f;
   }

   public float getDistanceSquared(Vec3d vec3d) {
      float f = (float)(this.posX - vec3d.x);
      float f1 = (float)(this.posY - vec3d.y);
      float f2 = (float)(this.posZ - vec3d.z);
      return f * f + f1 * f1 + f2 * f2;
   }

   public boolean replaceItemInInventory(int inventorySlot, @Nullable ItemStack itemStackIn) {
      int j = inventorySlot - 500 + 2;
      if (j >= 0 && j < this.dragonInv.getSizeInventory()) {
         this.dragonInv.setInventorySlotContents(j, itemStackIn);
         return true;
      } else {
         return false;
      }
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.itemHandler : super.getCapability(capability, facing));
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   public abstract Item getVariantScale(int var1);

   public abstract Item getVariantEgg(int var1);

   @SideOnly(Side.CLIENT)
   protected void updateClientControls() {
      Minecraft mc = Minecraft.getMinecraft();
      if (this.isRidingPlayer(mc.player)) {
         byte previousState = this.getControlState();
         this.up(mc.gameSettings.keyBindJump.isKeyDown());
         this.down(ModKeys.dragon_down.isKeyDown());
         this.attack(ModKeys.dragon_fireAttack.isKeyDown());
         this.strike(ModKeys.dragon_strike.isKeyDown());
         this.dismount(mc.gameSettings.keyBindSneak.isKeyDown());
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

   public boolean canBeSteered() {
      return true;
   }

   public void travel(float strafe, float forward, float vertical) {
      if (this.getAnimation() != ANIMATION_SHAKEPREY && (this.canMove() || this.isBeingRidden())) {
         if (this.isBeingRidden() && this.canBeSteered()) {
            EntityLivingBase controller = (EntityLivingBase)this.getControllingPassenger();
            if (controller != null && controller != this.getAttackTarget()) {
               this.rotationYaw = controller.rotationYaw;
               this.prevRotationYaw = this.rotationYaw;
               this.rotationPitch = controller.rotationPitch * 0.5F;
               this.setRotation(this.rotationYaw, this.rotationPitch);
               this.renderYawOffset = this.rotationYaw;
               this.rotationYawHead = this.renderYawOffset;
               strafe = controller.moveStrafing * 0.5F;
               forward = controller.moveForward;
               if (forward <= 0.0F) {
                  forward *= 0.25F;
               }

               if (this.isFlying() || this.isHovering()) {
                  this.motionX *= 1.06D;
                  this.motionZ *= 1.06D;
               }

               this.jumpMovementFactor = 0.05F;
               this.setAIMoveSpeed(this.onGround ? (float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() : (float)this.getFlySpeed());
               vertical = 0.0F;
               super.travel(strafe, 0.0F, forward);
               return;
            }
         }

         super.travel(strafe, forward, vertical);
      } else {
         strafe = 0.0F;
         forward = 0.0F;
         vertical = 0.0F;
         super.travel(strafe, forward, vertical);
      }
   }

   public void updateCheckPlayer() {
      double checklength = this.getEntityBoundingBox().getAverageEdgeLength() * 3.0D;
      EntityPlayer player = this.world.getClosestPlayerToEntity(this, checklength);
      if (!this.isTamed() && this.isSleeping() && player != null && !this.isOwner(player) && !player.capabilities.isCreativeMode) {
         this.setSleeping(false);
         this.setSitting(false);
         this.setAttackTarget(player);
      }

      this.world.getClosestPlayerToEntity(this, (double)(this.getRenderSize() / 2.0F + 15.0F));
   }

   public boolean shouldDismountInWater(Entity rider) {
      return false;
   }

   public boolean isDirectPathBetweenPoints(Vec3d vec1, Vec3d vec2) {
      RayTraceResult movingobjectposition = this.world.rayTraceBlocks(vec1, new Vec3d(vec2.x, vec2.y + (double)this.height * 0.5D, vec2.z), false, true, false);
      return movingobjectposition == null || movingobjectposition.typeOfHit != Type.BLOCK;
   }

   public void onDeath(DamageSource cause) {
      if (cause.getTrueSource() != null) {
         ;
      }

      super.onDeath(cause);
      if (this.dragonInv != null && !this.world.isRemote) {
         for(int i = 0; i < this.dragonInv.getSizeInventory(); ++i) {
            ItemStack itemstack = this.dragonInv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               this.entityDropItem(itemstack, 0.0F);
            }
         }
      }

   }

   public void onHearFlute(EntityPlayer player) {
      if (this.isTamed() && this.isOwner(player) && (this.isFlying() || this.isHovering())) {
         this.setFlying(false);
         this.setHovering(false);
      }

   }

   public abstract SoundEvent getRoarSound();

   public void roar() {
      if (!EntityGorgon.isStoneMob(this)) {
         if (this.getAnimation() != ANIMATION_ROAR) {
            this.setAnimation(ANIMATION_ROAR);
            this.playSound(this.getRoarSound(), this.getSoundVolume() + 2.0F + (float)Math.max(0, this.getDragonStage() - 3), this.getSoundPitch());
         }

         if (this.getDragonStage() > 3) {
            int size = (this.getDragonStage() - 3) * 30;

            for(Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand((double)size, (double)size, (double)size))) {
               boolean isStrongerDragon = entity instanceof EntityDragonBase && ((EntityDragonBase)entity).getDragonStage() >= this.getDragonStage();
               if (entity instanceof EntityLivingBase && !isStrongerDragon) {
                  EntityLivingBase living = (EntityLivingBase)entity;
                  if (!this.isOwner(living) && !this.isOwnersPet(living)) {
                     living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 30 * size));
                  } else {
                     living.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 30 * size));
                  }
               }
            }
         }

      }
   }

   private boolean isOwnersPet(EntityLivingBase living) {
      return this.isTamed() && this.getOwner() != null && living instanceof EntityTameable && ((EntityTameable)living).getOwner() != null && this.getOwner().isEntityEqual(((EntityTameable)living).getOwner());
   }

   public boolean isDirectPathBetweenPoints(Entity entity, Vec3d vec1, Vec3d vec2) {
      RayTraceResult movingobjectposition = entity.world.rayTraceBlocks(vec1, new Vec3d(vec2.x, vec2.y + (double)entity.height * 0.5D, vec2.z), false, true, false);
      return movingobjectposition == null || movingobjectposition.typeOfHit != Type.BLOCK;
   }

   public void processArrows() {
      for(Entity entity : this.world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox())) {
         if (entity instanceof EntityArrow) {
            ;
         }
      }

   }

   public boolean shouldRenderEyes() {
      return !this.isSleeping() && !this.isModelDead() && !this.isBlinking();
   }

   public boolean shouldAnimalsFear(Entity entity) {
      return DragonUtils.canTameDragonAttack(this, entity);
   }

   public void dropArmor() {
      if (this.dragonInv != null && !this.world.isRemote) {
         for(int i = 0; i < this.dragonInv.getSizeInventory(); ++i) {
            ItemStack itemstack = this.dragonInv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               this.entityDropItem(itemstack, 0.0F);
            }
         }
      }

   }
}
