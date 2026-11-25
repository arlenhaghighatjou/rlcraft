package stanhebben.zenscript.type.casting;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.MethodOutput;

public class CastingRuleNullableVirtualMethod implements ICastingRule {
   private final ZenType type;
   private final IJavaMethod method;

   public CastingRuleNullableVirtualMethod(ZenType type, IJavaMethod method) {
      this.type = type;
      this.method = method;
   }

   public void compile(IEnvironmentMethod method) {
      MethodOutput output = method.getOutput();
      Label lblNotNull = new Label();
      Label lblAfter = new Label();
      output.dup();
      output.ifNonNull(lblNotNull);
      output.pop();
      output.aConstNull();
      output.goTo(lblAfter);
      output.label(lblNotNull);
      this.method.invokeVirtual(method.getOutput());
      output.label(lblAfter);
   }

   public ZenType getInputType() {
      return this.type;
   }

   public ZenType getResultingType() {
      return this.method.getReturnType();
   }
}
