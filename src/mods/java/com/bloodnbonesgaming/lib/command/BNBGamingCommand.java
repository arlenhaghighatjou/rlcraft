package com.bloodnbonesgaming.lib.command;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public abstract class BNBGamingCommand implements ICommand {
   protected String name;
   protected final List<String> aliases;
   protected final Map<String, BNBGamingCommand> subCommands;
   protected String usage;

   public BNBGamingCommand(String name) {
      this(name, new ArrayList(), new HashMap());
   }

   public BNBGamingCommand(String name, List<String> aliases, Map<String, BNBGamingCommand> subCommands) {
      this.name = name;
      this.aliases = aliases;
      this.subCommands = subCommands;
   }

   public int compareTo(ICommand obj) {
      return obj instanceof ICommand ? obj.getName().compareTo(this.getName()) : 0;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public String getUsage(ICommandSender sender) {
      if (this.usage == null) {
         Set<BNBGamingCommand> sub = new TreeSet(this.subCommands.values());
         StringBuilder builder = (new StringBuilder(this.getName())).append(" (");
         BNBGamingLib.libInstance.getLog().info(builder.toString());

         for(BNBGamingCommand subCommand : sub) {
            builder.append(subCommand.getName()).append("|");
         }

         BNBGamingLib.libInstance.getLog().info(builder.toString());
         builder.deleteCharAt(builder.length() - 1).append(") ..");
         BNBGamingLib.libInstance.getLog().info(builder.toString());
         this.usage = I18n.format("bnbgaminglib.cmd.usage.text", new Object[]{builder.toString()});
      }

      return this.usage;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length == 0) {
         sender.sendMessage(new TextComponentString(this.getUsage(sender)));
      } else {
         BNBGamingCommand subCommand = this.findSubCommand(args[0].toLowerCase());
         if (subCommand == null) {
            sender.sendMessage(new TextComponentString(this.getUsage(sender)));
         } else {
            subCommand.execute(server, sender, (String[])Arrays.copyOfRange(args, 1, args.length));
         }
      }
   }

   public boolean isUsernameIndex(String[] args, int index) {
      if (args.length == 0) {
         return false;
      } else {
         BNBGamingCommand subCommand = this.findSubCommand(args[0].toLowerCase());
         return subCommand == null ? false : subCommand.isUsernameIndex((String[])Arrays.copyOfRange(args, 1, args.length), index - 1);
      }
   }

   public BNBGamingCommand findSubCommand(String name) {
      return (BNBGamingCommand)this.subCommands.get(name);
   }

   public static void putForAllAliases(BNBGamingCommand command, Map<String, BNBGamingCommand> subCommandMap) {
      subCommandMap.put(command.getName(), command);

      for(String alias : command.getAliases()) {
         subCommandMap.put(alias, command);
      }

   }
}
