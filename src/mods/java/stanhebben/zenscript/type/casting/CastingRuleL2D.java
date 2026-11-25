package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleL2D extends BaseCastingRule {
   public CastingRuleL2D(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().l2d();
   }

   public ZenType getInnerInputType() {
      return ZenType.LONG;
   }

   public ZenType getResultingType() {
      return ZenType.DOUBLE;
   }
}
