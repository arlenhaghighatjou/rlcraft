package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleF2D extends BaseCastingRule {
   public CastingRuleF2D(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().f2d();
   }

   public ZenType getInnerInputType() {
      return ZenType.FLOAT;
   }

   public ZenType getResultingType() {
      return ZenType.DOUBLE;
   }
}
