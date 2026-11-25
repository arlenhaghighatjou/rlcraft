package pl.asie.foamfix.util;

import gnu.trove.strategy.HashingStrategy;
import gnu.trove.strategy.IdentityHashingStrategy;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public final class HashingStrategies {
   public static final Strategy<Object> FASTUTIL_IDENTITY = new Strategy<Object>() {
      public int hashCode(Object o) {
         return System.identityHashCode(o);
      }

      public boolean equals(Object a, Object b) {
         return a == b;
      }
   };
   public static final HashingStrategy<byte[]> BYTE_ARRAY = new HashingStrategies.ByteArray();
   public static final HashingStrategy<float[]> FLOAT_ARRAY = new HashingStrategies.FloatArray();
   public static final HashingStrategy<float[][]> FLOAT_ARRAY_ARRAY = new HashingStrategies.FloatArrayArray();
   public static final HashingStrategy<int[]> INT_ARRAY = new HashingStrategies.IntArray();
   public static final HashingStrategy<Object> GENERIC = new HashingStrategies.ObjectStrategy();
   public static final HashingStrategy<Object> IDENTITY = new IdentityHashingStrategy();
   public static final HashingStrategy<ItemCameraTransforms> ITEM_CAMERA_TRANSFORMS = new HashingStrategies.ItemCameraTransformsStrategy();
   public static final HashingStrategy<ItemTransformVec3f> ITEM_TRANSFORM_VEC3F = new HashingStrategies.ItemTransformVecStrategy();
   public static final HashingStrategy<Object[]> OBJECT_ARRAY = new HashingStrategies.ObjectArrayStrategy();

   public HashingStrategies() {
   }

   private static int hashCode(Vector3f vector) {
      return (Float.floatToIntBits(vector.getX()) * 31 + Float.floatToIntBits(vector.getY())) * 31 + Float.floatToIntBits(vector.getZ());
   }

   private static int hashCode(ItemTransformVec3f transform) {
      int hash = hashCode(transform.rotation);
      hash = hash * 31 + hashCode(transform.scale);
      hash = hash * 31 + hashCode(transform.translation);
      return hash;
   }

   private static final class ByteArray implements HashingStrategy<byte[]> {
      private ByteArray() {
      }

      public int computeHashCode(byte[] object) {
         return Arrays.hashCode(object);
      }

      public boolean equals(byte[] o1, byte[] o2) {
         return Arrays.equals(o1, o2);
      }
   }

   private static final class FloatArray implements HashingStrategy<float[]> {
      private FloatArray() {
      }

      public int computeHashCode(float[] object) {
         return Arrays.hashCode(object);
      }

      public boolean equals(float[] o1, float[] o2) {
         return Arrays.equals(o1, o2);
      }
   }

   private static final class FloatArrayArray implements HashingStrategy<float[][]> {
      private FloatArrayArray() {
      }

      public int computeHashCode(float[][] object) {
         int hash = 1;

         for(float[] anObject : object) {
            hash = hash * 31 + Arrays.hashCode(anObject);
         }

         return hash;
      }

      public boolean equals(float[][] o1, float[][] o2) {
         if (o1 == null) {
            return o2 == null;
         } else if (o1.length != o2.length) {
            return false;
         } else {
            for(int i = 0; i < o1.length; ++i) {
               if (!Arrays.equals(o1[i], o2[i])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static final class IntArray implements HashingStrategy<int[]> {
      private IntArray() {
      }

      public int computeHashCode(int[] object) {
         return Arrays.hashCode(object);
      }

      public boolean equals(int[] o1, int[] o2) {
         return Arrays.equals(o1, o2);
      }
   }

   private static final class ItemCameraTransformsStrategy implements HashingStrategy<ItemCameraTransforms> {
      private ItemCameraTransformsStrategy() {
      }

      public int computeHashCode(ItemCameraTransforms object) {
         int hash = HashingStrategies.hashCode(object.firstperson_left);
         hash = hash * 31 + HashingStrategies.hashCode(object.firstperson_right);
         hash = hash * 31 + HashingStrategies.hashCode(object.fixed);
         hash = hash * 31 + HashingStrategies.hashCode(object.ground);
         hash = hash * 31 + HashingStrategies.hashCode(object.gui);
         hash = hash * 31 + HashingStrategies.hashCode(object.head);
         hash = hash * 31 + HashingStrategies.hashCode(object.thirdperson_left);
         hash = hash * 31 + HashingStrategies.hashCode(object.thirdperson_right);
         return hash;
      }

      public boolean equals(ItemCameraTransforms o1, ItemCameraTransforms o2) {
         if (o1 == null) {
            return o2 == null;
         } else {
            return Objects.equals(o1.firstperson_left, o2.firstperson_left) && Objects.equals(o1.firstperson_right, o2.firstperson_right) && Objects.equals(o1.fixed, o2.fixed) && Objects.equals(o1.ground, o2.ground) && Objects.equals(o1.gui, o2.gui) && Objects.equals(o1.head, o2.head) && Objects.equals(o1.thirdperson_left, o2.thirdperson_left) && Objects.equals(o1.thirdperson_right, o2.thirdperson_right);
         }
      }
   }

   private static final class ItemTransformVecStrategy implements HashingStrategy<ItemTransformVec3f> {
      private ItemTransformVecStrategy() {
      }

      public int computeHashCode(ItemTransformVec3f transform) {
         return HashingStrategies.hashCode(transform);
      }

      public boolean equals(ItemTransformVec3f o1, ItemTransformVec3f o2) {
         return Objects.equals(o1, o2);
      }
   }

   private static final class ObjectArrayStrategy implements HashingStrategy<Object[]> {
      private ObjectArrayStrategy() {
      }

      public int computeHashCode(Object[] object) {
         return Arrays.hashCode(object);
      }

      public boolean equals(Object[] o1, Object[] o2) {
         return Arrays.equals(o1, o2);
      }
   }

   private static final class ObjectStrategy implements HashingStrategy<Object> {
      private ObjectStrategy() {
      }

      public int computeHashCode(Object object) {
         return Objects.hashCode(object);
      }

      public boolean equals(Object o1, Object o2) {
         return Objects.equals(o1, o2);
      }
   }
}
