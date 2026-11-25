package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleNone implements ICastingRule {
   private final ZenType fromType;
   private final ZenType toType;

   public CastingRuleNone(ZenType fromType, ZenType toType) {
      this.fromType = fromType;
      this.toType = toType;
   }

   public void compile(IEnvironmentMethod method) {
   }

   public ZenType getInputType() {
      return this.fromType;
   }

   public ZenType getResultingType() {
      return this.toType;
   }
}
