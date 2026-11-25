package com.tmtravlr.qualitytools.config;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandQualityToolsReload extends CommandBase {
   public CommandQualityToolsReload() {
   }

   public String getName() {
      return "qualitytoolsreload";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getUsage(ICommandSender sender) {
      return "commands.qualitytoolsreload.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      ConfigLoader.reloadConfigs();
      if (!ConfigLoader.problems.isEmpty()) {
         if (sender instanceof EntityPlayer) {
            ConfigLoader.notifyProblems((EntityPlayer)sender);
         }

         throw new CommandException("commands.qualitytoolsreload.problems", new Object[0]);
      } else {
         notifyCommandListener(sender, this, "commands.qualitytoolsreload.success", new Object[0]);
      }
   }
}
