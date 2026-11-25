package ivorius.ivtoolkit.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class IvNodeMatcherLDC implements IvSingleNodeMatcher {
   public String cst;

   public IvNodeMatcherLDC(String cst) {
      this.cst = cst;
   }

   public boolean matchNode(AbstractInsnNode node) {
      if (node.getOpcode() != 18) {
         return false;
      } else {
         LdcInsnNode ldcInsnNode = (LdcInsnNode)node;
         return this.cst == null || this.cst.equals(ldcInsnNode.cst);
      }
   }
}
