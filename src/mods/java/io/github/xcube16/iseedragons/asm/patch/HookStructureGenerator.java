package io.github.xcube16.iseedragons.asm.patch;

import io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer;
import io.github.xcube16.iseedragons.asm.Patch;
import io.github.xcube16.iseedragons.asm.PatchResult;
import io.github.xcube16.iseedragons.asm.Patcher;
import io.github.xcube16.iseedragons.asm.helper.ASMHelper;
import io.github.xcube16.iseedragons.asm.helper.PatchHelper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@Patcher(
   name = "Hook Structure Generator",
   config = "HookStructureGenerator"
)
public class HookStructureGenerator {
   public HookStructureGenerator() {
   }

   @Patch(
      target = "com.github.alexthe666.iceandfire.event.StructureGenerator"
   )
   public static PatchResult hookGenerate(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      MethodNode gen = PatchHelper.findMethod(node, "generate");
      AbstractInsnNode first = ASMHelper.findFirstInstruction(gen);
      InsnList hook = new InsnList();
      LabelNode continueLabel = new LabelNode();
      hook.add(new VarInsnNode(25, 4));
      hook.add(new MethodInsnNode(184, "io/github/xcube16/iseedragons/ISeeDragons", "iceAndFireGenerateHook", "(Lnet/minecraft/world/World;)Z", false));
      hook.add(new JumpInsnNode(157, continueLabel));
      hook.add(new InsnNode(177));
      hook.add(continueLabel);
      gen.instructions.insertBefore(first, hook);
      return PatchResult.FRAMES;
   }
}
