package org.jwaresoftware.mcmods.lib;

import net.minecraft.util.ResourceLocation;
import org.jwaresoftware.mcmods.lib.api.mod.IModInfo;

public final class LibInfo implements IModInfo {
   private static final LibInfo _sharedINSTANCE = new LibInfo();

   public static final LibInfo getInstance() {
      return _sharedINSTANCE;
   }

   public String mod_id() {
      return "carrots";
   }

   public static final String MOD_ID() {
      return _sharedINSTANCE.mod_id();
   }

   public static final String rstring(String s) {
      return "carrots:" + s;
   }

   public static final ResourceLocation r(String s) {
      return new ResourceLocation("carrots", s);
   }

   private LibInfo() {
   }
}
