package org.spongepowered.asm.mixin.transformer;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidInterfaceMixinException;
import org.spongepowered.asm.util.Bytecode;

class MixinPreProcessorInterface extends MixinPreProcessorStandard {
   MixinPreProcessorInterface(MixinInfo mixin, MixinInfo.MixinClassNode classNode) {
      super(mixin, classNode);
   }

   protected void prepareMethod(MixinInfo.MixinMethodNode mixinMethod, ClassInfo.Method method) {
      if (!Bytecode.hasFlag(mixinMethod, 1)) {
         if (!Bytecode.hasFlag(mixinMethod, 4096)) {
            throw new InvalidInterfaceMixinException(this.mixin, String.format("Interface mixin contains a non-public method! Found %s in %s", method, this.mixin));
         }

         MixinEnvironment.CompatibilityLevel requiredLevel = MixinEnvironment.CompatibilityLevel.requiredFor(2);
         if (MixinEnvironment.getCompatibilityLevel().isLessThan(requiredLevel)) {
            throw new InvalidInterfaceMixinException(this.mixin, String.format("Interface mixin contains a synthetic private method but compatibility level %s is required! Found %s in %s", requiredLevel, method, this.mixin));
         }
      }

      super.prepareMethod(mixinMethod, method);
   }

   protected boolean validateField(MixinTargetContext context, FieldNode field, AnnotationNode shadow) {
      if (!Bytecode.isStatic(field)) {
         throw new InvalidInterfaceMixinException(this.mixin, String.format("Interface mixin contains an instance field! Found %s in %s", field.name, this.mixin));
      } else {
         return super.validateField(context, field, shadow);
      }
   }
}
