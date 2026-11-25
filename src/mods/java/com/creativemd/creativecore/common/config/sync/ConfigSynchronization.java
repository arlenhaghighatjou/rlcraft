package com.creativemd.creativecore.common.config.sync;

import net.minecraftforge.fml.relauncher.Side;

public enum ConfigSynchronization {
   CLIENT {
      public boolean useFolder(boolean forceSynchronization, Side side) {
         return forceSynchronization ? side.isServer() : side.isClient();
      }

      public boolean useValue(boolean forceSynchronization, Side side) {
         return forceSynchronization ? side.isServer() : side.isClient();
      }
   },
   UNIVERSAL {
      public boolean useFolder(boolean forceSynchronization, Side side) {
         return true;
      }

      public boolean useValue(boolean forceSynchronization, Side side) {
         return side.isServer();
      }
   },
   SERVER {
      public boolean useFolder(boolean forceSynchronization, Side side) {
         return side.isServer();
      }

      public boolean useValue(boolean forceSynchronization, Side side) {
         return side.isServer();
      }
   };

   private ConfigSynchronization() {
   }

   public abstract boolean useFolder(boolean var1, Side var2);

   public abstract boolean useValue(boolean var1, Side var2);
}
