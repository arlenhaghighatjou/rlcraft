package stanhebben.zenscript.type.iterator;

import java.util.Iterator;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;

public class IteratorIterable implements IZenIterator {
   private final MethodOutput methodOutput;
   private final ZenType iteratorType;
   private int iterator;

   public IteratorIterable(MethodOutput methodOutput, ZenType iteratorType) {
      this.methodOutput = methodOutput;
      this.iteratorType = iteratorType;
   }

   public void compileStart(int[] locals) {
      this.iterator = this.methodOutput.local(Type.getType(Iterator.class));
      this.methodOutput.invokeInterface(Iterable.class, "iterator", Iterator.class);
      this.methodOutput.storeObject(this.iterator);
   }

   public void compilePreIterate(int[] locals, Label exit) {
      this.methodOutput.loadObject(this.iterator);
      this.methodOutput.invokeInterface(Iterator.class, "hasNext", Boolean.TYPE);
      this.methodOutput.ifEQ(exit);
      this.methodOutput.loadObject(this.iterator);
      this.methodOutput.invokeInterface(Iterator.class, "next", Object.class);
      this.methodOutput.checkCast(this.iteratorType.toASMType().getInternalName());
      this.methodOutput.store(this.iteratorType.toASMType(), locals[0]);
   }

   public void compilePostIterate(int[] locals, Label exit, Label repeat) {
      this.methodOutput.goTo(repeat);
   }

   public ZenType getType(int i) {
      return this.iteratorType;
   }

   public void compileEnd() {
   }
}
