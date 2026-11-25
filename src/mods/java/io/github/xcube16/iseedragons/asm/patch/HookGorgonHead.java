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
   config = "HookGorgonHead"
)
public class HookGorgonHead {
   public HookGorgonHead() {
   }

   @Patch(
      target = "com.github.alexthe666.iceandfire.item.ItemGorgonHead$1",
      desc = "Hook ItemGorganHead$1 to stop some entities from getting stoned"
   )
   public static PatchResult hookItemGorgon(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      MethodNode apply = PatchHelper.findMethod(node, "apply", "(Lnet/minecraft/entity/Entity;)Z");
      AbstractInsnNode first = ASMHelper.findFirstInstruction(apply);
      InsnList hook = new InsnList();
      LabelNode continueLabel = new LabelNode();
      hook.add(new VarInsnNode(25, 1));
      hook.add(new MethodInsnNode(184, "io/github/xcube16/iseedragons/ISeeDragons", "canStoneHook", "(Lnet/minecraft/entity/Entity;)Z", false));
      hook.add(new JumpInsnNode(157, continueLabel));
      hook.add(new InsnNode(3));
      hook.add(new InsnNode(172));
      hook.add(continueLabel);
      apply.instructions.insertBefore(first, hook);
      return PatchResult.FRAMES;
   }

   @Patch(
      target = "com.github.alexthe666.iceandfire.entity.EntityGorgon$4",
      desc = "Hook EntityGorgon$4 to stop some entities from getting stoned"
   )
   public static PatchResult hookEntityGorgon(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      return hookItemGorgon(tweaker, node);
   }
}
