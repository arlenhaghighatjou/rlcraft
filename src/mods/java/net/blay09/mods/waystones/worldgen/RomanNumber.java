package net.blay09.mods.waystones.worldgen;

import com.google.common.collect.Maps;
import java.util.TreeMap;

public class RomanNumber {
   private static final TreeMap<Integer, String> map = Maps.newTreeMap();

   public RomanNumber() {
   }

   public static String toRoman(int number) {
      int l = ((Integer)map.floorKey(Integer.valueOf(number))).intValue();
      return number == l ? (String)map.get(Integer.valueOf(number)) : (String)map.get(Integer.valueOf(l)) + toRoman(number - l);
   }

   static {
      map.put(Integer.valueOf(1000), "M");
      map.put(Integer.valueOf(900), "CM");
      map.put(Integer.valueOf(500), "D");
      map.put(Integer.valueOf(400), "CD");
      map.put(Integer.valueOf(100), "C");
      map.put(Integer.valueOf(90), "XC");
      map.put(Integer.valueOf(50), "L");
      map.put(Integer.valueOf(40), "XL");
      map.put(Integer.valueOf(10), "X");
      map.put(Integer.valueOf(9), "IX");
      map.put(Integer.valueOf(5), "V");
      map.put(Integer.valueOf(4), "IV");
      map.put(Integer.valueOf(1), "I");
   }
}
