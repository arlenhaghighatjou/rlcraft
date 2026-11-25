package stanhebben.zenscript.type.iterator;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;

public class IteratorWhileDo implements IZenIterator {
   private ParsedExpression condition;
   private MethodOutput output;
   private IEnvironmentMethod environment;

   public IteratorWhileDo(ParsedExpression condition, IEnvironmentMethod environment) {
      this.condition = condition;
      this.output = environment.getOutput();
      this.environment = environment;
   }

   public void compileStart(int[] locals) {
   }

   public void compilePreIterate(int[] locals, Label exit) {
      this.condition.compile(this.environment, ZenType.BOOL).eval(this.environment).cast(this.condition.getPosition(), this.environment, ZenType.BOOL).compile(true, this.environment);
      this.output.ifEQ(exit);
   }

   public void compilePostIterate(int[] locals, Label exit, Label repeat) {
      this.output.goTo(repeat);
   }

   public void compileEnd() {
   }

   public ZenType getType(int i) {
      return ZenType.VOID;
   }
}
