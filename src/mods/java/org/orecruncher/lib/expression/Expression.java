package org.orecruncher.lib.expression;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.orecruncher.lib.math.MathStuff;
import org.orecruncher.lib.random.XorShiftRandom;

public final class Expression {
   public static final Variant PI = new NumberValue("PI", 3.1415927F);
   public static final Variant e = new NumberValue("e", 2.7182817F);
   public static final Variant TRUE = new BooleanValue("TRUE", true);
   public static final Variant FALSE = new BooleanValue("FALSE", false);
   private static final OperatorTable builtInOperators = new OperatorTable();
   private static final FunctionTable builtInFunctions = new FunctionTable();
   private static final VariableTable builtInVariables = new VariableTable();
   private String expression = null;
   private List<String> rpn = null;
   private LazyVariant program;
   private final OperatorTable operators;
   private final FunctionTable functions;
   private final VariableTable variables;

   public static void addBuiltInOperator(Operator op) {
      builtInOperators.put(op.getOper(), op);
   }

   public static void addBuiltInFunction(LazyFunction func) {
      builtInFunctions.put(func.getName(), func);
   }

   public static void addBuiltInVariable(String name, LazyVariant number) {
      builtInVariables.put(name, number);
   }

   public static void addBuiltInVariable(@Nonnull Variant v) {
      builtInVariables.put(v.getName(), v);
   }

   public Expression(String expression) {
      this.expression = expression;
      this.operators = new OperatorTable(builtInOperators);
      this.functions = new FunctionTable(builtInFunctions);
      this.variables = new VariableTable(builtInVariables);
   }

   public IVariant eval() {
      return this.getProgram().eval();
   }

   public LazyVariant getProgram() {
      if (this.program == null) {
         Compiler.Result result = (new Compiler(this.operators, this.functions, this.variables)).compile(this.expression);
         this.program = result.expression;
         this.rpn = result.rpn;
      }

      return this.program;
   }

   public List<String> getRPN() {
      if (this.rpn == null) {
         this.getProgram();
      }

      return this.rpn;
   }

   public String toRPN() {
      return String.join(" ", this.getRPN());
   }

   public Expression addVariable(@Nonnull Variant v) {
      this.variables.put(v.getName(), v);
      return this;
   }

   public Expression addVariables(@Nonnull List<? extends Variant> list) {
      list.forEach((v) -> {
         this.addVariable(v);
      });
      return this;
   }

   public Expression addVariables(@Nonnull Map<String, ? extends Variant> map) {
      this.variables.putAll(map);
      return this;
   }

   @Nonnull
   public Expression addFunction(@Nonnull Function func) {
      this.functions.put(func.getName(), func);
      return this;
   }

   public Set<String> getDeclaredVariables() {
      return Collections.unmodifiableSet(this.variables.keySet());
   }

   public Set<String> getDeclaredOperators() {
      return Collections.unmodifiableSet(this.operators.keySet());
   }

   public Set<String> getDeclaredFunctions() {
      return Collections.unmodifiableSet(this.functions.keySet());
   }

   public String getExpression() {
      return this.expression;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Expression that = (Expression)o;
         if (this.expression == null) {
            return that.expression == null;
         } else {
            return this.expression.equals(that.expression);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.expression == null ? 0 : this.expression.hashCode();
   }

   public String toString() {
      return this.toRPN();
   }

   static {
      addBuiltInOperator(new Operator("!", 20, false, true) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().asBoolean() ? Expression.FALSE : Expression.TRUE;
         }
      });
      addBuiltInOperator(new Operator("+", 20, true) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().add(operands[1].eval());
         }
      });
      addBuiltInOperator(new Operator("-", 20, true) {
         public IVariant eval(LazyVariant... operands) {
            return new NumberValue(operands[0].eval().asNumber() - operands[1].eval().asNumber());
         }
      });
      addBuiltInOperator(new Operator("*", 30, true) {
         public IVariant eval(LazyVariant... operands) {
            return new NumberValue(operands[0].eval().asNumber() * operands[1].eval().asNumber());
         }
      });
      addBuiltInOperator(new Operator("/", 30, true) {
         public IVariant eval(LazyVariant... operands) {
            return new NumberValue(operands[0].eval().asNumber() / operands[1].eval().asNumber());
         }
      });
      addBuiltInOperator(new Operator("%", 30, true) {
         public IVariant eval(LazyVariant... operands) {
            return new NumberValue(operands[0].eval().asNumber() % operands[1].eval().asNumber());
         }
      });
      addBuiltInOperator(new Operator("&&", 4, false) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().asBoolean() && operands[1].eval().asBoolean() ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInOperator(new Operator("||", 2, false) {
         public IVariant eval(LazyVariant... operands) {
            return !operands[0].eval().asBoolean() && !operands[1].eval().asBoolean() ? Expression.FALSE : Expression.TRUE;
         }
      });
      addBuiltInOperator(new Operator(">", 10, false) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().compareTo(operands[1].eval()) > 0 ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInOperator(new Operator(">=", 10, false) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().compareTo(operands[1].eval()) >= 0 ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInOperator(new Operator("<", 10, false) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().compareTo(operands[1].eval()) < 0 ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInOperator(new Operator("<=", 10, false) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().compareTo(operands[1].eval()) <= 0 ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInOperator(new Operator("=", 7, false) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().compareTo(operands[1].eval()) == 0 ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInOperator(new Operator("==", 7, false) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().compareTo(operands[1].eval()) == 0 ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInOperator(new Operator("!=", 7, false) {
         public IVariant eval(LazyVariant... operands) {
            return operands[0].eval().compareTo(operands[1].eval()) != 0 ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInOperator(new Operator("<>", 7, false) {
         public Variant eval(LazyVariant... operands) {
            return operands[0].eval().compareTo(operands[1].eval()) != 0 ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInFunction(new Function("MATCH", 2) {
         public IVariant eval(IVariant... parameters) {
            String regex = parameters[0].asString();
            String input = parameters[1].asString();
            return Pattern.matches(regex, input) ? Expression.TRUE : Expression.FALSE;
         }
      });
      addBuiltInFunction(new Function("NOT", 1) {
         public IVariant eval(IVariant... parameters) {
            return parameters[0].asBoolean() ? Expression.FALSE : Expression.TRUE;
         }
      });
      addBuiltInFunction(new LazyFunction("IF", 3) {
         public LazyVariant lazyEval(LazyVariant... lazyParams) {
            boolean isTrue = lazyParams[0].eval().asBoolean();
            return isTrue ? lazyParams[1] : lazyParams[2];
         }
      });
      addBuiltInFunction(new Function("RANDOM", 0) {
         public IVariant eval(IVariant... parameters) {
            return new NumberValue(XorShiftRandom.current().nextFloat());
         }
      });
      addBuiltInFunction(new Function("SIN", 1) {
         public IVariant eval(IVariant... parameters) {
            float d = MathStuff.sin(MathStuff.toRadians(parameters[0].asNumber()));
            return new NumberValue(d);
         }
      });
      addBuiltInFunction(new Function("COS", 1) {
         public IVariant eval(IVariant... parameters) {
            float d = MathStuff.cos(MathStuff.toRadians(parameters[0].asNumber()));
            return new NumberValue(d);
         }
      });
      addBuiltInFunction(new Function("TAN", 1) {
         public IVariant eval(IVariant... parameters) {
            float d = MathStuff.tan(MathStuff.toRadians(parameters[0].asNumber()));
            return new NumberValue(d);
         }
      });
      addBuiltInFunction(new Function("RAD", 1) {
         public Variant eval(IVariant... parameters) {
            float d = MathStuff.toRadians(parameters[0].asNumber());
            return new NumberValue(d);
         }
      });
      addBuiltInFunction(new Function("DEG", 1) {
         public IVariant eval(IVariant... parameters) {
            float d = MathStuff.toDegrees(parameters[0].asNumber());
            return new NumberValue(d);
         }
      });
      addBuiltInFunction(new Function("MAX", -1) {
         public IVariant eval(IVariant... parameters) {
            if (parameters.length == 0) {
               throw new ExpressionException("MAX requires at least one parameter");
            } else {
               IVariant max = null;

               for(IVariant parameter : parameters) {
                  if (max == null || parameter.compareTo(max) > 0) {
                     max = parameter;
                  }
               }

               return max;
            }
         }
      });
      addBuiltInFunction(new Function("ONEOF", -1) {
         public IVariant eval(IVariant... parameters) {
            if (parameters.length < 2) {
               throw new ExpressionException("ONEOF requires at least two parameters");
            } else {
               IVariant selector = parameters[0];

               for(int i = 1; i < parameters.length; ++i) {
                  if (selector.compareTo(parameters[i]) == 0) {
                     return Expression.TRUE;
                  }
               }

               return Expression.FALSE;
            }
         }
      });
      addBuiltInFunction(new Function("MIN", -1) {
         public IVariant eval(IVariant... parameters) {
            if (parameters.length == 0) {
               throw new ExpressionException("MIN requires at least one parameter");
            } else {
               IVariant min = null;

               for(IVariant parameter : parameters) {
                  if (min == null || parameter.compareTo(min) < 0) {
                     min = parameter;
                  }
               }

               return min;
            }
         }
      });
      addBuiltInFunction(new Function("ABS", 1) {
         public IVariant eval(IVariant... parameters) {
            return new NumberValue(MathStuff.abs(parameters[0].asNumber()));
         }
      });
      addBuiltInFunction(new Function("ROUND", 1) {
         public IVariant eval(IVariant... parameters) {
            float toRound = parameters[0].asNumber();
            return new NumberValue((float)Math.round(toRound));
         }
      });
      addBuiltInFunction(new Function("FLOOR", 1) {
         public IVariant eval(IVariant... parameters) {
            float toRound = parameters[0].asNumber();
            return new NumberValue(Math.floor((double)toRound));
         }
      });
      addBuiltInFunction(new Function("CEILING", 1) {
         public IVariant eval(IVariant... parameters) {
            float toRound = parameters[0].asNumber();
            return new NumberValue(Math.ceil((double)toRound));
         }
      });
      addBuiltInFunction(new Function("SQRT", 1) {
         public IVariant eval(IVariant... parameters) {
            float x = parameters[0].asNumber();
            return new NumberValue(Math.sqrt((double)x));
         }
      });
      addBuiltInFunction(new Function("CLAMP", 3) {
         public IVariant eval(IVariant... parameters) {
            float val = parameters[0].asNumber();
            float low = parameters[1].asNumber();
            float high = parameters[2].asNumber();
            return new NumberValue(MathStuff.clamp(val, low, high));
         }
      });
      addBuiltInVariable(e.getName(), e);
      addBuiltInVariable(PI.getName(), PI);
      addBuiltInVariable(TRUE.getName(), TRUE);
      addBuiltInVariable(FALSE.getName(), FALSE);
   }
}
