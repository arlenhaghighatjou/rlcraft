package me.ichun.mods.ichunutil.common.core.config.types;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

public class NestedIntArray {
   public TreeMap<Integer, ArrayList<Integer>> values = new TreeMap();

   public NestedIntArray(TreeMap<Integer, ArrayList<Integer>> vals) {
      this.values = vals;
   }

   public String serialize() {
      StringBuilder sb = new StringBuilder();
      boolean added = false;

      for(Entry<Integer, ArrayList<Integer>> e : this.values.entrySet()) {
         if (added) {
            sb.append(", ");
         }

         sb.append(e.getKey());

         for(int i = 0; i < ((ArrayList)e.getValue()).size(); ++i) {
            sb.append(": ");
            sb.append(((ArrayList)e.getValue()).get(i));
         }

         added = true;
      }

      return sb.toString();
   }

   public void deserialize(String s, int min, int max, int nestedMin, int nestedMax) {
      String[] split = s.split(", *");
      if (split.length > 0 && !split[0].equalsIgnoreCase("")) {
         for(String splits : split) {
            String[] split1 = splits.split(": *");
            if (split1.length > 0 && !split1[0].equalsIgnoreCase("")) {
               try {
                  if (split1.length == 1) {
                     int val = Integer.parseInt(split1[0]);
                     if (val <= max && val >= min) {
                        this.values.put(Integer.valueOf(val), new ArrayList());
                     }
                  } else {
                     ArrayList<Integer> ints = new ArrayList();

                     for(int i = 1; i < split1.length; ++i) {
                        try {
                           int val = Integer.parseInt(split1[i]);
                           if (val <= nestedMax && val >= nestedMin) {
                              ints.add(Integer.valueOf(val));
                           }
                        } catch (NumberFormatException var15) {
                           var15.printStackTrace();
                        }
                     }

                     int val = Integer.parseInt(split1[0]);
                     if (val <= max && val >= min) {
                        this.values.put(Integer.valueOf(val), ints);
                     }
                  }
               } catch (NumberFormatException var16) {
                  var16.printStackTrace();
               }
            }
         }
      }

   }
}
