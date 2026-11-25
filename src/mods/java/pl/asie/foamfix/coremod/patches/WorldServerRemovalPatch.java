package pl.asie.foamfix.coremod.patches;

import java.util.ListIterator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import pl.asie.patchy.TransformerFunction;

public class WorldServerRemovalPatch implements TransformerFunction<ClassNode> {
   public WorldServerRemovalPatch() {
   }

   public ClassNode apply(ClassNode classNode) {
      for(MethodNode method : classNode.methods) {
         if ("updateEntities".equals(method.name) || "updateEntities".equals(method.name)) {
            ListIterator<AbstractInsnNode> it = method.instructions.iterator();

            while(it.hasNext()) {
               AbstractInsnNode node = (AbstractInsnNode)it.next();
               if (node instanceof IntInsnNode && node.getOpcode() == 17 && ((IntInsnNode)node).operand == 300) {
                  AbstractInsnNode node2 = (AbstractInsnNode)it.next();
                  if (node2.getOpcode() == 161) {
                     it.add(new VarInsnNode(25, 0));
                     it.add(new MethodInsnNode(185, "pl/asie/foamfix/coremod/patches/IFoamFixWorldRemovable", "foamfix_removeUnloadedEntities", "()V", true));
                     System.out.println("Patched updateEntities in " + classNode.name + " " + method.name);
                  }
               }
            }
         }
      }

      return classNode;
   }
}
