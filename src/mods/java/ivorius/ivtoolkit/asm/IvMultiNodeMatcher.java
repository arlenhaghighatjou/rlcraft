package ivorius.ivtoolkit.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public interface IvMultiNodeMatcher {
   boolean matchFromNodeInList(InsnList var1, AbstractInsnNode var2);
}
