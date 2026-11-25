package ivorius.reccomplex.world.gen.feature.structure;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import ivorius.reccomplex.json.SerializableStringTypeRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.Transformer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class StructureRegistry extends SimpleLeveledRegistry<Structure<?>> {
   public static final StructureRegistry INSTANCE = new StructureRegistry();
   public static SerializableStringTypeRegistry<Transformer> TRANSFORMERS = new SerializableStringTypeRegistry<Transformer>("transformer", "type", Transformer.class);
   public static SerializableStringTypeRegistry<GenerationType> GENERATION_TYPES = new SerializableStringTypeRegistry<GenerationType>("generationInfo", "type", GenerationType.class);
   private Map<Class<? extends GenerationType>, Collection<Pair<Structure<?>, ? extends GenerationType>>> cachedGeneration = new HashMap();

   public StructureRegistry() {
      super("structure");
   }

   public Structure register(String id, String domain, Structure structure, boolean active, LeveledRegistry.ILevel level) {
      if (active && (!RCConfig.shouldStructureGenerate(id, domain) || !structure.areDependenciesResolved())) {
         active = false;
      }

      return (Structure)super.register(id, domain, structure, active, level);
   }

   public <T extends GenerationType> Collection<Pair<Structure<?>, T>> getGenerationTypes(Class<T> clazz) {
      Collection<Pair<Structure<?>, T>> pairs = (Collection)this.cachedGeneration.get(clazz);
      if (pairs == null) {
         pairs = (Collection)this.allActive().stream().flatMap((s) -> {
            return s.generationTypes(clazz).stream().map((t) -> {
               return Pair.of(s, t);
            });
         }).collect(Collectors.toList());
         this.cachedGeneration.put(clazz, pairs);
      }

      return pairs;
   }

   protected void invalidateCaches() {
      super.invalidateCaches();
      this.cachedGeneration.clear();
   }

   private static class StructureData {
      public boolean disabled;
      public String domain;

      public StructureData(boolean disabled, String domain) {
         this.disabled = disabled;
         this.domain = domain;
      }
   }
}
