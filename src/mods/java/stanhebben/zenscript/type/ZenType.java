package stanhebben.zenscript.type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.objectweb.asm.Type;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.dump.IDumpConvertable;
import stanhebben.zenscript.dump.types.DumpZenType;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.type.casting.CastingRuleDelegateMap;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.expand.ZenExpandCaster;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public abstract class ZenType implements IDumpConvertable {
   public static final ZenTypeAny ANY = ZenTypeAny.INSTANCE;
   public static final ZenTypeBool BOOL = new ZenTypeBool();
   public static final ZenTypeBoolObject BOOLOBJECT = ZenTypeBoolObject.INSTANCE;
   public static final ZenTypeByte BYTE = ZenTypeByte.INSTANCE;
   public static final ZenTypeByteObject BYTEOBJECT = ZenTypeByteObject.INSTANCE;
   public static final ZenTypeShort SHORT = ZenTypeShort.INSTANCE;
   public static final ZenTypeShortObject SHORTOBJECT = ZenTypeShortObject.INSTANCE;
   public static final ZenTypeInt INT = ZenTypeInt.INSTANCE;
   public static final ZenTypeIntObject INTOBJECT = ZenTypeIntObject.INSTANCE;
   public static final ZenTypeLong LONG = ZenTypeLong.INSTANCE;
   public static final ZenTypeLongObject LONGOBJECT = ZenTypeLongObject.INSTANCE;
   public static final ZenTypeFloat FLOAT = ZenTypeFloat.INSTANCE;
   public static final ZenTypeFloatObject FLOATOBJECT = ZenTypeFloatObject.INSTANCE;
   public static final ZenTypeDouble DOUBLE = ZenTypeDouble.INSTANCE;
   public static final ZenTypeDoubleObject DOUBLEOBJECT = ZenTypeDoubleObject.INSTANCE;
   public static final ZenTypeString STRING = ZenTypeString.INSTANCE;
   public static final ZenTypeVoid VOID = ZenTypeVoid.INSTANCE;
   public static final ZenTypeNull NULL = ZenTypeNull.INSTANCE;
   public static final ZenTypeIntRange INTRANGE = ZenTypeIntRange.INSTANCE;
   public static final ZenTypeArrayBasic ANYARRAY = new ZenTypeArrayBasic(ANY);
   public static final ZenTypeAssociative ANYMAP = new ZenTypeAssociative(ANY, ANY);
   public static final int NUM_BYTE = 1;
   public static final int NUM_SHORT = 2;
   public static final int NUM_INT = 3;
   public static final int NUM_LONG = 4;
   public static final int NUM_FLOAT = 5;
   public static final int NUM_DOUBLE = 6;
   protected static final IJavaMethod BOOL_VALUEOF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Boolean.class, "valueOf", Boolean.TYPE);
   protected static final IJavaMethod BYTE_VALUEOF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Byte.class, "valueOf", Byte.TYPE);
   protected static final IJavaMethod SHORT_VALUEOF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Short.class, "valueOf", Short.TYPE);
   protected static final IJavaMethod INT_VALUEOF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Integer.class, "valueOf", Integer.TYPE);
   protected static final IJavaMethod LONG_VALUEOF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Long.class, "valueOf", Long.TYPE);
   protected static final IJavaMethod FLOAT_VALUEOF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Float.class, "valueOf", Float.TYPE);
   protected static final IJavaMethod DOUBLE_VALUEOF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Double.class, "valueOf", Double.TYPE);
   protected static final IJavaMethod BOOL_VALUE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Boolean.class, "booleanValue");
   protected static final IJavaMethod BYTE_VALUE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Number.class, "byteValue");
   protected static final IJavaMethod SHORT_VALUE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Number.class, "shortValue");
   protected static final IJavaMethod INT_VALUE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Number.class, "intValue");
   protected static final IJavaMethod LONG_VALUE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Number.class, "longValue");
   protected static final IJavaMethod FLOAT_VALUE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Number.class, "floatValue");
   protected static final IJavaMethod DOUBLE_VALUE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Number.class, "doubleValue");
   protected static final IJavaMethod BOOL_TOSTRING_STATIC = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Boolean.class, "toString", Boolean.TYPE);
   protected static final IJavaMethod BYTE_TOSTRING_STATIC = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Byte.class, "toString", Byte.TYPE);
   protected static final IJavaMethod SHORT_TOSTRING_STATIC = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Short.class, "toString", Short.TYPE);
   protected static final IJavaMethod INT_TOSTRING_STATIC = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Integer.class, "toString", Integer.TYPE);
   protected static final IJavaMethod LONG_TOSTRING_STATIC = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Long.class, "toString", Long.TYPE);
   protected static final IJavaMethod FLOAT_TOSTRING_STATIC = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Float.class, "toString", Float.TYPE);
   protected static final IJavaMethod DOUBLE_TOSTRING_STATIC = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Double.class, "toString", Double.TYPE);
   protected static final IJavaMethod BOOL_TOSTRING = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Boolean.class, "toString");
   protected static final IJavaMethod BYTE_TOSTRING = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Byte.class, "toString");
   protected static final IJavaMethod SHORT_TOSTRING = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Short.class, "toString");
   protected static final IJavaMethod INT_TOSTRING = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Integer.class, "toString");
   protected static final IJavaMethod LONG_TOSTRING = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Long.class, "toString");
   protected static final IJavaMethod FLOAT_TOSTRING = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Float.class, "toString");
   protected static final IJavaMethod DOUBLE_TOSTRING = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Double.class, "toString");
   protected static final IJavaMethod PARSE_BOOL = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Boolean.class, "parseBoolean", String.class);
   protected static final IJavaMethod PARSE_BYTE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Byte.class, "parseByte", String.class);
   protected static final IJavaMethod PARSE_SHORT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Short.class, "parseShort", String.class);
   protected static final IJavaMethod PARSE_INT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Integer.class, "parseInt", String.class);
   protected static final IJavaMethod PARSE_LONG = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Long.class, "parseLong", String.class);
   protected static final IJavaMethod PARSE_FLOAT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Float.class, "parseFloat", String.class);
   protected static final IJavaMethod PARSE_DOUBLE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Double.class, "parseDouble", String.class);
   protected static final IJavaMethod PARSE_BOOL_OBJECT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Boolean.class, "valueOf", String.class);
   protected static final IJavaMethod PARSE_BYTE_OBJECT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Byte.class, "valueOf", String.class);
   protected static final IJavaMethod PARSE_SHORT_OBJECT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Short.class, "valueOf", String.class);
   protected static final IJavaMethod PARSE_INT_OBJECT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Integer.class, "valueOf", String.class);
   protected static final IJavaMethod PARSE_LONG_OBJECT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Long.class, "valueOf", String.class);
   protected static final IJavaMethod PARSE_FLOAT_OBJECT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Float.class, "valueOf", String.class);
   protected static final IJavaMethod PARSE_DOUBLE_OBJECT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Double.class, "valueOf", String.class);
   protected static final IJavaMethod STRING_COMPARETO = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, String.class, "compareTo", String.class);
   private Map<ZenType, ICastingRule> castingRules = null;

   public ZenType() {
   }

   public static ZenType parse(String type, IEnvironmentGlobal environment) {
      try {
         String fileName = "Internal_ZenTypeParse";
         ZenTokener parser = new ZenTokener(type, environment.getEnvironment(), "Internal_ZenTypeParse", false);
         parser.setFile(new ZenParsedFile("Internal_ZenTypeParse", "Internal_ZenTypeParse", new ZenTokener("", environment.getEnvironment(), "Internal_ZenTypeParse", false), environment));
         return read(parser, environment);
      } catch (IOException var4) {
         return null;
      }
   }

   public static ZenType read(ZenTokener parser, IEnvironmentGlobal environment) {
      Token next = parser.next();
      ZenType base;
      switch(next.getType()) {
      case 1:
         base = ANY;
         StringBuilder typeName = new StringBuilder();
         typeName.append(next.getValue());
         IPartialExpression partial = environment.getValue(next.getValue(), next.getPosition());
         if (partial == null) {
            environment.error(next.getPosition(), "could not find type " + typeName);
         } else {
            while(parser.optional(10) != null) {
               typeName.append('.');
               Token member = parser.required(1, "identifier expected");
               typeName.append(member.getValue());
               partial = partial.getMember(member.getPosition(), environment, member.getValue());
               if (partial == null) {
                  environment.error(member.getPosition(), "could not find type " + typeName);
                  break;
               }
            }

            if (partial != null) {
               base = partial.toType(environment);
            }
         }
         break;
      case 7:
         base = new ZenTypeArrayList(read(parser, environment));
         parser.required(8, "] expected");
         break;
      case 99:
         base = ANY;
         break;
      case 100:
         base = BOOL;
         break;
      case 101:
         base = BYTE;
         break;
      case 102:
         base = SHORT;
         break;
      case 103:
         base = INT;
         break;
      case 104:
         base = LONG;
         break;
      case 105:
         base = FLOAT;
         break;
      case 106:
         base = DOUBLE;
         break;
      case 107:
         base = STRING;
         break;
      case 108:
         List<ZenType> argumentTypes = new ArrayList();
         parser.required(30, "( required");
         if (parser.optional(31) == null) {
            argumentTypes.add(read(parser, environment));

            while(parser.optional(11) != null) {
               argumentTypes.add(read(parser, environment));
            }

            parser.required(31, ") required");
         }

         ZenType returnType = read(parser, environment);
         base = new ZenTypeFunctionCallable(returnType, (ZenType[])argumentTypes.toArray(new ZenType[0]), environment.makeClassNameWithMiddleName(next.getPosition().getFile().getClassName()));
         ((ZenTypeFunctionCallable)base).writeInterfaceClass(environment);
         break;
      case 110:
         base = VOID;
         break;
      default:
         throw new ParseException(next, "Unknown type: " + next.getValue());
      }

      while(parser.optional(7) != null) {
         if (parser.optional(8) == null) {
            base = new ZenTypeAssociative(base, read(parser, environment));
            parser.required(8, "] expected");
         } else {
            base = new ZenTypeArrayBasic(base);
         }
      }

      return base;
   }

   public abstract Expression unary(ZenPosition var1, IEnvironmentGlobal var2, Expression var3, OperatorType var4);

   public abstract Expression binary(ZenPosition var1, IEnvironmentGlobal var2, Expression var3, Expression var4, OperatorType var5);

   public abstract Expression trinary(ZenPosition var1, IEnvironmentGlobal var2, Expression var3, Expression var4, Expression var5, OperatorType var6);

   public abstract Expression compare(ZenPosition var1, IEnvironmentGlobal var2, Expression var3, Expression var4, CompareType var5);

   public abstract IPartialExpression getMember(ZenPosition var1, IEnvironmentGlobal var2, IPartialExpression var3, String var4);

   public abstract IPartialExpression getStaticMember(ZenPosition var1, IEnvironmentGlobal var2, String var3);

   public abstract Expression call(ZenPosition var1, IEnvironmentGlobal var2, Expression var3, Expression... var4);

   public ZenType[] predictCallTypes(int numArguments) {
      return new ZenType[numArguments];
   }

   public abstract void constructCastingRules(IEnvironmentGlobal var1, ICastingRuleDelegate var2, boolean var3);

   public abstract IZenIterator makeIterator(int var1, IEnvironmentMethod var2);

   public ICastingRule getCastingRule(ZenType type, IEnvironmentGlobal environment) {
      if (this.castingRules == null) {
         this.castingRules = new HashMap();
         this.constructCastingRules(environment, new CastingRuleDelegateMap(this, this.castingRules), true);
      }

      return (ICastingRule)this.castingRules.get(type);
   }

   public final boolean canCastImplicit(ZenType type, IEnvironmentGlobal environment) {
      if (!this.equals(type) && this.getCastingRule(type, environment) == null) {
         return type.toJavaClass() != null && this.toJavaClass() != null ? type.toJavaClass().isAssignableFrom(this.toJavaClass()) : false;
      } else {
         return true;
      }
   }

   public boolean canCastExplicit(ZenType type, IEnvironmentGlobal environment) {
      return this.canCastImplicit(type, environment);
   }

   public abstract Class toJavaClass();

   public abstract Type toASMType();

   public abstract int getNumberType();

   public abstract String getSignature();

   public abstract boolean isPointer();

   public final void compileCast(ZenPosition position, IEnvironmentMethod environment, ZenType type) {
      if (!this.equals(type)) {
         ICastingRule castingRule = this.getCastingRule(type, environment);
         if (castingRule != null) {
            castingRule.compile(environment);
         } else {
            throw new RuntimeException("Cannot cast " + this.getName() + " to " + type.getName());
         }
      }
   }

   public abstract String getAnyClassName(IEnvironmentGlobal var1);

   public abstract String getName();

   public abstract Expression defaultValue(ZenPosition var1);

   public boolean isLarge() {
      return false;
   }

   public String toString() {
      return this.getName();
   }

   protected Expression unaryExpansion(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      TypeExpansion expansion = environment.getExpansion(this.getName());
      return expansion != null ? expansion.unary(position, environment, value, operator) : null;
   }

   protected Expression binaryExpansion(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      TypeExpansion expansion = environment.getExpansion(this.getName());
      return expansion != null ? expansion.binary(position, environment, left, right, operator) : null;
   }

   protected Expression trinaryExpansion(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      TypeExpansion expansion = environment.getExpansion(this.getName());
      return expansion != null ? expansion.ternary(position, environment, first, second, third, operator) : null;
   }

   protected IPartialExpression memberExpansion(ZenPosition position, IEnvironmentGlobal environment, Expression value, String member) {
      TypeExpansion expansion = environment.getExpansion(this.getName());
      return expansion != null ? expansion.instanceMember(position, environment, value, member) : null;
   }

   protected IPartialExpression staticMemberExpansion(ZenPosition position, IEnvironmentGlobal environment, String member) {
      TypeExpansion expansion = environment.getExpansion(this.getName());
      return expansion != null ? expansion.staticMember(position, environment, member) : null;
   }

   protected void constructExpansionCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules) {
      TypeExpansion expansion = environment.getExpansion(this.getName());
      if (expansion != null) {
         expansion.constructCastingRules(environment, rules);
      }

   }

   protected Expression castExpansion(ZenPosition position, IEnvironmentGlobal environment, Expression value, ZenType toType) {
      TypeExpansion expansion = environment.getExpansion(this.getName());
      if (expansion != null) {
         ZenExpandCaster caster = expansion.getCaster(toType, environment);
         if (caster != null) {
            return caster.cast(position, environment, value);
         }
      }

      return null;
   }

   protected boolean canCastExpansion(IEnvironmentGlobal environment, ZenType toType) {
      String name = this.getName();
      TypeExpansion expansion = environment.getExpansion(name);
      if (expansion != null) {
         ZenExpandCaster caster = expansion.getCaster(toType, environment);
         return caster != null;
      } else {
         return false;
      }
   }

   protected boolean compileCastExpansion(ZenPosition position, IEnvironmentMethod environment, ZenType toType) {
      TypeExpansion expansion = environment.getExpansion(this.getName());
      if (expansion != null) {
         ZenExpandCaster caster = expansion.getCaster(toType, environment);
         if (caster != null) {
            caster.compile(environment.getOutput());
            return true;
         }
      }

      return false;
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         return other instanceof ZenType && (Objects.equals(this.getName(), ((ZenType)other).getName()) || Objects.equals(this.toJavaClass(), ((ZenType)other).toJavaClass()));
      }
   }

   public List<DumpZenType> asDumpedObject() {
      return Collections.singletonList(new DumpZenType(this.toJavaClass(), this.getName()));
   }

   public String getNameForInterfaceSignature() {
      return this.getName().replace('.', '_').replace(']', '_').replace('[', '_').replaceAll("\\?", "Object");
   }
}
