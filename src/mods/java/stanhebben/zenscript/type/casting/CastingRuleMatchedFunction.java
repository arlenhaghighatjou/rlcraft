package stanhebben.zenscript.type.casting;

import java.lang.reflect.Method;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.EnvironmentMethod;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ZenClassWriter;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeFunction;
import stanhebben.zenscript.type.ZenTypeFunctionCallable;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenTypeUtil;

public class CastingRuleMatchedFunction implements ICastingRule {
   private final ZenTypeFunction fromType;
   private final ZenType toType;
   private final ICastingRule returnCastingRule;
   private final ICastingRule[] argumentCastingRules;

   public CastingRuleMatchedFunction(ZenTypeFunction fromType, ZenType toType, ICastingRule returnCastingRule, ICastingRule[] argumentCastingRules) {
      this.fromType = fromType;
      this.toType = toType;
      this.returnCastingRule = returnCastingRule;
      this.argumentCastingRules = argumentCastingRules;
   }

   public void compile(IEnvironmentMethod outerEnvironment) {
      Class<?> aClass = this.toType.toJavaClass();
      Method method = ZenTypeUtil.findFunctionalInterfaceMethod(aClass);
      if (method == null) {
         outerEnvironment.error("Internal error: Cannot convert from " + this.fromType + " to " + this.toType + " because latter is not a functional interface!");
      } else {
         String className = outerEnvironment.makeClassNameWithMiddleName("generated_bridge_class");
         ZenClassWriter classWriter = new ZenClassWriter(2);
         classWriter.visit(50, 1, className, (String)null, "java/lang/Object", new String[]{Type.getInternalName(aClass)});
         classWriter.visitSource("generated_classfile", (String)null);
         classWriter.visitField(18, "wrappedFun", this.fromType.getSignature(), (String)null, (Object)null).visitEnd();
         MethodOutput bridgeOutput = new MethodOutput(classWriter, 1, "<init>", "(" + this.fromType.getSignature() + ")V", (String)null, (String[])null);
         bridgeOutput.start();
         bridgeOutput.loadObject(0);
         bridgeOutput.invokeSpecial("java/lang/Object", "<init>", "()V");
         bridgeOutput.loadObject(0);
         bridgeOutput.loadObject(1);
         bridgeOutput.putField(className, "wrappedFun", this.fromType.getSignature());
         bridgeOutput.ret();
         bridgeOutput.end();
         bridgeOutput = new MethodOutput(classWriter, 1, method.getName(), Type.getMethodDescriptor(method), (String)null, (String[])null);
         EnvironmentMethod environment = new EnvironmentMethod(bridgeOutput, outerEnvironment);
         bridgeOutput.start();
         bridgeOutput.loadObject(0);
         bridgeOutput.getField(className, "wrappedFun", this.fromType.getSignature());
         int i = 0;

         for(Class<?> parameterType : method.getParameterTypes()) {
            bridgeOutput.load(Type.getType(parameterType), i + 1);
            ICastingRule argumentCastingRule = this.argumentCastingRules[i];
            if (argumentCastingRule != null) {
               argumentCastingRule.compile(environment);
            }

            ++i;
         }

         bridgeOutput.invokeInterface(Type.getType(this.fromType.getSignature()).getInternalName(), "accept", ((ZenTypeFunctionCallable)this.fromType).getDescriptor());
         if (this.returnCastingRule != null) {
            this.returnCastingRule.compile(environment);
         }

         if (method.getReturnType() != Void.TYPE) {
            bridgeOutput.returnType(Type.getType(method.getReturnType()));
         }

         bridgeOutput.ret();
         bridgeOutput.end();
         classWriter.visitEnd();
         outerEnvironment.putClass(className, classWriter.toByteArray());
         bridgeOutput = outerEnvironment.getOutput();
         bridgeOutput.newObject(className);
         bridgeOutput.dupX1();
         bridgeOutput.swap();
         bridgeOutput.invokeSpecial(className, "<init>", "(" + this.fromType.getSignature() + ")V");
      }
   }

   public ZenType getInputType() {
      return this.fromType;
   }

   public ZenType getResultingType() {
      return this.toType;
   }
}
