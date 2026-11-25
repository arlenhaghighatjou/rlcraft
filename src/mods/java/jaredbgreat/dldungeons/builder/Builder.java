package jaredbgreat.dldungeons.builder;

import jaredbgreat.dldungeons.DoomlikeDungeons;
import jaredbgreat.dldungeons.api.DLDEvent;
import jaredbgreat.dldungeons.planner.Dungeon;
import jaredbgreat.dldungeons.util.cache.Coords;
import jaredbgreat.dldungeons.util.cache.WeakCache;
import java.util.Arrays;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.IntPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class Builder {
   private static final WeakCache<Dungeon> DUNGEON_CACHE = new WeakCache<Dungeon>();
   private static boolean debugPole = false;

   public Builder() {
   }

   public static void placeDungeon(EaglercraftRandom random, int chunkX, int chunkZ, World world) throws Throwable {
      if (!world.isRemote) {
         if (!MinecraftForge.TERRAIN_GEN_BUS.post(new DLDEvent.PlaceDungeonBegin(chunkX, chunkZ, world))) {
            DoomlikeDungeons.profiler.startTask("Create Dungeons");
            Dungeon dungeon = new Dungeon(world.getBiome(new BlockPos(chunkX * 16, 64, chunkZ * 16)), world, chunkX, chunkZ);
            buildDungeon(dungeon);
            dungeon.preFinalize();
            dungeon = null;
            MinecraftForge.TERRAIN_GEN_BUS.post(new DLDEvent.PlaceDungeonFinish(chunkX, chunkZ, world, dungeon));
            DoomlikeDungeons.profiler.endTask("Create Dungeons");
         }
      }
   }

   public static void placeDungeon(int chunkX, int chunkZ, World world) throws Throwable {
      if (!world.isRemote) {
         if (!MinecraftForge.TERRAIN_GEN_BUS.post(new DLDEvent.PlaceDungeonBegin(chunkX, chunkZ, world))) {
            DoomlikeDungeons.profiler.startTask("Create Dungeons");
            Dungeon dungeon = new Dungeon(world.getBiome(new BlockPos(chunkX * 16, 64, chunkZ * 16)), world, chunkX, chunkZ);
            if (dungeon.theme != null && (dungeon.theme.dimensionWhitelist.length == 0 || Arrays.stream(dungeon.theme.dimensionWhitelist).anyMatch((id) -> {
               return id == world.provider.getDimension();
            }))) {
               if (debugPole) {
                  debuggingPole(world, chunkX, chunkZ, dungeon);
               }

               buildDungeon(dungeon);
            }

            dungeon.preFinalize();
            dungeon = null;
            MinecraftForge.TERRAIN_GEN_BUS.post(new DLDEvent.PlaceDungeonFinish(chunkX, chunkZ, world, dungeon));
            DoomlikeDungeons.profiler.endTask("Create Dungeons");
         }
      }
   }

   public static void buildDungeonChunk(int cx, int cz, Coords dc, World world) throws Throwable {
      Dungeon dungeon = DUNGEON_CACHE.get(dc);
      if (dungeon == null) {
         dungeon = new Dungeon(world, dc);
         DUNGEON_CACHE.add(dungeon);
      }

      if (dungeon != null) {
         dungeon.map.buildInChunk(dungeon, cx, cz);
      }

   }

   public static void buildDungeonsChunk(int cx, int cz, List<Coords> dcs, World world) throws Throwable {
      for(Coords dc : dcs) {
         buildDungeonChunk(cx, cz, dc, world);
      }

   }

   public static void buildDungeon(Dungeon dungeon) {
      if (dungeon.theme != null) {
         dungeon.map.buildByChunks(dungeon);
      }

   }

   public static void debuggingPole(World world, int chunkX, int chunkZ, Dungeon dungeon) {
      int x = chunkX * 16 + 8;
      int z = chunkZ * 16 + 8;

      for(int y = 16; y <= 241; ++y) {
         RegisteredBlock.placeBlock(world, x, y, z, RegisteredBlock.quartz);
      }

      for(int i = -dungeon.size.radius; i <= dungeon.size.radius; ++i) {
         RegisteredBlock.placeBlock(world, x - dungeon.size.radius, 80, z + i, RegisteredBlock.lapis);
         RegisteredBlock.placeBlock(world, x + dungeon.size.radius, 80, z + i, RegisteredBlock.lapis);
         RegisteredBlock.placeBlock(world, x + i, 80, z - dungeon.size.radius, RegisteredBlock.lapis);
         RegisteredBlock.placeBlock(world, x + i, 80, z + dungeon.size.radius, RegisteredBlock.lapis);
      }

   }

   public static void setDebugPole(boolean val) {
      debugPole = val;
   }
}
