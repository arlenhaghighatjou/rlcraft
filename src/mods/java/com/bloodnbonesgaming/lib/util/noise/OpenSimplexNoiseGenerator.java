package com.bloodnbonesgaming.lib.util.noise;

public class OpenSimplexNoiseGenerator {
   private static final double STRETCH_CONSTANT_2D = -0.211324865405187D;
   private static final double SQUISH_CONSTANT_2D = 0.366025403784439D;
   private static final double STRETCH_CONSTANT_3D = -0.16666666666666666D;
   private static final double SQUISH_CONSTANT_3D = 0.3333333333333333D;
   private static final double STRETCH_CONSTANT_4D = -0.138196601125011D;
   private static final double SQUISH_CONSTANT_4D = 0.309016994374947D;
   private static final double NORM_CONSTANT_2D = 47.0D;
   private static final double NORM_CONSTANT_3D = 103.0D;
   private static final double NORM_CONSTANT_4D = 30.0D;
   private static final long DEFAULT_SEED = 0L;
   private short[] perm;
   private short[] permGradIndex3D;
   private static byte[] gradients2D = new byte[]{5, 2, 2, 5, -5, 2, -2, 5, 5, -2, 2, -5, -5, -2, -2, -5};
   private static byte[] gradients3D = new byte[]{-11, 4, 4, -4, 11, 4, -4, 4, 11, 11, 4, 4, 4, 11, 4, 4, 4, 11, -11, -4, 4, -4, -11, 4, -4, -4, 11, 11, -4, 4, 4, -11, 4, 4, -4, 11, -11, 4, -4, -4, 11, -4, -4, 4, -11, 11, 4, -4, 4, 11, -4, 4, 4, -11, -11, -4, -4, -4, -11, -4, -4, -4, -11, 11, -4, -4, 4, -11, -4, 4, -4, -11};
   private static byte[] gradients4D = new byte[]{3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, -3, 1, 1, 1, -1, 3, 1, 1, -1, 1, 3, 1, -1, 1, 1, 3, 3, -1, 1, 1, 1, -3, 1, 1, 1, -1, 3, 1, 1, -1, 1, 3, -3, -1, 1, 1, -1, -3, 1, 1, -1, -1, 3, 1, -1, -1, 1, 3, 3, 1, -1, 1, 1, 3, -1, 1, 1, 1, -3, 1, 1, 1, -1, 3, -3, 1, -1, 1, -1, 3, -1, 1, -1, 1, -3, 1, -1, 1, -1, 3, 3, -1, -1, 1, 1, -3, -1, 1, 1, -1, -3, 1, 1, -1, -1, 3, -3, -1, -1, 1, -1, -3, -1, 1, -1, -1, -3, 1, -1, -1, -1, 3, 3, 1, 1, -1, 1, 3, 1, -1, 1, 1, 3, -1, 1, 1, 1, -3, -3, 1, 1, -1, -1, 3, 1, -1, -1, 1, 3, -1, -1, 1, 1, -3, 3, -1, 1, -1, 1, -3, 1, -1, 1, -1, 3, -1, 1, -1, 1, -3, -3, -1, 1, -1, -1, -3, 1, -1, -1, -1, 3, -1, -1, -1, 1, -3, 3, 1, -1, -1, 1, 3, -1, -1, 1, 1, -3, -1, 1, 1, -1, -3, -3, 1, -1, -1, -1, 3, -1, -1, -1, 1, -3, -1, -1, 1, -1, -3, 3, -1, -1, -1, 1, -3, -1, -1, 1, -1, -3, -1, 1, -1, -1, -3, -3, -1, -1, -1, -1, -3, -1, -1, -1, -1, -3, -1, -1, -1, -1, -3};

   public OpenSimplexNoiseGenerator() {
      this(0L);
   }

   public OpenSimplexNoiseGenerator(short[] perm) {
      this.perm = perm;
      this.permGradIndex3D = new short[256];

      for(int i = 0; i < 256; ++i) {
         this.permGradIndex3D[i] = (short)(perm[i] % (gradients3D.length / 3) * 3);
      }

   }

   public OpenSimplexNoiseGenerator(long seed) {
      this.perm = new short[256];
      this.permGradIndex3D = new short[256];
      short[] source = new short[256];

      for(short i = 0; i < 256; source[i] = i++) {
         ;
      }

      seed = seed * 6364136223846793005L + 1442695040888963407L;
      seed = seed * 6364136223846793005L + 1442695040888963407L;
      seed = seed * 6364136223846793005L + 1442695040888963407L;

      for(int i = 255; i >= 0; --i) {
         seed = seed * 6364136223846793005L + 1442695040888963407L;
         int r = (int)((seed + 31L) % (long)(i + 1));
         if (r < 0) {
            r += i + 1;
         }

         this.perm[i] = source[r];
         this.permGradIndex3D[i] = (short)(this.perm[i] % (gradients3D.length / 3) * 3);
         source[r] = source[i];
      }

   }

   public double eval(double x, double y) {
      double stretchOffset = (x + y) * -0.211324865405187D;
      double xs = x + stretchOffset;
      double ys = y + stretchOffset;
      int xsb = fastFloor(xs);
      int ysb = fastFloor(ys);
      double squishOffset = (double)(xsb + ysb) * 0.366025403784439D;
      double xb = (double)xsb + squishOffset;
      double yb = (double)ysb + squishOffset;
      double xins = xs - (double)xsb;
      double yins = ys - (double)ysb;
      double inSum = xins + yins;
      double dx0 = x - xb;
      double dy0 = y - yb;
      double value = 0.0D;
      double dx1 = dx0 - 1.0D - 0.366025403784439D;
      double dy1 = dy0 - 0.0D - 0.366025403784439D;
      double attn1 = 2.0D - dx1 * dx1 - dy1 * dy1;
      if (attn1 > 0.0D) {
         attn1 = attn1 * attn1;
         value += attn1 * attn1 * this.extrapolate(xsb + 1, ysb + 0, dx1, dy1);
      }

      double dx2 = dx0 - 0.0D - 0.366025403784439D;
      double dy2 = dy0 - 1.0D - 0.366025403784439D;
      double attn2 = 2.0D - dx2 * dx2 - dy2 * dy2;
      if (attn2 > 0.0D) {
         attn2 = attn2 * attn2;
         value += attn2 * attn2 * this.extrapolate(xsb + 0, ysb + 1, dx2, dy2);
      }

      double dx_ext;
      double dy_ext;
      int xsv_ext;
      int ysv_ext;
      if (inSum <= 1.0D) {
         double zins = 1.0D - inSum;
         if (zins <= xins && zins <= yins) {
            xsv_ext = xsb + 1;
            ysv_ext = ysb + 1;
            dx_ext = dx0 - 1.0D - 0.732050807568878D;
            dy_ext = dy0 - 1.0D - 0.732050807568878D;
         } else if (xins > yins) {
            xsv_ext = xsb + 1;
            ysv_ext = ysb - 1;
            dx_ext = dx0 - 1.0D;
            dy_ext = dy0 + 1.0D;
         } else {
            xsv_ext = xsb - 1;
            ysv_ext = ysb + 1;
            dx_ext = dx0 + 1.0D;
            dy_ext = dy0 - 1.0D;
         }
      } else {
         double zins = 2.0D - inSum;
         if (zins >= xins && zins >= yins) {
            dx_ext = dx0;
            dy_ext = dy0;
            xsv_ext = xsb;
            ysv_ext = ysb;
         } else if (xins > yins) {
            xsv_ext = xsb + 2;
            ysv_ext = ysb + 0;
            dx_ext = dx0 - 2.0D - 0.732050807568878D;
            dy_ext = dy0 + 0.0D - 0.732050807568878D;
         } else {
            xsv_ext = xsb + 0;
            ysv_ext = ysb + 2;
            dx_ext = dx0 + 0.0D - 0.732050807568878D;
            dy_ext = dy0 - 2.0D - 0.732050807568878D;
         }

         ++xsb;
         ++ysb;
         dx0 = dx0 - 1.0D - 0.732050807568878D;
         dy0 = dy0 - 1.0D - 0.732050807568878D;
      }

      double attn0 = 2.0D - dx0 * dx0 - dy0 * dy0;
      if (attn0 > 0.0D) {
         attn0 = attn0 * attn0;
         value += attn0 * attn0 * this.extrapolate(xsb, ysb, dx0, dy0);
      }

      double attn_ext = 2.0D - dx_ext * dx_ext - dy_ext * dy_ext;
      if (attn_ext > 0.0D) {
         attn_ext = attn_ext * attn_ext;
         value += attn_ext * attn_ext * this.extrapolate(xsv_ext, ysv_ext, dx_ext, dy_ext);
      }

      return value / 47.0D;
   }

   public double eval(double x, double y, double z) {
      double stretchOffset = (x + y + z) * -0.16666666666666666D;
      double xs = x + stretchOffset;
      double ys = y + stretchOffset;
      double zs = z + stretchOffset;
      int xsb = fastFloor(xs);
      int ysb = fastFloor(ys);
      int zsb = fastFloor(zs);
      double squishOffset = (double)(xsb + ysb + zsb) * 0.3333333333333333D;
      double xb = (double)xsb + squishOffset;
      double yb = (double)ysb + squishOffset;
      double zb = (double)zsb + squishOffset;
      double xins = xs - (double)xsb;
      double yins = ys - (double)ysb;
      double zins = zs - (double)zsb;
      double inSum = xins + yins + zins;
      double dx0 = x - xb;
      double dy0 = y - yb;
      double dz0 = z - zb;
      double value = 0.0D;
      double dx_ext0;
      double dy_ext0;
      double dz_ext0;
      double dx_ext1;
      double dy_ext1;
      double dz_ext1;
      int xsv_ext0;
      int ysv_ext0;
      int zsv_ext0;
      int xsv_ext1;
      int ysv_ext1;
      int zsv_ext1;
      if (inSum <= 1.0D) {
         byte aPoint = 1;
         double aScore = xins;
         byte bPoint = 2;
         double bScore = yins;
         if (xins >= yins && zins > yins) {
            bScore = zins;
            bPoint = 4;
         } else if (xins < yins && zins > xins) {
            aScore = zins;
            aPoint = 4;
         }

         double wins = 1.0D - inSum;
         if (wins <= aScore && wins <= bScore) {
            byte c = (byte)(aPoint | bPoint);
            if ((c & 1) == 0) {
               xsv_ext0 = xsb;
               xsv_ext1 = xsb - 1;
               dx_ext0 = dx0 - 0.6666666666666666D;
               dx_ext1 = dx0 + 1.0D - 0.3333333333333333D;
            } else {
               xsv_ext0 = xsv_ext1 = xsb + 1;
               dx_ext0 = dx0 - 1.0D - 0.6666666666666666D;
               dx_ext1 = dx0 - 1.0D - 0.3333333333333333D;
            }

            if ((c & 2) == 0) {
               ysv_ext0 = ysb;
               ysv_ext1 = ysb - 1;
               dy_ext0 = dy0 - 0.6666666666666666D;
               dy_ext1 = dy0 + 1.0D - 0.3333333333333333D;
            } else {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy0 - 1.0D - 0.6666666666666666D;
               dy_ext1 = dy0 - 1.0D - 0.3333333333333333D;
            }

            if ((c & 4) == 0) {
               zsv_ext0 = zsb;
               zsv_ext1 = zsb - 1;
               dz_ext0 = dz0 - 0.6666666666666666D;
               dz_ext1 = dz0 + 1.0D - 0.3333333333333333D;
            } else {
               zsv_ext0 = zsv_ext1 = zsb + 1;
               dz_ext0 = dz0 - 1.0D - 0.6666666666666666D;
               dz_ext1 = dz0 - 1.0D - 0.3333333333333333D;
            }
         } else {
            byte c = bScore > aScore ? bPoint : aPoint;
            if ((c & 1) == 0) {
               xsv_ext0 = xsb - 1;
               xsv_ext1 = xsb;
               dx_ext0 = dx0 + 1.0D;
               dx_ext1 = dx0;
            } else {
               xsv_ext0 = xsv_ext1 = xsb + 1;
               dx_ext0 = dx_ext1 = dx0 - 1.0D;
            }

            if ((c & 2) == 0) {
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext1 = dy0;
               dy_ext0 = dy0;
               if ((c & 1) == 0) {
                  ysv_ext1 = ysb - 1;
                  dy_ext1 = dy0 + 1.0D;
               } else {
                  ysv_ext0 = ysb - 1;
                  dy_ext0 = dy0 + 1.0D;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy_ext1 = dy0 - 1.0D;
            }

            if ((c & 4) == 0) {
               zsv_ext0 = zsb;
               zsv_ext1 = zsb - 1;
               dz_ext0 = dz0;
               dz_ext1 = dz0 + 1.0D;
            } else {
               zsv_ext0 = zsv_ext1 = zsb + 1;
               dz_ext0 = dz_ext1 = dz0 - 1.0D;
            }
         }

         double attn0 = 2.0D - dx0 * dx0 - dy0 * dy0 - dz0 * dz0;
         if (attn0 > 0.0D) {
            attn0 = attn0 * attn0;
            value += attn0 * attn0 * this.extrapolate(xsb + 0, ysb + 0, zsb + 0, dx0, dy0, dz0);
         }

         double dx1 = dx0 - 1.0D - 0.3333333333333333D;
         double dy1 = dy0 - 0.0D - 0.3333333333333333D;
         double dz1 = dz0 - 0.0D - 0.3333333333333333D;
         double attn1 = 2.0D - dx1 * dx1 - dy1 * dy1 - dz1 * dz1;
         if (attn1 > 0.0D) {
            attn1 = attn1 * attn1;
            value += attn1 * attn1 * this.extrapolate(xsb + 1, ysb + 0, zsb + 0, dx1, dy1, dz1);
         }

         double dx2 = dx0 - 0.0D - 0.3333333333333333D;
         double dy2 = dy0 - 1.0D - 0.3333333333333333D;
         double attn2 = 2.0D - dx2 * dx2 - dy2 * dy2 - dz1 * dz1;
         if (attn2 > 0.0D) {
            attn2 = attn2 * attn2;
            value += attn2 * attn2 * this.extrapolate(xsb + 0, ysb + 1, zsb + 0, dx2, dy2, dz1);
         }

         double dz3 = dz0 - 1.0D - 0.3333333333333333D;
         double attn3 = 2.0D - dx2 * dx2 - dy1 * dy1 - dz3 * dz3;
         if (attn3 > 0.0D) {
            attn3 = attn3 * attn3;
            value += attn3 * attn3 * this.extrapolate(xsb + 0, ysb + 0, zsb + 1, dx2, dy1, dz3);
         }
      } else if (inSum >= 2.0D) {
         byte aPoint = 6;
         double aScore = xins;
         byte bPoint = 5;
         double bScore = yins;
         if (xins <= yins && zins < yins) {
            bScore = zins;
            bPoint = 3;
         } else if (xins > yins && zins < xins) {
            aScore = zins;
            aPoint = 3;
         }

         double wins = 3.0D - inSum;
         if (wins >= aScore && wins >= bScore) {
            byte c = (byte)(aPoint & bPoint);
            if ((c & 1) != 0) {
               xsv_ext0 = xsb + 1;
               xsv_ext1 = xsb + 2;
               dx_ext0 = dx0 - 1.0D - 0.3333333333333333D;
               dx_ext1 = dx0 - 2.0D - 0.6666666666666666D;
            } else {
               xsv_ext1 = xsb;
               xsv_ext0 = xsb;
               dx_ext0 = dx0 - 0.3333333333333333D;
               dx_ext1 = dx0 - 0.6666666666666666D;
            }

            if ((c & 2) != 0) {
               ysv_ext0 = ysb + 1;
               ysv_ext1 = ysb + 2;
               dy_ext0 = dy0 - 1.0D - 0.3333333333333333D;
               dy_ext1 = dy0 - 2.0D - 0.6666666666666666D;
            } else {
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext0 = dy0 - 0.3333333333333333D;
               dy_ext1 = dy0 - 0.6666666666666666D;
            }

            if ((c & 4) != 0) {
               zsv_ext0 = zsb + 1;
               zsv_ext1 = zsb + 2;
               dz_ext0 = dz0 - 1.0D - 0.3333333333333333D;
               dz_ext1 = dz0 - 2.0D - 0.6666666666666666D;
            } else {
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               dz_ext0 = dz0 - 0.3333333333333333D;
               dz_ext1 = dz0 - 0.6666666666666666D;
            }
         } else {
            byte c = bScore < aScore ? bPoint : aPoint;
            if ((c & 1) != 0) {
               xsv_ext0 = xsb + 2;
               xsv_ext1 = xsb + 1;
               dx_ext0 = dx0 - 2.0D - 1.0D;
               dx_ext1 = dx0 - 1.0D - 1.0D;
            } else {
               xsv_ext1 = xsb;
               xsv_ext0 = xsb;
               dx_ext0 = dx_ext1 = dx0 - 1.0D;
            }

            if ((c & 2) != 0) {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy_ext1 = dy0 - 1.0D - 1.0D;
               if ((c & 1) != 0) {
                  ++ysv_ext1;
                  --dy_ext1;
               } else {
                  ++ysv_ext0;
                  --dy_ext0;
               }
            } else {
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext0 = dy_ext1 = dy0 - 1.0D;
            }

            if ((c & 4) != 0) {
               zsv_ext0 = zsb + 1;
               zsv_ext1 = zsb + 2;
               dz_ext0 = dz0 - 1.0D - 1.0D;
               dz_ext1 = dz0 - 2.0D - 1.0D;
            } else {
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               dz_ext0 = dz_ext1 = dz0 - 1.0D;
            }
         }

         double dx3 = dx0 - 1.0D - 0.6666666666666666D;
         double dy3 = dy0 - 1.0D - 0.6666666666666666D;
         double dz3 = dz0 - 0.0D - 0.6666666666666666D;
         double attn3 = 2.0D - dx3 * dx3 - dy3 * dy3 - dz3 * dz3;
         if (attn3 > 0.0D) {
            attn3 = attn3 * attn3;
            value += attn3 * attn3 * this.extrapolate(xsb + 1, ysb + 1, zsb + 0, dx3, dy3, dz3);
         }

         double dy2 = dy0 - 0.0D - 0.6666666666666666D;
         double dz2 = dz0 - 1.0D - 0.6666666666666666D;
         double attn2 = 2.0D - dx3 * dx3 - dy2 * dy2 - dz2 * dz2;
         if (attn2 > 0.0D) {
            attn2 = attn2 * attn2;
            value += attn2 * attn2 * this.extrapolate(xsb + 1, ysb + 0, zsb + 1, dx3, dy2, dz2);
         }

         double dx1 = dx0 - 0.0D - 0.6666666666666666D;
         double attn1 = 2.0D - dx1 * dx1 - dy3 * dy3 - dz2 * dz2;
         if (attn1 > 0.0D) {
            attn1 = attn1 * attn1;
            value += attn1 * attn1 * this.extrapolate(xsb + 0, ysb + 1, zsb + 1, dx1, dy3, dz2);
         }

         dx0 = dx0 - 1.0D - 1.0D;
         dy0 = dy0 - 1.0D - 1.0D;
         dz0 = dz0 - 1.0D - 1.0D;
         double attn0 = 2.0D - dx0 * dx0 - dy0 * dy0 - dz0 * dz0;
         if (attn0 > 0.0D) {
            attn0 = attn0 * attn0;
            value += attn0 * attn0 * this.extrapolate(xsb + 1, ysb + 1, zsb + 1, dx0, dy0, dz0);
         }
      } else {
         double p1 = xins + yins;
         byte aPoint;
         double aScore;
         boolean aIsFurtherSide;
         if (p1 > 1.0D) {
            aScore = p1 - 1.0D;
            aPoint = 3;
            aIsFurtherSide = true;
         } else {
            aScore = 1.0D - p1;
            aPoint = 4;
            aIsFurtherSide = false;
         }

         double p2 = xins + zins;
         boolean bIsFurtherSide;
         double bScore;
         byte bPoint;
         if (p2 > 1.0D) {
            bScore = p2 - 1.0D;
            bPoint = 5;
            bIsFurtherSide = true;
         } else {
            bScore = 1.0D - p2;
            bPoint = 2;
            bIsFurtherSide = false;
         }

         double p3 = yins + zins;
         if (p3 > 1.0D) {
            double score = p3 - 1.0D;
            if (aScore <= bScore && aScore < score) {
               aPoint = 6;
               aIsFurtherSide = true;
            } else if (aScore > bScore && bScore < score) {
               bPoint = 6;
               bIsFurtherSide = true;
            }
         } else {
            double score = 1.0D - p3;
            if (aScore <= bScore && aScore < score) {
               aPoint = 1;
               aIsFurtherSide = false;
            } else if (aScore > bScore && bScore < score) {
               bPoint = 1;
               bIsFurtherSide = false;
            }
         }

         if (aIsFurtherSide == bIsFurtherSide) {
            if (aIsFurtherSide) {
               dx_ext0 = dx0 - 1.0D - 1.0D;
               dy_ext0 = dy0 - 1.0D - 1.0D;
               dz_ext0 = dz0 - 1.0D - 1.0D;
               xsv_ext0 = xsb + 1;
               ysv_ext0 = ysb + 1;
               zsv_ext0 = zsb + 1;
               byte c = (byte)(aPoint & bPoint);
               if ((c & 1) != 0) {
                  dx_ext1 = dx0 - 2.0D - 0.6666666666666666D;
                  dy_ext1 = dy0 - 0.6666666666666666D;
                  dz_ext1 = dz0 - 0.6666666666666666D;
                  xsv_ext1 = xsb + 2;
                  ysv_ext1 = ysb;
                  zsv_ext1 = zsb;
               } else if ((c & 2) != 0) {
                  dx_ext1 = dx0 - 0.6666666666666666D;
                  dy_ext1 = dy0 - 2.0D - 0.6666666666666666D;
                  dz_ext1 = dz0 - 0.6666666666666666D;
                  xsv_ext1 = xsb;
                  ysv_ext1 = ysb + 2;
                  zsv_ext1 = zsb;
               } else {
                  dx_ext1 = dx0 - 0.6666666666666666D;
                  dy_ext1 = dy0 - 0.6666666666666666D;
                  dz_ext1 = dz0 - 2.0D - 0.6666666666666666D;
                  xsv_ext1 = xsb;
                  ysv_ext1 = ysb;
                  zsv_ext1 = zsb + 2;
               }
            } else {
               dx_ext0 = dx0;
               dy_ext0 = dy0;
               dz_ext0 = dz0;
               xsv_ext0 = xsb;
               ysv_ext0 = ysb;
               zsv_ext0 = zsb;
               byte c = (byte)(aPoint | bPoint);
               if ((c & 1) == 0) {
                  dx_ext1 = dx0 + 1.0D - 0.3333333333333333D;
                  dy_ext1 = dy0 - 1.0D - 0.3333333333333333D;
                  dz_ext1 = dz0 - 1.0D - 0.3333333333333333D;
                  xsv_ext1 = xsb - 1;
                  ysv_ext1 = ysb + 1;
                  zsv_ext1 = zsb + 1;
               } else if ((c & 2) == 0) {
                  dx_ext1 = dx0 - 1.0D - 0.3333333333333333D;
                  dy_ext1 = dy0 + 1.0D - 0.3333333333333333D;
                  dz_ext1 = dz0 - 1.0D - 0.3333333333333333D;
                  xsv_ext1 = xsb + 1;
                  ysv_ext1 = ysb - 1;
                  zsv_ext1 = zsb + 1;
               } else {
                  dx_ext1 = dx0 - 1.0D - 0.3333333333333333D;
                  dy_ext1 = dy0 - 1.0D - 0.3333333333333333D;
                  dz_ext1 = dz0 + 1.0D - 0.3333333333333333D;
                  xsv_ext1 = xsb + 1;
                  ysv_ext1 = ysb + 1;
                  zsv_ext1 = zsb - 1;
               }
            }
         } else {
            byte c2;
            byte c1;
            if (aIsFurtherSide) {
               c1 = aPoint;
               c2 = bPoint;
            } else {
               c1 = bPoint;
               c2 = aPoint;
            }

            if ((c1 & 1) == 0) {
               dx_ext0 = dx0 + 1.0D - 0.3333333333333333D;
               dy_ext0 = dy0 - 1.0D - 0.3333333333333333D;
               dz_ext0 = dz0 - 1.0D - 0.3333333333333333D;
               xsv_ext0 = xsb - 1;
               ysv_ext0 = ysb + 1;
               zsv_ext0 = zsb + 1;
            } else if ((c1 & 2) == 0) {
               dx_ext0 = dx0 - 1.0D - 0.3333333333333333D;
               dy_ext0 = dy0 + 1.0D - 0.3333333333333333D;
               dz_ext0 = dz0 - 1.0D - 0.3333333333333333D;
               xsv_ext0 = xsb + 1;
               ysv_ext0 = ysb - 1;
               zsv_ext0 = zsb + 1;
            } else {
               dx_ext0 = dx0 - 1.0D - 0.3333333333333333D;
               dy_ext0 = dy0 - 1.0D - 0.3333333333333333D;
               dz_ext0 = dz0 + 1.0D - 0.3333333333333333D;
               xsv_ext0 = xsb + 1;
               ysv_ext0 = ysb + 1;
               zsv_ext0 = zsb - 1;
            }

            dx_ext1 = dx0 - 0.6666666666666666D;
            dy_ext1 = dy0 - 0.6666666666666666D;
            dz_ext1 = dz0 - 0.6666666666666666D;
            xsv_ext1 = xsb;
            ysv_ext1 = ysb;
            zsv_ext1 = zsb;
            if ((c2 & 1) != 0) {
               dx_ext1 -= 2.0D;
               xsv_ext1 = xsb + 2;
            } else if ((c2 & 2) != 0) {
               dy_ext1 -= 2.0D;
               ysv_ext1 = ysb + 2;
            } else {
               dz_ext1 -= 2.0D;
               zsv_ext1 = zsb + 2;
            }
         }

         double dx1 = dx0 - 1.0D - 0.3333333333333333D;
         double dy1 = dy0 - 0.0D - 0.3333333333333333D;
         double dz1 = dz0 - 0.0D - 0.3333333333333333D;
         double attn1 = 2.0D - dx1 * dx1 - dy1 * dy1 - dz1 * dz1;
         if (attn1 > 0.0D) {
            attn1 = attn1 * attn1;
            value += attn1 * attn1 * this.extrapolate(xsb + 1, ysb + 0, zsb + 0, dx1, dy1, dz1);
         }

         double dx2 = dx0 - 0.0D - 0.3333333333333333D;
         double dy2 = dy0 - 1.0D - 0.3333333333333333D;
         double attn2 = 2.0D - dx2 * dx2 - dy2 * dy2 - dz1 * dz1;
         if (attn2 > 0.0D) {
            attn2 = attn2 * attn2;
            value += attn2 * attn2 * this.extrapolate(xsb + 0, ysb + 1, zsb + 0, dx2, dy2, dz1);
         }

         double dz3 = dz0 - 1.0D - 0.3333333333333333D;
         double attn3 = 2.0D - dx2 * dx2 - dy1 * dy1 - dz3 * dz3;
         if (attn3 > 0.0D) {
            attn3 = attn3 * attn3;
            value += attn3 * attn3 * this.extrapolate(xsb + 0, ysb + 0, zsb + 1, dx2, dy1, dz3);
         }

         double dx4 = dx0 - 1.0D - 0.6666666666666666D;
         double dy4 = dy0 - 1.0D - 0.6666666666666666D;
         double dz4 = dz0 - 0.0D - 0.6666666666666666D;
         double attn4 = 2.0D - dx4 * dx4 - dy4 * dy4 - dz4 * dz4;
         if (attn4 > 0.0D) {
            attn4 = attn4 * attn4;
            value += attn4 * attn4 * this.extrapolate(xsb + 1, ysb + 1, zsb + 0, dx4, dy4, dz4);
         }

         double dy5 = dy0 - 0.0D - 0.6666666666666666D;
         double dz5 = dz0 - 1.0D - 0.6666666666666666D;
         double attn5 = 2.0D - dx4 * dx4 - dy5 * dy5 - dz5 * dz5;
         if (attn5 > 0.0D) {
            attn5 = attn5 * attn5;
            value += attn5 * attn5 * this.extrapolate(xsb + 1, ysb + 0, zsb + 1, dx4, dy5, dz5);
         }

         double dx6 = dx0 - 0.0D - 0.6666666666666666D;
         double attn6 = 2.0D - dx6 * dx6 - dy4 * dy4 - dz5 * dz5;
         if (attn6 > 0.0D) {
            attn6 = attn6 * attn6;
            value += attn6 * attn6 * this.extrapolate(xsb + 0, ysb + 1, zsb + 1, dx6, dy4, dz5);
         }
      }

      double attn_ext0 = 2.0D - dx_ext0 * dx_ext0 - dy_ext0 * dy_ext0 - dz_ext0 * dz_ext0;
      if (attn_ext0 > 0.0D) {
         attn_ext0 = attn_ext0 * attn_ext0;
         value += attn_ext0 * attn_ext0 * this.extrapolate(xsv_ext0, ysv_ext0, zsv_ext0, dx_ext0, dy_ext0, dz_ext0);
      }

      double attn_ext1 = 2.0D - dx_ext1 * dx_ext1 - dy_ext1 * dy_ext1 - dz_ext1 * dz_ext1;
      if (attn_ext1 > 0.0D) {
         attn_ext1 = attn_ext1 * attn_ext1;
         value += attn_ext1 * attn_ext1 * this.extrapolate(xsv_ext1, ysv_ext1, zsv_ext1, dx_ext1, dy_ext1, dz_ext1);
      }

      return value / 103.0D;
   }

   public double eval(double x, double y, double z, double w) {
      double stretchOffset = (x + y + z + w) * -0.138196601125011D;
      double xs = x + stretchOffset;
      double ys = y + stretchOffset;
      double zs = z + stretchOffset;
      double ws = w + stretchOffset;
      int xsb = fastFloor(xs);
      int ysb = fastFloor(ys);
      int zsb = fastFloor(zs);
      int wsb = fastFloor(ws);
      double squishOffset = (double)(xsb + ysb + zsb + wsb) * 0.309016994374947D;
      double xb = (double)xsb + squishOffset;
      double yb = (double)ysb + squishOffset;
      double zb = (double)zsb + squishOffset;
      double wb = (double)wsb + squishOffset;
      double xins = xs - (double)xsb;
      double yins = ys - (double)ysb;
      double zins = zs - (double)zsb;
      double wins = ws - (double)wsb;
      double inSum = xins + yins + zins + wins;
      double dx0 = x - xb;
      double dy0 = y - yb;
      double dz0 = z - zb;
      double dw0 = w - wb;
      double value = 0.0D;
      double dx_ext0;
      double dy_ext0;
      double dz_ext0;
      double dw_ext0;
      double dx_ext1;
      double dy_ext1;
      double dz_ext1;
      double dw_ext1;
      double dx_ext2;
      double dy_ext2;
      double dz_ext2;
      double dw_ext2;
      int xsv_ext0;
      int ysv_ext0;
      int zsv_ext0;
      int wsv_ext0;
      int xsv_ext1;
      int ysv_ext1;
      int zsv_ext1;
      int wsv_ext1;
      int xsv_ext2;
      int ysv_ext2;
      int zsv_ext2;
      int wsv_ext2;
      if (inSum <= 1.0D) {
         byte aPoint = 1;
         double aScore = xins;
         byte bPoint = 2;
         double bScore = yins;
         if (xins >= yins && zins > yins) {
            bScore = zins;
            bPoint = 4;
         } else if (xins < yins && zins > xins) {
            aScore = zins;
            aPoint = 4;
         }

         if (aScore >= bScore && wins > bScore) {
            bScore = wins;
            bPoint = 8;
         } else if (aScore < bScore && wins > aScore) {
            aScore = wins;
            aPoint = 8;
         }

         double uins = 1.0D - inSum;
         if (uins <= aScore && uins <= bScore) {
            byte c = (byte)(aPoint | bPoint);
            if ((c & 1) == 0) {
               xsv_ext2 = xsb;
               xsv_ext0 = xsb;
               xsv_ext1 = xsb - 1;
               dx_ext0 = dx0 - 0.618033988749894D;
               dx_ext1 = dx0 + 1.0D - 0.309016994374947D;
               dx_ext2 = dx0 - 0.309016994374947D;
            } else {
               xsv_ext0 = xsv_ext1 = xsv_ext2 = xsb + 1;
               dx_ext0 = dx0 - 1.0D - 0.618033988749894D;
               dx_ext1 = dx_ext2 = dx0 - 1.0D - 0.309016994374947D;
            }

            if ((c & 2) == 0) {
               ysv_ext2 = ysb;
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext0 = dy0 - 0.618033988749894D;
               dy_ext1 = dy_ext2 = dy0 - 0.309016994374947D;
               if ((c & 1) == 1) {
                  ysv_ext1 = ysb - 1;
                  ++dy_ext1;
               } else {
                  ysv_ext2 = ysb - 1;
                  ++dy_ext2;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb + 1;
               dy_ext0 = dy0 - 1.0D - 0.618033988749894D;
               dy_ext1 = dy_ext2 = dy0 - 1.0D - 0.309016994374947D;
            }

            if ((c & 4) == 0) {
               zsv_ext2 = zsb;
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               dz_ext0 = dz0 - 0.618033988749894D;
               dz_ext1 = dz_ext2 = dz0 - 0.309016994374947D;
               if ((c & 3) == 3) {
                  zsv_ext1 = zsb - 1;
                  ++dz_ext1;
               } else {
                  zsv_ext2 = zsb - 1;
                  ++dz_ext2;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb + 1;
               dz_ext0 = dz0 - 1.0D - 0.618033988749894D;
               dz_ext1 = dz_ext2 = dz0 - 1.0D - 0.309016994374947D;
            }

            if ((c & 8) == 0) {
               wsv_ext1 = wsb;
               wsv_ext0 = wsb;
               wsv_ext2 = wsb - 1;
               dw_ext0 = dw0 - 0.618033988749894D;
               dw_ext1 = dw0 - 0.309016994374947D;
               dw_ext2 = dw0 + 1.0D - 0.309016994374947D;
            } else {
               wsv_ext0 = wsv_ext1 = wsv_ext2 = wsb + 1;
               dw_ext0 = dw0 - 1.0D - 0.618033988749894D;
               dw_ext1 = dw_ext2 = dw0 - 1.0D - 0.309016994374947D;
            }
         } else {
            byte c = bScore > aScore ? bPoint : aPoint;
            if ((c & 1) == 0) {
               xsv_ext0 = xsb - 1;
               xsv_ext2 = xsb;
               xsv_ext1 = xsb;
               dx_ext0 = dx0 + 1.0D;
               dx_ext2 = dx0;
               dx_ext1 = dx0;
            } else {
               xsv_ext0 = xsv_ext1 = xsv_ext2 = xsb + 1;
               dx_ext0 = dx_ext1 = dx_ext2 = dx0 - 1.0D;
            }

            if ((c & 2) == 0) {
               ysv_ext2 = ysb;
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext2 = dy0;
               dy_ext1 = dy0;
               dy_ext0 = dy0;
               if ((c & 1) == 1) {
                  ysv_ext0 = ysb - 1;
                  dy_ext0 = dy0 + 1.0D;
               } else {
                  ysv_ext1 = ysb - 1;
                  dy_ext1 = dy0 + 1.0D;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb + 1;
               dy_ext0 = dy_ext1 = dy_ext2 = dy0 - 1.0D;
            }

            if ((c & 4) == 0) {
               zsv_ext2 = zsb;
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               dz_ext2 = dz0;
               dz_ext1 = dz0;
               dz_ext0 = dz0;
               if ((c & 3) != 0) {
                  if ((c & 3) == 3) {
                     zsv_ext0 = zsb - 1;
                     dz_ext0 = dz0 + 1.0D;
                  } else {
                     zsv_ext1 = zsb - 1;
                     dz_ext1 = dz0 + 1.0D;
                  }
               } else {
                  zsv_ext2 = zsb - 1;
                  dz_ext2 = dz0 + 1.0D;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb + 1;
               dz_ext0 = dz_ext1 = dz_ext2 = dz0 - 1.0D;
            }

            if ((c & 8) == 0) {
               wsv_ext1 = wsb;
               wsv_ext0 = wsb;
               wsv_ext2 = wsb - 1;
               dw_ext1 = dw0;
               dw_ext0 = dw0;
               dw_ext2 = dw0 + 1.0D;
            } else {
               wsv_ext0 = wsv_ext1 = wsv_ext2 = wsb + 1;
               dw_ext0 = dw_ext1 = dw_ext2 = dw0 - 1.0D;
            }
         }

         double attn0 = 2.0D - dx0 * dx0 - dy0 * dy0 - dz0 * dz0 - dw0 * dw0;
         if (attn0 > 0.0D) {
            attn0 = attn0 * attn0;
            value += attn0 * attn0 * this.extrapolate(xsb + 0, ysb + 0, zsb + 0, wsb + 0, dx0, dy0, dz0, dw0);
         }

         double dx1 = dx0 - 1.0D - 0.309016994374947D;
         double dy1 = dy0 - 0.0D - 0.309016994374947D;
         double dz1 = dz0 - 0.0D - 0.309016994374947D;
         double dw1 = dw0 - 0.0D - 0.309016994374947D;
         double attn1 = 2.0D - dx1 * dx1 - dy1 * dy1 - dz1 * dz1 - dw1 * dw1;
         if (attn1 > 0.0D) {
            attn1 = attn1 * attn1;
            value += attn1 * attn1 * this.extrapolate(xsb + 1, ysb + 0, zsb + 0, wsb + 0, dx1, dy1, dz1, dw1);
         }

         double dx2 = dx0 - 0.0D - 0.309016994374947D;
         double dy2 = dy0 - 1.0D - 0.309016994374947D;
         double attn2 = 2.0D - dx2 * dx2 - dy2 * dy2 - dz1 * dz1 - dw1 * dw1;
         if (attn2 > 0.0D) {
            attn2 = attn2 * attn2;
            value += attn2 * attn2 * this.extrapolate(xsb + 0, ysb + 1, zsb + 0, wsb + 0, dx2, dy2, dz1, dw1);
         }

         double dz3 = dz0 - 1.0D - 0.309016994374947D;
         double attn3 = 2.0D - dx2 * dx2 - dy1 * dy1 - dz3 * dz3 - dw1 * dw1;
         if (attn3 > 0.0D) {
            attn3 = attn3 * attn3;
            value += attn3 * attn3 * this.extrapolate(xsb + 0, ysb + 0, zsb + 1, wsb + 0, dx2, dy1, dz3, dw1);
         }

         double dw4 = dw0 - 1.0D - 0.309016994374947D;
         double attn4 = 2.0D - dx2 * dx2 - dy1 * dy1 - dz1 * dz1 - dw4 * dw4;
         if (attn4 > 0.0D) {
            attn4 = attn4 * attn4;
            value += attn4 * attn4 * this.extrapolate(xsb + 0, ysb + 0, zsb + 0, wsb + 1, dx2, dy1, dz1, dw4);
         }
      } else if (inSum >= 3.0D) {
         byte aPoint = 14;
         double aScore = xins;
         byte bPoint = 13;
         double bScore = yins;
         if (xins <= yins && zins < yins) {
            bScore = zins;
            bPoint = 11;
         } else if (xins > yins && zins < xins) {
            aScore = zins;
            aPoint = 11;
         }

         if (aScore <= bScore && wins < bScore) {
            bScore = wins;
            bPoint = 7;
         } else if (aScore > bScore && wins < aScore) {
            aScore = wins;
            aPoint = 7;
         }

         double uins = 4.0D - inSum;
         if (uins >= aScore && uins >= bScore) {
            byte c = (byte)(aPoint & bPoint);
            if ((c & 1) != 0) {
               xsv_ext0 = xsv_ext2 = xsb + 1;
               xsv_ext1 = xsb + 2;
               dx_ext0 = dx0 - 1.0D - 0.618033988749894D;
               dx_ext1 = dx0 - 2.0D - 0.927050983124841D;
               dx_ext2 = dx0 - 1.0D - 0.927050983124841D;
            } else {
               xsv_ext2 = xsb;
               xsv_ext1 = xsb;
               xsv_ext0 = xsb;
               dx_ext0 = dx0 - 0.618033988749894D;
               dx_ext1 = dx_ext2 = dx0 - 0.927050983124841D;
            }

            if ((c & 2) != 0) {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb + 1;
               dy_ext0 = dy0 - 1.0D - 0.618033988749894D;
               dy_ext1 = dy_ext2 = dy0 - 1.0D - 0.927050983124841D;
               if ((c & 1) != 0) {
                  ++ysv_ext2;
                  --dy_ext2;
               } else {
                  ++ysv_ext1;
                  --dy_ext1;
               }
            } else {
               ysv_ext2 = ysb;
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext0 = dy0 - 0.618033988749894D;
               dy_ext1 = dy_ext2 = dy0 - 0.927050983124841D;
            }

            if ((c & 4) != 0) {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb + 1;
               dz_ext0 = dz0 - 1.0D - 0.618033988749894D;
               dz_ext1 = dz_ext2 = dz0 - 1.0D - 0.927050983124841D;
               if ((c & 3) != 0) {
                  ++zsv_ext2;
                  --dz_ext2;
               } else {
                  ++zsv_ext1;
                  --dz_ext1;
               }
            } else {
               zsv_ext2 = zsb;
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               dz_ext0 = dz0 - 0.618033988749894D;
               dz_ext1 = dz_ext2 = dz0 - 0.927050983124841D;
            }

            if ((c & 8) != 0) {
               wsv_ext0 = wsv_ext1 = wsb + 1;
               wsv_ext2 = wsb + 2;
               dw_ext0 = dw0 - 1.0D - 0.618033988749894D;
               dw_ext1 = dw0 - 1.0D - 0.927050983124841D;
               dw_ext2 = dw0 - 2.0D - 0.927050983124841D;
            } else {
               wsv_ext2 = wsb;
               wsv_ext1 = wsb;
               wsv_ext0 = wsb;
               dw_ext0 = dw0 - 0.618033988749894D;
               dw_ext1 = dw_ext2 = dw0 - 0.927050983124841D;
            }
         } else {
            byte c = bScore < aScore ? bPoint : aPoint;
            if ((c & 1) != 0) {
               xsv_ext0 = xsb + 2;
               xsv_ext1 = xsv_ext2 = xsb + 1;
               dx_ext0 = dx0 - 2.0D - 1.236067977499788D;
               dx_ext1 = dx_ext2 = dx0 - 1.0D - 1.236067977499788D;
            } else {
               xsv_ext2 = xsb;
               xsv_ext1 = xsb;
               xsv_ext0 = xsb;
               dx_ext0 = dx_ext1 = dx_ext2 = dx0 - 1.236067977499788D;
            }

            if ((c & 2) != 0) {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb + 1;
               dy_ext0 = dy_ext1 = dy_ext2 = dy0 - 1.0D - 1.236067977499788D;
               if ((c & 1) != 0) {
                  ++ysv_ext1;
                  --dy_ext1;
               } else {
                  ++ysv_ext0;
                  --dy_ext0;
               }
            } else {
               ysv_ext2 = ysb;
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext0 = dy_ext1 = dy_ext2 = dy0 - 1.236067977499788D;
            }

            if ((c & 4) != 0) {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb + 1;
               dz_ext0 = dz_ext1 = dz_ext2 = dz0 - 1.0D - 1.236067977499788D;
               if ((c & 3) != 3) {
                  if ((c & 3) == 0) {
                     ++zsv_ext0;
                     --dz_ext0;
                  } else {
                     ++zsv_ext1;
                     --dz_ext1;
                  }
               } else {
                  ++zsv_ext2;
                  --dz_ext2;
               }
            } else {
               zsv_ext2 = zsb;
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               dz_ext0 = dz_ext1 = dz_ext2 = dz0 - 1.236067977499788D;
            }

            if ((c & 8) != 0) {
               wsv_ext0 = wsv_ext1 = wsb + 1;
               wsv_ext2 = wsb + 2;
               dw_ext0 = dw_ext1 = dw0 - 1.0D - 1.236067977499788D;
               dw_ext2 = dw0 - 2.0D - 1.236067977499788D;
            } else {
               wsv_ext2 = wsb;
               wsv_ext1 = wsb;
               wsv_ext0 = wsb;
               dw_ext0 = dw_ext1 = dw_ext2 = dw0 - 1.236067977499788D;
            }
         }

         double dx4 = dx0 - 1.0D - 0.927050983124841D;
         double dy4 = dy0 - 1.0D - 0.927050983124841D;
         double dz4 = dz0 - 1.0D - 0.927050983124841D;
         double dw4 = dw0 - 0.927050983124841D;
         double attn4 = 2.0D - dx4 * dx4 - dy4 * dy4 - dz4 * dz4 - dw4 * dw4;
         if (attn4 > 0.0D) {
            attn4 = attn4 * attn4;
            value += attn4 * attn4 * this.extrapolate(xsb + 1, ysb + 1, zsb + 1, wsb + 0, dx4, dy4, dz4, dw4);
         }

         double dz3 = dz0 - 0.927050983124841D;
         double dw3 = dw0 - 1.0D - 0.927050983124841D;
         double attn3 = 2.0D - dx4 * dx4 - dy4 * dy4 - dz3 * dz3 - dw3 * dw3;
         if (attn3 > 0.0D) {
            attn3 = attn3 * attn3;
            value += attn3 * attn3 * this.extrapolate(xsb + 1, ysb + 1, zsb + 0, wsb + 1, dx4, dy4, dz3, dw3);
         }

         double dy2 = dy0 - 0.927050983124841D;
         double attn2 = 2.0D - dx4 * dx4 - dy2 * dy2 - dz4 * dz4 - dw3 * dw3;
         if (attn2 > 0.0D) {
            attn2 = attn2 * attn2;
            value += attn2 * attn2 * this.extrapolate(xsb + 1, ysb + 0, zsb + 1, wsb + 1, dx4, dy2, dz4, dw3);
         }

         double dx1 = dx0 - 0.927050983124841D;
         double attn1 = 2.0D - dx1 * dx1 - dy4 * dy4 - dz4 * dz4 - dw3 * dw3;
         if (attn1 > 0.0D) {
            attn1 = attn1 * attn1;
            value += attn1 * attn1 * this.extrapolate(xsb + 0, ysb + 1, zsb + 1, wsb + 1, dx1, dy4, dz4, dw3);
         }

         dx0 = dx0 - 1.0D - 1.236067977499788D;
         dy0 = dy0 - 1.0D - 1.236067977499788D;
         dz0 = dz0 - 1.0D - 1.236067977499788D;
         dw0 = dw0 - 1.0D - 1.236067977499788D;
         double attn0 = 2.0D - dx0 * dx0 - dy0 * dy0 - dz0 * dz0 - dw0 * dw0;
         if (attn0 > 0.0D) {
            attn0 = attn0 * attn0;
            value += attn0 * attn0 * this.extrapolate(xsb + 1, ysb + 1, zsb + 1, wsb + 1, dx0, dy0, dz0, dw0);
         }
      } else if (inSum <= 2.0D) {
         boolean aIsBiggerSide = true;
         boolean bIsBiggerSide = true;
         byte aPoint;
         double aScore;
         if (xins + yins > zins + wins) {
            aScore = xins + yins;
            aPoint = 3;
         } else {
            aScore = zins + wins;
            aPoint = 12;
         }

         double bScore;
         byte bPoint;
         if (xins + zins > yins + wins) {
            bScore = xins + zins;
            bPoint = 5;
         } else {
            bScore = yins + wins;
            bPoint = 10;
         }

         if (xins + wins > yins + zins) {
            double score = xins + wins;
            if (aScore >= bScore && score > bScore) {
               bScore = score;
               bPoint = 9;
            } else if (aScore < bScore && score > aScore) {
               aScore = score;
               aPoint = 9;
            }
         } else {
            double score = yins + zins;
            if (aScore >= bScore && score > bScore) {
               bScore = score;
               bPoint = 6;
            } else if (aScore < bScore && score > aScore) {
               aScore = score;
               aPoint = 6;
            }
         }

         double p1 = 2.0D - inSum + xins;
         if (aScore >= bScore && p1 > bScore) {
            bScore = p1;
            bPoint = 1;
            bIsBiggerSide = false;
         } else if (aScore < bScore && p1 > aScore) {
            aScore = p1;
            aPoint = 1;
            aIsBiggerSide = false;
         }

         double p2 = 2.0D - inSum + yins;
         if (aScore >= bScore && p2 > bScore) {
            bScore = p2;
            bPoint = 2;
            bIsBiggerSide = false;
         } else if (aScore < bScore && p2 > aScore) {
            aScore = p2;
            aPoint = 2;
            aIsBiggerSide = false;
         }

         double p3 = 2.0D - inSum + zins;
         if (aScore >= bScore && p3 > bScore) {
            bScore = p3;
            bPoint = 4;
            bIsBiggerSide = false;
         } else if (aScore < bScore && p3 > aScore) {
            aScore = p3;
            aPoint = 4;
            aIsBiggerSide = false;
         }

         double p4 = 2.0D - inSum + wins;
         if (aScore >= bScore && p4 > bScore) {
            bPoint = 8;
            bIsBiggerSide = false;
         } else if (aScore < bScore && p4 > aScore) {
            aPoint = 8;
            aIsBiggerSide = false;
         }

         if (aIsBiggerSide == bIsBiggerSide) {
            if (aIsBiggerSide) {
               byte c1 = (byte)(aPoint | bPoint);
               byte c2 = (byte)(aPoint & bPoint);
               if ((c1 & 1) == 0) {
                  xsv_ext0 = xsb;
                  xsv_ext1 = xsb - 1;
                  dx_ext0 = dx0 - 0.927050983124841D;
                  dx_ext1 = dx0 + 1.0D - 0.618033988749894D;
               } else {
                  xsv_ext0 = xsv_ext1 = xsb + 1;
                  dx_ext0 = dx0 - 1.0D - 0.927050983124841D;
                  dx_ext1 = dx0 - 1.0D - 0.618033988749894D;
               }

               if ((c1 & 2) == 0) {
                  ysv_ext0 = ysb;
                  ysv_ext1 = ysb - 1;
                  dy_ext0 = dy0 - 0.927050983124841D;
                  dy_ext1 = dy0 + 1.0D - 0.618033988749894D;
               } else {
                  ysv_ext0 = ysv_ext1 = ysb + 1;
                  dy_ext0 = dy0 - 1.0D - 0.927050983124841D;
                  dy_ext1 = dy0 - 1.0D - 0.618033988749894D;
               }

               if ((c1 & 4) == 0) {
                  zsv_ext0 = zsb;
                  zsv_ext1 = zsb - 1;
                  dz_ext0 = dz0 - 0.927050983124841D;
                  dz_ext1 = dz0 + 1.0D - 0.618033988749894D;
               } else {
                  zsv_ext0 = zsv_ext1 = zsb + 1;
                  dz_ext0 = dz0 - 1.0D - 0.927050983124841D;
                  dz_ext1 = dz0 - 1.0D - 0.618033988749894D;
               }

               if ((c1 & 8) == 0) {
                  wsv_ext0 = wsb;
                  wsv_ext1 = wsb - 1;
                  dw_ext0 = dw0 - 0.927050983124841D;
                  dw_ext1 = dw0 + 1.0D - 0.618033988749894D;
               } else {
                  wsv_ext0 = wsv_ext1 = wsb + 1;
                  dw_ext0 = dw0 - 1.0D - 0.927050983124841D;
                  dw_ext1 = dw0 - 1.0D - 0.618033988749894D;
               }

               xsv_ext2 = xsb;
               ysv_ext2 = ysb;
               zsv_ext2 = zsb;
               wsv_ext2 = wsb;
               dx_ext2 = dx0 - 0.618033988749894D;
               dy_ext2 = dy0 - 0.618033988749894D;
               dz_ext2 = dz0 - 0.618033988749894D;
               dw_ext2 = dw0 - 0.618033988749894D;
               if ((c2 & 1) != 0) {
                  xsv_ext2 = xsb + 2;
                  dx_ext2 -= 2.0D;
               } else if ((c2 & 2) != 0) {
                  ysv_ext2 = ysb + 2;
                  dy_ext2 -= 2.0D;
               } else if ((c2 & 4) != 0) {
                  zsv_ext2 = zsb + 2;
                  dz_ext2 -= 2.0D;
               } else {
                  wsv_ext2 = wsb + 2;
                  dw_ext2 -= 2.0D;
               }
            } else {
               xsv_ext2 = xsb;
               ysv_ext2 = ysb;
               zsv_ext2 = zsb;
               wsv_ext2 = wsb;
               dx_ext2 = dx0;
               dy_ext2 = dy0;
               dz_ext2 = dz0;
               dw_ext2 = dw0;
               byte c = (byte)(aPoint | bPoint);
               if ((c & 1) == 0) {
                  xsv_ext0 = xsb - 1;
                  xsv_ext1 = xsb;
                  dx_ext0 = dx0 + 1.0D - 0.309016994374947D;
                  dx_ext1 = dx0 - 0.309016994374947D;
               } else {
                  xsv_ext0 = xsv_ext1 = xsb + 1;
                  dx_ext0 = dx_ext1 = dx0 - 1.0D - 0.309016994374947D;
               }

               if ((c & 2) == 0) {
                  ysv_ext1 = ysb;
                  ysv_ext0 = ysb;
                  dy_ext0 = dy_ext1 = dy0 - 0.309016994374947D;
                  if ((c & 1) == 1) {
                     ysv_ext0 = ysb - 1;
                     ++dy_ext0;
                  } else {
                     ysv_ext1 = ysb - 1;
                     ++dy_ext1;
                  }
               } else {
                  ysv_ext0 = ysv_ext1 = ysb + 1;
                  dy_ext0 = dy_ext1 = dy0 - 1.0D - 0.309016994374947D;
               }

               if ((c & 4) == 0) {
                  zsv_ext1 = zsb;
                  zsv_ext0 = zsb;
                  dz_ext0 = dz_ext1 = dz0 - 0.309016994374947D;
                  if ((c & 3) == 3) {
                     zsv_ext0 = zsb - 1;
                     ++dz_ext0;
                  } else {
                     zsv_ext1 = zsb - 1;
                     ++dz_ext1;
                  }
               } else {
                  zsv_ext0 = zsv_ext1 = zsb + 1;
                  dz_ext0 = dz_ext1 = dz0 - 1.0D - 0.309016994374947D;
               }

               if ((c & 8) == 0) {
                  wsv_ext0 = wsb;
                  wsv_ext1 = wsb - 1;
                  dw_ext0 = dw0 - 0.309016994374947D;
                  dw_ext1 = dw0 + 1.0D - 0.309016994374947D;
               } else {
                  wsv_ext0 = wsv_ext1 = wsb + 1;
                  dw_ext0 = dw_ext1 = dw0 - 1.0D - 0.309016994374947D;
               }
            }
         } else {
            byte c1;
            byte c2;
            if (aIsBiggerSide) {
               c1 = aPoint;
               c2 = bPoint;
            } else {
               c1 = bPoint;
               c2 = aPoint;
            }

            if ((c1 & 1) == 0) {
               xsv_ext0 = xsb - 1;
               xsv_ext1 = xsb;
               dx_ext0 = dx0 + 1.0D - 0.309016994374947D;
               dx_ext1 = dx0 - 0.309016994374947D;
            } else {
               xsv_ext0 = xsv_ext1 = xsb + 1;
               dx_ext0 = dx_ext1 = dx0 - 1.0D - 0.309016994374947D;
            }

            if ((c1 & 2) == 0) {
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext0 = dy_ext1 = dy0 - 0.309016994374947D;
               if ((c1 & 1) == 1) {
                  ysv_ext0 = ysb - 1;
                  ++dy_ext0;
               } else {
                  ysv_ext1 = ysb - 1;
                  ++dy_ext1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy_ext1 = dy0 - 1.0D - 0.309016994374947D;
            }

            if ((c1 & 4) == 0) {
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               dz_ext0 = dz_ext1 = dz0 - 0.309016994374947D;
               if ((c1 & 3) == 3) {
                  zsv_ext0 = zsb - 1;
                  ++dz_ext0;
               } else {
                  zsv_ext1 = zsb - 1;
                  ++dz_ext1;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsb + 1;
               dz_ext0 = dz_ext1 = dz0 - 1.0D - 0.309016994374947D;
            }

            if ((c1 & 8) == 0) {
               wsv_ext0 = wsb;
               wsv_ext1 = wsb - 1;
               dw_ext0 = dw0 - 0.309016994374947D;
               dw_ext1 = dw0 + 1.0D - 0.309016994374947D;
            } else {
               wsv_ext0 = wsv_ext1 = wsb + 1;
               dw_ext0 = dw_ext1 = dw0 - 1.0D - 0.309016994374947D;
            }

            xsv_ext2 = xsb;
            ysv_ext2 = ysb;
            zsv_ext2 = zsb;
            wsv_ext2 = wsb;
            dx_ext2 = dx0 - 0.618033988749894D;
            dy_ext2 = dy0 - 0.618033988749894D;
            dz_ext2 = dz0 - 0.618033988749894D;
            dw_ext2 = dw0 - 0.618033988749894D;
            if ((c2 & 1) != 0) {
               xsv_ext2 = xsb + 2;
               dx_ext2 -= 2.0D;
            } else if ((c2 & 2) != 0) {
               ysv_ext2 = ysb + 2;
               dy_ext2 -= 2.0D;
            } else if ((c2 & 4) != 0) {
               zsv_ext2 = zsb + 2;
               dz_ext2 -= 2.0D;
            } else {
               wsv_ext2 = wsb + 2;
               dw_ext2 -= 2.0D;
            }
         }

         double dx1 = dx0 - 1.0D - 0.309016994374947D;
         double dy1 = dy0 - 0.0D - 0.309016994374947D;
         double dz1 = dz0 - 0.0D - 0.309016994374947D;
         double dw1 = dw0 - 0.0D - 0.309016994374947D;
         double attn1 = 2.0D - dx1 * dx1 - dy1 * dy1 - dz1 * dz1 - dw1 * dw1;
         if (attn1 > 0.0D) {
            attn1 = attn1 * attn1;
            value += attn1 * attn1 * this.extrapolate(xsb + 1, ysb + 0, zsb + 0, wsb + 0, dx1, dy1, dz1, dw1);
         }

         double dx2 = dx0 - 0.0D - 0.309016994374947D;
         double dy2 = dy0 - 1.0D - 0.309016994374947D;
         double attn2 = 2.0D - dx2 * dx2 - dy2 * dy2 - dz1 * dz1 - dw1 * dw1;
         if (attn2 > 0.0D) {
            attn2 = attn2 * attn2;
            value += attn2 * attn2 * this.extrapolate(xsb + 0, ysb + 1, zsb + 0, wsb + 0, dx2, dy2, dz1, dw1);
         }

         double dz3 = dz0 - 1.0D - 0.309016994374947D;
         double attn3 = 2.0D - dx2 * dx2 - dy1 * dy1 - dz3 * dz3 - dw1 * dw1;
         if (attn3 > 0.0D) {
            attn3 = attn3 * attn3;
            value += attn3 * attn3 * this.extrapolate(xsb + 0, ysb + 0, zsb + 1, wsb + 0, dx2, dy1, dz3, dw1);
         }

         double dw4 = dw0 - 1.0D - 0.309016994374947D;
         double attn4 = 2.0D - dx2 * dx2 - dy1 * dy1 - dz1 * dz1 - dw4 * dw4;
         if (attn4 > 0.0D) {
            attn4 = attn4 * attn4;
            value += attn4 * attn4 * this.extrapolate(xsb + 0, ysb + 0, zsb + 0, wsb + 1, dx2, dy1, dz1, dw4);
         }

         double dx5 = dx0 - 1.0D - 0.618033988749894D;
         double dy5 = dy0 - 1.0D - 0.618033988749894D;
         double dz5 = dz0 - 0.0D - 0.618033988749894D;
         double dw5 = dw0 - 0.0D - 0.618033988749894D;
         double attn5 = 2.0D - dx5 * dx5 - dy5 * dy5 - dz5 * dz5 - dw5 * dw5;
         if (attn5 > 0.0D) {
            attn5 = attn5 * attn5;
            value += attn5 * attn5 * this.extrapolate(xsb + 1, ysb + 1, zsb + 0, wsb + 0, dx5, dy5, dz5, dw5);
         }

         double dx6 = dx0 - 1.0D - 0.618033988749894D;
         double dy6 = dy0 - 0.0D - 0.618033988749894D;
         double dz6 = dz0 - 1.0D - 0.618033988749894D;
         double dw6 = dw0 - 0.0D - 0.618033988749894D;
         double attn6 = 2.0D - dx6 * dx6 - dy6 * dy6 - dz6 * dz6 - dw6 * dw6;
         if (attn6 > 0.0D) {
            attn6 = attn6 * attn6;
            value += attn6 * attn6 * this.extrapolate(xsb + 1, ysb + 0, zsb + 1, wsb + 0, dx6, dy6, dz6, dw6);
         }

         double dx7 = dx0 - 1.0D - 0.618033988749894D;
         double dy7 = dy0 - 0.0D - 0.618033988749894D;
         double dz7 = dz0 - 0.0D - 0.618033988749894D;
         double dw7 = dw0 - 1.0D - 0.618033988749894D;
         double attn7 = 2.0D - dx7 * dx7 - dy7 * dy7 - dz7 * dz7 - dw7 * dw7;
         if (attn7 > 0.0D) {
            attn7 = attn7 * attn7;
            value += attn7 * attn7 * this.extrapolate(xsb + 1, ysb + 0, zsb + 0, wsb + 1, dx7, dy7, dz7, dw7);
         }

         double dx8 = dx0 - 0.0D - 0.618033988749894D;
         double dy8 = dy0 - 1.0D - 0.618033988749894D;
         double dz8 = dz0 - 1.0D - 0.618033988749894D;
         double dw8 = dw0 - 0.0D - 0.618033988749894D;
         double attn8 = 2.0D - dx8 * dx8 - dy8 * dy8 - dz8 * dz8 - dw8 * dw8;
         if (attn8 > 0.0D) {
            attn8 = attn8 * attn8;
            value += attn8 * attn8 * this.extrapolate(xsb + 0, ysb + 1, zsb + 1, wsb + 0, dx8, dy8, dz8, dw8);
         }

         double dx9 = dx0 - 0.0D - 0.618033988749894D;
         double dy9 = dy0 - 1.0D - 0.618033988749894D;
         double dz9 = dz0 - 0.0D - 0.618033988749894D;
         double dw9 = dw0 - 1.0D - 0.618033988749894D;
         double attn9 = 2.0D - dx9 * dx9 - dy9 * dy9 - dz9 * dz9 - dw9 * dw9;
         if (attn9 > 0.0D) {
            attn9 = attn9 * attn9;
            value += attn9 * attn9 * this.extrapolate(xsb + 0, ysb + 1, zsb + 0, wsb + 1, dx9, dy9, dz9, dw9);
         }

         double dx10 = dx0 - 0.0D - 0.618033988749894D;
         double dy10 = dy0 - 0.0D - 0.618033988749894D;
         double dz10 = dz0 - 1.0D - 0.618033988749894D;
         double dw10 = dw0 - 1.0D - 0.618033988749894D;
         double attn10 = 2.0D - dx10 * dx10 - dy10 * dy10 - dz10 * dz10 - dw10 * dw10;
         if (attn10 > 0.0D) {
            attn10 = attn10 * attn10;
            value += attn10 * attn10 * this.extrapolate(xsb + 0, ysb + 0, zsb + 1, wsb + 1, dx10, dy10, dz10, dw10);
         }
      } else {
         boolean aIsBiggerSide = true;
         boolean bIsBiggerSide = true;
         double aScore;
         byte aPoint;
         if (xins + yins < zins + wins) {
            aScore = xins + yins;
            aPoint = 12;
         } else {
            aScore = zins + wins;
            aPoint = 3;
         }

         double bScore;
         byte bPoint;
         if (xins + zins < yins + wins) {
            bScore = xins + zins;
            bPoint = 10;
         } else {
            bScore = yins + wins;
            bPoint = 5;
         }

         if (xins + wins < yins + zins) {
            double score = xins + wins;
            if (aScore <= bScore && score < bScore) {
               bScore = score;
               bPoint = 6;
            } else if (aScore > bScore && score < aScore) {
               aScore = score;
               aPoint = 6;
            }
         } else {
            double score = yins + zins;
            if (aScore <= bScore && score < bScore) {
               bScore = score;
               bPoint = 9;
            } else if (aScore > bScore && score < aScore) {
               aScore = score;
               aPoint = 9;
            }
         }

         double p1 = 3.0D - inSum + xins;
         if (aScore <= bScore && p1 < bScore) {
            bScore = p1;
            bPoint = 14;
            bIsBiggerSide = false;
         } else if (aScore > bScore && p1 < aScore) {
            aScore = p1;
            aPoint = 14;
            aIsBiggerSide = false;
         }

         double p2 = 3.0D - inSum + yins;
         if (aScore <= bScore && p2 < bScore) {
            bScore = p2;
            bPoint = 13;
            bIsBiggerSide = false;
         } else if (aScore > bScore && p2 < aScore) {
            aScore = p2;
            aPoint = 13;
            aIsBiggerSide = false;
         }

         double p3 = 3.0D - inSum + zins;
         if (aScore <= bScore && p3 < bScore) {
            bScore = p3;
            bPoint = 11;
            bIsBiggerSide = false;
         } else if (aScore > bScore && p3 < aScore) {
            aScore = p3;
            aPoint = 11;
            aIsBiggerSide = false;
         }

         double p4 = 3.0D - inSum + wins;
         if (aScore <= bScore && p4 < bScore) {
            bPoint = 7;
            bIsBiggerSide = false;
         } else if (aScore > bScore && p4 < aScore) {
            aPoint = 7;
            aIsBiggerSide = false;
         }

         if (aIsBiggerSide == bIsBiggerSide) {
            if (aIsBiggerSide) {
               byte c1 = (byte)(aPoint & bPoint);
               byte c2 = (byte)(aPoint | bPoint);
               xsv_ext1 = xsb;
               xsv_ext0 = xsb;
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               wsv_ext1 = wsb;
               wsv_ext0 = wsb;
               dx_ext0 = dx0 - 0.309016994374947D;
               dy_ext0 = dy0 - 0.309016994374947D;
               dz_ext0 = dz0 - 0.309016994374947D;
               dw_ext0 = dw0 - 0.309016994374947D;
               dx_ext1 = dx0 - 0.618033988749894D;
               dy_ext1 = dy0 - 0.618033988749894D;
               dz_ext1 = dz0 - 0.618033988749894D;
               dw_ext1 = dw0 - 0.618033988749894D;
               if ((c1 & 1) != 0) {
                  xsv_ext0 = xsb + 1;
                  --dx_ext0;
                  xsv_ext1 = xsb + 2;
                  dx_ext1 -= 2.0D;
               } else if ((c1 & 2) != 0) {
                  ysv_ext0 = ysb + 1;
                  --dy_ext0;
                  ysv_ext1 = ysb + 2;
                  dy_ext1 -= 2.0D;
               } else if ((c1 & 4) != 0) {
                  zsv_ext0 = zsb + 1;
                  --dz_ext0;
                  zsv_ext1 = zsb + 2;
                  dz_ext1 -= 2.0D;
               } else {
                  wsv_ext0 = wsb + 1;
                  --dw_ext0;
                  wsv_ext1 = wsb + 2;
                  dw_ext1 -= 2.0D;
               }

               xsv_ext2 = xsb + 1;
               ysv_ext2 = ysb + 1;
               zsv_ext2 = zsb + 1;
               wsv_ext2 = wsb + 1;
               dx_ext2 = dx0 - 1.0D - 0.618033988749894D;
               dy_ext2 = dy0 - 1.0D - 0.618033988749894D;
               dz_ext2 = dz0 - 1.0D - 0.618033988749894D;
               dw_ext2 = dw0 - 1.0D - 0.618033988749894D;
               if ((c2 & 1) == 0) {
                  xsv_ext2 -= 2;
                  dx_ext2 += 2.0D;
               } else if ((c2 & 2) == 0) {
                  ysv_ext2 -= 2;
                  dy_ext2 += 2.0D;
               } else if ((c2 & 4) == 0) {
                  zsv_ext2 -= 2;
                  dz_ext2 += 2.0D;
               } else {
                  wsv_ext2 -= 2;
                  dw_ext2 += 2.0D;
               }
            } else {
               xsv_ext2 = xsb + 1;
               ysv_ext2 = ysb + 1;
               zsv_ext2 = zsb + 1;
               wsv_ext2 = wsb + 1;
               dx_ext2 = dx0 - 1.0D - 1.236067977499788D;
               dy_ext2 = dy0 - 1.0D - 1.236067977499788D;
               dz_ext2 = dz0 - 1.0D - 1.236067977499788D;
               dw_ext2 = dw0 - 1.0D - 1.236067977499788D;
               byte c = (byte)(aPoint & bPoint);
               if ((c & 1) != 0) {
                  xsv_ext0 = xsb + 2;
                  xsv_ext1 = xsb + 1;
                  dx_ext0 = dx0 - 2.0D - 0.927050983124841D;
                  dx_ext1 = dx0 - 1.0D - 0.927050983124841D;
               } else {
                  xsv_ext1 = xsb;
                  xsv_ext0 = xsb;
                  dx_ext0 = dx_ext1 = dx0 - 0.927050983124841D;
               }

               if ((c & 2) != 0) {
                  ysv_ext0 = ysv_ext1 = ysb + 1;
                  dy_ext0 = dy_ext1 = dy0 - 1.0D - 0.927050983124841D;
                  if ((c & 1) == 0) {
                     ++ysv_ext0;
                     --dy_ext0;
                  } else {
                     ++ysv_ext1;
                     --dy_ext1;
                  }
               } else {
                  ysv_ext1 = ysb;
                  ysv_ext0 = ysb;
                  dy_ext0 = dy_ext1 = dy0 - 0.927050983124841D;
               }

               if ((c & 4) != 0) {
                  zsv_ext0 = zsv_ext1 = zsb + 1;
                  dz_ext0 = dz_ext1 = dz0 - 1.0D - 0.927050983124841D;
                  if ((c & 3) == 0) {
                     ++zsv_ext0;
                     --dz_ext0;
                  } else {
                     ++zsv_ext1;
                     --dz_ext1;
                  }
               } else {
                  zsv_ext1 = zsb;
                  zsv_ext0 = zsb;
                  dz_ext0 = dz_ext1 = dz0 - 0.927050983124841D;
               }

               if ((c & 8) != 0) {
                  wsv_ext0 = wsb + 1;
                  wsv_ext1 = wsb + 2;
                  dw_ext0 = dw0 - 1.0D - 0.927050983124841D;
                  dw_ext1 = dw0 - 2.0D - 0.927050983124841D;
               } else {
                  wsv_ext1 = wsb;
                  wsv_ext0 = wsb;
                  dw_ext0 = dw_ext1 = dw0 - 0.927050983124841D;
               }
            }
         } else {
            byte c1;
            byte c2;
            if (aIsBiggerSide) {
               c1 = aPoint;
               c2 = bPoint;
            } else {
               c1 = bPoint;
               c2 = aPoint;
            }

            if ((c1 & 1) != 0) {
               xsv_ext0 = xsb + 2;
               xsv_ext1 = xsb + 1;
               dx_ext0 = dx0 - 2.0D - 0.927050983124841D;
               dx_ext1 = dx0 - 1.0D - 0.927050983124841D;
            } else {
               xsv_ext1 = xsb;
               xsv_ext0 = xsb;
               dx_ext0 = dx_ext1 = dx0 - 0.927050983124841D;
            }

            if ((c1 & 2) != 0) {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy_ext1 = dy0 - 1.0D - 0.927050983124841D;
               if ((c1 & 1) == 0) {
                  ++ysv_ext0;
                  --dy_ext0;
               } else {
                  ++ysv_ext1;
                  --dy_ext1;
               }
            } else {
               ysv_ext1 = ysb;
               ysv_ext0 = ysb;
               dy_ext0 = dy_ext1 = dy0 - 0.927050983124841D;
            }

            if ((c1 & 4) != 0) {
               zsv_ext0 = zsv_ext1 = zsb + 1;
               dz_ext0 = dz_ext1 = dz0 - 1.0D - 0.927050983124841D;
               if ((c1 & 3) == 0) {
                  ++zsv_ext0;
                  --dz_ext0;
               } else {
                  ++zsv_ext1;
                  --dz_ext1;
               }
            } else {
               zsv_ext1 = zsb;
               zsv_ext0 = zsb;
               dz_ext0 = dz_ext1 = dz0 - 0.927050983124841D;
            }

            if ((c1 & 8) != 0) {
               wsv_ext0 = wsb + 1;
               wsv_ext1 = wsb + 2;
               dw_ext0 = dw0 - 1.0D - 0.927050983124841D;
               dw_ext1 = dw0 - 2.0D - 0.927050983124841D;
            } else {
               wsv_ext1 = wsb;
               wsv_ext0 = wsb;
               dw_ext0 = dw_ext1 = dw0 - 0.927050983124841D;
            }

            xsv_ext2 = xsb + 1;
            ysv_ext2 = ysb + 1;
            zsv_ext2 = zsb + 1;
            wsv_ext2 = wsb + 1;
            dx_ext2 = dx0 - 1.0D - 0.618033988749894D;
            dy_ext2 = dy0 - 1.0D - 0.618033988749894D;
            dz_ext2 = dz0 - 1.0D - 0.618033988749894D;
            dw_ext2 = dw0 - 1.0D - 0.618033988749894D;
            if ((c2 & 1) == 0) {
               xsv_ext2 -= 2;
               dx_ext2 += 2.0D;
            } else if ((c2 & 2) == 0) {
               ysv_ext2 -= 2;
               dy_ext2 += 2.0D;
            } else if ((c2 & 4) == 0) {
               zsv_ext2 -= 2;
               dz_ext2 += 2.0D;
            } else {
               wsv_ext2 -= 2;
               dw_ext2 += 2.0D;
            }
         }

         double dx4 = dx0 - 1.0D - 0.927050983124841D;
         double dy4 = dy0 - 1.0D - 0.927050983124841D;
         double dz4 = dz0 - 1.0D - 0.927050983124841D;
         double dw4 = dw0 - 0.927050983124841D;
         double attn4 = 2.0D - dx4 * dx4 - dy4 * dy4 - dz4 * dz4 - dw4 * dw4;
         if (attn4 > 0.0D) {
            attn4 = attn4 * attn4;
            value += attn4 * attn4 * this.extrapolate(xsb + 1, ysb + 1, zsb + 1, wsb + 0, dx4, dy4, dz4, dw4);
         }

         double dz3 = dz0 - 0.927050983124841D;
         double dw3 = dw0 - 1.0D - 0.927050983124841D;
         double attn3 = 2.0D - dx4 * dx4 - dy4 * dy4 - dz3 * dz3 - dw3 * dw3;
         if (attn3 > 0.0D) {
            attn3 = attn3 * attn3;
            value += attn3 * attn3 * this.extrapolate(xsb + 1, ysb + 1, zsb + 0, wsb + 1, dx4, dy4, dz3, dw3);
         }

         double dy2 = dy0 - 0.927050983124841D;
         double attn2 = 2.0D - dx4 * dx4 - dy2 * dy2 - dz4 * dz4 - dw3 * dw3;
         if (attn2 > 0.0D) {
            attn2 = attn2 * attn2;
            value += attn2 * attn2 * this.extrapolate(xsb + 1, ysb + 0, zsb + 1, wsb + 1, dx4, dy2, dz4, dw3);
         }

         double dx1 = dx0 - 0.927050983124841D;
         double attn1 = 2.0D - dx1 * dx1 - dy4 * dy4 - dz4 * dz4 - dw3 * dw3;
         if (attn1 > 0.0D) {
            attn1 = attn1 * attn1;
            value += attn1 * attn1 * this.extrapolate(xsb + 0, ysb + 1, zsb + 1, wsb + 1, dx1, dy4, dz4, dw3);
         }

         double dx5 = dx0 - 1.0D - 0.618033988749894D;
         double dy5 = dy0 - 1.0D - 0.618033988749894D;
         double dz5 = dz0 - 0.0D - 0.618033988749894D;
         double dw5 = dw0 - 0.0D - 0.618033988749894D;
         double attn5 = 2.0D - dx5 * dx5 - dy5 * dy5 - dz5 * dz5 - dw5 * dw5;
         if (attn5 > 0.0D) {
            attn5 = attn5 * attn5;
            value += attn5 * attn5 * this.extrapolate(xsb + 1, ysb + 1, zsb + 0, wsb + 0, dx5, dy5, dz5, dw5);
         }

         double dx6 = dx0 - 1.0D - 0.618033988749894D;
         double dy6 = dy0 - 0.0D - 0.618033988749894D;
         double dz6 = dz0 - 1.0D - 0.618033988749894D;
         double dw6 = dw0 - 0.0D - 0.618033988749894D;
         double attn6 = 2.0D - dx6 * dx6 - dy6 * dy6 - dz6 * dz6 - dw6 * dw6;
         if (attn6 > 0.0D) {
            attn6 = attn6 * attn6;
            value += attn6 * attn6 * this.extrapolate(xsb + 1, ysb + 0, zsb + 1, wsb + 0, dx6, dy6, dz6, dw6);
         }

         double dx7 = dx0 - 1.0D - 0.618033988749894D;
         double dy7 = dy0 - 0.0D - 0.618033988749894D;
         double dz7 = dz0 - 0.0D - 0.618033988749894D;
         double dw7 = dw0 - 1.0D - 0.618033988749894D;
         double attn7 = 2.0D - dx7 * dx7 - dy7 * dy7 - dz7 * dz7 - dw7 * dw7;
         if (attn7 > 0.0D) {
            attn7 = attn7 * attn7;
            value += attn7 * attn7 * this.extrapolate(xsb + 1, ysb + 0, zsb + 0, wsb + 1, dx7, dy7, dz7, dw7);
         }

         double dx8 = dx0 - 0.0D - 0.618033988749894D;
         double dy8 = dy0 - 1.0D - 0.618033988749894D;
         double dz8 = dz0 - 1.0D - 0.618033988749894D;
         double dw8 = dw0 - 0.0D - 0.618033988749894D;
         double attn8 = 2.0D - dx8 * dx8 - dy8 * dy8 - dz8 * dz8 - dw8 * dw8;
         if (attn8 > 0.0D) {
            attn8 = attn8 * attn8;
            value += attn8 * attn8 * this.extrapolate(xsb + 0, ysb + 1, zsb + 1, wsb + 0, dx8, dy8, dz8, dw8);
         }

         double dx9 = dx0 - 0.0D - 0.618033988749894D;
         double dy9 = dy0 - 1.0D - 0.618033988749894D;
         double dz9 = dz0 - 0.0D - 0.618033988749894D;
         double dw9 = dw0 - 1.0D - 0.618033988749894D;
         double attn9 = 2.0D - dx9 * dx9 - dy9 * dy9 - dz9 * dz9 - dw9 * dw9;
         if (attn9 > 0.0D) {
            attn9 = attn9 * attn9;
            value += attn9 * attn9 * this.extrapolate(xsb + 0, ysb + 1, zsb + 0, wsb + 1, dx9, dy9, dz9, dw9);
         }

         double dx10 = dx0 - 0.0D - 0.618033988749894D;
         double dy10 = dy0 - 0.0D - 0.618033988749894D;
         double dz10 = dz0 - 1.0D - 0.618033988749894D;
         double dw10 = dw0 - 1.0D - 0.618033988749894D;
         double attn10 = 2.0D - dx10 * dx10 - dy10 * dy10 - dz10 * dz10 - dw10 * dw10;
         if (attn10 > 0.0D) {
            attn10 = attn10 * attn10;
            value += attn10 * attn10 * this.extrapolate(xsb + 0, ysb + 0, zsb + 1, wsb + 1, dx10, dy10, dz10, dw10);
         }
      }

      double attn_ext0 = 2.0D - dx_ext0 * dx_ext0 - dy_ext0 * dy_ext0 - dz_ext0 * dz_ext0 - dw_ext0 * dw_ext0;
      if (attn_ext0 > 0.0D) {
         attn_ext0 = attn_ext0 * attn_ext0;
         value += attn_ext0 * attn_ext0 * this.extrapolate(xsv_ext0, ysv_ext0, zsv_ext0, wsv_ext0, dx_ext0, dy_ext0, dz_ext0, dw_ext0);
      }

      double attn_ext1 = 2.0D - dx_ext1 * dx_ext1 - dy_ext1 * dy_ext1 - dz_ext1 * dz_ext1 - dw_ext1 * dw_ext1;
      if (attn_ext1 > 0.0D) {
         attn_ext1 = attn_ext1 * attn_ext1;
         value += attn_ext1 * attn_ext1 * this.extrapolate(xsv_ext1, ysv_ext1, zsv_ext1, wsv_ext1, dx_ext1, dy_ext1, dz_ext1, dw_ext1);
      }

      double attn_ext2 = 2.0D - dx_ext2 * dx_ext2 - dy_ext2 * dy_ext2 - dz_ext2 * dz_ext2 - dw_ext2 * dw_ext2;
      if (attn_ext2 > 0.0D) {
         attn_ext2 = attn_ext2 * attn_ext2;
         value += attn_ext2 * attn_ext2 * this.extrapolate(xsv_ext2, ysv_ext2, zsv_ext2, wsv_ext2, dx_ext2, dy_ext2, dz_ext2, dw_ext2);
      }

      return value / 30.0D;
   }

   private double extrapolate(int xsb, int ysb, double dx, double dy) {
      int index = this.perm[this.perm[xsb & 255] + ysb & 255] & 14;
      return (double)gradients2D[index] * dx + (double)gradients2D[index + 1] * dy;
   }

   private double extrapolate(int xsb, int ysb, int zsb, double dx, double dy, double dz) {
      int index = this.permGradIndex3D[this.perm[this.perm[xsb & 255] + ysb & 255] + zsb & 255];
      return (double)gradients3D[index] * dx + (double)gradients3D[index + 1] * dy + (double)gradients3D[index + 2] * dz;
   }

   private double extrapolate(int xsb, int ysb, int zsb, int wsb, double dx, double dy, double dz, double dw) {
      int index = this.perm[this.perm[this.perm[this.perm[xsb & 255] + ysb & 255] + zsb & 255] + wsb & 255] & 252;
      return (double)gradients4D[index] * dx + (double)gradients4D[index + 1] * dy + (double)gradients4D[index + 2] * dz + (double)gradients4D[index + 3] * dw;
   }

   private static int fastFloor(double x) {
      int xi = (int)x;
      return x < (double)xi ? xi - 1 : xi;
   }
}
