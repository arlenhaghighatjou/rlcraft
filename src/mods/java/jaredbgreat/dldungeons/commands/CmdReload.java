package jaredbgreat.dldungeons.commands;

import jaredbgreat.dldungeons.ConfigHandler;
import jaredbgreat.dldungeons.ReadAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdReload extends CommandBase {
   public CmdReload() {
   }

   public String getName() {
      return "dldreload";
   }

   public String getUsage(ICommandSender sender) {
      return "/dldreload";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      System.out.println("[DLDUNGEONS] " + sender.getDisplayName().getFormattedText() + " Used /dldreload command; Reloading config file now.");
      ReadAPI.reloadConfig();
      if (ConfigHandler.announceCommands) {
         sender.sendMessage(new TextComponentString("[DLDUNGEONS] " + sender.getDisplayName().getFormattedText() + " Used /dldreload command; Reloading config file now."));
      }

   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public boolean isUsernameIndex(String[] astring, int i) {
      return false;
   }
}
