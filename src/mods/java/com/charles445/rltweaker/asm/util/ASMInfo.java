package com.charles445.rltweaker.asm.util;

public class ASMInfo {
   public static ServerType serverType = ServerType.NONE;
   public static boolean hasSponge = false;

   public ASMInfo() {
   }

   public static void cacheServerType(ClassLoader loader) {
      serverType = ServerType.NONE;
      hasSponge = false;

      try {
         Class.forName("org.spongepowered.mod.SpongeMod", false, loader);
         hasSponge = true;
      } catch (ClassNotFoundException var4) {
         ;
      }

      try {
         Class.forName("catserver.server.CatServer", false, loader);
         serverType = ServerType.CATSERVER;
      } catch (ClassNotFoundException var3) {
         try {
            Class.forName("com.mohistmc.MohistMC", false, loader);
            serverType = ServerType.MOHIST;
         } catch (ClassNotFoundException var2) {
            ;
         }
      }
   }
}
