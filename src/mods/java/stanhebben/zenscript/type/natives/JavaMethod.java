package stanhebben.zenscript.type.natives;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClassMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArray;
import stanhebben.zenscript.expression.ExpressionBool;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionFloat;
import stanhebben.zenscript.expression.ExpressionGetZenClassDefaultParameter;
import stanhebben.zenscript.expression.ExpressionInt;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class JavaMethod implements IJavaMethod {
   public static final int PRIORITY_INVALID = -1;
   public static final int PRIORITY_LOW = 1;
   public static final int PRIORITY_MEDIUM = 2;
   public static final int PRIORITY_HIGH = 3;
   private final Method method;
   public final boolean returnsSelf;
   private final ZenType[] parameterTypes;
   private final boolean[] optional;
   private final ZenType returnType;

   public JavaMethod(Method method, ITypeRegistry types) {
      this.method = method;
      this.returnsSelf = ((ReturnsSelf[])method.getDeclaredAnnotationsByType(ReturnsSelf.class)).length != 0;
      this.returnType = types.getType(method.getGenericReturnType());
      this.parameterTypes = new ZenType[method.getParameterTypes().length];
      this.optional = new boolean[this.parameterTypes.length];

      for(int i = 0; i < this.parameterTypes.length; ++i) {
         this.parameterTypes[i] = types.getType(method.getGenericParameterTypes()[i]);
         this.optional[i] = false;

         for(Annotation annotation : method.getParameterAnnotations()[i]) {
            if (annotation instanceof Optional) {
               this.optional[i] = true;
            }
         }
      }

      boolean lastOptional = false;

      for(boolean optional : this.optional) {
         if (lastOptional && !optional) {
            throw new IllegalArgumentException("All optionals need to be placed at the end of the method declaration: " + method.toGenericString());
         }

         lastOptional = optional;
      }

   }

   public static IJavaMethod get(ITypeRegistry types, Class cls, String name, Class... parameterTypes) {
      try {
         Method method = cls.getMethod(name, parameterTypes);
         if (method == null) {
            throw new RuntimeException("method " + name + " not found in class " + cls.getName());
         } else {
            return new JavaMethod(method, types);
         }
      } catch (NoSuchMethodException var5) {
         throw new RuntimeException("method " + name + " not found in class " + cls.getName(), var5);
      } catch (SecurityException var6) {
         throw new RuntimeException("method retrieval not permitted", var6);
      }
   }

   public static IJavaMethod getStatic(String owner, String name, ZenType returnType, ZenType... arguments) {
      return new JavaMethodGenerated(true, false, false, owner, name, returnType, arguments, new boolean[arguments.length]);
   }

   public static IJavaMethod get(ITypeRegistry types, Method method) {
      return new JavaMethod(method, types);
   }

   public static IJavaMethod select(boolean doStatic, List<IJavaMethod> methods, IEnvironmentGlobal environment, Expression... arguments) {
      int bestPriority = -1;
      IJavaMethod bestMethod = null;
      boolean isValid = false;

      for(IJavaMethod method : methods) {
         if (method.isStatic() == doStatic) {
            int priority = method.getPriority(environment, arguments);
            if (priority == bestPriority) {
               isValid = false;
            } else if (priority > bestPriority) {
               isValid = true;
               bestMethod = method;
               bestPriority = priority;
            }
         }
      }

      return isValid ? bestMethod : null;
   }

   public static ZenType[] predict(List<IJavaMethod> methods, int numArguments) {
      ZenType[] results = new ZenType[numArguments];
      boolean[] ambiguous = new boolean[numArguments];

      for(IJavaMethod method : methods) {
         if (method.accepts(numArguments)) {
            ZenType[] parameterTypes = method.getParameterTypes();

            for(int i = 0; i < numArguments; ++i) {
               if (i >= parameterTypes.length - 1 && method.isVarargs()) {
                  if (numArguments == parameterTypes.length) {
                     ambiguous[i] = true;
                  } else if (numArguments > parameterTypes.length) {
                     if (results[i] != null) {
                        ambiguous[i] = true;
                     } else {
                        results[i] = ((ZenTypeArray)parameterTypes[parameterTypes.length - 1]).getBaseType();
                     }
                  }
               } else if (results[i] != null) {
                  ambiguous[i] = true;
               } else {
                  results[i] = parameterTypes[i];
               }
            }
         }
      }

      for(int i = 0; i < results.length; ++i) {
         if (ambiguous[i]) {
            results[i] = null;
         }
      }

      return results;
   }

   public static Expression[] rematch(ZenPosition position, IJavaMethod method, IEnvironmentGlobal environment, Expression... arguments) {
      ZenType[] parameterTypes = method.getParameterTypes();
      if (arguments.length == 0 && parameterTypes.length == 0) {
         return arguments;
      } else {
         Expression[] result = new Expression[method.getParameterTypes().length];

         for(int i = arguments.length; i < method.getParameterTypes().length; ++i) {
            result[i] = parameterTypes[i].defaultValue(position);
         }

         int doUntil = parameterTypes.length;
         if (method.isVarargs()) {
            doUntil = parameterTypes.length - 1;
            ZenType paramType = parameterTypes[parameterTypes.length - 1];
            ZenType baseType = ((ZenTypeArray)paramType).getBaseType();
            if (arguments.length == parameterTypes.length) {
               ZenType argType = arguments[arguments.length - 1].getType();
               if (argType.equals(paramType)) {
                  result[arguments.length - 1] = arguments[arguments.length - 1];
               } else if (argType.equals(baseType)) {
                  result[arguments.length - 1] = new ExpressionArray(position, (ZenTypeArrayBasic)paramType, new Expression[]{arguments[arguments.length - 1]});
               } else if (argType.canCastImplicit(paramType, environment)) {
                  result[arguments.length - 1] = arguments[arguments.length - 1].cast(position, environment, paramType);
               } else {
                  result[arguments.length - 1] = (new ExpressionArray(position, (ZenTypeArrayBasic)paramType, new Expression[]{arguments[arguments.length - 1]})).cast(position, environment, paramType);
               }
            } else if (arguments.length > parameterTypes.length) {
               int offset = parameterTypes.length - 1;
               Expression[] values = new Expression[arguments.length - offset];

               for(int i = 0; i < values.length; ++i) {
                  values[i] = arguments[offset + i].cast(position, environment, baseType);
               }

               result[offset] = new ExpressionArray(position, (ZenTypeArrayBasic)paramType, values);
            }
         }

         for(int i = arguments.length; i < doUntil; ++i) {
            if (method instanceof JavaMethod) {
               JavaMethod javaMethod = (JavaMethod)method;
               result[i] = getOptionalValue(position, javaMethod, i, environment);
            } else if (method instanceof ParsedZenClassMethod.ZenClassMethod) {
               ParsedZenClassMethod.ZenClassMethod zenClassMethod = (ParsedZenClassMethod.ZenClassMethod)method;
               result[i] = new ExpressionGetZenClassDefaultParameter(position, zenClassMethod, i);
            } else {
               result[i] = parameterTypes[i].defaultValue(position);
            }
         }

         for(int i = 0; i < Math.min(arguments.length, doUntil); ++i) {
            result[i] = arguments[i].cast(position, environment, parameterTypes[i]);
         }

         return result;
      }
   }

   private static Expression getOptionalValue(ZenPosition position, JavaMethod javaMethod, int parameterNr, IEnvironmentGlobal environment) {
      Parameter parameter = javaMethod.getMethod().getParameters()[parameterNr];
      Optional optional = (Optional)parameter.getAnnotation(Optional.class);
      if (optional == null) {
         return javaMethod.getParameterTypes()[parameterNr].defaultValue(position);
      } else {
         Class<?> parameterType = parameter.getType();
         if (parameterType.isPrimitive()) {
            Class<?> clazz = parameter.getType();
            if (clazz != Integer.TYPE && clazz != Short.TYPE && clazz != Long.TYPE && clazz != Byte.TYPE) {
               if (clazz == Boolean.TYPE) {
                  return new ExpressionBool(position, optional.valueBoolean());
               } else if (clazz != Float.TYPE && clazz != Double.TYPE) {
                  environment.error(position, "Optional Annotation Error, not a known primitive: " + clazz);
                  return new ExpressionInvalid(position);
               } else {
                  return new ExpressionFloat(position, optional.valueDouble(), environment.getType(clazz));
               }
            } else {
               return new ExpressionInt(position, optional.valueLong(), environment.getType(clazz));
            }
         } else {
            Class<?> methodClass = optional.methodClass();
            if (methodClass != Optional.class) {
               try {
                  Method method = methodClass.getMethod(optional.methodName(), String.class);
                  if (!parameterType.isAssignableFrom(method.getReturnType())) {
                     environment.error(position, "Optional Annotation Error, cannot assign " + parameterType + " from " + method);
                     return new ExpressionInvalid(position);
                  } else {
                     return new ExpressionCallStatic(position, environment, new JavaMethod(method, environment.getEnvironment().getTypeRegistry()), new Expression[]{new ExpressionString(position, optional.value())});
                  }
               } catch (NoSuchMethodException var9) {
                  environment.error(position, "Optional Annotation Error, cannot find method " + optional.methodName());
                  return new ExpressionInvalid(position);
               }
            } else {
               return (Expression)(parameterType == String.class && !optional.value().isEmpty() ? new ExpressionString(position, optional.value()) : javaMethod.getParameterTypes()[parameterNr].defaultValue(position));
            }
         }
      }
   }

   public boolean isStatic() {
      return (this.method.getModifiers() & 8) > 0;
   }

   public boolean isVarargs() {
      return this.method.isVarArgs();
   }

   public ZenType getReturnType() {
      return this.returnType;
   }

   public ZenType[] getParameterTypes() {
      return this.parameterTypes;
   }

   public Class getOwner() {
      return this.method.getDeclaringClass();
   }

   public Method getMethod() {
      return this.method;
   }

   public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
      return this.getPriority(environment, arguments) > 0;
   }

   public boolean accepts(int numArguments) {
      if (numArguments > this.parameterTypes.length) {
         return this.method.isVarArgs();
      } else if (numArguments == this.parameterTypes.length) {
         return true;
      } else {
         for(int i = numArguments; i < this.parameterTypes.length; ++i) {
            if (!this.optional[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
      int result = 3;
      if (arguments.length > this.parameterTypes.length) {
         if (!this.method.isVarArgs()) {
            return -1;
         }

         ZenType arrayType = this.parameterTypes[this.method.getParameterTypes().length - 1];
         ZenType baseType = ((ZenTypeArray)arrayType).getBaseType();

         for(int i = this.parameterTypes.length - 1; i < arguments.length; ++i) {
            ZenType argType = arguments[i].getType();
            if (!argType.equals(baseType)) {
               if (!argType.canCastImplicit(baseType, environment)) {
                  return -1;
               }

               result = Math.min(result, 1);
            }
         }
      } else if (arguments.length < this.parameterTypes.length) {
         result = 2;
         int checkUntil = this.parameterTypes.length;

         for(int i = arguments.length; i < checkUntil; ++i) {
            if (!this.optional[i]) {
               return -1;
            }
         }
      }

      int checkUntil = arguments.length;
      if (this.method.isVarArgs()) {
         checkUntil = this.parameterTypes.length - 1;
      }

      if (arguments.length == this.parameterTypes.length && this.method.isVarArgs()) {
         ZenType arrayType = this.parameterTypes[this.method.getParameterTypes().length - 1];
         ZenType baseType = ((ZenTypeArray)arrayType).getBaseType();
         ZenType argType = arguments[arguments.length - 1].getType();
         if (!argType.equals(arrayType) && !argType.equals(baseType)) {
            if (argType.canCastImplicit(arrayType, environment)) {
               result = Math.min(result, 1);
            } else {
               if (!argType.canCastImplicit(baseType, environment)) {
                  return -1;
               }

               result = Math.min(result, 1);
            }
         }

         checkUntil = arguments.length - 1;
      }

      for(int i = 0; i < checkUntil; ++i) {
         ZenType argType = arguments[i].getType();
         ZenType paramType = this.parameterTypes[i];
         if (!argType.equals(paramType)) {
            if (!argType.canCastImplicit(paramType, environment)) {
               return -1;
            }

            result = Math.min(result, 1);
         }
      }

      return result;
   }

   public void invokeVirtual(MethodOutput output) {
      if (this.isStatic()) {
         throw new UnsupportedOperationException("Method is static");
      } else {
         if (this.method.getDeclaringClass().isInterface()) {
            output.invokeInterface(this.method.getDeclaringClass(), this.method.getName(), this.method.getReturnType(), this.method.getParameterTypes());
         } else {
            output.invokeVirtual(this.method.getDeclaringClass(), this.method.getName(), this.method.getReturnType(), this.method.getParameterTypes());
         }

      }
   }

   public void invokeStatic(MethodOutput output) {
      if (!this.isStatic()) {
         throw new UnsupportedOperationException("Method is not static");
      } else {
         output.invokeStatic(this.method.getDeclaringClass(), this.method.getName(), this.method.getReturnType(), this.method.getParameterTypes());
      }
   }

   public String toString() {
      return "JavaMethod: " + this.method.toString();
   }

   public String getErrorDescription() {
      StringBuilder builder = new StringBuilder();
      builder.append("\n").append(this.method.getName()).append("(");

      for(int i = 0; i < this.parameterTypes.length; ++i) {
         ZenType type = this.parameterTypes[i];

         for(int i1 = 0; i1 < this.method.getParameterAnnotations()[i].length; ++i1) {
            Annotation an = this.method.getParameterAnnotations()[i][i1];
            builder.append("§a").append(an.annotationType().getSimpleName()).append(" ");
         }

         builder.append("§r").append(type.toString()).append(", ");
      }

      int length = builder.length();
      builder.delete(length - 2, length);
      builder.append(")");
      return builder.toString();
   }
}
