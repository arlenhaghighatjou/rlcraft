package mcjty.lostcities.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import mcjty.lostcities.LostCities;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class LostCityProfile {
   public static final String CATEGORY_LOSTCITY = "lostcity";
   public static final String CATEGORY_STRUCTURES = "structures";
   public static final String CATEGORY_EXPLOSIONS = "explosions";
   public static final String CATEGORY_CITIES = "cities";
   public static final String CATEGORY_CITY_SPHERES = "cityspheres";
   public static final String CATEGORY_CLIENT = "client";
   private final String name;
   private final Optional<LostCityProfile> inheritFrom;
   private final boolean isPublic;
   private String description = "Default generation, common cities, explosions";
   private String extraDescription = "";
   private String worldStyle = "standard";
   private String iconFile = "";
   private ResourceLocation icon;
   public int DEBRIS_TO_NEARBYCHUNK_FACTOR = 200;
   private String LIQUID_BLOCK = "minecraft:water";
   private IBlockState liquidBlock = null;
   private String BASE_BLOCK = "minecraft:stone";
   private IBlockState baseBlock = null;
   public float VINE_CHANCE = 0.009F;
   public float CHANCE_OF_RANDOM_LEAFBLOCKS = 0.1F;
   public int THICKNESS_OF_RANDOM_LEAFBLOCKS = 2;
   public boolean AVOID_FOLIAGE = false;
   public boolean AVOID_GENERATED_FLOWERS = false;
   public boolean AVOID_GENERATED_MUSHROOMS = false;
   public boolean AVOID_GENERATED_REEDS = false;
   public boolean AVOID_GENERATED_LILYPADS = false;
   public boolean AVOID_GENERATED_GRASS = false;
   public boolean AVOID_GENERATED_PUMPKINS = false;
   public boolean AVOID_GENERATED_CACTII = false;
   public boolean AVOID_GENERATED_TREES = false;
   public boolean AVOID_GENERATED_LAKE_WATER = false;
   public boolean AVOID_GENERATED_DESERT_WELL = false;
   public boolean AVOID_GENERATED_FOSSILS = false;
   public boolean RUBBLELAYER = true;
   public float RUBBLE_DIRT_SCALE = 3.0F;
   public float RUBBLE_LEAVE_SCALE = 6.0F;
   public boolean RUINS = true;
   public float RUIN_CHANCE = 0.05F;
   public float RUIN_MINLEVEL_PERCENT = 0.8F;
   public float RUIN_MAXLEVEL_PERCENT = 1.0F;
   public int GROUNDLEVEL = 71;
   public int WATERLEVEL_OFFSET = 8;
   public boolean HIGHWAY_REQUIRES_TWO_CITIES = true;
   public int HIGHWAY_LEVEL_FROM_CITIES_MODE = 0;
   public float HIGHWAY_MAINPERLIN_SCALE = 50.0F;
   public float HIGHWAY_SECONDARYPERLIN_SCALE = 10.0F;
   public float HIGHWAY_PERLIN_FACTOR = 2.0F;
   public int HIGHWAY_DISTANCE_MASK = 7;
   public boolean HIGHWAY_SUPPORTS = true;
   public float RAILWAY_DUNGEON_CHANCE = 0.01F;
   public boolean RAILWAYS_CAN_END = false;
   public boolean RAILWAYS_ENABLED = true;
   public boolean RAILWAY_STATIONS_ENABLED = true;
   public float DESTROY_LONE_BLOCKS_FACTOR = 0.05F;
   public float DESTROY_OR_MOVE_CHANCE = 0.4F;
   public int DESTROY_SMALL_SECTIONS_SIZE = 50;
   public boolean EXPLOSIONS_IN_CITIES_ONLY = true;
   public boolean GENERATE_SPAWNERS = true;
   public boolean GENERATE_LOOT = true;
   public boolean GENERATE_LIGHTING = false;
   public boolean AVOID_WATER = false;
   public float EXPLOSION_CHANCE = 0.002F;
   public int EXPLOSION_MINRADIUS = 15;
   public int EXPLOSION_MAXRADIUS = 35;
   public int EXPLOSION_MINHEIGHT = 75;
   public int EXPLOSION_MAXHEIGHT = 90;
   public float MINI_EXPLOSION_CHANCE = 0.03F;
   public int MINI_EXPLOSION_MINRADIUS = 5;
   public int MINI_EXPLOSION_MAXRADIUS = 12;
   public int MINI_EXPLOSION_MINHEIGHT = 60;
   public int MINI_EXPLOSION_MAXHEIGHT = 100;
   public float CITY_CHANCE = 0.02F;
   public int CITY_MINRADIUS = 50;
   public int CITY_MAXRADIUS = 128;
   public float CITY_THRESSHOLD = 0.2F;
   public float CITYSPHERE_FACTOR = 1.2F;
   public float CITYSPHERE_CHANCE = 0.7F;
   public float CITYSPHERE_SURFACE_VARIATION = 1.0F;
   public float CITYSPHERE_OUTSIDE_SURFACE_VARIATION = 1.0F;
   public float CITYSPHERE_MONORAIL_CHANCE = 0.8F;
   public int CITYSPHERE_OUTSIDE_GROUNDLEVEL = -1;
   public boolean CITYSPHERE_LANDSCAPE_OUTSIDE = false;
   public String CITYSPHERE_OUTSIDE_PROFILE = "";
   public boolean CITYSPHERE_ONLY_PREDEFINED = false;
   public int CITYSPHERE_MONORAIL_HEIGHT_OFFSET = -2;
   public boolean CITYSPHERE_SINGLE_BIOME = false;
   public int CITY_LEVEL0_HEIGHT = 75;
   public int CITY_LEVEL1_HEIGHT = 83;
   public int CITY_LEVEL2_HEIGHT = 91;
   public int CITY_LEVEL3_HEIGHT = 99;
   public Float CITY_DEFAULT_BIOME_FACTOR = 1.0F;
   public String[] CITY_BIOME_FACTORS = new String[]{"river=0", "frozen_river=0", "ocean=.7", "frozen_ocean=.7", "deep_ocean=.4"};
   public Map<String, Float> biomeFactorMap = null;
   public String GENERATOR_OPTIONS = "";
   public String[] ALLOWED_BIOME_FACTORS = new String[0];
   public String[] MANUAL_BIOME_MAPPINGS = new String[0];
   public BiomeSelectionStrategy BIOME_SELECTION_STRATEGY = BiomeSelectionStrategy.ORIGINAL;
   public float CHEST_WITHOUT_LOOT_CHANCE = 0.2F;
   public float BUILDING_WITHOUT_LOOT_CHANCE = 0.2F;
   public float BUILDING_CHANCE = 0.3F;
   public int BUILDING_MINFLOORS = 0;
   public int BUILDING_MAXFLOORS = 9;
   public int BUILDING_MINFLOORS_CHANCE = 4;
   public int BUILDING_MAXFLOORS_CHANCE = 6;
   public int BUILDING_MINCELLARS = 0;
   public int BUILDING_MAXCELLARS = 4;
   public float BUILDING_DOORWAYCHANCE = 0.6F;
   public float BUILDING_FRONTCHANCE = 0.2F;
   public float LIBRARY_CHANCE = 0.1F;
   public float DATACENTER_CHANCE = 0.1F;
   public float PARK_CHANCE = 0.2F;
   public float CORRIDOR_CHANCE = 0.7F;
   public float BRIDGE_CHANCE = 0.7F;
   public float FOUNTAIN_CHANCE = 0.05F;
   public float BUILDING2X2_CHANCE = 0.03F;
   public boolean BRIDGE_SUPPORTS = true;
   public int BEDROCK_LAYER = 1;
   public boolean GENERATE_VILLAGES = true;
   public boolean GENERATE_CAVES = true;
   public boolean GENERATE_RAVINES = true;
   public boolean GENERATE_MINESHAFTS = true;
   public boolean GENERATE_STRONGHOLDS = true;
   public boolean GENERATE_SCATTERED = true;
   public boolean GENERATE_OCEANMONUMENTS = true;
   public boolean GENERATE_MANSIONS = true;
   public boolean GENERATE_LAKES = true;
   public boolean GENERATE_DUNGEONS = true;
   public float HORIZON = -1.0F;
   public float FOG_RED = -1.0F;
   public float FOG_GREEN = -1.0F;
   public float FOG_BLUE = -1.0F;
   public float FOG_DENSITY = -1.0F;
   public String SPAWN_BIOME = "";
   public String SPAWN_CITY = "";
   public String SPAWN_SPHERE = "";
   public boolean SPAWN_NOT_IN_BUILDING = false;
   public int MAX_CAVE_HEIGHT = 128;
   public LandscapeType LANDSCAPE_TYPE = LandscapeType.DEFAULT;
   public boolean PREVENT_VILLAGES_IN_CITIES = true;
   public boolean PREVENT_LAKES_RAVINES_IN_CITIES = false;
   private String categoryLostcity;
   private String categoryStructures;
   private String categoryExplosions;
   private String categoryCities;
   private String categoryCitySpheres;
   private String categoryClient;

   public LostCityProfile(String name, boolean isPublic) {
      this.name = name;
      this.inheritFrom = Optional.empty();
      this.isPublic = isPublic;
   }

   public LostCityProfile(String name, LostCityProfile inheritFrom, boolean isPublic) {
      this.name = name;
      this.inheritFrom = Optional.ofNullable(inheritFrom);
      this.isPublic = isPublic;
   }

   public void setIconFile(String iconFile) {
      this.iconFile = iconFile;
   }

   public ResourceLocation getIcon() {
      if (this.icon != null) {
         return this.icon;
      } else if (this.iconFile != null && !this.iconFile.isEmpty()) {
         this.icon = new ResourceLocation("lostcities", this.iconFile);
         return this.icon;
      } else {
         return null;
      }
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   public void init(Configuration cfg) {
      this.categoryLostcity = "lostcity_" + this.name;
      this.categoryStructures = "structures_" + this.name;
      this.categoryExplosions = "explosions_" + this.name;
      this.categoryCities = "cities_" + this.name;
      this.categoryCitySpheres = "cityspheres_" + this.name;
      this.categoryClient = "client_" + this.name;
      cfg.addCustomCategoryComment(this.categoryLostcity, "Settings related to the Lost City for the " + this.name + " profile");
      cfg.addCustomCategoryComment(this.categoryStructures, "Settings related to structure generation for the " + this.name + " profile");
      cfg.addCustomCategoryComment(this.categoryExplosions, "Settings related to explosions and damage for the " + this.name + " profile");
      cfg.addCustomCategoryComment(this.categoryCities, "Settings related to city generation for the " + this.name + " profile");
      cfg.addCustomCategoryComment(this.categoryCitySpheres, "Settings related to city sphere generation for the " + this.name + " profile");
      cfg.addCustomCategoryComment(this.categoryClient, "Client side settings for the " + this.name + " profile");
      this.initLostcity(cfg);
      this.initExplosions(cfg);
      this.initStructures(cfg);
      this.initCities(cfg);
      this.initCitySpheres(cfg);
      this.initClient(cfg);
   }

   public String getCategoryCitySpheres() {
      return this.categoryCitySpheres;
   }

   public String getCategoryLostcity() {
      return this.categoryLostcity;
   }

   private void initClient(Configuration cfg) {
      this.HORIZON = cfg.getFloat("horizon", this.categoryClient, ((LostCityProfile)this.inheritFrom.orElse(this)).HORIZON, -1.0F, 256.0F, "This is used client-side (but only if the client has this mod) to set the height of the horizon");
      this.FOG_RED = cfg.getFloat("fogRed", this.categoryClient, ((LostCityProfile)this.inheritFrom.orElse(this)).FOG_RED, -1.0F, 1.0F, "This is used client-side (but only if the client has this mod) for the fog color");
      this.FOG_GREEN = cfg.getFloat("fogGreen", this.categoryClient, ((LostCityProfile)this.inheritFrom.orElse(this)).FOG_GREEN, -1.0F, 1.0F, "This is used client-side (but only if the client has this mod) for the fog color");
      this.FOG_BLUE = cfg.getFloat("fogBlue", this.categoryClient, ((LostCityProfile)this.inheritFrom.orElse(this)).FOG_BLUE, -1.0F, 1.0F, "This is used client-side (but only if the client has this mod) for the fog color");
      this.FOG_DENSITY = cfg.getFloat("fogDensity", this.categoryClient, ((LostCityProfile)this.inheritFrom.orElse(this)).FOG_DENSITY, -1.0F, 1.0F, "This is used client-side (but only if the client has this mod) for the fog density");
   }

   private void initCitySpheres(Configuration cfg) {
      this.CITYSPHERE_FACTOR = cfg.getFloat("citySphereFactor", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_FACTOR, 0.1F, 10.0F, "Only used in 'space' landscape. This factor will be multiplied with the radius of the city to calculate the radius of the outer sphere");
      this.CITYSPHERE_CHANCE = cfg.getFloat("citySphereChance", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_CHANCE, 0.0F, 1.0F, "The chance that a city sphere will be generated");
      this.CITYSPHERE_SURFACE_VARIATION = cfg.getFloat("sphereSurfaceVariation", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_SURFACE_VARIATION, 0.0F, 1.0F, "Smaller numbers make the surface inside a city sphere more varied");
      this.CITYSPHERE_OUTSIDE_SURFACE_VARIATION = cfg.getFloat("outsideSurfaceVariation", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_OUTSIDE_SURFACE_VARIATION, 0.0F, 1.0F, "Smaller numbers make the surface outside a city sphere more varied");
      this.CITYSPHERE_MONORAIL_CHANCE = cfg.getFloat("monorailChance", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_MONORAIL_CHANCE, 0.0F, 1.0F, "The chance that a city will have a monorail connection in a certain direction. There will only be an actual connection if there is a city in that direction that also wants a monorail");
      this.CITYSPHERE_LANDSCAPE_OUTSIDE = cfg.getBoolean("landscapeOutside", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_LANDSCAPE_OUTSIDE, "If this is true then there will be a landscape outside the city spheres");
      this.CITYSPHERE_ONLY_PREDEFINED = cfg.getBoolean("onlyPredefined", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_ONLY_PREDEFINED, "If this is true then only predefined spheres are generated");
      this.CITYSPHERE_OUTSIDE_GROUNDLEVEL = cfg.getInt("outsideGroundLevel", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_OUTSIDE_GROUNDLEVEL, -1, 256, "Ground level for outside city spheres (DEPRECATED, USE GROUNDLEVEL OF OTHER PROFILE)");
      this.CITYSPHERE_OUTSIDE_PROFILE = cfg.getString("outsideProfile", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_OUTSIDE_PROFILE, "An optional profile to use for the outside world");
      this.CITYSPHERE_MONORAIL_HEIGHT_OFFSET = cfg.getInt("monorailOffset", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_MONORAIL_HEIGHT_OFFSET, -100, 100, "Offset compared to main height");
      this.CITYSPHERE_SINGLE_BIOME = cfg.getBoolean("singleBiome", this.categoryCitySpheres, ((LostCityProfile)this.inheritFrom.orElse(this)).CITYSPHERE_SINGLE_BIOME, "If this is true then every city sphere will be limited to one (random) biome");
   }

   private void initLostcity(Configuration cfg) {
      this.description = cfg.getString("description", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).description, "The description of this profile");
      this.extraDescription = cfg.getString("extraDescription", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).extraDescription, "Additional information");
      this.worldStyle = cfg.getString("worldStyle", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).worldStyle, "The worldstyle used by this profile (defined in the assets)");
      this.iconFile = cfg.getString("icon", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).iconFile, "The icon to use in the configuration screen (64x64)");
      this.LIQUID_BLOCK = cfg.getString("liquidBlock", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).LIQUID_BLOCK, "Block to use as a liquid");
      this.BASE_BLOCK = cfg.getString("baseBlock", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BASE_BLOCK, "Block to use as the worldgen base");
      this.SPAWN_BIOME = cfg.getString("spawnBiome", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).SPAWN_BIOME, "When this is set the player will always spawn in the given biome");
      this.SPAWN_CITY = cfg.getString("spawnCity", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).SPAWN_CITY, "When this is set the player will always spawn in the given predefined city");
      this.SPAWN_SPHERE = cfg.getString("spawnSphere", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).SPAWN_SPHERE, "When this is set the player will always spawn in the given predefined sphere. If you use <in> the player will always spawn in a random sphere. If you use <out> the player will always spawn outside a sphere");
      this.SPAWN_NOT_IN_BUILDING = cfg.getBoolean("spawnNotInBuilding", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).SPAWN_NOT_IN_BUILDING, "If this is true the player will not spawn in a building. This can be used in combination with the other spawn settings");
      this.VINE_CHANCE = cfg.getFloat("vineChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).VINE_CHANCE, 0.0F, 1.0F, "The chance that a block on the outside of a building will be covered with a vine");
      this.CHANCE_OF_RANDOM_LEAFBLOCKS = cfg.getFloat("randomLeafBlockChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).CHANCE_OF_RANDOM_LEAFBLOCKS, 0.0F, 1.0F, "Chance that leafblocks will be generated at the border of a building and a street");
      this.THICKNESS_OF_RANDOM_LEAFBLOCKS = cfg.getInt("randomLeafBlockThickness", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).THICKNESS_OF_RANDOM_LEAFBLOCKS, 1, 8, "Frequency of leafblocks as seen from the sides of buildings");
      this.AVOID_FOLIAGE = cfg.getBoolean("avoidFoliage", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_FOLIAGE, "If this is true then parks will have no foliage (trees and flowers currently)");
      this.AVOID_GENERATED_CACTII = cfg.getBoolean("avoidGeneratedCactii", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_CACTII, "This will prevent biomes from generating cactii");
      this.AVOID_GENERATED_FLOWERS = cfg.getBoolean("avoidGeneratedFlowers", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_FLOWERS, "This will prevent biomes from generating flowers");
      this.AVOID_GENERATED_GRASS = cfg.getBoolean("avoidGeneratedGrass", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_GRASS, "This will prevent biomes from generating grass");
      this.AVOID_GENERATED_PUMPKINS = cfg.getBoolean("avoidGeneratedPumpkins", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_PUMPKINS, "This will prevent biomes from generating pumpkins");
      this.AVOID_GENERATED_LILYPADS = cfg.getBoolean("avoidGeneratedLilypads", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_LILYPADS, "This will prevent biomes from generating lilypads");
      this.AVOID_GENERATED_REEDS = cfg.getBoolean("avoidGeneratedReeds", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_REEDS, "This will prevent biomes from generating reeds");
      this.AVOID_GENERATED_TREES = cfg.getBoolean("avoidGeneratedTrees", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_TREES, "This will prevent biomes from generating trees");
      this.AVOID_GENERATED_MUSHROOMS = cfg.getBoolean("avoidGeneratedMushrooms", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_MUSHROOMS, "This will prevent biomes from generating mushrooms");
      this.AVOID_GENERATED_LAKE_WATER = cfg.getBoolean("avoidGeneratedLakewater", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_LAKE_WATER, "This will prevent the generation of water in lakes");
      this.AVOID_GENERATED_DESERT_WELL = cfg.getBoolean("avoidGeneratedDesertWell", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_DESERT_WELL, "This will prevent the generation of desert wells");
      this.AVOID_GENERATED_FOSSILS = cfg.getBoolean("avoidGeneratedFossils", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_GENERATED_FOSSILS, "This will prevent the generation of fossils");
      String type = cfg.getString("landscapeType", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).LANDSCAPE_TYPE.getName(), "Type of landscape", new String[]{LandscapeType.DEFAULT.getName(), LandscapeType.FLOATING.getName(), LandscapeType.SPACE.getName(), LandscapeType.CAVERN.getName()});
      this.LANDSCAPE_TYPE = LandscapeType.getTypeByName(type);
      if (this.LANDSCAPE_TYPE == null) {
         throw new RuntimeException("Bad landscape type: " + type + "!");
      } else {
         this.RUBBLELAYER = cfg.getBoolean("rubbleLayer", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RUBBLELAYER, "If this is true an alternative way to generate dirt/stone/sand + leave blocks is used that makes the city appear more overgrown");
         this.RUBBLE_DIRT_SCALE = cfg.getFloat("rubbleDirtScale", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RUBBLE_DIRT_SCALE, 0.0F, 100.0F, "The scale of the dirt layer. Smaller values make the layer larger. Use 0 to disable");
         this.RUBBLE_LEAVE_SCALE = cfg.getFloat("rubbleLeaveScale", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RUBBLE_LEAVE_SCALE, 0.0F, 100.0F, "The scale of the leave layer. Smaller values make the layer larger. Use 0 to disable");
         this.RUINS = cfg.getBoolean("ruins", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RUINS, "If true there is a chance a building is ruined from the top (not caused by explosion damage)");
         this.RUIN_CHANCE = cfg.getFloat("ruinChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RUIN_CHANCE, 0.0F, 1.0F, "If ruines are enabled this gives the chance that a building is ruined");
         this.RUIN_MINLEVEL_PERCENT = cfg.getFloat("ruinMinlevelPercent", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RUIN_MINLEVEL_PERCENT, 0.0F, 1.0F, "If a building is ruined this indicates the minimum start height for the ruin destruction layer");
         this.RUIN_MAXLEVEL_PERCENT = cfg.getFloat("ruinMaxlevelPercent", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RUIN_MAXLEVEL_PERCENT, 0.0F, 1.0F, "If a building is ruined this indicates the maximum start height for the ruin destruction layer");
         this.GROUNDLEVEL = cfg.getInt("groundLevel", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).GROUNDLEVEL, 2, 256, "Ground level");
         this.WATERLEVEL_OFFSET = cfg.getInt("waterLevelOffset", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).WATERLEVEL_OFFSET, -100, 100, "How much lower the water level is compared to the ground level (63)");
         this.CHEST_WITHOUT_LOOT_CHANCE = cfg.getFloat("chestWithoutLootChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).CHEST_WITHOUT_LOOT_CHANCE, 0.0F, 1.0F, "The chance that a chest will have no loot");
         this.BUILDING_WITHOUT_LOOT_CHANCE = cfg.getFloat("buildingWithoutLootChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_WITHOUT_LOOT_CHANCE, 0.0F, 1.0F, "The chance that a building will have no loot and no spawners");
         this.BUILDING_CHANCE = cfg.getFloat("buildingChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_CHANCE, 0.0F, 1.0F, "The chance that a chunk in a city will have a building. Otherwise it will be a street");
         this.BUILDING_MINFLOORS = cfg.getInt("buildingMinFloors", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_MINFLOORS, 0, 30, "The minimum number of floors (above ground) for a building (0 means the first floor only)");
         this.BUILDING_MAXFLOORS = cfg.getInt("buildingMaxFloors", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_MAXFLOORS, 0, 30, "A cap for the amount of floors a city can have (above ground)");
         this.BUILDING_MINFLOORS_CHANCE = cfg.getInt("buildingMinFloorsChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_MINFLOORS_CHANCE, 1, 30, "The amount of floors of a building is equal to: MINFLOORS + random(MINFLOORS_CHANCE + (cityFactor + .1f) * (MAXFLOORS_CHANCE - MINFLOORS_CHANCE))");
         this.BUILDING_MAXFLOORS_CHANCE = cfg.getInt("buildingMaxFloorsChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_MAXFLOORS_CHANCE, 1, 30, "The amount of floors of a building is equal to: MINFLOORS + random(MINFLOORS_CHANCE + (cityFactor + .1f) * (MAXFLOORS_CHANCE - MINFLOORS_CHANCE))");
         this.BUILDING_MINCELLARS = cfg.getInt("buildingMinCellars", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_MINCELLARS, 0, 7, "The minimum number of cellars (below ground). 0 means no cellar");
         this.BUILDING_MAXCELLARS = cfg.getInt("buildingMaxCellars", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_MAXCELLARS, 0, 7, "The maximum number of cellars (below ground). 0 means no cellar");
         this.BUILDING_DOORWAYCHANCE = cfg.getFloat("buildingDoorwayChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_DOORWAYCHANCE, 0.0F, 1.0F, "The chance that a doorway will be generated at a side of a building (on any level). Only when possible");
         this.BUILDING_FRONTCHANCE = cfg.getFloat("buildingFrontChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING_FRONTCHANCE, 0.0F, 1.0F, "The chance that a building will have a 'front' part if this is possible (i.e. adjacent street)");
         this.LIBRARY_CHANCE = cfg.getFloat("libraryChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).LIBRARY_CHANCE, 0.0F, 1.0F, "The chance that a 2x2 building will be a library");
         this.DATACENTER_CHANCE = cfg.getFloat("dataCenterChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).DATACENTER_CHANCE, 0.0F, 1.0F, "The chance that a 2x2 building will be a data center");
         this.PARK_CHANCE = cfg.getFloat("parkChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).PARK_CHANCE, 0.0F, 1.0F, "The chance that a non-building section can be a park section");
         this.BUILDING2X2_CHANCE = cfg.getFloat("building2x2Chance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BUILDING2X2_CHANCE, 0.0F, 1.0F, "The chance that a chunk can possibly be the top-left chunk of 2x2 building. There actually being a 2x2 building also depends on the condition of those other chunks");
         this.CORRIDOR_CHANCE = cfg.getFloat("corridorChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).CORRIDOR_CHANCE, 0.0F, 1.0F, "The chance that a chunk can possibly contain a corridor. There actually being a corridor also depends on the presence of adjacent corridors");
         this.BRIDGE_CHANCE = cfg.getFloat("bridgeChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BRIDGE_CHANCE, 0.0F, 1.0F, "The chance that a chunk can possibly contain a bridge. There actually being a bridge also depends on the presence of adjacent bridges and other conditions");
         this.BRIDGE_SUPPORTS = cfg.getBoolean("bridgeSupports", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BRIDGE_SUPPORTS, "If true bridges get supports when needed. You can disable this if you have bridges that span void chunks");
         this.FOUNTAIN_CHANCE = cfg.getFloat("fountainChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).FOUNTAIN_CHANCE, 0.0F, 1.0F, "The chance that a street section contains a fountain");
         this.RAILWAY_DUNGEON_CHANCE = cfg.getFloat("railwayDungeonChance", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RAILWAY_DUNGEON_CHANCE, 0.0F, 1.0F, "The chance that a chunk next to a railway will have a railway dungeon");
         this.RAILWAYS_CAN_END = cfg.getBoolean("railwaysCanEnd", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RAILWAYS_CAN_END, "If true the a place where a station would have been if there was a city above will have an 'ending' rail part if one side of the 'station' has no connections. Useful in case cities are rare");
         this.RAILWAYS_ENABLED = cfg.getBoolean("railwaysEnabled", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RAILWAYS_ENABLED, "If true then railways are enabled. If false they are not (but stations will still generate)");
         this.RAILWAY_STATIONS_ENABLED = cfg.getBoolean("railwayStationsEnabled", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).RAILWAY_STATIONS_ENABLED, "If true then railway stations are enabled");
         this.HIGHWAY_REQUIRES_TWO_CITIES = cfg.getBoolean("highwayRequiresTwoCities", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).HIGHWAY_REQUIRES_TWO_CITIES, "If true then a highway will only generate if both sides have a valid city. If false then one city is sufficient");
         this.HIGHWAY_LEVEL_FROM_CITIES_MODE = cfg.getInt("highwayLevelFromCities", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).HIGHWAY_LEVEL_FROM_CITIES_MODE, 0, 3, "0 (take height from top-left city), 1 (take minimum height from both cities), 2 (take maximum height from both cities), 3 (take average height)");
         this.HIGHWAY_DISTANCE_MASK = cfg.getInt("highwayDistanceMask", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).HIGHWAY_DISTANCE_MASK, 0, Integer.MAX_VALUE, "Mask to control how far highways can generate. Must be a power of 2 (minus 1). If 0 there are no highways at all");
         this.HIGHWAY_MAINPERLIN_SCALE = cfg.getFloat("highwayMainPerlinScale", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).HIGHWAY_MAINPERLIN_SCALE, 1.0F, 1000.0F, "For highways on a certain axis, this value is used to scale the perlin noise generator on the main axis. Increasing this value will increase the frequency of highways but make them smaller");
         this.HIGHWAY_SECONDARYPERLIN_SCALE = cfg.getFloat("highwaySecondaryPerlinScale", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).HIGHWAY_SECONDARYPERLIN_SCALE, 1.0F, 1000.0F, "For highways on a certain axis, this value is used to scale the perlin noise generator on the secondary axis. Increasing this value will increase the variation of nearby highways");
         this.HIGHWAY_PERLIN_FACTOR = cfg.getFloat("highwayPerlinFactor", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).HIGHWAY_PERLIN_FACTOR, -100.0F, 100.0F, "The highway perlin noise is compared to this value. Setting this to 0 would give 50% chance of a highway being at a spot. Note that highways only generate on chunks a multiple of 8. Setting this very high will prevent highways from generating");
         this.HIGHWAY_SUPPORTS = cfg.getBoolean("highwaySupports", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).HIGHWAY_SUPPORTS, "If true highways get supports when needed. You can disable this if you have highways that span void chunks");
         this.BEDROCK_LAYER = cfg.getInt("bedrockLayer", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BEDROCK_LAYER, 0, 10, "The height of the bedrock layer that is generated at the bottom of some world types. Set to 0 to disable this and get default bedrock generation");
         this.GENERATE_SPAWNERS = cfg.getBoolean("generateSpawners", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_SPAWNERS, "If true then the buildings will be full of spawners");
         this.GENERATE_LOOT = cfg.getBoolean("generateLoot", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_LOOT, "If true the chests in the buildings will contain loot");
         this.GENERATE_LIGHTING = cfg.getBoolean("generateLighting", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_LIGHTING, "If true then there will be minimal lighting in the buildings");
         this.AVOID_WATER = cfg.getBoolean("avoidWater", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).AVOID_WATER, "If true then all water will be avoided (replaced with air)");
         this.ALLOWED_BIOME_FACTORS = cfg.getStringList("allowedBiomeFactors", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).ALLOWED_BIOME_FACTORS, "List of biomes that are allowed in the world. Empty list is default all biomes. The factor controls how much that biome is favored over the others (higher means less favored!)");
         this.MANUAL_BIOME_MAPPINGS = cfg.getStringList("manualBiomeMappings", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).MANUAL_BIOME_MAPPINGS, "Use in combination with 'allowedBiomeFactors' to manually map some biomes to others. This is a list of the format oldbiome=newbiome");
         String biomeSelectionStrategy = cfg.getString("biomeSelectionStrategy", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).BIOME_SELECTION_STRATEGY.getName(), "This is used in combination with allowedBiomeFactors. 'original' is the old strategy. 'randomized' is a new strategy that tries to randomize the biomes better. 'varied' is similar but has a more relaxed biome distance function", new String[]{BiomeSelectionStrategy.ORIGINAL.getName(), BiomeSelectionStrategy.RANDOMIZED.getName(), BiomeSelectionStrategy.VARIED.getName()});
         this.BIOME_SELECTION_STRATEGY = BiomeSelectionStrategy.getTypeByName(biomeSelectionStrategy);
         if (this.BIOME_SELECTION_STRATEGY == null) {
            throw new RuntimeException("Bad biome selection strategy: " + biomeSelectionStrategy + "!");
         } else {
            this.GENERATOR_OPTIONS = cfg.getString("generatorOptions", this.categoryLostcity, ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATOR_OPTIONS, "A json with generator options for the chunk generator");
         }
      }
   }

   private void initCities(Configuration cfg) {
      this.CITY_CHANCE = cfg.getFloat("cityChance", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_CHANCE, 0.0F, 1.0F, "The chance this chunk will be the center of a city");
      this.CITY_MINRADIUS = cfg.getInt("cityMinRadius", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_MINRADIUS, 1, 10000, "The minimum radius of a city");
      this.CITY_MAXRADIUS = cfg.getInt("cityMaxRadius", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_MAXRADIUS, 1, 10000, "The maximum radius of a city");
      this.CITY_THRESSHOLD = cfg.getFloat("cityThresshold", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_THRESSHOLD, 0.0F, 1.0F, "The center and radius of a city define a sphere. This thresshold indicates from which point a city is considered a city. This is important for calculating where cities are based on overlapping city circles (where the city thressholds are added)");
      this.CITY_BIOME_FACTORS = cfg.getStringList("cityBiomeFactors", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_BIOME_FACTORS, "List of biomes with a factor to affect the city factor in that biome. Using the value 0 you can disable city generation in biomes");
      this.CITY_DEFAULT_BIOME_FACTOR = cfg.getFloat("cityBiomeFactorDefault", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_DEFAULT_BIOME_FACTOR.floatValue(), 0.0F, 1.0F, "The default biome factor which is used if your biome is not specified in 'cityBiomeFactors'");
      this.CITY_LEVEL0_HEIGHT = cfg.getInt("cityLevel0Height", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_LEVEL0_HEIGHT, 1, 255, "Below this chunk height cities will be level 0");
      this.CITY_LEVEL1_HEIGHT = cfg.getInt("cityLevel1Height", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_LEVEL1_HEIGHT, 1, 255, "Below this chunk height cities will be level 1");
      this.CITY_LEVEL2_HEIGHT = cfg.getInt("cityLevel2Height", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_LEVEL2_HEIGHT, 1, 255, "Below this chunk height cities will be level 2");
      this.CITY_LEVEL3_HEIGHT = cfg.getInt("cityLevel3Height", this.categoryCities, ((LostCityProfile)this.inheritFrom.orElse(this)).CITY_LEVEL3_HEIGHT, 1, 255, "Below this chunk height cities will be level 3");
   }

   private void initExplosions(Configuration cfg) {
      this.DEBRIS_TO_NEARBYCHUNK_FACTOR = cfg.getInt("debrisToNearbyChunkFactor", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).DEBRIS_TO_NEARBYCHUNK_FACTOR, 1, 10000, "A factor that determines how much debris will overflow from nearby damaged chunks. Bigger numbers mean less debris");
      this.DESTROY_LONE_BLOCKS_FACTOR = cfg.getFloat("destroyLoneBlocksFactor", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).DESTROY_LONE_BLOCKS_FACTOR, 0.0F, 1.0F, "When a section of blocks in in an explosion the generator will count the number of blocks that are connected. The number of connections divided by the total number of blocks in a connected section is compared with this number. If it is smaller then the section of blocks is destroyed or moved down with gravity");
      this.DESTROY_OR_MOVE_CHANCE = cfg.getFloat("destroyOrMoveChance", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).DESTROY_OR_MOVE_CHANCE, 0.0F, 1.0F, "When a section of blocks is to be moved or destroyed this chance gives the chance of removal (as opposed to moving with gravity)");
      this.DESTROY_SMALL_SECTIONS_SIZE = cfg.getInt("destroySmallSectionsSize", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).DESTROY_SMALL_SECTIONS_SIZE, 1, 5000, "A section of blocks that is about to be moved or destroyed is always destroyed if it is smaller then this size");
      this.EXPLOSION_CHANCE = cfg.getFloat("explosionChance", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).EXPLOSION_CHANCE, 0.0F, 1.0F, "The chance that a chunk will contain an explosion");
      this.EXPLOSION_MINRADIUS = cfg.getInt("explosionMinRadius", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).EXPLOSION_MINRADIUS, 1, 1000, "The minimum radius of an explosion");
      this.EXPLOSION_MAXRADIUS = cfg.getInt("explosionMaxRadius", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).EXPLOSION_MAXRADIUS, 1, 3000, "The maximum radius of an explosion");
      this.EXPLOSION_MINHEIGHT = cfg.getInt("explosionMinHeight", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).EXPLOSION_MINHEIGHT, 1, 256, "The minimum height of an explosion");
      this.EXPLOSION_MAXHEIGHT = cfg.getInt("explosionMaxHeight", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).EXPLOSION_MAXHEIGHT, 1, 256, "The maximum height of an explosion");
      this.MINI_EXPLOSION_CHANCE = cfg.getFloat("miniExplosionChance", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).MINI_EXPLOSION_CHANCE, 0.0F, 1.0F, "The chance that a chunk will contain a mini explosion");
      this.MINI_EXPLOSION_MINRADIUS = cfg.getInt("miniExplosionMinRadius", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).MINI_EXPLOSION_MINRADIUS, 1, 1000, "The minimum radius of a mini explosion");
      this.MINI_EXPLOSION_MAXRADIUS = cfg.getInt("miniExplosionMaxRadius", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).MINI_EXPLOSION_MAXRADIUS, 1, 3000, "The maximum radius of a mini explosion");
      this.MINI_EXPLOSION_MINHEIGHT = cfg.getInt("miniExplosionMinHeight", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).MINI_EXPLOSION_MINHEIGHT, 1, 256, "The minimum height of a mini explosion");
      this.MINI_EXPLOSION_MAXHEIGHT = cfg.getInt("miniExplosionMaxHeight", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).MINI_EXPLOSION_MAXHEIGHT, 1, 256, "The maximum height of a mini explosion");
      this.EXPLOSIONS_IN_CITIES_ONLY = cfg.getBoolean("explosionsInCitiesOnly", this.categoryExplosions, ((LostCityProfile)this.inheritFrom.orElse(this)).EXPLOSIONS_IN_CITIES_ONLY, "If this is true the center of an explosion can only be in a city (the blast can still affect non-city chunks)");
   }

   private void initStructures(Configuration cfg) {
      this.GENERATE_OCEANMONUMENTS = cfg.get(this.categoryStructures, "generateOceanMonuments", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_OCEANMONUMENTS, "Generate ocean monuments").getBoolean();
      this.GENERATE_MANSIONS = cfg.get(this.categoryStructures, "generateMansions", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_MANSIONS, "Generate mansions").getBoolean();
      this.GENERATE_SCATTERED = cfg.get(this.categoryStructures, "generateScattered", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_SCATTERED, "Generate scattered features (swamphunts, desert temples, ...)").getBoolean();
      this.GENERATE_STRONGHOLDS = cfg.get(this.categoryStructures, "generateStrongholds", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_STRONGHOLDS, "Generate strongholds").getBoolean();
      this.GENERATE_VILLAGES = cfg.get(this.categoryStructures, "generateVillages", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_VILLAGES, "Generate villages").getBoolean();
      this.GENERATE_CAVES = cfg.get(this.categoryStructures, "generateCaves", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_CAVES, "Generate caves").getBoolean();
      this.GENERATE_RAVINES = cfg.get(this.categoryStructures, "generateRavines", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_RAVINES, "Generate ravines").getBoolean();
      this.GENERATE_MINESHAFTS = cfg.get(this.categoryStructures, "generateMineshafts", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_MINESHAFTS, "Generate mineshafts").getBoolean();
      this.GENERATE_LAKES = cfg.get(this.categoryStructures, "generateLakes", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_LAKES, "Generate lakes (lava/water)").getBoolean();
      this.GENERATE_DUNGEONS = cfg.get(this.categoryStructures, "generateDungeons", ((LostCityProfile)this.inheritFrom.orElse(this)).GENERATE_DUNGEONS, "Generate dungeons").getBoolean();
      this.MAX_CAVE_HEIGHT = cfg.getInt(this.categoryStructures, "maxCaveHeight", ((LostCityProfile)this.inheritFrom.orElse(this)).MAX_CAVE_HEIGHT, 20, 240, "Maximum height at which vanilla caves can generate. Default is 128. Lower this if you don't want the caves to damage buildings");
      this.PREVENT_VILLAGES_IN_CITIES = cfg.get(this.categoryStructures, "preventVillagesInCities", ((LostCityProfile)this.inheritFrom.orElse(this)).PREVENT_VILLAGES_IN_CITIES, "If true then an attempt will be made to prevent villages in cities. Note that enabling this option will likely require a low city density in order to actually get a reasonable chance for villages.").getBoolean();
      this.PREVENT_LAKES_RAVINES_IN_CITIES = cfg.get(this.categoryStructures, "preventLakesRavinesInCities", ((LostCityProfile)this.inheritFrom.orElse(this)).PREVENT_LAKES_RAVINES_IN_CITIES, "If true then no lakes and ravines will be generated in cities").getBoolean();
   }

   public String getName() {
      return this.name;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getDescription() {
      return this.description;
   }

   public String getExtraDescription() {
      return this.extraDescription;
   }

   public void setExtraDescription(String extraDescription) {
      this.extraDescription = extraDescription;
   }

   public void setWorldStyle(String worldStyle) {
      this.worldStyle = worldStyle;
   }

   public String getWorldStyle() {
      return this.worldStyle;
   }

   public Map<String, Float> getBiomeFactorMap() {
      if (this.biomeFactorMap == null) {
         this.biomeFactorMap = new HashMap();

         for(String s : this.CITY_BIOME_FACTORS) {
            String[] split = StringUtils.split(s, '=');
            if (split.length < 2) {
               LostCities.setup.getLogger().error("Badly specified biome factor. Must be <biome>=<factor>!");
            } else {
               float f = Float.parseFloat(split[1]);
               String biomeId = split[0];
               Biome biome = (Biome)Biome.REGISTRY.getObject(new ResourceLocation(biomeId));
               if (biome != null) {
                  this.biomeFactorMap.put(((ResourceLocation)Biome.REGISTRY.getNameForObject(biome)).toString(), Float.valueOf(f));
               }
            }
         }
      }

      return this.biomeFactorMap;
   }

   public boolean isDefault() {
      return this.LANDSCAPE_TYPE == LandscapeType.DEFAULT;
   }

   public boolean isFloating() {
      return this.LANDSCAPE_TYPE == LandscapeType.FLOATING;
   }

   public boolean isSpace() {
      return this.LANDSCAPE_TYPE == LandscapeType.SPACE;
   }

   public boolean isCavern() {
      return this.LANDSCAPE_TYPE == LandscapeType.CAVERN;
   }

   public IBlockState getLiquidBlock() {
      if (this.liquidBlock == null) {
         Block b = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.LIQUID_BLOCK));
         if (b == null) {
            LostCities.setup.getLogger().error("Bad liquid block: " + this.LIQUID_BLOCK + "!");
            this.liquidBlock = Blocks.WATER.getDefaultState();
         } else {
            this.liquidBlock = b.getDefaultState();
         }
      }

      return this.liquidBlock;
   }

   public IBlockState getBaseBlock() {
      if (this.baseBlock == null) {
         Block b = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.BASE_BLOCK));
         if (b == null) {
            LostCities.setup.getLogger().error("Bad base block: " + this.BASE_BLOCK + "!");
            this.baseBlock = Blocks.STONE.getDefaultState();
         } else {
            this.baseBlock = b.getDefaultState();
         }
      }

      return this.baseBlock;
   }
}
