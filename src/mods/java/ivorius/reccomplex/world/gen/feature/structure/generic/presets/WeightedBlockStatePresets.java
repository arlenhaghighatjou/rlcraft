package ivorius.reccomplex.world.gen.feature.structure.generic.presets;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.utils.presets.PresetRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBlockState;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class WeightedBlockStatePresets extends PresetRegistry<ArrayList<WeightedBlockState>> {
   private static WeightedBlockStatePresets instance;

   public WeightedBlockStatePresets(String fileSuffix) {
      super(fileSuffix, "block preset");
   }

   public static WeightedBlockStatePresets instance() {
      return instance != null ? instance : (instance = new WeightedBlockStatePresets("rcbm"));
   }

   protected void registerGson(GsonBuilder builder) {
      builder.registerTypeAdapter(WeightedBlockState.class, new WeightedBlockState.Serializer(RecurrentComplex.specialRegistry));
   }

   protected Type getType() {
      return (new TypeToken<ArrayList<WeightedBlockState>>() {
      }).getType();
   }
}
