package ivorius.ivtoolkit.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class IvMatrixHelper {
   public IvMatrixHelper() {
   }

   public static Matrix4f getProjectionMatrix(float fov, float aspect, float nearPlane, float farPlane) {
      Matrix4f projectionMatrix = new Matrix4f();
      float y_scale = coTangent(fov / 2.0F);
      float x_scale = y_scale / aspect;
      float frustum_length = farPlane - nearPlane;
      projectionMatrix.m00 = x_scale;
      projectionMatrix.m11 = y_scale;
      projectionMatrix.m22 = -((farPlane + nearPlane) / frustum_length);
      projectionMatrix.m23 = -1.0F;
      projectionMatrix.m32 = -(2.0F * nearPlane * farPlane / frustum_length);
      projectionMatrix.m33 = 0.0F;
      return projectionMatrix;
   }

   public static Matrix4f getOrthographicMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
      Matrix4f projectionMatrix = new Matrix4f();
      projectionMatrix.m00 = 2.0F / (right - left);
      projectionMatrix.m11 = 2.0F / (top - bottom);
      projectionMatrix.m22 = -2.0F / (zFar - zNear);
      projectionMatrix.m30 = -(right + left) / (right - left);
      projectionMatrix.m31 = -(top + bottom) / (top - bottom);
      projectionMatrix.m32 = -(zFar + zNear) / (zFar - zNear);
      projectionMatrix.m33 = 1.0F;
      return projectionMatrix;
   }

   public static float coTangent(float f) {
      return 1.0F / (float)Math.tan((double)f);
   }

   public static Matrix4f lookFrom(float posX, float posY, float posZ, float yaw, float pitch, float roll, Matrix4f src, Matrix4f dest) {
      Matrix4f.rotate(roll, new Vector3f(0.0F, 0.0F, 1.0F), src, dest);
      Matrix4f.rotate(pitch, new Vector3f(1.0F, 0.0F, 0.0F), src, dest);
      Matrix4f.rotate(yaw, new Vector3f(0.0F, 1.0F, 0.0F), src, dest);
      Matrix4f.translate(new Vector3f(-posX, -posY, -posZ), src, dest);
      return dest;
   }

   public static Vector3f projectPoint(Matrix4f projectionMatrix, float posX, float posY, float posZ, float yaw, float pitch, float roll, Vector3f point) {
      Matrix4f lookProjectionMatrix = lookFrom(posX, posY, posZ, yaw, pitch, roll, projectionMatrix, projectionMatrix);
      Vector4f clippedPoint = new Vector4f(point.x, point.y, point.z, 1.0F);
      Matrix4f.transform(lookProjectionMatrix, clippedPoint, clippedPoint);
      return new Vector3f(clippedPoint.x, clippedPoint.y, clippedPoint.z);
   }
}
