package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionLocalSet extends Expression {
   private final SymbolLocal variable;
   private final Expression value;

   public ExpressionLocalSet(ZenPosition position, SymbolLocal variable, Expression value) {
      super(position);
      this.variable = variable;
      this.value = value;
   }

   public ZenType getType() {
      return this.variable.getType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      int local = environment.getLocal(this.variable);
      this.value.compile(true, environment);
      if (result) {
         environment.getOutput().dup();
      }

      environment.getOutput().store(this.variable.getType().toASMType(), local);
   }
}
