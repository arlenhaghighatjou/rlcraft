package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClassMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionGetZenClassDefaultParameter extends Expression {
   private final ParsedZenClassMethod.ZenClassMethod method;
   private final int parameterNumber;

   public ExpressionGetZenClassDefaultParameter(ZenPosition position, ParsedZenClassMethod.ZenClassMethod method, int parameterNumber) {
      super(position);
      this.method = method;
      this.parameterNumber = parameterNumber;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         MethodOutput output = environment.getOutput();
         output.getStaticField(this.method.getOwner(), this.method.getFunction().getDefaultParameterFieldName(this.parameterNumber), this.getType().toASMType().getDescriptor());
      }

   }

   public ZenType getType() {
      return this.method.getParameterTypes()[this.parameterNumber];
   }
}
