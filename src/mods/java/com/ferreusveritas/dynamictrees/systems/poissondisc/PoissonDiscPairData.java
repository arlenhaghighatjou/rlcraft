package com.ferreusveritas.dynamictrees.systems.poissondisc;

public class PoissonDiscPairData {
   private final Vec2i[] coordData;
   private final int sectors;
   private static final Vec2i[][][] coordTable = new Vec2i[7][7][];

   private static void createPairData(int rad1, int rad2, int codeSize, int curveCode, int looseMask) {
      int idx1 = rad1 - 2;
      int idx2 = rad2 - 2;
      int sectors = codeSize * 4;
      Vec2i[] coord = coordTable[idx1][idx2] = coordTable[idx2][idx1] = new Vec2i[sectors];
      byte[] curveData = uncompressCurve(codeSize, (long)curveCode);

      for(int sector = 0; sector < sectors; ++sector) {
         int modulus = Math.abs((sector + codeSize) % (codeSize * 2) - codeSize);
         coord[sector] = new Vec2iPCA((-(sector / codeSize + 1 & 2) + 1) * curveData[codeSize - modulus], (-(sector / codeSize & 2) + 1) * curveData[modulus], (looseMask >> Math.min(modulus - 1, 32) & 1) == 0);
      }

   }

   private static byte[] uncompressCurve(int codeSize, long curveCode) {
      byte[] wave = new byte[codeSize + 2];

      for(int i = 0; i <= codeSize; ++i) {
         wave[i + 1] = (byte)((int)((long)wave[i] + (curveCode >> i & 1L)));
      }

      return wave;
   }

   public PoissonDiscPairData(int rad1, int rad2) {
      int idx1 = rad1 - 2;
      int idx2 = rad2 - 2;
      this.coordData = coordTable[idx1][idx2];
      this.sectors = this.coordData.length;
   }

   public Vec2i getCoords(int sector) {
      return new Vec2i(this.coordData[PoissonDiscMathHelper.wrap(sector, this.sectors)]);
   }

   public int getSector(double actualAngle) {
      int sector = (int)(PoissonDiscMathHelper.radiansToTurns(actualAngle) * (float)this.sectors);
      double smallestDelta = PoissonDiscMathHelper.deltaAngle(actualAngle, this.getCoords(sector).angle());
      boolean runNextDir = true;

      for(int dir = -1; runNextDir && dir <= 1; dir += 2) {
         while(true) {
            double ang = this.getCoords(sector + dir).angle();
            double del = PoissonDiscMathHelper.deltaAngle(actualAngle, ang);
            if (del >= smallestDelta) {
               break;
            }

            smallestDelta = del;
            sector += dir;
            runNextDir = false;
         }
      }

      return sector;
   }

   static {
      createPairData(8, 8, 32, 76895967, 76895376);
      createPairData(8, 7, 30, 19224287, 19223680);
      createPairData(8, 6, 28, 4806367, 4805776);
      createPairData(8, 5, 26, 1223391, 1223312);
      createPairData(8, 4, 24, 305887, 305808);
      createPairData(8, 3, 22, 152943, 152904);
      createPairData(8, 2, 20, 38255, 38216);
      createPairData(7, 7, 28, 4806367, 4805776);
      createPairData(7, 6, 26, 1201887, 1201296);
      createPairData(7, 5, 24, 305887, 305808);
      createPairData(7, 4, 22, 76511, 76432);
      createPairData(7, 3, 20, 38255, 38216);
      createPairData(7, 2, 18, 9583, 9544);
      createPairData(6, 6, 24, 300767, 300176);
      createPairData(6, 5, 22, 76511, 76432);
      createPairData(6, 4, 20, 19167, 19088);
      createPairData(6, 3, 18, 9583, 9544);
      createPairData(6, 2, 16, 2415, 2376);
      createPairData(5, 5, 20, 21855, 21840);
      createPairData(5, 4, 18, 5471, 5456);
      createPairData(5, 3, 16, 2735, 2728);
      createPairData(5, 2, 14, 687, 680);
      createPairData(4, 4, 16, 1375, 1360);
      createPairData(4, 3, 14, 687, 680);
      createPairData(4, 2, 12, 175, 168);
      createPairData(3, 3, 12, 343, 340);
      createPairData(3, 2, 10, 87, 84);
      createPairData(2, 2, 8, 23, 20);
   }
}
