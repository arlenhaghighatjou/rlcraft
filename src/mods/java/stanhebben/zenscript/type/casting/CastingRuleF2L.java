package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleF2L extends BaseCastingRule {
   public CastingRuleF2L(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().f2l();
   }

   public ZenType getInnerInputType() {
      return ZenType.FLOAT;
   }

   public ZenType getResultingType() {
      return ZenType.LONG;
   }
}
