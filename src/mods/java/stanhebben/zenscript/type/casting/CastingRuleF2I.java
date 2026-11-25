package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleF2I extends BaseCastingRule {
   public CastingRuleF2I(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().f2i();
   }

   public ZenType getInnerInputType() {
      return ZenType.FLOAT;
   }

   public ZenType getResultingType() {
      return ZenType.INT;
   }
}
