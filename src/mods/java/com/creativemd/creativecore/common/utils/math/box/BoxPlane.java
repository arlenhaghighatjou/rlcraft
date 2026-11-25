package com.creativemd.creativecore.common.utils.math.box;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing.Axis;

public class BoxPlane {
   public final BoxFace face;
   public final Vector3d normal;
   public final Vector3d origin;

   public BoxPlane(Vector3d[] corners, BoxFace face) {
      this.face = face;
      this.origin = face.first(corners);
      this.normal = face.normal(corners);
   }

   public double getIntersectingScale(Vector3d rayOrigin, Vector3d ray) {
      Double result = linePlaneIntersection(ray, rayOrigin, this.normal, this.origin);
      return result != null && result.doubleValue() >= 0.0D ? result.doubleValue() : Double.MAX_VALUE;
   }

   public static Double linePlaneIntersection(Vector3d ray, Vector3d rayOrigin, Vector3d normal, Vector3d origin) {
      Double d = normal.dot(origin);
      return normal.dot(ray) == 0.0D ? null : (d.doubleValue() - normal.dot(rayOrigin)) / normal.dot(ray);
   }

   public static BoxPlane createPlane(Axis axis, Vector3d direction, Vector3d[] corners) {
      double value = VectorUtils.get(axis, direction);
      return value == 0.0D ? null : new BoxPlane(corners, BoxFace.get(axis, value > 0.0D));
   }

   public static BoxPlane createOppositePlane(Axis axis, Vector3d direction, Vector3d[] corners) {
      double value = VectorUtils.get(axis, direction);
      return value == 0.0D ? null : new BoxPlane(corners, BoxFace.get(axis, value < 0.0D));
   }
}
