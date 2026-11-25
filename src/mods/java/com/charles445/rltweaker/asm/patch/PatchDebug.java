package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class PatchDebug extends PatchManager {
   private MethodInsnNode objectDebug() {
      return new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookDebug", "printObject", "(Ljava/lang/Object;)V", false);
   }

   private LdcInsnNode stringObject(String s) {
      return new LdcInsnNode(s);
   }

   private InsnList debugPrint(String s) {
      InsnList inject = new InsnList();
      inject.add(this.stringObject(s));
      inject.add(this.objectDebug());
      return inject;
   }

   public PatchDebug() {
      super("Debug");
   }
}
