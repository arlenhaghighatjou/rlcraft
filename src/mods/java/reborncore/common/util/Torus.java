package reborncore.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.BlockPos;
import reborncore.RebornCore;

public class Torus {
   public static Map<Integer, Integer> TORUS_SIZE_MAP = new HashMap();

   public Torus() {
   }

   public static List<BlockPos> generate(BlockPos orgin, int radius) {
      List<BlockPos> posLists = new ArrayList();

      for(int x = -radius; x < radius; ++x) {
         for(int y = -radius; y < radius; ++y) {
            for(int z = -radius; z < radius; ++z) {
               if (Math.pow((double)(radius / 2) - Math.sqrt(Math.pow((double)x, 2.0D) + Math.pow((double)y, 2.0D)), 2.0D) + Math.pow((double)z, 2.0D) < Math.pow((double)radius * 0.05D, 2.0D)) {
                  posLists.add(orgin.add(x, z, y));
               }
            }
         }
      }

      return posLists;
   }

   public static void genSizeMap(int maxRadius) {
      if (TORUS_SIZE_MAP.isEmpty()) {
         long start = System.currentTimeMillis();

         for(int radius = 0; radius < maxRadius + 10; ++radius) {
            int size = 0;

            for(int x = -radius; x < radius; ++x) {
               for(int y = -radius; y < radius; ++y) {
                  for(int z = -radius; z < radius; ++z) {
                     if (Math.pow((double)(radius / 2) - Math.sqrt(Math.pow((double)x, 2.0D) + Math.pow((double)y, 2.0D)), 2.0D) + Math.pow((double)z, 2.0D) < Math.pow((double)radius * 0.05D, 2.0D)) {
                        ++size;
                     }
                  }
               }
            }

            TORUS_SIZE_MAP.put(Integer.valueOf(radius), Integer.valueOf(size));
         }

         RebornCore.logHelper.info("Loaded torus size map in " + (System.currentTimeMillis() - start) + "ms");
      }
   }
}
