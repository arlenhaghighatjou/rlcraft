package stanhebben.zenscript.statements;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.util.ZenPosition;

public class StatementNull extends Statement {
   public StatementNull(ZenPosition position) {
      super(position);
   }

   public void compile(IEnvironmentMethod environment) {
   }
}
