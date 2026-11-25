package pl.asie.foamfix.ghostbuster;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandGhostBuster extends CommandBase {
   public CommandGhostBuster() {
   }

   public String getName() {
      return "ghostbuster";
   }

   public String getUsage(ICommandSender sender) {
      return "/ghostbuster [on|off]";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length >= 1) {
         if ("on".equals(args[0])) {
            ChunkProviderServerWrapped.debugChunkProviding = true;
            sender.sendMessage(new TextComponentString("Ghost chunkload logging ON!"));
            return;
         }

         if ("off".equals(args[0])) {
            ChunkProviderServerWrapped.debugChunkProviding = false;
            sender.sendMessage(new TextComponentString("Ghost chunkload logging OFF!"));
            return;
         }
      }

      sender.sendMessage(new TextComponentString("Ghost chunkload logging status: " + (ChunkProviderServerWrapped.debugChunkProviding ? "ON" : "OFF")));
   }
}
