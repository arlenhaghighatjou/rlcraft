package ivorius.ivtoolkit.models.utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public final class BufferUtils {
   public BufferUtils() {
   }

   public static void copy(float[] vertices, ByteBuffer byteBuffer, int numFloats, int offset) {
      byteBuffer.position(0);
      byteBuffer.limit(numFloats << 2);

      for(int index = 0; index < numFloats; ++index) {
         byteBuffer.putFloat(vertices[index + offset]);
      }

      byteBuffer.position(0);
   }

   public static void copy(float[] vertices, FloatBuffer floatBuffer, int numFloats, int offset) {
      floatBuffer.position(0);
      floatBuffer.limit(numFloats);

      for(int index = 0; index < numFloats; ++index) {
         floatBuffer.put(vertices[index + offset]);
      }

      floatBuffer.position(0);
   }

   public static void copy(float[] vertices, int sourceOffset, int count, ByteBuffer byteBuffer) {
      for(int index = 0; index < count; ++index) {
         byteBuffer.putFloat(vertices[index + sourceOffset]);
      }

   }

   public static String toString(FloatBuffer floatBuffer) {
      StringBuilder builder = new StringBuilder();

      for(int i = floatBuffer.position(); i < floatBuffer.limit(); ++i) {
         builder.append(i == floatBuffer.position() ? "" : ", ").append(floatBuffer.get(i));
      }

      return builder.toString();
   }
}
