package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchFallingBlockPortalDupe extends PatchManager {
   public PatchFallingBlockPortalDupe() {
      super("Patch Falling Block Portal Dupe");
      this.add(new Patch(this, "net.minecraft.entity.item.EntityFallingBlock", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_onUpdate = this.findMethod(clazzNode, new String[]{"onUpdate", "onUpdate"});
            AbstractInsnNode target = ASMUtil.findMethodInsn(m_onUpdate, 182, "net/minecraft/entity/item/EntityFallingBlock", "move", "move", "(Lnet/minecraft/entity/MoverType;DDD)V", 0);
            LabelNode labelNode = new LabelNode();
            m_onUpdate.instructions.insert(target, ASMUtil.listOf(new VarInsnNode(25, 0), TransformUtil.createObfFieldInsn(180, "net/minecraft/entity/item/EntityFallingBlock", "isDead", "Z"), new JumpInsnNode(153, labelNode), new InsnNode(177), labelNode));
         }
      });
   }
}
