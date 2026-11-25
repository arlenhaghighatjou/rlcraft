package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchFixOldHippocampus extends PatchManager {
   public PatchFixOldHippocampus() {
      super("Old Hippocampus");
      this.add(new Patch(this, "com.github.alexthe666.iceandfire.entity.EntityHippocampus", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_openGUI = this.findMethod(clazzNode, new String[]{"openGUI"});
            if (m_openGUI == null) {
               this.cancelled = true;
               this.announce("Couldn't find openGUI in EntityHippocampus, skipping");
            } else {
               AbstractInsnNode anchor = TransformUtil.findNextFieldWithOpcodeAndName(this.first(m_openGUI), 180, "isRemote", "isRemote");
               if (anchor == null) {
                  this.cancelled = true;
                  this.announce("Couldn't find isRemote in EntityHippocampus, skipping");
               } else {
                  anchor = this.nextInsn(anchor);
                  if (anchor == null) {
                     this.cancelled = true;
                     this.announce("Unexpected end of method in EntityHippocampus, skipping");
                  } else if (anchor.getType() != 7) {
                     this.cancelled = true;
                     this.announce("Remote check was not a jump, skipping");
                  } else {
                     JumpInsnNode remoteJump = (JumpInsnNode)anchor;
                     if (remoteJump.getOpcode() != 153) {
                        this.cancelled = true;
                        this.announce("Remote check was server, skipping");
                     } else {
                        remoteJump.setOpcode(154);
                        AbstractInsnNode var7 = TransformUtil.findNextCallWithOpcodeAndName(remoteJump, 182, "isPassenger", "isPassenger");
                        if (var7 == null) {
                           throw new RuntimeException("Couldn't find isPassenger or isPassenger in old EntityHippocampus");
                        } else {
                           AbstractInsnNode var8 = this.nextInsn(var7);
                           if (var8 == null) {
                              throw new RuntimeException("Unexpected critical end of method in EntityHippocampus");
                           } else if (var8.getType() != 7) {
                              throw new RuntimeException("Couldn't find branch after isPassenger in old EntityHippocampus");
                           } else {
                              JumpInsnNode passengerJump = (JumpInsnNode)var8;
                              if (passengerJump.getOpcode() != 154) {
                                 throw new RuntimeException("Branch for isPassenger is already IFNE in old EntityHippocampus, somehow. Something has gone wrong.");
                              } else {
                                 passengerJump.setOpcode(153);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      });
   }
}
