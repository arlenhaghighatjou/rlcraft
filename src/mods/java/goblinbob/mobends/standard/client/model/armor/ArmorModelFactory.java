package goblinbob.mobends.standard.client.model.armor;

import goblinbob.mobends.standard.main.MoBends;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.ModelBiped;

public class ArmorModelFactory {
   protected static Map<ModelBiped, ArmorWrapper> wrapperArchive = new HashMap();

   public ArmorModelFactory() {
   }

   public static ModelBiped getArmorModel(ModelBiped suggested, boolean shouldBeMutated) {
      ArmorWrapper wrapper = (ArmorWrapper)wrapperArchive.get(suggested);
      if (shouldBeMutated) {
         if (wrapper == null) {
            wrapper = ArmorWrapper.createFor(suggested);
            wrapperArchive.put(suggested, wrapper);
            MoBends.LOG.info("Creating an armor wrapper for " + suggested);
         }

         return wrapper;
      } else {
         if (wrapper != null) {
            wrapper.deapply();
         }

         return suggested;
      }
   }

   public static void refresh() {
      for(ArmorWrapper model : wrapperArchive.values()) {
         model.demutate();
      }

      wrapperArchive.clear();
   }
}
