package ivorius.reccomplex.utils.expression;

import com.google.common.primitives.Ints;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.saving.FileSaver;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;

public class EnvironmentExpression extends BoolFunctionExpressionCache<Environment, Object> {
   public static final String BIOME_PREFIX = "biome.";
   public static final String DIMENSION_PREFIX = "dimension.";
   public static final String DEPENDENCY_PREFIX = "dependency.";
   public static final String VILLAGE_TYPE_PREFIX = "villagetype=";
   public static final String GENERATION_INFO_PREFIX = "generation.";
   public static final String VARIABLE_PREFIX = "variable.";

   public EnvironmentExpression() {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Always");
      this.addType(new EnvironmentExpression.BiomeVariableType("biome.", ""));
      this.addTypes(new EnvironmentExpression.DimensionVariableType("dimension.", ""), new Function[]{(t) -> {
         return t.alias("dim.", "");
      }});
      this.addTypes(new EnvironmentExpression.DependencyVariableType("dependency.", ""), new Function[]{(t) -> {
         return t.alias("dep.", "");
      }});
      this.addTypes(new EnvironmentExpression.VillageTypeType("villagetype=", ""), new Function[]{(t) -> {
         return t.alias("vtype.", "");
      }});
      this.addTypes(new EnvironmentExpression.GenerationType("generation.", ""), new Function[]{(t) -> {
         return t.alias("gen.", "");
      }});
      this.addTypes(new EnvironmentExpression.VariableDomainType("variable.", ""), new Function[]{(t) -> {
         return t.alias("var.", "");
      }});
   }

   public static class BiomeVariableType extends DelegatingVariableType<Boolean, Environment, Object, Biome, Object, BiomeExpression> {
      public BiomeVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Biome convertEvaluateArgument(String var, Environment argument) {
         return argument.biome;
      }

      public BiomeExpression createCache() {
         return new BiomeExpression();
      }
   }

   public static class DependencyVariableType extends DelegatingVariableType<Boolean, Environment, Object, FileSaver, FileSaver, DependencyExpression> {
      public DependencyVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public FileSaver convertArgument(String var, Environment environment) {
         return RecurrentComplex.saver;
      }

      public DependencyExpression createCache() {
         return new DependencyExpression();
      }
   }

   public static class DimensionVariableType extends DelegatingVariableType<Boolean, Environment, Object, WorldProvider, Object, DimensionExpression> {
      public DimensionVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public WorldProvider convertEvaluateArgument(String var, Environment argument) {
         return argument.world.provider;
      }

      public DimensionExpression createCache() {
         return new DimensionExpression();
      }
   }

   protected static class GenerationType extends DelegatingVariableType<Boolean, Environment, Object, ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType, Object, GenerationTypeExpression> {
      public GenerationType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType convertEvaluateArgument(String var, Environment environment) {
         return environment.generationType;
      }

      public GenerationTypeExpression createCache() {
         return new GenerationTypeExpression();
      }
   }

   protected static class VariableDomainType extends FunctionExpressionCache.VariableType<Boolean, Environment, Object> {
      public VariableDomainType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<Environment>, Boolean> parse(String var) {
         return (environment) -> {
            return ((Environment)environment.get()).variables.get(var);
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         return FunctionExpressionCache.Validity.KNOWN;
      }
   }

   protected static class VillageTypeType extends FunctionExpressionCache.VariableType<Boolean, Environment, Object> {
      public VillageTypeType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<Environment>, Boolean> parse(String var) {
         Integer villageType = this.parseVillageType(var);
         return (environment) -> {
            return Objects.equals(villageType, ((Environment)environment.get()).villageType);
         };
      }

      public Integer parseVillageType(String var) {
         Integer integer = Ints.tryParse(var);
         return integer != null && integer.intValue() >= 0 && integer.intValue() < 4 ? integer : null;
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         return this.parseVillageType(var) != null ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.ERROR;
      }
   }
}
