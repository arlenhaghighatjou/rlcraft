package io.github.xcube16.iseedragons.asm.patch;

import io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer;
import io.github.xcube16.iseedragons.asm.Patch;
import io.github.xcube16.iseedragons.asm.PatchResult;
import io.github.xcube16.iseedragons.asm.Patcher;
import io.github.xcube16.iseedragons.asm.helper.PatchHelper;
import org.objectweb.asm.tree.ClassNode;

@Patcher(
   name = "Stop Log Spammers",
   config = "STFU"
)
public class PatchLogSpam {
   public PatchLogSpam() {
   }

   @Patch(
      target = "net.minecraft.advancements.AdvancementManager"
   )
   public static PatchResult muteAdvancementManager(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      return PatchHelper.muteMethod(PatchHelper.findMethod(node, "loadCustomAdvancements")).add(PatchHelper.muteMethod(PatchHelper.findMethod(node, "loadBuiltInAdvancements")));
   }

   @Patch(
      target = "net.minecraftforge.common.ForgeHooks"
   )
   public static PatchResult muteForgeHooksLoadAdv(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      return PatchHelper.muteMethod(PatchHelper.findMethod(node, "lambda$loadAdvancements$0"));
   }
}
