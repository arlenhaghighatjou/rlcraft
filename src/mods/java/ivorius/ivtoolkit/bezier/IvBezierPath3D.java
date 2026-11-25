package ivorius.ivtoolkit.bezier;

import ivorius.ivtoolkit.math.IvVecMathHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IvBezierPath3D {
   private List<IvBezierPoint3D> bezierPoints;
   private List<Double> cachedDistances = new ArrayList();
   private double cachedFullDistance;
   private List<Double> cachedProgresses = new ArrayList();
   private boolean isDirty;

   public IvBezierPath3D() {
      this.setBezierPoints(new ArrayList());
   }

   public IvBezierPath3D(List<IvBezierPoint3D> bezierPoints) {
      this.setBezierPoints(bezierPoints);
   }

   public void buildDistances() {
      this.isDirty = false;
      this.cachedFullDistance = 0.0D;
      this.cachedDistances.clear();
      this.cachedProgresses.clear();
      IvBezierPoint3D previousPoint = null;

      for(IvBezierPoint3D bezierPoint : this.bezierPoints) {
         if (previousPoint != null) {
            double distance = 0.0D;
            int samples = 50;

            for(int i = 0; i < samples; ++i) {
               double[] bezierFrom = previousPoint.getBezierDirectionPointFrom();
               double[] bezierTo = bezierPoint.getBezierDirectionPointTo();
               double[] position = IvVecMathHelper.cubicMix(previousPoint.position, bezierFrom, bezierTo, bezierPoint.position, (double)i / (double)samples);
               double[] position1 = IvVecMathHelper.cubicMix(previousPoint.position, bezierFrom, bezierTo, bezierPoint.position, (double)(i + 1) / (double)samples);
               distance += IvVecMathHelper.distance(position, position1);
            }

            this.cachedFullDistance += distance;
            this.cachedDistances.add(Double.valueOf(distance));
         }

         previousPoint = bezierPoint;
      }

      this.cachedProgresses.addAll((Collection)this.cachedDistances.stream().map((d) -> {
         return d.doubleValue() / this.cachedFullDistance;
      }).collect(Collectors.toList()));
   }

   public double getPathLengthInRange(int startIndex, int endIndex) {
      double maxProgress = 0.0D;
      int arraySize = this.cachedProgresses.size();

      for(int i = startIndex; i < endIndex; ++i) {
         maxProgress += ((Double)this.cachedProgresses.get(i % arraySize)).doubleValue();
      }

      return maxProgress;
   }

   public double getPathLength() {
      return this.cachedFullDistance;
   }

   private IvBezierPoint3DCachedStep getCachedStep(int leftIndex, int rightIndex, double leftProgress, double rightProgress, double innerProgress) {
      return new IvBezierPoint3DCachedStep((IvBezierPoint3D)this.bezierPoints.get(leftIndex), leftIndex, (IvBezierPoint3D)this.bezierPoints.get(rightIndex), rightIndex, leftProgress, rightProgress, innerProgress);
   }

   public IvBezierPoint3DCachedStep getCachedStep(int leftIndex, int rightIndex, double innerProgress) {
      double leftProgress = this.getPathLengthInRange(0, leftIndex);
      double rightProgress = this.getPathLengthInRange(0, rightIndex);
      return this.getCachedStep(leftIndex, rightIndex, leftProgress, rightProgress, innerProgress);
   }

   public IvBezierPoint3DCachedStep getCachedStep(double progress) {
      progress = (progress % 1.0D + 1.0D) % 1.0D;
      double curProgress = 0.0D;

      for(int i = 1; i < this.bezierPoints.size(); ++i) {
         double distance = ((Double)this.cachedProgresses.get(i - 1)).doubleValue();
         if (progress - distance <= 0.0D) {
            return this.getCachedStep(i - 1, i, curProgress, curProgress + distance, progress / distance);
         }

         progress -= distance;
         curProgress += distance;
      }

      int bezierPointsLength = this.bezierPoints.size();
      return this.getCachedStep(bezierPointsLength - 2, bezierPointsLength - 1, 1.0D);
   }

   public IvBezierPoint3DCachedStep getCachedStepAfterStep(IvBezierPoint3DCachedStep cachedStep, double stepSize) {
      return this.getCachedStep(cachedStep.getBezierPathProgress() + stepSize);
   }

   public double[] getMotion(IvBezierPoint3DCachedStep cachedStep, IvBezierPoint3DCachedStep cachedStep1) {
      return IvVecMathHelper.sub(cachedStep1.getPosition(), cachedStep.getPosition());
   }

   public double[] getPVector(IvBezierPoint3DCachedStep cachedStep, double stepSize) {
      double[] motion1 = this.getMotion(cachedStep, this.getCachedStepAfterStep(cachedStep, stepSize * 0.3D));
      double[] motion2 = this.getMotion(cachedStep, this.getCachedStepAfterStep(cachedStep, stepSize * 0.6D));
      double[] pVector = IvVecMathHelper.crossProduct(motion1, motion2);
      return pVector;
   }

   public double[] getNaturalRotation(IvBezierPoint3DCachedStep cachedStep, double stepSize) {
      double[] motion1 = this.getMotion(cachedStep, this.getCachedStepAfterStep(cachedStep, stepSize * 0.3D));
      double[] spherical = IvVecMathHelper.sphericalFromCartesian(motion1);
      return new double[]{-spherical[0] / 3.141592653589793D * 180.0D, spherical[1] / 3.141592653589793D * 180.0D + 90.0D};
   }

   public void markDirty() {
      this.isDirty = true;
   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public void setBezierPoints(List<IvBezierPoint3D> points) {
      this.bezierPoints = new ArrayList();
      this.bezierPoints.addAll(points);
      this.markDirty();
   }

   public List<IvBezierPoint3D> getBezierPoints() {
      List<IvBezierPoint3D> l = new ArrayList(this.bezierPoints.size());
      l.addAll(this.bezierPoints);
      return l;
   }

   public void removeBezierPoint(IvBezierPoint3D point) {
      this.bezierPoints.remove(point);
      this.markDirty();
   }

   public void addBezierPoint(IvBezierPoint3D point) {
      this.bezierPoints.add(point);
      this.markDirty();
   }

   public void addBezierPoints(List<IvBezierPoint3D> points) {
      this.bezierPoints.addAll(points);
      this.markDirty();
   }
}
