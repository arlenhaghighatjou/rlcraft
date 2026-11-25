package com.blamejared.compat.chisel;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.function.Consumer;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import team.chisel.api.carving.CarvingUtils;

public class Chisel {
   public Chisel() {
   }

   public static void registerCommands() {
      CTChatCommand.registerCommand(new CraftTweakerCommand("chiselGroups") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("\u00a72/ct chiselGroups", "/ct chiselGroups", true), SpecialMessagesChat.getNormalMessage(" \u00a73Dumps all the Chisel group names to the crafttweaker log")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Chisel Group Names:");
            CarvingUtils.getChiselRegistry().getSortedGroupNames().forEach((str) -> {
               CraftTweakerAPI.logCommand("- " + str);
            });
            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Chisel Group Names list generated", sender));
         }
      });
   }
}
