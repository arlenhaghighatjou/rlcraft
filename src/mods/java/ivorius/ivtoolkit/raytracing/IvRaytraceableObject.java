package ivorius.ivtoolkit.raytracing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class IvRaytraceableObject {
   public Object userInfo;
   private List<IvRaytraceableObject> containedObjects;
   private boolean isContainer;

   public IvRaytraceableObject(Object userInfo) {
      this.userInfo = userInfo;
      this.containedObjects = new ArrayList();
   }

   public void addIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir) {
      if (this.isContainer()) {
         ArrayList<IvRaytracedIntersection> intersections = new ArrayList();
         this.addRaytracedIntersectionsForLineToList(intersections, x, y, z, xDir, yDir, zDir);
         if (intersections.size() > 0) {
            new ArrayList();

            for(IvRaytraceableObject subObject : this.containedObjects) {
               subObject.addIntersectionsForLineToList(list, x, y, z, xDir, yDir, zDir);
            }
         }
      } else {
         this.addRaytracedIntersectionsForLineToList(list, x, y, z, xDir, yDir, zDir);
      }

   }

   public abstract void addRaytracedIntersectionsForLineToList(List<IvRaytracedIntersection> var1, double var2, double var4, double var6, double var8, double var10, double var12);

   public double[][] getRaytracedIntersectionsForLine(double x, double y, double z, double xDir, double yDir, double zDir) {
      ArrayList<IvRaytracedIntersection> intersections = new ArrayList();
      this.addRaytracedIntersectionsForLineToList(intersections, x, y, z, xDir, yDir, zDir);
      double[][] intersectionPoints = new double[intersections.size()][];

      for(int i = 0; i < intersections.size(); ++i) {
         intersectionPoints[i] = ((IvRaytracedIntersection)intersections.get(i)).getPoint();
      }

      return intersectionPoints;
   }

   public IvRaytracedIntersection getFirstHitPointForLine(double x, double y, double z, double xDir, double yDir, double zDir) {
      ArrayList<IvRaytracedIntersection> intersections = new ArrayList();
      this.addRaytracedIntersectionsForLineToList(intersections, x, y, z, xDir, yDir, zDir);
      return IvRaytracer.findFirstIntersection(intersections, x, y, z, xDir, yDir, zDir);
   }

   public boolean isContainer() {
      return this.isContainer;
   }

   public void setIsContainer(boolean isContainer) {
      this.isContainer = isContainer;
   }

   public void addRaytracedObjectToContainer(IvRaytraceableObject object) {
      if (!this.containedObjects.contains(object)) {
         this.containedObjects.add(object);
      }

   }

   public void removeRaytracedObjectFromContainer(IvRaytraceableObject object) {
      this.containedObjects.remove(object);
   }

   public void clearRaytracedObjectsFromContainer() {
      this.containedObjects.clear();
   }

   public void drawOutlines() {
      this.containedObjects.forEach(IvRaytraceableObject::drawOutlines);
   }
}
