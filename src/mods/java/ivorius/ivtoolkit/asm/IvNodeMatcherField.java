package ivorius.ivtoolkit.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

public class IvNodeMatcherField implements IvSingleNodeMatcher {
   public int opCode;
   public String srgFieldName;
   public String owner;
   public Type type;

   public IvNodeMatcherField(int opCode, String srgFieldName, String owner, Type type) {
      this.opCode = opCode;
      this.srgFieldName = srgFieldName;
      this.owner = owner;
      this.type = type;
   }

   public boolean matchNode(AbstractInsnNode node) {
      if (node.getOpcode() != this.opCode) {
         return false;
      } else {
         FieldInsnNode fieldInsnNode = (FieldInsnNode)node;
         if (this.srgFieldName != null && !this.srgFieldName.equals(fieldInsnNode.name)) {
            return false;
         } else if (this.owner != null && !this.owner.equals(fieldInsnNode.owner)) {
            return false;
         } else {
            return this.type == null || this.type.equals(Type.getType(fieldInsnNode.desc));
         }
      }
   }
}
