package com.ferreusveritas.dynamictrees.cells;

public class CellMetadata {
   public static final int NONE = 0;
   public static final int CONIFERTOP = 1;

   public CellMetadata() {
   }

   public static int radiusAndMeta(int radius, int meta) {
      return radius & 255 | meta << 8;
   }

   public static int getRadius(int radiusMeta) {
      return radiusMeta & 255;
   }

   public static int getMeta(int radiusMeta) {
      return radiusMeta >> 8;
   }
}
