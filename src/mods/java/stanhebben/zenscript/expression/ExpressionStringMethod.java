package stanhebben.zenscript.expression;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionStringMethod implements IPartialExpression {
   private static final Map<String, ZenNativeMember> members = new HashMap();
   private static boolean initialised = false;
   private final ZenPosition position;
   private final Expression source;
   private final String methodName;
   private final List<IJavaMethod> methods;

   public ExpressionStringMethod(ZenPosition position, Expression expression, String method) {
      this.position = position;
      this.source = expression;
      this.methodName = method;
      this.methods = ((ZenNativeMember)members.get(method)).getMethods();
   }

   public static boolean hasMethod(String name, ITypeRegistry typeRegistry) {
      if (!initialised) {
         initialise(typeRegistry);
      }

      return members.containsKey(name);
   }

   private static boolean checkMethod(Method method) {
      for(Class<?> parameter : method.getParameterTypes()) {
         if (parameter.isAssignableFrom(Character.TYPE) || parameter.isAssignableFrom(char[].class)) {
            return false;
         }
      }

      return !method.getReturnType().isAssignableFrom(Character.TYPE) && !method.getReturnType().isAssignableFrom(char[].class) ? (method.getModifiers() & 8) == 0 : false;
   }

   private static void initialise(ITypeRegistry typeRegistry) {
      for(Method method : String.class.getMethods()) {
         if (checkMethod(method)) {
            String methodName = method.getName();
            members.putIfAbsent(methodName, new ZenNativeMember());
            ((ZenNativeMember)members.get(methodName)).addMethod(new JavaMethod(method, typeRegistry));
         }
      }

      initialised = true;
   }

   public ZenType getType() {
      return ZenType.ANY;
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      return ZenType.ANY;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      for(IJavaMethod method : this.methods) {
         if (method.accepts(0)) {
            return new ExpressionCallVirtual(this.position, environment, method, this.source.eval(environment), new Expression[0]);
         }
      }

      environment.error(this.position, "Cannot evaluate a StringMethod");
      return new ExpressionInvalid(this.position);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      environment.error(position, "Cannot assign to a String method");
      return new ExpressionInvalid(position);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "Cannot get the member of a String method");
      return new ExpressionInvalid(position);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      IJavaMethod method = JavaMethod.select(false, this.methods, environment, values);
      if (method == null) {
         environment.error(position, StringUtil.methodMatchingError(this.methods, values));
         return new ExpressionInvalid(position);
      } else {
         return new ExpressionCallVirtual(position, environment, method, this.source.eval(environment), values);
      }
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return JavaMethod.predict(((ZenNativeMember)members.get(this.methodName)).getMethods(), numArguments);
   }

   public IZenSymbol toSymbol() {
      throw new UnsupportedOperationException("Cannot get a symbol from a String method");
   }
}
