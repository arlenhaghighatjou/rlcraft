package ivorius.reccomplex.utils.expression;

import com.google.common.collect.Lists;
import ivorius.reccomplex.utils.accessor.RCAccessorBiomeDictionary;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeExpression extends BoolFunctionExpressionCache<Biome, Object> {
   public static final String BIOME_NAME_PREFIX = "name=";
   public static final String BIOME_ID_PREFIX = "id=";
   public static final String BIOME_TYPE_PREFIX = "type=";

   public BiomeExpression() {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Any Biome");
      this.addTypes(new BiomeExpression.BiomeNameVariableType("name=", ""), new Function[0]);
      this.addTypes(Expressions.registryVariableType("id=", "", Biome.REGISTRY), new Function[]{(t) -> {
         return t.alias("", "");
      }});
      this.addTypes(new BiomeExpression.BiomeDictVariableType("type=", ""), new Function[]{(t) -> {
         return t.alias("$", "");
      }});
   }

   public static String ofTypes(Type... biomeTypes) {
      return "type=" + String.join(" & type=", Lists.transform(Arrays.asList(biomeTypes), (input) -> {
         return input != null ? input.getName() : null;
      }));
   }

   protected class BiomeDictVariableType extends FunctionExpressionCache.VariableType<Boolean, Biome, Object> {
      public BiomeDictVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<Biome>, Boolean> parse(String var) {
         Type type = RCAccessorBiomeDictionary.getTypeWeak(var);
         return type != null ? (b) -> {
            return BiomeDictionary.hasType((Biome)b.get(), type) || type == Type.WATER && (BiomeDictionary.hasType((Biome)b.get(), Type.OCEAN) || BiomeDictionary.hasType((Biome)b.get(), Type.RIVER));
         } : (b) -> {
            return false;
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object biomes) {
         return RCAccessorBiomeDictionary.getTypeWeak(var) != null ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }

   protected class BiomeNameVariableType extends FunctionExpressionCache.VariableType<Boolean, Biome, Object> {
      public BiomeNameVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<Biome>, Boolean> parse(String var) {
         Stream var10000 = Biome.REGISTRY.getKeys().stream();
         RegistryNamespaced var10001 = Biome.REGISTRY;
         Biome.REGISTRY.getClass();
         List<Biome> biomes = (List)var10000.map(var10001::getObject).filter((b) -> {
            return b.getBiomeName().equals(var);
         }).collect(Collectors.toList());
         return (o) -> {
            return biomes.contains(o.get());
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object biomes) {
         Stream var10000 = Biome.REGISTRY.getKeys().stream();
         RegistryNamespaced var10001 = Biome.REGISTRY;
         Biome.REGISTRY.getClass();
         return var10000.map(var10001::getObject).anyMatch((b) -> {
            return b.getBiomeName().equals(var);
         }) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }
}
