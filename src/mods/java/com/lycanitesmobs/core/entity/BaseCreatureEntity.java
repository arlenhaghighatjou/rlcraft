package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.LycanitesClasses;
import net.minecraft.entity.EntityList;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.api.IGroupIce;
import com.lycanitesmobs.api.Targeting;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.container.CreatureContainer;
import com.lycanitesmobs.core.entity.damagesources.MinionEntityDamageSource;
import com.lycanitesmobs.core.entity.goals.actions.AvoidGoal;
import com.lycanitesmobs.core.entity.goals.actions.FollowFuseGoal;
import com.lycanitesmobs.core.entity.goals.actions.FollowMasterGoal;
import com.lycanitesmobs.core.entity.goals.actions.LookIdleGoal;
import com.lycanitesmobs.core.entity.goals.actions.MoveRestrictionGoal;
import com.lycanitesmobs.core.entity.goals.actions.PaddleGoal;
import com.lycanitesmobs.core.entity.goals.actions.StayByWaterGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import com.lycanitesmobs.core.entity.goals.actions.WatchClosestGoal;
import com.lycanitesmobs.core.entity.goals.targeting.AvoidIfHitGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindFuseTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindGroupAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindGroupAvoidTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal;
import com.lycanitesmobs.core.entity.navigate.CreatureMoveHelper;
import com.lycanitesmobs.core.entity.navigate.CreaturePathNavigate;
import com.lycanitesmobs.core.entity.navigate.DirectNavigator;
import com.lycanitesmobs.core.entity.navigate.ICreatureNodeProcessor;
import com.lycanitesmobs.core.info.CreatureGroup;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ExtraMobBehaviour;
import com.lycanitesmobs.core.info.ItemDrop;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.info.Variant;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.inventory.InventoryCreature;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.network.MessageCreature;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BaseCreatureEntity extends EntityLiving {
   public static Boolean ENABLE_HITAREAS = false;
   public static final IAttribute DEFENSE = (new RangedAttribute((IAttribute)null, "generic.defense", 4.0D, 0.0D, 1024.0D)).setShouldWatch(true);
   public static final IAttribute RANGED_SPEED = (new RangedAttribute((IAttribute)null, "generic.rangedSpeed", 4.0D, 0.0D, 1024.0D)).setShouldWatch(true);
   public CreatureInfo creatureInfo;
   public CreatureStats creatureStats;
   public Subspecies subspecies = null;
   public Variant variant = null;
   public EnumCreatureAttribute attribute = EnumCreatureAttribute.UNDEAD;
   public CreatureRelationships relationships;
   public ExtraMobBehaviour extraMobBehaviour;
   public long updateTick = 0L;
   protected boolean syncQueued = true;
   public String spawnEventType = "";
   public int spawnEventCount = -1;
   public PetEntry petEntry;
   public boolean altarSummoned = false;
   public int spawnedWithBlockProtection = 0;
   public boolean forceBossHealthBar = false;
   public TileEntitySummoningPedestal summoningPedestal;
   public float setWidth;
   public float setDepth;
   public float setHeight;
   public double sizeScale = 1.0D;
   public EntityHitArea[][][] hitAreas;
   public float hitAreaWidthScale = 1.0F;
   public float hitAreaHeightScale = 1.0F;
   public int attackTime = 20;
   protected int level = 1;
   protected int experience = 0;
   public byte attackPhase = 0;
   public byte attackPhaseMax = 0;
   public int fleeTime = 200;
   public int currentFleeTime = 0;
   public float fleeHealthPercent = 0.0F;
   public int battlePhase = 0;
   public int damageMax = 0;
   public float damageLimit = 0.0F;
   protected int growingAge;
   public static int BOSS_DAMAGE_LIMIT = 50;
   public float damageTakenThisSec = 0.0F;
   public float healthLastTick = -1.0F;
   public int bossRange = 60;
   public boolean isAggressiveByDefault = true;
   public boolean spreadFire = false;
   public boolean stealthPrev = false;
   public int currentBlockingTime = 0;
   public int blockingTime = 60;
   public EntityLivingBase pickupEntity;
   public boolean solidCollision = false;
   public boolean trueSight = false;
   protected BlockPos homePosition = new BlockPos(0, 0, 0);
   protected float homeDistanceMax = -1.0F;
   protected BlockPos arenaCenter = null;
   public int nextPriorityGoalIndex;
   public int nextDistractionGoalIndex;
   public int nextCombatGoalIndex;
   public int nextTravelGoalIndex;
   public int nextIdleGoalIndex;
   public int nextReactTargetIndex;
   public int nextSpecialTargetIndex;
   public int nextFindTargetIndex;
   public boolean firstSpawn = true;
   public boolean needsInitialLevel = true;
   public boolean spawnsInBlock = false;
   public boolean spawnsUnderground = true;
   public boolean spawnsOnLand = true;
   public boolean spawnsInWater = false;
   public boolean isLavaCreature = false;
   public boolean isMinion = false;
   public boolean isTemporary = false;
   public int temporaryDuration = 0;
   public boolean forceNoDespawn = false;
   public boolean spawnedRare = false;
   public boolean spawnedAsBoss = false;
   private boolean leashAIActive = false;
   private EntityAIBase leashMoveTowardsRestrictionAI = new MoveRestrictionGoal(this);
   public DirectNavigator directNavigator;
   protected List<Class<? extends Entity>> hostileTargetClasses = new ArrayList();
   private EntityLivingBase masterTarget;
   private EntityLivingBase parentTarget;
   private EntityLivingBase avoidTarget;
   private EntityLivingBase fixateTarget;
   private EaglercraftUUID fixateUUID = null;
   private EntityLivingBase perchTarget;
   public List<EntityPlayer> playerTargets = new ArrayList();
   public List<EntityLivingBase> minions = new ArrayList();
   public List<EntityPlayer> guiViewers = new ArrayList();
   public int guiRefreshTick = 0;
   public int guiRefreshTime = 2;
   protected int attackCooldownMax = 5;
   private int attackCooldown = 0;
   public boolean hasAttackSound = false;
   public boolean hasStepSound = true;
   public boolean hasJumpSound = false;
   public int flySoundSpeed = 0;
   public boolean extraAnimation01 = false;
   public BossInfoServer bossInfo;
   public float onlyRenderTicks = -1.0F;
   public boolean initialized = false;
   protected static final DataParameter<Byte> TARGET = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Byte> ATTACK_PHASE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Byte> ANIMATION_STATE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Integer> ANIMATION_ATTACK_COOLDOWN_MAX = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.VARINT);
   protected static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Float> STEALTH = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.FLOAT);
   protected static final DataParameter<Boolean> BABY = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BOOLEAN);
   protected static final DataParameter<Byte> COLOR = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Float> SIZE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.FLOAT);
   protected static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.VARINT);
   protected static final DataParameter<Integer> EXPERIENCE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.VARINT);
   protected static final DataParameter<Byte> SUBSPECIES = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Byte> VARIANT = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Optional<BlockPos>> ARENA = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
   public static final DataParameter<ItemStack> EQUIPMENT_HEAD = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.ITEM_STACK);
   public static final DataParameter<ItemStack> EQUIPMENT_CHEST = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.ITEM_STACK);
   public static final DataParameter<ItemStack> EQUIPMENT_LEGS = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.ITEM_STACK);
   public static final DataParameter<ItemStack> EQUIPMENT_FEET = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.ITEM_STACK);
   public static final DataParameter<ItemStack> EQUIPMENT_BAG = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.ITEM_STACK);
   public static final DataParameter<ItemStack> EQUIPMENT_SADDLE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.ITEM_STACK);
   public InventoryCreature inventory;
   public List<ItemDrop> drops = new ArrayList();
   public List<ItemDrop> savedDrops = new ArrayList();
   protected boolean dropsRequirePlayerDamage = false;
   public FindAttackTargetGoal aiTargetPlayer = null;
   public RevengeGoal aiDefendAnimals = null;

   public BaseCreatureEntity(World world) {
      super(world);
      this.setWidth = (float)this.creatureInfo.width;
      this.setDepth = (float)this.creatureInfo.width;
      this.setHeight = (float)this.creatureInfo.height;
      this.width = this.setWidth;
      this.height = this.setHeight;
      this.moveHelper = this.createMoveHelper();
      if (!this.canBurn()) {
         this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
         this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
         if (this.canBreatheUnderwater()) {
            this.setPathPriority(PathNodeType.LAVA, 8.0F);
            if (!this.canBreatheAir() || this.isLavaCreature) {
               this.setPathPriority(PathNodeType.LAVA, 0.0F);
            }
         }
      }

      if (this.waterDamage()) {
         this.setPathPriority(PathNodeType.WATER, -1.0F);
      } else if (this.canBreatheUnderwater()) {
         this.setPathPriority(PathNodeType.WATER, 8.0F);
         if (!this.canBreatheAir()) {
            this.setPathPriority(PathNodeType.WATER, 0.0F);
         }
      }

      if (this.canWade() && this.getNavigator() instanceof PathNavigateGround) {
         PathNavigateGround groundNavigator = (PathNavigateGround)this.getNavigator();
         groundNavigator.setCanSwim(true);
      }

   }

   protected void applyEntityAttributes() {
      this.loadCreatureFlags();
      this.creatureStats = new CreatureStats(this);
      this.relationships = new CreatureRelationships(this);
      this.extraMobBehaviour = new ExtraMobBehaviour(this);
      this.directNavigator = new DirectNavigator(this);
      this.nextPriorityGoalIndex = 10;
      this.nextDistractionGoalIndex = 30;
      this.nextCombatGoalIndex = 50;
      this.nextTravelGoalIndex = 70;
      this.nextIdleGoalIndex = 90;
      this.nextReactTargetIndex = 10;
      this.nextSpecialTargetIndex = 30;
      this.nextFindTargetIndex = 50;
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(DEFENSE);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
      this.getAttributeMap().registerAttribute(RANGED_SPEED);
      this.applyDynamicAttributes();
   }

   public void applyDynamicAttributes() {
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.creatureStats.getHealth());
      this.getEntityAttribute(DEFENSE).setBaseValue(this.creatureStats.getDefense());
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(this.creatureStats.getArmor());
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.creatureStats.getSpeed());
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(this.creatureStats.getKnockbackResistance());
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(this.creatureStats.getSight());
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(this.creatureStats.getDamage());
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(this.creatureStats.getAttackSpeed());
      this.getEntityAttribute(RANGED_SPEED).setBaseValue(this.creatureStats.getRangedSpeed());
   }

   public void refreshAttributes() {
      this.applyDynamicAttributes();
      this.setHealth(this.getMaxHealth());
      this.refreshBossHealthName();
   }

   public void loadCreatureFlags() {
   }

   protected void initEntityAI() {
      if (this instanceof IFusable) {
         this.targetTasks.addTask(this.nextSpecialTargetIndex++, new FindFuseTargetGoal(this));
      }

      this.targetTasks.addTask(this.nextFindTargetIndex++, (new AvoidIfHitGoal(this)).setHelpCall(true));
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new RevengeGoal(this)).setHelpCall(true).setCheckSight(true));
      this.tasks.addTask(this.nextPriorityGoalIndex++, new PaddleGoal(this));
      this.tasks.addTask(this.nextPriorityGoalIndex++, new StayByWaterGoal(this));
      this.tasks.addTask(this.nextPriorityGoalIndex++, (new AvoidGoal(this)).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
      if (this.creatureInfo.isTameable()) {
         this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setTemptDistanceMin(4.0D));
      }

      if (this instanceof IFusable) {
         this.tasks.addTask(this.nextDistractionGoalIndex++, (new FollowFuseGoal(this)).setLostDistance(16.0D));
      }

      super.initEntityAI();
      this.targetTasks.addTask(this.nextFindTargetIndex++, new FindGroupAttackTargetGoal(this));
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindGroupAvoidTargetGoal(this)).setTameTargetting(false));
      this.tasks.addTask(this.nextTravelGoalIndex++, (new FollowMasterGoal(this)).setStrayDistance(12.0D));
      this.tasks.addTask(this.nextIdleGoalIndex++, new WanderGoal(this));
      this.tasks.addTask(this.nextIdleGoalIndex++, (new WatchClosestGoal(this)).setTargetClass(EntityPlayer.class));
      this.tasks.addTask(this.nextIdleGoalIndex++, new LookIdleGoal(this));
   }

   public void setupMob() {
      this.setWidth = (float)((double)this.setWidth * this.creatureInfo.hitboxScale);
      this.setHeight = (float)((double)this.setHeight * this.creatureInfo.hitboxScale);
      this.updateSize();
      this.stepHeight = 0.5F;
      this.experienceValue = this.creatureInfo.experience;
      this.inventory = new InventoryCreature(this.getName(), this);
      this.loadItemDrops();
      if (ItemEquipmentPart.MOB_PART_DROPS.containsKey(this.creatureInfo.getEntityId())) {
         for(ItemEquipmentPart itemEquipmentPart : (List<ItemEquipmentPart>)ItemEquipmentPart.MOB_PART_DROPS.get(this.creatureInfo.getEntityId())) {
            ItemDrop partDrop = (new ItemDrop(itemEquipmentPart.getRegistryName().toString(), 0, itemEquipmentPart.dropChance)).setMaxAmount(1);
            partDrop.bonusAmount = false;
            partDrop.amountMultiplier = false;
            this.drops.add(partDrop);
         }
      }

      this.isImmuneToFire = !this.canBurn();
      this.setAttackCooldownMax(this.attackCooldownMax);
   }

   public void loadItemDrops() {
      this.drops.addAll(this.creatureInfo.drops);
      this.drops.addAll(CreatureManager.getInstance().config.getGlobalDrops());
   }

   public void addSavedItemDrop(ItemDrop itemDrop) {
      this.drops.add(itemDrop);
      this.savedDrops.add(itemDrop);
   }

   protected void entityInit() {
      this.creatureInfo = CreatureManager.getInstance().getCreature(this.getClass());
      super.entityInit();
      this.dataManager.register(TARGET, Byte.valueOf((byte)0));
      this.dataManager.register(ATTACK_PHASE, Byte.valueOf((byte)0));
      this.dataManager.register(ANIMATION_STATE, Byte.valueOf((byte)0));
      this.dataManager.register(ANIMATION_ATTACK_COOLDOWN_MAX, Integer.valueOf(0));
      this.dataManager.register(CLIMBING, Byte.valueOf((byte)0));
      this.dataManager.register(STEALTH, Float.valueOf(0.0F));
      this.dataManager.register(COLOR, Byte.valueOf((byte)0));
      this.dataManager.register(SIZE, Float.valueOf(1.0F));
      this.dataManager.register(LEVEL, Integer.valueOf(1));
      this.dataManager.register(EXPERIENCE, Integer.valueOf(0));
      this.dataManager.register(SUBSPECIES, Byte.valueOf((byte)0));
      this.dataManager.register(VARIANT, Byte.valueOf((byte)0));
      this.dataManager.register(ARENA, Optional.absent());
      InventoryCreature.registerDataParameters(this.dataManager);
      this.initialized = true;
   }

   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : this.getFullName();
   }

   public String getFullName() {
      String nameFormatting = LanguageManager.translate("entity.lycanitesmobs.creature.name.format");
      String[] nameParts = nameFormatting.split("\\|");
      if (nameParts.length < 4 || nameFormatting.equals("entity.lycanitesmobs.creature.name.format")) {
         nameParts = new String[]{"age", "variant", "subspecies", "species", "level"};
      }

      StringBuilder name = new StringBuilder();
      List<String> nameComponents = new ArrayList();

      for(String namePart : nameParts) {
         byte var10 = -1;
         switch(namePart.hashCode()) {
         case -2008465092:
            if (namePart.equals("species")) {
               var10 = 3;
            }
            break;
         case -859768068:
            if (namePart.equals("subspecies")) {
               var10 = 2;
            }
            break;
         case 96511:
            if (namePart.equals("age")) {
               var10 = 0;
            }
            break;
         case 102865796:
            if (namePart.equals("level")) {
               var10 = 4;
            }
            break;
         case 236785797:
            if (namePart.equals("variant")) {
               var10 = 1;
            }
         }

         switch(var10) {
         case 0:
            nameComponents.add(this.getAgeName());
            break;
         case 1:
            nameComponents.add(this.getVariantName());
            break;
         case 2:
            nameComponents.add(this.getSubspeciesName());
            break;
         case 3:
            nameComponents.add(this.getSpeciesName());
            break;
         case 4:
            nameComponents.add(this.getLevelName());
         }
      }

      boolean first = true;

      for(String nameComponent : nameComponents) {
         if (!nameComponent.isEmpty()) {
            if (!first) {
               name.append(" ");
            }

            first = false;
            name.append(nameComponent);
         }
      }

      return name.toString();
   }

   public String getAgeName() {
      return "";
   }

   public String getSpeciesName() {
      return this.creatureInfo.getTitle();
   }

   public String getVariantName() {
      return this.getVariant() != null ? this.getVariant().getTitle() : "";
   }

   public String getSubspeciesName() {
      return this.getSubspecies() != null ? this.getSubspecies().getTitle() : "";
   }

   public String getLevelName() {
      return this.getLevel() < 2 ? "" : " " + LanguageManager.translate("entity.level") + " " + this.getLevel();
   }

   public void queueSync() {
      if (!this.getEntityWorld().isRemote) {
         this.syncQueued = true;
      }
   }

   public void doSync() {
      this.syncQueued = false;
      if (!this.getEntityWorld().isRemote) {
         for(EntityPlayer player : this.relationships.getPlayers()) {
            CreatureRelationshipEntry relationshipEntry = this.relationships.getEntry(player);
            if (relationshipEntry != null) {
               MessageCreature message = new MessageCreature(this, relationshipEntry.reputation);
               LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)player);
            }
         }

      }
   }

   public boolean getBoolFromDataManager(DataParameter<Boolean> key) {
      try {
         return ((Boolean)this.getDataManager().get(key)).booleanValue();
      } catch (Exception var3) {
         return false;
      }
   }

   public byte getByteFromDataManager(DataParameter<Byte> key) {
      try {
         return ((Byte)this.getDataManager().get(key)).byteValue();
      } catch (Exception var3) {
         return 0;
      }
   }

   public int getIntFromDataManager(DataParameter<Integer> key) {
      try {
         return ((Integer)this.getDataManager().get(key)).intValue();
      } catch (Exception var3) {
         return 0;
      }
   }

   public float getFloatFromDataManager(DataParameter<Float> key) {
      try {
         return ((Float)this.getDataManager().get(key)).floatValue();
      } catch (Exception var3) {
         return 0.0F;
      }
   }

   public String getStringFromDataManager(DataParameter<String> key) {
      try {
         return (String)this.getDataManager().get(key);
      } catch (Exception var3) {
         return null;
      }
   }

   public Optional<EaglercraftUUID> getUUIDFromDataManager(DataParameter<Optional<EaglercraftUUID>> key) {
      try {
         return (Optional)this.getDataManager().get(key);
      } catch (Exception var3) {
         return null;
      }
   }

   public ItemStack getItemStackFromDataManager(DataParameter<ItemStack> key) {
      try {
         return (ItemStack)this.getDataManager().get(key);
      } catch (Exception var3) {
         return ItemStack.EMPTY;
      }
   }

   public Optional<BlockPos> getBlockPosFromDataManager(DataParameter<Optional<BlockPos>> key) {
      try {
         return (Optional)this.getDataManager().get(key);
      } catch (Exception var3) {
         return Optional.absent();
      }
   }

   public boolean getCanSpawnHere() {
      return this.checkSpawnVanilla(this.getEntityWorld(), this.getPosition());
   }

   public int getMaxSpawnedInChunk() {
      return this.creatureInfo.creatureSpawn.spawnGroupMax;
   }

   public boolean isNotColliding() {
      if (!this.spawnsInWater) {
         return super.isNotColliding();
      } else {
         return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty();
      }
   }

   public boolean checkSpawnVanilla(World world, BlockPos pos) {
      if (world.isRemote) {
         return false;
      } else {
         LycanitesMobs.logDebug("MobSpawns", " ~O==================== Vanilla Spawn Check: " + this.creatureInfo.getName() + " ====================O~");
         LycanitesMobs.logDebug("MobSpawns", "Attempting to Spawn: " + this.creatureInfo.getName());
         LycanitesMobs.logDebug("MobSpawns", "Target Spawn Location: " + pos);
         LycanitesMobs.logDebug("MobSpawns", "Checking if creature is enabled...");
         if (this.creatureInfo.enabled && this.creatureInfo.creatureSpawn.enabled) {
            LycanitesMobs.logDebug("MobSpawns", "Checking for peaceful difficulty...");
            if (!this.creatureInfo.peaceful && this.getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL) {
               return false;
            } else {
               LycanitesMobs.logDebug("MobSpawns", "Checking gamerules...");
               if (!this.getEntityWorld().getGameRules().getBoolean("doMobSpawning")) {
                  return false;
               } else {
                  LycanitesMobs.logDebug("MobSpawns", "Fixed spawn check (light level, collisions)...");
                  if (!this.fixedSpawnCheck(world, pos)) {
                     return false;
                  } else {
                     LycanitesMobs.logDebug("MobSpawns", "Checking for nearby Mob Spawner...");
                     if (this.isSpawnerNearby(world, pos)) {
                        LycanitesMobs.logDebug("MobSpawns", "Mob Spawner found, skipping other checks.");
                        LycanitesMobs.logDebug("MobSpawns", "Vanilla Spawn Check Passed!");
                        return true;
                     } else {
                        LycanitesMobs.logDebug("MobSpawns", "No Mob Spawner found.");
                        LycanitesMobs.logDebug("MobSpawns", "Global Spawn Check (Master Dimension List, etc)...");
                        if (!CreatureManager.getInstance().spawnConfig.isAllowedGlobal(world)) {
                           return false;
                        } else {
                           LycanitesMobs.logDebug("MobSpawns", "Environment spawn check (dimension, group limit, ground type, water, lava, underground)...");
                           if (!this.environmentSpawnCheck(world, pos)) {
                              return false;
                           } else {
                              LycanitesMobs.logDebug("MobSpawns", "Vanilla Spawn Check Passed!");
                              return true;
                           }
                        }
                     }
                  }
               }
            }
         } else {
            return false;
         }
      }
   }

   public boolean fixedSpawnCheck(World world, BlockPos pos) {
      if (!this.checkSpawnLightLevel(world, pos)) {
         return false;
      } else {
         LycanitesMobs.logDebug("MobSpawns", "Checking collision...");
         if (!this.checkSpawnCollision(world, pos)) {
            return false;
         } else {
            LycanitesMobs.logDebug("MobSpawns", "Counting mobs of the same kind, max allowed is: " + this.creatureInfo.creatureSpawn.spawnAreaLimit);
            return this.checkSpawnGroupLimit(world, pos, CreatureManager.getInstance().spawnConfig.spawnLimitRange);
         }
      }
   }

   public boolean environmentSpawnCheck(World world, BlockPos pos) {
      if (this.creatureInfo.creatureSpawn.worldDayMin > 0.0D) {
         int currentDay = (int)Math.floor((double)world.getTotalWorldTime() / 24000.0D);
         LycanitesMobs.logDebug("MobSpawns", "Checking world age, currently on day: " + currentDay + ", must be at least day: " + this.creatureInfo.creatureSpawn.worldDayMin + ".");
         if ((double)currentDay < this.creatureInfo.creatureSpawn.worldDayMin) {
            return false;
         }
      }

      LycanitesMobs.logDebug("MobSpawns", "Checking dimension.");
      if (!this.isNativeDimension(this.getEntityWorld())) {
         return false;
      } else {
         LycanitesMobs.logDebug("MobSpawns", "Block preference.");
         if (this.getBlockPathWeight(pos.getX(), pos.getY(), pos.getZ()) < 0.0F) {
            return false;
         } else {
            LycanitesMobs.logDebug("MobSpawns", "Checking for liquid (water, lava, ooze, etc).");
            if (!this.spawnsInWater && this.getEntityWorld().containsAnyLiquid(this.getEntityBoundingBox())) {
               return false;
            } else if (!this.spawnsOnLand && !this.getEntityWorld().containsAnyLiquid(this.getEntityBoundingBox())) {
               return false;
            } else {
               LycanitesMobs.logDebug("MobSpawns", "Checking for underground.");
               if (!this.spawnsUnderground && this.isBlockUnderground(pos.getX(), pos.getY() + 1, pos.getZ())) {
                  return false;
               } else {
                  LycanitesMobs.logDebug("MobSpawns", "Checking for nearby bosses.");
                  return this.checkSpawnBoss(world, pos);
               }
            }
         }
      }
   }

   public boolean isNativeDimension(World world) {
      return this.creatureInfo.creatureSpawn.isAllowedDimension(world);
   }

   public boolean checkSpawnCollision(World world, BlockPos pos) {
      return this.spawnsInBlock || this.getEntityWorld().getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty();
   }

   public boolean checkSpawnLightLevel(World world, BlockPos pos) {
      if (this.creatureInfo.creatureSpawn.spawnsInDark && this.creatureInfo.creatureSpawn.spawnsInLight) {
         return true;
      } else if (!this.creatureInfo.creatureSpawn.spawnsInDark && !this.creatureInfo.creatureSpawn.spawnsInLight) {
         return false;
      } else {
         byte light = this.testLightLevel(pos);
         if (this.creatureInfo.creatureSpawn.spawnsInDark && light <= 1) {
            return true;
         } else {
            return this.creatureInfo.creatureSpawn.spawnsInLight && light >= 2;
         }
      }
   }

   public boolean checkSpawnGroupLimit(World world, BlockPos pos, double range) {
      if (range <= 0.0D) {
         return true;
      } else {
         int typesLimit = CreatureManager.getInstance().spawnConfig.typeSpawnLimit;
         int speciesLimit = this.creatureInfo.creatureSpawn.spawnAreaLimit;
         if (typesLimit <= 0 && speciesLimit <= 0) {
            return true;
         } else {
            List<BaseCreatureEntity> targets = this.<BaseCreatureEntity>getNearbyEntities(BaseCreatureEntity.class, (entity) -> {
               return BaseCreatureEntity.class.isAssignableFrom(entity.getClass());
            }, range);
            int typesFound = 0;
            int speciesFound = 0;

            for(BaseCreatureEntity targetCreature : targets) {
               if (targetCreature.creatureInfo.peaceful == this.creatureInfo.peaceful) {
                  ++typesFound;
               }

               if (this.creatureInfo.entityClass.isAssignableFrom(targetCreature.getClass())) {
                  ++speciesFound;
               }
            }

            if (range > 0.0D) {
               if (typesLimit > 0 && typesFound >= typesLimit) {
                  return false;
               }

               if (speciesLimit > 0 && speciesFound >= speciesLimit) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public boolean checkSpawnBoss(World world, BlockPos pos) {
      List bosses = this.getNearbyEntities(BaseCreatureEntity.class, (entity) -> {
         return IGroupBoss.class.isAssignableFrom(entity.getClass());
      }, CreatureManager.getInstance().spawnConfig.spawnLimitRange);
      return bosses.size() == 0;
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingData) {
      livingData = super.onInitialSpawn(difficulty, livingData);
      return livingData;
   }

   protected boolean canDespawn() {
      if (this.creatureInfo.creatureSpawn.despawnForced) {
         return true;
      } else if (!this.creatureInfo.creatureSpawn.despawnNatural) {
         return false;
      } else if (!this.creatureInfo.boss && (!this.isRareVariant() || Variant.RARE_DESPAWNING)) {
         return !this.isPersistant() && !this.getLeashed() && (!this.hasCustomName() || !"".equals(this.spawnEventType)) ? super.canDespawn() : false;
      } else {
         return false;
      }
   }

   public boolean isPersistant() {
      return this.forceNoDespawn;
   }

   public void enablePersistence() {
      super.enablePersistence();
      this.forceNoDespawn = true;
   }

   public boolean despawnCheck() {
      if (this.getEntityWorld().isRemote) {
         return false;
      } else if (!this.creatureInfo.enabled) {
         return true;
      } else if (this.isTemporary && this.temporaryDuration-- <= 0) {
         return true;
      } else if (!this.creatureInfo.peaceful && this.getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL && !this.hasCustomName()) {
         return true;
      } else {
         ExtendedWorld worldExt = ExtendedWorld.getForWorld(this.getEntityWorld());
         if (worldExt != null && !"".equals(this.spawnEventType) && this.spawnEventCount >= 0 && this.spawnEventCount != worldExt.getWorldEventCount()) {
            if (!this.getLeashed() && !this.isPersistant()) {
               return true;
            } else {
               this.spawnEventType = "";
               this.spawnEventCount = -1;
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public boolean isSpawnerNearby(World world, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (world == null) {
         return false;
      } else {
         int checkRange = 8;

         for(int i = x - checkRange; i <= x + checkRange; ++i) {
            for(int j = y - checkRange; j <= y + checkRange; ++j) {
               for(int k = z - checkRange; k <= z + checkRange; ++k) {
                  IBlockState spawnerBlock = world.getBlockState(new BlockPos(i, j, k));
                  if (spawnerBlock != null) {
                     TileEntity tileEntity = world.getTileEntity(new BlockPos(i, j, k));
                     if (tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
                        try {
                           TileEntityMobSpawner mobSpawner = (TileEntityMobSpawner)tileEntity;
                           NBTTagCompound nbt = new NBTTagCompound();
                           mobSpawner.getSpawnerBaseLogic().writeToNBT(nbt);
                           if (nbt.hasKey("SpawnData")) {
                              NBTTagCompound spawnData = nbt.getCompoundTag("SpawnData");
                              if (spawnData.hasKey("id")) {
                                 String mobSpawnEntityId = spawnData.getString("id");
                                 if (mobSpawnEntityId.equalsIgnoreCase(this.creatureInfo.getEntityId())) {
                                    return true;
                                 }
                              }
                           }
                        } catch (Exception var16) {
                           ;
                        }
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public boolean isBlockUnderground(int x, int y, int z) {
      if (this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z))) {
         return false;
      } else {
         for(int j = y; j < this.getEntityWorld().getHeight(); ++j) {
            Material blockMaterial = this.getEntityWorld().getBlockState(new BlockPos(x, j, z)).getMaterial();
            if (blockMaterial != Material.AIR && blockMaterial != Material.LEAVES && blockMaterial != Material.PLANTS && blockMaterial != Material.GRASS && blockMaterial != Material.VINE) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isBoss() {
      return this.isBossAlways() || this.spawnedAsBoss;
   }

   public boolean isBossAlways() {
      return this.creatureInfo.boss;
   }

   public boolean isNonBoss() {
      return !this.isBoss();
   }

   public void createBossInfo(Color color, boolean darkenSky) {
      String name = this.getName();
      if (this.isBossAlways()) {
         name = name + " (" + LanguageManager.translate("entity.phase") + " " + (this.getBattlePhase() + 1) + ")";
      }

      this.bossInfo = (BossInfoServer)(new BossInfoServer(new TextComponentString(name), color, Overlay.PROGRESS)).setDarkenSky(darkenSky);
   }

   public BossInfo getBossInfo() {
      if (this.bossInfo == null && this.showBossInfo() && !this.getEntityWorld().isRemote) {
         if (this.isBoss()) {
            this.createBossInfo(Color.RED, false);
         } else {
            this.createBossInfo(Color.GREEN, false);
         }
      }

      return this.bossInfo;
   }

   public void refreshBossHealthName() {
      if (this.bossInfo != null) {
         String name = this.getFullName();
         if (this.isBossAlways()) {
            name = name + " (" + LanguageManager.translate("entity.phase") + " " + (this.getBattlePhase() + 1) + ")";
         }

         this.bossInfo.setName(new TextComponentString(name));
      }

   }

   public void summonMinion(EntityLivingBase minion, double angle, double distance) {
      double angleRadians = Math.toRadians(angle);
      double x = this.posX + (((double)this.width + distance) * Math.cos(angleRadians) - Math.sin(angleRadians));
      double y = this.posY + 1.0D;
      if (minion instanceof BaseCreatureEntity && ((BaseCreatureEntity)minion).isCurrentlyFlying()) {
         y += (double)(this.height / 2.0F);
      }

      double z = this.posZ + ((double)this.width + distance) * Math.sin(angleRadians) + Math.cos(angleRadians);
      minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
      this.getEntityWorld().spawnEntity(minion);
      if (minion instanceof BaseCreatureEntity) {
         ((BaseCreatureEntity)minion).setMinion(true);
         if (!this.isRareVariant()) {
            ((BaseCreatureEntity)minion).applyVariant(this.getVariantIndex());
         }

         ((BaseCreatureEntity)minion).setSubspecies(this.getSubspeciesIndex());
         ((BaseCreatureEntity)minion).setMasterTarget(this);
         ((BaseCreatureEntity)minion).spawnEventType = this.spawnEventType;
         if (this.isTemporary) {
            ((BaseCreatureEntity)minion).setTemporary(this.temporaryDuration);
         }

         ((BaseCreatureEntity)minion).onFirstSpawn();
      }

      if (this.getAttackTarget() != null) {
         minion.setRevengeTarget(this.getAttackTarget());
      }

      this.addMinion(minion);
   }

   public boolean addMinion(EntityLivingBase minion) {
      if (this.minions.contains(minion)) {
         return false;
      } else {
         this.minions.add(minion);
         return true;
      }
   }

   public List<EntityLivingBase> getMinions(Class<? extends Entity> filterClass) {
      if (filterClass == null) {
         return this.minions;
      } else {
         List<EntityLivingBase> filteredMinions = new ArrayList();

         for(EntityLivingBase minion : this.minions) {
            if (filterClass.isAssignableFrom(minion.getClass())) {
               filteredMinions.add(minion);
            }
         }

         return filteredMinions;
      }
   }

   public void onMinionUpdate(EntityLivingBase minion, long tick) {
   }

   public void onMinionDeath(EntityLivingBase minion, DamageSource damageSource) {
   }

   public void onTryToDamageMinion(EntityLivingBase minion, float damageAmount) {
   }

   public void setMinion(boolean minion) {
      this.isMinion = minion;
   }

   public boolean isMinion() {
      return this.isMinion;
   }

   public void setTemporary(int duration) {
      this.temporaryDuration = duration;
      this.isTemporary = true;
   }

   public void unsetTemporary() {
      this.isTemporary = false;
      this.temporaryDuration = 0;
   }

   public boolean isBoundPet() {
      return this.hasPetEntry();
   }

   public boolean hasPetEntry() {
      return this.getPetEntry() != null;
   }

   public PetEntry getPetEntry() {
      return this.petEntry;
   }

   public void setPetEntry(PetEntry petEntry) {
      this.petEntry = petEntry;
   }

   public boolean isPetType(String type) {
      return !this.hasPetEntry() ? false : type.equals(this.getPetEntry().getType());
   }

   public void onFirstSpawn() {
      this.firstSpawn = false;
      if (this.hasPetEntry()) {
         if (this.getPetEntry().summonSet != null && this.getPetEntry().summonSet.playerExt != null) {
            this.getPetEntry().summonSet.playerExt.sendPetEntryToPlayer(this.getPetEntry());
         }

      } else if (!this.isMinion()) {
         if (this.needsInitialLevel) {
            this.applyLevel(this.getStartingLevel());
         }

         if (this.getSubspeciesIndex() == 0 && this.getVariantIndex() == 0) {
            this.getRandomSubspecies();
            if (CreatureManager.getInstance().config.variantSpawn && !this.creatureInfo.creatureSpawn.disableVariants) {
               this.getRandomVariant();
            }
         }

         if (this.sizeScale == 1.0D && CreatureManager.getInstance().config.randomSizes) {
            this.getRandomSize();
         }

      }
   }

   public void getRandomSubspecies() {
      if (!this.isMinion()) {
         Subspecies randomSubspecies = this.creatureInfo.getRandomSubspecies(this);
         this.setSubspecies(randomSubspecies.index);
         LycanitesMobs.logDebug("Subspecies", "Setting " + this.getSpeciesName() + " subspecies to " + randomSubspecies.getTitle());
      }

   }

   public void getRandomVariant() {
      if (!this.isMinion()) {
         Variant randomVariant = this.getSubspecies().getRandomVariant(this, this.spawnedRare);
         if (randomVariant != null) {
            LycanitesMobs.logDebug("Subspecies", "Setting " + this.getSpeciesName() + " variant to " + randomVariant.getTitle());
            this.applyVariant(randomVariant.index);
         } else {
            LycanitesMobs.logDebug("Subspecies", "Setting " + this.getSpeciesName() + " variant to base variant.");
            this.applyVariant(0);
         }
      }

   }

   public void getRandomSize() {
      double range = CreatureManager.getInstance().config.randomSizeMax - CreatureManager.getInstance().config.randomSizeMin;
      double randomScale = range * this.getRNG().nextDouble();
      double scale = CreatureManager.getInstance().config.randomSizeMin + randomScale;
      if (this.getVariant() != null) {
         scale *= this.getVariant().getScale();
      }

      this.setSizeScale(scale);
   }

   public int getAge() {
      if (this.world.isRemote) {
         return this.getBoolFromDataManager(BABY) ? -1 : 1;
      } else {
         return this.growingAge;
      }
   }

   public int getLevel() {
      return this.getEntityWorld().isRemote ? this.getIntFromDataManager(LEVEL) : this.level;
   }

   public int getStartingLevel() {
      int startingLevelMin = Math.max(1, CreatureManager.getInstance().config.startingLevelMin);
      if (CreatureManager.getInstance().config.startingLevelMax > startingLevelMin) {
         return startingLevelMin + this.getRNG().nextInt(CreatureManager.getInstance().config.startingLevelMax - startingLevelMin);
      } else {
         if (CreatureManager.getInstance().config.levelPerDay > 0.0D && CreatureManager.getInstance().config.levelPerDayMax > 0) {
            int day = (int)Math.floor((double)this.getEntityWorld().getTotalWorldTime() / 23999.0D);
            double levelGain = Math.min(CreatureManager.getInstance().config.levelPerDay * (double)day, (double)CreatureManager.getInstance().config.levelPerDayMax);
            startingLevelMin += (int)Math.floor(levelGain);
         }

         if (CreatureManager.getInstance().config.levelPerLocalDifficulty > 0.0D) {
            double levelGain = (double)this.getEntityWorld().getDifficultyForLocation(this.getPosition()).getAdditionalDifficulty();
            startingLevelMin += Math.max(0, (int)Math.floor(levelGain - 1.5D));
         }

         return startingLevelMin;
      }
   }

   public void applyLevel(int level) {
      this.needsInitialLevel = false;
      this.setLevel(level);
      this.refreshAttributes();
   }

   public void setLevel(int level) {
      this.level = level;
      this.dataManager.set(LEVEL, Integer.valueOf(level));
   }

   public void addLevel(int level) {
      this.applyLevel(this.level + level);
   }

   public void updateLevelExperience() {
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(EXPERIENCE, Integer.valueOf(this.experience));
      }

      if (this.getExperience() >= this.creatureStats.getExperienceForNextLevel()) {
         this.setExperience(this.getExperience() - this.creatureStats.getExperienceForNextLevel());
         this.addLevel(1);
      }

   }

   public int getExperience() {
      return this.getEntityWorld().isRemote ? this.getIntFromDataManager(EXPERIENCE) : this.experience;
   }

   public void setExperience(int experience) {
      this.experience = experience;
      this.updateLevelExperience();
   }

   public void addExperience(int experience) {
      this.experience += experience;
      this.updateLevelExperience();
   }

   public int getMeleeCooldown() {
      return Math.round((float)(1.0D / this.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D));
   }

   public int getRangedCooldown() {
      return Math.round((float)(1.0D / this.getEntityAttribute(RANGED_SPEED).getAttributeValue() * 20.0D));
   }

   public int getEffectDuration(int seconds) {
      return Math.round((float)seconds * (float)this.creatureStats.getEffect() * 20.0F);
   }

   public int getEffectAmplifier(float scale) {
      return Math.round((float)this.creatureStats.getAmplifier());
   }

   public int getEffectStrength(float value) {
      return Math.round(value * (float)this.creatureStats.getAmplifier());
   }

   public List<ElementInfo> getElements() {
      return this.getSubspecies() != null && !this.getSubspecies().elements.isEmpty() ? this.getSubspecies().elements : this.creatureInfo.elements;
   }

   public boolean hasElement(ElementInfo element) {
      return this.getElements().contains(element);
   }

   public void setSubspecies(int subspeciesIndex) {
      this.subspecies = this.creatureInfo.getSubspecies(subspeciesIndex);
   }

   public void setVariant(int variantIndex) {
      this.variant = this.getSubspecies().getVariant(variantIndex);
      if (this.variant != null) {
         float scaledExp = (float)this.creatureInfo.experience;
         if ("uncommon".equals(this.variant.rarity)) {
            scaledExp = (float)Math.round((float)((double)this.creatureInfo.experience * Variant.UNCOMMON_EXPERIENCE_SCALE));
         } else if ("rare".equals(this.variant.rarity)) {
            scaledExp = (float)Math.round((float)((double)this.creatureInfo.experience * Variant.RARE_EXPERIENCE_SCALE));
         }

         this.experienceValue = Math.round(scaledExp);
         if ("rare".equals(this.variant.rarity)) {
            this.damageLimit = (float)BOSS_DAMAGE_LIMIT;
            this.damageMax = BOSS_DAMAGE_LIMIT;
         }
      }

   }

   public void applyVariant(int variantIndex) {
      this.setVariant(variantIndex);
      this.refreshAttributes();
   }

   public Subspecies getSubspecies() {
      if (this.subspecies == null) {
         this.subspecies = this.creatureInfo.getSubspecies(0);
      }

      return this.subspecies;
   }

   public Variant getVariant() {
      return this.variant;
   }

   public int getSubspeciesIndex() {
      return this.getSubspecies().index;
   }

   public int getVariantIndex() {
      return this.getVariant() != null ? this.getVariant().index : 0;
   }

   public boolean isRareVariant() {
      return this.getVariant() != null && "rare".equals(this.getVariant().rarity);
   }

   public int scaleKnowledgeExperience(int knowledgeExperience) {
      if (this.isBoss()) {
         knowledgeExperience = Math.round((float)CreatureManager.getInstance().config.creatureBossKnowledgeScale * (float)knowledgeExperience);
      } else if (this.getVariant() != null) {
         knowledgeExperience = Math.round((float)CreatureManager.getInstance().config.creatureVariantKnowledgeScale * (float)knowledgeExperience);
      }

      return knowledgeExperience;
   }

   public void updateBattlePhase() {
   }

   public int getBattlePhase() {
      return this.battlePhase;
   }

   public void setBattlePhase(int phase) {
      if (this.getBattlePhase() != phase) {
         this.battlePhase = phase;
         this.refreshBossHealthName();
         this.playPhaseSound();
      }
   }

   public EntityLivingBase transform(Class transformClass, Entity partner, boolean destroyPartner) {
      EntityLivingBase transformedEntity = null;

      try {
         transformedEntity = (EntityLivingBase)EntityList.newEntity(transformClass, this.getEntityWorld());
      } catch (Exception var12) {
         var12.printStackTrace();
      }

      if (transformedEntity == null) {
         return null;
      } else {
         if (transformedEntity instanceof BaseCreatureEntity) {
            BaseCreatureEntity transformedCreature = (BaseCreatureEntity)transformedEntity;
            transformedCreature.firstSpawn = false;
            if (this.isTemporary) {
               transformedCreature.setTemporary(this.temporaryDuration);
            }

            if (this.isMinion()) {
               transformedCreature.setMinion(true);
            }

            if (this.hasMaster()) {
               transformedCreature.setMasterTarget(this.getMasterTarget());
            }

            if (partner instanceof BaseCreatureEntity) {
               BaseCreatureEntity partnerCreature = (BaseCreatureEntity)partner;
               Variant fusionVariant = transformedCreature.getSubspecies().getChildVariant(this, this.getVariant(), partnerCreature.getVariant());
               transformedCreature.setSubspecies(this.getSubspeciesIndex());
               transformedCreature.applyVariant(fusionVariant != null ? fusionVariant.index : 0);
               transformedCreature.setSizeScale(this.sizeScale + partnerCreature.sizeScale);
               if (partnerCreature.summoningPedestal != null) {
                  partnerCreature.summoningPedestal.minions.add(transformedCreature);
                  transformedCreature.summoningPedestal = partnerCreature.summoningPedestal;
               }

               if (this.summoningPedestal != null) {
                  this.summoningPedestal.minions.add(transformedCreature);
                  transformedCreature.summoningPedestal = this.summoningPedestal;
               }

               int transformedLevel = this.getLevel();
               if ("lowest".equalsIgnoreCase(CreatureManager.getInstance().config.elementalFusionLevelMix)) {
                  if (transformedLevel > partnerCreature.getLevel()) {
                     transformedLevel = partnerCreature.getLevel();
                  }
               } else if ("highest".equalsIgnoreCase(CreatureManager.getInstance().config.elementalFusionLevelMix)) {
                  if (transformedLevel < partnerCreature.getLevel()) {
                     transformedLevel = partnerCreature.getLevel();
                  }
               } else {
                  transformedLevel += partnerCreature.getLevel();
               }

               transformedCreature.applyLevel(Math.round((float)transformedLevel * (float)CreatureManager.getInstance().config.elementalFusionLevelMultiplier));
               if (transformedCreature instanceof TameableCreatureEntity) {
                  TameableCreatureEntity fusionTameable = (TameableCreatureEntity)transformedCreature;
                  if (this instanceof TameableCreatureEntity) {
                     Entity owner = ((TameableCreatureEntity)this).getPlayerOwner();
                     if (owner != null) {
                        transformedCreature.applyLevel(transformedLevel);
                        fusionTameable.setPlayerOwner((EntityPlayer)owner);
                        ((TameableCreatureEntity)this).copyPetBehaviourTo(fusionTameable);
                     }
                  } else {
                     Entity partnerOwner = null;
                     if (partnerCreature instanceof TameableCreatureEntity) {
                        Entity var14 = ((TameableCreatureEntity)partnerCreature).getPlayerOwner();
                        if (var14 != null) {
                           transformedCreature.applyLevel(transformedLevel);
                           fusionTameable.setPlayerOwner((EntityPlayer)var14);
                           ((TameableCreatureEntity)partnerCreature).copyPetBehaviourTo(fusionTameable);
                           if (partnerCreature.isTemporary) {
                              transformedCreature.setTemporary(partnerCreature.temporaryDuration);
                           }

                           transformedCreature.setMinion(partnerCreature.isMinion());
                           if (partnerCreature.hasMaster()) {
                              transformedCreature.setMasterTarget(partnerCreature.getMasterTarget());
                           }
                        }
                     }
                  }
               }
            } else {
               transformedCreature.setSubspecies(this.getSubspeciesIndex());
               transformedCreature.applyVariant(this.getVariantIndex());
               transformedCreature.setSizeScale(this.sizeScale);
               transformedCreature.applyLevel(this.getLevel());
               if (transformedCreature instanceof TameableCreatureEntity) {
                  TameableCreatureEntity fusionTameable = (TameableCreatureEntity)transformedCreature;
                  if (this.getOwner() != null && this.getOwner() instanceof EntityPlayer) {
                     fusionTameable.setPlayerOwner((EntityPlayer)this.getOwner());
                     if (this instanceof TameableCreatureEntity) {
                        ((TameableCreatureEntity)this).copyPetBehaviourTo(fusionTameable);
                     }
                  }
               }
            }
         }

         transformedEntity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.getEntityWorld().spawnEntity(transformedEntity);
         this.setDead();
         if (partner != null && destroyPartner) {
            partner.setDead();
         }

         return transformedEntity;
      }
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.creatureInfo.dummy) {
         this.onSyncUpdate();
         if (!this.getEntityWorld().isRemote) {
            this.updateHitAreas();
         }

         if (this.despawnCheck()) {
            if (!this.isBoundPet() || this.isTemporary) {
               this.inventory.dropInventory();
            }

            this.setDead();
         }

         this.isImmuneToFire = !this.canBurn();
         if (!this.canWalk() && !this.isCurrentlyFlying() && !this.isInWater() && this.isMoving() || !this.canMove()) {
            this.clearMovement();
         }

         if (!this.getEntityWorld().isRemote || this.canPassengerSteer()) {
            this.setBesideClimbableBlock(this.collidedHorizontally);
            if (!this.onGround && this.flySoundSpeed > 0 && this.ticksExisted % 20 == 0) {
               this.playFlySound();
            }
         }

         if (!this.getEntityWorld().isRemote && this.isFlying() && this.hasAttackTarget() && this.updateTick % 40L == 0L) {
            this.leap(0.0D, 0.4D);
         }

         EntityLivingBase perchTarget = this.getPerchTarget();
         if (perchTarget != null) {
            ExtendedEntity perchEntityExt = ExtendedEntity.getForEntity(this.getPerchTarget());
            if (perchEntityExt != null) {
               Vec3d perchPosition = perchEntityExt.getPerchPosition();
               this.setPosition(perchPosition.x, perchPosition.y, perchPosition.z);
               this.motionX = perchTarget.motionX;
               this.motionY = perchTarget.motionY;
               this.motionZ = perchTarget.motionZ;
               this.rotationYaw = perchTarget.rotationYaw;
            }

            if (perchTarget instanceof EntityPlayer && !perchTarget.isRiding()) {
               ExtendedPlayer perchPlayerExt = ExtendedPlayer.getForPlayer((EntityPlayer)perchTarget);
               if (perchPlayerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_DISMOUNT)) {
                  this.perchOnEntity((EntityLivingBase)null);
               }
            }
         }

         if (!this.getEntityWorld().isRemote && this.isBoss() && this.updateTick % 20L == 0L && this.playerTargets.isEmpty()) {
            this.heal(1.0F);
         }

         if (this.bossInfo != null) {
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
         }

         if (!this.getEntityWorld().isRemote && this.updateTick % 40L == 0L) {
            for(EntityPlayer player : this.getEntityWorld().getPlayers(EntityPlayer.class, (player) -> {
               return player != null && this.getDistance(player) <= 5.0F;
            })) {
               ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
               if (extendedPlayer != null) {
                  CreatureKnowledge creatureKnowledge = extendedPlayer.getBeastiary().getCreatureKnowledge(this.creatureInfo.getName());
                  if (creatureKnowledge == null || creatureKnowledge.rank < 1) {
                     extendedPlayer.studyCreature(this, CreatureManager.getInstance().config.creatureProximityKnowledge, false, false);
                  }
               }
            }
         }

         if (!this.getEntityWorld().isRemote && this.guiViewers.size() <= 0) {
            this.guiRefreshTick = 0;
         }

         if (!this.getEntityWorld().isRemote && this.guiRefreshTick > 0 && --this.guiRefreshTick <= 0) {
            this.refreshGUIViewers();
            this.guiRefreshTick = 0;
         }

      }
   }

   protected void updateAITasks() {
      if (this.useDirectNavigator()) {
         this.directNavigator.updateFlight();
      }

      super.updateAITasks();
   }

   public void onLivingUpdate() {
      if (this.damageLimit > 0.0F) {
         if (this.healthLastTick < 0.0F) {
            this.healthLastTick = this.getHealth();
         }

         if (this.healthLastTick - this.getHealth() > this.damageLimit) {
            this.setHealth(this.healthLastTick - this.damageLimit);
         }

         this.healthLastTick = this.getHealth();
         if (!this.getEntityWorld().isRemote && this.updateTick % 20L == 0L) {
            this.damageTakenThisSec = 0.0F;
         }
      }

      super.onLivingUpdate();
      if (this.creatureInfo.dummy) {
         ++this.updateTick;
      } else {
         if (this.attackCooldown > 0) {
            --this.attackCooldown;
            if (this.attackCooldown > this.getAttackCooldownMax()) {
               this.triggerAttackCooldown();
            }
         }

         this.updateBattlePhase();
         this.updateArmSwingProgress();
         if (this.currentBlockingTime > 0) {
            --this.currentBlockingTime;
         }

         if (this.currentBlockingTime < 0) {
            this.currentBlockingTime = 0;
         }

         if (!this.getEntityWorld().isRemote && this.firstSpawn) {
            this.onFirstSpawn();
         }

         if (!this.getEntityWorld().isRemote && !this.hasFixateTarget() && this.fixateUUID != null) {
            double range = 64.0D;

            for(EntityLivingBase possibleFixateTarget : this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(range, range, range))) {
               if (possibleFixateTarget != this && possibleFixateTarget.getUniqueID().equals(this.fixateUUID)) {
                  this.setFixateTarget(possibleFixateTarget);
                  break;
               }
            }

            this.fixateUUID = null;
         }

         if (this.hasFixateTarget()) {
            this.setAttackTarget(this.getFixateTarget());
         }

         if (this.hasAttackTarget() && this.getAttackTarget() instanceof EntityPlayer) {
            EntityPlayer targetPlayer = (EntityPlayer)this.getAttackTarget();
            if (targetPlayer.capabilities.isCreativeMode) {
               this.setAttackTarget((EntityLivingBase)null);
            }
         }

         if (this.hasAvoidTarget() && this.currentFleeTime-- <= 0) {
            this.setAvoidTarget((EntityLivingBase)null);
         }

         if (!this.onGround && this.motionY < 0.0D) {
            this.motionY *= this.getFallingMod();
         }

         if (!this.getEntityWorld().isRemote && this.daylightBurns() && this.getEntityWorld().isDaytime()) {
            float brightness = this.getBrightness();
            if (brightness > 0.5F && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
               boolean shouldBurn = true;
               ItemStack helmet = this.inventory.getEquipmentStack("head");
               if (!helmet.isEmpty()) {
                  if (helmet.isItemStackDamageable()) {
                     helmet.setItemDamage(helmet.getItemDamage() + this.rand.nextInt(2));
                     if (helmet.getItemDamage() >= helmet.getMaxDamage()) {
                        this.renderBrokenItemStack(helmet);
                        this.setCurrentItemOrArmor(4, ItemStack.EMPTY);
                     }
                  }

                  shouldBurn = false;
               }

               if (shouldBurn) {
                  this.setFire(8);
               }
            }
         }

         if (!this.getEntityWorld().isRemote && this.waterDamage() && this.isWet() && !this.isInLava()) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0F);
         }

         if (!this.getEntityWorld().isRemote && !this.canBreatheAir()) {
            int currentAir = this.getAir();
            if (this.isEntityAlive()) {
               if (!this.isLavaCreature && !this.waterContact() || this.isLavaCreature && !this.lavaContact()) {
                  --currentAir;
                  this.setAir(currentAir);
                  if (this.getAir() <= -200) {
                     this.setAir(-180);
                     this.attackEntityFrom(DamageSource.DROWN, 2.0F);
                  }
               } else {
                  this.setAir(299);
               }
            }
         }

         if (!this.getEntityWorld().isRemote) {
            float light = this.getBrightness();
            if (!this.creatureInfo.creatureSpawn.spawnsInLight && light > 0.5F) {
               this.idleTime += 2;
            } else if (!this.creatureInfo.creatureSpawn.spawnsInDark && light <= 0.5F) {
               this.idleTime += 2;
            }
         }

         if (!this.getEntityWorld().isRemote) {
            if (this.isStealthed() && !this.isInvisible()) {
               this.setInvisible(true);
            } else if (!this.isStealthed() && this.isInvisible() && !this.isPotionActive(MobEffects.INVISIBILITY)) {
               this.setInvisible(false);
            }
         }

         if (this.isStealthed()) {
            if (this.stealthPrev != this.isStealthed()) {
               this.startStealth();
            }

            this.onStealth();
         } else if (this.isInvisible() && !this.isPotionActive(MobEffects.INVISIBILITY) && !this.getEntityWorld().isRemote) {
            this.setInvisible(false);
         }

         this.stealthPrev = this.isStealthed();
         if (this.ticksExisted % 20 == 0 && !this.getEntityWorld().isRemote && this.isEntityAlive() && this.canPickupItems()) {
            this.pickupItems();
         }

         if (!this.getEntityWorld().isRemote && this.pickupEntity != null) {
            if (!this.pickupEntity.isEntityAlive()) {
               this.dropPickupEntity();
            } else if (Math.sqrt((double)this.getDistance(this.pickupEntity)) > 32.0D) {
               this.dropPickupEntity();
            }
         }

         this.getBossInfo();
         if (this.isBossAlways() || this.spawnedWithBlockProtection > 0) {
            ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(this.getEntityWorld());
            extendedWorld.bossUpdate(this);
         }

         if (!this.minions.isEmpty()) {
            for(EntityLivingBase minion : (EntityLivingBase[])this.minions.toArray(new EntityLivingBase[0])) {
               if (!minion.isEntityAlive()) {
                  this.minions.remove(minion);
               }
            }
         }

         if (this.getMasterTarget() != null && this.getMasterTarget() instanceof BaseCreatureEntity) {
            ((BaseCreatureEntity)this.getMasterTarget()).onMinionUpdate(this, this.updateTick);
         }

         ++this.updateTick;
      }
   }

   public void onSyncUpdate() {
      if (this.syncQueued) {
         this.doSync();
      }

      if (!this.getEntityWorld().isRemote) {
         byte targets = 0;
         if (this.getAttackTarget() != null) {
            targets += BaseCreatureEntity.TARGET_BITS.ATTACK.id;
         }

         if (this.getMasterTarget() != null) {
            targets += BaseCreatureEntity.TARGET_BITS.MASTER.id;
         }

         if (this.getParentTarget() != null) {
            targets += BaseCreatureEntity.TARGET_BITS.PARENT.id;
         }

         if (this.getAvoidTarget() != null) {
            targets += BaseCreatureEntity.TARGET_BITS.AVOID.id;
         }

         if (this.getControllingPassenger() != null) {
            targets += BaseCreatureEntity.TARGET_BITS.RIDER.id;
         }

         if (this.getPickupEntity() != null) {
            targets += BaseCreatureEntity.TARGET_BITS.PICKUP.id;
         }

         if (this.getPerchTarget() != null) {
            targets += BaseCreatureEntity.TARGET_BITS.PERCH.id;
         }

         this.dataManager.set(TARGET, Byte.valueOf(targets));
      }

      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(ATTACK_PHASE, Byte.valueOf(this.attackPhase));
      }

      if (!this.getEntityWorld().isRemote) {
         byte animations = 0;
         if (this.isAttackOnCooldown()) {
            animations += BaseCreatureEntity.ANIMATION_STATE_BITS.ATTACKED.id;
         }

         if (this.onGround) {
            animations += BaseCreatureEntity.ANIMATION_STATE_BITS.GROUNDED.id;
         }

         if (this.inWater) {
            animations += BaseCreatureEntity.ANIMATION_STATE_BITS.IN_WATER.id;
         }

         if (this.isBlocking()) {
            animations += BaseCreatureEntity.ANIMATION_STATE_BITS.BLOCKING.id;
         }

         if (this.isMinion()) {
            animations += BaseCreatureEntity.ANIMATION_STATE_BITS.MINION.id;
         }

         if (this.extraAnimation01()) {
            animations += BaseCreatureEntity.ANIMATION_STATE_BITS.EXTRA01.id;
         }

         if (this.spawnedAsBoss) {
            animations += BaseCreatureEntity.ANIMATION_STATE_BITS.BOSS.id;
         }

         this.dataManager.set(ANIMATION_STATE, Byte.valueOf(animations));
      } else if (this.getEntityWorld().isRemote) {
         byte animationState = this.getByteFromDataManager(ANIMATION_STATE);
         if ((animationState & BaseCreatureEntity.ANIMATION_STATE_BITS.ATTACKED.id) > 0) {
            if (!this.isAttackOnCooldown()) {
               this.triggerAttackCooldown();
            }
         } else {
            this.resetAttackCooldown();
         }

         this.onGround = (animationState & BaseCreatureEntity.ANIMATION_STATE_BITS.GROUNDED.id) > 0;
         this.inWater = (animationState & BaseCreatureEntity.ANIMATION_STATE_BITS.IN_WATER.id) > 0;
         this.extraAnimation01 = (animationState & BaseCreatureEntity.ANIMATION_STATE_BITS.EXTRA01.id) > 0;
         this.spawnedAsBoss = (animationState & BaseCreatureEntity.ANIMATION_STATE_BITS.BOSS.id) > 0;
      }

      if (this.getEntityWorld().isRemote) {
         this.isMinion = (this.getByteFromDataManager(ANIMATION_STATE) & BaseCreatureEntity.ANIMATION_STATE_BITS.MINION.id) > 0;
      }

      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(SUBSPECIES, Byte.valueOf((byte)this.getSubspeciesIndex()));
      } else if (this.getSubspeciesIndex() != this.getByteFromDataManager(SUBSPECIES)) {
         this.setSubspecies(this.getByteFromDataManager(SUBSPECIES));
      }

      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(VARIANT, Byte.valueOf((byte)this.getVariantIndex()));
      } else if (this.getVariantIndex() != this.getByteFromDataManager(VARIANT)) {
         this.applyVariant(this.getByteFromDataManager(VARIANT));
      }

      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(SIZE, Float.valueOf((float)this.sizeScale));
      } else if (this.sizeScale != (double)this.getFloatFromDataManager(SIZE)) {
         this.setSizeScale((double)this.getFloatFromDataManager(SIZE));
      }

      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(ARENA, this.getArenaCenter() != null ? Optional.of(this.getArenaCenter()) : Optional.absent());
      }

   }

   public void updateHitAreas() {
      if (ENABLE_HITAREAS.booleanValue()) {
         int hitAreaWidthCount = Math.max(1, Math.round(this.width * this.hitAreaWidthScale / 4.0F));
         int hitAreaHeightCount = Math.max(1, Math.round(this.height * this.hitAreaHeightScale / 4.0F));
         if (hitAreaWidthCount < 2 && hitAreaHeightCount < 2) {
            this.hitAreas = (EntityHitArea[][][])null;
         } else {
            if (this.hitAreas == null || this.hitAreas[0] == null || this.hitAreas[0][0] == null || this.hitAreas.length != hitAreaHeightCount || this.hitAreas[0].length != hitAreaWidthCount || this.hitAreas[0][0].length != hitAreaWidthCount) {
               this.hitAreas = new EntityHitArea[hitAreaHeightCount][hitAreaWidthCount][hitAreaWidthCount];
            }

            for(int y = 0; y < hitAreaHeightCount; ++y) {
               for(int x = 0; x < hitAreaWidthCount; ++x) {
                  for(int z = 0; z < hitAreaWidthCount; ++z) {
                     if (y == 0 || y == hitAreaHeightCount - 1 || x == 0 || x == hitAreaWidthCount - 1 || z == 0 || z == hitAreaWidthCount - 1) {
                        if (this.hitAreas[y][x][z] == null) {
                           this.hitAreas[y][x][z] = new EntityHitArea(this, this.width / (float)hitAreaWidthCount * this.hitAreaWidthScale, this.height / (float)hitAreaHeightCount * this.hitAreaHeightScale);
                           this.getEntityWorld().spawnEntity(this.hitAreas[y][x][z]);
                        }

                        this.hitAreas[y][x][z].posX = this.posX - (double)(this.width * this.hitAreaWidthScale / 2.0F) + (double)(this.width * this.hitAreaWidthScale / (float)hitAreaWidthCount / 2.0F) + (double)(this.width * this.hitAreaWidthScale / (float)hitAreaWidthCount * (float)x);
                        this.hitAreas[y][x][z].posY = this.posY + (double)(this.height / (float)hitAreaHeightCount * this.hitAreaHeightScale * (float)y);
                        this.hitAreas[y][x][z].posZ = this.posZ - (double)(this.width * this.hitAreaWidthScale / 2.0F) + (double)(this.width * this.hitAreaWidthScale / (float)hitAreaWidthCount / 2.0F) + (double)(this.width * this.hitAreaWidthScale / (float)hitAreaWidthCount * (float)z);
                        this.hitAreas[y][x][z].rotationYaw = this.rotationYaw;
                     }
                  }
               }
            }

         }
      }
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return this.solidCollision ? this.getEntityBoundingBox() : null;
   }

   public AxisAlignedBB getCollisionBox(Entity entity) {
      return super.getCollisionBox(entity);
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox();
   }

   public float getBlockPathWeight(int par1, int par2, int par3) {
      if (this.creatureInfo.creatureSpawn.spawnsInDark && !this.creatureInfo.creatureSpawn.spawnsInLight) {
         return 0.5F - this.getEntityWorld().getLightBrightness(new BlockPos(par1, par2, par3));
      } else {
         return this.creatureInfo.creatureSpawn.spawnsInLight && !this.creatureInfo.creatureSpawn.spawnsInDark ? this.getEntityWorld().getLightBrightness(new BlockPos(par1, par2, par3)) - 0.5F : 0.0F;
      }
   }

   public boolean useDirectNavigator() {
      return false;
   }

   public boolean shouldSwim() {
      if (!this.isInWater() && !this.isInLava()) {
         return false;
      } else if (this.canWade() && this.canBreatheUnderwater()) {
         boolean targetInWater = true;
         if (this.getAttackTarget() != null) {
            targetInWater = this.getAttackTarget().isInWater();
         } else if (this.getParentTarget() != null) {
            targetInWater = this.getParentTarget().isInWater();
         } else if (this.getMasterTarget() != null) {
            targetInWater = this.getMasterTarget().isInWater();
         }

         if (!targetInWater) {
            IBlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().up());
            if (blockState.getBlock().isAir(blockState, this.getEntityWorld(), this.getPosition().up())) {
               return false;
            }
         }

         return true;
      } else {
         return this.isStrongSwimmer();
      }
   }

   public boolean isInLava() {
      AxisAlignedBB boundingBox = this.getEntityBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);
      if (this.width / 2.0F > 2.0F || this.height > 2.0F) {
         double radius = Math.min((double)this.width / 2.0D, 2.0D);
         double height = Math.min((double)this.height, 2.0D);
         boundingBox = new AxisAlignedBB(this.posX - radius, this.posY, this.posZ - radius, this.posX + radius, this.posY + height, this.posZ + radius);
      }

      return this.world.isMaterialInBB(boundingBox, Material.LAVA);
   }

   public void move(MoverType type, double x, double y, double z) {
      if (Double.isNaN(x)) {
         x = 0.0D;
      }

      if (Double.isNaN(y)) {
         y = 0.0D;
      }

      if (Double.isNaN(z)) {
         z = 0.0D;
      }

      x = Math.max(Math.min(x, 10.0D), -10.0D);
      y = Math.max(Math.min(y, 10.0D), -10.0D);
      z = Math.max(Math.min(z, 10.0D), -10.0D);
      super.move(type, x, y, z);
   }

   public void travel(float strafe, float up, float forward) {
      if (!this.useDirectNavigator()) {
         if (this.isFlying() && !this.isInWater() && !this.isInLava()) {
            this.moveFlyingWithHeading(strafe, forward);
            this.updateLimbSwing();
         } else if (this.shouldSwim()) {
            this.moveSwimmingWithHeading(strafe, forward);
            this.updateLimbSwing();
         } else {
            super.travel(strafe, up, forward);
         }
      } else {
         this.directNavigator.flightMovement(strafe, forward);
         this.updateLimbSwing();
      }

   }

   public void updateLimbSwing() {
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double distanceX = this.posX - this.prevPosX;
      double distanceZ = this.posZ - this.prevPosZ;
      float distance = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ) * 4.0F;
      if (distance > 1.0F) {
         distance = 1.0F;
      }

      this.limbSwingAmount += (distance - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public void moveFlyingWithHeading(float strafe, float forward) {
      float f = 0.91F;
      if (this.onGround) {
         f = this.getEntityWorld().getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
      }

      float f1 = 0.16277136F / (f * f * f);
      this.moveFlying(strafe, forward, this.onGround ? 0.1F * f1 : 0.02F);
      f = 0.91F;
      if (this.onGround) {
         f = this.getEntityWorld().getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
      }

      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)f;
      this.motionY *= (double)f;
      this.motionZ *= (double)f;
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d1 = this.posX - this.prevPosX;
      double d0 = this.posZ - this.prevPosZ;
      float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public void moveFlying(float strafe, float forward, float friction) {
      if (!this.isPushedByWater() && this.canWalk() && this.isInWater()) {
         float sliperryness = 0.91F;
         PooledMutableBlockPos blockpos$pooledmutableblockpos = PooledMutableBlockPos.retain(this.posX, this.getEntityBoundingBox().minY - 1.0D, this.posZ);
         if (this.onGround) {
            sliperryness = this.getEntityWorld().getBlockState(blockpos$pooledmutableblockpos).getBlock().slipperiness * sliperryness * 2.0F;
         }

         float f7 = 0.16277136F / (sliperryness * sliperryness * sliperryness);
         friction = this.getAIMoveSpeed() * f7;
      }

   }

   public void moveSwimmingWithHeading(float strafe, float forward) {
      super.moveRelative(strafe, 0.0F, forward, 0.1F);
      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.8999999761581421D;
      this.motionY *= 0.8999999761581421D;
      this.motionZ *= 0.8999999761581421D;
   }

   protected PathNavigate createNavigator(World world) {
      return new CreaturePathNavigate(this, world);
   }

   protected EntityMoveHelper createMoveHelper() {
      return new CreatureMoveHelper(this);
   }

   public PathNavigate getNavigator() {
      return super.getNavigator();
   }

   public EntityMoveHelper getMoveHelper() {
      return super.getMoveHelper();
   }

   public void clearMovement() {
      if (!this.useDirectNavigator() && this.getNavigator() != null) {
         this.getNavigator().clearPath();
      } else {
         this.directNavigator.clearTargetPosition(1.0D);
      }

   }

   public boolean rollLookChance() {
      return this.getRNG().nextFloat() < 0.02F;
   }

   public boolean rollWanderChance() {
      if (this.width >= 3.0F) {
         return this.getRNG().nextDouble() <= 5.0E-4D;
      } else {
         return this.getRNG().nextDouble() <= 0.008D;
      }
   }

   protected void updateLeashedState() {
      super.updateLeashedState();
      if (this.getLeashed() && this.getLeashHolder().getEntityWorld() == this.getEntityWorld()) {
         Entity entity = this.getLeashHolder();
         this.setHome((int)entity.posX, (int)entity.posY, (int)entity.posZ, 5.0F);
         float distance = this.getDistance(entity);
         this.testLeash(distance);
         if (!this.leashAIActive) {
            this.tasks.addTask(2, this.leashMoveTowardsRestrictionAI);
            if (!this.isStrongSwimmer()) {
               this.setPathPriority(PathNodeType.WATER, 0.0F);
            }

            this.leashAIActive = true;
         }

         if (distance > 4.0F) {
            this.getNavigator().tryMoveToEntityLiving(entity, 1.0D);
         }

         if (distance > 6.0F) {
            double d0 = (entity.posX - this.posX) / (double)distance;
            double d1 = (entity.posY - this.posY) / (double)distance;
            double d2 = (entity.posZ - this.posZ) / (double)distance;
            this.motionX += d0 * Math.abs(d0) * 0.4D;
            this.motionY += d1 * Math.abs(d1) * 0.4D;
            this.motionZ += d2 * Math.abs(d2) * 0.4D;
         }

         if (distance > 10.0F) {
            this.clearLeashed(true, true);
         }
      } else if (!this.getLeashed() && this.leashAIActive) {
         this.leashAIActive = false;
         this.tasks.removeTask(this.leashMoveTowardsRestrictionAI);
         if (!this.isStrongSwimmer()) {
            this.setPathPriority(PathNodeType.WATER, PathNodeType.WATER.getPriority());
         }

         this.detachHome();
      }

   }

   public boolean isPushedByWater() {
      return !this.isStrongSwimmer() && !this.isBoss();
   }

   public boolean isMoving() {
      if (!this.useDirectNavigator()) {
         return this.getNavigator().getPath() != null;
      } else {
         return !this.directNavigator.atTargetPosition();
      }
   }

   public boolean canBePushed() {
      return super.canBePushed();
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return false;
   }

   public void testLeash(float distance) {
   }

   public void setAIMoveSpeed(float speed) {
      super.setAIMoveSpeed(speed * this.getAISpeedModifier());
   }

   public float getAISpeedModifier() {
      return !this.canWalk() && !this.isInWater() && !this.isFlying() ? 0.1F : 1.0F;
   }

   public double getFallingMod() {
      return 1.0D;
   }

   protected float getWaterSlowDown() {
      return !this.isPushedByWater() ? 1.0F : 0.8F;
   }

   public void leap(double distance, double leapHeight) {
      if (!this.isFlying()) {
         this.playJumpSound();
      }

      float yaw = this.rotationYaw;
      float pitch = this.rotationPitch;
      double angle = Math.toRadians((double)yaw);
      double xAmount = -Math.sin(angle);
      double yAmount = leapHeight;
      double zAmount = Math.cos(angle);
      if (this.isFlying()) {
         yAmount = Math.sin(Math.toRadians((double)pitch)) * distance + this.motionY * 0.2D;
      }

      this.addVelocity(xAmount * distance + this.motionX * 0.2D, yAmount, zAmount * distance + this.motionZ * 0.2D);
      ForgeHooks.onLivingJump(this);
   }

   public void leap(float range, double leapHeight, Entity target) {
      if (target != null) {
         this.leap(range, leapHeight, target.getPosition());
      }
   }

   public void leap(float range, double leapHeight, BlockPos targetPos) {
      if (targetPos != null) {
         if (!this.isFlying()) {
            this.playJumpSound();
         }

         double distance = targetPos.distanceSq(this.getPosition());
         if (distance > 4.0D && distance <= (double)(range * range)) {
            double xDist = (double)(targetPos.getX() - this.getPosition().getX());
            double zDist = (double)(targetPos.getZ() - this.getPosition().getZ());
            if (xDist == 0.0D) {
               xDist = 0.05D;
            }

            if (zDist == 0.0D) {
               zDist = 0.05D;
            }

            double xzDist = (double)MathHelper.sqrt(xDist * xDist + zDist * zDist);
            this.addVelocity(xDist / xzDist * 0.5D * 0.8D + this.motionX * 0.2D, leapHeight, zDist / xzDist * 0.5D * 0.8D + this.motionZ * 0.2D);
            ForgeHooks.onLivingJump(this);
         }

      }
   }

   public void strafe(double distance, double leapHeight) {
      boolean opposite = false;
      if (distance < 0.0D) {
         distance = -distance;
         opposite = true;
      }

      float yaw = this.rotationYaw + (opposite ? -90.0F : 90.0F);
      float pitch = this.rotationPitch;
      double angle = Math.toRadians((double)yaw);
      double xAmount = -Math.sin(angle);
      double yAmount = leapHeight;
      double zAmount = Math.cos(angle);
      if (this.isFlying()) {
         yAmount = Math.sin(Math.toRadians((double)pitch)) * distance + this.motionY * 0.2D;
      }

      this.addVelocity(xAmount * distance + this.motionX * 0.2D, yAmount, zAmount * distance + this.motionZ * 0.2D);
   }

   public boolean isLookingAtMe(Entity targetEntity) {
      if (targetEntity == null) {
         return false;
      } else {
         Vec3d targetViewVector = targetEntity.getLook(1.0F).normalize();
         Vec3d distance = new Vec3d(this.posX - targetEntity.posX, this.posY + (double)this.getEyeHeight() - (targetEntity.posY + (double)targetEntity.getEyeHeight()), this.posZ - targetEntity.posZ);
         double distanceStraight = distance.lengthVector();
         distance = distance.normalize();
         double lookDistance = targetViewVector.dotProduct(distance);
         double lookRange = 1.5D;
         double comparison = 1.0D - lookRange / distanceStraight;
         if (!(targetEntity instanceof EntityPlayer)) {
            return lookDistance > comparison;
         } else {
            return lookDistance > comparison && ((EntityPlayer)targetEntity).canEntityBeSeen(this);
         }
      }
   }

   public void setHome(int x, int y, int z, float distance) {
      this.setHomePosition(x, y, z);
      this.setHomeDistanceMax(distance);
   }

   public void setHomePosition(int x, int y, int z) {
      this.homePosition = new BlockPos(x, y, z);
   }

   public void setHomeDistanceMax(float distance) {
      this.homeDistanceMax = distance;
   }

   public BlockPos getHomePosition() {
      return this.homePosition;
   }

   public float getHomeDistanceMax() {
      return this.homeDistanceMax;
   }

   public void detachHome() {
      this.setHomeDistanceMax(-1.0F);
   }

   public boolean hasHome() {
      return this.getHomePosition() != null && this.getHomeDistanceMax() >= 0.0F;
   }

   public boolean positionNearHome(int x, int y, int z) {
      if (!this.hasHome()) {
         return true;
      } else {
         return this.getDistanceFromHome(x, y, z) < (double)(this.getHomeDistanceMax() * this.getHomeDistanceMax());
      }
   }

   public double getDistanceFromHome(int x, int y, int z) {
      return !this.hasHome() ? 0.0D : this.homePosition.getDistance(x, y, z);
   }

   public double getDistanceFromHome() {
      return this.homePosition.getDistance((int)this.posX, (int)this.posY, (int)this.posZ);
   }

   public boolean hasArenaCenter() {
      return this.getArenaCenter() != null;
   }

   public void setArenaCenter(BlockPos pos) {
      this.arenaCenter = pos;
   }

   public BlockPos getArenaCenter() {
      return this.arenaCenter;
   }

   public BlockPos getWanderPosition(BlockPos wanderPosition) {
      return wanderPosition;
   }

   public int restrictYHeightFromGround(BlockPos coords, int minY, int maxY) {
      int groundY = this.getGroundY(coords);
      int airYMax = Math.min(this.getAirY(coords), groundY + maxY);
      int airYMin = Math.min(airYMax, groundY + minY);
      return airYMin >= airYMax ? airYMin : airYMin + this.getRNG().nextInt(airYMax - airYMin);
   }

   public int getGroundY(BlockPos pos) {
      int y = pos.getY();
      if (y <= 0) {
         return 0;
      } else {
         IBlockState startBlock = this.getEntityWorld().getBlockState(pos);
         if (startBlock.getBlock().isAir(startBlock, this.getEntityWorld(), pos)) {
            for(int possibleGroundY = Math.max(0, y - 1); possibleGroundY >= 0; y = possibleGroundY--) {
               IBlockState possibleGroundBlock = this.getEntityWorld().getBlockState(new BlockPos(pos.getX(), possibleGroundY, pos.getZ()));
               if (!possibleGroundBlock.getBlock().isAir(possibleGroundBlock, this.getEntityWorld(), new BlockPos(pos.getX(), possibleGroundY, pos.getZ()))) {
                  break;
               }
            }
         }

         return y;
      }
   }

   public int getAirY(BlockPos pos) {
      int y = pos.getY();
      int yMax = this.getEntityWorld().provider.getActualHeight() - 1;
      if (y >= yMax) {
         return yMax;
      } else if (this.getEntityWorld().canBlockSeeSky(pos)) {
         return yMax;
      } else {
         IBlockState startBlock = this.getEntityWorld().getBlockState(pos);
         if (startBlock.getBlock().isAir(startBlock, this.getEntityWorld(), pos)) {
            for(int possibleAirY = Math.min(yMax, y + 1); possibleAirY <= yMax; y = possibleAirY++) {
               IBlockState possibleGroundBlock = this.getEntityWorld().getBlockState(new BlockPos(pos.getX(), possibleAirY, pos.getZ()));
               if (!possibleGroundBlock.getBlock().isAir(possibleGroundBlock, this.getEntityWorld(), new BlockPos(pos.getX(), possibleAirY, pos.getZ()))) {
                  break;
               }
            }
         }

         return y;
      }
   }

   public int getWaterSurfaceY(BlockPos pos) {
      int y = pos.getY();
      if (y <= 0) {
         return 0;
      } else {
         int yMax = this.getEntityWorld().provider.getActualHeight() - 1;
         if (y >= yMax) {
            return yMax;
         } else {
            int yLimit = 24;
            yMax = Math.min(yMax, y + yLimit);
            IBlockState startBlock = this.getEntityWorld().getBlockState(pos);
            if (startBlock.getMaterial() == Material.WATER) {
               int possibleSurfaceY;
               for(possibleSurfaceY = y + 1; possibleSurfaceY <= yMax; ++possibleSurfaceY) {
                  IBlockState possibleSurfaceBlock = this.getEntityWorld().getBlockState(new BlockPos(pos.getX(), possibleSurfaceY, pos.getZ()));
                  if (possibleSurfaceBlock.getBlock().isAir(possibleSurfaceBlock, this.getEntityWorld(), new BlockPos(pos.getX(), possibleSurfaceY, pos.getZ()))) {
                     return possibleSurfaceY;
                  }

                  if (possibleSurfaceBlock.getMaterial() != Material.WATER) {
                     return possibleSurfaceY - 1;
                  }
               }

               return Math.max(possibleSurfaceY - 1, y);
            } else {
               return y;
            }
         }
      }
   }

   protected void setSize(float width, float height) {
      width = (float)((double)width * (double)((float)this.sizeScale) * this.creatureInfo.sizeScale);
      height = (float)((double)height * (double)((float)this.sizeScale) * this.creatureInfo.sizeScale);
      super.setSize(width, height);
      this.hitAreas = (EntityHitArea[][][])null;
      if (!this.getEntityWorld().isRemote && this.getNavigator() != null && this.getNavigator().getNodeProcessor() instanceof ICreatureNodeProcessor) {
         ((ICreatureNodeProcessor)this.getNavigator().getNodeProcessor()).updateEntitySize(this);
      }

   }

   public void updateSize() {
      this.setSize(Math.max(this.setWidth, 0.5F), Math.max(this.setHeight, 0.5F));
   }

   public void setSizeScale(double scale) {
      this.sizeScale = scale;
      this.updateSize();
   }

   public double getRenderScale() {
      return this.sizeScale * this.creatureInfo.sizeScale;
   }

   public boolean canAttackClass(Class targetClass) {
      return CreatureManager.getInstance().config.mobsAttackVillagers || targetClass != EntityVillager.class;
   }

   public boolean canAttackEntity(EntityLivingBase targetEntity) {
      if (!CreatureManager.getInstance().config.mobsAttackVillagers && targetEntity instanceof EntityVillager) {
         return false;
      } else if (this.getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL && targetEntity instanceof EntityPlayer) {
         return false;
      } else if (!Targeting.isValidTarget(this, targetEntity)) {
         return false;
      } else {
         if (targetEntity instanceof EntityPlayer) {
            EntityPlayer targetPlayer = (EntityPlayer)targetEntity;
            if (targetPlayer.capabilities.disableDamage) {
               return false;
            }
         }

         if (this.isOnSameTeam(targetEntity)) {
            return false;
         } else {
            CreatureRelationshipEntry relationshipEntry = this.relationships.getEntry(targetEntity);
            if (relationshipEntry != null && !relationshipEntry.canAttack()) {
               return false;
            } else {
               if (targetEntity instanceof BaseCreatureEntity) {
                  BaseCreatureEntity targetCreature = (BaseCreatureEntity)targetEntity;
                  if (!this.canAttackOwnSpecies() && targetCreature.getClass() == this.getClass() && !targetCreature.isTamed()) {
                     return false;
                  }

                  if (targetCreature.getMasterTarget() == this) {
                     return false;
                  }

                  if (!(this instanceof IGroupBoss) && !this.isTamed()) {
                     if (targetCreature.isBoss()) {
                        return false;
                     }

                     if (this.isRareVariant()) {
                        return false;
                     }
                  }
               }

               return true;
            }
         }
      }
   }

   public boolean canAttackOwnSpecies() {
      return false;
   }

   public double getPhysicalRange() {
      double range = (double)this.width + 1.5D;
      if (this.isFlying()) {
         range += this.getFlightOffset();
      }

      return range * range;
   }

   public double getMeleeAttackRange(EntityLivingBase attackTarget, double additionalReach) {
      double creatureRange = this.getPhysicalRange();
      double targetSize = 1.0D;
      if (attackTarget != null) {
         targetSize = (double)((attackTarget.width + 1.0F) * (attackTarget.width + 1.0F));
      }

      return creatureRange + targetSize + additionalReach;
   }

   public boolean shouldCreatureGroupRevenge(EntityLivingBase target) {
      boolean shouldRevenge = this.creatureInfo.getGroups().isEmpty();
      boolean shouldPackHunt = false;

      for(CreatureGroup group : this.creatureInfo.getGroups()) {
         if (group.shouldRevenge(target)) {
            shouldRevenge = true;
         }

         if (group.shouldPackHunt(target)) {
            shouldPackHunt = true;
         }
      }

      boolean canPackHunt = shouldPackHunt && this.isInPack();
      return shouldRevenge || canPackHunt;
   }

   public boolean shouldCreatureGroupHunt(EntityLivingBase target) {
      boolean shouldFlee = false;
      boolean shouldHunt = false;
      boolean shouldPackHunt = false;

      for(CreatureGroup group : this.creatureInfo.getGroups()) {
         if (group.shouldFlee(target)) {
            shouldFlee = true;
         }

         if (group.shouldHunt(target)) {
            shouldHunt = true;
         }

         if (group.shouldPackHunt(target)) {
            shouldPackHunt = true;
         }
      }

      boolean canPackHunt = shouldPackHunt && this.isInPack();
      if (shouldFlee && !canPackHunt) {
         return false;
      } else {
         return shouldHunt || shouldPackHunt;
      }
   }

   public boolean shouldCreatureGroupFlee(EntityLivingBase target) {
      if (!this.isBoss() && !this.isRareVariant() && !this.isTamed()) {
         boolean shouldFlee = false;
         boolean shouldPackHunt = false;

         for(CreatureGroup group : this.creatureInfo.getGroups()) {
            if (group.shouldFlee(target)) {
               shouldFlee = true;
            }

            if (group.shouldPackHunt(target)) {
               shouldPackHunt = true;
            }
         }

         boolean canPackHunt = shouldPackHunt && this.isInPack();
         return shouldFlee && !canPackHunt;
      } else {
         return false;
      }
   }

   public EntityLivingBase getMasterAttackTarget() {
      if (this.masterTarget == null) {
         return null;
      } else {
         return this.masterTarget instanceof EntityLiving ? ((EntityLiving)this.masterTarget).getAttackTarget() : null;
      }
   }

   public EntityLivingBase getParentAttackTarget() {
      if (this.parentTarget == null) {
         return null;
      } else if (this.parentTarget instanceof BaseCreatureEntity) {
         return ((BaseCreatureEntity)this.parentTarget).getAttackTarget();
      } else {
         return this.parentTarget instanceof EntityCreature ? ((EntityCreature)this.parentTarget).getAttackTarget() : null;
      }
   }

   public void setRevengeTarget(EntityLivingBase entityLivingBase) {
      super.setRevengeTarget(entityLivingBase);
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (this.isBlocking() && !this.canAttackWhileBlocking()) {
         return false;
      } else if (!this.attackEntityAsMob(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntityLivingBase) {
            EntityLivingBase livingTarget = (EntityLivingBase)target;
            if (!livingTarget.isActiveItemStackBlocking() || !livingTarget.getActiveItemStack().getItem().isShield(livingTarget.getActiveItemStack(), livingTarget)) {
               if (this.spreadFire && this.isBurning() && (double)this.rand.nextFloat() < this.creatureStats.getEffect()) {
                  target.setFire(this.getEffectDuration(4) / 20);
               }

               if (this.creatureStats.getAmplifier() >= 0.0D) {
                  this.applyDebuffs(livingTarget, 1, 1);
               }
            }
         }

         this.triggerAttackCooldown();
         this.playAttackSound();
         return true;
      }
   }

   public boolean attackHitscan(Entity target, double damageScale) {
      if (this.isBlocking() && !this.canAttackWhileBlocking()) {
         return false;
      } else if (target != null && this.canEntityBeSeen(target)) {
         if (!this.attackEntityAsMob(target, damageScale)) {
            return false;
         } else {
            if (target instanceof EntityLivingBase) {
               EntityLivingBase livingTarget = (EntityLivingBase)target;
               if ((!livingTarget.isActiveItemStackBlocking() || !livingTarget.getActiveItemStack().getItem().isShield(livingTarget.getActiveItemStack(), livingTarget)) && this.creatureStats.getAmplifier() >= 0.0D) {
                  this.applyDebuffs(livingTarget, 1, 1);
               }
            }

            this.triggerAttackCooldown();
            this.playAttackSound();
            return true;
         }
      } else {
         return false;
      }
   }

   public void attackRanged(Entity target, float range) {
      if (!this.isBlocking() || this.canAttackWhileBlocking()) {
         this.triggerAttackCooldown();
         this.playAttackSound();
      }
   }

   public boolean doRangedDamage(Entity target, EntityThrowable projectile, float damage, boolean noPierce) {
      damage = (float)((double)damage * (this.creatureStats.getDamage() / 2.0D));
      double pierceDamage = noPierce ? 0.0D : this.creatureStats.getPierce();
      boolean success;
      if ((double)damage <= pierceDamage) {
         success = target.attackEntityFrom(this.getDamageSource((EntityDamageSource)DamageSource.causeThrownDamage(projectile, this).setDamageBypassesArmor(), false).setDamageIsAbsolute(), damage);
      } else {
         int hurtResistantTimeBefore = target.hurtResistantTime;
         if (pierceDamage > 0.0D) {
            target.attackEntityFrom(this.getDamageSource((EntityDamageSource)DamageSource.causeThrownDamage(projectile, this).setDamageBypassesArmor(), false).setDamageIsAbsolute(), (float)pierceDamage);
         }

         target.hurtResistantTime = hurtResistantTimeBefore;
         damage = (float)((double)damage - pierceDamage);
         success = target.attackEntityFrom(this.getDamageSource((EntityDamageSource)DamageSource.causeThrownDamage(projectile, this), false), damage);
      }

      if (success && target instanceof EntityLivingBase && this.creatureStats.getAmplifier() >= 0.0D) {
         this.applyDebuffs((EntityLivingBase)target, 1, 1);
      }

      return success;
   }

   public BaseProjectileEntity fireProjectile(String projectileName, Entity target, float range, float angle, Vec3d offset, float velocity, float scale, float inaccuracy) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(projectileName);
      return projectileInfo == null ? null : this.fireProjectile(projectileInfo.createProjectile(this.getEntityWorld(), this), target, range, angle, offset, velocity, scale, inaccuracy);
   }

   public BaseProjectileEntity fireProjectile(Class projectileClass, Entity target, float range, float angle, Vec3d offset, float velocity, float scale, float inaccuracy) {
      BaseProjectileEntity projectile = null;

      try {
         projectile = (BaseProjectileEntity)LycanitesClasses.create(projectileClass, this.getEntityWorld(), this);
      } catch (Exception var11) {
         LycanitesMobs.logWarning("", "Unable to create a projectile from the class: " + projectileClass);
      }

      return this.fireProjectile(projectile, target, range, angle, offset, velocity, scale, inaccuracy);
   }

   public BaseProjectileEntity fireProjectile(BaseProjectileEntity projectile, Entity target, float range, float angle, Vec3d offset, float velocity, float scale, float inaccuracy) {
      if (projectile == null) {
         return null;
      } else {
         projectile.posX += offset.x * this.sizeScale;
         projectile.posY -= (double)(this.height / 4.0F) + offset.y * this.sizeScale;
         projectile.posZ += offset.z * this.sizeScale;
         projectile.setProjectileScale(scale);
         Vec3d facing = this.getFacingPositionDouble(this.posX, this.posY, this.posZ, (double)range, (double)angle);
         double distanceX = facing.x - this.posX;
         double distanceZ = facing.z - this.posZ;
         double distanceXZ = (double)MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ) * 0.0D;
         double distanceY = distanceXZ;
         if (target != null) {
            double targetX = target.posX - this.posX;
            double targetZ = target.posZ - this.posZ;
            double newX = targetX * Math.cos((double)angle) - targetZ * Math.sin((double)angle);
            double newY = targetX * Math.sin((double)angle) + targetZ * Math.cos((double)angle);
            targetX = newX + this.posX;
            targetZ = newY + this.posZ;
            distanceX = targetX - this.posX;
            double var10001 = (double)target.height;
            distanceY = target.getEntityBoundingBox().minY + var10001 * 0.5D - projectile.posY;
            distanceZ = targetZ - this.posZ;
         }

         projectile.shoot(distanceX, distanceY, distanceZ, velocity, inaccuracy);
         this.getEntityWorld().spawnEntity(projectile);
         if (projectile.getLaunchSound() != null) {
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
         }

         return projectile;
      }
   }

   public byte getAttackPhase() {
      return this.getByteFromDataManager(ATTACK_PHASE);
   }

   public void setAttackPhase(byte setAttackPhase) {
      this.attackPhase = setAttackPhase;
   }

   public void nextAttackPhase() {
      if (++this.attackPhase > this.attackPhaseMax - 1) {
         this.attackPhase = 0;
      }

   }

   public boolean attackEntityAsMob(Entity target, double damageScale) {
      if (!this.isEntityAlive()) {
         return false;
      } else if (target == null) {
         return false;
      } else if (!this.canEntityBeSeen(target)) {
         return false;
      } else {
         float damage = this.getAttackDamage(damageScale);
         int enchantmentKnockback = 0;
         boolean targetIsShielding = false;
         if (target instanceof EntityPlayer) {
            EntityPlayer targetPlayer = (EntityPlayer)target;
            ItemStack playerActiveItemStack = targetPlayer.getActiveItemStack();
            targetIsShielding = targetPlayer.isActiveItemStackBlocking() && playerActiveItemStack.getItem().isShield(playerActiveItemStack, targetPlayer);
         }

         double pierceDamage = this.creatureStats.getPierce();
         if (targetIsShielding) {
            pierceDamage = 0.0D;
         }

         boolean attackSuccess;
         if ((double)damage <= pierceDamage) {
            attackSuccess = target.attackEntityFrom(this.getDamageSource((EntityDamageSource)null, false).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
         } else {
            if (pierceDamage > 0.0D) {
               int hurtResistantTimeBefore = target.hurtResistantTime;
               target.attackEntityFrom(this.getDamageSource((EntityDamageSource)null, false).setDamageBypassesArmor().setDamageIsAbsolute(), (float)pierceDamage);
               target.hurtResistantTime = hurtResistantTimeBefore;
               damage = (float)((double)damage - pierceDamage);
            }

            attackSuccess = target.attackEntityFrom(this.getDamageSource((EntityDamageSource)null, false), damage);
         }

         if (attackSuccess) {
            this.setLastAttackedEntity(target);
            if (enchantmentKnockback > 0) {
               target.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F) * (float)enchantmentKnockback * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F) * (float)enchantmentKnockback * 0.5F));
               this.motionX *= 0.6D;
               this.motionZ *= 0.6D;
            }

            int fireEnchantDuration = EnchantmentHelper.getFireAspectModifier(this);
            if (fireEnchantDuration > 0) {
               target.setFire(fireEnchantDuration * 4);
            }

            if (target instanceof EntityPlayer && this.canInteruptShields(true)) {
               EntityPlayer targetPlayer = (EntityPlayer)target;
               if (targetIsShielding && this.canInteruptShields(false)) {
                  ItemStack playerActiveItemStack = targetPlayer.isHandActive() ? targetPlayer.getActiveItemStack() : ItemStack.EMPTY;
                  targetPlayer.getCooldownTracker().setCooldown(playerActiveItemStack.getItem(), 100);
                  this.world.setEntityState(targetPlayer, (byte)30);
               }
            }

            this.applyEnchantments(this, target);
         }

         return attackSuccess;
      }
   }

   public boolean canTargetBlockDamageSource(EntityLivingBase target, DamageSource damageSource) {
      if (!damageSource.isUnblockable() && target.isActiveItemStackBlocking()) {
         Vec3d vec3d = damageSource.getDamageLocation();
         if (vec3d != null) {
            Vec3d vec3d1 = target.getLook(1.0F);
            Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(target.posX, target.posY, target.posZ)).normalize();
            vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
            if (vec3d2.dotProduct(vec3d1) < 0.0D) {
               return true;
            }
         }
      }

      return false;
   }

   public float getAttackDamage(double damageScale) {
      float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
      damage = (float)((double)damage * damageScale);
      return damage;
   }

   public DamageSource getDamageSource(EntityDamageSource nestedDamageSource, boolean playerCredit) {
      return (DamageSource)(nestedDamageSource != null ? nestedDamageSource : DamageSource.causeMobDamage(this));
   }

   public boolean canInteruptShields(boolean checkAbility) {
      return false;
   }

   protected void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         damageAmount = ForgeHooks.onLivingHurt(this, damageSrc, damageAmount);
         if (damageAmount > 0.0F) {
            damageAmount = damageAmount * this.getDamageModifier(damageSrc);
            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            damageAmount = this.getDamageAfterDefense(damageAmount);
            if ((this.isBoss() || this.isRareVariant()) && !(damageSrc.getTrueSource() instanceof EntityPlayer)) {
               damageAmount *= 0.25F;
            }

            float damageBeforeAbsorption = damageAmount;
            damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageBeforeAbsorption - damageAmount));
            damageAmount = ForgeHooks.onLivingDamage(this, damageSrc, damageAmount);
            if (damageAmount != 0.0F) {
               float healthBeforeDamage = this.getHealth();
               this.getCombatTracker().trackDamage(damageSrc, healthBeforeDamage, damageAmount);
               this.setHealth(healthBeforeDamage - damageAmount);
               this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
            }

         }
      }
   }

   public boolean attackEntityFrom(DamageSource damageSrc, float damageAmount) {
      if (this.getEntityWorld().isRemote) {
         return false;
      } else if (this.isEntityInvulnerable(damageSrc)) {
         return false;
      } else if (!this.isDamageTypeApplicable(damageSrc.getDamageType(), damageSrc, damageAmount)) {
         return false;
      } else if (!this.isDamageEntityApplicable(damageSrc.getTrueSource())) {
         return false;
      } else {
         if (this.dropsRequirePlayerDamage && damageSrc.getTrueSource() instanceof EntityPlayer) {
            if (damageSrc instanceof MinionEntityDamageSource) {
               MinionEntityDamageSource minionEntityDamageSource = (MinionEntityDamageSource)damageSrc;
               if (minionEntityDamageSource.getMinion() instanceof TameableCreatureEntity) {
                  TameableCreatureEntity tameableCreatureEntity = (TameableCreatureEntity)minionEntityDamageSource.getMinion();
                  if (!tameableCreatureEntity.fromSummoningPedestal) {
                     this.dropsRequirePlayerDamage = false;
                  }
               }
            } else {
               this.dropsRequirePlayerDamage = false;
            }
         }

         if (super.attackEntityFrom(damageSrc, damageAmount)) {
            this.onDamage(damageSrc, damageAmount);
            if (this.isBoss() && this.playerTargets != null && damageSrc.getTrueSource() != null && damageSrc.getTrueSource() instanceof EntityPlayer && !this.playerTargets.contains(damageSrc.getTrueSource())) {
               this.playerTargets.add((EntityPlayer)damageSrc.getTrueSource());
            }

            Entity entity = damageSrc.getImmediateSource();
            if (entity instanceof EntityThrowable) {
               entity = ((EntityThrowable)entity).getThrower();
            }

            if (entity instanceof EntityLivingBase && this.getRider() != entity && this.getRidingEntity() != entity && entity != this) {
               this.setRevengeTarget((EntityLivingBase)entity);
               CreatureRelationshipEntry relationshipEntry = this.relationships.getOrCreateEntry(entity);
               int reputationAmount = 50 + this.getRNG().nextInt(50);
               relationshipEntry.decreaseReputation(reputationAmount);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean attackEntityFromArea(EntityHitArea entityHitArea, DamageSource damageSrc, float damage) {
      return this.attackEntityFrom(damageSrc, damage);
   }

   public float getDamageAfterDefense(float damage) {
      float defense = (float)this.creatureStats.getDefense();
      float minDamage = 0.0F;
      if (this.isBlocking()) {
         if (defense <= 0.0F) {
            defense = 1.0F;
         }

         defense *= (float)this.getBlockingMultiplier();
      }

      damage = Math.max(damage - defense, 1.0F);
      if (this.damageMax > 0) {
         damage = Math.min(damage, (float)this.damageMax);
      }

      return Math.max(damage, minDamage);
   }

   public void onDamage(DamageSource damageSrc, float damage) {
      this.damageTakenThisSec += damage;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return 1.0F;
   }

   public void onDeath(DamageSource damageSource) {
      super.onDeath(damageSource);
      if (this.dead) {
         if (this.isBossAlways()) {
            ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(this.getEntityWorld());
            extendedWorld.bossRemoved(this);
         }

         if (!this.getEntityWorld().isRemote) {
            if (!this.isBoundPet()) {
               this.inventory.dropInventory();
            }

            if (damageSource.getTrueSource() != null && damageSource.getTrueSource() instanceof EntityPlayer) {
               try {
                  EntityPlayer player = (EntityPlayer)damageSource.getTrueSource();
                  player.addStat(ObjectManager.getStat(this.creatureInfo.getName() + ".kill"), 1);
                  if (!this.isTamed()) {
                     ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
                     if (extendedPlayer != null) {
                        extendedPlayer.studyCreature(this, CreatureManager.getInstance().config.creatureKillKnowledge, false, false);
                     }
                  }
               } catch (Exception var4) {
                  ;
               }
            }
         }

         if (this.getMasterTarget() != null && this.getMasterTarget() instanceof BaseCreatureEntity) {
            ((BaseCreatureEntity)this.getMasterTarget()).onMinionDeath(this, damageSource);
         }

      }
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return !this.isTamed() && !this.isMinion() && !this.dropsRequirePlayerDamage ? super.getExperiencePoints(player) : 0;
   }

   public boolean isAggressive() {
      return this.extraMobBehaviour != null && this.extraMobBehaviour.aggressiveOverride ? true : this.isAggressiveByDefault;
   }

   public boolean isHostileTo(Entity target) {
      if (target == null) {
         return false;
      } else if (this.hostileTargetClasses.contains(target.getClass())) {
         return true;
      } else {
         for(CreatureGroup group : this.creatureInfo.getGroups()) {
            if (group.shouldHunt(target) || group.shouldPackHunt(target)) {
               return true;
            }
         }

         return false;
      }
   }

   public void setHostileTo(Class<? extends Entity> targetClass) {
      if (this.hostileTargetClasses == null) {
         this.hostileTargetClasses = new ArrayList();
      }

      if (!this.hostileTargetClasses.contains(targetClass)) {
         this.hostileTargetClasses.add(targetClass);
      }
   }

   public boolean isProtective(Entity entity) {
      return entity.getClass() == this.getClass();
   }

   public boolean hasAttackTarget() {
      if (!this.getEntityWorld().isRemote) {
         return this.getAttackTarget() != null;
      } else {
         return (this.getByteFromDataManager(TARGET) & BaseCreatureEntity.TARGET_BITS.ATTACK.id) > 0;
      }
   }

   public EntityLivingBase getMasterTarget() {
      return this.masterTarget;
   }

   public void setMasterTarget(EntityLivingBase setTarget) {
      this.masterTarget = setTarget;
   }

   public boolean hasMaster() {
      if (!this.getEntityWorld().isRemote) {
         return this.getMasterTarget() != null;
      } else {
         return (this.getByteFromDataManager(TARGET) & BaseCreatureEntity.TARGET_BITS.MASTER.id) > 0;
      }
   }

   public EntityLivingBase getParentTarget() {
      return this.parentTarget;
   }

   public void setParentTarget(EntityLivingBase setTarget) {
      this.parentTarget = setTarget;
   }

   public boolean hasParent() {
      if (!this.getEntityWorld().isRemote) {
         return this.getParentTarget() != null;
      } else {
         return (this.getByteFromDataManager(TARGET) & BaseCreatureEntity.TARGET_BITS.PARENT.id) > 0;
      }
   }

   public EntityLivingBase getAvoidTarget() {
      return this.avoidTarget;
   }

   public void setAvoidTarget(EntityLivingBase setTarget) {
      this.currentFleeTime = this.fleeTime;
      this.avoidTarget = setTarget;
   }

   public boolean hasAvoidTarget() {
      if (!this.getEntityWorld().isRemote) {
         return this.getAvoidTarget() != null;
      } else {
         return (this.getByteFromDataManager(TARGET) & BaseCreatureEntity.TARGET_BITS.AVOID.id) > 0;
      }
   }

   public EntityLivingBase getFixateTarget() {
      return this.fixateTarget;
   }

   public void setFixateTarget(EntityLivingBase target) {
      this.fixateTarget = target;
   }

   public boolean hasFixateTarget() {
      return this.getFixateTarget() != null;
   }

   public EntityLivingBase getPerchTarget() {
      return this.perchTarget;
   }

   public void setPerchTarget(EntityLivingBase setTarget) {
      this.perchTarget = setTarget;
   }

   public boolean hasPerchTarget() {
      if (!this.getEntityWorld().isRemote) {
         return this.getPerchTarget() != null;
      } else {
         return (this.getByteFromDataManager(TARGET) & BaseCreatureEntity.TARGET_BITS.PERCH.id) > 0;
      }
   }

   public boolean rollAttackTargetChance(EntityLivingBase target) {
      return true;
   }

   public boolean canEntityBeSeen(Entity target) {
      return super.canEntityBeSeen(target);
   }

   public Entity getOwner() {
      return null;
   }

   public EaglercraftUUID getOwnerId() {
      return null;
   }

   public EntityLivingBase getRider() {
      return this.getControllingPassenger() instanceof EntityLivingBase ? (EntityLivingBase)this.getControllingPassenger() : null;
   }

   public void setRiderTarget(Entity setTarget) {
      this.addPassenger(setTarget);
   }

   public boolean hasRiderTarget() {
      if (!this.getEntityWorld().isRemote) {
         return this.getControllingPassenger() != null;
      } else {
         return (this.getByteFromDataManager(TARGET) & BaseCreatureEntity.TARGET_BITS.RIDER.id) > 0;
      }
   }

   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
   }

   protected boolean canBeRidden(Entity entity) {
      return this.isBoss() ? false : super.canBeRidden(entity);
   }

   public boolean canBeSteered() {
      return false;
   }

   public boolean canBeTargetedBy(EntityLivingBase entity) {
      if (this.isBoss() && entity instanceof BaseCreatureEntity) {
         BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
         if (entityCreature instanceof TameableCreatureEntity) {
            TameableCreatureEntity entityTameable = (TameableCreatureEntity)entity;
            if (entityTameable.getPlayerOwner() != null) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean isInPack() {
      return this.creatureInfo.packSize <= 1 || this.countAllies(10.0D) >= this.creatureInfo.packSize;
   }

   public BlockPos getFacingPosition(double distance) {
      return this.getFacingPosition(this, distance, 0.0D);
   }

   public BlockPos getFacingPosition(Entity entity, double distance, double angleOffset) {
      return this.getFacingPosition(entity.posX, entity.posY, entity.posZ, distance, (double)entity.rotationYaw + angleOffset);
   }

   public BlockPos getFacingPosition(double x, double y, double z, double distance, double angle) {
      angle = Math.toRadians(angle);
      double xAmount = -Math.sin(angle);
      double zAmount = Math.cos(angle);
      return new BlockPos(x + distance * xAmount, y, z + distance * zAmount);
   }

   public Vec3d getFacingPositionDouble(double x, double y, double z, double distance, double angle) {
      if (distance == 0.0D) {
         distance = 1.0D;
      }

      angle = Math.toRadians(angle);
      double xAmount = -Math.sin(angle);
      double zAmount = Math.cos(angle);
      return new Vec3d(x + distance * xAmount, y, z + distance * zAmount);
   }

   public boolean isTamed() {
      return false;
   }

   public boolean isHostile() {
      return this.isAggressive();
   }

   public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
      if (forSpawnCount) {
         if (this.isMinion()) {
            return false;
         } else {
            for(EnumCreatureType creatureType : this.creatureInfo.creatureSpawn.vanillaSpawnerTypes) {
               if (creatureType == type) {
                  return true;
               }
            }

            return false;
         }
      } else if (type.getCreatureClass() == IMob.class) {
         try {
            return this.isHostile();
         } catch (Exception var5) {
            return true;
         }
      } else {
         return type.getCreatureClass().isAssignableFrom(this.getClass());
      }
   }

   public boolean canMove() {
      return !this.isBlocking();
   }

   public boolean canWalk() {
      return true;
   }

   public boolean canWade() {
      return true;
   }

   public boolean canFloat() {
      return !this.canBreatheUnderwater();
   }

   public boolean canDive() {
      return this.canBreatheUnderwater();
   }

   public boolean isStrongSwimmer() {
      return this.extraMobBehaviour != null && this.extraMobBehaviour.swimmingOverride;
   }

   public boolean canJump() {
      return !this.isBlocking();
   }

   public boolean canClimb() {
      return false;
   }

   public boolean isFlying() {
      return this.extraMobBehaviour != null && this.extraMobBehaviour.flightOverride;
   }

   public int getFlyingHeight() {
      return !this.isFlying() ? 20 : 0;
   }

   public boolean isSafeToLand() {
      if (this.onGround) {
         return true;
      } else if (this.getEntityWorld().getBlockState(this.getPosition().down()).getMaterial().isSolid()) {
         return true;
      } else {
         return this.getEntityWorld().getBlockState(this.getPosition().down(2)).getMaterial().isSolid();
      }
   }

   public double getFlightOffset() {
      return 0.0D;
   }

   public boolean isCurrentlyFlying() {
      return this.isFlying();
   }

   public boolean canBeTempted() {
      if (!this.isRareVariant() && !this.spawnedAsBoss) {
         if (this.creatureInfo.isFarmable()) {
            return true;
         } else {
            return this.isInPack() && !CreatureManager.getInstance().config.packTreatLuring ? false : this.creatureInfo.isTameable();
         }
      } else {
         return false;
      }
   }

   public void onEat() {
   }

   public boolean canStealth() {
      return this.extraMobBehaviour != null && this.extraMobBehaviour.stealthOverride;
   }

   public float getStealth() {
      return this.getFloatFromDataManager(STEALTH);
   }

   public void setStealth(float setStealth) {
      setStealth = Math.min(setStealth, 1.0F);
      setStealth = Math.max(setStealth, 0.0F);
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(STEALTH, Float.valueOf(setStealth));
      }

   }

   public boolean isStealthed() {
      return this.getStealth() >= 1.0F;
   }

   public void startStealth() {
   }

   public void onStealth() {
      if (!this.getEntityWorld().isRemote && this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityLiving && ((EntityLiving)this.getAttackTarget()).getAttackTarget() != null) {
         ((EntityLiving)this.getAttackTarget()).setAttackTarget((EntityLivingBase)null);
      }

   }

   public boolean isOnLadder() {
      if (!this.isFlying() && (!this.isStrongSwimmer() || !this.isInWater())) {
         if (this.canClimb()) {
            return (this.getByteFromDataManager(CLIMBING) & 1) != 0;
         } else {
            return super.isOnLadder();
         }
      } else {
         return false;
      }
   }

   public void setBesideClimbableBlock(boolean collided) {
      if (this.canClimb()) {
         byte climbing = this.getByteFromDataManager(CLIMBING);
         if (collided) {
            climbing = (byte)(climbing | 1);
         } else {
            climbing = (byte)(climbing & -2);
         }

         this.dataManager.set(CLIMBING, Byte.valueOf(climbing));
      }

   }

   public boolean isBesideClimbableBlock() {
      return (this.getByteFromDataManager(CLIMBING) & 1) != 0;
   }

   public void fall(float fallDistance, float damageMultiplier) {
      if (!this.isFlying()) {
         fallDistance = fallDistance - this.getFallResistance();
         if (this.getFallResistance() >= 100.0F) {
            fallDistance = 0.0F;
         }

         super.fall(fallDistance, damageMultiplier);
      }
   }

   protected void updateFallState(double y, boolean onGround, IBlockState state, BlockPos pos) {
      if (!this.isFlying()) {
         super.updateFallState(y, onGround, state, pos);
      }

   }

   public void setBlocking() {
      this.currentBlockingTime = this.blockingTime;
   }

   public boolean isBlocking() {
      if (this.getEntityWorld().isRemote) {
         return (this.getByteFromDataManager(ANIMATION_STATE) & BaseCreatureEntity.ANIMATION_STATE_BITS.BLOCKING.id) > 0;
      } else {
         return this.currentBlockingTime > 0;
      }
   }

   public boolean canAttackWhileBlocking() {
      return false;
   }

   public int getBlockingMultiplier() {
      return 4;
   }

   public boolean canPickupEntity(EntityLivingBase entity) {
      if (this.getPickupEntity() == entity) {
         return false;
      } else if (entity instanceof IGroupBoss) {
         return false;
      } else {
         if (entity instanceof EntityPlayer) {
            if (((EntityPlayer)entity).isCreative()) {
               return false;
            }

            if (((EntityPlayer)entity).isSpectator()) {
               return false;
            }
         }

         if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).hasPickupEntity()) {
            return false;
         } else if (CreatureManager.getInstance().creatureGroups.containsKey("boss") && ((CreatureGroup)CreatureManager.getInstance().creatureGroups.get("boss")).hasEntity(entity)) {
            return false;
         } else {
            boolean heavyTarget = entity instanceof IGroupHeavy || entity.height >= 4.0F || entity.width >= 4.0F;
            if (heavyTarget && !(this instanceof IGroupHeavy)) {
               return false;
            } else {
               ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
               if (extendedEntity == null) {
                  return false;
               } else if ((entity.getRidingEntity() == null || entity.getRidingEntity() instanceof EntityBoat || entity.getRidingEntity() instanceof EntityMinecart) && entity.getControllingPassenger() == null) {
                  if (ObjectManager.getEffect("weight") != null && entity.isPotionActive(ObjectManager.getEffect("weight"))) {
                     return false;
                  } else if (ObjectManager.getEffect("repulsion") != null && entity.isPotionActive(ObjectManager.getEffect("repulsion"))) {
                     return false;
                  } else {
                     return extendedEntity.pickedUpByEntity == null || extendedEntity.pickedUpByEntity instanceof FearEntity;
                  }
               } else {
                  return false;
               }
            }
         }
      }
   }

   public void pickupEntity(EntityLivingBase entity) {
      ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
      if (extendedEntity != null) {
         extendedEntity.setPickedUpByEntity(this);
      }

      this.pickupEntity = entity;
      this.clearMovement();
   }

   public EntityLivingBase getPickupEntity() {
      return this.pickupEntity;
   }

   public boolean hasPickupEntity() {
      if (!this.getEntityWorld().isRemote) {
         return this.getPickupEntity() != null;
      } else {
         return (this.getByteFromDataManager(TARGET) & BaseCreatureEntity.TARGET_BITS.PICKUP.id) > 0;
      }
   }

   public void dropPickupEntity() {
      ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
      if (extendedEntity != null) {
         extendedEntity.setPickedUpByEntity((Entity)null);
      }

      this.pickupEntity = null;
   }

   public double[] getPickupOffset(Entity entity) {
      return new double[]{0.0D, 0.0D, 0.0D};
   }

   public boolean canAttackWithPickup() {
      return false;
   }

   public void destroyArea(int x, int y, int z, float strength, boolean drop) {
      this.destroyArea(x, y, z, strength, drop, 0);
   }

   public void destroyArea(int x, int y, int z, float strength, boolean drop, int range) {
      this.destroyArea(x, y, z, strength, drop, range, (EntityPlayer)null, 0);
   }

   public void destroyArea(int x, int y, int z, float strength, boolean drop, int range, EntityPlayer player, int chain) {
      range = Math.max(range - 1, 0);

      for(int w = -((int)Math.ceil((double)this.width) - range); (double)w <= Math.ceil((double)this.width) + (double)range; ++w) {
         for(int d = -((int)Math.ceil((double)this.width) - range); (double)d <= Math.ceil((double)this.width) + (double)range; ++d) {
            for(int h = 0; (double)h <= Math.ceil((double)this.height); ++h) {
               BlockPos breakPos = new BlockPos(x + w, y + h, z + d);
               if (this.getEntityWorld().getTileEntity(breakPos) == null) {
                  IBlockState blockState = this.getEntityWorld().getBlockState(breakPos);
                  float hardness = blockState.getBlockHardness(this.getEntityWorld(), breakPos);
                  Material material = blockState.getMaterial();
                  if (hardness >= 0.0F && strength >= hardness && strength >= blockState.getBlock().getExplosionResistance(this) && material != Material.WATER && material != Material.LAVA && (player == null || w != 0 || h != 0 || d != 0)) {
                     SpawnerEventListener.getInstance().onBlockBreak(this.getEntityWorld(), breakPos, blockState, player, chain);
                     this.getEntityWorld().destroyBlock(breakPos, drop);
                  }
               }
            }
         }
      }

   }

   public void destroyAreaBlock(int x, int y, int z, Class<? extends Block> blockClass, boolean drop, int range) {
      for(int w = -((int)Math.ceil((double)this.width) + range); (double)w <= Math.ceil((double)this.width) + (double)range; ++w) {
         for(int d = -((int)Math.ceil((double)this.width) + range); (double)d <= Math.ceil((double)this.width) + (double)range; ++d) {
            for(int h = 0; (double)h <= Math.ceil((double)this.height); ++h) {
               BlockPos breakPos = new BlockPos(x + w, y + h, z + d);
               if (this.getEntityWorld().getTileEntity(breakPos) == null) {
                  IBlockState blockState = this.getEntityWorld().getBlockState(breakPos);
                  if (blockClass.isInstance(blockState.getBlock())) {
                     this.getEntityWorld().destroyBlock(breakPos, drop);
                  }
               }
            }
         }
      }

   }

   public boolean extraAnimation01() {
      return this.extraAnimation01;
   }

   public void applyDebuffs(EntityLivingBase entity, int duration, int amplifier) {
      for(ElementInfo element : this.getElements()) {
         element.debuffEntity(entity, this.getEffectDuration(duration), this.getEffectAmplifier((float)amplifier));
      }

   }

   public void applyBuffs(EntityLivingBase entity, int duration, int amplifier) {
      if (this.creatureStats.getAmplifier() >= 0.0D) {
         for(ElementInfo element : this.getElements()) {
            element.buffEntity(entity, this.getEffectDuration(duration), this.getEffectAmplifier((float)amplifier));
         }
      }

   }

   protected Item getDropItem() {
      return this.drops != null && this.drops.get(0) != null && !this.isMinion() && !this.isBoundPet() ? ((ItemDrop)this.drops.get(0)).getItemStack().getItem() : null;
   }

   protected void dropFewItems(boolean playerKill, int lootingModifier) {
      if (!this.getEntityWorld().isRemote && !this.isMinion() && !this.isBoundPet() && !this.dropsRequirePlayerDamage) {
         int variantScale = 1;
         if (this.isRareVariant()) {
            variantScale = Variant.RARE_DROP_SCALE;
         } else if (this.getVariant() != null && "uncommon".equals(this.getVariant().rarity)) {
            variantScale = Variant.UNCOMMON_DROP_SCALE;
         }

         for(ItemDrop itemDrop : this.drops) {
            if (this.canDropItem(itemDrop)) {
               int multiplier = 1;
               if (itemDrop.variantIndex < 0) {
                  multiplier *= variantScale;
               }

               if (this.extraMobBehaviour != null && this.extraMobBehaviour.itemDropMultiplierOverride != 1.0D) {
                  multiplier = Math.round((float)multiplier * (float)this.extraMobBehaviour.itemDropMultiplierOverride);
               }

               int quantity = itemDrop.getQuantity(this.rand, lootingModifier, multiplier);
               if (quantity > 0) {
                  this.dropItem(itemDrop.getEntityDropItemStack(this, quantity));
               }
            }
         }

      }
   }

   public boolean canDropItem(ItemDrop itemDrop) {
      if (itemDrop.subspeciesIndex >= 0 && itemDrop.subspeciesIndex != this.getSubspeciesIndex()) {
         return false;
      } else {
         return itemDrop.variantIndex < 0 || itemDrop.variantIndex == this.getVariantIndex();
      }
   }

   public void dropItem(ItemStack itemStack) {
      this.entityDropItem(itemStack, 0.0F);
   }

   public EntityItem entityDropItem(ItemStack itemStack, float heightOffset) {
      if (itemStack.getCount() != 0) {
         EntityItemCustom entityItem = new EntityItemCustom(this.getEntityWorld(), this.posX, this.posY + (double)heightOffset, this.posZ, itemStack);
         entityItem.setPickupDelay(10);
         this.applyDropEffects(entityItem);
         if (this.captureDrops) {
            this.capturedDrops.add(entityItem);
         } else {
            this.getEntityWorld().spawnEntity(entityItem);
         }

         return entityItem;
      } else {
         return null;
      }
   }

   public void applyDropEffects(EntityItemCustom entityItem) {
   }

   public void setDropsRequirePlayerDamage(boolean requiresPlayerDamage) {
      this.dropsRequirePlayerDamage = requiresPlayerDamage;
   }

   public void perchOnEntity(EntityLivingBase target) {
      if (target == null) {
         if (this.getPerchTarget() != null) {
            ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPerchTarget());
            if (extendedEntity != null) {
               extendedEntity.setPerchedByEntity((Entity)null);
            }
         }

         this.setPerchTarget((EntityLivingBase)null);
      } else {
         ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(target);
         if (extendedEntity != null) {
            this.setPerchTarget(target);
            extendedEntity.setPerchedByEntity(this);
         }
      }
   }

   public void openGUI(EntityPlayer player) {
      if (!this.getEntityWorld().isRemote) {
         this.addGUIViewer(player);
         this.refreshGUIViewers();
         this.openGUIToPlayer(player);
      }
   }

   public void addGUIViewer(EntityPlayer player) {
      if (!this.getEntityWorld().isRemote) {
         this.guiViewers.add(player);
      }

   }

   public void removeGUIViewer(EntityPlayer player) {
      if (!this.getEntityWorld().isRemote) {
         this.guiViewers.remove(player);
      }

   }

   public void refreshGUIViewers() {
      if (!this.getEntityWorld().isRemote) {
         if (this.guiViewers.size() > 0) {
            for(EntityPlayer player : (EntityPlayer[])this.guiViewers.toArray(new EntityPlayer[this.guiViewers.size()])) {
               if (player.openContainer != null && player.openContainer instanceof CreatureContainer) {
                  if (((CreatureContainer)player.openContainer).creature == this) {
                     this.openGUIToPlayer(player);
                  } else {
                     this.removeGUIViewer(player);
                  }
               }
            }
         }

      }
   }

   public void openGUIToPlayer(EntityPlayer player) {
      if (player != null) {
         player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.ENTITY.id, this.getEntityWorld(), this.getEntityId(), 0, 0);
      }

   }

   public void scheduleGUIRefresh() {
      this.guiRefreshTick = this.guiRefreshTime + 1;
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      if (this.hasPerchTarget()) {
         return false;
      } else {
         return this.assessInteractCommand(this.getInteractCommands(player, hand, player.getHeldItem(hand)), player, hand, player.getHeldItem(hand)) ? true : super.processInteract(player, hand);
      }
   }

   public boolean assessInteractCommand(HashMap<Integer, String> commands, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      if (commands.isEmpty()) {
         return false;
      } else {
         int priority = 100;
         Iterator var6 = commands.keySet().iterator();

         while(var6.hasNext()) {
            int testPriority = ((Integer)var6.next()).intValue();
            if (testPriority < priority) {
               priority = testPriority;
            }
         }

         if (!commands.containsKey(Integer.valueOf(priority))) {
            return false;
         } else {
            this.performCommand((String)commands.get(Integer.valueOf(priority)), player, hand, itemStack);
            return true;
         }
      }
   }

   public HashMap<Integer, String> getInteractCommands(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      HashMap<Integer, String> commands = new HashMap();
      if (itemStack != null) {
         if (itemStack.getItem() == Items.LEAD && this.canBeLeashedTo(player)) {
            commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Leash");
         }

         if (itemStack.getItem() == Items.NAME_TAG) {
            if (this.canNameTag(player)) {
               return new HashMap();
            }

            commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Name Tag");
         }

         if (this.canBeColored(player) && itemStack.getItem() == Items.DYE) {
            commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Color");
         }
      }

      return commands;
   }

   public boolean performCommand(String command, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      if ("Leash".equals(command)) {
         this.setLeashHolder(player, true);
         this.consumePlayersItem(player, hand, itemStack);
         return true;
      } else {
         if ("Color".equals(command)) {
            int colorID = 15 - itemStack.getItemDamage();
            if (colorID != this.getColor()) {
               this.setColor(colorID);
               this.consumePlayersItem(player, hand, itemStack);
               return true;
            }
         }

         return false;
      }
   }

   public boolean canNameTag(EntityPlayer player) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public boolean getAlwaysRenderNameTagForRender() {
      return this.getVariant() != null && !this.hasCustomName() ? this.renderVariantNameTag() : super.getAlwaysRenderNameTagForRender();
   }

   public boolean renderVariantNameTag() {
      return CreatureManager.getInstance().config.subspeciesTags;
   }

   public void consumePlayersItem(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      this.consumePlayersItem(player, hand, itemStack, 1);
   }

   public void consumePlayersItem(EntityPlayer player, EnumHand hand, ItemStack itemStack, int amount) {
      if (!player.capabilities.isCreativeMode) {
         itemStack.shrink(amount);
      }

   }

   public void replacePlayersItem(EntityPlayer player, EnumHand hand, ItemStack itemStack, ItemStack newStack) {
      this.replacePlayersItem(player, hand, itemStack, 1, newStack);
   }

   public void replacePlayersItem(EntityPlayer player, EnumHand hand, ItemStack itemStack, int amount, ItemStack newStack) {
      if (!player.capabilities.isCreativeMode) {
         itemStack.shrink(amount);
      }

      if (itemStack.isEmpty()) {
         player.setHeldItem(hand, newStack);
      } else if (!player.addItemStackToInventory(newStack)) {
         player.dropItem(newStack, false);
      }

   }

   public void performGUICommand(EntityPlayer player, byte guiCommandID) {
      this.scheduleGUIRefresh();
   }

   public boolean canCarryItems() {
      return this.getInventorySize() > 0;
   }

   public int getInventorySize() {
      return this.inventory.getSizeInventory();
   }

   public int getInventorySizeMax() {
      return Math.max(this.getNoBagSize(), this.getBagSize());
   }

   public boolean hasBag() {
      return this.inventory.getEquipmentStack("bag") != null;
   }

   public int getNoBagSize() {
      return this.extraMobBehaviour != null && this.extraMobBehaviour.inventorySizeOverride > 0 ? this.extraMobBehaviour.inventorySizeOverride : 0;
   }

   public int getBagSize() {
      return this.creatureInfo != null ? this.creatureInfo.bagSize : 5;
   }

   public boolean canPickupItems() {
      return this.extraMobBehaviour != null && this.extraMobBehaviour.itemPickupOverride;
   }

   public int getSpaceForStack(ItemStack pickupStack) {
      return this.inventory.getSpaceForStack(pickupStack);
   }

   public boolean canEquip() {
      return this.creatureInfo.isTameable();
   }

   public void setCurrentItemOrArmor(int slot, ItemStack itemStack) {
      String type = "item";
      if (slot == 0) {
         type = "weapon";
      }

      if (slot == 1) {
         type = "feet";
      }

      if (slot == 2) {
         type = "legs";
      }

      if (slot == 3) {
         type = "chest";
      }

      if (slot == 4) {
         type = "head";
      }

      if (slot == 100) {
         type = "bag";
      }

      this.inventory.setEquipmentStack(type, itemStack);
   }

   public String getEquipmentName(String type) {
      return this.inventory.getEquipmentGrade(type) != null ? type + this.inventory.getEquipmentGrade(type) : null;
   }

   public int getTotalArmorValue() {
      return super.getTotalArmorValue() + this.inventory.getArmorValue();
   }

   public void pickupItems() {
      for(EntityItem entityItem : this.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
         if (!entityItem.isDead && entityItem.getItem() != null) {
            ItemStack itemStack = entityItem.getItem();
            int space = this.getSpaceForStack(itemStack);
            if (space > 0) {
               this.onPickupStack(itemStack);
               this.doItemPickup(entityItem);
            }
         }
      }

   }

   public void onPickupStack(ItemStack itemStack) {
   }

   public void doItemPickup(EntityItem entityItem) {
      if (!entityItem.isDead && entityItem.getItem() != null) {
         ItemStack leftoverStack = this.inventory.autoInsertStack(entityItem.getItem());
         if (leftoverStack != null) {
            entityItem.setItem(leftoverStack);
         } else {
            entityItem.setDead();
         }
      }

   }

   public void onBlockPlaced(BlockPos blockPos, IBlockState blockState) {
   }

   public void onBuildTaskComplete(CreatureBuildTask buildTask) {
   }

   public boolean isEntityInvulnerable(DamageSource source) {
      return this.damageLimit > 0.0F && this.damageTakenThisSec >= this.damageLimit ? true : super.isEntityInvulnerable(source);
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      if (!"inWall".equals(type) && !"cactus".equals(type) || !this.isRareVariant() && !this.isBoss()) {
         if (!"inWall".equals(type)) {
            if ("drown".equals(type)) {
               return !CreatureManager.getInstance().config.drownImmunity;
            } else if ("fall".equals(type)) {
               return this.getFallResistance() < 100.0F;
            } else {
               return true;
            }
         } else {
            return !CreatureManager.getInstance().config.suffocationImmunity && !this.hasPerchTarget();
         }
      } else {
         return false;
      }
   }

   public boolean isDamageEntityApplicable(Entity entity) {
      if (this.isBoss() || this.isRareVariant()) {
         if (entity == null) {
            return false;
         }

         if (this.getDistance(entity) > (float)this.bossRange) {
            if (entity instanceof EntityPlayer) {
               ((EntityPlayer)entity).sendStatusMessage(new TextComponentString(LanguageManager.translate("boss.damage.protection.range")), true);
            }

            return false;
         }
      }

      return true;
   }

   public boolean isPotionApplicable(PotionEffect potionEffect) {
      for(ElementInfo element : this.getElements()) {
         if (!element.isEffectApplicable(potionEffect)) {
            return false;
         }
      }

      return true;
   }

   public boolean canBurn() {
      if (this.extraMobBehaviour != null && this.extraMobBehaviour.fireImmunityOverride) {
         return false;
      } else {
         for(ElementInfo element : this.getElements()) {
            if (!element.canBurn()) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean daylightBurns() {
      return false;
   }

   public boolean canFreeze() {
      if (this instanceof IGroupIce) {
         return false;
      } else {
         for(ElementInfo element : this.getElements()) {
            if (!element.canFreeze()) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean waterDamage() {
      return false;
   }

   public boolean webProof() {
      return false;
   }

   public void setInWeb() {
      if (!this.webProof()) {
         super.setInWeb();
      }

   }

   public boolean canBreatheAir() {
      return true;
   }

   public boolean canBreatheUnderwater() {
      return this.extraMobBehaviour != null && this.extraMobBehaviour.waterBreathingOverride;
   }

   public boolean canBreatheUnderlava() {
      return true;
   }

   public void setAir(int air) {
      if (air != 300 || this.canBreatheAir()) {
         super.setAir(air);
      }
   }

   public boolean isInWater() {
      if (this.isLavaCreature && this.width <= 2.0F) {
         return this.isInLava() || super.isInWater();
      } else {
         return super.isInWater();
      }
   }

   public boolean waterContact() {
      if (this.isWet()) {
         return true;
      } else {
         return this.getEntityWorld().isRaining() && !this.isBlockUnderground((int)this.posX, (int)this.posY, (int)this.posZ);
      }
   }

   public boolean lavaContact() {
      return this.isInLava();
   }

   public boolean canBreatheAtLocation(BlockPos pos) {
      IBlockState blockState = this.getEntityWorld().getBlockState(pos);
      if (blockState == null) {
         return true;
      } else if (this.canBreatheAir() && blockState.getMaterial() == Material.AIR) {
         return true;
      } else {
         if (this.canBreatheUnderwater()) {
            if (!this.isLavaCreature && blockState.getMaterial() == Material.WATER) {
               return true;
            }

            if (this.isLavaCreature && blockState.getMaterial() == Material.LAVA) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isSwimmable(int x, int y, int z) {
      IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y, z));
      if (blockState == null) {
         return false;
      } else if (this.isLavaCreature && Material.LAVA.equals(blockState.getMaterial())) {
         return true;
      } else {
         return Material.WATER.equals(blockState.getMaterial());
      }
   }

   public float getFallResistance() {
      return 0.0F;
   }

   public byte testLightLevel() {
      return this.testLightLevel(this.getPosition());
   }

   public byte testLightLevel(BlockPos pos) {
      IBlockState spawnBlockState = this.getEntityWorld().getBlockState(pos);
      if (pos.getY() < 0) {
         return 0;
      } else {
         if (spawnBlockState.getMaterial() == Material.WATER && CreatureManager.getInstance().spawnConfig.useSurfaceLightLevel) {
            pos = new BlockPos(pos.getX(), this.getWaterSurfaceY(pos), pos.getZ());
         } else {
            pos = new BlockPos(pos.getX(), this.getGroundY(pos), pos.getZ());
         }

         int light = this.getEntityWorld().getLightFromNeighbors(pos);
         if (this.getEntityWorld().isThundering()) {
            int originalSkylight = this.getEntityWorld().getSkylightSubtracted();
            this.getEntityWorld().setSkylightSubtracted(10);
            light = this.getEntityWorld().getLightFromNeighbors(pos);
            this.getEntityWorld().setSkylightSubtracted(originalSkylight);
         }

         if (light == 0) {
            return 0;
         } else if (light <= 7) {
            return 1;
         } else {
            return (byte)(light <= 14 ? 2 : 3);
         }
      }
   }

   public boolean isDaytime() {
      if (!this.getEntityWorld().isRemote) {
         return this.getEntityWorld().isDaytime();
      } else {
         long time = this.getEntityWorld().getWorldTime();
         if (time < 12500L) {
            return true;
         } else {
            return time < 12542L || time >= 23460L;
         }
      }
   }

   public int nearbyCreatureCount(Class targetClass, double range) {
      return this.getNearbyEntities(Entity.class, (entity) -> {
         return targetClass == null || targetClass.isAssignableFrom(entity.getClass());
      }, range).size();
   }

   public int countAllies(double range) {
      return this.getNearbyEntities(Entity.class, (entity) -> {
         return this.getClass().isAssignableFrom(entity.getClass()) && entity instanceof BaseCreatureEntity && this.isTamed() == ((BaseCreatureEntity)entity).isTamed();
      }, range).size();
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return this.attribute;
   }

   public double getMountedXOffset() {
      return this.getSubspecies() != null && this.getSubspecies().mountOffset != null ? (double)this.width * this.getSubspecies().mountOffset.x : (double)this.width * this.creatureInfo.mountOffset.x;
   }

   public double getMountedYOffset() {
      return this.getSubspecies() != null && this.getSubspecies().mountOffset != null ? (double)this.height * this.getSubspecies().mountOffset.y : (double)this.height * this.creatureInfo.mountOffset.y;
   }

   public double getMountedZOffset() {
      return this.getSubspecies() != null && this.getSubspecies().mountOffset != null ? (double)this.width * this.getSubspecies().mountOffset.z : (double)this.width * this.creatureInfo.mountOffset.z;
   }

   public <T extends Entity> List<T> getNearbyEntities(Class<? extends T> clazz, Predicate<Entity> predicate, double range) {
      return this.getEntityWorld().getEntitiesWithinAABB(clazz, this.getEntityBoundingBox().grow(range, range, range), predicate);
   }

   public <T extends Entity> T getNearestEntity(Class<? extends T> clazz, Class filterClass, double range, boolean canAttack) {
      List aoeTargets = this.getNearbyEntities(clazz, (entity) -> {
         return filterClass == null || filterClass.isAssignableFrom(entity.getClass());
      }, range);
      if (aoeTargets.size() == 0) {
         return (T)null;
      } else {
         double nearestDistance = range + 10.0D;
         T nearestEntity = null;

         for(Object entityObj : aoeTargets) {
            T targetEntity = (T)entityObj;
            if (targetEntity != this && targetEntity instanceof EntityLivingBase && (!canAttack || this.canAttackEntity((EntityLivingBase)targetEntity)) && targetEntity != this.getControllingPassenger()) {
               double distance = (double)this.getDistance(targetEntity);
               if (distance < nearestDistance) {
                  nearestDistance = distance;
                  nearestEntity = targetEntity;
               }
            }
         }

         return nearestEntity;
      }
   }

   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      if (this.creatureInfo.dummy) {
         super.readEntityFromNBT(nbtTagCompound);
      } else {
         if (nbtTagCompound.hasKey("FirstSpawn")) {
            this.firstSpawn = nbtTagCompound.getBoolean("FirstSpawn");
         } else {
            this.firstSpawn = true;
         }

         if (nbtTagCompound.hasKey("SpawnEventType")) {
            this.spawnEventType = nbtTagCompound.getString("SpawnEventType");
         }

         if (nbtTagCompound.hasKey("SpawnEventCount")) {
            this.spawnEventCount = nbtTagCompound.getInteger("SpawnEventCount");
         }

         if (nbtTagCompound.hasKey("Stealth")) {
            this.setStealth(nbtTagCompound.getFloat("Stealth"));
         }

         if (nbtTagCompound.hasKey("IsMinion")) {
            this.setMinion(nbtTagCompound.getBoolean("IsMinion"));
         }

         if (nbtTagCompound.hasKey("IsTemporary") && nbtTagCompound.getBoolean("IsTemporary") && nbtTagCompound.hasKey("TemporaryDuration")) {
            this.setTemporary(nbtTagCompound.getInteger("TemporaryDuration"));
         } else {
            this.unsetTemporary();
         }

         if (nbtTagCompound.hasKey("IsBoundPet") && nbtTagCompound.getBoolean("IsBoundPet") && !this.hasPetEntry()) {
            this.setDead();
         }

         if (nbtTagCompound.hasKey("ForceNoDespawn")) {
            this.forceNoDespawn = nbtTagCompound.getBoolean("ForceNoDespawn");
         }

         if (nbtTagCompound.hasKey("Color")) {
            this.setColor(nbtTagCompound.getByte("Color"));
         }

         if (nbtTagCompound.hasKey("Size")) {
            this.setSizeScale(nbtTagCompound.getDouble("Size"));
         }

         if (!this.firstSpawn && nbtTagCompound.hasKey("Subspecies") && !nbtTagCompound.hasKey("Variant")) {
            this.setSubspecies(Variant.getIndexFromOld(nbtTagCompound.getByte("Subspecies")));
            this.setVariant(Variant.getIndexFromOld(nbtTagCompound.getByte("Subspecies")));
         } else {
            if (nbtTagCompound.hasKey("Subspecies")) {
               this.setSubspecies(nbtTagCompound.getByte("Subspecies"));
            }

            if (nbtTagCompound.hasKey("Variant")) {
               if (this.firstSpawn) {
                  this.applyVariant(nbtTagCompound.getByte("Variant"));
               } else {
                  this.setVariant(nbtTagCompound.getByte("Variant"));
               }
            }
         }

         if (nbtTagCompound.hasKey("MobLevel")) {
            if (this.firstSpawn) {
               this.applyLevel(nbtTagCompound.getInteger("MobLevel"));
            } else {
               this.setLevel(nbtTagCompound.getInteger("MobLevel"));
            }
         }

         if (nbtTagCompound.hasKey("Experience")) {
            this.setExperience(nbtTagCompound.getInteger("Experience"));
         }

         if (nbtTagCompound.hasKey("SpawnedAsBoss")) {
            this.spawnedAsBoss = nbtTagCompound.getBoolean("SpawnedAsBoss");
         }

         if (nbtTagCompound.hasKey("SpawnedWithBlockProtection")) {
            this.spawnedWithBlockProtection = nbtTagCompound.getInteger("SpawnedWithBlockProtection");
         }

         super.readEntityFromNBT(nbtTagCompound);
         this.relationships.load(nbtTagCompound);
         this.inventory.readFromNBT(nbtTagCompound);
         if (nbtTagCompound.hasKey("Drops")) {
            NBTTagList nbtDropList = nbtTagCompound.getTagList("Drops", 10);

            for(int i = 0; i < nbtDropList.tagCount(); ++i) {
               NBTTagCompound dropNBT = nbtDropList.getCompoundTagAt(i);
               ItemDrop drop = new ItemDrop(dropNBT);
               this.addSavedItemDrop(drop);
            }
         }

         if (nbtTagCompound.hasKey("DropsRequirePlayerDamage")) {
            this.dropsRequirePlayerDamage = nbtTagCompound.getBoolean("DropsRequirePlayerDamage");
         }

         if (nbtTagCompound.hasKey("ExtraBehaviour")) {
            this.extraMobBehaviour.readFromNBT(nbtTagCompound.getCompoundTag("ExtraBehaviour"));
         }

         if (nbtTagCompound.hasKey("HomeX") && nbtTagCompound.hasKey("HomeY") && nbtTagCompound.hasKey("HomeZ") && nbtTagCompound.hasKey("HomeDistanceMax")) {
            this.setHome(nbtTagCompound.getInteger("HomeX"), nbtTagCompound.getInteger("HomeY"), nbtTagCompound.getInteger("HomeZ"), nbtTagCompound.getFloat("HomeDistanceMax"));
         }

         if (nbtTagCompound.hasKey("ArenaX") && nbtTagCompound.hasKey("ArenaY") && nbtTagCompound.hasKey("ArenaZ")) {
            this.setArenaCenter(new BlockPos(nbtTagCompound.getInteger("ArenaX"), nbtTagCompound.getInteger("ArenaY"), nbtTagCompound.getInteger("ArenaZ")));
         }

         if (nbtTagCompound.hasKey("FixateUUIDMost") && nbtTagCompound.hasKey("FixateUUIDLeast")) {
            this.fixateUUID = new EaglercraftUUID(nbtTagCompound.getLong("FixateUUIDMost"), nbtTagCompound.getLong("FixateUUIDLeast"));
         }

         if (nbtTagCompound.hasKey("MinionIds")) {
            NBTTagList minionIds = nbtTagCompound.getTagList("MinionIds", 10);

            for(int i = 0; i < minionIds.tagCount(); ++i) {
               NBTTagCompound minionId = minionIds.getCompoundTagAt(i);
               if (minionId.hasKey("ID")) {
                  Entity entity = this.getEntityWorld().getEntityByID(minionId.getInteger("ID"));
                  if (entity instanceof EntityLivingBase) {
                     this.addMinion((EntityLivingBase)entity);
                  }
               }
            }
         }

      }
   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      if (this.creatureInfo.dummy) {
         super.writeEntityToNBT(nbtTagCompound);
      } else {
         nbtTagCompound.setBoolean("FirstSpawn", this.firstSpawn);
         nbtTagCompound.setString("SpawnEventType", this.spawnEventType);
         nbtTagCompound.setInteger("SpawnEventCount", this.spawnEventCount);
         nbtTagCompound.setFloat("Stealth", this.getStealth());
         nbtTagCompound.setBoolean("IsMinion", this.isMinion());
         nbtTagCompound.setBoolean("IsTemporary", this.isTemporary);
         nbtTagCompound.setInteger("TemporaryDuration", this.temporaryDuration);
         nbtTagCompound.setBoolean("IsBoundPet", this.isBoundPet());
         nbtTagCompound.setBoolean("ForceNoDespawn", this.forceNoDespawn);
         nbtTagCompound.setByte("Color", (byte)this.getColor());
         nbtTagCompound.setByte("Subspecies", (byte)this.getSubspeciesIndex());
         nbtTagCompound.setByte("Variant", (byte)this.getVariantIndex());
         nbtTagCompound.setDouble("Size", this.sizeScale);
         nbtTagCompound.setInteger("MobLevel", this.getLevel());
         nbtTagCompound.setInteger("Experience", this.getExperience());
         nbtTagCompound.setBoolean("SpawnedAsBoss", this.spawnedAsBoss);
         nbtTagCompound.setInteger("SpawnedWithBlockProtection", this.spawnedWithBlockProtection);
         if (this.hasHome()) {
            BlockPos homePos = this.getHomePosition();
            nbtTagCompound.setInteger("HomeX", homePos.getX());
            nbtTagCompound.setInteger("HomeY", homePos.getY());
            nbtTagCompound.setInteger("HomeZ", homePos.getZ());
            nbtTagCompound.setFloat("HomeDistanceMax", this.getHomeDistanceMax());
         }

         if (this.hasArenaCenter()) {
            BlockPos arenaPos = this.getArenaCenter();
            nbtTagCompound.setInteger("ArenaX", arenaPos.getX());
            nbtTagCompound.setInteger("ArenaY", arenaPos.getY());
            nbtTagCompound.setInteger("ArenaZ", arenaPos.getZ());
         }

         if (this.getFixateTarget() != null) {
            nbtTagCompound.setLong("FixateUUIDMost", this.getFixateTarget().getUniqueID().getMostSignificantBits());
            nbtTagCompound.setLong("FixateUUIDLeast", this.getFixateTarget().getUniqueID().getLeastSignificantBits());
         }

         super.writeEntityToNBT(nbtTagCompound);
         this.relationships.save(nbtTagCompound);
         this.inventory.writeToNBT(nbtTagCompound);
         NBTTagList nbtDropList = new NBTTagList();

         for(ItemDrop drop : this.savedDrops) {
            NBTTagCompound dropNBT = new NBTTagCompound();
            if (drop.writeToNBT(dropNBT)) {
               nbtDropList.appendTag(dropNBT);
            }
         }

         nbtTagCompound.setTag("Drops", nbtDropList);
         nbtTagCompound.setBoolean("DropsRequirePlayerDamage", this.dropsRequirePlayerDamage);
         NBTTagCompound extTagCompound = new NBTTagCompound();
         this.extraMobBehaviour.writeToNBT(extTagCompound);
         nbtTagCompound.setTag("ExtraBehaviour", extTagCompound);
         NBTTagList minionIds = new NBTTagList();

         for(EntityLivingBase minion : this.minions) {
            NBTTagCompound minionId = new NBTTagCompound();
            minionId.setInteger("ID", minion.getEntityId());
            minionIds.appendTag(minionId);
         }

         nbtTagCompound.setTag("MinionIds", minionIds);
      }
   }

   public int getAttackCooldown() {
      return this.attackCooldown;
   }

   public boolean isAttackOnCooldown() {
      return this.getAttackCooldown() > 0;
   }

   public void triggerAttackCooldown() {
      this.attackCooldown = this.getAttackCooldownMax();
   }

   public void resetAttackCooldown() {
      this.attackCooldown = 0;
      this.setAttackCooldownMax(this.getAttackCooldownMax());
   }

   public int getAttackCooldownMax() {
      return !this.getEntityWorld().isRemote ? this.attackCooldownMax : this.getIntFromDataManager(ANIMATION_ATTACK_COOLDOWN_MAX);
   }

   public void setAttackCooldownMax(int cooldownMax) {
      this.attackCooldownMax = cooldownMax;
      if (!this.getEntityWorld().isRemote) {
         this.getDataManager().set(ANIMATION_ATTACK_COOLDOWN_MAX, Integer.valueOf(this.attackCooldownMax));
      }

   }

   public ResourceLocation getTexture() {
      return this.getTexture("");
   }

   public ResourceLocation getTexture(String suffix) {
      String textureName = this.getTextureName();
      if (this.getSubspecies().name != null) {
         textureName = textureName + "_" + this.getSubspecies().name;
      }

      if (this.getVariant() != null) {
         textureName = textureName + "_" + this.getVariant().color;
      }

      if (!"".equals(suffix)) {
         textureName = textureName + "_" + suffix;
      }

      if (AssetManager.getTexture(textureName) == null) {
         AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
      }

      return AssetManager.getTexture(textureName);
   }

   public ResourceLocation getEquipmentTexture(String equipmentName) {
      if (!this.canEquip()) {
         return this.getTexture();
      } else {
         if (this.getSubspecies() != null && this.getSubspecies().name != null) {
            equipmentName = this.getSubspecies().name + "_" + equipmentName;
         }

         return this.getSubTexture(equipmentName);
      }
   }

   public ResourceLocation getSubTexture(String subName) {
      subName = subName.toLowerCase();
      String textureName = this.getTextureName();
      textureName = textureName + "_" + subName;
      if (AssetManager.getTexture(textureName) == null) {
         AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
      }

      return AssetManager.getTexture(textureName);
   }

   public String getTextureName() {
      return this.creatureInfo.getName();
   }

   public boolean canBeColored(EntityPlayer player) {
      return false;
   }

   public int getColor() {
      return this.getByteFromDataManager(COLOR) & 15;
   }

   public void setColor(int color) {
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(COLOR, Byte.valueOf((byte)(color & 15)));
      }

   }

   public boolean showBossInfo() {
      if (!this.forceBossHealthBar && !this.isBoss()) {
         return this.isRareVariant() ? Variant.RARE_HEALTH_BARS : false;
      } else {
         return true;
      }
   }

   public void addTrackingPlayer(EntityPlayerMP player) {
      super.addTrackingPlayer(player);
      if (this.getBossInfo() != null) {
         this.bossInfo.addPlayer(player);
      }

   }

   public void removeTrackingPlayer(EntityPlayerMP player) {
      super.removeTrackingPlayer(player);
      if (this.getBossInfo() != null) {
         this.bossInfo.removePlayer(player);
      }

   }

   protected float getSoundVolume() {
      if (this.isBoss()) {
         return 4.0F;
      } else {
         return this.isRareVariant() ? 2.0F : 1.0F;
      }
   }

   public String getSoundName() {
      String soundSuffix = "";
      if (this.getSubspecies() != null && this.getSubspecies().name != null) {
         soundSuffix = soundSuffix + "." + this.getSubspecies().name;
      }

      return this.creatureInfo.getName() + soundSuffix;
   }

   public int getTalkInterval() {
      return CreatureManager.getInstance().config.idleSoundTicks;
   }

   protected SoundEvent getAmbientSound() {
      return AssetManager.getSound(this.getSoundName() + "_say");
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return AssetManager.getSound(this.getSoundName() + "_hurt");
   }

   protected SoundEvent getDeathSound() {
      return AssetManager.getSound(this.getSoundName() + "_death");
   }

   protected void playStepSound(BlockPos pos, Block block) {
      if (!this.isCurrentlyFlying()) {
         if (!this.hasStepSound) {
            super.playStepSound(pos, block);
         } else {
            this.playSound(AssetManager.getSound(this.getSoundName() + "_step"), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
         }
      }
   }

   protected SoundEvent getFallSound(int height) {
      return height > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_HOSTILE_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_HOSTILE_SPLASH;
   }

   public void playJumpSound() {
      if (this.hasJumpSound) {
         this.playSound(AssetManager.getSound(this.getSoundName() + "_jump"), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      }
   }

   public void playFlySound() {
      if (this.isFlying() && !this.hasPerchTarget()) {
         this.playSound(AssetManager.getSound(this.getSoundName() + "_fly"), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      }
   }

   public void playAttackSound() {
      if (this.hasAttackSound) {
         this.playSound(AssetManager.getSound(this.getSoundName() + "_attack"), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      }
   }

   public void playPhaseSound() {
      if (AssetManager.getSound(this.creatureInfo.getName() + "_phase") != null) {
         this.playSound(AssetManager.getSound(this.getSoundName() + "_phase"), this.getSoundVolume() * 2.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      }
   }

   public void playSound(SoundEvent sound, float volume, float pitch) {
      super.playSound(sound, volume, pitch);
   }

   public static enum ANIMATION_STATE_BITS {
      ATTACKED((byte)1),
      GROUNDED((byte)2),
      IN_WATER((byte)4),
      BLOCKING((byte)8),
      MINION((byte)16),
      EXTRA01((byte)32),
      BOSS((byte)64);

      public final byte id;

      private ANIMATION_STATE_BITS(byte value) {
         this.id = value;
      }

      public byte getValue() {
         return this.id;
      }
   }

   public static enum COMMAND_PIORITIES {
      OVERRIDE(0),
      IMPORTANT(1),
      EQUIPPING(2),
      ITEM_USE(3),
      EMPTY_HAND(4),
      MAIN(5);

      public final int id;

      private COMMAND_PIORITIES(int value) {
         this.id = value;
      }

      public int getValue() {
         return this.id;
      }
   }

   public static enum GUI_COMMAND {
      CLOSE((byte)0),
      SITTING((byte)1),
      FOLLOWING((byte)2),
      PASSIVE((byte)3),
      STANCE((byte)4),
      PVP((byte)5),
      TELEPORT((byte)6),
      SPAWNING((byte)7),
      RELEASE((byte)8);

      public byte id;

      private GUI_COMMAND(byte i) {
         this.id = i;
      }
   }

   public class GroupData implements IEntityLivingData {
      public boolean isChild;

      public GroupData(boolean child) {
         this.isChild = child;
      }
   }

   public static enum PET_COMMAND_ID {
      ACTIVE((byte)0),
      TELEPORT((byte)1),
      PVP((byte)2),
      RELEASE((byte)3),
      PASSIVE((byte)4),
      DEFENSIVE((byte)5),
      ASSIST((byte)6),
      AGGRESSIVE((byte)7),
      FOLLOW((byte)8),
      WANDER((byte)9),
      SIT((byte)10),
      FLEE((byte)11);

      public byte id;

      private PET_COMMAND_ID(byte i) {
         this.id = i;
      }
   }

   public static enum TARGET_BITS {
      ATTACK((byte)1),
      MASTER((byte)2),
      PARENT((byte)4),
      AVOID((byte)8),
      RIDER((byte)16),
      PICKUP((byte)32),
      PERCH((byte)64);

      public final byte id;

      private TARGET_BITS(byte value) {
         this.id = value;
      }

      public byte getValue() {
         return this.id;
      }
   }

   public static enum TARGET_TYPES {
      ENEMY((byte)1),
      ALLY((byte)2),
      SELF((byte)4);

      public final byte id;

      private TARGET_TYPES(byte value) {
         this.id = value;
      }

      public byte getValue() {
         return this.id;
      }
   }
}
