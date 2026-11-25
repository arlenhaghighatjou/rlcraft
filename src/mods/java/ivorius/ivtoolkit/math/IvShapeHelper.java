package ivorius.ivtoolkit.math;

public class IvShapeHelper {
   public IvShapeHelper() {
   }

   public static boolean isPointInSpheroid(double[] point, double[] spheroidOrigin, double[] spheroidRadius) {
      double totalDist = 0.0D;

      for(int i = 0; i < point.length; ++i) {
         double dist = (point[i] - spheroidOrigin[i]) / spheroidRadius[i];
         totalDist += dist * dist;
      }

      return totalDist < 1.0D;
   }
}
