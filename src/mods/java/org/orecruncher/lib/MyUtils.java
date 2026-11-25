package org.orecruncher.lib;

import com.google.common.base.Predicate;
import java.lang.reflect.Array;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public final class MyUtils {
   private static final int[] EMPTY = new int[0];

   private MyUtils() {
   }

   @Nonnull
   public static int[] splitToInts(@Nonnull String str, char splitChar) {
      String[] tokens = StringUtils.split(str, splitChar);
      if (tokens != null && tokens.length != 0) {
         int[] result = new int[tokens.length];

         for(int i = 0; i < tokens.length; ++i) {
            result[i] = Integer.parseInt(tokens[i]);
         }

         return result;
      } else {
         return EMPTY;
      }
   }

   @Nonnull
   public static <T> T[] concatenate(@Nonnull T[] a, @Nullable T[] b) {
      if (b == null) {
         return a;
      } else {
         int aLen = a.length;
         int bLen = b.length;
         if (bLen == 0) {
            return a;
         } else {
            T[] c = (T[])((Object[])Array.newInstance(a.getClass().getComponentType(), aLen + bLen));
            System.arraycopy(a, 0, c, 0, aLen);
            System.arraycopy(b, 0, c, aLen, bLen);
            return c;
         }
      }
   }

   @Nonnull
   public static <T> T[] append(@Nonnull T[] a, @Nullable T b) {
      if (b == null) {
         return a;
      } else {
         int aLen = a.length;
         T[] c = (T[])((Object[])Array.newInstance(a.getClass().getComponentType(), aLen + 1));
         System.arraycopy(a, 0, c, 0, aLen);
         c[aLen] = b;
         return c;
      }
   }

   @Nullable
   public static <T> T find(@Nullable List<T> list, @Nonnull Predicate<T> pred) {
      if (list != null && list.size() != 0) {
         for(T e : list) {
            if (pred.apply(e)) {
               return e;
            }
         }

         return (T)null;
      } else {
         return (T)null;
      }
   }

   @SafeVarargs
   public static <T> List<T> addAll(@Nonnull List<T> list, T... objs) {
      if (objs != null) {
         for(int i = 0; i < objs.length; ++i) {
            if (objs[i] != null) {
               list.add(objs[i]);
            }
         }
      }

      return list;
   }
}
