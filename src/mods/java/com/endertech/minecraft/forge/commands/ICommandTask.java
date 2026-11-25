package com.endertech.minecraft.forge.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;

public interface ICommandTask {
   String DELIMITER = " ";

   static String enclosedArg(String arg) {
      return "<" + arg + ">";
   }

   static String[] enclosedArgs(String... args) {
      String[] enclosedArgs = new String[args.length];

      for(int i = 0; i < args.length; ++i) {
         enclosedArgs[i] = enclosedArg(args[i]);
      }

      return enclosedArgs;
   }

   String getName();

   default String getUsage() {
      return this.getName() + " " + String.join(" ", enclosedArgs(this.getUsageArgs()));
   }

   String[] getUsageArgs();

   default boolean complyWith(String taskName) {
      return this.getName().equals(taskName);
   }

   default boolean complyWith(String taskName, String[] args) throws CommandException {
      boolean result = this.complyWith(taskName);
      if (result && this.getUsageArgs().length != args.length) {
         throw new WrongUsageException(this.getUsage(), new Object[0]);
      } else {
         return result;
      }
   }
}
