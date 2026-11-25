package stanhebben.zenscript.parser.expression;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArray;
import stanhebben.zenscript.expression.ExpressionAs;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionArray extends ParsedExpression {
   private final List<ParsedExpression> contents;

   public ParsedExpressionArray(ZenPosition position, List<ParsedExpression> contents) {
      super(position);
      this.contents = contents;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      ZenType predictedBaseType = null;
      ZenTypeArrayBasic arrayType = ZenType.ANYARRAY;
      ICastingRule castingRule = null;
      if (predictedType instanceof ZenTypeArray) {
         predictedBaseType = ((ZenTypeArray)predictedType).getBaseType();
         if (predictedType instanceof ZenTypeArrayBasic) {
            arrayType = (ZenTypeArrayBasic)predictedType;
         }
      } else {
         castingRule = ZenType.ANYARRAY.getCastingRule(predictedType, environment);
         if (castingRule != null) {
            if (castingRule.getInputType() instanceof ZenTypeArray) {
               predictedBaseType = ((ZenTypeArray)castingRule.getInputType()).getBaseType();
               if (castingRule.getInputType() instanceof ZenTypeArrayBasic) {
                  arrayType = (ZenTypeArrayBasic)castingRule.getInputType();
               }
            } else {
               environment.error(this.getPosition(), "Invalid caster - any[] caster but input type is not an array");
               castingRule = null;
            }
         }
      }

      Expression[] cContents = new Expression[this.contents.size()];

      for(int i = 0; i < this.contents.size(); ++i) {
         cContents[i] = ((ParsedExpression)this.contents.get(i)).compile(environment, predictedBaseType).eval(environment);
      }

      Expression result = new ExpressionArray(this.getPosition(), arrayType, cContents);
      return (IPartialExpression)(castingRule != null ? new ExpressionAs(this.getPosition(), result, castingRule) : result);
   }

   public Expression compileKey(IEnvironmentMethod environment, ZenType predictedType) {
      return this.contents.size() == 1 && this.contents.get(0) instanceof ParsedExpressionVariable ? ((ParsedExpression)this.contents.get(0)).compile(environment, predictedType).eval(environment) : this.compile(environment, predictedType).eval(environment);
   }
}
