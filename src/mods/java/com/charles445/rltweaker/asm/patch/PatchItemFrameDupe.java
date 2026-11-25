package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchItemFrameDupe extends PatchManager {
   public PatchItemFrameDupe() {
      super("Item Frame Dupe");
      this.add(new Patch(this, "net.minecraft.entity.item.EntityItemFrame", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_onBroken = this.findMethod(clazzNode, new String[]{"onBroken", "onBroken"});
            if (m_onBroken == null) {
               throw new RuntimeException("Couldn't find onBroken or onBroken");
            } else {
               AbstractInsnNode dropItemCall = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_onBroken), 182, "dropItemOrSelf", "dropItemOrSelf");
               if (dropItemCall == null) {
                  throw new RuntimeException("Couldn't find call to dropItemOrSelf or dropItemOrSelf");
               } else {
                  InsnList inject = new InsnList();
                  inject.add(new VarInsnNode(25, 0));
                  inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "clearItemFrame", "(Lnet/minecraft/entity/item/EntityItemFrame;)V", false));
                  this.insert(m_onBroken, dropItemCall, inject);
               }
            }
         }
      });
   }
}
