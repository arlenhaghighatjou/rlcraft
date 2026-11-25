package ivorius.ivtoolkit.asm;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

public class IvInsnHelper {
   public IvInsnHelper() {
   }

   public static void insertDUP3(InsnList list) {
      list.add(new InsnNode(93));
      list.add(new InsnNode(88));
      list.add(new InsnNode(91));
      list.add(new InsnNode(91));
      list.add(new InsnNode(87));
      list.add(new InsnNode(93));
   }

   public static void insertDUP4(InsnList list) {
      list.add(new InsnNode(94));
      list.add(new InsnNode(88));
      list.add(new InsnNode(94));
      list.add(new InsnNode(94));
      list.add(new InsnNode(88));
      list.add(new InsnNode(94));
   }
}
