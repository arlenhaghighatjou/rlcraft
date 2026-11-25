package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.tools.IvGsonHelper;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.ArrayUtils;

public class Directions {
   public static final EnumFacing[] HORIZONTAL = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
   public static final EnumFacing[] X_AXIS = new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST};
   public static final EnumFacing[] Y_AXIS = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
   public static final EnumFacing[] Z_AXIS = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.NORTH};

   public Directions() {
   }

   @Nullable
   public static Integer getHorizontalClockwiseRotations(EnumFacing source, EnumFacing dest, boolean mirrorX) {
      if (source == dest) {
         return mirrorX && ArrayUtils.contains(X_AXIS, dest) ? 2 : 0;
      } else {
         int arrayIndexSrc = ArrayUtils.indexOf(HORIZONTAL, source);
         int arrayIndexDst = ArrayUtils.indexOf(HORIZONTAL, dest);
         if (arrayIndexSrc >= 0 && arrayIndexDst >= 0) {
            int mirrorRotations = mirrorX && ArrayUtils.contains(X_AXIS, source) ? 2 : 0;
            return (arrayIndexDst - arrayIndexSrc + mirrorRotations + HORIZONTAL.length) % HORIZONTAL.length;
         } else {
            return null;
         }
      }
   }

   public static EnumFacing rotate(EnumFacing direction, AxisAlignedTransform2D transform) {
      if (direction != EnumFacing.UP && direction != EnumFacing.DOWN) {
         int rotations = transform.getRotation();
         if (transform.isMirrorX() && ArrayUtils.contains(X_AXIS, direction)) {
            rotations += 2;
         }

         return HORIZONTAL[(ArrayUtils.indexOf(HORIZONTAL, direction) + rotations) % HORIZONTAL.length];
      } else {
         return direction;
      }
   }

   public static EnumFacing deserialize(String id) {
      EnumFacing direction = (EnumFacing)IvGsonHelper.enumForNameIgnoreCase(id, EnumFacing.values());
      return direction != null ? direction : EnumFacing.NORTH;
   }

   public static EnumFacing deserializeHorizontal(String id) {
      EnumFacing direction = (EnumFacing)IvGsonHelper.enumForNameIgnoreCase(id, HORIZONTAL);
      return direction != null ? direction : EnumFacing.NORTH;
   }

   public static String serialize(EnumFacing direction) {
      return IvGsonHelper.serializedName(direction);
   }

   public static EnumFacing getDirectionFromVRotation(int front) {
      switch(front) {
      case 0:
      default:
         return EnumFacing.SOUTH;
      case 1:
         return EnumFacing.EAST;
      case 2:
         return EnumFacing.NORTH;
      case 3:
         return EnumFacing.WEST;
      }
   }
}
