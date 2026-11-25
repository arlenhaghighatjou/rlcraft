package ivorius.reccomplex.world.gen.feature.structure.generic.presets;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import ivorius.reccomplex.utils.presets.PresetRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.GenericPlacer;
import java.lang.reflect.Type;

public class GenericPlacerPresets extends PresetRegistry<GenericPlacer> {
   private static GenericPlacerPresets instance;

   public GenericPlacerPresets(String fileSuffix) {
      super(fileSuffix, "placer preset");
   }

   public static GenericPlacerPresets instance() {
      return instance != null ? instance : (instance = new GenericPlacerPresets("rcgp"));
   }

   protected void registerGson(GsonBuilder builder) {
      builder.registerTypeAdapter(GenericPlacer.class, new GenericPlacer.Serializer());
   }

   protected Type getType() {
      return (new TypeToken<GenericPlacer>() {
      }).getType();
   }
}
