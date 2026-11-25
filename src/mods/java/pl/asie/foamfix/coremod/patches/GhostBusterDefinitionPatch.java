package pl.asie.foamfix.coremod.patches;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import pl.asie.foamfix.ghostbuster.GhostBusterDefinition;
import pl.asie.patchy.TransformerFunction;

public class GhostBusterDefinitionPatch implements TransformerFunction<ClassNode> {
   private final GhostBusterDefinition definition;
   private final Set<String> methods;

   public GhostBusterDefinitionPatch(GhostBusterDefinition definition) {
      this.definition = definition;
      this.methods = ImmutableSet.of(this.definition.obfMethodName, this.definition.deobfMethodName);
   }

   public ClassNode apply(ClassNode classNode) {
      for(MethodNode methodNode : classNode.methods) {
         if (this.methods.contains(methodNode.name)) {
            InsnList list = new InsnList();
            Label l = new Label();
            LabelNode ln = new LabelNode(l);
            list.add(new VarInsnNode(25, this.definition.accessPos));
            list.add(new VarInsnNode(25, this.definition.posPos));
            if (this.definition.radius == 0) {
               list.add(new MethodInsnNode(184, "pl/asie/foamfix/ghostbuster/GhostBusterSafeAccessors", "isBlockLoaded", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Z", false));
            } else {
               switch(this.definition.radius) {
               case 1:
                  list.add(new InsnNode(4));
                  break;
               case 2:
                  list.add(new InsnNode(5));
                  break;
               case 3:
                  list.add(new InsnNode(6));
                  break;
               case 4:
                  list.add(new InsnNode(7));
                  break;
               case 5:
                  list.add(new InsnNode(8));
                  break;
               default:
                  throw new RuntimeException("Invalid ghost buster radius: " + this.definition.radius);
               }

               list.add(new MethodInsnNode(184, "pl/asie/foamfix/ghostbuster/GhostBusterSafeAccessors", "isAreaLoaded", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;I)Z", false));
            }

            list.add(new JumpInsnNode(154, ln));
            if (this.definition.returnValue != null) {
               if (!(this.definition.returnValue instanceof Boolean)) {
                  throw new RuntimeException("Invalid ghost buster return value: " + this.definition.returnValue);
               }

               list.add(new InsnNode(((Boolean)this.definition.returnValue).booleanValue() ? 4 : 3));
               list.add(new InsnNode(172));
            } else {
               list.add(new InsnNode(177));
            }

            list.add(ln);
            list.add(new FrameNode(3, 0, (Object[])null, 0, (Object[])null));
            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), list);
            System.out.println("Added ghost buster patch (radius = " + this.definition.radius + ") in " + classNode.name + " " + methodNode.name);
         }
      }

      return classNode;
   }
}
