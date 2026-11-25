package ivorius.reccomplex..shadow.mcopts.commands.parameters;

import java.util.function.IntFunction;
import net.minecraft.command.CommandBase;

public class NaP {
   public NaP() {
   }

   public static String join(String a, String b) {
      return a + " " + b;
   }

   public static Parameter<Integer> asInt(Parameter<String> p) {
      return p.<Integer>map(CommandBase::parseInt);
   }

   public static Parameter<Boolean> asBoolean(Parameter<String> p) {
      return p.<Boolean>map(CommandBase::parseBoolean);
   }

   public static Parameter<Double> asDouble(Parameter<String> p) {
      return p.<Double>map(CommandBase::parseDouble);
   }

   public static Parameter<Long> asLong(Parameter<String> p) {
      return p.<Long>map(CommandBase::parseLong);
   }

   public static Parameter<String[]> varargs(Parameter<String> p) {
      return p.varargs((x$0) -> {
         return new String[x$0];
      });
   }
}
