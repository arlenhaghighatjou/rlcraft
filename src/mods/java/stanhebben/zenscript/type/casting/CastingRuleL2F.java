package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleL2F extends BaseCastingRule {
   public CastingRuleL2F(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().l2f();
   }

   public ZenType getInnerInputType() {
      return ZenType.LONG;
   }

   public ZenType getResultingType() {
      return ZenType.FLOAT;
   }
}
