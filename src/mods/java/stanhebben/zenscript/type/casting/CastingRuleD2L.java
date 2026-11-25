package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleD2L extends BaseCastingRule {
   public CastingRuleD2L(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().d2l();
   }

   public ZenType getInnerInputType() {
      return ZenType.DOUBLE;
   }

   public ZenType getResultingType() {
      return ZenType.LONG;
   }
}
