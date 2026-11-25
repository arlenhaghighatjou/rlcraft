package stanhebben.zenscript.expression;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArray extends Expression {
   private final Expression[] contents;
   private final ZenTypeArrayBasic type;

   public ExpressionArray(ZenPosition position, ZenTypeArrayBasic type, Expression... contents) {
      super(position);
      this.contents = contents;
      this.type = type;
   }

   public Expression cast(ZenPosition position, IEnvironmentGlobal environment, ZenType type) {
      if (this.type.equals(type)) {
         return this;
      } else if (!(type instanceof ZenTypeArray)) {
         ICastingRule castingRule = this.type.getCastingRule(type, environment);
         if (castingRule == null) {
            environment.error(position, "cannot cast " + this.type + " to " + type);
            return new ExpressionInvalid(position, type);
         } else {
            return new ExpressionAs(position, this, castingRule);
         }
      } else {
         ZenTypeArray arrayType = (ZenTypeArray)type;
         Expression[] newContents = new Expression[this.contents.length];

         for(int i = 0; i < this.contents.length; ++i) {
            newContents[i] = this.contents[i].cast(position, environment, arrayType.getBaseType());
         }

         return (Expression)(type instanceof ZenTypeArrayBasic ? new ExpressionArray(this.getPosition(), (ZenTypeArrayBasic)arrayType, newContents) : new ExpressionArrayList(this.getPosition(), (ZenTypeArrayList)arrayType, newContents));
      }
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      ZenType baseType = this.type.getBaseType();
      Type asmBaseType = this.type.getBaseType().toASMType();
      MethodOutput output = environment.getOutput();
      output.constant(Integer.valueOf(this.contents.length));
      output.newArray(asmBaseType);

      for(int i = 0; i < this.contents.length; ++i) {
         output.dup();
         output.constant(Integer.valueOf(i));
         this.contents[i].cast(this.getPosition(), environment, baseType).compile(result, environment);
         output.arrayStore(asmBaseType);
      }

   }
}
