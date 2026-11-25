package de.Whitedraco.switchbow.command;

import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.Config.WriteLoadConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

public class ClientCommand implements IClientCommand {
   public ClientCommand() {
   }

   public String getName() {
      return "switchbowClient";
   }

   public String getUsage(ICommandSender sender) {
      return "switchbowClient <Text>";
   }

   public List<String> getAliases() {
      List aliases = new ArrayList();
      aliases.add("switchbowClient");
      return aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         sender.sendMessage(new TextComponentString("Invalid argument"));
      } else {
         if (args[0].equals("showScope")) {
            if (args[1].equals("true")) {
               SwitchBowConfig.showScope.setBool(true);

               try {
                  SwitchBowConfig.writeConfigSwitchBow();
               } catch (IOException var6) {
                  var6.printStackTrace();
               }

               sender.sendMessage(new TextComponentString("Show Scope to true"));
            } else if (args[1].equals("false")) {
               SwitchBowConfig.showScope.setBool(false);

               try {
                  SwitchBowConfig.writeConfigSwitchBow();
               } catch (IOException var5) {
                  var5.printStackTrace();
               }

               sender.sendMessage(new TextComponentString("Show Scope to false"));
            }
         }

         if (args[0].equals("resetArrowGui")) {
            SwitchBowConfig.arrowPicPosX.setInteger(-99);
            SwitchBowConfig.arrowPicPosY.setInteger(-99);
            SwitchBowConfig.arrowStringPosX.setInteger(-99);
            SwitchBowConfig.arrowStringPosY.setInteger(-99);
            WriteLoadConfig.rewriteConfigSwitchBow();
            sender.sendMessage(new TextComponentString("Reset Arrow-GUI"));
         }

      }
   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return true;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      if (args.length < 2) {
         List<String> returni = new ArrayList();
         returni.add("showScope");
         returni.add("resetArrowGui");
         return returni;
      } else if (args.length == 2 && !args[0].isEmpty() && args[0].equals("showScope")) {
         List<String> returni = new ArrayList();
         returni.add("true");
         returni.add("false");
         return returni;
      } else {
         return new ArrayList();
      }
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }

   public int compareTo(ICommand o) {
      return this.getName().compareTo(o.getName());
   }

   public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
      return false;
   }
}
