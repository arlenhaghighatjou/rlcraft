package net.ilexiconn.llibrary.server.command.argument;

import java.util.List;

public class CommandArguments {
   private List<Argument<?>> arguments;

   public CommandArguments(List<Argument<?>> arguments) {
      this.arguments = arguments;
   }

   public boolean hasArgument(String name) {
      for(Argument argument : this.arguments) {
         if (argument.getName().equals(name)) {
            return true;
         }
      }

      return false;
   }

   public <T> T getArgument(String name) {
      for(Argument<?> argument : this.arguments) {
         if (argument.getName().equals(name)) {
            return (T)argument.getValue();
         }
      }

      return (T)null;
   }
}
