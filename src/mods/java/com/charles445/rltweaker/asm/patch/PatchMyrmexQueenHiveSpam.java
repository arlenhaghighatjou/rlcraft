package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchMyrmexQueenHiveSpam extends PatchManager {
   public PatchMyrmexQueenHiveSpam() {
      super("Myrmex Queen Hive Spam");
      this.add(new Patch(this, "com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_onLivingUpdate = this.findMethod(clazzNode, new String[]{"onLivingUpdate", "onLivingUpdate"});
            if (m_onLivingUpdate == null) {
               throw new RuntimeException("Couldn't find onLivingUpdate or onLivingUpdate");
            } else {
               AbstractInsnNode anchor = TransformUtil.findNextFieldWithOpcodeAndName(this.first(m_onLivingUpdate), 181, "eggTicks");
               if (anchor == null) {
                  throw new RuntimeException("Couldn't find eggTicks in onLivingUpdate");
               } else {
                  MethodInsnNode canSeeSkyCall = TransformUtil.findNextCallWithOpcodeAndName(anchor, 182, "canSeeSky");
                  if (canSeeSkyCall == null) {
                     throw new RuntimeException("Couldn't find canSeeSky after eggTicks in onLivingUpdate");
                  } else {
                     anchor = ASMHelper.findNextInstruction(canSeeSkyCall);
                     if (anchor.getType() != 7) {
                        throw new RuntimeException("Node after canSeeSky was not a jump instruction");
                     } else {
                        JumpInsnNode canSeeJump = (JumpInsnNode)anchor;
                        LabelNode l_quit = canSeeJump.label;
                        InsnList newCheck = new InsnList();
                        newCheck.add(new VarInsnNode(25, 0));
                        newCheck.add(new MethodInsnNode(182, "com/github/alexthe666/iceandfire/entity/EntityMyrmexQueen", "hasMadeHome", "()Z", false));
                        newCheck.add(new JumpInsnNode(154, l_quit));
                        m_onLivingUpdate.instructions.insert(canSeeJump, newCheck);
                     }
                  }
               }
            }
         }
      });
   }
}
