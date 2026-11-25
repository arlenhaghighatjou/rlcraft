package ivorius.reccomplex.world.gen.feature.structure.generic.presets;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import ivorius.reccomplex.utils.presets.PresetRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerMulti;
import java.lang.reflect.Type;

public class TransfomerPresets extends PresetRegistry<TransformerMulti.Data> {
   private static TransfomerPresets instance;

   public TransfomerPresets(String fileSuffix) {
      super(fileSuffix, "transformer preset");
   }

   public static TransfomerPresets instance() {
      return instance != null ? instance : (instance = new TransfomerPresets("rcmt"));
   }

   protected void registerGson(GsonBuilder builder) {
      builder.registerTypeAdapter(TransformerMulti.Data.class, new TransformerMulti.DataSerializer());
   }

   protected Type getType() {
      return (new TypeToken<TransformerMulti.Data>() {
      }).getType();
   }
}
