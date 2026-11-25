package pl.asie.foamfix.coremod.patches.jei;

import java.lang.invoke.MethodHandle;
import net.minecraftforge.fml.common.Loader;
import pl.asie.foamfix.util.MethodHandleHelper;

public class SearchTreeJEIPatchGlue {
   public static MethodHandle GET_INGREDIENT_LIST_UNCACHED;
   public static Object src;
   private static boolean initialized;

   public SearchTreeJEIPatchGlue() {
   }

   public static boolean isValid() {
      if (!initialized) {
         if (Loader.isModLoaded("jei")) {
            try {
               GET_INGREDIENT_LIST_UNCACHED = MethodHandleHelper.findMethod(Class.forName("mezz.jei.ingredients.IngredientFilter"), "getIngredientListUncached", "getIngredientListUncached", String.class);
            } catch (Exception var1) {
               GET_INGREDIENT_LIST_UNCACHED = null;
            }
         }

         initialized = true;
      }

      return GET_INGREDIENT_LIST_UNCACHED != null;
   }
}
