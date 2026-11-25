package me.lucko.spark.common.command;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.sender.CommandSender;

public class Command {
   private final List<String> aliases;
   private final List<Command.ArgumentInfo> arguments;
   private final Command.Executor executor;
   private final Command.TabCompleter tabCompleter;

   public static Command.Builder builder() {
      return new Command.Builder();
   }

   private Command(List<String> aliases, List<Command.ArgumentInfo> arguments, Command.Executor executor, Command.TabCompleter tabCompleter) {
      this.aliases = aliases;
      this.arguments = arguments;
      this.executor = executor;
      this.tabCompleter = tabCompleter;
   }

   public List<String> aliases() {
      return this.aliases;
   }

   public List<Command.ArgumentInfo> arguments() {
      return this.arguments;
   }

   public Command.Executor executor() {
      return this.executor;
   }

   public Command.TabCompleter tabCompleter() {
      return this.tabCompleter;
   }

   public String primaryAlias() {
      return (String)this.aliases.get(0);
   }

   public static final class ArgumentInfo {
      private final String argumentName;
      private final String parameterDescription;

      public ArgumentInfo(String argumentName, String parameterDescription) {
         this.argumentName = argumentName;
         this.parameterDescription = parameterDescription;
      }

      public String argumentName() {
         return this.argumentName;
      }

      public String parameterDescription() {
         return this.parameterDescription;
      }

      public boolean requiresParameter() {
         return this.parameterDescription != null;
      }
   }

   public static final class Builder {
      private final com.google.common.collect.ImmutableList.Builder<String> aliases = ImmutableList.builder();
      private final com.google.common.collect.ImmutableList.Builder<Command.ArgumentInfo> arguments = ImmutableList.builder();
      private Command.Executor executor = null;
      private Command.TabCompleter tabCompleter = null;

      Builder() {
      }

      public Command.Builder aliases(String... aliases) {
         this.aliases.add(aliases);
         return this;
      }

      public Command.Builder argumentUsage(String argumentName, String parameterDescription) {
         this.arguments.add(new Command.ArgumentInfo(argumentName, parameterDescription));
         return this;
      }

      public Command.Builder executor(Command.Executor executor) {
         this.executor = (Command.Executor)Objects.requireNonNull(executor, "executor");
         return this;
      }

      public Command.Builder tabCompleter(Command.TabCompleter tabCompleter) {
         this.tabCompleter = (Command.TabCompleter)Objects.requireNonNull(tabCompleter, "tabCompleter");
         return this;
      }

      public Command build() {
         List<String> aliases = this.aliases.build();
         if (aliases.isEmpty()) {
            throw new IllegalStateException("No aliases defined");
         } else if (this.executor == null) {
            throw new IllegalStateException("No defined executor");
         } else {
            if (this.tabCompleter == null) {
               this.tabCompleter = Command.TabCompleter.empty();
            }

            return new Command(aliases, this.arguments.build(), this.executor, this.tabCompleter);
         }
      }
   }

   @FunctionalInterface
   public interface Executor {
      void execute(SparkPlatform var1, CommandSender var2, CommandResponseHandler var3, Arguments var4);
   }

   @FunctionalInterface
   public interface TabCompleter {
      static <S> Command.TabCompleter empty() {
         return (platform, sender, arguments) -> {
            return Collections.emptyList();
         };
      }

      List<String> completions(SparkPlatform var1, CommandSender var2, List<String> var3);
   }
}
