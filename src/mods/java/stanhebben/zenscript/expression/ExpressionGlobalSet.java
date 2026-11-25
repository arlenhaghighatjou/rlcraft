package stanhebben.zenscript.expression;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.symbols.SymbolGlobalValue;
import stanhebben.zenscript.type.ZenType;

public class ExpressionGlobalSet extends Expression {
   private final SymbolGlobalValue global;
   private Expression newVal;

   public ExpressionGlobalSet(SymbolGlobalValue value, Expression newVal) {
      super(value.getPosition());
      this.global = value;
      this.newVal = newVal;
   }

   public ZenType getType() {
      return ZenType.VOID;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.newVal.compile(true, environment);
      if (this.getType().toASMType().equals(Type.getType(Object.class))) {
         environment.getOutput().checkCast(this.getType().getSignature());
      }

      environment.getOutput().putStaticField(this.getOwner(), this.getName(), this.getASMDescriptor());
   }

   public String getOwner() {
      return this.global.getOwner();
   }

   public String getName() {
      return this.global.getName();
   }

   public String getASMDescriptor() {
      return this.global.getASMDescriptor();
   }
}
