package ivorius.ivtoolkit.asm;

import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public class IvMultiNodeMatcherSimple implements IvMultiNodeMatcher {
   public List<IvSingleNodeMatcher> singleNodeMatchers;

   public IvMultiNodeMatcherSimple(List<IvSingleNodeMatcher> singleNodeMatchers) {
      this.singleNodeMatchers = singleNodeMatchers;
   }

   public IvMultiNodeMatcherSimple(IvSingleNodeMatcher... singleNodeMatchers) {
      this(Arrays.asList(singleNodeMatchers));
   }

   public boolean matchFromNodeInList(InsnList nodes, AbstractInsnNode node) {
      for(IvSingleNodeMatcher singleNodeMatcher : this.singleNodeMatchers) {
         if (!singleNodeMatcher.matchNode(node)) {
            return false;
         }

         node = node.getNext();
      }

      return true;
   }
}
