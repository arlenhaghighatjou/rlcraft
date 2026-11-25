package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;

public class CastingRuleVirtualMethod implements ICastingRule {
   private final IJavaMethod method;

   public CastingRuleVirtualMethod(IJavaMethod method) {
      this.method = method;
   }

   public void compile(IEnvironmentMethod method) {
      this.method.invokeVirtual(method.getOutput());
   }

   public ZenType getInputType() {
      return this.method.getParameterTypes()[0];
   }

   public ZenType getResultingType() {
      return this.method.getReturnType();
   }
}
