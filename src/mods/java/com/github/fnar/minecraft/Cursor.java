package com.github.fnar.minecraft;

import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.Direction;

public class Cursor extends Coord {
   private Direction facing;

   public Cursor(int x, int y, int z, Direction facing) {
      this(x, y, z);
      this.facing = facing;
   }

   public Cursor(int x, int y, int z) {
      super(x, y, z);
      this.facing = Direction.NORTH;
   }

   public Cursor(Coord toClone) {
      super(toClone);
      this.facing = Direction.NORTH;
   }

   public Cursor face(Direction direction) {
      this.facing = direction;
      return this;
   }

   public Direction getFacing() {
      return this.facing;
   }

   public Cursor forward() {
      return this.forward(1);
   }

   public Cursor forward(int n) {
      this.translate(this.facing, n);
      return this;
   }
}
