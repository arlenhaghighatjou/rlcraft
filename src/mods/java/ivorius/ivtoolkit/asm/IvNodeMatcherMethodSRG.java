package ivorius.ivtoolkit.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class IvNodeMatcherMethodSRG implements IvSingleNodeMatcher {
   public int opCode;
   public String srgMethodName;
   public String owner;
   public String desc;

   public IvNodeMatcherMethodSRG(int opCode, String srgMethodName, String owner, String desc) {
      this.opCode = opCode;
      this.srgMethodName = srgMethodName;
      this.owner = owner;
      this.desc = desc;
   }

   public IvNodeMatcherMethodSRG(int opCode, String srgMethodName, String owner, Type returnValue, Type... desc) {
      this(opCode, srgMethodName, owner, IvClassTransformer.getMethodDescriptor(returnValue, (Object[])desc));
   }

   public boolean matchNode(AbstractInsnNode node) {
      if (node.getOpcode() != this.opCode) {
         return false;
      } else {
         MethodInsnNode methodInsnNode = (MethodInsnNode)node;
         if (this.srgMethodName != null && !this.srgMethodName.equals(IvClassTransformer.getSrgName(methodInsnNode))) {
            return false;
         } else if (this.owner != null && !this.owner.equals(IvClassTransformer.getSrgClassName(methodInsnNode.owner))) {
            return false;
         } else {
            return this.desc == null || this.desc.equals(IvClassTransformer.getSRGDescriptor(methodInsnNode.desc));
         }
      }
   }
}
