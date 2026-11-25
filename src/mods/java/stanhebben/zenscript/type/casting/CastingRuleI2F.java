package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleI2F extends BaseCastingRule {
   public CastingRuleI2F(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().i2f();
   }

   public ZenType getInnerInputType() {
      return ZenType.INT;
   }

   public ZenType getResultingType() {
      return ZenType.FLOAT;
   }
}
