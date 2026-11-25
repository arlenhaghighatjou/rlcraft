package reborncore;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

public enum Distribution {
   UNIVERSAL,
   CLIENT,
   SERVER;

   private Distribution() {
   }

   public boolean isInvalid() {
      if (this == UNIVERSAL) {
         return false;
      } else {
         return FMLLaunchHandler.side().isClient() && this == CLIENT;
      }
   }
}
