package org.spongepowered.asm.mixin.transformer;

import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

class MixinCoprocessorSyntheticInner extends MixinCoprocessor {
   private final Set<String> syntheticInnerClasses = new HashSet();

   MixinCoprocessorSyntheticInner() {
   }

   String getName() {
      return "syntheticinner";
   }

   public void onInit(MixinInfo mixin) {
      for(String innerClass : mixin.getSyntheticInnerClasses()) {
         this.registerSyntheticInner(innerClass.replace('/', '.'));
      }

   }

   void registerSyntheticInner(String className) {
      this.syntheticInnerClasses.add(className);
   }

   MixinCoprocessor.ProcessResult process(String className, ClassNode classNode) {
      if (!this.syntheticInnerClasses.contains(className)) {
         return MixinCoprocessor.ProcessResult.NONE;
      } else {
         classNode.access |= 1;

         for(FieldNode field : classNode.fields) {
            if ((field.access & 6) == 0) {
               field.access |= 1;
            }
         }

         for(MethodNode method : classNode.methods) {
            if ((method.access & 6) == 0) {
               method.access |= 1;
            }
         }

         return MixinCoprocessor.ProcessResult.PASSTHROUGH_TRANSFORMED;
      }
   }
}
