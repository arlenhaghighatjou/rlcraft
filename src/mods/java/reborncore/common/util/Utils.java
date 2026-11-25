package reborncore.common.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.EnumFacing;

public class Utils {
   public static final Set<EnumFacing> NO_FACINGS = Collections.emptySet();
   public static final Set<EnumFacing> HORIZONTAL_FACINGS = Collections.unmodifiableSet(EnumSet.of(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST));
   public static final Set<EnumFacing> VERTICAL_FACINGS = Collections.unmodifiableSet(EnumSet.of(EnumFacing.DOWN, EnumFacing.UP));
   public static final Set<EnumFacing> ALL_FACINGS = Collections.unmodifiableSet(EnumSet.allOf(EnumFacing.class));

   public Utils() {
   }
}
