package net.theawesomegem.fishingmadebetter.common.command;

import net.lax1dude.eaglercraft.v1_8.HString;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.server.command.CommandTreeBase;

public class FishingCommand extends CommandTreeBase {
   public FishingCommand() {
      this.addSubcommand(new FishingReloadCommand(this.getName()));
   }

   public String getName() {
      return "fishing";
   }

   public String getUsage(ICommandSender sender) {
      return HString.format("commands.%s.%s.usage", "fishingmadebetter", this.getName());
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      World world = sender.getEntityWorld();
      if (!world.isRemote) {
         if (args.length > 0) {
            ICommand cmd = this.getSubCommand(args[0]);
            if (cmd == null) {
               String subCommandsString = this.getAvailableSubCommandsString(server, sender);
               throw new CommandException("commands.tree_base.invalid_cmd.list_subcommands", new Object[]{args[0], subCommandsString});
            } else if (!cmd.checkPermission(server, sender)) {
               throw new CommandException("commands.generic.permission", new Object[0]);
            } else {
               cmd.execute(server, sender, shiftArgs(args));
            }
         }
      }
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   private static String[] shiftArgs(@Nullable String[] s) {
      if (s != null && s.length != 0) {
         String[] s1 = new String[s.length - 1];
         System.arraycopy(s, 1, s1, 0, s1.length);
         return s1;
      } else {
         return new String[0];
      }
   }

   private String getAvailableSubCommandsString(MinecraftServer server, ICommandSender sender) {
      Collection<String> availableCommands = new ArrayList();

      for(ICommand command : this.getSubCommands()) {
         if (command.checkPermission(server, sender)) {
            availableCommands.add(command.getName());
         }
      }

      return CommandBase.joinNiceStringFromCollection(availableCommands);
   }
}
