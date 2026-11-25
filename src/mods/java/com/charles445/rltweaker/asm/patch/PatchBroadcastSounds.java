package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchBroadcastSounds extends PatchManager {
   public PatchBroadcastSounds() {
      super("Broadcast Sounds");
      this.add(new Patch(this, "net.minecraft.world.ServerWorldEventHandler", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_broadcastSound = this.findMethod(clazzNode, new String[]{"broadcastSound", "broadcastSound"});
            if (m_broadcastSound == null) {
               throw new RuntimeException("Couldn't find broadcastSound or broadcastSound");
            } else {
               AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_broadcastSound), 182, "sendPacketToAllPlayers", "sendPacketToAllPlayers");
               if (anchor == null) {
                  throw new RuntimeException("Couldn't find sendPacketToAllPlayers or sendPacketToAllPlayers call in broadcastSound");
               } else {
                  MethodInsnNode call = (MethodInsnNode)anchor;
                  call.setOpcode(184);
                  call.owner = "com/charles445/rltweaker/hook/HookMinecraft";
                  call.name = "playLimitedBroadcastSound";
                  call.desc = "(Lnet/minecraft/server/management/PlayerList;Lnet/minecraft/network/play/server/SPacketEffect;Lnet/minecraft/util/math/BlockPos;)V";
                  this.insertBefore(m_broadcastSound, anchor, new VarInsnNode(25, 2));
               }
            }
         }
      });
   }
}
