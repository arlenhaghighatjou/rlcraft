package com.teamacronymcoders.contenttweaker.api.utils;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.List;
import java.util.Set;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

public class ResourceListCommand extends CraftTweakerCommand {
   private List<String> resourceList;

   public ResourceListCommand(String subCommandName, Set<String> resourceList) {
      this(subCommandName, Lists.newArrayList(resourceList));
   }

   public ResourceListCommand(String subCommandName, List<String> resourceList) {
      super(subCommandName);
      this.resourceList = resourceList;
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct " + this.subCommandName, "/ct " + this.subCommandName, true), SpecialMessagesChat.getNormalMessage(" §3Outputs a list of all " + this.subCommandName + "names in the game to the crafttweaker.log")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      CraftTweakerAPI.logCommand(this.subCommandName + ":");

      for(String name : this.resourceList) {
         CraftTweakerAPI.logCommand("<" + this.subCommandName + ":" + name + ">");
      }

      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of " + this.subCommandName + " generated;", sender));
   }
}
