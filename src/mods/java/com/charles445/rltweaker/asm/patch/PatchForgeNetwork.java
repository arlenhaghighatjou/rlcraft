package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchForgeNetwork extends PatchManager {
   public PatchForgeNetwork() {
      super("Forge Network");
      this.add(new Patch(this, "net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_channelRead0 = this.findMethod(clazzNode, new String[]{"channelRead0"});
            if (m_channelRead0 == null) {
               throw new RuntimeException("Couldn't findMethod channelRead0");
            } else {
               MethodInsnNode callOnMessage = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_channelRead0), 185, "onMessage");
               if (callOnMessage == null) {
                  throw new RuntimeException("Couldn't find onMessage in channelRead0");
               } else {
                  callOnMessage.setOpcode(184);
                  callOnMessage.owner = "com/charles445/rltweaker/hook/HookForge";
                  callOnMessage.name = "onMessage";
                  callOnMessage.desc = "(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessageHandler;Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;";
                  callOnMessage.itf = false;
               }
            }
         }
      });
   }
}
