package stanhebben.zenscript.expression.partial;

import java.lang.reflect.Field;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionJavaStaticField extends Expression {
   private final Class cls;
   private final Field field;
   private final ITypeRegistry types;

   public ExpressionJavaStaticField(ZenPosition position, Class cls, Field field, ITypeRegistry types) {
      super(position);
      this.cls = cls;
      this.field = field;
      this.types = types;
   }

   public ZenType getType() {
      return this.types.getType(this.field.getGenericType());
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         environment.getOutput().getStaticField(this.cls, this.field);
      }

   }
}
