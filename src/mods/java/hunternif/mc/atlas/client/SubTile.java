package hunternif.mc.atlas.client;

import hunternif.mc.atlas.core.Tile;

public class SubTile {
   public Tile tile;
   public int x;
   public int y;
   public SubTile.Shape shape;
   public SubTile.Part part;

   public SubTile(SubTile.Part part) {
      this.part = part;
   }

   public int getTextureU() {
      switch(this.shape) {
      case SINGLE_OBJECT:
         return this.part.u;
      case CONCAVE:
         return 2 + this.part.u;
      case VERTICAL:
      case CONVEX:
         return this.part.u * 3;
      case HORIZONTAL:
      case FULL:
         return 2 - this.part.u;
      default:
         return 0;
      }
   }

   public int getTextureV() {
      switch(this.shape) {
      case SINGLE_OBJECT:
      case CONCAVE:
         return this.part.v;
      case VERTICAL:
      case FULL:
         return 4 - this.part.v;
      case CONVEX:
      case HORIZONTAL:
         return 2 + this.part.v * 3;
      default:
         return 0;
      }
   }

   public static enum Part {
      TOP_LEFT(0, 0),
      TOP_RIGHT(1, 0),
      BOTTOM_LEFT(0, 1),
      BOTTOM_RIGHT(1, 1);

      final int u;
      final int v;

      private Part(int u, int v) {
         this.u = u;
         this.v = v;
      }
   }

   public static enum Shape {
      CONVEX,
      CONCAVE,
      HORIZONTAL,
      VERTICAL,
      FULL,
      SINGLE_OBJECT;

      private Shape() {
      }
   }
}
