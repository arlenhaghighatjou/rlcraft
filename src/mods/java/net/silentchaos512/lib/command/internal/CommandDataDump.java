package net.silentchaos512.lib.command.internal;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.lib.debug.DataDump;

public class CommandDataDump extends CommandBase {
   public CommandDataDump() {
   }

   public String getName() {
      return "silentlib_datadump";
   }

   public List<String> getAliases() {
      return ImmutableList.of("slib_dump");
   }

   public String getUsage(ICommandSender sender) {
      StringBuilder subcommands = new StringBuilder();

      for(CommandDataDump.SubCommand sub : CommandDataDump.SubCommand.values()) {
         if (subcommands.length() > 0) {
            subcommands.append("|");
         }

         subcommands.append(sub.getName());
      }

      return String.format("%sUsage: /%s <%s>", TextFormatting.RED, this.getName(), subcommands.toString());
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length < 1) {
         sender.sendMessage(new TextComponentString(this.getUsage(sender)));
      } else {
         this.getSubCommand(args[0]).ifPresent((subCommand) -> {
            subCommand.execute();
            String line = String.format("Printed %s list to log", subCommand.getName());
            sender.sendMessage(new TextComponentString(line));
         });
      }
   }

   private Optional<CommandDataDump.SubCommand> getSubCommand(String arg) {
      for(CommandDataDump.SubCommand subCommand : CommandDataDump.SubCommand.values()) {
         if (subCommand.name().equalsIgnoreCase(arg)) {
            return Optional.of(subCommand);
         }
      }

      return Optional.empty();
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return (List<String>)(args.length == 1 ? getListOfStringsMatchingLastWord(args, (Collection)Arrays.stream(CommandDataDump.SubCommand.values()).map(CommandDataDump.SubCommand::getName).collect(ImmutableList.toImmutableList())) : ImmutableList.of());
   }

   static enum SubCommand implements IStringSerializable {
      BLOCK(DataDump::dumpBlocks),
      ENCHANTMENT(DataDump::dumpEnchantments),
      ENTITY(DataDump::dumpEntityList),
      ITEM(DataDump::dumpItems),
      POTION(DataDump::dumpPotionEffects);

      private final Runnable command;

      private SubCommand(Runnable command) {
         this.command = command;
      }

      void execute() {
         this.command.run();
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
