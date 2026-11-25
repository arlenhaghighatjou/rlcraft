package stanhebben.zenscript.statements;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class StatementReturn extends Statement {
   private final ZenType returnType;
   private final ParsedExpression expression;

   public StatementReturn(ZenPosition position, ZenType returnType, ParsedExpression expression) {
      super(position);
      this.returnType = returnType;
      this.expression = expression;
   }

   public ParsedExpression getExpression() {
      return this.expression;
   }

   public boolean isReturn() {
      return false;
   }

   public void compile(IEnvironmentMethod environment) {
      environment.getOutput().position(this.getPosition());
      if (this.expression == null) {
         environment.getOutput().ret();
      } else {
         Expression cExpression = this.expression.compile(environment, this.returnType).eval(environment);
         cExpression.compile(true, environment);
         Type returnType = cExpression.getType().toASMType();
         environment.getOutput().returnType(returnType);
      }

   }
}
