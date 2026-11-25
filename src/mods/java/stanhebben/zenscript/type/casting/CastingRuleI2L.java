package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleI2L extends BaseCastingRule {
   public CastingRuleI2L(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().i2l();
   }

   public ZenType getInnerInputType() {
      return ZenType.INT;
   }

   public ZenType getResultingType() {
      return ZenType.LONG;
   }
}
