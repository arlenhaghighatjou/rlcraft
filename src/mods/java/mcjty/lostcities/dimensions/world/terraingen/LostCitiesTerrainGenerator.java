package mcjty.lostcities.dimensions.world.terraingen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import mcjty.lostcities.api.LostCityEvent;
import mcjty.lostcities.api.RailChunkType;
import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.ChunkHeightmap;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.driver.IIndex;
import mcjty.lostcities.dimensions.world.driver.IPrimerDriver;
import mcjty.lostcities.dimensions.world.driver.OptimizedDriver;
import mcjty.lostcities.dimensions.world.driver.SafeDriver;
import mcjty.lostcities.dimensions.world.lost.BiomeInfo;
import mcjty.lostcities.dimensions.world.lost.BuildingInfo;
import mcjty.lostcities.dimensions.world.lost.CitySphere;
import mcjty.lostcities.dimensions.world.lost.DamageArea;
import mcjty.lostcities.dimensions.world.lost.Direction;
import mcjty.lostcities.dimensions.world.lost.Highway;
import mcjty.lostcities.dimensions.world.lost.Orientation;
import mcjty.lostcities.dimensions.world.lost.Railway;
import mcjty.lostcities.dimensions.world.lost.Transform;
import mcjty.lostcities.dimensions.world.lost.cityassets.AssetRegistries;
import mcjty.lostcities.dimensions.world.lost.cityassets.BuildingPart;
import mcjty.lostcities.dimensions.world.lost.cityassets.CityStyle;
import mcjty.lostcities.dimensions.world.lost.cityassets.CompiledPalette;
import mcjty.lostcities.dimensions.world.lost.cityassets.IBuildingPart;
import mcjty.lostcities.dimensions.world.lost.cityassets.Palette;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.varia.GeometryTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

public class LostCitiesTerrainGenerator extends NormalTerrainGenerator {
   private static int g_seed = 123456789;
   private final int mainGroundLevel;
   private final int waterLevel;
   private boolean charsSetup = false;
   public static char airChar;
   public static char hardAirChar;
   public static char glowstoneChar;
   public static char gravelChar;
   public static char glassChar;
   public static char leavesChar;
   public static char leaves2Char;
   public static char leaves3Char;
   public static char ironbarsChar;
   public static char grassChar;
   public static char bedrockChar;
   public static char endportalChar;
   public static char endportalFrameChar;
   public static char goldBlockChar;
   public static char diamondBlockChar;
   public char liquidChar;
   public char baseChar;
   private static Set<Character> rotatableChars = null;
   private static Set<Character> railChars = null;
   private static Set<Character> glassChars = null;
   private static Set<Character> charactersNeedingTodo = null;
   private static Set<Character> charactersNeedingLightingUpdate = null;
   private Character street;
   private Character streetBase;
   private Character street2;
   private int streetBorder;
   private NoiseGeneratorPerlin rubbleNoise;
   private NoiseGeneratorPerlin leavesNoise;
   private NoiseGeneratorPerlin ruinNoise;
   private static char[] randomLeafs = null;
   private IPrimerDriver driver = (IPrimerDriver)(LostCityConfiguration.OPTIMIZED_CHUNKGEN ? new OptimizedDriver() : new SafeDriver());
   private IslandTerrainGenerator islandTerrainGenerator = new IslandTerrainGenerator(4);
   private CavernTerrainGenerator cavernTerrainGenerator = new CavernTerrainGenerator();
   private SpaceTerrainGenerator spaceTerrainGenerator = new SpaceTerrainGenerator();
   private double[] rubbleBuffer = new double[256];
   private double[] leavesBuffer = new double[256];
   private double[] ruinBuffer = new double[256];

   public LostCitiesTerrainGenerator(LostCityChunkGenerator provider) {
      super(provider);
      this.mainGroundLevel = provider.getProfile().GROUNDLEVEL;
      this.waterLevel = provider.getProfile().GROUNDLEVEL - provider.getProfile().WATERLEVEL_OFFSET;
      this.rubbleNoise = new NoiseGeneratorPerlin(provider.rand, 4);
      this.leavesNoise = new NoiseGeneratorPerlin(provider.rand, 4);
      this.ruinNoise = new NoiseGeneratorPerlin(provider.rand, 4);
      this.islandTerrainGenerator.setup(provider.worldObj, provider);
      this.cavernTerrainGenerator.setup(provider.worldObj, provider);
      this.spaceTerrainGenerator.setup(provider.worldObj, provider);
   }

   public static char getRandomLeaf() {
      if (randomLeafs == null) {
         randomLeafs = new char[128];

         int i;
         for(i = 0; i < 20; ++i) {
            randomLeafs[i] = leaves2Char;
         }

         while(i < 40) {
            randomLeafs[i] = leaves3Char;
            ++i;
         }

         while(i < randomLeafs.length) {
            randomLeafs[i] = leavesChar;
            ++i;
         }
      }

      return randomLeafs[fastrand128()];
   }

   public static Set<Character> getRailChars() {
      if (railChars == null) {
         railChars = new HashSet();
         addStates(Blocks.RAIL, railChars);
         addStates(Blocks.GOLDEN_RAIL, railChars);
      }

      return railChars;
   }

   public static Set<Character> getGlassChars() {
      if (glassChars == null) {
         glassChars = new HashSet();
         addStates(Blocks.GLASS, glassChars);
         addStates(Blocks.STAINED_GLASS, glassChars);
         addStates(Blocks.GLASS_PANE, glassChars);
         addStates(Blocks.STAINED_GLASS_PANE, glassChars);
      }

      return glassChars;
   }

   public static Set<Character> getCharactersNeedingTodo() {
      if (charactersNeedingTodo == null) {
         charactersNeedingTodo = new HashSet();
         charactersNeedingTodo.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, EnumType.ACACIA))));
         charactersNeedingTodo.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, EnumType.BIRCH))));
         charactersNeedingTodo.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, EnumType.OAK))));
         charactersNeedingTodo.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, EnumType.SPRUCE))));
         charactersNeedingTodo.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, EnumType.DARK_OAK))));
         charactersNeedingTodo.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, EnumType.JUNGLE))));
         charactersNeedingTodo.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(Blocks.RED_FLOWER.getDefaultState())));
         charactersNeedingTodo.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(Blocks.YELLOW_FLOWER.getDefaultState())));
      }

      return charactersNeedingTodo;
   }

   public static Set<Character> getCharactersNeedingLightingUpdate() {
      if (charactersNeedingLightingUpdate == null) {
         charactersNeedingLightingUpdate = new HashSet();

         for(String s : LostCityConfiguration.BLOCKS_REQUIRING_LIGHTING_UPDATES) {
            Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
            if (block != null) {
               addStates(block, charactersNeedingLightingUpdate);
            }
         }
      }

      return charactersNeedingLightingUpdate;
   }

   public static Set<Character> getRotatableChars() {
      if (rotatableChars == null) {
         rotatableChars = new HashSet();
         addStates(Blocks.ACACIA_STAIRS, rotatableChars);
         addStates(Blocks.BIRCH_STAIRS, rotatableChars);
         addStates(Blocks.BRICK_STAIRS, rotatableChars);
         addStates(Blocks.QUARTZ_STAIRS, rotatableChars);
         addStates(Blocks.STONE_BRICK_STAIRS, rotatableChars);
         addStates(Blocks.DARK_OAK_STAIRS, rotatableChars);
         addStates(Blocks.JUNGLE_STAIRS, rotatableChars);
         addStates(Blocks.NETHER_BRICK_STAIRS, rotatableChars);
         addStates(Blocks.OAK_STAIRS, rotatableChars);
         addStates(Blocks.PURPUR_STAIRS, rotatableChars);
         addStates(Blocks.RED_SANDSTONE_STAIRS, rotatableChars);
         addStates(Blocks.SANDSTONE_STAIRS, rotatableChars);
         addStates(Blocks.SPRUCE_STAIRS, rotatableChars);
         addStates(Blocks.STONE_STAIRS, rotatableChars);
         addStates(Blocks.LADDER, rotatableChars);
      }

      return rotatableChars;
   }

   private static void addStates(Block block, Set<Character> set) {
      for(int m = 0; m < 16; ++m) {
         try {
            IBlockState state = block.getStateFromMeta(m);
            set.add(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(state)));
         } catch (Exception var4) {
            ;
         }
      }

   }

   public void setupChars(LostCityProfile profile) {
      if (!this.charsSetup) {
         airChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.AIR.getDefaultState());
         hardAirChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.COMMAND_BLOCK.getDefaultState());
         glowstoneChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.GLOWSTONE.getDefaultState());
         gravelChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.GRAVEL.getDefaultState());
         this.baseChar = (char)Block.BLOCK_STATE_IDS.get(profile.getBaseBlock());
         this.liquidChar = (char)Block.BLOCK_STATE_IDS.get(profile.getLiquidBlock());
         glassChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.GLASS.getDefaultState());
         leavesChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, Boolean.valueOf(false)));
         leaves2Char = (char)Block.BLOCK_STATE_IDS.get(Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, Boolean.valueOf(false)).withProperty(BlockOldLeaf.VARIANT, EnumType.JUNGLE));
         leaves3Char = (char)Block.BLOCK_STATE_IDS.get(Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, Boolean.valueOf(false)).withProperty(BlockOldLeaf.VARIANT, EnumType.SPRUCE));
         ironbarsChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.IRON_BARS.getDefaultState());
         grassChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.GRASS.getDefaultState());
         bedrockChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.BEDROCK.getDefaultState());
         endportalChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.END_PORTAL.getDefaultState());
         endportalFrameChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.END_PORTAL_FRAME.getDefaultState());
         goldBlockChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.GOLD_BLOCK.getDefaultState());
         diamondBlockChar = (char)Block.BLOCK_STATE_IDS.get(Blocks.DIAMOND_BLOCK.getDefaultState());
         this.charsSetup = true;
      }

   }

   private static int fastrand() {
      g_seed = 214013 * g_seed + 2531011;
      return g_seed >> 16 & 32767;
   }

   public static int fastrand128() {
      g_seed = 214013 * g_seed + 2531011;
      return g_seed >> 16 & 127;
   }

   public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
      this.driver.setPrimer(primer);
      BuildingInfo info = BuildingInfo.getBuildingInfo(chunkX, chunkZ, this.provider);
      CityStyle cityStyle = info.getCityStyle();
      this.street = info.getCompiledPalette().get(cityStyle.getStreetBlock().charValue());
      this.streetBase = info.getCompiledPalette().get(cityStyle.getStreetBaseBlock().charValue());
      this.street2 = info.getCompiledPalette().get(cityStyle.getStreetVariantBlock().charValue());
      this.streetBorder = (16 - cityStyle.getStreetWidth()) / 2;
      if (!info.isCity && (!info.outsideChunk || !info.hasBuilding)) {
         this.doNormalChunk(chunkX, chunkZ, info);
      } else {
         this.doCityChunk(chunkX, chunkZ, info);
      }

      Railway.RailChunkInfo railInfo = info.getRailInfo();
      if (railInfo.getType() != RailChunkType.NONE) {
         this.generateRailways(info, railInfo);
      }

      this.generateRailwayDungeons(info);
      if (this.provider.getProfile().isSpace()) {
         this.generateMonorails(info);
      }

      this.fixTorches(info);
      this.provider.rand.setSeed((long)chunkX * 257017164707L + (long)chunkZ * 101754694003L);
      LostCityEvent.PreExplosionEvent event = new LostCityEvent.PreExplosionEvent(this.provider.worldObj, this.provider, chunkX, chunkZ, this.driver.getPrimer());
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         if (info.getDamageArea().hasExplosions()) {
            this.breakBlocksForDamage(chunkX, chunkZ, info);
            this.fixAfterExplosionNew(info, this.provider.rand);
         }

         this.generateDebris(this.provider.rand, info);
      }

   }

   private void generateMonorails(BuildingInfo info) {
      boolean horiz = info.hasHorizontalMonorail();
      boolean vert = info.hasVerticalMonorail();
      if (horiz && vert) {
         if (!CitySphere.intersectsWithCitySphere(info.chunkX, info.chunkZ, this.provider)) {
            BuildingPart part = AssetRegistries.PARTS.get("monorails_both");
            this.generatePart(info, part, Transform.ROTATE_NONE, 0, this.mainGroundLevel + info.profile.CITYSPHERE_MONORAIL_HEIGHT_OFFSET, 0, true);
         }

      } else {
         Transform transform;
         if (horiz) {
            transform = Transform.ROTATE_90;
         } else {
            if (!vert) {
               return;
            }

            transform = Transform.ROTATE_NONE;
         }

         BuildingPart part;
         if (CitySphere.fullyInsideCitySpere(info.chunkX, info.chunkZ, this.provider)) {
            if (this.hasNonStationMonoRail(info.getXmin())) {
               part = AssetRegistries.PARTS.get("monorails_station");
               Character borderBlock = info.getCityStyle().getBorderBlock();
               transform = Transform.MIRROR_90_X;
               this.fillToGround(info, this.mainGroundLevel + info.profile.CITYSPHERE_MONORAIL_HEIGHT_OFFSET, borderBlock);
            } else if (this.hasNonStationMonoRail(info.getXmax())) {
               part = AssetRegistries.PARTS.get("monorails_station");
               Character borderBlock = info.getCityStyle().getBorderBlock();
               transform = Transform.ROTATE_90;
               this.fillToGround(info, this.mainGroundLevel + info.profile.CITYSPHERE_MONORAIL_HEIGHT_OFFSET, borderBlock);
            } else if (this.hasNonStationMonoRail(info.getZmin())) {
               part = AssetRegistries.PARTS.get("monorails_station");
               Character borderBlock = info.getCityStyle().getBorderBlock();
               transform = Transform.ROTATE_NONE;
               this.fillToGround(info, this.mainGroundLevel + info.profile.CITYSPHERE_MONORAIL_HEIGHT_OFFSET, borderBlock);
            } else {
               if (!this.hasNonStationMonoRail(info.getZmax())) {
                  return;
               }

               part = AssetRegistries.PARTS.get("monorails_station");
               Character borderBlock = info.getCityStyle().getBorderBlock();
               transform = Transform.MIRROR_Z;
               this.fillToGround(info, this.mainGroundLevel + info.profile.CITYSPHERE_MONORAIL_HEIGHT_OFFSET, borderBlock);
            }
         } else {
            part = AssetRegistries.PARTS.get("monorails_vertical");
         }

         this.generatePart(info, part, transform, 0, this.mainGroundLevel + info.profile.CITYSPHERE_MONORAIL_HEIGHT_OFFSET, 0, true);
      }
   }

   private boolean hasNonStationMonoRail(BuildingInfo info) {
      return info.hasMonorail() && !CitySphere.fullyInsideCitySpere(info.chunkX, info.chunkZ, this.provider);
   }

   private void fixTorches(BuildingInfo info) {
      List<Pair<IIndex, Map<String, Integer>>> torches = info.getTorchTodo();
      if (!torches.isEmpty()) {
         for(Pair<IIndex, Map<String, Integer>> pair : torches) {
            IIndex idx = (IIndex)pair.getLeft();
            this.driver.current(idx);
            Map<String, Integer> map = (Map)pair.getRight();
            char torch = this.driver.getBlock();
            IBlockState torchState = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(torch);
            if (map != null) {
               int x = this.driver.getX();
               int z = this.driver.getZ();
               if (this.driver.getY() != 0 && this.driver.getBlockDown() != airChar) {
                  this.driver.block(torchState.getBlock().getStateFromMeta(((Integer)map.get("up")).intValue()));
               } else if (x > 0 && this.driver.getBlockWest() != airChar) {
                  this.driver.block(torchState.getBlock().getStateFromMeta(((Integer)map.get("east")).intValue()));
               } else if (x < 15 && this.driver.getBlockEast() != airChar) {
                  this.driver.block(torchState.getBlock().getStateFromMeta(((Integer)map.get("west")).intValue()));
               } else if (z > 0 && this.driver.getBlockNorth() != airChar) {
                  this.driver.block(torchState.getBlock().getStateFromMeta(((Integer)map.get("south")).intValue()));
               } else if (z < 15 && this.driver.getBlockSouth() != airChar) {
                  this.driver.block(torchState.getBlock().getStateFromMeta(((Integer)map.get("north")).intValue()));
               }
            }
         }

         info.clearTorchTodo();
      }
   }

   public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomes) {
      switch(this.provider.getProfile().LANDSCAPE_TYPE) {
      case DEFAULT:
         super.replaceBlocksForBiome(chunkX, chunkZ, primer, biomes);
         break;
      case FLOATING:
         this.islandTerrainGenerator.replaceBlocksForBiome(chunkX, chunkZ, primer, biomes, this);
         break;
      case SPACE:
         this.spaceTerrainGenerator.replaceBlocksForBiome(chunkX, chunkZ, primer, biomes, this);
         break;
      case CAVERN:
         this.cavernTerrainGenerator.replaceBlocksForBiome(chunkX, chunkZ, primer, biomes, this);
      }

   }

   public void doCoreChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
      switch(this.provider.getProfile().LANDSCAPE_TYPE) {
      case DEFAULT:
         this.defaultGenerate(chunkX, chunkZ, primer);
         break;
      case FLOATING:
         this.islandTerrainGenerator.generate(chunkX, chunkZ, primer, this);
         break;
      case SPACE:
         this.spaceTerrainGenerator.generate(chunkX, chunkZ, primer, this);
         break;
      case CAVERN:
         this.cavernTerrainGenerator.generate(chunkX, chunkZ, primer, this);
      }

   }

   private void defaultGenerate(int chunkX, int chunkZ, ChunkPrimer primer) {
      IPrimerDriver driver = (IPrimerDriver)(LostCityConfiguration.OPTIMIZED_CHUNKGEN ? new OptimizedDriver() : new SafeDriver());
      driver.setPrimer(primer);
      this.generateHeightmap(chunkX, chunkZ);

      for(int x4 = 0; x4 < 4; ++x4) {
         int l = x4 * 5;
         int i1 = (x4 + 1) * 5;

         for(int z4 = 0; z4 < 4; ++z4) {
            int k1 = (l + z4) * 33;
            int l1 = (l + z4 + 1) * 33;
            int i2 = (i1 + z4) * 33;
            int j2 = (i1 + z4 + 1) * 33;

            for(int height32 = 0; height32 < 32; ++height32) {
               double d1 = this.heightMap[k1 + height32];
               double d2 = this.heightMap[l1 + height32];
               double d3 = this.heightMap[i2 + height32];
               double d4 = this.heightMap[j2 + height32];
               double d5 = (this.heightMap[k1 + height32 + 1] - d1) * 0.125D;
               double d6 = (this.heightMap[l1 + height32 + 1] - d2) * 0.125D;
               double d7 = (this.heightMap[i2 + height32 + 1] - d3) * 0.125D;
               double d8 = (this.heightMap[j2 + height32 + 1] - d4) * 0.125D;

               for(int h = 0; h < 8; ++h) {
                  double d10 = d1;
                  double d11 = d2;
                  double d12 = (d3 - d1) * 0.25D;
                  double d13 = (d4 - d2) * 0.25D;
                  int height = height32 * 8 + h;

                  for(int x = 0; x < 4; ++x) {
                     driver.current(x + x4 * 4, height, 0 + z4 * 4);
                     double d16 = (d11 - d10) * 0.25D;
                     double d15 = d10 - d16;

                     for(int z = 0; z < 4; ++z) {
                        if ((d15 += d16) > 0.0D) {
                           driver.block(this.baseChar);
                        } else if (height < this.waterLevel) {
                           driver.block(this.liquidChar);
                        }

                        driver.incZ();
                     }

                     d10 += d12;
                     d11 += d13;
                  }

                  d1 += d5;
                  d2 += d6;
                  d3 += d7;
                  d4 += d8;
               }
            }
         }
      }

   }

   private void doNormalChunk(int chunkX, int chunkZ, BuildingInfo info) {
      if (info.profile.isDefault()) {
         this.flattenChunkToCityBorder(chunkX, chunkZ);
      }

      LostCityEvent.PostGenOutsideChunkEvent postevent = new LostCityEvent.PostGenOutsideChunkEvent(this.provider.worldObj, this.provider, chunkX, chunkZ, this.driver.getPrimer());
      MinecraftForge.EVENT_BUS.post(postevent);
      this.generateBridges(info);
      this.generateHighways(chunkX, chunkZ, info);
   }

   private void breakBlocksForDamage(int chunkX, int chunkZ, BuildingInfo info) {
      int cx = chunkX * 16;
      int cz = chunkZ * 16;
      DamageArea damageArea = info.getDamageArea();
      boolean clear = false;
      float damageFactor = 1.0F;

      for(int yy = 0; yy < 16; ++yy) {
         if (clear || damageArea.hasExplosions(yy)) {
            if (!clear && !damageArea.isCompletelyDestroyed(yy)) {
               for(int y = 0; y < 16; ++y) {
                  int cntDamaged = 0;
                  int cntAir = 0;
                  int cury = yy * 16 + y;

                  for(int x = 0; x < 16; ++x) {
                     this.driver.current(x, cury, 0);

                     for(int z = 0; z < 16; ++z) {
                        char d = this.driver.getBlock();
                        if (d == airChar && cury > info.waterLevel) {
                           ++cntAir;
                        } else {
                           float damage = damageArea.getDamage(cx + x, cury, cz + z) * damageFactor;
                           if ((double)damage >= 0.001D) {
                              Character newd = damageArea.damageBlock(Character.valueOf(d), this.provider, cury, damage, info.getCompiledPalette(), this.liquidChar);
                              if (newd.charValue() != d) {
                                 this.driver.block(newd.charValue());
                                 ++cntDamaged;
                              }
                           }
                        }

                        this.driver.incZ();
                     }
                  }

                  int tot = cntDamaged + cntAir;
                  if (tot > 250) {
                     damageFactor = 200.0F;
                     clear = true;
                  } else if (tot > 220) {
                     damageFactor *= 1.4F;
                  } else if (tot > 180) {
                     damageFactor *= 1.2F;
                  }
               }
            } else {
               for(int x = 0; x < 16; ++x) {
                  for(int z = 0; z < 16; ++z) {
                     int height = yy * 16;
                     this.driver.current(x, height, z);

                     for(int y = 0; y < 16; ++y) {
                        this.driver.add(height + y <= info.waterLevel ? this.liquidChar : airChar);
                     }
                  }
               }

               clear = true;
            }
         }
      }

   }

   private void generateHighways(int chunkX, int chunkZ, BuildingInfo info) {
      int levelX = Highway.getXHighwayLevel(chunkX, chunkZ, this.provider, info.profile);
      int levelZ = Highway.getZHighwayLevel(chunkX, chunkZ, this.provider, info.profile);
      if (levelX == levelZ && levelX >= 0) {
         this.generateHighwayPart(info, levelX, Transform.ROTATE_NONE, info.getXmax(), info.getZmax(), "_bi");
      } else if (levelX >= 0 && levelZ >= 0) {
         if (levelX == 0) {
            this.generateHighwayPart(info, levelX, Transform.ROTATE_NONE, info.getZmin(), info.getZmax(), "");
            this.generateHighwayPart(info, levelZ, Transform.ROTATE_90, info.getXmax(), info.getXmax(), "");
         } else {
            this.generateHighwayPart(info, levelZ, Transform.ROTATE_90, info.getXmax(), info.getXmax(), "");
            this.generateHighwayPart(info, levelX, Transform.ROTATE_NONE, info.getZmin(), info.getZmax(), "");
         }
      } else if (levelX >= 0) {
         this.generateHighwayPart(info, levelX, Transform.ROTATE_NONE, info.getZmin(), info.getZmax(), "");
      } else if (levelZ >= 0) {
         this.generateHighwayPart(info, levelZ, Transform.ROTATE_90, info.getXmax(), info.getXmax(), "");
      }

   }

   private void generateHighwayPart(BuildingInfo info, int level, Transform transform, BuildingInfo adjacent1, BuildingInfo adjacent2, String suffix) {
      int highwayGroundLevel = info.groundLevel + level * 6;
      BuildingPart part;
      if (info.isTunnel(level)) {
         part = AssetRegistries.PARTS.get("highway_tunnel" + suffix);
         this.generatePart(info, part, transform, 0, highwayGroundLevel, 0, true);
      } else if (info.isCity && level <= adjacent1.cityLevel && level <= adjacent2.cityLevel && adjacent1.isCity && adjacent2.isCity) {
         part = AssetRegistries.PARTS.get("highway_open" + suffix);
         int height = this.generatePart(info, part, transform, 0, highwayGroundLevel, 0, true);
         if (!info.profile.isCavern()) {
            int clearheight = 15;

            for(int x = 0; x < 16; ++x) {
               for(int z = 0; z < 16; ++z) {
                  this.clearRange(info, x, z, height, height + clearheight, info.waterLevel > info.groundLevel);
               }
            }
         }
      } else {
         part = AssetRegistries.PARTS.get("highway_bridge" + suffix);
         int height = this.generatePart(info, part, transform, 0, highwayGroundLevel, 0, true);
         if (!info.profile.isCavern()) {
            int clearheight = 15;

            for(int x = 0; x < 16; ++x) {
               for(int z = 0; z < 16; ++z) {
                  this.clearRange(info, x, z, height, height + clearheight, info.waterLevel > info.groundLevel);
               }
            }
         }
      }

      Character support = part.getMetaChar("support");
      if (info.profile.HIGHWAY_SUPPORTS && support != null) {
         char sup = info.getCompiledPalette().get(support.charValue()).charValue();
         int x1 = transform.rotateX(0, 15);
         int z1 = transform.rotateZ(0, 15);
         this.driver.current(x1, highwayGroundLevel - 1, z1);

         for(int y = 0; y < 40 && (this.driver.getBlock() == airChar || this.driver.getBlock() == this.liquidChar); ++y) {
            this.driver.block(sup);
            this.driver.decY();
         }

         int x2 = transform.rotateX(0, 0);
         int z2 = transform.rotateZ(0, 0);
         this.driver.current(x2, highwayGroundLevel - 1, z2);

         for(int y = 0; y < 40 && (this.driver.getBlock() == airChar || this.driver.getBlock() == this.liquidChar); ++y) {
            this.driver.block(sup);
            this.driver.decY();
         }
      }

   }

   private void clearRange(BuildingInfo info, int x, int z, int height1, int height2, boolean dowater) {
      if (dowater) {
         this.driver.setBlockRangeSafe(x, height1, z, info.waterLevel, this.liquidChar);
         this.driver.setBlockRangeSafe(x, info.waterLevel + 1, z, height2, airChar);
      } else {
         this.driver.setBlockRange(x, height1, z, height2, airChar);
      }

   }

   private void generateBridges(BuildingInfo info) {
      if (info.getHighwayXLevel() != 0 && info.getHighwayZLevel() != 0) {
         BuildingPart bt = info.hasXBridge(this.provider);
         if (bt != null) {
            this.generateBridge(info, bt, Orientation.X);
         } else {
            bt = info.hasZBridge(this.provider);
            if (bt != null) {
               this.generateBridge(info, bt, Orientation.Z);
            }
         }

      }
   }

   private void generateBridge(BuildingInfo info, BuildingPart bt, Orientation orientation) {
      CompiledPalette compiledPalette = info.getCompiledPalette();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            this.driver.current(x, this.mainGroundLevel + 1, z);

            for(int l = 0; l < bt.getSliceCount(); ++l) {
               Character c = orientation == Orientation.X ? bt.getPaletteChar(x, l, z) : bt.getPaletteChar(z, l, x);
               Character b = info.getCompiledPalette().get(c.charValue());
               CompiledPalette.Info inf = compiledPalette.getInfo(c);
               if (inf != null) {
                  Map<String, Integer> orientations = inf.getTorchOrientations();
                  if (orientations != null) {
                     if (info.profile.GENERATE_LIGHTING) {
                        info.addTorchTodo(this.driver.getCurrent(), orientations);
                     } else {
                        b = airChar;
                     }
                  }
               }

               this.driver.add(b.charValue());
            }
         }
      }

      Character support = bt.getMetaChar("support");
      if (info.profile.BRIDGE_SUPPORTS && support != null) {
         char sup = compiledPalette.get(support.charValue()).charValue();
         BuildingInfo minDir = orientation.getMinDir().get(info);
         BuildingInfo maxDir = orientation.getMaxDir().get(info);
         if (minDir.hasBridge(this.provider, orientation) != null && maxDir.hasBridge(this.provider, orientation) != null) {
            for(int y = info.waterLevel - 10; y <= info.groundLevel; ++y) {
               this.driver.current(7, y, 7).block(sup);
               this.driver.current(7, y, 8).block(sup);
               this.driver.current(8, y, 7).block(sup);
               this.driver.current(8, y, 8).block(sup);
            }
         }

         if (minDir.hasBridge(this.provider, orientation) == null) {
            if (orientation == Orientation.X) {
               int x = 0;
               this.driver.current(x, this.mainGroundLevel, 6);

               for(int z = 6; z <= 9; ++z) {
                  this.driver.block(sup).incZ();
               }
            } else {
               int z = 0;
               this.driver.current(6, this.mainGroundLevel, z);

               for(int x = 6; x <= 9; ++x) {
                  this.driver.block(sup).incX();
               }
            }
         }

         if (maxDir.hasBridge(this.provider, orientation) == null) {
            if (orientation == Orientation.X) {
               int x = 15;
               this.driver.current(x, this.mainGroundLevel, 6);

               for(int z = 6; z <= 9; ++z) {
                  this.driver.block(sup).incZ();
               }
            } else {
               int z = 15;
               this.driver.current(6, this.mainGroundLevel, z);

               for(int x = 6; x <= 9; ++x) {
                  this.driver.block(sup).incX();
               }
            }
         }
      }

   }

   private int getHeightAt00Corner(BuildingInfo info) {
      int h = this.getHeightForChunk(info);
      h = Math.min(h, this.getHeightForChunk(info.getXmin()));
      h = Math.min(h, this.getHeightForChunk(info.getZmin()));
      h = Math.min(h, this.getHeightForChunk(info.getXmin().getZmin()));
      return h;
   }

   private int getHeightForChunk(BuildingInfo info) {
      if (info.isCity) {
         return info.getCityGroundLevel();
      } else {
         return info.isOcean() ? info.groundLevel - 4 : info.getCityGroundLevel();
      }
   }

   private void flattenChunkToCityBorder(int chunkX, int chunkZ) {
      int cx = chunkX * 16;
      int cz = chunkZ * 16;
      BuildingInfo info = BuildingInfo.getBuildingInfo(chunkX, chunkZ, this.provider);
      float h00 = (float)this.getHeightAt00Corner(info);
      float h10 = (float)this.getHeightAt00Corner(info.getXmax());
      float h01 = (float)this.getHeightAt00Corner(info.getZmax());
      float h11 = (float)this.getHeightAt00Corner(info.getXmax().getZmax());
      List<GeometryTools.AxisAlignedBB2D> boxes = new ArrayList();
      List<GeometryTools.AxisAlignedBB2D> boxesDownwards = new ArrayList();

      for(int x = -1; x <= 1; ++x) {
         for(int z = -1; z <= 1; ++z) {
            if (x != 0 || z != 0) {
               int ccx = chunkX + x;
               int ccz = chunkZ + z;
               BuildingInfo info2 = BuildingInfo.getBuildingInfo(ccx, ccz, this.provider);
               if (info2.isCity) {
                  GeometryTools.AxisAlignedBB2D box = new GeometryTools.AxisAlignedBB2D((double)(ccx * 16), (double)(ccz * 16), (double)(ccx * 16 + 15), (double)(ccz * 16 + 15));
                  boxes.add(box);
               } else if (info2.getMaxHighwayLevel() >= 0 && !info2.isTunnel(info2.getMaxHighwayLevel())) {
                  GeometryTools.AxisAlignedBB2D box = new GeometryTools.AxisAlignedBB2D((double)(ccx * 16), (double)(ccz * 16), (double)(ccx * 16 + 15), (double)(ccz * 16 + 15));
                  box.height = info.groundLevel + info2.getMaxHighwayLevel() * 6;
                  boxesDownwards.add(box);
               }
            }
         }
      }

      if (!boxes.isEmpty()) {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               double mindist = 1.0E9D;
               int height = this.bipolate(h11, h01, h10, h00, x, z);

               for(GeometryTools.AxisAlignedBB2D box : boxes) {
                  double dist = GeometryTools.squaredDistanceBoxPoint(box, cx + x, cz + z);
                  if (dist < mindist) {
                     mindist = dist;
                  }
               }

               int offset = (int)(Math.sqrt(mindist) * 2.0D);
               this.flattenChunkBorder(info, x, offset, z, this.provider.rand, height);
            }
         }
      }

      if (!boxesDownwards.isEmpty()) {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               double mindist = 1.0E9D;
               int minheight = 1000000000;

               for(GeometryTools.AxisAlignedBB2D box : boxesDownwards) {
                  double dist = GeometryTools.squaredDistanceBoxPoint(box, cx + x, cz + z);
                  if (dist < mindist) {
                     mindist = dist;
                  }

                  if (box.height < minheight) {
                     minheight = box.height;
                  }
               }

               int offset = (int)(Math.sqrt(mindist) * 2.0D);
               this.flattenChunkBorderDownwards(info, x, offset, z, this.provider.rand, minheight);
            }
         }
      }

   }

   public static boolean isWaterBiome(LostCityChunkGenerator provider, ChunkCoord coord) {
      BiomeInfo biomeInfo = BiomeInfo.getBiomeInfo(provider, coord);
      Biome[] biomes = biomeInfo.getBiomes();
      return isWaterBiome(biomes[55]) || isWaterBiome(biomes[54]) || isWaterBiome(biomes[56]);
   }

   private static boolean isWaterBiome(Biome biome) {
      return biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN || biome == Biomes.FROZEN_OCEAN || biome == Biomes.RIVER || biome == Biomes.FROZEN_RIVER || biome == Biomes.BEACH || biome == Biomes.COLD_BEACH;
   }

   private void flattenChunkBorder(BuildingInfo info, int x, int offset, int z, Random rand, int level) {
      this.driver.current(x, 0, z);

      for(int y = 0; y <= level - offset - rand.nextInt(2); ++y) {
         char b = this.driver.getBlock();
         if (b != bedrockChar) {
            this.driver.add(this.baseChar);
         } else {
            this.driver.incY();
         }
      }

      int r = rand.nextInt(2);
      this.clearRange(info, x, z, level + offset + r, 230, info.waterLevel > info.groundLevel);
   }

   private void flattenChunkBorderDownwards(BuildingInfo info, int x, int offset, int z, Random rand, int level) {
      int r = rand.nextInt(2);
      this.clearRange(info, x, z, level + offset + r, 230, info.waterLevel > info.groundLevel);
   }

   private void doCityChunk(int chunkX, int chunkZ, BuildingInfo info) {
      boolean building = info.hasBuilding;
      ChunkHeightmap heightmap = this.provider.getHeightmap(info.chunkX, info.chunkZ);
      Random rand = new Random(this.provider.seed * 377L + (long)chunkZ * 341873128712L + (long)chunkX * 132897987541L);
      rand.nextFloat();
      rand.nextFloat();
      if (info.profile.isDefault()) {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               this.driver.setBlockRange(x, 0, z, info.profile.BEDROCK_LAYER, bedrockChar);
            }
         }

         if (info.waterLevel > info.groundLevel) {
            for(int x = 0; x < 16; ++x) {
               for(int z = 0; z < 16; ++z) {
                  this.driver.setBlockRange(x, info.groundLevel, z, info.waterLevel, this.liquidChar);
               }
            }
         }
      }

      LostCityEvent.PreGenCityChunkEvent event = new LostCityEvent.PreGenCityChunkEvent(this.provider.worldObj, this.provider, chunkX, chunkZ, this.driver.getPrimer());
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         if (building) {
            this.generateBuilding(info, heightmap);
         } else {
            this.generateStreet(info, heightmap, rand);
         }
      }

      LostCityEvent.PostGenCityChunkEvent postevent = new LostCityEvent.PostGenCityChunkEvent(this.provider.worldObj, this.provider, chunkX, chunkZ, this.driver.getPrimer());
      MinecraftForge.EVENT_BUS.post(postevent);
      if (info.profile.RUINS) {
         this.generateRuins(info);
      }

      int levelX = info.getHighwayXLevel();
      int levelZ = info.getHighwayZLevel();
      if (!building) {
         Railway.RailChunkInfo railInfo = info.getRailInfo();
         if (levelX < 0 && levelZ < 0 && !railInfo.getType().isSurface()) {
            this.generateStreetDecorations(info);
         }
      }

      if (levelX >= 0 || levelZ >= 0) {
         this.generateHighways(chunkX, chunkZ, info);
      }

      if (info.profile.RUBBLELAYER && (!info.hasBuilding || info.ruinHeight >= 0.0F)) {
         this.generateRubble(chunkX, chunkZ, info);
      }

   }

   private void generateRailwayDungeons(BuildingInfo info) {
      if (info.railDungeon != null) {
         if (info.getZmin().getRailInfo().getType() == RailChunkType.HORIZONTAL || info.getZmax().getRailInfo().getType() == RailChunkType.HORIZONTAL) {
            int height = info.groundLevel + -18;
            this.generatePart(info, info.railDungeon, Transform.ROTATE_NONE, 0, height, 0, false);
         }

      }
   }

   private void generateRailways(BuildingInfo info, Railway.RailChunkInfo railInfo) {
      int height = info.groundLevel + railInfo.getLevel() * 6;
      RailChunkType type = railInfo.getType();
      Transform transform = Transform.ROTATE_NONE;
      boolean needsStaircase = false;
      BuildingPart part;
      switch(type) {
      case NONE:
         return;
      case STATION_SURFACE:
      case STATION_EXTENSION_SURFACE:
         if (railInfo.getLevel() < info.cityLevel) {
            part = AssetRegistries.PARTS.get("station_underground");
         } else if (railInfo.getPart() != null) {
            part = AssetRegistries.PARTS.get(railInfo.getPart());
         } else {
            part = AssetRegistries.PARTS.get("station_open");
         }
         break;
      case STATION_UNDERGROUND:
         part = AssetRegistries.PARTS.get("station_underground_stairs");
         needsStaircase = true;
         break;
      case STATION_EXTENSION_UNDERGROUND:
         part = AssetRegistries.PARTS.get("station_underground");
         break;
      case RAILS_END_HERE:
         part = AssetRegistries.PARTS.get("rails_horizontal_end");
         if (railInfo.getDirection() == Railway.RailDirection.EAST) {
            transform = Transform.MIRROR_X;
         }
         break;
      case HORIZONTAL:
         part = AssetRegistries.PARTS.get("rails_horizontal");
         RailChunkType type1 = info.getXmin().getRailInfo().getType();
         RailChunkType type2 = info.getXmax().getRailInfo().getType();
         if (!type1.isStation() && !type2.isStation() && this.driver.getBlock(3, height + 2, 3) == this.liquidChar && this.driver.getBlock(12, height + 2, 3) == this.liquidChar && this.driver.getBlock(3, height + 2, 12) == this.liquidChar && this.driver.getBlock(12, height + 2, 12) == this.liquidChar && this.driver.getBlock(3, height + 4, 7) == this.liquidChar && this.driver.getBlock(12, height + 4, 8) == this.liquidChar) {
            part = AssetRegistries.PARTS.get("rails_horizontal_water");
         }
         break;
      case VERTICAL:
         part = AssetRegistries.PARTS.get("rails_vertical");
         if (this.driver.getBlock(3, height + 2, 3) == this.liquidChar && this.driver.getBlock(12, height + 2, 3) == this.liquidChar && this.driver.getBlock(3, height + 2, 12) == this.liquidChar && this.driver.getBlock(12, height + 2, 12) == this.liquidChar && this.driver.getBlock(3, height + 4, 7) == this.liquidChar && this.driver.getBlock(12, height + 4, 8) == this.liquidChar) {
            part = AssetRegistries.PARTS.get("rails_vertical_water");
         }

         if (railInfo.getDirection() == Railway.RailDirection.EAST) {
            transform = Transform.MIRROR_X;
         }
         break;
      case THREE_SPLIT:
         part = AssetRegistries.PARTS.get("rails_3split");
         if (railInfo.getDirection() == Railway.RailDirection.EAST) {
            transform = Transform.MIRROR_X;
         }
         break;
      case GOING_DOWN_TWO_FROM_SURFACE:
      case GOING_DOWN_FURTHER:
         part = AssetRegistries.PARTS.get("rails_down2");
         if (railInfo.getDirection() == Railway.RailDirection.EAST) {
            transform = Transform.MIRROR_X;
         }
         break;
      case GOING_DOWN_ONE_FROM_SURFACE:
         part = AssetRegistries.PARTS.get("rails_down1");
         if (railInfo.getDirection() == Railway.RailDirection.EAST) {
            transform = Transform.MIRROR_X;
         }
         break;
      case DOUBLE_BEND:
         part = AssetRegistries.PARTS.get("rails_bend");
         if (railInfo.getDirection() == Railway.RailDirection.EAST) {
            transform = Transform.MIRROR_X;
         }
         break;
      default:
         part = AssetRegistries.PARTS.get("rails_flat");
      }

      this.generatePart(info, part, transform, 0, height, 0, false);
      Character railMainBlock = info.getCityStyle().getRailMainBlock();
      char rail = info.getCompiledPalette().get(railMainBlock.charValue()).charValue();
      if (type == RailChunkType.HORIZONTAL) {
         if (info.getZmin().railDungeon != null) {
            for(int z = 0; z < 4; ++z) {
               this.driver.current(6, height + 1, z).add(rail).add(airChar).add(airChar);
               this.driver.current(7, height + 1, z).add(rail).add(airChar).add(airChar);
            }

            for(int z = 0; z < 3; ++z) {
               this.driver.current(5, height + 2, z).add(rail).add(rail).add(rail);
               this.driver.current(6, height + 4, z).block(rail);
               this.driver.current(7, height + 4, z).block(rail);
               this.driver.current(8, height + 2, z).add(rail).add(rail).add(rail);
            }
         }

         if (info.getZmax().railDungeon != null) {
            for(int z = 0; z < 5; ++z) {
               this.driver.current(6, height + 1, 15 - z).add(rail).add(airChar).add(airChar);
               this.driver.current(7, height + 1, 15 - z).add(rail).add(airChar).add(airChar);
            }

            for(int z = 0; z < 4; ++z) {
               this.driver.current(5, height + 2, 15 - z).add(rail).add(rail).add(rail);
               this.driver.current(6, height + 4, 15 - z).block(rail);
               this.driver.current(7, height + 4, 15 - z).block(rail);
               this.driver.current(8, height + 2, 15 - z).add(rail).add(rail).add(rail);
            }
         }
      }

      if (railInfo.getRails() < 3) {
         label179:
         switch(railInfo.getType()) {
         case NONE:
         case RAILS_END_HERE:
         case VERTICAL:
         case THREE_SPLIT:
         case DOUBLE_BEND:
         default:
            break;
         case STATION_SURFACE:
         case STATION_EXTENSION_SURFACE:
         case STATION_UNDERGROUND:
         case STATION_EXTENSION_UNDERGROUND:
         case HORIZONTAL:
            if (railInfo.getRails() == 1) {
               this.driver.current(0, height + 1, 5);

               for(int x = 0; x < 16; ++x) {
                  this.driver.block(rail).incX();
               }

               this.driver.current(0, height + 1, 9);
               int x = 0;

               while(true) {
                  if (x >= 16) {
                     break label179;
                  }

                  this.driver.block(rail).incX();
                  ++x;
               }
            } else {
               this.driver.current(0, height + 1, 7);
               int x = 0;

               while(true) {
                  if (x >= 16) {
                     break label179;
                  }

                  this.driver.block(rail).incX();
                  ++x;
               }
            }
         case GOING_DOWN_TWO_FROM_SURFACE:
         case GOING_DOWN_FURTHER:
         case GOING_DOWN_ONE_FROM_SURFACE:
            if (railInfo.getRails() == 1) {
               for(int x = 0; x < 16; ++x) {
                  for(int y = height + 1; y < height + part.getSliceCount(); ++y) {
                     this.driver.current(x, y, 5);
                     if (getRailChars().contains(Character.valueOf(this.driver.getBlock()))) {
                        this.driver.block(rail);
                     }

                     this.driver.current(x, y, 9);
                     if (getRailChars().contains(Character.valueOf(this.driver.getBlock()))) {
                        this.driver.block(rail);
                     }
                  }
               }
            } else {
               for(int x = 0; x < 16; ++x) {
                  for(int y = height + 1; y < height + part.getSliceCount(); ++y) {
                     this.driver.current(x, y, 7);
                     if (getRailChars().contains(Character.valueOf(this.driver.getBlock()))) {
                        this.driver.block(rail);
                     }
                  }
               }
            }
         }
      }

      if (needsStaircase) {
         part = AssetRegistries.PARTS.get("station_staircase");

         for(int i = railInfo.getLevel() + 1; i < info.cityLevel; ++i) {
            height = info.groundLevel + i * 6;
            this.generatePart(info, part, transform, 0, height, 0, false);
         }

         height = info.groundLevel + info.cityLevel * 6;
         part = AssetRegistries.PARTS.get("station_staircase_surface");
         this.generatePart(info, part, transform, 0, height, 0, false);
      }

   }

   private void generateStreetDecorations(BuildingInfo info) {
      Direction stairDirection = info.getActualStairDirection();
      if (stairDirection != null) {
         BuildingPart stairs = info.stairType;
         int oy = info.getCityGroundLevel() + 1;
         Transform transform;
         switch(stairDirection) {
         case XMIN:
            transform = Transform.ROTATE_NONE;
            break;
         case XMAX:
            transform = Transform.ROTATE_180;
            break;
         case ZMIN:
            transform = Transform.ROTATE_90;
            break;
         case ZMAX:
            transform = Transform.ROTATE_270;
            break;
         default:
            throw new RuntimeException("Cannot happen!");
         }

         this.generatePart(info, stairs, transform, 0, oy, 0, false);
      }

   }

   private LostCitiesTerrainGenerator.Blob findBlob(List<LostCitiesTerrainGenerator.Blob> blobs, IIndex index) {
      for(LostCitiesTerrainGenerator.Blob blob : blobs) {
         if (blob.contains(index)) {
            return blob;
         }
      }

      return null;
   }

   private void fixAfterExplosionNew(BuildingInfo info, Random rand) {
      int start = info.getDamageArea().getLowestExplosionHeight();
      if (start != -1) {
         int end = info.getDamageArea().getHighestExplosionHeight();
         List<LostCitiesTerrainGenerator.Blob> blobs = new ArrayList();

         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               this.driver.current(x, start, z);

               for(int y = start; y < end; ++y) {
                  char p = this.driver.getBlock();
                  if (p != airChar && p != this.liquidChar) {
                     LostCitiesTerrainGenerator.Blob blob = this.findBlob(blobs, this.driver.getCurrent());
                     if (blob == null) {
                        blob = new LostCitiesTerrainGenerator.Blob(start, end + 6);
                        blob.scan(info, this.driver.copy(), airChar, this.liquidChar, new BlockPos(x, y, z));
                        blobs.add(blob);
                     }
                  }

                  this.driver.incY();
               }
            }
         }

         LostCitiesTerrainGenerator.Blob blocksToMove = new LostCitiesTerrainGenerator.Blob(0, 256);
         blobs.stream().filter((blob) -> {
            return blob.destroyOrMoveThis(this.provider, info);
         }).sorted().forEachOrdered((blob) -> {
            if (rand.nextFloat() >= info.profile.DESTROY_OR_MOVE_CHANCE && blob.connectedBlocks.size() >= info.profile.DESTROY_SMALL_SECTIONS_SIZE && blob.connections >= 5) {
               blocksToMove.connectedBlocks.addAll(blob.connectedBlocks);
            } else {
               for(IIndex index : blob.connectedBlocks) {
                  this.driver.current(index);
                  this.driver.block(this.driver.getY() < info.waterLevel ? this.liquidChar : airChar);
               }
            }

         });

         for(IIndex index : blocksToMove.connectedBlocks) {
            this.driver.current(index);
            char c = this.driver.getBlock();
            this.driver.block(this.driver.getY() < info.waterLevel ? this.liquidChar : airChar);
            this.driver.decY();

            for(int y = this.driver.getY(); y > 2 && (blocksToMove.contains(this.driver.getCurrent()) || this.driver.getBlock() == airChar || this.driver.getBlock() == this.liquidChar); --y) {
               this.driver.decY();
            }

            this.driver.incY();
            this.driver.block(c);
         }

      }
   }

   private void generateRubble(int chunkX, int chunkZ, BuildingInfo info) {
      this.rubbleBuffer = this.rubbleNoise.getRegion(this.rubbleBuffer, (double)(chunkX * 16), (double)(chunkZ * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);
      this.leavesBuffer = this.leavesNoise.getRegion(this.leavesBuffer, (double)(chunkX * 64), (double)(chunkZ * 64), 16, 16, 0.015625D, 0.015625D, 4.0D);

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            double vr = info.profile.RUBBLE_DIRT_SCALE < 0.01F ? 0.0D : this.rubbleBuffer[x + z * 16] / (double)info.profile.RUBBLE_DIRT_SCALE;
            double vl = info.profile.RUBBLE_LEAVE_SCALE < 0.01F ? 0.0D : this.leavesBuffer[x + z * 16] / (double)info.profile.RUBBLE_LEAVE_SCALE;
            if (vr > 0.5D || vl > 0.5D) {
               int height = this.getInterpolatedHeight(info, x, z);
               this.driver.current(x, height, z);
               if (height != 0) {
                  char c = this.driver.getBlockDown();
                  if (c != airChar && c != this.liquidChar) {
                     for(int i = 0; (double)i < vr; ++i) {
                        if (this.driver.getBlock() != airChar && this.driver.getBlock() != this.liquidChar) {
                           this.driver.incY();
                        } else {
                           this.driver.add(this.baseChar);
                        }
                     }
                  }

                  if (this.driver.getBlockDown() == this.baseChar) {
                     for(int i = 0; (double)i < vl; ++i) {
                        if (this.driver.getBlock() != airChar && this.driver.getBlock() != this.liquidChar) {
                           this.driver.incY();
                        } else {
                           this.driver.add(getRandomLeaf());
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private int getInterpolatedHeight(BuildingInfo info, int x, int z) {
      if (x < 8 && z < 8) {
         float h00 = (float)info.getXmin().getZmin().getCityGroundLevelOutsideLower();
         float h10 = (float)info.getZmin().getCityGroundLevelOutsideLower();
         float h01 = (float)info.getXmin().getCityGroundLevelOutsideLower();
         float h11 = (float)info.getCityGroundLevelOutsideLower();
         return this.bipolate(h00, h10, h01, h11, x + 8, z + 8);
      } else if (x >= 8 && z < 8) {
         float h00 = (float)info.getZmin().getCityGroundLevelOutsideLower();
         float h10 = (float)info.getXmax().getZmin().getCityGroundLevelOutsideLower();
         float h01 = (float)info.getCityGroundLevelOutsideLower();
         float h11 = (float)info.getXmax().getCityGroundLevelOutsideLower();
         return this.bipolate(h00, h10, h01, h11, x - 8, z + 8);
      } else if (x < 8 && z >= 8) {
         float h00 = (float)info.getXmin().getCityGroundLevelOutsideLower();
         float h10 = (float)info.getCityGroundLevelOutsideLower();
         float h01 = (float)info.getXmin().getZmax().getCityGroundLevelOutsideLower();
         float h11 = (float)info.getZmax().getCityGroundLevelOutsideLower();
         return this.bipolate(h00, h10, h01, h11, x + 8, z - 8);
      } else {
         float h00 = (float)info.getCityGroundLevelOutsideLower();
         float h10 = (float)info.getXmax().getCityGroundLevelOutsideLower();
         float h01 = (float)info.getZmax().getCityGroundLevelOutsideLower();
         float h11 = (float)info.getXmax().getZmax().getCityGroundLevelOutsideLower();
         return this.bipolate(h00, h10, h01, h11, x - 8, z - 8);
      }
   }

   private int bipolate(float h00, float h10, float h01, float h11, int dx, int dz) {
      float factor = (15.0F - (float)dx) / 15.0F;
      float h0 = h00 + (h10 - h00) * factor;
      float h1 = h01 + (h11 - h01) * factor;
      float h = h0 + (h1 - h0) * (15.0F - (float)dz) / 15.0F;
      return (int)h;
   }

   private void generateRuins(BuildingInfo info) {
      if (info.ruinHeight >= 0.0F) {
         int chunkX = info.chunkX;
         int chunkZ = info.chunkZ;
         double d0 = 0.03125D;
         this.ruinBuffer = this.ruinNoise.getRegion(this.ruinBuffer, (double)(chunkX * 16), (double)(chunkZ * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);
         boolean doLeaves = info.profile.RUBBLELAYER;
         if (doLeaves) {
            this.leavesBuffer = this.leavesNoise.getRegion(this.leavesBuffer, (double)(chunkX * 64), (double)(chunkZ * 64), 16, 16, 0.015625D, 0.015625D, 4.0D);
         }

         int baseheight = (int)((float)(info.getCityGroundLevel() + 1) + info.ruinHeight * (float)info.getNumFloors() * 6.0F);

         for(int x = 0; x < 16; ++x) {
            label80:
            for(int z = 0; z < 16; ++z) {
               double v = this.ruinBuffer[x + z * 16];
               int height = baseheight + (int)v;
               if (height != 0) {
                  this.driver.current(x, height, z);
                  height = info.getMaxHeight() + 10 - height;
                  int vl = 0;
                  if (doLeaves) {
                     vl = (int)(info.profile.RUBBLE_LEAVE_SCALE < 0.01F ? 0.0D : this.leavesBuffer[x + z * 16] / (double)info.profile.RUBBLE_LEAVE_SCALE);
                  }

                  for(; height > 0; --height) {
                     Character damage = info.getCompiledPalette().canBeDamagedToIronBars(Character.valueOf(this.driver.getBlock()));
                     char c = this.driver.getBlockDown();
                     if ((damage != null || c == ironbarsChar) && c != airChar && c != this.liquidChar && this.provider.rand.nextFloat() < 0.2F) {
                        this.driver.add(ironbarsChar);
                     } else if (vl <= 0) {
                        this.driver.add(airChar);
                     } else {
                        for(char var17 = this.driver.getBlockDown(); var17 == airChar || var17 == this.liquidChar; var17 = this.driver.getBlockDown()) {
                           this.driver.decY();
                           if (this.driver.getY() == 0) {
                              continue label80;
                           }

                           ++height;
                        }

                        this.driver.add(getRandomLeaf());
                        --vl;
                     }
                  }
               }
            }
         }

      }
   }

   private void generateStreet(BuildingInfo info, ChunkHeightmap heightmap, Random rand) {
      boolean xRail = info.hasXCorridor();
      boolean zRail = info.hasZCorridor();
      if (xRail || zRail) {
         this.generateCorridors(info, xRail, zRail);
      }

      Railway.RailChunkInfo railInfo = info.getRailInfo();
      boolean canDoParks = info.getHighwayXLevel() != info.cityLevel && info.getHighwayZLevel() != info.cityLevel && railInfo.getType() != RailChunkType.STATION_SURFACE && (railInfo.getType() != RailChunkType.STATION_EXTENSION_SURFACE || railInfo.getLevel() < info.cityLevel);
      if (canDoParks) {
         int height = info.getCityGroundLevel();
         BuildingInfo.StreetType streetType = info.streetType;
         boolean elevated = info.isElevatedParkSection();
         if (elevated) {
            Character elevationBlock = info.getCityStyle().getParkElevationBlock();
            char elevation = info.getCompiledPalette().get(elevationBlock.charValue()).charValue();
            streetType = BuildingInfo.StreetType.PARK;

            for(int x = 0; x < 16; ++x) {
               this.driver.current(x, height, 0);

               for(int z = 0; z < 16; ++z) {
                  this.driver.block(elevation).incZ();
               }
            }

            ++height;
         }

         switch(streetType) {
         case NORMAL:
            this.generateNormalStreetSection(info, height);
            break;
         case FULL:
            this.generateFullStreetSection(height);
            break;
         case PARK:
            this.generateParkSection(info, height, elevated);
         }

         ++height;
         if (streetType == BuildingInfo.StreetType.PARK || info.fountainType != null) {
            BuildingPart part;
            if (streetType == BuildingInfo.StreetType.PARK) {
               part = info.parkType;
            } else {
               part = info.fountainType;
            }

            this.generatePart(info, part, Transform.ROTATE_NONE, 0, height, 0, false);
         }

         this.generateRandomVegetation(info, rand, height);
         this.generateFrontPart(info, height, info.getXmin(), Transform.ROTATE_NONE);
         this.generateFrontPart(info, height, info.getZmin(), Transform.ROTATE_90);
         this.generateFrontPart(info, height, info.getXmax(), Transform.ROTATE_180);
         this.generateFrontPart(info, height, info.getZmax(), Transform.ROTATE_270);
      }

      this.generateBorders(info, canDoParks);
   }

   private void generateBorders(BuildingInfo info, boolean canDoParks) {
      Character borderBlock = info.getCityStyle().getBorderBlock();
      switch(info.profile.LANDSCAPE_TYPE) {
      case DEFAULT:
         this.fillToBedrockStreetBlock(info);
         break;
      case FLOATING:
         this.fillMainStreetBlock(info, borderBlock, 3);
         break;
      case SPACE:
         this.fillToGroundStreetBlock(info, info.getCityGroundLevel());
         break;
      case CAVERN:
         this.fillMainStreetBlock(info, borderBlock, 2);
      }

      if (this.doBorder(info, Direction.XMIN)) {
         int x = 0;

         for(int z = 0; z < 16; ++z) {
            this.generateBorder(info, canDoParks, x, z, Direction.XMIN.get(info));
         }
      }

      if (this.doBorder(info, Direction.XMAX)) {
         int x = 15;

         for(int z = 0; z < 16; ++z) {
            this.generateBorder(info, canDoParks, x, z, Direction.XMAX.get(info));
         }
      }

      if (this.doBorder(info, Direction.ZMIN)) {
         int z = 0;

         for(int x = 0; x < 16; ++x) {
            this.generateBorder(info, canDoParks, x, z, Direction.ZMIN.get(info));
         }
      }

      if (this.doBorder(info, Direction.ZMAX)) {
         int z = 15;

         for(int x = 0; x < 16; ++x) {
            this.generateBorder(info, canDoParks, x, z, Direction.ZMAX.get(info));
         }
      }

   }

   private void fillToBedrockStreetBlock(BuildingInfo info) {
      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            this.driver.setBlockRange(x, info.profile.BEDROCK_LAYER, z, info.getCityGroundLevel(), this.baseChar);
         }
      }

   }

   private void fillToGroundStreetBlock(BuildingInfo info, int lowestLevel) {
      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int y = lowestLevel - 1;
            this.driver.current(x, y, z);

            while(y > 1 && this.driver.getBlock() == airChar) {
               this.driver.block(this.baseChar).decY();
               --y;
            }
         }
      }

   }

   private void fillMainStreetBlock(BuildingInfo info, Character borderBlock, int offset) {
      char border = info.getCompiledPalette().get(borderBlock.charValue()).charValue();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            this.driver.setBlockRange(x, info.getCityGroundLevel() - (offset - 1), z, info.getCityGroundLevel(), this.baseChar);
            this.driver.current(x, info.getCityGroundLevel() - offset, z).block(border);
         }
      }

   }

   private void generateBorder(BuildingInfo info, boolean canDoParks, int x, int z, BuildingInfo adjacent) {
      Character borderBlock = info.getCityStyle().getBorderBlock();
      Character wallBlock = info.getCityStyle().getWallBlock();
      char wall = info.getCompiledPalette().get(wallBlock.charValue()).charValue();
      switch(info.profile.LANDSCAPE_TYPE) {
      case DEFAULT:
         this.setBlocksFromPalette(x, info.groundLevel - 6, z, info.getCityGroundLevel() + 1, info.getCompiledPalette(), borderBlock.charValue());
         break;
      case FLOATING:
         this.setBlocksFromPalette(x, info.getCityGroundLevel() - 3, z, info.getCityGroundLevel() + 1, info.getCompiledPalette(), borderBlock.charValue());
         if (this.isCorner(x, z)) {
            this.generateBorderSupport(info, wall, x, z, 3);
         }
         break;
      case SPACE:
         int adjacentY = info.getCityGroundLevel() - 8;
         if (adjacent.isCity) {
            adjacentY = Math.min(adjacentY, adjacent.getCityGroundLevel());
         } else {
            ChunkHeightmap adjacentHeightmap = this.provider.getHeightmap(adjacent.chunkX, adjacent.chunkZ);
            int minimumHeight = adjacentHeightmap.getMinimumHeight();
            adjacentY = Math.min(adjacentY, minimumHeight - 2);
         }

         if (adjacentY > 5) {
            this.setBlocksFromPalette(x, adjacentY, z, info.getCityGroundLevel() + 1, info.getCompiledPalette(), borderBlock.charValue());
         }
         break;
      case CAVERN:
         this.setBlocksFromPalette(x, info.getCityGroundLevel() - 2, z, info.getCityGroundLevel() + 1, info.getCompiledPalette(), borderBlock.charValue());
         if (this.isCorner(x, z)) {
            this.generateBorderSupport(info, wall, x, z, 2);
         }
      }

      if (canDoParks) {
         if (!this.borderNeedsConnectionToAdjacentChunk(info, x, z)) {
            this.driver.current(x, info.getCityGroundLevel() + 1, z).block(wall);
         } else {
            this.driver.current(x, info.getCityGroundLevel() + 1, z).block(airChar);
         }
      }

   }

   private void generateBorderSupport(BuildingInfo info, char wall, int x, int z, int offset) {
      ChunkHeightmap heightmap = this.provider.getHeightmap(info.chunkX, info.chunkZ);
      int height = heightmap.getHeight(x, z);
      if (height > 1) {
         int y = info.getCityGroundLevel() - offset - 1;
         this.driver.current(x, y, z);

         while(y > 1 && this.driver.getBlock() == airChar) {
            this.driver.block(wall).decY();
            --y;
         }

         while(y > 1 && this.driver.getBlock() == this.liquidChar) {
            this.driver.block(this.baseChar).decY();
            --y;
         }
      }

   }

   private void generateFrontPart(BuildingInfo info, int height, BuildingInfo adj, Transform rot) {
      if (info.hasFrontPartFrom(adj)) {
         this.generatePart(adj, adj.frontType, rot, 0, height, 0, false);
      }

   }

   private void generateCorridors(BuildingInfo info, boolean xRail, boolean zRail) {
      IBlockState railx = Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST);
      char railxC = (char)Block.BLOCK_STATE_IDS.get(railx);
      IBlockState railz = Blocks.RAIL.getDefaultState();
      char railzC = (char)Block.BLOCK_STATE_IDS.get(railz);
      Character corridorRoofBlock = info.getCityStyle().getCorridorRoofBlock();
      Character corridorGlassBlock = info.getCityStyle().getCorridorGlassBlock();
      CompiledPalette palette = info.getCompiledPalette();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            if (xRail && z >= 7 && z <= 10 || zRail && x >= 7 && x <= 10) {
               int height = info.groundLevel - 5;
               char b;
               if (xRail && z == 10) {
                  b = railxC;
               } else if (zRail && x == 10) {
                  b = railzC;
               } else {
                  b = airChar;
               }

               this.driver.current(x, height, z).add(b).add(airChar).add(airChar);
               if ((!xRail || x != 7 || z != 8 && z != 9) && (!zRail || z != 7 || x != 8 && x != 9)) {
                  char roof = palette.get(corridorRoofBlock.charValue()).charValue();
                  this.driver.add(roof).add(roof);
               } else {
                  char glass = palette.get(corridorGlassBlock.charValue()).charValue();
                  info.addLightingUpdateTodo(new BlockPos(x, height, z));
                  this.driver.add(glass).add(glowstoneChar);
               }
            } else {
               this.driver.setBlockRange(x, info.groundLevel - 5, z, info.getCityGroundLevel(), this.baseChar);
            }
         }
      }

   }

   private void generateRandomVegetation(BuildingInfo info, Random rand, int height) {
      if (height != 0) {
         if (info.getXmin().hasBuilding) {
            for(int x = 0; x < info.profile.THICKNESS_OF_RANDOM_LEAFBLOCKS; ++x) {
               label165:
               for(int z = 0; z < 16; ++z) {
                  this.driver.current(x, height, z);

                  while(true) {
                     if (this.driver.getBlockDown() != airChar) {
                        float v = Math.min(0.8F, info.profile.CHANCE_OF_RANDOM_LEAFBLOCKS * (float)(info.profile.THICKNESS_OF_RANDOM_LEAFBLOCKS + 1 - x));
                        int cnt = 0;

                        while(true) {
                           if (rand.nextFloat() >= v || cnt >= 30) {
                              continue label165;
                           }

                           this.driver.add(getRandomLeaf());
                           ++cnt;
                        }
                     }

                     this.driver.decY();
                     if (this.driver.getY() == 0) {
                        break;
                     }
                  }
               }
            }
         }

         if (info.getXmax().hasBuilding) {
            for(int x = 15 - info.profile.THICKNESS_OF_RANDOM_LEAFBLOCKS; x < 15; ++x) {
               label139:
               for(int z = 0; z < 16; ++z) {
                  this.driver.current(x, height, z);

                  while(true) {
                     if (this.driver.getBlockDown() != airChar) {
                        float v = Math.min(0.8F, info.profile.CHANCE_OF_RANDOM_LEAFBLOCKS * (float)(x - 14 + info.profile.THICKNESS_OF_RANDOM_LEAFBLOCKS));
                        int cnt = 0;

                        while(true) {
                           if (rand.nextFloat() >= v || cnt >= 30) {
                              continue label139;
                           }

                           this.driver.add(getRandomLeaf());
                           ++cnt;
                        }
                     }

                     this.driver.decY();
                     if (this.driver.getY() == 0) {
                        break;
                     }
                  }
               }
            }
         }

         if (info.getZmin().hasBuilding) {
            for(int z = 0; z < info.profile.THICKNESS_OF_RANDOM_LEAFBLOCKS; ++z) {
               label113:
               for(int x = 0; x < 16; ++x) {
                  this.driver.current(x, height, z);

                  while(true) {
                     if (this.driver.getBlockDown() != airChar) {
                        float v = Math.min(0.8F, info.profile.CHANCE_OF_RANDOM_LEAFBLOCKS * (float)(info.profile.THICKNESS_OF_RANDOM_LEAFBLOCKS + 1 - z));
                        int cnt = 0;

                        while(true) {
                           if (rand.nextFloat() >= v || cnt >= 30) {
                              continue label113;
                           }

                           this.driver.add(getRandomLeaf());
                           ++cnt;
                        }
                     }

                     this.driver.decY();
                     if (this.driver.getY() == 0) {
                        break;
                     }
                  }
               }
            }
         }

         if (info.getZmax().hasBuilding) {
            for(int z = 15 - info.profile.THICKNESS_OF_RANDOM_LEAFBLOCKS; z < 15; ++z) {
               label87:
               for(int x = 0; x < 16; ++x) {
                  this.driver.current(x, height, z);

                  while(true) {
                     if (this.driver.getBlockDown() != airChar) {
                        float v = info.profile.CHANCE_OF_RANDOM_LEAFBLOCKS * (float)(z - 14 + info.profile.THICKNESS_OF_RANDOM_LEAFBLOCKS);
                        int cnt = 0;

                        while(true) {
                           if (rand.nextFloat() >= v || cnt >= 30) {
                              continue label87;
                           }

                           this.driver.add(getRandomLeaf());
                           ++cnt;
                        }
                     }

                     this.driver.decY();
                     if (this.driver.getY() == 0) {
                        break;
                     }
                  }
               }
            }
         }

      }
   }

   private void generateParkSection(BuildingInfo info, int height, boolean elevated) {
      boolean el00 = info.getXmin().getZmin().isElevatedParkSection();
      boolean el10 = info.getZmin().isElevatedParkSection();
      boolean el20 = info.getXmax().getZmin().isElevatedParkSection();
      boolean el01 = info.getXmin().isElevatedParkSection();
      boolean el21 = info.getXmax().isElevatedParkSection();
      boolean el02 = info.getXmin().getZmax().isElevatedParkSection();
      boolean el12 = info.getZmax().isElevatedParkSection();
      boolean el22 = info.getXmax().getZmax().isElevatedParkSection();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            char b;
            if (x != 0 && x != 15 && z != 0 && z != 15) {
               b = grassChar;
            } else {
               b = this.street.charValue();
               if (elevated) {
                  if (x == 0 && z == 0) {
                     if (el01 && el00 && el10) {
                        b = grassChar;
                     }
                  } else if (x == 15 && z == 0) {
                     if (el21 && el20 && el10) {
                        b = grassChar;
                     }
                  } else if (x == 0 && z == 15) {
                     if (el01 && el02 && el12) {
                        b = grassChar;
                     }
                  } else if (x == 15 && z == 15) {
                     if (el12 && el22 && el21) {
                        b = grassChar;
                     }
                  } else if (x == 0) {
                     if (el01) {
                        b = grassChar;
                     }
                  } else if (x == 15) {
                     if (el21) {
                        b = grassChar;
                     }
                  } else if (z == 0) {
                     if (el10) {
                        b = grassChar;
                     }
                  } else if (z == 15 && el12) {
                     b = grassChar;
                  }
               }
            }

            this.driver.current(x, height, z).block(b);
         }
      }

   }

   private void generateFullStreetSection(int height) {
      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            char b;
            if (this.isSide(x, z)) {
               b = this.street.charValue();
            } else {
               b = this.street2.charValue();
            }

            this.driver.current(x, height, z).block(b);
         }
      }

   }

   private void generateNormalStreetSection(BuildingInfo info, int height) {
      char defaultStreet = this.streetBase.charValue();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            char b = defaultStreet;
            if (!this.isStreetBorder(x, z)) {
               b = this.street.charValue();
            } else if (x > this.streetBorder || z <= this.streetBorder || z >= 15 - this.streetBorder || !BuildingInfo.hasRoadConnection(info, info.getXmin()) && info.getXmin().hasXBridge(this.provider) == null) {
               if (x < 15 - this.streetBorder || z <= this.streetBorder || z >= 15 - this.streetBorder || !BuildingInfo.hasRoadConnection(info, info.getXmax()) && info.getXmax().hasXBridge(this.provider) == null) {
                  if (z > this.streetBorder || x <= this.streetBorder || x >= 15 - this.streetBorder || !BuildingInfo.hasRoadConnection(info, info.getZmin()) && info.getZmin().hasZBridge(this.provider) == null) {
                     if (z >= 15 - this.streetBorder && x > this.streetBorder && x < 15 - this.streetBorder && (BuildingInfo.hasRoadConnection(info, info.getZmax()) || info.getZmax().hasZBridge(this.provider) != null)) {
                        b = this.street.charValue();
                     }
                  } else {
                     b = this.street.charValue();
                  }
               } else {
                  b = this.street.charValue();
               }
            } else {
               b = this.street.charValue();
            }

            this.driver.current(x, height, z).block(b);
         }
      }

   }

   private boolean borderNeedsConnectionToAdjacentChunk(BuildingInfo info, int x, int z) {
      for(Direction direction : Direction.VALUES) {
         if (direction.atSide(x, z)) {
            BuildingInfo adjacent = direction.get(info);
            if (adjacent.getActualStairDirection() == direction.getOpposite()) {
               BuildingPart stairType = adjacent.stairType;
               Integer z1 = stairType.getMetaInteger("z1");
               Integer z2 = stairType.getMetaInteger("z2");
               Transform transform = direction.getOpposite().getRotation();
               int xx1 = transform.rotateX(15, z1.intValue());
               int zz1 = transform.rotateZ(15, z1.intValue());
               int xx2 = transform.rotateX(15, z2.intValue());
               int zz2 = transform.rotateZ(15, z2.intValue());
               if (x >= Math.min(xx1, xx2) && x <= Math.max(xx1, xx2) && z >= Math.min(zz1, zz2) && z <= Math.max(zz1, zz2)) {
                  return true;
               }
            }

            if (adjacent.hasBridge(this.provider, direction.getOrientation()) != null) {
               return true;
            }
         }
      }

      return false;
   }

   private int generatePart(BuildingInfo info, IBuildingPart part, Transform transform, int ox, int oy, int oz, boolean airWaterLevel) {
      CompiledPalette compiledPalette = info.getCompiledPalette();
      Palette localPalette = part.getLocalPalette();
      if (localPalette != null) {
         compiledPalette = new CompiledPalette(compiledPalette, new Palette[]{localPalette});
      }

      boolean nowater = part.getMetaBoolean("nowater");

      for(int x = 0; x < part.getXSize(); ++x) {
         for(int z = 0; z < part.getZSize(); ++z) {
            char[] vs = part.getVSlice(x, z);
            if (vs != null) {
               int rx = ox + transform.rotateX(x, z);
               int rz = oz + transform.rotateZ(x, z);
               this.driver.current(rx, oy, rz);
               int len = vs.length;

               for(int y = 0; y < len; ++y) {
                  char c = vs[y];
                  Character b = compiledPalette.get(c);
                  if (b == null) {
                     throw new RuntimeException("Could not find entry '" + c + "' in the palette for part '" + part.getName() + "'!");
                  }

                  CompiledPalette.Info inf = compiledPalette.getInfo(Character.valueOf(c));
                  if (transform != Transform.ROTATE_NONE) {
                     if (getRotatableChars().contains(b)) {
                        IBlockState bs = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(b.charValue());
                        bs = bs.withRotation(transform.getMcRotation());
                        b = (char)Block.BLOCK_STATE_IDS.get(bs);
                     } else if (getRailChars().contains(b)) {
                        IBlockState bs = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(b.charValue());
                        PropertyEnum<EnumRailDirection> shapeProperty;
                        if (bs.getBlock() == Blocks.RAIL) {
                           shapeProperty = BlockRail.SHAPE;
                        } else {
                           if (bs.getBlock() != Blocks.GOLDEN_RAIL) {
                              throw new RuntimeException("Error with rail!");
                           }

                           shapeProperty = BlockRailPowered.SHAPE;
                        }

                        EnumRailDirection shape = (EnumRailDirection)bs.getValue(shapeProperty);
                        bs = bs.withProperty(shapeProperty, transform.transform(shape));
                        b = (char)Block.BLOCK_STATE_IDS.get(bs);
                     }
                  }

                  if (b.charValue() == airChar) {
                     this.driver.incY();
                  } else {
                     if (b.charValue() == this.liquidChar) {
                        if (info.profile.AVOID_WATER) {
                           b = airChar;
                        }
                     } else if (b.charValue() == hardAirChar) {
                        if (airWaterLevel && !info.profile.AVOID_WATER && !nowater) {
                           b = oy + y < info.waterLevel ? this.liquidChar : airChar;
                        } else {
                           b = airChar;
                        }
                     } else if (inf != null) {
                        Map<String, Integer> orientations = inf.getTorchOrientations();
                        if (orientations != null) {
                           if (info.profile.GENERATE_LIGHTING) {
                              info.addTorchTodo(this.driver.getCurrent(), orientations);
                           } else {
                              b = airChar;
                           }
                        } else if (inf.getLoot() != null && !inf.getLoot().isEmpty()) {
                           if (!info.noLoot) {
                              info.getTodoChunk(rx, rz).addLootTodo(new BlockPos(info.chunkX * 16 + rx, oy + y, info.chunkZ * 16 + rz), new BuildingInfo.ConditionTodo(inf.getLoot(), part.getName(), info));
                           }
                        } else if (inf.getMobId() != null && !inf.getMobId().isEmpty()) {
                           if (info.profile.GENERATE_SPAWNERS && !info.noLoot) {
                              String mobid = inf.getMobId();
                              info.getTodoChunk(rx, rz).addSpawnerTodo(new BlockPos(info.chunkX * 16 + rx, oy + y, info.chunkZ * 16 + rz), new BuildingInfo.ConditionTodo(mobid, part.getName(), info));
                           } else {
                              b = airChar;
                           }
                        }
                     } else if (getCharactersNeedingLightingUpdate().contains(b)) {
                        info.getTodoChunk(rx, rz).addLightingUpdateTodo(new BlockPos(info.chunkX * 16 + rx, oy + y, info.chunkZ * 16 + rz));
                     } else if (getCharactersNeedingTodo().contains(b)) {
                        IBlockState bs = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(b.charValue());
                        Block block = bs.getBlock();
                        if (block instanceof BlockSapling || block instanceof BlockFlower) {
                           if (info.profile.AVOID_FOLIAGE) {
                              b = airChar;
                           } else {
                              info.getTodoChunk(rx, rz).addSaplingTodo(new BlockPos(info.chunkX * 16 + rx, oy + y, info.chunkZ * 16 + rz));
                           }
                        }
                     }

                     this.driver.add(b.charValue());
                  }
               }
            }
         }
      }

      return oy + part.getSliceCount();
   }

   private void generateDebris(Random rand, BuildingInfo info) {
      this.generateDebrisFromChunk(rand, info, info.getXmin(), (xx, zz) -> {
         return (15.0F - (float)xx.intValue()) / 16.0F;
      });
      this.generateDebrisFromChunk(rand, info, info.getXmax(), (xx, zz) -> {
         return (float)xx.intValue() / 16.0F;
      });
      this.generateDebrisFromChunk(rand, info, info.getZmin(), (xx, zz) -> {
         return (15.0F - (float)zz.intValue()) / 16.0F;
      });
      this.generateDebrisFromChunk(rand, info, info.getZmax(), (xx, zz) -> {
         return (float)zz.intValue() / 16.0F;
      });
      this.generateDebrisFromChunk(rand, info, info.getXmin().getZmin(), (xx, zz) -> {
         return (15.0F - (float)xx.intValue()) * (15.0F - (float)zz.intValue()) / 256.0F;
      });
      this.generateDebrisFromChunk(rand, info, info.getXmax().getZmax(), (xx, zz) -> {
         return (float)(xx.intValue() * zz.intValue()) / 256.0F;
      });
      this.generateDebrisFromChunk(rand, info, info.getXmin().getZmax(), (xx, zz) -> {
         return (15.0F - (float)xx.intValue()) * (float)zz.intValue() / 256.0F;
      });
      this.generateDebrisFromChunk(rand, info, info.getXmax().getZmin(), (xx, zz) -> {
         return (float)xx.intValue() * (15.0F - (float)zz.intValue()) / 256.0F;
      });
   }

   private void generateDebrisFromChunk(Random rand, BuildingInfo info, BuildingInfo adjacentInfo, BiFunction<Integer, Integer, Float> locationFactor) {
      if (adjacentInfo.hasBuilding) {
         char filler = adjacentInfo.getCompiledPalette().get(adjacentInfo.getBuilding().getFillerBlock()).charValue();
         float damageFactor = adjacentInfo.getDamageArea().getDamageFactor();
         if (damageFactor > 0.5F) {
            int blocks = (1 + adjacentInfo.getNumFloors()) * 1000;
            float damage = Math.max(1.0F, damageFactor * 0.7F);
            int destroyedBlocks = (int)((float)blocks * damage);
            destroyedBlocks = destroyedBlocks / info.profile.DEBRIS_TO_NEARBYCHUNK_FACTOR;
            int h = adjacentInfo.getMaxHeight() + 10;

            for(int i = 0; i < destroyedBlocks; ++i) {
               int x = rand.nextInt(16);
               int z = rand.nextInt(16);
               if (rand.nextFloat() < ((Float)locationFactor.apply(Integer.valueOf(x), Integer.valueOf(z))).floatValue()) {
                  this.driver.current(x, h, z);

                  while(h > 0 && (this.driver.getBlock() == airChar || this.driver.getBlock() == this.liquidChar)) {
                     --h;
                     this.driver.decY();
                  }

                  char b;
                  switch(rand.nextInt(5)) {
                  case 0:
                     b = ironbarsChar;
                     break;
                  default:
                     b = filler;
                  }

                  this.driver.current(x, h + 1, z).block(b);
               }
            }
         }
      }

   }

   private boolean doBorder(BuildingInfo info, Direction direction) {
      BuildingInfo adjacent = direction.get(info);
      if (this.isHigherThenNearbyStreetChunk(info, adjacent)) {
         return true;
      } else {
         if (!adjacent.isCity) {
            if (adjacent.cityLevel <= info.cityLevel) {
               return true;
            }

            if (info.profile.isSpace()) {
               ChunkHeightmap adjacentHeightmap = this.provider.getHeightmap(adjacent.chunkX, adjacent.chunkZ);
               int adjacentHeight = adjacentHeightmap.getAverageHeight();
               if (adjacentHeight > 5 && adjacentHeight - 4 < info.getCityGroundLevel()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean isHigherThenNearbyStreetChunk(BuildingInfo info, BuildingInfo adjacent) {
      if (!adjacent.isCity) {
         return false;
      } else if (adjacent.hasBuilding) {
         return adjacent.cityLevel + adjacent.getNumFloors() < info.cityLevel;
      } else {
         return adjacent.cityLevel < info.cityLevel;
      }
   }

   private void setBlocksFromPalette(int x, int y, int z, int y2, CompiledPalette palette, char character) {
      if (palette.isSimple(character)) {
         char b = palette.get(character).charValue();
         this.driver.setBlockRangeSafe(x, y, z, y2, b);
      } else {
         this.driver.current(x, y, z);

         while(y < y2) {
            this.driver.add(palette.get(character).charValue());
            ++y;
         }
      }

   }

   private void generateBuilding(BuildingInfo info, ChunkHeightmap heightmap) {
      int lowestLevel = info.getCityGroundLevel() - info.floorsBelowGround * 6;
      Character borderBlock = info.getCityStyle().getBorderBlock();
      CompiledPalette palette = info.getCompiledPalette();
      char fillerBlock = info.getBuilding().getFillerBlock();
      if (info.profile.isFloating()) {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               int index = x << 12 | z << 8;
               int height = heightmap.getHeight(x, z);
               if (height > 1 && height < lowestLevel - 1) {
                  this.driver.setBlockRange(x, height + 1, z, lowestLevel, this.baseChar);
               }

               this.clearRange(info, x, z, lowestLevel, info.getCityGroundLevel() + info.getNumFloors() * 6, info.waterLevel > info.groundLevel);
            }
         }
      } else if (info.profile.isSpace()) {
         this.fillToGround(info, lowestLevel, borderBlock);

         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               this.clearRange(info, x, z, lowestLevel, info.getCityGroundLevel() + info.getNumFloors() * 6, false);
            }
         }
      } else {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               if (this.isSide(x, z)) {
                  this.driver.setBlockRange(x, info.profile.BEDROCK_LAYER, z, lowestLevel - 10, this.baseChar);
                  int y = lowestLevel - 10;
                  this.driver.current(x, y, z);

                  while(y < lowestLevel) {
                     this.driver.add(palette.get(borderBlock.charValue()).charValue());
                     ++y;
                  }
               } else if (info.profile.isDefault()) {
                  this.driver.setBlockRange(x, info.profile.BEDROCK_LAYER, z, lowestLevel, this.baseChar);
               }

               if (this.driver.getBlock(x, lowestLevel, z) == airChar) {
                  char filler = palette.get(fillerBlock).charValue();
                  this.driver.current(x, lowestLevel, z).block(filler);
               }

               if (info.profile.isCavern()) {
                  this.clearRange(info, x, z, lowestLevel, info.getCityGroundLevel() + info.getNumFloors() * 6, info.waterLevel > info.groundLevel);
               }
            }
         }
      }

      int height = lowestLevel;

      for(int f = -info.floorsBelowGround; f <= info.getNumFloors(); ++f) {
         BuildingPart part = info.getFloor(f);
         this.generatePart(info, part, Transform.ROTATE_NONE, 0, height, 0, false);
         part = info.getFloorPart2(f);
         if (part != null) {
            this.generatePart(info, part, Transform.ROTATE_NONE, 0, height, 0, false);
         }

         boolean isTop = f == info.getNumFloors();
         if (!isTop) {
            this.generateDoors(info, height + 1, f);
         }

         height += 6;
      }

      if (info.floorsBelowGround > 0) {
         for(int x = 0; x < 16; ++x) {
            this.setBlocksFromPalette(x, lowestLevel, 0, Math.min(info.getCityGroundLevel(), info.getZmin().getCityGroundLevel()) + 1, palette, fillerBlock);
            this.setBlocksFromPalette(x, lowestLevel, 15, Math.min(info.getCityGroundLevel(), info.getZmax().getCityGroundLevel()) + 1, palette, fillerBlock);
         }

         for(int z = 1; z < 15; ++z) {
            this.setBlocksFromPalette(0, lowestLevel, z, Math.min(info.getCityGroundLevel(), info.getXmin().getCityGroundLevel()) + 1, palette, fillerBlock);
            this.setBlocksFromPalette(15, lowestLevel, z, Math.min(info.getCityGroundLevel(), info.getXmax().getCityGroundLevel()) + 1, palette, fillerBlock);
         }
      }

      if (info.floorsBelowGround >= 1) {
         this.generateCorridorConnections(info);
      }

   }

   private void fillToGround(BuildingInfo info, int lowestLevel, Character borderBlock) {
      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int y = lowestLevel - 1;
            this.driver.current(x, y, z);
            if (this.isSide(x, z)) {
               while(y > 1 && this.driver.getBlock() == airChar) {
                  this.driver.block(info.getCompiledPalette().get(borderBlock.charValue()).charValue()).decY();
               }
            } else {
               while(y > 1 && this.driver.getBlock() == airChar) {
                  this.driver.block(this.baseChar).decY();
               }
            }
         }
      }

   }

   private char getDoor(Block door, boolean upper, boolean left, EnumFacing facing) {
      IBlockState bs = door.getDefaultState().withProperty(BlockDoor.HALF, upper ? EnumDoorHalf.UPPER : EnumDoorHalf.LOWER).withProperty(BlockDoor.HINGE, left ? EnumHingePosition.LEFT : EnumHingePosition.RIGHT).withProperty(BlockDoor.FACING, facing);
      return (char)Block.BLOCK_STATE_IDS.get(bs);
   }

   private void generateDoors(BuildingInfo info, int height, int f) {
      char filler = info.getCompiledPalette().get(info.getBuilding().getFillerBlock()).charValue();
      --height;
      if (info.hasConnectionAtX(f + info.floorsBelowGround)) {
         int x = 0;
         if (this.hasConnectionWithBuilding(f, info, info.getXmin())) {
            this.driver.setBlockRange(x, height, 6, height + 4, filler);
            this.driver.setBlockRange(x, height, 9, height + 4, filler);
            this.driver.current(x, height, 7).add(filler).add(airChar).add(airChar).add(filler);
            this.driver.current(x, height, 8).add(filler).add(airChar).add(airChar).add(filler);
         } else if (this.hasConnectionToTopOrOutside(f, info, info.getXmin())) {
            this.driver.setBlockRange(x, height, 6, height + 4, filler);
            this.driver.setBlockRange(x, height, 9, height + 4, filler);
            this.driver.current(x, height, 7).add(filler).add(this.getDoor(info.doorBlock, false, true, EnumFacing.EAST)).add(this.getDoor(info.doorBlock, true, true, EnumFacing.EAST)).add(filler);
            this.driver.current(x, height, 8).add(filler).add(this.getDoor(info.doorBlock, false, false, EnumFacing.EAST)).add(this.getDoor(info.doorBlock, true, false, EnumFacing.EAST)).add(filler);
         }
      }

      if (this.hasConnectionWithBuildingMax(f, info, info.getXmax(), Orientation.X)) {
         int x = 15;
         this.driver.setBlockRange(x, height, 6, height + 4, filler);
         this.driver.setBlockRange(x, height, 9, height + 4, filler);
         this.driver.current(x, height, 7).add(filler).add(airChar).add(airChar).add(filler);
         this.driver.current(x, height, 8).add(filler).add(airChar).add(airChar).add(filler);
      } else if (this.hasConnectionToTopOrOutside(f, info, info.getXmax()) && info.getXmax().hasConnectionAtXFromStreet(f + info.getXmax().floorsBelowGround)) {
         int x = 15;
         this.driver.setBlockRange(x, height, 6, height + 4, filler);
         this.driver.setBlockRange(x, height, 9, height + 4, filler);
         this.driver.current(x, height, 7).add(filler).add(this.getDoor(info.doorBlock, false, false, EnumFacing.WEST)).add(this.getDoor(info.doorBlock, true, false, EnumFacing.WEST)).add(filler);
         this.driver.current(x, height, 8).add(filler).add(this.getDoor(info.doorBlock, false, true, EnumFacing.WEST)).add(this.getDoor(info.doorBlock, true, true, EnumFacing.WEST)).add(filler);
      }

      if (info.hasConnectionAtZ(f + info.floorsBelowGround)) {
         int z = 0;
         if (this.hasConnectionWithBuilding(f, info, info.getZmin())) {
            this.driver.setBlockRange(6, height, z, height + 4, filler);
            this.driver.setBlockRange(9, height, z, height + 4, filler);
            this.driver.current(7, height, z).add(filler).add(airChar).add(airChar).add(filler);
            this.driver.current(8, height, z).add(filler).add(airChar).add(airChar).add(filler);
         } else if (this.hasConnectionToTopOrOutside(f, info, info.getZmin())) {
            this.driver.setBlockRange(6, height, z, height + 4, filler);
            this.driver.setBlockRange(9, height, z, height + 4, filler);
            this.driver.current(7, height, z).add(filler).add(this.getDoor(info.doorBlock, false, true, EnumFacing.NORTH)).add(this.getDoor(info.doorBlock, true, true, EnumFacing.NORTH)).add(filler);
            this.driver.current(8, height, z).add(filler).add(this.getDoor(info.doorBlock, false, false, EnumFacing.NORTH)).add(this.getDoor(info.doorBlock, true, false, EnumFacing.NORTH)).add(filler);
         }
      }

      if (this.hasConnectionWithBuildingMax(f, info, info.getZmax(), Orientation.Z)) {
         int z = 15;
         this.driver.setBlockRange(6, height, z, height + 4, filler);
         this.driver.setBlockRange(9, height, z, height + 4, filler);
         this.driver.current(7, height, z).add(filler).add(airChar).add(airChar).add(filler);
         this.driver.current(8, height, z).add(filler).add(airChar).add(airChar).add(filler);
      } else if (this.hasConnectionToTopOrOutside(f, info, info.getZmax()) && info.getZmax().hasConnectionAtZFromStreet(f + info.getZmax().floorsBelowGround)) {
         int z = 15;
         this.driver.setBlockRange(6, height, z, height + 4, filler);
         this.driver.setBlockRange(9, height, z, height + 4, filler);
         this.driver.current(7, height, z).add(filler).add(this.getDoor(info.doorBlock, false, false, EnumFacing.SOUTH)).add(this.getDoor(info.doorBlock, true, false, EnumFacing.SOUTH)).add(filler);
         this.driver.current(8, height, z).add(filler).add(this.getDoor(info.doorBlock, false, true, EnumFacing.SOUTH)).add(this.getDoor(info.doorBlock, true, true, EnumFacing.SOUTH)).add(filler);
      }

   }

   private void generateCorridorConnections(BuildingInfo info) {
      if (info.getXmin().hasXCorridor()) {
         int x = 0;

         for(int z = 7; z <= 10; ++z) {
            this.driver.setBlockRange(x, info.groundLevel - 5, z, info.groundLevel - 2, airChar);
         }
      }

      if (info.getXmax().hasXCorridor()) {
         int x = 15;

         for(int z = 7; z <= 10; ++z) {
            this.driver.setBlockRange(x, info.groundLevel - 5, z, info.groundLevel - 2, airChar);
         }
      }

      if (info.getZmin().hasXCorridor()) {
         int z = 0;

         for(int x = 7; x <= 10; ++x) {
            this.driver.setBlockRange(x, info.groundLevel - 5, z, info.groundLevel - 2, airChar);
         }
      }

      if (info.getZmax().hasXCorridor()) {
         int z = 15;

         for(int x = 7; x <= 10; ++x) {
            this.driver.setBlockRange(x, info.groundLevel - 5, z, info.groundLevel - 2, airChar);
         }
      }

   }

   private boolean hasConnectionWithBuildingMax(int localLevel, BuildingInfo info, BuildingInfo info2, Orientation x) {
      if (info.isValidFloor(localLevel) && info.getFloor(localLevel).getMetaBoolean("dontconnect")) {
         return false;
      } else {
         int globalLevel = info.localToGlobal(localLevel);
         int localAdjacent = info2.globalToLocal(globalLevel);
         if (info2.isValidFloor(localAdjacent) && info2.getFloor(localAdjacent).getMetaBoolean("dontconnect")) {
            return false;
         } else {
            int level = localAdjacent + info2.floorsBelowGround;
            return info2.hasBuilding && (localAdjacent >= 0 && localAdjacent < info2.getNumFloors() || localAdjacent < 0 && -localAdjacent <= info2.floorsBelowGround) && info2.hasConnectionAt(level, x);
         }
      }
   }

   private boolean hasConnectionToTopOrOutside(int localLevel, BuildingInfo info, BuildingInfo info2) {
      int globalLevel = info.localToGlobal(localLevel);
      int localAdjacent = info2.globalToLocal(globalLevel);
      if (info.getFloor(localLevel).getMetaBoolean("dontconnect")) {
         return false;
      } else {
         return info2.isCity && !info2.hasBuilding && localLevel == 0 && localAdjacent == 0 || info2.hasBuilding && localAdjacent == info2.getNumFloors();
      }
   }

   private boolean hasConnectionWithBuilding(int localLevel, BuildingInfo info, BuildingInfo info2) {
      int globalLevel = info.localToGlobal(localLevel);
      int localAdjacent = info2.globalToLocal(globalLevel);
      return info2.hasBuilding && (localAdjacent >= 0 && localAdjacent < info2.getNumFloors() || localAdjacent < 0 && -localAdjacent <= info2.floorsBelowGround);
   }

   private boolean isSide(int x, int z) {
      return x == 0 || x == 15 || z == 0 || z == 15;
   }

   private boolean isCorner(int x, int z) {
      return (x == 0 || x == 15) && (z == 0 || z == 15);
   }

   private boolean isStreetBorder(int x, int z) {
      return x <= this.streetBorder || x >= 15 - this.streetBorder || z <= this.streetBorder || z >= 15 - this.streetBorder;
   }

   private static class Blob implements Comparable<LostCitiesTerrainGenerator.Blob> {
      private final int starty;
      private final int endy;
      private final Set<IIndex> connectedBlocks = new HashSet();
      private final Map<Integer, Integer> blocksPerY = new HashMap();
      private int connections = 0;
      private int lowestY;
      private int highestY;
      private float avgdamage;
      private int cntMindamage;

      public Blob(int starty, int endy) {
         this.starty = starty;
         this.endy = endy;
         this.lowestY = 256;
         this.highestY = 0;
      }

      public float getAvgdamage() {
         return this.avgdamage;
      }

      public int getCntMindamage() {
         return this.cntMindamage;
      }

      public boolean contains(IIndex index) {
         return this.connectedBlocks.contains(index);
      }

      public int getLowestY() {
         return this.lowestY;
      }

      public int getHighestY() {
         return this.highestY;
      }

      public int needsSplitting() {
         float averageBlocksPerLevel = (float)this.connectedBlocks.size() / (float)(this.highestY - this.lowestY + 1);
         int connectionThresshold = (int)(averageBlocksPerLevel / 10.0F);
         if (connectionThresshold <= 0) {
            return -1;
         } else {
            int cuttingY = -1;
            int cuttingCount = 1000000;

            for(int y = this.lowestY; y <= this.highestY; ++y) {
               if (y >= 3 && ((Integer)this.blocksPerY.get(Integer.valueOf(y))).intValue() <= connectionThresshold) {
                  if (((Integer)this.blocksPerY.get(Integer.valueOf(y))).intValue() < cuttingCount) {
                     cuttingCount = ((Integer)this.blocksPerY.get(Integer.valueOf(y))).intValue();
                     cuttingY = y;
                  } else if (((Integer)this.blocksPerY.get(Integer.valueOf(y))).intValue() > cuttingCount * 4) {
                     return cuttingY;
                  }
               }
            }

            return -1;
         }
      }

      public boolean destroyOrMoveThis(LostCityChunkGenerator provider, BuildingInfo info) {
         return this.connections < 5 || (float)this.connections / (float)this.connectedBlocks.size() < info.profile.DESTROY_LONE_BLOCKS_FACTOR;
      }

      private boolean isOutside(BuildingInfo info, int x, int y, int z) {
         if (x < 0) {
            if (y <= info.getXmin().getMaxHeight() + 3) {
               ++this.connections;
            }

            return true;
         } else if (x > 15) {
            if (y <= info.getXmax().getMaxHeight() + 3) {
               ++this.connections;
            }

            return true;
         } else if (z < 0) {
            if (y <= info.getZmin().getMaxHeight() + 3) {
               ++this.connections;
            }

            return true;
         } else if (z > 15) {
            if (y <= info.getZmax().getMaxHeight() + 3) {
               ++this.connections;
            }

            return true;
         } else if (y < this.starty) {
            this.connections += 5;
            return true;
         } else {
            return false;
         }
      }

      public void scan(BuildingInfo info, IPrimerDriver driver, char air, char liquid, BlockPos pos) {
         DamageArea damageArea = info.getDamageArea();
         this.avgdamage = 0.0F;
         this.cntMindamage = 0;
         Queue<BlockPos> todo = new ArrayDeque();
         todo.add(pos);

         while(!todo.isEmpty()) {
            pos = (BlockPos)todo.poll();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            IIndex index = driver.getIndex(x, y, z);
            if (!this.connectedBlocks.contains(index) && !this.isOutside(info, x, y, z)) {
               driver.current(x, y, z);
               if (driver.getBlock() != air && driver.getBlock() != liquid) {
                  this.connectedBlocks.add(index);
                  float damage = damageArea.getDamage(x, y, z);
                  if (damage < 0.01F) {
                     ++this.cntMindamage;
                  }

                  this.avgdamage += damage;
                  if (!this.blocksPerY.containsKey(Integer.valueOf(y))) {
                     this.blocksPerY.put(Integer.valueOf(y), Integer.valueOf(1));
                  } else {
                     this.blocksPerY.put(Integer.valueOf(y), Integer.valueOf(((Integer)this.blocksPerY.get(Integer.valueOf(y))).intValue() + 1));
                  }

                  if (y < this.lowestY) {
                     this.lowestY = y;
                  }

                  if (y > this.highestY) {
                     this.highestY = y;
                  }

                  todo.add(pos.up());
                  todo.add(pos.down());
                  todo.add(pos.east());
                  todo.add(pos.west());
                  todo.add(pos.south());
                  todo.add(pos.north());
               }
            }
         }

         this.avgdamage /= (float)this.connectedBlocks.size();
      }

      public int compareTo(LostCitiesTerrainGenerator.Blob o) {
         return this.lowestY - o.lowestY;
      }
   }
}
