package stanhebben.zenscript.definitions.zenclasses;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.objectweb.asm.ClassVisitor;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.EnvironmentMethod;
import stanhebben.zenscript.compiler.IEnvironmentClass;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.SymbolArgument;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeZenClass;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ParsedClassConstructor {
   final ZenType[] types;
   private final String[] names;
   private final List<Statement> statements;

   private ParsedClassConstructor(List<ZenType> types, List<String> names, List<Statement> statements) {
      this.types = (ZenType[])types.toArray(new ZenType[types.size()]);
      this.names = (String[])names.toArray(new String[names.size()]);
      this.statements = statements;
   }

   static ParsedClassConstructor parse(ZenTokener parser, IEnvironmentGlobal environment) {
      parser.required(30, "( Needed");
      List<ZenType> types = new LinkedList();
      List<String> names = new LinkedList();

      while(parser.optional(31) == null) {
         String name = parser.required(1, "Parameter identifier required").getValue();
         ZenType type = ZenType.ANY;
         if (parser.optional(120) != null) {
            type = ZenType.read(parser, environment);
         }

         if (names.contains(name)) {
            environment.error("Constructor parameter already present: " + name);
         }

         types.add(type);
         names.add(name);
         parser.optional(11);
      }

      parser.required(5, "{ required");
      List<Statement> statements = new ArrayList();

      while(parser.optional(6) == null) {
         statements.add(Statement.read(parser, environment, (ZenType)null));
      }

      return new ParsedClassConstructor(types, names, statements);
   }

   public String getDescription() {
      StringBuilder builder = new StringBuilder("(");

      for(ZenType type : this.types) {
         builder.append(type.toASMType().getDescriptor());
      }

      return builder.append(")V").toString();
   }

   public void writeAll(IEnvironmentClass environmentNewClass, ClassVisitor newClass, List<ParsedZenClassField> nonStatics, String className, ZenPosition position) {
      MethodOutput init = new MethodOutput(newClass, 1, "<init>", this.getDescription(), (String)null, (String[])null);
      EnvironmentMethod initEnvironment = new EnvironmentMethod(init, environmentNewClass);
      init.start();
      init.loadObject(0);
      init.invokeSpecial(ZenTypeUtil.internal(Object.class), "<init>", "()V");

      for(ParsedZenClassField nonStatic : nonStatics) {
         if (nonStatic.hasInitializer()) {
            init.loadObject(0);
            Expression expression = nonStatic.initializer.compile(initEnvironment, nonStatic.type).eval(environmentNewClass);
            if (nonStatic.type == ZenType.ANY) {
               nonStatic.type = expression.getType();
            }

            expression.compile(true, initEnvironment);
            init.putField(className, nonStatic.name, nonStatic.type.toASMType().getDescriptor());
         }
      }

      EnvironmentMethod environmentMethod = new EnvironmentMethod(init, environmentNewClass);
      this.injectParameters(environmentMethod, position);
      this.writeConstructor(environmentMethod);
      init.ret();
      init.end();
   }

   public void writeConstructor(IEnvironmentMethod environmentMethod) {
      for(Statement statement : this.statements) {
         statement.compile(environmentMethod);
      }

   }

   public boolean canAccept(Expression[] arguments, IEnvironmentGlobal environment) {
      return arguments.length == this.types.length && IntStream.range(0, arguments.length).allMatch((i) -> {
         return arguments[i].getType().canCastImplicit(this.types[i], environment);
      });
   }

   public Expression call(ZenPosition position, Expression[] arguments, ZenTypeZenClass type) {
      if (arguments.length != this.types.length) {
         throw new IllegalArgumentException(String.format("Expected %d arguments, received %d", this.types.length, arguments.length));
      } else {
         return new ParsedClassConstructor.ExpressionCallConstructor(position, type, arguments);
      }
   }

   public void injectParameters(IEnvironmentMethod environmentMethod, ZenPosition position) {
      int i = 0;

      for(int j = 0; i < this.types.length; ++i) {
         environmentMethod.putValue(this.names[i], new SymbolArgument(i + 1 + j, this.types[i]), position);
         if (this.types[i].isLarge()) {
            ++j;
         }
      }

   }

   private final class ExpressionCallConstructor extends Expression {
      private final ZenTypeZenClass type;
      private final Expression[] arguments;

      ExpressionCallConstructor(ZenPosition position, ZenTypeZenClass type, Expression[] arguments) {
         super(position);
         this.type = type;
         this.arguments = arguments;
      }

      public void compile(boolean result, IEnvironmentMethod environment) {
         MethodOutput output = environment.getOutput();
         output.newObject(this.type.getName());
         output.dup();

         for(int i = 0; i < this.arguments.length; ++i) {
            Expression ex = this.arguments[i];
            ex.cast(ex.getPosition(), environment, ParsedClassConstructor.this.types[i]).compile(true, environment);
         }

         output.invokeSpecial(this.type.getName(), "<init>", ParsedClassConstructor.this.getDescription());
      }

      public ZenType getType() {
         return this.type;
      }
   }
}
