package io.github.xcube16.iseedragons.asm.patch;

import io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer;
import io.github.xcube16.iseedragons.asm.Patch;
import io.github.xcube16.iseedragons.asm.PatchResult;
import io.github.xcube16.iseedragons.asm.Patcher;
import io.github.xcube16.iseedragons.asm.helper.PatchHelper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@Patcher(
   name = "Patch Statue Dupe",
   config = "PatchStatueDupe"
)
public class PatchStatueDupe {
   public PatchStatueDupe() {
   }

   @Patch(
      target = "com.github.alexthe666.iceandfire.event.EventLiving"
   )
   public static PatchResult patchStatueDupe(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      MethodNode methodNode = PatchHelper.findMethod(node, "onPlayerAttack");
      LabelNode label = new LabelNode();
      InsnList insnList = new InsnList();
      insnList.add(new VarInsnNode(25, 1));
      insnList.add(new MethodInsnNode(182, "net/minecraftforge/event/entity/player/AttackEntityEvent", "getTarget", "()Lnet/minecraft/entity/Entity;", false));
      insnList.add(new MethodInsnNode(182, "net/minecraft/entity/Entity", "isEntityAlive", "()Z", false));
      insnList.add(new JumpInsnNode(154, label));
      insnList.add(new InsnNode(177));
      insnList.add(label);
      methodNode.instructions.insert(insnList);
      return PatchResult.FRAMES;
   }
}
