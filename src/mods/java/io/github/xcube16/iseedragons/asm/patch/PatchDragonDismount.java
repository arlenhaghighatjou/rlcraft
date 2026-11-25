package io.github.xcube16.iseedragons.asm.patch;

import io.github.xcube16.iseedragons.ISD;
import io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer;
import io.github.xcube16.iseedragons.asm.Patch;
import io.github.xcube16.iseedragons.asm.PatchResult;
import io.github.xcube16.iseedragons.asm.Patcher;
import io.github.xcube16.iseedragons.asm.helper.PatchHelper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@Patcher(
   name = "Fix Dragon Escape",
   config = "DragonDismountFix"
)
public class PatchDragonDismount {
   public PatchDragonDismount() {
   }

   @Patch(
      target = "net.minecraft.entity.player.EntityPlayer",
      desc = "add hook to EntityPlayer#updateRidden()"
   )
   public static PatchResult hookPlayerUpdateRidden(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      MethodNode updateRidden = PatchHelper.findMethod(node, "updateRidden");
      boolean foundFirst = false;

      AbstractInsnNode secondIfeqInsn;
      for(secondIfeqInsn = updateRidden.instructions.getFirst(); secondIfeqInsn != null && (secondIfeqInsn.getOpcode() != 153 || !foundFirst); secondIfeqInsn = secondIfeqInsn.getNext()) {
         if (secondIfeqInsn.getOpcode() == 153) {
            foundFirst = true;
         }
      }

      if (secondIfeqInsn == null) {
         ISD.logger.error("Failed to find second ifeq instruction in EntityPlayer#updateRidden");
         return PatchResult.NO_MUTATION;
      } else {
         LabelNode elseBlock = ((JumpInsnNode)secondIfeqInsn).label;
         InsnList callHook = new InsnList();
         callHook.add(new VarInsnNode(25, 0));
         callHook.add(new MethodInsnNode(184, "io/github/xcube16/iseedragons/ISeeDragons", "canPlayerDismountHook", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
         callHook.add(new JumpInsnNode(153, elseBlock));
         updateRidden.instructions.insert(secondIfeqInsn, callHook);
         return PatchResult.NO_FLAGS;
      }
   }

   @Patch(
      target = "com.github.alexthe666.iceandfire.entity.EntityDragonBase",
      desc = "add hook to EntityDragonBase#updatePreyInMouth()"
   )
   public static PatchResult hookDragonShakePray(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      MethodNode updatePreyInMouth = PatchHelper.findMethod(node, "updatePreyInMouth");

      AbstractInsnNode ifNullInsn;
      for(ifNullInsn = updatePreyInMouth.instructions.getFirst(); ifNullInsn != null && ifNullInsn.getOpcode() != 198; ifNullInsn = ifNullInsn.getNext()) {
         ;
      }

      if (ifNullInsn == null) {
         ISD.logger.error("Failed to find ifnull instruction in EntityDragonBase#updatePreyInMouth()");
         return PatchResult.NO_MUTATION;
      } else {
         AbstractInsnNode toRemove;
         AbstractInsnNode next;
         for(toRemove = ifNullInsn.getNext().getNext(); toRemove != null && (toRemove.getType() != 8 || toRemove != ((JumpInsnNode)ifNullInsn).label); toRemove = next) {
            next = toRemove.getNext();
            updatePreyInMouth.instructions.remove(toRemove);
         }

         if (toRemove == null) {
            ISD.logger.error("Failed to find end of if statement in EntityDragonBase#updatePreyInMouth()");
            return PatchResult.NO_MUTATION;
         } else {
            InsnList callHook = new InsnList();
            callHook.add(new VarInsnNode(25, 0));
            callHook.add(new VarInsnNode(25, 1));
            callHook.add(new VarInsnNode(25, 0));
            callHook.add(new MethodInsnNode(182, "com/github/alexthe666/iceandfire/entity/EntityDragonBase", "getAnimationTick", "()I", false));
            callHook.add(new MethodInsnNode(184, "io/github/xcube16/iseedragons/ISeeDragons", "dragonShakePrayHook", "(Lnet/minecraft/entity/passive/EntityTameable;Lnet/minecraft/entity/Entity;I)V", false));
            updatePreyInMouth.instructions.insert(ifNullInsn.getNext(), callHook);
            return PatchResult.NO_FLAGS;
         }
      }
   }
}
