package com.charles445.rltweaker.asm.patch.compat;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchCatServer extends PatchManager {
   public PatchCatServer() {
      super("CatServer");
      this.add(new Patch(this, "net.minecraftforge.common.ForgeHooks", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_loadFactories = this.findMethod(clazzNode, new String[]{"loadFactories"});
            if (m_loadFactories == null) {
               throw new RuntimeException("Couldn't find loadFactories method in ForgeHooks");
            } else {
               MethodInsnNode hookCall = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_loadFactories), 184, "loadFactories");
               if (hookCall == null) {
                  throw new RuntimeException("Couldn't find loadFactories invokestatic in ForgeHooks loadFactories");
               } else {
                  hookCall.setOpcode(184);
                  hookCall.owner = "com/charles445/rltweaker/hook/compat/HookCatServer";
                  hookCall.name = "loadAdvancementFactories";
                  hookCall.desc = "(Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;[Ljava/lang/Object;)V";
                  this.announce("Patched loadFactories to avoid CatServer crashes");
               }
            }
         }
      });
   }
}
