package cursedflames.bountifulbaubles.wormhole;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandWormhole extends CommandBase {
   private static final String USAGE = "bountifulbaubles.commands.wormhole.usage";

   public CommandWormhole() {
   }

   public String getName() {
      return "wormhole";
   }

   public String getUsage(ICommandSender sender) {
      return "bountifulbaubles.commands.wormhole.usage";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return true;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      EntityPlayer target = getCommandSenderAsPlayer(sender);
      if (args.length < 1) {
         throw new WrongUsageException("bountifulbaubles.commands.wormhole.usage", new Object[0]);
      } else if (!args[0].equals("acc") && !args[0].equals("deny")) {
         throw new WrongUsageException("bountifulbaubles.commands.wormhole.usage", new Object[0]);
      } else {
         boolean accept = args[0].equals("acc");
         String origin = args.length > 1 ? args[1] : null;
         TeleportRequest.acceptReject(target, accept, origin);
      }
   }
}
