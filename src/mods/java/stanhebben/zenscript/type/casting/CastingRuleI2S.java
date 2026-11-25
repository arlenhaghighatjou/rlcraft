package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleI2S extends BaseCastingRule {
   public CastingRuleI2S(ICastingRule baseRule) {
      super(baseRule);
   }

   public void compileInner(IEnvironmentMethod method) {
      method.getOutput().i2s();
   }

   public ZenType getInnerInputType() {
      return ZenType.INT;
   }

   public ZenType getResultingType() {
      return ZenType.SHORT;
   }
}
