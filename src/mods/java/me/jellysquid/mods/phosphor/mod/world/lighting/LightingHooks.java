package me.jellysquid.mods.phosphor.mod.world.lighting;

import me.jellysquid.mods.phosphor.api.IChunkLighting;
import me.jellysquid.mods.phosphor.api.IChunkLightingData;
import me.jellysquid.mods.phosphor.api.ILightingEngine;
import me.jellysquid.mods.phosphor.api.ILightingEngineProvider;
import me.jellysquid.mods.phosphor.mod.PhosphorMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class LightingHooks {
   private static final EnumSkyBlock[] ENUM_SKY_BLOCK_VALUES = EnumSkyBlock.values();
   private static final AxisDirection[] ENUM_AXIS_DIRECTION_VALUES = AxisDirection.values();
   private static final int FLAG_COUNT = 32;
   public static final String neighborLightChecksKey = "NeighborLightChecks";

   public LightingHooks() {
   }

   public static void relightSkylightColumn(World world, Chunk chunk, int x, int z, int height1, int height2) {
      int yMin = Math.min(height1, height2);
      int yMax = Math.max(height1, height2) - 1;
      ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();
      int xBase = (chunk.x << 4) + x;
      int zBase = (chunk.z << 4) + z;
      scheduleRelightChecksForColumn(world, EnumSkyBlock.SKY, xBase, zBase, yMin, yMax);
      if (sections[yMin >> 4] == Chunk.NULL_BLOCK_STORAGE && yMin > 0) {
         world.checkLightFor(EnumSkyBlock.SKY, new BlockPos(xBase, yMin - 1, zBase));
      }

      short emptySections = 0;

      for(int sec = yMax >> 4; sec >= yMin >> 4; --sec) {
         if (sections[sec] == Chunk.NULL_BLOCK_STORAGE) {
            emptySections = (short)(emptySections | 1 << sec);
         }
      }

      if (emptySections != 0) {
         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            int xOffset = dir.getFrontOffsetX();
            int zOffset = dir.getFrontOffsetZ();
            boolean neighborColumnExists = ((x + xOffset | z + zOffset) & 16) == 0 || world.getChunkProvider().getLoadedChunk(chunk.x + xOffset, chunk.z + zOffset) != null;
            if (neighborColumnExists) {
               for(int sec = yMax >> 4; sec >= yMin >> 4; --sec) {
                  if ((emptySections & 1 << sec) != 0) {
                     scheduleRelightChecksForColumn(world, EnumSkyBlock.SKY, xBase + xOffset, zBase + zOffset, sec << 4, (sec << 4) + 15);
                  }
               }
            } else {
               flagChunkBoundaryForUpdate(chunk, emptySections, EnumSkyBlock.SKY, dir, getAxisDirection(dir, x, z), LightingHooks.EnumBoundaryFacing.OUT);
            }
         }
      }

   }

   public static void scheduleRelightChecksForArea(World world, EnumSkyBlock lightType, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
      for(int x = xMin; x <= xMax; ++x) {
         for(int z = zMin; z <= zMax; ++z) {
            scheduleRelightChecksForColumn(world, lightType, x, z, yMin, yMax);
         }
      }

   }

   private static void scheduleRelightChecksForColumn(World world, EnumSkyBlock lightType, int x, int z, int yMin, int yMax) {
      MutableBlockPos pos = new MutableBlockPos();

      for(int y = yMin; y <= yMax; ++y) {
         world.checkLightFor(lightType, pos.setPos(x, y, z));
      }

   }

   public static void flagSecBoundaryForUpdate(Chunk chunk, BlockPos pos, EnumSkyBlock lightType, EnumFacing dir, LightingHooks.EnumBoundaryFacing boundaryFacing) {
      flagChunkBoundaryForUpdate(chunk, (short)(1 << (pos.getY() >> 4)), lightType, dir, getAxisDirection(dir, pos.getX(), pos.getZ()), boundaryFacing);
   }

   public static void flagChunkBoundaryForUpdate(Chunk chunk, short sectionMask, EnumSkyBlock lightType, EnumFacing dir, AxisDirection axisDirection, LightingHooks.EnumBoundaryFacing boundaryFacing) {
      initNeighborLightChecks(chunk);
      short[] var10000 = ((IChunkLightingData)chunk).getNeighborLightChecks();
      int var10001 = getFlagIndex(lightType, dir, axisDirection, boundaryFacing);
      var10000[var10001] |= sectionMask;
      chunk.markDirty();
   }

   public static int getFlagIndex(EnumSkyBlock lightType, int xOffset, int zOffset, AxisDirection axisDirection, LightingHooks.EnumBoundaryFacing boundaryFacing) {
      return (lightType == EnumSkyBlock.BLOCK ? 0 : 16) | xOffset + 1 << 2 | zOffset + 1 << 1 | axisDirection.getOffset() + 1 | boundaryFacing.ordinal();
   }

   public static int getFlagIndex(EnumSkyBlock lightType, EnumFacing dir, AxisDirection axisDirection, LightingHooks.EnumBoundaryFacing boundaryFacing) {
      return getFlagIndex(lightType, dir.getFrontOffsetX(), dir.getFrontOffsetZ(), axisDirection, boundaryFacing);
   }

   private static AxisDirection getAxisDirection(EnumFacing dir, int x, int z) {
      return ((dir.getAxis() == Axis.X ? z : x) & 15) < 8 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE;
   }

   public static void scheduleRelightChecksForChunkBoundaries(World world, Chunk chunk) {
      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
         int xOffset = dir.getFrontOffsetX();
         int zOffset = dir.getFrontOffsetZ();
         Chunk nChunk = world.getChunkProvider().getLoadedChunk(chunk.x + xOffset, chunk.z + zOffset);
         if (nChunk != null) {
            for(EnumSkyBlock lightType : ENUM_SKY_BLOCK_VALUES) {
               for(AxisDirection axisDir : ENUM_AXIS_DIRECTION_VALUES) {
                  mergeFlags(lightType, chunk, nChunk, dir, axisDir);
                  mergeFlags(lightType, nChunk, chunk, dir.getOpposite(), axisDir);
                  scheduleRelightChecksForBoundary(world, chunk, nChunk, (Chunk)null, lightType, xOffset, zOffset, axisDir);
                  scheduleRelightChecksForBoundary(world, nChunk, chunk, (Chunk)null, lightType, -xOffset, -zOffset, axisDir);
                  scheduleRelightChecksForBoundary(world, nChunk, (Chunk)null, chunk, lightType, zOffset != 0 ? axisDir.getOffset() : 0, xOffset != 0 ? axisDir.getOffset() : 0, dir.getAxisDirection() == AxisDirection.POSITIVE ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
               }
            }
         }
      }

   }

   private static void mergeFlags(EnumSkyBlock lightType, Chunk inChunk, Chunk outChunk, EnumFacing dir, AxisDirection axisDir) {
      IChunkLightingData outChunkLightingData = (IChunkLightingData)outChunk;
      if (outChunkLightingData.getNeighborLightChecks() != null) {
         IChunkLightingData inChunkLightingData = (IChunkLightingData)inChunk;
         initNeighborLightChecks(inChunk);
         int inIndex = getFlagIndex(lightType, dir, axisDir, LightingHooks.EnumBoundaryFacing.IN);
         int outIndex = getFlagIndex(lightType, dir.getOpposite(), axisDir, LightingHooks.EnumBoundaryFacing.OUT);
         short[] var10000 = inChunkLightingData.getNeighborLightChecks();
         var10000[inIndex] |= outChunkLightingData.getNeighborLightChecks()[outIndex];
      }
   }

   private static void scheduleRelightChecksForBoundary(World world, Chunk chunk, Chunk nChunk, Chunk sChunk, EnumSkyBlock lightType, int xOffset, int zOffset, AxisDirection axisDir) {
      IChunkLightingData chunkLightingData = (IChunkLightingData)chunk;
      if (chunkLightingData.getNeighborLightChecks() != null) {
         int flagIndex = getFlagIndex(lightType, xOffset, zOffset, axisDir, LightingHooks.EnumBoundaryFacing.IN);
         int flags = chunkLightingData.getNeighborLightChecks()[flagIndex];
         if (flags != 0) {
            if (nChunk == null) {
               nChunk = world.getChunkProvider().getLoadedChunk(chunk.x + xOffset, chunk.z + zOffset);
               if (nChunk == null) {
                  return;
               }
            }

            if (sChunk == null) {
               sChunk = world.getChunkProvider().getLoadedChunk(chunk.x + (zOffset != 0 ? axisDir.getOffset() : 0), chunk.z + (xOffset != 0 ? axisDir.getOffset() : 0));
               if (sChunk == null) {
                  return;
               }
            }

            int reverseIndex = getFlagIndex(lightType, -xOffset, -zOffset, axisDir, LightingHooks.EnumBoundaryFacing.OUT);
            chunkLightingData.getNeighborLightChecks()[flagIndex] = 0;
            IChunkLightingData nChunkLightingData = (IChunkLightingData)nChunk;
            if (nChunkLightingData.getNeighborLightChecks() != null) {
               nChunkLightingData.getNeighborLightChecks()[reverseIndex] = 0;
            }

            chunk.markDirty();
            nChunk.markDirty();
            int xMin = chunk.x << 4;
            int zMin = chunk.z << 4;
            if ((xOffset | zOffset) > 0) {
               xMin += 15 * xOffset;
               zMin += 15 * zOffset;
            }

            if (axisDir == AxisDirection.POSITIVE) {
               xMin += 8 * (zOffset & 1);
               zMin += 8 * (xOffset & 1);
            }

            int xMax = xMin + 7 * (zOffset & 1);
            int zMax = zMin + 7 * (xOffset & 1);

            for(int y = 0; y < 16; ++y) {
               if ((flags & 1 << y) != 0) {
                  scheduleRelightChecksForArea(world, lightType, xMin, y << 4, zMin, xMax, (y << 4) + 15, zMax);
               }
            }

         }
      }
   }

   public static void initNeighborLightChecks(Chunk chunk) {
      IChunkLightingData lightingData = (IChunkLightingData)chunk;
      if (lightingData.getNeighborLightChecks() == null) {
         lightingData.setNeighborLightChecks(new short[32]);
      }

   }

   public static void writeNeighborLightChecksToNBT(Chunk chunk, NBTTagCompound nbt) {
      short[] neighborLightChecks = ((IChunkLightingData)chunk).getNeighborLightChecks();
      if (neighborLightChecks != null) {
         boolean empty = true;
         NBTTagList list = new NBTTagList();

         for(short flags : neighborLightChecks) {
            list.appendTag(new NBTTagShort(flags));
            if (flags != 0) {
               empty = false;
            }
         }

         if (!empty) {
            nbt.setTag("NeighborLightChecks", list);
         }

      }
   }

   public static void readNeighborLightChecksFromNBT(Chunk chunk, NBTTagCompound nbt) {
      if (nbt.hasKey("NeighborLightChecks", 9)) {
         NBTTagList list = nbt.getTagList("NeighborLightChecks", 2);
         if (list.tagCount() == 32) {
            initNeighborLightChecks(chunk);
            short[] neighborLightChecks = ((IChunkLightingData)chunk).getNeighborLightChecks();

            for(int i = 0; i < 32; ++i) {
               neighborLightChecks[i] = ((NBTTagShort)list.get(i)).getShort();
            }
         } else {
            PhosphorMod.LOGGER.warn("Chunk field {} had invalid length, ignoring it (chunk coordinates: {} {})", "NeighborLightChecks", Integer.valueOf(chunk.x), Integer.valueOf(chunk.z));
         }
      }

   }

   public static void initChunkLighting(Chunk chunk, World world) {
      int xBase = chunk.x << 4;
      int zBase = chunk.z << 4;
      PooledMutableBlockPos pos = PooledMutableBlockPos.retain(xBase, 0, zBase);
      if (world.isAreaLoaded(pos.add(-16, 0, -16), pos.add(31, 255, 31), false)) {
         ExtendedBlockStorage[] extendedBlockStorage = chunk.getBlockStorageArray();

         for(int j = 0; j < extendedBlockStorage.length; ++j) {
            ExtendedBlockStorage storage = extendedBlockStorage[j];
            if (storage != Chunk.NULL_BLOCK_STORAGE) {
               int yBase = j * 16;

               for(int y = 0; y < 16; ++y) {
                  for(int z = 0; z < 16; ++z) {
                     for(int x = 0; x < 16; ++x) {
                        int key = storage.data.storage.getAt(y << 8 | z << 4 | x);
                        if (key != 0) {
                           IBlockState state = storage.data.palette.getBlockState(key);
                           if (state != null) {
                              int light = state.getLightValue(world, pos);
                              if (light > 0) {
                                 pos.setPos(xBase + x, yBase + y, zBase + z);
                                 world.checkLightFor(EnumSkyBlock.BLOCK, pos);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (world.provider.hasSkyLight()) {
            ((IChunkLightingData)chunk).setSkylightUpdatedPublic();
         }

         ((IChunkLightingData)chunk).setLightInitialized(true);
      }

      pos.release();
   }

   public static void checkChunkLighting(Chunk chunk, World world) {
      if (!((IChunkLightingData)chunk).isLightInitialized()) {
         initChunkLighting(chunk, world);
      }

      for(int x = -1; x <= 1; ++x) {
         for(int z = -1; z <= 1; ++z) {
            if (x != 0 || z != 0) {
               Chunk nChunk = world.getChunkProvider().getLoadedChunk(chunk.x + x, chunk.z + z);
               if (nChunk == null || !((IChunkLightingData)nChunk).isLightInitialized()) {
                  return;
               }
            }
         }
      }

      chunk.setLightPopulated(true);
   }

   public static void initSkylightForSection(World world, Chunk chunk, ExtendedBlockStorage section) {
      if (world.provider.hasSkyLight()) {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               if (chunk.getHeightValue(x, z) <= section.getYLocation()) {
                  for(int y = 0; y < 16; ++y) {
                     section.setSkyLight(x, y, z, EnumSkyBlock.SKY.defaultLightValue);
                  }
               }
            }
         }
      }

   }

   private static short[] getNeighborLightChecks(Chunk chunk) {
      return ((IChunkLightingData)chunk).getNeighborLightChecks();
   }

   private static void setNeighborLightChecks(Chunk chunk, short[] table) {
      ((IChunkLightingData)chunk).setNeighborLightChecks(table);
   }

   public static int getCachedLightFor(Chunk chunk, EnumSkyBlock type, BlockPos pos) {
      return ((IChunkLighting)chunk).getCachedLightFor(type, pos);
   }

   public static ILightingEngine getLightingEngine(World world) {
      return ((ILightingEngineProvider)world).getLightingEngine();
   }

   public static enum EnumBoundaryFacing {
      IN,
      OUT;

      private EnumBoundaryFacing() {
      }

      public LightingHooks.EnumBoundaryFacing getOpposite() {
         return this == IN ? OUT : IN;
      }
   }
}
