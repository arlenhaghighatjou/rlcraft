package stanhebben.zenscript.type.natives;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.util.MethodOutput;

public class JavaMethodGenerated implements IJavaMethod {
   private final boolean isStatic;
   private final boolean isInterface;
   private final boolean isVarargs;
   private final String owner;
   private final String name;
   private final ZenType[] parameterTypes;
   private final boolean[] optional;
   private final ZenType returnType;
   private final String descriptor;

   public JavaMethodGenerated(boolean isStatic, boolean isInterface, boolean isVarargs, String owner, String name, ZenType returnType, ZenType[] arguments, boolean[] optional) {
      this.isStatic = isStatic;
      this.isInterface = isInterface;
      this.isVarargs = isVarargs;
      this.owner = owner;
      this.name = name;
      this.returnType = returnType;
      this.parameterTypes = arguments;
      this.optional = optional;
      StringBuilder descriptorString = new StringBuilder();
      descriptorString.append('(');

      for(ZenType argument : arguments) {
         descriptorString.append(argument.getSignature());
      }

      descriptorString.append(')');
      descriptorString.append(returnType.getSignature());
      this.descriptor = descriptorString.toString();
   }

   public boolean isStatic() {
      return this.isStatic;
   }

   public boolean isVarargs() {
      return this.isVarargs;
   }

   public String getErrorDescription() {
      return this.descriptor;
   }

   public boolean accepts(int numArguments) {
      if (numArguments > this.parameterTypes.length) {
         return this.isVarargs;
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

   public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
      return this.getPriority(environment, arguments) > 0;
   }

   public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
      int result = 3;
      if (arguments.length > this.parameterTypes.length) {
         if (!this.isVarargs) {
            return -1;
         }

         ZenType arrayType = this.parameterTypes[this.parameterTypes.length - 1];
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
         if (this.isVarargs) {
            --checkUntil;
         }

         for(int i = arguments.length; i < checkUntil; ++i) {
            if (!this.optional[i]) {
               return -1;
            }
         }
      }

      int checkUntil = arguments.length;
      if (arguments.length == this.parameterTypes.length && this.isVarargs) {
         ZenType arrayType = this.parameterTypes[this.parameterTypes.length - 1];
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
      if (this.isStatic) {
         throw new UnsupportedOperationException("Not supported yet.");
      } else {
         if (this.isInterface) {
            output.invokeInterface(this.owner, this.name, this.descriptor);
         } else {
            output.invokeVirtual(this.owner, this.name, this.descriptor);
         }

      }
   }

   public void invokeStatic(MethodOutput output) {
      if (!this.isStatic) {
         throw new UnsupportedOperationException("Not supported yet.");
      } else {
         output.invokeStatic(this.owner, this.name, this.descriptor);
      }
   }

   public ZenType getReturnType() {
      return this.returnType;
   }

   public ZenType[] getParameterTypes() {
      return this.parameterTypes;
   }
}
