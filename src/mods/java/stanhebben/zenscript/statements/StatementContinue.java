package stanhebben.zenscript.statements;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.util.ZenPosition;

public class StatementContinue extends Statement {
   private Label exit;

   public StatementContinue(ZenPosition position) {
      super(position);
   }

   public void setExit(Label exit) {
      this.exit = exit;
   }

   public void compile(IEnvironmentMethod environment) {
      environment.getOutput().position(this.getPosition());
      if (this.exit != null) {
         environment.getOutput().goTo(this.exit);
      } else {
         environment.error(this.getPosition(), "Skipping continue statement as it has no proper label. Only use them in loops!");
      }

   }
}
