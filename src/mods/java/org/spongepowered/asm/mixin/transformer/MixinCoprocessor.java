package org.spongepowered.asm.mixin.transformer;

import org.objectweb.asm.tree.ClassNode;

abstract class MixinCoprocessor implements MixinConfig.IListener {
   MixinCoprocessor() {
   }

   abstract String getName();

   public void onPrepare(MixinInfo mixin) {
   }

   public void onInit(MixinInfo mixin) {
   }

   MixinCoprocessor.ProcessResult process(String className, ClassNode classNode) {
      return MixinCoprocessor.ProcessResult.NONE;
   }

   boolean postProcess(String className, ClassNode classNode) {
      return false;
   }

   static enum ProcessResult {
      NONE(false, false),
      TRANSFORMED(false, true),
      PASSTHROUGH_NONE(true, false),
      PASSTHROUGH_TRANSFORMED(true, true);

      private boolean passthrough;
      private boolean transformed;

      private ProcessResult(boolean passthrough, boolean transformed) {
         this.passthrough = passthrough;
         this.transformed = transformed;
      }

      boolean isPassthrough() {
         return this.passthrough;
      }

      boolean isTransformed() {
         return this.transformed;
      }

      MixinCoprocessor.ProcessResult with(MixinCoprocessor.ProcessResult other) {
         return other == this ? this : of(this.passthrough || other.passthrough, this.transformed || other.transformed);
      }

      static MixinCoprocessor.ProcessResult of(boolean passthrough, boolean transformed) {
         if (passthrough) {
            return transformed ? PASSTHROUGH_TRANSFORMED : PASSTHROUGH_NONE;
         } else {
            return transformed ? TRANSFORMED : NONE;
         }
      }
   }
}
