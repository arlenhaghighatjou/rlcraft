package stanhebben.zenscript.type;

import java.lang.reflect.Array;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArrayAdd;
import stanhebben.zenscript.expression.ExpressionArrayGet;
import stanhebben.zenscript.expression.ExpressionArrayLength;
import stanhebben.zenscript.expression.ExpressionArraySet;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingRuleArrayArray;
import stanhebben.zenscript.type.casting.CastingRuleArrayList;
import stanhebben.zenscript.type.casting.CastingRuleDelegateArray;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ZenTypeArrayBasic extends ZenTypeArray {
   private final Type asmType;

   public ZenTypeArrayBasic(ZenType base) {
      super(base);
      this.asmType = Type.getType("[" + base.toASMType().getDescriptor());
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ZenTypeArrayBasic) {
         ZenTypeArrayBasic o = (ZenTypeArrayBasic)other;
         return o.getBaseType().equals(this.getBaseType());
      } else {
         return false;
      }
   }

   public int hashCode() {
      int hash = 5;
      hash = 23 * hash + (this.getBaseType() != null ? this.getBaseType().hashCode() : 0);
      return hash;
   }

   public ICastingRule getCastingRule(ZenType type, IEnvironmentGlobal environment) {
      ICastingRule base = super.getCastingRule(type, environment);
      if (base == null && type instanceof ZenTypeArray) {
         ZenType toBaseType = ((ZenTypeArray)type).getBaseType();
         if (type instanceof ZenTypeArrayBasic) {
            return new CastingRuleArrayArray(this.getBaseType().getCastingRule(toBaseType, environment), this, (ZenTypeArrayBasic)type);
         } else if (type instanceof ZenTypeArrayList) {
            return new CastingRuleArrayList(this.getBaseType().getCastingRule(toBaseType, environment), this, (ZenTypeArrayList)type);
         } else {
            throw new RuntimeException("Invalid array type: " + type);
         }
      } else {
         return base;
      }
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      ICastingRuleDelegate arrayRules = new CastingRuleDelegateArray(rules, this);
      this.getBaseType().constructCastingRules(environment, arrayRules, followCasters);
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      if (numValues == 1) {
         return new ZenTypeArrayBasic.ValueIterator(methodOutput.getOutput());
      } else {
         return numValues == 2 ? new ZenTypeArrayBasic.IndexValueIterator(methodOutput.getOutput()) : null;
      }
   }

   public String getAnyClassName(IEnvironmentGlobal global) {
      return null;
   }

   public Type toASMType() {
      return this.asmType;
   }

   public Class toJavaClass() {
      return Array.newInstance(this.getBaseType().toJavaClass(), 0).getClass();
   }

   public String getSignature() {
      return "[" + this.getBaseType().getSignature();
   }

   public IPartialExpression getMemberLength(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value) {
      return new ExpressionArrayLength(position, value.eval(environment));
   }

   public Expression indexGet(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression index) {
      return new ExpressionArrayGet(position, array, index.cast(position, environment, INT));
   }

   public Expression indexSet(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression index, Expression value) {
      return new ExpressionArraySet(position, array, index.cast(position, environment, INT), value.cast(position, environment, this.getBaseType()));
   }

   public Expression add(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression val) {
      return new ExpressionArrayAdd(position, environment, array, val);
   }

   private class IndexValueIterator implements IZenIterator {
      private final MethodOutput methodOutput;

      public IndexValueIterator(MethodOutput methodOutput) {
         this.methodOutput = methodOutput;
      }

      public void compileStart(int[] locals) {
         this.methodOutput.iConst0();
         this.methodOutput.storeInt(locals[0]);
      }

      public void compilePreIterate(int[] locals, Label exit) {
         this.methodOutput.dup();
         this.methodOutput.arrayLength();
         this.methodOutput.loadInt(locals[0]);
         this.methodOutput.ifICmpLE(exit);
         this.methodOutput.dup();
         this.methodOutput.loadInt(locals[0]);
         this.methodOutput.arrayLoad(ZenTypeArrayBasic.this.getBaseType().toASMType());
         this.methodOutput.store(ZenTypeArrayBasic.this.getBaseType().toASMType(), locals[1]);
      }

      public void compilePostIterate(int[] locals, Label exit, Label repeat) {
         this.methodOutput.iinc(locals[0]);
         this.methodOutput.goTo(repeat);
      }

      public void compileEnd() {
         this.methodOutput.pop();
      }

      public ZenType getType(int i) {
         return (ZenType)(i == 0 ? ZenType.INT : ZenTypeArrayBasic.this.getBaseType());
      }
   }

   private class ValueIterator implements IZenIterator {
      private final MethodOutput methodOutput;
      private int index;

      public ValueIterator(MethodOutput methodOutput) {
         this.methodOutput = methodOutput;
      }

      public void compileStart(int[] locals) {
         this.index = this.methodOutput.local(Type.INT_TYPE);
         this.methodOutput.iConst0();
         this.methodOutput.storeInt(this.index);
      }

      public void compilePreIterate(int[] locals, Label exit) {
         this.methodOutput.dup();
         this.methodOutput.arrayLength();
         this.methodOutput.loadInt(this.index);
         this.methodOutput.ifICmpLE(exit);
         this.methodOutput.dup();
         this.methodOutput.loadInt(this.index);
         this.methodOutput.arrayLoad(ZenTypeArrayBasic.this.getBaseType().toASMType());
         this.methodOutput.store(ZenTypeArrayBasic.this.getBaseType().toASMType(), locals[0]);
      }

      public void compilePostIterate(int[] locals, Label exit, Label repeat) {
         this.methodOutput.iinc(this.index, 1);
         this.methodOutput.goTo(repeat);
      }

      public void compileEnd() {
         this.methodOutput.pop();
      }

      public ZenType getType(int i) {
         return ZenTypeArrayBasic.this.getBaseType();
      }
   }
}
