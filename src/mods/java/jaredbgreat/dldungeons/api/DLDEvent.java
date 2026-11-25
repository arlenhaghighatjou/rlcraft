package jaredbgreat.dldungeons.api;

import jaredbgreat.dldungeons.builder.IBlockPlacer;
import jaredbgreat.dldungeons.planner.Dungeon;
import jaredbgreat.dldungeons.planner.mapping.MapMatrix;
import jaredbgreat.dldungeons.rooms.Room;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DLDEvent extends Event {
   public DLDEvent() {
   }

   @Cancelable
   public static class AddChestBlocksToRoom extends DLDEvent.DungeonRoom {
      public AddChestBlocksToRoom(Dungeon dungeon, Room room) {
         super(dungeon, room);
      }
   }

   @Cancelable
   public static class AddEntrance extends DLDEvent.DungeonRoom {
      public AddEntrance(Dungeon dungeon, Room room) {
         super(dungeon, room);
      }
   }

   @Cancelable
   public static class AddTileEntitiesToRoom extends DLDEvent.DungeonRoom {
      public AddTileEntitiesToRoom(Dungeon dungeon, Room room) {
         super(dungeon, room);
      }
   }

   public static class AfterBuild extends DLDEvent.BeforeBuild {
      public AfterBuild(MapMatrix mapMatrix, int shiftX, int shiftZ, boolean flooded) {
         super(mapMatrix, shiftX, shiftZ, flooded);
      }
   }

   public static class AfterChestTileEntity extends DLDEvent {
      private final World world;
      private final TileEntityChest contents;
      private final int which;
      private final int x;
      private final int y;
      private final int z;
      private final Random random;
      private final int level;

      public AfterChestTileEntity(World world, TileEntityChest contents, int which, int x, int y, int z, Random random, int level) {
         this.world = world;
         this.contents = contents;
         this.which = which;
         this.x = x;
         this.y = y;
         this.z = z;
         this.random = random;
         this.level = level;
      }

      public World getWorld() {
         return this.world;
      }

      public TileEntityChest getContents() {
         return this.contents;
      }

      public int getWhich() {
         return this.which;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getZ() {
         return this.z;
      }

      public Random getRandom() {
         return this.random;
      }

      public int getLevel() {
         return this.level;
      }
   }

   public static class BeforeBuild extends DLDEvent {
      protected final MapMatrix mapMatrix;
      protected final int shiftX;
      protected final int shiftZ;
      protected final boolean flooded;

      public BeforeBuild(MapMatrix mapMatrix, int shiftX, int shiftZ, boolean flooded) {
         this.mapMatrix = mapMatrix;
         this.shiftX = shiftX;
         this.shiftZ = shiftZ;
         this.flooded = flooded;
      }

      public MapMatrix getMapMatrix() {
         return this.mapMatrix;
      }

      public int getShiftX() {
         return this.shiftX;
      }

      public int getShiftZ() {
         return this.shiftZ;
      }

      public boolean isFlooded() {
         return this.flooded;
      }
   }

   @Cancelable
   public static class BeforePlaceSpawner extends DLDEvent {
      private final World world;
      private final BlockPos pos;
      private final String mob;

      public BeforePlaceSpawner(World world, BlockPos pos, String mob) {
         this.world = world;
         this.pos = pos;
         this.mob = mob;
      }

      public World getWorld() {
         return this.world;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public String getMob() {
         return this.mob;
      }
   }

   public static class DungeonRoom extends DLDEvent {
      protected final Dungeon dungeon;
      protected final Room room;

      public DungeonRoom(Dungeon dungeon, Room room) {
         this.dungeon = dungeon;
         this.room = room;
      }

      public Dungeon getDungeon() {
         return this.dungeon;
      }

      public Room getRoom() {
         return this.room;
      }
   }

   @Cancelable
   public abstract static class Place extends DLDEvent {
      protected final World world;
      protected BlockPos pos;

      public Place(World world, BlockPos pos) {
         this.world = world;
         this.pos = pos;
      }

      public World getWorld() {
         return this.world;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public void setPos(BlockPos pos) {
         this.pos = pos;
      }
   }

   @Cancelable
   public static class PlaceBlock extends DLDEvent.Place {
      protected final Block block;

      public PlaceBlock(World world, BlockPos pos, Block block) {
         super(world, pos);
         this.block = block;
      }

      public Block getBlock() {
         return this.block;
      }
   }

   @Cancelable
   public static class PlaceDBlock extends DLDEvent.Place {
      protected final IBlockPlacer block;

      public PlaceDBlock(World world, BlockPos pos, IBlockPlacer block) {
         super(world, pos);
         this.block = block;
      }

      public IBlockPlacer getBlock() {
         return this.block;
      }
   }

   @Cancelable
   public abstract static class PlaceDungeon extends DLDEvent {
      protected final int chunkX;
      protected final int chunkZ;
      protected final World world;

      public PlaceDungeon(int chunkX, int chunkZ, World world) {
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
         this.world = world;
      }

      public int getChunkX() {
         return this.chunkX;
      }

      public int getChunkZ() {
         return this.chunkZ;
      }

      public World getWorld() {
         return this.world;
      }
   }

   @Cancelable
   public static class PlaceDungeonBegin extends DLDEvent.PlaceDungeon {
      public PlaceDungeonBegin(int chunkX, int chunkZ, World world) {
         super(chunkX, chunkZ, world);
      }
   }

   public static class PlaceDungeonFinish extends DLDEvent.PlaceDungeon {
      private final Dungeon dungeon;

      public PlaceDungeonFinish(int chunkX, int chunkZ, World world, Dungeon dungeon) {
         super(chunkX, chunkZ, world);
         this.dungeon = dungeon;
      }

      public Dungeon getDungeon() {
         return this.dungeon;
      }
   }
}
