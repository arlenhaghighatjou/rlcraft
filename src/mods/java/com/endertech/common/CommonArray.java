package com.endertech.common;

import java.lang.reflect.Array;
import java.util.ArrayList;

public final class CommonArray {
   public CommonArray() {
   }

   public static <T> boolean isNullOrEmpty(T[] array) {
      return array == null || array.length == 0;
   }

   public static <T> void reverse(T[] array) {
      ArrayList<T> reversedList = new ArrayList();

      for(int i = array.length - 1; i >= 0; --i) {
         reversedList.add(array[i]);
      }

      reversedList.toArray(array);
   }

   @SafeVarargs
   public static <T> T[] join(T[]... arrays) {
      int length = 0;

      for(T[] array : arrays) {
         length += array.length;
      }

      T[] result = (T[])((Object[])Array.newInstance(arrays[0].getClass().getComponentType(), length));
      int offset = 0;

      for(T[] array : arrays) {
         System.arraycopy(array, 0, result, offset, array.length);
         offset += array.length;
      }

      return result;
   }
}
