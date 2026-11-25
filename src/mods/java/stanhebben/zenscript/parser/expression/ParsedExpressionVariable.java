package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionVariable extends ParsedExpression {
   private final String name;

   public ParsedExpressionVariable(ZenPosition position, String name) {
      super(position);
      this.name = name;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      IPartialExpression result = environment.getValue(this.name, this.getPosition());
      if (result == null) {
         if (predictedType == null) {
            environment.error(this.getPosition(), "could not find " + this.name);
            return new ExpressionInvalid(this.getPosition());
         } else {
            IPartialExpression member = predictedType.getStaticMember(this.getPosition(), environment, this.name);
            if (member != null && member.getType().getCastingRule(predictedType, environment) != null) {
               return member;
            } else {
               environment.error(this.getPosition(), "could not find " + this.name);
               return new ExpressionInvalid(this.getPosition());
            }
         }
      } else {
         return result;
      }
   }

   public Expression compileKey(IEnvironmentMethod environment, ZenType predictedType) {
      return new ExpressionString(this.getPosition(), this.name);
   }
}
