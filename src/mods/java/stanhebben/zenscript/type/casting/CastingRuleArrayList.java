package stanhebben.zenscript.type.casting;

import java.util.ArrayList;
import java.util.Collection;
import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.util.MethodOutput;

public class CastingRuleArrayList implements ICastingRule {
   private final ICastingRule base;
   private final ZenTypeArrayBasic from;
   private final ZenTypeArrayList to;

   public CastingRuleArrayList(ICastingRule base, ZenTypeArrayBasic from, ZenTypeArrayList to) {
      this.base = base;
      this.from = from;
      this.to = to;
   }

   public void compile(IEnvironmentMethod method) {
      if (this.from.toJavaClass().getComponentType().isPrimitive()) {
         throw new IllegalArgumentException("Cannot convert primitive Array to List!");
      } else {
         MethodOutput methodOutput = method.getOutput();
         methodOutput.iConst0();
         int localCounter = methodOutput.local(Integer.TYPE);
         methodOutput.storeInt(localCounter);
         int localList = methodOutput.local(ArrayList.class);
         methodOutput.newObject(ArrayList.class);
         methodOutput.dup();
         methodOutput.invokeSpecial("java/util/ArrayList", "<init>", "()V");
         methodOutput.storeObject(localList);
         Label start = new Label();
         Label end = new Label();
         methodOutput.label(start);
         methodOutput.dup();
         methodOutput.dup();
         methodOutput.arrayLength();
         methodOutput.loadInt(localCounter);
         methodOutput.ifICmpLE(end);
         methodOutput.loadInt(localCounter);
         methodOutput.arrayLoad(this.from.getBaseType().toASMType());
         if (this.base != null) {
            this.base.compile(method);
         }

         methodOutput.loadObject(localList);
         methodOutput.swap();
         methodOutput.invokeInterface(Collection.class, "add", Boolean.TYPE, Object.class);
         methodOutput.pop();
         methodOutput.iinc(localCounter);
         methodOutput.goTo(start);
         methodOutput.label(end);
         methodOutput.pop();
         methodOutput.pop();
         methodOutput.loadObject(localList);
      }
   }

   public ZenType getInputType() {
      return this.from;
   }

   public ZenType getResultingType() {
      return this.to;
   }
}
