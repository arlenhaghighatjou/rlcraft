package stanhebben.zenscript.type.casting;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.MethodOutput;

public class CastingRuleNullableStaticMethod implements ICastingRule {
   private final IJavaMethod method;
   private final ICastingRule base;

   public CastingRuleNullableStaticMethod(IJavaMethod method) {
      this.method = method;
      this.base = null;
   }

   public CastingRuleNullableStaticMethod(IJavaMethod method, ICastingRule base) {
      this.method = method;
      this.base = base;
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
      if (this.base != null) {
         this.base.compile(method);
      }

      this.method.invokeStatic(method.getOutput());
      output.label(lblAfter);
   }

   public ZenType getInputType() {
      return this.method.getParameterTypes()[0];
   }

   public ZenType getResultingType() {
      return this.method.getReturnType();
   }
}
