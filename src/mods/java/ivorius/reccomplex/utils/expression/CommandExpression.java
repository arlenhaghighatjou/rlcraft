package ivorius.reccomplex.utils.expression;

import com.google.common.primitives.Ints;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.text.ParseException;
import java.util.function.Function;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;

public class CommandExpression extends BoolFunctionExpressionCache<CommandExpression.Argument, Object> {
   public static final String NAME_PREFIX = "name=";
   public static final String PERM_PREFIX = "canUseLevel:";

   public CommandExpression() {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Any Command");
      this.addTypes(new CommandExpression.NameType("name=", ""), new Function[]{(t) -> {
         return t.alias("$", "");
      }});
      this.addTypes(new CommandExpression.PermType("canUseLevel:", ""), new Function[]{(t) -> {
         return t.alias("#", "");
      }});
   }

   public static class Argument {
      public final String name;
      public final ICommandSender sender;

      public Argument(String name, ICommandSender sender) {
         this.name = name;
         this.sender = sender;
      }
   }

   protected class NameType extends FunctionExpressionCache.VariableType<Boolean, CommandExpression.Argument, Object> {
      public NameType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<CommandExpression.Argument>, Boolean> parse(String var) {
         return (argument) -> {
            return ((CommandExpression.Argument)argument.get()).sender.getName().equals(var);
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         return FunctionExpressionCache.Validity.KNOWN;
      }
   }

   protected class PermType extends FunctionExpressionCache.VariableType<Boolean, CommandExpression.Argument, Object> {
      public PermType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Integer parseNumber(String var) {
         Integer integer = Ints.tryParse(var);
         return integer != null ? integer.intValue() : 0;
      }

      public Function<SupplierCache<CommandExpression.Argument>, Boolean> parse(String var) throws ParseException {
         Integer level = this.parseNumber(var);
         if (level == null) {
            throw new ParseException("Not a number: " + var, 0);
         } else {
            return (argument) -> {
               return ((CommandExpression.Argument)argument.get()).sender.canUseCommand(level.intValue(), ((CommandExpression.Argument)argument.get()).name);
            };
         }
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         return FunctionExpressionCache.Validity.KNOWN;
      }
   }
}
