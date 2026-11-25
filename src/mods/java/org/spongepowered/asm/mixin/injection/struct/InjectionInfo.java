package org.spongepowered.asm.mixin.injection.struct;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.tools.Diagnostic.Kind;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.code.InjectorTarget;
import org.spongepowered.asm.mixin.injection.code.MethodSlice;
import org.spongepowered.asm.mixin.injection.code.MethodSlices;
import org.spongepowered.asm.mixin.injection.selectors.ElementNode;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.InvalidSelectorException;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.throwables.SelectorConstraintException;
import org.spongepowered.asm.mixin.injection.selectors.throwables.SelectorException;
import org.spongepowered.asm.mixin.injection.throwables.InjectionError;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.struct.SpecialMethodInfo;
import org.spongepowered.asm.mixin.throwables.MixinError;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.asm.ASM;
import org.spongepowered.asm.util.asm.MethodNodeEx;
import org.spongepowered.asm.util.logging.MessageRouter;

public abstract class InjectionInfo extends SpecialMethodInfo implements ISliceContext {
   public static final String DEFAULT_PREFIX = "handler";
   private static Map<String, InjectionInfo.InjectorEntry> registry = new LinkedHashMap();
   private static Class<? extends Annotation>[] registeredAnnotations = new Class[0];
   protected final boolean isStatic;
   protected final Set<ITargetSelector> selectors;
   protected final List<InjectionInfo.SelectedTarget> targets;
   protected final MethodSlices slices;
   protected final String atKey;
   protected final List<InjectionPoint> injectionPoints;
   protected final Map<Target, List<InjectionNodes.InjectionNode>> targetNodes;
   protected int targetCount;
   protected Injector injector;
   protected InjectorGroupInfo group;
   private final List<MethodNode> injectedMethods;
   private int expectedCallbackCount;
   private int requiredCallbackCount;
   private int maxCallbackCount;
   private int injectedCallbackCount;
   private List<String> messages;

   protected InjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
      this(mixin, method, annotation, "at");
   }

   protected InjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation, String atKey) {
      super(mixin, method, annotation);
      this.selectors = new LinkedHashSet();
      this.targets = new ArrayList();
      this.injectionPoints = new ArrayList();
      this.targetNodes = new LinkedHashMap();
      this.targetCount = 0;
      this.injectedMethods = new ArrayList(0);
      this.expectedCallbackCount = 1;
      this.requiredCallbackCount = 0;
      this.maxCallbackCount = Integer.MAX_VALUE;
      this.injectedCallbackCount = 0;
      this.isStatic = Bytecode.isStatic(method);
      this.slices = MethodSlices.parse(this);
      this.atKey = atKey;
      this.readAnnotation();
   }

   protected void readAnnotation() {
      if (this.annotation != null) {
         List<AnnotationNode> injectionPoints = this.readInjectionPoints();
         this.parseRequirements();
         this.parseSelectors();
         this.findTargets();
         this.parseInjectionPoints(injectionPoints);
         this.injector = this.parseInjector(this.annotation);
      }
   }

   protected void parseSelectors() {
      Set<ITargetSelector> selectors = new LinkedHashSet();
      TargetSelector.parse(Annotations.getValue(this.annotation, "method", false), this, selectors);
      TargetSelector.parse(Annotations.getValue(this.annotation, "target", false), this, selectors);
      if (selectors.size() == 0) {
         throw new InvalidInjectionException(this, String.format("%s annotation on %s is missing 'method' or 'target' to specify targets", this.annotationType, this.methodName));
      } else {
         for(ITargetSelector selector : selectors) {
            try {
               this.selectors.add(selector.validate().attach(this));
            } catch (InvalidMemberDescriptorException var5) {
               throw new InvalidInjectionException(this, String.format("%s annotation on %s, has invalid target descriptor: %s. %s", this.annotationType, this.methodName, var5.getMessage(), this.mixin.getReferenceMapper().getStatus()));
            } catch (TargetNotSupportedException var6) {
               throw new InvalidInjectionException(this, String.format("%s annotation on %s specifies a target class '%s', which is not supported", this.annotationType, this.methodName, var6.getMessage()));
            } catch (InvalidSelectorException var7) {
               throw new InvalidInjectionException(this, String.format("%s annotation on %s is decorated with an invalid selector: %s", this.annotationType, this.methodName, var7.getMessage()));
            }
         }

      }
   }

   protected List<AnnotationNode> readInjectionPoints() {
      List<AnnotationNode> ats = Annotations.<AnnotationNode>getValue(this.annotation, this.atKey, false);
      if (ats == null) {
         throw new InvalidInjectionException(this, String.format("%s annotation on %s is missing '%s' value(s)", this.annotationType, this.methodName, this.atKey));
      } else {
         return ats;
      }
   }

   protected void parseInjectionPoints(List<AnnotationNode> ats) {
      this.injectionPoints.addAll(InjectionPoint.parse(this, ats));
   }

   protected void parseRequirements() {
      this.group = this.mixin.getInjectorGroups().parseGroup(this.method, this.mixin.getDefaultInjectorGroup()).add(this);
      Integer expect = (Integer)Annotations.getValue(this.annotation, "expect");
      if (expect != null) {
         this.expectedCallbackCount = expect.intValue();
      }

      Integer require = (Integer)Annotations.getValue(this.annotation, "require");
      if (require != null && require.intValue() > -1) {
         this.requiredCallbackCount = require.intValue();
      } else if (this.group.isDefault()) {
         this.requiredCallbackCount = this.mixin.getDefaultRequiredInjections();
      }

      Integer allow = (Integer)Annotations.getValue(this.annotation, "allow");
      if (allow != null) {
         this.maxCallbackCount = Math.max(Math.max(this.requiredCallbackCount, 1), allow.intValue());
      }

   }

   protected abstract Injector parseInjector(AnnotationNode var1);

   public boolean isValid() {
      return this.targets.size() > 0 && this.injectionPoints.size() > 0;
   }

   public void prepare() {
      this.targetNodes.clear();

      for(InjectionInfo.SelectedTarget targetMethod : this.targets) {
         Target target = this.mixin.getTargetMethod(targetMethod.method);
         InjectorTarget injectorTarget = new InjectorTarget(this, target, targetMethod.selector);

         try {
            this.targetNodes.put(target, this.injector.find(injectorTarget, this.injectionPoints));
         } catch (SelectorException var9) {
            throw new InvalidInjectionException(this, String.format("Injection validation failed: %s on %s: %s. %s%s", this.annotationType, this.methodName, var9.getMessage(), this.mixin.getReferenceMapper().getStatus(), this.getDynamicInfo()));
         } finally {
            injectorTarget.dispose();
         }
      }

   }

   public void preInject() {
      for(Entry<Target, List<InjectionNodes.InjectionNode>> entry : this.targetNodes.entrySet()) {
         this.injector.preInject((Target)entry.getKey(), (List)entry.getValue());
      }

   }

   public void inject() {
      for(Entry<Target, List<InjectionNodes.InjectionNode>> entry : this.targetNodes.entrySet()) {
         this.injector.inject((Target)entry.getKey(), (List)entry.getValue());
      }

      this.targets.clear();
   }

   public void postInject() {
      for(MethodNode method : this.injectedMethods) {
         this.classNode.methods.add(method);
      }

      String description = this.getDescription();
      String refMapStatus = this.mixin.getReferenceMapper().getStatus();
      String extraInfo = this.getDynamicInfo() + this.getMessages();
      if (this.mixin.getOption(MixinEnvironment.Option.DEBUG_INJECTORS) && this.injectedCallbackCount < this.expectedCallbackCount) {
         throw new InvalidInjectionException(this, String.format("Injection validation failed: %s %s%s in %s expected %d invocation(s) but %d succeeded. Scanned %d target(s). %s%s", description, this.methodName, this.method.desc, this.mixin, this.expectedCallbackCount, this.injectedCallbackCount, this.targetCount, refMapStatus, extraInfo));
      } else if (this.injectedCallbackCount < this.requiredCallbackCount) {
         throw new InjectionError(String.format("Critical injection failure: %s %s%s in %s failed injection check, (%d/%d) succeeded. Scanned %d target(s). %s%s", description, this.methodName, this.method.desc, this.mixin, this.injectedCallbackCount, this.requiredCallbackCount, this.targetCount, refMapStatus, extraInfo));
      } else if (this.injectedCallbackCount > this.maxCallbackCount) {
         throw new InjectionError(String.format("Critical injection failure: %s %s%s in %s failed injection check, %d succeeded of %d allowed.%s", description, this.methodName, this.method.desc, this.mixin, this.injectedCallbackCount, this.maxCallbackCount, extraInfo));
      }
   }

   public void notifyInjected(Target target) {
   }

   protected String getDescription() {
      return "Callback method";
   }

   public String toString() {
      return describeInjector(this.mixin, this.annotation, this.method);
   }

   public int getTargetCount() {
      return this.targets.size();
   }

   public MethodSlice getSlice(String id) {
      return this.slices.get(this.getSliceId(id));
   }

   public String getSliceId(String id) {
      return "";
   }

   public int getInjectedCallbackCount() {
      return this.injectedCallbackCount;
   }

   public MethodNode addMethod(int access, String name, String desc) {
      MethodNode method = new MethodNode(ASM.API_VERSION, access | 4096, name, desc, (String)null, (String[])null);
      this.injectedMethods.add(method);
      return method;
   }

   public void addCallbackInvocation(MethodNode handler) {
      ++this.injectedCallbackCount;
   }

   public void addMessage(String format, Object... args) {
      super.addMessage(format, args);
      if (this.messages == null) {
         this.messages = new ArrayList();
      }

      String message = String.format(format, args);
      this.messages.add(message);
   }

   protected String getMessages() {
      return this.messages != null ? " Messages: { " + Joiner.on(" ").join(this.messages) + "}" : "";
   }

   protected void findTargets() {
      this.targets.clear();
      this.findRootTargets();
      this.validateTargets();
   }

   private void findRootTargets() {
      int passes = this.mixin.getOption(MixinEnvironment.Option.REFMAP_REMAP) ? 2 : 1;

      for(ITargetSelector selector : this.selectors) {
         selector = selector.configure(ITargetSelector.Configure.SELECT_MEMBER);
         int matchCount = 0;
         int maxCount = selector.getMaxMatchCount();
         ITargetSelector permissiveSelector = selector.configure(ITargetSelector.Configure.PERMISSIVE);
         int selectorPasses = permissiveSelector == selector ? 1 : passes;

         label75:
         for(int pass = 0; pass < selectorPasses && matchCount < 1; ++pass) {
            ITargetSelector passSelector = pass == 0 ? selector : permissiveSelector;

            for(MethodNode target : this.classNode.methods) {
               if (passSelector.match(ElementNode.of(this.classNode, target)).isExactMatch()) {
                  ++matchCount;
                  boolean isMixinMethod = Annotations.getVisible(target, MixinMerged.class) != null;
                  if (maxCount <= 1 || (this.isStatic || !Bytecode.isStatic(target)) && target != this.method && !isMixinMethod) {
                     this.checkTarget(target);
                     this.targets.add(new InjectionInfo.SelectedTarget(passSelector, target));
                  }

                  if (matchCount >= maxCount) {
                     break label75;
                  }
               }
            }
         }

         if (matchCount < selector.getMinMatchCount()) {
            throw new InvalidInjectionException(this, new SelectorConstraintException(selector, String.format("Injection validation failed: %s for %s on %s did not match the required number of targets (required=%d, matched=%d). %s%s", selector, this.annotationType, this.methodName, selector.getMinMatchCount(), matchCount, this.mixin.getReferenceMapper().getStatus(), this.getDynamicInfo())));
         }
      }

   }

   protected void validateTargets() {
      this.targetCount = this.targets.size();
      if (this.targetCount <= 0) {
         if (this.mixin.getOption(MixinEnvironment.Option.DEBUG_INJECTORS) && this.expectedCallbackCount > 0) {
            throw new InvalidInjectionException(this, String.format("Injection validation failed: %s annotation on %s could not find any targets matching %s in %s. %s%s", this.annotationType, this.methodName, namesOf(this.selectors), this.mixin.getTarget(), this.mixin.getReferenceMapper().getStatus(), this.getDynamicInfo()));
         } else if (this.requiredCallbackCount > 0) {
            throw new InvalidInjectionException(this, String.format("Critical injection failure: %s annotation on %s could not find any targets matching %s in %s. %s%s", this.annotationType, this.methodName, namesOf(this.selectors), this.mixin.getTarget(), this.mixin.getReferenceMapper().getStatus(), this.getDynamicInfo()));
         }
      }
   }

   protected void checkTarget(MethodNode target) {
      AnnotationNode merged = Annotations.getVisible(target, MixinMerged.class);
      if (merged != null) {
         if (Annotations.getVisible(target, Final.class) != null) {
            throw new InvalidInjectionException(this, String.format("%s cannot inject into @Final method %s::%s%s merged by %s", this, this.classNode.name, target.name, target.desc, Annotations.getValue(merged, "mixin")));
         }
      }
   }

   protected String getDynamicInfo() {
      AnnotationNode annotation = Annotations.getInvisible(this.method, Dynamic.class);
      String description = Strings.nullToEmpty((String)Annotations.getValue(annotation));
      Type upstream = (Type)Annotations.getValue(annotation, "mixin");
      if (upstream != null) {
         description = String.format("{%s} %s", upstream.getClassName(), description).trim();
      }

      return description.length() > 0 ? String.format(" Method is @Dynamic(%s).", description) : "";
   }

   public static InjectionInfo parse(MixinTargetContext mixin, MethodNode method) {
      AnnotationNode annotation = getInjectorAnnotation(mixin.getMixin(), method);
      if (annotation == null) {
         return null;
      } else {
         for(InjectionInfo.InjectorEntry injector : registry.values()) {
            if (annotation.desc.equals(injector.annotationDesc)) {
               return injector.create(mixin, method, annotation);
            }
         }

         return null;
      }
   }

   public static AnnotationNode getInjectorAnnotation(IMixinInfo mixin, MethodNode method) {
      AnnotationNode annotation = null;

      try {
         annotation = Annotations.getSingleVisible(method, registeredAnnotations);
         return annotation;
      } catch (IllegalArgumentException var4) {
         throw new InvalidMixinException(mixin, String.format("Error parsing annotations on %s in %s: %s", method.name, mixin.getClassName(), var4.getMessage()));
      }
   }

   public static String getInjectorPrefix(AnnotationNode annotation) {
      if (annotation == null) {
         return "handler";
      } else {
         for(InjectionInfo.InjectorEntry injector : registry.values()) {
            if (annotation.desc.endsWith(injector.annotationDesc)) {
               return injector.prefix;
            }
         }

         return "handler";
      }
   }

   static String describeInjector(IMixinContext mixin, AnnotationNode annotation, MethodNode method) {
      return String.format("%s->@%s::%s%s", mixin.toString(), Annotations.getSimpleName(annotation), MethodNodeEx.getName(method), method.desc);
   }

   private static String namesOf(Collection<ITargetSelector> selectors) {
      int index = 0;
      int count = selectors.size();
      StringBuilder sb = new StringBuilder();

      for(ITargetSelector selector : selectors) {
         if (index > 0) {
            if (index == count - 1) {
               sb.append(" or ");
            } else {
               sb.append(", ");
            }
         }

         sb.append('\'').append(selector.toString()).append('\'');
         ++index;
      }

      return sb.toString();
   }

   public static void register(Class<? extends InjectionInfo> type) {
      InjectionInfo.AnnotationType annotationType = (InjectionInfo.AnnotationType)type.getAnnotation(InjectionInfo.AnnotationType.class);
      if (annotationType == null) {
         throw new IllegalArgumentException("Injection info class " + type + " is not annotated with @AnnotationType");
      } else {
         InjectionInfo.InjectorEntry entry;
         try {
            entry = new InjectionInfo.InjectorEntry(annotationType.value(), type);
         } catch (NoSuchMethodException var7) {
            throw new MixinError("InjectionInfo class " + type.getName() + " is missing a valid constructor");
         }

         InjectionInfo.InjectorEntry existing = (InjectionInfo.InjectorEntry)registry.get(entry.annotationDesc);
         if (existing != null) {
            MessageRouter.getMessager().printMessage(Kind.WARNING, String.format("Overriding InjectionInfo for @%s with %s (previously %s)", annotationType.value().getSimpleName(), type.getName(), existing.injectorType.getName()));
         } else {
            MessageRouter.getMessager().printMessage(Kind.OTHER, String.format("Registering new injector for @%s with %s", annotationType.value().getSimpleName(), type.getName()));
         }

         registry.put(entry.annotationDesc, entry);
         ArrayList<Class<? extends Annotation>> annotations = new ArrayList();

         for(InjectionInfo.InjectorEntry injector : registry.values()) {
            annotations.add(injector.annotationType);
         }

         registeredAnnotations = (Class[])annotations.toArray(registeredAnnotations);
      }
   }

   public static Set<Class<? extends Annotation>> getRegisteredAnnotations() {
      return ImmutableSet.copyOf(registeredAnnotations);
   }

   static {
      register(CallbackInjectionInfo.class);
      register(ModifyArgInjectionInfo.class);
      register(ModifyArgsInjectionInfo.class);
      register(RedirectInjectionInfo.class);
      register(ModifyVariableInjectionInfo.class);
      register(ModifyConstantInjectionInfo.class);
   }

   @Retention(RetentionPolicy.RUNTIME)
   @java.lang.annotation.Target({ElementType.TYPE})
   public @interface AnnotationType {
      Class<? extends Annotation> value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @java.lang.annotation.Target({ElementType.TYPE})
   public @interface HandlerPrefix {
      String value();
   }

   static class InjectorEntry {
      final Class<? extends Annotation> annotationType;
      final Class<? extends InjectionInfo> injectorType;
      final Constructor<? extends InjectionInfo> ctor;
      final String annotationDesc;
      final String prefix;

      InjectorEntry(Class<? extends Annotation> annotationType, Class<? extends InjectionInfo> type) throws NoSuchMethodException {
         this.annotationType = annotationType;
         this.injectorType = type;
         this.ctor = type.getDeclaredConstructor(MixinTargetContext.class, MethodNode.class, AnnotationNode.class);
         this.annotationDesc = Type.getDescriptor(annotationType);
         InjectionInfo.HandlerPrefix handlerPrefix = (InjectionInfo.HandlerPrefix)type.getAnnotation(InjectionInfo.HandlerPrefix.class);
         this.prefix = handlerPrefix != null ? handlerPrefix.value() : "handler";
      }

      InjectionInfo create(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
         try {
            return (InjectionInfo)this.ctor.newInstance(mixin, method, annotation);
         } catch (InvocationTargetException var7) {
            Throwable cause = var7.getCause();
            if (cause instanceof MixinException) {
               throw (MixinException)cause;
            } else {
               Throwable ex = (Throwable)(cause != null ? cause : var7);
               throw new MixinError("Error initialising injector metaclass [" + this.injectorType + "] for annotation " + annotation.desc, ex);
            }
         } catch (ReflectiveOperationException var8) {
            throw new MixinError("Failed to instantiate injector metaclass [" + this.injectorType + "] for annotation " + annotation.desc, var8);
         }
      }
   }

   static class SelectedTarget {
      private final ITargetSelector root;
      final ITargetSelector selector;
      final MethodNode method;

      SelectedTarget(ITargetSelector root, ITargetSelector selector, MethodNode method) {
         this.root = root;
         this.selector = selector;
         this.method = method;
      }

      SelectedTarget(ITargetSelector selector, MethodNode method) {
         this((ITargetSelector)null, selector, method);
      }

      ITargetSelector getRoot() {
         return this.root != null ? this.root : this.selector;
      }
   }
}
