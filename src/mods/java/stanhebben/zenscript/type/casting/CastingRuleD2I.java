package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleD2I extends BaseCastingRule {
   public CastingRuleD2I(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().d2i();
   }

   public ZenType getInnerInputType() {
      return ZenType.DOUBLE;
   }

   public ZenType getResultingType() {
      return ZenType.INT;
   }
}
