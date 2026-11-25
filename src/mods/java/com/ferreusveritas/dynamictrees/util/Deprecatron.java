package com.ferreusveritas.dynamictrees.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Deprecatron {
   public static Map<String, Integer> notificationMap = new HashMap();

   public Deprecatron() {
   }

   public static void Complain(String issue, String msg, int spamCount) {
      int count = ((Integer)notificationMap.computeIfAbsent(issue, (i) -> {
         return Integer.valueOf(0);
      })).intValue();
      if (count == 0) {
         (new Exception()).printStackTrace();
      }

      if (count < spamCount || spamCount == 0) {
         notificationMap.put(issue, Integer.valueOf(count + 1));
         System.err.println(msg);
      }

   }

   public static void Complain(String issue, String msg) {
      Complain(issue, msg, 200);
   }
}
