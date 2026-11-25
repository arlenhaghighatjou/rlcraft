package io.github.xcube16.iseedragons.asm.patch;

import io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer;
import io.github.xcube16.iseedragons.asm.Patch;
import io.github.xcube16.iseedragons.asm.PatchResult;
import io.github.xcube16.iseedragons.asm.Patcher;
import io.github.xcube16.iseedragons.asm.helper.PatchHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@Patcher(
   name = "Ice and Fire Myrmex egg dupe fix",
   config = "FixMyrmexDupeBug"
)
public class PatchMyrmexEggDupe {
   public PatchMyrmexEggDupe() {
   }

   @Patch(
      target = "com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg",
      desc = "adds a check to if the entity is already dead"
   )
   public static PatchResult fixMyrmexEggDupe(ISeeDragonsTransformer tweaker, ClassNode node) throws NoSuchMethodException {
      MethodNode attackEntityFrom = PatchHelper.findMethod(node, "attackEntityFrom");
      InsnList deadCheck = new InsnList();
      LabelNode continueLabel = new LabelNode();
      deadCheck.add(new VarInsnNode(25, 0));
      deadCheck.add(new FieldInsnNode(180, "net/minecraft/entity/Entity", "isDead", "Z"));
      deadCheck.add(new JumpInsnNode(153, continueLabel));
      deadCheck.add(new InsnNode(3));
      deadCheck.add(new InsnNode(172));
      deadCheck.add(new FrameNode(3, 3, new Object[]{"com/github/alexthe666/entity/EntityMyrmexEgg", "net/minecraft/util/DamageSource", Opcodes.FLOAT}, 0, new Object[0]));
      deadCheck.add(continueLabel);
      attackEntityFrom.instructions.insert(deadCheck);
      return PatchResult.NO_FLAGS;
   }
}
