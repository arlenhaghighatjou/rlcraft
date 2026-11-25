package stanhebben.zenscript.type.casting;

import java.util.Collection;
import java.util.List;
import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.util.MethodOutput;

public class CastingRuleListArray implements ICastingRule {
   private final ICastingRule base;
   private final ZenTypeArrayList from;
   private final ZenTypeArrayBasic to;

   public CastingRuleListArray(ICastingRule base, ZenTypeArrayList from, ZenTypeArrayBasic to) {
      this.base = base;
      this.from = from;
      this.to = to;
   }

   public void compile(IEnvironmentMethod method) {
      if (this.base == null) {
         this.compileNoConversion(method.getOutput());
      } else {
         this.compileWithConversion(method);
      }

   }

   private void compileWithConversion(IEnvironmentMethod method) {
      MethodOutput methodOutput = method.getOutput();
      methodOutput.iConst0();
      int localCounter = methodOutput.local(Integer.TYPE);
      methodOutput.storeInt(localCounter);
      int localArray = methodOutput.local(this.to.toASMType());
      methodOutput.dup();
      methodOutput.invokeInterface(Collection.class, "size", Integer.TYPE);
      methodOutput.newArray(this.to.getBaseType().toASMType());
      methodOutput.storeObject(localArray);
      Label start = new Label();
      Label end = new Label();
      methodOutput.label(start);
      methodOutput.dup();
      methodOutput.dup();
      methodOutput.invokeInterface(Collection.class, "size", Integer.TYPE);
      methodOutput.loadInt(localCounter);
      methodOutput.ifICmpLE(end);
      methodOutput.loadInt(localCounter);
      methodOutput.invokeInterface(List.class, "get", Object.class, Integer.TYPE);
      methodOutput.checkCast(this.from.getBaseType().toASMType().getInternalName());
      this.base.compile(method);
      methodOutput.loadObject(localArray);
      methodOutput.swap();
      methodOutput.loadInt(localCounter);
      methodOutput.swap();
      methodOutput.arrayStore(this.to.getBaseType().toASMType());
      methodOutput.iinc(localCounter);
      methodOutput.goTo(start);
      methodOutput.label(end);
      methodOutput.pop();
      methodOutput.pop();
      methodOutput.loadObject(localArray);
   }

   private void compileNoConversion(MethodOutput methodOutput) {
      methodOutput.iConst0();
      methodOutput.newArray(this.to.getBaseType().toASMType());
      methodOutput.invokeInterface(Collection.class, "toArray", Object[].class, Object[].class);
      methodOutput.checkCast(this.to.toJavaClass());
   }

   public ZenType getInputType() {
      return this.from;
   }

   public ZenType getResultingType() {
      return this.to;
   }
}
