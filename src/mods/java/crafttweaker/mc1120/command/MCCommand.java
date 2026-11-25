package crafttweaker.mc1120.command;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.command.ICommand;
import crafttweaker.api.command.ICommandSender;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import java.util.List;
import net.minecraft.command.CommandException;

public class MCCommand implements ICommand {
   private final net.minecraft.command.ICommand command;

   public MCCommand(net.minecraft.command.ICommand iCommand) {
      this.command = iCommand;
   }

   public String getName() {
      return this.command.getName();
   }

   public String getUsage(ICommandSender sender) {
      return this.command.getUsage(CraftTweakerMC.getICommandSender(sender));
   }

   public List<String> getAliases() {
      return this.command.getAliases();
   }

   public void execute(IServer server, ICommandSender sender, String[] args) {
      try {
         this.command.execute(CraftTweakerMC.getMCServer(server), CraftTweakerMC.getICommandSender(sender), args);
      } catch (CommandException var5) {
         CraftTweakerAPI.logError("Error during command execution: ", var5);
      }

   }

   public boolean checkPermission(IServer server, ICommandSender sender) {
      return this.command.checkPermission(CraftTweakerMC.getMCServer(server), CraftTweakerMC.getICommandSender(sender));
   }

   public List<String> getTabCompletions(IServer server, ICommandSender sender, String[] args, IBlockPos targetPos) {
      return this.command.getTabCompletions(CraftTweakerMC.getMCServer(server), CraftTweakerMC.getICommandSender(sender), args, CraftTweakerMC.getBlockPos(targetPos));
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return this.command.isUsernameIndex(args, index);
   }

   public int compare(ICommand other) {
      return this.command.compareTo(CraftTweakerMC.getICommand(other));
   }

   public net.minecraft.command.ICommand getInternal() {
      return this.command;
   }
}
