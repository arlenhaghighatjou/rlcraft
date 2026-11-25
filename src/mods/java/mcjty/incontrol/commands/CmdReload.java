package mcjty.incontrol.commands;

import mcjty.incontrol.rules.RulesManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdReload extends CommandBase {
   public CmdReload() {
   }

   public String getName() {
      return "ctrlreload";
   }

   public String getUsage(ICommandSender sender) {
      return "ctrlreload";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      sender.sendMessage(new TextComponentString("Reloaded InControl rules"));
      RulesManager.reloadRules();
   }
}
