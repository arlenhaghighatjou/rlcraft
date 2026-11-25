package ivorius.reccomplex.world.gen.feature.structure.generic.presets;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ivorius.reccomplex.utils.presets.PresetRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedDimensionMatcher;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DimensionMatcherPresets extends PresetRegistry<ArrayList<WeightedDimensionMatcher>> {
   private static DimensionMatcherPresets instance;

   public DimensionMatcherPresets(String fileSuffix) {
      super(fileSuffix, "dimension preset");
   }

   public static DimensionMatcherPresets instance() {
      return instance != null ? instance : (instance = new DimensionMatcherPresets("rcdp"));
   }

   protected void registerGson(GsonBuilder builder) {
      builder.registerTypeAdapter(WeightedDimensionMatcher.class, new WeightedDimensionMatcher.Serializer());
   }

   protected Type getType() {
      return (new TypeToken<ArrayList<WeightedDimensionMatcher>>() {
      }).getType();
   }
}
