package stanhebben.zenscript.type.casting;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.util.MethodOutput;

public class CastingRuleArrayArray implements ICastingRule {
   private final ICastingRule base;
   private final ZenTypeArrayBasic from;
   private final ZenTypeArrayBasic to;

   public CastingRuleArrayArray(ICastingRule base, ZenTypeArrayBasic from, ZenTypeArrayBasic to) {
      this.base = base;
      this.from = from;
      this.to = to;
   }

   public void compile(IEnvironmentMethod method) {
      MethodOutput output = method.getOutput();
      Label end = new Label();
      Label start = new Label();
      Type toType = this.to.getBaseType().toASMType();
      int result = output.local(this.to.toASMType());
      output.dup();
      output.arrayLength();
      output.newArray(toType);
      output.storeObject(result);
      int counter = output.local(Integer.TYPE);
      output.iConst0();
      output.storeInt(counter);
      output.label(start);
      output.dup();
      output.dup();
      output.arrayLength();
      output.loadInt(counter);
      output.dupX1();
      output.ifICmpLE(end);
      output.arrayLoad(this.from.getBaseType().toASMType());
      if (this.base != null) {
         this.base.compile(method);
      }

      output.loadObject(result);
      if (this.to.getBaseType().isLarge()) {
         output.swapLargeLower();
         output.loadInt(counter);
         output.swapLargeLower();
      } else {
         output.swap();
         output.loadInt(counter);
         output.swap();
      }

      output.arrayStore(toType);
      output.iinc(counter);
      output.goTo(start);
      output.label(end);
      output.pop();
      output.pop();
      output.pop();
      output.loadObject(result);
   }

   public ZenType getInputType() {
      return this.from;
   }

   public ZenType getResultingType() {
      return this.to;
   }
}
