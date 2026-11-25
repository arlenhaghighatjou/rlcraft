package org.spongepowered.tools.obfuscation;

import com.google.common.base.Strings;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.struct.SelectorAnnotationContext;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.refmap.ReferenceMapper;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.util.asm.IAnnotatedElement;
import org.spongepowered.asm.util.asm.IAnnotationHandle;
import org.spongepowered.tools.obfuscation.interfaces.IMessagerSuppressible;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IMixinValidator;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationManager;
import org.spongepowered.tools.obfuscation.interfaces.ITypeHandleProvider;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;
import org.spongepowered.tools.obfuscation.struct.InjectorRemap;

class AnnotatedMixin implements IMixinContext, IAnnotatedElement {
   private final AnnotationHandle annotation;
   private final IMessagerSuppressible messager;
   private final ITypeHandleProvider typeProvider;
   private final IObfuscationManager obf;
   private final IMappingConsumer mappings;
   private final TypeElement mixin;
   private final List<ExecutableElement> methods;
   private final TypeHandle handle;
   private final List<TypeHandle> targets = new ArrayList();
   private final TypeHandle primaryTarget;
   private final String classRef;
   private final boolean remap;
   private final boolean virtual;
   private final AnnotatedMixinElementHandlerOverwrite overwrites;
   private final AnnotatedMixinElementHandlerShadow shadows;
   private final AnnotatedMixinElementHandlerInjector injectors;
   private final AnnotatedMixinElementHandlerAccessor accessors;
   private final AnnotatedMixinElementHandlerSoftImplements softImplements;
   private boolean validated = false;

   public AnnotatedMixin(IMixinAnnotationProcessor ap, TypeElement type) {
      this.typeProvider = ap.getTypeProvider();
      this.obf = ap.getObfuscationManager();
      this.mappings = this.obf.createMappingConsumer();
      this.messager = ap;
      this.mixin = type;
      this.handle = new TypeHandle(type);
      this.methods = new ArrayList(this.handle.getEnclosedElements(ElementKind.METHOD));
      this.virtual = this.handle.getAnnotation(Pseudo.class).exists();
      this.annotation = this.handle.getAnnotation(Mixin.class);
      this.classRef = TypeUtils.getInternalName(type);
      this.primaryTarget = this.initTargets();
      this.remap = this.annotation.getBoolean("remap", true) && this.targets.size() > 0;
      this.overwrites = new AnnotatedMixinElementHandlerOverwrite(ap, this);
      this.shadows = new AnnotatedMixinElementHandlerShadow(ap, this);
      this.injectors = new AnnotatedMixinElementHandlerInjector(ap, this);
      this.accessors = new AnnotatedMixinElementHandlerAccessor(ap, this);
      this.softImplements = new AnnotatedMixinElementHandlerSoftImplements(ap, this);
   }

   AnnotatedMixin runValidators(IMixinValidator.ValidationPass pass, Collection<IMixinValidator> validators) {
      for(IMixinValidator validator : validators) {
         if (!validator.validate(pass, this.mixin, this.annotation, this.targets)) {
            break;
         }
      }

      if (pass == IMixinValidator.ValidationPass.FINAL && !this.validated) {
         this.validated = true;
         this.runFinalValidation();
      }

      return this;
   }

   private TypeHandle initTargets() {
      TypeHandle primaryTarget = null;

      try {
         for(TypeMirror target : this.annotation.getList()) {
            TypeHandle type = new TypeHandle((DeclaredType)target);
            if (!this.targets.contains(type)) {
               this.addTarget(type);
               if (primaryTarget == null) {
                  primaryTarget = type;
               }
            }
         }
      } catch (Exception var7) {
         this.printMessage(Kind.WARNING, "Error processing public targets: " + var7.getClass().getName() + ": " + var7.getMessage(), this);
      }

      try {
         for(String softTarget : this.annotation.getList("targets")) {
            TypeHandle type = this.typeProvider.getTypeHandle(softTarget);
            if (!this.targets.contains(type)) {
               if (this.virtual) {
                  type = this.typeProvider.getSimulatedHandle(softTarget, this.mixin.asType());
               } else {
                  if (type == null) {
                     this.printMessage(Kind.ERROR, "Mixin target " + softTarget + " could not be found", this);
                     return null;
                  }

                  if (type.isImaginary()) {
                     this.printMessage(Kind.WARNING, "Mixin target " + softTarget + " could not be fully resolved.", this, SuppressedBy.UNRESOLVABLE_TARGET);
                     return null;
                  }

                  if (type.isPublic()) {
                     SuppressedBy suppressedBy = type.getPackage().isUnnamed() ? SuppressedBy.DEFAULT_PACKAGE : SuppressedBy.PUBLIC_TARGET;
                     this.printMessage(Kind.WARNING, "Mixin target " + softTarget + " is public and must be specified in value", this, suppressedBy);
                     return null;
                  }
               }

               this.addSoftTarget(type, softTarget);
               if (primaryTarget == null) {
                  primaryTarget = type;
               }
            }
         }
      } catch (Exception var6) {
         this.printMessage(Kind.WARNING, "Error processing private targets: " + var6.getClass().getName() + ": " + var6.getMessage(), this);
      }

      if (primaryTarget == null) {
         this.printMessage(Kind.ERROR, "Mixin has no targets", this);
      }

      return primaryTarget;
   }

   private void printMessage(Kind kind, CharSequence msg, AnnotatedMixin mixin) {
      this.messager.printMessage(kind, msg, this.mixin, this.annotation.asMirror());
   }

   private void printMessage(Kind kind, CharSequence msg, AnnotatedMixin mixin, SuppressedBy suppressedBy) {
      this.messager.printMessage(kind, msg, this.mixin, this.annotation.asMirror(), suppressedBy);
   }

   private void addSoftTarget(TypeHandle type, String reference) {
      ObfuscationData<String> obfClassData = this.obf.getDataProvider().getObfClass(type);
      if (!obfClassData.isEmpty()) {
         this.obf.getReferenceManager().addClassMapping(this.classRef, reference, obfClassData);
      }

      this.addTarget(type);
   }

   private void addTarget(TypeHandle type) {
      this.targets.add(type);
   }

   public String toString() {
      return this.mixin.getSimpleName().toString();
   }

   public AnnotationHandle getAnnotation() {
      return this.annotation;
   }

   public TypeElement getMixinElement() {
      return this.mixin;
   }

   public TypeHandle getHandle() {
      return this.handle;
   }

   public String getClassRef() {
      return this.classRef;
   }

   public boolean isInterface() {
      return this.mixin.getKind() == ElementKind.INTERFACE;
   }

   /** @deprecated */
   @Deprecated
   public TypeHandle getPrimaryTarget() {
      return this.primaryTarget;
   }

   public List<TypeHandle> getTargets() {
      return this.targets;
   }

   public boolean isMultiTarget() {
      return this.targets.size() > 1;
   }

   public boolean remap() {
      return this.remap;
   }

   public IMappingConsumer getMappings() {
      return this.mappings;
   }

   private void runFinalValidation() {
      for(ExecutableElement method : this.methods) {
         this.overwrites.registerMerge(method);
      }

   }

   public void registerOverwrite(ExecutableElement method, AnnotationHandle overwrite, boolean shouldRemap) {
      this.methods.remove(method);
      this.overwrites.registerOverwrite(new AnnotatedMixinElementHandlerOverwrite.AnnotatedElementOverwrite(method, overwrite, shouldRemap));
   }

   public void registerShadow(VariableElement field, AnnotationHandle shadow, boolean shouldRemap) {
      AnnotatedMixinElementHandlerShadow var10000 = this.shadows;
      AnnotatedMixinElementHandlerShadow var10003 = this.shadows;
      this.shadows.getClass();
      var10000.registerShadow(var10003.new AnnotatedElementShadowField(field, shadow, shouldRemap));
   }

   public void registerShadow(ExecutableElement method, AnnotationHandle shadow, boolean shouldRemap) {
      this.methods.remove(method);
      AnnotatedMixinElementHandlerShadow var10000 = this.shadows;
      AnnotatedMixinElementHandlerShadow var10003 = this.shadows;
      this.shadows.getClass();
      var10000.registerShadow(var10003.new AnnotatedElementShadowMethod(method, shadow, shouldRemap));
   }

   public void registerInjector(ExecutableElement method, AnnotationHandle inject, InjectorRemap remap) {
      this.methods.remove(method);
      AnnotatedMixinElementHandlerInjector.AnnotatedElementInjector injectorElement = new AnnotatedMixinElementHandlerInjector.AnnotatedElementInjector(method, inject, this, remap);
      this.injectors.registerInjector(injectorElement);

      for(IAnnotationHandle at : inject.getAnnotationList("at")) {
         this.registerInjectionPoint(method, inject, "at", (AnnotationHandle)at, remap, "@At(%s)");
      }

      for(IAnnotationHandle slice : inject.getAnnotationList("slice")) {
         String id = (String)slice.getValue("id", "");
         String coord = "slice";
         if (!Strings.isNullOrEmpty(id)) {
            coord = coord + "." + id;
         }

         SelectorAnnotationContext sliceContext = new SelectorAnnotationContext(injectorElement, slice, coord);
         IAnnotationHandle from = slice.getAnnotation("from");
         if (from != null) {
            this.registerSliceInjectionPoint(method, inject, "from", (AnnotationHandle)from, remap, "@Slice[" + id + "](from=@At(%s))", sliceContext);
         }

         IAnnotationHandle to = slice.getAnnotation("to");
         if (to != null) {
            this.registerSliceInjectionPoint(method, inject, "to", (AnnotationHandle)to, remap, "@Slice[" + id + "](to=@At(%s))", sliceContext);
         }
      }

   }

   public void registerInjectionPoint(ExecutableElement element, AnnotationHandle inject, String selectorCoordinate, AnnotationHandle at, InjectorRemap remap, String format) {
      this.injectors.registerInjectionPoint(new AnnotatedMixinElementHandlerInjector.AnnotatedElementInjectionPoint(element, inject, this, selectorCoordinate, at, remap), format);
   }

   public void registerSliceInjectionPoint(ExecutableElement element, AnnotationHandle inject, String selectorCoordinate, AnnotationHandle at, InjectorRemap remap, String format, ISelectorContext parentContext) {
      this.injectors.registerInjectionPoint(new AnnotatedMixinElementHandlerInjector.AnnotatedElementSliceInjectionPoint(element, inject, this, selectorCoordinate, at, remap, parentContext), format);
   }

   public void registerAccessor(ExecutableElement element, AnnotationHandle accessor, boolean shouldRemap) {
      this.methods.remove(element);
      this.accessors.registerAccessor(new AnnotatedMixinElementHandlerAccessor.AnnotatedElementAccessor(element, accessor, this, shouldRemap));
   }

   public void registerInvoker(ExecutableElement element, AnnotationHandle invoker, boolean shouldRemap) {
      this.methods.remove(element);
      this.accessors.registerAccessor(new AnnotatedMixinElementHandlerAccessor.AnnotatedElementInvoker(element, invoker, this, shouldRemap));
   }

   public void registerSoftImplements(AnnotationHandle implementsAnnotation) {
      this.softImplements.process(implementsAnnotation);
   }

   public ReferenceMapper getReferenceMapper() {
      return null;
   }

   public String getClassName() {
      return this.getClassRef().replace('/', '.');
   }

   public String getTargetClassRef() {
      return this.primaryTarget.getName();
   }

   public IMixinInfo getMixin() {
      throw new UnsupportedOperationException("MixinInfo not available at compile time");
   }

   public Extensions getExtensions() {
      throw new UnsupportedOperationException("Mixin Extensions not available at compile time");
   }

   public boolean getOption(MixinEnvironment.Option option) {
      throw new UnsupportedOperationException("Options not available at compile time");
   }

   public int getPriority() {
      throw new UnsupportedOperationException("Priority not available at compile time");
   }

   public IAnnotationHandle getAnnotation(Class<? extends Annotation> annotationClass) {
      return AnnotationHandle.of(this.mixin, annotationClass);
   }
}
