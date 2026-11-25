package stanhebben.zenscript.type.natives;

import java.util.Collections;
import java.util.List;
import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.dump.IDumpConvertable;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.dump.types.DumpIJavaMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.CastingRuleVirtualMethod;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.util.MethodOutput;

public class ZenNativeCaster implements IDumpConvertable {
   private final IJavaMethod method;

   public ZenNativeCaster(IJavaMethod method) {
      this.method = method;
   }

   public ZenType getReturnType() {
      return this.method.getReturnType();
   }

   public void constructCastingRule(ICastingRuleDelegate rules) {
      ZenType type = this.getReturnType();
      rules.registerCastingRule(type, new CastingRuleVirtualMethod(this.method));
   }

   public void compile(MethodOutput output) {
      if (this.method.isStatic()) {
         this.method.invokeStatic(output);
      } else {
         this.method.invokeVirtual(output);
      }

   }

   public void compileAnyCanCastImplicit(ZenType type, MethodOutput output, IEnvironmentGlobal environment, int localClass) {
      String casterAny = this.method.getReturnType().getAnyClassName(environment);
      if (casterAny != null) {
         Label skip = new Label();
         output.loadObject(localClass);
         output.invokeStatic(casterAny, "rtCanCastImplicit", "(Ljava/lang/Class;)Z");
         output.ifEQ(skip);
         output.iConst1();
         output.returnInt();
         output.label(skip);
      }
   }

   public void compileAnyCast(ZenType type, MethodOutput output, IEnvironmentGlobal environment, int localValue, int localClass) {
      Label skip = new Label();
      output.loadObject(localClass);
      output.constant(this.method.getReturnType().toASMType());
      output.ifACmpNe(skip);
      output.load(type.toASMType(), localValue);
      this.compile(output);
      output.returnType(this.method.getReturnType().toASMType());
      output.label(skip);
      String casterAny = this.method.getReturnType().getAnyClassName(environment);
      if (casterAny != null) {
         Label skip2 = new Label();
         output.loadObject(localClass);
         output.invokeStatic(casterAny, "rtCanCastImplicit", "(Ljava/lang/Class;)Z");
         output.ifEQ(skip2);
         output.load(type.toASMType(), localValue);
         this.compile(output);
         output.returnType(this.method.getReturnType().toASMType());
         output.label(skip2);
      }
   }

   public List<? extends IDumpable> asDumpedObject() {
      return Collections.singletonList(new DumpIJavaMethod(this.method));
   }
}
