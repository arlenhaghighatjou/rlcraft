package rtg.world.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import rtg.RTG;
import rtg.RTGConfig;
import rtg.api.RTGAPI;
import rtg.api.util.LimitedArrayCacheMap;
import rtg.api.util.Logger;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.RTGChunkGenSettings;
import rtg.api.world.gen.feature.WorldGenPond;
import rtg.api.world.terrain.TerrainBase;
import rtg.world.biome.BiomeAnalyzer;
import rtg.world.gen.structure.WoodlandMansionRTG;

public class ChunkGeneratorRTG implements IChunkGenerator {
   public final RTGWorld rtgWorld;
   private final RTGChunkGenSettings settings;
   private final MapGenBase caveGenerator;
   private final MapGenBase ravineGenerator;
   private final MapGenStronghold strongholdGenerator;
   private final WoodlandMansionRTG woodlandMansionGenerator;
   private final MapGenMineshaft mineshaftGenerator;
   private final MapGenVillage villageGenerator;
   private final MapGenScatteredFeature scatteredFeatureGenerator;
   private final StructureOceanMonument oceanMonumentGenerator;
   private final World world;
   private final LimitedArrayCacheMap<ChunkPos, ChunkLandscape> landscapeCache = new LimitedArrayCacheMap<ChunkPos, ChunkLandscape>(1024);
   private final int sampleSize = 8;
   private final int sampleArraySize = 21;
   private final int[] biomeData = new int[441];
   private final float[] weightedBiomes = new float[256];
   private final float[][] weightings = new float[441][256];
   private final MesaBiomeCombiner mesaCombiner = new MesaBiomeCombiner();
   private BiomeAnalyzer analyzer = new BiomeAnalyzer();
   private int[] xyinverted;
   private boolean mapFeaturesEnabled;
   private EaglercraftRandom rand;
   private Biome[] baseBiomesList;

   public ChunkGeneratorRTG(RTGWorld rtgWorld) {
      this.xyinverted = this.analyzer.xyinverted();
      Logger.debug("Instantiating CPRTG using generator settings: {}", rtgWorld.world().getWorldInfo().getGeneratorOptions());
      this.world = rtgWorld.world();
      this.rtgWorld = rtgWorld;
      this.settings = rtgWorld.getGeneratorSettings();
      this.rand = new EaglercraftRandom(rtgWorld.seed());
      this.rtgWorld.setRandom(this.rand);
      this.mapFeaturesEnabled = this.world.getWorldInfo().isMapFeaturesEnabled();
      this.caveGenerator = TerrainGen.getModdedMapGen(new MapGenCavesRTG(this.settings.caveChance, this.settings.caveDensity), EventType.CAVE);
      this.ravineGenerator = TerrainGen.getModdedMapGen(new MapGenRavineRTG(this.settings.ravineChance), EventType.RAVINE);
      this.villageGenerator = (MapGenVillage)TerrainGen.getModdedMapGen(new MapGenVillage(ChunkGeneratorRTG.StructureType.VILLAGE.getSettings(this.settings)), EventType.VILLAGE);
      this.strongholdGenerator = (MapGenStronghold)TerrainGen.getModdedMapGen(new MapGenStronghold(ChunkGeneratorRTG.StructureType.STRONGHOLD.getSettings(this.settings)), EventType.STRONGHOLD);
      this.woodlandMansionGenerator = new WoodlandMansionRTG(this, ChunkGeneratorRTG.StructureType.MANSION.getSettings(this.settings));
      this.mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(new MapGenMineshaft(ChunkGeneratorRTG.StructureType.MINESHAFT.getSettings(this.settings)), EventType.MINESHAFT);
      this.scatteredFeatureGenerator = (MapGenScatteredFeature)TerrainGen.getModdedMapGen(new MapGenScatteredFeature(ChunkGeneratorRTG.StructureType.TEMPLE.getSettings(this.settings)), EventType.SCATTERED_FEATURE);
      this.oceanMonumentGenerator = (StructureOceanMonument)TerrainGen.getModdedMapGen(new StructureOceanMonument(ChunkGeneratorRTG.StructureType.MONUMENT.getSettings(this.settings)), EventType.OCEAN_MONUMENT);
      this.baseBiomesList = new Biome[256];
      this.setWeightings();
      Logger.debug("FINISHED instantiating CPRTG.");
   }

   public Chunk generateChunk(int cx, int cz) {
      ChunkPos chunkPos = new ChunkPos(cx, cz);
      BlockPos blockPos = new BlockPos(cx * 16, 0, cz * 16);
      BiomeProvider biomeProvider = this.world.getBiomeProvider();
      this.rand.setSeed((long)cx * 341873128712L + (long)cz * 132897987541L);
      ChunkPrimer primer = new ChunkPrimer();
      ChunkLandscape landscape = this.getLandscape(biomeProvider, chunkPos);
      this.generateTerrain(primer, landscape.noise);

      for(int i = 0; i < 256; ++i) {
         this.baseBiomesList[i] = landscape.biome[i].baseBiome();
      }

      ISimplexData2D jitterData = SimplexData2D.newDisk();
      IRealisticBiome[] jitteredBiomes = new IRealisticBiome[256];

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int x = blockPos.getX() + i;
            int z = blockPos.getZ() + j;
            this.rtgWorld.simplexInstance(0).multiEval2D((double)x, (double)z, jitterData);
            int pX = (int)Math.round((double)x + jitterData.getDeltaX() * (double)RTGConfig.surfaceBlendRadius());
            int pZ = (int)Math.round((double)z + jitterData.getDeltaY() * (double)RTGConfig.surfaceBlendRadius());
            IRealisticBiome actualbiome = landscape.biome[(x & 15) * 16 + (z & 15)];
            IRealisticBiome jitterbiome = landscape.biome[(pX & 15) * 16 + (pZ & 15)];
            jitteredBiomes[i * 16 + j] = actualbiome.getConfig().SURFACE_BLEED_IN.get() && jitterbiome.getConfig().SURFACE_BLEED_OUT.get() ? jitterbiome : actualbiome;
         }
      }

      this.replaceBiomeBlocks(cx, cz, primer, jitteredBiomes, this.baseBiomesList, landscape.noise);
      if (this.settings.useCaves) {
         this.caveGenerator.generate(this.world, cx, cz, primer);
      }

      if (this.settings.useRavines) {
         this.ravineGenerator.generate(this.world, cx, cz, primer);
      }

      if (this.mapFeaturesEnabled) {
         if (this.settings.useMineShafts) {
            this.mineshaftGenerator.generate(this.world, cx, cz, primer);
         }

         if (this.settings.useStrongholds) {
            this.strongholdGenerator.generate(this.world, cx, cz, primer);
         }

         if (this.settings.useVillages) {
            this.villageGenerator.generate(this.world, cx, cz, primer);
         }

         if (this.settings.useTemples) {
            this.scatteredFeatureGenerator.generate(this.world, cx, cz, primer);
         }

         if (this.settings.useMonuments) {
            this.oceanMonumentGenerator.generate(this.world, cx, cz, primer);
         }

         if (this.settings.useMansions) {
            this.woodlandMansionGenerator.generate(this.world, cx, cz, primer);
         }
      }

      Chunk chunk = new Chunk(this.world, primer, cx, cz);
      byte[] abyte1 = chunk.getBiomeArray();

      for(int i = 0; i < abyte1.length; ++i) {
         byte b = (byte)Biome.getIdForBiome(this.baseBiomesList[this.xyinverted[i]]);
         abyte1[i] = b;
      }

      chunk.setBiomeArray(abyte1);
      chunk.generateSkylightMap();
      return chunk;
   }

   public void generateTerrain(ChunkPrimer primer, float[] noise) {
      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int height = (int)noise[x * 16 + z];

            for(int y = 0; y < 256; ++y) {
               if (y > height) {
                  if (y < this.settings.seaLevel) {
                     primer.setBlockState(x, y, z, Blocks.WATER.getDefaultState());
                  } else {
                     primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
                  }
               } else {
                  primer.setBlockState(x, y, z, Blocks.STONE.getDefaultState());
               }
            }
         }
      }

   }

   private void replaceBiomeBlocks(int cx, int cz, ChunkPrimer primer, IRealisticBiome[] biomes, Biome[] base, float[] noise) {
      if (ForgeEventFactory.onReplaceBiomeBlocks(this, cx, cz, primer, this.world)) {
         int worldX = cx * 16;
         int worldZ = cz * 16;
         MutableBlockPos mpos = new MutableBlockPos();

         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               mpos.setPos(worldX + x, 0, worldZ + z);
               float river = -TerrainBase.getRiverStrength(mpos, this.rtgWorld);
               int depth = -1;
               biomes[x * 16 + z].rReplace(primer, mpos, x, z, depth, this.rtgWorld, noise, river, base);
               if (this.settings.bedrockLayers > 1) {
                  for(int bl = 9; bl >= 0; --bl) {
                     if (bl <= this.rand.nextInt(this.settings.bedrockLayers)) {
                        primer.setBlockState(x, bl, z, Blocks.BEDROCK.getDefaultState());
                     }
                  }
               } else {
                  primer.setBlockState(x, 0, z, Blocks.BEDROCK.getDefaultState());
               }
            }
         }

      }
   }

   public void populate(int chunkX, int chunkZ) {
      BlockFalling.fallInstantly = true;
      BiomeProvider biomeProvider = this.world.getBiomeProvider();
      ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
      BlockPos blockPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
      BlockPos offsetpos = blockPos.add(8, 0, 8);
      IRealisticBiome biome = RTGAPI.getRTGBiome(biomeProvider.getBiome(blockPos.add(16, 0, 16)));
      this.rand.setSeed(this.rtgWorld.getChunkSeed(chunkX, chunkZ));
      boolean hasVillage = false;
      ForgeEventFactory.onChunkPopulate(true, this, this.world, this.rand, chunkX, chunkZ, false);
      if (this.mapFeaturesEnabled) {
         if (this.settings.useMineShafts) {
            this.mineshaftGenerator.generateStructure(this.world, this.rand, chunkPos);
         }

         if (this.settings.useStrongholds) {
            this.strongholdGenerator.generateStructure(this.world, this.rand, chunkPos);
         }

         if (this.settings.useVillages) {
            hasVillage = this.villageGenerator.generateStructure(this.world, this.rand, chunkPos);
         }

         if (this.settings.useTemples) {
            this.scatteredFeatureGenerator.generateStructure(this.world, this.rand, chunkPos);
         }

         if (this.settings.useMonuments) {
            this.oceanMonumentGenerator.generateStructure(this.world, this.rand, chunkPos);
         }

         if (this.settings.useMansions) {
            this.woodlandMansionGenerator.generateStructure(this.world, this.rand, chunkPos);
         }
      }

      if (this.settings.useWaterLakes && this.settings.waterLakeChance > 0 && !hasVillage) {
         long nextchance = this.rand.nextLong();
         int surfacechance = this.settings.getSurfaceWaterLakeChance(biome.waterLakeMult());
         BlockPos pos = offsetpos.add(this.rand.nextInt(16), 0, this.rand.nextInt(16));
         if (surfacechance > 0 && nextchance % (long)surfacechance == 0L) {
            if (TerrainGen.populate(this, this.world, this.rand, chunkX, chunkZ, hasVillage, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE)) {
               (new WorldGenPond(Blocks.WATER.getDefaultState())).generate(this.world, this.rand, pos.up(this.rand.nextInt(256)));
            }
         } else if (nextchance % (long)this.settings.waterLakeChance == 0L && TerrainGen.populate(this, this.world, this.rand, chunkX, chunkZ, hasVillage, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE)) {
            (new WorldGenLakes(Blocks.WATER)).generate(this.world, this.rand, pos.up(this.rand.nextInt(50) + 4));
         }
      }

      if (this.settings.useLavaLakes && this.settings.lavaLakeChance > 0 && !hasVillage) {
         long nextchance = this.rand.nextLong();
         int surfacechance = this.settings.getSurfaceLavaLakeChance(biome.lavaLakeMult());
         BlockPos pos = offsetpos.add(this.rand.nextInt(16), 0, this.rand.nextInt(16));
         if (surfacechance > 0 && nextchance % (long)surfacechance == 0L) {
            if (TerrainGen.populate(this, this.world, this.rand, chunkX, chunkZ, hasVillage, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA)) {
               (new WorldGenPond(Blocks.LAVA.getDefaultState())).generate(this.world, this.rand, pos.up(this.rand.nextInt(256)));
            }
         } else if (nextchance % (long)this.settings.lavaLakeChance == 0L && TerrainGen.populate(this, this.world, this.rand, chunkX, chunkZ, hasVillage, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA)) {
            (new WorldGenLakes(Blocks.LAVA)).generate(this.world, this.rand, pos.up(this.rand.nextInt(50) + 4));
         }
      }

      if (this.settings.useDungeons && TerrainGen.populate(this, this.world, this.rand, chunkX, chunkZ, hasVillage, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON)) {
         for(int i = 0; i < this.settings.dungeonChance; ++i) {
            (new WorldGenDungeons()).generate(this.world, this.rand, offsetpos.add(this.rand.nextInt(16), this.rand.nextInt(256), this.rand.nextInt(16)));
         }
      }

      float river = -TerrainBase.getRiverStrength(blockPos.add(16, 0, 16), this.rtgWorld);
      if (!RTG.decorationsDisable() && !biome.getConfig().DISABLE_RTG_DECORATIONS.get()) {
         if (river > 0.9F) {
            biome.getRiverBiome().rDecorate(this.rtgWorld, this.rand, chunkPos, river, hasVillage);
         } else {
            biome.rDecorate(this.rtgWorld, this.rand, chunkPos, river, hasVillage);
         }
      } else if (river > 0.9F) {
         biome.getRiverBiome().baseBiome().decorate(this.world, this.rand, blockPos);
      } else {
         biome.baseBiome().decorate(this.world, this.rand, blockPos);
      }

      if (TerrainGen.populate(this, this.world, this.rand, chunkX, chunkZ, hasVillage, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS)) {
         WorldEntitySpawner.performWorldGenSpawning(this.world, biome.baseBiome(), blockPos.getX() + 8, blockPos.getZ() + 8, 16, 16, this.rand);
      }

      if (TerrainGen.populate(this, this.world, this.rand, chunkX, chunkZ, hasVillage, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE)) {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               BlockPos freezePos = this.world.getPrecipitationHeight(offsetpos.add(x, 0, z)).down();
               if (this.world.canBlockFreezeWater(freezePos)) {
                  this.world.setBlockState(freezePos, Blocks.ICE.getDefaultState(), 2);
               }

               BlockPos surfacePos = this.world.getTopSolidOrLiquidBlock(offsetpos.add(x, 0, z));
               if (this.settings.useSnowLayers) {
                  for(BlockPos checkPos = surfacePos.up(32); checkPos.getY() >= surfacePos.getY(); checkPos = checkPos.down()) {
                     if (this.world.getBlockState(checkPos).getMaterial() == Material.AIR) {
                        float temp = biomeProvider.getBiome(surfacePos).getTemperature(checkPos);
                        if (temp <= this.settings.getClampedSnowLayerTemp()) {
                           IBlockState below = this.world.getBlockState(checkPos.down());
                           if (below.isFullBlock() || below.getMaterial() == Material.LEAVES) {
                              this.world.setBlockState(checkPos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                              checkPos = checkPos.down();
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      ForgeEventFactory.onChunkPopulate(false, this, this.world, this.rand, chunkX, chunkZ, hasVillage);
      BlockFalling.fallInstantly = false;
   }

   public boolean generateStructures(Chunk chunkIn, int x, int z) {
      boolean flag = false;
      if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L) {
         flag = this.oceanMonumentGenerator.generateStructure(this.world, this.rand, new ChunkPos(x, z));
      }

      return flag;
   }

   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      Biome biome = this.world.getBiome(pos);
      if (this.mapFeaturesEnabled) {
         if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(pos)) {
            return this.scatteredFeatureGenerator.getMonsters();
         }

         if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.world, pos)) {
            return this.oceanMonumentGenerator.getMonsters();
         }
      }

      return biome.getSpawnableList(creatureType);
   }

   @Nullable
   public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
      if (!this.mapFeaturesEnabled) {
         return null;
      } else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
         return this.strongholdGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else if ("Mansion".equals(structureName) && this.woodlandMansionGenerator != null) {
         return this.woodlandMansionGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
         return this.oceanMonumentGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else if ("Village".equals(structureName) && this.villageGenerator != null) {
         return this.villageGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
         return this.mineshaftGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
      } else {
         if ("Temple".equals(structureName) && this.scatteredFeatureGenerator != null) {
            this.scatteredFeatureGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
         }

         return null;
      }
   }

   public void recreateStructures(Chunk chunk, int cx, int cz) {
      if (this.mapFeaturesEnabled) {
         if (this.settings.useMineShafts) {
            this.mineshaftGenerator.generate(this.world, cx, cz, (ChunkPrimer)null);
         }

         if (this.settings.useVillages) {
            this.villageGenerator.generate(this.world, cx, cz, (ChunkPrimer)null);
         }

         if (this.settings.useStrongholds) {
            this.strongholdGenerator.generate(this.world, cx, cz, (ChunkPrimer)null);
         }

         if (this.settings.useTemples) {
            this.scatteredFeatureGenerator.generate(this.world, cx, cz, (ChunkPrimer)null);
         }

         if (this.settings.useMonuments) {
            this.oceanMonumentGenerator.generate(this.world, cx, cz, (ChunkPrimer)null);
         }

         if (this.settings.useMansions) {
            this.woodlandMansionGenerator.generate(this.world, cx, cz, (ChunkPrimer)null);
         }
      }

   }

   public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
      if (!this.mapFeaturesEnabled) {
         return false;
      } else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
         return this.strongholdGenerator.isInsideStructure(pos);
      } else if ("Mansion".equals(structureName) && this.woodlandMansionGenerator != null) {
         return this.woodlandMansionGenerator.isInsideStructure(pos);
      } else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
         return this.oceanMonumentGenerator.isInsideStructure(pos);
      } else if ("Village".equals(structureName) && this.villageGenerator != null) {
         return this.villageGenerator.isInsideStructure(pos);
      } else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
         return this.mineshaftGenerator.isInsideStructure(pos);
      } else {
         return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null && this.scatteredFeatureGenerator.isInsideStructure(pos);
      }
   }

   private void setWeightings() {
      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            float limit = (float)Math.pow(3136.0D, 0.7D);

            for(int mapX = 0; mapX < 21; ++mapX) {
               for(int mapZ = 0; mapZ < 21; ++mapZ) {
                  float xDist = (float)(x - (mapX - 8) * 8);
                  float zDist = (float)(z - (mapZ - 8) * 8);
                  float distanceSquared = xDist * xDist + zDist * zDist;
                  float distance = (float)Math.pow((double)distanceSquared, 0.7D);
                  float weight = 1.0F - distance / limit;
                  if (weight < 0.0F) {
                     weight = 0.0F;
                  }

                  this.weightings[mapX * 21 + mapZ][x * 16 + z] = weight;
               }
            }
         }
      }

   }

   public ChunkLandscape getLandscape(BiomeProvider biomeProvider, ChunkPos chunkPos) {
      BlockPos blockPos = new BlockPos(chunkPos.x * 16, 0, chunkPos.z * 16);
      ChunkLandscape landscape = this.landscapeCache.get(chunkPos);
      if (landscape == null) {
         landscape = this.generateLandscape(biomeProvider, blockPos);
         this.landscapeCache.put(chunkPos, landscape);
      }

      return landscape;
   }

   private synchronized ChunkLandscape generateLandscape(BiomeProvider biomeProvider, BlockPos blockPos) {
      ChunkLandscape landscape = new ChunkLandscape();
      this.getNewerNoise(biomeProvider, blockPos.getX(), blockPos.getZ(), landscape);
      Biome[] biomes = new Biome[256];

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            biomes[x * 16 + z] = biomeProvider.getBiome(blockPos.add(x, 0, z));
         }
      }

      this.analyzer.newRepair(biomes, this.biomeData, landscape);
      return landscape;
   }

   private synchronized void getNewerNoise(BiomeProvider biomeProvider, int worldX, int worldZ, ChunkLandscape landscape) {
      for(int x = -8; x < 13; ++x) {
         for(int z = -8; z < 13; ++z) {
            this.biomeData[(x + 8) * 21 + z + 8] = Biome.getIdForBiome(biomeProvider.getBiome(new BlockPos(worldX + x * 8, 0, worldZ + z * 8)));
         }
      }

      MutableBlockPos mpos = new MutableBlockPos(worldX, 0, worldZ);

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            float totalWeight = 0.0F;

            for(int mapX = 0; mapX < 21; ++mapX) {
               for(int mapZ = 0; mapZ < 21; ++mapZ) {
                  float weight = this.weightings[mapX * 21 + mapZ][x * 16 + z];
                  if (weight > 0.0F) {
                     totalWeight += weight;
                     this.weightedBiomes[this.biomeData[mapX * 21 + mapZ]] += weight;
                  }
               }
            }

            for(int biomeIndex = 0; biomeIndex < this.weightedBiomes.length; ++biomeIndex) {
               this.weightedBiomes[biomeIndex] /= totalWeight;
            }

            this.mesaCombiner.adjust(this.weightedBiomes);
            landscape.noise[x * 16 + z] = 0.0F;
            float river = TerrainBase.getRiverStrength(mpos.setPos(worldX + x, 0, worldZ + z), this.rtgWorld);
            landscape.river[x * 16 + z] = -river;

            for(int i = 0; i < 256; ++i) {
               if (this.weightedBiomes[i] > 0.0F) {
                  landscape.noise[x * 16 + z] += RTGAPI.getRTGBiome(i).rNoise(this.rtgWorld, worldX + x, worldZ + z, this.weightedBiomes[i], river + 1.0F) * this.weightedBiomes[i];
                  this.weightedBiomes[i] = 0.0F;
               }
            }
         }
      }

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            BlockPos pos = new BlockPos(worldX + (x - 7) * 8 + 4, 0, worldZ + (z - 7) * 8 + 4);
            landscape.biome[x * 16 + z] = RTGAPI.getRTGBiome(biomeProvider.getBiome(pos));
         }
      }

   }

   private static enum StructureType {
      MINESHAFT,
      MONUMENT,
      STRONGHOLD,
      TEMPLE,
      VILLAGE,
      MANSION;

      private StructureType() {
      }

      Map<String, String> getSettings(RTGChunkGenSettings settings) {
         Map<String, String> ret = new HashMap();
         if (this == MINESHAFT) {
            ret.put("chance", String.valueOf(settings.mineShaftChance));
            return ret;
         } else if (this == MONUMENT) {
            ret.put("separation", String.valueOf(settings.monumentSeparation));
            ret.put("spacing", String.valueOf(settings.monumentSpacing));
            return ret;
         } else if (this == STRONGHOLD) {
            ret.put("count", String.valueOf(settings.strongholdCount));
            ret.put("distance", String.valueOf(settings.strongholdDistance));
            ret.put("spread", String.valueOf(settings.strongholdSpread));
            return ret;
         } else if (this == TEMPLE) {
            ret.put("distance", String.valueOf(settings.templeDistance));
            return ret;
         } else if (this == VILLAGE) {
            ret.put("distance", String.valueOf(settings.villageDistance));
            ret.put("size", String.valueOf(settings.villageSize));
            return ret;
         } else if (this == MANSION) {
            ret.put("spacing", String.valueOf(settings.mansionSpacing));
            ret.put("separation", String.valueOf(settings.mansionSeparation));
            return ret;
         } else {
            return ret;
         }
      }
   }
}
