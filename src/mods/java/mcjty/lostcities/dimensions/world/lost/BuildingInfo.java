package mcjty.lostcities.dimensions.world.lost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lostcities.api.ILostChunkInfo;
import mcjty.lostcities.api.ILostCityBuilding;
import mcjty.lostcities.api.ILostCityInfo;
import mcjty.lostcities.api.ILostCityMultiBuilding;
import mcjty.lostcities.api.ILostExplosion;
import mcjty.lostcities.api.ILostSphere;
import mcjty.lostcities.api.LostChunkCharacteristics;
import mcjty.lostcities.api.LostCityEvent;
import mcjty.lostcities.api.RailChunkType;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.ChunkHeightmap;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.driver.IIndex;
import mcjty.lostcities.dimensions.world.lost.cityassets.AssetRegistries;
import mcjty.lostcities.dimensions.world.lost.cityassets.Building;
import mcjty.lostcities.dimensions.world.lost.cityassets.BuildingPart;
import mcjty.lostcities.dimensions.world.lost.cityassets.CityStyle;
import mcjty.lostcities.dimensions.world.lost.cityassets.CompiledPalette;
import mcjty.lostcities.dimensions.world.lost.cityassets.ConditionContext;
import mcjty.lostcities.dimensions.world.lost.cityassets.Palette;
import mcjty.lostcities.dimensions.world.lost.cityassets.PredefinedCity;
import mcjty.lostcities.dimensions.world.lost.cityassets.Style;
import mcjty.lostcities.dimensions.world.terraingen.LostCitiesTerrainGenerator;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.varia.Counter;
import mcjty.lostcities.varia.QualityRandom;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class BuildingInfo implements ILostChunkInfo {
   public final int chunkX;
   public final int chunkZ;
   public final ChunkCoord coord;
   public final LostCityChunkGenerator provider;
   public final LostCityProfile profile;
   public final boolean outsideChunk;
   public final int groundLevel;
   public final int waterLevel;
   public final boolean isCity;
   public final boolean hasBuilding;
   public final int building2x2Section;
   public final ILostCityMultiBuilding multiBuilding;
   public final ILostCityBuilding buildingType;
   public final BuildingPart fountainType;
   public final BuildingPart parkType;
   public final BuildingPart bridgeType;
   public final BuildingPart stairType;
   public final BuildingPart frontType;
   private final float stairPriority;
   public final BuildingPart railDungeon;
   public final BuildingInfo.StreetType streetType;
   private final int floors;
   public final int floorsBelowGround;
   public final BuildingPart[] floorTypes;
   public final BuildingPart[] floorTypes2;
   public final boolean[] connectionAtX;
   public final boolean[] connectionAtZ;
   public final boolean noLoot;
   public final float ruinHeight;
   public final int highwayXLevel;
   public final int highwayZLevel;
   public final int cityLevel;
   public final boolean xBridge;
   public final boolean zBridge;
   public final boolean xRailCorridor;
   public final boolean zRailCorridor;
   public final Block doorBlock;
   private BuildingInfo xmin = null;
   private BuildingInfo xmax = null;
   private BuildingInfo zmin = null;
   private BuildingInfo zmax = null;
   private DamageArea damageArea = null;
   private Palette palette = null;
   private CompiledPalette compiledPalette = null;
   private Boolean isOcean = null;
   private boolean xBridgeTypeCalculated = false;
   private boolean zBridgeTypeCalculated = false;
   private BuildingPart xBridgeType = null;
   private BuildingPart zBridgeType = null;
   private boolean stairsCalculated = false;
   private Direction stairDirection;
   private boolean actualStairsCalculated = false;
   private Direction actualStairDirection;
   private Boolean horizontalMonorail = null;
   private Boolean verticalMonorail = null;
   private final List<Pair<BlockPos, BuildingInfo.ConditionTodo>> mobSpawnerTodo = new ArrayList();
   private final List<Pair<BlockPos, BuildingInfo.ConditionTodo>> lootTodo = new ArrayList();
   private final List<BlockPos> lightingUpdateTodo = new ArrayList();
   private final List<Pair<IIndex, Map<String, Integer>>> torchTodo = new ArrayList();
   private final List<BlockPos> saplingTodo = new ArrayList();
   private static Map<ChunkCoord, BuildingInfo> buildingInfoMap = new HashMap();
   private static Map<ChunkCoord, LostChunkCharacteristics> cityInfoMap = new HashMap();

   public void addSaplingTodo(BlockPos pos) {
      this.saplingTodo.add(pos);
   }

   public List<BlockPos> getSaplingTodo() {
      return this.saplingTodo;
   }

   public void clearSaplingTodo() {
      this.saplingTodo.clear();
   }

   public void addTorchTodo(IIndex index, Map<String, Integer> orientations) {
      this.torchTodo.add(Pair.of(index, orientations));
   }

   public List<Pair<IIndex, Map<String, Integer>>> getTorchTodo() {
      return this.torchTodo;
   }

   public void clearTorchTodo() {
      this.torchTodo.clear();
   }

   public void addLightingUpdateTodo(BlockPos pos) {
      this.lightingUpdateTodo.add(pos);
   }

   public List<BlockPos> getLightingUpdateTodo() {
      return this.lightingUpdateTodo;
   }

   public void clearLightingUpdateTodo() {
      this.lightingUpdateTodo.clear();
   }

   public void addSpawnerTodo(BlockPos pos, BuildingInfo.ConditionTodo mobId) {
      this.mobSpawnerTodo.add(Pair.of(pos, mobId));
   }

   public void addLootTodo(BlockPos pos, @Nullable BuildingInfo.ConditionTodo lootTable) {
      this.lootTodo.add(Pair.of(pos, lootTable));
   }

   public List<Pair<BlockPos, BuildingInfo.ConditionTodo>> getMobSpawnerTodo() {
      return this.mobSpawnerTodo;
   }

   public List<Pair<BlockPos, BuildingInfo.ConditionTodo>> getLootTodo() {
      return this.lootTodo;
   }

   public void clearMobSpawnerTodo() {
      this.mobSpawnerTodo.clear();
   }

   public void clearLootTodo() {
      this.lootTodo.clear();
   }

   public CompiledPalette getCompiledPalette() {
      if (this.compiledPalette == null) {
         this.compiledPalette = new CompiledPalette(new Palette[]{this.palette});
      }

      return this.compiledPalette;
   }

   public DamageArea getDamageArea() {
      if (this.damageArea == null) {
         this.damageArea = new DamageArea(this.chunkX, this.chunkZ, this.provider, this);
      }

      return this.damageArea;
   }

   public BuildingInfo getTodoChunk(int x, int z) {
      if (x >= 8 && z >= 8) {
         return this;
      } else if (x < 8 && z >= 8) {
         return this.getXmin();
      } else {
         return x >= 8 && z < 8 ? this.getZmin() : this.getXmin().getZmin();
      }
   }

   public Style getOutsideStyle() {
      return AssetRegistries.STYLES.get(this.provider.worldStyle.getOutsideStyle());
   }

   private void createPalette(Random rand) {
      Style style;
      if (!this.isCity) {
         style = this.getOutsideStyle();
      } else {
         String name = this.getCityStyle().getStyle();
         style = AssetRegistries.STYLES.get(name);
         if (style == null) {
            throw new RuntimeException("Cannot find style '" + name + "'!");
         }
      }

      this.palette = style.getRandomPalette(this.provider, rand);
   }

   public BuildingInfo getAdjacent(int x, int z) {
      if (x == 0) {
         return this.getXmin();
      } else if (x == 15) {
         return this.getXmax();
      } else if (z == 0) {
         return this.getZmin();
      } else {
         return z == 15 ? this.getZmax() : null;
      }
   }

   public BuildingInfo getXmin() {
      if (this.xmin == null) {
         this.xmin = getBuildingInfo(this.chunkX - 1, this.chunkZ, this.provider);
      }

      return this.xmin;
   }

   public BuildingInfo getXmax() {
      if (this.xmax == null) {
         this.xmax = getBuildingInfo(this.chunkX + 1, this.chunkZ, this.provider);
      }

      return this.xmax;
   }

   public BuildingInfo getZmin() {
      if (this.zmin == null) {
         this.zmin = getBuildingInfo(this.chunkX, this.chunkZ - 1, this.provider);
      }

      return this.zmin;
   }

   public BuildingInfo getZmax() {
      if (this.zmax == null) {
         this.zmax = getBuildingInfo(this.chunkX, this.chunkZ + 1, this.provider);
      }

      return this.zmax;
   }

   public int getMaxHeight() {
      if (this.hasBuilding) {
         return this.getCityGroundLevel() + this.floors * 6;
      } else {
         int m = this.getMaxHighwayLevel();
         return m >= 0 ? this.groundLevel + m * 6 : this.getCityGroundLevel();
      }
   }

   public int getCityGroundLevel() {
      return this.groundLevel + this.cityLevel * 6;
   }

   public int getCityGroundLevelOutsideLower() {
      return this.isCity ? this.groundLevel + this.cityLevel * 6 : this.groundLevel + this.cityLevel * 6 - 1;
   }

   public boolean isValidFloor(int l) {
      return l + this.floorsBelowGround >= 0 && l + this.floorsBelowGround < this.floorTypes.length;
   }

   public BuildingPart getFloor(int l) {
      return this.floorTypes[l + this.floorsBelowGround];
   }

   public BuildingPart getFloorPart2(int l) {
      return this.floorTypes2[l + this.floorsBelowGround];
   }

   public ILostCityBuilding getBuilding() {
      return this.buildingType;
   }

   public CityStyle getCityStyle() {
      return (CityStyle)getChunkCharacteristics(this.chunkX, this.chunkZ, this.provider).cityStyle;
   }

   public static LostChunkCharacteristics getChunkCharacteristics(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      ChunkCoord key = new ChunkCoord(provider.dimensionId, chunkX, chunkZ);
      if (cityInfoMap.containsKey(key)) {
         return (LostChunkCharacteristics)cityInfoMap.get(key);
      } else {
         LostCityProfile profile = getProfile(chunkX, chunkZ, provider);
         LostChunkCharacteristics characteristics = new LostChunkCharacteristics();
         characteristics.isCity = isCityRaw(chunkX, chunkZ, provider, profile);
         characteristics.section = getMultiBuildingSection(chunkX, chunkZ, provider, profile);
         if (characteristics.section > 0) {
            characteristics.cityLevel = getTopLeftCityInfo(characteristics, chunkX, chunkZ, provider).cityLevel;
         } else {
            characteristics.cityLevel = getCityLevel(chunkX, chunkZ, provider);
         }

         Random rand = getBuildingRandom(chunkX, chunkZ, provider.seed);
         characteristics.couldHaveBuilding = characteristics.isCity && checkBuildingPossibility(chunkX, chunkZ, provider, profile, characteristics.section, characteristics.cityLevel, rand);
         if (profile.isSpace() && characteristics.section == -1) {
            float dist = CitySphere.getRelativeDistanceToCityCenter(chunkX, chunkZ, provider);
            if (dist > 0.7F) {
               characteristics.couldHaveBuilding = false;
            }
         }

         ChunkCoord coord = new ChunkCoord(provider.dimensionId, chunkX, chunkZ);
         CityStyle cityStyle;
         if (characteristics.isCity && !characteristics.couldHaveBuilding) {
            Counter<String> counter = new Counter<String>();

            for(int cx = -1; cx <= 1; ++cx) {
               for(int cz = -1; cz <= 1; ++cz) {
                  cityStyle = City.getCityStyle(coord.getChunkX() + cx, coord.getChunkZ() + cz, provider, profile);
                  counter.add(cityStyle.getName());
                  if (cx == 0 && cz == 0) {
                     counter.add(cityStyle.getName());
                  }
               }
            }

            cityStyle = AssetRegistries.CITYSTYLES.get(counter.getMostOccuring());
         } else {
            cityStyle = City.getCityStyle(chunkX, chunkZ, provider, profile);
         }

         characteristics.cityStyle = cityStyle;
         if (characteristics.section >= 1) {
            LostChunkCharacteristics topleft = getTopLeftCityInfo(characteristics, chunkX, chunkZ, provider);
            characteristics.multiBuilding = topleft.multiBuilding;
            if (characteristics.multiBuilding != null) {
               switch(characteristics.section) {
               case 1:
                  characteristics.buildingType = AssetRegistries.BUILDINGS.get(characteristics.multiBuilding.getBuilding(1, 0));
                  break;
               case 2:
                  characteristics.buildingType = AssetRegistries.BUILDINGS.get(characteristics.multiBuilding.getBuilding(0, 1));
                  break;
               case 3:
                  characteristics.buildingType = AssetRegistries.BUILDINGS.get(characteristics.multiBuilding.getBuilding(1, 1));
                  break;
               default:
                  throw new RuntimeException("What 2!");
               }
            } else {
               characteristics.buildingType = topleft.buildingType;
            }
         } else {
            PredefinedCity.PredefinedBuilding predefinedBuilding = City.getPredefinedBuilding(chunkX, chunkZ, provider);
            if (characteristics.section == 0) {
               String name = cityStyle.getRandomMultiBuilding(rand);
               if (predefinedBuilding != null) {
                  name = predefinedBuilding.getBuilding();
               }

               characteristics.multiBuilding = AssetRegistries.MULTI_BUILDINGS.get(name);
               characteristics.buildingType = AssetRegistries.BUILDINGS.get(characteristics.multiBuilding.getBuilding(0, 0));
            } else {
               characteristics.multiBuilding = null;
               String name = cityStyle.getRandomBuilding(rand);
               if (predefinedBuilding != null) {
                  name = predefinedBuilding.getBuilding();
               }

               characteristics.buildingType = AssetRegistries.BUILDINGS.get(name);
            }
         }

         LostCityEvent.CharacteristicsEvent event = new LostCityEvent.CharacteristicsEvent(provider.worldObj, provider, chunkX, chunkZ, characteristics);
         MinecraftForge.EVENT_BUS.post(event);
         cityInfoMap.put(key, characteristics);
         return characteristics;
      }
   }

   public static boolean isCityRaw(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      if (isVoidChunk(chunkX, chunkZ, provider)) {
         return false;
      } else {
         if (provider.getProfile().isSpace()) {
            if (CitySphere.onCitySphereBorder(chunkX, chunkZ, provider)) {
               return false;
            }

            if (!provider.getProfile().CITYSPHERE_LANDSCAPE_OUTSIDE && !CitySphere.fullyInsideCitySpere(chunkX, chunkZ, provider)) {
               return false;
            }

            if (CitySphere.hasMonorailStation(chunkX, chunkZ, provider)) {
               return false;
            }
         }

         float cityFactor = City.getCityFactor(chunkX, chunkZ, provider, profile);
         return cityFactor > profile.CITY_THRESSHOLD;
      }
   }

   public static boolean isCity(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      return getChunkCharacteristics(chunkX, chunkZ, provider).isCity;
   }

   private static boolean checkBuildingPossibility(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile, int section, int cityLevel, Random rand) {
      float bc = rand.nextFloat();
      PredefinedCity.PredefinedBuilding predefinedBuilding = City.getPredefinedBuilding(chunkX, chunkZ, provider);
      if (predefinedBuilding != null) {
         return true;
      } else {
         PredefinedCity.PredefinedStreet predefinedStreet = City.getPredefinedStreet(chunkX, chunkZ, provider);
         if (predefinedStreet != null) {
            return false;
         } else {
            boolean b;
            if (section >= 0) {
               b = true;
            } else if (bc >= profile.BUILDING_CHANCE) {
               b = false;
            } else if (hasHighway(chunkX, chunkZ, provider, profile)) {
               int maxh = Math.max(Highway.getXHighwayLevel(chunkX, chunkZ, provider, profile), Highway.getZHighwayLevel(chunkX, chunkZ, provider, profile));
               b = cityLevel > maxh + 1;
            } else if (hasRailway(chunkX, chunkZ, provider, profile)) {
               Railway.RailChunkInfo info = Railway.getRailChunkType(chunkX, chunkZ, provider, profile);
               if (info.getType() == RailChunkType.STATION_UNDERGROUND) {
                  b = false;
               } else {
                  int maxh = info.getLevel();
                  b = cityLevel > maxh + 1;
               }
            } else {
               b = true;
            }

            return b;
         }
      }
   }

   private static int getMultiBuildingSection(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      int section;
      if (isTopLeftOf2x2Building(chunkX, chunkZ, provider, profile)) {
         section = 0;
      } else if (isTopLeftOf2x2Building(chunkX - 1, chunkZ, provider, profile)) {
         section = 1;
      } else if (isTopLeftOf2x2Building(chunkX, chunkZ - 1, provider, profile)) {
         section = 2;
      } else if (isTopLeftOf2x2Building(chunkX - 1, chunkZ - 1, provider, profile)) {
         section = 3;
      } else {
         section = -1;
      }

      return section;
   }

   private BuildingInfo calculateTopLeft() {
      switch(this.building2x2Section) {
      case 0:
         return this;
      case 1:
         return this.getXmin();
      case 2:
         return this.getZmin();
      case 3:
         return this.getXmin().getZmin();
      default:
         throw new RuntimeException("What!");
      }
   }

   private static LostChunkCharacteristics getTopLeftCityInfo(LostChunkCharacteristics thisone, int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      switch(thisone.section) {
      case 0:
         return thisone;
      case 1:
         return getChunkCharacteristics(chunkX - 1, chunkZ, provider);
      case 2:
         return getChunkCharacteristics(chunkX, chunkZ - 1, provider);
      case 3:
         return getChunkCharacteristics(chunkX - 1, chunkZ - 1, provider);
      default:
         throw new RuntimeException("What!");
      }
   }

   private static boolean isCandidateForTopLeftOf2x2Building(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      PredefinedCity.PredefinedBuilding predefinedBuilding = City.getPredefinedBuilding(chunkX, chunkZ, provider);
      if (predefinedBuilding != null && predefinedBuilding.isMulti()) {
         return true;
      } else {
         PredefinedCity.PredefinedStreet predefinedStreet = City.getPredefinedStreet(chunkX, chunkZ, provider);
         if (predefinedStreet != null) {
            return false;
         } else if (isMultiBuildingCandidate(chunkX, chunkZ, provider, profile)) {
            Random rand = getBuildingRandom(chunkX, chunkZ, provider.seed);
            return rand.nextFloat() < profile.BUILDING2X2_CHANCE;
         } else {
            return false;
         }
      }
   }

   private static boolean isMultiBuildingCandidate(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      return isCityRaw(chunkX, chunkZ, provider, profile) && !hasHighway(chunkX, chunkZ, provider, profile) && !hasRailway(chunkX, chunkZ, provider, profile);
   }

   private static boolean hasHighway(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      return Highway.getXHighwayLevel(chunkX, chunkZ, provider, profile) >= 0 || Highway.getZHighwayLevel(chunkX, chunkZ, provider, profile) >= 0;
   }

   private static boolean hasRailway(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      return Railway.getRailChunkType(chunkX, chunkZ, provider, profile).getType() != RailChunkType.NONE;
   }

   public Railway.RailChunkInfo getRailInfo() {
      return Railway.getRailChunkType(this.chunkX, this.chunkZ, this.provider, this.profile);
   }

   public boolean isTunnel(int level) {
      if (this.isCity) {
         return this.cityLevel > level;
      } else {
         ChunkHeightmap heightmap = this.provider.getHeightmap(this.chunkX, this.chunkZ);
         int highwayHeight = this.groundLevel + level * 6 + 3;
         int cnt = 0;

         for(int x = 2; x < 16; x += 3) {
            for(int z = 2; z < 16; z += 3) {
               if (heightmap.getHeight(x, z) > highwayHeight) {
                  ++cnt;
               }
            }
         }

         return cnt > 12;
      }
   }

   private static boolean isTopLeftOf2x2Building(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      PredefinedCity.PredefinedBuilding predefinedBuilding = City.getPredefinedBuilding(chunkX, chunkZ, provider);
      if (predefinedBuilding != null && predefinedBuilding.isMulti()) {
         return true;
      } else if (isCandidateForTopLeftOf2x2Building(chunkX, chunkZ, provider, profile) && !isCandidateForTopLeftOf2x2Building(chunkX - 1, chunkZ, provider, profile) && !isCandidateForTopLeftOf2x2Building(chunkX - 1, chunkZ - 1, provider, profile) && !isCandidateForTopLeftOf2x2Building(chunkX, chunkZ - 1, provider, profile) && !isCandidateForTopLeftOf2x2Building(chunkX + 1, chunkZ - 1, provider, profile) && !isCandidateForTopLeftOf2x2Building(chunkX + 1, chunkZ, provider, profile) && !isCandidateForTopLeftOf2x2Building(chunkX + 1, chunkZ + 1, provider, profile) && !isCandidateForTopLeftOf2x2Building(chunkX, chunkZ + 1, provider, profile) && !isCandidateForTopLeftOf2x2Building(chunkX - 1, chunkZ + 1, provider, profile)) {
         PredefinedCity.PredefinedStreet predefinedStreet = City.getPredefinedStreet(chunkX, chunkZ, provider);
         if (predefinedStreet != null) {
            return false;
         } else {
            return isMultiBuildingCandidate(chunkX + 1, chunkZ, provider, profile) && isMultiBuildingCandidate(chunkX + 1, chunkZ + 1, provider, profile) && isMultiBuildingCandidate(chunkX, chunkZ + 1, provider, profile);
         }
      } else {
         return false;
      }
   }

   public static void cleanCache() {
      buildingInfoMap.clear();
      cityInfoMap.clear();
   }

   public static BuildingInfo getBuildingInfo(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      ChunkCoord key = new ChunkCoord(provider.dimensionId, chunkX, chunkZ);
      if (buildingInfoMap.containsKey(key)) {
         return (BuildingInfo)buildingInfoMap.get(key);
      } else {
         BuildingInfo info = new BuildingInfo(chunkX, chunkZ, provider);
         buildingInfoMap.put(key, info);
         return info;
      }
   }

   public static LostCityProfile getProfile(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      if (provider.getProfile().isSpace()) {
         return CitySphere.intersectsWithCitySphere(chunkX, chunkZ, provider) ? provider.getProfile() : provider.getOutsideProfile();
      } else {
         return provider.getProfile();
      }
   }

   private BuildingInfo(final int chunkX, final int chunkZ, final LostCityChunkGenerator provider) {
      this.provider = provider;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.coord = new ChunkCoord(provider.dimensionId, chunkX, chunkZ);
      this.outsideChunk = provider.getProfile().isSpace() && !CitySphere.intersectsWithCitySphere(chunkX, chunkZ, provider);
      this.profile = getProfile(chunkX, chunkZ, provider);
      LostChunkCharacteristics characteristics = getChunkCharacteristics(chunkX, chunkZ, provider);
      this.isCity = characteristics.isCity;
      this.building2x2Section = characteristics.section;
      this.cityLevel = characteristics.cityLevel;
      this.buildingType = characteristics.buildingType;
      this.multiBuilding = characteristics.multiBuilding;
      Random rand = getBuildingRandom(chunkX, chunkZ, provider.seed);
      rand.nextFloat();
      boolean b = characteristics.couldHaveBuilding;
      if (b && this.building2x2Section < 0) {
         if (rand.nextFloat() < getChunkCharacteristics(chunkX - 1, chunkZ, provider).buildingType.getPrefersLonely()) {
            b = false;
         } else if (rand.nextFloat() < getChunkCharacteristics(chunkX + 1, chunkZ, provider).buildingType.getPrefersLonely()) {
            b = false;
         } else if (rand.nextFloat() < getChunkCharacteristics(chunkX, chunkZ - 1, provider).buildingType.getPrefersLonely()) {
            b = false;
         } else if (rand.nextFloat() < getChunkCharacteristics(chunkX, chunkZ + 1, provider).buildingType.getPrefersLonely()) {
            b = false;
         }
      }

      this.hasBuilding = b;
      if (this.outsideChunk && provider.getProfile().CITYSPHERE_LANDSCAPE_OUTSIDE) {
         this.groundLevel = provider.getOutsideProfile().GROUNDLEVEL;
         this.waterLevel = this.groundLevel - provider.getOutsideProfile().WATERLEVEL_OFFSET;
      } else {
         this.groundLevel = provider.getProfile().GROUNDLEVEL;
         this.waterLevel = this.groundLevel - provider.getProfile().WATERLEVEL_OFFSET;
      }

      CityStyle cs = (CityStyle)characteristics.cityStyle;
      if (this.building2x2Section >= 1) {
         BuildingInfo topleft = this.calculateTopLeft();
         this.highwayXLevel = topleft.highwayXLevel;
         this.highwayZLevel = topleft.highwayZLevel;
         this.streetType = topleft.streetType;
         this.fountainType = topleft.fountainType;
         this.parkType = topleft.parkType;
         this.floors = topleft.floors;
         this.floorsBelowGround = topleft.floorsBelowGround;
         this.doorBlock = topleft.doorBlock;
         this.bridgeType = topleft.bridgeType;
         this.stairType = topleft.stairType;
         this.stairPriority = topleft.stairPriority;
         this.palette = topleft.palette;
         this.compiledPalette = topleft.getCompiledPalette();
         this.noLoot = topleft.noLoot;
         this.ruinHeight = topleft.ruinHeight;
      } else {
         PredefinedCity.PredefinedBuilding predefinedBuilding = City.getPredefinedBuilding(chunkX, chunkZ, provider);
         this.highwayXLevel = Highway.getXHighwayLevel(chunkX, chunkZ, provider, this.profile);
         this.highwayZLevel = Highway.getZHighwayLevel(chunkX, chunkZ, provider, this.profile);
         if (rand.nextDouble() < (double)this.profile.PARK_CHANCE) {
            this.streetType = BuildingInfo.StreetType.values()[rand.nextInt(BuildingInfo.StreetType.values().length)];
         } else {
            this.streetType = BuildingInfo.StreetType.NORMAL;
         }

         if (rand.nextFloat() < this.profile.FOUNTAIN_CHANCE) {
            this.fountainType = AssetRegistries.PARTS.get(cs.getRandomFountain(rand));
         } else {
            this.fountainType = null;
         }

         this.parkType = AssetRegistries.PARTS.get(cs.getRandomPark(rand));
         float cityFactor = City.getCityFactor(chunkX, chunkZ, provider, this.profile);
         int maxfloors = this.getMaxfloors(provider, cs);
         int f = this.profile.BUILDING_MINFLOORS + rand.nextInt((int)((float)this.profile.BUILDING_MINFLOORS_CHANCE + (cityFactor + 0.1F) * (float)(this.profile.BUILDING_MAXFLOORS_CHANCE - this.profile.BUILDING_MINFLOORS_CHANCE)));
         ++f;
         if (f > maxfloors) {
            f = maxfloors;
         }

         int minfloors = this.getMinfloors(provider, cs);
         if (f < minfloors) {
            f = minfloors;
         }

         if (provider.getProfile().isSpace() && CitySphere.intersectsWithCitySphere(chunkX, chunkZ, provider)) {
            float reldest = CitySphere.getRelativeDistanceToCityCenter(chunkX, chunkZ, provider);
            if (reldest > 0.6F) {
               f = Math.max(minfloors, f - 2);
            } else if (reldest > 0.5F) {
               f = Math.max(minfloors, f - 1);
            }
         }

         this.floors = f;
         int maxcellars = this.getMaxcellars(provider, cs);
         int fb = this.profile.BUILDING_MINCELLARS + (maxcellars <= 0 ? 0 : rand.nextInt(maxcellars));
         if (this.getMaxHighwayLevel() >= 0) {
            fb = Math.min(this.cityLevel - this.getMaxHighwayLevel() - 1, fb);
            if (fb < 0) {
               fb = 0;
            }
         }

         this.floorsBelowGround = fb;
         this.doorBlock = this.getRandomDoor(rand);
         this.bridgeType = AssetRegistries.PARTS.get(cs.getRandomBridge(rand));
         this.stairType = AssetRegistries.PARTS.get(cs.getRandomStair(rand));
         this.stairPriority = rand.nextFloat();
         this.createPalette(rand);
         float r = rand.nextFloat();
         this.noLoot = this.building2x2Section == -1 && r < this.profile.BUILDING_WITHOUT_LOOT_CHANCE;
         r = rand.nextFloat();
         if (rand.nextFloat() >= this.profile.RUIN_CHANCE || predefinedBuilding != null && predefinedBuilding.isPreventRuins()) {
            this.ruinHeight = -1.0F;
         } else {
            this.ruinHeight = this.profile.RUIN_MINLEVEL_PERCENT + (this.profile.RUIN_MAXLEVEL_PERCENT - this.profile.RUIN_MINLEVEL_PERCENT) * r;
         }
      }

      this.floorTypes = new BuildingPart[this.floors + this.floorsBelowGround + 1];
      this.floorTypes2 = new BuildingPart[this.floors + this.floorsBelowGround + 1];
      this.connectionAtX = new boolean[this.floors + this.floorsBelowGround + 1];
      this.connectionAtZ = new boolean[this.floors + this.floorsBelowGround + 1];
      Building building = (Building)this.getBuilding();

      for(int i = 0; i <= this.floors + this.floorsBelowGround; ++i) {
         ConditionContext conditionContext = new ConditionContext(this.cityLevel + i - this.floorsBelowGround, i - this.floorsBelowGround, this.floorsBelowGround, this.floors, "<none>", building.getName(), chunkX, chunkZ) {
            public boolean isBuilding() {
               return true;
            }

            public boolean isSphere() {
               return CitySphere.isInSphere(chunkX, chunkZ, new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8), provider);
            }

            public String getBiome() {
               return provider.worldObj.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)).getBiomeName();
            }
         };
         String randomPart = building.getRandomPart(rand, conditionContext);
         this.floorTypes[i] = (BuildingPart)Validate.notNull(AssetRegistries.PARTS.get(randomPart), "Null part for " + randomPart, new Object[0]);
         randomPart = building.getRandomPart2(rand, conditionContext);
         this.floorTypes2[i] = AssetRegistries.PARTS.get(randomPart);
         this.connectionAtX[i] = isCity(chunkX - 1, chunkZ, provider) && rand.nextFloat() < this.profile.BUILDING_DOORWAYCHANCE;
         this.connectionAtZ[i] = isCity(chunkX, chunkZ - 1, provider) && rand.nextFloat() < this.profile.BUILDING_DOORWAYCHANCE;
      }

      if (this.hasBuilding && this.floorsBelowGround > 0) {
         this.xRailCorridor = false;
         this.zRailCorridor = false;
      } else {
         this.xRailCorridor = rand.nextFloat() < this.profile.CORRIDOR_CHANCE;
         this.zRailCorridor = rand.nextFloat() < this.profile.CORRIDOR_CHANCE;
      }

      if (this.isCity) {
         this.xBridge = false;
         this.zBridge = false;
      } else {
         this.xBridge = rand.nextFloat() < this.profile.BRIDGE_CHANCE;
         this.zBridge = rand.nextFloat() < this.profile.BRIDGE_CHANCE;
      }

      if (rand.nextFloat() < this.profile.RAILWAY_DUNGEON_CHANCE) {
         if (this.hasBuilding && -3 >= this.cityLevel - this.floorsBelowGround) {
            this.railDungeon = null;
         } else {
            this.railDungeon = AssetRegistries.PARTS.get(this.getCityStyle().getRandomRailDungeon(rand));
         }
      } else {
         this.railDungeon = null;
      }

      if (rand.nextFloat() < this.profile.BUILDING_FRONTCHANCE) {
         this.frontType = AssetRegistries.PARTS.get(this.getCityStyle().getRandomFront(rand));
      } else {
         this.frontType = null;
      }

   }

   public boolean hasHorizontalMonorail() {
      if (this.horizontalMonorail == null) {
         this.horizontalMonorail = CitySphere.hasHorizontalMonorail(this.chunkX, this.chunkZ, this.provider);
      }

      return this.horizontalMonorail.booleanValue();
   }

   public boolean hasVerticalMonorail() {
      if (this.verticalMonorail == null) {
         this.verticalMonorail = CitySphere.hasVerticalMonorail(this.chunkX, this.chunkZ, this.provider);
      }

      return this.verticalMonorail.booleanValue();
   }

   public boolean hasMonorail() {
      return this.hasHorizontalMonorail() || this.hasVerticalMonorail();
   }

   private int getMaxcellars(LostCityChunkGenerator provider, CityStyle cs) {
      int maxcellars = this.profile.BUILDING_MAXCELLARS + this.cityLevel;
      if (this.buildingType.getMaxCellars() != -1) {
         maxcellars = Math.min(maxcellars, this.buildingType.getMaxCellars());
      }

      if (this.buildingType.getMinCellars() != -1) {
         maxcellars = Math.max(maxcellars, this.buildingType.getMinCellars());
      }

      if (cs.getMaxCellarCount() != null) {
         maxcellars = Math.min(maxcellars, cs.getMaxCellarCount().intValue());
      }

      if (cs.getMinCellarCount() != null) {
         maxcellars = Math.max(maxcellars, cs.getMinCellarCount().intValue());
      }

      return maxcellars;
   }

   private int getMinfloors(LostCityChunkGenerator provider, CityStyle cs) {
      int minfloors = this.profile.BUILDING_MINFLOORS + 1;
      if (this.buildingType.getMinFloors() != -1) {
         minfloors = Math.max(minfloors, this.buildingType.getMinFloors());
      }

      if (cs.getMinFloorCount() != null) {
         minfloors = Math.max(minfloors, cs.getMinFloorCount().intValue());
      }

      return minfloors;
   }

   private int getMaxfloors(LostCityChunkGenerator provider, CityStyle cs) {
      int maxfloors = this.profile.BUILDING_MAXFLOORS;
      if (this.buildingType.getMaxFloors() != -1) {
         maxfloors = Math.min(maxfloors, this.buildingType.getMaxFloors());
      }

      if (cs.getMaxFloorCount() != null) {
         maxfloors = Math.min(maxfloors, cs.getMaxFloorCount().intValue());
      }

      return maxfloors;
   }

   public int getHighwayXLevel() {
      return Highway.getXHighwayLevel(this.chunkX, this.chunkZ, this.provider, this.profile);
   }

   public int getHighwayZLevel() {
      return Highway.getZHighwayLevel(this.chunkX, this.chunkZ, this.provider, this.profile);
   }

   public static boolean isVoidChunk(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      if (provider.otherGenerator != null) {
         return false;
      } else if (provider.getProfile().isFloating()) {
         if (provider.getHeightmap(chunkX, chunkZ).getHeight(8, 8) <= 0) {
            return true;
         } else if (provider.getHeightmap(chunkX, chunkZ).getHeight(3, 3) <= 0) {
            return true;
         } else if (provider.getHeightmap(chunkX, chunkZ).getHeight(12, 3) <= 0) {
            return true;
         } else if (provider.getHeightmap(chunkX, chunkZ).getHeight(3, 12) <= 0) {
            return true;
         } else {
            return provider.getHeightmap(chunkX, chunkZ).getHeight(12, 12) <= 0;
         }
      } else {
         return false;
      }
   }

   public static int getCityLevel(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      if (provider.otherGenerator != null) {
         int height = provider.otherGenerator.getHeight(chunkX, chunkZ, 8, 8);
         return getLevelBasedOnHeight(height, provider.getProfile());
      } else if (provider.getProfile().isSpace()) {
         return getCityLevelSpace(chunkX, chunkZ, provider);
      } else if (provider.getProfile().isFloating()) {
         return getCityLevelFloating(chunkX, chunkZ, provider);
      } else {
         return provider.getProfile().isCavern() ? getCityLevelCavern(chunkX, chunkZ, provider) : getCityLevelNormal(chunkX, chunkZ, provider, provider.getProfile());
      }
   }

   private static int getCityLevelCavern(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      return getCityLevelFloating(chunkX, chunkZ, provider);
   }

   private static int getCityLevelSpace(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      if (CitySphere.intersectsWithCitySphere(chunkX, chunkZ, provider)) {
         float dist = CitySphere.getRelativeDistanceToCityCenter(chunkX, chunkZ, provider);
         Random rand = new Random(provider.seed + (long)chunkZ * 817505771L + (long)chunkX * 217645177L);
         rand.nextFloat();
         rand.nextFloat();
         if (dist < 0.3F) {
            return 2 + rand.nextInt(2);
         } else if (dist < 0.4F) {
            return 1 + rand.nextInt(2);
         } else {
            return dist < 0.6F ? rand.nextInt(2) : 0;
         }
      } else {
         return getCityLevelNormal(chunkX, chunkZ, provider, provider.getOutsideProfile());
      }
   }

   private static int getCityLevelNormal(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      Biome[] biomes = BiomeInfo.getBiomeInfo(provider, new ChunkCoord(provider.dimensionId, chunkX, chunkZ)).getBiomes();
      float h = 0.0F;

      for(Biome biome : biomes) {
         h += biome.getBaseHeight();
      }

      h = h / (float)biomes.length;
      int height = 0;
      if (h < 0.15F) {
         height = 70;
      } else if (h < 0.4F) {
         height = 79;
      } else if (h < 0.7F) {
         height = 88;
      } else if ((double)h < 1.3D) {
         height = 95;
      } else {
         height = 100;
      }

      return getLevelBasedOnHeight(height, profile);
   }

   private static int getCityLevelFloating(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      int cnt = 0;
      int h = 0;
      int h0 = provider.getHeightmap(chunkX, chunkZ).getHeight(8, 8);
      if (h0 > 1) {
         h += h0;
         ++cnt;
      }

      int h1 = provider.getHeightmap(chunkX, chunkZ).getHeight(3, 3);
      if (h1 > 1) {
         h += h1;
         ++cnt;
      }

      int h2 = provider.getHeightmap(chunkX, chunkZ).getHeight(12, 3);
      if (h2 > 1) {
         h += h2;
         ++cnt;
      }

      int h3 = provider.getHeightmap(chunkX, chunkZ).getHeight(3, 12);
      if (h3 > 1) {
         h += h3;
         ++cnt;
      }

      int h4 = provider.getHeightmap(chunkX, chunkZ).getHeight(12, 12);
      if (h4 > 1) {
         h += h4;
         ++cnt;
      }

      if (cnt > 0) {
         h /= cnt;
      }

      return getLevelBasedOnHeight(h, provider.getProfile());
   }

   private static int getLevelBasedOnHeight(int height, LostCityProfile profile) {
      if (height < profile.CITY_LEVEL0_HEIGHT) {
         return 0;
      } else if (height < profile.CITY_LEVEL1_HEIGHT) {
         return 1;
      } else if (height < profile.CITY_LEVEL2_HEIGHT) {
         return 2;
      } else {
         return height < profile.CITY_LEVEL3_HEIGHT ? 3 : 4;
      }
   }

   private Block getRandomDoor(Random rand) {
      Block doorBlock;
      switch(rand.nextInt(7)) {
      case 0:
         doorBlock = Blocks.BIRCH_DOOR;
         break;
      case 1:
         doorBlock = Blocks.ACACIA_DOOR;
         break;
      case 2:
         doorBlock = Blocks.DARK_OAK_DOOR;
         break;
      case 3:
         doorBlock = Blocks.SPRUCE_DOOR;
         break;
      case 4:
         doorBlock = Blocks.OAK_DOOR;
         break;
      case 5:
         doorBlock = Blocks.JUNGLE_DOOR;
         break;
      case 6:
         doorBlock = Blocks.IRON_DOOR;
         break;
      default:
         doorBlock = Blocks.OAK_DOOR;
      }

      return doorBlock;
   }

   public boolean isStreetSection() {
      return this.isCity && !this.hasBuilding;
   }

   public boolean isElevatedParkSection() {
      if (!this.isStreetSection()) {
         return false;
      } else if (!this.getXmin().isStreetSection()) {
         return false;
      } else if (!this.getXmax().isStreetSection()) {
         return false;
      } else if (!this.getZmin().isStreetSection()) {
         return false;
      } else if (!this.getZmax().isStreetSection()) {
         return false;
      } else {
         int cnt = 0;
         cnt = cnt + (this.getXmin().getZmin().isStreetSection() ? 1 : 0);
         cnt = cnt + (this.getXmin().getZmax().isStreetSection() ? 1 : 0);
         cnt = cnt + (this.getXmax().getZmin().isStreetSection() ? 1 : 0);
         cnt = cnt + (this.getXmax().getZmax().isStreetSection() ? 1 : 0);
         return cnt >= 3;
      }
   }

   private Direction getStairDirection() {
      if (!this.stairsCalculated) {
         this.stairsCalculated = true;
         if (this.streetType != BuildingInfo.StreetType.PARK && !this.hasBuilding && this.isCity) {
            if (this.cityLevel == this.getXmin().cityLevel - 1 && !this.getXmin().hasBuilding && this.getXmin().isCity) {
               this.stairDirection = Direction.XMIN;
            } else if (this.cityLevel == this.getXmax().cityLevel - 1 && !this.getXmax().hasBuilding && this.getXmax().isCity) {
               this.stairDirection = Direction.XMAX;
            } else if (this.cityLevel == this.getZmin().cityLevel - 1 && !this.getZmin().hasBuilding && this.getZmin().isCity) {
               this.stairDirection = Direction.ZMIN;
            } else if (this.cityLevel == this.getZmax().cityLevel - 1 && !this.getZmax().hasBuilding && this.getZmax().isCity) {
               this.stairDirection = Direction.ZMAX;
            } else {
               this.stairDirection = null;
            }
         } else {
            this.stairDirection = null;
         }
      }

      return this.stairDirection;
   }

   public Direction getActualStairDirection() {
      if (!this.actualStairsCalculated) {
         this.actualStairsCalculated = true;
         this.actualStairDirection = this.getStairDirection();
         if (this.actualStairDirection != null) {
            for(int cx = -1; cx <= 1; ++cx) {
               for(int cz = -1; cz <= 1; ++cz) {
                  if (cx != 0 || cz != 0) {
                     BuildingInfo adjacent = getBuildingInfo(this.chunkX + cx, this.chunkZ + cz, this.provider);
                     if (adjacent.getStairDirection() != null && adjacent.stairPriority > this.stairPriority) {
                        this.actualStairDirection = null;
                        break;
                     }
                  }
               }
            }
         }
      }

      return this.actualStairDirection;
   }

   public BuildingPart hasBridge(LostCityChunkGenerator provider, Orientation orientation) {
      switch(orientation) {
      case X:
         return this.hasXBridge(provider);
      case Z:
         return this.hasZBridge(provider);
      default:
         return null;
      }
   }

   public BuildingPart hasXBridge(LostCityChunkGenerator provider) {
      if (this.xBridgeTypeCalculated) {
         return this.xBridgeType;
      } else {
         this.xBridgeTypeCalculated = true;
         this.xBridgeType = null;
         if (!this.xBridge) {
            return null;
         } else if (!this.isSuitableForBridge(provider, this)) {
            return null;
         } else if (this.chunkZ % 2 == 0 || this.getZmin().hasXBridge(provider) == null && this.getZmax().hasXBridge(provider) == null) {
            BuildingPart bt = this.bridgeType;
            BuildingInfo i = this.getXmin();

            while(true) {
               if (!i.isCity && i.xBridge && this.isSuitableForBridge(provider, i)) {
                  if (this.chunkZ % 2 == 0 || i.getZmin().hasXBridge(provider) == null && i.getZmax().hasXBridge(provider) == null) {
                     bt = i.bridgeType;
                     i = i.getXmin();
                     continue;
                  }

                  return null;
               }

               if (i.isCity && !i.hasBuilding && i.cityLevel <= 0) {
                  BuildingInfo minimum = i;
                  i = this.getXmax();

                  while(true) {
                     if (!i.isCity && i.xBridge && this.isSuitableForBridge(provider, i)) {
                        if (this.chunkZ % 2 == 0 || i.getZmin().hasXBridge(provider) == null && i.getZmax().hasXBridge(provider) == null) {
                           i = i.getXmax();
                           continue;
                        }

                        return null;
                     }

                     if (i.isCity && !i.hasBuilding && i.cityLevel <= 0) {
                        this.xBridgeType = bt;

                        for(BuildingInfo var6 = i.getXmin(); var6 != minimum; var6 = var6.getXmin()) {
                           var6.xBridgeType = bt;
                           var6.xBridgeTypeCalculated = true;
                           var6.zBridgeType = null;
                           var6.zBridgeTypeCalculated = true;
                        }

                        return bt;
                     }

                     return null;
                  }
               }

               return null;
            }
         } else {
            return null;
         }
      }
   }

   public BuildingPart hasZBridge(LostCityChunkGenerator provider) {
      if (this.zBridgeTypeCalculated) {
         return this.zBridgeType;
      } else {
         this.zBridgeTypeCalculated = true;
         this.zBridgeType = null;
         if (!this.zBridge) {
            return null;
         } else if (!this.isSuitableForBridge(provider, this)) {
            return null;
         } else if (this.hasXBridge(provider) != null) {
            return null;
         } else if (this.chunkX % 2 == 0 || this.getXmin().hasZBridge(provider) == null && this.getXmax().hasZBridge(provider) == null) {
            BuildingPart bt = this.bridgeType;
            BuildingInfo i = this.getZmin();

            while(true) {
               if (!i.isCity && i.zBridge && this.isSuitableForBridge(provider, i)) {
                  if (i.hasXBridge(provider) != null) {
                     return null;
                  }

                  if (this.chunkX % 2 == 0 || i.getXmin().hasZBridge(provider) == null && i.getXmax().hasZBridge(provider) == null) {
                     bt = i.bridgeType;
                     i = i.getZmin();
                     continue;
                  }

                  return null;
               }

               BuildingInfo minimum = i;
               if (i.isCity && !i.hasBuilding && i.cityLevel <= 0) {
                  i = this.getZmax();

                  while(true) {
                     if (!i.isCity && i.zBridge && this.isSuitableForBridge(provider, i)) {
                        if (i.hasXBridge(provider) != null) {
                           return null;
                        }

                        if (this.chunkX % 2 == 0 || i.getXmin().hasZBridge(provider) == null && i.getXmax().hasZBridge(provider) == null) {
                           i = i.getZmax();
                           continue;
                        }

                        return null;
                     }

                     if (i.isCity && !i.hasBuilding && i.cityLevel <= 0) {
                        this.zBridgeType = bt;

                        for(BuildingInfo var6 = i.getZmin(); var6 != minimum; var6 = var6.getZmin()) {
                           var6.zBridgeType = bt;
                           var6.zBridgeTypeCalculated = true;
                           var6.xBridgeType = null;
                           var6.xBridgeTypeCalculated = true;
                        }

                        return bt;
                     }

                     return null;
                  }
               }

               return null;
            }
         } else {
            return null;
         }
      }
   }

   public boolean isOcean() {
      if (this.isOcean != null) {
         return this.isOcean.booleanValue();
      } else {
         Biome[] biomes = BiomeInfo.getBiomeInfo(this.provider, new ChunkCoord(this.provider.dimensionId, this.chunkX, this.chunkZ)).getBiomes();
         this.isOcean = isOcean(biomes);
         return this.isOcean.booleanValue();
      }
   }

   private static boolean isOcean(Biome[] biomes) {
      int cnt = 0;

      for(Biome biome : biomes) {
         if (biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN || biome == Biomes.FROZEN_OCEAN) {
            ++cnt;
         }
      }

      return cnt * 100 / biomes.length > 50;
   }

   private boolean isSuitableForBridge(LostCityChunkGenerator provider, BuildingInfo i) {
      if (provider.getProfile().isSpace() && this.hasMonorail()) {
         return false;
      } else {
         return i.cityLevel < this.cityLevel || LostCitiesTerrainGenerator.isWaterBiome(provider, i.coord);
      }
   }

   public boolean hasXCorridor() {
      if (!this.xRailCorridor) {
         return false;
      } else {
         BuildingInfo i;
         for(i = this.getXmin(); i.canRailGoThrough() && i.xRailCorridor; i = i.getXmin()) {
            ;
         }

         if (i.hasBuilding && i.floorsBelowGround != 0) {
            for(i = this.getXmax(); i.canRailGoThrough() && i.xRailCorridor; i = i.getXmax()) {
               ;
            }

            return i.hasBuilding && i.floorsBelowGround != 0;
         } else {
            return false;
         }
      }
   }

   public boolean hasZCorridor() {
      if (!this.zRailCorridor) {
         return false;
      } else {
         BuildingInfo i;
         for(i = this.getZmin(); i.canRailGoThrough() && i.zRailCorridor; i = i.getZmin()) {
            ;
         }

         if (i.hasBuilding && i.floorsBelowGround != 0) {
            for(i = this.getZmax(); i.canRailGoThrough() && i.zRailCorridor; i = i.getZmax()) {
               ;
            }

            return i.hasBuilding && i.floorsBelowGround != 0;
         } else {
            return false;
         }
      }
   }

   public boolean canRailGoThrough() {
      if (!this.isCity) {
         return false;
      } else if (!this.hasBuilding) {
         return true;
      } else {
         return this.floorsBelowGround == 0;
      }
   }

   public boolean canWaterCorridorGoThrough() {
      if (!this.isCity) {
         return false;
      } else if (!this.hasBuilding) {
         return true;
      } else {
         return this.floorsBelowGround <= 1;
      }
   }

   public boolean doesRoadExtendTo() {
      boolean b = this.isCity && !this.hasBuilding;
      if (b) {
         return !this.isElevatedParkSection();
      } else {
         return false;
      }
   }

   public static boolean hasRoadConnection(BuildingInfo i1, BuildingInfo i2) {
      if (!i1.doesRoadExtendTo()) {
         return false;
      } else if (!i2.doesRoadExtendTo()) {
         return false;
      } else {
         return Math.abs(i1.cityLevel - i2.cityLevel) <= 0;
      }
   }

   public static Random getBuildingRandom(int chunkX, int chunkZ, long seed) {
      Random rand = new QualityRandom(seed + (long)chunkZ * 341873128712L + (long)chunkX * 132897987541L);
      rand.nextFloat();
      rand.nextFloat();
      return rand;
   }

   public int localToGlobal(int l) {
      return l + this.cityLevel;
   }

   public int globalToLocal(int l) {
      return l - this.cityLevel;
   }

   public boolean hasConnectionAt(int level, Orientation orientation) {
      switch(orientation) {
      case X:
         return this.hasConnectionAtX(level);
      case Z:
         return this.hasConnectionAtZ(level);
      default:
         throw new IllegalStateException("Cannot happen!");
      }
   }

   public boolean hasFrontPartFrom(BuildingInfo adj) {
      BuildingInfo.StreetType st = this.streetType;
      boolean elevated = this.isElevatedParkSection();
      if (elevated) {
         st = BuildingInfo.StreetType.PARK;
      }

      if (adj.hasBuilding && adj.frontType != null && st == BuildingInfo.StreetType.NORMAL && this.cityLevel < adj.cityLevel + adj.getNumFloors()) {
         RailChunkType type = this.getRailInfo().getType();
         if (type == RailChunkType.STATION_UNDERGROUND) {
            return false;
         } else if (type == RailChunkType.GOING_DOWN_ONE_FROM_SURFACE) {
            return false;
         } else if (this.getMaxHighwayLevel() >= 0) {
            return false;
         } else {
            int local = adj.globalToLocal(this.cityLevel);
            return !adj.isValidFloor(local) || !adj.getFloor(local).getMetaBoolean("dontconnect");
         }
      } else {
         return false;
      }
   }

   public boolean hasConnectionAtX(int level) {
      if (!this.isCity) {
         return false;
      } else if (this.building2x2Section != 1 && this.building2x2Section != 3) {
         if (level >= 0 && level < this.connectionAtX.length) {
            if (level < this.floorTypes.length && this.floorTypes[level].getMetaBoolean("dontconnect")) {
               return false;
            } else {
               return this.getXmin().hasFrontPartFrom(this) ? true : this.connectionAtX[level];
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean hasConnectionAtXFromStreet(int level) {
      if (!this.isCity) {
         return false;
      } else if (this.building2x2Section != 1 && this.building2x2Section != 3) {
         if (level >= 0 && level < this.connectionAtX.length) {
            return this.hasFrontPartFrom(this.getXmin()) ? true : this.connectionAtX[level];
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean hasConnectionAtZ(int level) {
      if (!this.isCity) {
         return false;
      } else if (this.building2x2Section != 2 && this.building2x2Section != 3) {
         if (level >= 0 && level < this.connectionAtZ.length) {
            if (level < this.floorTypes.length && this.floorTypes[level].getMetaBoolean("dontconnect")) {
               return false;
            } else {
               return this.getZmin().hasFrontPartFrom(this) ? true : this.connectionAtZ[level];
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean hasConnectionAtZFromStreet(int level) {
      if (!this.isCity) {
         return false;
      } else if (this.building2x2Section != 2 && this.building2x2Section != 3) {
         if (level >= 0 && level < this.connectionAtZ.length) {
            return this.hasFrontPartFrom(this.getZmin()) ? true : this.connectionAtZ[level];
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isCity() {
      return this.isCity;
   }

   public String getBuildingType() {
      return this.hasBuilding ? this.buildingType.getName() : null;
   }

   public int getCityLevel() {
      return this.cityLevel;
   }

   public int getNumFloors() {
      return this.floors;
   }

   public int getNumCellars() {
      return this.floorsBelowGround;
   }

   public float getDamage(int chunkY) {
      return this.getDamageArea().getDamage(this.chunkX * 16 + 8, chunkY * 16 + 8, this.chunkZ * 16 + 8);
   }

   public Collection<ILostExplosion> getExplosions() {
      return new ArrayList(this.getDamageArea().getExplosions());
   }

   public int getMaxHighwayLevel() {
      return Math.max(this.getHighwayXLevel(), this.getHighwayZLevel());
   }

   @Nonnull
   public RailChunkType getRailType() {
      return this.getRailInfo().getType();
   }

   public int getRailLevel() {
      return this.getRailInfo().getLevel();
   }

   @Nullable
   public ILostCityInfo getCityInfo() {
      return City.isCityCenter(this.chunkX, this.chunkZ, this.provider) ? new ILostCityInfo() {
         public float getCityRadius() {
            return City.getCityRadius(BuildingInfo.this.chunkX, BuildingInfo.this.chunkZ, BuildingInfo.this.provider);
         }

         public String getCityStyle() {
            return City.getCityStyleForCityCenter(BuildingInfo.this.chunkX, BuildingInfo.this.chunkZ, BuildingInfo.this.provider);
         }
      } : null;
   }

   public int getRuinLevel() {
      if (!this.profile.RUINS) {
         return -1;
      } else {
         return this.ruinHeight < 0.0F ? -1 : (int)((float)(this.getCityGroundLevel() + 1) + this.ruinHeight * (float)this.getNumFloors() * 6.0F);
      }
   }

   @Nullable
   public ILostSphere getSphere() {
      CitySphere sphere = CitySphere.getCitySphere(this.chunkX, this.chunkZ, this.provider);
      return sphere.isEnabled() ? sphere : null;
   }

   public static class ConditionTodo {
      private final String condition;
      private final String part;
      private final String building;

      public ConditionTodo(String condition, String part, BuildingInfo info) {
         this.part = part == null ? "<none>" : part;
         this.condition = condition;
         if (info.hasBuilding) {
            this.building = info.getBuildingType();
         } else {
            this.building = "<none>";
         }

      }

      public String getCondition() {
         return this.condition;
      }

      public String getPart() {
         return this.part;
      }

      public String getBuilding() {
         return this.building;
      }
   }

   public static enum StreetType {
      NORMAL,
      FULL,
      PARK;

      private StreetType() {
      }
   }
}
