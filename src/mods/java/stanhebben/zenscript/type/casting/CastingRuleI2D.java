package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleI2D extends BaseCastingRule {
   public CastingRuleI2D(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().i2d();
   }

   public ZenType getInnerInputType() {
      return ZenType.INT;
   }

   public ZenType getResultingType() {
      return ZenType.DOUBLE;
   }
}
