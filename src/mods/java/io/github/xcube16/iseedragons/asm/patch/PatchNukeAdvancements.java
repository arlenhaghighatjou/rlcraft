package io.github.xcube16.iseedragons.asm.patch;

import io.github.xcube16.iseedragons.ISD;
import io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer;
import io.github.xcube16.iseedragons.asm.Patch;
import io.github.xcube16.iseedragons.asm.PatchResult;
import io.github.xcube16.iseedragons.asm.Patcher;
import java.util.Optional;
import java.util.function.Predicate;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

@Patcher(
   name = "Remove all achievements",
   config = "NukeAchievements",
   defaultEnable = false
)
public class PatchNukeAdvancements {
   public PatchNukeAdvancements() {
   }

   @Patch(
      target = "net.minecraft.advancements.AdvancementManager"
   )
   public static PatchResult nukeAdvancementManager(ISeeDragonsTransformer tweaker, ClassNode node) {
      Optional<MethodNode> reloadAdvMethod = node.methods.stream().filter((method) -> {
         return method.name.equals("reload");
      }).findFirst();
      if (!reloadAdvMethod.isPresent()) {
         ISD.logger.warn("Failed to find reload() (reload) method");
         return PatchResult.NO_MUTATION;
      } else {
         ((MethodNode)reloadAdvMethod.get()).instructions.clear();
         ((MethodNode)reloadAdvMethod.get()).localVariables.clear();
         ((MethodNode)reloadAdvMethod.get()).tryCatchBlocks.clear();
         ((MethodNode)reloadAdvMethod.get()).instructions.add(new InsnNode(177));
         return PatchResult.NO_FLAGS;
      }
   }
}
