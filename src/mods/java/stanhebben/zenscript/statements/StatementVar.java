package stanhebben.zenscript.statements;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.util.ZenPosition;

public class StatementVar extends Statement {
   private final String name;
   private final ZenType type;
   private final ParsedExpression initializer;
   private final boolean isFinal;

   public StatementVar(ZenPosition position, String name, ZenType type, ParsedExpression initializer, boolean isFinal) {
      super(position);
      this.name = name;
      this.type = type;
      this.initializer = initializer;
      this.isFinal = isFinal;
   }

   public void compile(IEnvironmentMethod environment) {
      environment.getOutput().position(this.getPosition());
      Expression cInitializer = this.initializer == null ? null : this.initializer.compile(environment, this.type).eval(environment);
      ZenType cType = (ZenType)(this.type == null ? (cInitializer == null ? ZenTypeAny.INSTANCE : cInitializer.getType()) : this.type);
      SymbolLocal symbol = new SymbolLocal(cType, this.isFinal);
      environment.putValue(this.name, symbol, this.getPosition());
      if (cInitializer != null) {
         cInitializer.compile(true, environment);
         environment.getOutput().store(symbol.getType().toASMType(), environment.getLocal(symbol));
      }

   }
}
