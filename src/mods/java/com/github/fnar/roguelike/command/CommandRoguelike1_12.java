package com.github.fnar.roguelike.command;

import greymerk.roguelike.command.CommandContext1_12;
import greymerk.roguelike.command.ICommandRouter;
import greymerk.roguelike.command.routes.CommandRouteRoguelike1_12;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandRoguelike1_12 extends CommandBase {
   private final ICommandRouter router = new CommandRouteRoguelike1_12();

   public CommandRoguelike1_12() {
   }

   @Nonnull
   public String getName() {
      return "roguelike";
   }

   public String getUsage(ICommandSender sender) {
      return null;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      CommandContext1_12 context = new CommandContext1_12(new CommandSender1_12(sender));
      this.router.execute(context, Arrays.asList(args));
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return this.router.getTabCompletion(Arrays.asList(args));
   }
}
