package org.jwaresoftware.mcmods.lib;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.jwaresoftware.mcmods.lib.integration.BaublesHelper;

public final class SharedGlue extends OldNames {
   @ItemStackHolder("minecraft:air")
   public static final ItemStack ITEMSTACK_AIR = null;
   public static final String MINECRAFT_DOMAIN = "minecraft";
   public static final BlockPos NOWHERE = new BlockPos(-1, -1, -1);
   public static final int DIMENSION_SURFACE = 0;
   public static final int DIMENSION_NETHER = -1;
   public static final int DIMENSION_END = 1;
   private static final AxisAlignedBB __SOUL_SAND_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);
   public static final String NBT_INSTRUCTIONS = "_Hord_Instructions";
   public static final String NBT_VERSION = "Version";
   public static final String NBT_MOD_ID = "ModId";
   public static final String NBT_GENERIC_UITIP = "UiTip";
   public static final String NBT_ADVANCED_UITIP = "AdvancedUiTip";
   public static final String NBT_X_ENCHANT = "PrizeEnchant";
   public static final String NBT_X_ENCHANT_MAX = "MaxPrizeEnchant";
   public static final String NBT_X_GEAR_FLAG = "XGear";
   public static final String NBT_ORIGIN = "Origin";
   public static final String NBT_X_FOOD_FLAG = "XFood";
   public static final String NBT_USED = "Used";
   public static final String NBT_CAPACITY = "Capacity";
   public static final String NBT_COLOR_RGB = "ColorRGB";
   public static final String NBT_OWNERID = "OwnerUUID";
   public static final String NBT_OWNERNAME = "OwnerName";
   public static final String NBT_UNBREAKABLE = "Unbreakable";
   public static final String NBT_UNREPAIRABLE = "Unrepairable";
   public static final String NBT_LOOTED_FLAG = "Looted";
   public static final String NBT_CRAFTED_FLAG = "Crafted";
   public static final String NBT_UNVANISHING_FLAG = "Unvanishing";
   public static final String NBT_VANISHING_FLAG = "Vanishing";
   public static final String NBT_STATS_PROFILE_NAME = "StatsProfile";
   public static final String NBT_SKIN = "Skin";
   public static final String NBT_JSON_LOOTTABLE = "withLoot";
   public static final String NBT_JSON_COLOR_RGB = "withColor";
   public static final int OP_ADD_ATTR = 0;
   public static final int OP_MUL_BASE_ATTR = 1;
   public static final int OP_MUL_TOTAL_ATTR = 2;

   public static final boolean isDefined(Item item) {
      return item != null && item != Items.AIR;
   }

   public static final int CHUNK_DIM() {
      return 16;
   }

   public static final int WORLD_HEIGHT() {
      return 256;
   }

   public static final int DEFAULT_TICKS_PER_FULL_DAYNIGHT_CYCLE() {
      return 24000;
   }

   public static final int DEFAULT_TICKS_TWEEN_TIMES() {
      return 1800;
   }

   public static final int DEFAULT_TICKS_PER_DAYTIME_CYCLE() {
      return 13800;
   }

   public static final int SECS_TO_TICKS_DURATION_MULTIPLIER() {
      return 20;
   }

   public static final int SECS_TO_TICKS_MULTIPLIER() {
      return SECS_TO_TICKS_DURATION_MULTIPLIER();
   }

   public static final int HALF_SECS_TO_TICKS_DURATION_MULTIPLIER() {
      return 10;
   }

   public static final int PLAYER_CRAFT_GRID_SIZE() {
      return 4;
   }

   public static final int PLAYER_HOTBAR_SIZE() {
      return 9;
   }

   public static final int TABLE_CRAFT_GRID_SIZE() {
      return 9;
   }

   public static final int TABLE_CRAFT_GRID_SIDE() {
      return 3;
   }

   public static final int MAX_STACK_SIZE(int default_max) {
      int n = ModIntegration.STACK_UP.isLoaded() ? Items.AIR.getItemStackLimit(ITEMSTACK_AIR) : 64;
      if (default_max > n) {
         n = default_max;
      }

      return n;
   }

   public static final int MAX_STACK_SIZE() {
      return MAX_STACK_SIZE(-1);
   }

   public static final int PINNED_MAX_STACK_SIZE(int default_max, float max_minecraft_stacks) {
      int count = MAX_STACK_SIZE(default_max);
      int limit = (int)(64.0F * max_minecraft_stacks);
      if (count > limit) {
         count = limit;
      }

      return count;
   }

   public static final int OVERMAX_STACK_SIZE() {
      return PINNED_MAX_STACK_SIZE(96, 1.5F);
   }

   public static final int AMMO_STANDARD_MAX_STACK_SIZE() {
      return PINNED_MAX_STACK_SIZE(-1, 16.0F);
   }

   public static final int AMMO_OVER_MAX_STACK_SIZE() {
      return PINNED_MAX_STACK_SIZE(96, 16.0F);
   }

   public static final int REDUCED_STACK_SIZE() {
      int n = MAX_STACK_SIZE(-1);
      return n >= 256 ? 64 : 16;
   }

   public static final int SINGLE_CHEST_SIZE() {
      return 27;
   }

   public static final int DOUBLE_CHEST_SIZE() {
      return 54;
   }

   public static final int VILLAGER_INVENTORY_SIZE() {
      return 8;
   }

   public static final int MAX_ARRAY_SIZE_OF_PERSISTABLE_INDEX_META() {
      return 16;
   }

   public static final boolean validForBlockmeta(int size) {
      return size >= 0 && size <= MAX_ARRAY_SIZE_OF_PERSISTABLE_INDEX_META();
   }

   public static final int MAX_LEVEL_CAULDRON() {
      return 3;
   }

   public static final int MAX_LIGHT_LEVEL() {
      return 15;
   }

   public static final int MIN_CROPS_SPAWN_LIGHT() {
      return 9;
   }

   public static final int MAX_MOB_SPAWN_LIGHT_LEVEL() {
      return 7;
   }

   public static final int DEFAULT_CROPS_MATURE_AGE() {
      return 7;
   }

   public static final int REEDS_MATURE_AGE() {
      return 15;
   }

   public static final int MAX_WATER_FLOW_RANGE() {
      return 7;
   }

   public static final int DEFAULT_FARMLAND_HYDRATE_RANGE() {
      return 4;
   }

   public static final int DEFAULT_BLOCK_TICKRATE() {
      return 10;
   }

   public static final int REDSTONE_LAMP_ONOFF_DELAY_TICKS() {
      return 4;
   }

   public static final int MAX_REDSTONE_DISTANCE() {
      return 14;
   }

   public static final int MAX_REDSTONE_POWER_OUT() {
      return MAX_REDSTONE_DISTANCE() + 1;
   }

   public static final int HOPPER_COOLDOWN_TICKS() {
      return 8;
   }

   public static final int PLAYER_XP_CONSUME_COOLDOWN_TICKS() {
      return 2;
   }

   public static final int MAX_MATERIAL_ENCHANTIBILITY() {
      return ToolMaterial.GOLD.getEnchantability();
   }

   public static final int UNREPAIRABLE_TOOL_COST() {
      return 150;
   }

   public static final int HIGH_ANVIL_REPAIR_XP_LEVEL_COST() {
      return 39;
   }

   public static final int MAX_ANVIL_REPAIR_XP_LEVEL_COST() {
      return 50;
   }

   public static final int THRESHOLD_TOO_EXPENSIVE_REPAIR_XP_LEVEL_COST() {
      return 35;
   }

   public static final String I18N_ITEM_KEY_PREFIX() {
      return "item.";
   }

   public static final String I18N_BLOCK_KEY_PREFIX() {
      return "tile.";
   }

   public static final String I81N_KEY_SUFFIX() {
      return ".name";
   }

   public static final int SKELETON_SKULL_META() {
      return 0;
   }

   public static final int CREEPER_SKULL_META() {
      return 4;
   }

   public static final int WITHER_SKULL_META() {
      return 1;
   }

   public static final int ZOMBIE_SKULL_META() {
      return 2;
   }

   public static final int DRAGON_HEAD_META() {
      return 5;
   }

   public static final int PLAYER_SKULL_META() {
      return 3;
   }

   public static final int WHITE_BUNNY_TYPE_META() {
      return 1;
   }

   public static final int GOLD_BUNNY_TYPE_META() {
      return 4;
   }

   public static final int EVIL_BUNNY_TYPE_META() {
      return 99;
   }

   public static final int NUM_PARROT_TYPES() {
      return 5;
   }

   public static final int GREEN_PARROT_TYPE_META() {
      return 2;
   }

   public static final boolean isSolidParrotColor(int variant) {
      return variant != 0 || variant != 3;
   }

   public static final String DISCO_SHEEP_NAME() {
      return "jeb_";
   }

   public static final float LIVING_ENTITY_ONE_HEALTH_LEVEL() {
      return 2.0F;
   }

   public static final float DEFAULT_MAX_PLAYER_HEALTH() {
      return Math.max(20.0F, (float)SharedMonsterAttributes.MAX_HEALTH.getDefaultValue());
   }

   public static final float LIVING_ENTITY_ABSORPTION_PER_TICK() {
      return 4.0F;
   }

   public static final float DEFAULT_PLAYER_MAX_HEALTH_LEVEL() {
      return DEFAULT_MAX_PLAYER_HEALTH() / LIVING_ENTITY_ONE_HEALTH_LEVEL();
   }

   public static final int DEFAULT_LIQUID_USE_DURATION() {
      return 32;
   }

   public static final int DEFAULT_FOOD_CONSUME_DURATION() {
      return 32;
   }

   public static final int SHORT_FOOD_CONSUME_DURATION() {
      return 14;
   }

   public static final int HEARTY_FOOD_CONSUME_DURATION() {
      return 42;
   }

   public static final int HEARTY_FOOD_POINTS_THRESHOLD() {
      return 11;
   }

   public static final int PLAYER_ONE_FOOD_LEVEL() {
      return 2;
   }

   public static final int DEFAULT_MAX_PLAYER_FOOD_LEVEL() {
      return 10 * PLAYER_ONE_FOOD_LEVEL();
   }

   public static final float DEFAULT_MAX_PLAYER_SATURATION_LEVEL() {
      return 20.0F;
   }

   public static final int PLAYER_ONE_AIR_LEVEL() {
      return 30;
   }

   public static final int PLAYER_MAX_AIR_LEVEL() {
      return 10 * PLAYER_ONE_AIR_LEVEL();
   }

   public static final float LIGHT_TO_LEVEL_MULTIPLIER() {
      return 0.06666667F;
   }

   public static final float LEVEL_TO_LIGHT_MULTIPLIER() {
      return 15.0F;
   }

   public static final float HARDNESS_TO_RESISTENCE_MULTIPLIER() {
      return 5.0F;
   }

   public static final float RESISTENCE_TO_RESISTENCE_SETTER_MULTIPLIER() {
      return 0.33333334F;
   }

   public static final float BLAST_RESISTENCE_TO_RESISTENCE_MULTIPLIER() {
      return HARDNESS_TO_RESISTENCE_MULTIPLIER();
   }

   public static final float ENDERCHEST_HARDNESS() {
      return 23.0F;
   }

   public static final float OBSIDIAN_HARDNESS() {
      return 50.0F;
   }

   public static final float OBSIDIAN_EXPLOSION_RESISTANCE() {
      return 2000.0F;
   }

   public static final boolean isUnbreakable(@Nullable World world, @Nonnull IBlockState state, @Nullable BlockPos pos) {
      Block block = state.getBlock();
      if (block != Blocks.BEDROCK && block != Blocks.BARRIER && block != Blocks.END_PORTAL && block != Blocks.END_PORTAL_FRAME && block != Blocks.END_GATEWAY && block != Blocks.STRUCTURE_BLOCK && block != Blocks.STRUCTURE_VOID) {
         if (world != null && pos != null) {
            float hardness = state.getBlockHardness(world, pos);
            return hardness < 0.0F;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static final boolean isUnbreakableToAttackByExploders(Entity e) {
      boolean allowed = true;
      if (e instanceof EntityTNTPrimed || e instanceof EntityFireball || e instanceof EntityWeatherEffect || NPE.isaDragon(e, true) || NPE.isaWither(e, true) || e instanceof EntityGhast) {
         allowed = false;
      }

      return allowed;
   }

   public static final String AXE_TOOLNAME() {
      return "axe";
   }

   public static final String SHOVEL_TOOLNAME() {
      return "shovel";
   }

   public static final String PICKAXE_TOOLNAME() {
      return "pickaxe";
   }

   public static final String HOE_TOOLNAME() {
      return "hoe";
   }

   public static final String HAMMER_TOOLNAME() {
      return "hammer";
   }

   public static final String CRACKHAMMER_TOOLNAME() {
      return "crackhammer";
   }

   public static final String CLUB_TOOLNAME() {
      return "club";
   }

   public static final String SWORD_TOOLNAME() {
      return "sword";
   }

   public static final String MACHETE_TOOLNAME() {
      return "machete";
   }

   public static final String PAXEL_TOOLNAME() {
      return "paxel";
   }

   public static final String SLINGSHOT_TOOLNAME() {
      return "slingshot";
   }

   public static final String KNIFE_TOOLNAME() {
      return "knife";
   }

   public static final boolean isAxeOrShovelOrHoe(String type) {
      return PAXEL_TOOLNAME().equals(type) || AXE_TOOLNAME().equals(type) || SHOVEL_TOOLNAME().equals(type) || HOE_TOOLNAME().equals(type);
   }

   public static final boolean isBluntTool(String type) {
      return PAXEL_TOOLNAME().equals(type) || AXE_TOOLNAME().equals(type) || CLUB_TOOLNAME().equals(type) || HAMMER_TOOLNAME().equals(type);
   }

   public static final int STONE_TOOL_LEVEL() {
      return 1;
   }

   public static final int IRON_TOOL_LEVEL() {
      return 2;
   }

   public static final int DIAMOND_TOOL_LEVEL() {
      return 3;
   }

   public static final float MAX_TOOL_EFFICIENCY() {
      return 15.0F;
   }

   public static final boolean isMainHand(EntityEquipmentSlot slot) {
      return slot == EntityEquipmentSlot.MAINHAND;
   }

   public static final boolean isOffHand(EntityEquipmentSlot slot) {
      return slot == EntityEquipmentSlot.OFFHAND;
   }

   public static final boolean isEitherHand(EntityEquipmentSlot slot) {
      return slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND;
   }

   public static final int BASE_TOOL_LEVEL() {
      return 0;
   }

   public static final int JUNK_WEIGHT() {
      return 10;
   }

   public static final int UNCOMMON_WEIGHT() {
      return 3;
   }

   public static final int UNCOMMON_WEIGHT_CHESTS() {
      return 5;
   }

   public static final int TREASURE_WEIGHT() {
      return 1;
   }

   public static final AxisAlignedBB SOUL_SAND_AABB() {
      return __SOUL_SAND_AABB;
   }

   public static final SoundEvent SPARKLE_SOUND() {
      return SoundEvents.BLOCK_NOTE_HARP;
   }

   public static final SoundEvent EGG_THROWN_SOUND() {
      return SoundEvents.ENTITY_EGG_THROW;
   }

   public static final SoundEvent PEARL_THROWN_SOUND() {
      return SoundEvents.ENTITY_ENDERPEARL_THROW;
   }

   public static final SoundEvent SQUISHY_THROWN_SOUND() {
      return SoundEvents.ENTITY_SLIME_SQUISH;
   }

   public static final SoundEvent SNOWBALL_THROWN_SOUND() {
      return SoundEvents.ENTITY_SNOWBALL_THROW;
   }

   public static final SoundEvent RANDOM_CLICK_SOUND() {
      return SoundEvents.BLOCK_DISPENSER_FAIL;
   }

   public static final SoundEvent RANDOM_FIZZ_SOUND() {
      return SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE;
   }

   public static final SoundEvent PLAYER_BURP_SOUND() {
      return SoundEvents.ENTITY_PLAYER_BURP;
   }

   public static final SoundEvent XP_INTERACT_SOUND() {
      return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
   }

   public static final SoundEvent LOCKABLE_UNLOCKED_SOUND() {
      return SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN;
   }

   public static final SoundEvent LOCKABLE_LOCKED_SOUND() {
      return SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE;
   }

   public static final SoundEvent LIQUID_SCOOPED_SOUND(Material material) {
      return material == Material_lava ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
   }

   public static final SoundEvent LIQUID_PUTDOWN_SOUND(Material material) {
      return material == Material_lava ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
   }

   public static final int BONEMEAL_SPARKLE_SFX() {
      return 2005;
   }

   public static final int POTION_SFX() {
      return 2002;
   }

   public static final int DISPENSER_FAILED_SFX() {
      return 1001;
   }

   public static final int DISPENSER_SUCCEEDED_SFX() {
      return 1000;
   }

   public static final int DISPENSER_LAUNCHED_SFX() {
      return 1002;
   }

   public static final int BLOCK_BREAK_SFX() {
      return 2001;
   }

   public static final int ANVIL_LANDED_SFX() {
      return 1031;
   }

   public static final int PORTAL_TRAVEL_SFX() {
      return 1032;
   }

   public static final String GAMERULE_MOB_SPAWNING() {
      return "doMobSpawning";
   }

   public static final String GAMERULE_MOB_LOOT() {
      return "doMobLoot";
   }

   public static final String GAMERULE_TILE_DROPS() {
      return "doTileDrops";
   }

   public static final String GAMERULE_SILVERFISH() {
      return GAMERULE_TILE_DROPS();
   }

   public static final String GAMERULE_MOB_GRIEFING() {
      return "mobGriefing";
   }

   public static final boolean allowGriefing(@Nonnull World world, @Nullable Entity from, GriefingType type) {
      if (type != GriefingType.THEFT && type != GriefingType.CONTAMINATION) {
         boolean is;
         try {
            is = ForgeEventFactory.getMobGriefingEvent(world, from);
         } catch (Exception var5) {
            is = world.getGameRules().getBoolean(GAMERULE_MOB_GRIEFING());
         }

         return is;
      } else {
         return true;
      }
   }

   public static final int FULL_100PERCENT_FLAMMABILITY() {
      return 300;
   }

   public static final int CREEPER_NO_BOOM_STATE() {
      return -1;
   }

   public static final int SLURRY_HIT_DAMAGE() {
      return 50;
   }

   public static final int MAX_HIT_DAMAGE() {
      return 200;
   }

   public static final int CHILD_SPAWN_AGE() {
      return -24000;
   }

   public static final int DEFAULT_XP_PICKUP_COOLDOWN_TICKS() {
      return 2;
   }

   public static final int DEFAULT_DROPPED_ITEM_LIFESPAN_TICKS() {
      return 300 * SECS_TO_TICKS_DURATION_MULTIPLIER();
   }

   public static final String DEFAULT_NONBOOK_ENCHANTS_TAGNAME() {
      return "ench";
   }

   public static final String DEFAULT_BOOK_ENCHANTS_TAGNAME() {
      return "StoredEnchantments";
   }

   public static final String DEFAULT_LOCKCODE_TAGNAME() {
      return "Lock";
   }

   public static final String DEFAULT_ITEMLIST_TAGNAME() {
      return "Items";
   }

   public static final String DEFAULT_CUSTOM_NAME_TAGNAME() {
      return "CustomName";
   }

   public static final String DEFAULT_LOOT_TABLE_TAGNAME() {
      return "LootTable";
   }

   public static final String DEFAULT_LOOT_TABLE_SEED_TAGNAME() {
      return "LootTableSeed";
   }

   public static String ITEM_INFO_HIDDEN_TAGNAME() {
      return "HideFlags";
   }

   public static final String CUSTOM_POTION_EFFECTS_TAGNAME() {
      return "CustomPotionEffects";
   }

   public static final String CUSTOM_POTION_COLOR_TAGNAME() {
      return "CustomPotionColor";
   }

   public static final String CUSTOM_POTION_UNLOCALIZED_TAGNAME() {
      return "CustomPotionLocName";
   }

   public static final String BLOCK_TILEENTITY_NBT_TAGNAME() {
      return "BlockEntityTag";
   }

   public static final String CUSTOM_COLOR_TAGNAME() {
      return "CustomColorRgb";
   }

   public static final String CUSTOM_ATTRIBUTE_MODIFIERS_TAGNAME() {
      return "AttributeModifiers";
   }

   public static final String WEAPON_MODIFIER_ATTRNAME() {
      return "Weapon modifier";
   }

   public static final String TOOL_MODIFIER_ATTRNAME() {
      return "Tool modifier";
   }

   public static final String ARMOR_MODIFIER_ATTRNAME() {
      return "Armor modifier";
   }

   public static final String ARMOR_TOUGHNESS_MODIFIER_ATTRNAME() {
      return "Armor toughness";
   }

   public static final boolean ingredientDefined(@Nullable String name) {
      return name != null && !OreDictionary.getOres(name, false).isEmpty();
   }

   public static final boolean ingredientDefinedLooking(@Nullable String name) {
      return name != null && OreDictionary.doesOreNameExist(name) && ingredientDefined(name);
   }

   public static final boolean isLightweightDifficulty(@Nonnull World world) {
      return world.getDifficulty() == EnumDifficulty.PEACEFUL || world.getDifficulty() == EnumDifficulty.EASY;
   }

   public static final boolean isDeobfuscatedEnvironment() {
      return ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
   }

   public static final boolean isZeroishAbsolute(float f) {
      return Math.abs(f) < 1.0E-4F;
   }

   public static final boolean isaWorld(@Nullable IBlockAccess aworld) {
      return aworld instanceof World;
   }

   public static final boolean isaServerWorld(@Nullable IBlockAccess aworld) {
      return aworld instanceof World && !((World)aworld).isRemote;
   }

   public static final boolean isaClientWorld(@Nullable IBlockAccess aworld) {
      return aworld instanceof World && ((World)aworld).isRemote;
   }

   public static final boolean isaPlayer(@Nullable Entity entity) {
      return entity instanceof EntityPlayer;
   }

   public static final boolean isaFakePlayer(@Nullable Entity entity, boolean nullIsFake) {
      return entity == null ? nullIsFake : entity instanceof FakePlayer;
   }

   public static final boolean isLivingBeing(@Nullable Entity entity) {
      return entity == null ? false : entity instanceof EntityLiving || entity instanceof EntityPlayer;
   }

   @Nullable
   public static final EntityLiving toMob(@Nullable Entity entity, boolean includeBoss) {
      if (!(entity instanceof EntityLiving)) {
         return null;
      } else {
         EntityLiving livingEntity = null;
         if (!entity.isCreatureType(EnumCreatureType.MONSTER, false) && entity.getSoundCategory() != SoundCategory.HOSTILE) {
            ResourceLocation eId = EntityList.getKey(entity);
            if (NPE.isModdedBadGuy(eId)) {
               livingEntity = (EntityLiving)entity;
            }
         } else {
            livingEntity = (EntityLiving)entity;
         }

         return livingEntity != null ? (includeBoss ? livingEntity : (entity.isNonBoss() ? livingEntity : null)) : null;
      }
   }

   public static final boolean isMonster(@Nullable Entity entity) {
      return toMob(entity, false) != null;
   }

   public static final boolean isUnfriendly(@Nullable Entity entity, @Nullable EntityLivingBase wrt) {
      if (entity == null) {
         return false;
      } else {
         ResourceLocation rid = EntityList.getKey(entity);
         if (ModIntegration.ELEMENTAL_CREEPERS.belongsTo(rid)) {
            return entity instanceof EntityTameable ? !((EntityTameable)entity).isTamed() : true;
         } else {
            if (ModIntegration.ANCIENT_WARFARE.belongsTo(rid)) {
               Class<?> klass = entity.getClass();
               if (klass.getName().contains("ancientwarfare.npc.entity.")) {
                  boolean is = false;
                  if (wrt != null) {
                     try {
                        Method m = klass.getMethod("isHostileTowards", Entity.class);
                        is = Boolean.TRUE.equals(m.invoke(entity, wrt));
                     } catch (Exception var6) {
                        ;
                     }
                  }

                  return is;
               }
            }

            return entity instanceof EntityWolf && ((EntityWolf)entity).isAngry() || wrt != null && entity instanceof EntityIronGolem && ((EntityIronGolem)entity).getAttackTarget() == wrt || entity instanceof EntityRabbit && ((EntityRabbit)entity).getRabbitType() == EVIL_BUNNY_TYPE_META();
         }
      }
   }

   public static final boolean isMonsterOrPiOoed(@Nullable Entity entity) {
      return isMonster(entity) || isUnfriendly(entity, (EntityLivingBase)null);
   }

   public static final boolean isMonsterOrPiOoed(@Nullable Entity entity, @Nullable EntityLivingBase wrt) {
      return isMonster(entity) || isUnfriendly(entity, wrt);
   }

   public static final boolean isAnyMonster(@Nullable Entity entity) {
      return toMob(entity, true) != null;
   }

   public static final boolean isAnyMonsterOrPiOoed(@Nullable Entity entity) {
      return isAnyMonster(entity) || isUnfriendly(entity, (EntityLivingBase)null);
   }

   public static final boolean isAnyMonsterOrPiOoed(@Nullable Entity entity, @Nullable EntityLivingBase wrt) {
      return isAnyMonster(entity) || isUnfriendly(entity, wrt);
   }

   public static final boolean isBoss(@Nullable Entity entity) {
      return entity != null && !entity.isNonBoss();
   }

   public static final boolean isBossLike(@Nullable Entity entity) {
      return entity != null && (!entity.isNonBoss() || entity instanceof EntityGiantZombie || entity instanceof EntityLiving && ((EntityLiving)entity).getMaxHealth() > 199.0F);
   }

   public static final EntityLiving toCreature(@Nullable Entity entity) {
      return entity instanceof EntityLiving && entity.isCreatureType(EnumCreatureType.CREATURE, false) ? (EntityLiving)entity : null;
   }

   public static final boolean isLivingHorseOrPackAnimal(@Nullable Entity entity) {
      return entity instanceof EntityHorse || entity instanceof AbstractChestHorse;
   }

   public static final boolean isPackAnimalCarrying(Entity entity) {
      return entity instanceof AbstractChestHorse && ((AbstractChestHorse)entity).hasChest();
   }

   public static final boolean isRealPlayer(@Nullable Entity entity) {
      return entity instanceof EntityPlayer && !(entity instanceof FakePlayer) && !((EntityPlayer)entity).isEntityInvulnerable(DamageSource_void);
   }

   public static final boolean isRealNonSpectatingPlayer(@Nullable Entity entity) {
      return isRealPlayer(entity) && !((EntityPlayer)entity).isSpectator();
   }

   public static final boolean isEntityAlive(@Nonnull EntityLivingBase entity, boolean checkHealth) {
      return !entity.isDead && (!checkHealth || entity.getHealth() > 0.0F);
   }

   @Nullable
   public static final EntityLivingBase findEntityByUUID(@Nonnull World world, @Nonnull UUID uuid) {
      if (uuid != null && world != null) {
         List<EntityLivingBase> hit = world.getEntities(EntityLivingBase.class, (e) -> {
            return uuid.equals(e.getPersistentID());
         });
         return hit.size() == 1 ? (EntityLivingBase)hit.get(0) : null;
      } else {
         return null;
      }
   }

   public static final void copySlipperiness(@Nonnull Block block, @Nonnull Block source) {
      block.slipperiness = source.slipperiness;
   }

   public static final NBTTagCompound getPlayerPersistedData(@Nonnull EntityPlayer player) {
      NBTTagCompound persisted = player.getEntityData().getCompoundTag("PlayerPersisted");
      if (!player.getEntityData().hasKey("PlayerPersisted")) {
         player.getEntityData().setTag("PlayerPersisted", persisted);
      }

      return persisted;
   }

   public static final NBTTagCompound getPlayerPersistedDataIfPresent(@Nonnull EntityPlayer player) {
      NBTTagCompound persisted = null;
      if (player.getEntityData().hasKey("PlayerPersisted")) {
         persisted = player.getEntityData().getCompoundTag("PlayerPersisted");
      }

      return persisted;
   }

   public static final boolean getPlayerInCreative(@Nonnull EntityPlayer player) {
      return player.capabilities.isCreativeMode;
   }

   public static final boolean getPlayerInSurvival(@Nonnull EntityPlayer player) {
      return !player.capabilities.isCreativeMode;
   }

   public static final boolean getPlayerCanFly(@Nonnull EntityPlayer player, boolean elytra) {
      if (!player.capabilities.allowFlying && (!elytra || !player.isElytraFlying())) {
         if (elytra) {
            ItemStack chestpiece = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (chestpiece.getItem() == Items.ELYTRA && ItemElytra.isUsable(chestpiece)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static final boolean isEntityOffGround(@Nonnull EntityLivingBase entity) {
      if (!entity.onGround) {
         return true;
      } else {
         EntityPlayer player = getRealPlayerOrNull(entity);
         return player != null && player.capabilities.isFlying;
      }
   }

   public static final boolean canPlayerPickupXp(@Nonnull EntityPlayer player) {
      return player.xpCooldown <= 0;
   }

   public static final void setPlayerXpPickupCooldownPeriod(@Nonnull EntityPlayer player, int multiplier) {
      if (multiplier <= 0) {
         multiplier = 1;
      }

      player.xpCooldown = multiplier * DEFAULT_XP_PICKUP_COOLDOWN_TICKS();
   }

   public static final void setSurvivalModeFlightSpeed(@Nonnull EntityPlayer player, float value) {
      if (!player.capabilities.isCreativeMode && !player.isSpectator()) {
         NBTTagCompound c0 = new NBTTagCompound();
         player.capabilities.writeCapabilitiesToNBT(c0);
         if (c0.hasKey("abilities", 10)) {
            NBTTagCompound c1 = c0.getCompoundTag("abilities");
            if (value <= 0.0F) {
               value = (new PlayerCapabilities()).getFlySpeed();
            }

            c1.setFloat("flySpeed", value);
            player.capabilities.readCapabilitiesFromNBT(c0);
         }
      }

   }

   public static final EnumHand getActiveHandOrMain(@Nonnull EntityPlayer player) {
      EnumHand hand = player.getActiveHand();
      if (hand == null) {
         hand = EnumHand.MAIN_HAND;
      }

      return hand;
   }

   @Nullable
   public static final ItemStack getPlayerHeldItem(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack helditem = player.getHeldItem(hand);
      return ItemStacks.isEmpty(helditem) ? null : helditem;
   }

   @Nullable
   public static final ItemStack getPlayerUsingItem(@Nonnull EntityPlayer player) {
      return getPlayerHeldItem(player, getActiveHandOrMain(player));
   }

   @Nullable
   public static final Item getPlayerUsingItemItem(@Nonnull EntityPlayer player) {
      ItemStack helditem = getPlayerHeldItem(player, getActiveHandOrMain(player));
      return helditem != null ? helditem.getItem() : null;
   }

   public static final int getPlayerUsingItemSlotIndex(@Nonnull EntityPlayer player, @Nullable EnumHand hand) {
      if (hand == null) {
         hand = getActiveHandOrMain(player);
      }

      int i = player.inventory.currentItem;
      if (hand != EnumHand.MAIN_HAND) {
         i = player.inventory.getSizeInventory() - 1;
      }

      return i;
   }

   @Nullable
   public static final EntityPlayer getPlayerOrNull(@Nullable Entity entity) {
      return entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
   }

   @Nullable
   public static final EntityPlayerMP getPlayerMPOrNull(@Nullable EntityPlayer player) {
      return player instanceof EntityPlayerMP ? (EntityPlayerMP)player : null;
   }

   @Nullable
   public static final EntityPlayer getRealPlayerOrNull(@Nullable Entity entity) {
      return isRealNonSpectatingPlayer(entity) ? (EntityPlayer)entity : null;
   }

   public static final boolean hasPotionsInEffect(@Nonnull EntityLivingBase entity) {
      return entity != null && !entity.getActivePotionMap().isEmpty();
   }

   public static final boolean isAffectingPlayer(ItemStack stack, EntityPlayer player, int slotindex) {
      return InventoryPlayer.isHotbar(slotindex) || player.getHeldItemOffhand() == stack || player.getHeldItemMainhand() == stack;
   }

   public static final boolean inHotbarOrOffhand(Predicate<ItemStack> test, @Nonnull EntityPlayer player) {
      ItemStack stack = player.getHeldItemOffhand();
      if (test.apply(stack)) {
         return true;
      } else {
         int N = PLAYER_HOTBAR_SIZE();

         for(int i = 0; i < N; ++i) {
            stack = player.inventory.getStackInSlot(i);
            if (test.apply(stack)) {
               return true;
            }
         }

         return false;
      }
   }

   @Nonnull
   public static final ItemStack isAffectingPlayer(Predicate<ItemStack> test, @Nonnull EntityPlayer player, boolean includeArmor, boolean includeBaubles) {
      if (test == null) {
         return ItemStack.EMPTY;
      } else {
         if (includeBaubles && Loader.isModLoaded(ModIntegration.BAUBLES.modid())) {
            ItemStack bauble = BaublesHelper.findBauble(test, player);
            if (!bauble.isEmpty()) {
               return bauble;
            }
         }

         if (includeArmor) {
            for(ItemStack armorpiece : player.getArmorInventoryList()) {
               if (test.apply(armorpiece)) {
                  return armorpiece;
               }
            }
         }

         ItemStack stack = player.getHeldItemOffhand();
         if (test.apply(stack)) {
            return stack;
         } else {
            stack = player.getHeldItemMainhand();
            if (test.apply(stack)) {
               return stack;
            } else {
               int N = PLAYER_HOTBAR_SIZE();

               for(int i = 0; i < N; ++i) {
                  stack = player.inventory.getStackInSlot(i);
                  if (test.apply(stack)) {
                     return stack;
                  }
               }

               return ItemStack.EMPTY;
            }
         }
      }
   }

   @Nonnull
   public static final ItemStack getAffectingPlayer(final Item item, EntityPlayer player, boolean includeArmor, boolean includeBaubles) {
      ItemStack hit = isAffectingPlayer(new Predicate<ItemStack>() {
         public boolean apply(ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() == item;
         }
      }, player, includeArmor, includeBaubles);
      return hit;
   }

   public static final boolean isAffectingPlayer(Item item, EntityPlayer player, boolean includeArmor, boolean includeBaubles) {
      return !getAffectingPlayer(item, player, includeArmor, includeBaubles).isEmpty();
   }

   @Nonnull
   public static final List<ItemStack> getAffectingEntity(final Predicate<ItemStack> test, @Nonnull EntityLivingBase entity, boolean includeArmor, boolean includeBaubles) {
      NonNullList<ItemStack> out = NonNullList.create();
      if (test == null) {
         return out;
      } else {
         EntityPlayer player = getRealPlayerOrNull(entity);
         if (player != null && includeBaubles && Loader.isModLoaded(ModIntegration.BAUBLES.modid())) {
            BaublesHelper.iterateBaubles(new Function<BaublesHelper.OwnedBauble, List<ItemStack>>() {
               public List<ItemStack> apply(BaublesHelper.OwnedBauble input) {
                  List<ItemStack> l = (List)List.class.cast(input.ticket);
                  if (test.apply(input.bauble)) {
                     l.add(input.bauble);
                  }

                  return l;
               }
            }, player, out);
         }

         if (includeArmor) {
            for(ItemStack armorpiece : entity.getArmorInventoryList()) {
               if (test.apply(armorpiece)) {
                  out.add(armorpiece);
               }
            }
         }

         ItemStack stack = entity.getHeldItemOffhand();
         if (test.apply(stack)) {
            out.add(stack);
         }

         stack = entity.getHeldItemMainhand();
         if (test.apply(stack)) {
            out.add(stack);
         }

         int N = PLAYER_HOTBAR_SIZE();
         if (player != null) {
            for(int i = 0; i < N; ++i) {
               if (i != player.inventory.currentItem) {
                  stack = player.inventory.getStackInSlot(i);
                  if (test.apply(stack)) {
                     out.add(stack);
                  }
               }
            }
         }

         return out;
      }
   }

   @Nonnull
   public static final List<ItemStack> getAffectingEntity(EntityLivingBase entity, boolean includeArmor, boolean includeBaubles) {
      return getAffectingEntity(new Predicate<ItemStack>() {
         public boolean apply(ItemStack stack) {
            return !stack.isEmpty();
         }
      }, entity, includeArmor, includeBaubles);
   }

   @Nullable
   public static final <T> T getCapability(@Nullable ICapabilityProvider provider, Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(provider != null && provider.hasCapability(capability, facing) ? provider.getCapability(capability, facing) : null);
   }

   @Nullable
   public static final IItemHandler getItemHandlerCapability(@Nullable ICapabilityProvider provider, @Nullable EnumFacing facing) {
      return (IItemHandler)getCapability(provider, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
   }

   public static final void consumeItemFromStack(EntityPlayer player, ItemStack stack, EnumHand hand, boolean updateHUD) {
      if (getPlayerInSurvival(player)) {
         if (ItemStacks.decrBy(stack, 1)) {
            int i = player.inventory.currentItem;
            if (hand != EnumHand.MAIN_HAND) {
               i = player.inventory.getSizeInventory() - 1;
            }

            player.inventory.setInventorySlotContents(i, ItemStacks.NULLSTACK());
         }

         if (updateHUD) {
            refreshPlayerHUD(player);
         }
      }

   }

   public static final void refreshPlayerHUD(@Nonnull EntityPlayer player) {
      player.inventory.markDirty();
      if (player instanceof EntityPlayerMP) {
         ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
      }

   }

   public static <E> NonNullList<E> emptyNonNullList() {
      return NonNullList.create();
   }

   public static NonNullList<ItemStack> emptyNonNullItemStackList(int size) {
      return NonNullList.withSize(size, ItemStack.EMPTY);
   }

   protected SharedGlue() {
   }

   public static final void init0() {
   }

   public static final class IsSameStack implements Predicate<ItemStack> {
      @Nonnull
      private final ItemStack _match;

      public IsSameStack(@Nonnull ItemStack check) {
         this._match = check;
      }

      public boolean apply(ItemStack stack) {
         return stack != null && stack == this._match;
      }
   }
}
