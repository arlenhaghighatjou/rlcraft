package pl.asie.foamfix.coremod.patches;

import java.util.ListIterator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import pl.asie.patchy.TransformerFunction;

public class FastAirLookupPatch implements TransformerFunction<ClassNode> {
   public FastAirLookupPatch() {
   }

   public ClassNode apply(ClassNode classNode) {
      for(MethodNode method : classNode.methods) {
         ListIterator<AbstractInsnNode> it = method.instructions.iterator();

         while(it.hasNext()) {
            AbstractInsnNode node = (AbstractInsnNode)it.next();
            if (node instanceof FieldInsnNode && node.getOpcode() == 178 && "net/minecraft/init/Blocks".equals(((FieldInsnNode)node).owner) && ("AIR".equals(((FieldInsnNode)node).name) || "AIR".equals(((FieldInsnNode)node).name))) {
               AbstractInsnNode node2 = (AbstractInsnNode)it.next();
               if (node2 instanceof MethodInsnNode && node2.getOpcode() == 184 && "net/minecraft/item/Item".equals(((MethodInsnNode)node2).owner) && ("getItemFromBlock".equals(((MethodInsnNode)node2).name) || "getItemFromBlock".equals(((MethodInsnNode)node2).name))) {
                  it.remove();
                  it.previous();
                  it.set(new MethodInsnNode(184, "pl/asie/foamfix/FoamFix", "getItemAir", "()Lnet/minecraft/item/Item;", false));
                  System.out.println("Replaced Item.getItemFromBlock(Blocks.AIR) in " + classNode.name + " " + method.name);
               }
            }
         }
      }

      return classNode;
   }
}
