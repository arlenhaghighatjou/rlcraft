package betterquesting.api2.client.gui.misc;

import java.nio.FloatBuffer;
import org.lwjgl.util.vector.ReadableVector4f;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector4f;

public class GuiAlign {
   public static final ReadableVector4f FULL_BOX = new GuiAlign.ImmutableVec4f(0.0F, 0.0F, 1.0F, 1.0F);
   public static final ReadableVector4f TOP_LEFT = new GuiAlign.ImmutableVec4f(0.0F, 0.0F, 0.0F, 0.0F);
   public static final ReadableVector4f TOP_CENTER = new GuiAlign.ImmutableVec4f(0.5F, 0.0F, 0.5F, 0.0F);
   public static final ReadableVector4f TOP_RIGHT = new GuiAlign.ImmutableVec4f(1.0F, 0.0F, 1.0F, 0.0F);
   public static final ReadableVector4f TOP_EDGE = new GuiAlign.ImmutableVec4f(0.0F, 0.0F, 1.0F, 0.0F);
   public static final ReadableVector4f MID_LEFT = new GuiAlign.ImmutableVec4f(0.0F, 0.5F, 0.0F, 0.5F);
   public static final ReadableVector4f MID_CENTER = new GuiAlign.ImmutableVec4f(0.5F, 0.5F, 0.5F, 0.5F);
   public static final ReadableVector4f MID_RIGHT = new GuiAlign.ImmutableVec4f(1.0F, 0.5F, 1.0F, 0.5F);
   public static final ReadableVector4f BOTTOM_LEFT = new GuiAlign.ImmutableVec4f(0.0F, 1.0F, 0.0F, 1.0F);
   public static final ReadableVector4f BOTTOM_CENTER = new GuiAlign.ImmutableVec4f(0.5F, 1.0F, 0.5F, 1.0F);
   public static final ReadableVector4f BOTTOM_RIGHT = new GuiAlign.ImmutableVec4f(1.0F, 1.0F, 1.0F, 1.0F);
   public static final ReadableVector4f BOTTOM_EDGE = new GuiAlign.ImmutableVec4f(0.0F, 1.0F, 1.0F, 1.0F);
   public static final ReadableVector4f HALF_LEFT = new GuiAlign.ImmutableVec4f(0.0F, 0.0F, 0.5F, 1.0F);
   public static final ReadableVector4f HALF_RIGHT = new GuiAlign.ImmutableVec4f(0.5F, 0.0F, 1.0F, 1.0F);
   public static final ReadableVector4f HALF_TOP = new GuiAlign.ImmutableVec4f(0.0F, 0.0F, 1.0F, 0.5F);
   public static final ReadableVector4f HALF_BOTTOM = new GuiAlign.ImmutableVec4f(0.0F, 0.5F, 1.0F, 1.0F);
   public static final ReadableVector4f LEFT_EDGE = new GuiAlign.ImmutableVec4f(0.0F, 0.0F, 0.0F, 1.0F);
   public static final ReadableVector4f RIGHT_EDGE = new GuiAlign.ImmutableVec4f(1.0F, 0.0F, 1.0F, 1.0F);

   public GuiAlign() {
   }

   public static Vector4f quickAnchor(ReadableVector4f v1, ReadableVector4f v2) {
      float x1 = Math.min(v1.getX(), v2.getX());
      float y1 = Math.min(v1.getY(), v2.getY());
      float x2 = Math.max(v1.getZ(), v2.getZ());
      float y2 = Math.max(v1.getW(), v2.getW());
      return new Vector4f(x1, y1, x2, y2);
   }

   private static class ImmutableVec4f implements ReadableVector4f {
      private final ReadableVector4f v4f;

      public ImmutableVec4f(float x, float y, float z, float w) {
         this.v4f = new Vector4f(x, y, z, w);
      }

      public float getZ() {
         return this.v4f.getZ();
      }

      public float getX() {
         return this.v4f.getX();
      }

      public float getY() {
         return this.v4f.getY();
      }

      public float length() {
         return this.v4f.length();
      }

      public float lengthSquared() {
         return this.v4f.lengthSquared();
      }

      public Vector store(FloatBuffer buf) {
         return this.v4f.store(buf);
      }

      public float getW() {
         return this.v4f.getW();
      }
   }
}
