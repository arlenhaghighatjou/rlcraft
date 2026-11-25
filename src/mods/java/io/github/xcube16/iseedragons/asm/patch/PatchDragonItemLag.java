package io.github.xcube16.iseedragons.asm.patch;

import io.github.xcube16.iseedragons.ISD;
import io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer;
import io.github.xcube16.iseedragons.asm.Patch;
import io.github.xcube16.iseedragons.asm.PatchResult;
import io.github.xcube16.iseedragons.asm.Patcher;
import java.util.Optional;
import java.util.function.Predicate;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@Patcher(
   name = "Fix Dragon Item Lag",
   config = "DragonLag"
)
public class PatchDragonItemLag {
   public PatchDragonItemLag() {
   }

   @Patch(
      target = "com.github.alexthe666.iceandfire.entity.EntityDragonBase",
      desc = "hooks into EntityDragonBase#breakBlock to spawn less items"
   )
   public static PatchResult fixEntityDragonBase(ISeeDragonsTransformer tweaker, ClassNode node) {
      Optional<MethodNode> breakBlockMethod = node.methods.stream().filter((method) -> {
         return method.name.equals("breakBlock");
      }).findFirst();
      if (!breakBlockMethod.isPresent()) {
         ISD.logger.warn("Failed to find breakBlock() method");
         return PatchResult.NO_MUTATION;
      } else {
         AbstractInsnNode breakBlockCall;
         for(breakBlockCall = ((MethodNode)breakBlockMethod.get()).instructions.getFirst(); breakBlockCall != null && (breakBlockCall.getOpcode() != 182 || !((MethodInsnNode)breakBlockCall).name.equals("destroyBlock")); breakBlockCall = breakBlockCall.getNext()) {
            ;
         }

         if (breakBlockCall == null) {
            ISD.logger.error("Failed to find destroyBlock (World#destroyBlock()) call");
            return PatchResult.NO_MUTATION;
         } else {
            ((MethodNode)breakBlockMethod.get()).instructions.remove(breakBlockCall.getPrevious());
            InsnList callHook = new InsnList();
            callHook.add(new VarInsnNode(25, 5));
            callHook.add(new MethodInsnNode(184, "io/github/xcube16/iseedragons/ISeeDragons", "dragonBreakBlockHook", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z"));
            ((MethodNode)breakBlockMethod.get()).instructions.insertBefore(breakBlockCall, callHook);
            ((MethodNode)breakBlockMethod.get()).instructions.remove(breakBlockCall);
            return PatchResult.NO_FLAGS;
         }
      }
   }
}
