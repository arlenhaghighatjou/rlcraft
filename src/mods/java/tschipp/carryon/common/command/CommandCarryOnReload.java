package tschipp.carryon.common.command;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import tschipp.carryon.CarryOn;
import tschipp.carryon.common.config.CarryOnConfig;
import tschipp.carryon.common.scripting.ScriptReader;
import tschipp.carryon.network.client.ScriptReloadPacket;

public class CommandCarryOnReload extends CommandBase {
   public CommandCarryOnReload() {
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (CarryOnConfig.settings.useScripts) {
         ScriptReader.reloadScripts();
         CarryOn.network.sendToAll(new ScriptReloadPacket());
         sender.sendMessage(new TextComponentString("Successfully reloaded scripts!"));
      } else {
         sender.sendMessage(new TextComponentString("To use custom Carry On scripts, enable them in the config!"));
      }

   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length > 0) {
         return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"reload"}) : Collections.emptyList();
      } else {
         return Collections.emptyList();
      }
   }

   public String getName() {
      return "reloadscripts";
   }

   public String getUsage(ICommandSender sender) {
      return "/reloadscripts";
   }
}
