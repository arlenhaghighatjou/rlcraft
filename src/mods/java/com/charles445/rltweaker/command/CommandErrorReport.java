package com.charles445.rltweaker.command;

import com.charles445.rltweaker.util.ErrorUtil;
import java.util.Map.Entry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandErrorReport extends CommandBase {
   public CommandErrorReport() {
   }

   public String getName() {
      return "rlerrorreport";
   }

   public String getUsage(ICommandSender sender) {
      return "/rlerrorreport";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      this.inform("RLTweaker Errors Count", sender);
      if (ErrorUtil.errorCount.size() == 0) {
         this.inform("None!", sender);
      } else {
         for(Entry<String, Integer> entry : ErrorUtil.errorCount.entrySet()) {
            this.inform((String)entry.getKey() + " : " + entry.getValue(), sender);
         }

      }
   }

   public void inform(String s, ICommandSender sender) {
      sender.sendMessage(new TextComponentString(s));
   }
}
