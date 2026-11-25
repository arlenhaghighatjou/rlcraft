package net.ilexiconn.llibrary.server.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.ilexiconn.llibrary.server.command.argument.ArgumentParsers;
import net.ilexiconn.llibrary.server.command.argument.IArgumentParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public enum CommandHandler {
   INSTANCE;

   private Map<Class<?>, IArgumentParser<?>> argumentParserMap = new HashMap();

   private CommandHandler() {
   }

   public <T> void registerArgumentParser(Class<T> type, IArgumentParser<T> argumentParser) {
      this.argumentParserMap.put(type, argumentParser);
   }

   public <T extends Enum<T>> IArgumentParser<T> getEnumParser(final Class<T> enumClass) {
      return new IArgumentParser<T>() {
         private String[] values = (String[])Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).map((s) -> {
            return s.toLowerCase(Locale.ENGLISH);
         }).toArray((x$0) -> {
            return new String[x$0];
         });

         public T parseArgument(MinecraftServer server, ICommandSender sender, String argument) {
            return (T)Enum.valueOf(enumClass, argument.toUpperCase(Locale.ENGLISH));
         }

         public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            return CommandBase.getListOfStringsMatchingLastWord(args, this.values);
         }
      };
   }

   public <T> IArgumentParser<T> getParserForType(Class<T> type) {
      IArgumentParser<T> argumentParser = ArgumentParsers.<T>getBuiltinParser(type);
      if (argumentParser != null) {
         return argumentParser;
      } else {
         return this.argumentParserMap.containsKey(type) ? (IArgumentParser)this.argumentParserMap.get(type) : null;
      }
   }

   public void registerCommand(FMLServerStartingEvent event, Command command, ICommandExecutor executor) {
      event.registerServerCommand(command.setExecutor(executor));
   }
}
