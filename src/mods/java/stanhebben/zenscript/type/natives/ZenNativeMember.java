package stanhebben.zenscript.type.natives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionCallVirtual;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeFunctionCallable;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenPosition;

public class ZenNativeMember {
   private final List<IJavaMethod> methods = new ArrayList();
   private IJavaMethod getter;
   private IJavaMethod setter;

   public ZenNativeMember() {
   }

   public IJavaMethod getGetter() {
      return this.getter;
   }

   public void setGetter(IJavaMethod getter) {
      if (this.getter == null) {
         this.getter = getter;
      } else {
         throw new RuntimeException("already has a getter");
      }
   }

   public IJavaMethod getSetter() {
      return this.setter;
   }

   public void setSetter(IJavaMethod setter) {
      if (this.setter == null) {
         this.setter = setter;
      } else {
         throw new RuntimeException("already has a setter");
      }
   }

   public IPartialExpression instance(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value) {
      return new ZenNativeMember.InstanceGetValue(position, value);
   }

   public IPartialExpression instance(ZenPosition position, IEnvironmentGlobal environment) {
      return new ZenNativeMember.StaticGetValue(position);
   }

   public void addMethod(IJavaMethod method) {
      this.methods.add(method);
   }

   public List<IJavaMethod> getMethods() {
      return this.methods;
   }

   public String toString() {
      return Arrays.toString(this.methods.toArray());
   }

   private class InstanceGetValue implements IPartialExpression {
      private final ZenPosition position;
      private final IPartialExpression value;

      public InstanceGetValue(ZenPosition position, IPartialExpression value) {
         this.position = position;
         this.value = value;
      }

      public Expression eval(IEnvironmentGlobal environment) {
         if (ZenNativeMember.this.getter != null) {
            return new ExpressionCallVirtual(this.position, environment, ZenNativeMember.this.getter, this.value.eval(environment), new Expression[0]);
         } else {
            environment.error(this.position, "No getter available");
            ZenType type = this.getType();
            return new ExpressionInvalid(this.position, (ZenType)(type == null ? ZenType.ANY : type));
         }
      }

      public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
         if (ZenNativeMember.this.setter != null) {
            return new ExpressionCallVirtual(position, environment, ZenNativeMember.this.setter, this.value.eval(environment), new Expression[]{other});
         } else {
            environment.error(position, "No setter available");
            return new ExpressionInvalid(position, ZenType.VOID);
         }
      }

      public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
         ZenType type = this.getType();
         if (type == null) {
            environment.error(position, "No such member: " + name);
            return new ExpressionInvalid(position);
         } else {
            return type.getMember(position, environment, this, name);
         }
      }

      public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
         IJavaMethod method = JavaMethod.select(false, ZenNativeMember.this.methods, environment, values);
         if (method == null) {
            if (ZenNativeMember.this.getter != null && this.getType() instanceof ZenTypeFunctionCallable) {
               return this.eval(environment).call(position, environment, values);
            } else {
               environment.error(position, StringUtil.methodMatchingError(ZenNativeMember.this.methods, values));
               return new ExpressionInvalid(position);
            }
         } else {
            return new ExpressionCallVirtual(position, environment, method, this.value.eval(environment), values);
         }
      }

      public ZenType[] predictCallTypes(int numArguments) {
         return JavaMethod.predict(ZenNativeMember.this.methods, numArguments);
      }

      public IZenSymbol toSymbol() {
         return null;
      }

      public ZenType getType() {
         return ZenNativeMember.this.getter != null ? ZenNativeMember.this.getter.getReturnType() : (ZenNativeMember.this.setter != null ? ZenNativeMember.this.setter.getParameterTypes()[0] : null);
      }

      public ZenType toType(IEnvironmentGlobal environment) {
         environment.error(this.position, "not a valid type");
         return ZenType.ANY;
      }
   }

   public class StaticGetValue implements IPartialExpression {
      private final ZenPosition position;

      private StaticGetValue(ZenPosition position) {
         this.position = position;
      }

      public Expression eval(IEnvironmentGlobal environment) {
         if (ZenNativeMember.this.getter == null) {
            throw new RuntimeException("No Getter found!");
         } else {
            return new ExpressionCallStatic(this.position, environment, ZenNativeMember.this.getter, new Expression[0]);
         }
      }

      public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
         if (ZenNativeMember.this.setter == null) {
            throw new RuntimeException("No Setter found!");
         } else {
            return new ExpressionCallStatic(position, environment, ZenNativeMember.this.setter, new Expression[]{other});
         }
      }

      public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
         ZenType type = this.getType();
         if (type == null) {
            environment.error(position, "No such member: " + name);
            return new ExpressionInvalid(position);
         } else {
            return type.getMember(position, environment, this, name);
         }
      }

      public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
         IJavaMethod method = JavaMethod.select(true, ZenNativeMember.this.methods, environment, values);
         if (method == null) {
            if (ZenNativeMember.this.getter != null && this.getType() instanceof ZenTypeFunctionCallable) {
               return this.eval(environment).call(position, environment, values);
            } else {
               environment.error(position, StringUtil.methodMatchingError(ZenNativeMember.this.methods, values));
               return new ExpressionInvalid(position);
            }
         } else {
            return new ExpressionCallStatic(position, environment, method, values);
         }
      }

      public ZenType[] predictCallTypes(int numArguments) {
         return JavaMethod.predict(ZenNativeMember.this.methods, numArguments);
      }

      public IZenSymbol toSymbol() {
         return ZenNativeMember.this.new StaticSymbol();
      }

      public ZenType getType() {
         return ZenNativeMember.this.getter != null ? ZenNativeMember.this.getter.getReturnType() : (ZenNativeMember.this.setter != null ? ZenNativeMember.this.setter.getParameterTypes()[0] : null);
      }

      public ZenType toType(IEnvironmentGlobal environment) {
         environment.error(this.position, "not a valid type");
         return ZenType.ANY;
      }
   }

   private class StaticSymbol implements IZenSymbol {
      private StaticSymbol() {
      }

      public IPartialExpression instance(ZenPosition position) {
         return ZenNativeMember.this.new StaticGetValue(position);
      }
   }
}
