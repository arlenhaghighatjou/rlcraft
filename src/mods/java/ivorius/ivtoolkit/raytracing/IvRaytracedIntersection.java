package ivorius.ivtoolkit.raytracing;

public class IvRaytracedIntersection {
   private IvRaytraceableObject hitObject;
   private Object hitInfo;
   private double[] point;

   public IvRaytracedIntersection(IvRaytraceableObject hitObject, Object hitInfo, double[] point) {
      this.hitObject = hitObject;
      this.hitInfo = hitInfo;
      this.point = point;
   }

   public Object getUserInfo() {
      return this.hitObject.userInfo;
   }

   public IvRaytraceableObject getHitObject() {
      return this.hitObject;
   }

   public Object getHitInfo() {
      return this.hitInfo;
   }

   public double getX() {
      return this.point[0];
   }

   public double getY() {
      return this.point[1];
   }

   public double getZ() {
      return this.point[2];
   }

   public double[] getPoint() {
      return (double[])this.point.clone();
   }

   public String toString() {
      return String.format("%s: [%.3f, %.3f, %.3f]", String.valueOf(this.getUserInfo()), this.getX(), this.getY(), this.getZ());
   }
}
