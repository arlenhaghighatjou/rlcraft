package com.ferreusveritas.dynamictrees.systems.poissondisc;

import java.util.Arrays;
import java.util.List;

public class PoissonDiscChunkSet {
   private byte[] discData;
   public boolean generated = false;

   public PoissonDiscChunkSet() {
      this.discData = new byte[16];
   }

   public PoissonDiscChunkSet(byte[] data) {
      this.generated = true;
      this.discData = data != null && data.length == 16 ? Arrays.copyOf(data, 16) : new byte[16];
   }

   public List<PoissonDisc> getDiscs(List<PoissonDisc> discs, int chunkX, int chunkZ) {
      for(int tile = 0; tile < 16; ++tile) {
         byte cd = this.discData[tile];
         if (cd != 0) {
            if ((cd & 128) != 0) {
               int flip = (cd | cd << 1) & 3;
               discs.add(unpackDiscData(tile, 16 ^ flip, chunkX, chunkZ));
               discs.add(unpackDiscData(tile, 31 ^ flip, chunkX, chunkZ));
            } else if ((cd & 112) != 0) {
               discs.add(unpackDiscData(tile, cd, chunkX, chunkZ));
            }
         }
      }

      return discs;
   }

   public void clearDiscs() {
      Arrays.fill(this.discData, (byte)0);
   }

   public List<PoissonDisc> addDiscs(List<PoissonDisc> discs) {
      this.clearDiscs();

      for(PoissonDisc d : discs) {
         this.addDisc(d);
      }

      return discs;
   }

   private static final PoissonDisc unpackDiscData(int tile, int diskData, int chunkX, int chunkZ) {
      int radius = getRadiusFromDiscData(diskData);
      int x = tile << 2 & 12;
      int z = tile & 12;
      return new PoissonDisc(chunkX << 4 | x | diskData >> 2 & 3, chunkZ << 4 | z | diskData & 3, radius, true);
   }

   private static final int getRadiusFromDiscData(int discData) {
      return (discData >> 4 & 7) + 1;
   }

   private static final byte buildDiscData(PoissonDisc c) {
      return (byte)((c.radius - 1 & 7) << 4 | (c.x & 3) << 2 | c.z & 3);
   }

   private static final int calcTileNum(PoissonDisc c) {
      return c.z & 12 | (c.x & 12) >> 2;
   }

   public boolean addDisc(PoissonDisc d) {
      if (d.radius >= 2 && d.radius <= 8) {
         int tile = calcTileNum(d);
         int cd = this.discData[tile];
         if (cd == 0) {
            this.discData[tile] = buildDiscData(d);
            return true;
         }

         if (d.radius == 2 && getRadiusFromDiscData(cd) == 2) {
            int oldDiscPos = cd & 15;
            int newDiscPos = (d.x & 3) << 2 | d.z & 3;
            switch(oldDiscPos << 4 | newDiscPos) {
            case 15:
            case 240:
               this.discData[tile] = -128;
               return true;
            case 60:
            case 195:
               this.discData[tile] = -127;
               return true;
            }
         }
      }

      return false;
   }

   public byte[] getDiscData() {
      return this.discData;
   }

   public void setDiscData(byte[] discData) {
      this.discData = Arrays.copyOf(discData, 16);
   }
}
