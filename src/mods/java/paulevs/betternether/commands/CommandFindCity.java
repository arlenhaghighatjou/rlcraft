package paulevs.betternether.commands;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import paulevs.betternether.world.BNWorldGenerator;

public class CommandFindCity extends CommandBase {
   public CommandFindCity() {
   }

   public String getName() {
      return "findNetherCity";
   }

   public String getUsage(ICommandSender sender) {
      return "/findNetherCity or /findNetherCity x y z";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      BlockPos pos = null;
      if (args.length == 0) {
         if (!(sender instanceof EntityPlayer)) {
            this.sendMessage("commands.findnethercity.usage.console", sender);
            return;
         }

         pos = BNWorldGenerator.getNearestCity(sender.getEntityWorld(), sender.getPosition().getX() >> 4, sender.getPosition().getZ() >> 4);
      } else {
         if (args.length != 3) {
            this.sendMessage("commands.findnethercity.usage", sender);
            return;
         }

         try {
            double posX = Double.parseDouble(args[0]);
            double posZ = Double.parseDouble(args[2]);
            pos = BNWorldGenerator.getNearestCity(sender.getEntityWorld(), (int)posX >> 4, (int)posZ >> 4);
         } catch (NumberFormatException var9) {
            this.sendMessage("commands.findnethercity.usage.coordinates", sender);
            return;
         }
      }

      this.sendResult(String.format("%d %d %d", pos.getX() << 4, Integer.valueOf(40), pos.getZ() << 4), sender);
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      List<String> tabs = new ArrayList();
      if (sender instanceof EntityPlayer) {
         if (args.length == 1) {
            tabs.add(Double.toString((double)sender.getPosition().getX()));
         } else if (args.length == 2) {
            tabs.add(Double.toString((double)sender.getPosition().getY()));
         } else if (args.length == 3) {
            tabs.add(Double.toString((double)sender.getPosition().getZ()));
         }
      }

      return tabs;
   }

   private void sendMessage(String message, ICommandSender sender) {
      TextComponentTranslation text = new TextComponentTranslation(message, new Object[0]);
      text.getStyle().setColor(TextFormatting.RED);
      sender.sendMessage(text);
   }

   private void sendResult(String message, ICommandSender sender) {
      sender.sendMessage(new TextComponentString(message));
   }
}
