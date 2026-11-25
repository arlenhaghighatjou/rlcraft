package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleL2I extends BaseCastingRule {
   public CastingRuleL2I(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().l2i();
   }

   public ZenType getInnerInputType() {
      return ZenType.LONG;
   }

   public ZenType getResultingType() {
      return ZenType.INT;
   }
}
