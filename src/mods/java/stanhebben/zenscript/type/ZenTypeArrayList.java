package stanhebben.zenscript.type;

import java.util.Collection;
import java.util.List;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArrayListAdd;
import stanhebben.zenscript.expression.ExpressionArrayListGet;
import stanhebben.zenscript.expression.ExpressionArrayListRemove;
import stanhebben.zenscript.expression.ExpressionArrayListSet;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingRuleDelegateList;
import stanhebben.zenscript.type.casting.CastingRuleListArray;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.iterator.IteratorIterable;
import stanhebben.zenscript.type.iterator.IteratorList;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ZenTypeArrayList extends ZenTypeArray {
   private final Type type = Type.getType(List.class);
   private static final ZenNativeMember removeMember = new ZenNativeMember();

   public ZenTypeArrayList(ZenType baseType) {
      super(baseType, "[" + baseType + "]");
   }

   public IPartialExpression getMemberLength(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value) {
      return new ZenTypeArrayList.ExpressionListLength(position, value.eval(environment));
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      ICastingRuleDelegate arrayRules = new CastingRuleDelegateList(rules, this);
      this.getBaseType().constructCastingRules(environment, arrayRules, followCasters);
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      if (numValues == 1) {
         return new IteratorIterable(methodOutput.getOutput(), this.getBaseType());
      } else {
         return numValues == 2 ? new IteratorList(methodOutput.getOutput(), this.getBaseType()) : null;
      }
   }

   public ICastingRule getCastingRule(ZenType type, IEnvironmentGlobal environment) {
      ICastingRule superResult = super.getCastingRule(type, environment);
      if (superResult != null) {
         return superResult;
      } else {
         return type instanceof ZenTypeArrayBasic ? new CastingRuleListArray(((ZenTypeArrayBasic)type).getBaseType().getCastingRule(this.getBaseType(), environment), this, (ZenTypeArrayBasic)type) : null;
      }
   }

   public Class toJavaClass() {
      return List.class;
   }

   public String getAnyClassName(IEnvironmentGlobal global) {
      return null;
   }

   public Type toASMType() {
      return Type.getType(List.class);
   }

   public String getSignature() {
      return ZenTypeUtil.signature(List.class);
   }

   public Expression indexGet(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression index) {
      return new ExpressionArrayListGet(position, array, index);
   }

   public Expression indexSet(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression index, Expression value) {
      return new ExpressionArrayListSet(position, array, index, value);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression list, Expression other, OperatorType operator) {
      return (Expression)(operator == OperatorType.SUB ? new ExpressionArrayListRemove(position, list, other) : super.binary(position, environment, list, other, operator));
   }

   public Expression add(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression val) {
      return new ExpressionArrayListAdd(position, environment, array, val);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      return "remove".equals(name) ? removeMember.instance(position, environment, value) : super.getMember(position, environment, value, name);
   }

   public String getNameForInterfaceSignature() {
      return this.getBaseType().getNameForInterfaceSignature() + "Arraylist";
   }

   static {
      removeMember.addMethod(JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Collection.class, "remove", Object.class));
   }

   private class ExpressionListLength extends Expression {
      private final Expression value;

      public ExpressionListLength(ZenPosition position, Expression value) {
         super(position);
         this.value = value;
      }

      public ZenType getType() {
         return ZenTypeInt.INSTANCE;
      }

      public void compile(boolean result, IEnvironmentMethod environment) {
         this.value.compile(result, environment);
         if (result) {
            environment.getOutput().invokeInterface(List.class, "size", Integer.TYPE);
         }

      }
   }
}
