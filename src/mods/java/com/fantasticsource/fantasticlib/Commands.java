package com.fantasticsource.fantasticlib;

import com.fantasticsource.tools.Tools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class Commands extends CommandBase {
   private static LinkedHashMap<String, Integer> subcommands = new LinkedHashMap();

   public Commands() {
   }

   public String getName() {
      return "fantasticlib";
   }

   public List<String> getAliases() {
      return Arrays.asList("flib");
   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return true;
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public String getUsage(ICommandSender sender) {
      String result = "";
      if (sender.canUseCommand(((Integer)subcommands.get("uptime")).intValue(), this.getName())) {
         result = result + "\n" + TextFormatting.AQUA + "/" + this.getName() + " uptime";
      }

      if (sender.canUseCommand(((Integer)subcommands.get("heapdump")).intValue(), this.getName())) {
         result = result + "\n" + TextFormatting.AQUA + "/" + this.getName() + " heapdump";
      }

      return !result.equals("") ? result : I18n.translateToLocalFormatted("commands.generic.permission", new Object[0]);
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length == 0) {
         sender.getCommandSenderEntity().sendMessage(new TextComponentString(this.getUsage(sender)));
      } else {
         this.subCommand(sender, args);
      }

   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      ArrayList<String> result = new ArrayList();
      String partial = args[args.length - 1];
      if (args.length == 1) {
         for(Entry<String, Integer> entry : subcommands.entrySet()) {
            if (sender.canUseCommand(((Integer)entry.getValue()).intValue(), this.getName())) {
               result.add(entry.getKey());
            }
         }
      }

      if (partial.length() != 0) {
         result.removeIf((k) -> {
            return partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial);
         });
      }

      return result;
   }

   private void subCommand(ICommandSender sender, String[] args) {
      String cmd = args[0];
      if (!sender.canUseCommand(((Integer)subcommands.get(cmd)).intValue(), this.getName())) {
         notifyCommandListener(sender, this, "commands.generic.permission", new Object[0]);
      } else {
         byte var5 = -1;
         switch(cmd.hashCode()) {
         case -1104203296:
            if (cmd.equals("heapdump")) {
               var5 = 1;
            }
            break;
         case -838362136:
            if (cmd.equals("uptime")) {
               var5 = 0;
            }
         }

         switch(var5) {
         case 0:
            if (FantasticLib.serverStartTime == -1L) {
               notifyCommandListener(sender, this, "fantasticlib.cmd.uptime.noserver", new Object[0]);
            } else {
               long n = System.nanoTime() - FantasticLib.serverStartTime;
               int s = (int)(n / 1000000000L % 60L);
               int m = (int)(n / 1000000000L / 60L % 60L);
               int h = (int)(n / 1000000000L / 60L / 60L % 24L);
               int d = (int)(n / 1000000000L / 60L / 60L / 24L);
               notifyCommandListener(sender, this, "fantasticlib.cmd.uptime", new Object[]{d, h, m, s});
            }
            break;
         case 1:
            Tools.heapdump();
            break;
         default:
            notifyCommandListener(sender, this, this.getUsage(sender), new Object[0]);
         }

      }
   }

   static {
      subcommands.put("uptime", Integer.valueOf(3));
      subcommands.put("heapdump", Integer.valueOf(3));
   }
}
