package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSpawning;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class SRPWorldEntitySpawner {
   private static final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();

   public SRPWorldEntitySpawner() {
   }

   public static int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
      if (!spawnHostileMobs && !spawnPeacefulMobs) {
         return 0;
      } else {
         eligibleChunksForSpawning.clear();
         int i = 0;

         for(EntityPlayer entityplayer : worldServerIn.playerEntities) {
            if (!entityplayer.isSpectator()) {
               int j = MathHelper.floor(entityplayer.posX / 16.0D);
               int k = MathHelper.floor(entityplayer.posZ / 16.0D);
               int l = 8;

               for(int i1 = -8; i1 <= 8; ++i1) {
                  for(int j1 = -8; j1 <= 8; ++j1) {
                     boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                     ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                     if (!eligibleChunksForSpawning.contains(chunkpos)) {
                        ++i;
                        if (!flag && worldServerIn.getWorldBorder().contains(chunkpos)) {
                           PlayerChunkMapEntry playerchunkmapentry = worldServerIn.getPlayerChunkMap().getEntry(chunkpos.x, chunkpos.z);
                           if (playerchunkmapentry != null && playerchunkmapentry.isSentToPlayers()) {
                              eligibleChunksForSpawning.add(chunkpos);
                           }
                        }
                     }
                  }
               }
            }
         }

         int j4 = 0;
         BlockPos blockpos1 = worldServerIn.getSpawnPoint();
         List<Entity> serverList = worldServerIn.loadedEntityList;
         int count = 0;

         for(int x = 0; x < serverList.size(); ++x) {
            if (serverList.get(x) instanceof EntityParasiteBase) {
               ++count;
            }
         }

         if (count <= SRPConfig.worldMobCap) {
            ArrayList<ChunkPos> shuffled = Lists.newArrayList(eligibleChunksForSpawning);
            Collections.shuffle(shuffled);
            MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

            label125:
            for(ChunkPos chunkpos1 : shuffled) {
               BlockPos blockpos = getRandomChunkPosition(worldServerIn, chunkpos1.x, chunkpos1.z);
               int k1 = blockpos.getX();
               int l1 = blockpos.getY();
               int i2 = blockpos.getZ();
               IBlockState iblockstate = worldServerIn.getBlockState(blockpos);
               if (!iblockstate.isNormalCube()) {
                  int j2 = 0;

                  for(int k2 = 0; k2 < 3; ++k2) {
                     int l2 = k1;
                     int i3 = l1;
                     int j3 = i2;
                     int k3 = 6;
                     SpawnListEntry biome$spawnlistentry = null;
                     IEntityLivingData ientitylivingdata = null;
                     int l3 = MathHelper.ceil(Math.random() * 4.0D);

                     for(int i4 = 0; i4 < l3; ++i4) {
                        l2 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6);
                        i3 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1);
                        j3 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6);
                        blockpos$mutableblockpos.setPos(l2, i3, j3);
                        float f = (float)l2 + 0.5F;
                        float f1 = (float)j3 + 0.5F;
                        if (!worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0D) && blockpos1.distanceSq((double)f, (double)i3, (double)f1) >= 576.0D) {
                           if (biome$spawnlistentry == null) {
                              biome$spawnlistentry = getSpawnListEntryForTypeAt(worldServerIn, blockpos$mutableblockpos);
                              if (biome$spawnlistentry == null) {
                                 break;
                              }
                           }

                           if (canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(biome$spawnlistentry.entityClass), worldServerIn, blockpos$mutableblockpos)) {
                              EntityParasiteBase entityliving;
                              try {
                                 entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance(worldServerIn);
                                 entityliving.canSpawnSpawn = true;
                              } catch (Exception var32) {
                                 return j4;
                              }

                              entityliving.setLocationAndAngles((double)f, (double)i3, (double)f1, worldServerIn.rand.nextFloat() * 360.0F, 0.0F);
                              Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, worldServerIn, f, (float)i3, f1, false);
                              if (canSpawn == Result.ALLOW || canSpawn == Result.DEFAULT && entityliving.getCanSpawnHere() && entityliving.isNotColliding()) {
                                 if (!ForgeEventFactory.doSpecialSpawn(entityliving, worldServerIn, f, (float)i3, f1)) {
                                    ientitylivingdata = entityliving.onInitialSpawn(worldServerIn.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata);
                                 }

                                 if (entityliving.isNotColliding()) {
                                    ++j2;
                                    worldServerIn.spawnEntity(entityliving);
                                 } else {
                                    entityliving.setDead();
                                 }

                                 if (j2 >= ForgeEventFactory.getMaxSpawnPackSize(entityliving)) {
                                    continue label125;
                                 }
                              }

                              j4 += j2;
                           }
                        }
                     }
                  }
               }
            }
         }

         return j4;
      }
   }

   private static BlockPos getRandomChunkPosition(World worldIn, int x, int z) {
      Chunk chunk = worldIn.getChunkFromChunkCoords(x, z);
      int i = x * 16 + worldIn.rand.nextInt(16);
      int j = z * 16 + worldIn.rand.nextInt(16);
      int k = MathHelper.roundUp(chunk.getHeight(new BlockPos(i, 0, j)) + 1, 16);
      int l = worldIn.rand.nextInt(Math.max(1, k > 0 ? k : chunk.getTopFilledSegment() + 16 - 1));
      return new BlockPos(i, l, j);
   }

   @Nullable
   public static SpawnListEntry getSpawnListEntryForTypeAt(WorldServer worldServerIn, BlockPos pos) {
      List<SpawnListEntry> list = SRPSpawning.getSpawns(SRPWorldData.get(worldServerIn).getEvolutionPhase());
      return list != null && !list.isEmpty() ? (SpawnListEntry)WeightedRandom.getRandomItem(new EaglercraftRandom(), list) : null;
   }

   public static boolean canCreatureTypeSpawnAtLocation(SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
      return !worldIn.getWorldBorder().contains(pos) ? false : spawnPlacementTypeIn.canSpawnAt(worldIn, pos);
   }
}
