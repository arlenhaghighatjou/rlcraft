package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.value.IAny;

public class CastingRuleAny implements ICastingRule {
   private final ZenType toType;

   public CastingRuleAny(ZenType toType) {
      this.toType = toType;
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().constant(this.toType);
      method.getOutput().invokeInterface(IAny.class, "as", Class.class, Object.class);
   }

   public ZenType getInputType() {
      return ZenType.ANY;
   }

   public ZenType getResultingType() {
      return this.toType;
   }
}
