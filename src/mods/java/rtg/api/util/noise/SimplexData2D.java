package rtg.api.util.noise;

public abstract class SimplexData2D implements ISimplexData2D {
   private double deltaX;
   private double deltaY;

   private SimplexData2D() {
      this.clear();
   }

   public static ISimplexData2D newDisk() {
      return new SimplexData2D.Disk();
   }

   public static ISimplexData2D newDerivative() {
      return new SimplexData2D.Derivative();
   }

   public final double getDeltaX() {
      return this.deltaX;
   }

   public final void setDeltaX(double deltaX) {
      this.deltaX = deltaX;
   }

   public final double getDeltaY() {
      return this.deltaY;
   }

   public final void setDeltaY(double deltaY) {
      this.deltaY = deltaY;
   }

   public final void addToDeltaX(double val) {
      this.deltaX += val;
   }

   public final void addToDeltaY(double val) {
      this.deltaY += val;
   }

   public final void clear() {
      this.setDeltaX(0.0D);
      this.setDeltaY(0.0D);
   }

   public static final class Derivative extends SimplexData2D implements ISimplexData2D {
      private Derivative() {
      }

      public ISimplexData2D.DataRequest request() {
         return (attn, extrapolation, gx, gy, gi_sph2, dx, dy) -> {
            double attnSq = Math.pow(attn, 2.0D);
            this.addToDeltaX((gx * attn - 8.0D * dx * extrapolation) * attnSq * attn);
            this.addToDeltaY((gy * attn - 8.0D * dy * extrapolation) * attnSq * attn);
         };
      }
   }

   public static final class Disk extends SimplexData2D implements ISimplexData2D {
      private Disk() {
      }

      public ISimplexData2D.DataRequest request() {
         return (attn, extrapolation, gx, gy, gi_sph2, dx, dy) -> {
            double attnSq = Math.pow(attn, 2.0D);
            double extrap = Math.pow(attnSq, 2.0D) * extrapolation;
            this.addToDeltaX(extrap * OpenSimplexNoise.GRADIENTS_SPH2[gi_sph2]);
            this.addToDeltaY(extrap * OpenSimplexNoise.GRADIENTS_SPH2[gi_sph2 + 1]);
         };
      }
   }
}
