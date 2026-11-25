package org.spongepowered.asm.mixin.transformer;

import java.util.Map.Entry;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidInterfaceMixinException;

class MixinApplicatorInterface extends MixinApplicatorStandard {
   MixinApplicatorInterface(TargetClassContext context) {
      super(context);
   }

   protected void applyInterfaces(MixinTargetContext mixin) {
      for(String interfaceName : mixin.getInterfaces()) {
         if (!this.targetClass.name.equals(interfaceName) && !this.targetClass.interfaces.contains(interfaceName)) {
            this.targetClass.interfaces.add(interfaceName);
            mixin.getTargetClassInfo().addInterface(interfaceName);
         }
      }

   }

   protected void applyFields(MixinTargetContext mixin) {
      for(Entry<FieldNode, ClassInfo.Field> entry : mixin.getShadowFields()) {
         FieldNode shadow = (FieldNode)entry.getKey();
         this.logger.error("Ignoring redundant @Shadow field {}:{} in {}", shadow.name, shadow.desc, mixin);
      }

      this.mergeNewFields(mixin);
   }

   protected void applyInitialisers(MixinTargetContext mixin) {
   }

   protected void prepareInjections(MixinTargetContext mixin) {
      for(MethodNode method : this.targetClass.methods) {
         try {
            InjectionInfo injectInfo = InjectionInfo.parse(mixin, method);
            if (injectInfo != null) {
               throw new InvalidInterfaceMixinException(mixin, injectInfo + " is not supported on interface mixin method " + method.name);
            }
         } catch (InvalidInjectionException var6) {
            String description = var6.getContext() != null ? var6.getContext().toString() : "Injection";
            throw new InvalidInterfaceMixinException(mixin, description + " is not supported in interface mixin");
         }
      }

   }

   protected void applyInjections(MixinTargetContext mixin) {
   }
}
