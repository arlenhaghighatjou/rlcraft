package com.teamacronymcoders.base.command;

import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandSubBase extends CommandTreeBase {
   private final String name;

   public CommandSubBase(String name) {
      this.name = name;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return "/" + this.name + " " + (String)this.getSubCommands().stream().map(ICommand::getName).collect(Collectors.joining(" | "));
   }
}
