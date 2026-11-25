package mcjty.lostcities.dimensions.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import mcjty.lostcities.LostCities;
import mcjty.lostcities.api.IChunkPrimerFactory;
import mcjty.lostcities.api.ILostChunkGenerator;
import mcjty.lostcities.api.ILostChunkInfo;
import mcjty.lostcities.api.ILostCityAssetRegistry;
import mcjty.lostcities.api.ILostCityBuilding;
import mcjty.lostcities.api.ILostCityCityStyle;
import mcjty.lostcities.api.ILostCityMultiBuilding;
import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.driver.IPrimerDriver;
import mcjty.lostcities.dimensions.world.driver.OptimizedDriver;
import mcjty.lostcities.dimensions.world.driver.SafeDriver;
import mcjty.lostcities.dimensions.world.lost.BuildingInfo;
import mcjty.lostcities.dimensions.world.lost.CitySphere;
import mcjty.lostcities.dimensions.world.lost.LostStructureOceanMonument;
import mcjty.lostcities.dimensions.world.lost.cityassets.AssetRegistries;
import mcjty.lostcities.dimensions.world.lost.cityassets.Condition;
import mcjty.lostcities.dimensions.world.lost.cityassets.ConditionContext;
import mcjty.lostcities.dimensions.world.lost.cityassets.WorldStyle;
import mcjty.lostcities.dimensions.world.terraingen.LostCitiesTerrainGenerator;
import mcjty.lostcities.varia.ChunkCoord;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.EntityId;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.ChunkGeneratorSettings.Factory;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Post;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Pre;
import org.apache.commons.lang3.tuple.Pair;

public class LostCityChunkGenerator implements IChunkGenerator, ILostChunkGenerator {
   private LostCityProfile profile;
   private LostCityProfile outsideProfile;
   public WorldStyle worldStyle;
   public Random rand;
   public long seed;
   public int dimensionId;
   public World worldObj;
   public WorldType worldType;
   public final LostCitiesTerrainGenerator terrainGenerator;
   private ChunkGeneratorSettings settings;
   private Biome[] biomesForGeneration;
   private MapGenBase caveGenerator;
   private Map<ChunkCoord, ChunkPrimer> cachedPrimers;
   private Map<ChunkCoord, ChunkHeightmap> cachedHeightmaps;
   private MapGenStronghold strongholdGenerator;
   private StructureOceanMonument oceanMonumentGenerator;
   private MapGenVillage villageGenerator;
   private MapGenMineshaft mineshaftGenerator;
   private MapGenScatteredFeature scatteredFeatureGenerator;
   private LostWoodlandMansion woodlandMansionGenerator;
   private MapGenBase ravineGenerator;
   public IChunkPrimerFactory otherGenerator;
   private static final EntityId FIXER = new EntityId();

   public ChunkGeneratorSettings getSettings() {
      if (this.settings == null) {
         Factory factory = new Factory();
         this.settings = factory.build();
      }

      return this.settings;
   }

   public LostCityProfile getProfile() {
      return this.profile;
   }

   public LostCityProfile getOutsideProfile() {
      return this.outsideProfile;
   }

   public LostCityChunkGenerator(World world, IChunkPrimerFactory otherGenerator) {
      this(world, world.getSeed());
      this.otherGenerator = otherGenerator;
   }

   public LostCityChunkGenerator(World world, long seed) {
      this.settings = null;
      this.cachedPrimers = new HashMap();
      this.cachedHeightmaps = new HashMap();
      this.strongholdGenerator = new MapGenStronghold();
      this.oceanMonumentGenerator = new LostStructureOceanMonument();
      this.villageGenerator = new MapGenVillage();
      this.mineshaftGenerator = new MapGenMineshaft();
      this.scatteredFeatureGenerator = new MapGenScatteredFeature();
      this.woodlandMansionGenerator = new LostWoodlandMansion(this);
      this.ravineGenerator = new MapGenRavine();
      this.otherGenerator = null;
      WorldTypeTools.registerChunkGenerator(Integer.valueOf(world.provider.getDimension()), this);
      this.caveGenerator = new LostGenCaves(this);
      this.caveGenerator = TerrainGen.getModdedMapGen(this.caveGenerator, EventType.CAVE);
      this.strongholdGenerator = (MapGenStronghold)TerrainGen.getModdedMapGen(this.strongholdGenerator, EventType.STRONGHOLD);
      this.villageGenerator = (MapGenVillage)TerrainGen.getModdedMapGen(this.villageGenerator, EventType.VILLAGE);
      this.mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(this.mineshaftGenerator, EventType.MINESHAFT);
      this.scatteredFeatureGenerator = (MapGenScatteredFeature)TerrainGen.getModdedMapGen(this.scatteredFeatureGenerator, EventType.SCATTERED_FEATURE);
      this.ravineGenerator = TerrainGen.getModdedMapGen(this.ravineGenerator, EventType.RAVINE);
      this.oceanMonumentGenerator = (StructureOceanMonument)TerrainGen.getModdedMapGen(this.oceanMonumentGenerator, EventType.OCEAN_MONUMENT);
      this.dimensionId = world.provider.getDimension();
      this.profile = WorldTypeTools.getProfile(world);
      if (this.profile.isSpace() && !this.profile.CITYSPHERE_OUTSIDE_PROFILE.isEmpty()) {
         this.outsideProfile = (LostCityProfile)LostCityConfiguration.profiles.get(this.profile.CITYSPHERE_OUTSIDE_PROFILE);
      } else {
         this.outsideProfile = this.profile;
      }

      this.worldStyle = AssetRegistries.WORLDSTYLES.get(this.profile.getWorldStyle());
      if (this.worldStyle == null) {
         throw new RuntimeException("Unknown worldstyle '" + this.profile.getWorldStyle() + "'!");
      } else {
         String generatorOptions = this.profile.GENERATOR_OPTIONS;
         if (generatorOptions != null && !generatorOptions.isEmpty()) {
            this.settings = Factory.jsonToFactory(generatorOptions).build();
         }

         this.worldObj = world;
         this.worldType = world.getWorldInfo().getTerrainType();
         this.seed = seed;
         this.rand = new Random((seed + 516L) * 314L);
         int waterLevel = (byte)(this.profile.GROUNDLEVEL - this.profile.WATERLEVEL_OFFSET);
         if (waterLevel <= 0) {
            waterLevel = 1;
         }

         world.setSeaLevel(waterLevel);
         this.terrainGenerator = new LostCitiesTerrainGenerator(this);
         this.terrainGenerator.setup(world);
      }
   }

   public ChunkPrimer generatePrimer(int chunkX, int chunkZ) {
      this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
      ChunkPrimer chunkprimer = new ChunkPrimer();
      if (this.otherGenerator != null) {
         this.otherGenerator.fillChunk(chunkX, chunkZ, chunkprimer);
      } else {
         this.terrainGenerator.doCoreChunk(chunkX, chunkZ, chunkprimer);
      }

      return chunkprimer;
   }

   public ChunkHeightmap getHeightmap(int chunkX, int chunkZ) {
      ChunkCoord key = new ChunkCoord(this.worldObj.provider.getDimension(), chunkX, chunkZ);
      if (this.cachedHeightmaps.containsKey(key)) {
         return (ChunkHeightmap)this.cachedHeightmaps.get(key);
      } else if (this.cachedPrimers.containsKey(key)) {
         char baseChar = (char)Block.BLOCK_STATE_IDS.get(this.profile.getBaseBlock());
         ChunkPrimer primer = (ChunkPrimer)this.cachedPrimers.get(key);
         IPrimerDriver driver = (IPrimerDriver)(LostCityConfiguration.OPTIMIZED_CHUNKGEN ? new OptimizedDriver() : new SafeDriver());
         driver.setPrimer(primer);
         ChunkHeightmap heightmap = new ChunkHeightmap(driver, this.profile.LANDSCAPE_TYPE, this.profile.GROUNDLEVEL, baseChar);
         this.cachedHeightmaps.put(key, heightmap);
         return heightmap;
      } else {
         ChunkPrimer primer = this.generatePrimer(chunkX, chunkZ);
         this.cachedPrimers.put(key, primer);
         char baseChar = (char)Block.BLOCK_STATE_IDS.get(this.profile.getBaseBlock());
         IPrimerDriver driver = (IPrimerDriver)(LostCityConfiguration.OPTIMIZED_CHUNKGEN ? new OptimizedDriver() : new SafeDriver());
         driver.setPrimer(primer);
         ChunkHeightmap heightmap = new ChunkHeightmap(driver, this.profile.LANDSCAPE_TYPE, this.profile.GROUNDLEVEL, baseChar);
         this.cachedHeightmaps.put(key, heightmap);
         return heightmap;
      }
   }

   public Chunk generateChunk(int chunkX, int chunkZ) {
      LostCityProfile profile = BuildingInfo.getProfile(chunkX, chunkZ, this);
      this.terrainGenerator.setupChars(profile);
      boolean isCity = BuildingInfo.isCity(chunkX, chunkZ, this);
      ChunkPrimer chunkprimer = this.getChunkPrimer(chunkX, chunkZ, isCity);

      try {
         this.terrainGenerator.generate(chunkX, chunkZ, chunkprimer);
      } catch (Exception var10) {
         LostCities.setup.getLogger().error("An exception occured while generating chunk: " + chunkX + "," + chunkZ, var10);
         BuildingInfo info = BuildingInfo.getBuildingInfo(chunkX, chunkZ, this);
         LostCities.setup.getLogger().error("    Chunk profile: " + info.profile.getName());
         LostCities.setup.getLogger().error("    Is City: " + info.isCity());
         LostCities.setup.getLogger().error("    Building type: " + info.getBuildingType());
         LostCities.setup.getLogger().error("    City level: " + info.getCityLevel());
         LostCities.setup.getLogger().error("    City ground level: " + info.getCityGroundLevel());
         LostCities.setup.getLogger().error("    Num floors: " + info.getNumFloors());
         LostCities.setup.getLogger().error("    Num cellars: " + info.getNumCellars());
         throw var10;
      }

      BiomeProvider biomeProvider = this.worldObj.getBiomeProvider();
      this.biomesForGeneration = biomeProvider.getBiomes(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
      this.terrainGenerator.replaceBlocksForBiome(chunkX, chunkZ, chunkprimer, this.biomesForGeneration);
      if (profile.GENERATE_CAVES) {
         this.caveGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
      }

      if (profile.GENERATE_RAVINES && (!profile.PREVENT_LAKES_RAVINES_IN_CITIES || !isCity)) {
         this.ravineGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
      }

      if (profile.GENERATE_MINESHAFTS) {
         this.mineshaftGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
      }

      if (profile.GENERATE_VILLAGES) {
         if (profile.PREVENT_VILLAGES_IN_CITIES) {
            if (!isCity) {
               this.villageGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
            }
         } else {
            this.villageGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
         }
      }

      if (profile.GENERATE_STRONGHOLDS) {
         this.strongholdGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
      }

      if (profile.GENERATE_SCATTERED) {
         this.scatteredFeatureGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
      }

      if (profile.GENERATE_OCEANMONUMENTS) {
         this.oceanMonumentGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
      }

      if (profile.GENERATE_MANSIONS) {
         this.woodlandMansionGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
      }

      Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
      byte[] abyte = chunk.getBiomeArray();

      for(int i = 0; i < abyte.length; ++i) {
         abyte[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
      }

      chunk.generateSkylightMap();
      return chunk;
   }

   public ChunkPrimer getChunkPrimer(int chunkX, int chunkZ, boolean isCity) {
      ChunkPrimer chunkprimer;
      if (isCity && this.profile.isDefault()) {
         chunkprimer = new ChunkPrimer();
      } else {
         ChunkCoord key = new ChunkCoord(this.worldObj.provider.getDimension(), chunkX, chunkZ);
         if (this.cachedPrimers.containsKey(key)) {
            chunkprimer = (ChunkPrimer)this.cachedPrimers.get(key);
            this.cachedPrimers.remove(key);
         } else {
            chunkprimer = this.generatePrimer(chunkX, chunkZ);
         }

         if (!this.cachedHeightmaps.containsKey(key)) {
            char baseChar = (char)Block.BLOCK_STATE_IDS.get(this.profile.getBaseBlock());
            IPrimerDriver driver = (IPrimerDriver)(LostCityConfiguration.OPTIMIZED_CHUNKGEN ? new OptimizedDriver() : new SafeDriver());
            driver.setPrimer(chunkprimer);
            this.cachedHeightmaps.put(key, new ChunkHeightmap(driver, this.profile.LANDSCAPE_TYPE, this.profile.GROUNDLEVEL, baseChar));
         }
      }

      return chunkprimer;
   }

   private void generateTrees(Random random, int chunkX, int chunkZ, World world, LostCityChunkGenerator provider) {
      BuildingInfo info = BuildingInfo.getBuildingInfo(chunkX, chunkZ, provider);

      for(BlockPos pos : info.getSaplingTodo()) {
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() == Blocks.SAPLING) {
            ((BlockSapling)Blocks.SAPLING).generateTree(world, pos, state, random);
         }
      }

      info.clearSaplingTodo();
   }

   private void generateVines(Random random, int chunkX, int chunkZ, World world, LostCityChunkGenerator provider) {
      int cx = chunkX * 16;
      int cz = chunkZ * 16;
      BuildingInfo info = BuildingInfo.getBuildingInfo(chunkX, chunkZ, provider);
      if (info.hasBuilding) {
         BuildingInfo adjacent = info.getXmax();
         int bottom = Math.max(adjacent.getCityGroundLevel() + 3, adjacent.hasBuilding ? adjacent.getMaxHeight() : adjacent.getCityGroundLevel() + 3);

         for(int z = 0; z < 15; ++z) {
            for(int y = bottom; y < info.getMaxHeight(); ++y) {
               if (random.nextFloat() < provider.profile.VINE_CHANCE) {
                  this.createVineStrip(random, world, bottom, BlockVine.WEST, new BlockPos(cx + 16, y, cz + z), new BlockPos(cx + 15, y, cz + z));
               }
            }
         }
      }

      if (info.getXmax().hasBuilding) {
         BuildingInfo adjacent = info.getXmax();
         int bottom = Math.max(info.getCityGroundLevel() + 3, info.hasBuilding ? info.getMaxHeight() : info.getCityGroundLevel() + 3);

         for(int z = 0; z < 15; ++z) {
            for(int y = bottom; y < adjacent.getMaxHeight(); ++y) {
               if (random.nextFloat() < provider.profile.VINE_CHANCE) {
                  this.createVineStrip(random, world, bottom, BlockVine.EAST, new BlockPos(cx + 15, y, cz + z), new BlockPos(cx + 16, y, cz + z));
               }
            }
         }
      }

      if (info.hasBuilding) {
         BuildingInfo adjacent = info.getZmax();
         int bottom = Math.max(adjacent.getCityGroundLevel() + 3, adjacent.hasBuilding ? adjacent.getMaxHeight() : adjacent.getCityGroundLevel() + 3);

         for(int x = 0; x < 15; ++x) {
            for(int y = bottom; y < info.getMaxHeight(); ++y) {
               if (random.nextFloat() < provider.profile.VINE_CHANCE) {
                  this.createVineStrip(random, world, bottom, BlockVine.NORTH, new BlockPos(cx + x, y, cz + 16), new BlockPos(cx + x, y, cz + 15));
               }
            }
         }
      }

      if (info.getZmax().hasBuilding) {
         BuildingInfo adjacent = info.getZmax();
         int bottom = Math.max(info.getCityGroundLevel() + 3, info.hasBuilding ? info.getMaxHeight() : info.getCityGroundLevel() + 3);

         for(int x = 0; x < 15; ++x) {
            for(int y = bottom; y < adjacent.getMaxHeight(); ++y) {
               if (random.nextFloat() < provider.profile.VINE_CHANCE) {
                  this.createVineStrip(random, world, bottom, BlockVine.SOUTH, new BlockPos(cx + x, y, cz + 15), new BlockPos(cx + x, y, cz + 16));
               }
            }
         }
      }

   }

   private void createVineStrip(Random random, World world, int bottom, PropertyBool direction, BlockPos pos, BlockPos vineHolderPos) {
      if (!world.isAirBlock(vineHolderPos)) {
         if (world.isAirBlock(pos)) {
            world.setBlockState(pos, Blocks.VINE.getDefaultState().withProperty(direction, Boolean.valueOf(true)));

            for(BlockPos var7 = pos.down(); var7.getY() >= bottom && random.nextFloat() < 0.8F; var7 = var7.down()) {
               if (!world.isAirBlock(var7)) {
                  return;
               }

               world.setBlockState(var7, Blocks.VINE.getDefaultState().withProperty(direction, Boolean.valueOf(true)));
            }

         }
      }
   }

   public static String fixEntityId(String id) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("id", id);
      nbt = FIXER.fixTagCompound(nbt);
      return nbt.getString("id");
   }

   private void generateLootSpawners(Random random, final int chunkX, final int chunkZ, final World world, LostCityChunkGenerator chunkGenerator) {
      BuildingInfo info = BuildingInfo.getBuildingInfo(chunkX, chunkZ, chunkGenerator);

      for(Pair<BlockPos, BuildingInfo.ConditionTodo> pair : info.getMobSpawnerTodo()) {
         final BlockPos pos = (BlockPos)pair.getKey();
         if (world.getBlockState(pos).getBlock() == Blocks.MOB_SPAWNER) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileEntityMobSpawner) {
               TileEntityMobSpawner spawner = (TileEntityMobSpawner)tileentity;
               BuildingInfo.ConditionTodo todo = (BuildingInfo.ConditionTodo)pair.getValue();
               String condition = todo.getCondition();
               Condition cnd = AssetRegistries.CONDITIONS.get(condition);
               if (cnd == null) {
                  throw new RuntimeException("Cannot find condition '" + condition + "'!");
               }

               int level = (pos.getY() - this.profile.GROUNDLEVEL) / 6;
               int floor = (pos.getY() - info.getCityGroundLevel()) / 6;
               ConditionContext conditionContext = new ConditionContext(level, floor, info.floorsBelowGround, info.getNumFloors(), todo.getPart(), todo.getBuilding(), info.chunkX, info.chunkZ) {
                  public boolean isSphere() {
                     return CitySphere.isInSphere(chunkX, chunkZ, pos, LostCityChunkGenerator.this);
                  }

                  public String getBiome() {
                     return world.getBiome(pos).getBiomeName();
                  }
               };
               String randomValue = cnd.getRandomValue(random, conditionContext);
               if (randomValue == null) {
                  throw new RuntimeException("Condition '" + cnd.getName() + "' did not return a valid mob!");
               }

               String fixedId = fixEntityId(randomValue);
               MobSpawnerBaseLogic mobspawnerbaselogic = spawner.getSpawnerBaseLogic();
               mobspawnerbaselogic.setEntityId(new ResourceLocation(fixedId));
               spawner.markDirty();
               if (LostCityConfiguration.DEBUG) {
                  LostCities.setup.getLogger().debug("generateLootSpawners: mob=" + randomValue + " pos=" + pos.toString());
               }
            } else if (tileentity != null) {
               LostCities.setup.getLogger().error("The mob spawner at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ") has a TileEntity of incorrect type " + tileentity.getClass().getName() + "!");
            } else {
               LostCities.setup.getLogger().error("The mob spawner at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ") is missing its TileEntity!");
            }
         }
      }

      info.clearMobSpawnerTodo();

      for(Pair<BlockPos, BuildingInfo.ConditionTodo> pair : info.getLootTodo()) {
         BlockPos pos = (BlockPos)pair.getKey();
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileEntityLockableLoot) {
            if (chunkGenerator.profile.GENERATE_LOOT) {
               this.createLoot(info, random, world, pos, (BuildingInfo.ConditionTodo)pair.getRight());
            }
         } else if (te == null) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block.hasTileEntity(state)) {
               LostCities.setup.getLogger().error("The block " + block.getRegistryName() + " (" + block.getClass().getName() + ") at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ") is missing its TileEntity!");
            }
         }
      }

      info.clearLootTodo();

      for(BlockPos pos : info.getLightingUpdateTodo()) {
         IBlockState state = world.getBlockState(pos);
         world.setBlockState(pos, state, 3);
      }

      info.clearLightingUpdateTodo();
   }

   private void createLoot(final BuildingInfo info, Random random, final World world, final BlockPos pos, BuildingInfo.ConditionTodo todo) {
      if (random.nextFloat() >= this.profile.CHEST_WITHOUT_LOOT_CHANCE) {
         TileEntity tileentity = world.getTileEntity(pos);
         if (tileentity instanceof TileEntityLockableLoot && todo != null) {
            String lootTable = todo.getCondition();
            int level = (pos.getY() - this.profile.GROUNDLEVEL) / 6;
            int floor = (pos.getY() - info.getCityGroundLevel()) / 6;
            ConditionContext conditionContext = new ConditionContext(level, floor, info.floorsBelowGround, info.getNumFloors(), todo.getPart(), todo.getBuilding(), info.chunkX, info.chunkZ) {
               public boolean isSphere() {
                  return CitySphere.isInSphere(info.chunkX, info.chunkZ, pos, LostCityChunkGenerator.this);
               }

               public String getBiome() {
                  return world.getBiome(pos).getBiomeName();
               }
            };
            String randomValue = ((Condition)AssetRegistries.CONDITIONS.get(lootTable)).getRandomValue(random, conditionContext);
            if (randomValue == null) {
               throw new RuntimeException("Condition '" + lootTable + "' did not return a table under certain conditions!");
            }

            ((TileEntityLockableLoot)tileentity).setLootTable(new ResourceLocation(randomValue), random.nextLong());
            tileentity.markDirty();
            if (LostCityConfiguration.DEBUG) {
               LostCities.setup.getLogger().debug("createLootChest: loot=" + randomValue + " pos=" + pos.toString());
            }
         }

      }
   }

   public void populate(int chunkX, int chunkZ) {
      BlockFalling.fallInstantly = true;
      int x = chunkX * 16;
      int z = chunkZ * 16;
      World w = this.worldObj;
      Biome biome = w.getBiomeForCoordsBody(new BlockPos(x + 16, 0, z + 16));
      this.rand.setSeed(w.getSeed());
      long i1 = this.rand.nextLong() / 2L * 2L + 1L;
      long j1 = this.rand.nextLong() / 2L * 2L + 1L;
      this.rand.setSeed((long)chunkX * i1 + (long)chunkZ * j1 ^ w.getSeed());
      boolean flag = false;
      MinecraftForge.EVENT_BUS.post(new Pre(this, w, this.rand, chunkX, chunkZ, flag));
      ChunkPos cp = new ChunkPos(chunkX, chunkZ);
      LostCityProfile profile = BuildingInfo.getProfile(chunkX, chunkZ, this);
      if (profile.GENERATE_MINESHAFTS) {
         this.mineshaftGenerator.generateStructure(w, this.rand, cp);
      }

      if (profile.GENERATE_VILLAGES) {
         if (profile.PREVENT_VILLAGES_IN_CITIES) {
            if (!BuildingInfo.isCity(chunkX, chunkZ, this)) {
               flag = this.villageGenerator.generateStructure(w, this.rand, cp);
            }
         } else {
            flag = this.villageGenerator.generateStructure(w, this.rand, cp);
         }
      }

      if (profile.GENERATE_STRONGHOLDS) {
         this.strongholdGenerator.generateStructure(w, this.rand, cp);
      }

      if (profile.GENERATE_SCATTERED) {
         this.scatteredFeatureGenerator.generateStructure(w, this.rand, cp);
      }

      if (profile.GENERATE_OCEANMONUMENTS) {
         this.oceanMonumentGenerator.generateStructure(w, this.rand, cp);
      }

      if (profile.GENERATE_MANSIONS) {
         this.woodlandMansionGenerator.generateStructure(w, this.rand, cp);
      }

      if (profile.GENERATE_LAKES) {
         boolean isCity = BuildingInfo.isCity(chunkX, chunkZ, this);
         if (!profile.PREVENT_LAKES_RAVINES_IN_CITIES || !isCity) {
            if (biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && !flag && this.rand.nextInt(4) == 0 && TerrainGen.populate(this, w, this.rand, chunkX, chunkZ, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE)) {
               int k1 = x + this.rand.nextInt(16) + 8;
               int l1 = this.rand.nextInt(256);
               int i2 = z + this.rand.nextInt(16) + 8;
               (new WorldGenLakes(profile.getLiquidBlock().getBlock())).generate(w, this.rand, new BlockPos(k1, l1, i2));
            }

            if (TerrainGen.populate(this, w, this.rand, chunkX, chunkZ, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA) && !flag && this.rand.nextInt(8) == 0) {
               int k1 = x + this.rand.nextInt(16) + 8;
               int l1 = this.rand.nextInt(this.rand.nextInt(248) + 8);
               int i2 = z + this.rand.nextInt(16) + 8;
               if (l1 < profile.GROUNDLEVEL - profile.WATERLEVEL_OFFSET || this.rand.nextInt(10) == 0) {
                  (new WorldGenLakes(Blocks.LAVA)).generate(w, this.rand, new BlockPos(k1, l1, i2));
               }
            }
         }
      }

      boolean doGen = false;
      if (profile.GENERATE_DUNGEONS) {
         doGen = TerrainGen.populate(this, w, this.rand, chunkX, chunkZ, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON);

         for(int k1 = 0; doGen && k1 < 8; ++k1) {
            int l1 = x + this.rand.nextInt(16) + 8;
            int i2 = this.rand.nextInt(256);
            int j2 = z + this.rand.nextInt(16) + 8;
            (new WorldGenDungeons()).generate(w, this.rand, new BlockPos(l1, i2, j2));
         }
      }

      BlockPos pos = new BlockPos(x, 0, z);
      biome.decorate(w, this.rand, pos);
      if (TerrainGen.populate(this, w, this.rand, chunkX, chunkZ, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS)) {
         WorldEntitySpawner.performWorldGenSpawning(w, biome, x + 8, z + 8, 16, 16, this.rand);
      }

      x = x + 8;
      z = z + 8;
      doGen = TerrainGen.populate(this, w, this.rand, chunkX, chunkZ, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE);

      for(int k1 = 0; doGen && k1 < 16; ++k1) {
         for(int l1 = 0; l1 < 16; ++l1) {
            int i2 = w.getPrecipitationHeight(new BlockPos(x + k1, 0, z + l1)).getY();
            if (w.canBlockFreeze(new BlockPos(k1 + x, i2 - 1, l1 + z), false)) {
               w.setBlockState(new BlockPos(k1 + x, i2 - 1, l1 + z), Blocks.ICE.getDefaultState(), 2);
            }

            if (w.canSnowAt(new BlockPos(k1 + x, i2, l1 + z), true)) {
               w.setBlockState(new BlockPos(k1 + x, i2, l1 + z), Blocks.SNOW_LAYER.getDefaultState(), 2);
            }
         }
      }

      this.generateTrees(this.rand, chunkX, chunkZ, w, this);
      this.generateVines(this.rand, chunkX, chunkZ, w, this);
      this.generateLootSpawners(this.rand, chunkX, chunkZ, w, this);
      MinecraftForge.EVENT_BUS.post(new Post(this, w, this.rand, chunkX, chunkZ, flag));
      BlockFalling.fallInstantly = false;
   }

   public boolean generateStructures(Chunk chunkIn, int x, int z) {
      boolean flag = false;
      if (this.profile.GENERATE_OCEANMONUMENTS && chunkIn.getInhabitedTime() < 3600L) {
         flag |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkPos(x, z));
      }

      return flag;
   }

   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.getDefaultCreatures(creatureType, pos);
   }

   private List<SpawnListEntry> getDefaultCreatures(EnumCreatureType creatureType, BlockPos pos) {
      Biome Biome = this.worldObj.getBiomeForCoordsBody(pos);
      if (creatureType == EnumCreatureType.MONSTER) {
         if (this.profile.GENERATE_SCATTERED && this.scatteredFeatureGenerator.isInsideStructure(pos)) {
            return this.scatteredFeatureGenerator.getMonsters();
         }

         if (this.profile.GENERATE_OCEANMONUMENTS && this.oceanMonumentGenerator.isPositionInStructure(this.worldObj, pos)) {
            return this.oceanMonumentGenerator.getMonsters();
         }
      }

      return Biome.getSpawnableList(creatureType);
   }

   @Nullable
   public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
      if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
         return this.strongholdGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
         return this.oceanMonumentGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else if ("LostMansion".equals(structureName) && this.woodlandMansionGenerator != null) {
         return this.woodlandMansionGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else if ("Village".equals(structureName) && this.villageGenerator != null) {
         return this.villageGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
         return this.mineshaftGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else {
         return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.getNearestStructurePos(worldIn, position, findUnexplored) : null;
      }
   }

   public void recreateStructures(Chunk chunkIn, int x, int z) {
      if (this.profile.GENERATE_MINESHAFTS) {
         this.mineshaftGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.profile.GENERATE_VILLAGES) {
         if (this.profile.PREVENT_VILLAGES_IN_CITIES) {
            if (!BuildingInfo.isCity(x, z, this)) {
               this.villageGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
            }
         } else {
            this.villageGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
         }
      }

      if (this.profile.GENERATE_STRONGHOLDS) {
         this.strongholdGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.profile.GENERATE_SCATTERED) {
         this.scatteredFeatureGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.profile.GENERATE_OCEANMONUMENTS) {
         this.oceanMonumentGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.profile.GENERATE_MANSIONS) {
         this.woodlandMansionGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
      }

   }

   public boolean hasMansion(int chunkX, int chunkZ) {
      return this.woodlandMansionGenerator != null && this.woodlandMansionGenerator.hasStructure(this.worldObj, chunkX, chunkZ);
   }

   public boolean isInsideStructure(World world, String structureName, BlockPos blockPos) {
      if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
         return this.strongholdGenerator.isInsideStructure(blockPos);
      } else if ("LostMansion".equals(structureName) && this.woodlandMansionGenerator != null) {
         return this.woodlandMansionGenerator.isInsideStructure(blockPos);
      } else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
         return this.oceanMonumentGenerator.isInsideStructure(blockPos);
      } else if ("Village".equals(structureName) && this.villageGenerator != null) {
         return this.villageGenerator.isInsideStructure(blockPos);
      } else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
         return this.mineshaftGenerator.isInsideStructure(blockPos);
      } else {
         return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.isInsideStructure(blockPos) : false;
      }
   }

   public boolean hasOceanMonument(int chunkX, int chunkZ) {
      return this.oceanMonumentGenerator instanceof LostStructureOceanMonument && ((LostStructureOceanMonument)this.oceanMonumentGenerator).hasStructure(this.worldObj, chunkX, chunkZ);
   }

   public ILostChunkInfo getChunkInfo(int chunkX, int chunkZ) {
      return BuildingInfo.getBuildingInfo(chunkX, chunkZ, this);
   }

   public int getRealHeight(int level) {
      return this.profile.GROUNDLEVEL + level * 6;
   }

   public ILostCityAssetRegistry<ILostCityBuilding> getBuildings() {
      return AssetRegistries.BUILDINGS.<ILostCityBuilding>cast();
   }

   public ILostCityAssetRegistry<ILostCityMultiBuilding> getMultiBuildings() {
      return AssetRegistries.MULTI_BUILDINGS.<ILostCityMultiBuilding>cast();
   }

   public ILostCityAssetRegistry<ILostCityCityStyle> getCityStyles() {
      return AssetRegistries.CITYSTYLES.<ILostCityCityStyle>cast();
   }
}
