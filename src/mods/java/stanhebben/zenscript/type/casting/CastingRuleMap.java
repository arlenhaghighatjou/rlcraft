package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;

public class CastingRuleMap implements ICastingRule {
   private final ICastingRule keyRule;
   private final ICastingRule valueRule;
   private final ZenTypeAssociative fromType;
   private final ZenTypeAssociative toType;

   public CastingRuleMap(ICastingRule keyRule, ICastingRule valueRule, ZenTypeAssociative fromType, ZenTypeAssociative toType) {
      this.keyRule = keyRule;
      this.valueRule = valueRule;
      this.fromType = fromType;
      this.toType = toType;
   }

   public void compile(IEnvironmentMethod method) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public ZenType getInputType() {
      return this.fromType;
   }

   public ZenType getResultingType() {
      return this.toType;
   }
}
