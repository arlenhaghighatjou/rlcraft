package hunternif.mc.atlas.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class MathUtil {
   public MathUtil() {
   }

   public static int roundToBase(int a, int base) {
      return a - a % base;
   }

   public static int ceilAbsToBase(int a, int base) {
      int ceil = a - a % base;
      if (a >= 0) {
         return a > ceil ? ceil + base : ceil;
      } else {
         return a < ceil ? ceil - base : ceil;
      }
   }

   public static AxisAlignedBB toAABB(StructureBoundingBox box) {
      return new AxisAlignedBB((double)box.minX, (double)box.minY, (double)box.minZ, (double)box.maxX, (double)box.maxY, (double)box.maxZ);
   }

   public static BlockPos getCenter(StructureBoundingBox box) {
      return new BlockPos(box.minX + box.getXSize() / 2, box.minY + box.getYSize() / 2, box.minZ + box.getZSize() / 2);
   }

   public static boolean exceedsShort(int x, int z) {
      return x <= -32768 || x >= 32767 || z <= -32768 || z >= 32767;
   }
}
