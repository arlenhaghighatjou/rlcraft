package jaredbgreat.dldungeons.pieces.entrances;

import jaredbgreat.dldungeons.builder.RegisteredBlock;
import jaredbgreat.dldungeons.planner.Dungeon;
import net.minecraft.world.World;

public class SimpleEntrance extends AbstractEntrance {
   public SimpleEntrance(int x, int z) {
      super(x, z);
   }

   public void build(Dungeon dungeon, World world) {
      int wx = this.x + dungeon.map.chunkX * 16 - dungeon.map.room.length / 2 + 8;
      int wz = this.z + dungeon.map.chunkZ * 16 - dungeon.map.room.length / 2 + 8;
      int bottom = dungeon.map.floorY[this.x][this.z];

      int top;
      for(top = world.getActualHeight(); !RegisteredBlock.isGroundBlock(world, wx, top, wz); --top) {
         ;
      }

      ++top;
      int side = dungeon.random.nextInt(4);
      switch(side) {
      case 0:
         for(int i = bottom; i <= top; ++i) {
            RegisteredBlock.place(world, wx, i, wz, dungeon.wallBlock1);
            RegisteredBlock.placeBlock(world, wx + 1, i, wz, LADDER, 5, 3);
         }

         return;
      case 1:
         for(int i = bottom; i <= top; ++i) {
            RegisteredBlock.place(world, wx, i, wz, dungeon.wallBlock1);
            RegisteredBlock.placeBlock(world, wx, i, wz + 1, LADDER, 3, 3);
         }

         return;
      case 2:
         for(int i = bottom; i <= top; ++i) {
            RegisteredBlock.place(world, wx, i, wz, dungeon.wallBlock1);
            RegisteredBlock.placeBlock(world, wx - 1, i, wz, LADDER, 4, 3);
         }

         return;
      case 3:
         for(int i = bottom; i <= top; ++i) {
            RegisteredBlock.place(world, wx, i, wz, dungeon.wallBlock1);
            RegisteredBlock.placeBlock(world, wx, i, wz - 1, LADDER, 2, 3);
         }
      }

   }
}
