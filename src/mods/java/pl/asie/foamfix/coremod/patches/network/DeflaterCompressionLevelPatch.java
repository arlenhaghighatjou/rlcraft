package pl.asie.foamfix.coremod.patches.network;

import java.util.ListIterator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import pl.asie.patchy.TransformerFunction;

public class DeflaterCompressionLevelPatch implements TransformerFunction<ClassNode> {
   public DeflaterCompressionLevelPatch() {
   }

   public ClassNode apply(ClassNode classNode) {
      for(MethodNode method : classNode.methods) {
         if ("<init>".equals(method.name)) {
            ListIterator<AbstractInsnNode> it = method.instructions.iterator();

            while(it.hasNext()) {
               AbstractInsnNode node = (AbstractInsnNode)it.next();
               if (node instanceof MethodInsnNode && node.getOpcode() == 183 && ((MethodInsnNode)node).owner.equals("java/util/zip/Deflater") && ((MethodInsnNode)node).name.equals("<init>") && ((MethodInsnNode)node).desc.equals("()V")) {
                  ((MethodInsnNode)node).desc = "(I)V";
                  it.previous();
                  it.previous();
                  it.add(new FieldInsnNode(178, "pl/asie/foamfix/shared/FoamFixShared", "neDeflaterCompression", "I"));
                  System.out.println("Patched deflater init in " + classNode.name + " " + method.name);
               }
            }
         }
      }

      return classNode;
   }
}
