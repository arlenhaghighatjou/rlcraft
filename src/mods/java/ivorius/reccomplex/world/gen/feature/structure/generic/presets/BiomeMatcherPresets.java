package ivorius.reccomplex.world.gen.feature.structure.generic.presets;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import ivorius.reccomplex.utils.presets.PresetRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBiomeMatcher;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class BiomeMatcherPresets extends PresetRegistry<ArrayList<WeightedBiomeMatcher>> {
   private static BiomeMatcherPresets instance;

   public BiomeMatcherPresets(String fileSuffix) {
      super(fileSuffix, "biome preset");
   }

   public static BiomeMatcherPresets instance() {
      return instance != null ? instance : (instance = new BiomeMatcherPresets("rcbp"));
   }

   protected void registerGson(GsonBuilder builder) {
      builder.registerTypeAdapter(WeightedBiomeMatcher.class, new WeightedBiomeMatcher.Serializer());
   }

   protected Type getType() {
      return (new TypeToken<ArrayList<WeightedBiomeMatcher>>() {
      }).getType();
   }
}
