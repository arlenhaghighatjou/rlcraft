package stanhebben.zenscript.expression;

import java.util.HashMap;
import java.util.Map;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionMap extends Expression {
   private final Expression[] keys;
   private final Expression[] values;
   private final ZenTypeAssociative type;

   public ExpressionMap(ZenPosition position, Expression[] keys, Expression[] values, ZenTypeAssociative type) {
      super(position);
      this.keys = keys;
      this.values = values;
      this.type = type;
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         ZenType keyType = this.type.getKeyType();
         ZenType valueType = this.type.getValueType();
         MethodOutput output = environment.getOutput();
         output.newObject(HashMap.class);
         output.dup();
         output.invokeSpecial(ZenTypeUtil.internal(HashMap.class), "<init>", "()V");

         for(int i = 0; i < this.keys.length; ++i) {
            output.dup();
            this.keys[i].cast(this.getPosition(), environment, keyType).compile(true, environment);
            this.values[i].cast(this.getPosition(), environment, valueType).compile(true, environment);
            output.invokeInterface(ZenTypeUtil.internal(Map.class), "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
            output.pop();
         }
      } else {
         for(Expression key : this.keys) {
            key.compile(false, environment);
         }

         for(Expression value : this.values) {
            value.compile(false, environment);
         }
      }

   }

   public Expression cast(ZenPosition position, IEnvironmentGlobal environment, ZenType type) {
      if (this.type.equals(type)) {
         return this;
      } else if (!(type instanceof ZenTypeAssociative)) {
         ICastingRule castingRule = this.type.getCastingRule(type, environment);
         if (castingRule == null) {
            environment.error(position, "cannot cast " + this.type + " to " + type);
            return new ExpressionInvalid(position, type);
         } else {
            return new ExpressionAs(position, this, castingRule);
         }
      } else {
         ZenTypeAssociative associativeType = (ZenTypeAssociative)type;
         Expression[] newKeys = new Expression[this.keys.length];
         Expression[] newValues = new Expression[this.values.length];

         for(int i = 0; i < this.keys.length; ++i) {
            newKeys[i] = this.keys[i].cast(position, environment, associativeType.getKeyType());
            newValues[i] = this.values[i].cast(position, environment, associativeType.getValueType());
         }

         return new ExpressionMap(this.getPosition(), newKeys, newValues, associativeType);
      }
   }
}
