package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleD2F extends BaseCastingRule {
   public CastingRuleD2F(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().d2f();
   }

   public ZenType getInnerInputType() {
      return ZenType.DOUBLE;
   }

   public ZenType getResultingType() {
      return ZenType.FLOAT;
   }
}
