package com.yungnickyoung.minecraft.yungslaw.world;

import com.yungnickyoung.minecraft.yungslaw.YungsLaw;
import com.yungnickyoung.minecraft.yungslaw.config.Configuration;
import com.yungnickyoung.minecraft.yungslaw.config.io.ConfigLoader;
import com.yungnickyoung.minecraft.yungslaw.config.util.ConfigHolder;
import java.util.Arrays;
import java.util.HashSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class BlockGenerator implements IWorldGenerator {
   public BlockGenerator() {
   }

   public void generate(EaglercraftRandom random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (world instanceof WorldServer) {
         if (this.isDimensionWhitelisted(world)) {
            ConfigHolder config = (ConfigHolder)YungsLaw.configMap.computeIfAbsent(Integer.valueOf(world.provider.getDimension()), ConfigLoader::loadConfigFromFileForDimension);
            boolean enableOreDeletion = ((Boolean)config.enableOreDeletion.get()).booleanValue();
            int radius = ((Integer)config.genDistance.get()).intValue();
            int maxAltitude = ((Integer)config.maxAltitude.get()).intValue();
            boolean enableLiquidSafety = ((Boolean)config.enableLiquidSafety.get()).booleanValue();
            IBlockState hardBlock = this.getHardBlockFromString(config.hardBlock.get());
            Set<IBlockState> whitelistedOreBlocks = this.getBlockSetFromNames(config.oreWhitelist.get());
            Set<IBlockState> safeBlocks = this.getBlockSetFromNames(config.safeBlocks.get());
            Set<IBlockState> untouchableBlocks = this.getBlockSetFromNames(config.untouchableBlocks.get());
            int innerXStart = chunkX * 16 + 8;
            int innerZStart = chunkZ * 16 + 8;
            int innerXEnd = innerXStart + 16;
            int innerZEnd = innerZStart + 16;
            int outerXStart = innerXStart - radius;
            int outerZStart = innerZStart - radius;
            int outerXEnd = innerXEnd + radius;
            int outerZEnd = innerZEnd + radius;
            int[][][] values = new int[outerXEnd - outerXStart][maxAltitude + radius][outerZEnd - outerZStart];
            MutableBlockPos pos = new MutableBlockPos();

            for(int x = 0; x < outerXEnd - outerXStart; ++x) {
               for(int z = 0; z < outerZEnd - outerZStart; ++z) {
                  for(int y = 0; y < maxAltitude + radius; ++y) {
                     pos.setPos(outerXStart + x, y, outerZStart + z);
                     IBlockState state = world.getBlockState(pos);
                     if (!safeBlocks.contains(state) && (!enableLiquidSafety || !state.getMaterial().isLiquid())) {
                        if (untouchableBlocks.contains(state)) {
                           values[x][y][z] = -1;
                        } else if (enableOreDeletion && whitelistedOreBlocks.contains(state)) {
                           values[x][y][z] = 3;
                        } else {
                           values[x][y][z] = 2;
                        }
                     } else {
                        values[x][y][z] = 0;
                     }
                  }
               }
            }

            for(int x = outerXStart; x < outerXEnd; ++x) {
               for(int z = outerZStart; z < outerZEnd; ++z) {
                  for(int y = 0; y < maxAltitude + radius; ++y) {
                     if (values[x - outerXStart][y][z - outerZStart] == 0) {
                        for(int offsetX = x - outerXStart - radius; offsetX <= x - outerXStart + radius; ++offsetX) {
                           if (offsetX >= radius && offsetX <= 15 + radius) {
                              for(int offsetZ = z - outerZStart - radius; offsetZ <= z - outerZStart + radius; ++offsetZ) {
                                 if (offsetZ >= radius && offsetZ <= 15 + radius) {
                                    for(int offsetY = y - radius; offsetY <= y + radius; ++offsetY) {
                                       if (offsetY >= 0 && offsetY <= maxAltitude) {
                                          values[offsetX][offsetY][offsetZ] = Math.min(values[offsetX][offsetY][offsetZ], 1);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            for(int x = radius; x < 16 + radius; ++x) {
               for(int z = radius; z < 16 + radius; ++z) {
                  for(int y = 0; y < maxAltitude; ++y) {
                     pos.setPos(x + outerXStart, y, z + outerZStart);
                     if (enableOreDeletion && values[x][y][z] == 3) {
                        world.setBlockState(pos, world.getBiome(pos).fillerBlock);
                     } else if (!enableOreDeletion && values[x][y][z] == 2) {
                        world.setBlockState(pos, hardBlock);
                     }
                  }
               }
            }

         }
      }
   }

   private boolean isDimensionWhitelisted(World world) {
      return Configuration.enableGlobalWhitelist || Arrays.stream(Configuration.whitelistedDimensionIDs).anyMatch((id) -> {
         return id == world.provider.getDimension();
      });
   }

   private Set<IBlockState> getBlockSetFromNames(String[] blockNames) {
      Set<IBlockState> blockStateList = new HashSet();

      for(String blockName : blockNames) {
         try {
            if (blockName.indexOf(64) > -1) {
               String[] nameSplit = blockName.split("@", 2);
               Block block = Block.getBlockFromName(nameSplit[0]);
               if (block != null) {
                  blockStateList.add(block.getStateFromMeta(Integer.parseInt(nameSplit[1])));
               }
            } else {
               Block block = Block.getBlockFromName(blockName);
               if (block != null) {
                  blockStateList.add(block.getDefaultState());
               }
            }
         } catch (Exception var9) {
            YungsLaw.LOGGER.error("ERROR: Unable to find block {}: {}", blockName, var9);
         }
      }

      return blockStateList;
   }

   private IBlockState getHardBlockFromString(String hardBlockString) {
      IBlockState hardBlock;
      try {
         hardBlock = Block.getBlockFromName(hardBlockString).getDefaultState();
      } catch (Exception var4) {
         YungsLaw.LOGGER.error("ERROR: Unable to use block {}: {}", hardBlockString, var4);
         YungsLaw.LOGGER.error("Using obsidian instead...");
         hardBlock = Blocks.OBSIDIAN.getDefaultState();
      }

      if (hardBlock == null) {
         YungsLaw.LOGGER.error("ERROR: Unable to use block {}: null block returned.", hardBlockString);
         YungsLaw.LOGGER.warn("Using obsidian instead...");
         hardBlock = Blocks.OBSIDIAN.getDefaultState();
      }

      return hardBlock;
   }
}
