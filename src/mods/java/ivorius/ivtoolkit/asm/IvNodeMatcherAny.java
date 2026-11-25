package ivorius.ivtoolkit.asm;

import org.objectweb.asm.tree.AbstractInsnNode;

public class IvNodeMatcherAny implements IvSingleNodeMatcher {
   public IvNodeMatcherAny() {
   }

   public boolean matchNode(AbstractInsnNode node) {
      return true;
   }
}
