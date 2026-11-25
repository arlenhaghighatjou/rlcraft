package com.ferreusveritas.dynamictrees.systems.poissondisc;

import com.ferreusveritas.dynamictrees.util.SimpleBitmap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PoissonDisc extends Vec2i {
   public int radius;
   public int arc;
   public boolean real;
   private static final SimpleBitmap[] cbm = new SimpleBitmap[9];
   private static final SimpleBitmap[] icbm = new SimpleBitmap[9];

   private static SimpleBitmap circleBitmapGen(int radius, int points) {
      int dim = radius * 2 + 1;
      int top = 0;
      int bot = dim - 1;

      int[] lines;
      int slice;
      for(lines = new int[dim]; top <= bot; lines[top++] = lines[bot--] = bitRun(radius - slice, radius + 1 + slice)) {
         slice = (points >> top * 3 & 7) + 1;
      }

      return new SimpleBitmap(dim, dim, lines);
   }

   private static int bitRun(int start, int stop) {
      return start < stop ? (int)(4294967295L >>> 32 - stop & 4294967295L << start) : (int)(4294967295L >>> 32 - stop | 4294967295L << start);
   }

   private static SimpleBitmap getCircleBitmap(int radius) {
      return cbm[radius];
   }

   private SimpleBitmap getCircleBitmap() {
      return getCircleBitmap(this.radius);
   }

   private static SimpleBitmap getCircleInteriorBitmap(int radius) {
      return icbm[radius];
   }

   private SimpleBitmap getCircleInteriorBitmap() {
      return getCircleInteriorBitmap(this.radius);
   }

   public PoissonDisc() {
      this(0, 0, 2);
   }

   public PoissonDisc(BlockPos pos, int radius) {
      this(pos.getX(), pos.getZ(), radius);
   }

   public PoissonDisc(int x, int z, int radius, boolean real) {
      this(x, z, radius);
      this.real = real;
   }

   public PoissonDisc(int x, int z, int radius) {
      this.set(x, z, radius);
   }

   public PoissonDisc(Vec2i c, int radius) {
      this.set(c.x, c.z, radius);
   }

   public PoissonDisc(PoissonDisc o) {
      this.set(o.x, o.z, o.radius);
      this.arc = o.arc;
   }

   public PoissonDisc set(int x, int z, int radius) {
      return this.set(x, z).setRadius(radius);
   }

   public PoissonDisc set(int x, int z) {
      super.set(x, z);
      return this;
   }

   public PoissonDisc setRadius(int radius) {
      this.radius = MathHelper.clamp(radius, 2, 8);
      return this;
   }

   public boolean isInside(int x, int z) {
      return this.getCircleBitmap().isPixelOn(x - this.x + this.radius, z - this.z + this.radius);
   }

   public boolean isInterior(int x, int z) {
      return this.getCircleInteriorBitmap().isPixelOn(x - this.x + this.radius, z - this.z + this.radius);
   }

   public boolean isEdge(int x, int z) {
      return this.isInside(x, z) && !this.isInterior(x, z);
   }

   public boolean doCirclesIntersect(PoissonDisc other) {
      SimpleBitmap thisbm = this.getCircleBitmap();
      SimpleBitmap otherbm = other.getCircleBitmap();
      int dx = other.x - this.x;
      int dz = other.z - this.z;
      return thisbm.isColliding((thisbm.getW() - otherbm.getW()) / 2 + dx, (thisbm.getH() - otherbm.getH()) / 2 + dz, otherbm);
   }

   public boolean doCirclesIntersectPadding(PoissonDisc other) {
      SimpleBitmap thisbm = this.getCircleBitmap();
      SimpleBitmap otherbm = other.getCircleInteriorBitmap();
      int dx = other.x - this.x;
      int dz = other.z - this.z;
      return thisbm.isColliding((thisbm.getW() - otherbm.getW()) / 2 + dx, (thisbm.getH() - otherbm.getH()) / 2 + dz, otherbm);
   }

   public void maskArc(double startAngle, double endAngle) {
      int start = Math.round(PoissonDiscMathHelper.radiansToTurns(startAngle) * 32.0F) & 31;
      int end = Math.round(PoissonDiscMathHelper.radiansToTurns(endAngle) * 32.0F) & 31;
      this.arc |= bitRun(start, end);
   }

   public void fillArc() {
      this.arc = -1;
   }

   public void setSolved() {
      this.fillArc();
   }

   public void clearArc() {
      this.arc = 0;
   }

   public void edgeMask(int chunkXStart, int chunkZStart) {
      int x = this.x - chunkXStart + 16;
      int z = this.z - chunkZStart + 16;
      if (x >= 0 && z >= 0 && x < 48 && z < 48) {
         byte[] pointMap = new byte[]{6, 4, 12, 2, 0, 13, 3, 11, 9, 0, 0, 0, 0, 0, 0, 0};
         byte points = pointMap[(x >> 4) + (z >> 4) * 3 & 15];
         if (points == 0) {
            double[] adjs = new double[]{31.5D - (double)x, 31.5D - (double)z, (double)x - 15.5D, (double)z - 15.5D};
            double r = (double)(this.radius + 2);
            double offset = 0.0D;

            for(int edge = 0; edge < 4; ++edge) {
               double cos = adjs[edge] / r;
               if (cos < 1.0D) {
                  double angle = Math.acos(cos);
                  this.maskArc(offset - angle, offset + angle);
               }

               ++offset;
            }
         } else {
            double angle1 = Math.atan2(15.5D + (double)((points >> 1 & 1) << 4) - (double)z, 15.5D + (double)((points >> 0 & 1) << 4) - (double)x);
            double angle2 = Math.atan2(15.5D + (double)((points >> 3 & 1) << 4) - (double)z, 15.5D + (double)((points >> 2 & 1) << 4) - (double)x);
            this.maskArc(angle1, angle2);
         }

      } else {
         this.fillArc();
      }
   }

   public int getFreeBitCW() {
      if (this.arc == 0) {
         return (this.x ^ this.z) & 31;
      } else {
         int arc = this.arc;
         int pos = Integer.numberOfTrailingZeros(arc);
         if (pos == 0) {
            pos = Integer.numberOfTrailingZeros(~arc);
            pos = pos + Integer.numberOfTrailingZeros(Integer.rotateRight(arc, pos));
         }

         return pos - 1 & 31;
      }
   }

   public int getFreeBitCCW() {
      if (this.arc == 0) {
         return (this.x ^ this.z) & 31;
      } else {
         int arc = Integer.rotateRight(this.arc, 1);
         int pos = Integer.numberOfLeadingZeros(arc);
         if (pos == 0) {
            pos = Integer.numberOfLeadingZeros(~arc);
            pos = pos + Integer.numberOfLeadingZeros(Integer.rotateLeft(arc, pos));
         }

         return 33 - pos & 31;
      }
   }

   public double bitToAngle(int bit) {
      return (double)bit / 16.0D * 3.141592653589793D;
   }

   public double getFreeAngleCW() {
      return this.bitToAngle(this.getFreeBitCW());
   }

   public double getFreeAngleCCW() {
      return this.bitToAngle(this.getFreeBitCCW());
   }

   public boolean hasFreeAngles() {
      return this.arc != -1;
   }

   public boolean isSolved() {
      return !this.hasFreeAngles();
   }

   public double discPenetration(PoissonDisc o) {
      Vec2i delta = new Vec2i(this.x - o.x, this.z - o.z);
      return delta.len() - (double)(this.radius + o.radius + 1);
   }

   public boolean isInCenterChunk(int chunkXStart, int chunkZStart) {
      return this.x >= chunkXStart && this.z >= chunkZStart && this.x < chunkXStart + 16 && this.z < chunkZStart + 16;
   }

   public String toString() {
      return "Circle x" + this.x + ", z" + this.z + ", r" + this.radius + ", " + (this.real ? "T" : "F") + ", " + Integer.toHexString(this.arc);
   }

   static {
      int[] circledata = new int[]{72, 1160, 14033, 149713, 1497369, 14375705, 134179609};

      for(int r = 2; r <= 8; ++r) {
         SimpleBitmap whole = circleBitmapGen(r, circledata[r - 2]);
         SimpleBitmap inside = new SimpleBitmap(whole.getW(), whole.getH());

         for(int z = 0; z < inside.getH(); ++z) {
            for(int x = 0; x < inside.getW(); ++x) {
               boolean in = whole.isPixelOn(x, z) && whole.isPixelOn(x + 1, z) && whole.isPixelOn(x - 1, z) && whole.isPixelOn(x, z + 1) && whole.isPixelOn(x, z - 1);
               inside.setPixel(x, z, in ? 1 : 0);
            }
         }

         cbm[r] = whole;
         icbm[r] = inside;
      }

      cbm[0] = cbm[1] = cbm[2];
      icbm[0] = icbm[1] = icbm[2];
   }
}
