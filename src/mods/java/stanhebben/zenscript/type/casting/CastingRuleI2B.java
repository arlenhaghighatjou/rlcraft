package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleI2B extends BaseCastingRule {
   public CastingRuleI2B(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().i2b();
   }

   public ZenType getInnerInputType() {
      return ZenType.INT;
   }

   public ZenType getResultingType() {
      return ZenType.BYTE;
   }
}
