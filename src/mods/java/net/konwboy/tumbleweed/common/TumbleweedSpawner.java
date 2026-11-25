package net.konwboy.tumbleweed.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.entity.EntityEvent.CanUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class TumbleweedSpawner {
   private static final int TRY_SPAWN_TICKS = 200;
   private static final int MOB_COUNT_DIV = 289;
   private static final int SEARCH_RADIUS = 2;
   private static final int SPAWN_ATTEMPTS = 10;

   public TumbleweedSpawner() {
   }

   private static void trySpawn(WorldServer world) {
      Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();

      for(EntityPlayer entityplayer : world.playerEntities) {
         if (!entityplayer.isSpectator()) {
            int playerX = MathHelper.floor(entityplayer.posX / 16.0D);
            int playerZ = MathHelper.floor(entityplayer.posZ / 16.0D);

            for(int x = 8; x >= -8; --x) {
               for(int z = 8; z >= -8; --z) {
                  boolean corner = x == -8 || x == 8 || z == -8 || z == 8;
                  ChunkPos chunk = new ChunkPos(x + playerX, z + playerZ);
                  if (!eligibleChunksForSpawning.contains(chunk) && !corner && world.getWorldBorder().contains(chunk) && isEntityProcessing(world, (double)(chunk.x * 16), (double)(chunk.z * 16))) {
                     Biome biome = world.getBiome(chunk.getBlock(8, 0, 8));
                     if (isValidBiome(biome)) {
                        eligibleChunksForSpawning.add(chunk);
                     }
                  }
               }
            }
         }
      }

      List<ChunkPos> chunkList = Lists.newArrayList(eligibleChunksForSpawning);
      Collections.shuffle(chunkList);
      BlockPos worldSpawn = world.getSpawnPoint();
      int current = world.countEntities(EntityTumbleweed.class);
      int max = MathHelper.ceil((double)(TumbleweedConfig.maxPerPlayer * eligibleChunksForSpawning.size()) / 289.0D);

      for(ChunkPos chunk : chunkList) {
         if (current > max) {
            break;
         }

         if (world.rand.nextDouble() <= TumbleweedConfig.spawnChance) {
            BlockPos start = getRandomSurfacePosition(world, chunk.x, chunk.z);
            BlockPos spawner = null;

            for(int x = -2; x <= 2; ++x) {
               for(int z = -2; z <= 2; ++z) {
                  BlockPos check = world.getHeight(new BlockPos(start.getX() + x, 0, start.getZ() + z));
                  IBlockState state = world.getBlockState(check);
                  Block block = state.getBlock();
                  TumbleweedConfig.Metadata meta = new TumbleweedConfig.Metadata(block.getRegistryName(), block.getMetaFromState(state));
                  if (TumbleweedConfig.Logic.spawningBlocks.contains(meta) && world.canBlockSeeSky(check)) {
                     spawner = check;
                     break;
                  }
               }
            }

            if (spawner != null) {
               int packSize = 1 + (world.rand.nextFloat() < 0.2F ? 1 : 0);
               int packSpawned = 0;

               for(int i = 0; i < 10; ++i) {
                  int x = spawner.getX() + world.rand.nextInt(5) - world.rand.nextInt(5);
                  int y = spawner.getY() + world.rand.nextInt(2) - world.rand.nextInt(2);
                  int z = spawner.getZ() + world.rand.nextInt(5) - world.rand.nextInt(5);
                  if (isEntityProcessing(world, (double)x, (double)z) && world.isSideSolid(new BlockPos(x, y - 1, z), EnumFacing.UP) && !world.isAnyPlayerWithinRangeAt((double)x, (double)y, (double)z, 32.0D) && worldSpawn.distanceSq((double)x, (double)y, (double)z) >= 576.0D) {
                     EntityTumbleweed entity = new EntityTumbleweed(world);
                     entity.setSize(world.rand.nextInt(5) - 2);
                     entity.setLocationAndAngles((double)x + 0.5D, (double)y + 0.5D + 0.5D * world.rand.nextDouble(), (double)z + 0.5D, 0.0F, 0.0F);
                     if (entity.isNotColliding()) {
                        ++current;
                        ++packSpawned;
                        world.spawnEntity(entity);
                     }

                     if (packSpawned == packSize) {
                        break;
                     }
                  }
               }
            }
         }
      }

   }

   private static boolean isValidBiome(Biome biome) {
      boolean rightType = BiomeDictionary.hasType(biome, Type.DRY) || BiomeDictionary.hasType(biome, Type.SANDY);
      return TumbleweedConfig.Logic.biomeWhitelist.isEmpty() && rightType || TumbleweedConfig.Logic.biomeWhitelist.contains(biome.getRegistryName());
   }

   private static BlockPos getRandomSurfacePosition(World world, int chunkX, int chunkZ) {
      Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
      int x = chunkX * 16 + world.rand.nextInt(14) + 2;
      int z = chunkZ * 16 + world.rand.nextInt(14) + 2;
      int y = chunk.getHeight(new BlockPos(x, 0, z));
      return new BlockPos(x, y, z);
   }

   private static boolean isEntityProcessing(World world, double posX, double posZ) {
      int x = MathHelper.floor(posX);
      int z = MathHelper.floor(posZ);
      return world.isAreaLoaded(new BlockPos(x - 32, 0, z - 32), new BlockPos(x + 32, 0, z + 32));
   }

   @SubscribeEvent
   public void onTick(WorldTickEvent event) {
      WorldServer world = (WorldServer)event.world;
      if (event.phase == Phase.END) {
         if (world.getTotalWorldTime() % 200L == 7L && world.getGameRules().getBoolean("doMobSpawning")) {
            world.profiler.startSection("spawn_tumbleweed");
            trySpawn(world);
            world.profiler.endSection();
         }

      }
   }

   @SubscribeEvent
   public void onCanUpdate(CanUpdate event) {
      Entity entity = event.getEntity();
      World world = entity.getEntityWorld();
      if (!world.isRemote && entity instanceof EntityTumbleweed && entity.ticksExisted > 0 && !isEntityProcessing(world, entity.posX, entity.posZ) && !((EntityTumbleweed)entity).persistent && !entity.isRiding()) {
         world.removeEntity(entity);
      }

   }
}
