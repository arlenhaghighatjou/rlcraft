package ivorius.reccomplex.utils.expression;

import ivorius.reccomplex.files.loading.LeveledRegistry;
import ivorius.reccomplex.files.saving.FileSaver;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.tuple.Pair;

public class DependencyExpression extends BoolFunctionExpressionCache<FileSaver, FileSaver> {
   public static final String MOD_PREFIX = "mod:";
   public static final String REGISTRY_PREFIX = "registry:";

   public DependencyExpression() {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "No Dependencies");
      this.addTypes(new DependencyExpression.ModVariableType("mod:", ""), new Function[]{(t) -> {
         return t.alias("$", "");
      }});
      this.addTypes(new DependencyExpression.RegistryVariableType("registry:", ""), new Function[]{(t) -> {
         return t.alias("reg:", "");
      }});
      this.addTypes(new DependencyExpression.RegistryHasVariableType("structure:", "", "structure"), new Function[]{(t) -> {
         return t.alias("#", "");
      }, (t) -> {
         return t.alias("strc:", "");
      }});
   }

   public static String ofMods(String... ids) {
      return ids.length > 0 ? "mod:" + String.join(" & mod:", Arrays.asList(ids)) : "";
   }

   protected static class ModVariableType extends FunctionExpressionCache.VariableType<Boolean, Object, Object> {
      public ModVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<Object>, Boolean> parse(String var) throws ParseException {
         boolean modLoaded = Loader.isModLoaded(var);
         return (a) -> {
            return modLoaded;
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         return Loader.isModLoaded(var) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }

   protected static class RegistryHasVariableType extends FunctionExpressionCache.VariableType<Boolean, FileSaver, FileSaver> {
      public String registryID;

      public RegistryHasVariableType(String prefix, String suffix, String registry) {
         super(prefix, suffix);
         this.registryID = registry;
      }

      public Function<SupplierCache<FileSaver>, Boolean> parse(String var) throws ParseException {
         return (saver) -> {
            return ((FileSaver)saver.get()).registry(this.registryID).has(var);
         };
      }

      public FunctionExpressionCache.Validity validity(String var, FileSaver saver) {
         return !saver.has(this.registryID) ? FunctionExpressionCache.Validity.ERROR : (saver.registry(this.registryID).has(var) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN);
      }
   }

   protected static class RegistryVariableType extends DelegatingVariableType<Boolean, FileSaver, FileSaver, LeveledRegistry, LeveledRegistry, RegistryExpression> {
      public RegistryVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public static Optional<Pair<String, String>> splitOnce(String var, String split) {
         int index = var.indexOf(split);
         return index >= 0 ? Optional.of(Pair.of(var.substring(0, index), var.substring(index + 1, var.length()))) : Optional.empty();
      }

      public FunctionExpressionCache.Validity validity(String var, FileSaver fileSaver) {
         return !splitOnce(var, ".").isPresent() ? FunctionExpressionCache.Validity.ERROR : super.validity(var, fileSaver);
      }

      protected String getVarRepresentation(String var, FileSaver fileSaver) {
         return (String)splitOnce(var, ".").map((p) -> {
            return TextFormatting.BLUE + (String)p.getLeft() + TextFormatting.YELLOW + "." + TextFormatting.RESET + super.getVarRepresentation(var, fileSaver);
         }).orElse(this.getRepresentation(FunctionExpressionCache.Validity.ERROR) + var);
      }

      public RegistryExpression createCache() {
         return new RegistryExpression();
      }

      public Function<SupplierCache<FileSaver>, Boolean> parse(String var) throws ParseException {
         Optional<Pair<String, String>> split = splitOnce(var, ".");
         return (Function)split.map((p) -> {
            RegistryExpression c = (RegistryExpression)ExpressionCache.of(this.createCache(), (String)p.getRight());
            return (fileSaver) -> {
               return (Boolean)c.evaluate(this.convertEvaluateArgument(var, fileSaver.get()));
            };
         }).orElseThrow(IllegalStateException::new);
      }

      public LeveledRegistry convertArgument(String var, FileSaver fileSaver) {
         return (LeveledRegistry)splitOnce(var, ".").map((p) -> {
            return fileSaver.registry((String)p.getLeft());
         }).orElseThrow(IllegalStateException::new);
      }
   }
}
