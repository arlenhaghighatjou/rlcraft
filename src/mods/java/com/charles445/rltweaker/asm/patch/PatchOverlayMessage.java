package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ObfHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchOverlayMessage extends PatchManager {
   public PatchOverlayMessage() {
      super("Overlay Message");
      this.add(new Patch(this, "net.minecraftforge.client.GuiIngameForge", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_renderGameOverlay = this.findMethod(clazzNode, new String[]{"renderRecordOverlay"});
            if (m_renderGameOverlay == null) {
               throw new RuntimeException("Couldn't find renderRecordOverlay");
            } else {
               AbstractInsnNode anchor = TransformUtil.findNextIntInsnNodeWithValue(this.first(m_renderGameOverlay), 68);
               if (anchor == null) {
                  throw new RuntimeException("Couldn't find BIPUSH 68 in renderRecordOverlay");
               } else {
                  this.insert(m_renderGameOverlay, anchor, new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "overlayTextYOffset", "(I)I", false));
                  MethodInsnNode textCall = TransformUtil.findNextCallWithOpcodeAndName(anchor, 182, "drawString", "drawString");
                  if (textCall == null) {
                     throw new RuntimeException("Couldn't find drawString or drawString call in renderRecordOverlay");
                  } else {
                     textCall.name = ObfHelper.isObfuscated() ? "drawString" : "drawString";
                     textCall.desc = "(Ljava/lang/String;FFIZ)I";
                     this.insertBefore(m_renderGameOverlay, textCall, new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "overlayTextDropShadow", "()Z", false));
                     anchor = TransformUtil.findPreviousIntInsnNodeWithValue(textCall, -4);
                     if (anchor == null) {
                        throw new RuntimeException("Couldn't find BIPUSH -4 in renderRecordOverlay, please report to RLTweaker dev");
                     } else {
                        this.insertInsnBefore(m_renderGameOverlay, anchor, 134);
                        this.insertInsn(m_renderGameOverlay, anchor, 134);
                     }
                  }
               }
            }
         }
      });
   }
}
