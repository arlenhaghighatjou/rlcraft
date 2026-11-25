package ivorius.ivtoolkit.asm;

import org.objectweb.asm.tree.AbstractInsnNode;

public class IvNodeMatcherSimple implements IvSingleNodeMatcher {
   public int opCode;

   public IvNodeMatcherSimple(int opCode) {
      this.opCode = opCode;
   }

   public boolean matchNode(AbstractInsnNode node) {
      return node.getOpcode() == this.opCode;
   }
}
