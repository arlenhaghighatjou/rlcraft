package stanhebben.zenscript.expression.partial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolZenStaticMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.Pair;
import stanhebben.zenscript.util.ZenPosition;

public class PartialStaticGenerated implements IPartialExpression {
   private final ZenPosition position;
   private final String owner;
   private final String method;
   private final String signature;
   private final List<ParsedFunctionArgument> arguments;
   private final ZenType returnType;

   public PartialStaticGenerated(ZenPosition position, String owner, String method, String signature, List<ParsedFunctionArgument> arguments, ZenType returnType) {
      this.position = position;
      this.owner = owner;
      this.method = method;
      this.signature = signature;
      this.arguments = arguments;
      this.returnType = returnType;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      environment.error(this.position, "cannot use a function as value");
      return new ExpressionInvalid(this.position);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      environment.error(position, "cannot assign to a function");
      return new ExpressionInvalid(position);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "functions don't have members");
      return new ExpressionInvalid(position);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      List<ParsedFunctionArgument> filledDefaultExpressions = this.arguments.subList(values.length, this.arguments.size());
      if (filledDefaultExpressions.stream().anyMatch(Objects::isNull)) {
         environment.error(position, "invalid number of arguments");
         return new ExpressionInvalid(position);
      } else {
         Expression[] arguments = new Expression[values.length];

         for(int i = 0; i < values.length; ++i) {
            arguments[i] = values[i].cast(position, environment, ((ParsedFunctionArgument)this.arguments.get(i)).getType());
         }

         List<Pair<ZenType, ParsedExpression>> collect = new ArrayList();

         for(ParsedFunctionArgument filledDefaultExpression : filledDefaultExpressions) {
            collect.add(new Pair(filledDefaultExpression.getType(), filledDefaultExpression.getDefaultExpression()));
         }

         return new ExpressionCallStatic(position, environment, JavaMethod.getStatic(this.owner, this.method, this.returnType, this.getArgumentTypes()), collect, arguments);
      }
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return (ZenType[])Arrays.copyOf(this.getArgumentTypes(), numArguments);
   }

   public IZenSymbol toSymbol() {
      return new SymbolZenStaticMethod(this.owner, this.method, this.signature, this.arguments, this.returnType);
   }

   public ZenType getType() {
      return this.returnType;
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      environment.error(this.position, "not a valid type");
      return ZenType.ANY;
   }

   private ZenType[] getArgumentTypes() {
      return (ZenType[])this.arguments.stream().map(ParsedFunctionArgument::getType).toArray((x$0) -> {
         return new ZenType[x$0];
      });
   }
}
