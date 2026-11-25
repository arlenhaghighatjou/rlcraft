package stanhebben.zenscript.symbols;

import java.lang.reflect.Field;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.expression.partial.ExpressionJavaStaticField;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolJavaStaticField implements IZenSymbol {
   private final Class cls;
   private final Field field;
   private final ITypeRegistry types;

   public SymbolJavaStaticField(Class cls, Field field, ITypeRegistry types) {
      this.cls = cls;
      this.field = field;
      this.types = types;
   }

   public IPartialExpression instance(ZenPosition position) {
      return new ExpressionJavaStaticField(position, this.cls, this.field, this.types);
   }

   public String toString() {
      return "SymbolJavaStaticField: " + this.field.toString();
   }
}
