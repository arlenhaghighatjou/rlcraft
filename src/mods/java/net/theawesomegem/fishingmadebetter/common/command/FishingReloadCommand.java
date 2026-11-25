package net.theawesomegem.fishingmadebetter.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;

public class FishingReloadCommand extends CommandBase {
   private final String parentCommandName;

   public FishingReloadCommand(String parentCommandName) {
      this.parentCommandName = parentCommandName;
   }

   public String getName() {
      return "reload";
   }

   public String getUsage(ICommandSender sender) {
      return String.format("commands.%s.%s.%s.usage", "fishingmadebetter", this.parentCommandName, this.getName());
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      boolean loaded = CustomConfigurationHandler.loadFishes();
      String message;
      if (loaded) {
         message = "Config files reloaded for Fishing Made Better.";
      } else {
         message = "Config files could not be loaded because the folder for the fishes data does not exist.";
      }

      sender.sendMessage(new TextComponentString(message));
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }
}
