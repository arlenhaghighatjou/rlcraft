package codersafterdark.reskillable.base.asm;

import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassTransformer implements IClassTransformer {
   public ClassTransformer() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if (transformedName.equals("net.minecraft.client.renderer.InventoryEffectRenderer")) {
         ClassReader reader = new ClassReader(basicClass);
         ClassNode node = new ClassNode();
         reader.accept(node, 0);
         String funcName = "drawActivePotionEffects";
         String obfName = "f";
         String desc = "()V";

         for(MethodNode method : node.methods) {
            if ((method.name.equals(funcName) || method.name.equals(obfName)) && method.desc.equals(desc)) {
               Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

               while(iterator.hasNext()) {
                  AbstractInsnNode itrNode = (AbstractInsnNode)iterator.next();
                  if (itrNode.getOpcode() == 16) {
                     IntInsnNode intNode = (IntInsnNode)itrNode;
                     if (intNode.operand == 124) {
                        MethodInsnNode newNode = new MethodInsnNode(184, "codersafterdark/reskillable/client/gui/handler/InventoryTabHandler", "getPotionOffset", "()I", false);
                        method.instructions.insert(intNode, newNode);
                        method.instructions.remove(intNode);
                        break;
                     }
                  }
               }

               ClassWriter writer = new ClassWriter(3);
               node.accept(writer);
               return writer.toByteArray();
            }
         }
      }

      return basicClass;
   }
}
