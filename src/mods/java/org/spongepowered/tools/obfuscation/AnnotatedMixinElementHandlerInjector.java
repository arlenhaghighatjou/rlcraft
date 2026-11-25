package org.spongepowered.tools.obfuscation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorByName;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorConstructor;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorRemappable;
import org.spongepowered.asm.mixin.injection.selectors.InvalidSelectorException;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.util.asm.IAnnotationHandle;
import org.spongepowered.tools.obfuscation.ext.SpecialPackages;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IReferenceManager;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.struct.InjectorRemap;

class AnnotatedMixinElementHandlerInjector extends AnnotatedMixinElementHandler {
   AnnotatedMixinElementHandlerInjector(IMixinAnnotationProcessor ap, AnnotatedMixin mixin) {
      super(ap, mixin);
   }

   public void registerInjector(AnnotatedMixinElementHandlerInjector.AnnotatedElementInjector elem) {
      if (this.mixin.isInterface()) {
         this.ap.printMessage(Kind.ERROR, "Injector in interface is unsupported", elem.getElement());
      }

      for(String reference : elem.getAnnotation().getList("method")) {
         this.registerInjectorTarget(elem, reference, TargetSelector.parse(reference, elem), elem + ".method=\"" + reference + "\"");
      }

      for(IAnnotationHandle desc : elem.getAnnotation().getAnnotationList("target")) {
         String subject = String.format("%s.target=@Desc(id = \"%s\")", elem, desc.getValue("id", ""));
         this.registerInjectorTarget(elem, (String)null, TargetSelector.parse(desc, elem), subject);
      }

   }

   private void registerInjectorTarget(AnnotatedMixinElementHandlerInjector.AnnotatedElementInjector elem, String reference, ITargetSelector targetSelector, String subject) {
      try {
         targetSelector.validate();
      } catch (InvalidSelectorException var8) {
         elem.printMessage(this.ap, Kind.ERROR, var8.getMessage());
      }

      if (targetSelector instanceof ITargetSelectorByName) {
         ITargetSelectorByName targetMember = (ITargetSelectorByName)targetSelector;
         if (targetMember.getName() != null) {
            if (targetMember.getDesc() != null) {
               this.validateReferencedTarget(elem, reference, targetMember, subject);
            }

            if (targetSelector instanceof ITargetSelectorRemappable && elem.shouldRemap()) {
               for(TypeHandle target : this.mixin.getTargets()) {
                  if (!this.registerInjector(elem, reference, (ITargetSelectorRemappable)targetMember, target)) {
                     break;
                  }
               }
            }

         }
      }
   }

   private boolean registerInjector(AnnotatedMixinElementHandlerInjector.AnnotatedElementInjector elem, String reference, ITargetSelectorRemappable targetMember, TypeHandle target) {
      String desc = target.findDescriptor(targetMember);
      if (desc == null) {
         Kind error = this.mixin.isMultiTarget() ? Kind.ERROR : Kind.WARNING;
         if (target.isSimulated()) {
            elem.printMessage(this.ap, Kind.OTHER, elem + " target '" + reference + "' in @Pseudo mixin will not be obfuscated");
         } else if (target.isImaginary()) {
            elem.printMessage(this.ap, error, elem + " target requires method signature because enclosing type information for " + target + " is unavailable");
         } else if (!targetMember.isInitialiser()) {
            elem.printMessage(this.ap, error, "Unable to determine signature for " + elem + " target method");
         }

         return true;
      } else {
         String targetName = elem + " target " + targetMember.getName();
         MappingMethod targetMethod = target.getMappingMethod(targetMember.getName(), desc);
         ObfuscationData<MappingMethod> obfData = this.obf.getDataProvider().getObfMethod(targetMethod);
         if (obfData.isEmpty()) {
            if (!target.isSimulated()) {
               if (targetMember.isClassInitialiser()) {
                  return true;
               }

               Kind error = targetMember.isConstructor() ? Kind.WARNING : Kind.ERROR;
               elem.addMessage(error, "No obfuscation mapping for " + targetName, elem.getElement(), elem.getAnnotation());
               return false;
            }

            obfData = this.obf.getDataProvider().getRemappedMethod(targetMethod);
         }

         IReferenceManager refMap = this.obf.getReferenceManager();

         try {
            if (targetMember.getOwner() == null && this.mixin.isMultiTarget() || target.isSimulated()) {
               obfData = AnnotatedMixinElementHandler.<MappingMethod>stripOwnerData(obfData);
            }

            refMap.addMethodMapping(this.classRef, reference, obfData);
         } catch (ReferenceManager.ReferenceConflictException var16) {
            String conflictType = this.mixin.isMultiTarget() ? "Multi-target" : "Target";
            if (elem.hasCoerceArgument() && targetMember.getOwner() == null && targetMember.getDesc() == null) {
               ITargetSelector oldMember = TargetSelector.parse(var16.getOld(), elem);
               ITargetSelector newMember = TargetSelector.parse(var16.getNew(), elem);
               String oldName = oldMember instanceof ITargetSelectorByName ? ((ITargetSelectorByName)oldMember).getName() : oldMember.toString();
               String newName = newMember instanceof ITargetSelectorByName ? ((ITargetSelectorByName)newMember).getName() : newMember.toString();
               if (oldName != null && oldName.equals(newName)) {
                  obfData = AnnotatedMixinElementHandler.<MappingMethod>stripDescriptors(obfData);
                  refMap.setAllowConflicts(true);
                  refMap.addMethodMapping(this.classRef, reference, obfData);
                  refMap.setAllowConflicts(false);
                  elem.printMessage(this.ap, Kind.WARNING, "Coerced " + conflictType + " reference has conflicting descriptors for " + targetName + ": Storing bare references " + obfData.values() + " in refMap");
                  return true;
               }
            }

            elem.printMessage(this.ap, Kind.ERROR, conflictType + " reference conflict for " + targetName + ": " + reference + " -> " + var16.getNew() + " previously defined as " + var16.getOld());
         }

         return true;
      }
   }

   public void registerInjectionPoint(AnnotatedMixinElementHandlerInjector.AnnotatedElementInjectionPoint elem, String format) {
      if (this.mixin.isInterface()) {
         this.ap.printMessage(Kind.ERROR, "Injector in interface is unsupported", elem.getElement());
      }

      ITargetSelector targetSelector = null;
      String targetReference = (String)elem.getAt().getValue("target");
      if (targetReference != null) {
         targetSelector = TargetSelector.parse(targetReference, elem);

         try {
            targetSelector.validate();
         } catch (InvalidSelectorException var10) {
            this.ap.printMessage(Kind.ERROR, var10.getMessage(), elem.getElement(), elem.getAtErrorElement(this.ap.getCompilerEnvironment()));
         }
      }

      String type = InjectionPointData.parseType((String)elem.getAt().getValue("value", ""));
      ITargetSelector classSelector = null;
      String classReference = elem.getAtArg("class");
      if ("NEW".equals(type) && classReference != null) {
         classSelector = TargetSelector.parse(classReference, elem);

         try {
            classSelector.validate();
         } catch (InvalidSelectorException var9) {
            this.ap.printMessage(Kind.ERROR, var9.getMessage(), elem.getElement(), elem.getAtErrorElement(this.ap.getCompilerEnvironment()));
         }
      }

      if (elem.shouldRemap()) {
         if ("NEW".equals(type)) {
            this.remapNewTarget(String.format(format, type + ".<target>"), targetReference, targetSelector, elem);
            this.remapNewTarget(String.format(format, type + ".args[class]"), classReference, classSelector, elem);
         } else {
            this.remapReference(String.format(format, type + ".<target>"), targetReference, targetSelector, elem);
         }
      }

   }

   protected final void remapNewTarget(String subject, String reference, ITargetSelector selector, AnnotatedMixinElementHandlerInjector.AnnotatedElementInjectionPoint elem) {
      if (selector instanceof ITargetSelectorConstructor) {
         ITargetSelectorConstructor member = (ITargetSelectorConstructor)selector;
         String target = member.toCtorType();
         if (target != null) {
            String desc = member.toCtorDesc();
            MappingMethod m = new MappingMethod(target, ".", desc != null ? desc : "()V");
            ObfuscationData<MappingMethod> remapped = this.obf.getDataProvider().getRemappedMethod(m);
            if (remapped.isEmpty() && !SpecialPackages.isExcludedPackage(member.toCtorType())) {
               this.ap.printMessage(Kind.WARNING, "Cannot find class mapping for " + subject + " '" + target + "'", elem.getElement(), elem.getAnnotation().asMirror(), SuppressedBy.MAPPING);
               return;
            }

            ObfuscationData<String> mappings = new ObfuscationData<String>();

            for(ObfuscationType type : remapped) {
               MappingMethod mapping = remapped.get(type);
               if (desc == null) {
                  mappings.put(type, mapping.getOwner());
               } else {
                  mappings.put(type, mapping.getDesc().replace(")V", ")L" + mapping.getOwner() + ";"));
               }
            }

            this.obf.getReferenceManager().addClassMapping(this.classRef, reference, mappings);
         }

         elem.notifyRemapped();
      }
   }

   protected final void remapReference(String subject, String reference, ITargetSelector selector, AnnotatedMixinElementHandlerInjector.AnnotatedElementInjectionPoint elem) {
      if (selector instanceof ITargetSelectorRemappable) {
         ITargetSelectorRemappable targetMember = (ITargetSelectorRemappable)selector;
         AnnotationMirror errorElement = elem.getAtErrorElement(this.ap.getCompilerEnvironment());
         if (!targetMember.isFullyQualified()) {
            String missing = targetMember.getOwner() == null ? (targetMember.getDesc() == null ? "owner and signature" : "owner") : "signature";
            this.ap.printMessage(Kind.ERROR, subject + " is not fully qualified, missing " + missing, elem.getElement(), errorElement);
         } else {
            try {
               if (targetMember.isField()) {
                  ObfuscationData<MappingField> obfFieldData = this.obf.getDataProvider().getObfFieldRecursive(targetMember);
                  if (obfFieldData.isEmpty()) {
                     if (targetMember.getOwner() == null || !SpecialPackages.isExcludedPackage(targetMember.getOwner())) {
                        this.ap.printMessage(Kind.WARNING, "Cannot find field mapping for " + subject + " '" + reference + "'", elem.getElement(), errorElement, SuppressedBy.MAPPING);
                     }

                     return;
                  }

                  this.obf.getReferenceManager().addFieldMapping(this.classRef, reference, targetMember, obfFieldData);
               } else {
                  ObfuscationData<MappingMethod> obfMethodData = this.obf.getDataProvider().getObfMethodRecursive(targetMember);
                  if (obfMethodData.isEmpty()) {
                     if (targetMember.getOwner() == null || !SpecialPackages.isExcludedPackage(targetMember.getOwner())) {
                        this.ap.printMessage(Kind.WARNING, "Cannot find method mapping for " + subject + " '" + reference + "'", elem.getElement(), errorElement, SuppressedBy.MAPPING);
                     }

                     return;
                  }

                  this.obf.getReferenceManager().addMethodMapping(this.classRef, reference, targetMember, obfMethodData);
               }
            } catch (ReferenceManager.ReferenceConflictException var8) {
               this.ap.printMessage(Kind.ERROR, "Unexpected reference conflict for " + subject + ": " + reference + " -> " + var8.getNew() + " previously defined as " + var8.getOld(), elem.getElement(), errorElement);
               return;
            }

            elem.notifyRemapped();
         }
      }
   }

   static class AnnotatedElementInjectionPoint extends AnnotatedMixinElementHandler.AnnotatedElementExecutable {
      private final AnnotationHandle at;
      private Map<String, String> args;
      private final InjectorRemap state;

      public AnnotatedElementInjectionPoint(ExecutableElement element, AnnotationHandle inject, IMixinContext context, String selectorCoordinate, AnnotationHandle at, InjectorRemap state) {
         super(element, inject, context, selectorCoordinate);
         this.at = at;
         this.state = state;
      }

      public boolean shouldRemap() {
         return this.at.getBoolean("remap", this.state.shouldRemap());
      }

      public AnnotationHandle getAt() {
         return this.at;
      }

      public AnnotationMirror getAtErrorElement(IMixinAnnotationProcessor.CompilerEnvironment compilerEnvironment) {
         return (compilerEnvironment == IMixinAnnotationProcessor.CompilerEnvironment.JDT ? this.getAt() : this.getAnnotation()).asMirror();
      }

      public IAnnotationHandle getSelectorAnnotation() {
         return this.getAt();
      }

      public String getAtArg(String key) {
         if (this.args == null) {
            this.args = new HashMap();

            for(String arg : this.at.getList("args")) {
               if (arg != null) {
                  int eqPos = arg.indexOf(61);
                  if (eqPos > -1) {
                     this.args.put(arg.substring(0, eqPos), arg.substring(eqPos + 1));
                  } else {
                     this.args.put(arg, "");
                  }
               }
            }
         }

         return (String)this.args.get(key);
      }

      public void notifyRemapped() {
         this.state.notifyRemapped();
      }
   }

   static class AnnotatedElementInjector extends AnnotatedMixinElementHandler.AnnotatedElementExecutable {
      private final InjectorRemap state;

      public AnnotatedElementInjector(ExecutableElement element, AnnotationHandle annotation, IMixinContext context, InjectorRemap shouldRemap) {
         super(element, annotation, context, "method");
         this.state = shouldRemap;
      }

      public boolean shouldRemap() {
         return this.state.shouldRemap();
      }

      public boolean hasCoerceArgument() {
         if (!this.annotation.toString().equals("@Inject")) {
            return false;
         } else {
            Iterator var1 = ((ExecutableElement)this.element).getParameters().iterator();
            if (var1.hasNext()) {
               VariableElement param = (VariableElement)var1.next();
               return AnnotationHandle.of(param, Coerce.class).exists();
            } else {
               return false;
            }
         }
      }

      public void addMessage(Kind kind, CharSequence msg, Element element, AnnotationHandle annotation) {
         this.state.addMessage(kind, msg, element, annotation);
      }

      public String toString() {
         return this.getAnnotation().toString();
      }
   }

   static class AnnotatedElementSliceInjectionPoint extends AnnotatedMixinElementHandlerInjector.AnnotatedElementInjectionPoint {
      private final ISelectorContext parentContext;

      public AnnotatedElementSliceInjectionPoint(ExecutableElement element, AnnotationHandle inject, IMixinContext context, String selectorCoordinate, AnnotationHandle at, InjectorRemap state, ISelectorContext parentContext) {
         super(element, inject, context, selectorCoordinate, at, state);
         this.parentContext = parentContext;
      }

      public ISelectorContext getParent() {
         return this.parentContext;
      }
   }
}
