package ivorius.ivtoolkit.bezier;

public class IvBezierPoint3D {
   public double[] position;
   public double[] bezierDirection;
   public double rotation;
   public int color;
   public double fontSize;

   public IvBezierPoint3D(double[] position, double[] bezierDirection, int color, double rotation) {
      this.position = position;
      this.bezierDirection = bezierDirection;
      this.color = color;
      this.rotation = rotation;
      this.fontSize = 1.0D;
   }

   public IvBezierPoint3D(double[] position, double[] bezierDirection, int color, double rotation, double fontSize) {
      this(position, bezierDirection, color, rotation);
      this.fontSize = fontSize;
   }

   public double getAlpha() {
      return (double)(this.color >> 24 & 255) / 255.0D;
   }

   public double getRed() {
      return (double)(this.color >> 16 & 255) / 255.0D;
   }

   public double getGreen() {
      return (double)(this.color >> 8 & 255) / 255.0D;
   }

   public double getBlue() {
      return (double)(this.color & 255) / 255.0D;
   }

   public double[] getBezierDirectionPointTo() {
      double[] result = new double[this.bezierDirection.length];

      for(int i = 0; i < result.length; ++i) {
         result[i] = this.position[i] - this.bezierDirection[i];
      }

      return result;
   }

   public double[] getBezierDirectionPointFrom() {
      double[] result = new double[this.bezierDirection.length];

      for(int i = 0; i < result.length; ++i) {
         result[i] = this.position[i] + this.bezierDirection[i];
      }

      return result;
   }

   public double getFontSize() {
      return this.fontSize;
   }
}
