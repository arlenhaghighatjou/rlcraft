package stanhebben.zenscript.type.expand;

import java.util.ArrayList;
import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenPosition;

public class ZenExpandMember {
   private final String type;
   private final String name;
   private final List<IJavaMethod> methods = new ArrayList();
   private IJavaMethod getter;
   private IJavaMethod setter;

   public ZenExpandMember(String type, String name) {
      this.type = type;
      this.name = name;
   }

   public IPartialExpression instance(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value) {
      return new ZenExpandMember.InstanceGetValue(position, value);
   }

   public IPartialExpression instance(ZenPosition position, IEnvironmentGlobal environment) {
      return new ZenExpandMember.StaticGetValue(position);
   }

   public void setGetter(IJavaMethod getter) {
      if (this.getter != null) {
         throw new RuntimeException(this.type + "." + this.name + " already has a getter");
      } else {
         this.getter = getter;
      }
   }

   public void setSetter(IJavaMethod setter) {
      if (this.setter != null) {
         throw new RuntimeException(this.type + "." + this.name + " already has a setter");
      } else {
         this.setter = setter;
      }
   }

   public void addMethod(IJavaMethod method) {
      this.methods.add(method);
   }

   private class InstanceGetValue implements IPartialExpression {
      private final ZenPosition position;
      private final IPartialExpression value;

      public InstanceGetValue(ZenPosition position, IPartialExpression value) {
         this.position = position;
         this.value = value;
      }

      public Expression eval(IEnvironmentGlobal environment) {
         return new ExpressionCallStatic(this.position, environment, ZenExpandMember.this.getter, new Expression[]{this.value.eval(environment)});
      }

      public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
         return new ExpressionCallStatic(position, environment, ZenExpandMember.this.setter, new Expression[]{this.value.eval(environment), other});
      }

      public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
         return ZenExpandMember.this.getter.getReturnType().getMember(position, environment, this, name);
      }

      public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
         Expression[] newValues = new Expression[values.length + 1];
         newValues[0] = this.value.eval(environment);
         System.arraycopy(values, 0, newValues, 1, values.length);
         IJavaMethod method = JavaMethod.select(true, ZenExpandMember.this.methods, environment, newValues);
         if (method == null) {
            environment.error(position, StringUtil.methodMatchingError(ZenExpandMember.this.methods, values));
            return new ExpressionInvalid(position);
         } else {
            return new ExpressionCallStatic(position, environment, method, newValues);
         }
      }

      public IZenSymbol toSymbol() {
         return null;
      }

      public ZenType getType() {
         return ZenExpandMember.this.getter.getReturnType();
      }

      public ZenType toType(IEnvironmentGlobal environment) {
         environment.error(this.position, "not a valid type");
         return ZenType.ANY;
      }

      public ZenType[] predictCallTypes(int numArguments) {
         ZenType[] base = JavaMethod.predict(ZenExpandMember.this.methods, numArguments + 1);
         ZenType[] result = new ZenType[base.length - 1];
         System.arraycopy(base, 1, result, 0, result.length);
         return result;
      }
   }

   public class StaticGetValue implements IPartialExpression {
      private final ZenPosition position;

      private StaticGetValue(ZenPosition position) {
         this.position = position;
      }

      public Expression eval(IEnvironmentGlobal environment) {
         if (ZenExpandMember.this.getter == null) {
            for(IJavaMethod method : ZenExpandMember.this.methods) {
               if (method.accepts(0)) {
                  return new ExpressionCallStatic(this.position, environment, method, new Expression[0]);
               }
            }

            environment.error(this.position, "Could not evaluate ZenExpandMember " + ZenExpandMember.this.name + " in " + ZenExpandMember.this.type);
            return new ExpressionInvalid(this.position);
         } else {
            return new ExpressionCallStatic(this.position, environment, ZenExpandMember.this.getter, new Expression[0]);
         }
      }

      public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
         return new ExpressionCallStatic(position, environment, ZenExpandMember.this.setter, new Expression[]{other});
      }

      public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
         return ZenExpandMember.this.getter.getReturnType().getMember(position, environment, this, name);
      }

      public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
         IJavaMethod method = JavaMethod.select(true, ZenExpandMember.this.methods, environment, values);
         if (method == null) {
            environment.error(position, StringUtil.methodMatchingError(ZenExpandMember.this.methods, values));
            return new ExpressionInvalid(position);
         } else {
            return new ExpressionCallStatic(position, environment, method, values);
         }
      }

      public IZenSymbol toSymbol() {
         return ZenExpandMember.this.new StaticSymbol();
      }

      public ZenType getType() {
         if (ZenExpandMember.this.getter != null) {
            return ZenExpandMember.this.getter.getReturnType();
         } else {
            return (ZenType)(ZenExpandMember.this.methods.size() > 1 ? ((IJavaMethod)ZenExpandMember.this.methods.get(0)).getReturnType() : ZenType.ANY);
         }
      }

      public ZenType toType(IEnvironmentGlobal environment) {
         environment.error(this.position, "not a valid type");
         return ZenType.ANY;
      }

      public ZenType[] predictCallTypes(int numArguments) {
         return JavaMethod.predict(ZenExpandMember.this.methods, numArguments);
      }
   }

   private class StaticSymbol implements IZenSymbol {
      private StaticSymbol() {
      }

      public IPartialExpression instance(ZenPosition position) {
         return ZenExpandMember.this.new StaticGetValue(position);
      }
   }
}
