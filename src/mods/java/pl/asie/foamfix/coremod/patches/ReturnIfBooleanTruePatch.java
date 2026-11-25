package pl.asie.foamfix.coremod.patches;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import pl.asie.patchy.TransformerFunction;

public class ReturnIfBooleanTruePatch implements TransformerFunction<ClassNode> {
   private final String optionName;
   private final Set<String> methods;
   private final InsnList list;

   public ReturnIfBooleanTruePatch(String optionName, String... methods) {
      this.optionName = optionName;
      this.methods = ImmutableSet.copyOf(methods);
      this.list = new InsnList();
      Label l = new Label();
      LabelNode ln = new LabelNode(l);
      this.list.add(new FieldInsnNode(178, "pl/asie/foamfix/shared/FoamFixShared", "config", "Lpl/asie/foamfix/shared/FoamFixConfig;"));
      this.list.add(new FieldInsnNode(180, "pl/asie/foamfix/shared/FoamFixConfig", optionName, "Z"));
      this.list.add(new JumpInsnNode(153, ln));
      this.list.add(new InsnNode(177));
      this.list.add(ln);
      this.list.add(new FrameNode(3, 0, (Object[])null, 0, (Object[])null));
   }

   public ClassNode apply(ClassNode classNode) {
      for(MethodNode methodNode : classNode.methods) {
         if (this.methods.contains(methodNode.name)) {
            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), this.list);
            System.out.println("Added return if option true in " + classNode.name + " " + methodNode.name);
         }
      }

      return classNode;
   }
}
