package stanhebben.zenscript;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import stanhebben.zenscript.annotations.ZenOperator;
import stanhebben.zenscript.annotations.ZenProperty;
import stanhebben.zenscript.annotations.ZenSetter;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.definitions.ParsedExpansion;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.expand.ZenExpandCaster;
import stanhebben.zenscript.type.expand.ZenExpandMember;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.type.natives.ZenNativeOperator;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class TypeExpansion {
   private final String type;
   private final Map<String, ZenExpandMember> members;
   private final Map<String, ZenExpandMember> staticMembers;
   private final Map<String, ParsedExpansion> zenMembers;
   private final List<ZenExpandCaster> casters;
   private final List<ZenNativeOperator> trinaryOperators;
   private final List<ZenNativeOperator> binaryOperators;
   private final List<ZenNativeOperator> unaryOperators;

   public TypeExpansion(String type) {
      this.type = type;
      this.members = new HashMap();
      this.staticMembers = new HashMap();
      this.zenMembers = new HashMap();
      this.casters = new ArrayList();
      this.trinaryOperators = new ArrayList();
      this.binaryOperators = new ArrayList();
      this.unaryOperators = new ArrayList();
   }

   public void expand(Class<?> cls, ITypeRegistry types) {
      for(Method method : cls.getMethods()) {
         String methodName = method.getName();

         for(Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof ZenCaster) {
               this.checkStatic(method);
               this.casters.add(new ZenExpandCaster(new JavaMethod(method, types)));
            } else if (annotation instanceof ZenGetter) {
               this.checkStatic(method);
               ZenGetter getterAnnotation = (ZenGetter)annotation;
               String name = getterAnnotation.value().length() == 0 ? method.getName() : getterAnnotation.value();
               this.checkGetter(method, cls);
               if (!this.members.containsKey(name)) {
                  this.members.put(name, new ZenExpandMember(this.type, name));
               }

               ((ZenExpandMember)this.members.get(name)).setGetter(new JavaMethod(method, types));
            } else if (annotation instanceof ZenSetter) {
               this.checkStatic(method);
               ZenSetter setterAnnotation = (ZenSetter)annotation;
               this.checkSetter(method, cls);
               String name = setterAnnotation.value().length() == 0 ? method.getName() : setterAnnotation.value();
               if (!this.members.containsKey(name)) {
                  this.members.put(name, new ZenExpandMember(this.type, name));
               }

               ((ZenExpandMember)this.members.get(name)).setSetter(new JavaMethod(method, types));
            } else if (annotation instanceof ZenOperator) {
               this.checkStatic(method);
               ZenOperator operatorAnnotation = (ZenOperator)annotation;
               switch(operatorAnnotation.value()) {
               case NEG:
               case NOT:
                  if (method.getParameterTypes().length == 1) {
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
                  if (method.getParameterTypes().length != 2) {
                     throw new RuntimeException("Binary operator expansion needs a static method with 2 arguments - " + cls.getName() + "." + method.getName());
                  }

                  this.binaryOperators.add(new ZenNativeOperator(operatorAnnotation.value(), new JavaMethod(method, types)));
                  break;
               case INDEXSET:
                  if (method.getParameterTypes().length == 3) {
                     this.trinaryOperators.add(new ZenNativeOperator(operatorAnnotation.value(), new JavaMethod(method, types)));
                  }
               }
            } else if (annotation instanceof ZenMethod) {
               this.checkStatic(method);
               ZenMethod methodAnnotation = (ZenMethod)annotation;
               if (methodAnnotation.value().length() > 0) {
                  methodName = methodAnnotation.value();
               }

               if (!this.members.containsKey(methodName)) {
                  this.members.put(methodName, new ZenExpandMember(this.type, methodName));
               }

               ((ZenExpandMember)this.members.get(methodName)).addMethod(new JavaMethod(method, types));
            } else if (annotation instanceof ZenMethodStatic) {
               this.checkStatic(method);
               ZenMethodStatic methodAnnotation = (ZenMethodStatic)annotation;
               if (methodAnnotation.value().length() > 0) {
                  methodName = methodAnnotation.value();
               }

               if (!this.staticMembers.containsKey(methodName)) {
                  this.staticMembers.put(methodName, new ZenExpandMember(this.type, methodName));
               }

               ((ZenExpandMember)this.staticMembers.get(methodName)).addMethod(new JavaMethod(method, types));
            }
         }
      }

      for(Field field : cls.getFields()) {
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

               this.members.putIfAbsent(propertyName, new ZenExpandMember(this.type, propertyName));

               try {
                  Method getterMethod = cls.getMethod(getterName);
                  this.checkGetter(getterMethod, cls);
                  ((ZenExpandMember)this.members.get(propertyName)).setGetter(new JavaMethod(getterMethod, types));
               } catch (NoSuchMethodException var18) {
                  throw new RuntimeException("Couldn't find getter for property " + propertyName + " on " + cls.getName());
               }

               try {
                  Method setterMethod = cls.getMethod(setterName, field.getType());
                  this.checkSetter(setterMethod, cls);
                  ((ZenExpandMember)this.members.get(propertyName)).setSetter(new JavaMethod(setterMethod, types));
               } catch (NoSuchMethodException var17) {
                  throw new RuntimeException("Couldn't find setter for property " + propertyName + " on " + cls.getName());
               }
            }
         }
      }

   }

   public void addZenExpandMethod(String name, ParsedExpansion expansion) {
      this.zenMembers.put(name, expansion);
   }

   private void checkGetter(Method method, Class cls) {
      if (method.getReturnType().equals(Void.TYPE)) {
         throw new RuntimeException("ZenGetter needs a non Void returntype - " + cls.getName() + "." + method.getName());
      } else if (method.getParameterCount() != 1) {
         throw new RuntimeException("ZenGetters in Expansions must have exactly one parameter - " + cls.getName() + "." + method.getName());
      }
   }

   private void checkSetter(Method method, Class cls) {
      if (method.getParameterCount() != 2) {
         throw new RuntimeException("ZenSetter in Expansions must have exactly two parameters - " + cls.getName() + "." + method.getName());
      } else if (!method.getReturnType().equals(Void.TYPE)) {
         throw new RuntimeException("ZenSetter must have a void return type");
      }
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules) {
      for(ZenExpandCaster caster : this.casters) {
         caster.constructCastingRules(environment, rules);
      }

   }

   public ZenExpandCaster getCaster(ZenType type, IEnvironmentGlobal environment) {
      for(ZenExpandCaster caster : this.casters) {
         if (caster.getTarget().equals(type)) {
            return caster;
         }
      }

      for(ZenExpandCaster caster : this.casters) {
         if (caster.getTarget().canCastImplicit(type, environment)) {
            return caster;
         }
      }

      return null;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      for(ZenNativeOperator op : this.unaryOperators) {
         if (op.getOperator() == operator) {
            return new ExpressionCallStatic(position, environment, op.getMethod(), new Expression[]{value});
         }
      }

      return null;
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      for(ZenNativeOperator op : this.binaryOperators) {
         if (op.getOperator() == operator) {
            return new ExpressionCallStatic(position, environment, op.getMethod(), new Expression[]{left, right});
         }
      }

      return null;
   }

   public Expression ternary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      for(ZenNativeOperator op : this.trinaryOperators) {
         if (op.getOperator() == operator) {
            return new ExpressionCallStatic(position, environment, op.getMethod(), new Expression[]{first, second, third});
         }
      }

      return null;
   }

   public IPartialExpression instanceMember(ZenPosition position, IEnvironmentGlobal environment, Expression value, String member) {
      if (this.members.containsKey(member)) {
         return ((ZenExpandMember)this.members.get(member)).instance(position, environment, value);
      } else {
         return this.zenMembers.containsKey(member) ? ((ParsedExpansion)this.zenMembers.get(member)).instance(position, value) : null;
      }
   }

   public IPartialExpression staticMember(ZenPosition position, IEnvironmentGlobal environment, String member) {
      return this.staticMembers.containsKey(member) ? ((ZenExpandMember)this.staticMembers.get(member)).instance(position, environment) : null;
   }

   public void compileAnyCast(ZenType type, MethodOutput output, IEnvironmentGlobal environment, int localValue, int localClass) {
      if (type == null) {
         throw new IllegalArgumentException("type cannot be null");
      } else {
         Type asmType = type.toASMType();
         if (asmType == null) {
            throw new RuntimeException("type has no asm type");
         } else {
            for(ZenExpandCaster caster : this.casters) {
               Label skip = new Label();
               output.loadObject(localClass);
               output.constant(caster.getTarget().toASMType());
               output.ifACmpNe(skip);
               output.load(asmType, localValue);
               caster.compile(output);
               output.returnType(caster.getTarget().toASMType());
               output.label(skip);
            }

            for(ZenExpandCaster caster : this.casters) {
               String casterAny = caster.getTarget().getAnyClassName(environment);
               if (casterAny != null) {
                  Label skip = new Label();
                  output.loadObject(localClass);
                  output.invokeStatic(casterAny, "rtCanCastImplicit", "(Ljava/lang/Class;)Z");
                  output.ifEQ(skip);
                  output.load(type.toASMType(), localValue);
                  caster.compile(output);
                  output.loadObject(localClass);
                  output.invokeStatic(casterAny, "rtAs", "(" + caster.getTarget().getSignature() + "Ljava/lang/Class;)Ljava/lang/Object;");
                  output.returnType(caster.getTarget().toASMType());
                  output.label(skip);
               }
            }

         }
      }
   }

   public void compileAnyCanCastImplicit(ZenType type, MethodOutput output, IEnvironmentGlobal environment, int localClass) {
      for(ZenExpandCaster caster : this.casters) {
         Label skip = new Label();
         output.loadObject(localClass);
         output.constant(caster.getTarget().toASMType());
         output.ifACmpNe(skip);
         output.iConst1();
         output.returnInt();
         output.label(skip);
      }

      for(ZenExpandCaster caster : this.casters) {
         String casterAny = caster.getTarget().getAnyClassName(environment);
         if (casterAny != null) {
            Label skip = new Label();
            output.loadObject(localClass);
            output.invokeStatic(casterAny, "rtCanCastImplicit", "(Ljava/lang/Class;)Z");
            output.ifEQ(skip);
            output.iConst1();
            output.returnInt();
            output.label(skip);
         }
      }

   }

   public boolean compileAnyUnary(MethodOutput output, OperatorType type, IEnvironmentMethod environment) {
      for(ZenNativeOperator operator : this.unaryOperators) {
         if (operator.getOperator() == type) {
            ZenType returnType = operator.getMethod().getReturnType();
            output.loadObject(0);
            operator.getMethod().invokeStatic(output);
            output.invokeStatic(returnType.getAnyClassName(environment), "valueOf", "(" + returnType.getSignature() + ")" + ZenType.ANY.getSignature());
            output.returnObject();
            return true;
         }
      }

      return false;
   }

   private void checkStatic(Method method) {
      if ((method.getModifiers() & 8) == 0) {
         throw new RuntimeException("Expansion method " + method.getName() + " must be static");
      }
   }
}
