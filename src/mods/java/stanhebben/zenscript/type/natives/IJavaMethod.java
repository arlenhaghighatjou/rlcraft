package stanhebben.zenscript.type.natives;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;

public interface IJavaMethod {
   boolean isStatic();

   boolean accepts(int var1);

   boolean accepts(IEnvironmentGlobal var1, Expression... var2);

   int getPriority(IEnvironmentGlobal var1, Expression... var2);

   void invokeVirtual(MethodOutput var1);

   void invokeStatic(MethodOutput var1);

   ZenType[] getParameterTypes();

   ZenType getReturnType();

   boolean isVarargs();

   String getErrorDescription();
}
