package stanhebben.zenscript.statements;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class StatementIf extends Statement {
   private final ParsedExpression condition;
   private final Statement onThen;
   private final Statement onElse;

   public StatementIf(ZenPosition position, ParsedExpression condition, Statement onThen, Statement onElse) {
      super(position);
      this.condition = condition;
      this.onThen = onThen;
      this.onElse = onElse;
   }

   public void compile(IEnvironmentMethod environment) {
      environment.getOutput().position(this.getPosition());
      Expression cCondition = this.condition.compile(environment, ZenType.BOOL).eval(environment).cast(this.getPosition(), environment, ZenType.BOOL);
      ZenType expressionType = cCondition.getType();
      if (expressionType.canCastImplicit(ZenType.BOOL, environment)) {
         Label labelEnd = new Label();
         Label labelElse = this.onElse == null ? labelEnd : new Label();
         cCondition.compileIf(labelElse, environment);
         this.onThen.compile(environment);
         MethodOutput methodOutput = environment.getOutput();
         if (this.onElse != null) {
            methodOutput.goTo(labelEnd);
            methodOutput.label(labelElse);
            this.onElse.compile(environment);
         }

         methodOutput.label(labelEnd);
      } else {
         environment.error(this.getPosition(), "condition is not a boolean");
      }

   }

   public List<Statement> getSubStatements() {
      List<Statement> out = new ArrayList();
      out.add(this);
      out.addAll(this.onThen.getSubStatements());
      if (this.onElse != null) {
         out.addAll(this.onElse.getSubStatements());
      }

      return out;
   }
}
