package stanhebben.zenscript.type.natives;

import java.lang.reflect.Field;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;

public class ZenFieldMethod implements IJavaMethod {
   private final Field field;
   private final ZenType type;
   private final boolean isSetter;

   public ZenFieldMethod(Field field, ITypeRegistry types, boolean isSetter) {
      this.field = field;
      this.isSetter = isSetter;
      this.type = types.getType(field.getType());
   }

   public boolean isStatic() {
      return (this.field.getModifiers() & 8) != 0;
   }

   public boolean accepts(int numArguments) {
      return numArguments == (this.isSetter ? 1 : 0);
   }

   public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
      return arguments.length == (this.isSetter ? 1 : 0);
   }

   public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
      return 0;
   }

   public void invokeVirtual(MethodOutput output) {
      if (this.isSetter) {
         output.putField(this.field.getDeclaringClass(), this.field.getName(), this.field.getType());
      } else {
         output.getField(this.field.getDeclaringClass(), this.field.getName(), this.field.getType());
      }

   }

   public void invokeStatic(MethodOutput output) {
      if (!this.isStatic()) {
         throw new UnsupportedOperationException("Cannot perform static operations on a nonstatic Field");
      } else {
         if (this.isSetter) {
            output.putStaticField(this.field.getDeclaringClass(), this.field);
         } else {
            output.getStaticField(this.field.getDeclaringClass(), this.field);
         }

      }
   }

   public ZenType[] getParameterTypes() {
      return this.isSetter ? new ZenType[]{this.type} : new ZenType[0];
   }

   public ZenType getReturnType() {
      return (ZenType)(this.isSetter ? ZenType.VOID : this.type);
   }

   public boolean isVarargs() {
      return false;
   }

   public String getErrorDescription() {
      return "FIELD " + this.field.getName();
   }
}
