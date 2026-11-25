package goblinbob.mobends.core.math;

import goblinbob.mobends.core.math.matrix.IMat4x4d;
import goblinbob.mobends.core.math.vector.IVec3f;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.math.vector.VectorUtils;
import java.nio.FloatBuffer;

public class QuaternionUtils {
   public static final float PI = 3.1415927F;

   public QuaternionUtils() {
   }

   public static void multiply(IVec3f vector, Quaternion quat, IVec3f dest) {
      Vec3f u = new Vec3f(-quat.x, quat.y, quat.z);
      Vec3f crossResult = new Vec3f();
      float s = -quat.w;
      float x = vector.getX();
      float y = vector.getY();
      float z = vector.getZ();
      float dotUU = VectorUtils.dot(u, u);
      float dotUV = VectorUtils.dot(u, vector);
      VectorUtils.cross(u, vector, crossResult);
      dest.set(u);
      dest.scale(2.0F * dotUV);
      dest.add(x * (s * s - dotUU), y * (s * s - dotUU), z * (s * s - dotUU));
      crossResult.scale(2.0F * s);
      dest.add(crossResult);
   }

   public static Quaternion rotate(Quaternion quat, float angle, float x, float y, float z, Quaternion dest) {
      dest.set(quat);
      dest.rotate(x, y, z, angle / 180.0F * 3.1415927F);
      return dest;
   }

   public static Quaternion rotate(Quaternion quat, float angle, float x, float y, float z) {
      quat.rotate(x, y, z, angle / 180.0F * 3.1415927F);
      return quat;
   }

   public static FloatBuffer quatToGlMatrix(FloatBuffer buffer, Quaternion quaternionIn) {
      buffer.clear();
      float f = quaternionIn.x * quaternionIn.x;
      float f1 = quaternionIn.x * quaternionIn.y;
      float f2 = quaternionIn.x * quaternionIn.z;
      float f3 = quaternionIn.x * quaternionIn.w;
      float f4 = quaternionIn.y * quaternionIn.y;
      float f5 = quaternionIn.y * quaternionIn.z;
      float f6 = quaternionIn.y * quaternionIn.w;
      float f7 = quaternionIn.z * quaternionIn.z;
      float f8 = quaternionIn.z * quaternionIn.w;
      buffer.put(1.0F - 2.0F * (f4 + f7));
      buffer.put(2.0F * (f1 + f8));
      buffer.put(2.0F * (f2 - f6));
      buffer.put(0.0F);
      buffer.put(2.0F * (f1 - f8));
      buffer.put(1.0F - 2.0F * (f + f7));
      buffer.put(2.0F * (f5 + f3));
      buffer.put(0.0F);
      buffer.put(2.0F * (f2 + f6));
      buffer.put(2.0F * (f5 - f3));
      buffer.put(1.0F - 2.0F * (f + f4));
      buffer.put(0.0F);
      buffer.put(0.0F);
      buffer.put(0.0F);
      buffer.put(0.0F);
      buffer.put(1.0F);
      buffer.rewind();
      return buffer;
   }

   public static void quatToMat(Quaternion quaternionIn, IMat4x4d dest) {
      float f = quaternionIn.x * quaternionIn.x;
      float f1 = quaternionIn.x * quaternionIn.y;
      float f2 = quaternionIn.x * quaternionIn.z;
      float f3 = quaternionIn.x * quaternionIn.w;
      float f4 = quaternionIn.y * quaternionIn.y;
      float f5 = quaternionIn.y * quaternionIn.z;
      float f6 = quaternionIn.y * quaternionIn.w;
      float f7 = quaternionIn.z * quaternionIn.z;
      float f8 = quaternionIn.z * quaternionIn.w;
      dest.setFields(new double[]{(double)(1.0F - 2.0F * (f4 + f7)), (double)(2.0F * (f1 + f8)), (double)(2.0F * (f2 - f6)), 0.0D, (double)(2.0F * (f1 - f8)), (double)(1.0F - 2.0F * (f + f7)), (double)(2.0F * (f5 + f3)), 0.0D, (double)(2.0F * (f2 + f6)), (double)(2.0F * (f5 - f3)), (double)(1.0F - 2.0F * (f + f4)), 0.0D, 0.0D, 0.0D, 0.0D, 1.0D});
   }
}
