package stanhebben.zenscript.type;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.casting.CastingRuleMatchedFunction;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ZenTypeFunction extends ZenType {
   protected final ZenType returnType;
   protected final ZenType[] argumentTypes;
   protected final ParsedExpression[] defaultExpressions;
   private final String name;
   protected final Map<ZenType, CastingRuleMatchedFunction> implementedInterfaces = new HashMap();

   public ZenTypeFunction(ZenType returnType, List<ParsedFunctionArgument> arguments) {
      this.returnType = returnType;
      this.argumentTypes = new ZenType[arguments.size()];
      this.defaultExpressions = new ParsedExpression[arguments.size()];

      for(int i = 0; i < this.argumentTypes.length; ++i) {
         ParsedFunctionArgument argument = (ParsedFunctionArgument)arguments.get(i);
         this.argumentTypes[i] = argument.getType();
         this.defaultExpressions[i] = argument.getDefaultExpression();
      }

      StringBuilder nameBuilder = new StringBuilder();
      nameBuilder.append("function(");
      boolean first = true;

      for(ZenType type : this.argumentTypes) {
         if (first) {
            first = false;
         } else {
            nameBuilder.append(',');
         }

         nameBuilder.append(type.getName());
      }

      nameBuilder.append(returnType.getName());
      this.name = nameBuilder.toString();
   }

   public ZenTypeFunction(ZenType returnType, ZenType[] argumentTypes) {
      this.returnType = returnType;
      this.argumentTypes = argumentTypes;
      this.defaultExpressions = new ParsedExpression[argumentTypes.length];
      StringBuilder nameBuilder = new StringBuilder();
      nameBuilder.append("function(");
      boolean first = true;

      for(ZenType type : argumentTypes) {
         if (first) {
            first = false;
         } else {
            nameBuilder.append(',');
         }

         nameBuilder.append(type.getName());
      }

      nameBuilder.append(')');
      nameBuilder.append(returnType.getName());
      this.name = nameBuilder.toString();
   }

   public String getAnyClassName(IEnvironmentGlobal global) {
      throw new UnsupportedOperationException("functions cannot yet be used as any value");
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      environment.error(position, "Functions have no members");
      return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "Functions have no static members");
      return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public ICastingRule getCastingRule(ZenType type, IEnvironmentGlobal environment) {
      if (this.implementedInterfaces.containsKey(type)) {
         return (ICastingRule)this.implementedInterfaces.get(type);
      } else {
         Class<?> cls = type.toJavaClass();
         System.out.println("Can cast this function to " + cls.getName() + "?");
         Method method = ZenTypeUtil.findFunctionalInterfaceMethod(cls);
         if (method != null) {
            ZenType methodReturnType = environment.getType(method.getGenericReturnType());
            ICastingRule returnCastingRule = null;
            if (!this.returnType.equals(methodReturnType)) {
               returnCastingRule = this.returnType.getCastingRule(environment.getType(method.getGenericReturnType()), environment);
               if (returnCastingRule == null) {
                  System.out.println("Return types don't match");
                  return null;
               }
            }

            Type[] methodParameters = method.getGenericParameterTypes();
            if (methodParameters.length < this.argumentTypes.length) {
               System.out.println("Argument count doesn't match");
               return null;
            } else {
               ICastingRule[] argumentCastingRules = new ICastingRule[this.argumentTypes.length];

               for(int i = 0; i < argumentCastingRules.length; ++i) {
                  ZenType argumentType = environment.getType(methodParameters[i]);
                  if (!argumentType.equals(this.argumentTypes[i])) {
                     argumentCastingRules[i] = argumentType.getCastingRule(this.argumentTypes[i], environment);
                     if (argumentCastingRules[i] == null) {
                        System.out.println("Argument " + i + " doesn't match");
                        System.out.println("Cannot cast " + argumentType.getName() + " to " + this.argumentTypes[i].getName());
                        return null;
                     }
                  }
               }

               CastingRuleMatchedFunction castingRule = new CastingRuleMatchedFunction(this, type, returnCastingRule, argumentCastingRules);
               this.implementedInterfaces.put(type, castingRule);
               System.out.println("Can cast this function");
               return castingRule;
            }
         } else {
            return null;
         }
      }
   }

   public org.objectweb.asm.Type toASMType() {
      return org.objectweb.asm.Type.getType(Object.class);
   }

   public int getNumberType() {
      return 0;
   }

   public String getSignature() {
      return "Ljava/lang/Object;";
   }

   public boolean isPointer() {
      return true;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      environment.error(position, "cannot apply operators on a function");
      return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      environment.error(position, "cannot apply operators on a function");
      return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      environment.error(position, "cannot apply operators on a function");
      return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      environment.error(position, "cannot apply operators on a function");
      return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      return null;
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return (ZenType[])Arrays.copyOf(this.argumentTypes, numArguments);
   }

   public Class toJavaClass() {
      return null;
   }

   public String getName() {
      return this.name;
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }

   public ZenType getReturnType() {
      return this.returnType;
   }

   public ZenType[] getArgumentTypes() {
      return this.argumentTypes;
   }

   public String getNameForInterfaceSignature() {
      return ZenTypeFunctionCallable.makeInterfaceName(this.returnType, this.argumentTypes);
   }
}
