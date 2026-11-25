package com.charles445.rltweaker.command;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import java.io.IOException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandReloadInvestigateAIConfig extends CommandBase {
   public CommandReloadInvestigateAIConfig() {
   }

   public String getName() {
      return "rlreloadinvestigateai";
   }

   public String getUsage(ICommandSender sender) {
      return "/rlreloadinvestigateai";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      try {
         JsonConfig.loadInvestigateAIConfig();
         sender.sendMessage(new TextComponentString(String.format("Loaded %d investigate AI entries", JsonConfig.investigateAI.size())));
      } catch (JsonConfig.JsonFileException var6) {
         Throwable t;
         for(t = var6; t.getCause() != null; t = t.getCause()) {
            ;
         }

         sender.sendMessage(new TextComponentString(String.format("%sFailed to load investigate AI file '%s': %s: %s", TextFormatting.RED, var6.getFile(), t.getClass().getSimpleName(), t.getMessage())));
         RLTweaker.logger.error("Failed to load investigate AI file '{}'", var6.getFile(), var6);
      } catch (IOException var7) {
         sender.sendMessage(new TextComponentString(String.format("%sFailed to load investigate AI config files", TextFormatting.RED)));
         RLTweaker.logger.error("Failed to load investigate AI config files", var7);
      }

   }
}
