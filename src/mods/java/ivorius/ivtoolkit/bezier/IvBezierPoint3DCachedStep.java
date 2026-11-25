package ivorius.ivtoolkit.bezier;

import ivorius.ivtoolkit.math.IvVecMathHelper;

public class IvBezierPoint3DCachedStep {
   private IvBezierPoint3D leftPoint;
   private int leftPointIndex;
   private double leftPointProgress;
   private IvBezierPoint3D rightPoint;
   private int rightPointIndex;
   private double rightPointProgress;
   private double bezierPathProgress;
   private double innerProgress;
   private double[] cachedPosition;

   public IvBezierPoint3DCachedStep(IvBezierPoint3D leftPoint, int leftPointIndex, IvBezierPoint3D rightPoint, int rightPointIndex, double leftPointProgress, double rightPointProgress, double innerProgress) {
      this.leftPoint = leftPoint;
      this.leftPointIndex = leftPointIndex;
      this.rightPoint = rightPoint;
      this.rightPointIndex = rightPointIndex;
      this.leftPointProgress = leftPointProgress;
      this.rightPointProgress = rightPointProgress;
      this.innerProgress = innerProgress;
      this.bezierPathProgress = leftPointProgress + (rightPointProgress - leftPointProgress) * innerProgress;
   }

   public IvBezierPoint3D getLeftPoint() {
      return this.leftPoint;
   }

   public int getLeftPointIndex() {
      return this.leftPointIndex;
   }

   public IvBezierPoint3D getRightPoint() {
      return this.rightPoint;
   }

   public int getRightPointIndex() {
      return this.rightPointIndex;
   }

   public double getLeftPointProgress() {
      return this.leftPointProgress;
   }

   public double getRightPointProgress() {
      return this.rightPointProgress;
   }

   public double getBezierPathProgress() {
      return this.bezierPathProgress;
   }

   public double getInnerProgress() {
      return this.innerProgress;
   }

   public double[] getPosition() {
      if (this.cachedPosition == null) {
         double[] bezierFrom = this.leftPoint.getBezierDirectionPointFrom();
         double[] bezierTo = this.rightPoint.getBezierDirectionPointTo();
         this.cachedPosition = IvVecMathHelper.cubicMix(this.leftPoint.position, bezierFrom, bezierTo, this.rightPoint.position, this.getInnerProgress());
      }

      return this.cachedPosition;
   }
}
