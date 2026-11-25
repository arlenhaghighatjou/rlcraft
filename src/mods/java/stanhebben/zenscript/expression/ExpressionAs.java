package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionAs extends Expression {
   private final Expression value;
   private final ICastingRule castingRule;

   public ExpressionAs(ZenPosition position, Expression value, ICastingRule castingRule) {
      super(position);
      this.value = value;
      this.castingRule = castingRule;
   }

   public ZenType getType() {
      return this.castingRule.getResultingType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.value.compile(result, environment);
      if (result) {
         this.castingRule.compile(environment);
      }

   }
}
