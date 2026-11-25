package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionLocalGet extends Expression {
   private final SymbolLocal variable;

   public ExpressionLocalGet(ZenPosition position, SymbolLocal variable) {
      super(position);
      this.variable = variable;
   }

   public ZenType getType() {
      return this.variable.getType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      int local = environment.getLocal(this.variable);
      environment.getOutput().load(this.variable.getType().toASMType(), local);
   }
}
