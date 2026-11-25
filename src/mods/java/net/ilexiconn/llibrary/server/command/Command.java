package net.ilexiconn.llibrary.server.command;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.command.argument.Argument;
import net.ilexiconn.llibrary.server.command.argument.CommandArguments;
import net.ilexiconn.llibrary.server.command.argument.IArgumentParser;
import net.ilexiconn.llibrary.server.util.ListHashMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Command extends CommandBase {
   private String name;
   private String commandUsage;
   private int permissionLevel = 4;
   private ListHashMap<String, IArgumentParser<?>> requiredArguments = new ListHashMap<String, IArgumentParser<?>>();
   private ListHashMap<String, IArgumentParser<?>> optionalArguments = new ListHashMap<String, IArgumentParser<?>>();
   private ICommandExecutor executor;

   private Command(String name) {
      this.name = name;
   }

   public static Command create(String name) {
      return new Command(name);
   }

   public Command setPermissionLevel(int permissionLevel) {
      this.permissionLevel = permissionLevel;
      return this;
   }

   public <T> Command addRequiredArgument(String argument, Class<T> type) {
      if (!this.optionalArguments.isEmpty()) {
         LLibrary.LOGGER.error("Please register required arguments before optional ones! Skipping argument {} with type {}.", argument, type);
         return this;
      } else {
         IArgumentParser<T> argumentParser = CommandHandler.INSTANCE.<T>getParserForType(type);
         if (argumentParser != null) {
            this.requiredArguments.put(argument, argumentParser);
         } else {
            LLibrary.LOGGER.error("Unable to find argument parser for type {}. Skipping argument.", type);
         }

         return this;
      }
   }

   public <T> Command addOptionalArgument(String argument, Class<T> type) {
      IArgumentParser<T> argumentParser = CommandHandler.INSTANCE.<T>getParserForType(type);
      if (argumentParser != null) {
         this.optionalArguments.put(argument, argumentParser);
      } else {
         LLibrary.LOGGER.error("Unable to find argument parser for type {}. Skipping argument.", type);
      }

      return this;
   }

   Command setExecutor(ICommandExecutor executor) {
      this.executor = executor;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public int getRequiredPermissionLevel() {
      return this.permissionLevel;
   }

   public String getUsage(ICommandSender sender) {
      if (this.commandUsage != null) {
         return this.commandUsage;
      } else {
         StringBuilder builder = new StringBuilder();
         builder.append("/");
         builder.append(this.getName());

         for(String requiredArgument : this.requiredArguments.keySet()) {
            builder.append(" ");
            builder.append("<");
            builder.append(requiredArgument);
            builder.append(">");
         }

         for(String optionalArgument : this.optionalArguments.keySet()) {
            builder.append(" ");
            builder.append("[");
            builder.append(optionalArgument);
            builder.append("]");
         }

         this.commandUsage = builder.toString();
         return this.commandUsage;
      }
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length < this.requiredArguments.size()) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else if (args.length > this.requiredArguments.size() + this.optionalArguments.size()) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         List<Argument<?>> arguments = Lists.newArrayList();

         for(int i = 0; i < args.length; ++i) {
            if (i < this.requiredArguments.size()) {
               Entry<String, IArgumentParser<?>> entry = this.requiredArguments.getEntry(i);

               try {
                  arguments.add(new Argument((String)entry.getKey(), ((IArgumentParser)entry.getValue()).parseArgument(server, sender, args[i])));
               } catch (CommandException var8) {
                  sender.sendMessage((new TextComponentString(var8.getLocalizedMessage())).setStyle((new Style()).setColor(TextFormatting.RED)));
               }
            } else {
               Entry<String, IArgumentParser<?>> entry = this.optionalArguments.getEntry(i - this.requiredArguments.size());

               try {
                  arguments.add(new Argument((String)entry.getKey(), ((IArgumentParser)entry.getValue()).parseArgument(server, sender, args[i])));
               } catch (CommandException var9) {
                  sender.sendMessage((new TextComponentString(var9.getLocalizedMessage())).setStyle((new Style()).setColor(TextFormatting.RED)));
               }
            }
         }

         this.executor.execute(server, sender, new CommandArguments(arguments));
      }
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length <= this.requiredArguments.size()) {
         return ((IArgumentParser)this.requiredArguments.getValue(args.length - 1)).getTabCompletion(server, sender, args, pos);
      } else {
         return args.length <= this.requiredArguments.size() + this.optionalArguments.size() ? ((IArgumentParser)this.optionalArguments.getValue(args.length - this.requiredArguments.size() - 1)).getTabCompletion(server, sender, args, pos) : Collections.emptyList();
      }
   }
}
