package stanhebben.zenscript.expression;

import java.util.Objects;
import org.objectweb.asm.Label;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.CastingAnySubtype;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.util.ZenPosition;

public abstract class Expression implements IPartialExpression {
   private final ZenPosition position;

   public Expression(ZenPosition position) {
      this.position = position;
   }

   public static Expression parse(ZenTokener parser, IEnvironmentMethod environment, ZenType predictedType) {
      return ParsedExpression.read(parser, environment).compile(environment, predictedType).eval(environment);
   }

   public ZenPosition getPosition() {
      return this.position;
   }

   public Expression cast(ZenPosition position, IEnvironmentGlobal environment, ZenType type) {
      if (this.getType().equals(type)) {
         return this;
      } else if (Objects.equals(type.toJavaClass(), Object.class)) {
         if (this.getType().toJavaClass().isPrimitive()) {
            Class clazz = this.getType().toJavaClass();
            ICastingRule castingRule = null;
            if (clazz.equals(Integer.TYPE)) {
               castingRule = this.getType().getCastingRule(ZenType.INTOBJECT, environment);
            } else if (clazz.equals(Double.TYPE)) {
               castingRule = this.getType().getCastingRule(ZenType.DOUBLEOBJECT, environment);
            } else if (clazz.equals(Boolean.TYPE)) {
               castingRule = this.getType().getCastingRule(ZenType.BOOLOBJECT, environment);
            } else if (clazz.equals(Short.TYPE)) {
               castingRule = this.getType().getCastingRule(ZenType.SHORTOBJECT, environment);
            } else if (clazz.equals(Float.TYPE)) {
               castingRule = this.getType().getCastingRule(ZenType.FLOATOBJECT, environment);
            } else if (clazz.equals(Byte.TYPE)) {
               castingRule = this.getType().getCastingRule(ZenType.BYTEOBJECT, environment);
            } else if (clazz.equals(Long.TYPE)) {
               castingRule = this.getType().getCastingRule(ZenType.LONGOBJECT, environment);
            }

            if (castingRule != null) {
               return new ExpressionAs(position, this, castingRule);
            } else {
               environment.error(position, "Cannot cast primitive " + this.getType() + " to " + type);
               return new ExpressionInvalid(position, type);
            }
         } else {
            return this;
         }
      } else {
         ICastingRule castingRule = this.getType().getCastingRule(type, environment);
         if (castingRule == null) {
            if (this.getType().canCastImplicit(type, environment)) {
               return new ExpressionAs(position, this, new CastingAnySubtype(this.getType(), type));
            } else {
               environment.error(position, "Cannot cast " + this.getType() + " to " + type);
               return new ExpressionInvalid(position, type);
            }
         } else {
            return new ExpressionAs(position, this, castingRule);
         }
      }
   }

   public abstract void compile(boolean var1, IEnvironmentMethod var2);

   public void compileIf(Label onElse, IEnvironmentMethod environment) {
      if (this.getType() == ZenType.BOOL) {
         this.compile(true, environment);
         environment.getOutput().ifEQ(onElse);
      } else {
         if (!this.getType().isPointer()) {
            throw new RuntimeException("cannot compile non-pointer non-boolean value to if condition");
         }

         this.compile(true, environment);
         environment.getOutput().ifNull(onElse);
      }

   }

   public Expression eval(IEnvironmentGlobal environment) {
      return this;
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      environment.error(position, "not a valid lvalue");
      return new ExpressionInvalid(position, this.getType());
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return this.getType().getMember(position, environment, this, name);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      return this.getType().call(position, environment, this, values);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return this.getType().predictCallTypes(numArguments);
   }

   public IZenSymbol toSymbol() {
      return null;
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      environment.error(this.position, "not a valid type");
      return ZenType.ANY;
   }
}
