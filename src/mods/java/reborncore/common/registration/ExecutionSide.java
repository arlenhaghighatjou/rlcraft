package reborncore.common.registration;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

public enum ExecutionSide {
   COMMON,
   CLIENT,
   SERVER;

   private ExecutionSide() {
   }

   public boolean canExcetue() {
      if (this == COMMON) {
         return true;
      } else if (FMLLaunchHandler.side() == Side.CLIENT && this == CLIENT) {
         return true;
      } else {
         return FMLLaunchHandler.side() == Side.SERVER && this == SERVER;
      }
   }
}
