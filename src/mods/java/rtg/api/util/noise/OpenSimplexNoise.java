package rtg.api.util.noise;

public class OpenSimplexNoise implements SimplexNoise {
   static final double[] GRADIENTS_2D = new double[]{0.114251372530929D, 0.065963060686016D, 0.131926121372032D, 0.0D, 0.114251372530929D, -0.065963060686016D, 0.065963060686016D, -0.114251372530929D, 0.0D, -0.131926121372032D, -0.065963060686016D, -0.114251372530929D, -0.114251372530929D, -0.065963060686016D, -0.131926121372032D, -0.0D, -0.114251372530929D, 0.065963060686016D, -0.065963060686016D, 0.114251372530929D, -0.0D, 0.131926121372032D, 0.065963060686016D, 0.114251372530929D};
   static final double[] GRADIENTS_SPH2 = new double[]{0.0D, 1.0D, 0.5D, 0.866025403784439D, 0.866025403784439D, 0.5D, 1.0D, 0.0D, 0.866025403784439D, -0.5D, 0.5D, -0.866025403784439D, 0.0D, -1.0D, -0.5D, -0.866025403784439D, -0.866025403784439D, -0.5D, -1.0D, 0.0D, -0.866025403784439D, 0.5D, -0.5D, 0.866025403784439D};
   static final double[] GRADIENTS_3D = new double[]{-0.00919201927982D, 0.061948581592974D, 0.10551312462631D, 0.061948581592974D, -0.00919201927982D, 0.10551312462631D, 0.052339395980958D, 0.052339395980958D, 0.097858646551677D, 0.002784312704445D, 0.002784312704445D, 0.122636188189934D, -0.00919201927982D, 0.10551312462631D, 0.061948581592974D, 0.061948581592974D, 0.10551312462631D, -0.00919201927982D, 0.052339395980958D, 0.097858646551677D, 0.052339395980958D, 0.002784312704445D, 0.122636188189934D, 0.002784312704445D, 0.10551312462631D, -0.00919201927982D, 0.061948581592974D, 0.10551312462631D, 0.061948581592974D, -0.00919201927982D, 0.097858646551677D, 0.052339395980958D, 0.052339395980958D, 0.122636188189934D, 0.002784312704445D, 0.002784312704445D, -0.0672780766576D, 0.090991610281865D, 0.047427067248529D, -0.090991610281865D, 0.0672780766576D, -0.047427067248529D, -0.057908021389848D, 0.107463104666361D, -0.012388770819128D, -0.107463104666361D, 0.057908021389848D, 0.012388770819128D, -0.0672780766576D, 0.047427067248529D, 0.090991610281865D, -0.090991610281865D, -0.047427067248529D, 0.0672780766576D, -0.057908021389848D, -0.012388770819128D, 0.107463104666361D, -0.107463104666361D, 0.012388770819128D, 0.057908021389848D, 0.047427067248529D, -0.0672780766576D, 0.090991610281865D, -0.047427067248529D, -0.090991610281865D, 0.0672780766576D, -0.012388770819128D, -0.057908021389848D, 0.107463104666361D, 0.012388770819128D, -0.107463104666361D, 0.057908021389848D, 0.0672780766576D, -0.090991610281865D, -0.047427067248529D, 0.090991610281865D, -0.0672780766576D, 0.047427067248529D, 0.107463104666361D, -0.057908021389848D, -0.012388770819128D, 0.057908021389848D, -0.107463104666361D, 0.012388770819128D, 0.0672780766576D, -0.047427067248529D, -0.090991610281865D, 0.090991610281865D, 0.047427067248529D, -0.0672780766576D, 0.107463104666361D, -0.012388770819128D, -0.057908021389848D, 0.057908021389848D, 0.012388770819128D, -0.107463104666361D, -0.047427067248529D, 0.0672780766576D, -0.090991610281865D, 0.047427067248529D, 0.090991610281865D, -0.0672780766576D, -0.012388770819128D, 0.107463104666361D, -0.057908021389848D, 0.012388770819128D, 0.057908021389848D, -0.107463104666361D, 0.00919201927982D, -0.061948581592974D, -0.10551312462631D, -0.061948581592974D, 0.00919201927982D, -0.10551312462631D, -0.002784312704445D, -0.002784312704445D, -0.122636188189934D, -0.052339395980958D, -0.052339395980958D, -0.097858646551677D, 0.00919201927982D, -0.10551312462631D, -0.061948581592974D, -0.061948581592974D, -0.10551312462631D, 0.00919201927982D, -0.002784312704445D, -0.122636188189934D, -0.002784312704445D, -0.052339395980958D, -0.097858646551677D, -0.052339395980958D, -0.10551312462631D, 0.00919201927982D, -0.061948581592974D, -0.10551312462631D, -0.061948581592974D, 0.00919201927982D, -0.122636188189934D, -0.002784312704445D, -0.002784312704445D, -0.097858646551677D, -0.052339395980958D, -0.052339395980958D};
   private static final double STRETCH_2D = -0.211324865405187D;
   private static final double SQUISH_2D = 0.366025403784439D;
   private static final double STRETCH_3D = -0.16666666666666666D;
   private static final double SQUISH_3D = 0.3333333333333333D;
   private static final OpenSimplexNoise.LatticePoint2D[] LOOKUP_2D = new OpenSimplexNoise.LatticePoint2D[32];
   private static final OpenSimplexNoise.Contribution3D[] LOOKUP_3D;
   private int[] perm = new int[1024];
   private int[] perm2D = new int[1024];
   private int[] perm2D_sph2 = new int[1024];
   private int[] perm3D = new int[1024];

   public OpenSimplexNoise(long seed) {
      int[] source = new int[1024];

      for(int i = 0; i < 1024; source[i] = i++) {
         ;
      }

      for(int i = 1023; i >= 0; --i) {
         seed = seed * 6364136223846793005L + 1442695040888963407L;
         int r = (int)((seed + 31L) % (long)(i + 1));
         if (r < 0) {
            r += i + 1;
         }

         this.perm[i] = source[r];
         this.perm2D[i] = this.perm[i] % 12 * 2;
         this.perm2D_sph2[i] = this.perm[i] / 12 % 12 * 2;
         this.perm3D[i] = this.perm[i] % 48 * 3;
         source[r] = source[i];
      }

   }

   private static int fastFloor(double d) {
      int i;
      return d < (double)(i = (int)d) ? i - 1 : i;
   }

   public float noise2f(float x, float y) {
      return (float)this.noise2d((double)x, (double)y);
   }

   public float noise3f(float x, float y, float z) {
      return (float)this.noise3d((double)x, (double)y, (double)z);
   }

   public double noise2d(double x, double y) {
      double value = 0.0D;
      double s = -0.211324865405187D * (x + y);
      double xs = x + s;
      double ys = y + s;
      int xsb = fastFloor(xs);
      int ysb = fastFloor(ys);
      double xsi = xs - (double)xsb;
      double ysi = ys - (double)ysb;
      int a = (int)(ysi - xsi + 1.0D);
      int index = a << 2 | (int)(xsi + ysi / 2.0D + (double)a / 2.0D) << 3 | (int)(ysi + xsi / 2.0D + 0.5D - (double)a / 2.0D) << 4;
      double ssi = (xsi + ysi) * 0.366025403784439D;
      double xi = xsi + ssi;
      double yi = ysi + ssi;

      for(int i = 0; i < 4; ++i) {
         OpenSimplexNoise.LatticePoint2D lattice = LOOKUP_2D[index + i];
         double dx = xi + lattice.getDx();
         double dy = yi + lattice.getDy();
         double attn = 2.0D - Math.pow(dx, 2.0D) - Math.pow(dy, 2.0D);
         if (attn > 0.0D) {
            int pxm = xsb + lattice.getXsv() & 1023;
            int pym = ysb + lattice.getYsv() & 1023;
            int gi = this.perm2D[this.perm[pxm] ^ pym];
            double extrp = GRADIENTS_2D[gi] * dx + GRADIENTS_2D[gi + 1] * dy;
            value += Math.pow(attn, 4.0D) * extrp;
         }
      }

      return value;
   }

   public double noise3d(double x, double y, double z) {
      double stretchOffset = (x + y + z) * -0.16666666666666666D;
      double xs = x + stretchOffset;
      double ys = y + stretchOffset;
      double zs = z + stretchOffset;
      int xsb = fastFloor(xs);
      int ysb = fastFloor(ys);
      int zsb = fastFloor(zs);
      double squishOffset = (double)(xsb + ysb + zsb) * 0.3333333333333333D;
      double dx0 = x - ((double)xsb + squishOffset);
      double dy0 = y - ((double)ysb + squishOffset);
      double dz0 = z - ((double)zsb + squishOffset);
      double xins = xs - (double)xsb;
      double yins = ys - (double)ysb;
      double zins = zs - (double)zsb;
      double inSum = xins + yins + zins;
      int hash = (int)(yins - zins + 1.0D) | (int)(xins - yins + 1.0D) << 1 | (int)(xins - zins + 1.0D) << 2 | (int)inSum << 3 | (int)(inSum + zins) << 5 | (int)(inSum + yins) << 7 | (int)(inSum + xins) << 9;
      OpenSimplexNoise.Contribution3D c = LOOKUP_3D[hash];

      double value;
      for(value = 0.0D; c != null; c = c.getNext()) {
         double dx = dx0 + c.getDx();
         double dy = dy0 + c.getDy();
         double dz = dz0 + c.getDz();
         double attn = 2.0D - dx * dx - dy * dy - dz * dz;
         if (attn > 0.0D) {
            int px = xsb + c.getXsb();
            int py = ysb + c.getYsb();
            int pz = zsb + c.getZsb();
            int i = this.perm3D[(this.perm[(this.perm[px & 1023] ^ py) & 1023] ^ pz) & 1023];
            double valuePart = GRADIENTS_3D[i] * dx + GRADIENTS_3D[i + 1] * dy + GRADIENTS_3D[i + 2] * dz;
            value += Math.pow(attn, 4.0D) * valuePart;
         }
      }

      return value;
   }

   public void multiEval2D(double x, double y, ISimplexData2D data) {
      double s = -0.211324865405187D * (x + y);
      double xs = x + s;
      double ys = y + s;
      int xsb = fastFloor(xs);
      int ysb = fastFloor(ys);
      double xsi = xs - (double)xsb;
      double ysi = ys - (double)ysb;
      int a = (int)(ysi - xsi + 1.0D);
      int index = a << 2 | (int)(xsi + ysi / 2.0D + (double)a / 2.0D) << 3 | (int)(ysi + xsi / 2.0D + 0.5D - (double)a / 2.0D) << 4;
      double ssi = (xsi + ysi) * 0.366025403784439D;
      double xi = xsi + ssi;
      double yi = ysi + ssi;
      data.clear();

      for(int i = 0; i < 4; ++i) {
         OpenSimplexNoise.LatticePoint2D lattice = LOOKUP_2D[index + i];
         double dx = xi + lattice.getDx();
         double dy = yi + lattice.getDy();
         double attn = 2.0D - Math.pow(dx, 2.0D) - Math.pow(dy, 2.0D);
         if (attn > 0.0D) {
            int pxm = xsb + lattice.getXsv() & 1023;
            int pym = ysb + lattice.getYsv() & 1023;
            int gi_p = this.perm[pxm] ^ pym;
            int gi = this.perm2D[gi_p];
            double gx = GRADIENTS_2D[gi];
            double gy = GRADIENTS_2D[gi + 1];
            double extrp = gx * dx + gy * dy;
            int gi_sph2 = this.perm2D_sph2[gi_p];
            data.request().apply(attn, extrp, gx, gy, gi_sph2, dx, dy);
         }
      }

   }

   static {
      for(int i = 0; i < 8; ++i) {
         int i1;
         int j1;
         int i2;
         int j2;
         if ((i & 1) == 0) {
            if ((i & 2) == 0) {
               i1 = 0;
               j1 = 0;
            } else {
               i1 = 2;
               j1 = 0;
            }

            if ((i & 4) == 0) {
               i2 = 1;
               j2 = -1;
            } else {
               i2 = 1;
               j2 = 1;
            }
         } else {
            if ((i & 2) == 0) {
               i1 = -1;
               j1 = 1;
            } else {
               i1 = 1;
               j1 = 1;
            }

            if ((i & 4) == 0) {
               i2 = 0;
               j2 = 0;
            } else {
               i2 = 0;
               j2 = 2;
            }
         }

         LOOKUP_2D[i * 4] = new OpenSimplexNoise.LatticePoint2D(1, 0);
         LOOKUP_2D[i * 4 + 1] = new OpenSimplexNoise.LatticePoint2D(0, 1);
         LOOKUP_2D[i * 4 + 2] = new OpenSimplexNoise.LatticePoint2D(i1, j1);
         LOOKUP_2D[i * 4 + 3] = new OpenSimplexNoise.LatticePoint2D(i2, j2);
      }

      int[][] base3D = new int[][]{{0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1}, {2, 1, 1, 0, 2, 1, 0, 1, 2, 0, 1, 1, 3, 1, 1, 1}, {1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 2, 1, 1, 0, 2, 1, 0, 1, 2, 0, 1, 1}};
      int[] p3D = new int[]{0, 0, 1, -1, 0, 0, 1, 0, -1, 0, 0, -1, 1, 0, 0, 0, 1, -1, 0, 0, -1, 0, 1, 0, 0, -1, 1, 0, 2, 1, 1, 0, 1, 1, 1, -1, 0, 2, 1, 0, 1, 1, 1, -1, 1, 0, 2, 0, 1, 1, 1, -1, 1, 1, 1, 3, 2, 1, 0, 3, 1, 2, 0, 1, 3, 2, 0, 1, 3, 1, 0, 2, 1, 3, 0, 2, 1, 3, 0, 1, 2, 1, 1, 1, 0, 0, 2, 2, 0, 0, 1, 1, 0, 1, 0, 2, 0, 2, 0, 1, 1, 0, 0, 1, 2, 0, 0, 2, 2, 0, 0, 0, 0, 1, 1, -1, 1, 2, 0, 0, 0, 0, 1, -1, 1, 1, 2, 0, 0, 0, 0, 1, 1, 1, -1, 2, 3, 1, 1, 1, 2, 0, 0, 2, 2, 3, 1, 1, 1, 2, 2, 0, 0, 2, 3, 1, 1, 1, 2, 0, 2, 0, 2, 1, 1, -1, 1, 2, 0, 0, 2, 2, 1, 1, -1, 1, 2, 2, 0, 0, 2, 1, -1, 1, 1, 2, 0, 0, 2, 2, 1, -1, 1, 1, 2, 0, 2, 0, 2, 1, 1, 1, -1, 2, 2, 0, 0, 2, 1, 1, 1, -1, 2, 0, 2, 0};
      int[] lookupPairs3D = new int[]{0, 2, 1, 1, 2, 2, 5, 1, 6, 0, 7, 0, 32, 2, 34, 2, 129, 1, 133, 1, 160, 5, 161, 5, 518, 0, 519, 0, 546, 4, 550, 4, 645, 3, 647, 3, 672, 5, 673, 5, 674, 4, 677, 3, 678, 4, 679, 3, 680, 13, 681, 13, 682, 12, 685, 14, 686, 12, 687, 14, 712, 20, 714, 18, 809, 21, 813, 23, 840, 20, 841, 21, 1198, 19, 1199, 22, 1226, 18, 1230, 19, 1325, 23, 1327, 22, 1352, 15, 1353, 17, 1354, 15, 1357, 17, 1358, 16, 1359, 16, 1360, 11, 1361, 10, 1362, 11, 1365, 10, 1366, 9, 1367, 9, 1392, 11, 1394, 11, 1489, 10, 1493, 10, 1520, 8, 1521, 8, 1878, 9, 1879, 9, 1906, 7, 1910, 7, 2005, 6, 2007, 6, 2032, 8, 2033, 8, 2034, 7, 2037, 6, 2038, 7, 2039, 6};
      OpenSimplexNoise.Contribution3D[] contributions3D = new OpenSimplexNoise.Contribution3D[p3D.length / 9];

      for(int i = 0; i < p3D.length; i += 9) {
         int[] baseSet = base3D[p3D[i]];
         OpenSimplexNoise.Contribution3D previous = null;
         OpenSimplexNoise.Contribution3D current = null;

         for(int j = 0; j < baseSet.length; j += 4) {
            current = new OpenSimplexNoise.Contribution3D((double)baseSet[j], baseSet[j + 1], baseSet[j + 2], baseSet[j + 3]);
            if (previous == null) {
               contributions3D[i / 9] = current;
            } else {
               previous.setNext(current);
            }

            previous = current;
         }

         if (current != null) {
            current.setNext(new OpenSimplexNoise.Contribution3D((double)p3D[i + 1], p3D[i + 2], p3D[i + 3], p3D[i + 4]));
            current.getNext().setNext(new OpenSimplexNoise.Contribution3D((double)p3D[i + 5], p3D[i + 6], p3D[i + 7], p3D[i + 8]));
         }
      }

      LOOKUP_3D = new OpenSimplexNoise.Contribution3D[2048];

      for(int i = 0; i < lookupPairs3D.length; i += 2) {
         LOOKUP_3D[lookupPairs3D[i]] = contributions3D[lookupPairs3D[i + 1]];
      }

   }

   private static final class Contribution3D {
      private int xsb;
      private int ysb;
      private int zsb;
      private double dx;
      private double dy;
      private double dz;
      private OpenSimplexNoise.Contribution3D next;

      private Contribution3D(double multiplier, int xsb, int ysb, int zsb) {
         this.xsb = xsb;
         this.ysb = ysb;
         this.zsb = zsb;
         this.dx = (double)(-xsb) - multiplier * 0.3333333333333333D;
         this.dy = (double)(-ysb) - multiplier * 0.3333333333333333D;
         this.dz = (double)(-zsb) - multiplier * 0.3333333333333333D;
      }

      private OpenSimplexNoise.Contribution3D getNext() {
         return this.next;
      }

      private void setNext(OpenSimplexNoise.Contribution3D next) {
         this.next = next;
      }

      private double getDx() {
         return this.dx;
      }

      private double getDy() {
         return this.dy;
      }

      private double getDz() {
         return this.dz;
      }

      private int getXsb() {
         return this.xsb;
      }

      private int getYsb() {
         return this.ysb;
      }

      private int getZsb() {
         return this.zsb;
      }
   }

   private static final class LatticePoint2D {
      private int xsv;
      private int ysv;
      private double dx;
      private double dy;

      private LatticePoint2D(int xsv, int ysv) {
         this.xsv = xsv;
         this.ysv = ysv;
         this.dx = (double)(-xsv) - (double)(xsv + ysv) * 0.366025403784439D;
         this.dy = (double)(-ysv) - (double)(xsv + ysv) * 0.366025403784439D;
      }

      public int getXsv() {
         return this.xsv;
      }

      public int getYsv() {
         return this.ysv;
      }

      public double getDx() {
         return this.dx;
      }

      public double getDy() {
         return this.dy;
      }
   }
}
