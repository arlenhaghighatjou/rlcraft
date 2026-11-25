package stanhebben.zenscript.type.iterator;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenTypeUtil;

public class IteratorMap implements IZenIterator {
   private final MethodOutput methodOutput;
   private final ZenTypeAssociative type;
   private int iterator;

   public IteratorMap(MethodOutput methodOutput, ZenTypeAssociative type) {
      this.methodOutput = methodOutput;
      this.type = type;
   }

   public void compileStart(int[] locals) {
      this.methodOutput.invokeInterface(Map.class, "entrySet", Set.class);
      this.iterator = this.methodOutput.local(Type.getType(Iterator.class));
      this.methodOutput.invokeInterface(Set.class, "iterator", Iterator.class);
      this.methodOutput.storeObject(this.iterator);
   }

   public void compilePreIterate(int[] locals, Label exit) {
      this.methodOutput.loadObject(this.iterator);
      this.methodOutput.invokeInterface(Iterator.class, "hasNext", Boolean.TYPE);
      this.methodOutput.ifEQ(exit);
      this.methodOutput.loadObject(this.iterator);
      this.methodOutput.invokeInterface(Iterator.class, "next", Object.class);
      this.methodOutput.dup();
      this.methodOutput.invokeInterface(Entry.class, "getKey", Object.class);
      this.methodOutput.checkCast(ZenTypeUtil.internal(this.type.getKeyType().toJavaClass()));
      this.methodOutput.store(this.type.getKeyType().toASMType(), locals[0]);
      this.methodOutput.invokeInterface(Entry.class, "getValue", Object.class);
      this.methodOutput.checkCast(ZenTypeUtil.internal(this.type.getValueType().toJavaClass()));
      this.methodOutput.store(this.type.getValueType().toASMType(), locals[1]);
   }

   public void compilePostIterate(int[] locals, Label exit, Label repeat) {
      this.methodOutput.goTo(repeat);
   }

   public void compileEnd() {
   }

   public ZenType getType(int i) {
      return i == 1 ? this.type.getValueType() : this.type.getKeyType();
   }
}
