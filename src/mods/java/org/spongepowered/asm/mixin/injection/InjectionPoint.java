package org.spongepowered.asm.mixin.injection;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.modify.AfterStoreLocal;
import org.spongepowered.asm.mixin.injection.modify.BeforeLoadLocal;
import org.spongepowered.asm.mixin.injection.points.AfterInvoke;
import org.spongepowered.asm.mixin.injection.points.BeforeConstant;
import org.spongepowered.asm.mixin.injection.points.BeforeFieldAccess;
import org.spongepowered.asm.mixin.injection.points.BeforeFinalReturn;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.points.BeforeNew;
import org.spongepowered.asm.mixin.injection.points.BeforeReturn;
import org.spongepowered.asm.mixin.injection.points.BeforeStringInvoke;
import org.spongepowered.asm.mixin.injection.points.JumpInsnPoint;
import org.spongepowered.asm.mixin.injection.points.MethodHead;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointAnnotationContext;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.struct.AnnotatedMethodInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.IMessageSink;

public abstract class InjectionPoint {
   public static final int DEFAULT_ALLOWED_SHIFT_BY = 0;
   public static final int MAX_ALLOWED_SHIFT_BY = 5;
   private static Map<String, Class<? extends InjectionPoint>> types = new HashMap();
   private final String slice;
   private final InjectionPoint.Selector selector;
   private final String id;
   private final IMessageSink messageSink;

   protected InjectionPoint() {
      this("", InjectionPoint.Selector.DEFAULT, (String)null);
   }

   protected InjectionPoint(InjectionPointData data) {
      this(data.getSlice(), data.getSelector(), data.getId(), data.getMessageSink());
   }

   public InjectionPoint(String slice, InjectionPoint.Selector selector, String id) {
      this(slice, selector, id, (IMessageSink)null);
   }

   public InjectionPoint(String slice, InjectionPoint.Selector selector, String id, IMessageSink messageSink) {
      this.slice = slice;
      this.selector = selector;
      this.id = id;
      this.messageSink = messageSink;
   }

   public String getSlice() {
      return this.slice;
   }

   public InjectionPoint.Selector getSelector() {
      return this.selector;
   }

   public String getId() {
      return this.id;
   }

   protected void addMessage(String format, Object... args) {
      if (this.messageSink != null) {
         this.messageSink.addMessage(format, args);
      }

   }

   public boolean checkPriority(int targetPriority, int mixinPriority) {
      return targetPriority < mixinPriority;
   }

   public InjectionPoint.RestrictTargetLevel getTargetRestriction(IInjectionPointContext context) {
      return InjectionPoint.RestrictTargetLevel.METHODS_ONLY;
   }

   public abstract boolean find(String var1, InsnList var2, Collection<AbstractInsnNode> var3);

   public String toString() {
      return String.format("@At(\"%s\")", this.getAtCode());
   }

   protected static AbstractInsnNode nextNode(InsnList insns, AbstractInsnNode insn) {
      int index = insns.indexOf(insn) + 1;
      return index > 0 && index < insns.size() ? insns.get(index) : insn;
   }

   public static InjectionPoint and(InjectionPoint... operands) {
      return new InjectionPoint.Intersection(operands);
   }

   public static InjectionPoint or(InjectionPoint... operands) {
      return new InjectionPoint.Union(operands);
   }

   public static InjectionPoint after(InjectionPoint point) {
      return new InjectionPoint.Shift(point, 1);
   }

   public static InjectionPoint before(InjectionPoint point) {
      return new InjectionPoint.Shift(point, -1);
   }

   public static InjectionPoint shift(InjectionPoint point, int count) {
      return new InjectionPoint.Shift(point, count);
   }

   public static List<InjectionPoint> parse(IMixinContext context, MethodNode method, AnnotationNode parent, List<AnnotationNode> ats) {
      return parse(new AnnotatedMethodInfo(context, method, parent), ats);
   }

   public static List<InjectionPoint> parse(IInjectionPointContext context, List<AnnotationNode> ats) {
      Builder<InjectionPoint> injectionPoints = ImmutableList.builder();

      for(AnnotationNode at : ats) {
         InjectionPoint injectionPoint = parse(new InjectionPointAnnotationContext(context, at, "at"), at);
         if (injectionPoint != null) {
            injectionPoints.add(injectionPoint);
         }
      }

      return injectionPoints.build();
   }

   public static InjectionPoint parse(IInjectionPointContext context, At at) {
      return parse(context, at.value(), at.shift(), at.by(), Arrays.asList(at.args()), at.target(), at.slice(), at.ordinal(), at.opcode(), at.id());
   }

   public static InjectionPoint parse(IMixinContext context, MethodNode method, AnnotationNode parent, At at) {
      return parse(new AnnotatedMethodInfo(context, method, parent), at.value(), at.shift(), at.by(), Arrays.asList(at.args()), at.target(), at.slice(), at.ordinal(), at.opcode(), at.id());
   }

   public static InjectionPoint parse(IMixinContext context, MethodNode method, AnnotationNode parent, AnnotationNode at) {
      return parse(new InjectionPointAnnotationContext(new AnnotatedMethodInfo(context, method, parent), at, "at"), at);
   }

   public static InjectionPoint parse(IInjectionPointContext context, AnnotationNode at) {
      String value = (String)Annotations.getValue(at, "value");
      List<String> args = (List)Annotations.getValue(at, "args");
      String target = (String)Annotations.getValue(at, "target", "");
      String slice = (String)Annotations.getValue(at, "slice", "");
      At.Shift shift = (At.Shift)Annotations.getValue(at, "shift", At.Shift.class, At.Shift.NONE);
      int by = ((Integer)Annotations.getValue(at, "by", Integer.valueOf(0))).intValue();
      int ordinal = ((Integer)Annotations.getValue(at, "ordinal", Integer.valueOf(-1))).intValue();
      int opcode = ((Integer)Annotations.getValue(at, "opcode", Integer.valueOf(0))).intValue();
      String id = (String)Annotations.getValue(at, "id");
      if (args == null) {
         args = ImmutableList.of();
      }

      return parse(context, value, shift, by, args, target, slice, ordinal, opcode, id);
   }

   public static InjectionPoint parse(IMixinContext context, MethodNode method, AnnotationNode parent, String at, At.Shift shift, int by, List<String> args, String target, String slice, int ordinal, int opcode, String id) {
      return parse(new AnnotatedMethodInfo(context, method, parent), at, shift, by, args, target, slice, ordinal, opcode, id);
   }

   public static InjectionPoint parse(IInjectionPointContext context, String at, At.Shift shift, int by, List<String> args, String target, String slice, int ordinal, int opcode, String id) {
      InjectionPointData data = new InjectionPointData(context, at, args, target, slice, ordinal, opcode, id);
      Class<? extends InjectionPoint> ipClass = findClass(context.getMixin(), data);
      InjectionPoint point = create(context.getMixin(), data, ipClass);
      return shift(context, point, shift, by);
   }

   private static Class<? extends InjectionPoint> findClass(IMixinContext context, InjectionPointData data) {
      String type = data.getType();
      Class<? extends InjectionPoint> ipClass = (Class)types.get(type.toUpperCase(Locale.ROOT));
      if (ipClass == null) {
         if (!type.matches("^([A-Za-z_][A-Za-z0-9_]*[\\.\\$])+[A-Za-z_][A-Za-z0-9_]*$")) {
            throw new InvalidInjectionException(context, data + " is not a valid injection point specifier");
         }

         try {
            ipClass = MixinService.getService().getClassProvider().findClass(type);
            types.put(type, ipClass);
         } catch (Exception var5) {
            throw new InvalidInjectionException(context, data + " could not be loaded or is not a valid InjectionPoint", var5);
         }
      }

      return ipClass;
   }

   private static InjectionPoint create(IMixinContext context, InjectionPointData data, Class<? extends InjectionPoint> ipClass) {
      Constructor<? extends InjectionPoint> ipCtor = null;

      try {
         ipCtor = ipClass.getDeclaredConstructor(InjectionPointData.class);
         ipCtor.setAccessible(true);
      } catch (NoSuchMethodException var8) {
         throw new InvalidInjectionException(context, ipClass.getName() + " must contain a constructor which accepts an InjectionPointData", var8);
      }

      InjectionPoint point = null;

      try {
         point = (InjectionPoint)ipCtor.newInstance(data);
         return point;
      } catch (InvocationTargetException var6) {
         throw new InvalidInjectionException(context, "Error whilst instancing injection point " + ipClass.getName() + " for " + data.getAt(), var6.getCause());
      } catch (Exception var7) {
         throw new InvalidInjectionException(context, "Error whilst instancing injection point " + ipClass.getName() + " for " + data.getAt(), var7);
      }
   }

   private static InjectionPoint shift(IInjectionPointContext context, InjectionPoint point, At.Shift shift, int by) {
      if (point != null) {
         if (shift == At.Shift.BEFORE) {
            return before(point);
         }

         if (shift == At.Shift.AFTER) {
            return after(point);
         }

         if (shift == At.Shift.BY) {
            validateByValue(context.getMixin(), context.getMethod(), context.getAnnotationNode(), point, by);
            return shift(point, by);
         }
      }

      return point;
   }

   private static void validateByValue(IMixinContext context, MethodNode method, AnnotationNode parent, InjectionPoint point, int by) {
      MixinEnvironment env = context.getMixin().getConfig().getEnvironment();
      InjectionPoint.ShiftByViolationBehaviour err = (InjectionPoint.ShiftByViolationBehaviour)env.getOption(MixinEnvironment.Option.SHIFT_BY_VIOLATION_BEHAVIOUR, InjectionPoint.ShiftByViolationBehaviour.WARN);
      if (err != InjectionPoint.ShiftByViolationBehaviour.IGNORE) {
         String limitBreached = "the maximum allowed value: ";
         String advice = "Increase the value of maxShiftBy to suppress this warning.";
         int allowed = 0;
         if (context instanceof MixinTargetContext) {
            allowed = ((MixinTargetContext)context).getMaxShiftByValue();
         }

         if (by > allowed) {
            if (by > 5) {
               limitBreached = "MAX_ALLOWED_SHIFT_BY=";
               advice = "You must use an alternate query or a custom injection point.";
               allowed = 5;
            }

            String message = String.format("@%s(%s) Shift.BY=%d on %s::%s exceeds %s%d. %s", Annotations.getSimpleName(parent), point, by, context, method.name, limitBreached, allowed, advice);
            if (err == InjectionPoint.ShiftByViolationBehaviour.WARN && allowed < 5) {
               MixinService.getService().getLogger("mixin").warn(message);
            } else {
               throw new InvalidInjectionException(context, message);
            }
         }
      }
   }

   protected String getAtCode() {
      InjectionPoint.AtCode code = (InjectionPoint.AtCode)this.getClass().getAnnotation(InjectionPoint.AtCode.class);
      return code == null ? this.getClass().getName() : code.value().toUpperCase();
   }

   /** @deprecated */
   @Deprecated
   public static void register(Class<? extends InjectionPoint> type) {
      register(type, (String)null);
   }

   public static void register(Class<? extends InjectionPoint> type, String namespace) {
      InjectionPoint.AtCode code = (InjectionPoint.AtCode)type.getAnnotation(InjectionPoint.AtCode.class);
      if (code == null) {
         throw new IllegalArgumentException("Injection point class " + type + " is not annotated with @AtCode");
      } else {
         String annotationNamespace = code.namespace();
         if (!Strings.isNullOrEmpty(annotationNamespace)) {
            namespace = annotationNamespace;
         }

         Class<? extends InjectionPoint> existing = (Class)types.get(code.value());
         if (existing != null && !existing.equals(type)) {
            MixinService.getService().getLogger("mixin").debug("Overriding InjectionPoint {} with {} (previously {})", code.value(), type.getName(), existing.getName());
         } else if (Strings.isNullOrEmpty(namespace)) {
            MixinService.getService().getLogger("mixin").warn("Registration of InjectionPoint {} with {} without specifying namespace is deprecated.", code.value(), type.getName());
         }

         String id = code.value().toUpperCase(Locale.ROOT);
         if (!Strings.isNullOrEmpty(namespace)) {
            id = namespace.toUpperCase(Locale.ROOT) + ":" + id;
         }

         types.put(id, type);
      }
   }

   private static void registerBuiltIn(Class<? extends InjectionPoint> type) {
      String code = ((InjectionPoint.AtCode)type.getAnnotation(InjectionPoint.AtCode.class)).value().toUpperCase(Locale.ROOT);
      types.put(code, type);
      types.put("MIXIN:" + code, type);
   }

   static {
      registerBuiltIn(BeforeFieldAccess.class);
      registerBuiltIn(BeforeInvoke.class);
      registerBuiltIn(BeforeNew.class);
      registerBuiltIn(BeforeReturn.class);
      registerBuiltIn(BeforeStringInvoke.class);
      registerBuiltIn(JumpInsnPoint.class);
      registerBuiltIn(MethodHead.class);
      registerBuiltIn(AfterInvoke.class);
      registerBuiltIn(BeforeLoadLocal.class);
      registerBuiltIn(AfterStoreLocal.class);
      registerBuiltIn(BeforeFinalReturn.class);
      registerBuiltIn(BeforeConstant.class);
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE})
   public @interface AtCode {
      String namespace() default "";

      String value();
   }

   abstract static class CompositeInjectionPoint extends InjectionPoint {
      protected final InjectionPoint[] components;

      protected CompositeInjectionPoint(InjectionPoint... components) {
         if (components != null && components.length >= 2) {
            this.components = components;
         } else {
            throw new IllegalArgumentException("Must supply two or more component injection points for composite point!");
         }
      }

      public String toString() {
         return "CompositeInjectionPoint(" + this.getClass().getSimpleName() + ")[" + Joiner.on(',').join(this.components) + "]";
      }
   }

   static final class Intersection extends InjectionPoint.CompositeInjectionPoint {
      public Intersection(InjectionPoint... points) {
         super(points);
      }

      public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
         boolean found = false;
         ArrayList<AbstractInsnNode>[] allNodes = (ArrayList[])((ArrayList[])Array.newInstance(ArrayList.class, this.components.length));

         for(int i = 0; i < this.components.length; ++i) {
            allNodes[i] = new ArrayList();
            this.components[i].find(desc, insns, allNodes[i]);
         }

         ArrayList<AbstractInsnNode> alpha = allNodes[0];

         for(int nodeIndex = 0; nodeIndex < alpha.size(); ++nodeIndex) {
            AbstractInsnNode node = (AbstractInsnNode)alpha.get(nodeIndex);
            boolean in = true;

            for(int b = 1; b < allNodes.length && allNodes[b].contains(node); ++b) {
               ;
            }

            if (in) {
               nodes.add(node);
               found = true;
            }
         }

         return found;
      }
   }

   public static enum RestrictTargetLevel {
      METHODS_ONLY,
      CONSTRUCTORS_AFTER_DELEGATE,
      ALLOW_ALL;

      private RestrictTargetLevel() {
      }
   }

   public static enum Selector {
      FIRST,
      LAST,
      ONE;

      public static final InjectionPoint.Selector DEFAULT = FIRST;

      private Selector() {
      }
   }

   static final class Shift extends InjectionPoint {
      private final InjectionPoint input;
      private final int shift;

      public Shift(InjectionPoint input, int shift) {
         if (input == null) {
            throw new IllegalArgumentException("Must supply an input injection point for SHIFT");
         } else {
            this.input = input;
            this.shift = shift;
         }
      }

      public String toString() {
         return "InjectionPoint(" + this.getClass().getSimpleName() + ")[" + this.input + "]";
      }

      public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
         List<AbstractInsnNode> list = (List<AbstractInsnNode>)(nodes instanceof List ? (List)nodes : new ArrayList(nodes));
         this.input.find(desc, insns, nodes);
         ListIterator<AbstractInsnNode> iter = list.listIterator();

         while(iter.hasNext()) {
            int sourceIndex = insns.indexOf((AbstractInsnNode)iter.next());
            int newIndex = sourceIndex + this.shift;
            if (newIndex >= 0 && newIndex < insns.size()) {
               iter.set(insns.get(newIndex));
            } else {
               iter.remove();
               int absShift = Math.abs(this.shift);
               char operator = (char)(absShift != this.shift ? 45 : 43);
               this.input.addMessage("@At.shift offset outside the target bounds: Index (index(%d) %s offset(%d) = %d) is outside the allowed range (0-%d)", sourceIndex, operator, absShift, newIndex, insns.size());
            }
         }

         if (nodes != list) {
            nodes.clear();
            nodes.addAll(list);
         }

         return nodes.size() > 0;
      }
   }

   static enum ShiftByViolationBehaviour {
      IGNORE,
      WARN,
      ERROR;

      private ShiftByViolationBehaviour() {
      }
   }

   static final class Union extends InjectionPoint.CompositeInjectionPoint {
      public Union(InjectionPoint... points) {
         super(points);
      }

      public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
         LinkedHashSet<AbstractInsnNode> allNodes = new LinkedHashSet();

         for(int i = 0; i < this.components.length; ++i) {
            this.components[i].find(desc, insns, allNodes);
         }

         nodes.addAll(allNodes);
         return allNodes.size() > 0;
      }
   }
}
