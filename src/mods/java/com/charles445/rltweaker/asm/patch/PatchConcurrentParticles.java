package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchConcurrentParticles extends PatchManager {
   public PatchConcurrentParticles() {
      super("Concurrent Particles");
      this.add(new Patch(this, "net.minecraft.client.particle.ParticleManager", 1) {
         public void patch(ClassNode c_ParticleManager) {
            MethodNode m_init = this.findMethod(c_ParticleManager, new String[]{"<init>"});
            AbstractInsnNode anchor = TransformUtil.findNextFieldWithOpcodeAndName(this.first(m_init), 181, "queue", "queue");
            if (anchor == null) {
               throw new RuntimeException("Couldn't find queue or queue");
            } else {
               MethodInsnNode hookCaller = TransformUtil.findPreviousCallWithOpcodeAndName(anchor, 184, "newArrayDeque");
               if (hookCaller == null) {
                  throw new RuntimeException("Couldn't find newArrayDeque");
               } else {
                  hookCaller.owner = "com/charles445/rltweaker/hook/HookMinecraft";
                  hookCaller.name = "newConcurrentLinkedDeque";
                  hookCaller.desc = "()Ljava/util/concurrent/ConcurrentLinkedDeque;";
               }
            }
         }
      });
   }
}
