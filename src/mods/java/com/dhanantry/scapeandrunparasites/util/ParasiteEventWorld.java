package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRubble;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.network.SRPPacketBiomeChange;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBall;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBigBall;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyCore;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteMouth;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNodeCore;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteSpine;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTallFlower;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTenFlower;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;

public class ParasiteEventWorld {
   public static int blockInfestedCount;
   public static int blockParasiteCount;

   public ParasiteEventWorld() {
   }

   public static int getTimer(int meta) {
      int timer = 40;
      if (meta == 1) {
         timer = 20 * SRPConfigMobs.venkrolBlockTime;
      } else if (meta == 2) {
         timer = 20 * SRPConfigMobs.venkrolsiiBlockTime;
      } else if (meta == 3) {
         timer = 20 * SRPConfigMobs.venkrolsiiiBlockTime;
      }

      return timer;
   }

   private static boolean blockException(World worldIn, BlockPos pos, Block block, IBlockState state, String[] list, boolean invert, float maxHardness) {
      float bHard = state.getBlockHardness(worldIn, pos);
      if (bHard <= maxHardness && bHard >= 0.0F) {
         if (ParasiteEventEntity.checkName(block.getRegistryName().toString(), list, invert)) {
            return true;
         } else {
            return block instanceof BlockBreakable || block instanceof BlockContainer || block instanceof BlockOre || block instanceof BlockHorizontal || block instanceof BlockTNT || block.isPassable(worldIn, pos) || block instanceof IPlantable || !state.isFullCube();
         }
      } else {
         return true;
      }
   }

   public static int canBiomeStillExist(World worldIn, BlockPos pos, boolean spread) {
      return SRPConfigWorld.nodesActivated && SRPConfigWorld.biomeRegster ? SRPWorldData.get(worldIn).nearestHeartAge(pos, spread, 0) : -1;
   }

   public static void SpreadBiome(World worldIn, BlockPos pos, int age) {
      if (SRPConfigWorld.nodesActivated && SRPConfigWorld.biomeRegster) {
         SRPWorldData data = SRPWorldData.get(worldIn);
         int distance = data.getDistanceSpreadByAge(age, false);

         for(int x = pos.getX() - 4; x <= pos.getX() + 4; ++x) {
            for(int z = pos.getZ() - 4; z <= pos.getZ() + 4; ++z) {
               BlockPos convert = new BlockPos(x, pos.getY(), z);
               if (data.isInRangeOfHeart(convert, (double)distance) != -1 && !ParasiteEventEntity.checkName(worldIn.getBiome(convert).getRegistryName().toString(), SRPConfigWorld.biomeBlackList, SRPConfigWorld.biomeBlackListInverted)) {
                  positionToParasiteBiome(worldIn, convert);
                  SRPMain.network.sendToDimension(new SRPPacketBiomeChange(convert, true), worldIn.provider.getDimension());
               }
            }
         }

      }
   }

   public static void positionToParasiteBiome(World worldIn, BlockPos pos) {
      int inChunkX = pos.getX() & 15;
      int inChunkZ = pos.getZ() & 15;
      worldIn.getChunkFromBlockCoords(pos).getBiomeArray()[inChunkZ << 4 | inChunkX] = (byte)Biome.getIdForBiome(SRPBiomes.biomeInfested);
   }

   public static void killBiome(World worldIn, BlockPos pos, int range) {
      SRPWorldData data = SRPWorldData.get(worldIn);
      int worldTime = 0;

      for(int x = pos.getX() - range; x <= pos.getX() + range; ++x) {
         for(int z = pos.getZ() - range; z <= pos.getZ() + range; ++z) {
            int age = data.nearestHeartAge(pos, true, worldTime);
            data.getDistanceSpreadByAge(age, false);
            BlockPos convert = new BlockPos(x, pos.getY(), z);
            if (worldIn.getBiome(convert) instanceof BiomeParasite) {
               positionToBiome(worldIn, convert);
               SRPMain.network.sendToDimension(new SRPPacketBiomeChange(convert, false), worldIn.provider.getDimension());
            }
         }
      }

   }

   public static void positionToBiome(World worldIn, BlockPos pos) {
      int inChunkX = pos.getX() & 15;
      int inChunkZ = pos.getZ() & 15;
      worldIn.getChunkFromBlockCoords(pos).getBiomeArray()[inChunkZ << 4 | inChunkX] = (byte)Biome.getIdForBiome(Biomes.PLAINS);
   }

   public static int placeHeartInWorld(World worldIn, BlockPos pos) {
      if (!SRPConfigWorld.nodesActivated) {
         return 3;
      } else if (!SRPConfigWorld.biomeRegster) {
         return 4;
      } else if (!chechBlackListNodes(worldIn)) {
         return 2;
      } else {
         BlockPos origin = worldIn.getSpawnPoint();
         if (getDistanceSQ((double)origin.getX(), (double)origin.getY(), (double)origin.getZ(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) < (double)(SRPConfigWorld.minimumDistanceFromSpawnPoint * SRPConfigWorld.minimumDistanceFromSpawnPoint)) {
            return 5;
         } else {
            SRPWorldData data = SRPWorldData.get(worldIn);
            if (SRPConfigSystems.useEvolution) {
               if (data.getEvolutionPhase() < SRPConfigSystems.evolutionNodeUnlock) {
                  return 6;
               }
            } else if (!SRPConfigWorld.venkrolNode) {
               return 10;
            }

            pos = ParasiteEventEntity.getFloor(worldIn, pos, 100);
            if (pos == null) {
               return 7;
            } else {
               int key = data.setNode(pos.getX(), pos.getY(), pos.getZ(), 0);
               if (key == 1) {
                  WorldGenParasiteNodeCore gen = new WorldGenParasiteNodeCore(false, 1);
                  gen.generate(worldIn, new EaglercraftRandom(), pos);
                  SpreadBiome(worldIn, pos, 1);
                  ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.nodeWarning, 10);
                  return 1;
               } else {
                  return key;
               }
            }
         }
      }
   }

   public static double getDistanceSQ(double rootx, double rooty, double rootz, double standingx, double standingy, double standingz) {
      double d0 = rootx - standingx;
      double d1 = rooty - standingy;
      double d2 = rootz - standingz;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   private static boolean chechBlackListNodes(World worldIn) {
      for(int i : SRPConfigWorld.blackListedDimensionsNodes) {
         if (i == worldIn.provider.getDimension()) {
            return true;
         }
      }

      return false;
   }

   public static boolean removeHeartInWorld(World worldIn, BlockPos pos) {
      if (SRPConfigWorld.nodesActivated && SRPConfigWorld.biomeRegster) {
         SRPWorldData data = SRPWorldData.get(worldIn);
         return data.removeNode(pos.getX(), pos.getY(), pos.getZ());
      } else {
         return false;
      }
   }

   public static int getHeartAgePostion(World worldIn, BlockPos pos) {
      SRPWorldData data = SRPWorldData.get(worldIn);
      return data.getHeartPocition(pos, 0);
   }

   public static int nodesPoints(World worldIn) {
      SRPWorldData data = SRPWorldData.get(worldIn);
      return data.totalNodePoints(0);
   }

   public static void canInfestBlock(World worldIn, BlockPos pos, EaglercraftRandom rand, int stage, boolean fromVenkrol) {
      if (blockInfestedCount <= SRPConfig.BlockInfestedLimit) {
         if (worldIn.getDifficulty() != EnumDifficulty.PEACEFUL) {
            int covertedBlocks = 0;
            int range = SRPConfigMobs.venkrolsiiiRange - 1;
            int rangeY = SRPConfigMobs.venkrolsiiiRangeY;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow((double)range, (double)rangeY, (double)range);
            List<EntityPStationaryArchitect> moblist = worldIn.getEntitiesWithinAABB(EntityPStationaryArchitect.class, axisalignedbb);
            if (fromVenkrol) {
               if (moblist.isEmpty() && stage < 4) {
                  if (worldIn.rand.nextDouble() < SRPConfigSystems.rsVenkrolEmpty && stage != 1 && (worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR || worldIn.getBlockState(pos.up()).getBlock() instanceof BlockBush)) {
                     if (worldIn.getLightBrightness(pos.up()) > 0.46666667F && stage < 4) {
                        return;
                     }

                     if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos.up()) > 7 && stage < 4) {
                        return;
                     }

                     EntityVenkrol out = new EntityVenkrol(worldIn);
                     out.setLocationAndAngles((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), 0.0F, 0.0F);
                     worldIn.spawnEntity(out);
                     out.cannotDespawn(true);
                  }

                  return;
               }

               if (!moblist.isEmpty() && stage < 4) {
                  int flagV = 0;

                  for(EntityPStationaryArchitect mob : moblist) {
                     if (mob.getStageV() >= stage) {
                        flagV = 0;
                        BlockPos venPos = mob.getPosition();
                        if (worldIn.getLightBrightness(venPos.up()) < (float)SRPConfigSystems.rsBlockLight / 15.0F && stage < 4) {
                           ++flagV;
                        }

                        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, venPos.up()) < SRPConfigSystems.rsBlockLight && stage < 4) {
                           ++flagV;
                        }
                     }
                  }

                  if (flagV < 2) {
                     return;
                  }
               }
            }

            for(int dir = 0; dir <= 5; ++dir) {
               BlockPos helper = directionToSpread(pos, dir);
               IBlockState lookingState = worldIn.getBlockState(helper);
               Material mat = lookingState.getMaterial();
               Block lookingBlock = lookingState.getBlock();
               if (!(lookingBlock instanceof IMetaName)) {
                  if (lookingBlock.isWood(worldIn, helper) && stage <= 3) {
                     ++covertedBlocks;
                     worldIn.setBlockState(helper, SRPBlocks.InfestedTrunk.getDefaultState());
                  }

                  if (!blockException(worldIn, helper, lookingBlock, lookingState, SRPConfigSystems.blockBList, SRPConfigSystems.blockBListWhite, SRPConfigSystems.rsBlockIMaxH) || stage > 3) {
                     if (lookingBlock instanceof BlockBase) {
                        if (lookingBlock != SRPBlocks.BiomeHeart && lookingBlock != SRPBlocks.ColonyHeart) {
                           if (mat == Material.GROUND) {
                              int lookM = lookingBlock.getMetaFromState(lookingState);
                              if (stage < 4 || lookM > 1 && stage != 5) {
                                 if (lookM < stage && stage < 4) {
                                    worldIn.setBlockState(helper, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(stage)));
                                 }
                              } else {
                                 worldIn.setBlockState(helper, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(stage)));
                                 worldIn.updateBlockTick(helper, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(stage)).getBlock(), 40, 5);
                              }
                           } else if (mat == Material.ROCK) {
                              int lookM = lookingBlock.getMetaFromState(lookingState);
                              if (stage < 4 || lookM > 1 && stage != 5) {
                                 if (lookM < stage && stage < 4 && stage >= 2) {
                                    worldIn.setBlockState(helper, SRPBlocks.InfestedRubble.getDefaultState().withProperty(BlockInfestedRubble.STAGE, Integer.valueOf(stage)));
                                 }
                              } else {
                                 worldIn.setBlockState(helper, SRPBlocks.InfestedRubble.getDefaultState().withProperty(BlockInfestedRubble.STAGE, Integer.valueOf(stage)));
                                 worldIn.updateBlockTick(helper, SRPBlocks.InfestedRubble.getDefaultState().withProperty(BlockInfestedRubble.STAGE, Integer.valueOf(stage)).getBlock(), 40, 5);
                              }
                           }
                        }
                     } else if (stage < 4) {
                        if (mat != Material.GROUND && mat != Material.GRASS) {
                           if (mat == Material.SAND) {
                              worldIn.setBlockState(helper, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(stage)));
                              ++covertedBlocks;
                              spawnGenFeatureInfested(worldIn, helper.up(), rand);
                              spawnGenRoofInfested(worldIn, helper.down(), rand);
                           } else if (mat == Material.ROCK) {
                              if (stage >= 2) {
                                 worldIn.setBlockState(helper, SRPBlocks.InfestedRubble.getDefaultState().withProperty(BlockInfestedRubble.STAGE, Integer.valueOf(stage)));
                                 ++covertedBlocks;
                                 spawnGenFeatureInfested(worldIn, helper.up(), rand);
                                 spawnGenRoofInfested(worldIn, helper.down(), rand);
                              }
                           } else if (mat == Material.PLANTS) {
                              ;
                           }
                        } else {
                           worldIn.setBlockState(helper, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(stage)));
                           ++covertedBlocks;
                           spawnGenFeatureInfested(worldIn, helper.up(), rand);
                           spawnGenRoofInfested(worldIn, helper.down(), rand);
                        }
                     }
                  }
               }
            }

            if (stage >= 4) {
               if (worldIn.getBlockState(pos).getBlock() == SRPBlocks.InfestedStain) {
                  worldIn.setBlockState(pos, SRPBlocks.optionalDirt.getDefaultState());
               } else if (worldIn.getBlockState(pos).getBlock() == SRPBlocks.InfestedRubble) {
                  worldIn.setBlockState(pos, SRPBlocks.optionalRub.getDefaultState());
               }

               if (worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR || worldIn.getBlockState(pos.up()).getBlock() == SRPBlocks.ParasiteBush) {
                  worldIn.setBlockState(pos.up(), SRPBlocks.InfestRemain.getDefaultState());
               }
            } else if (covertedBlocks != 0) {
               blockInfestedCount += covertedBlocks;
               if (SRPConfigSystems.useEvolution) {
                  SRPWorldData data = SRPWorldData.get(worldIn);
                  data.setTotalKills(SRPConfigSystems.valueBlock * covertedBlocks, true, worldIn, true);
               }
            }

         }
      }
   }

   public static void spreadBiomeBlockStain(World worldIn, BlockPos pos, EaglercraftRandom rand) {
      int covertedBlocks = 0;

      for(int dir = 0; dir <= 5; ++dir) {
         BlockPos helper = directionToSpread(pos, dir);
         IBlockState lookingState = worldIn.getBlockState(helper);
         Material mat = lookingState.getMaterial();
         Block lookingBlock = lookingState.getBlock();
         if (!(lookingBlock instanceof IMetaName)) {
            if (lookingBlock.isWood(worldIn, helper) || lookingBlock == SRPBlocks.InfestedTrunk) {
               ++covertedBlocks;
               worldIn.setBlockState(helper, SRPBlocks.ParasiteTrunk.getDefaultState());
            }

            if (!blockException(worldIn, helper, lookingBlock, lookingState, SRPConfigWorld.blockBBiomeList, SRPConfigWorld.blockBBiomeListWhite, SRPConfigWorld.biomeBlockIMaxH)) {
               if (lookingBlock == SRPBlocks.InfestedStain) {
                  worldIn.setBlockState(helper, SRPBlocks.ParasiteStain.getDefaultState());
                  spawnGenFeatureParasite(worldIn, helper.up(), rand);
                  spawnGenRoofParasite(worldIn, helper.down(), rand);
               } else if (mat != Material.GROUND && mat != Material.GRASS) {
                  if (mat == Material.SAND) {
                     worldIn.setBlockState(helper, SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.MUD));
                     ++covertedBlocks;
                     spawnGenFeatureParasite(worldIn, helper.up(), rand);
                     spawnGenRoofParasite(worldIn, helper.down(), rand);
                  } else if (mat == Material.ROCK) {
                     if (lookingBlock != SRPBlocks.BiomeHeart && lookingBlock != SRPBlocks.ColonyHeart) {
                        worldIn.setBlockState(helper, SRPBlocks.ParasiteRubble.getDefaultState().withProperty(BlockParasiteRubble.VARIANT, BlockParasiteRubble.EnumType.STONE));
                        ++covertedBlocks;
                        spawnGenFeatureParasite(worldIn, helper.up(), rand);
                        spawnGenRoofParasite(worldIn, helper.down(), rand);
                     }
                  } else if (mat == Material.PLANTS) {
                     worldIn.setBlockState(helper, SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FLESH));
                     ++covertedBlocks;
                  }
               } else {
                  worldIn.setBlockState(helper, SRPBlocks.ParasiteStain.getDefaultState());
                  ++covertedBlocks;
                  spawnGenFeatureParasite(worldIn, helper.up(), rand);
                  spawnGenRoofParasite(worldIn, helper.down(), rand);
               }
            }
         }
      }

      if (covertedBlocks != 0) {
         blockParasiteCount += covertedBlocks;
         if (SRPConfigSystems.useEvolution) {
            SRPWorldData data = SRPWorldData.get(worldIn);
            data.setTotalKills(SRPConfigSystems.valueBlock * covertedBlocks, true, worldIn, true);
         }
      }

   }

   public static void spreadBiomeBlockTrunk(World worldIn, BlockPos pos, EaglercraftRandom rand) {
      int posx = pos.getX();
      int posz = pos.getZ();
      int posy = pos.getY();

      for(int x = -1; x <= 1; ++x) {
         for(int z = -1; z <= 1; ++z) {
            for(int y = -1; y <= 1; ++y) {
               BlockPos helper = new BlockPos(posx + x, posy + y, posz + z);
               IBlockState lookingState = worldIn.getBlockState(helper);
               Block lookingBlock = lookingState.getBlock();
               if (lookingBlock.isWood(worldIn, helper) && lookingBlock != SRPBlocks.ParasiteTrunk) {
                  worldIn.setBlockState(helper, SRPBlocks.ParasiteTrunk.getDefaultState());
                  if (SRPConfigSystems.useEvolution) {
                     SRPWorldData data = SRPWorldData.get(worldIn);
                     data.setTotalKills(SRPConfigSystems.valueBlock, true, worldIn, true);
                  }
               }
            }
         }
      }

   }

   public static BlockPos directionToSpread(BlockPos atm, int choice) {
      switch(choice) {
      case 0:
         atm = atm.north();
         break;
      case 1:
         atm = atm.east();
         break;
      case 2:
         atm = atm.west();
         break;
      case 3:
         atm = atm.south();
         break;
      case 4:
         atm = atm.up();
         break;
      default:
         atm = atm.down();
      }

      return atm;
   }

   public static void spawnGenFeatureParasite(World worldIn, BlockPos pos, EaglercraftRandom rand) {
      double bonus = 0.0D;
      if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR && worldIn.getBlockState(pos).getMaterial() == Material.WATER) {
         pos = ParasiteEventEntity.getFloor(worldIn, pos, 10);
         bonus = 3.0E-5D;
         if (pos == null) {
            return;
         }
      }

      if (rand.nextDouble() < SRPConfigWorld.biomeTree) {
         if (rand.nextDouble() < 0.2D) {
            WorldGenAbstractTree gen = new WorldGenParasiteTreeThin(false);
            if (!gen.generate(worldIn, rand, pos) && worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
               worldIn.setBlockState(pos, SRPBlocks.ParasiteSapling.getDefaultState().withProperty(BlockParasiteSapling.VARIANT, BlockParasiteSapling.EnumType.TREE));
            }

         } else {
            WorldGenAbstractTree gen = new WorldGenParasiteTree(false);
            if (!gen.generate(worldIn, rand, pos) && worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
               worldIn.setBlockState(pos, SRPBlocks.ParasiteSapling.getDefaultState().withProperty(BlockParasiteSapling.VARIANT, BlockParasiteSapling.EnumType.TREETHIN));
            }

         }
      } else if (rand.nextDouble() < SRPConfigWorld.biomeFlower) {
         WorldGenParasiteTallFlower gen = new WorldGenParasiteTallFlower(false);
         if (!gen.generate(worldIn, rand, pos) && worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
            worldIn.setBlockState(pos, SRPBlocks.ParasiteSapling.getDefaultState().withProperty(BlockParasiteSapling.VARIANT, BlockParasiteSapling.EnumType.FLOWERTALL));
         }

      } else if (rand.nextDouble() < SRPConfigWorld.biomeSpine) {
         WorldGenParasiteSpine gen = new WorldGenParasiteSpine(false);
         if (!gen.generate(worldIn, rand, pos)) {
            ;
         }

      } else if (rand.nextDouble() < SRPConfigWorld.biomeTenflower) {
         WorldGenParasiteTenFlower gen = new WorldGenParasiteTenFlower(false);
         if (!gen.generate(worldIn, rand, pos)) {
            ;
         }

      } else if (rand.nextDouble() < SRPConfigWorld.biomeBall + bonus) {
         WorldGenParasiteBall gen = new WorldGenParasiteBall(false);
         if (!gen.generate(worldIn, rand, pos)) {
            ;
         }

      } else if (rand.nextDouble() < SRPConfigWorld.biomeBigBall + bonus) {
         WorldGenParasiteBigBall gen = new WorldGenParasiteBigBall(false);
         if (!gen.generate(worldIn, rand, pos)) {
            ;
         }

      } else {
         if (rand.nextInt(500) == 0) {
            WorldGenParasiteBush gen = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.TENDRIL, 4);
            gen.generate(worldIn, rand, pos);
         }

         if (rand.nextInt(250) == 0) {
            switch(rand.nextInt(3)) {
            case 0:
               WorldGenParasiteBush gen1 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.POP, 1);
               gen1.generate(worldIn, rand, pos);
               break;
            case 1:
               WorldGenParasiteBush gen2 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 2);
               gen2.generate(worldIn, rand, pos);
               break;
            case 2:
               WorldGenParasiteBush gen3 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.TOOH, 3);
               gen3.generate(worldIn, rand, pos);
            }
         }

         if (rand.nextDouble() < SRPConfigWorld.biomeMouth) {
            WorldGenParasiteMouth gen = new WorldGenParasiteMouth(false);
            if (gen.generate(worldIn, rand, pos)) {
               return;
            }
         }

      }
   }

   public static void spawnGenRoofParasite(World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if (worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
         worldIn.setBlockState(pos, SRPBlocks.ParasiteBush.getDefaultState().withProperty(BlockParasiteBush.VARIANT, BlockParasiteBush.EnumType.BINE));
         pos = pos.down();
         if (worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
            worldIn.setBlockState(pos, SRPBlocks.ParasiteBush.getDefaultState().withProperty(BlockParasiteBush.VARIANT, BlockParasiteBush.EnumType.BINE));
            pos = pos.down();
            if (rand.nextInt(2) == 0) {
               if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                  return;
               }

               worldIn.setBlockState(pos, SRPBlocks.ParasiteBush.getDefaultState().withProperty(BlockParasiteBush.VARIANT, BlockParasiteBush.EnumType.BINE));
               pos = pos.down();
               if (rand.nextInt(2) == 0) {
                  if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                     return;
                  }

                  worldIn.setBlockState(pos, SRPBlocks.ParasiteBush.getDefaultState().withProperty(BlockParasiteBush.VARIANT, BlockParasiteBush.EnumType.BINE));
                  pos = pos.down();
                  if (rand.nextInt(2) == 0) {
                     if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                        return;
                     }

                     worldIn.setBlockState(pos, SRPBlocks.ParasiteBush.getDefaultState().withProperty(BlockParasiteBush.VARIANT, BlockParasiteBush.EnumType.BINE));
                  }
               }
            }

         }
      }
   }

   public static void spawnGenFeatureInfested(World worldIn, BlockPos pos, EaglercraftRandom rand) {
      double bonus = 0.0D;
      if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR && worldIn.getBlockState(pos).getMaterial() == Material.WATER) {
         pos = ParasiteEventEntity.getFloor(worldIn, pos, 10);
         bonus = 3.0E-5D;
         if (pos == null) {
            return;
         }
      }

      if (rand.nextInt(20) == 0) {
         WorldGenParasiteBush gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.SPINE, 4);
         gen.generate(worldIn, rand, pos);
      }

      if (rand.nextInt(20) == 0) {
         if (rand.nextInt(2) == 0) {
            WorldGenParasiteBush gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.ARC, 2);
            gen.generate(worldIn, rand, pos);
         } else {
            WorldGenParasiteBush gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.GRASS1, 1);
            gen.generate(worldIn, rand, pos);
         }
      }

   }

   public static void spawnGenRoofInfested(World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if (worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
         if (rand.nextInt(5) == 0) {
            worldIn.setBlockState(pos, SRPBlocks.InfestedBush.getDefaultState().withProperty(BlockInfestedBush.VARIANT, BlockInfestedBush.EnumType.VINE));
            pos = pos.down();
            if (rand.nextInt(4) == 0) {
               if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                  return;
               }

               worldIn.setBlockState(pos, SRPBlocks.InfestedBush.getDefaultState().withProperty(BlockInfestedBush.VARIANT, BlockInfestedBush.EnumType.VINE));
               pos = pos.down();
               if (rand.nextInt(3) == 0) {
                  if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                     return;
                  }

                  worldIn.setBlockState(pos, SRPBlocks.InfestedBush.getDefaultState().withProperty(BlockInfestedBush.VARIANT, BlockInfestedBush.EnumType.VINE));
               }
            }
         }

      }
   }

   public static void checkNodeStatus(World worldIn) {
      SRPWorldData data = SRPWorldData.get(worldIn);
      data.checkHeartExistance(worldIn);
   }

   public static int placeColonyInWorld(World worldIn, BlockPos pos) {
      if (!SRPConfigWorld.coloniesActivated) {
         return 3;
      } else if (!chechBlackListColonies(worldIn)) {
         return 2;
      } else {
         SRPWorldData data = SRPWorldData.get(worldIn);
         if (SRPConfigSystems.useEvolution) {
            if (data.getEvolutionPhase() < SRPConfigSystems.evolutionColonyUnlock) {
               return 4;
            }
         } else if (!SRPConfigWorld.dodColony) {
            return 6;
         }

         int newX = findNumberMultipleOf(pos.getX(), 26);
         int newZ = findNumberMultipleOf(pos.getZ(), 26);
         BlockPos newPos = new BlockPos(newX, pos.getY(), newZ);
         newPos = ParasiteEventEntity.getFloor(worldIn, newPos, 100);
         if (newPos == null) {
            return 5;
         } else {
            int key = data.setColony(newPos.getX(), newPos.getY(), newPos.getZ(), 0);
            if (key == 1) {
               WorldGenParasiteColonyCore gen = new WorldGenParasiteColonyCore(false);
               gen.generate(worldIn, new EaglercraftRandom(), newPos);
               ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.colonyWarning, 11);
               return 1;
            } else {
               return key;
            }
         }
      }
   }

   private static boolean chechBlackListColonies(World worldIn) {
      for(int i : SRPConfigWorld.blackListedDimensionsColonies) {
         if (i == worldIn.provider.getDimension()) {
            return true;
         }
      }

      return false;
   }

   private static int findNumberMultipleOf(int n, int x) {
      if (x > n) {
         ;
      }

      boolean neg = false;
      if (n < 0) {
         n *= -1;
         neg = true;
      }

      n = n + x / 2;
      n = n - n % x;
      if (neg) {
         n *= -1;
      }

      return n;
   }

   public static boolean removeColonyInWorld(World worldIn, BlockPos pos) {
      if (!SRPConfigWorld.coloniesActivated) {
         return false;
      } else {
         SRPWorldData data = SRPWorldData.get(worldIn);
         if (data.removeColony(pos.getX(), pos.getY(), pos.getZ())) {
            data.resetGlobalAdaptation();
            return true;
         } else {
            return false;
         }
      }
   }

   public static BlockPos rangeOfColony(World worldIn, BlockPos pos, boolean effect) {
      return SRPWorldData.get(worldIn).nearestColonyPosition(pos, 0, effect);
   }

   public static int numberofColonies(World worldIn) {
      return SRPWorldData.get(worldIn).colonunumber();
   }

   public static int spreadOfColony(World worldIn, BlockPos pos) {
      return SRPWorldData.get(worldIn).getColonyDistanceSpreadByPosition(pos, false, 0);
   }

   public static void checkColonyStatus(World worldIn) {
      SRPWorldData data = SRPWorldData.get(worldIn);
      data.checkColonyExistance(worldIn);
   }
}
