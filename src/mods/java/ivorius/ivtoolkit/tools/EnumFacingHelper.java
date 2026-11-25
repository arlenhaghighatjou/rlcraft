package ivorius.ivtoolkit.tools;

import net.minecraft.util.EnumFacing;

public class EnumFacingHelper {
   public EnumFacingHelper() {
   }

   public static EnumFacing byName(String name) {
      return byName(name, (EnumFacing)null);
   }

   public static EnumFacing byName(String name, EnumFacing defaultFacing) {
      for(EnumFacing facing : EnumFacing.values()) {
         if (facing.getName().equals(name)) {
            return facing;
         }
      }

      return defaultFacing;
   }
}
