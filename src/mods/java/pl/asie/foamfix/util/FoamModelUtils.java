package pl.asie.foamfix.util;

import java.lang.invoke.MethodHandle;

public final class FoamModelUtils {
   public static final MethodHandle PMW_GET_PARENT;

   private FoamModelUtils() {
   }

   static {
      MethodHandle PMW_GET_PARENT_TMP = null;

      try {
         Class k = Class.forName("net.minecraftforge.client.model.PerspectiveMapWrapper");
         PMW_GET_PARENT_TMP = MethodHandleHelper.findFieldGetter(k, "parent");
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      PMW_GET_PARENT = PMW_GET_PARENT_TMP;
   }
}
