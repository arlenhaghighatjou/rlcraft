package pl.asie.foamfix.coremod.patches;

import com.google.common.collect.ImmutableSet;
import java.util.ListIterator;
import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import pl.asie.patchy.TransformerFunction;

public class ModelLoaderCleanupPatch implements TransformerFunction<ClassNode> {
   private static final Set<String> methods = ImmutableSet.of("onResourceManagerReload", "onResourceManagerReload");

   public ModelLoaderCleanupPatch() {
   }

   public ClassNode apply(ClassNode classNode) {
      for(MethodNode methodNode : classNode.methods) {
         if (methods.contains(methodNode.name)) {
            InsnList list = null;
            int added = 0;
            ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator();

            while(it.hasNext()) {
               AbstractInsnNode node = (AbstractInsnNode)it.next();
               if (node instanceof MethodInsnNode && node.getOpcode() == 183 && ((MethodInsnNode)node).owner.equals("net/minecraftforge/client/model/ModelLoader") && ((MethodInsnNode)node).name.equals("<init>")) {
                  node = (AbstractInsnNode)it.next();
                  if (node.getOpcode() == 58 && node instanceof VarInsnNode) {
                     int modelLoaderId = ((VarInsnNode)node).var;
                     list = new InsnList();
                     list.add(new VarInsnNode(25, modelLoaderId));
                     list.add(new MethodInsnNode(184, "pl/asie/foamfix/client/ModelLoaderCleanup", "cleanup", "(Lnet/minecraftforge/client/model/ModelLoader;)V", false));
                  }
               } else if (node.getOpcode() == 177 && list != null) {
                  methodNode.instructions.insertBefore(node, list);
                  ++added;
               }
            }

            System.out.println("Added ModelLoader cleanup patch (" + added + " occurences).");
         }
      }

      return classNode;
   }
}
