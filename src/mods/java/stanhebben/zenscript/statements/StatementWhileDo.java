package stanhebben.zenscript.statements;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.iterator.IteratorWhileDo;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class StatementWhileDo extends Statement {
   private final Statement body;
   private final ParsedExpression condition;

   public StatementWhileDo(ZenPosition position, Statement body, ParsedExpression condition) {
      super(position);
      this.body = body;
      this.condition = condition;
   }

   public void compile(IEnvironmentMethod environment) {
      MethodOutput output = environment.getOutput();
      output.position(this.getPosition());
      IteratorWhileDo iterator = new IteratorWhileDo(this.condition, environment);
      int[] locals = new int[0];
      iterator.compileStart(locals);
      Label repeat = new Label();
      Label exit = new Label();
      output.label(repeat);
      iterator.compilePreIterate(locals, exit);

      for(Statement statement : this.body.getSubStatements()) {
         if (statement instanceof StatementBreak) {
            ((StatementBreak)statement).setExit(exit);
         }
      }

      this.body.compile(environment);
      iterator.compilePostIterate(locals, exit, repeat);
      output.label(exit);
      iterator.compileEnd();
   }

   public List<Statement> getSubStatements() {
      List<Statement> out = new ArrayList();
      out.add(this);
      out.addAll(this.body.getSubStatements());
      return out;
   }
}
