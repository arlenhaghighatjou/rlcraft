package ivorius.reccomplex.world.gen.feature.structure.generic.placement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ivorius.reccomplex.json.SerializableStringTypeRegistry;

public class FactorRegistry {
   public static final FactorRegistry INSTANCE = new FactorRegistry();
   private SerializableStringTypeRegistry<GenericPlacer.Factor> factorRegistry = new SerializableStringTypeRegistry<GenericPlacer.Factor>("factor", "type", GenericPlacer.Factor.class);
   public final Gson gson = this.createGson();

   public FactorRegistry() {
   }

   private Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(GenericPlacer.class, new GenericPlacer.Serializer());
      this.factorRegistry.constructGson(builder);
      return builder.create();
   }

   public SerializableStringTypeRegistry<GenericPlacer.Factor> getTypeRegistry() {
      return this.factorRegistry;
   }
}
