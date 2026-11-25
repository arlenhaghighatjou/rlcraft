package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchWaystoneScroll extends PatchManager {
   public PatchWaystoneScroll() {
      super("Waystone Scroll");
      this.add(new Patch(this, "net.blay09.mods.waystones.item.ItemBoundScroll", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_onItemUseFinish = this.findMethod(clazzNode, new String[]{"onItemUseFinish", "onItemUseFinish"});
            if (m_onItemUseFinish == null) {
               throw new RuntimeException("Couldn't find method onItemUseFinish or onItemUseFinish in ItemBoundScroll");
            } else {
               AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_onItemUseFinish), 182, "activateWaystone");
               if (anchor == null) {
                  throw new RuntimeException("Couldn't find activateWaystone call in ItemBoundScroll");
               } else {
                  anchor = anchor.getNext();
                  this.removePreviousInsn(m_onItemUseFinish, anchor);
                  this.removePreviousInsn(m_onItemUseFinish, anchor);
                  this.removePreviousInsn(m_onItemUseFinish, anchor);
                  this.removePreviousInsn(m_onItemUseFinish, anchor);
                  this.removePreviousInsn(m_onItemUseFinish, anchor);
                  this.removePreviousInsn(m_onItemUseFinish, anchor);
               }
            }
         }
      });
   }
}
