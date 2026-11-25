package stanhebben.zenscript.expression;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.symbols.SymbolGlobalValue;
import stanhebben.zenscript.type.ZenType;

public class ExpressionGlobalGet extends Expression {
   private final SymbolGlobalValue global;

   public ExpressionGlobalGet(SymbolGlobalValue value) {
      super(value.getPosition());
      this.global = value;
   }

   public ZenType getType() {
      return this.global.getType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      environment.getOutput().getStaticField(this.getOwner(), this.getName(), this.getASMDescriptor());
      if (this.getType().toASMType().equals(Type.getType(Object.class))) {
         environment.getOutput().checkCast(this.getType().getSignature());
      }

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
