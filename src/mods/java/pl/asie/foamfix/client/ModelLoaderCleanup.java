package pl.asie.foamfix.client;

import java.lang.invoke.MethodHandle;
import net.minecraftforge.client.model.ModelLoader;
import pl.asie.foamfix.FoamFix;
import pl.asie.foamfix.shared.FoamFixShared;
import pl.asie.foamfix.util.MethodHandleHelper;

public class ModelLoaderCleanup {
   private static final MethodHandle LOADING_EXCEPTIONS_GETTER = MethodHandleHelper.findFieldGetter(ModelLoader.class, "loadingExceptions");
   private static final MethodHandle STATE_MODELS_GETTER = MethodHandleHelper.findFieldGetter(ModelLoader.class, "stateModels");

   public ModelLoaderCleanup() {
   }

   public static void cleanup(ModelLoader loader) {
      if (FoamFixShared.config.clModelLoaderCleanup) {
         FoamFix.getLogger().info("Cleaning up ModelLoader...");

         try {
            LOADING_EXCEPTIONS_GETTER.invoke(loader).clear();
         } catch (Throwable var3) {
            var3.printStackTrace();
         }

         try {
            STATE_MODELS_GETTER.invoke(loader).clear();
         } catch (Throwable var2) {
            var2.printStackTrace();
         }
      }

   }
}
