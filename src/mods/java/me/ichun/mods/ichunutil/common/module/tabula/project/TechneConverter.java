package me.ichun.mods.ichunutil.common.module.tabula.project;

public class TechneConverter {
   public static TechneConverter.Rotation fromTechne(TechneConverter.Rotation rotation) {
      TechneConverter.Matrix3x3 rotationMatrixX = TechneConverter.Matrix3x3.createRotationMatrixX(rotation.getRadiansX());
      TechneConverter.Matrix3x3 rotationMatrixY = TechneConverter.Matrix3x3.createRotationMatrixY(rotation.getRadiansY());
      TechneConverter.Matrix3x3 rotationMatrixZ = TechneConverter.Matrix3x3.createRotationMatrixZ(rotation.getRadiansZ());
      TechneConverter.Matrix3x3 combinedRotation = rotationMatrixY.multiply(rotationMatrixZ).multiply(rotationMatrixX);
      return TechneConverter.Rotation.createFromMatrixZYX(combinedRotation);
   }

   public static TechneConverter.Rotation toTechne(TechneConverter.Rotation rotation) {
      TechneConverter.Matrix3x3 rotationMatrixX = TechneConverter.Matrix3x3.createRotationMatrixX(rotation.getRadiansX());
      TechneConverter.Matrix3x3 rotationMatrixY = TechneConverter.Matrix3x3.createRotationMatrixY(rotation.getRadiansY());
      TechneConverter.Matrix3x3 rotationMatrixZ = TechneConverter.Matrix3x3.createRotationMatrixZ(rotation.getRadiansZ());
      TechneConverter.Matrix3x3 combinedRotation = rotationMatrixZ.multiply(rotationMatrixY).multiply(rotationMatrixX);
      return TechneConverter.Rotation.createFromMatrixYZX(combinedRotation);
   }

   private TechneConverter() {
   }

   public static class Matrix3x3 {
      private double[] data;

      private Matrix3x3(double... data) {
         this.data = data;
      }

      public static TechneConverter.Matrix3x3 createRotationMatrixX(double angle) {
         double c = Math.cos(angle);
         double s = Math.sin(angle);
         return new TechneConverter.Matrix3x3(new double[]{1.0D, 0.0D, 0.0D, 0.0D, c, -s, 0.0D, s, c});
      }

      public static TechneConverter.Matrix3x3 createRotationMatrixY(double angle) {
         double c = Math.cos(angle);
         double s = Math.sin(angle);
         return new TechneConverter.Matrix3x3(new double[]{c, 0.0D, s, 0.0D, 1.0D, 0.0D, -s, 0.0D, c});
      }

      public static TechneConverter.Matrix3x3 createRotationMatrixZ(double angle) {
         double c = Math.cos(angle);
         double s = Math.sin(angle);
         return new TechneConverter.Matrix3x3(new double[]{c, -s, 0.0D, s, c, 0.0D, 0.0D, 0.0D, 1.0D});
      }

      public TechneConverter.Matrix3x3 multiply(TechneConverter.Matrix3x3 other) {
         TechneConverter.Matrix3x3 result = new TechneConverter.Matrix3x3(new double[9]);

         for(int x = 0; x < 3; ++x) {
            for(int y = 0; y < 3; ++y) {
               double val = 0.0D;

               for(int i = 0; i < 3; ++i) {
                  val += this.getValue(i, y) * other.getValue(x, i);
               }

               result.setValue(x, y, val);
            }
         }

         return result;
      }

      private void setValue(int x, int y, double val) {
         this.data[x + y * 3] = val;
      }

      public double getValue(int x, int y) {
         return this.data[x + y * 3];
      }

      public void debug() {
         System.out.println();
         System.out.println("[" + this.data[0] + ", " + this.data[1] + ", " + this.data[2]);
         System.out.println(this.data[3] + ", " + this.data[4] + ", " + this.data[5]);
         System.out.println(this.data[6] + ", " + this.data[7] + ", " + this.data[8] + "]");
      }
   }

   public static class Rotation {
      private double rotationX;
      private double rotationY;
      private double rotationZ;

      private Rotation(double rotationX, double rotationY, double rotationZ) {
         this.rotationX = rotationX;
         this.rotationY = rotationY;
         this.rotationZ = rotationZ;
      }

      public static TechneConverter.Rotation createFromRadians(double rotationX, double rotationY, double rotationZ) {
         return new TechneConverter.Rotation(rotationX, rotationY, rotationZ);
      }

      public static TechneConverter.Rotation createFromDegrees(double rotationX, double rotationY, double rotationZ) {
         return new TechneConverter.Rotation(Math.toRadians(rotationX), Math.toRadians(rotationY), Math.toRadians(rotationZ));
      }

      public double getRadiansX() {
         return this.rotationX;
      }

      public double getRadiansY() {
         return this.rotationY;
      }

      public double getRadiansZ() {
         return this.rotationZ;
      }

      public double getDegreesX() {
         return Math.toDegrees(this.rotationX);
      }

      public double getDegreesY() {
         return Math.toDegrees(this.rotationY);
      }

      public double getDegreesZ() {
         return Math.toDegrees(this.rotationZ);
      }

      public static TechneConverter.Rotation createFromMatrixZYX(TechneConverter.Matrix3x3 matrix) {
         TechneConverter.Rotation result = new TechneConverter.Rotation(0.0D, 0.0D, 0.0D);
         if (matrix.getValue(0, 2) < 1.0D) {
            if (matrix.getValue(0, 2) > -1.0D) {
               result.rotationY = Math.asin(-matrix.getValue(0, 2));
               result.rotationZ = Math.atan2(matrix.getValue(0, 1), matrix.getValue(0, 0));
               result.rotationX = Math.atan2(matrix.getValue(1, 2), matrix.getValue(2, 2));
            } else {
               result.rotationY = 1.5707963267948966D;
               result.rotationZ = -Math.atan2(-matrix.getValue(2, 1), matrix.getValue(1, 1));
               result.rotationX = 0.0D;
            }
         } else {
            result.rotationY = -1.5707963267948966D;
            result.rotationZ = Math.atan2(-matrix.getValue(2, 1), matrix.getValue(1, 1));
            result.rotationX = 0.0D;
         }

         return result;
      }

      public static TechneConverter.Rotation createFromMatrixYZX(TechneConverter.Matrix3x3 matrix) {
         TechneConverter.Rotation result = new TechneConverter.Rotation(0.0D, 0.0D, 0.0D);
         if (matrix.getValue(0, 1) < 1.0D) {
            if (matrix.getValue(0, 1) > -1.0D) {
               result.rotationZ = Math.asin(matrix.getValue(0, 1));
               result.rotationY = Math.atan2(-matrix.getValue(0, 2), matrix.getValue(0, 0));
               result.rotationX = Math.atan2(-matrix.getValue(2, 1), matrix.getValue(1, 1));
            } else {
               result.rotationZ = -1.5707963267948966D;
               result.rotationY = -Math.atan2(matrix.getValue(1, 2), matrix.getValue(2, 2));
               result.rotationX = 0.0D;
            }
         } else {
            result.rotationZ = 1.5707963267948966D;
            result.rotationY = Math.atan2(matrix.getValue(1, 2), matrix.getValue(2, 2));
            result.rotationX = 0.0D;
         }

         return result;
      }
   }
}
