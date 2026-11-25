package stanhebben.zenscript.type.casting;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;

public class CastingNotNull implements ICastingRule {
   public ZenType fromType;

   public CastingNotNull(ZenType fromType) {
      this.fromType = fromType;
   }

   public void compile(IEnvironmentMethod method) {
      MethodOutput output = method.getOutput();
      Label labelElse = new Label();
      Label labelAfter = new Label();
      output.ifNull(labelElse);
      output.iConst1();
      output.goTo(labelAfter);
      output.label(labelElse);
      output.iConst0();
      output.label(labelAfter);
   }

   public ZenType getInputType() {
      return this.fromType;
   }

   public ZenType getResultingType() {
      return ZenType.BOOL;
   }
}
