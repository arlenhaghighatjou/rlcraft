package stanhebben.zenscript.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.IterableList;
import stanhebben.zenscript.annotations.IterableMap;
import stanhebben.zenscript.annotations.IterableSimple;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenConstructor;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMemberGetter;
import stanhebben.zenscript.annotations.ZenMemberSetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;
import stanhebben.zenscript.annotations.ZenProperty;
import stanhebben.zenscript.annotations.ZenSetter;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.dump.types.DumpZenType;
import stanhebben.zenscript.dump.types.DumpZenTypeNative;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArithmeticUnary;
import stanhebben.zenscript.expression.ExpressionCallVirtual;
import stanhebben.zenscript.expression.ExpressionCompareGeneric;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingNotNull;
import stanhebben.zenscript.type.casting.CastingRuleNone;
import stanhebben.zenscript.type.casting.CastingRuleNullableStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.iterator.IteratorIterable;
import stanhebben.zenscript.type.iterator.IteratorList;
import stanhebben.zenscript.type.iterator.IteratorMap;
import stanhebben.zenscript.type.iterator.IteratorMapKeys;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.type.natives.ZenFieldMethod;
import stanhebben.zenscript.type.natives.ZenNativeCaster;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.type.natives.ZenNativeOperator;
import stanhebben.zenscript.util.AnyClassWriter;
import stanhebben.zenscript.util.IAnyDefinition;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import stanhebben.zenscript.value.IAny;

public class ZenTypeNative extends ZenType {
   private static final int ITERATOR_NONE = 0;
   private static final int ITERATOR_ITERABLE = 1;
   private static final int ITERATOR_LIST = 2;
   private static final int ITERATOR_MAP = 3;
   private final Class<?> cls;
   private final String anyName;
   private final String anyName2;
   private final List<ZenTypeNative> implementing;
   private final Map<String, ZenNativeMember> members;
   private final Map<String, ZenNativeMember> staticMembers;
   private final List<ZenTypeNative.ZenNativeConstructor> constructors;
   private final List<ZenNativeCaster> casters;
   private final List<ZenNativeOperator> trinaryOperators;
   private final List<ZenNativeOperator> binaryOperators;
   private final List<ZenNativeOperator> unaryOperators;
   private int iteratorType;
   private String classPkg;
   private String className;
   private Annotation iteratorAnnotation;
   private ZenType iteratorKeyType;
   private ZenType iteratorValueType;

   public ZenTypeNative(Class<?> cls) {
      this.cls = cls;
      this.members = new HashMap();
      this.staticMembers = new HashMap();
      this.casters = new ArrayList();
      this.trinaryOperators = new ArrayList();
      this.binaryOperators = new ArrayList();
      this.unaryOperators = new ArrayList();
      this.implementing = new ArrayList();
      this.constructors = new ArrayList();
      this.anyName2 = cls.getName() + "Any";
      this.anyName = this.anyName2.replace('.', '/');
   }

   public void complete(ITypeRegistry types) {
      int iterator = 0;
      Annotation _iteratorAnnotation = null;
      String _classPkg = ((String)Optional.ofNullable(this.cls.getPackage()).map(Package::getName).orElse("null")).replace('/', '.');
      String _className = this.cls.getSimpleName();
      Queue<ZenTypeNative> todo = new LinkedList();
      todo.add(this);
      this.addSubtypes(todo, types);
      Annotation[] clsAnnotations = this.cls.getAnnotations();

      for(Annotation annotation : clsAnnotations) {
         if (annotation instanceof ZenClass) {
            String value = ((ZenClass)annotation).value();
            int dot = value.lastIndexOf(46);
            if (dot < 0) {
               _classPkg = null;
               _className = value;
            } else {
               _classPkg = value.substring(0, dot);
               _className = value.substring(dot + 1);
            }
         }

         if (annotation instanceof IterableSimple) {
            iterator = 1;
            _iteratorAnnotation = annotation;
            if (!Iterable.class.isAssignableFrom(this.cls)) {
               ;
            }
         }

         if (annotation instanceof IterableList) {
            iterator = 2;
            _iteratorAnnotation = annotation;
            if (!List.class.isAssignableFrom(this.cls)) {
               ;
            }
         }

         if (annotation instanceof IterableMap) {
            iterator = 3;
            _iteratorAnnotation = annotation;
            if (!Map.class.isAssignableFrom(this.cls)) {
               ;
            }
         }
      }

      for(Method method : this.cls.getMethods()) {
         boolean isMethod = false;
         String methodName = method.getName();

         for(Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof ZenCaster) {
               this.casters.add(new ZenNativeCaster(JavaMethod.get(types, method)));
            } else if (annotation instanceof ZenGetter) {
               ZenGetter getterAnnotation = (ZenGetter)annotation;
               this.checkGetter(method, this.cls);
               String name = getterAnnotation.value().length() == 0 ? method.getName() : getterAnnotation.value();
               if (!this.members.containsKey(name)) {
                  this.members.put(name, new ZenNativeMember());
               }

               JavaMethod javaMethod = new JavaMethod(method, types);
               ((ZenNativeMember)this.members.get(name)).setGetter(javaMethod);
            } else if (annotation instanceof ZenSetter) {
               ZenSetter setterAnnotation = (ZenSetter)annotation;
               this.checkSetter(method, this.cls);
               String name = setterAnnotation.value().length() == 0 ? method.getName() : setterAnnotation.value();
               if (!this.members.containsKey(name)) {
                  this.members.put(name, new ZenNativeMember());
               }

               ((ZenNativeMember)this.members.get(name)).setSetter(new JavaMethod(method, types));
            } else if (annotation instanceof ZenMemberGetter) {
               this.binaryOperators.add(new ZenNativeOperator(OperatorType.MEMBERGETTER, new JavaMethod(method, types)));
            } else if (annotation instanceof ZenMemberSetter) {
               this.trinaryOperators.add(new ZenNativeOperator(OperatorType.MEMBERSETTER, new JavaMethod(method, types)));
            } else if (annotation instanceof ZenOperator) {
               ZenOperator operatorAnnotation = (ZenOperator)annotation;
               switch(operatorAnnotation.value()) {
               case NEG:
               case NOT:
                  if (method.getParameterTypes().length == 0) {
                     this.unaryOperators.add(new ZenNativeOperator(operatorAnnotation.value(), new JavaMethod(method, types)));
                  }
                  break;
               case ADD:
               case SUB:
               case CAT:
               case MUL:
               case DIV:
               case MOD:
               case AND:
               case OR:
               case XOR:
               case INDEXGET:
               case RANGE:
               case CONTAINS:
               case COMPARE:
               case EQUALS:
                  if (method.getParameterTypes().length == 1) {
                     this.binaryOperators.add(new ZenNativeOperator(operatorAnnotation.value(), new JavaMethod(method, types)));
                  }
                  break;
               case INDEXSET:
                  if (method.getParameterTypes().length == 2) {
                     this.trinaryOperators.add(new ZenNativeOperator(operatorAnnotation.value(), new JavaMethod(method, types)));
                  }
               }
            } else if (annotation instanceof ZenMethod) {
               isMethod = true;
               ZenMethod methodAnnotation = (ZenMethod)annotation;
               if (methodAnnotation.value().length() > 0) {
                  methodName = methodAnnotation.value();
               }
            }
         }

         if (isMethod) {
            if ((method.getModifiers() & 8) > 0) {
               if (!this.staticMembers.containsKey(methodName)) {
                  this.staticMembers.put(methodName, new ZenNativeMember());
               }

               ((ZenNativeMember)this.staticMembers.get(methodName)).addMethod(new JavaMethod(method, types));
            } else {
               if (!this.members.containsKey(methodName)) {
                  this.members.put(methodName, new ZenNativeMember());
               }

               ((ZenNativeMember)this.members.get(methodName)).addMethod(new JavaMethod(method, types));
            }
         }
      }

      for(Field field : this.cls.getFields()) {
         for(Annotation annotation : field.getAnnotations()) {
            if (annotation instanceof ZenProperty) {
               ZenProperty zenProperty = (ZenProperty)annotation;
               String propertyName = zenProperty.value();
               if (propertyName.isEmpty()) {
                  propertyName = field.getName();
               }

               String methodEnding = propertyName.substring(0, 1).toUpperCase(Locale.US) + propertyName.substring(1);
               String getterName = zenProperty.getter();
               if (getterName.isEmpty()) {
                  if (!field.getType().equals(Boolean.class) && !field.getType().equals(Boolean.TYPE)) {
                     getterName = "get" + methodEnding;
                  } else {
                     getterName = "is" + methodEnding;
                  }
               }

               String setterName = zenProperty.setter();
               if (setterName.isEmpty()) {
                  setterName = "set" + methodEnding;
               }

               Map<String, ZenNativeMember> memberMap = Modifier.isStatic(field.getModifiers()) ? this.staticMembers : this.members;
               memberMap.putIfAbsent(propertyName, new ZenNativeMember());
               ZenNativeMember zenNativeMember = (ZenNativeMember)memberMap.get(propertyName);

               try {
                  Method getterMethod = this.cls.getMethod(getterName);
                  this.checkGetter(getterMethod, this.cls);
                  zenNativeMember.setGetter(new JavaMethod(getterMethod, types));
               } catch (NoSuchMethodException var26) {
                  ZenFieldMethod getterMethod = new ZenFieldMethod(field, types, false);
                  zenNativeMember.setGetter(getterMethod);
                  memberMap.putIfAbsent(getterName, new ZenNativeMember());
                  ((ZenNativeMember)memberMap.get(getterName)).addMethod(getterMethod);
               }

               if (!Modifier.isFinal(field.getModifiers())) {
                  try {
                     Method setterMethod = this.cls.getMethod(setterName, field.getType());
                     this.checkSetter(setterMethod, this.cls);
                     zenNativeMember.setSetter(new JavaMethod(setterMethod, types));
                  } catch (NoSuchMethodException var25) {
                     ZenFieldMethod setterMethod = new ZenFieldMethod(field, types, true);
                     zenNativeMember.setSetter(setterMethod);
                     memberMap.putIfAbsent(setterName, new ZenNativeMember());
                     ((ZenNativeMember)memberMap.get(setterName)).addMethod(setterMethod);
                  }
               }
            }
         }
      }

      for(Constructor constructor : this.cls.getConstructors()) {
         if (constructor.isAnnotationPresent(ZenConstructor.class)) {
            this.constructors.add(new ZenTypeNative.ZenNativeConstructor(constructor));
         }
      }

      this.iteratorType = iterator;
      this.iteratorAnnotation = _iteratorAnnotation;
      this.classPkg = _classPkg;
      this.className = _className;
   }

   private void checkGetter(Method method, Class cls) {
      if (method.getReturnType().equals(Void.TYPE)) {
         throw new RuntimeException("ZenGetter needs a non Void returntype - " + cls.getName() + "." + method.getName());
      } else if (method.getParameterCount() > 0) {
         throw new RuntimeException("ZenGetter may not have any parameters - " + cls.getName() + "." + method.getName());
      }
   }

   private void checkSetter(Method method, Class cls) {
      if (method.getParameterCount() != 1) {
         throw new RuntimeException("ZenSetter must have exactly one parameter - " + cls.getName() + "." + method.getName());
      } else if (!method.getReturnType().equals(Void.TYPE)) {
         throw new RuntimeException("ZenSetter must have a void return type");
      }
   }

   public Class getNativeClass() {
      return this.cls;
   }

   public void completeIterators(IEnvironmentGlobal environment) {
      if (this.iteratorAnnotation instanceof IterableSimple) {
         IterableSimple annotation = (IterableSimple)this.iteratorAnnotation;
         this.iteratorValueType = ZenType.parse(annotation.value(), environment);
      }

      if (this.iteratorAnnotation instanceof IterableList) {
         IterableList annotation = (IterableList)this.iteratorAnnotation;
         this.iteratorKeyType = ZenTypeInt.INSTANCE;
         this.iteratorValueType = ZenType.parse(annotation.value(), environment);
      }

      if (this.iteratorAnnotation instanceof IterableMap) {
         IterableMap annotation = (IterableMap)this.iteratorAnnotation;
         this.iteratorKeyType = ZenType.parse(annotation.key(), environment);
         this.iteratorValueType = ZenType.parse(annotation.value(), environment);
      }

   }

   public String getAnyClassName(IEnvironmentGlobal global) {
      if (!global.containsClass(this.anyName2)) {
         global.putClass(this.anyName2, new byte[0]);
      }

      return this.anyName;
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      ZenNativeMember member = (ZenNativeMember)this.members.get(name);
      if (member == null) {
         for(ZenTypeNative type : this.implementing) {
            if (type.hasMember(name)) {
               return type.getMember(position, environment, value, name);
            }
         }
      }

      if (member != null) {
         return member.instance(position, environment, value);
      } else {
         Expression evalue = value.eval(environment);
         IPartialExpression member2 = this.memberExpansion(position, environment, evalue, name);
         if (member2 == null) {
            for(ZenTypeNative type : this.implementing) {
               member2 = type.memberExpansion(position, environment, evalue, name);
               if (member2 != null) {
                  break;
               }
            }
         }

         if (member2 == null) {
            if (this.hasBinary(STRING, OperatorType.MEMBERGETTER)) {
               return this.binary(position, environment, value.eval(environment), new ExpressionString(position, name), OperatorType.MEMBERGETTER);
            } else {
               environment.error(position, "No such member in " + this.getName() + ": " + name);
               return new ExpressionInvalid(position);
            }
         } else {
            return member2;
         }
      }
   }

   private boolean hasMember(String name) {
      if (this.members.containsKey(name)) {
         return true;
      } else {
         for(ZenTypeNative zenTypeNative : this.implementing) {
            if (zenTypeNative.hasStaticMember(name)) {
               return true;
            }
         }

         return false;
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      ZenNativeMember member = (ZenNativeMember)this.staticMembers.get(name);
      if (member == null) {
         for(ZenTypeNative type : this.implementing) {
            if (type.hasStaticMember(name)) {
               return type.getStaticMember(position, environment, name);
            }
         }
      }

      if (member != null) {
         return member.instance(position, environment);
      } else {
         IPartialExpression member2 = this.staticMemberExpansion(position, environment, name);
         if (member2 == null) {
            for(ZenTypeNative type : this.implementing) {
               member2 = type.staticMemberExpansion(position, environment, name);
               if (member2 != null) {
                  break;
               }
            }
         }

         if (member2 == null) {
            environment.error(position, "No such static member in " + this.getName() + ": " + name);
            return new ExpressionInvalid(position);
         } else {
            return member2;
         }
      }
   }

   private boolean hasStaticMember(String name) {
      if (this.staticMembers.containsKey(name)) {
         return true;
      } else {
         for(ZenTypeNative zenTypeNative : this.implementing) {
            if (zenTypeNative.hasStaticMember(name)) {
               return true;
            }
         }

         return false;
      }
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      this.completeIterators(methodOutput);
      switch(this.iteratorType) {
      case 0:
      default:
         break;
      case 1:
         if (numValues == 1) {
            return new IteratorIterable(methodOutput.getOutput(), this.iteratorValueType);
         }

         if (numValues == 2) {
            return new IteratorList(methodOutput.getOutput(), this.iteratorValueType);
         }
         break;
      case 2:
         if (numValues == 1) {
            return new IteratorIterable(methodOutput.getOutput(), this.iteratorValueType);
         }

         if (numValues == 2) {
            return new IteratorList(methodOutput.getOutput(), this.iteratorValueType);
         }
         break;
      case 3:
         if (numValues == 1) {
            return new IteratorMapKeys(methodOutput.getOutput(), new ZenTypeAssociative(this.iteratorValueType, this.iteratorKeyType));
         }

         if (numValues == 2) {
            return new IteratorMap(methodOutput.getOutput(), new ZenTypeAssociative(this.iteratorValueType, this.iteratorKeyType));
         }
      }

      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      if (this.cls.getSuperclass() != null) {
         ZenType superType = environment.getType(this.cls.getSuperclass());
         rules.registerCastingRule(superType, new CastingRuleNone(this, superType));
         superType.constructCastingRules(environment, rules, followCasters);
      }

      for(Class iface : this.cls.getInterfaces()) {
         ZenType ifaceType = environment.getType(iface);
         rules.registerCastingRule(ifaceType, new CastingRuleNone(this, ifaceType));
         ifaceType.constructCastingRules(environment, rules, followCasters);
      }

      if (followCasters) {
         for(ZenNativeCaster caster : this.casters) {
            caster.constructCastingRule(rules);
         }

         TypeExpansion expansion = environment.getExpansion(this.getName());
         if (expansion != null) {
            expansion.constructCastingRules(environment, rules);
         }
      }

      rules.registerCastingRule(BOOL, new CastingNotNull(this));
      rules.registerCastingRule(ANY, new CastingRuleNullableStaticMethod(JavaMethod.getStatic(this.getAnyClassName(environment), "valueOf", ANY, this)));
   }

   public boolean canCastExplicit(ZenType type, IEnvironmentGlobal environment) {
      return type == this || this.canCastImplicit(type, environment) || type.canCastExplicit(this, environment);
   }

   public Class toJavaClass() {
      return this.cls;
   }

   public Type toASMType() {
      return Type.getType(this.cls);
   }

   public int getNumberType() {
      return 0;
   }

   public String getSignature() {
      return ZenTypeUtil.signature(this.cls);
   }

   public boolean isPointer() {
      return true;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      for(ZenNativeOperator unaryOperator : this.unaryOperators) {
         if (unaryOperator.getOperator() == operator) {
            return new ExpressionCallVirtual(position, environment, unaryOperator.getMethod(), value, new Expression[0]);
         }
      }

      for(ZenTypeNative parent : this.implementing) {
         if (parent.hasTernary(this, operator)) {
            return parent.unary(position, environment, value, operator);
         }
      }

      environment.error(position, "operator not supported");
      return new ExpressionInvalid(position);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      for(ZenNativeOperator binaryOperator : this.binaryOperators) {
         if (binaryOperator.getOperator() == operator) {
            return new ExpressionCallVirtual(position, environment, binaryOperator.getMethod(), left, new Expression[]{right});
         }
      }

      for(ZenTypeNative parent : this.implementing) {
         if (parent.hasBinary(right.getType(), operator)) {
            return parent.binary(position, environment, left, right, operator);
         }
      }

      environment.error(position, "operator not supported");
      return new ExpressionInvalid(position);
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      for(ZenNativeOperator trinaryOperator : this.trinaryOperators) {
         if (trinaryOperator.getOperator() == operator) {
            return new ExpressionCallVirtual(position, environment, trinaryOperator.getMethod(), first, new Expression[]{second, third});
         }
      }

      for(ZenTypeNative parent : this.implementing) {
         if (parent.hasTernary(third.getType(), operator)) {
            return parent.trinary(position, environment, first, second, third, operator);
         }
      }

      environment.error(position, "operator not supported");
      return new ExpressionInvalid(position);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      if (type == CompareType.EQ || type == CompareType.NE) {
         for(ZenNativeOperator binaryOperator : this.binaryOperators) {
            if (binaryOperator.getOperator() == OperatorType.EQUALS) {
               Expression result = new ExpressionCallVirtual(position, environment, binaryOperator.getMethod(), left, new Expression[]{right});
               if (type == CompareType.EQ) {
                  return result;
               }

               return new ExpressionArithmeticUnary(position, OperatorType.NOT, result);
            }
         }
      }

      return new ExpressionCompareGeneric(position, this.binary(position, environment, left, right, OperatorType.COMPARE), type);
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      for(ZenTypeNative.ZenNativeConstructor constructor : this.constructors) {
         if (constructor.canAccept(environment, arguments)) {
            return constructor.call(position, arguments);
         }
      }

      environment.error(position, "Could not find matching constructor with " + arguments.length + " arguments!");
      return new ExpressionInvalid(position);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return new ZenType[numArguments];
   }

   private boolean hasBinary(ZenType type, OperatorType operator) {
      for(ZenNativeOperator binaryOperator : this.binaryOperators) {
         if (binaryOperator.getOperator() == operator) {
            return true;
         }
      }

      for(ZenTypeNative parent : this.implementing) {
         if (parent.hasBinary(type, operator)) {
            return true;
         }
      }

      return false;
   }

   private boolean hasTernary(ZenType type, OperatorType operator) {
      for(ZenNativeOperator ternaryOperator : this.trinaryOperators) {
         if (ternaryOperator.getOperator() == operator) {
            return true;
         }
      }

      for(ZenTypeNative parent : this.implementing) {
         if (parent.hasTernary(type, operator)) {
            return true;
         }
      }

      return false;
   }

   private boolean hasUnary(ZenType type, OperatorType operator) {
      for(ZenNativeOperator unaryOperator : this.unaryOperators) {
         if (unaryOperator.getOperator() == operator) {
            return true;
         }
      }

      for(ZenTypeNative parent : this.implementing) {
         if (parent.hasUnary(type, operator)) {
            return true;
         }
      }

      return false;
   }

   public String getName() {
      return this.classPkg + '.' + this.className;
   }

   public String toString() {
      return "ZenTypeNative: " + this.getName();
   }

   public List<String> dumpTypeInfo() {
      List<String> stringList = new ArrayList();
      this.members.forEach((s, zenNativeMember) -> {
         stringList.add("Members: " + s);

         for(IJavaMethod iJavaMethod : zenNativeMember.getMethods()) {
            stringList.add("\t" + iJavaMethod.toString());
         }

         if (zenNativeMember.getGetter() != null) {
            stringList.add("Getter: " + zenNativeMember.getGetter().toString());
         }

         if (zenNativeMember.getSetter() != null) {
            stringList.add("Setter: " + zenNativeMember.getSetter().toString());
         }

      });
      this.staticMembers.forEach((s, zenNativeMember) -> {
         stringList.add("Static Members: " + s);

         for(IJavaMethod iJavaMethod : zenNativeMember.getMethods()) {
            stringList.add("\t" + iJavaMethod.toString());
         }

         if (zenNativeMember.getGetter() != null) {
            stringList.add("Static Getter: " + zenNativeMember.getGetter().toString());
         }

         if (zenNativeMember.getSetter() != null) {
            stringList.add("Static Setter: " + zenNativeMember.getSetter().toString());
         }

      });
      return stringList;
   }

   public Map<String, ZenNativeMember> getMembers() {
      return this.members;
   }

   public Map<String, ZenNativeMember> getStaticMembers() {
      return this.staticMembers;
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }

   private void addSubtypes(Queue<ZenTypeNative> todo, ITypeRegistry types) {
      label26:
      while(true) {
         if (!todo.isEmpty()) {
            ZenTypeNative current = (ZenTypeNative)todo.poll();
            if (current.cls.getSuperclass() != Object.class) {
               ZenType type = types.getType(current.cls.getSuperclass());
               if (type instanceof ZenTypeNative) {
                  todo.offer((ZenTypeNative)type);
                  this.implementing.add((ZenTypeNative)type);
               }
            }

            Class[] var9 = current.cls.getInterfaces();
            int var5 = var9.length;
            int var6 = 0;

            while(true) {
               if (var6 >= var5) {
                  continue label26;
               }

               Class iface = var9[var6];
               ZenType type = types.getType(iface);
               if (type instanceof ZenTypeNative) {
                  todo.offer((ZenTypeNative)type);
                  this.implementing.add((ZenTypeNative)type);
               }

               ++var6;
            }
         }

         return;
      }
   }

   public List<DumpZenType> asDumpedObject() {
      return Collections.singletonList(new DumpZenTypeNative(this.toJavaClass(), this.getName(), this.members, this.staticMembers, this.casters, this.trinaryOperators, this.binaryOperators, this.unaryOperators));
   }

   public String getNameForInterfaceSignature() {
      return this.getName().replace('.', '_');
   }

   private class AnyNativeDefinition implements IAnyDefinition {
      private final IEnvironmentGlobal environment;

      public AnyNativeDefinition(IEnvironmentGlobal environment) {
         this.environment = environment;
      }

      public void defineMembers(ClassVisitor output) {
         output.visitField(2, "value", "F", (String)null, (Object)null);
         MethodOutput valueOf = new MethodOutput(output, 9, "valueOf", "(F)" + ZenTypeUtil.signature(IAny.class), (String)null, (String[])null);
         valueOf.start();
         valueOf.newObject(ZenTypeNative.this.anyName2);
         valueOf.dup();
         valueOf.load(ZenTypeNative.this.toASMType(), 0);
         valueOf.construct(ZenTypeNative.this.anyName2, "L" + ZenTypeNative.this.cls.getName() + ";");
         valueOf.returnObject();
         valueOf.end();
         MethodOutput constructor = new MethodOutput(output, 1, "<init>", "(L" + ZenTypeNative.this.cls.getName() + ";)V", (String)null, (String[])null);
         constructor.start();
         constructor.loadObject(0);
         constructor.invokeSpecial(ZenTypeUtil.internal(Object.class), "<init>", "()V");
         constructor.loadObject(0);
         constructor.load(Type.FLOAT_TYPE, 1);
         constructor.putField(ZenTypeNative.this.anyName2, "value", "L" + ZenTypeNative.this.cls.getName() + ";");
         constructor.returnType(Type.VOID_TYPE);
         constructor.end();
      }

      public void defineStaticCanCastImplicit(MethodOutput output) {
         output.constant(Type.getType(ZenTypeNative.this.cls));
         output.loadObject(1);
         output.invokeVirtual(Class.class, "isAssignableFrom", Boolean.TYPE, Class.class);
         Label lblNotAssignable = new Label();
         output.ifEQ(lblNotAssignable);
         output.iConst1();
         output.returnInt();
         output.label(lblNotAssignable);

         for(ZenNativeCaster caster : ZenTypeNative.this.casters) {
            caster.compileAnyCanCastImplicit(ZenTypeNative.this, output, this.environment, 0);
         }

         TypeExpansion expansion = this.environment.getExpansion(ZenTypeNative.this.getName());
         if (expansion != null) {
            expansion.compileAnyCanCastImplicit(ZenTypeNative.this, output, this.environment, 0);
         }

         output.iConst0();
         output.returnInt();
      }

      public void defineStaticAs(MethodOutput output) {
         output.constant(Type.getType(ZenTypeNative.this.cls));
         output.loadObject(1);
         output.invokeVirtual(Class.class, "isAssignableFrom", Boolean.TYPE, Class.class);
         Label lblNotAssignable = new Label();
         output.ifEQ(lblNotAssignable);
         output.loadObject(0);
         output.returnObject();
         output.label(lblNotAssignable);

         for(ZenNativeCaster caster : ZenTypeNative.this.casters) {
            caster.compileAnyCast(ZenTypeNative.this, output, this.environment, 0, 1);
         }

         TypeExpansion expansion = this.environment.getExpansion(ZenTypeNative.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenTypeNative.this, output, this.environment, 0, 1);
         }

         AnyClassWriter.throwCastException(output, "float", 1);
      }

      public void defineNot(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "not");
      }

      public void defineNeg(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "negate");
      }

      public void defineAdd(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "+");
      }

      public void defineSub(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "-");
      }

      public void defineCat(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "~");
      }

      public void defineMul(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "*");
      }

      public void defineDiv(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "/");
      }

      public void defineMod(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "%");
      }

      public void defineAnd(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "&");
      }

      public void defineOr(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "|");
      }

      public void defineXor(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "^");
      }

      public void defineRange(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "..");
      }

      public void defineCompareTo(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "compare");
      }

      public void defineContains(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "in");
      }

      public void defineMemberGet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "member get");
      }

      public void defineMemberSet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "member set");
      }

      public void defineMemberCall(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "member call");
      }

      public void defineIndexGet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "index get");
      }

      public void defineIndexSet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "index set");
      }

      public void defineCall(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "call");
      }

      public void defineAsBool(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "asBool");
      }

      public void defineAsByte(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "asByte");
      }

      public void defineAsShort(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "asShort");
      }

      public void defineAsInt(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "asInt");
      }

      public void defineAsLong(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "asLong");
      }

      public void defineAsFloat(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "asFloat");
      }

      public void defineAsDouble(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "asDouble");
      }

      public void defineAsString(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "asString");
      }

      public void defineAs(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "as");
      }

      public void defineIs(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "is");
      }

      public void defineGetNumberType(MethodOutput output) {
         output.iConst0();
         output.returnInt();
      }

      public void defineIteratorSingle(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "iterator");
      }

      public void defineIteratorMulti(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "iterator");
      }

      public void defineEquals(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "equals");
      }

      public void defineHashCode(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, ZenTypeNative.this.getName(), "hashCode");
      }
   }

   private class ZenNativeConstructor {
      private final Constructor<?> constructor;

      private ZenNativeConstructor(Constructor<?> constructor) {
         this.constructor = constructor;
      }

      boolean canAccept(IEnvironmentGlobal environment, Expression... arguments) {
         Class<?>[] parameters = this.constructor.getParameterTypes();
         if (arguments.length != parameters.length) {
            return false;
         } else {
            for(int i = 0; i < arguments.length; ++i) {
               if (!arguments[i].getType().canCastImplicit(environment.getType(parameters[i]), environment)) {
                  return false;
               }
            }

            return true;
         }
      }

      public Expression call(ZenPosition position, final Expression... arguments) {
         return new Expression(position) {
            public void compile(boolean result, IEnvironmentMethod environment) {
               environment.getOutput().newObject(ZenTypeNative.this.getNativeClass());
               environment.getOutput().dup();

               for(int i = 0; i < arguments.length; ++i) {
                  Expression argument = arguments[i];
                  argument.cast(this.getPosition(), environment, environment.getType(ZenNativeConstructor.this.constructor.getParameterTypes()[i])).compile(true, environment);
               }

               String signatureBuilder = (String)Arrays.stream(ZenNativeConstructor.this.constructor.getParameterTypes()).map(Type::getDescriptor).collect(Collectors.joining("", "(", ")V"));
               environment.getOutput().invokeSpecial(Type.getInternalName(ZenNativeConstructor.this.constructor.getDeclaringClass()), "<init>", signatureBuilder);
            }

            public ZenType getType() {
               return ZenTypeNative.this;
            }
         };
      }
   }
}
