package pl.asie.foamfix.coremod.patches;

import java.util.ListIterator;
import java.util.Map;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import pl.asie.foamfix.coremod.common.FoamyArrayBackedDataManagerMap;
import pl.asie.patchy.TransformerFunction;

public class EntityDataManagerPatch implements TransformerFunction<ClassNode> {
   public EntityDataManagerPatch() {
   }

   public static Map<Integer, DataEntry<?>> newArrayBackedMap() {
      return new FoamyArrayBackedDataManagerMap();
   }

   public ClassNode apply(ClassNode classNode) {
      for(MethodNode method : classNode.methods) {
         ListIterator<AbstractInsnNode> it = method.instructions.iterator();

         while(it.hasNext()) {
            AbstractInsnNode node = (AbstractInsnNode)it.next();
            if (node instanceof MethodInsnNode && node.getOpcode() == 184 && "com/google/common/collect/Maps".equals(((MethodInsnNode)node).owner) && "newHashMap".equals(((MethodInsnNode)node).name)) {
               AbstractInsnNode node2 = (AbstractInsnNode)it.next();
               if (node2 instanceof FieldInsnNode && node2.getOpcode() == 181 && "net/minecraft/network/datasync/EntityDataManager".equals(((FieldInsnNode)node2).owner) && ("entries".equals(((FieldInsnNode)node2).name) || "entries".equals(((FieldInsnNode)node2).name))) {
                  while(!(it.previous() instanceof MethodInsnNode)) {
                     ;
                  }

                  it.next();
                  it.set(new MethodInsnNode(184, "pl/asie/foamfix/coremod/patches/EntityDataManagerPatch", "newArrayBackedMap", "()Ljava/util/Map;", false));
                  it.next();
                  System.out.println("Replaced Maps.newHashMap() in " + classNode.name + " " + method.name);
               }
            }
         }
      }

      return classNode;
   }
}
