package org.spongepowered.tools.obfuscation;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorByName;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.util.ConstraintParser;
import org.spongepowered.asm.util.asm.IAnnotatedElement;
import org.spongepowered.asm.util.asm.IAnnotationHandle;
import org.spongepowered.asm.util.throwables.ConstraintViolationException;
import org.spongepowered.asm.util.throwables.InvalidConstraintException;
import org.spongepowered.tools.obfuscation.interfaces.IMessagerSuppressible;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationManager;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.FieldHandle;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;
import org.spongepowered.tools.obfuscation.mirror.Visibility;

abstract class AnnotatedMixinElementHandler {
   protected final AnnotatedMixin mixin;
   protected final String classRef;
   protected final IMixinAnnotationProcessor ap;
   protected final IObfuscationManager obf;
   private IMappingConsumer mappings;

   AnnotatedMixinElementHandler(IMixinAnnotationProcessor ap, AnnotatedMixin mixin) {
      this.ap = ap;
      this.mixin = mixin;
      this.classRef = mixin.getClassRef();
      this.obf = ap.getObfuscationManager();
   }

   private IMappingConsumer getMappings() {
      if (this.mappings == null) {
         IMappingConsumer mappingConsumer = this.mixin.getMappings();
         if (mappingConsumer instanceof Mappings) {
            this.mappings = ((Mappings)mappingConsumer).asUnique();
         } else {
            this.mappings = mappingConsumer;
         }
      }

      return this.mappings;
   }

   protected final void addFieldMappings(String mcpName, String mcpSignature, ObfuscationData<MappingField> obfData) {
      for(ObfuscationType type : obfData) {
         MappingField obfField = obfData.get(type);
         this.addFieldMapping(type, mcpName, obfField.getSimpleName(), mcpSignature, obfField.getDesc());
      }

   }

   protected final void addFieldMapping(ObfuscationType type, AnnotatedMixinElementHandler.ShadowElementName name, String mcpSignature, String obfSignature) {
      this.addFieldMapping(type, name.name(), name.obfuscated(), mcpSignature, obfSignature);
   }

   protected final void addFieldMapping(ObfuscationType type, String mcpName, String obfName, String mcpSignature, String obfSignature) {
      MappingField from = new MappingField(this.classRef, mcpName, mcpSignature);
      MappingField to = new MappingField(this.classRef, obfName, obfSignature);
      this.getMappings().addFieldMapping(type, from, to);
   }

   protected final void addMethodMappings(String mcpName, String mcpSignature, ObfuscationData<MappingMethod> obfData) {
      for(ObfuscationType type : obfData) {
         MappingMethod obfMethod = obfData.get(type);
         this.addMethodMapping(type, mcpName, obfMethod.getSimpleName(), mcpSignature, obfMethod.getDesc());
      }

   }

   protected final void addMethodMapping(ObfuscationType type, AnnotatedMixinElementHandler.ShadowElementName name, String mcpSignature, String obfSignature) {
      this.addMethodMapping(type, name.name(), name.obfuscated(), mcpSignature, obfSignature);
   }

   protected final void addMethodMapping(ObfuscationType type, String mcpName, String obfName, String mcpSignature, String obfSignature) {
      MappingMethod from = new MappingMethod(this.classRef, mcpName, mcpSignature);
      MappingMethod to = new MappingMethod(this.classRef, obfName, obfSignature);
      this.getMappings().addMethodMapping(type, from, to);
   }

   protected final void checkConstraints(ExecutableElement method, AnnotationHandle annotation) {
      try {
         ConstraintParser.Constraint constraint = ConstraintParser.parse((String)annotation.getValue("constraints"));

         try {
            constraint.check(this.ap.getTokenProvider());
         } catch (ConstraintViolationException var5) {
            this.ap.printMessage(Kind.ERROR, var5.getMessage(), method, annotation.asMirror());
         }
      } catch (InvalidConstraintException var6) {
         this.ap.printMessage(Kind.WARNING, var6.getMessage(), method, annotation.asMirror(), SuppressedBy.CONSTRAINTS);
      }

   }

   protected final void validateTarget(Element element, AnnotationHandle annotation, AnnotatedMixinElementHandler.AliasedElementName name, String type) {
      if (element instanceof ExecutableElement) {
         this.validateTargetMethod((ExecutableElement)element, annotation, name, type, false, false);
      } else if (element instanceof VariableElement) {
         this.validateTargetField((VariableElement)element, annotation, name, type);
      }

   }

   protected final void validateTargetMethod(ExecutableElement method, AnnotationHandle annotation, AnnotatedMixinElementHandler.AliasedElementName name, String type, boolean overwrite, boolean merge) {
      String signature = TypeUtils.getJavaSignature(method);

      for(TypeHandle target : this.mixin.getTargets()) {
         if (!target.isImaginary()) {
            MethodHandle targetMethod = target.findMethod(method);
            if (targetMethod == null && name.hasPrefix()) {
               targetMethod = target.findMethod(name.baseName(), signature);
            }

            if (targetMethod == null && name.hasAliases()) {
               for(String alias : name.getAliases()) {
                  if ((targetMethod = target.findMethod(alias, signature)) != null) {
                     break;
                  }
               }
            }

            if (targetMethod != null) {
               if (overwrite) {
                  this.validateMethodVisibility(method, annotation, type, target, targetMethod);
               }
            } else if (!merge) {
               this.printMessage(Kind.WARNING, "Cannot find target for " + type + " method in " + target, method, annotation, SuppressedBy.TARGET);
            }
         }
      }

   }

   private void validateMethodVisibility(ExecutableElement method, AnnotationHandle annotation, String type, TypeHandle target, MethodHandle targetMethod) {
      Visibility visTarget = targetMethod.getVisibility();
      if (visTarget != null) {
         Visibility visMethod = TypeUtils.getVisibility(method);
         String visibility = "visibility of " + visTarget + " method in " + target;
         if (visTarget.ordinal() > visMethod.ordinal()) {
            this.printMessage(Kind.WARNING, visMethod + " " + type + " method cannot reduce " + visibility, method, annotation, SuppressedBy.VISIBILITY);
         } else if (visTarget == Visibility.PRIVATE && visMethod.ordinal() > visTarget.ordinal()) {
            this.printMessage(Kind.WARNING, visMethod + " " + type + " method will upgrade " + visibility, method, annotation, SuppressedBy.VISIBILITY);
         }

      }
   }

   protected final void validateTargetField(VariableElement field, AnnotationHandle annotation, AnnotatedMixinElementHandler.AliasedElementName name, String type) {
      String fieldType = field.asType().toString();

      for(TypeHandle target : this.mixin.getTargets()) {
         if (!target.isImaginary()) {
            FieldHandle targetField = target.findField(field);
            if (targetField == null) {
               for(String alias : name.getAliases()) {
                  if ((targetField = target.findField(alias, fieldType)) != null) {
                     break;
                  }
               }

               if (targetField == null) {
                  this.ap.printMessage(Kind.WARNING, "Cannot find target for " + type + " field in " + target, field, annotation.asMirror(), SuppressedBy.TARGET);
               }
            }
         }
      }

   }

   protected final void validateReferencedTarget(AnnotatedMixinElementHandler.AnnotatedElementExecutable elem, String reference, ITargetSelector targetSelector, String subject) {
      if (targetSelector instanceof ITargetSelectorByName) {
         ITargetSelectorByName nameRef = (ITargetSelectorByName)targetSelector;
         String signature = nameRef.toDescriptor();

         for(TypeHandle target : this.mixin.getTargets()) {
            if (!target.isImaginary()) {
               MethodHandle targetMethod = target.findMethod(nameRef.getName(), signature);
               if (targetMethod == null) {
                  this.ap.printMessage(Kind.WARNING, "Cannot find target method \"" + nameRef.getName() + nameRef.getDesc() + "\" for " + subject + " in " + target, elem.getElement(), elem.getAnnotation().asMirror(), SuppressedBy.TARGET);
               }
            }
         }

      }
   }

   private void printMessage(Kind kind, String msg, Element e, AnnotationHandle annotation, SuppressedBy suppressedBy) {
      if (annotation == null) {
         this.ap.printMessage(kind, msg, e, suppressedBy);
      } else {
         this.ap.printMessage(kind, msg, e, annotation.asMirror(), suppressedBy);
      }

   }

   protected static <T extends IMapping<T>> ObfuscationData<T> stripOwnerData(ObfuscationData<T> data) {
      ObfuscationData<T> stripped = new ObfuscationData<T>();

      for(ObfuscationType type : data) {
         T mapping = data.get(type);
         stripped.put(type, mapping.move((String)null));
      }

      return stripped;
   }

   protected static <T extends IMapping<T>> ObfuscationData<T> stripDescriptors(ObfuscationData<T> data) {
      ObfuscationData<T> stripped = new ObfuscationData<T>();

      for(ObfuscationType type : data) {
         T mapping = data.get(type);
         stripped.put(type, mapping.transform((String)null));
      }

      return stripped;
   }

   static class AliasedElementName {
      protected final String originalName;
      private final List<String> aliases;
      private boolean caseSensitive;

      public AliasedElementName(Element element, AnnotationHandle annotation) {
         this.originalName = element.getSimpleName().toString();
         this.aliases = annotation.<String>getList("aliases");
      }

      public AnnotatedMixinElementHandler.AliasedElementName setCaseSensitive(boolean caseSensitive) {
         this.caseSensitive = caseSensitive;
         return this;
      }

      public boolean isCaseSensitive() {
         return this.caseSensitive;
      }

      public boolean hasAliases() {
         return this.aliases.size() > 0;
      }

      public List<String> getAliases() {
         return this.aliases;
      }

      public String elementName() {
         return this.originalName;
      }

      public String baseName() {
         return this.originalName;
      }

      public boolean hasPrefix() {
         return false;
      }
   }

   abstract static class AnnotatedElement<E extends Element> implements IAnnotatedElement {
      protected final E element;
      protected final AnnotationHandle annotation;
      private final String desc;

      public AnnotatedElement(E element, AnnotationHandle annotation) {
         this.element = element;
         this.annotation = annotation;
         this.desc = TypeUtils.getDescriptor(element);
      }

      public E getElement() {
         return this.element;
      }

      public AnnotationHandle getAnnotation() {
         return this.annotation;
      }

      public String getSimpleName() {
         return this.getElement().getSimpleName().toString();
      }

      public String getDesc() {
         return this.desc;
      }

      public final void printMessage(Messager messager, Kind kind, CharSequence msg) {
         messager.printMessage(kind, msg, this.element, this.annotation.asMirror());
      }

      public final void printMessage(IMessagerSuppressible messager, Kind kind, CharSequence msg, SuppressedBy suppressedBy) {
         messager.printMessage(kind, msg, this.element, this.annotation.asMirror(), suppressedBy);
      }

      public IAnnotationHandle getAnnotation(Class<? extends Annotation> annotationClass) {
         return AnnotationHandle.of(this.element, annotationClass);
      }
   }

   abstract static class AnnotatedElementExecutable extends AnnotatedMixinElementHandler.AnnotatedElement<ExecutableElement> implements ISelectorContext {
      private final IMixinContext context;
      private final String selectorCoordinate;

      public AnnotatedElementExecutable(ExecutableElement element, AnnotationHandle annotation, IMixinContext context, String selectorCoordinate) {
         super(element, annotation);
         this.context = context;
         this.selectorCoordinate = selectorCoordinate;
      }

      public ISelectorContext getParent() {
         return null;
      }

      public IMixinContext getMixin() {
         return this.context;
      }

      public Object getMethod() {
         return new IAnnotatedElement() {
            public IAnnotationHandle getAnnotation(Class<? extends Annotation> annotationClass) {
               return AnnotationHandle.of(AnnotatedElementExecutable.this.getElement(), annotationClass);
            }

            public String toString() {
               return ((ExecutableElement)AnnotatedElementExecutable.this.getElement()).getSimpleName().toString();
            }
         };
      }

      public IAnnotationHandle getSelectorAnnotation() {
         return this.getAnnotation();
      }

      public String getSelectorCoordinate(boolean leaf) {
         return leaf ? this.selectorCoordinate : TypeUtils.getName((ExecutableElement)this.element);
      }

      public String remap(String reference) {
         return reference;
      }

      public String toString() {
         return TypeUtils.getName((ExecutableElement)this.element);
      }
   }

   static class ShadowElementName extends AnnotatedMixinElementHandler.AliasedElementName {
      private final boolean hasPrefix;
      private final String prefix;
      private final String baseName;
      private String obfuscated;

      ShadowElementName(Element element, AnnotationHandle shadow) {
         super(element, shadow);
         this.prefix = (String)shadow.getValue("prefix", "shadow$");
         boolean hasPrefix = false;
         String name = this.originalName;
         if (name.startsWith(this.prefix)) {
            hasPrefix = true;
            name = name.substring(this.prefix.length());
         }

         this.hasPrefix = hasPrefix;
         this.obfuscated = this.baseName = name;
      }

      public String toString() {
         return this.baseName;
      }

      public String baseName() {
         return this.baseName;
      }

      public AnnotatedMixinElementHandler.ShadowElementName setObfuscatedName(IMapping<?> name) {
         this.obfuscated = name.getName();
         return this;
      }

      public AnnotatedMixinElementHandler.ShadowElementName setObfuscatedName(String name) {
         this.obfuscated = name;
         return this;
      }

      public boolean hasPrefix() {
         return this.hasPrefix;
      }

      public String prefix() {
         return this.hasPrefix ? this.prefix : "";
      }

      public String name() {
         return this.prefix(this.baseName);
      }

      public String obfuscated() {
         return this.prefix(this.obfuscated);
      }

      public String prefix(String name) {
         return this.hasPrefix ? this.prefix + name : name;
      }
   }
}
