package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.tree.ClassNode;

public interface IPatch {
   String getTargetClazz();

   int getFlags();

   IPatchManager getPatchManager();

   void patch(ClassNode var1);

   boolean isCancelled();
}
