package stanhebben.zenscript.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;
import org.objectweb.asm.ClassWriter;
import stanhebben.zenscript.expression.partial.ExpressionJavaStaticField;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialGlobalValue;
import stanhebben.zenscript.expression.partial.PartialJavaClass;
import stanhebben.zenscript.expression.partial.PartialPackage;
import stanhebben.zenscript.expression.partial.PartialScriptReference;
import stanhebben.zenscript.expression.partial.PartialStaticGenerated;
import stanhebben.zenscript.expression.partial.PartialStaticGetter;
import stanhebben.zenscript.expression.partial.PartialStaticMethod;
import stanhebben.zenscript.expression.partial.PartialType;
import stanhebben.zenscript.expression.partial.PartialZSClass;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolCaptured;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.expand.ZenExpandMember;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class EnvironmentMethodLambda extends EnvironmentMethod {
   private static final List<Class<? extends IPartialExpression>> nonCapturedExpressions = new ArrayList();
   private final List<SymbolCaptured> capturedVariables;
   private final String clsName;

   public EnvironmentMethodLambda(MethodOutput output, IEnvironmentClass environment, String clsName) {
      super(output, environment);
      this.clsName = clsName;
      this.capturedVariables = new ArrayList(0);
   }

   public IPartialExpression getValue(String name, ZenPosition position) {
      if (this.local.containsKey(name)) {
         return ((IZenSymbol)this.local.get(name)).instance(position);
      } else {
         IPartialExpression value = this.environment.getValue(name, position);
         if (value != null) {
            if (nonCapturedExpressions.stream().anyMatch((c) -> {
               return c.isInstance(value);
            })) {
               return value;
            } else {
               SymbolCaptured capture = new SymbolCaptured(value.eval(this.environment), name, this.clsName);
               this.capturedVariables.add(capture);
               this.local.put(name, capture);
               return capture.instance(position);
            }
         } else {
            return null;
         }
      }
   }

   public List<SymbolCaptured> getCapturedVariables() {
      return this.capturedVariables;
   }

   public void createConstructor(ClassWriter cw) {
      StringJoiner sj = new StringJoiner("", "(", ")V");

      for(SymbolCaptured value : this.capturedVariables) {
         cw.visitField(18, value.getFieldName(), value.getType().toASMType().getDescriptor(), (String)null, (Object)null).visitEnd();
         sj.add(value.getType().toASMType().getDescriptor());
      }

      MethodOutput constructor = new MethodOutput(cw, 1, "<init>", sj.toString(), (String)null, (String[])null);
      constructor.start();
      constructor.loadObject(0);
      constructor.invokeSpecial("java/lang/Object", "<init>", "()V");
      int i = 1;
      int j = 0;

      for(SymbolCaptured capturedVariable : this.capturedVariables) {
         ZenType type = capturedVariable.getType();
         constructor.loadObject(0);
         constructor.load(type.toASMType(), i + j);
         if (type.isLarge()) {
            ++j;
         }

         constructor.putField(this.clsName, capturedVariable.getFieldName(), capturedVariable.getType().toASMType().getDescriptor());
         ++i;
      }

      constructor.ret();
      constructor.end();
   }

   static {
      nonCapturedExpressions.add(PartialGlobalValue.class);
      nonCapturedExpressions.add(PartialJavaClass.class);
      nonCapturedExpressions.add(PartialPackage.class);
      nonCapturedExpressions.add(PartialScriptReference.class);
      nonCapturedExpressions.add(PartialStaticGenerated.class);
      nonCapturedExpressions.add(PartialStaticGetter.class);
      nonCapturedExpressions.add(PartialStaticMethod.class);
      nonCapturedExpressions.add(PartialType.class);
      nonCapturedExpressions.add(PartialZSClass.class);
      nonCapturedExpressions.add(ExpressionJavaStaticField.class);
      nonCapturedExpressions.add(ZenNativeMember.StaticGetValue.class);
      nonCapturedExpressions.add(ZenExpandMember.StaticGetValue.class);
   }
}
