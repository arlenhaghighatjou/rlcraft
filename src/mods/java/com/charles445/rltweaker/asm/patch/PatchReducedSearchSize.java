package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ASMLogger;
import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchReducedSearchSize extends PatchManager {
   public PatchReducedSearchSize() {
      super("Reduced Search Size");
      this.add(new Patch(this, "net.minecraft.world.World", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_getEntitiesWithinAABB = this.findMethodWithDesc(clazzNode, "(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;", new String[]{"getEntitiesWithinAABB", "getEntitiesWithinAABB"});
            if (m_getEntitiesWithinAABB == null) {
               throw new RuntimeException("Couldn't find getEntitiesWithinAABB or getEntitiesWithinAABB with matching desc");
            } else {
               MethodInsnNode getAABBCall = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_getEntitiesWithinAABB), 182, "getEntitiesWithinAABB", "getEntitiesWithinAABB");
               if (getAABBCall == null) {
                  ASMLogger.warn("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesWithinAABB or getEntitiesWithinAABB");
                  ClassDisplayer.instance.printMethod(m_getEntitiesWithinAABB);
                  throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or getEntitiesWithinAABB");
               } else {
                  getAABBCall.setOpcode(184);
                  getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
                  getAABBCall.name = "getEntitiesWithinAABB";
                  getAABBCall.desc = "(Lnet/minecraft/world/World;Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
               }
            }
         }
      });
   }
}
