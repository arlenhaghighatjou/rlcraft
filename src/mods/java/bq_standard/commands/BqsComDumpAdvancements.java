package bq_standard.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class BqsComDumpAdvancements extends CommandBase {
   public BqsComDumpAdvancements() {
   }

   public String getName() {
      return "bqs_advancement_dump";
   }

   public String getUsage(ICommandSender sender) {
      return "/bqs_advancement_dump";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length != 0) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         AdvancementDump.INSTANCE.dumpAdvancements(server);
      }
   }
}
