package com.bloodnbonesgaming.lib.util;

import java.util.ArrayList;
import java.util.List;

public class StringHelper {
   public StringHelper() {
   }

   public static String build(Object... objects) {
      StringBuilder builder = new StringBuilder();

      for(Object object : objects) {
         builder.append(object);
      }

      return builder.toString();
   }

   public static String repeat(char c, int length) {
      StringBuilder a = new StringBuilder();

      for(int b = 0; b < length; ++b) {
         a.append(c);
      }

      return a.toString();
   }

   public static String[] recursiveBuild(List<String>[] options, String delimeter) {
      if (options.length == 0) {
         return new String[0];
      } else if (options.length == 1) {
         return (String[])options[0].toArray(new String[options[0].size()]);
      } else {
         List<String>[] smallCopy = new List[options.length - 1];
         System.arraycopy(options, 1, smallCopy, 0, options.length - 1);
         String[] recursed = recursiveBuild(smallCopy, delimeter);
         String[] finalArray = new String[recursed.length * options[0].size()];

         for(int i = 0; i < options[0].size(); ++i) {
            for(int j = 0; j < recursed.length; ++j) {
               finalArray[i * recursed.length + j] = (String)options[0].get(i) + delimeter + recursed[j];
            }
         }

         return finalArray;
      }
   }

   public static int[] findAllIndexsOf(String haystack, String needle) {
      int index = haystack.indexOf(needle);

      List<Integer> list;
      for(list = new ArrayList(); index != -1; index = haystack.indexOf(haystack, index + 1)) {
         list.add(Integer.valueOf(index));
      }

      if (list.size() <= 0) {
         return null;
      } else {
         int[] indexs = new int[list.size()];

         for(int i = 0; i < list.size(); ++i) {
            indexs[i] = ((Integer)list.get(i)).intValue();
         }

         return indexs;
      }
   }
}
