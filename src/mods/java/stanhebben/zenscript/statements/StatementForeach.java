package stanhebben.zenscript.statements;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.EnvironmentScope;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class StatementForeach extends Statement {
   private final String[] varnames;
   private final ParsedExpression list;
   private final Statement body;

   public StatementForeach(ZenPosition position, String[] varnames, ParsedExpression list, Statement body) {
      super(position);
      this.varnames = varnames;
      this.list = list;
      this.body = body;
   }

   public void compile(IEnvironmentMethod environment) {
      Expression cList = this.list.compile(environment, ZenType.ANYARRAY).eval(environment);
      ZenType listType = cList.getType();
      IZenIterator iterator = listType.makeIterator(this.varnames.length, environment);
      if (iterator == null) {
         environment.error(this.getPosition(), "No iterator with " + this.varnames.length + " variables");
      } else {
         MethodOutput methodOutput = environment.getOutput();
         environment.getOutput().position(this.getPosition());
         IEnvironmentMethod local = new EnvironmentScope(environment);
         int[] localVariables = new int[this.varnames.length];

         for(int i = 0; i < localVariables.length; ++i) {
            SymbolLocal localVar = new SymbolLocal(iterator.getType(i), true);
            local.putValue(this.varnames[i], localVar, this.getPosition());
            localVariables[i] = local.getLocal(localVar);
         }

         cList.compile(true, environment);
         iterator.compileStart(localVariables);
         Label repeat = new Label();
         Label exit = new Label();
         Label postIterate = new Label();

         for(Statement statement : this.body.getSubStatements()) {
            if (statement instanceof StatementBreak) {
               ((StatementBreak)statement).setExit(exit);
            } else if (statement instanceof StatementContinue) {
               ((StatementContinue)statement).setExit(postIterate);
            }
         }

         methodOutput.label(repeat);
         iterator.compilePreIterate(localVariables, exit);
         this.body.compile(local);
         methodOutput.label(postIterate);
         iterator.compilePostIterate(localVariables, exit, repeat);
         methodOutput.label(exit);
         iterator.compileEnd();
      }
   }

   public List<Statement> getSubStatements() {
      List<Statement> out = new ArrayList();
      out.add(this);
      out.addAll(this.body.getSubStatements());
      return out;
   }
}
