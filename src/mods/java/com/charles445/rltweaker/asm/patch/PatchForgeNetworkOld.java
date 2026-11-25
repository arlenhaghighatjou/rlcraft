package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchForgeNetworkOld extends PatchManager {
   public PatchForgeNetworkOld() {
      super("Forge Network");
      this.add(new Patch(this, "net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_channelRead0 = this.findMethod(clazzNode, new String[]{"channelRead0"});
            if (m_channelRead0 == null) {
               throw new RuntimeException("Couldn't findMethod channelRead0");
            } else {
               LocalVariableNode lvn_context = TransformUtil.findLocalVariableWithName(m_channelRead0, "context");
               if (lvn_context == null) {
                  throw new RuntimeException("Couldn't findLocalVariableWithName context");
               } else {
                  AbstractInsnNode anchor;
                  for(anchor = this.first(m_channelRead0); anchor != null; anchor = anchor.getNext()) {
                     if (anchor.getType() == 2) {
                        VarInsnNode vAnchor = (VarInsnNode)anchor;
                        if (vAnchor.getOpcode() == 58 && vAnchor.var == lvn_context.index) {
                           break;
                        }
                     }
                  }

                  if (anchor == null) {
                     throw new RuntimeException("Couldn't find 'context' astore");
                  } else {
                     LocalVariableNode lvn_msg = TransformUtil.findLocalVariableWithName(m_channelRead0, "msg");
                     if (lvn_msg == null) {
                        throw new RuntimeException("Couldn't findLocalVariableWithName msg");
                     } else {
                        InsnList insert = new InsnList();
                        insert.add(new VarInsnNode(25, lvn_msg.index));
                        insert.add(new VarInsnNode(25, lvn_context.index));
                        insert.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookForge", "receiveMessage", "(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)V", false));
                        m_channelRead0.instructions.insert(anchor, insert);
                     }
                  }
               }
            }
         }
      });
   }
}
