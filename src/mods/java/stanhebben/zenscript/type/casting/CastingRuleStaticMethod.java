package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;

public class CastingRuleStaticMethod implements ICastingRule {
   private final IJavaMethod method;
   private final ICastingRule base;

   public CastingRuleStaticMethod(IJavaMethod method) {
      this.method = method;
      this.base = null;
   }

   public CastingRuleStaticMethod(IJavaMethod method, ICastingRule base) {
      this.method = method;
      this.base = base;
   }

   public void compile(IEnvironmentMethod method) {
      if (this.base != null) {
         this.base.compile(method);
      }

      this.method.invokeStatic(method.getOutput());
   }

   public ZenType getInputType() {
      return this.method.getParameterTypes()[0];
   }

   public ZenType getResultingType() {
      return this.method.getReturnType();
   }
}
